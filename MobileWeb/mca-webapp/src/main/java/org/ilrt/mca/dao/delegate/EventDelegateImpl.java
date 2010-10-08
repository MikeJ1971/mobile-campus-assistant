/*
 * Copyright (c) 2009, University of Bristol
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1) Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2) Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3) Neither the name of the University of Bristol nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
package org.ilrt.mca.dao.delegate;

import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDFS;
import org.apache.log4j.Logger;
import org.ilrt.mca.Common;
import org.ilrt.mca.dao.AbstractDao;
import org.ilrt.mca.rdf.QueryManager;
import org.ilrt.mca.vocab.MCA_REGISTRY;

import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Chris Bailey (c.bailey@bristol.ac.uk)
 */
public class EventDelegateImpl extends AbstractDao implements Delegate {

    private String findEventsCollection = null;
    private String findEventsList = null;
    private String findEventDetails = null;
    private final QueryManager queryManager;
    Logger log = Logger.getLogger(EventDelegateImpl.class);

    public EventDelegateImpl(final QueryManager queryManager) {
        this.queryManager = queryManager;
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
    public Resource createResource(Resource resource, MultivaluedMap<String, String> parameters) {

        Model model = queryManager.find("id", resource.getURI(), findEventsCollection);
        resource.getModel().add(model);

        Resource graphUri = null;

        if (resource.hasProperty(RDFS.seeAlso))
            graphUri = resource.getProperty(RDFS.seeAlso).getResource();

        String startDate = Common.parseXsdDate(EventDelegateImpl.getStartDate());
        String endDate = Common.parseXsdDate(EventDelegateImpl
                .getEndDate(resource.getProperty(MCA_REGISTRY.eventlist).getLiteral()
                .getLexicalForm()));

        log.info("graphUri:" + graphUri);
        log.info("Looking for events from " + startDate + " to " + endDate);

        if (parameters.containsKey("item")) {

            String queryUid = parameters.get("item").get(0);

            // we've requested information about a repeating event.
            // We could search the repo for the item and calculate the date, however a simpler option is to be passed the startdate

            QuerySolutionMap bindings = new QuerySolutionMap();
            bindings.add("id", ResourceFactory.createPlainLiteral(queryUid));
            bindings.add("this", resource);
            if (graphUri != null) bindings.add("graph", graphUri);

            Model resultModel = queryManager.find(bindings, findEventDetails);
            resource.getModel().add(resultModel);

            resource.getProperty(MCA_REGISTRY.template).changeObject(resource.getModel()
                    .createResource("template://eventDetails.ftl"));


        } else {

            // get all events for this calendar feed
            QuerySolutionMap bindings = new QuerySolutionMap();
            if (graphUri != null) bindings.add("graph", graphUri);
            bindings.add("startDate", ResourceFactory.createPlainLiteral(startDate));
            bindings.add("endDate", ResourceFactory.createPlainLiteral(endDate));
            bindings.add("this", resource);

            // search feeds with the specified item
            Model resultModel = queryManager.find(bindings, findEventsList);
            resource.getModel().add(resultModel);
        }

        return resource;
    }

    public static Date getStartDate() {
        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();
    }

    public static Date getEndDate(String s) {
        Calendar endCal = Calendar.getInstance();

        if (s.equalsIgnoreCase("TODAY")) {
            endCal.set(Calendar.HOUR_OF_DAY, 23);
            endCal.set(Calendar.MINUTE, 59);
            endCal.set(Calendar.SECOND, 59);
            endCal.set(Calendar.MILLISECOND, 999);
        }
        if (s.equalsIgnoreCase("ONEMONTH")) endCal.add(Calendar.MONTH, 1);

        return endCal.getTime();
    }
}
