package org.ilrt.mca.rest.resources;

import com.sun.jersey.api.view.Viewable;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


@Path("/helloworld")
public class HelloWorldResource {

    @GET
    @Produces("text/plain")
    public String getMessage() {
        return "Hello, World!\n";
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Viewable getBlank() {
        return new Viewable("/test.ftl", "Hello, World!");
    }


}
