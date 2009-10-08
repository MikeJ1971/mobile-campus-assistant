package org.ilrt.mca.dao.delegate;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;
import org.apache.log4j.Logger;
import org.ilrt.mca.dao.AbstractDao;
import org.ilrt.mca.domain.Item;
import org.ilrt.mca.domain.map.KmlMapItemImpl;
import org.ilrt.mca.rdf.Repository;
import org.ilrt.mca.vocab.GEO;

import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;


public class KmlMapDelegateImpl extends AbstractDao implements Delegate {

    public KmlMapDelegateImpl(final Repository repository) {
        this.repository = repository;
        try {
            kmlMapDetailsSparql = loadSparql("/sparql/findKmlMapDetails.rql");
        } catch (IOException ex) {
            Logger log = Logger.getLogger(KmlMapDelegateImpl.class);
            log.error("Unable to load SPARQL query: " + ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Item createItem(Resource resource, MultivaluedMap<String, String> parameters) {

        KmlMapItemImpl item = new KmlMapItemImpl();

        getBasicDetails(resource, item);

        if (resource.hasProperty(GEO.latitude)) {
            item.setLatitude(resource.getProperty(GEO.latitude).getDouble());
        }

        if (resource.hasProperty(GEO.longitude)) {
            item.setLongitude(resource.getProperty(GEO.longitude).getDouble());
        }

        if (resource.hasProperty(RDFS.seeAlso)) {
            item.setKmlUrl(resource.getProperty(RDFS.seeAlso).getResource().getURI());
        }

        return item;
    }

    @Override
    public Model createModel(Resource resource, MultivaluedMap<String, String> parameters) {

        Model kmlModel = repository.find("id", resource.getURI(), kmlMapDetailsSparql);
        return ModelFactory.createUnion(resource.getModel(), kmlModel);
    }


    private String kmlMapDetailsSparql = null;
    private final Repository repository;
}
