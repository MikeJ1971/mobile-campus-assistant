package org.ilrt.mca.rdf;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.sdb.SDBFactory;
import com.hp.hpl.jena.sdb.util.StoreUtils;
import com.hp.hpl.jena.vocabulary.DC;
import org.ilrt.mca.Common;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class SdbRepositoryImplTest {

    @Before
    public void setUp() throws Exception {

        setUpStore();
        StoreWrapper storeWrapper = getStoreWrapper();
        assertTrue("The store is not formatted", StoreUtils.isFormatted(storeWrapper.getStore()));
        storeWrapper.close();
    }


    @Test
    public void addModel() throws Exception {

        // test that the database is empty
        StoreWrapper storeWrapper = getStoreWrapper();
        Model beforeModel = SDBFactory.connectDefaultModel(storeWrapper.getStore());
        assertEquals("The model should be empty", 0, beforeModel.size());
        storeWrapper.close();

        // add some data
        UpdateManager repository = getRepository();
        repository.add(getTestData());

        // test that the database is not empty
        storeWrapper = getStoreWrapper();
        Model afterModel = SDBFactory.connectDefaultModel(storeWrapper.getStore());
        assertEquals("The model should not be empty", 1, afterModel.size());
        storeWrapper.close();
    }


    @Test
    public void addModelToGraph() throws Exception {

        // test that the graph isn't stored
        StoreWrapper storeWrapper = getStoreWrapper();
        Dataset dataset = SDBFactory.connectDataset(storeWrapper.getStore());
        Model beforeModel = dataset.getNamedModel(graphUri);
        assertEquals("The model should be empty", 0, beforeModel.size());
        storeWrapper.close();

        // add some data to the graph
        UpdateManager repository = getRepository();
        repository.add(graphUri, getTestData());

        // test that the graph is stored
        storeWrapper = getStoreWrapper();
        Dataset afterDataset = SDBFactory.connectDataset(storeWrapper.getStore());
        Model afterModel = afterDataset.getNamedModel(graphUri);
        assertEquals("The model should not be empty", 1, afterModel.size());
        storeWrapper.close();
    }


    @Test
    public void deleteModelFromGraph() throws Exception {

        // test that the graph is stored
        StoreWrapper storeWrapper = getStoreWrapper();
        Dataset dataset = SDBFactory.connectDataset(storeWrapper.getStore());
        dataset.getNamedModel(graphUri).add(getTestData());
        Model beforeModel = dataset.getNamedModel(graphUri);
        assertEquals("The model should not be empty", 1, beforeModel.size());
        storeWrapper.close();

        // remove data from the graph
        UpdateManager repository = getRepository();
        repository.delete(graphUri, getTestData());

        // test that the graph isn't stored
        storeWrapper = getStoreWrapper();
        Dataset afterDataset = SDBFactory.connectDataset(getStoreWrapper().getStore());
        Model afterModel = afterDataset.getNamedModel(graphUri);
        assertEquals("The model should be empty", 0, afterModel.size());
        storeWrapper.close();
    }

    @Test
    public void deleteModel() throws Exception {

        // test that the database is empty
        StoreWrapper storeWrapper = getStoreWrapper();
        Model beforeModel = SDBFactory.connectDefaultModel(storeWrapper.getStore());
        assertEquals("The model should be empty", 0, beforeModel.size());
        storeWrapper.close();

        // add some data
        UpdateManager repository = getRepository();
        repository.add(getTestData());

        // test that the database is not empty
        storeWrapper = getStoreWrapper();
        Model afterModel = SDBFactory.connectDefaultModel(storeWrapper.getStore());
        assertEquals("The model should not be empty", 1, afterModel.size());
        storeWrapper.close();

        // try deleting
        repository.delete(getTestData());

        // test that the database is now empty
        storeWrapper = getStoreWrapper();
        Model afterDeleteModel = SDBFactory.connectDefaultModel(storeWrapper.getStore());
        assertEquals("The model should be empty", 0, afterDeleteModel.size());
        storeWrapper.close();
    }


    @Test
    public void findWithBindings() throws Exception {

        // add some data
        DataManager repository = getRepository();
        repository.add(getTestData());

        // test that the database is not empty
        StoreWrapper storeWrapper = getStoreWrapper();
        Model afterModel = SDBFactory.connectDefaultModel(storeWrapper.getStore());
        assertEquals("The model should not be empty", 1, afterModel.size());

        QuerySolutionMap bindings = new QuerySolutionMap();
        bindings.add("id", ResourceFactory.createResource(uri));
        Model results = repository.find(bindings, query);

        assertEquals("The results should not be empty", 1, results.size());
        storeWrapper.close();
    }

    @Test
    public void findWithJustSparql() throws Exception {

        // add some data
        DataManager repository = getRepository();
        repository.add(getTestData());

        // test that the database is not empty
        StoreWrapper storeWrapper = getStoreWrapper();
        Model afterModel = SDBFactory.connectDefaultModel(storeWrapper.getStore());
        assertEquals("The model should not be empty", 1, afterModel.size());

        Model results = repository.find(query);

        assertEquals("The results should not be empty", 1, results.size());
        storeWrapper.close();
    }

    @Test
    public void findWithBindingId() throws Exception {

        // add some data
        DataManager dataManager = getRepository();
        dataManager.add(getTestData());

        // test that the database is not empty
        StoreWrapper storeWrapper = getStoreWrapper();
        Model afterModel = SDBFactory.connectDefaultModel(storeWrapper.getStore());
        assertEquals("The model should not be empty", 1, afterModel.size());

        Model results = dataManager.find("id", uri, query);

        assertEquals("The results should not be empty", 1, results.size());
        storeWrapper.close();
    }


    @Test
    public void updatePropertyValueInGraph() {

        // test data
        String uri = "mca://registry/news/events/";
        String graph = "mca://audit/";

        Calendar calendar = new GregorianCalendar(2009, Calendar.SEPTEMBER, 30, 11, 38);
        String date = Common.parseXsdDate(calendar.getTime());

        // add some data
        StoreWrapper storeWrapper = getStoreWrapper();

        Model beforeModel = SDBFactory.connectDataset(storeWrapper.getStore())
                .getNamedModel(graph);
        Resource resource = beforeModel.getResource(uri);
        beforeModel.add(beforeModel.createStatement(resource, DC.date,
                beforeModel.createTypedLiteral(date, XSDDatatype.XSDdateTime)));

        // check we have expected data
        assertTrue("The model should have a dc:date", beforeModel.getResource(uri)
                .hasProperty(DC.date));
        assertEquals("Unexpected date value", date, beforeModel.getResource(uri)
                .getProperty(DC.date).getLiteral().getLexicalForm());

        // clean up
        beforeModel.close();
        storeWrapper.close();

        UpdateManager repository = getRepository();

        String newdate = Common.parseXsdDate(new Date());

        repository.updatePropertyInGraph(graph, uri, DC.date,
                ModelFactory.createDefaultModel().createTypedLiteral(newdate,
                        XSDDatatype.XSDdateTime));

        storeWrapper = getStoreWrapper();
        Model afterModel = SDBFactory.connectDataset(storeWrapper.getStore())
                .getNamedModel(graph);

        String changedDate = afterModel.getResource(uri).getProperty(DC.date).getLiteral()
                .getLexicalForm();

        assertFalse("The dates should be different", changedDate.equals(date));
        storeWrapper.close();

    }


    @Test
    public void testDeleteDataInGraph() throws Exception {

        setUpStore();
        String graph = "http://example.org/graph";

        StoreWrapper storeWrapper = getStoreWrapper();
        Model model = SDBFactory.connectNamedModel(storeWrapper.getStore(), graph);

        assertTrue("The graph should be empty", model.isEmpty());

        model.add(getTestData());

        assertFalse("The model should not be empty", model.isEmpty());

        model.close();

        UpdateManager repository = getRepository();
        repository.deleteAllInGraph(graph);

        storeWrapper = getStoreWrapper();
        model = SDBFactory.connectNamedModel(storeWrapper.getStore(), graph);

        assertTrue("The graph should be empty", model.isEmpty());
    }

    @Test
    public void testDeleteDataInDefaultGraph() throws Exception {

        setUpStore();

        StoreWrapper storeWrapper = getStoreWrapper();
        Model model = SDBFactory.connectDefaultModel(storeWrapper.getStore());

        assertTrue("The graph should be empty", model.isEmpty());

        model.add(getTestData());

        assertFalse("The model should not be empty", model.isEmpty());

        model.close();

        UpdateManager repository = getRepository();
        repository.deleteAllInGraph(null);

        storeWrapper = getStoreWrapper();
        model = SDBFactory.connectDefaultModel(storeWrapper.getStore());

        assertTrue("The graph should be empty", model.isEmpty());
    }


    // -- Utility methods

    void setUpStore() throws Exception {

        StoreWrapper storeWrapper = getStoreWrapper();

        if (StoreUtils.isFormatted(storeWrapper.getStore())) {
            storeWrapper.getStore().getTableFormatter().truncate();
        } else {
            storeWrapper.getStore().getTableFormatter().format();
        }

        assertTrue("The store is not formatted", StoreUtils.isFormatted(storeWrapper.getStore()));

        storeWrapper.close();
    }

    StoreWrapper getStoreWrapper() {
        return getStoreWrapperManager().getStoreWrapper();
    }

    DataManager getRepository() {
        return new SdbManagerImpl(getStoreWrapperManager());
    }

    StoreWrapperManager getStoreWrapperManager() {
        return new StoreWrapperManagerImpl(TEST_CONFIG);
    }

    Model getTestData() {

        Model model = ModelFactory.createDefaultModel();
        Resource resource = model.getResource(uri);
        Statement stmt = model.createStatement(resource, DC.title,
                model.createLiteral("Test Data"));
        model.add(stmt);

        return model;
    }

    final String TEST_CONFIG = "/test-sdb.ttl";

    String uri = "http://example.org";

    String graphUri = "http://example.org/graph1";

    String query = "PREFIX dc: <http://purl.org/dc/elements/1.1/> " +
            "CONSTRUCT { ?id dc:title ?title } WHERE { ?id dc:title ?title }";
}




