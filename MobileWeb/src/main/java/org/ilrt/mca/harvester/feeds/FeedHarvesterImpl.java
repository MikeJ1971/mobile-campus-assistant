package org.ilrt.mca.harvester.feeds;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;
import org.apache.log4j.Logger;
import org.ilrt.mca.Common;
import org.ilrt.mca.dao.AbstractDao;
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
public class FeedHarvesterImpl extends AbstractDao implements Harvester {

    public FeedHarvesterImpl(Repository repository) throws IOException {
        resolver = new HttpResolverImpl();
        this.repository = repository;
        findSources = loadSparql("/sparql/findHarvestableFeeds.rql");
    }


    @Override
    public void harvest() {

        // new date to keep track of the visit
        Date lastVisited = new Date();

        // query registry for list of feeds to harvest
        // get the date that they were last updated
        List<Source> sources = findSources();

        // harvest each source
        for (Source source : sources) {

            log.info("Request to harvest: <" + source.getUrl() + ">");

            // harvest the data
            Model model = resolver.resolve(source, new FeedResponseHandlerImpl());

            if (model != null) {

                // delete the old data
                repository.delete(source.getUrl(), model);

                // add the harvested data
                repository.delete(source.getUrl(), model);

                // update the last visited date
                RDFNode date = ModelFactory.createDefaultModel()
                        .createTypedLiteral(Common.parseDate(lastVisited), XSDDatatype.XSDdateTime);
                repository.updatePropertyInGraph(Common.AUDIT_GRAPH_URI, source.getUrl(),
                        DC.date, date);
            }
        }

    }


    private List<Source> findSources() {

        List<Source> sources = new ArrayList<Source>();

        Model m = repository.find(findSources);

        if (!m.isEmpty()) {

            ResIterator iterator = m.listSubjects();

            while (iterator.hasNext()) {
                sources.add(getDetails(iterator.nextResource()));
            }
        }

        return sources;
    }


    private Source getDetails(Resource resource) {

        Date lastVisited = null;

        String uri = resource.getURI();

        if (resource.hasProperty(DC.date)) {
            try {
                lastVisited = Common.parseDate(resource.getProperty(DC.date)
                        .getLiteral().getLexicalForm());
            } catch (ParseException e) {
                log.error(e.getMessage());
            }
        }

        return new Source(uri, lastVisited);
    }


    final private Logger log = Logger.getLogger(FeedHarvesterImpl.class);


    private Resolver resolver;
    private Repository repository;
    private String findSources;
}
