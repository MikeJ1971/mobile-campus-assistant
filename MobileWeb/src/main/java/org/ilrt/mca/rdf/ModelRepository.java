package org.ilrt.mca.rdf;

import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.util.FileManager;

public class ModelRepository extends AbstractRepository {

    public ModelRepository() {
        try {

            model = FileManager.get().loadModel("registry.ttl");
            findGroupsSparql = loadSparql("/findItems.rql");
            findItemSparql = loadSparql("/findItem.rql");
            findHomepageSparql = loadSparql("/homepage.rql");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public Model findHomePage() {
        return executeConstructQuery(findHomepageSparql, model);
    }

    public Model findItem(String id) {

        QuerySolutionMap qs = new QuerySolutionMap();
        qs.add("id", ResourceFactory.createResource(id));
        return executeConstructQuery(findGroupsSparql, model, qs);
    }

    public Model findByType(String id) {

        QuerySolutionMap qs = new QuerySolutionMap();
        qs.add("type", ResourceFactory.createResource(id));
        return executeConstructQuery(findGroupsSparql, model, qs);
    }

    private Model model = null;
    private String findGroupsSparql = null;
    private String findItemSparql = null;
    private String findHomepageSparql = null;
}
