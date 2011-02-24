package org.ilrt.mca.rest.resources;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.sun.jersey.api.view.Viewable;
import com.sun.jersey.spi.resource.Singleton;
import org.ilrt.mca.RdfMediaType;
import org.ilrt.mca.services.ldap.BasicLdapSearch;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.util.Properties;

@Singleton
@Path("/ldap/")
public class LdapSearchResource {

    public LdapSearchResource() throws IOException {

        // holds the server details
        Properties ldapProperties = new Properties();
        ldapProperties.load(this.getClass().getResourceAsStream("/ldap.server.properties"));

        // holds search details
        ldapSearchProperties = new Properties();
        ldapSearchProperties.load(this.getClass().getResourceAsStream("/ldap.search.properties"));

        // service for handling searches
        ldapSearch = new BasicLdapSearch(ldapProperties);

        m = ModelFactory.createDefaultModel();

    }

    @GET
    @Produces({MediaType.TEXT_HTML})
    public Response processForm(@QueryParam("search") String search) {

        // just return the form
        if (search == null || search.isEmpty()) {
            r = m.createResource();
            r.addProperty(RDFS.label, "Staff Search");
            return Response.ok(new Viewable("/staffsearch", r)).build();
        }

        // create a URI for the results
        String path = "mca://registry" + uriInfo.getRequestUri().getPath();


        if (uriInfo.getRequestUri().getQuery() != null) {
            path = path + "?" + uriInfo.getRequestUri().getQuery();
        }

        Resource resultUri = m.createResource(path);

        // get the results
        Resource results = ldapSearch.search(resultUri, createFilter(search));
        results.addProperty(RDFS.label, "Staff Search Results");

        results.getModel().write(System.out);

        return Response.ok(new Viewable("/staffsearchResults", results)).build();
    }


    @GET
    @Produces({RdfMediaType.APPLICATION_RDF_XML, RdfMediaType.TEXT_RDF_N3})
    public Response processFormAsRDF(@QueryParam("search") String search) {

        // create a URI for the results
        String path = "mca://registry" + uriInfo.getRequestUri().getPath();


        if (uriInfo.getRequestUri().getQuery() != null) {
            path = path + "?" + uriInfo.getRequestUri().getQuery();
        }

        Resource resultUri = r.getModel().createResource(path);

        // get the results
        Resource results = ldapSearch.search(resultUri, createFilter(search));

        return Response.ok(results.getModel()).build();
    }


    private String createFilter(String searchString) {

        // TODO - we need to sanitise the input string

        String searchQuery = ldapSearchProperties.getProperty("searchQuery");

        searchQuery = searchQuery.replace("{0}", searchString);

        return searchQuery;
    }


    private Model m;
    private Resource r;
    private BasicLdapSearch ldapSearch;

    private Properties ldapSearchProperties;

    @Context
    private
    UriInfo uriInfo;
}
