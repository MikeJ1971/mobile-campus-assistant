/**
 * 
 */
package org.ilrt.mca.domain.directory;

import org.ilrt.mca.domain.Item;

/**
 * @author ecjet
 *
 */
public interface Directory extends Item {

	String getDetailsUrlStem();
	
	String getQueryUrlStem();
}
