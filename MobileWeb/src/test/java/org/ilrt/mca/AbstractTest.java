package org.ilrt.mca;

import com.hp.hpl.jena.sdb.util.StoreUtils;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.ilrt.mca.rdf.Repository;
import org.ilrt.mca.rdf.SdbRepositoryImpl;
import org.ilrt.mca.rdf.StoreWrapper;
import org.ilrt.mca.rdf.StoreWrapperManager;
import org.ilrt.mca.rdf.StoreWrapperManagerImpl;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class AbstractTest {

    protected void setUpStore() throws Exception {

        StoreWrapper storeWrapper = getStoreWrapper();

        if (StoreUtils.isFormatted(storeWrapper.getStore())) {
            storeWrapper.getStore().getTableFormatter().truncate();
        } else {
            storeWrapper.getStore().getTableFormatter().format();
        }

        assertTrue("The store is not formatted", StoreUtils.isFormatted(storeWrapper.getStore()));

        storeWrapper.close();
    }

    protected StoreWrapper getStoreWrapper() {
        return getStoreWrapperManager().getStoreWrapper();
    }

    protected Repository getRepository() {
        return new SdbRepositoryImpl(getStoreWrapperManager());
    }

    protected StoreWrapperManager getStoreWrapperManager() {
        return new StoreWrapperManagerImpl(TEST_CONFIG);
    }

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


    private String TEST_CONFIG = "/test-sdb.ttl";
    public String host = "http://localhost";
    public int port = 8090;
    public String context = "/feed/";


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

}
