
package org.ilrt.mca.rest.providers;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.talis.rdfwriters.json.JSONJenaWriter;

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

/**
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
@Provider
@Produces({MediaType.APPLICATION_JSON})
public class JenaResourceJsonProvider implements MessageBodyWriter<Object> {

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations,
                               MediaType mediaType) {

        return Resource.class.isAssignableFrom(type) || Model.class.isAssignableFrom(type);
    }

    @Override
    public long getSize(Object o, Class<?> type, Type genericType, Annotation[] annotations,
                        MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(Object o, Class<?> type, Type genericType, Annotation[] annotations,
                        MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
                        OutputStream entityStream) throws IOException, WebApplicationException {

        Model m;

        if (o instanceof Resource) {
            m = ((Resource) o).getModel();
        } else {
            m = (Model) o;
        }

        JSONJenaWriter jsonJenaWriter = new JSONJenaWriter();
        jsonJenaWriter.write(m, entityStream, null);
    }
}
