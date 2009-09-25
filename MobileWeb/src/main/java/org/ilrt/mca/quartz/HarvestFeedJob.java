package org.ilrt.mca.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.apache.log4j.Logger;
import org.ilrt.mca.harvester.Harvester;
import org.ilrt.mca.harvester.FeedHarvesterImpl;


public class HarvestFeedJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        log.info("The HarvestFeedJob has started.");

        Harvester harvester = new FeedHarvesterImpl();
        harvester.harvest();

        log.info("The HarvestFeedJob has finished.");
    }

    Logger log = Logger.getLogger(HarvestFeedJob.class);
}
