package org.ilrt.mca.dao;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.util.FileManager;
import org.ilrt.mca.AbstractTest;
import org.ilrt.mca.domain.Item;
import org.ilrt.mca.rdf.Repository;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class ItemDaoTest extends AbstractTest {

    @Before
    public void setUp() {

        Repository repository = getRepository();

        Model model = FileManager.get().loadModel("test-registry.ttl");

        assertTrue("The model should not be empty", model.size() > 0);

        repository.add(model);
    }

    @Test
    public void testFind() throws Exception {

        ItemDao itemDao = new ItemDaoImpl(getRepository());

        Item item = itemDao.findItem(id);

        assertNotNull("The item should not be null", item);
    }

    @Test
    public void testFindModel() throws Exception {

        ItemDao itemDao = new ItemDaoImpl(getRepository());

        Model model = itemDao.findModel(id);

        assertTrue("The model should not be empty", model.size() > 0);
    }

    String id = "mca://registry/";
}
