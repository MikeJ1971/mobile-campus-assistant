package org.ilrt.mca.domain.map;

import org.ilrt.mca.domain.BaseItem;
import com.google.gson.annotations.Expose;

/**
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
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

    public String getProxyURLStem() {
        return proxyURLStem;
    }

	public void setMarkersLocation(String location) {
		this.markersLocation = location;
	}

	public void setProxyURLStem(String uri) {
		this.proxyURLStem = uri;
	}

    @Expose private double longitude = -1;
    @Expose private double latitude = -1;
    @Expose private String markersLocation = null;
    @Expose private String proxyURLStem = null;
}
