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

    public static final Resource HtmlFragment = model.createProperty(NS + "HtmlFragment");

    public static final Resource Contact = model.createProperty(NS + "Contact");

    public static final Resource EventCalendar = model.createProperty(NS + "EventCalendar");

    // ----- Properties

    public static final Property order = model.createProperty(NS + "order");

    public static final Property hasItem = model.createProperty(NS + "hasItem");

    public static final Property template = model.createProperty(NS + "template");

    public static final Property markers = model.createProperty(NS + "markers");

    public static final Property icon = model.createProperty(NS + "icon");

    public static final Property urlStem = model.createProperty(NS + "urlStem");

    public static final Property lastVisitedDate = model.createProperty(NS + "lastVisitedDate");

    public static final Property hasXslSource = model.createProperty(NS + "hasXslSource");

    public static final Property hasHtmlFragment = model.createProperty(NS + "hasHtmlFragment");

    public static final Property hasSource = model.createProperty(NS + "hasSource");

    public static final Property hasNewsItem = model.createProperty(NS + "hasNewsItem");

    public static final Property htmlLink = model.createProperty(NS + "htmlLink");

    public static final Property icalLink = model.createProperty(NS + "icalLink");
}
