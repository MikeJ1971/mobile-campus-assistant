package org.ilrt.mca.services;

import com.hp.hpl.jena.rdf.model.Resource;

/**
 * MCA needs to be able to integrate and query third party services. This provides a
 * generic interface that should be implemented by an entry point to these services.
 * Ideally, each implementation should be held in its own module with the name
 * convention mca-services-XXX, e.g. mca-services-ldap.
 *
 * @param <T> the arguments needed by the service.
 */
public interface SearchService<T> {

    Resource search(T ... args);

}
