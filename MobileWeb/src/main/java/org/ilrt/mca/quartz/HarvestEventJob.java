package org.ilrt.mca.quartz;

import org.apache.log4j.Logger;
import org.ilrt.mca.harvester.Harvester;
import org.ilrt.mca.rdf.StoreWrapperManager;
import org.ilrt.mca.rdf.StoreWrapperManagerImpl;
import org.ilrt.mca.rdf.Repository;
import org.ilrt.mca.rdf.SdbRepositoryImpl;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.IOException;
import org.ilrt.mca.harvester.events.EventHarvesterImpl;

/**
 * @author Chris Bailey (c.bailey@bristol.ac.uk)
 */
public class HarvestEventJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        log.info("The HarvestEventJob has started.");
        try {

            StoreWrapperManager manager = new StoreWrapperManagerImpl("/sdb.ttl");
            Repository repository = new SdbRepositoryImpl(manager);

            Harvester harvester = new EventHarvesterImpl(repository);
            harvester.harvest();

        } catch (IOException ex) {
            log.error("Failed to execute job: " + ex.getMessage());
            throw new JobExecutionException(ex);
        }

        log.info("The HarvestEventJob has finished.");
    }

    Logger log = Logger.getLogger(HarvestEventJob.class);
}
