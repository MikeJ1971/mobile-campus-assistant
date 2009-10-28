/**
 * 
 */
package org.ilrt.mca.domain.transport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.annotations.Expose;

/**
 * @author ecjet
 *
 */
public class DepartureInfoImpl implements DepartureInfo {

	@Expose List<Departure> departures = new ArrayList<Departure>();
	@Expose String base_time = "";
	@Expose DepartureLocation location;
	
	@Override
	public String getBaseTime() {
		return base_time;
	}

	@Override
	public List<Departure> getDepartures() {
		return Collections.unmodifiableList(departures);
	}

	@Override
	public DepartureLocation getLocation() {
		return location;
	}

	public void setBaseTime(String baseTime) {
		this.base_time = baseTime;
	}

	public void setDepartures(List<Departure> deps) {
		this.departures = deps;
	}

	public void setLocation(DepartureLocation location) {
		this.location = location;
	}

}
