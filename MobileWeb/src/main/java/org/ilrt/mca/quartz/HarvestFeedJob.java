package org.ilrt.mca.quartz;

import org.apache.log4j.Logger;
import org.ilrt.mca.harvester.FeedHarvesterImpl;
import org.ilrt.mca.harvester.Harvester;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.IOException;


public class HarvestFeedJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        log.info("The HarvestFeedJob has started.");
        try {

            Harvester harvester = new FeedHarvesterImpl();
            harvester.harvest();

        } catch (IOException ex) {
            log.error("Failed to execute job: " + ex.getMessage());
            throw new JobExecutionException(ex);
        }

        log.info("The HarvestFeedJob has finished.");
    }

    Logger log = Logger.getLogger(HarvestFeedJob.class);
}
