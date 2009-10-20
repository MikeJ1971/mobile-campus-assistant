package org.ilrt.mca.vocab;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Property;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class FOAF {

    private static final Model model = ModelFactory.createDefaultModel();

    public static final String NS = "http://xmlns.com/foaf/0.1/";

    public static String getURI() {
        return NS;
    }

    public static final Resource NAMESPACE = model.createResource(NS);

    public static final Property mbox = model.createProperty(NS + "mbox");

    public static final Property phone = model.createProperty(NS + "phone");
}
