package org.ilrt.mca.rdf;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sdb.StoreDesc;
import com.hp.hpl.jena.sdb.util.StoreUtils;

import java.io.InputStream;
import java.sql.SQLException;

public abstract class AbstractStoreWrapperManagerImpl implements StoreWrapperManager {

    public AbstractStoreWrapperManagerImpl(String configLocation) {
        this.storeDesc = getStoreDesc(configLocation);
    }

    protected StoreDesc getStoreDesc(String configLocation) {

        Model ttl = ModelFactory.createDefaultModel();
        InputStream input = getClass().getResourceAsStream(configLocation);

        if (input == null) {
            throw new RuntimeException("Config file " + configLocation
                    + " not found in classpath");
        }

        ttl.read(input, null, "TTL");

        return StoreDesc.read(ttl);
    }


    protected void prepareDatabase(StoreWrapper wrapper) {
        try {
            if (!StoreUtils.isFormatted(wrapper.getStore())) {
                wrapper.getStore().getTableFormatter().format();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        wrapper.close();
    }


    @Override
    public abstract StoreWrapper getStoreWrapper();

    protected StoreDesc storeDesc;
}
