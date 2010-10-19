package org.ilrt.mca.rdf;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


public class ConnPoolStoreWrapperManagerImplTest {

    @Test
    public void testFailedConfigFile() {

        try {
            wrapperManager = new ConnPoolStoreWrapperManagerImpl("/sdb-not.ttl", null);
            fail("No exception was thrown");
        } catch (RuntimeException ex) {
            assertEquals("Unexpected message", "Config file /sdb-not.ttl not found in classpath", ex.getMessage());
        }
    }

    @Test
    public void testNullDataSource() {

        try {
            wrapperManager = new ConnPoolStoreWrapperManagerImpl("/sdb.ttl", null);
            fail("No exception was thrown");
        } catch (RuntimeException ex) {
            assertEquals("Unexpected message", "The data source is null, we won't be able "
                    + "to create a database connection", ex.getMessage());
        }


    }

    StoreWrapperManager wrapperManager;
}
