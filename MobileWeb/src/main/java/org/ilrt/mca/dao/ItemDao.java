package org.ilrt.mca.dao;

import org.ilrt.mca.domain.Item;
import com.hp.hpl.jena.rdf.model.Model;

public interface ItemDao {

    Item findItem(String id);

    Model findModel(String id);
}
