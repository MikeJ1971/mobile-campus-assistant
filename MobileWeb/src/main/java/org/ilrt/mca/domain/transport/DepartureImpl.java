/**
 * 
 */
package org.ilrt.mca.domain.transport;

import com.google.gson.annotations.Expose;

/**
 * @author ecjet
 *
 */
public class DepartureImpl implements Departure {

	@Expose String destination;
	@Expose String id;
	@Expose String offset_time;

	@Override
	public String getDestination() {
		return destination;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getOffsetTime() {
		return offset_time;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setDestination(String dest) {
		this.destination = dest;
	}

	public void setOffsetTime(String time) {
		this.offset_time = time;
	}

}
