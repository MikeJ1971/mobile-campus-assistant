package org.ilrt.mca.domain.map;

import org.ilrt.mca.domain.BaseItem;
import com.google.gson.annotations.Expose;

/**
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class KmlMapItemImpl extends BaseItem implements KmlMapItem {

    public KmlMapItemImpl() {
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setKmlUrl(String kmlUrl) {
        this.kmlUrl = kmlUrl;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getKmlUrl() {
        return kmlUrl;
    }

    @Expose private double longitude = -1;
    @Expose private double latitude = -1;
    @Expose private String kmlUrl = null;
}
