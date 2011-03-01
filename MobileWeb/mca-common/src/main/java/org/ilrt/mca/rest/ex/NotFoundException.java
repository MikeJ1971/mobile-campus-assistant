package org.ilrt.mca.rest.ex;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


public class NotFoundException extends WebApplicationException {
        public NotFoundException(String message) {
        super(Response.status(Response.Status.NOT_FOUND).entity(message)
                .type(MediaType.TEXT_PLAIN).build());
    }
}
