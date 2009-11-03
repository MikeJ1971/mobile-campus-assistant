package org.ilrt.mca.dao.delegate;

import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.RSS;
import org.apache.log4j.Logger;
import org.ilrt.mca.Common;
import org.ilrt.mca.dao.AbstractDao;
import org.ilrt.mca.domain.Item;
import org.ilrt.mca.domain.feeds.FeedItemImpl;
import org.ilrt.mca.rdf.Repository;
import org.ilrt.mca.vocab.MCA_REGISTRY;
import org.joda.time.DateTime;

import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.text.ParseException;
import java.util.Collections;

/**
 * The feed delegate handles requests for feeds:
 * <p/>
 * 1) It might request all news items for a specific feed. Each feed is held in its own
 * named graph.
 * 2) It might request feed items that span across all named graphs.
 * 3) It requests the details of an individual news item.
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class FeedDelegateImpl extends AbstractDao implements Delegate {

    public FeedDelegateImpl(final Repository repository) {
        this.repository = repository;
        try {
            findNewsItems = loadSparql("/sparql/findNewsItems.rql");
            findNewsItemsByDate = loadSparql("/sparql/findNewsItemsByDate.rql");
        } catch (IOException ex) {
            log.error("Unable to load SPARQL query: " + ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Item createItem(Resource resource, MultivaluedMap<String, String> parameters) {

        // The item that we'll be returning
        FeedItemImpl item = new FeedItemImpl();

        // the RDF returned should now have each news item as an object of the mca:hasItem
        // property. the subject of the triple is the regitry URI that represents the
        // path to getting the news, e.g. <mca://registry/news/main/>
        StmtIterator iter = resource.listProperties(MCA_REGISTRY.hasNewsItem);

        // iterate through the news items
        while (iter.hasNext()) {
            Statement statement = iter.nextStatement();
            Resource r = statement.getResource();
            item.getItems().add(feedItemDetails(r));
        }

        // order them by date
        Collections.sort(item.getItems());

        // check to see if we are dealing with a request for an individual news item
        if (parameters.containsKey("item")) {
            if (item.getItems().size() != 0) {
                item = (FeedItemImpl) item.getItems().get(0);
            }
        }

        getBasicDetails(resource, item);

        return item;
    }

    @Override
    public Model createModel(Resource resource, MultivaluedMap<String, String> parameters) {


        if (resource.hasProperty(RDFS.seeAlso)) { // we are looking for a specific graph

            // we have a parameter so we are interested in a single item
            if (parameters.containsKey("item")) {
                return newsItem(resource, parameters.getFirst("item"));
            }

            QuerySolutionMap bindings = new QuerySolutionMap();

            // seeAlso will be the name of the graph and so we need to bind
            Resource graph = resource.getProperty(RDFS.seeAlso).getResource();
            bindings.add("id", resource);
            bindings.add("graph", graph);

            // search feeds with the specified item
            Model feedModel = repository.find(bindings, findNewsItems);

            return ModelFactory.createUnion(feedModel, resource.getModel());

        } else { // search all graphs

            //try {

                // calculate the start and end dates
                DateTime current = new DateTime();
                DateTime past = current.minusHours(24); // TODO the interval should be set in the registry

                String endDate = Common.parseXsdDate(current.toDate());
                String startDate = Common.parseXsdDate(past.toDate());

                QuerySolutionMap bindings = new QuerySolutionMap();
                bindings.add("startDate", ResourceFactory.createPlainLiteral(startDate));
                bindings.add("endDate", ResourceFactory.createPlainLiteral(endDate));
                bindings.add("id", resource);

                Model results = repository.find(bindings, findNewsItemsByDate);

                return ModelFactory.createUnion(results, resource.getModel());
        }

    }

    private FeedItemImpl feedItemDetails(Resource resource) {

        FeedItemImpl feedItem = new FeedItemImpl();

        feedItem.setId(resource.getURI());

        // provenance
        if (resource.hasProperty(MCA_REGISTRY.hasSource)) {
            feedItem.setProvenance(resource.getProperty(MCA_REGISTRY.hasSource)
                    .getResource().getURI());
        }

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

    private Model newsItem(Resource resource, String newsItemUri) {

        QuerySolutionMap bindings = new QuerySolutionMap();

        // bind to the graph - the source URI
        bindings.add("graph", resource.getProperty(RDFS.seeAlso).getResource());

        // bind to the URI of the specific news item
        bindings.add("itemId", ResourceFactory.createResource(newsItemUri));

        // bind to the URI in the registry that matches the HTTP request
        bindings.add("id", resource);

        Model model = repository.find(bindings, findNewsItems);

        // we want to use a special template for an individual news item
        Resource r = model.getResource(resource.getURI());
        Resource template = ResourceFactory.createResource("template://newsItem.ftl");

        if (r.hasProperty(MCA_REGISTRY.template)) {
            r.getProperty(MCA_REGISTRY.template).changeObject(template);
        } else {
            r.addProperty(MCA_REGISTRY.template, template);
        }

        return model;
    }

    private String findNewsItems = null;
    private String findNewsItemsByDate = null;
    private final Repository repository;
    Logger log = Logger.getLogger(FeedDelegateImpl.class);
}
