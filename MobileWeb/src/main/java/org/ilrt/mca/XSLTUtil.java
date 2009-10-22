/**
 * 
 */
package org.ilrt.mca;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * @author ecjet
 *
 */
public class XSLTUtil {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
        try {

            // get the xml and xsl sources
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Source xslSource = new StreamSource(Class.class.getClass().getResourceAsStream(args[0]));
            Source xmlSource = new StreamSource(Class.class.getClass().getResourceAsStream(args[1]));

            // output for the transformer
            StringWriter writer = new StringWriter();
            javax.xml.transform.Result result =
                    new javax.xml.transform.stream.StreamResult(writer);

            // transform ...
            Transformer transformer = transformerFactory.newTransformer(xslSource);
            transformer.transform(xmlSource, result);

            System.out.println(writer.toString());

        } catch (TransformerConfigurationException e) {
            throw new RuntimeException(e);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
	}

}
