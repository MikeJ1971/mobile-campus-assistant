package org.ilrt.mca.rest;

import javax.ws.rs.core.Response;

public interface ResponseResolver {

    Response reponse(String path);

}
