package org.ilrt.mca.rest.resources;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.sparql.algebra.Algebra;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.sun.jersey.spi.resource.Singleton;
import org.ilrt.mca.RdfMediaType;

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

    @GET
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces({RdfMediaType.APPLICATION_RDF_XML, RdfMediaType.SPARQL_RESULTS_JSON,
            RdfMediaType.SPARQL_RESULTS_XML, RdfMediaType.TEXT_RDF_N3})
    public Response query(@QueryParam("query") String query) {

        if (query == null) {
            //Response.notAcceptable().
        }

        // compile the query
        //Query q = QueryFactory.create(query);
        //Op opQuery = Algebra.compile(q);


        return Response.ok().build();
    }

}
