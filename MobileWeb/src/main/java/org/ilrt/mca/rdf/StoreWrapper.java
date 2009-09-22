package org.ilrt.mca.rdf;

import com.hp.hpl.jena.sdb.Store;

import java.sql.SQLException;

/**
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
