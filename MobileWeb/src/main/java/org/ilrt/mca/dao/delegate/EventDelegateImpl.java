package org.ilrt.mca.dao.delegate;

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
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.ilrt.mca.domain.events.EventItemImpl;
import org.ilrt.mca.domain.events.EventSourceImpl;
import org.ilrt.mca.vocab.EVENT;
import org.ilrt.mca.vocab.MCA_REGISTRY;

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

        Resource graphUri = resource.getProperty(RDFS.seeAlso).getResource();
        
        Calendar oneMonthFromNowCal = Calendar.getInstance();
        // add 18 months to current date
        oneMonthFromNowCal.add( Calendar.MONTH, 1 );

        Date now = new Date();
        Date oneMonthFromNow = oneMonthFromNowCal.getTime();

        if (parameters.containsKey("item"))
        {
            EventItemImpl item = new EventItemImpl();

            String queryUid = parameters.get("item").get(0).toString();

            // we've requested information about a repeating event.
            // We could search the repo for the item and calculate the date, however a simpler option is to be passed the startdate

            QuerySolutionMap bindings = new QuerySolutionMap();
            bindings.add("id", ResourceFactory.createPlainLiteral(queryUid));
            bindings.add("graph", graphUri);

            log.info("queryUid:"+queryUid);
            log.info("graph:"+graphUri);

            Model resultModel = repository.find(bindings, findEventDetails);

            resultModel.write(System.out);
            
            StmtIterator stmtiter = resultModel.listStatements(null, RDF.type, EVENT.event);
            if (stmtiter.hasNext())
            {
                Statement st = stmtiter.nextStatement();

                Resource r = st.getSubject();
                item = eventItemDetails(r, queryUid);

                Date startDate = null;

                if (parameters.containsKey("r"))
                {
                    startDate = new Date();
                    startDate.setTime(Long.parseLong(parameters.get("r").get(0).toString()));
                    log.debug("Supplied start date is " + startDate);
                }

                if (startDate != null)
                {
                    long diff = 0;

                    if (item.getEndDate() != null)
                    {
                        log.info(item.getEndDate() );

                        // Calculate expectedEndDate based on diff between original start and end dates
                        long milis1 = item.getStartDate().getTime();
                        long milis2 = item.getEndDate().getTime();
                        diff = milis2 - milis1;

                        Calendar end = Calendar.getInstance();
                        // add diff ms here
                        end.setTimeInMillis(startDate.getTime()+diff);
                        item.setEndDate(end.getTime());
                    }

                    item.setStartDate(startDate);
                }
            }
            else
            {
                log.info("Item not found");
            }

            return item;
        }
        else
        {
            EventSourceImpl item = new EventSourceImpl();

            // get all events for this calendar feed
            QuerySolutionMap bindings = new QuerySolutionMap();
            bindings.add("graph", graphUri);

            // search feeds with the specified item
            Model resultModel = repository.find(bindings, findEventsList);

//            resultModel.write(System.out);
            StmtIterator stmtiter = resultModel.listStatements(null, RDF.type, EVENT.event);

            if (!stmtiter.hasNext()) log.info("no iterators");

            while (stmtiter.hasNext()) {
                Statement statement = stmtiter.nextStatement();
                Resource r = statement.getSubject();
                EventItemImpl calEvent = eventItemDetails(r, graphUri.getURI());

                // filter date range
                if (calEvent.getStartDate().after(now) && calEvent.getStartDate().before(oneMonthFromNow))
                {
                    log.debug("Adding event " + calEvent.getStartDate());
                    item.getItems().add(calEvent);
                }

                // create repeating items
                if (calEvent.isRecurring())
                {
                    // generate the repeating events for this item
                    List<Date> dates = calEvent.getRecurringDatesUntil(oneMonthFromNow);

                    long diff = 0;

                    if (calEvent.getEndDate() != null)
                    {
                        // Calculate expectedEndDate;
                        Calendar start = Calendar.getInstance();
                        start.setTime(calEvent.getStartDate());

                        long milis1 = calEvent.getStartDate().getTime();
                        long milis2 = calEvent.getEndDate().getTime();
                        diff = milis2 - milis1;
                    }

                    for (Date d : dates)
                    {
                        // if dates are valid, add this event
                        if (d.after(now) &&d.before(oneMonthFromNow))
                        {
                            EventItemImpl repeatEvent = calEvent.clone();
                            repeatEvent.setStartDate(d);

                            if (calEvent.getEndDate() != null)
                            {
                                Calendar end = Calendar.getInstance();
                                // add diff ms here
                                end.setTimeInMillis(d.getTime()+diff);
                                repeatEvent.setEndDate(end.getTime());
                            }

                            item.getItems().add(repeatEvent);
                        }
                    }
                }
            }
            Collections.sort(item.getItems());

            eventSourceDetails(resource, item);

            return item;
        }
    }

    @Override
    public Model createModel(Resource resource, MultivaluedMap<String, String> parameters) {

        log.info("Creating model for " + resource.getURI());

        Model model = repository.find("id", resource.getURI(), findEventsCollection);

        return ModelFactory.createUnion(resource.getModel(), model);
    }

    private void eventSourceDetails(Resource resource, EventSourceImpl item) {

        getBasicDetails(resource,item);

        item.setHTMLLink(resource.getProperty(MCA_REGISTRY.htmlLink).getLiteral().getLexicalForm());

        item.setiCalLink(resource.getProperty(MCA_REGISTRY.icalLink).getLiteral().getLexicalForm());
    }

    private EventItemImpl eventItemDetails(Resource resource, String provenance) {
        
        EventItemImpl item = new EventItemImpl();

        getBasicDetails(resource,item);

        // override default id with uid from ical.
        // resource.getURI() returns null anyway.
        item.setId(resource.getProperty(EVENT.UID).getLiteral().getLexicalForm());

        item.setProvenance(provenance);

        if (resource.hasProperty(EVENT.startDate)) {
            String strDate = resource.getProperty(EVENT.startDate).getLiteral().getLexicalForm();

            try {
                item.setStartDate(Common.parseDate(strDate));
            } catch (ParseException e) {
                log.error("Unable to parse: " + strDate + " : " + e.getMessage());
            }
        }

        if (resource.hasProperty(EVENT.endDate)) {
            String strDate = resource.getProperty(EVENT.endDate).getLiteral().getLexicalForm();

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

        // set recurring event properties
        if (resource.hasProperty(EVENT.frequency)) {
            item.setFrequency(resource.getProperty(EVENT.frequency).getLiteral().getLexicalForm());
        }

        if (resource.hasProperty(EVENT.until)) {
            String strDate = resource.getProperty(EVENT.until).getLiteral().getLexicalForm();

            try {
                item.setUntil(Common.parseDate(strDate));
            } catch (ParseException e) {
                log.error("Unable to parse: " + strDate + " : " + e.getMessage());
            }
        }

        if (resource.hasProperty(EVENT.byDay)) {
            item.setByDays(resource.getProperty(EVENT.byDay).getLiteral().getLexicalForm());
        }

        if (resource.hasProperty(EVENT.byMonth)) {
            item.setByMonth(resource.getProperty(EVENT.byMonth).getLiteral().getLexicalForm());
        }

        return item;
    }
}
