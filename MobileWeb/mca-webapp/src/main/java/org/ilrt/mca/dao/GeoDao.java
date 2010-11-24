package org.ilrt.mca.dao;

import com.hp.hpl.jena.rdf.model.Model;
import org.ilrt.mca.rdf.QueryManager;

import java.io.IOException;

public class GeoDao extends AbstractDao {

    public GeoDao(QueryManager manager) throws IOException {
        this.manager = manager;
        findByTypeSparql = loadSparql("/sparql/findGeoPointsByType.rql");
    }


    public Model findGeoPointByType(String typeUri) {

        return manager.find("type", typeUri, findByTypeSparql);
    }

    private String findByTypeSparql;

    QueryManager manager;
}
