package org.ilrt.mca.harvester.xml;

import com.hp.hpl.jena.rdf.model.Model;
import org.apache.log4j.Logger;
import org.ilrt.mca.harvester.Harvester;
import org.ilrt.mca.harvester.HttpResolverImpl;
import org.ilrt.mca.harvester.Resolver;
import org.ilrt.mca.rdf.Repository;

import java.io.IOException;
import java.util.Date;
import java.util.List;


/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class XmlSourceHarvesterImplImpl extends AbstractXmlSourceHarvesterImpl implements Harvester {

    public XmlSourceHarvesterImplImpl(Repository repository) throws IOException {
        this.repository = repository;
        resolver = new HttpResolverImpl();
        findSources = loadSparql("/sparql/findHarvestableXml.rql");
    }

    @Override
    public void harvest() {

        // new date to keep track of the visit
        Date lastVisited = new Date();

        // query registry for list of feeds to harvest
        // get the date that they were last updated
        List<XmlSource> sources = findSources(findSources);

        log.info("Found " + sources.size() + " sources to harvest");

        // harvest each source
        for (XmlSource source : sources) {

            log.info("Request to harvest: <" + source.getUrl() + ">");

            String xsl = "/" + source.getXsl().substring(6, source.getXsl().length());

            // harvest the data
            Model model = resolver.resolve(source, new XmlSourceResponseHandlerImpl(xsl));

            saveOrUpdate(source,  lastVisited, model);
        }
    }

    final private Resolver resolver;

    final private String findSources;

    final private Logger log = Logger.getLogger(XmlSourceHarvesterImplImpl.class);
}
