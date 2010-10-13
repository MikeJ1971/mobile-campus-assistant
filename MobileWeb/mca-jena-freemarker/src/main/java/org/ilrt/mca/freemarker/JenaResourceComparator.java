package org.ilrt.mca.freemarker;

import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import org.ilrt.mca.vocab.MCA_REGISTRY;

import java.util.Comparator;

public class JenaResourceComparator implements Comparator {

    @Override
    public int compare(Object o1, Object o2) {

        if (o1 instanceof ResourceTemplate && o2 instanceof ResourceTemplate) {

            Resource r1 = ((ResourceTemplate) o1).getResource();
            Resource r2 = ((ResourceTemplate) o2).getResource();

            // do we have an "mca:order"

            if (r1.hasProperty(MCA_REGISTRY.order) || r2.hasProperty(MCA_REGISTRY.order)) {

                // we can only compare literals
                RDFNode node1 = r1.getProperty(MCA_REGISTRY.order).getObject();
                RDFNode node2 = r2.getProperty(MCA_REGISTRY.order).getObject();

                if (node1.isLiteral() && node2.isLiteral()) {
                    return node1.asLiteral().getLexicalForm()
                            .compareTo(node2.asLiteral().getLexicalForm());
                }

            }

        }

        return 0;
    }
}
