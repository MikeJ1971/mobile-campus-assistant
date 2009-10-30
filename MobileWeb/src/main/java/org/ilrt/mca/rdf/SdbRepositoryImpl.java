package org.ilrt.mca.rdf;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.sdb.SDBFactory;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class SdbRepositoryImpl implements Repository {

    public SdbRepositoryImpl(final StoreWrapperManager manager) {
        this.manager = manager;
    }

    @Override
    public Model find(String sparql) {
        return find(null, sparql);
    }

    @Override
    public Model find(String bindingId, String id, String sparql) {

        QuerySolutionMap bindings = new QuerySolutionMap();
        bindings.add(bindingId, ResourceFactory.createResource(id));
        return find(bindings, sparql);
    }

    @Override
    public Model find(QuerySolutionMap bindings, String sparql) {

        // get a store
        StoreWrapper storeWrapper = manager.getStoreWrapper();

        // dataset to query
        Dataset dataset = SDBFactory.connectDataset(storeWrapper.getStore());

        // query
        QueryExecution qe;
        if (bindings != null) {
            qe = QueryExecutionFactory.create(sparql, dataset, bindings);
        } else {
            qe = QueryExecutionFactory.create(sparql, dataset);
        }

        Model results = qe.execConstruct();

        //results.write(System.out);

        // clean up and returm
        qe.close();
        storeWrapper.close();
        return results;
    }

    @Override
    public void add(Model model) {
        StoreWrapper storeWrapper = manager.getStoreWrapper();
        Model sdbModel = SDBFactory.connectDefaultModel(storeWrapper.getStore());
        sdbModel.add(model);
        storeWrapper.close();
    }

    @Override
    public void add(String graphUri, Model model) {
        StoreWrapper storeWrapper = manager.getStoreWrapper();
        Model sdbModel = SDBFactory.connectNamedModel(storeWrapper.getStore(), graphUri);
        sdbModel.add(model);
        storeWrapper.close();
    }

    @Override
    public Model get(String graphUri) {
        StoreWrapper storeWrapper = manager.getStoreWrapper();
        Model sdbModel = SDBFactory.connectNamedModel(storeWrapper.getStore(), graphUri);
        Model results = ModelFactory.createDefaultModel();
        
        Writer out = new StringWriter();
        sdbModel.write(out);
        storeWrapper.close();

        Reader in = new StringReader(out.toString());
        results.read(in,graphUri);
        return results;
    }

    @Override
    public void delete(String graphUri, Model model) {
        StoreWrapper storeWrapper = manager.getStoreWrapper();
        Model sdbModel = SDBFactory.connectNamedModel(storeWrapper.getStore(), graphUri);
        sdbModel.remove(model);
        storeWrapper.close();
    }

    @Override
    public void delete(Model model) {
        StoreWrapper storeWrapper = manager.getStoreWrapper();
        Model sdbModel = SDBFactory.connectDefaultModel(storeWrapper.getStore());
        sdbModel.remove(model);
        sdbModel.close();
        storeWrapper.close();
    }

    @Override
    public void updatePropertyInGraph(String graphUri, String uri, Property property,
                                      RDFNode value) {

        StoreWrapper storeWrapper = manager.getStoreWrapper();
        Model sdbModel = SDBFactory.connectNamedModel(storeWrapper.getStore(), graphUri);
        Resource resource = sdbModel.getResource(uri);

        if (resource.hasProperty(property)) {
            resource.getProperty(property).changeObject(value);
        } else {
            resource.addProperty(property, value);
        }

        sdbModel.close();
        storeWrapper.close();
    }

    @Override
    public void deleteAllInGraph(String graphUri) {

        StoreWrapper storeWrapper = manager.getStoreWrapper();

        Model model;

        if (graphUri != null) {
            model = SDBFactory.connectNamedModel(storeWrapper.getStore(), graphUri);
        } else {
            model = SDBFactory.connectDefaultModel(storeWrapper.getStore());
        }

        model.removeAll();
        model.close();
        storeWrapper.close();
    }

    private final StoreWrapperManager manager;
}
