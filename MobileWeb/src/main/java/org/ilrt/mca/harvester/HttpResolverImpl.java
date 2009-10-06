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
            //TODO set in the properities file
            //httpClient.getParams().setParameter("http.protocol.content-charset", "UTF-8");
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

                InputStream is = httpMethod.getResponseBodyAsStream();

                return responseHandler.getModel(source.getUrl(), is);

            } catch (IOException e) {
                log.error("Error occured when handling response: " + e.getMessage());

            }
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