/**
 * 
 */
package org.ilrt.mca.domain.transport;

import com.google.gson.annotations.Expose;

/**
 * @author ecjet
 *
 */
public class DepartureLocationImpl implements DepartureLocation {

	@Expose String id;
	@Expose String name;

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setId(String id) {
		this.id = id;
	}

}
