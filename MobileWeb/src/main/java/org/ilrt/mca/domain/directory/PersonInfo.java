/**
 * 
 */
package org.ilrt.mca.domain.directory;

/**
 * @author ecjet
 *
 */
public interface PersonInfo {

	String getPersonKey();
	
	String getTitle();
	
	String getGivenName();
	
	String getFamilyName();
	
	String getTelephone();
	
	String getEmail();
	
	String getJobTitle();
	
	String getOrganizationUnit();
	
	String[] getStreetAddress();
	
	String getPostCode();

}
