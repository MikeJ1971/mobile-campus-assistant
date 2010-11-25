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
package org.ilrt.mca.rest.providers;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import org.ilrt.mca.KmlMediaType;
import org.ilrt.mca.vocab.GEO;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
@Produces({KmlMediaType.APPLICATION_KML})
public class JenaModelKmlProvider implements MessageBodyWriter<Model> {


    @Override
    public boolean isWriteable(Class<?> aClass, Type type, Annotation[] annotations,
                               MediaType mediaType) {

        return Model.class.isAssignableFrom(aClass);
    }

    @Override
    public long getSize(Model model, Class<?> aClass, Type type, Annotation[] annotations,
                        MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(Model model, Class<?> aClass, Type type, Annotation[] annotations,
                        MediaType mediaType,
                        MultivaluedMap<String, Object> stringObjectMultivaluedMap,
                        OutputStream outputStream) throws IOException, WebApplicationException {

        try {

            // we need to create an XML document
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbf.newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            // create the start of the document
            Element kml = doc.createElementNS("http://www.opengis.net/kml/2.2", "kml");
            Element document = doc.createElement("Document");

            // find the points
            ResIterator iter = model.listResourcesWithProperty(RDF.type, GEO.Point);

            while (iter.hasNext()) {
                Resource resource = iter.nextResource();
                document.appendChild(createPlacemark(resource, doc));
            }

            kml.appendChild(document);
            doc.appendChild(kml);

            TransformerFactory tFactory =
                    TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(outputStream);
            transformer.transform(source, result);


        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }


    private Element createPlacemark(Resource resource, Document doc) {

        Element placemark = doc.createElement("Placemark");

        // get the label
        if (resource.hasProperty(RDFS.label)) {
            Element name = doc.createElement("name");
            name.setTextContent(resource.getProperty(RDFS.label).getLiteral().getLexicalForm());
            placemark.appendChild(name);
        }

        // create the point with coordinates
        if (resource.hasProperty(GEO.longitude) && resource.hasProperty(GEO.latitude)) {

            Element point = doc.createElement("Point");
            Element coord = doc.createElement("coordinates");

            String val = resource.getProperty(GEO.longitude).getLiteral().getLexicalForm() + ","
                    + resource.getProperty(GEO.latitude).getLiteral().getLexicalForm();
            coord.setTextContent(val);

            point.appendChild(coord);
            placemark.appendChild(point);
        }


        return placemark;
    }
}
