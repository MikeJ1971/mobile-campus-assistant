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
package org.ilrt.mca.dao.delegate;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;
import org.apache.log4j.Logger;
import org.ilrt.mca.dao.AbstractDao;
import org.ilrt.mca.domain.Item;
import org.ilrt.mca.domain.contacts.ContactImpl;
import org.ilrt.mca.domain.directory.DirectoryImpl;
import org.ilrt.mca.rdf.Repository;
import org.ilrt.mca.vocab.FOAF;
import org.ilrt.mca.vocab.MCA_REGISTRY;

import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @author Jasper Tredgold (jasper.tredgold@bris.ac.uk)
 */
public class DirectoryDelegateImpl extends AbstractDao implements Delegate {

    public DirectoryDelegateImpl(final Repository repository) {
        this.repository = repository;
        try {
        	findDirectorySparql = loadSparql("/sparql/findDirectoryDetails.rql");
        } catch (IOException ex) {
            log.error("Unable to load SPARQL query: " + ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Item createItem(Resource resource, MultivaluedMap<String, String> parameters) {

        DirectoryImpl directoryImpl = new DirectoryImpl();

        if (resource.hasProperty(MCA_REGISTRY.detailsUrlStem)) {
        	directoryImpl.setDetailsUrlStem(resource.getProperty(MCA_REGISTRY.detailsUrlStem).getString());
        }

        if (resource.hasProperty(MCA_REGISTRY.queryUrlStem)) {
        	directoryImpl.setQueryUrlStem(resource.getProperty(MCA_REGISTRY.queryUrlStem).getString());
        }

        getBasicDetails(resource, directoryImpl);

        return directoryImpl;
    }

    @Override
    public Model createModel(Resource resource, MultivaluedMap<String, String> parameters) {

        Model model = repository.find("id", resource.getURI(), findDirectorySparql);

        return ModelFactory.createUnion(resource.getModel(), model);
    }

    private String findDirectorySparql = null;
    private final Repository repository;
    Logger log = Logger.getLogger(DirectoryDelegateImpl.class);
}