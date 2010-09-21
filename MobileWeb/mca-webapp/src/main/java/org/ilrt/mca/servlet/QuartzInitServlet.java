package org.ilrt.mca.servlet;

import org.apache.log4j.Logger;
import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @see "http://kickjava.com/src/org/quartz/ee/servlet/QuartzInitializerServlet.java.htm"
 */
public class QuartzInitServlet extends HttpServlet {

    @Override
    public void init(ServletConfig config) throws ServletException {

        log.info("QuartzInitServlet started.");

        super.init(config);

        StdSchedulerFactory factory;
        try {

            String configFile = config.getInitParameter("config-file");
            String shutdownPref = config.getInitParameter("shutdown-on-unload");

            if (shutdownPref != null) {
                performShutdown = Boolean.valueOf(shutdownPref);
            }

            if (configFile != null) {
                factory = new StdSchedulerFactory(configFile);
            } else {
                factory = new StdSchedulerFactory();
            }

            scheduler = factory.getScheduler();

            String startOnLoad = config.getInitParameter("start-scheduler-on-load");

            if (startOnLoad == null || (Boolean.valueOf(startOnLoad))) {
                scheduler.start();
                log("Scheduler has been started...");
            } else {
                log("Scheduler has not been started. Use scheduler.start()");
            }

            String factoryKey = config.getInitParameter("servlet-context-factory-key");
            if (factoryKey == null) {
                factoryKey = QUARTZ_FACTORY_KEY;
            }

            log("Storing the Quartz Scheduler Factory in the servlet context at key: "
                    + factoryKey);
            config.getServletContext().setAttribute(factoryKey, factory);

        } catch (Exception e) {
            log("Quartz Scheduler failed to initialize: " + e.toString());
            throw new ServletException(e);
        }

    }

    @Override
    public void destroy() {

        if (!performShutdown) {
            return;
        }

        try {
            if (scheduler != null) {
                scheduler.shutdown();
            }
        } catch (Exception e) {
            log("Quartz Scheduler failed to shutdown cleanly: " + e.toString());
            e.printStackTrace();
        }

        log.info("QuartzInitServlet shutdown.");
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

    public static final String QUARTZ_FACTORY_KEY = "org.quartz.impl.StdSchedulerFactory.KEY";
    private boolean performShutdown = true;
    private Scheduler scheduler = null;

}
