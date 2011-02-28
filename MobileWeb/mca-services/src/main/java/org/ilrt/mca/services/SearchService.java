package org.ilrt.mca.services;

import com.hp.hpl.jena.rdf.model.Resource;

public interface SearchService<T> {

    Resource search(T ... args);

}
