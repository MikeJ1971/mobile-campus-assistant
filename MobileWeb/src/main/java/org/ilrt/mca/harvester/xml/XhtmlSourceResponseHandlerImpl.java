package org.ilrt.mca.harvester.xml;

import com.hp.hpl.jena.rdf.model.Model;
import org.ilrt.mca.harvester.ResponseHandler;
import org.w3c.tidy.Tidy;
import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.ByteArrayInputStream;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class XhtmlSourceResponseHandlerImpl extends AbstractXmlSourceResponseHandlerImpl
        implements ResponseHandler {

    public XhtmlSourceResponseHandlerImpl(String xslFilePath) {
        this.xslFilePath = xslFilePath;
    }

    @Override
    public Model getModel(String sourceUri, InputStream is) {

        try {
            Tidy tidy = new Tidy();
            tidy.setXHTML(true);
            tidy.setNumEntities(true);

            StringWriter writer = new StringWriter();
            tidy.parse(is, writer);

            XMLReader xmlReader = XMLReaderFactory.createXMLReader();
            xmlReader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd",
                    false);

            Source xmlSource = new SAXSource(xmlReader, new InputSource(new StringReader(writer.getBuffer().toString())));
            Source xslSource = new StreamSource(getClass().getResourceAsStream(xslFilePath));

            return getModelFromXml(xmlSource, xslSource, sourceUri);

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean isSupportedMediaType(String mediaType) {
        return mediaType.startsWith("text/html") || mediaType.startsWith("application/xhtml");
    }

    final private String xslFilePath;
}