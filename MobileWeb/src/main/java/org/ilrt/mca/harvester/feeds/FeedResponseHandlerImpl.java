package org.ilrt.mca.harvester.feeds;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.SyndFeedOutput;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;

import org.ilrt.mca.harvester.ResponseHandler;

/**
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class FeedResponseHandlerImpl implements ResponseHandler {

    @Override
    public Model getModel(String sourceUrl, InputStream inputStream) {

        try {
            
            // get the feed and convert to rss 1.0
            Reader reader = new InputStreamReader(inputStream);
            SyndFeedInput synfeed = new SyndFeedInput();
            SyndFeed syndFeed = synfeed.build(reader);
            syndFeed.setFeedType("rss_1.0");
            syndFeed.setUri(sourceUrl);
            syndFeed.setEncoding("UTF-8");

            // write the feed to a string
            StringWriter writer = new StringWriter();
            SyndFeedOutput output = new SyndFeedOutput();
            output.output(syndFeed, writer);
            String feed = writer.getBuffer().toString();

            // read into a model
            Model model = ModelFactory.createDefaultModel();
            ByteArrayInputStream bais = new ByteArrayInputStream(feed.getBytes());
            model.read(bais, syndFeed.getLink());

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

        return mediaType.equals("text/xml") || mediaType.equals("application/rss+xml")
                || mediaType.equals("application/xml")
                || mediaType.equals("application/atom+xml");

    }


}
