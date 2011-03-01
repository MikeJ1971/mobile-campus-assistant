/*
 * Copyright (c) 2009, University of Bristol
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
package org.ilrt.mca.rest.providers;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.ilrt.mca.Common;
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

/**
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
@Provider
@Produces({RdfMediaType.APPLICATION_RDF_XML, RdfMediaType.TEXT_RDF_N3})
@Consumes({RdfMediaType.APPLICATION_RDF_XML, RdfMediaType.TEXT_RDF_N3})
public final class JenaModelRdfProvider implements MessageBodyWriter<Object>,
        MessageBodyReader<Object> {

    // ---- Writer implementation

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
        model.setNsPrefixes(Common.getCommonPrefixes());

        // defaults to N3
        if (mediaType.getType().equals("text") && mediaType.getSubtype().startsWith("n3")) {
            model.write(outputStream, "N3");
        } else {
            model.write(outputStream, "RDF/XML-ABBREV");
        }

    }

    // ---- Reader implementation

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
