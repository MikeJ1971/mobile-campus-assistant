/*
 * Copyright (c) 2010, University of Bristol
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1) Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2) Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3) Neither the name of the University of Bristol nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
package org.ilrt.mca.harvester.geo;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;
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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class OpenStreetMapResponseHandlerImpl implements ResponseHandler {

    public OpenStreetMapResponseHandlerImpl() {
        model = ModelFactory.createDefaultModel();

        // get the rules
        InputStream is = getClass().getResourceAsStream("/rules/osmdata.rules");
        rules = Rule.parseRules(Rule.rulesParserFromReader(
                new BufferedReader(new InputStreamReader(is))));

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

            fireRules();

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return model;
    }

    @Override
    public boolean isSupportedMediaType(String mediaType) {
        return mediaType.startsWith("text/xml") || mediaType.startsWith("application/xml");
    }

    /**
     * The node id and the latitude and longitude are stored as attributes on the node
     * element. We get these via a NamedNodeMap and create a URI and appropriate
     * GEO properties.
     *
     * @param map NamedNodeMap that holds attributes of interest
     * @return a Resource object that holds a URI and lat/long values
     */
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


    /**
     * The OSM data is encapsulated in lots of Tag elements with key (k) and value (v)
     * attribute values. We pull this lexical information and covert it to RDF types
     * and literals.
     *
     * @param resource the Resource object we will add additional information.
     * @param tag      the XML node that holds the OSM data.
     */
    private void parseTagElement(Resource resource, Node tag) {

        // get the attributes in the tag element
        NamedNodeMap map = tag.getAttributes();

        // a name becomes a label
        if (map.getNamedItem("k").getTextContent().equals("name")) {
            resource.addProperty(RDFS.label, map.getNamedItem("v").getTextContent(),
                    XSDDatatype.XSDstring);
        }

        // we are interested in amenities
        if (map.getNamedItem("k").getTextContent().equals("amenity")) {

            resource.addProperty(RDF.type, MCA_GEO.Amenity);

            // values can be separated by semicolons
            parseValue(map.getNamedItem("v").getTextContent(), resource);
        }

        // we are interested in amenities
        if (map.getNamedItem("k").getTextContent().equals("shop")) {

            resource.addProperty(RDF.type, MCA_GEO.Shop);

            // values can be separated by semicolons
            parseValue(map.getNamedItem("v").getTextContent(), resource);
        }

        // does the node have a website?
        if (map.getNamedItem("k").getTextContent().equals("website")) {
            resource.addProperty(FOAF.homepage, model.createResource(map.getNamedItem("v")
                    .getTextContent()));
        }

        // does the node have an email address?
        if (map.getNamedItem("k").getTextContent().equals("email")) {
            resource.addProperty(FOAF.mbox, model.createResource("mailto:" + map.getNamedItem("v")
                    .getTextContent()));
        }

        // does the node have a telephone number?
        if (map.getNamedItem("k").getTextContent().equals("phone")) {
            resource.addProperty(FOAF.phone, model.createResource("tel:" + map.getNamedItem("v")
                    .getTextContent()));
        }

        // does the node have an atm
        if (map.getNamedItem("k").getTextContent().equals("atm")) {
            if (map.getNamedItem("v").getTextContent().equals("yes")) {
                resource.addProperty(RDF.type, MCA_GEO.BuildingWithCashPoint);
            }
        }
    }

    private void parseValue(String value, Resource resource) {

        // values can be separated by semicolons
        String[] values = value.split(";");

        // for each value create a tag and type, if appropriate
        for (String value1 : values) {
            resource.addProperty(MCA_GEO.hasTag, value1);
        }
    }


    void fireRules() {

        model.add(ModelFactory.createInfModel(new GenericRuleReasoner(rules), model));
    }

    private Model model;

    private final List<Rule> rules;
}