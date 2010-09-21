package org.ilrt.mca.harvester;

import com.hp.hpl.jena.rdf.model.Model;
import org.ilrt.mca.harvester.xml.XhtmlSourceResponseHandlerImpl;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

import java.io.InputStream;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class XhtmlSourceResponseHandlerImplTest {

    @Test
    public void resolve() throws Exception {

        String xslFilePath = "/xsl/pcavailability.xsl";
        String url = "http://is-freepcs.cse.bris.ac.uk/";
        InputStream is = this.getClass().getResourceAsStream("/pcavailability.html");

        ResponseHandler handler = new XhtmlSourceResponseHandlerImpl(xslFilePath);
        Model model = handler.getModel(url, is);

        model.write(System.out, "N3");

        assertNotNull("The model should not be null", model);
        assertEquals("There should be 1 triple", 1, model.size());
    }


}