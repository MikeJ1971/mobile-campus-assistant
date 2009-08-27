package org.ilrt.mca.domain.map;

import org.ilrt.mca.domain.BaseItem;
import org.ilrt.mca.domain.Item;

public class KmlMapItemImpl extends BaseItem implements KmlMapItem {

    public KmlMapItemImpl() {
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public void setKmlUrl(String kmlUrl) {
        this.kmlUrl = kmlUrl;
    }

    public float getLatitude() {
        return latitude;
    }

    public String getKmlUrl() {
        return kmlUrl;
    }

    private float longitude = -1;
    private float latitude = -1;
    private String kmlUrl = null;
}
