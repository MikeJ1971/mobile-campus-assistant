package org.ilrt.mca.vocab;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Property;

public class MCA_REGISTRY {

    private static final Model model = ModelFactory.createDefaultModel();

    public static final String NS = "http://org.ilrt.mca/schema#";

    public static String getURI() {
        return NS;
    }

    public static final Resource NAMESPACE = model.createResource(NS);

    public static final Resource WorkBench = model.createResource(NS + "Group");

    public static final Property order = model.createProperty(NS + "order");
}
