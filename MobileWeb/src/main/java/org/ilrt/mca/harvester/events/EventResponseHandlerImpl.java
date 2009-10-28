package org.ilrt.mca.harvester.events;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.ilrt.mca.harvester.ResponseHandler;

import java.io.InputStream;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class EventResponseHandlerImpl implements ResponseHandler {

    @Override
    public Model getModel(String sourceUrl, InputStream is) {

        // read into a model
        Model model = ModelFactory.createDefaultModel();
        model.read(sourceUrl);

        return model;

    }

    @Override
    public boolean isSupportedMediaType(String mediaType) {
        return mediaType.startsWith("text/xml");
    }


}
