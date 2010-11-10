package org.ilrt.mca.harvester.geo;

import org.apache.log4j.Logger;
import org.ilrt.mca.dao.AbstractDao;
import org.ilrt.mca.harvester.Harvester;
import org.ilrt.mca.harvester.HttpResolverImpl;
import org.ilrt.mca.harvester.Resolver;
import org.ilrt.mca.rdf.DataManager;

import java.io.IOException;

public class OpenStreetMapHarvesterImpl extends AbstractDao implements Harvester {

    public OpenStreetMapHarvesterImpl(DataManager manager) throws IOException {
        this.manager = manager;
        this.resolver = new HttpResolverImpl();
        findSources = loadSparql("/sparql/findHarvestableOPMData.rql");
    }

    @Override
    public void harvest() {

    }


    private Resolver resolver;
    private DataManager manager;
    private String findSources;

    final private Logger log = Logger.getLogger(OpenStreetMapHarvesterImpl.class);

}
