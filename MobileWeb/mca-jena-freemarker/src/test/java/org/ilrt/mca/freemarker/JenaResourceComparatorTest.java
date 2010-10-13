package org.ilrt.mca.freemarker;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import org.ilrt.mca.vocab.MCA_REGISTRY;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class JenaResourceComparatorTest {

    @Test
    public void test() throws TemplateModelException {


        Model model = ModelFactory.createDefaultModel();
        Resource resource = model.createResource("http://example.org");

        Resource lvl1 = model.createResource("http://example.org/1");
        model.add(model.createStatement(lvl1, DC.title, "First Item"));
        model.add(model.createLiteralStatement(lvl1, MCA_REGISTRY.order, 1));

        Resource lvl2 = model.createResource("http://example.org/2");
        model.add(model.createStatement(lvl2, DC.title, "Second Item"));
        model.add(model.createLiteralStatement(lvl2, MCA_REGISTRY.order, 2));

        Resource lvl3 = model.createResource("http://example.org/3");
        model.add(model.createStatement(lvl3, DC.title, "Third Item"));
        model.add(model.createLiteralStatement(lvl3, MCA_REGISTRY.order, 3));

        model.add(model.createStatement(resource, MCA_REGISTRY.hasItem, lvl1));
        model.add(model.createStatement(resource, MCA_REGISTRY.hasItem, lvl2));
        model.add(model.createStatement(resource, MCA_REGISTRY.hasItem, lvl3));

        model.write(System.out);

        ResourceHashModel resourceHashModel = new ResourceHashModel(resource);

        SimpleSequence sequence = (SimpleSequence) resourceHashModel.get("mca:hasItem");


        assertEquals("There should be three items in the sequence", 3, sequence.size());

        List list = sequence.toList();

        for (Object o : list) {
            System.out.println(o);
        }
   

    }


}
