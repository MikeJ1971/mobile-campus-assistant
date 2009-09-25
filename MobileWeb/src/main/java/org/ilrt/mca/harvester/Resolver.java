package org.ilrt.mca.harvester;

import com.hp.hpl.jena.rdf.model.Model;

import java.util.Date;

/**
 * 
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public interface Resolver {
    Model resolve(String url, Date lastVisited, ResponseHandler responseHandler);
}
