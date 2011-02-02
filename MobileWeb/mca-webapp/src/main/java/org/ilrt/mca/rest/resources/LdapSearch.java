package org.ilrt.mca.rest.resources;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.sun.jersey.api.view.Viewable;
import com.sun.jersey.spi.resource.Singleton;
import org.ilrt.mca.services.ldap.BasicLdapSearch;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Properties;

@Singleton
@Path("/ldap/")
public class LdapSearch {

    public LdapSearch() throws IOException {
        ldapProperties = new Properties();
        ldapProperties.load(this.getClass().getResourceAsStream("/ldap.properties"));

        

        ldapSearch = new BasicLdapSearch(ldapProperties);


        Model m = ModelFactory.createDefaultModel();
        r = m.createResource();
        r.addProperty(RDFS.label, "Staff Search");
    }

    @GET
    @Produces({MediaType.TEXT_HTML})
    public Response processForm(@QueryParam("search") String search) {

        if (search == null || search.isEmpty()) {
            return Response.ok(new Viewable("/staffsearch", r)).build();
        }

        System.out.println("Received .... " + search);

        return Response.ok(new Viewable("/staffsearch", r)).build();
    }

    Properties ldapProperties;
    Resource r;
    BasicLdapSearch ldapSearch;
}
