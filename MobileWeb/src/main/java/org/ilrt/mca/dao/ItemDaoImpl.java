package org.ilrt.mca.dao;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import org.apache.log4j.Logger;
import org.ilrt.mca.dao.delegate.ContactsDelegateImpl;
import org.ilrt.mca.dao.delegate.Delegate;
import org.ilrt.mca.dao.delegate.FeedDelegateImpl;
import org.ilrt.mca.dao.delegate.HtmlFragmentDelegateImpl;
import org.ilrt.mca.dao.delegate.KmlMapDelegateImpl;
import org.ilrt.mca.domain.BaseItem;
import org.ilrt.mca.domain.Item;
import org.ilrt.mca.rdf.Repository;
import org.ilrt.mca.vocab.MCA_REGISTRY;

import javax.ws.rs.core.MultivaluedMap;


/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class ItemDaoImpl extends AbstractDao implements ItemDao {

    public ItemDaoImpl(Repository repository) throws Exception {
        this.repository = repository;
        findItemsSparql = loadSparql("/sparql/findItems.rql");

    }

    // ---------- PUBLIC METHODS

    @Override
    public Item findItem(String id, MultivaluedMap<String, String> parameters) {

        // get the model based on the id and any parameters passed
        Model model = findModel(id, parameters);

        if (model.isEmpty()) {
            log.error("Unable to construct a model");
            return null;
        }

        // hand work to a delegate if possible
        Resource resource = model.getResource(id);
        Delegate delegate = findDelegate(resource);

        if (delegate != null) {
            return delegate.createItem(resource, parameters);
        }

        // fallback
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

        // hand work to a delegate if possible
        Resource resource = model.getResource(id);
        Delegate delegate = findDelegate(resource);

        if (delegate != null) {
            return delegate.createModel(resource, parameters);
        }

        return model;
    }

    private Delegate findDelegate(Resource resource) {

        if (resource.hasProperty(RDF.type)) {

            String type = resource.getProperty(RDF.type).getResource().getURI();

            if (type.equals(MCA_REGISTRY.KmlMapSource.getURI())) {
                return new KmlMapDelegateImpl(repository);
            } else if (type.equals(MCA_REGISTRY.News.getURI()) ||
                    type.equals(MCA_REGISTRY.FeedItem.getURI())) {
                return new FeedDelegateImpl(repository);
            } else if (type.equals(MCA_REGISTRY.HtmlFragment.getURI())) {
                return new HtmlFragmentDelegateImpl(repository);
            } else if (type.equals(MCA_REGISTRY.Contact.getURI())) {
                return new ContactsDelegateImpl(repository);
            }
        }

        return null; // 
    }


    private String findItemsSparql = null;
    private Repository repository;

    Logger log = Logger.getLogger(ItemDaoImpl.class);
}
