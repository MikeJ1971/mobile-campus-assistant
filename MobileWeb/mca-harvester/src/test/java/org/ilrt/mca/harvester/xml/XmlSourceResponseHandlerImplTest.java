package org.ilrt.mca.harvester.xml;

import com.hp.hpl.jena.rdf.model.Model;
import org.ilrt.mca.harvester.AbstractTest;
import org.ilrt.mca.harvester.HttpResolverImpl;
import org.ilrt.mca.harvester.Resolver;
import org.ilrt.mca.harvester.Source;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class XmlSourceResponseHandlerImplTest extends AbstractTest {

    @Before
    public void setUp() throws IOException {
        super.startServer(resourcePath, mediaType);
    }

    @After
    public void tearDown() {
        super.stopServer();
    }

    @Test
    public void resolve() throws Exception {

        // having an oldish last visited date
        GregorianCalendar lastVisited = new GregorianCalendar(2008, Calendar.SEPTEMBER, 24);

        // resolve!
        Resolver resolver = new HttpResolverImpl();
        Source source = new Source(host + ":" + portNumber + resourcePath, lastVisited.getTime());
        Model model = resolver.resolve(source, new XmlSourceResponseHandlerImpl(xslFilePath));

        assertNotNull("The model should not be null", model);
        assertEquals("There should be 1 triple", 1, model.size());
    }

    private final String xslFilePath = "/xsl/weatherData.xsl";
    private final String resourcePath = "/weather.xml";
    private final String mediaType = "application/xml";
}
