package org.ilrt.mca.harvester.events;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.util.FileManager;
import org.ilrt.mca.harvester.ResponseHandler;
import org.ilrt.mca.harvester.xml.AbstractXmlSourceResponseHandlerImpl;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class EventResponseHandlerImpl extends AbstractXmlSourceResponseHandlerImpl implements ResponseHandler {
    private String xslFilePath;

    public EventResponseHandlerImpl(String xslFilePath) {
        this.xslFilePath = xslFilePath;
    }

    @Override
    public Model getModel(String sourceUrl, InputStream is) {
        Model model;

        // check if we have an xsl file supplied
        if (xslFilePath != null && !xslFilePath.equals("")) {
            // if so, apply the transform to the xml source and return the result as a model
            try {
                XMLReader xmlReader = XMLReaderFactory.createXMLReader();
                xmlReader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd",
                        false);

                String contents = inputStreamToString(is);
                contents = contents.replaceAll("&", "&amp;");

                Source xmlSource = new SAXSource(xmlReader, new InputSource(new StringReader(contents)));
                Source xslSource = new StreamSource(getClass().getResourceAsStream(xslFilePath));

                return getModelFromXml(xmlSource, xslSource, sourceUrl);
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new RuntimeException(ex);
            }
        } else {
            // otherwise we have a direct rdf file, simply load it from the url
            model = FileManager.get().loadModel(sourceUrl);
        }

        return model;

    }

    @Override
    public boolean isSupportedMediaType(String mediaType) {
        return mediaType.startsWith("text/xml") || mediaType.startsWith("application/rdf+xml");
    }

    private String inputStreamToString(InputStream in) {
        StringBuffer out = new StringBuffer();
        try {
            byte[] b = new byte[4096];
            for (int n; (n = in.read(b)) != -1;) {
                out.append(new String(b, 0, n));
            }
            in.close();
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return out.toString();
    }
}
