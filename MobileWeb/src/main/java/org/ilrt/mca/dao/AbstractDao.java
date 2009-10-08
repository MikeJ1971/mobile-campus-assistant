package org.ilrt.mca.dao;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDF;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;

import org.ilrt.mca.domain.BaseItem;
import org.ilrt.mca.vocab.MCA_REGISTRY;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public abstract class AbstractDao {

        protected void getBasicDetails(Resource resource, BaseItem item) {

        if (resource == null) {
            return;
        }

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

    public String loadSparql(String path) throws IOException {

        StringBuffer buffer = new StringBuffer();
        InputStream is = getClass().getResourceAsStream(path);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
            buffer.append("\n");
        }

        return buffer.toString();
    }

}
