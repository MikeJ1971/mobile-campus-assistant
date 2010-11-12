package org.ilrt.mca.harvester.feeds;

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

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class FeedResponseHandlerImplTest extends AbstractTest {

    @Before
    public void setUp() throws IOException {

        super.startServer(resourcePath, mediaType);
    }

    @After
    public void tearDown() throws Exception {
        super.stopServer();
    }

    @Test
    public void resolveWithHandler() throws IOException {

        // having an oldish last visited date
        GregorianCalendar lastVisited = new GregorianCalendar(2008, Calendar.SEPTEMBER, 24);

        // resolve!
        Resolver resolver = new HttpResolverImpl();
        Source source = new Source(host + ":" + portNumber + resourcePath, lastVisited.getTime());
        Model model = resolver.resolve(source, new FeedResponseHandlerImpl());

        assertNotNull("The model should not be null", model);
        assertFalse("The model should not be empty", model.isEmpty());
    }

    private final String resourcePath = "/feed.xml";
    private final String mediaType = "application/xml";
}
