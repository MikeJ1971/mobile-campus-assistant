package org.ilrt.mca.harvester.xml;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDF;
import org.apache.log4j.Logger;
import org.ilrt.mca.Common;
import org.ilrt.mca.dao.AbstractDao;
import org.ilrt.mca.harvester.Harvester;
import org.ilrt.mca.harvester.Source;
import org.ilrt.mca.rdf.Repository;
import org.ilrt.mca.vocab.MCA_REGISTRY;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class AbstractXmlSourceHarvesterImpl extends AbstractDao implements Harvester {

    @Override
    public abstract void harvest();

    protected void saveOrUpdate(Source source, Date lastVisited, Model model) {

        if (model != null) {

            // delete the old data
            repository.deleteAllInGraph(source.getUrl());

            // add the harvested data
            repository.add(source.getUrl(), model);

            // update the last visited date
            RDFNode date = ModelFactory.createDefaultModel()
                    .createTypedLiteral(Common.parseXsdDate(lastVisited), XSDDatatype.XSDdateTime);
            repository.updatePropertyInGraph(Common.AUDIT_GRAPH_URI, source.getUrl(),
                    DC.date, date);
        } else {
            log.info("Unable to cache " + source.getUrl());
        }
    }

    protected List<XmlSource> findSources(String findSources) {

        List<XmlSource> sources = new ArrayList<XmlSource>();

        Model m = repository.find(findSources);

        if (!m.isEmpty()) {

            ResIterator iterator = m.listSubjectsWithProperty(RDF.type);

            while (iterator.hasNext()) {
                sources.add(getDetails(iterator.nextResource()));
            }
        }

        return sources;
    }

    protected XmlSource getDetails(Resource resource) {

        Date lastVisited = null;
        String xsl = null;

        String uri = resource.getURI();

        if (resource.hasProperty(MCA_REGISTRY.lastVisitedDate)) {
            try {
                lastVisited = Common.parseXsdDate(resource.getProperty(MCA_REGISTRY.lastVisitedDate)
                        .getLiteral().getLexicalForm());
            } catch (ParseException e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        if (resource.hasProperty(MCA_REGISTRY.hasXslSource)) {
            xsl = resource.getProperty(MCA_REGISTRY.hasXslSource).getResource().getURI();
        }

        return new XmlSource(uri, xsl, lastVisited);
    }

    protected Repository repository = null;
    Logger log = Logger.getLogger(AbstractXmlSourceHarvesterImpl.class);
}
