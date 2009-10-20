package org.ilrt.mca.dao.delegate;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;
import org.apache.log4j.Logger;
import org.ilrt.mca.dao.AbstractDao;
import org.ilrt.mca.domain.Item;
import org.ilrt.mca.domain.contacts.ContactImpl;
import org.ilrt.mca.rdf.Repository;
import org.ilrt.mca.vocab.FOAF;

import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class ContactsDelegateImpl extends AbstractDao implements Delegate {

    public ContactsDelegateImpl(final Repository repository) {
        this.repository = repository;
        try {
            findContactsSparql = loadSparql("/sparql/findContacts.rql");
        } catch (IOException ex) {
            log.error("Unable to load SPARQL query: " + ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Item createItem(Resource resource, MultivaluedMap<String, String> parameters) {

        ContactImpl contactsImpl = new ContactImpl();

        if (resource.hasProperty(FOAF.mbox)) {
            contactsImpl.setEmail(resource.getProperty(FOAF.mbox).getLiteral().getLexicalForm());
        }

        if (resource.hasProperty(FOAF.phone)) {

            Resource phoneUri = resource.getProperty(FOAF.phone).getResource();

            contactsImpl.setPhoneNumber(phoneUri.getURI());

            if (phoneUri.hasProperty(RDFS.label)) {
                contactsImpl.setPhoneNumberLabel(phoneUri.getProperty(RDFS.label)
                        .getLiteral().getLexicalForm());
            }
        }

        getBasicDetails(resource, contactsImpl);

        return contactsImpl;
    }

    @Override
    public Model createModel(Resource resource, MultivaluedMap<String, String> parameters) {

        Model model = repository.find("id", resource.getURI(), findContactsSparql);

        return ModelFactory.createUnion(resource.getModel(), model);
    }

    private String findContactsSparql = null;
    private final Repository repository;
    Logger log = Logger.getLogger(ContactsDelegateImpl.class);
}
