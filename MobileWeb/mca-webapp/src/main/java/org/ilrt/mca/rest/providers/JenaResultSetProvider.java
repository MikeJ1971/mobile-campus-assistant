package org.ilrt.mca.rest.providers;

import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import org.ilrt.mca.RdfMediaType;

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
@Produces({RdfMediaType.SPARQL_RESULTS_JSON, RdfMediaType.SPARQL_RESULTS_XML})
public class JenaResultSetProvider implements MessageBodyWriter<ResultSet> {
    @Override
    public boolean isWriteable(Class<?> aClass, Type type, Annotation[] annotations,
                               MediaType mediaType) {
        return ResultSet.class.isAssignableFrom(aClass);
    }

    @Override
    public long getSize(ResultSet resultSet, Class<?> aClass, Type type, Annotation[] annotations,
                        MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(ResultSet resultSet, Class<?> aClass, Type type, Annotation[] annotations,
                        MediaType mediaType, MultivaluedMap<String, Object> stringObjectMultivaluedMap,
                        OutputStream outputStream) throws IOException, WebApplicationException {

        if (mediaType.equals(RdfMediaType.SPARQL_RESULTS_XML_TYPE)) {
            ResultSetFormatter.outputAsXML(outputStream, resultSet);
        } else {
            ResultSetFormatter.outputAsJSON(outputStream, resultSet);
        }

    }
}
