package org.ilrt.mca.harvester;

import com.hp.hpl.jena.rdf.model.Model;
import com.sun.net.httpserver.HttpServer;
import static junit.framework.Assert.assertFalse;
import org.ilrt.mca.harvester.feeds.FeedResponseHandlerImpl;
import org.junit.After;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class FeedResponseHandlerImplTest extends AbstractTest {

    @Before
    public void setUp() throws IOException {

        InetSocketAddress address = new InetSocketAddress(port);
        httpServer = HttpServer.create(address, 0);
        httpServer.start();
    }

    @After
    public void tearDown() throws Exception {
        httpServer.stop(0);
    }

    @Test
    public void resolveWithHandler() throws IOException {

        httpServer.createContext(context, new RssHandler());

        // having an oldish last visited date
        GregorianCalendar lastVisited = new GregorianCalendar(2008, Calendar.SEPTEMBER, 24);

        // resolve!
        Resolver resolver = new HttpResolverImpl();
        Source source = new Source(host + ":" + port + context, lastVisited.getTime());
        Model model = resolver.resolve(source, new FeedResponseHandlerImpl());

        assertNotNull("The model should not be null", model);
        assertFalse("The model should not be empty", model.isEmpty());
    }

    HttpServer httpServer;
}
