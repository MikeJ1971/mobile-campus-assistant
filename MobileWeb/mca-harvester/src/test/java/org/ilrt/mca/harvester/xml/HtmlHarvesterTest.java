package org.ilrt.mca.harvester.xml;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sdb.SDBFactory;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDF;
import org.ilrt.mca.Common;
import org.ilrt.mca.harvester.AbstractTest;
import org.ilrt.mca.harvester.Harvester;
import org.ilrt.mca.rdf.SdbManagerImpl;
import org.ilrt.mca.rdf.StoreWrapper;
import org.ilrt.mca.rdf.StoreWrapperManager;
import org.ilrt.mca.vocab.MCA_REGISTRY;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class HtmlHarvesterTest extends AbstractTest {

    @Before
    public void setUp() throws Exception {

        // clear data store
        setUpStore();

        // create data set
        StoreWrapperManager manager = getStoreWrapperManager();
        StoreWrapper storeWrapper = manager.getStoreWrapper();
        Dataset dataset = SDBFactory.connectDataset(storeWrapper.getStore());

        // add the harvest source details to the default graph (registry)
        Model m = dataset.getDefaultModel();
        Resource r = m.createResource(uri);
        m.add(m.createStatement(r, RDF.type, MCA_REGISTRY.HtmlSource));
        m.add(m.createStatement(r, MCA_REGISTRY.hasXslSource,
                m.createResource("xsl://xsl/pcavailability.xsl")));

        // create a last visited date and add it to the audit graph
        Model audit = dataset.getNamedModel(Common.AUDIT_GRAPH_URI);
        Calendar calendar = new GregorianCalendar(2009, Calendar.SEPTEMBER, 30, 11, 38);
        date = Common.parseXsdDate(calendar.getTime());
        audit.add(audit.createStatement(r, DC.date,
                audit.createTypedLiteral(date, XSDDatatype.XSDdateTime)));

        // clean up
        storeWrapper.close();

        // start the web server
        super.startServer(resourcePath, mediaType);

        // data manager that can be used by the harvester
        dataManager = new SdbManagerImpl(manager);

    }


    @After
    public void tearDown() {
        super.stopServer();
    }

    @Test
    public void harvest() throws Exception {

        // ---------- test the data before we harvest

        StoreWrapper beforeWrapper = getStoreWrapper();

        // check there the registry has a source to harvest
        Model registry = SDBFactory.connectDefaultModel(beforeWrapper.getStore());
        Assert.assertTrue("There should be a harvest source in the registry (default graph)",
                registry.contains(registry.getResource(uri), RDF.type,
                        MCA_REGISTRY.HtmlSource));

        // check that the audit graph has a date
        Model auditModel = SDBFactory.connectNamedModel(beforeWrapper.getStore(),
                Common.AUDIT_GRAPH_URI);
        Assert.assertTrue(auditModel.getResource(uri).hasProperty(DC.date));
        assertEquals(date, auditModel.getResource(uri).getProperty(DC.date).getLiteral()
                .getLexicalForm());
        beforeWrapper.close();

        // ---------- fire the harvester

        Harvester harvester = new XhtmlHarvesterImpl(dataManager);
        harvester.harvest();

        // ---------- test the data after the harvest

        StoreWrapper afterWrapper = getStoreWrapper();

        // check that the harvest graph has got data
        Model harvestedData = SDBFactory.connectNamedModel(afterWrapper.getStore(), uri);
        assertFalse("The model shouldn't be empty", harvestedData.isEmpty());

        auditModel = SDBFactory.connectNamedModel(afterWrapper.getStore(),
                Common.AUDIT_GRAPH_URI);

        // check that the audit date has been updated
        String newDate = auditModel.getResource(uri).getProperty(DC.date).getLiteral()
                .getLexicalForm();
        assertFalse(date.equals(newDate));

        afterWrapper.close();
    }


    String date;
    private final String resourcePath = "/pcavailability.html";
    private final String mediaType = "text/html";
    String uri = host + ":" + portNumber + resourcePath;

}
