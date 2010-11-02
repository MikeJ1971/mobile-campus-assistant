package org.ilrt.mca.rest.resources;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sdb.SDBFactory;
import com.hp.hpl.jena.sdb.Store;
import com.hp.hpl.jena.sdb.StoreDesc;
import com.hp.hpl.jena.sdb.sql.SDBConnection;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.test.framework.JerseyTest;
import org.ilrt.mca.RdfMediaType;
import org.ilrt.mca.rest.providers.JenaModelRdfProvider;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;

import javax.sql.DataSource;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SparqlEndpointResourceTest extends JerseyTest {

    public SparqlEndpointResourceTest() {
        super("org.ilrt.mca.rest");
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

    @Test
    public void testWithoutQuery() {

        assertEquals("Unexpected response code", Response.Status.BAD_REQUEST.getStatusCode(),
                webResource.head().getStatus());
    }


    @Test
    public void testGibberishQuery() throws UnsupportedEncodingException {

        //WebResource webResource = resource().path("/sparql");

        MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
        queryParams.add("query", GIBBERISH_QUERY);

        ClientResponse response = webResource.queryParams(queryParams)
                .accept(RdfMediaType.APPLICATION_RDF_XML).get(ClientResponse.class);

        System.out.println(response.toString());

        assertEquals("Unexpected response code", Response.Status.BAD_REQUEST.getStatusCode(),
                response.getStatus());
    }


    @Test
    public void testDescribeQuery() {

        MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
        queryParams.add("query", DESCRIBE_QUERY);


        ClientResponse response = webResource.queryParams(queryParams)
                .accept(RdfMediaType.APPLICATION_RDF_XML).get(ClientResponse.class);

        assertEquals("Unexpected response code", Response.Status.OK.getStatusCode(),
                response.getStatus());

        Model m = response.getEntity(Model.class);

        assertTrue("Expected the model to have triples", m.size() > 0);
    }


    @Test
    public void testConstructQuery() {

        MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
        queryParams.add("query", CONSTRUCT_QUERY);


        ClientResponse response = webResource.queryParams(queryParams)
                .accept(RdfMediaType.APPLICATION_RDF_XML).get(ClientResponse.class);

        assertEquals("Unexpected response code", Response.Status.OK.getStatusCode(),
                response.getStatus());

        Model m = response.getEntity(Model.class);

        assertTrue("Expected the model to have triples", m.size() > 0);
    }


    @Test
    public void testSelectQuery() {

        MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
        queryParams.add("query", SELECT_QUERY);


        ClientResponse response = webResource.queryParams(queryParams)
                .accept(RdfMediaType.SPARQL_RESULTS_XML).get(ClientResponse.class);

        assertEquals("Unexpected response code", Response.Status.OK.getStatusCode(),
                response.getStatus());

        String s = response.getEntity(String.class);

        assertTrue("Expected results", s.contains("sparql xmlns"));
    }

    @Test
    public void testAskQuery() {

        MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
        queryParams.add("query", ASK_QUERY);

        ClientResponse response = webResource.queryParams(queryParams)
                .accept(RdfMediaType.SPARQL_RESULTS_XML).get(ClientResponse.class);

        assertEquals("Unexpected response code", Response.Status.OK.getStatusCode(),
                response.getStatus());

        String s = response.getEntity(String.class);

        assertTrue("Expected results", s.contains("sparql xmlns"));
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

    @Override
    public Client client() {

        ClientConfig config = new DefaultClientConfig();
        config.getClasses().add(JenaModelRdfProvider.class);
        return Client.create(config);
    }

    @Override
    public WebResource resource() {
        return client().resource("http://localhost:9998/");
    }


    final String CONFIG = "/sdb.ttl";
    final String TEST_REGISTRY = "/test-registry.ttl";

    private WebResource webResource = null;

    private final String DESCRIBE_QUERY = "DESCRIBE <mca://registry/>";
    private final String CONSTRUCT_QUERY = "CONSTRUCT { ?s ?p ?o } WHERE { ?s ?p ?o }";
    private final String SELECT_QUERY = "SELECT ?s ?p ?o WHERE { ?s ?p ?o }";
    private final String ASK_QUERY = "ASK { ?s ?p ?o }";
    private final String GIBBERISH_QUERY = "ahhkjha hja hjhkj akjh";

    private final String TEST_DATABASE_URL = "jdbc:h2:target/mca-dev";
    private final String TEST_CONTEXT = "java:comp/env/jdbc/mca";
}
