package org.ilrt.mca.dao.delegate;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import org.apache.log4j.Logger;
import org.ilrt.mca.Common;
import org.ilrt.mca.dao.AbstractDao;
import org.ilrt.mca.domain.Item;
import org.ilrt.mca.rdf.Repository;

import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.text.ParseException;
import org.ilrt.mca.domain.events.EventItemImpl;
import org.ilrt.mca.vocab.EVENT;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class EventDelegateImpl extends AbstractDao implements Delegate {

    private String findEventsCollection = null;
    private String findEventsList = null;
    private String findEventDetails = null;
    private final Repository repository;
    Logger log = Logger.getLogger(EventDelegateImpl.class);

    public EventDelegateImpl(final Repository repository) {
        this.repository = repository;
        try {
            findEventsCollection = loadSparql("/sparql/findEvents.rql");
            findEventsList = loadSparql("/sparql/findEventsList.rql");
            findEventDetails = loadSparql("/sparql/findEventDetails.rql");
        } catch (IOException ex) {
            log.error("Unable to load SPARQL query: " + ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Item createItem(Resource resource, MultivaluedMap<String, String> parameters) {

        EventItemImpl item = new EventItemImpl();

        Resource graphUri = resource.getProperty(RDFS.seeAlso).getResource();

        Model model = repository.get(graphUri.getURI());

        if (parameters.containsKey("item"))
        {
            String queryUid = parameters.get("item").get(0).toString();
System.out.println(queryUid);
            // get specific event details
            // query model with our sparql query

            QuerySolutionMap bindings = new QuerySolutionMap();
            bindings.add("id", ResourceFactory.createPlainLiteral(queryUid));
            QueryExecution qe = QueryExecutionFactory.create(findEventDetails, model, bindings);

            Model resultModel = qe.execConstruct();
            qe.close();

            Resource r = (Resource)resultModel.listSubjects().next();
            System.out.println(r);
            item = eventItemDetails(r, queryUid);
            System.out.println("Found:"+item.getStartDate());
            return item;
        }
        else
        {
            // get all events for this calendar feed
            QueryExecution qe = QueryExecutionFactory.create(findEventsList, model);
            Model resultModel = qe.execConstruct();
            qe.close();

            StmtIterator stmtiter = resultModel.listStatements(null, RDF.type, EVENT.event);

            if (!stmtiter.hasNext()) log.info("no iterators");

            while (stmtiter.hasNext()) {
                Statement statement = stmtiter.nextStatement();
                Resource r = statement.getSubject();
                item.getItems().add(eventItemDetails(r, graphUri.getURI()));
            }

            getBasicDetails(resource, item);
        }

        return item;
    }

    @Override
    public Model createModel(Resource resource, MultivaluedMap<String, String> parameters) {

        log.info("Creating model for " + resource.getURI());

        Model model = repository.find("id", resource.getURI(), findEventsCollection);

        return ModelFactory.createUnion(resource.getModel(), model);
    }

    private EventItemImpl eventItemDetails(Resource resource, String provenance) {
        
        EventItemImpl item = new EventItemImpl();

        getBasicDetails(resource,item);

        System.out.println(resource.getProperty(EVENT.UID));
        // override default id with uid from ical.
        // resource.getURI() returns null anyway.
        item.setId(resource.getProperty(EVENT.UID).getLiteral().getLexicalForm());

        item.setProvenance(provenance);

        if (resource.hasProperty(EVENT.startDate)) {
            String strDate = resource.getProperty(EVENT.startDate).getLiteral().getLexicalForm();
            log.info("Got start date "+strDate);

            try {
                item.setStartDate(Common.parseDate(strDate));
            } catch (ParseException e) {
                log.error("Unable to parse: " + strDate + " : " + e.getMessage());
            }
        }

        if (resource.hasProperty(EVENT.endDate)) {
            String strDate = resource.getProperty(EVENT.endDate).getLiteral().getLexicalForm();
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
            item.setOrganiser(resource.getProperty(EVENT.organizerName).getLiteral().getLexicalForm());
        }

        if (resource.hasProperty(EVENT.organizerEmail)) {
            item.setType(resource.getProperty(EVENT.organizerEmail).getLiteral().getLexicalForm());
        }

        if (resource.hasProperty(EVENT.location)) {
            item.setLocation(resource.getProperty(EVENT.location).getLiteral().getLexicalForm());
        }

        if (resource.hasProperty(EVENT.description)) {
            item.setDescription(resource.getProperty(EVENT.description).getLiteral().getLexicalForm());
        }


        return item;
    }
}
