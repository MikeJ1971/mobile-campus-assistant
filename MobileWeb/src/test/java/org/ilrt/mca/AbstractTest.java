package org.ilrt.mca;

import org.ilrt.mca.rdf.StoreWrapper;
import org.ilrt.mca.rdf.Repository;
import org.ilrt.mca.rdf.SdbRepositoryImpl;
import org.ilrt.mca.rdf.StoreWrapperManager;
import org.ilrt.mca.rdf.StoreWrapperManagerImpl;
import static org.junit.Assert.assertTrue;
import com.hp.hpl.jena.sdb.util.StoreUtils;

public class AbstractTest {

    protected void setUpStore() throws Exception {

        StoreWrapper storeWrapper = getStoreWrapper();

        if (StoreUtils.isFormatted(storeWrapper.getStore())) {
            storeWrapper.getStore().getTableFormatter().truncate();
        } else {
            storeWrapper.getStore().getTableFormatter().format();
        }

        assertTrue("The store is not formatted", StoreUtils.isFormatted(storeWrapper.getStore()));

        storeWrapper.close();
    }

    protected StoreWrapper getStoreWrapper() {
        return getStoreWrapperManager().getStoreWrapper();
    }

    protected Repository getRepository() {
        return new SdbRepositoryImpl(getStoreWrapperManager());
    }

    protected StoreWrapperManager getStoreWrapperManager() {
        return new StoreWrapperManagerImpl(TEST_CONFIG);
    }

    private String TEST_CONFIG = "/test-sdb.ttl";
}
