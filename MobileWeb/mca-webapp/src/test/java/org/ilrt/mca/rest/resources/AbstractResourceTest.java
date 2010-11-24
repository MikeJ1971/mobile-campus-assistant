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
package org.ilrt.mca.rest.resources;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sdb.SDBFactory;
import com.hp.hpl.jena.sdb.Store;
import com.hp.hpl.jena.sdb.StoreDesc;
import com.hp.hpl.jena.sdb.sql.SDBConnection;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;
import org.ilrt.mca.rest.providers.FreemarkerTemplateProvider;
import org.ilrt.mca.rest.providers.JenaModelRdfProvider;
import org.junit.Before;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;

import javax.sql.DataSource;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public abstract class AbstractResourceTest extends JerseyTest {


    public AbstractResourceTest(WebAppDescriptor descriptor) {
        super(descriptor);
    }

    @Before
    public void setup() {

        setupJndi();
        setUpDatabase();
    }

    // ---------- Override super class methods for client and web resource

    @Override
    public Client client() {

        ClientConfig config = new DefaultClientConfig();
        config.getClasses().add(JenaModelRdfProvider.class);
        config.getClasses().add(FreemarkerTemplateProvider.class);
        return Client.create(config);
    }

    @Override
    public WebResource resource() {
        return client().resource("http://localhost:9998/");
    }

    // ---------- protected methods

    /**
     * The database configuration is specific to the test and needs to be
     * implemented in each test that subclasses this abstract class.
     */
    protected abstract void setUpDatabase();

    /**
     * Convenience method for setting up the connection details via JNDI.
     */
    protected void setupJndi() {
        try {

            SimpleNamingContextBuilder builder = new SimpleNamingContextBuilder();
            DataSource ds = new DriverManagerDataSource(TEST_DATABASE_URL);
            builder.bind(TEST_CONTEXT, ds);
            builder.activate();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * @return an SDBConnection
     */
    protected SDBConnection createConnection() {
        return SDBFactory.createConnection(TEST_DATABASE_URL, "", "");
    }

    /**
     * @param conn a connection to the database.
     * @return a store based on the test store description.
     */
    protected Store createStore(SDBConnection conn) {

        Model ttl = ModelFactory.createDefaultModel();
        ttl.read(getClass().getResourceAsStream(CONFIG), null, "TTL");
        StoreDesc storeDesc = StoreDesc.read(ttl);
        return SDBFactory.connectStore(conn, storeDesc);
    }


    WebResource webResource = null;

    final String CONFIG = "/sdb.ttl";
    final String TEST_REGISTRY = "/test-registry.ttl";

    final String TEST_DATABASE_URL = "jdbc:h2:target/mca-dev";
    final String TEST_CONTEXT = "java:comp/env/jdbc/mca";
}
