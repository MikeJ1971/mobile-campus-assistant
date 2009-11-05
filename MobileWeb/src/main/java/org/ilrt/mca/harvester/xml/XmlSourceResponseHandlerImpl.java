package org.ilrt.mca.harvester.xml;

import com.hp.hpl.jena.rdf.model.Model;
import org.ilrt.mca.harvester.ResponseHandler;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class XmlSourceResponseHandlerImpl extends AbstractXmlSourceResponseHandlerImpl
        implements ResponseHandler {

    public XmlSourceResponseHandlerImpl(String xslFilePath) {
        this.xslFilePath = xslFilePath;
    }

    @Override
    public Model getModel(String sourceUri, InputStream is) {

        Source xmlSource = new StreamSource(is);
        Source xslSource = new StreamSource(getClass().getResourceAsStream(xslFilePath));

        return getModelFromXml(xmlSource, xslSource, sourceUri);
    }

    @Override
    public boolean isSupportedMediaType(String mediaType) {
        return mediaType.startsWith("text/xml") || mediaType.startsWith("application/xml");
    }

    final private String xslFilePath;
}
