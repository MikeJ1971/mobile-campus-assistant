package org.ilrt.mca.harvester.feeds;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.SyndFeedOutput;
import org.ilrt.mca.harvester.ResponseHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class FeedResponseHandlerImpl implements ResponseHandler {

    @Override
    public Model getModel(String sourceUrl, InputStream is) {

        try {

            // get the feed and convert to rss 1.0
            SyndFeedInput synfeed = new SyndFeedInput();
            SyndFeed syndFeed = synfeed.build(new InputStreamReader(is, "UTF-8"));
            syndFeed.setFeedType("rss_1.0");
            syndFeed.setUri(sourceUrl);


            // The student union feeds GUID is not a valid URI - we check that the value of
            // getUri (which the ROME API populated with the RSS GUID) starts with "http:, if not
            // use the link as the URI.
            for (Object o : syndFeed.getEntries()) {
                SyndEntry entry = (SyndEntry) o;
                if (!entry.getUri().startsWith("http")) {
                    entry.setUri(entry.getLink());
                }
            }


            // write the feed to a string
            StringWriter writer = new StringWriter();
            SyndFeedOutput output = new SyndFeedOutput();
            output.output(syndFeed, writer);
            String feed = writer.getBuffer().toString();

            // read into a model
            Model model = ModelFactory.createDefaultModel();
            model.read(new StringReader(feed), syndFeed.getLink());

            return model;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (FeedException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean isSupportedMediaType(String mediaType) {

        return mediaType.startsWith("text/xml") || mediaType.startsWith("application/rss+xml")
                || mediaType.startsWith("application/xml")
                || mediaType.startsWith("application/atom+xml");

    }


}
