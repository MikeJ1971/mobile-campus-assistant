package org.ilrt.mca.harvester;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import org.apache.log4j.Logger;
import org.ilrt.mca.Common;
import org.ilrt.mca.dao.AbstractDao;
import org.ilrt.mca.rdf.DataManager;
import org.ilrt.mca.vocab.MCA_REGISTRY;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class AbstractHarvesterImpl extends AbstractDao implements Harvester {

    protected AbstractHarvesterImpl(DataManager manager) throws IOException {
        this.manager = manager;
        resolver = new HttpResolverImpl();
        this.findSources = loadSparql("/sparql/findHarvestableSources.rql");
    }

    @Override
    public abstract void harvest();

    protected List<Source> findSources(String sourceType) {

        if (sourceType == null || sourceType.equals("")) {
            throw new RuntimeException("No type is provided for the source");
        }

        List<Source> sources = new ArrayList<Source>();

        Model m = manager.find("type", sourceType, findSources);

        if (!m.isEmpty()) {

            ResIterator iterator = m.listSubjectsWithProperty(RDF.type);

            while (iterator.hasNext()) {
                sources.add(getDetails(iterator.nextResource()));
            }
        }

        return sources;
    }

    private Source getDetails(Resource resource) {

        Date lastVisited = null;

        String uri = resource.getURI();

        if (resource.hasProperty(MCA_REGISTRY.lastVisitedDate)) {
            try {
                lastVisited = Common.parseXsdDate(resource.getProperty(MCA_REGISTRY.lastVisitedDate)
                        .getLiteral().getLexicalForm());
            } catch (ParseException e) {
                log.error(e.getMessage());
            }
        }

        return new Source(uri, lastVisited);
    }

    protected Resolver resolver;
    protected DataManager manager;
    protected String findSources;

    final private Logger log = Logger.getLogger(AbstractHarvesterImpl.class);

}
