package org.ilrt.mca.rdf;

import com.hp.hpl.jena.rdf.model.Model;

/**
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public interface Repository {

    Model findItem(String id);

    Model findMapDetails(String id);
}
