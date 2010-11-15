/*
 * Copyright (c) 2010, University of Bristol
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
package org.ilrt.mca.harvester;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDF;
import org.apache.log4j.Logger;
import org.ilrt.mca.Common;
import org.ilrt.mca.dao.AbstractDao;
import org.ilrt.mca.rdf.DataManager;
import org.ilrt.mca.vocab.MCA_REGISTRY;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public abstract class AbstractHarvesterImpl extends AbstractDao implements Harvester {

    protected AbstractHarvesterImpl(DataManager manager) throws IOException {
        this.manager = manager;
        resolver = new HttpResolverImpl();
        this.findSources = loadSparql("/sparql/findHarvestableSources.rql");
    }

    @Override
    public abstract void harvest();

    protected void harvest(String type, ResponseHandler handler) {

        // new date to keep track of the visit
        Date lastVisited = new Date();

        // find the sources to query
        List<Source> sources = findSources(type);

        log.info("Looking for " + type);

        log.info("Found " + sources.size() + " sources to harvest");

        // harvest each source
        for (Source source : sources) {

            log.info("Request to harvest: <" + source.getUrl() + ">");

            // harvest the data
            Model model = resolver.resolve(source, handler);

            if (model != null) {

                // delete the old data
                manager.deleteAllInGraph(source.getUrl());

                // add the harvested data
                manager.add(source.getUrl(), model);

                updateLastVisitedDate(lastVisited, source.getUrl());

            } else {
                log.info("Unable to cache " + source.getUrl());
            }

        }

    }

    protected List<Source> findSources(String sourceType) {

        if (sourceType == null || sourceType.equals("")) {
            throw new RuntimeException("No type is provided for the source");
        }

        List<Source> sources = new ArrayList<Source>();

        Model m = manager.find("type", sourceType, findSources);

        if (!m.isEmpty()) {

            ResIterator iterator = m.listSubjectsWithProperty(RDF.type);

            while (iterator.hasNext()) {
                sources.add(getDetails(iterator.nextResource()));
            }
        }

        return sources;
    }

    protected void updateLastVisitedDate(Date lastVisitedDate, String url) {

        RDFNode date = ModelFactory.createDefaultModel()
                .createTypedLiteral(Common.parseXsdDate(lastVisitedDate), XSDDatatype.XSDdateTime);
        manager.updatePropertyInGraph(Common.AUDIT_GRAPH_URI, url, DC.date, date);
    }

    protected Source getDetails(Resource resource) {

        Date lastVisited = null;

        String uri = resource.getURI();

        if (resource.hasProperty(MCA_REGISTRY.lastVisitedDate)) {
            try {
                lastVisited = Common.parseXsdDate(resource.getProperty(MCA_REGISTRY.lastVisitedDate)
                        .getLiteral().getLexicalForm());
            } catch (ParseException e) {
                log.error(e.getMessage());
            }
        }

        return new Source(uri, lastVisited);
    }

    protected Resolver resolver;
    protected DataManager manager;
    protected String findSources;

    final private Logger log = Logger.getLogger(AbstractHarvesterImpl.class);

}
