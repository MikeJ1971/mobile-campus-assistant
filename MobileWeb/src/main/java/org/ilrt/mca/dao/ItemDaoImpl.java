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
import org.ilrt.mca.rdf.ModelRepository;
import org.ilrt.mca.vocab.MCA_REGISTRY;

public class ItemDaoImpl implements ItemDao {

    public ItemDaoImpl(ModelRepository modelRepository) {
        this.modelRepository = modelRepository;
    }

    @Override
    public Item findItem(String id) {

        Model model = modelRepository.findItem(id);

        Resource resource = model.getResource(id);

        return getItem(resource);
    }

    @Override
    public Item findHomePage() {

        Model results = modelRepository.findHomePage();

        return getItem(results.getResource("mca://registry/"));
    }

    private BaseItem getItem(Resource resource) {

        BaseItem item = new BaseItem();

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
            BaseItem h = getItem(r);
            item.getItems().add(h);
        }
        return item;
    }


    ModelRepository modelRepository = new ModelRepository();
}
