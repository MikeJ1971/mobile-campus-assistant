package org.ilrt.mca.rdf;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sdb.StoreDesc;
import com.hp.hpl.jena.sdb.SDBFactory;
import com.hp.hpl.jena.sdb.util.StoreUtils;

import java.io.InputStream;
import java.sql.SQLException;

public class StoreWrapperManagerImpl implements StoreWrapperManager {

    /**
     * When the class is instantiated it checks to see if the underlying database
     * has been correctly formatted. If not, it will format the store so that
     * data can be stored.
     *
     * @param configLocation the SDB configuration file.
     */
    public StoreWrapperManagerImpl(String configLocation) {

        Model ttl = ModelFactory.createDefaultModel();
        InputStream input = getClass().getResourceAsStream(configLocation);

        if (input == null) {
            throw new RuntimeException("Config file " + configLocation
                    + " not found in classpath");
        }

        ttl.read(input, null, "TTL");

        this.storeDesc = StoreDesc.read(ttl);

        // get a store and check the underlying database is formatted
        StoreWrapper storeWrapper = getStoreWrapper();

        try {
            if (!StoreUtils.isFormatted(storeWrapper.getStore())) {
                storeWrapper.getStore().getTableFormatter().format();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        storeWrapper.close();
    }

    public StoreWrapper getStoreWrapper() {
        return new StoreWrapper(SDBFactory.connectStore(storeDesc));
    }

    // holds the description of the store
    private StoreDesc storeDesc;
}
