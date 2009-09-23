package org.ilrt.mca.servlet;

import org.ilrt.mca.rdf.Repository;
import org.ilrt.mca.rdf.SdbRepositoryImpl;
import org.ilrt.mca.rdf.StoreWrapperManager;
import org.ilrt.mca.rdf.StoreWrapperManagerImpl;

import javax.servlet.http.HttpServlet;
import javax.servlet.ServletConfig;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.util.FileManager;

public class RegistryInitServlet extends HttpServlet {

    @Override
    public void init(ServletConfig config) {

        // find the configuration files
        String configLocation = config.getInitParameter("configLocation");
        String registryLocation = config.getInitParameter("registryLocation");

        // create the repository
        StoreWrapperManager manager = new StoreWrapperManagerImpl(configLocation);
        Repository repository = new SdbRepositoryImpl(manager);

        // TODO - BLITZ THE DEFAULT MODEL BEFORE ADDING THE MODEL AGAIN

        // load the registry and add it to the database
        Model model = FileManager.get().loadModel(registryLocation);
        repository.add(model);
    }
}
