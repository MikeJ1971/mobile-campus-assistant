package org.ilrt.mca;

import javax.ws.rs.core.MediaType;

/**
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class RdfMediaType {

    private RdfMediaType() {
    }

    public final static String APPLICATION_RDF_XML = "application/rdf+xml";

    public final static MediaType APPLICATION_RDF_XML_TYPE =
            new MediaType("application", "rdf+xml");

    public final static String TEXT_RDF_N3 = "text/n3";

    public final static MediaType TEXT_RDF_N3_TYPE =
            new MediaType("text", "n3");

}
