package org.ilrt.mca.rdf;

import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class StoreWrapperImplTest {

    @Test
    public void testWrapper() {

        assertTrue(true);
    }

    @Test
    public void testStore() throws SQLException {

        // get the path location
        StoreWrapperManager storeManager = new StoreWrapperManagerImpl(TEST_CONFIG);

        // test getting the store
        StoreWrapper storeWrapper = storeManager.getStoreWrapper();
        assertNotNull("The store wrapper should not be null", storeWrapper);
        assertNotNull("The store should not be null", storeWrapper.getStore());
        assertFalse("Connection should not be closed", storeWrapper.getStore()
                .getConnection().getSqlConnection().isClosed());

        // test closing the store
        storeWrapper.close();
        assertTrue("Connection should be closed", storeWrapper.getStore()
                .getConnection().getSqlConnection().isClosed());
    }


    private String TEST_CONFIG = "/test-sdb.ttl";
}
