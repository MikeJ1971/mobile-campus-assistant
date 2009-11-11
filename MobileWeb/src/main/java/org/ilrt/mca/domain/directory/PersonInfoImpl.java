/**
 * 
 */
package org.ilrt.mca.domain.directory;

import com.google.gson.annotations.Expose;

/**
 * @author ecjet
 *
 */
public class PersonInfoImpl implements PersonInfo {

	@Expose private String person_key;
	@Expose private String email;
	@Expose private String family_name;
	@Expose private String given_name;
	@Expose private String job_title;
	@Expose private String org_unit;
	@Expose private String post_code;
	@Expose private String[] address;
	@Expose private String telephone;
	@Expose private String title;

	@Override
	public String getPersonKey() {
		return person_key;
	}

	public void setPersonKey(String v) {
		person_key = v;
	}
	
	@Override
	public String getEmail() {
		return email;
	}

	public void setEmail(String v) {
		email = v;
	}

	@Override
	public String getFamilyName() {
		return family_name;
	}
	
	public void setFamilyName(String v) {
		family_name = v;
	}

	@Override
	public String getGivenName() {
		return given_name;
	}
	
	public void setGivenName(String v) {
		given_name = v;
	}

	@Override
	public String getJobTitle() {
		return job_title;
	}

	public void setJobTitle(String v) {
		job_title = v;
	}
	
	@Override
	public String getOrganizationUnit() {
		return org_unit;
	}

	public void setOrganizationUnit(String v) {
		org_unit = v;
	}
	
	@Override
	public String getPostCode() {
		return post_code;
	}

	public void setPostCode(String v) {
		post_code = v;
	}
	
	@Override
	public String[] getStreetAddress() {
		return address;
	}

	public void setStreetAddress(String[] v) {
		address = v;
	}
	
	@Override
	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String v) {
		telephone = v;
	}
	
	@Override
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String v) {
		title = v;
	}

}
