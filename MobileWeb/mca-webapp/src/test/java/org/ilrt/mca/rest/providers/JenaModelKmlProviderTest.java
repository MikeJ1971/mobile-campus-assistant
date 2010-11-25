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

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import org.ilrt.mca.vocab.GEO;
import org.junit.Test;
import org.xml.sax.InputSource;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class JenaModelKmlProviderTest {

    @Test
    public void test() throws IOException, XPathExpressionException {

        // ---------- create a model with some data

        Model m = ModelFactory.createDefaultModel();

        Resource point1 = m.createResource("http://www.openstreetmap.org/api/0.6/node/943619199");
        point1.addProperty(GEO.latitude, "51.4541024", XSDDatatype.XSDdouble);
        point1.addProperty(GEO.longitude, "-2.6021703", XSDDatatype.XSDdouble);
        point1.addProperty(RDF.type, GEO.Point);
        point1.addProperty(RDFS.label, "Bristol Ram");

        Resource point2 = m.createResource("http://www.openstreetmap.org/api/0.6/node/26122195");
        point2.addProperty(GEO.latitude, "51.4542722", XSDDatatype.XSDdouble);
        point2.addProperty(GEO.longitude, "-2.6022771", XSDDatatype.XSDdouble);
        point2.addProperty(RDF.type, GEO.Point);
        point2.addProperty(RDFS.label, "Folk House");


        // we are going to test this object...
        JenaModelKmlProvider provider = new JenaModelKmlProvider();

        // we can't use a string
        assertFalse("Can write the object", provider.isWriteable((String.class),
                null, null, null));

        // we should be able to use a Model
        assertTrue("Cannot write the object", provider.isWriteable(m.getClass(), null, null, null));

        // ---------- get a stream and send it to the provider to convert to kml

        ByteOutputStream bos = new ByteOutputStream();
        provider.writeTo(m, null, null, null, null, null, bos);


        // ----------- create an xpath with namespace support

        String output = bos.toString();

        XPath engine = XPathFactory.newInstance().newXPath();

        NamespaceContext ctx = new NamespaceContext() {
            public String getNamespaceURI(String prefix) {
                return "http://www.opengis.net/kml/2.2";
            }

            @Override
            public String getPrefix(String s) {
                return null;
            }

            @Override
            public Iterator getPrefixes(String s) {
                return null;
            }
        };

        engine.setNamespaceContext(ctx);

        // ---------- run some tests

        assertEquals("Unexpected name", "Bristol Ram", engine.evaluate("//ns1:kml/ns1:Document/" +
                "ns1:Placemark[1]/ns1:name/text()", new InputSource(new StringReader(output))));


        assertEquals("Unexpected name", "Folk House", engine.evaluate("//ns1:kml/ns1:Document/" +
                "ns1:Placemark[2]/ns1:name/text()", new InputSource(new StringReader(output))));

    }


}
