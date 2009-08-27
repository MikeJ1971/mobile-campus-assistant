package org.ilrt.mca.domain.map;

import org.ilrt.mca.domain.Item;

public interface KmlMapItem extends Item {

    float getLongitude();

    float getLatitude();

    String getKmlUrl();

}
