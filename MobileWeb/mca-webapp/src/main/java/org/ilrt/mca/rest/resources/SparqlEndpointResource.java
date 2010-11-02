package org.ilrt.mca.rest.resources;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.sdb.SDBFactory;
import com.sun.jersey.spi.resource.Singleton;
import org.ilrt.mca.RdfMediaType;
import org.ilrt.mca.exceptions.BadRequestException;
import org.ilrt.mca.rdf.ConnPoolStoreWrapperManagerImpl;
import org.ilrt.mca.rdf.DataSourceManager;
import org.ilrt.mca.rdf.StoreWrapper;
import org.ilrt.mca.rdf.StoreWrapperManager;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Singleton
@Path("/sparql")
public class SparqlEndpointResource {

    public SparqlEndpointResource() {
        DataSourceManager dataSourceManager = new DataSourceManager();
        manager = new ConnPoolStoreWrapperManagerImpl(CONFIG,
                dataSourceManager.getDataSource());
    }

    @GET
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces({RdfMediaType.APPLICATION_RDF_XML, RdfMediaType.SPARQL_RESULTS_JSON,
            RdfMediaType.SPARQL_RESULTS_XML, RdfMediaType.TEXT_RDF_N3})
    public Response query(@QueryParam("query") String query) {

        // return a 404 if no query is provided
        if (query == null) throw new BadRequestException("No query string is provided");

        // compile the query
        Query q = QueryFactory.create(query);


        StoreWrapper storeWrapper = manager.getStoreWrapper();
        Dataset dataset = SDBFactory.connectDataset(manager.getStoreWrapper().getStore());
        QueryExecution qe = QueryExecutionFactory.create(q, dataset);


        if (q.isAskType()) {
            return Response.ok(ResultSetFormatter.asXMLString(qe.execAsk())).build();
        } else if (q.isDescribeType()) {
            return Response.ok(qe.execDescribe()).build();
        } else if (q.isConstructType()) {
            return Response.ok(qe.execConstruct()).build();
        } else if (q.isSelectType()) {
            return Response.ok(ResultSetFormatter.asXMLString(qe.execSelect())).build();
        } else {
            System.out.println("UKNOWN!!!!");
        }

        storeWrapper.close();

        return Response.ok().build();
    }


    final String CONFIG = "/sdb.ttl";
    StoreWrapperManager manager;
}
