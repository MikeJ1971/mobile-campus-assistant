package org.ilrt.mca.rdf;

import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.sdb.SDBFactory;
import com.hp.hpl.jena.sdb.util.StoreUtils;
import com.hp.hpl.jena.vocabulary.DC;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

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
        Model beforeModel = SDBFactory.connectDefaultModel(getStoreWrapper().getStore());
        assertEquals("The model should be empty", 0, beforeModel.size());

        // add some data
        Repository repository = getRepository();
        repository.add(getTestData());

        // test that the database is not empty
        Model afterModel = SDBFactory.connectDefaultModel(getStoreWrapper().getStore());
        assertEquals("The model should not be empty", 1, afterModel.size());
    }

    @Test
    public void deleteModel() throws Exception {

        // test that the database is empty
        Model beforeModel = SDBFactory.connectDefaultModel(getStoreWrapper().getStore());
        assertEquals("The model should be empty", 0, beforeModel.size());

        // add some data
        Repository repository = getRepository();
        repository.add(getTestData());

        // test that the database is not empty
        Model afterModel = SDBFactory.connectDefaultModel(getStoreWrapper().getStore());
        assertEquals("The model should not be empty", 1, afterModel.size());

        // try deleting
        repository.delete(getTestData());

        // test that the database is now empty
        Model afterDeleteModel = SDBFactory.connectDefaultModel(getStoreWrapper().getStore());
        assertEquals("The model should be empty", 0, afterDeleteModel.size());
    }


    @Test
    public void findWithBindings() throws Exception {

        // add some data
        Repository repository = getRepository();
        repository.add(getTestData());

        // test that the database is not empty
        Model afterModel = SDBFactory.connectDefaultModel(getStoreWrapper().getStore());
        assertEquals("The model should not be empty", 1, afterModel.size());

        QuerySolutionMap bindings = new QuerySolutionMap();
        bindings.add("id", ResourceFactory.createResource(uri));
        Model results = repository.find(bindings, query);

        assertEquals("The results should not be empty", 1, results.size());
    }

    @Test
    public void findWithJustSparql() throws Exception {

        // add some data
        Repository repository = getRepository();
        repository.add(getTestData());

        // test that the database is not empty
        Model afterModel = SDBFactory.connectDefaultModel(getStoreWrapper().getStore());
        assertEquals("The model should not be empty", 1, afterModel.size());

        Model results = repository.find(query);

        assertEquals("The results should not be empty", 1, results.size());
    }

    @Test
    public void findWithBindingId() throws Exception {

        // add some data
        Repository repository = getRepository();
        repository.add(getTestData());

        // test that the database is not empty
        Model afterModel = SDBFactory.connectDefaultModel(getStoreWrapper().getStore());
        assertEquals("The model should not be empty", 1, afterModel.size());

        Model results = repository.find("id", uri, query);

        assertEquals("The results should not be empty", 1, results.size());
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

    Repository getRepository() {
        return new SdbRepositoryImpl(getStoreWrapperManager());
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

    String query = "PREFIX dc: <http://purl.org/dc/elements/1.1/> " +
            "CONSTRUCT { ?id dc:title ?title } WHERE { ?id dc:title ?title }";
}




