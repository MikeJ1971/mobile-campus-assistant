package org.ilrt.mca.dao;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import org.ilrt.mca.domain.BaseItem;
import org.ilrt.mca.domain.Item;
import org.ilrt.mca.domain.map.KmlMapItemImpl;
import org.ilrt.mca.rdf.ModelRepository;
import org.ilrt.mca.vocab.GEO;
import org.ilrt.mca.vocab.MCA_REGISTRY;

public class ItemDaoImpl implements ItemDao {

    public ItemDaoImpl(ModelRepository modelRepository) {
        this.modelRepository = modelRepository;
    }

    @Override
    public Item findItem(String id) {

        Model model = modelRepository.findItem(id);

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
                findMapDetails(item);
                return item;
            }
        }

        // fallback item
        BaseItem item = new BaseItem();
        getBasicDetails(resource, item);
        return item;

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

        // other
        if (resource.hasProperty(RDFS.seeAlso)) {
            item.setOtherSource(resource.getProperty(RDFS.seeAlso).getResource().getURI());
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

    }


    private void findMapDetails(KmlMapItemImpl item) {


        Model model = modelRepository.findMapDetails(item.getId());

        Resource resource = model.getResource(item.getId());

        if (resource.hasProperty(GEO.latitude)) {
            item.setLatitude(resource.getProperty(GEO.latitude).getDouble());
        }

        if (resource.hasProperty(GEO.longitude)) {
            item.setLongitude(resource.getProperty(GEO.longitude).getDouble());
        }
    }

    ModelRepository modelRepository = new ModelRepository();
}
