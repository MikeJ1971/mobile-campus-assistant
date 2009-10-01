package org.ilrt.mca.harvester.feeds;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDFS;
import org.apache.log4j.Logger;
import org.ilrt.mca.Common;
import org.ilrt.mca.harvester.Harvester;
import org.ilrt.mca.harvester.HttpResolverImpl;
import org.ilrt.mca.harvester.Resolver;
import org.ilrt.mca.harvester.Source;
import org.ilrt.mca.rdf.Repository;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class FeedHarvesterImpl implements Harvester {

    public FeedHarvesterImpl(Repository repository) throws IOException {
        resolver = new HttpResolverImpl();
        this.repository = repository;
        //repository = new SdbRepositoryImpl(new StoreWrapperManagerImpl("/sdb.ttl"));
        Common common = new Common();
        findSources = common.loadSparql("/sparql/findHarvestableFeeds.rql");
    }


    @Override
    public void harvest() {

        // query registry for list of feeds to harvest
        // get the date that they were last updated
        List<Source> sources = findSources();

        // harvest each source
        for (Source source : sources) {

            // harvest the data
            Model model = resolver.resolve(source, new FeedResponseHandlerImpl());

            if (model != null) {

                // delete the old data
                repository.delete(source.getUrl(), model);

                // add the harvested data
                repository.delete(source.getUrl(), model);

                // update the last visited date

                // create sparql query for single thing
                // get the model
                // replace the last updated date
                // simples!

            }
        }

    }


    private List<Source> findSources() {

        List<Source> sources = new ArrayList<Source>();

        Model m = repository.find(findSources);

        if (!m.isEmpty()) {

            ResIterator iterator = m.listSubjects();

            while (iterator.hasNext()) {

                String uri, label = null;
                Date lastVisited = null;

                Resource resource = iterator.nextResource();

                uri = resource.getURI();

                if (resource.hasProperty(RDFS.label)) {
                    label = resource.getProperty(RDFS.label).getLiteral().getLexicalForm();
                }

                if (resource.hasProperty(DC.date)) {
                    try {
                        lastVisited = Common.parseDate(resource.getProperty(DC.date)
                                .getLiteral().getLexicalForm());
                    } catch (ParseException e) {
                        log.error(e.getMessage());
                    }
                }

                Source source = new Source(uri, label, lastVisited);
                sources.add(source);
            }

        }

        return sources;
    }

    private Source findSource(String id) {

        Model m = repository.find("id", id, findSources);
        return getDetails(m.getResource(id));

    }

    private Source getDetails(Resource resource) {

        String uri, label = null;
        Date lastVisited = null;

        uri = resource.getURI();

        if (resource.hasProperty(RDFS.label)) {
            label = resource.getProperty(RDFS.label).getLiteral().getLexicalForm();
        }

        if (resource.hasProperty(DC.date)) {
            try {
                lastVisited = Common.parseDate(resource.getProperty(DC.date)
                        .getLiteral().getLexicalForm());
            } catch (ParseException e) {
                log.error(e.getMessage());
            }
        }

        return new Source(uri, label, lastVisited);
    }


    final private Logger log = Logger.getLogger(FeedHarvesterImpl.class);


    private Resolver resolver;
    private Repository repository;
    private String findSources;
}
