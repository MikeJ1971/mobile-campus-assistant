package org.ilrt.mca.harvester.geo;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import org.ilrt.mca.harvester.ResponseHandler;
import org.ilrt.mca.vocab.GEO;
import org.ilrt.mca.vocab.MCA_GEO;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;


public class OpenStreetMapResponseHandlerImpl implements ResponseHandler {

    public OpenStreetMapResponseHandlerImpl() {
        model = ModelFactory.createDefaultModel();
    }

    @Override
    public Model getModel(String sourceUri, InputStream is) {
        try {

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();

            Document doc = db.parse(is);

            NodeList nodeList = doc.getFirstChild().getChildNodes();

            for (int i = 0; i < nodeList.getLength(); i++) {

                Node node = nodeList.item(i);

                if (node.getNodeName().equals("node")) {


                    // the attributes on the node hold the id and lat/long; use these
                    // to create uri (id) and initial data (lot/long)
                    Resource resource = createResource(node.getAttributes());


                    NodeList tagList = node.getChildNodes();


                    for (int j = 0; j < tagList.getLength(); j++) {

                        Node tag = tagList.item(j);


                        if (tag.getNodeName().equals("tag")) {

                            parseTagElement(resource, tag);
                        }

                    }

                    model.add(resource.getModel());

                }
            }

            model.write(System.out);


        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean isSupportedMediaType(String mediaType) {
        return mediaType.startsWith("text/xml") || mediaType.startsWith("application/xml");
    }

    private Resource createResource(NamedNodeMap map) {

        // create the URI
        Resource resource = model.createResource("http://www.openstreetmap.org/api/0.6/node/"
                + map.getNamedItem("id").getTextContent());

        // add the latitude and longitude
        resource.addProperty(GEO.latitude, map.getNamedItem("lat").getTextContent(),
                XSDDatatype.XSDdouble);
        resource.addProperty(GEO.longitude, map.getNamedItem("lon").getTextContent(),
                XSDDatatype.XSDdouble);

        // add type
        resource.addProperty(RDF.type, GEO.Point);

        return resource;
    }


    private void parseTagElement(Resource resource, Node tag) {

        NamedNodeMap map = tag.getAttributes();

        if (map.getNamedItem("k").getTextContent().equals("name")) {
            resource.addProperty(RDFS.label, map.getNamedItem("v").getTextContent(),
                    XSDDatatype.XSDstring);
        }

        if (map.getNamedItem("k").getTextContent().equals("amenity")) {

            String value = map.getNamedItem("v").getTextContent();

            if (value.contains("restaurant")) {
                resource.addProperty(RDF.type, MCA_GEO.Restaurant);
            } else if (value.contains("cafe")) {
                resource.addProperty(RDF.type, MCA_GEO.Cafe);
            } else if (value.contains("pub")) {
                resource.addProperty(RDF.type, MCA_GEO.Pub);
            } else if (value.contains("bar")) {
                resource.addProperty(RDF.type, MCA_GEO.Bar);
            } else if (value.contains("post_box")) {
                resource.addProperty(RDF.type, MCA_GEO.PostBox);
            } else if (value.contains("bicycle_parking")) {
                resource.addProperty(RDF.type, MCA_GEO.BicycleParking);
            } else if (value.contains("waste_basket")) {
                resource.addProperty(RDF.type, MCA_GEO.WasteBasket);
            } else if (value.contains("bank")) {
                resource.addProperty(RDF.type, MCA_GEO.Bank);
            } else if (value.contains("post_office")) {
                resource.addProperty(RDF.type, MCA_GEO.PostOffice);
            } else if (value.contains("telephone")) {
                resource.addProperty(RDF.type, MCA_GEO.Telephone);
            } else if (value.contains("theatre")) {
                resource.addProperty(RDF.type, MCA_GEO.Theatre);
            } else if (value.contains("cinema")) {
                resource.addProperty(RDF.type, MCA_GEO.Cinema);
            } else if (value.contains("nightclub")) {
                resource.addProperty(RDF.type, MCA_GEO.NightClub);
            } else if (value.contains("arts_centre")) {
                resource.addProperty(RDF.type, MCA_GEO.ArtsCentre);
            } else if (value.contains("fast_food")) {
                resource.addProperty(RDF.type, MCA_GEO.FastFood);
            }

        }

        if (map.getNamedItem("k").getTextContent().equals("website")) {
            resource.addProperty(FOAF.homepage, model.createResource(map.getNamedItem("v")
                    .getTextContent()));
        }

        if (map.getNamedItem("k").getTextContent().equals("email")) {
            resource.addProperty(FOAF.mbox, model.createResource("mailto:" + map.getNamedItem("v")
                    .getTextContent()));
        }

        if (map.getNamedItem("k").getTextContent().equals("phone")) {
            resource.addProperty(FOAF.phone, model.createResource("tel:" + map.getNamedItem("v")
                    .getTextContent()));
        }


    }

    private Model model;

}
