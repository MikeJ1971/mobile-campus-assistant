package org.ilrt.mca.rdf;

import com.hp.hpl.jena.sdb.Store;

import java.sql.SQLException;

/**
 * A basic wrapper around the SDB Store object. We just provide a close() method that
 * closes the underlying connection to a database. This should be useful if
 * connections are coming from a connection pool.
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class StoreWrapper {

    public StoreWrapper(Store store) {
        this.store = store;
    }

    public Store getStore() {
        return store;
    }

    public void close() {
        if (!store.isClosed()) {
            try {
                if (!store.getConnection().getSqlConnection().isClosed()) {
                    store.getConnection().close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            store.close();
        }
    }

    private final Store store;
}
