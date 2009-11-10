/**
 * 
 */
package org.ilrt.mca.domain.directory;

import org.ilrt.mca.domain.BaseItem;

/**
 * @author ecjet
 *
 */
public class DirectoryImpl extends BaseItem implements Directory {

	private String details_url_stem;
	private String query_url_stem;

	@Override
	public String getDetailsUrlStem() {
		return details_url_stem;
	}
	
	public void setDetailsUrlStem(String v) {
		details_url_stem = v;
	}

	@Override
	public String getQueryUrlStem() {
		return query_url_stem;
	}

	public void setQueryUrlStem(String v) {
		query_url_stem = v;
	}

}
