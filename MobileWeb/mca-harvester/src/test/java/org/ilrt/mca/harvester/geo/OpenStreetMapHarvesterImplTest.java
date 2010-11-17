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
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sdb.SDBFactory;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDF;
import org.ilrt.mca.Common;
import org.ilrt.mca.harvester.AbstractTest;
import org.ilrt.mca.harvester.Harvester;
import org.ilrt.mca.rdf.SdbManagerImpl;
import org.ilrt.mca.rdf.StoreWrapper;
import org.ilrt.mca.rdf.StoreWrapperManager;
import org.ilrt.mca.vocab.MCA_GEO;
import org.ilrt.mca.vocab.MCA_REGISTRY;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class OpenStreetMapHarvesterImplTest extends AbstractTest {

    @Before
    public void setUp() throws Exception {

        // clear data store
        setUpStore();

        // create data set
        StoreWrapperManager manager = getStoreWrapperManager();
        StoreWrapper storeWrapper = manager.getStoreWrapper();
        Dataset dataset = SDBFactory.connectDataset(storeWrapper.getStore());

        // add the harvest source details to the default graph (registry)
        Model m = dataset.getDefaultModel();
        Resource r = m.createResource(uri);
        m.add(m.createStatement(r, RDF.type, MCA_REGISTRY.OSMGeoSource));

        // create a last visited date and add it to the audit graph
        Model audit = dataset.getNamedModel(Common.AUDIT_GRAPH_URI);
        Calendar calendar = new GregorianCalendar(2009, Calendar.SEPTEMBER, 30, 11, 38);
        date = Common.parseXsdDate(calendar.getTime());
        audit.add(audit.createStatement(r, DC.date,
                audit.createTypedLiteral(date, XSDDatatype.XSDdateTime)));

        // clean up
        storeWrapper.close();

        // start the web server
        super.startServer(resourcePath, mediaType);

        // data manager that can be used by the harvester
        dataManager = new SdbManagerImpl(manager);
    }

    @Test
    public void testHarvest() throws IOException, ParseException {

        // ---------- test the data before we harvest

        StoreWrapper beforeWrapper = getStoreWrapper();

        // check there the registry has a source to harvest
        Model registry = SDBFactory.connectDefaultModel(beforeWrapper.getStore());
        assertTrue("There should be a harvest source in the registry (default graph)",
                registry.contains(registry.getResource(uri), RDF.type,
                        MCA_REGISTRY.OSMGeoSource));


        // check that the audit graph has a date
        Model auditModel = SDBFactory.connectNamedModel(beforeWrapper.getStore(),
                Common.AUDIT_GRAPH_URI);
        assertTrue(auditModel.getResource(uri).hasProperty(DC.date));
        assertEquals(date, auditModel.getResource(uri).getProperty(DC.date).getLiteral()
                .getLexicalForm());
        beforeWrapper.close();

        // ---------- fire the harvester

        Harvester harvester = new OpenStreetMapHarvesterImpl(dataManager);
        harvester.harvest();

        // ---------- test the data after the harvest

        StoreWrapper afterWrapper = getStoreWrapper();

        // check that the harvest graph has got data
        Model harvestedData = SDBFactory.connectNamedModel(afterWrapper.getStore(), uri);
        assertFalse("The model shouldn't be empty", harvestedData.isEmpty());

        // check we have some expected types
        assertTrue(harvestedData.contains(null, RDF.type, MCA_GEO.Amenity));
        assertTrue(harvestedData.contains(null, RDF.type, MCA_GEO.Shop));

        auditModel = SDBFactory.connectNamedModel(afterWrapper.getStore(),
                Common.AUDIT_GRAPH_URI);

        // check that the audit date has been updated
        String newDate = auditModel.getResource(uri).getProperty(DC.date).getLiteral()
                .getLexicalForm();
        assertFalse(date.equals(newDate));

        afterWrapper.close();
    }

    @After
    public void tearDown() throws IOException, InstantiationException {
        super.stopServer();
    }

    private final String resourcePath = "/data.osm.xml";
    private final String mediaType = "application/xml";

    String uri = host + ":" + portNumber + resourcePath;

    String date;
}
