package org.ilrt.mca.harvester;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.sdb.SDBFactory;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import org.ilrt.mca.harvester.feeds.FeedHarvesterImpl;
import org.ilrt.mca.rdf.Repository;
import org.ilrt.mca.rdf.SdbRepositoryImpl;
import org.ilrt.mca.rdf.StoreWrapperManager;
import org.ilrt.mca.rdf.StoreWrapperManagerImpl;
import org.ilrt.mca.Common;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class FeedHarvesterImplTest {

    @Before
    public void setUp() {

        // load the data
        StoreWrapperManager manager = new StoreWrapperManagerImpl("/test-sdb.ttl");
        Dataset dataset = SDBFactory.connectDataset(manager.getStoreWrapper().getStore());
        dataset.getDefaultModel().add(FileManager.get().loadModel("test-registry.ttl"));

        // create a last visited date and add it to a graph
        Model model = dataset.getNamedModel(Common.AUDIT_GRAPH_URI);
        Resource resource = ResourceFactory.createResource(uri);
        Calendar calendar = new GregorianCalendar(2009, Calendar.SEPTEMBER, 30, 11, 38);
        String date = Common.parseDate(calendar.getTime());
        model.add(model.createStatement(resource, DC.date,
                model.createTypedLiteral(date, XSDDatatype.XSDdateTime)));

        repository = new SdbRepositoryImpl(manager);
    }

    @Test
    public void harvest() throws Exception {


        Harvester harvester = new FeedHarvesterImpl(repository);
        harvester.harvest();

        assertTrue(true);
    }

    Repository repository;

    // these need to be in the test-registry.ttl file
    String feedUrl = "http://www.bris.ac.uk/news/news-feed.rss";
    String uri = "mca://registry/news/events/";

}
