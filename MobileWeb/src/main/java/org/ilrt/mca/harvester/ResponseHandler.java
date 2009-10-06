package org.ilrt.mca.harvester;

import com.hp.hpl.jena.rdf.model.Model;

import java.io.InputStream;

/**
 * A response handler is responsable for taking an InputStream and converting it to a
 * Jena model. It also specifies what media types it supports.
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public interface ResponseHandler {

    Model getModel(String sourceUri, InputStream is);

    boolean isSupportedMediaType(String mediaType);
}
