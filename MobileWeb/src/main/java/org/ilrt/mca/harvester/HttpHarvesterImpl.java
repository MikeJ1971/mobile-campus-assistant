package org.ilrt.mca.harvester;

import com.hp.hpl.jena.rdf.model.Model;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class HttpHarvesterImpl implements Harvester {

    /**
     * @param url             the URL of the source we want to harvest.
     * @param lastVisited     the date the source was list visited.
     * @param responseHandler handles the response and creates a model
     * @return a model
     */
    public Model harvest(String url, Date lastVisited, ResponseHandler responseHandler) {


        HttpClient httpClient = new HttpClient();
        HttpMethod httpMethod = new GetMethod(url);

        // only harvest if the source has been updated
        if (lastVisited != null) {
            httpMethod.addRequestHeader("If-Modified-Since", getDateFormat(lastVisited));
        }


        // request the url and get the status
        int status;

        try {
            status = httpClient.executeMethod(httpMethod);

        } catch (IOException e) {

            log.error("Error trying to GET " + url + " : " + e.getMessage());
            return null;
        }


        // handle unexpected response codes

        if (status != HttpStatus.SC_OK) {

            if (status > 400) {
                log.info("The requested resource " + url + " failed to return with the "
                        + "following response code: " + status + ")");
                return null;
            }
        }


        // get the content type
        String contentType = httpMethod.getResponseHeader("Content-Type").getValue();

        // only handle data if we have expected data type
        if (responseHandler.isSupportedMediaType(contentType)) {

            try {

                InputStream is = httpMethod.getResponseBodyAsStream();

                return responseHandler.getModel(is);

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


    Logger log = Logger.getLogger(HttpHarvesterImpl.class);
}