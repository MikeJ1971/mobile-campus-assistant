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
package org.ilrt.mca.vocab;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class MCA_REGISTRY {

    private static final Model model = ModelFactory.createDefaultModel();

    public static final String NS = "http://org.ilrt.mca/registry#";

    public static String getURI() {
        return NS;
    }

    // ----- Resources

    public static final Resource NAMESPACE = model.createResource(NS);

    public static final Resource Group = model.createResource(NS + "Group");

    public static final Resource KmlMapSource = model.createResource(NS + "KmlMapSource");

    public static final Resource ActiveMapSource = model.createResource(NS + "ActiveMapSource");

    public static final Resource FeedSource = model.createProperty(NS + "FeedSource");

    public static final Resource News = model.createProperty(NS + "News");

    public static final Resource FeedItem = model.createProperty(NS + "NewsItem");

    public static final Resource XmlSource = model.createProperty(NS + "XmlSource");

    public static final Resource HtmlSource = model.createProperty(NS + "HtmlSource");

    public static final Resource HtmlFragment = model.createProperty(NS + "HtmlFragment");

    public static final Resource Contact = model.createProperty(NS + "Contact");

    public static final Resource Directory = model.createProperty(NS + "Directory");

    public static final Resource EventCalendar = model.createProperty(NS + "EventCalendar");

    public static final Resource OSMGeoSource = model.createProperty(NS + "OSMGeoSource");


    // ----- Properties

    public static final Property order = model.createProperty(NS + "order");

    public static final Property hasItem = model.createProperty(NS + "hasItem");

    public static final Property template = model.createProperty(NS + "template");

    public static final Property markers = model.createProperty(NS + "markers");

    public static final Property icon = model.createProperty(NS + "icon");

    public static final Property urlStem = model.createProperty(NS + "urlStem");

    public static final Property detailsUrlStem = model.createProperty(NS + "detailsUrlStem");

    public static final Property queryUrlStem = model.createProperty(NS + "queryUrlStem");

    public static final Property lastVisitedDate = model.createProperty(NS + "lastVisitedDate");

    public static final Property hasXslSource = model.createProperty(NS + "hasXslSource");

    public static final Property hasHtmlFragment = model.createProperty(NS + "hasHtmlFragment");

    public static final Property hasSource = model.createProperty(NS + "hasSource");

    public static final Property hasNewsItem = model.createProperty(NS + "hasNewsItem");

    public static final Property htmlLink = model.createProperty(NS + "htmlLink");

    public static final Property icalLink = model.createProperty(NS + "icalLink");

    public static final Property eventlist = model.createProperty(NS + "eventlist");

    public static final Property style = model.createProperty(NS + "style");
}
