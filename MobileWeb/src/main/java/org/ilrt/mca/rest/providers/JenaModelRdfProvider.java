package org.ilrt.mca.rest.providers;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.ilrt.mca.RdfMediaType;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
@Produces({RdfMediaType.APPLICATION_RDF_XML, RdfMediaType.TEXT_RDF_N3})
@Consumes({RdfMediaType.APPLICATION_RDF_XML, RdfMediaType.TEXT_RDF_N3})
public final class JenaModelRdfProvider implements MessageBodyWriter<Object>,
        MessageBodyReader<Object> {

    // ---- Writer implememtation

    public boolean isWriteable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return Model.class.isAssignableFrom(aClass);
    }

    public long getSize(Object o, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    public void writeTo(final Object o, final Class<?> aClass, final Type type,
                        final Annotation[] annotations, final MediaType mediaType,
                        final MultivaluedMap<String, Object> stringObjectMultivaluedMap,
                        final OutputStream outputStream) throws IOException,
            WebApplicationException {

        Model model = (Model) o;

        // defaults to N3
        if (mediaType.getType().equals("text") && mediaType.getSubtype().startsWith("n3")) {
            model.write(outputStream, "N3");
        } else {
            model.write(outputStream, "RDF/XML-ABBREV");
        }

    }

    // ---- Reader implememtation

    public boolean isReadable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return aClass == Model.class;
    }

    public Object readFrom(Class<Object> objectClass, Type type, Annotation[] annotations,
                           MediaType mediaType,
                           MultivaluedMap<String, String> stringStringMultivaluedMap,
                           InputStream inputStream) throws IOException, WebApplicationException {

        Model model = ModelFactory.createDefaultModel();

        // defaults to RDF/XML
        if (mediaType.getType().equals("text") && mediaType.getSubtype().equals("rdf+n3")) {
            model.read(inputStream, null, "N3");
        } else {
            model.read(inputStream, null, "RDF/XML-ABBREV");
        }

        return model;
    }
}
