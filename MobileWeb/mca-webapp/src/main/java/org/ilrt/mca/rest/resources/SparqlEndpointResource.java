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

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.sdb.SDBFactory;
import com.sun.jersey.api.view.Viewable;
import com.sun.jersey.spi.container.servlet.WebConfig;
import com.sun.jersey.spi.resource.Singleton;
import com.talis.rdfwriters.json.JSONJenaWriter;
import org.apache.log4j.Logger;
import org.ilrt.mca.RdfMediaType;
import org.ilrt.mca.rdf.StoreWrapper;
import org.ilrt.mca.rest.ex.BadRequestException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
@Singleton
@Path("/sparql")
public class SparqlEndpointResource extends AbstractResource {

    // ---------- Constructors

    public SparqlEndpointResource() {
        super();
    }

    // ---------- Public methods (Jersey)

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response htmlView(@QueryParam("query") String query, @QueryParam("type") String type) {

        String enabled = wc.getServletContext().getInitParameter("sparqlEnabled");

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

        String enabled = wc.getServletContext().getInitParameter("sparqlEnabled");

        if (enabled == null || enabled.equals("false")) {

            logger.info("The SPARQL Endpoint is not enabled");

            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity("The SPARQL endpoint is unavailable").type(MediaType.TEXT_PLAIN)
                    .build();
        }

        // return a 404 if no query is provided
        if (query == null || query == "") {
            logger.info("There is no request, throw an exception");
            throw new BadRequestException("No query string is provided");
        }

        return query(query, null);
    }

    // ---------- Private helper methods

    private Response query(String query, String type) {

        logger.info("Querying the endpoint");

        // return a 404 if no query is provided
        if (query == null|| query == "") throw new BadRequestException("No query string is provided");

        Query q = QueryFactory.create(query);

        if (q.isUnknownType()) {
            throw new BadRequestException("Unexpected query");
        }

        return Response.ok(new SparqlQueryResults(manager.getStoreWrapper(), query, type)).build();

    }


    @Context
    private
    WebConfig wc;

    private final String xml = "xml";

    private final Logger logger = Logger.getLogger(SparqlEndpointResource.class);

    public class SparqlQueryResults {

        public SparqlQueryResults(StoreWrapper wrapper, String query, String type) {
            this.wrapper = wrapper;
            this.query = query;
            this.type = type;
        }


        public void executeAndStreamResults(OutputStream outputStream, MediaType mediaType) throws IOException {

            // prepare the query and get access to the data
            Query q = QueryFactory.create(query);
            Dataset dataset = SDBFactory.connectDataset(manager.getStoreWrapper().getStore());

            // execute the query
            QueryExecution qe = QueryExecutionFactory.create(q, dataset);

            if (q.isAskType()) {

                if (mediaType.equals(RdfMediaType.SPARQL_RESULTS_XML_TYPE)) {
                    ResultSetFormatter.outputAsXML(outputStream, qe.execAsk());
                } else {
                    ResultSetFormatter.outputAsJSON(outputStream, qe.execAsk());
                }
            } else if (q.isDescribeType()) {

                Model m = qe.execDescribe();
                streamModel(m, mediaType, outputStream);
                m.close();

            } else if (q.isConstructType()) {

                Model m = qe.execConstruct();
                streamModel(m, mediaType, outputStream);
                m.close();

            } else if (q.isSelectType()) {
                if (mediaType.equals(RdfMediaType.SPARQL_RESULTS_XML_TYPE)) {
                    ResultSetFormatter.outputAsXML(outputStream, qe.execSelect());
                } else {
                    ResultSetFormatter.outputAsJSON(outputStream, qe.execSelect());
                }
            }

            outputStream.flush();
            qe.close();
            dataset.close();
            wrapper.close();
        }

        private void streamModel(Model m, MediaType mediaType, OutputStream outputStream) {

            if (type == null) {
                if (mediaType.getType().equals("text") && mediaType.getSubtype().startsWith("n3")) {
                    m.write(outputStream, "N3");
                } else {
                    m.write(outputStream, "RDF/XML-ABBREV");
                }
            } else {
                if (type.equals(xml)) {
                    m.write(outputStream, "RDF/XML-ABBREV");
                } else {
                    JSONJenaWriter jsonJenaWriter = new JSONJenaWriter();
                    jsonJenaWriter.write(m, outputStream, null);
                }
            }
        }


        private final StoreWrapper wrapper;

        private final String query;
        private final String type;
    }

}
