/*
 * Copyright (c) 2009, University of Bristol
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1) Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2) Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3) Neither the name of the University of Bristol nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
package org.ilrt.mca.rdf;

import com.hp.hpl.jena.sdb.Store;
import org.apache.log4j.Logger;

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

        log.debug("Call to close the store");

        // the store might be already closed (due to the closure of a dataset) so cleanup the db
        try {
            if (!store.getConnection().getSqlConnection().isClosed()) {
                log.debug("Closing database connection");
                store.getConnection().close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (!store.isClosed()) {
            store.close();
        }
    }

    private final Store store;

    private final Logger log = Logger.getLogger(StoreWrapper.class);
}
