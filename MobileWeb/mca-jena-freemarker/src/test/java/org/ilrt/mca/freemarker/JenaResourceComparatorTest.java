package org.ilrt.mca.freemarker;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDFS;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateModelException;
import org.ilrt.mca.vocab.MCA_REGISTRY;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JenaResourceComparatorTest {

    @Test
    public void test() throws TemplateModelException {

        Model model = ModelFactory.createDefaultModel();
        Resource resource = model.createResource("http://example.org");

        Resource lvl1 = model.createResource(uriOne);
        model.add(model.createStatement(lvl1, DC.title, labelOne));
        model.add(model.createLiteralStatement(lvl1, MCA_REGISTRY.order, 1));

        Resource lvl2 = model.createResource(uriTwo);
        model.add(model.createStatement(lvl2, DC.title, labelTwo));
        model.add(model.createLiteralStatement(lvl2, MCA_REGISTRY.order, 2));

        Resource lvl3 = model.createResource(uriThree);
        model.add(model.createStatement(lvl3, DC.title, labelThree));
        model.add(model.createLiteralStatement(lvl3, MCA_REGISTRY.order, 3));

        Resource lvl4 = model.createResource(uriFour);
        model.add(model.createStatement(lvl4, DC.title, labelFour));
        model.add(model.createLiteralStatement(lvl4, MCA_REGISTRY.order, 3));

        Resource lvl5 = model.createResource(uriFive);
        model.add(model.createStatement(lvl5, RDFS.label, labelFive));
        model.add(model.createLiteralStatement(lvl5, MCA_REGISTRY.order, 3));

        model.add(model.createStatement(resource, MCA_REGISTRY.hasItem, lvl1));
        model.add(model.createStatement(resource, MCA_REGISTRY.hasItem, lvl2));
        model.add(model.createStatement(resource, MCA_REGISTRY.hasItem, lvl3));
        model.add(model.createStatement(resource, MCA_REGISTRY.hasItem, lvl4));
        model.add(model.createStatement(resource, MCA_REGISTRY.hasItem, lvl5));

        model.write(System.out);

        ResourceHashModel resourceHashModel = new ResourceHashModel(resource);

        SimpleSequence sequence = (SimpleSequence) resourceHashModel.get("mca:hasItem");
        sequence.setObjectWrapper(new JenaObjectWrapper());

        assertEquals("There should be three items in the sequence", 5, sequence.size());

        // get the first element
        ResourceHashModel elementOne = (ResourceHashModel) sequence.get(0);
        assertEquals("Unexpected URI", uriOne, elementOne.getAsString());
        Resource r1 = elementOne.getResource();
        assertEquals("Unexpected label", labelOne, r1.getProperty(DC.title).getLiteral().getLexicalForm());

        // get the second element
        ResourceHashModel elementTwo = (ResourceHashModel) sequence.get(1);
        assertEquals("Unexpected URI", uriTwo, elementTwo.getAsString());
        Resource r2 = elementTwo.getResource();
        assertEquals("Unexpected label", labelTwo, r2.getProperty(DC.title).getLiteral().getLexicalForm());

        // get the third element
        ResourceHashModel elementThree = (ResourceHashModel) sequence.get(2);
        assertEquals("Unexpected URI", uriThree, elementThree.getAsString());
        Resource r3 = elementThree.getResource();
        assertEquals("Unexpected label", labelThree, r3.getProperty(DC.title).getLiteral().getLexicalForm());

        // get the fourth element
        ResourceHashModel elementFour = (ResourceHashModel) sequence.get(3);
        assertEquals("Unexpected URI", uriFour, elementFour.getAsString());
        Resource r4 = elementFour.getResource();
        assertEquals("Unexpected label", labelFour, r4.getProperty(DC.title).getLiteral().getLexicalForm());

        // get the fifth element
        ResourceHashModel elementFive = (ResourceHashModel) sequence.get(4);
        assertEquals("Unexpected URI", uriFive, elementFive.getAsString());
        Resource r5 = elementFive.getResource();
        assertEquals("Unexpected label", labelFive, r5.getProperty(RDFS.label).getLiteral().getLexicalForm());
    }

    private final String uriOne = "http://example.org/1";
    private final String labelOne = "Item 1";

    private final String uriTwo = "http://example.org/2";
    private final String labelTwo = "Item 2";

    private final String uriThree = "http://example.org/3";
    private final String labelThree = "Item 3";

    private final String uriFour = "http://example.org/4";
    private final String labelFour = "Item 4";

    private final String uriFive = "http://example.org/5";
    private final String labelFive = "Item 5";

}
