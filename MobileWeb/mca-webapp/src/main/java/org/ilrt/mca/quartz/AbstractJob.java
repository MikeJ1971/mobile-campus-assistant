package org.ilrt.mca.quartz;

import org.ilrt.mca.rdf.ConnPoolStoreWrapperManagerImpl;
import org.ilrt.mca.rdf.DataManager;
import org.ilrt.mca.rdf.DataSourceManager;
import org.ilrt.mca.rdf.SdbManagerImpl;
import org.ilrt.mca.rdf.StoreWrapperManager;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public abstract class AbstractJob implements Job {

    public AbstractJob() {
        DataSourceManager dsm = new DataSourceManager();
        StoreWrapperManager wrapperManager = new ConnPoolStoreWrapperManagerImpl("/sdb.ttl",
                dsm.getDataSource());
        manager = new SdbManagerImpl(wrapperManager);
    }

    @Override
    public abstract void execute(JobExecutionContext context) throws JobExecutionException;


    protected final DataManager manager;
}
