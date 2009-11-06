package org.ilrt.mca.quartz;

import org.apache.log4j.Logger;
import org.ilrt.mca.harvester.Harvester;
import org.ilrt.mca.harvester.xml.XhtmlSourceHarvesterImplImpl;
import org.ilrt.mca.rdf.Repository;
import org.ilrt.mca.rdf.SdbRepositoryImpl;
import org.ilrt.mca.rdf.StoreWrapperManager;
import org.ilrt.mca.rdf.StoreWrapperManagerImpl;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.IOException;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class HarvestXhtmlJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("The HarvestXhtmlJob has started.");
        try {

            StoreWrapperManager manager = new StoreWrapperManagerImpl("/sdb.ttl");
            Repository repository = new SdbRepositoryImpl(manager);

            Harvester harvester = new XhtmlSourceHarvesterImplImpl(repository);
            harvester.harvest();

        } catch (IOException ex) {
            log.error("Failed to execute job: " + ex.getMessage());
            throw new JobExecutionException(ex);
        }

        log.info("The HarvestXhtmlJob has finished.");
    }

    Logger log = Logger.getLogger(HarvestXhtmlJob.class);
}