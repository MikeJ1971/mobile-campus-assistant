package org.ilrt.mca.rest.resources;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.sun.jersey.api.view.Viewable;
import org.ilrt.mca.Common;
import org.ilrt.mca.RdfMediaType;
import org.ilrt.mca.dao.ItemDao;
import org.ilrt.mca.dao.ItemDaoImpl;
import org.ilrt.mca.domain.Item;
import org.ilrt.mca.domain.map.KmlMapItem;
import org.ilrt.mca.rdf.ModelRepository;
import org.ilrt.mca.rdf.Repository;
import org.ilrt.mca.vocab.MCA_REGISTRY;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
@Path("{path:.*}")
public class MobileCampusResource {

    public MobileCampusResource() {

        repository = new ModelRepository();
        itemDao = new ItemDaoImpl(repository);
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response getGroupsAsHtml(@PathParam("path") String path) {

        // are we just after the root?
        String uri = isRoot(path) ? "mca://registry/" : Common.MCA_STUB + path;

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
        String uri = isRoot(path) ? "mca://registry/" : Common.MCA_STUB + path;

        Model model = repository.findItem(uri);

        if (model.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }


        // type checking
        Resource resource = model.getResource(uri);

        if (resource.getProperty(RDF.type).getResource().getURI()
                .equals(MCA_REGISTRY.KmlMapSource.getURI())) {

            Model additional = repository.findMapDetails(uri);
            model = model.add(additional);
        }


        return Response.ok(model).build();
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGroupAsJson(@PathParam("path") String path) {

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        // are we just after the root?
        String uri = isRoot(path) ? "mca://registry/" : Common.MCA_STUB + path;

        Item item = itemDao.findItem(uri);

        if (item != null) {

            if (item instanceof KmlMapItem) {
                return Response.ok(gson.toJson((KmlMapItem) item)).build();
            }


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

    private Repository repository;
    private ItemDao itemDao;
}
