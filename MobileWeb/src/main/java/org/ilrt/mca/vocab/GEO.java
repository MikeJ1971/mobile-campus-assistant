package org.ilrt.mca.vocab;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Property;

public class GEO {

    private static final Model model = ModelFactory.createDefaultModel();

    public static final String NS = "http://www.w3.org/2003/01/geo/wgs84_pos#";

    public static String getURI() {
        return NS;
    }

    public static final Resource NAMESPACE = model.createResource(NS);

    public static final Resource Position = model.createResource(NS + "Point");

    public static final Property latitude = model.createProperty(NS + "lat");

    public static final Property longitude = model.createProperty(NS + "long");

}
