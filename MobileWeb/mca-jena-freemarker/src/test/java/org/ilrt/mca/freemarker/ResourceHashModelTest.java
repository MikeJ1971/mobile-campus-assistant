package org.ilrt.mca.freemarker;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.VCARD;
import freemarker.template.SimpleDate;
import freemarker.template.SimpleNumber;
import freemarker.template.SimpleScalar;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import org.junit.Before;
import org.junit.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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

        assertEquals("Unexpected model size", 6, resource.getModel().size());

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

        assertEquals("Unexpected size", KEY_SIZE, resourceHashModel.size());
    }

    @Test
    public void testKeys() throws TemplateModelException {

        ArrayList<String> keysList = new ArrayList<String>();

        for (TemplateModelIterator i = resourceHashModel.keys().iterator(); i.hasNext();) {
            keysList.add(((SimpleScalar) i.next()).getAsString());
        }

        // check the keys
        assertEquals("Unexpected size", KEY_SIZE, keysList.size());
        assertTrue("Expected key: " + RDFS.label.getURI(), keysList.contains(RDFS.label.getURI()));
        assertTrue("Expected key: " + DC.identifier.getURI(), keysList.contains(DC.identifier.getURI()));
        assertTrue("Expected key: " + VCARD.CATEGORIES.getURI(), keysList.contains(VCARD.CATEGORIES.getURI()));
        assertTrue("Expected key: " + DC.publisher.getURI(), keysList.contains(DC.publisher.getURI()));
    }

    @Test
    public void testValues() throws TemplateModelException {

        ArrayList<String> valueList = new ArrayList<String>();

        for (TemplateModelIterator i = resourceHashModel.values().iterator(); i.hasNext();) {
            valueList.add((i.next()).toString());
        }

        // check the values
        assertEquals("Unexpected size", KEY_SIZE, valueList.size());
        assertTrue("Expected value: " + testStringLiteral, valueList.contains(testStringLiteral));
        assertTrue("Expected value: " + testIntegerLiteral, valueList.contains(String.valueOf(testIntegerLiteral)));
        assertTrue("Expected value: " + testDoubleLiteral, valueList.contains(String.valueOf(testDoubleLiteral)));
        assertTrue("Expected value: " + secondUri, valueList.contains(String.valueOf(secondUri)));
    }

    @Test
    public void testGet() throws TemplateModelException, ParseException {

        // test for the label
        SimpleSequence collection = (SimpleSequence) resourceHashModel.get(RDFS.label.getURI());
        assertEquals("There should only be one label", 1, collection.size());
        assertTrue("Expected a SimpleScalar", collection.get(0) instanceof SimpleScalar);
        assertEquals("Unexpected value", testStringLiteral, collection.get(0).toString());

        // test for the int
        collection = (SimpleSequence) resourceHashModel.get(DC.identifier.getURI());
        assertEquals("There should only be one int", 1, collection.size());
        assertTrue("Expected a SimpleNumber", collection.get(0) instanceof SimpleNumber);
        assertEquals("Unexpected value", testIntegerLiteral, ((SimpleNumber) collection.get(0)).getAsNumber());

        // test the double
        collection = (SimpleSequence) resourceHashModel.get(VCARD.CATEGORIES.getURI());
        assertEquals("There should only be one double", 1, collection.size());
        assertTrue("Expected a SimpleNumber", collection.get(0) instanceof SimpleNumber);
        assertEquals("Unexpected value", testDoubleLiteral, ((SimpleNumber) collection.get(0)).getAsNumber());

        // test for the date
        collection = (SimpleSequence) resourceHashModel.get(DC.date.getURI());
        assertEquals("There should only be one date", 1, collection.size());
        assertTrue("Expected a SimpleDate", collection.get(0) instanceof SimpleDate);
        DateFormat df = new SimpleDateFormat(DATE_FORMAT);
        Date date = df.parse(testDate);
        assertEquals("Unexpected value", date.getTime(), ((SimpleDate) collection.get(0)).getAsDate().getTime());

        // test for URI
        collection = (SimpleSequence) resourceHashModel.get(DC.publisher.getURI());
        assertEquals("There should only be one uri", 1, collection.size());
        assertTrue("Expected a ResourceHashModel", collection.get(0) instanceof ResourceHashModel);
        assertEquals("Unexpected value", secondUri, ((ResourceHashModel) collection.get(0)).getAsString());
    }

    @Test
    public void testEmpty() throws TemplateModelException {

        // check the number of keys
        assertFalse("The resource is not empty", resourceHashModel.isEmpty());
    }

    @Test
    public void testAsString() throws TemplateModelException {

        // check that we get the testUri
        assertEquals("Unexpected string value", testUri, resourceHashModel.getAsString());
    }


    private Resource createTestResource() {

        Model model = ModelFactory.createDefaultModel();
        Resource resource = model.createResource(testUri);
        resource.addLiteral(RDFS.label, testStringLiteral);
        resource.addLiteral(DC.identifier, testIntegerLiteral);
        resource.addLiteral(VCARD.CATEGORIES, testDoubleLiteral);
        resource.addProperty(DC.date, testDate, XSDDatatype.XSDdateTime);

        Resource secondResource = model.createResource(secondUri);
        secondResource.addLiteral(DC.title, secondLabel);

        model.add(model.createStatement(resource, DC.publisher, secondResource));

        return resource;
    }

    private String testUri = "http://example.org/1/";
    private String testStringLiteral = "This is a label";
    private int testIntegerLiteral = 999999;
    private double testDoubleLiteral = 9.8989;
    private String testDate = "2010-09-23T15:20:47";

    private String secondUri = "http://example.org/2/";
    private String secondLabel = "This is a another label";

    private ResourceHashModel resourceHashModel;

    private final int KEY_SIZE = 5;

    private final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

}
