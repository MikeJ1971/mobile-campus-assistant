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
package org.ilrt.mca.harvester.xml;

import com.hp.hpl.jena.rdf.model.Model;
import org.apache.log4j.Logger;
import org.ilrt.mca.harvester.Harvester;
import org.ilrt.mca.harvester.HttpResolverImpl;
import org.ilrt.mca.harvester.Resolver;
import org.ilrt.mca.rdf.Repository;

import java.io.IOException;
import java.util.Date;
import java.util.List;


/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class XmlSourceHarvesterImplImpl extends AbstractXmlSourceHarvesterImpl implements Harvester {

    public XmlSourceHarvesterImplImpl(Repository repository) throws IOException {
        this.repository = repository;
        resolver = new HttpResolverImpl();
        findSources = loadSparql("/sparql/findHarvestableXml.rql");
    }

    @Override
    public void harvest() {

        // new date to keep track of the visit
        Date lastVisited = new Date();

        // query registry for list of feeds to harvest
        // get the date that they were last updated
        List<XmlSource> sources = findSources(findSources);

        log.info("Found " + sources.size() + " sources to harvest");

        // harvest each source
        for (XmlSource source : sources) {

            log.info("Request to harvest: <" + source.getUrl() + ">");

            String xsl = "/" + source.getXsl().substring(6, source.getXsl().length());

            // harvest the data
            Model model = resolver.resolve(source, new XmlSourceResponseHandlerImpl(xsl));

            saveOrUpdate(source,  lastVisited, model);
        }
    }

    final private Resolver resolver;

    final private String findSources;

    final private Logger log = Logger.getLogger(XmlSourceHarvesterImplImpl.class);
}
