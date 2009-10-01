package org.ilrt.mca.dao;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import org.ilrt.mca.Common;
import org.ilrt.mca.domain.BaseItem;
import org.ilrt.mca.domain.Item;
import org.ilrt.mca.domain.map.KmlMapItemImpl;
import org.ilrt.mca.rdf.Repository;
import org.ilrt.mca.vocab.GEO;
import org.ilrt.mca.vocab.MCA_REGISTRY;

import java.util.Collections;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class ItemDaoImpl implements ItemDao {

    public ItemDaoImpl(Repository repository) throws Exception {
        this.repository = repository;

        Common common = new Common();
        findItemsSparql = common.loadSparql("/sparql/findItems.rql");
        kmlMapDetailsSparql = common.loadSparql("/sparql/findKmlMapDetails.rql");
    }

    @Override
    public Item findItem(String id) {

        Model model = findModel(id);

        if (model.isEmpty()) {
            return null;
        }

        Resource resource = model.getResource(id);

        // are we a specific type
        if (resource.hasProperty(RDF.type)) {

            // kml map source ?
            if (resource.getProperty(RDF.type).getResource().getURI()
                    .equals(MCA_REGISTRY.KmlMapSource.getURI())) {
                KmlMapItemImpl item = new KmlMapItemImpl();
                getBasicDetails(resource, item);
                findMapDetails(item, resource);
                return item;
            }
        }

        // fallback item
        BaseItem item = new BaseItem();
        getBasicDetails(resource, item);
        return item;

    }

    @Override
    public Model findModel(String id) {

        Model model = repository.find("id", id, findItemsSparql);

        if (model.isEmpty()) {
            return null;
        }

        Resource resource = model.getResource(id);

        // are we a specific type
        if (resource.hasProperty(RDF.type)) {

            // kml map source ?
            if (resource.getProperty(RDF.type).getResource().getURI()
                    .equals(MCA_REGISTRY.KmlMapSource.getURI())) {

                Model kmlModel = repository.find("id", id, kmlMapDetailsSparql);
                model = ModelFactory.createUnion(model, kmlModel);
            }
        }

        return model;
    }

    private void getBasicDetails(Resource resource, BaseItem item) {

        // identifier
        item.setId(resource.getURI());

        // label
        if (resource.hasProperty(RDFS.label)) {
            item.setLabel(resource.getProperty(RDFS.label).getLiteral().getLexicalForm());
        }

        // description
        if (resource.hasProperty(DC.description)) {
            item.setDescription(resource.getProperty(DC.description).getLiteral().getLexicalForm());
        }

        // order
        if (resource.hasProperty(MCA_REGISTRY.order)) {
            item.setOrder(resource.getProperty(MCA_REGISTRY.order).getInt());
        }

        // template
        if (resource.hasProperty(MCA_REGISTRY.template)) {
            item.setTemplate(resource.getProperty(MCA_REGISTRY.template).getResource().getURI());
        }

        // type
        if (resource.hasProperty(RDF.type)) {
            item.setType(resource.getProperty(RDF.type).getResource().getURI());
        }

        // items
        StmtIterator iter = resource.listProperties(MCA_REGISTRY.hasItem);

        while (iter.hasNext()) {

            Statement stmt = iter.nextStatement();

            Resource r = stmt.getResource();
            BaseItem link = new BaseItem();
            getBasicDetails(r, link);
            item.getItems().add(link);
        }

        Collections.sort(item.getItems());
    }


    private void findMapDetails(KmlMapItemImpl item, Resource resource) {

        if (resource.hasProperty(GEO.latitude)) {
            item.setLatitude(resource.getProperty(GEO.latitude).getDouble());
        }

        if (resource.hasProperty(GEO.longitude)) {
            item.setLongitude(resource.getProperty(GEO.longitude).getDouble());
        }

        if (resource.hasProperty(RDFS.seeAlso)) {
            item.setKmlUrl(resource.getProperty(RDFS.seeAlso).getResource().getURI());
        }
    }


    private String findItemsSparql = null;
    private String kmlMapDetailsSparql = null;
    private Repository repository;
}
