package org.ilrt.mca.dao.delegate;

import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.RSS;
import org.apache.log4j.Logger;
import org.ilrt.mca.Common;
import org.ilrt.mca.dao.AbstractDao;
import org.ilrt.mca.domain.Item;
import org.ilrt.mca.domain.feeds.FeedItemImpl;
import org.ilrt.mca.rdf.Repository;
import org.ilrt.mca.vocab.MCA_REGISTRY;

import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.text.ParseException;
import java.util.Collections;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class FeedDelegateImpl extends AbstractDao implements Delegate {

    public FeedDelegateImpl(final Repository repository) {
        this.repository = repository;
        try {
            findFeedsSparql = loadSparql("/sparql/findAllItemsInGraph.rql");
            findNewsItem = loadSparql("/sparql/findNewsItem.rql");
        } catch (IOException ex) {
            log.error("Unable to load SPARQL query: " + ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Item createItem(Resource resource, MultivaluedMap<String, String> parameters) {

        // the feed is from a specified graph
        Resource graphUri = resource.getProperty(RDFS.seeAlso).getResource();

        FeedItemImpl item = new FeedItemImpl();

        if (graphUri.hasProperty(RSS.items)) {  // dealing with a whole feed



            StmtIterator stmtiter = graphUri.getModel().listStatements(null, RDF.type, RSS.item);

            while (stmtiter.hasNext()) {
                Statement statement = stmtiter.nextStatement();
                Resource r = statement.getSubject();
                item.getItems().add(feedItemDetails(r));
            }

            Collections.sort(item.getItems());

        } else if (graphUri.hasProperty(MCA_REGISTRY.hasItem)) {    // dealing with an item

            item = feedItemDetails(graphUri.getProperty(MCA_REGISTRY.hasItem).getResource());
        }

        getBasicDetails(resource, item);

        return item;
    }

    @Override
    public Model createModel(Resource resource, MultivaluedMap<String, String> parameters) {

        QuerySolutionMap bindings = new QuerySolutionMap();

        if (resource.hasProperty(RDFS.seeAlso)) {

            // we have a parameter so we are interested in a single item
            if (parameters.containsKey("item")) {

                bindings.add("id", resource);
                bindings.add("seeAlso", resource.getProperty(RDFS.seeAlso).getResource());
                bindings.add("s", ResourceFactory.createResource(parameters.getFirst("item")));

                return repository.find(bindings, findNewsItem);
            }

            // seeAlso will be the name of the graph and so we need to bind
            Resource graph = resource.getProperty(RDFS.seeAlso).getResource();
            bindings.add("graph", graph);

            // if we have a parameter then we are interested in a single item
            if (parameters.containsKey("item")) {
                bindings.add("s", ResourceFactory.createResource(parameters.getFirst("item")));
            }

            // search feeds with the specified item
            Model feedModel = repository.find(bindings, findFeedsSparql);

            // single news item ...
            if (parameters.containsKey("item")) {

                feedModel.add(feedModel.getResource(parameters.getFirst("item")),
                        MCA_REGISTRY.template, feedModel.createLiteral("grr.ftl"));

                return feedModel;
            }

            return ModelFactory.createUnion(resource.getModel(), feedModel);
        }

        log.debug("Haven't got the data we expected!");
        return null;
    }

    private FeedItemImpl feedItemDetails(Resource resource) {

        FeedItemImpl feedItem = new FeedItemImpl();

        feedItem.setId(resource.getURI());

        // item title
        if (resource.hasProperty(RSS.title)) {
            feedItem.setLabel(resource.getProperty(RSS.title).getLiteral().getLexicalForm());
        }

        // item description
        if (resource.hasProperty(RSS.description)) {
            feedItem.setDescription(resource.getProperty(RSS.description).getLiteral()
                    .getLexicalForm());
        }

        // item link
        if (resource.hasProperty(RSS.link)) {

            String link = null;

            if (resource.getProperty(RSS.link).getObject().isResource()) {
                link = resource.getProperty(RSS.link).getResource().getURI();
            } else if (resource.getProperty(RSS.link).getObject().isLiteral()) {
                link = resource.getProperty(RSS.link).getLiteral().getLexicalForm();
            }

            feedItem.setLink(link);
        }

        // item date
        if (resource.hasProperty(DC.date)) {

            String feedItemDate = null;

            try {

                feedItemDate = resource.getProperty(DC.date).getLiteral().getLexicalForm();
                feedItem.setDate(Common.parseDate(feedItemDate));
            } catch (ParseException e) {
                log.error("Unable to parse: " + feedItemDate + " : " + e.getMessage());
            }
        }

        return feedItem;

    }


    private String findFeedsSparql = null;
    private String findNewsItem = null;
    private final Repository repository;
    Logger log = Logger.getLogger(FeedDelegateImpl.class);
}
