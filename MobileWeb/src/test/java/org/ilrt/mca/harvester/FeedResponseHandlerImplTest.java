package org.ilrt.mca.harvester;

import com.hp.hpl.jena.rdf.model.Model;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.Headers;
import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.ilrt.mca.harvester.feeds.FeedResponseHandlerImpl;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.net.InetSocketAddress;
import java.io.IOException;

/**
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class FeedResponseHandlerImplTest {

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
        Source source = new Source(host + ":" + port + context, "Test Source",
                lastVisited.getTime());
        Model model = resolver.resolve(source,new FeedResponseHandlerImpl());

        assertNotNull("The model should not be null", model);
        assertFalse("The model should not be empty", model.isEmpty());
    }

    HttpServer httpServer;

    private String getRssFeed() {

        StringBuffer buffer = new StringBuffer();
        buffer.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        buffer.append("<rss xmlns:dc=\"http://purl.org/dc/elements/1.1/\" ");
        buffer.append("xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" version=\"2.0\">");
        buffer.append("<channel>");
        buffer.append("<title>University of Bristol news</title>");
        buffer.append("<link>http://www.bris.ac.uk/news/</link>");
        buffer.append("<description>Latest news from the University of Bristol</description>");
        buffer.append("<item>");
        buffer.append("<title>Record turnout for David Attenborough lecture</title>");
        buffer.append("<link>http://www.bris.ac.uk/news/2009/6565.html</link>");
        buffer.append("<description>Last night [24 September] Sir David Attenborough delivered a ");
        buffer.append("lecture on Alfred Russel Wallace and the Birds of Paradise to a capacity ");
        buffer.append("audience at the University. More than 850 people packed the Great Hall in ");
        buffer.append("the Wills Memorial Building to hear the legendary and much-loved ");
        buffer.append("broadcaster speak.</description>");
        buffer.append("<pubDate>Fri, 25 Sep 2009 06:00:00 +0000</pubDate>");
        buffer.append("<guid>http://www.bris.ac.uk/news/2009/6565.html</guid>");
        buffer.append("<category>Press releases</category>");
        buffer.append("</item>");
        buffer.append("</channel>");
        buffer.append("</rss>");
        return buffer.toString();
    }


    public class RssHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {

            String feed = getRssFeed();
            System.out.println(feed);
            Headers responseHeaders = httpExchange.getResponseHeaders();
            responseHeaders.set("Content-Type", "application/rss+xml");
            httpExchange.sendResponseHeaders(200, feed.length());
            httpExchange.getResponseBody().write(feed.getBytes());
            httpExchange.close();
        }
    }

    private String host = "http://localhost";
    private int port = 8090;
    private String context = "/feed/";
}
