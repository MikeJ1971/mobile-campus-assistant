package org.ilrt.mca.dao;

import org.ilrt.mca.domain.Item;
import com.hp.hpl.jena.rdf.model.Model;

/**
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public interface ItemDao {

    Item findItem(String id);

    Model findModel(String id);
}
