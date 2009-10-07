package org.ilrt.mca.dao;

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
import org.ilrt.mca.domain.BaseItem;
import org.ilrt.mca.domain.Item;
import org.ilrt.mca.domain.feeds.FeedItemImpl;
import org.ilrt.mca.domain.map.KmlMapItemImpl;
import org.ilrt.mca.rdf.Repository;
import org.ilrt.mca.vocab.GEO;
import org.ilrt.mca.vocab.MCA_REGISTRY;

import javax.ws.rs.core.MultivaluedMap;
import java.text.ParseException;
import java.util.Collections;


/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class ItemDaoImpl extends AbstractDao implements ItemDao {

    public ItemDaoImpl(Repository repository) throws Exception {
        this.repository = repository;
        findItemsSparql = loadSparql("/sparql/findItems.rql");
        kmlMapDetailsSparql = loadSparql("/sparql/findKmlMapDetails.rql");
        findFeedsSparql = loadSparql("/sparql/findAllItemsInGraph.rql");
    }

    @Override
    public Item findItem(String id, MultivaluedMap<String, String> parameters) {

        Model model = findModel(id, parameters);

        if (model.isEmpty()) {
            return null;
        }

        Resource resource = model.getResource(id);

        // are we a specific type
        if (resource.hasProperty(RDF.type)) {

            // kml map source ?
            if (resource.getProperty(RDF.type).getResource().getURI()
                    .equals(MCA_REGISTRY.KmlMapSource.getURI())) {
                KmlMapItemImpl item = new KmlMapItemImpl();
                getBasicDetails(resource, item);
                findMapDetails(item, resource);
                return item;
            } else if (resource.getProperty(RDF.type).getResource().getURI()
                    .equals(MCA_REGISTRY.FeedSource.getURI())) {

                Resource graph = resource.getProperty(RDFS.seeAlso).getResource();
                FeedItemImpl item = new FeedItemImpl();
                getBasicDetails(resource, item);
                feedDetails(item, graph);
                return item;
            } else if (resource.getProperty(RDF.type).getResource().getURI().equals(RSS.item.getURI())) {

                System.out.println("Its an Item!");
                return feedItemDetails(resource);
            }
        }

        // fallback item
        BaseItem item = new BaseItem();
        getBasicDetails(resource, item);
        return item;

    }

    @Override
    public Model findModel(String id, MultivaluedMap<String, String> parameters) {

        Model model = repository.find("id", id, findItemsSparql);

        if (model.isEmpty()) {
            return null;
        }

        Resource resource = model.getResource(id);

        // are we a specific type
        if (resource.hasProperty(RDF.type)) {

            log.debug("The resource type: "
                    + resource.getProperty(RDF.type).getResource().getURI());

            // kml map source ?
            if (resource.getProperty(RDF.type).getResource().getURI()
                    .equals(MCA_REGISTRY.KmlMapSource.getURI())) {

                log.debug("We are dealing with a map");

                Model kmlModel = repository.find("id", id, kmlMapDetailsSparql);
                model = ModelFactory.createUnion(model, kmlModel);
            } else if (resource.getProperty(RDF.type).getResource().getURI()
                    .equals(MCA_REGISTRY.FeedSource.getURI())) {

                log.debug("We are dealing with a feed");


                if (resource.hasProperty(RDFS.seeAlso)) {

                    QuerySolutionMap bindings = new QuerySolutionMap();

                    Resource graph = resource.getProperty(RDFS.seeAlso).getResource();
                    bindings.add("graph", graph);

                    // check to see that we have a parameter value called "item"
                    // if so, we want to run a CONSTRUCT query that pulls just that
                    // news item and passes back the model for that. maybe give it
                    // a type called ITEM or something so that the DAO deals with it
                    // appropriately. also specify the template used? could be specified
                    // in the sparql or in a seperate part of the registry maybe?
                    // this area could do with a "buddy" review once we have an initial
                    // implementation up. ripe for some re-factoring :-)

                    if (parameters.containsKey("item")) {

                        // specific item
                        bindings.add("s", ResourceFactory.createResource(
                                parameters.getFirst("item")));
                        //Model feedModel = repository.find(bindings, findFeedsSparql);

                    }

                    Model feedModel = repository.find(bindings, findFeedsSparql);



                    if (parameters.containsKey("item")) {

                        System.out.println("YES, WE ARE HERE !!!?!!!??!!!!");

                        feedModel.add(feedModel.getResource(parameters.getFirst("item")), MCA_REGISTRY.template, feedModel.createLiteral("grr.ftl"));

                        feedModel.write(System.out);

                        return feedModel;
                    } else {

                        return ModelFactory.createUnion(model, feedModel);
                    }

                }

            }
        }

        return model;
    }

    private void getBasicDetails(Resource resource, BaseItem item) {

        // identifier
        item.setId(resource.getURI());

        // label
        if (resource.hasProperty(RDFS.label)) {
            item.setLabel(resource.getProperty(RDFS.label).getLiteral().getLexicalForm());
        }

        // description
        if (resource.hasProperty(DC.description)) {
            item.setDescription(resource.getProperty(DC.description).getLiteral().getLexicalForm());
        }

        // order
        if (resource.hasProperty(MCA_REGISTRY.order)) {
            item.setOrder(resource.getProperty(MCA_REGISTRY.order).getInt());
        }

        // template
        if (resource.hasProperty(MCA_REGISTRY.template)) {
            item.setTemplate(resource.getProperty(MCA_REGISTRY.template).getResource().getURI());
        }

        // type
        if (resource.hasProperty(RDF.type)) {
            item.setType(resource.getProperty(RDF.type).getResource().getURI());
        }

        // items
        StmtIterator iter = resource.listProperties(MCA_REGISTRY.hasItem);

        while (iter.hasNext()) {

            Statement stmt = iter.nextStatement();

            Resource r = stmt.getResource();
            BaseItem link = new BaseItem();
            getBasicDetails(r, link);
            item.getItems().add(link);
        }

        Collections.sort(item.getItems());
    }


    private void findMapDetails(KmlMapItemImpl item, Resource resource) {

        if (resource.hasProperty(GEO.latitude)) {
            item.setLatitude(resource.getProperty(GEO.latitude).getDouble());
        }

        if (resource.hasProperty(GEO.longitude)) {
            item.setLongitude(resource.getProperty(GEO.longitude).getDouble());
        }

        if (resource.hasProperty(RDFS.seeAlso)) {
            item.setKmlUrl(resource.getProperty(RDFS.seeAlso).getResource().getURI());
        }
    }


    private void feedDetails(FeedItemImpl item, Resource resource) {

        if (resource.hasProperty(RSS.items)) {

            StmtIterator stmtiter = resource.getModel().listStatements(null, RDF.type, RSS.item);


            while (stmtiter.hasNext()) {

                Statement statement = stmtiter.nextStatement();

                Resource r = statement.getSubject();

                item.getItems().add(feedItemDetails(r));

            }

            Collections.sort(item.getItems());
        }

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

    private String findItemsSparql = null;
    private String kmlMapDetailsSparql = null;
    private String findFeedsSparql = null;
    private Repository repository;

    Logger log = Logger.getLogger(ItemDaoImpl.class);
}
