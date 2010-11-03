package org.ilrt.mca.rest.resources;

import com.hp.hpl.jena.rdf.model.Model;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.test.framework.WebAppDescriptor;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.ilrt.mca.RdfMediaType;
import org.ilrt.mca.rest.providers.FreemarkerTemplateProvider;
import org.ilrt.mca.rest.providers.JenaModelRdfProvider;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class SparqlEndpointResourceEnabledTest extends AbstractSparqlEndpointResourceTest {

    public SparqlEndpointResourceEnabledTest() {

        super(new WebAppDescriptor.Builder("org.ilrt.mca.rest")
                .initParam("sparqlEnabled", "true")
                .build());
    }

    @Test
    public void testWithoutQuery() {

        ClientResponse response = getClientResponse(null, RdfMediaType.APPLICATION_RDF_XML);

        assertEquals("Unexpected response code", Response.Status.BAD_REQUEST.getStatusCode(),
                response.getStatus());
    }

    @Test
    public void testGibberishQuery() throws UnsupportedEncodingException {

        ClientResponse response = getClientResponse(GIBBERISH_QUERY,
                RdfMediaType.APPLICATION_RDF_XML);

        assertEquals("Unexpected response code", Response.Status.BAD_REQUEST.getStatusCode(),
                response.getStatus());
    }

    @Test
    public void testDescribeQueryRdfXml() {

        ClientResponse response = getClientResponse(DESCRIBE_QUERY,
                RdfMediaType.APPLICATION_RDF_XML);

        assertEquals("Unexpected response code", Response.Status.OK.getStatusCode(),
                response.getStatus());

        Model m = response.getEntity(Model.class);

        assertTrue("Expected the model to have triples", m.size() > 0);
    }

    @Test
    public void testDescribeQueryN3() {

        ClientResponse response = getClientResponse(DESCRIBE_QUERY,
                RdfMediaType.TEXT_RDF_N3);

        assertEquals("Unexpected response code", Response.Status.OK.getStatusCode(),
                response.getStatus());

        String s = response.getEntity(String.class);

        assertTrue("Expected to find URI: " + EXPECTED_URI + " in response",
                s.contains(EXPECTED_URI));
    }

    @Test
    public void testConstructQueryRdfXml() {

        ClientResponse response = getClientResponse(CONSTRUCT_QUERY,
                RdfMediaType.APPLICATION_RDF_XML);

        assertEquals("Unexpected response code", Response.Status.OK.getStatusCode(),
                response.getStatus());

        Model m = response.getEntity(Model.class);

        assertTrue("Expected the model to have triples", m.size() > 0);
    }

    @Test
    public void testConstructQueryN3() {

        ClientResponse response = getClientResponse(CONSTRUCT_QUERY,
                RdfMediaType.TEXT_RDF_N3);

        assertEquals("Unexpected response code", Response.Status.OK.getStatusCode(),
                response.getStatus());

        String s = response.getEntity(String.class);

        assertTrue("Expected to find URI: " + EXPECTED_URI + " in response",
                s.contains(EXPECTED_URI));
    }

    @Test
    public void testSelectQueryXml() throws IOException {

        ClientResponse response = getClientResponse(SELECT_QUERY, RdfMediaType.SPARQL_RESULTS_XML);

        assertEquals("Unexpected response code", Response.Status.OK.getStatusCode(),
                response.getStatus());

        String s = response.getEntity(String.class);
        validateXml(s);
    }

    @Test
    public void testSelectQueryJson() throws IOException {

        ClientResponse response = getClientResponse(SELECT_QUERY, RdfMediaType.SPARQL_RESULTS_JSON);

        assertEquals("Unexpected response code", Response.Status.OK.getStatusCode(),
                response.getStatus());

        String s = response.getEntity(String.class);
        validateJson(s);
    }

    @Test
    public void testAskQueryXml() throws IOException {

        ClientResponse response = getClientResponse(ASK_QUERY, RdfMediaType.SPARQL_RESULTS_XML);

        assertEquals("Unexpected response code", Response.Status.OK.getStatusCode(),
                response.getStatus());

        String s = response.getEntity(String.class);
        validateXml(s);
    }

    @Test
    public void testAskQueryJson() throws IOException {

        ClientResponse response = getClientResponse(ASK_QUERY, RdfMediaType.SPARQL_RESULTS_JSON);

        assertEquals("Unexpected response code", Response.Status.OK.getStatusCode(),
                response.getStatus());

        String s = response.getEntity(String.class);
        validateJson(s);
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

    private ClientResponse getClientResponse(String query, String mediaType) {

        MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
        queryParams.add("query", query);

        return webResource.queryParams(queryParams).accept(mediaType).get(ClientResponse.class);
    }

    private void validateXml(String result) throws IOException {
        try {
            XMLReader reader = org.xml.sax.helpers.XMLReaderFactory.createXMLReader();
            reader.parse(new InputSource(new StringReader(result)));
        } catch (SAXException ex) {
            fail("Unable to parse result as xml: " + ex.getMessage());
        }
    }

    private void validateJson(String result) {
        try {
            new JSONObject(result);
        } catch (JSONException ex) {
            fail("Unable to parse result as JSON: " + ex.getMessage());
        }
    }


    //private WebResource webResource = null;

    private final String DESCRIBE_QUERY = "DESCRIBE <mca://registry/>";
    private final String CONSTRUCT_QUERY = "CONSTRUCT { ?s ?p ?o } WHERE { ?s ?p ?o }";
    private final String SELECT_QUERY = "SELECT ?s ?p ?o WHERE { ?s ?p ?o }";
    private final String ASK_QUERY = "ASK { ?s ?p ?o }";
    private final String GIBBERISH_QUERY = "ahhkjha hja hjhkj akjh";


    private final String EXPECTED_URI = "mca://registry";
}
