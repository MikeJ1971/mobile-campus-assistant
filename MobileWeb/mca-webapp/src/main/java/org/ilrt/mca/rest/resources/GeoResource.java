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

import com.hp.hpl.jena.rdf.model.Model;
import com.sun.jersey.spi.resource.Singleton;
import org.apache.log4j.Logger;
import org.ilrt.mca.KmlMediaType;
import org.ilrt.mca.RdfMediaType;
import org.ilrt.mca.dao.GeoDao;
import org.ilrt.mca.rdf.SdbManagerImpl;
import org.ilrt.mca.vocab.MCA_GEO;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
@Singleton
@Path("/geo/")
public class GeoResource extends AbstractResource {

    // ---------- Constructors

    public GeoResource() throws IOException {
        super();
        geoDao = new GeoDao(new SdbManagerImpl(manager));
    }

    @GET
    @Produces({MediaType.WILDCARD, RdfMediaType.APPLICATION_RDF_XML, RdfMediaType.TEXT_RDF_N3, KmlMediaType.APPLICATION_KML})
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


    private GeoDao geoDao;
}
