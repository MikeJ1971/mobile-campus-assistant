package org.ilrt.mca.domain.map;

import org.ilrt.mca.domain.Item;

/**
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public interface KmlMapItem extends Item {

    double getLongitude();

    double getLatitude();

    String getKmlUrl();

}
