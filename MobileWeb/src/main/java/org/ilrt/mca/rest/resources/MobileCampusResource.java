package org.ilrt.mca.rest.resources;

import org.ilrt.mca.dao.ItemDao;
import org.ilrt.mca.dao.ItemDaoImpl;
import org.ilrt.mca.rdf.ModelRepository;
import org.ilrt.mca.rest.HtmlResponseResolverImpl;
import org.ilrt.mca.rest.ResponseResolver;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("{path:.*}")
public class MobileCampusResource {

    public MobileCampusResource() {

        ModelRepository modelRepository = new ModelRepository();
        ItemDao itemDao = new ItemDaoImpl(modelRepository);
        htmlResponseResolver = new HtmlResponseResolverImpl(itemDao);
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response getGroupsAsHtml(@PathParam("path") String path) {

        return htmlResponseResolver.reponse(path);
        /**
         System.out.println(path);

         if (path == null || path.equals("")) {
         List<Group> results = registryManager.findGroups(model);
         return Response.ok(new Viewable("/groups.ftl", results)).build();
         } else {

         String uri = Common.MCA_STUB + path;

         Model itemModel = registryManager.findItem(uri);
         Item  item = registryManager.findItem(uri, itemModel);

         return Response.ok(new Viewable("/kmlMap.ftl", item)).build();
         }

         **/


    }

    /**
     * @GET
     * @Produces({RdfMediaType.APPLICATION_RDF_XML, RdfMediaType.TEXT_RDF_N3})
     * public Response getModelAsRdf(@PathParam("path") String path) {
     * return Response.ok(model).build();
     * }
     * @GET
     * @Produces(MediaType.APPLICATION_JSON) public Response getGroupAsJson(@PathParam("path") String path) {
     * List<Group> results = registryManager.findGroups(model);
     * Gson gson = new Gson();
     * return Response.ok(gson.toJson(results)).build();
     * }
     */

    ResponseResolver htmlResponseResolver;
    //private Model model;
}
