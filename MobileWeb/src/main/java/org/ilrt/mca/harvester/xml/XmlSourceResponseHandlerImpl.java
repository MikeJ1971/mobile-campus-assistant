package org.ilrt.mca.harvester.xml;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.ilrt.mca.harvester.ResponseHandler;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class XmlSourceResponseHandlerImpl implements ResponseHandler {

    public XmlSourceResponseHandlerImpl(String xslFilePath) {
        this.xslFilePath = xslFilePath;
    }

    @Override
    public Model getModel(String sourceUri, InputStream is) {

        Model model;

        try {

            // get the xml and xsl sources
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Source xslSource = new StreamSource(getClass().getResourceAsStream(xslFilePath));
            Source xmlSource = new StreamSource(is);

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

    @Override
    public boolean isSupportedMediaType(String mediaType) {
        return mediaType.startsWith("text/xml") || mediaType.startsWith("application/xml");
    }

    final private String xslFilePath;
}
