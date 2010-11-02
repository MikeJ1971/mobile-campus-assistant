package org.ilrt.mca.rest.resources;

import com.hp.hpl.jena.query.QueryParseException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class QueryParseMapper
        implements ExceptionMapper<com.hp.hpl.jena.query.QueryParseException> {

    @Override
    public Response toResponse(QueryParseException ex) {
        return Response.status(400).entity(ex.getMessage()).type("text/plain").build();
    }
}
