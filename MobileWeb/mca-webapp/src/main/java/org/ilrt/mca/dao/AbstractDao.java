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

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import org.ilrt.mca.domain.BaseItem;
import org.ilrt.mca.vocab.MCA_REGISTRY;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public abstract class AbstractDao {

    protected void getBasicDetails(Resource resource, BaseItem item) {

        if (resource == null) {
            return;
        }

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

        // style
        if (resource.hasProperty(MCA_REGISTRY.style)) {
            item.setStyle(resource.getProperty(MCA_REGISTRY.style).getLiteral().getLexicalForm());
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

    public String loadSparql(String path) throws IOException {

        StringBuffer buffer = new StringBuffer();
        InputStream is = getClass().getResourceAsStream(path);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
            buffer.append("\n");
        }

        return buffer.toString();
    }

}
