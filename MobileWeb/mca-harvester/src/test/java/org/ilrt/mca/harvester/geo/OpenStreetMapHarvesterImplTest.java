package org.ilrt.mca.harvester.geo;

import org.ilrt.mca.harvester.AbstractTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class OpenStreetMapHarvesterImplTest extends AbstractTest {

    @Before
    public void setUp() throws IOException, InstantiationException {

        super.startServer(resourcePath, mediaType);
    }

    @Test
    public void testSources() {

        assertTrue(true);
    }

    @After
    public void tearDown() throws IOException, InstantiationException {
        super.stopServer();
    }

    private final String resourcePath = "/data.osm.xml";
    private final String mediaType = "application/xml";
}
