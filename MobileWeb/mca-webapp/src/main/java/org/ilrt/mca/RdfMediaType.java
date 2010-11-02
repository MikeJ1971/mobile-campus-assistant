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
package org.ilrt.mca;

import javax.ws.rs.core.MediaType;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class RdfMediaType {

    private RdfMediaType() {
    }

    // application/rdf+xml

    public final static String APPLICATION_RDF_XML = "application/rdf+xml";

    public final static MediaType APPLICATION_RDF_XML_TYPE =
            new MediaType("application", "rdf+xml");

    // text/n3

    public final static String TEXT_RDF_N3 = "text/n3";

    public final static MediaType TEXT_RDF_N3_TYPE =
            new MediaType("text", "n3");

    // application/sparql-results+json

    public final static String SPARQL_RESULTS_JSON = "application/sparql-results+json";

    public final static MediaType SPARQL_RESULTS_JSON_TYPE =
            new MediaType("application", "sparql-results+json");


    // application/sparql-results+json

    public final static String SPARQL_RESULTS_XML = "application/sparql-results+xml";

    public final static MediaType SPARQL_RESULTS_XML_TYPE =
            new MediaType("application", "sparql-results+xml");
}
