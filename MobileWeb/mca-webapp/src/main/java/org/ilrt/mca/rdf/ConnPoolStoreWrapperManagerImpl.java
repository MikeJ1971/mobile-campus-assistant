package org.ilrt.mca.rdf;

import com.hp.hpl.jena.sdb.SDBFactory;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.SQLException;

public class ConnPoolStoreWrapperManagerImpl extends AbstractStoreWrapperManagerImpl {

    public ConnPoolStoreWrapperManagerImpl(String configLocation, DataSource dataSource) {

        // call the super class to initialise the store description
        super(configLocation);

        // assign and check the validity of the data source
        this.dataSource = dataSource;

        if (this.dataSource == null) {
            throw new RuntimeException("The data source is null, we won't be able to create a database connection");
        }

        // prepare the database
        StoreWrapper wrapper = getStoreWrapper();
        prepareDatabase(wrapper);
    }


    @Override
    public StoreWrapper getStoreWrapper() {

        try {
            return new StoreWrapper(SDBFactory.connectStore(dataSource.getConnection(), storeDesc));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private DataSource dataSource = null;

}
