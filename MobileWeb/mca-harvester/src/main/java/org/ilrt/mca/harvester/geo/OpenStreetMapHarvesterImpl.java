package org.ilrt.mca.harvester.geo;

import org.apache.log4j.Logger;
import org.ilrt.mca.dao.AbstractDao;
import org.ilrt.mca.harvester.AbstractHarvesterImpl;
import org.ilrt.mca.harvester.Harvester;
import org.ilrt.mca.harvester.HttpResolverImpl;
import org.ilrt.mca.harvester.Resolver;
import org.ilrt.mca.rdf.DataManager;

import java.io.IOException;

public class OpenStreetMapHarvesterImpl extends AbstractHarvesterImpl implements Harvester {

    public OpenStreetMapHarvesterImpl(DataManager manager) throws IOException {
        super(manager);
    }

    @Override
    public void harvest() {

    }


    final private Logger log = Logger.getLogger(OpenStreetMapHarvesterImpl.class);

}
