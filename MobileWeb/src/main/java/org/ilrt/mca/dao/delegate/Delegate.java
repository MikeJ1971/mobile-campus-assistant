package org.ilrt.mca.dao.delegate;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import org.ilrt.mca.domain.Item;

import javax.ws.rs.core.MultivaluedMap;

public interface Delegate {

    Item createItem(Resource resource, MultivaluedMap<String, String> parameters);

    Model createModel(Resource resource, MultivaluedMap<String, String> parameters);
}
