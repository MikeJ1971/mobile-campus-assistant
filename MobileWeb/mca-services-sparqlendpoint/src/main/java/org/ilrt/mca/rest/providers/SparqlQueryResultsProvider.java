package org.ilrt.mca.rest.providers;

import org.apache.log4j.Logger;
import org.ilrt.mca.RdfMediaType;
import org.ilrt.mca.rest.resources.SparqlEndpointResource;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
@Produces({RdfMediaType.APPLICATION_RDF_XML, RdfMediaType.TEXT_RDF_N3, MediaType.APPLICATION_JSON,
        RdfMediaType.SPARQL_RESULTS_JSON, RdfMediaType.SPARQL_RESULTS_XML})
public class SparqlQueryResultsProvider
        implements MessageBodyWriter<SparqlEndpointResource.SparqlQueryResults>{


    @Override
    public boolean isWriteable(Class<?> aClass, Type type, Annotation[] annotations,
                               MediaType mediaType) {
        return SparqlEndpointResource.SparqlQueryResults.class.isAssignableFrom(aClass);
    }

    @Override
    public long getSize(SparqlEndpointResource.SparqlQueryResults sparqlQueryResults,
                        Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(SparqlEndpointResource.SparqlQueryResults sparqlQueryResults,
                        Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType,
                        MultivaluedMap<String, Object> stringObjectMultivaluedMap,
                        OutputStream outputStream) throws IOException, WebApplicationException {

        logger.info("Executing and streaming the SPARQL results");
        sparqlQueryResults.executeAndStreamResults(outputStream, mediaType);
    }

    private final Logger logger = Logger.getLogger(SparqlQueryResultsProvider.class);
}
