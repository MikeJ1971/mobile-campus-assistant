package org.ilrt.mca.registry;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import org.ilrt.mca.domain.Group;
import org.ilrt.mca.vocab.MCA_REGISTRY;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class RegistryManager {

    public RegistryManager() {
        model = loadModel();
    }

    public static void main(String[] args) {
        RegistryManager registryManager = new RegistryManager();
        registryManager.doStuff();
    }

    public void doStuff() {
        try {

            RegistryManager registryManager = new RegistryManager();
            Model model = registryManager.loadModel();
            model.write(System.out);
            findGroupsSparql = loadSparql(findGroupsSparqlPath);

            System.out.println(findGroupsSparql);


            Model results = executeConstructQuery(findGroupsSparql);

            System.out.println("*************************");

            results.write(System.out);


            findGroups();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public List<Group> findGroups() {

        List<Group> results = new ArrayList<Group>();

        Model resultsModel = executeConstructQuery(findGroupsSparql);

        ResIterator iter = resultsModel.listResourcesWithProperty(RDF.type,
                MCA_REGISTRY.WorkBench);


        while (iter.hasNext()) {

            Resource resource = iter.nextResource();

            String label = null;
            int order = -1;

            if (resource.hasProperty(RDFS.label)) {
                label = resource.getProperty(RDFS.label).getLiteral().getLexicalForm();
            }

            if (resource.hasProperty(MCA_REGISTRY.order)) {
                order = resource.getProperty(MCA_REGISTRY.order).getInt();
            }


            Group group = new Group(label, order);
            results.add(group);

            System.out.println(results.size());
        }
        return null;
    }

    private Model executeConstructQuery(String query) {

        QueryExecution qe = QueryExecutionFactory.create(query, model);
        Model results = qe.execConstruct();
        qe.close();
        return results;
    }


    private Model loadModel() {

        return FileManager.get().loadModel("registry.ttl");
    }

    private String loadSparql(String path) throws IOException {

        StringBuffer buffer = new StringBuffer();
        InputStream is = getClass().getResourceAsStream(path);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
            buffer.append("\n");
        }

        return buffer.toString();
    }


    private String findGroupsSparql;

    private String findGroupsSparqlPath = "/findGroups.rql";

    private Model model;
}
