package org.ilrt.mca.harvester.geo;

import com.hp.hpl.jena.rdf.model.Model;
import org.ilrt.mca.harvester.ResponseHandler;

import java.io.InputStream;


public class OpenStreetMapResponseHandlerImpl implements ResponseHandler {

    @Override
    public Model getModel(String sourceUri, InputStream is) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isSupportedMediaType(String mediaType) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
