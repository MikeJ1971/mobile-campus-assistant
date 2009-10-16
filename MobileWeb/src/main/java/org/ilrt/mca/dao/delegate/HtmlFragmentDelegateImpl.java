package org.ilrt.mca.dao.delegate;

import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;
import org.apache.log4j.Logger;
import org.ilrt.mca.dao.AbstractDao;
import org.ilrt.mca.domain.Item;
import org.ilrt.mca.domain.html.HtmlFragmentImpl;
import org.ilrt.mca.rdf.Repository;
import org.ilrt.mca.vocab.MCA_REGISTRY;

import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;

public class HtmlFragmentDelegateImpl extends AbstractDao implements Delegate {

    public HtmlFragmentDelegateImpl(final Repository repository) {
        this.repository = repository;
        try {
            sparql = loadSparql("/sparql/findHtmlFragment.rql");
        } catch (IOException ex) {
            Logger log = Logger.getLogger(HtmlFragmentDelegateImpl.class);
            log.error("Unable to load SPARQL query: " + ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Item createItem(Resource resource, MultivaluedMap<String, String> parameters) {

        HtmlFragmentImpl htmlFragment = new HtmlFragmentImpl();
        getBasicDetails(resource, htmlFragment);

        if (resource.hasProperty(RDFS.seeAlso)) {

            Resource seeAlso = resource.getProperty(RDFS.seeAlso).getResource();

            if (seeAlso.hasProperty(MCA_REGISTRY.hasHtmlFragment)) {

                htmlFragment.setHtmlFragment(seeAlso.getProperty(MCA_REGISTRY.hasHtmlFragment)
                        .getLiteral().getLexicalForm());
            }
        }

        return htmlFragment;
    }

    @Override
    public Model createModel(Resource resource, MultivaluedMap<String, String> parameters) {

        Resource graph = resource.getProperty(RDFS.seeAlso).getResource();

        QuerySolutionMap bindings = new QuerySolutionMap();
        bindings.add("id", graph);
        bindings.add("graph", graph);

        Model model = repository.find(bindings, sparql);

        return ModelFactory.createUnion(resource.getModel(), model);
    }

    private String sparql = null;
    private final Repository repository;
}
