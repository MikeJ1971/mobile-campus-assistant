package org.ilrt.mca.freemarker;

import com.hp.hpl.jena.rdf.model.ResourceFactory;
import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Test the custom object for wrapping Jena objects.
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class JenaObjectWrapperTest {

    @Before
    public void setUp() {
        wrapper = new JenaObjectWrapper();
    }

    @Test
    public void testJenaObjectWrapper() throws TemplateModelException {

        TemplateModel model = wrapper.wrap(ResourceFactory.createResource());
        assertTrue(model instanceof ResourceHashModel);
    }

    @Test
    public void testSuperClassCalled() throws TemplateModelException {

        TemplateModel model = wrapper.wrap("");
        assertTrue(model instanceof SimpleScalar);
    }

    private ObjectWrapper wrapper;

}
