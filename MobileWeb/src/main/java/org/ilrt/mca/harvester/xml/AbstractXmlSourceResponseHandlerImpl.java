package org.ilrt.mca.harvester.xml;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.ilrt.mca.harvester.ResponseHandler;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

public abstract class AbstractXmlSourceResponseHandlerImpl implements ResponseHandler {

    @Override
    public abstract Model getModel(String sourceUri, InputStream is);

    @Override
    public abstract boolean isSupportedMediaType(String mediaType);

    protected Model getModelFromXml(Source xmlSource, Source xslSource, String sourceUri) {

        Model model;

        try {

            TransformerFactory transformerFactory = TransformerFactory.newInstance();

            // output for the transformer
            StringWriter writer = new StringWriter();
            javax.xml.transform.Result result =
                    new javax.xml.transform.stream.StreamResult(writer);

            // transform ...
            Transformer transformer = transformerFactory.newTransformer(xslSource);
            transformer.setParameter("uri", sourceUri);
            transformer.transform(xmlSource, result);

            // create the model
            model = ModelFactory.createDefaultModel();
            model.read(new StringReader(writer.getBuffer().toString()), sourceUri);

        } catch (TransformerConfigurationException e) {
            throw new RuntimeException(e);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }

        return model;
    }

}

