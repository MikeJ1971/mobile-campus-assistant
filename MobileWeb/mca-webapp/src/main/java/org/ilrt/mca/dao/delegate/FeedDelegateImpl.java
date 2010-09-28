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
import org.joda.time.DateTime;

import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;

/**
 * The feed delegate handles requests for feeds:
 * <p/>
 * 1) It might request all news items for a specific feed. Each feed is held in its own
 * named graph.
 * 2) It might request feed items that span across all named graphs.
 * 3) It requests the details of an individual news item.
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class FeedDelegateImpl extends AbstractDao implements Delegate {

    public FeedDelegateImpl(final QueryManager queryManager) {
        this.queryManager = queryManager;
        try {
            findNewsItems = loadSparql("/sparql/findNewsItems.rql");
            findNewsItemsByDate = loadSparql("/sparql/findNewsItemsByDate.rql");
        } catch (IOException ex) {
            log.error("Unable to load SPARQL query: " + ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Resource createResource(Resource resource, MultivaluedMap<String, String> parameters) {

        // we have a parameter so we are interested in a single item
        if (parameters.containsKey("item")) {

            return newsItem(resource, parameters.getFirst("item"));
        }

        if (resource.hasProperty(RDFS.seeAlso)) { // we are looking for a specific graph

            QuerySolutionMap bindings = new QuerySolutionMap();

            // seeAlso will be the name of the graph and so we need to bind
            Resource graph = resource.getProperty(RDFS.seeAlso).getResource();
            bindings.add("id", resource);
            bindings.add("graph", graph);

            // search feeds with the specified item
            Model feedModel = queryManager.find(bindings, findNewsItems);

            resource.getModel().add(feedModel);

            return resource;

        } else { // search all graphs

            // calculate the start and end dates
            DateTime current = new DateTime();
            DateTime past = current.minusHours(24); // TODO the interval should be set in the registry

            String endDate = Common.parseXsdDate(current.toDate());
            String startDate = Common.parseXsdDate(past.toDate());

            QuerySolutionMap bindings = new QuerySolutionMap();
            bindings.add("startDate", ResourceFactory.createPlainLiteral(startDate));
            bindings.add("endDate", ResourceFactory.createPlainLiteral(endDate));
            bindings.add("id", resource);

            Model results = queryManager.find(bindings, findNewsItemsByDate);

            resource.getModel().add(results);

            return resource;
        }
    }

    private Resource newsItem(Resource resource, String newsItemUri) {

        QuerySolutionMap bindings = new QuerySolutionMap();

        // bind to the graph - the source URI
        if (resource.hasProperty(RDFS.seeAlso)) {
            bindings.add("graph", resource.getProperty(RDFS.seeAlso).getResource());
        }

        // bind to the URI of the specific news item
        bindings.add("itemId", ResourceFactory.createResource(newsItemUri));

        // bind to the URI in the registry that matches the HTTP request
        bindings.add("id", resource);

        Model model = queryManager.find(bindings, findNewsItems);

        // we want to use a special template for an individual news item
        Resource r = model.getResource(newsItemUri);
        Resource template = ResourceFactory.createResource("template://newsItem.ftl");

        if (r.hasProperty(MCA_REGISTRY.template)) {
            r.getProperty(MCA_REGISTRY.template).changeObject(template);
        } else {
            r.addProperty(MCA_REGISTRY.template, template);
        }

        return model.getResource(newsItemUri);
    }

    private String findNewsItems = null;
    private String findNewsItemsByDate = null;
    private final QueryManager queryManager;
    Logger log = Logger.getLogger(FeedDelegateImpl.class);
}
