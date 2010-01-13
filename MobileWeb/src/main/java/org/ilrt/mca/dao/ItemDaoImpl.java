/*
 * Copyright (c) 2009, University of Bristol
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1) Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2) Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3) Neither the name of the University of Bristol nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
package org.ilrt.mca.dao;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import org.apache.log4j.Logger;
import org.ilrt.mca.dao.delegate.ActiveMapDelegateImpl;
import org.ilrt.mca.dao.delegate.ContactsDelegateImpl;
import org.ilrt.mca.dao.delegate.Delegate;
import org.ilrt.mca.dao.delegate.DirectoryDelegateImpl;
import org.ilrt.mca.dao.delegate.FeedDelegateImpl;
import org.ilrt.mca.dao.delegate.HtmlFragmentDelegateImpl;
import org.ilrt.mca.dao.delegate.KmlMapDelegateImpl;
import org.ilrt.mca.domain.BaseItem;
import org.ilrt.mca.domain.Item;
import org.ilrt.mca.rdf.Repository;
import org.ilrt.mca.vocab.MCA_REGISTRY;

import javax.ws.rs.core.MultivaluedMap;
import org.ilrt.mca.dao.delegate.EventDelegateImpl;


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

        if (model == null || model.isEmpty()) {
            log.error("Unable to construct a model");
            return null;
        }

        // hand work to a delegate if possible
        Resource resource = model.getResource(id);
        Delegate delegate = findDelegate(resource);

        if (delegate != null) {
            log.debug("Using delegate: " + delegate.getClass().getName());
            return delegate.createItem(resource, parameters);
        }

        log.debug("We don't have delegate, defaulting to basic object");

        // fallback
        BaseItem item = new BaseItem();
        getBasicDetails(resource, item);
        model.close();
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
            } else if (type.equals(MCA_REGISTRY.ActiveMapSource.getURI())) {
                return new ActiveMapDelegateImpl(repository);
            } else if (type.equals(MCA_REGISTRY.Contact.getURI())) {
                return new ContactsDelegateImpl(repository);
            } else if (type.equals(MCA_REGISTRY.Directory.getURI())) {
                return new DirectoryDelegateImpl(repository);
            } else if (type.equals(MCA_REGISTRY.EventCalendar.getURI())) {
                return new EventDelegateImpl(repository);
            }

            log.debug("Haven't found an appropriate delegate");
        }

        return null; // 
    }


    private String findItemsSparql = null;
    private Repository repository;

    Logger log = Logger.getLogger(ItemDaoImpl.class);
}
