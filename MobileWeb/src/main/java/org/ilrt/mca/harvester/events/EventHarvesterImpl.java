/*
 *  © University of Bristol
 */

package org.ilrt.mca.harvester.events;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import java.text.ParseException;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDF;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import org.ilrt.mca.Common;
import org.ilrt.mca.dao.AbstractDao;
import org.ilrt.mca.harvester.Harvester;
import org.ilrt.mca.harvester.HttpResolverImpl;
import org.ilrt.mca.harvester.Resolver;
import org.ilrt.mca.harvester.Source;
import org.ilrt.mca.rdf.Repository;
import org.ilrt.mca.vocab.MCA_REGISTRY;

/**
 *
 * @author cmcpb
 */
public class EventHarvesterImpl extends AbstractDao implements Harvester {

    public EventHarvesterImpl(Repository repository) throws IOException {
        resolver = new HttpResolverImpl();
        this.repository = repository;
        findSources = loadSparql("/sparql/findHarvestableEvents.rql");
    }


    @Override
    public void harvest() {

        // new date to keep track of the visit
        Date lastVisited = new Date();

        // query registry for list of feeds to harvest
        // get the date that they were last updated
        List<Source> sources = findSources();

        log.info("Found " + sources.size() + " sources to harvest");

        // harvest each source
        for (Source source : sources) {

            log.info("Request to harvest: <" + source.getUrl() + ">");

            // harvest the data
            Model model = resolver.resolve(source, new EventResponseHandlerImpl());

            if (model != null) {

                // delete the old data
                repository.deleteAllInGraph(source.getUrl());

                // add the harvested data
                repository.add(source.getUrl(), model);

                // update the last visited date
                RDFNode date = ModelFactory.createDefaultModel()
                        .createTypedLiteral(Common.parseXsdDate(lastVisited), XSDDatatype.XSDdateTime);
                repository.updatePropertyInGraph(Common.AUDIT_GRAPH_URI, source.getUrl(),
                        DC.date, date);
            } else {
                log.info("Unable to cache " + source.getUrl());
            }

        }

    }


    private List<Source> findSources() {

        List<Source> sources = new ArrayList<Source>();

        Model m = repository.find(findSources);

        if (!m.isEmpty()) {

            ResIterator iterator = m.listSubjectsWithProperty(RDF.type);

            while (iterator.hasNext()) {
                sources.add(getDetails(iterator.nextResource()));
            }
        }

        return sources;
    }


    private Source getDetails(Resource resource) {

        Date lastVisited = null;

        String uri = resource.getURI();

        if (resource.hasProperty(MCA_REGISTRY.lastVisitedDate)) {
            try {
                lastVisited = Common.parseXsdDate(resource.getProperty(MCA_REGISTRY.lastVisitedDate).getLiteral().getLexicalForm());
            } catch (ParseException e) {
                log.error(e.getMessage());
            }
        }

        return new Source(uri, lastVisited);
    }

    private Resolver resolver;
    private Repository repository;
    private String findSources;

    final private Logger log = Logger.getLogger(EventHarvesterImpl.class);
}
