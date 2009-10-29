package org.ilrt.mca.dao.delegate;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import org.apache.log4j.Logger;
import org.ilrt.mca.Common;
import org.ilrt.mca.dao.AbstractDao;
import org.ilrt.mca.domain.Item;
import org.ilrt.mca.rdf.Repository;
import org.ilrt.mca.vocab.MCA_REGISTRY;

import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.text.ParseException;
import java.util.Collections;
import org.ilrt.mca.domain.events.EventItemImpl;
import org.ilrt.mca.vocab.EVENT;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class EventDelegateImpl extends AbstractDao implements Delegate {

    private String findEventsCollection = null;
    private String findEventItems = null;
    private final Repository repository;
    Logger log = Logger.getLogger(EventDelegateImpl.class);

    public EventDelegateImpl(final Repository repository) {
        this.repository = repository;
        try {
            findEventsCollection = loadSparql("/sparql/findEvents.rql");
            findEventItems = loadSparql("/sparql/findEventDetails.rql");
        } catch (IOException ex) {
            log.error("Unable to load SPARQL query: " + ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Item createItem(Resource resource, MultivaluedMap<String, String> parameters) {

        // the feed is from a specified graph
        Resource graphUri = resource.getProperty(RDFS.seeAlso).getResource();


        Model model = repository.find("id", resource.getURI(), findEventItems);

        log.info("Creating item " + resource.getURI());
        log.info("Creating item " + graphUri.getURI());

        model.write(System.out);

        EventItemImpl item = new EventItemImpl();
/*
            StmtIterator stmtiter = graphUri.getModel().listStatements(null, RDFS.type, EVENT.event);

//            graphUri.getModel().write(System.out);
            
            if (!stmtiter.hasNext()) log.info("no iterators");
            
            while (stmtiter.hasNext()) {
                Statement statement = stmtiter.nextStatement();
                Resource r = statement.getSubject();
                log.info(r);
                item.getItems().add(eventItemDetails(r, graphUri.getURI()));
            }
            */

        ResIterator iter = model.listSubjects();

        if (iter.hasNext())
        {

            while (iter.hasNext()) {
                Resource r = iter.nextResource();
                EventItemImpl event = eventItemDetails(r,r.getURI());

                log.info(event + " : " + event.getId());
                item.getItems().add(event);
            }

//            Collections.sort(item.getItems());

        } else if (graphUri.hasProperty(MCA_REGISTRY.hasItem)) {    // dealing with an item

            item = eventItemDetails(graphUri.getProperty(MCA_REGISTRY.hasItem).getResource(), graphUri.getURI());

            item.setTemplate("template://eventDetails.ftl");
        }

        getBasicDetails(resource, item);

        return item;
    }

    @Override
    public Model createModel(Resource resource, MultivaluedMap<String, String> parameters) {

        log.info("Creating model for " + resource.getURI());

        Model model = repository.find("id", resource.getURI(), findEventsCollection);

        return ModelFactory.createUnion(resource.getModel(), model);
    }

    private EventItemImpl eventItemDetails(Resource resource, String provenance) {

        log.info("Obtaining event details " + resource);

        EventItemImpl item = new EventItemImpl();

        getBasicDetails(resource,item);

        item.setId(resource.getURI());

        item.setProvenance(provenance);

        if (resource.hasProperty(EVENT.startDate)) {
            String strDate = resource.getProperty(EVENT.startDate).getString();
            log.info("Got start date "+strDate);
            try {
                item.setStartDate(Common.parseDate(strDate));
            } catch (ParseException e) {
                log.error("Unable to parse: " + strDate + " : " + e.getMessage());
            }
        }

        if (resource.hasProperty(EVENT.endDate)) {
            String strDate = resource.getProperty(EVENT.endDate).getString();
            log.info("Got end date "+strDate);
            try {
                item.setEndDate(Common.parseDate(strDate));
            } catch (ParseException e) {
                log.error("Unable to parse: " + strDate + " : " + e.getMessage());
            }
        }

        if (resource.hasProperty(EVENT.subject)) {
            item.setLabel(resource.getProperty(EVENT.subject).getString());
        }

        if (resource.hasProperty(EVENT.organizerName)) {
            item.setOrganiser(resource.getProperty(EVENT.organizerName).getString());
        }

        if (resource.hasProperty(EVENT.organizerEmail)) {
            item.setType(resource.getProperty(EVENT.organizerEmail).getString());
        }

        if (resource.hasProperty(EVENT.location)) {
            item.setLocation(resource.getProperty(EVENT.location).getString());
        }

        if (resource.hasProperty(EVENT.description)) {
            item.setDescription(resource.getProperty(EVENT.description).getString());
        }


        return item;
    }
}
