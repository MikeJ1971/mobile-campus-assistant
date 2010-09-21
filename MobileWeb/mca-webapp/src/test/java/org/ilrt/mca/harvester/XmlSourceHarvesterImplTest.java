package org.ilrt.mca.harvester;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.sdb.SDBFactory;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.DC;
import org.ilrt.mca.AbstractTest;
import org.ilrt.mca.Common;
import org.ilrt.mca.harvester.xml.XmlSourceHarvesterImplImpl;
import org.ilrt.mca.rdf.DataManager;
import org.ilrt.mca.rdf.SdbManagerImpl;
import org.ilrt.mca.rdf.StoreWrapper;
import org.ilrt.mca.rdf.StoreWrapperManager;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class XmlSourceHarvesterImplTest extends AbstractTest {

    @Before
    public void setUp() throws Exception {

        // clear data store
        setUpStore();

        // load the data
        StoreWrapperManager manager = getStoreWrapperManager();
        StoreWrapper storeWrapper = manager.getStoreWrapper();
        Dataset dataset = SDBFactory.connectDataset(storeWrapper.getStore());
        dataset.getDefaultModel().add(FileManager.get().loadModel("test-registry.ttl"));

        // create a last visited date and add it to a graph
        Model model = dataset.getNamedModel(Common.AUDIT_GRAPH_URI);
        Resource resource = ResourceFactory.createResource(feedUrl);
        Calendar calendar = new GregorianCalendar(2009, Calendar.SEPTEMBER, 30, 11, 38);
        date = Common.parseXsdDate(calendar.getTime());
        model.add(model.createStatement(resource, DC.date,
                model.createTypedLiteral(date, XSDDatatype.XSDdateTime)));

        storeWrapper.close();

        dataManager = new SdbManagerImpl(manager);
    }

    @Test
    public void harvest() throws Exception {

        StoreWrapper storeWrapper = getStoreWrapper();
        Model m = SDBFactory.connectNamedModel(storeWrapper.getStore(), Common.AUDIT_GRAPH_URI);

        assertTrue(m.getResource(feedUrl).hasProperty(DC.date));
        assertEquals(date, m.getResource(feedUrl).getProperty(DC.date).getLiteral()
                .getLexicalForm());
        storeWrapper.close();

        Harvester harvester = new XmlSourceHarvesterImplImpl(dataManager);
        harvester.harvest();

        storeWrapper = getStoreWrapper();
        Model afterModel = SDBFactory.connectNamedModel(storeWrapper.getStore(),
                Common.AUDIT_GRAPH_URI);

        assertTrue(afterModel.getResource(feedUrl).hasProperty(DC.date));
        String newDate = afterModel.getResource(feedUrl).getProperty(DC.date).getLiteral()
                .getLexicalForm();

        assertFalse(date.equals(newDate));

        storeWrapper.close();
    }

    DataManager dataManager;

    // these need to be in the test-registry.ttl file
    String feedUrl = "http://portal.bris.ac.uk/portal-weather/newXml";
    //String uri = "mca://registry/news/events/";
    String date;
}