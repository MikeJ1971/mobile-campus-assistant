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

public class JenaResourceComparator implements Comparator<TemplateModel> {

    @Override
    public int compare(TemplateModel o1, TemplateModel o2) {

        // make sure that we have two objects we can compare
        if (o1 instanceof ResourceTemplate && o2 instanceof ResourceTemplate) {

            // get the underlying resource
            Resource r1 = ((ResourceTemplate) o1).getResource();
            Resource r2 = ((ResourceTemplate) o2).getResource();

            // do we have an "mca:order" - this should take priority
            if (r1.hasProperty(MCA_REGISTRY.order) && r2.hasProperty(MCA_REGISTRY.order)) {

                return compareResources(r1, r2, MCA_REGISTRY.order);

            } else if (r1.hasProperty(DC.date) && r2.hasProperty(DC.date)) {

                return compareResources(r1, r2, DC.date);

            } else if (r1.hasProperty(EVENT.startDate) && r2.hasProperty(EVENT.startDate)) {

                return compareResources(r1, r2, EVENT.startDate);

            } else { // other wise try and order on a label
                return compareOnLabel(r1, r2);
            }
        }

        return 0; // give up!
    }

    private int compareLexicalForm(Literal a, Literal b) {
        return a.getLexicalForm().compareTo(b.getLexicalForm());
    }

    private int compareOnLabel(Resource a, Resource b) {

        Literal literal1 = getLabel(a);
        Literal literal2 = getLabel(b);

        if (literal1 != null && literal2 != null) {
            return literal1.getLexicalForm().compareTo(literal2.getLexicalForm());
        } else {
            return 0;
        }
    }

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

    private int compareResources(Resource r1, Resource r2, Property p) {

        // they should be Literal values, but check anyway
        RDFNode node1 = r1.getProperty(p).getObject();
        RDFNode node2 = r2.getProperty(p).getObject();

        if (node1.isLiteral() && node2.isLiteral()) {
            int val = compareLexicalForm(node1.asLiteral(), node2.asLiteral());
            return val == 0 ? compareOnLabel(r1, r2) : val;
        }

        return 0;
    }

}
