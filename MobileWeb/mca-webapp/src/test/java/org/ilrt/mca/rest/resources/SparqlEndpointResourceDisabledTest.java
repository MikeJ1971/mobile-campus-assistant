package org.ilrt.mca.rest.resources;

import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;


public class SparqlEndpointResourceDisabledTest extends AbstractSparqlEndpointResourceTest {

    public SparqlEndpointResourceDisabledTest() {

        super(new WebAppDescriptor.Builder("org.ilrt.mca.rest")
                .initParam("sparqlEnabled", "false")
                .build());
    }

    @Test
    public void test() {

        WebResource webResource = super.resource().path("/sparql");
        assertEquals(Response.Status.SERVICE_UNAVAILABLE.getStatusCode(), webResource.head().getStatus());

    }

}
