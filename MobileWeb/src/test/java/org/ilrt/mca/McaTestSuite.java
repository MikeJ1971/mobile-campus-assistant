package org.ilrt.mca;

import org.ilrt.mca.dao.ItemDaoTest;
import org.ilrt.mca.harvester.FeedHarvesterImplTest;
import org.ilrt.mca.harvester.FeedResponseHandlerImplTest;
import org.ilrt.mca.rdf.SdbRepositoryImplTest;
import org.ilrt.mca.rdf.StoreWrapperImplTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        StoreWrapperImplTest.class,
        SdbRepositoryImplTest.class,
        ItemDaoTest.class,
        FeedResponseHandlerImplTest.class,
        FeedHarvesterImplTest.class
})
public class McaTestSuite {
    // all done in the annotations ...
}
