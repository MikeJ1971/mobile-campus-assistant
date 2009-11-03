package org.ilrt.mca.harvester.events;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.util.FileManager;
import org.ilrt.mca.harvester.ResponseHandler;

import java.io.InputStream;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class EventResponseHandlerImpl implements ResponseHandler {

    @Override
    public Model getModel(String sourceUrl, InputStream is) {

        Model model = FileManager.get().loadModel(sourceUrl);

        return model;

    }

    @Override
    public boolean isSupportedMediaType(String mediaType) {
        return mediaType.startsWith("text/xml");
    }
}
