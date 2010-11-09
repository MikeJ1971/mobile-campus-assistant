/*
 * Copyright (c) 2010, University of Bristol
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

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sdb.StoreDesc;
import com.hp.hpl.jena.sdb.util.StoreUtils;

import java.io.InputStream;
import java.sql.SQLException;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
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