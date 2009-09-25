package org.ilrt.mca.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.apache.log4j.Logger;


public class HarvestFeedJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        log.info("The Harvest Job has started.");
    }

    Logger log = Logger.getLogger(HarvestFeedJob.class);
}
