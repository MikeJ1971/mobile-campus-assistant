package org.ilrt.mca.rest.resources;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.sdb.SDBFactory;
import com.sun.jersey.api.view.Viewable;
import com.sun.jersey.spi.container.servlet.WebConfig;
import com.sun.jersey.spi.resource.Singleton;
import org.ilrt.mca.RdfMediaType;
import org.ilrt.mca.exceptions.BadRequestException;
import org.ilrt.mca.rdf.ConnPoolStoreWrapperManagerImpl;
import org.ilrt.mca.rdf.DataSourceManager;
import org.ilrt.mca.rdf.StoreWrapper;
import org.ilrt.mca.rdf.StoreWrapperManager;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Singleton
@Path("/sparql")
public class SparqlEndpointResource {

    // ---------- Constructors

    public SparqlEndpointResource() {
        DataSourceManager dataSourceManager = new DataSourceManager();
        manager = new ConnPoolStoreWrapperManagerImpl(CONFIG,
                dataSourceManager.getDataSource());
    }

    // ---------- Public methods (Jersey)

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response htmlView(@QueryParam("query") String query, @QueryParam("type") String type) {

        String enabled = wc.getInitParameter("sparqlEnabled");

        if (enabled == null || enabled.equals("false"))
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity("The SPARQL endpoint is unavailable").type(MediaType.TEXT_PLAIN)
                    .build();

        if (query == null || query.equals(""))
            return Response.ok(new Viewable("/admin/sparqlForm", null)).build();


        return query(query, type);
    }

    @GET
    @Produces({RdfMediaType.APPLICATION_RDF_XML, RdfMediaType.SPARQL_RESULTS_JSON,
            RdfMediaType.SPARQL_RESULTS_XML, RdfMediaType.TEXT_RDF_N3})
    public Response query(@QueryParam("query") String query) {

        String enabled = wc.getInitParameter("sparqlEnabled");

        if (enabled == null || enabled.equals("false"))
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity("The SPARQL endpoint is unavailable").type(MediaType.TEXT_PLAIN)
                    .build();

        // return a 404 if no query is provided
        if (query == null) throw new BadRequestException("No query string is provided");

        return query(query, null);
    }

    // ---------- Private helper methods

    private Response query(String query, String type) {


        // return a 404 if no query is provided
        if (query == null) throw new BadRequestException("No query string is provided");

        // compile the query
        Query q = QueryFactory.create(query);

        // setup the query for execution
        StoreWrapper storeWrapper = manager.getStoreWrapper();
        Dataset dataset = SDBFactory.connectDataset(manager.getStoreWrapper().getStore());
        QueryExecution qe = QueryExecutionFactory.create(q, dataset);

        // get the response

        Response.ResponseBuilder response;

        if (type != null) {
            response = getResponse(q, qe, type);
        } else {
            response = getResponse(q, qe);
        }


        // cleanup
        storeWrapper.close();

        return response.build();
    }


    private Response.ResponseBuilder getResponse(Query q, QueryExecution qe) {
        if (q.isAskType()) {
            return Response.ok(qe.execAsk());
        } else if (q.isDescribeType()) {
            return Response.ok(qe.execDescribe());
        } else if (q.isConstructType()) {
            return Response.ok(qe.execConstruct());
        } else if (q.isSelectType()) {
            return Response.ok(qe.execSelect());
        } else {
            return null;
        }
    }


    private Response.ResponseBuilder getResponse(Query q, QueryExecution qe, String type) {

        Response.ResponseBuilder builder = getResponse(q, qe);

        if (q.isAskType() || q.isSelectType()) {
            System.out.println("ASK OR SELECT");
            if (type.equals(xml)) {
                return builder.type(RdfMediaType.SPARQL_RESULTS_XML_TYPE);
            } else {
                return builder.type(RdfMediaType.SPARQL_RESULTS_JSON_TYPE);
            }
        } else if (q.isConstructType() || q.isDescribeType()) {
            System.out.println("CONSTRUCT OR DESCRIBE");
            if (type.equals(xml)) {
                return builder.type(RdfMediaType.APPLICATION_RDF_XML_TYPE);
            } else {
                return builder.type(MediaType.APPLICATION_JSON_TYPE);
            }
        } else {
            return null;
        }
    }

    @Context
    WebConfig wc;

    final String CONFIG = "/sdb.ttl";
    StoreWrapperManager manager;
    private final String xml = "xml";
}
