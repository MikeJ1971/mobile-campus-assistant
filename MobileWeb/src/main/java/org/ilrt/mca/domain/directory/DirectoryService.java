/**
 * 
 */
package org.ilrt.mca.domain.directory;

import java.util.List;
import java.util.Properties;

/**
 * @author ecjet
 *
 */
public interface DirectoryService {

	public static final String PROXY_QUERY_URL_KEY = "org.ilrt.mca.directory.query.url";
	public static final String PROXY_DETAILS_URL_KEY = "org.ilrt.mca.directory.details.url";

	void init(Properties props);

	PersonInfo getDetails(String personKey) throws Exception;

	List<PersonInfo> getList(String query, StringBuilder countMesasge) throws Exception;

}
