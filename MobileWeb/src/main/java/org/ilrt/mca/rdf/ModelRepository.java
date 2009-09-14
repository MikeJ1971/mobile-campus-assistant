package org.ilrt.mca.rdf;

import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.util.FileManager;

/**
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class ModelRepository extends AbstractRepository {

    public ModelRepository() {
        try {

            model = FileManager.get().loadModel("registry.ttl");
            findItemsSparql = loadSparql("/findItems.rql");
            kmlMapDetailsSparql = loadSparql("/findKmlMapDetails.rql");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public Model findItem(String id) {

        QuerySolutionMap qs = new QuerySolutionMap();
        qs.add("id", ResourceFactory.createResource(id));
        return executeConstructQuery(findItemsSparql, model, qs);
    }

    public Model findMapDetails(String id) {

        QuerySolutionMap qs = new QuerySolutionMap();
        qs.add("id", ResourceFactory.createResource(id));
        return executeConstructQuery(kmlMapDetailsSparql, model, qs);
    }

    private Model model = null;
    private String findItemsSparql = null;
    private String kmlMapDetailsSparql = null;
}
