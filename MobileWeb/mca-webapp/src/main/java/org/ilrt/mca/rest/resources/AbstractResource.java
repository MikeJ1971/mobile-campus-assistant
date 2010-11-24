package org.ilrt.mca.rest.resources;

import org.ilrt.mca.rdf.ConnPoolStoreWrapperManagerImpl;
import org.ilrt.mca.rdf.DataSourceManager;
import org.ilrt.mca.rdf.StoreWrapperManager;

public abstract class AbstractResource {

    public AbstractResource() {
        DataSourceManager dataSourceManager = new DataSourceManager();
        manager = new ConnPoolStoreWrapperManagerImpl("/sdb.ttl",
                dataSourceManager.getDataSource());
    }

    protected StoreWrapperManager manager;
}
