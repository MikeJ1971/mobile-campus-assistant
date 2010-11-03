package org.ilrt.mca.rest.resources;

import com.hp.hpl.jena.query.Dataset;
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

public abstract class AbstractSparqlEndpointResourceTest extends JerseyTest {


    public AbstractSparqlEndpointResourceTest(WebAppDescriptor descriptor) {
        super(descriptor);
    }

    @Before
    public void setup() {
        try {

            SimpleNamingContextBuilder builder = new SimpleNamingContextBuilder();
            DataSource ds = new DriverManagerDataSource(TEST_DATABASE_URL);
            builder.bind(TEST_CONTEXT, ds);
            builder.activate();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        setUpDatabase();

        webResource = resource().path("/sparql");
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

    // ---------- Private methods

    private void setUpDatabase() {

        // load the SDB configuration
        Model ttl = ModelFactory.createDefaultModel();
        ttl.read(getClass().getResourceAsStream(CONFIG), null, "TTL");

        // create a connection and store
        SDBConnection conn = SDBFactory.createConnection(TEST_DATABASE_URL, "", "");
        StoreDesc storeDesc = StoreDesc.read(ttl);
        Store store = SDBFactory.connectStore(conn, storeDesc);

        // format the database
        store.getTableFormatter().format();
        store.getTableFormatter().truncate();

        // add the data
        Dataset dataset = SDBFactory.connectDataset(store);
        Model model = dataset.getDefaultModel();
        model.read(getClass().getResourceAsStream(TEST_REGISTRY), null, "TTL");

        // clean up
        store.close();
        conn.close();
    }

    WebResource webResource = null;

    final String CONFIG = "/sdb.ttl";
    final String TEST_REGISTRY = "/test-registry.ttl";

    final String TEST_DATABASE_URL = "jdbc:h2:target/mca-dev";
    final String TEST_CONTEXT = "java:comp/env/jdbc/mca";
}
