package org.ilrt.mca.rdf;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.Model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public abstract class AbstractRepository {

    Model executeConstructQuery(final String query, final Model model,
                                final QuerySolution bindings) {

        QueryExecution qe = QueryExecutionFactory.create(query, model, bindings);
        Model results = qe.execConstruct();
        //results.write(System.out);
        qe.close();
        return results;
    }

    Model executeConstructQuery(final String query, final Model model) {

        QueryExecution qe = QueryExecutionFactory.create(query, model);
        Model results = qe.execConstruct();
        //results.write(System.out);
        qe.close();
        return results;
    }

    String loadSparql(String path) throws IOException {

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
}
