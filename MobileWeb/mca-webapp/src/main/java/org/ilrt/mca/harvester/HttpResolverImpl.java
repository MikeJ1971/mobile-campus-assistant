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
package org.ilrt.mca.harvester;

import com.hp.hpl.jena.rdf.model.Model;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class HttpResolverImpl implements Resolver {

    public HttpResolverImpl() throws IOException {

        properties = new Properties();
        properties.load(this.getClass().getResourceAsStream("/httpResolver.properties"));
    }

    /**
     * @param source          source we want to resolve
     * @param responseHandler handles the response and creates a model
     * @return a model
     */
    public Model resolve(Source source, ResponseHandler responseHandler) {


        HttpClient httpClient = new HttpClient();

        // set the user agent (default provide by the apache client if null)
        if (properties.getProperty("user.agent") != null) {
            httpClient.getParams().setParameter(HttpMethodParams.USER_AGENT,
                    properties.getProperty("user.agent"));
        }


        HttpMethod httpMethod = new GetMethod(source.getUrl());

        // only resolve if the source has been updated
        if (source.getLastVisited() != null) {
            httpMethod.addRequestHeader("If-Modified-Since",
                    getDateFormat(source.getLastVisited()));
        }


        // request the url and get the status
        int status;

        try {
            status = httpClient.executeMethod(httpMethod);

        } catch (IOException e) {

            log.error("Error trying to GET " + source.getUrl() + " : " + e.getMessage());
            return null;
        }


        // handle unexpected response codes

        if (status != HttpStatus.SC_OK) {

            // TODO - what about access to feeds that need authentication?
            if (status > 400) {
                log.info("The requested resource " + source.getUrl() + " failed to return with the "
                        + "following response code: " + status + ")");
                return null;
            }
        }

        log.info("The requested resource " + source.getUrl()
                + " returned the following response code: " + status);

        // get the content type
        String contentType = httpMethod.getResponseHeader("Content-Type").getValue();

        // only handle data if we have expected data type
        if (responseHandler.isSupportedMediaType(contentType)) {

            try {

                log.info("Handler can respond to the content type: " + contentType);

                InputStream is = httpMethod.getResponseBodyAsStream();

                return responseHandler.getModel(source.getUrl(), is);

            } catch (IOException e) {
                log.error("Error occured when handling response: " + e.getMessage());

            }
        } else {
            log.info("Handler cannot handle the content type: " + contentType);
        }

        return null;
    }

    private String getDateFormat(Date date) {

        SimpleDateFormat httpDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
        httpDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return httpDateFormat.format(date);
    }


    Logger log = Logger.getLogger(HttpResolverImpl.class);
    Properties properties;
}