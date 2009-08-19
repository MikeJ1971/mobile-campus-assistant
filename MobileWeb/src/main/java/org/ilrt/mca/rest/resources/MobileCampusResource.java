package org.ilrt.mca.rest.resources;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hp.hpl.jena.rdf.model.Model;
import com.sun.jersey.api.view.Viewable;
import org.ilrt.mca.Common;
import org.ilrt.mca.RdfMediaType;
import org.ilrt.mca.dao.ItemDao;
import org.ilrt.mca.dao.ItemDaoImpl;
import org.ilrt.mca.domain.Item;
import org.ilrt.mca.rdf.ModelRepository;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("{path:.*}")
public class MobileCampusResource {

    public MobileCampusResource() {

        modelRepository = new ModelRepository();
        itemDao = new ItemDaoImpl(modelRepository);
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response getGroupsAsHtml(@PathParam("path") String path) {

        // are we just after the root?
        if (isRoot(path)) {

            Item item = itemDao.findHomePage();
            return Response.ok(new Viewable(getTemplatePath(item.getTemplate()), item)).build();
        }

        String uri = Common.MCA_STUB + path;

        Item item = itemDao.findItem(uri);

        if (item != null) {
            return Response.ok(new Viewable(getTemplatePath(item.getTemplate()), item)).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity(new Viewable("/404.ftl",
                    "Unable to resolve the requested path: " + path)).build();
        }
    }


    @GET
    @Produces({RdfMediaType.APPLICATION_RDF_XML, RdfMediaType.TEXT_RDF_N3})
    public Response getModelAsRdf(@PathParam("path") String path) {


        // are we just after the root?
        if (isRoot(path)) {
            Model model = modelRepository.findHomePage();
            return Response.ok(model).build();
        }

        String uri = Common.MCA_STUB + path;

        Model model = modelRepository.findItem(uri);

        if (!model.isEmpty()) {
            return Response.ok(model).build();
        }

        // default to not found
        return Response.status(Response.Status.NOT_FOUND).build();
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGroupAsJson(@PathParam("path") String path) {

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        // are we just after the root?
        if (isRoot(path)) {

            Item item = itemDao.findHomePage();
            return Response.ok(gson.toJson(item)).build();
        }

        String uri = Common.MCA_STUB + path;

        Item item = itemDao.findItem(uri);

        if (item != null) {
            return Response.ok(gson.toJson(item)).build();
        }

        // default to not found
        return Response.status(Response.Status.NOT_FOUND).build();
    }


    private String getTemplatePath(String templatePath) {
        return "/" + templatePath.substring(Common.TEMPLATE_STUB.length());
    }

    private boolean isRoot(String path) {
        return (path == null || path.equals("") || path.equals("/"));
    }

    private ModelRepository modelRepository;
    private ItemDao itemDao;
}
