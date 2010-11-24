package org.ilrt.mca.rest.resources;

import com.hp.hpl.jena.rdf.model.Model;
import com.sun.jersey.spi.resource.Singleton;
import org.apache.log4j.Logger;
import org.ilrt.mca.RdfMediaType;
import org.ilrt.mca.dao.GeoDao;
import org.ilrt.mca.rdf.QueryManager;
import org.ilrt.mca.rdf.SdbManagerImpl;
import org.ilrt.mca.vocab.MCA_GEO;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Singleton
@Path("/geo/")
public class GeoResource extends AbstractResource {

    // ---------- Constructors

    public GeoResource() throws IOException {
        super();
        geoDao = new GeoDao(new SdbManagerImpl(manager));
    }

    @GET
    @Produces({RdfMediaType.APPLICATION_RDF_XML, RdfMediaType.TEXT_RDF_N3})
    @Path("places/{type}")
    public Response places(@PathParam("type") String type) {

        String typeUri = MCA_GEO.NS + type;

        log.debug("Request for type: " + typeUri);

        Model m = geoDao.findGeoPointByType(typeUri);

        if (m == null || m.size() == 0) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            return Response.ok(m).build();
        }
    }

    Logger log = Logger.getLogger(GeoResource.class);

    private QueryManager queryManager;
    private GeoDao geoDao;
}
