package org.ilrt.mca.dao;

import com.hp.hpl.jena.rdf.model.Model;
import org.ilrt.mca.domain.Item;

import javax.ws.rs.core.MultivaluedMap;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public interface ItemDao {

    Item findItem(String id, MultivaluedMap<String, String> parameters);

    Model findModel(String id, MultivaluedMap<String, String> parameters);
}
