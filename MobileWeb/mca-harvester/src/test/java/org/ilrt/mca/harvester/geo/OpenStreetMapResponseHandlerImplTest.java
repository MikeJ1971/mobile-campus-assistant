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

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.vocabulary.RDF;
import org.ilrt.mca.harvester.AbstractTest;
import org.ilrt.mca.harvester.HttpResolverImpl;
import org.ilrt.mca.harvester.Resolver;
import org.ilrt.mca.harvester.Source;
import org.ilrt.mca.vocab.MCA_GEO;
import org.junit.After;
import org.junit.Test;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertTrue;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class OpenStreetMapResponseHandlerImplTest extends AbstractTest {

    @After
    public void tearDown() {
        super.stopServer();
    }

    @Test
    public void testOSMDataOne() throws IOException {

        super.startServer(resourcePathOne, mediaType);

        // resolve!
        Resolver resolver = new HttpResolverImpl();
        Source source = new Source(host + ":" + portNumber + resourcePathOne, lastVisited.getTime());
        Model model = resolver.resolve(source, new OpenStreetMapResponseHandlerImpl());

        assertTrue("The model shouldn't be empty", model.size() > 0);

        // check that are number of inferred types are available
        assertTrue(model.contains(null, RDF.type, MCA_GEO.Amenity));
        assertTrue(model.contains(null, RDF.type, MCA_GEO.ArtsCentre));
        assertTrue(model.contains(null, RDF.type, MCA_GEO.Bank));
        assertTrue(model.contains(null, RDF.type, MCA_GEO.PostBox));
        assertTrue(model.contains(null, RDF.type, MCA_GEO.BicycleParking));
        assertTrue(model.contains(null, RDF.type, MCA_GEO.Pharmacy));
    }

    @Test
    public void testOSMDataTwo() throws IOException {

        super.startServer(resourcePathTwo, mediaType);

        // resolve!
        Resolver resolver = new HttpResolverImpl();
        Source source = new Source(host + ":" + portNumber + resourcePathTwo, lastVisited.getTime());
        Model model = resolver.resolve(source, new OpenStreetMapResponseHandlerImpl());

        assertTrue("The model shouldn't be empty", model.size() > 0);

        // check that are number of inferred types are available
        assertTrue(model.contains(null, RDF.type, MCA_GEO.Shop));
        assertTrue(model.contains(null, RDF.type, MCA_GEO.Supermarket));
    }


    private final String resourcePathOne = "/data.osm.xml";
    private final String resourcePathTwo = "/data2.osm.xml";
    private final String mediaType = "application/xml?charset=UTF-8";

    // having an oldish last visited date
    GregorianCalendar lastVisited = new GregorianCalendar(2008, Calendar.SEPTEMBER, 24);


}
