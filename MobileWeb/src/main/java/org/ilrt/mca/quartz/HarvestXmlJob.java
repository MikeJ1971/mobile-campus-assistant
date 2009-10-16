package org.ilrt.mca.quartz;

import org.apache.log4j.Logger;
import org.ilrt.mca.harvester.Harvester;
import org.ilrt.mca.harvester.xml.XmlSourceHarvesterImpl;
import org.ilrt.mca.rdf.Repository;
import org.ilrt.mca.rdf.SdbRepositoryImpl;
import org.ilrt.mca.rdf.StoreWrapperManager;
import org.ilrt.mca.rdf.StoreWrapperManagerImpl;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.IOException;

public class HarvestXmlJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("The HarvestXmlJob has started.");
        try {

            StoreWrapperManager manager = new StoreWrapperManagerImpl("/sdb.ttl");
            Repository repository = new SdbRepositoryImpl(manager);

            Harvester harvester = new XmlSourceHarvesterImpl(repository);
            harvester.harvest();

        } catch (IOException ex) {
            log.error("Failed to execute job: " + ex.getMessage());
            throw new JobExecutionException(ex);
        }

        log.info("The HarvestXmlJob has finished.");
    }

    Logger log = Logger.getLogger(HarvestXmlJob.class);
}
