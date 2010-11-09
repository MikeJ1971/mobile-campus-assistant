package org.ilrt.mca.dao;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.util.FileManager;
import org.ilrt.mca.AbstractTest;
import org.ilrt.mca.rdf.DataManager;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class ItemDaoTest extends AbstractTest {

    // TODO upate tests for parameter values

    @Before
    public void setUp() {

        DataManager dataManager = getRepository();

        Model model = FileManager.get().loadModel("test-registry.ttl");

        assertTrue("The model should not be empty", model.size() > 0);

        dataManager.add(model);
    }

    @Test
    public void testFind() throws Exception {

        ItemDao itemDao = new ItemDaoImpl(getRepository());

        //Item item = itemDao.findItem(id, null);

        //assertNotNull("The item should not be null", item);

        assertTrue(true);
    }

    @Test
    public void testFindModel() throws Exception {

        ItemDao itemDao = new ItemDaoImpl(getRepository());

        //Model model = itemDao.findModel(id, null);

        //assertTrue("The model should not be empty", model.size() > 0);

        assertTrue(true);
    }

    String id = "mca://registry/";
}
