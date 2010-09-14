/*
 *  © University of Bristol
 */

package org.ilrt.mca.harvester.events;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDF;
import org.apache.log4j.Logger;
import org.ilrt.mca.Common;
import org.ilrt.mca.dao.AbstractDao;
import org.ilrt.mca.dao.delegate.EventDelegateImpl;
import org.ilrt.mca.domain.events.EventItemImpl;
import org.ilrt.mca.harvester.Harvester;
import org.ilrt.mca.harvester.HttpResolverImpl;
import org.ilrt.mca.harvester.Resolver;
import org.ilrt.mca.harvester.xml.XmlSource;
import org.ilrt.mca.rdf.SdbManagerImpl;
import org.ilrt.mca.vocab.EVENT;
import org.ilrt.mca.vocab.MCA_REGISTRY;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author cmcpb
 */
public class EventHarvesterImpl extends AbstractDao implements Harvester {

    public EventHarvesterImpl(SdbManagerImpl repository) throws IOException {
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
        List<XmlSource> sources = findSources();

        log.info("Found " + sources.size() + " sources to harvest");

        // harvest each source
        for (XmlSource source : sources) {

            log.info("Request to harvest: <" + source.getUrl() + ">");

            String xsl = source.getXsl();
            if (xsl != null && xsl.length() > 0) {
                xsl = "/" + xsl.substring(6, xsl.length());
            }

            // harvest the data
            Model model = resolver.resolve(source, new EventResponseHandlerImpl(xsl));

            if (model != null) {

                generateRepeatingEvents(model, source.getUrl());

//System.out.println("AFTER");
//model.write(System.out);

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

    private void generateRepeatingEvents(Model model, String graphUri) {
        Date oneMonthFromNow = EventDelegateImpl.getEndDate("ONEMONTH");

        StmtIterator stmtiter = model.listStatements(null, RDF.type, EVENT.event);

        Model newEvents = ModelFactory.createDefaultModel();

        while (stmtiter.hasNext()) {
            Statement statement = stmtiter.nextStatement();
            Resource r = statement.getSubject();
            EventItemImpl calEvent = eventItemDetails(r, graphUri);

            // create repeating items
            if (calEvent.isRecurring()) {
                // generate the repeating events for this item
                List<Date> dates = calEvent.getRecurringDatesUntil(oneMonthFromNow);

                long diff = 0;

                if (calEvent.getEndDate() != null) {
                    // Calculate expectedEndDate;
                    Calendar start = Calendar.getInstance();
                    start.setTime(calEvent.getStartDate());

                    long milis1 = calEvent.getStartDate().getTime();
                    long milis2 = calEvent.getEndDate().getTime();
                    diff = milis2 - milis1;
                }

                int count = 0;
                for (Date d : dates) {
                    EventItemImpl repeatEvent = calEvent.clone();
                    repeatEvent.setStartDate(d);

                    if (calEvent.getEndDate() != null) {
                        Calendar end = Calendar.getInstance();
                        // add diff ms here
                        end.setTimeInMillis(d.getTime() + diff);
                        repeatEvent.setEndDate(end.getTime());
                    }

                    // create a unique id
                    repeatEvent.setId(repeatEvent.getId() + "_" + (count++));

                    // store cached copy in repository
                    Model newEventModel = ModelFactory.createDefaultModel();
                    Resource newRes = ResourceFactory.createResource();
                    newEventModel.add(newRes, RDF.type, EVENT.event);
                    newEventModel.add(newRes, EVENT.UID, ResourceFactory.createPlainLiteral(repeatEvent.getId()));
                    newEventModel.add(newRes, EVENT.subject, ResourceFactory.createPlainLiteral(repeatEvent.getLabel()));

                    if (repeatEvent.getDescription() != null)
                        newEventModel.add(newRes, EVENT.description, ResourceFactory.createPlainLiteral(repeatEvent.getDescription()));
                    if (repeatEvent.getLocation() != null)
                        newEventModel.add(newRes, EVENT.location, ResourceFactory.createPlainLiteral(repeatEvent.getLocation()));
                    if (repeatEvent.getOrganiser() != null)
                        newEventModel.add(newRes, EVENT.organizerName, ResourceFactory.createPlainLiteral(repeatEvent.getOrganiser()));

                    Resource startDate = ResourceFactory.createResource();
                    newEventModel.add(newRes, newEventModel.createProperty(EVENT.NS + "dtstart"), startDate);
                    newEventModel.add(startDate, EVENT.dateTime, ResourceFactory.createPlainLiteral(Common.parseXsdDate(repeatEvent.getStartDate())));

                    if (repeatEvent.getEndDate() != null) {
                        Resource endDate = ResourceFactory.createResource();
                        newEventModel.add(newRes, newEventModel.createProperty(EVENT.NS + "dtend"), endDate);
                        newEventModel.add(endDate, EVENT.dateTime, ResourceFactory.createPlainLiteral(Common.parseXsdDate(repeatEvent.getEndDate())));
                    }

                    newEvents.add(newEventModel);

                }
            } // END if (calEvent.isRecurring())
            if (r.hasProperty(EVENT.startDate)) {
                Resource rDate = r.getProperty(EVENT.startDate).getResource();

                // If resource has a date property, change to dateTime
                if (rDate.hasProperty(EVENT.date)) {
                    String strDate = rDate.getProperty(EVENT.date).getLiteral().getLexicalForm();
                    try {
                        rDate.addProperty(
                                EVENT.dateTime, ResourceFactory.createPlainLiteral(
                                        Common.parseXsdDate(Common.parseDate(strDate))));
                    } catch (ParseException e) {
                        log.error("Unable to parse: " + strDate + " : " + e.getMessage());
                    }
                }
            }
            if (r.hasProperty(EVENT.endDate)) {
                Resource rDate = r.getProperty(EVENT.endDate).getResource();

                // If resource has a date property, change to dateTime
                if (rDate.hasProperty(EVENT.date)) {
                    String strDate = rDate.getProperty(EVENT.date).getLiteral().getLexicalForm();
                    try {
                        rDate.addProperty(
                                EVENT.dateTime, ResourceFactory.createPlainLiteral(
                                        Common.parseXsdDate(Common.parseDate(strDate))));
                    } catch (ParseException e) {
                        log.error("Unable to parse: " + strDate + " : " + e.getMessage());
                    }
                }
            }
        }
        model.add(newEvents);
    }

    private List<XmlSource> findSources() {

        List<XmlSource> sources = new ArrayList<XmlSource>();

        Model m = repository.find(findSources);

        if (!m.isEmpty()) {

            ResIterator iterator = m.listSubjectsWithProperty(RDF.type);

            while (iterator.hasNext()) {
                sources.add(getDetails(iterator.nextResource()));
            }
        }

        return sources;
    }


    private XmlSource getDetails(Resource resource) {

        Date lastVisited = null;

        String uri = resource.getURI();

        String xslSource = "";

        if (resource.hasProperty(MCA_REGISTRY.lastVisitedDate)) {
            try {
                lastVisited = Common.parseXsdDate(resource.getProperty(MCA_REGISTRY.lastVisitedDate).getLiteral().getLexicalForm());
            } catch (ParseException e) {
                log.error(e.getMessage());
            }
        }

        if (resource.hasProperty(MCA_REGISTRY.hasXslSource)) {
            xslSource = resource.getProperty(MCA_REGISTRY.hasXslSource).getResource().getURI();
        }

        return new XmlSource(uri, xslSource, lastVisited);
    }

    public EventItemImpl eventItemDetails(Resource resource, String provenance) {

        EventItemImpl item = new EventItemImpl();

        getBasicDetails(resource, item);

        // override default id with uid from ical.
        // resource.getURI() returns null anyway.
        item.setId(resource.getProperty(EVENT.UID).getLiteral().getLexicalForm());

        item.setProvenance(provenance);

        if (resource.hasProperty(EVENT.startDate)) {
            Resource startDate = resource.getProperty(EVENT.startDate).getResource();

            String strDate = "";
            if (startDate.hasProperty(EVENT.date)) {
                strDate = startDate.getProperty(EVENT.date).getLiteral().getLexicalForm();
            }
            if (startDate.hasProperty(EVENT.dateTime)) {
                strDate = startDate.getProperty(EVENT.dateTime).getLiteral().getLexicalForm();
            }
            try {
                item.setStartDate(Common.parseDate(strDate));
            } catch (ParseException e) {
                log.error("Unable to parse: " + strDate + " : " + e.getMessage());
            }
        }

        if (resource.hasProperty(EVENT.endDate)) {
            Resource endDate = resource.getProperty(EVENT.endDate).getResource();

            String strDate = "";
            if (endDate.hasProperty(EVENT.date)) {
                strDate = endDate.getProperty(EVENT.date).getLiteral().getLexicalForm();
            }
            if (endDate.hasProperty(EVENT.dateTime)) {
                strDate = endDate.getProperty(EVENT.dateTime).getLiteral().getLexicalForm();
            }

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

        if (resource.hasProperty(EVENT.rrule)) {
            Resource rrule = resource.getProperty(EVENT.rrule).getResource();

            // set recurring event properties
            if (rrule.hasProperty(EVENT.frequency)) {
                item.setFrequency(rrule.getProperty(EVENT.frequency).getLiteral().getLexicalForm());
            }

            if (rrule.hasProperty(EVENT.until)) {
                String strDate = rrule.getProperty(EVENT.until).getLiteral().getLexicalForm();

                try {
                    item.setUntil(Common.parseDate(strDate));
                } catch (ParseException e) {
                    log.error("Unable to parse: " + strDate + " : " + e.getMessage());
                }
            }

            if (rrule.hasProperty(EVENT.byDay)) {
                item.setByDays(rrule.getProperty(EVENT.byDay).getLiteral().getLexicalForm());
            }

            if (rrule.hasProperty(EVENT.byMonth)) {
                item.setByMonth(rrule.getProperty(EVENT.byMonth).getLiteral().getLexicalForm());
            }
        }

        return item;
    }

    private Resolver resolver;
    private SdbManagerImpl repository;
    private String findSources;

    final private Logger log = Logger.getLogger(EventHarvesterImpl.class);
}
