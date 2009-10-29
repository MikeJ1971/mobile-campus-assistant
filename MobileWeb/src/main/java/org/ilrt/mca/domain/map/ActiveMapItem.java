/**
 * 
 */
package org.ilrt.mca.domain.map;

import org.ilrt.mca.domain.Item;

/**
 * @author ecjet
 *
 */
public interface ActiveMapItem extends Item {

	double getLongitude();

    double getLatitude();

    String getProxyURLStem();
    
    String getMarkersLocation();
    
    String getMarkerIconLocation();

}
