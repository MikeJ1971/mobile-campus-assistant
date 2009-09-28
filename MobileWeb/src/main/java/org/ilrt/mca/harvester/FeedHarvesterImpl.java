package org.ilrt.mca.harvester;

import com.hp.hpl.jena.rdf.model.Model;

import java.util.GregorianCalendar;
import java.util.Calendar;
import java.io.IOException;

import org.apache.log4j.Logger;

/**
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class FeedHarvesterImpl implements Harvester {

    public FeedHarvesterImpl() throws IOException {
        resolver = new HttpResolverImpl();
    }


    @Override
    public void harvest() {

        // query registry for list of feeds to harvest

        // get the date that they were last updated?

        // harvest each source

        // add the source to the appropriate graph

        // update the last harvest date


        // -- some prototype code to test the scheduler


        String url = "http://www.bris.ac.uk/news/news-feed.rss";

        GregorianCalendar lastVisited = new GregorianCalendar(2008, Calendar.SEPTEMBER, 24);
        Model model = resolver.resolve(url, lastVisited.getTime(), new FeedResponseHandlerImpl());



        model.write(System.out);

        log.info("Source:  " + url + "; Triples created: " + model.size());


    }

    final private Logger log = Logger.getLogger(FeedHarvesterImpl.class);

    private Resolver resolver;
}
