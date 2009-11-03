package org.ilrt.mca.vocab;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Property;

/**
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class EVENT {

    private static final Model model = ModelFactory.createDefaultModel();

    public static final String NS = "http://www.w3.org/2002/12/cal/ical#";

    public static String getURI() {
        return NS;
    }

    public static final Resource NAMESPACE = model.createResource(NS);
    
    public static final Resource event = model.createResource(NS + "Vevent");

    public static final Property startDate = model.createProperty(NS + "dtstart");

    public static final Property endDate = model.createProperty(NS + "dtend");

    public static final Property organizerName = model.createProperty(NS + "organizer");

    public static final Property organizerEmail = model.createProperty(NS + "calAddress");

    public static final Property location = model.createProperty(NS + "location");

    public static final Property subject = model.createProperty(NS + "summary");

    public static final Property description = model.createProperty(NS + "description");

    public static final Property dateTime = model.createProperty(NS + "dateTime");

    public static final Property UID = model.createProperty(NS + "uid");
}
