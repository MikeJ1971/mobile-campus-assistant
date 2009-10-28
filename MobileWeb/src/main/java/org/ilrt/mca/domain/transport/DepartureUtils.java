/**
 * 
 */
package org.ilrt.mca.domain.transport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ecjet
 *
 */
public class DepartureUtils {

	public static Map<String, Object> toMap(DepartureInfo depInfo) {
		Map<String,Object> map = new HashMap<String,Object>();

		map.put("base_time", depInfo.getBaseTime());
		
		Map<String,String> loc = new HashMap<String,String>();
		loc.put("id", depInfo.getLocation().getId());
		loc.put("name", depInfo.getLocation().getName());
		map.put("stop", loc);
		
		List<Map<String,String>> deps = new ArrayList<Map<String,String>>();
		for(Departure dep: depInfo.getDepartures()) {
			Map<String,String> m = new HashMap<String,String>();
			m.put("service", dep.getId());
			m.put("destination", dep.getDestination());
			m.put("due", dep.getOffsetTime());
			deps.add(m);
		}
		map.put("departures", deps);
		return map;
	}

}
