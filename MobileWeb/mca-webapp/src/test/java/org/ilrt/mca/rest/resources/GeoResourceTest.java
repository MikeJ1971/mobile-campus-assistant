package org.ilrt.mca.rest.resources;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.sdb.SDBFactory;
import com.hp.hpl.jena.sdb.Store;
import com.hp.hpl.jena.sdb.sql.SDBConnection;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.test.framework.WebAppDescriptor;
import org.ilrt.mca.RdfMediaType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class GeoResourceTest extends AbstractResourceTest {


    public GeoResourceTest() {

        super(new WebAppDescriptor.Builder("org.ilrt.mca.rest")
                .initParam("sparqlEnabled", "true").build());
    }

    @Test
    public void testType() {

        webResource = resource().path("/geo/places/cafe");

        ClientResponse clientResponse = webResource.accept(RdfMediaType.APPLICATION_RDF_XML)
                .get(ClientResponse.class);

        assertEquals("Unexpected response from the server.", 200, clientResponse.getStatus());
    }


    @Test
    public void testInvalidType() {

        webResource = resource().path("/geo/places/invalid");

        ClientResponse clientResponse = webResource.accept(RdfMediaType.APPLICATION_RDF_XML)
                .get(ClientResponse.class);

        assertEquals("Unexpected response from the server.", 404, clientResponse.getStatus());
    }

    @Override
    protected void setUpDatabase() {

        // create a connection and store
        SDBConnection conn = createConnection();
        Store store = createStore(conn);

        // format the database
        store.getTableFormatter().format();
        store.getTableFormatter().truncate();

        // add the data
        Dataset dataset = SDBFactory.connectDataset(store);
        Model model = dataset.getNamedModel("geo://test_geo_graph1");
        model.read(getClass().getResourceAsStream("/test-geodata.xml"), null);

        // clean up
        store.close();
        conn.close();
    }
}
