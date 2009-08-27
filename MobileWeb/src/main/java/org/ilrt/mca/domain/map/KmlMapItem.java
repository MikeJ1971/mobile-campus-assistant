package org.ilrt.mca.domain.map;

import org.ilrt.mca.domain.Item;

public interface KmlMapItem extends Item {

    double getLongitude();

    double getLatitude();

    String getKmlUrl();

}
