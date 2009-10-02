package org.ilrt.mca.servlet;

import org.ilrt.mca.rdf.Repository;
import org.ilrt.mca.rdf.SdbRepositoryImpl;
import org.ilrt.mca.rdf.StoreWrapperManager;
import org.ilrt.mca.rdf.StoreWrapperManagerImpl;
import org.ilrt.mca.rdf.StoreWrapper;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.sdb.SDBFactory;

import java.io.IOException;

/**
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class RegistryInitServlet extends HttpServlet {

    @Override
    public void init(ServletConfig config) throws ServletException {

        log.info("RegistryInitServlet started.");

        super.init(config);

        // find the configuration files
        String configLocation = config.getInitParameter("configLocation");
        String registryLocation = config.getInitParameter("registryLocation");

        // create the repository
        StoreWrapperManager manager = new StoreWrapperManagerImpl(configLocation);
        Repository repository = new SdbRepositoryImpl(manager);

        // clear existing registry
        log.info("Clearing existing registry details");
        repository.deleteAllInGraph(null);

        // load the registry and add it to the database
        log.info("Loading registry details");
        Model model = FileManager.get().loadModel(registryLocation);
        repository.add(model);
        log.info("Added " + model.size() + " triples.");
    }

    @Override
    public void destroy() {
        log.info("RegistryInitServlet shutdown.");
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
    }

    private final Logger log = Logger.getLogger(RegistryInitServlet.class);
}
