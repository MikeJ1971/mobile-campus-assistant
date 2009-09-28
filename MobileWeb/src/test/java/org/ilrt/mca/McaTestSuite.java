package org.ilrt.mca;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.ilrt.mca.dao.ItemDaoTest;
import org.ilrt.mca.rdf.StoreWrapperImplTest;
import org.ilrt.mca.rdf.SdbRepositoryImplTest;
import org.ilrt.mca.harvester.FeedHarvesterImplTest;

/**
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        StoreWrapperImplTest.class,
        ItemDaoTest.class,
        SdbRepositoryImplTest.class,
        FeedHarvesterImplTest.class
})
public class McaTestSuite {
    // all done in the annotations ...
}
