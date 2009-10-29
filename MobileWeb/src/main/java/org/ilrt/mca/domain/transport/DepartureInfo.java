/**
 * 
 */
package org.ilrt.mca.domain.transport;

import java.util.List;
import java.util.Map;

/**
 * @author ecjet
 *
 */
public interface DepartureInfo {
	
	String getBaseTime();
	
	DepartureLocation getLocation();
	
	List<Departure> getDepartures();
	
}
