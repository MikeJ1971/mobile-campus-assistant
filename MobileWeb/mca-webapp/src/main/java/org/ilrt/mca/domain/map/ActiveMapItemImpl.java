package org.ilrt.mca.domain.map;

import com.google.gson.annotations.Expose;
import org.ilrt.mca.domain.BaseItem;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @author Jasper Tredgold (jasper.tredgold@bristol.ac.uk)
 */
public class ActiveMapItemImpl extends BaseItem implements ActiveMapItem {

    public ActiveMapItemImpl() {
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

    public double getLatitude() {
        return latitude;
    }

    public String getMarkersLocation() {
        return markersLocation;
    }

    public String getMarkerIconLocation() {
        return markerIconLocation;
    }

    public String getProxyURLStem() {
        return proxyURLStem;
    }

    public void setMarkersLocation(String location) {
        this.markersLocation = location;
    }

    public void setProxyURLStem(String uri) {
        this.proxyURLStem = uri;
    }

    public void setMarkerIconLocation(String location) {
        this.markerIconLocation = location;
    }

    @Expose
    private double longitude = -1;
    @Expose
    private double latitude = -1;
    @Expose
    private String markersLocation = null;
    @Expose
    private String proxyURLStem = null;
    @Expose
    private String markerIconLocation = null;
}
