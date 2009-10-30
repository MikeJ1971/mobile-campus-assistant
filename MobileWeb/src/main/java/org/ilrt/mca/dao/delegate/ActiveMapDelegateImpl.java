package org.ilrt.mca.dao.delegate;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;
import org.apache.log4j.Logger;
import org.ilrt.mca.dao.AbstractDao;
import org.ilrt.mca.domain.Item;
import org.ilrt.mca.domain.map.ActiveMapItemImpl;
import org.ilrt.mca.domain.map.KmlMapItemImpl;
import org.ilrt.mca.rdf.Repository;
import org.ilrt.mca.vocab.GEO;
import org.ilrt.mca.vocab.MCA_REGISTRY;

import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class ActiveMapDelegateImpl extends AbstractDao implements Delegate {

    public ActiveMapDelegateImpl(final Repository repository) {
        this.repository = repository;
        try {
            activeMapDetailsSparql = loadSparql("/sparql/findActiveMapDetails.rql");
        } catch (IOException ex) {
            Logger log = Logger.getLogger(ActiveMapDelegateImpl.class);
            log.error("Unable to load SPARQL query: " + ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Item createItem(Resource resource, MultivaluedMap<String, String> parameters) {

        ActiveMapItemImpl item = new ActiveMapItemImpl();

        getBasicDetails(resource, item);

        if (resource.hasProperty(GEO.latitude)) {
            item.setLatitude(resource.getProperty(GEO.latitude).getDouble());
        }

        if (resource.hasProperty(GEO.longitude)) {
            item.setLongitude(resource.getProperty(GEO.longitude).getDouble());
        }

        if (resource.hasProperty(MCA_REGISTRY.markers)) {
            item.setMarkersLocation(resource.getProperty(MCA_REGISTRY.markers).getString());
        }

        if (resource.hasProperty(MCA_REGISTRY.icon)) {
            item.setMarkerIconLocation(resource.getProperty(MCA_REGISTRY.icon).getString());
        }

        if (resource.hasProperty(MCA_REGISTRY.urlStem)) {
            item.setProxyURLStem(resource.getProperty(MCA_REGISTRY.urlStem).getString());
        }

        return item;
    }

    @Override
    public Model createModel(Resource resource, MultivaluedMap<String, String> parameters) {

        Model model = repository.find("id", resource.getURI(), activeMapDetailsSparql);
        return ModelFactory.createUnion(resource.getModel(), model);
    }


    private String activeMapDetailsSparql = null;
    private final Repository repository;
}
