/**
 * 
 */
package org.ilrt.mca.domain.transport;

import java.util.Properties;

/**
 * @author ecjet
 *
 */
public interface DepartureService {

	public static final String PROXY_URL_KEY = "org.ilrt.mca.transport.url";

	DepartureInfo getDepartureInfo(String location_id) throws Exception;
	
	void init(Properties props);
	
}
