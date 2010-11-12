package org.ilrt.mca.harvester.geo;

import com.hp.hpl.jena.rdf.model.Model;
import org.ilrt.mca.harvester.AbstractTest;
import org.ilrt.mca.harvester.HttpResolverImpl;
import org.ilrt.mca.harvester.Resolver;
import org.ilrt.mca.harvester.Source;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertTrue;

public class OpenStreetMapResponseHandlerImplTest extends AbstractTest {

    @Before
    public void setUp() throws IOException {
        super.startServer(resourcePath, mediaType);
    }

    @After
    public void tearDown() {
        super.stopServer();
    }

    @Test
    public void test() throws IOException {

        // having an oldish last visited date
        GregorianCalendar lastVisited = new GregorianCalendar(2008, Calendar.SEPTEMBER, 24);

        // resolve!
        Resolver resolver = new HttpResolverImpl();
        Source source = new Source(host + ":" + portNumber + resourcePath, lastVisited.getTime());
        Model model = resolver.resolve(source, new OpenStreetMapResponseHandlerImpl());


        assertTrue(true);

    }

    private final String resourcePath = "/data.osm.xml";
    private final String mediaType = "application/xml?charset=UTF-8";

}
