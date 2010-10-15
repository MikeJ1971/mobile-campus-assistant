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
 *
 */
package org.ilrt.mca.freemarker;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.RSS;
import freemarker.template.TemplateModel;
import org.ilrt.mca.vocab.EVENT;
import org.ilrt.mca.vocab.MCA_REGISTRY;

import java.util.Comparator;

/**
 * <p>A comparator class that helps to order Jena Resource objects. We query the database
 * with CONSTRUCT and DESCRIBE queries and the resulting RDF is encapsulated in a
 * Jena Resource and thus Model object. This data model has no concept of order, i.e.
 * displaying lists in alphabetical order, events in descending order of date etc.
 * A Resource is encapsulated in a ResourceHashModel which uses this comparator
 * to order any other resources that hang of it. It orders by priority: mca:order, dc:date,
 * ical:dtstart; then a label: rdfs:label, dc:title, rss:title, ical:summary. If it can't
 * make a comparison it just returns 0 (equal) because it can't say they are not equal.</p>
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class McaJenaResourceComparator implements Comparator<TemplateModel> {

    public McaJenaResourceComparator() {
    }

    // ---------- Public methods

    /**
     * @param o1 the first template model for comparison.
     * @param o2 the second template model for comparison.
     * @return an integer representing the equality of the two templates.
     */
    @Override
    public int compare(TemplateModel o1, TemplateModel o2) {

        // make sure that we have two objects we can compare
        if (o1 instanceof ResourceTemplate && o2 instanceof ResourceTemplate) {

            // get the underlying resource
            Resource r1 = ((ResourceTemplate) o1).getResource();
            Resource r2 = ((ResourceTemplate) o2).getResource();

            // do we have an "mca:order" - this should take priority
            if (hasProperty(r1, r2, MCA_REGISTRY.order)) {

                return compareResources(r1, r2, MCA_REGISTRY.order);

            } else if (hasProperty(r1, r2, DC.date)) {

                return compareResources(r1, r2, DC.date);

            } else if (hasProperty(r1, r2, EVENT.startDate)) {

                return compareResources(r1, r2, EVENT.startDate);

            } else { // other wise try and order on a label
                return compareOnLabel(r1, r2);
            }
        }

        return 0; // give up!
    }

    // ---------- Private methods

    /**
     * Do both resources have a specific property?
     *
     * @param r1 the first resource we need to test.
     * @param r2 the second resource we need to test.
     * @param p  the property that we are interested
     * @return true/false that both resources have the property.
     */
    private boolean hasProperty(Resource r1, Resource r2, Property p) {
        return r1.hasProperty(p) && r2.hasProperty(p);
    }

    /**
     * Compare the lexical form (String) of two literal values.
     *
     * @param a the first Literal for comparison
     * @param b the second Literal for comparison
     * @return an int representing the equality of two literal values.
     */
    private int compareLexicalForm(Literal a, Literal b) {
        return a.getLexicalForm().compareTo(b.getLexicalForm());
    }

    /**
     * Compares the labels associated with a resource. Returns 0 if it cannot compare
     * the two values.
     *
     * @param a the first resource for comparison.
     * @param b the second resource for comparison.
     * @return int representing the equality of the two labels.
     */
    private int compareOnLabel(Resource a, Resource b) {

        Literal literal1 = getLabel(a);
        Literal literal2 = getLabel(b);

        if (literal1 != null && literal2 != null) {
            return literal1.getLexicalForm().compareTo(literal2.getLexicalForm());
        } else {
            return 0;
        }
    }

    /**
     * Search a resource for an appropriate label.
     *
     * @param r resource to search for a label.
     * @return Literal that represents a label. null if there is no label.
     */
    private Literal getLabel(Resource r) {

        if (r.hasProperty(RDFS.label)) {
            return r.getProperty(RDFS.label).getLiteral();
        } else if (r.hasProperty(DC.title)) {
            return r.getProperty(DC.title).getLiteral();
        } else if (r.hasProperty(RSS.title)) {
            return r.getProperty(RSS.title).getLiteral();
        } else if (r.hasProperty(EVENT.subject)) {
            return (r.getProperty(EVENT.subject)).getLiteral();
        }

        return null;
    }

    /**
     * Compares the literal value of a property between two resources. If one or either
     * of the values are not a literal it will bail with a 0. If the values are equal, it
     * will then try and order them on an appropriate label if one exits.
     *
     * @param r1 the first resource for comparison.
     * @param r2 the second resource for comparison.
     * @param p  the property that returns a literal value from the resource.
     * @return int that represents equality.
     */
    private int compareResources(Resource r1, Resource r2, Property p) {

        // they should be Literal values, but check anyway
        RDFNode node1 = r1.getProperty(p).getObject();
        RDFNode node2 = r2.getProperty(p).getObject();

        if (node1.isLiteral() && node2.isLiteral()) {
            int val = compareLexicalForm(node1.asLiteral(), node2.asLiteral());
            return val == 0 ? compareOnLabel(r1, r2) : val;
        }

        return 0; // bail
    }

}
