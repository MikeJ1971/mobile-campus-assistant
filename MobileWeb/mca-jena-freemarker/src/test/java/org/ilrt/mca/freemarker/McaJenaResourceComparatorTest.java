/*
 * Copyright (c) 2010, University of Bristol
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1) Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2) Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3) Neither the name of the University of Bristol nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.ilrt.mca.freemarker;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.RSS;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateModelException;
import org.ilrt.mca.vocab.EVENT;
import org.ilrt.mca.vocab.MCA_REGISTRY;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/*
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class McaJenaResourceComparatorTest {

    @Test
    public void testMcaOrderProperty() throws TemplateModelException {

        // ---------- Data Model for the test

        // create the model and resource

        Model model = ModelFactory.createDefaultModel();
        Resource resource = model.createResource("http://example.org");

        // create the additional resources with different labels and orders

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

        // add the resources to the model

        model.add(model.createStatement(resource, MCA_REGISTRY.hasItem, lvl1));
        model.add(model.createStatement(resource, MCA_REGISTRY.hasItem, lvl2));
        model.add(model.createStatement(resource, MCA_REGISTRY.hasItem, lvl3));
        model.add(model.createStatement(resource, MCA_REGISTRY.hasItem, lvl4));
        model.add(model.createStatement(resource, MCA_REGISTRY.hasItem, lvl5));

        // wrap the resource in the Freemarker model and correct object wrapper

        ResourceHashModel resourceHashModel = new ResourceHashModel(resource);
        SimpleSequence sequence = (SimpleSequence) resourceHashModel.get("mca:hasItem");
        sequence.setObjectWrapper(new JenaObjectWrapper());

        // ---------- Run the tests

        assertEquals("There should be three items in the sequence", 5, sequence.size());

        // get the first element (this has an order of 1)
        ResourceHashModel elementOne = (ResourceHashModel) sequence.get(0);
        assertEquals("Unexpected URI", uriOne, elementOne.getAsString());
        Resource r1 = elementOne.getResource();
        assertEquals("Unexpected label", labelOne, r1.getProperty(DC.title)
                .getLiteral().getLexicalForm());

        // get the second element (this has an order of 2)
        ResourceHashModel elementTwo = (ResourceHashModel) sequence.get(1);
        assertEquals("Unexpected URI", uriTwo, elementTwo.getAsString());
        Resource r2 = elementTwo.getResource();
        assertEquals("Unexpected label", labelTwo, r2.getProperty(DC.title)
                .getLiteral().getLexicalForm());

        // get the third element (this has an order of 3, lexically first for this order)
        ResourceHashModel elementThree = (ResourceHashModel) sequence.get(2);
        assertEquals("Unexpected URI", uriThree, elementThree.getAsString());
        Resource r3 = elementThree.getResource();
        assertEquals("Unexpected label", labelThree, r3.getProperty(DC.title)
                .getLiteral().getLexicalForm());

        // get the fourth element (this has an order of 3, lexically second for this order)
        ResourceHashModel elementFour = (ResourceHashModel) sequence.get(3);
        assertEquals("Unexpected URI", uriFour, elementFour.getAsString());
        Resource r4 = elementFour.getResource();
        assertEquals("Unexpected label", labelFour, r4.getProperty(DC.title)
                .getLiteral().getLexicalForm());

        // get the fifth element (this has an order of 3, lexically third for this order)
        ResourceHashModel elementFive = (ResourceHashModel) sequence.get(4);
        assertEquals("Unexpected URI", uriFive, elementFive.getAsString());
        Resource r5 = elementFive.getResource();
        assertEquals("Unexpected label", labelFive, r5.getProperty(RDFS.label)
                .getLiteral().getLexicalForm());
    }


    @Test
    public void testDcDateProperty() throws TemplateModelException {

        // ---------- Data Model for the test

        // create the model and resource

        Model model = ModelFactory.createDefaultModel();
        Resource resource = model.createResource("http://example.org");

        // create the additional resources with different labels and orders

        Resource lvl1 = model.createResource(uriOne);
        model.add(model.createStatement(lvl1, DC.title, labelOne));
        model.add(model.createLiteralStatement(lvl1, DC.date, dateOne));

        Resource lvl2 = model.createResource(uriTwo);
        model.add(model.createStatement(lvl2, DC.title, labelTwo));
        model.add(model.createLiteralStatement(lvl2, DC.date, dateTwo));

        Resource lvl3 = model.createResource(uriThree);
        model.add(model.createStatement(lvl3, DC.title, labelThree));
        model.add(model.createLiteralStatement(lvl3, DC.date, dateThree));

        Resource lvl4 = model.createResource(uriFour);
        model.add(model.createStatement(lvl4, DC.title, labelFour));
        model.add(model.createLiteralStatement(lvl4, DC.date, dateFour));

        Resource lvl5 = model.createResource(uriFive);
        model.add(model.createStatement(lvl5, RDFS.label, labelFive));
        model.add(model.createLiteralStatement(lvl5, DC.date, dateFive));

        // add the resources to the model

        model.add(model.createStatement(resource, MCA_REGISTRY.hasItem, lvl1));
        model.add(model.createStatement(resource, MCA_REGISTRY.hasItem, lvl2));
        model.add(model.createStatement(resource, MCA_REGISTRY.hasItem, lvl3));
        model.add(model.createStatement(resource, MCA_REGISTRY.hasItem, lvl4));
        model.add(model.createStatement(resource, MCA_REGISTRY.hasItem, lvl5));

        model.write(System.out);

        // wrap the resource in the Freemarker model and correct object wrapper

        ResourceHashModel resourceHashModel = new ResourceHashModel(resource);
        SimpleSequence sequence = (SimpleSequence) resourceHashModel.get("mca:hasItem");
        sequence.setObjectWrapper(new JenaObjectWrapper());

        // ---------- Run the tests

        assertEquals("There should be three items in the sequence", 5, sequence.size());

        // get the first element (first by date)
        ResourceHashModel elementOne = (ResourceHashModel) sequence.get(0);
        assertEquals("Unexpected URI", uriOne, elementOne.getAsString());
        Resource r1 = elementOne.getResource();
        assertEquals("Unexpected label", labelOne, r1.getProperty(DC.title)
                .getLiteral().getLexicalForm());
        assertEquals("Unexpected date", dateOne, r1.getProperty(DC.date)
                .getLiteral().getLexicalForm());

        // get the second element (second by date)
        ResourceHashModel elementTwo = (ResourceHashModel) sequence.get(1);
        assertEquals("Unexpected URI", uriTwo, elementTwo.getAsString());
        Resource r2 = elementTwo.getResource();
        assertEquals("Unexpected label", labelTwo, r2.getProperty(DC.title)
                .getLiteral().getLexicalForm());
        assertEquals("Unexpected date", dateTwo, r2.getProperty(DC.date)
                .getLiteral().getLexicalForm());

        // get the third element (third by date, lexically first)
        ResourceHashModel elementThree = (ResourceHashModel) sequence.get(2);
        assertEquals("Unexpected URI", uriThree, elementThree.getAsString());
        Resource r3 = elementThree.getResource();
        assertEquals("Unexpected label", labelThree, r3.getProperty(DC.title)
                .getLiteral().getLexicalForm());
        assertEquals("Unexpected date", dateThree, r3.getProperty(DC.date)
                .getLiteral().getLexicalForm());

        // get the fourth element (third by date, lexically second)
        ResourceHashModel elementFour = (ResourceHashModel) sequence.get(3);
        assertEquals("Unexpected URI", uriFour, elementFour.getAsString());
        Resource r4 = elementFour.getResource();
        assertEquals("Unexpected label", labelFour, r4.getProperty(DC.title)
                .getLiteral().getLexicalForm());
        assertEquals("Unexpected date", dateFour, r4.getProperty(DC.date)
                .getLiteral().getLexicalForm());

        // get the fifth element (third by date, lexically third)
        ResourceHashModel elementFive = (ResourceHashModel) sequence.get(4);
        assertEquals("Unexpected URI", uriFive, elementFive.getAsString());
        Resource r5 = elementFive.getResource();
        assertEquals("Unexpected label", labelFive, r5.getProperty(RDFS.label)
                .getLiteral().getLexicalForm());
        assertEquals("Unexpected date", dateFive, r5.getProperty(DC.date)
                .getLiteral().getLexicalForm());
    }

    @Test
    public void testIcalDateProperty() throws TemplateModelException {

        // ---------- Data Model for the test

        // create the model and resource

        Model model = ModelFactory.createDefaultModel();
        Resource resource = model.createResource("http://example.org");

        // create the additional resources with different labels and orders

        Resource lvl1 = model.createResource(uriOne);
        model.add(model.createStatement(lvl1, DC.title, labelOne));
        model.add(model.createLiteralStatement(lvl1, EVENT.startDate, dateOne));

        Resource lvl2 = model.createResource(uriTwo);
        model.add(model.createStatement(lvl2, DC.title, labelTwo));
        model.add(model.createLiteralStatement(lvl2, EVENT.startDate, dateTwo));

        Resource lvl3 = model.createResource(uriThree);
        model.add(model.createStatement(lvl3, DC.title, labelThree));
        model.add(model.createLiteralStatement(lvl3, EVENT.startDate, dateThree));

        Resource lvl4 = model.createResource(uriFour);
        model.add(model.createStatement(lvl4, DC.title, labelFour));
        model.add(model.createLiteralStatement(lvl4, EVENT.startDate, dateFour));

        Resource lvl5 = model.createResource(uriFive);
        model.add(model.createStatement(lvl5, RDFS.label, labelFive));
        model.add(model.createLiteralStatement(lvl5, EVENT.startDate, dateFive));

        // add the resources to the model

        model.add(model.createStatement(resource, MCA_REGISTRY.hasItem, lvl1));
        model.add(model.createStatement(resource, MCA_REGISTRY.hasItem, lvl2));
        model.add(model.createStatement(resource, MCA_REGISTRY.hasItem, lvl3));
        model.add(model.createStatement(resource, MCA_REGISTRY.hasItem, lvl4));
        model.add(model.createStatement(resource, MCA_REGISTRY.hasItem, lvl5));

        // wrap the resource in the Freemarker model and correct object wrapper

        ResourceHashModel resourceHashModel = new ResourceHashModel(resource);
        SimpleSequence sequence = (SimpleSequence) resourceHashModel.get("mca:hasItem");
        sequence.setObjectWrapper(new JenaObjectWrapper());

        // ---------- Run the tests

        assertEquals("There should be three items in the sequence", 5, sequence.size());

        // get the first element (first by date)
        ResourceHashModel elementOne = (ResourceHashModel) sequence.get(0);
        assertEquals("Unexpected URI", uriOne, elementOne.getAsString());
        Resource r1 = elementOne.getResource();
        assertEquals("Unexpected label", labelOne, r1.getProperty(DC.title)
                .getLiteral().getLexicalForm());
        assertEquals("Unexpected date", dateOne, r1.getProperty(EVENT.startDate)
                .getLiteral().getLexicalForm());

        // get the second element (second by date)
        ResourceHashModel elementTwo = (ResourceHashModel) sequence.get(1);
        assertEquals("Unexpected URI", uriTwo, elementTwo.getAsString());
        Resource r2 = elementTwo.getResource();
        assertEquals("Unexpected label", labelTwo, r2.getProperty(DC.title)
                .getLiteral().getLexicalForm());
        assertEquals("Unexpected date", dateTwo, r2.getProperty(EVENT.startDate)
                .getLiteral().getLexicalForm());

        // get the third element (third by date, lexically first)
        ResourceHashModel elementThree = (ResourceHashModel) sequence.get(2);
        assertEquals("Unexpected URI", uriThree, elementThree.getAsString());
        Resource r3 = elementThree.getResource();
        assertEquals("Unexpected label", labelThree, r3.getProperty(DC.title)
                .getLiteral().getLexicalForm());
        assertEquals("Unexpected date", dateThree, r3.getProperty(EVENT.startDate)
                .getLiteral().getLexicalForm());

        // get the fourth element (third by date, lexically second)
        ResourceHashModel elementFour = (ResourceHashModel) sequence.get(3);
        assertEquals("Unexpected URI", uriFour, elementFour.getAsString());
        Resource r4 = elementFour.getResource();
        assertEquals("Unexpected label", labelFour, r4.getProperty(DC.title)
                .getLiteral().getLexicalForm());
        assertEquals("Unexpected date", dateFour, r4.getProperty(EVENT.startDate)
                .getLiteral().getLexicalForm());

        // get the fifth element (third by date, lexically third)
        ResourceHashModel elementFive = (ResourceHashModel) sequence.get(4);
        assertEquals("Unexpected URI", uriFive, elementFive.getAsString());
        Resource r5 = elementFive.getResource();
        assertEquals("Unexpected label", labelFive, r5.getProperty(RDFS.label)
                .getLiteral().getLexicalForm());
        assertEquals("Unexpected date", dateFive, r5.getProperty(EVENT.startDate)
                .getLiteral().getLexicalForm());
    }


    @Test
    public void testLabelProperty() throws TemplateModelException {

        // ---------- Data Model for the test

        // create the model and resource

        Model model = ModelFactory.createDefaultModel();
        Resource resource = model.createResource("http://example.org");

        // create the additional resources with different labels and orders

        Resource lvl1 = model.createResource(uriOne);
        model.add(model.createStatement(lvl1, DC.title, labelOne));

        Resource lvl2 = model.createResource(uriTwo);
        model.add(model.createStatement(lvl2, RDFS.label, labelTwo));

        Resource lvl3 = model.createResource(uriThree);
        model.add(model.createStatement(lvl3, RSS.title, labelThree));

        Resource lvl4 = model.createResource(uriFour);
        model.add(model.createStatement(lvl4, EVENT.subject, labelFour));

        Resource lvl5 = model.createResource(uriFive);
        model.add(model.createStatement(lvl5, DC.title, labelFive));

        // add the resources to the model

        model.add(model.createStatement(resource, MCA_REGISTRY.hasItem, lvl1));
        model.add(model.createStatement(resource, MCA_REGISTRY.hasItem, lvl2));
        model.add(model.createStatement(resource, MCA_REGISTRY.hasItem, lvl3));
        model.add(model.createStatement(resource, MCA_REGISTRY.hasItem, lvl4));
        model.add(model.createStatement(resource, MCA_REGISTRY.hasItem, lvl5));

        // wrap the resource in the Freemarker model and correct object wrapper

        ResourceHashModel resourceHashModel = new ResourceHashModel(resource);
        SimpleSequence sequence = (SimpleSequence) resourceHashModel.get("mca:hasItem");
        sequence.setObjectWrapper(new JenaObjectWrapper());

        // ---------- Run the tests

        assertEquals("There should be three items in the sequence", 5, sequence.size());

        // get the first element
        ResourceHashModel elementOne = (ResourceHashModel) sequence.get(0);
        assertEquals("Unexpected URI", uriOne, elementOne.getAsString());
        Resource r1 = elementOne.getResource();
        assertEquals("Unexpected label", labelOne, r1.getProperty(DC.title)
                .getLiteral().getLexicalForm());

        // get the second element
        ResourceHashModel elementTwo = (ResourceHashModel) sequence.get(1);
        assertEquals("Unexpected URI", uriTwo, elementTwo.getAsString());
        Resource r2 = elementTwo.getResource();
        assertEquals("Unexpected label", labelTwo, r2.getProperty(RDFS.label)
                .getLiteral().getLexicalForm());

        // get the third element
        ResourceHashModel elementThree = (ResourceHashModel) sequence.get(2);
        assertEquals("Unexpected URI", uriThree, elementThree.getAsString());
        Resource r3 = elementThree.getResource();
        assertEquals("Unexpected label", labelThree, r3.getProperty(RSS.title)
                .getLiteral().getLexicalForm());

        // get the fourth element
        ResourceHashModel elementFour = (ResourceHashModel) sequence.get(3);
        assertEquals("Unexpected URI", uriFour, elementFour.getAsString());
        Resource r4 = elementFour.getResource();
        assertEquals("Unexpected label", labelFour, r4.getProperty(EVENT.subject)
                .getLiteral().getLexicalForm());

        // get the fifth element
        ResourceHashModel elementFive = (ResourceHashModel) sequence.get(4);
        assertEquals("Unexpected URI", uriFive, elementFive.getAsString());
        Resource r5 = elementFive.getResource();
        assertEquals("Unexpected label", labelFive, r5.getProperty(DC.title)
                .getLiteral().getLexicalForm());
    }


    private final String uriOne = "http://example.org/1";
    private final String labelOne = "Item 1";
    private final String dateOne = "2010-10-14T06:00:00Z";

    private final String uriTwo = "http://example.org/2";
    private final String labelTwo = "Item 2";
    private final String dateTwo = "2010-10-15T06:00:00Z";

    private final String uriThree = "http://example.org/3";
    private final String labelThree = "Item 3";
    private final String dateThree = "2010-10-16T06:00:00Z";

    private final String uriFour = "http://example.org/4";
    private final String labelFour = "Item 4";
    private final String dateFour = "2010-10-17T06:00:00Z";

    private final String uriFive = "http://example.org/5";
    private final String labelFive = "Item 5";
    private final String dateFive = "2010-10-17T06:00:00Z";
}
