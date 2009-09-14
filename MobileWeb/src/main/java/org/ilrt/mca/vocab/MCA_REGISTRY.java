package org.ilrt.mca.vocab;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Property;

/**
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class MCA_REGISTRY {

    private static final Model model = ModelFactory.createDefaultModel();

    public static final String NS = "http://org.ilrt.mca/registry#";

    public static String getURI() {
        return NS;
    }

    public static final Resource NAMESPACE = model.createResource(NS);

    public static final Resource Group = model.createResource(NS + "Group");

    public static final Resource KmlMapSource = model.createResource(NS + "KmlMapSource");

    public static final Property order = model.createProperty(NS + "order");

    public static final Property hasItem = model.createProperty(NS + "hasItem");

    public static final Property template = model.createProperty(NS + "template");
}
