package org.ilrt.mca.rest.resources;

import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.test.framework.JerseyTest;
import org.junit.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;

import javax.sql.DataSource;

import static org.junit.Assert.assertTrue;

public class SparqlEndpointResourceTest extends JerseyTest {

    public SparqlEndpointResourceTest() {

        super("org.ilrt.mca.rest.resources");

        try {

            SimpleNamingContextBuilder builder = new SimpleNamingContextBuilder();
            DataSource ds = new DriverManagerDataSource(TEST_DATABASE_URL);
            builder.bind(TEST_CONTEXT, ds);
            builder.activate();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void test() {

        WebResource webResource = resource().path("/sparql");

        System.out.println(webResource.head().getStatus());


        //String msg = resource().path("/sparql").get(String.class);
        assertTrue(true);
    }


    private final String TEST_DATABASE_URL = "jdbc:h2:target/mca-dev";
    private final String TEST_CONTEXT = "java:comp/env/jdbc/mca";
}
