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
package org.ilrt.mca.cache;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.Assert.assertEquals;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class SimpleCachingAcceptHeadersPageCachingFilterTest {

    @Before
    public void setUp() {

        filter = new SimpleCachingAcceptHeadersPageCachingFilter();
        request = new MockHttpServletRequest();
        request.setRequestURI(requestURI);
        request.setQueryString(queryString);
    }

    @Test
    public void testCalculateKeyForWebkit() {

        request.addHeader(acceptHeader, webkitAccept);
        String key = filter.calculateKey(request);
        assertEquals("Unexpected key", requestURI + queryString, key);
    }

    @Test
    public void testCalculateKeyForFirefox() {

        request.addHeader(acceptHeader, firefoxAccept);
        String key = filter.calculateKey(request);
        assertEquals("Unexpected key", requestURI + queryString, key);
    }

    @Test
    public void testCalculateKeyForJson() {

        request.addHeader(acceptHeader, jsonAccept);
        String key = filter.calculateKey(request);
        assertEquals("Unexpected key", requestURI + queryString + jsonAccept, key);
    }

    @Test
    public void testCalculateKeyForN3() {

        request.addHeader(acceptHeader, n3Accept);
        String key = filter.calculateKey(request);
        assertEquals("Unexpected key", requestURI + queryString + n3Accept, key);
    }

    @Test
    public void testCalculateKeyForRdf() {

        request.addHeader(acceptHeader, rdfAccept);
        String key = filter.calculateKey(request);
        assertEquals("Unexpected key", requestURI + queryString + rdfAccept, key);
    }

    private SimpleCachingAcceptHeadersPageCachingFilter filter;
    private MockHttpServletRequest request;

    // uri and query string
    private final String requestURI = "/";
    private final String queryString = null;

    // possible accept headers values
    private final String acceptHeader = "accept";
    private final String webkitAccept = "application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;"
        + "q=0.8,image/png,*/*;q=0.5";
    private final String firefoxAccept = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
    private final String jsonAccept = "application/json";
    private final String n3Accept = "text/n3";
    private final String rdfAccept = "application/rdf+xml";
}
