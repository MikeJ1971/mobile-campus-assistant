package org.ilrt.mca.quartz;

import org.apache.log4j.Logger;
import org.ilrt.mca.harvester.feeds.FeedHarvesterImpl;
import org.ilrt.mca.harvester.Harvester;
import org.ilrt.mca.rdf.StoreWrapperManager;
import org.ilrt.mca.rdf.StoreWrapperManagerImpl;
import org.ilrt.mca.rdf.Repository;
import org.ilrt.mca.rdf.SdbRepositoryImpl;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.IOException;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class HarvestFeedJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        log.info("The HarvestFeedJob has started.");
        try {

            StoreWrapperManager manager = new StoreWrapperManagerImpl("/sdb.ttl");
            Repository repository = new SdbRepositoryImpl(manager);

            Harvester harvester = new FeedHarvesterImpl(repository);
            harvester.harvest();

        } catch (IOException ex) {
            log.error("Failed to execute job: " + ex.getMessage());
            throw new JobExecutionException(ex);
        }

        log.info("The HarvestFeedJob has finished.");
    }

    Logger log = Logger.getLogger(HarvestFeedJob.class);
}
