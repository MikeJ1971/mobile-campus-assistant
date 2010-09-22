package org.ilrt.mca.freemarker;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDFS;
import freemarker.template.SimpleScalar;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test the FreeMarker template class used to wrap a resource in a hash.
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class ResourceHashModelTest {

    @Before
    public void setUp() {
        
        Resource resource = createTestResource();

        assertEquals("Unexpected model size", SIZE, resource.getModel().size());

        resourceHashModel = new ResourceHashModel(resource);
    }

    @Test
    public void testDefaultProvidedNameSpaces() {

        // We test that expected prefixes are available. Adding new prefixes won't break the test but
        // any removed will be caught - this is useful since templates in the view layer will expect
        // these prefixes to exist.

        PrefixMapping prefixMapping = resourceHashModel.prefixMapping;

        // jena defines 5 by default and we define an extra 3
        assertTrue(prefixMapping.getNsPrefixMap().size() >= 5);

        // test that we have the expected predefined values from jena
        assertEquals("Unexpected expanded prefix", "http://purl.org/dc/elements/1.1/",
                prefixMapping.expandPrefix("dc:"));
        assertEquals("Unexpected expanded prefix", "http://www.w3.org/2000/01/rdf-schema#",
                prefixMapping.expandPrefix("rdfs:"));
        assertEquals("Unexpected expanded prefix", "http://www.w3.org/2002/07/owl#",
                prefixMapping.expandPrefix("owl:"));
        assertEquals("Unexpected expanded prefix", "http://www.w3.org/2001/XMLSchema#",
                prefixMapping.expandPrefix("xsd:"));
        assertEquals("Unexpected expanded prefix", "http://www.w3.org/1999/02/22-rdf-syntax-ns#",
                prefixMapping.expandPrefix("rdf:"));

        // test that we have the values provided in the constructor
        assertEquals("Unexpected expanded prefix", "http://xmlns.com/foaf/0.1/",
                prefixMapping.expandPrefix("foaf:"));
        assertEquals("Unexpected expanded prefix", "http://www.w3.org/2003/01/geo/wgs84_pos#",
                prefixMapping.expandPrefix("geo:"));
        assertEquals("Unexpected expanded prefix", "http://purl.org/dc/terms/",
                prefixMapping.expandPrefix("dcterms:"));
    }


    @Test
    public void testSize() throws TemplateModelException {

        assertEquals("Unexpected size", SIZE, resourceHashModel.size());
    }

    @Test
    public void testKeys() throws TemplateModelException {

        ArrayList<String> keysList = new ArrayList<String>();

        TemplateCollectionModel collection = resourceHashModel.keys();

        TemplateModelIterator i = collection.iterator();

        while (i.hasNext()) {
            keysList.add(((SimpleScalar)i.next()).getAsString());
        }

        // check the keys
        assertEquals("Unexpected size", 2, keysList.size());
        assertTrue("Unexpected key", keysList.contains(RDFS.label.getURI()));
        assertTrue("Unexpected key", keysList.contains(DC.identifier.getURI()));
    }

    @Test
    public void testValues() throws TemplateModelException {

        List<TemplateModel> valueList = new ArrayList<TemplateModel>();

        TemplateCollectionModel collection = resourceHashModel.values();

        TemplateModelIterator i = collection.iterator();

        while (i.hasNext()) {
            valueList.add(i.next());
        }

        // check the number of keys
        assertEquals("Unexpected size", SIZE, valueList.size());

        // check the first key value
//        assertEquals("Unexpected key", label,
 //               ((SimpleScalar) valueList.get(0)).getAsString());
    }

    @Test
    public void testGet() throws TemplateModelException {

        SimpleSequence collection = (SimpleSequence) resourceHashModel.get(RDFS.label.getURI());

        for (Object o : collection.toList()) {
            String s = (String) o;
            assertEquals("Unexpected label", label, s);
        }

    }

    @Test
    public void testEmpty() throws TemplateModelException {

        // check the number of keys
        assertFalse("The resource is not empty", resourceHashModel.isEmpty());
    }

    @Test
    public void testAsString() throws TemplateModelException {

        // check that we get the uri
        assertEquals("Unexpected string value", uri, resourceHashModel.getAsString());
    }




    private Resource createTestResource() {

        Model model = ModelFactory.createDefaultModel();
        Resource resource = model.createResource(uri);
        resource.addLiteral(RDFS.label, label);
        resource.addLiteral(DC.identifier, identifier);
        return resource;
    }


    private String uri = "http://example.org/1/";
    private String label = "This is a label";
    private int identifier = 999999;


    private ResourceHashModel resourceHashModel;

    private final int SIZE = 2;
}
