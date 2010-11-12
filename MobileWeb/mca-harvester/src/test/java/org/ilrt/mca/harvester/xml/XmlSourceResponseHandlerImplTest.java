package org.ilrt.mca.harvester.xml;

import com.hp.hpl.jena.rdf.model.Model;
import org.ilrt.mca.harvester.ResponseHandler;
import org.ilrt.mca.harvester.xml.XmlSourceResponseHandlerImpl;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

import java.io.InputStream;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class XmlSourceResponseHandlerImplTest {

    @Test
    public void resolve() throws Exception {

        String xslFilePath = "/xsl/weatherData.xsl";
        String url = "http://portal.bris.ac.uk/portal-weather/newXml";
        InputStream is = this.getClass().getResourceAsStream("/weather.xml");

        ResponseHandler handler = new XmlSourceResponseHandlerImpl(xslFilePath);
        Model model = handler.getModel(url, is);

        assertNotNull("The model should not be null", model);
        assertEquals("There should be 1 triple", 1, model.size());
    }


}
