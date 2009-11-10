/**
 * 
 */
package org.ilrt.mca.domain.directory.bristol;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.ilrt.mca.domain.directory.DirectoryService;
import org.ilrt.mca.domain.directory.PersonInfo;
import org.ilrt.mca.domain.directory.PersonInfoImpl;
import org.ilrt.mca.domain.transport.Departure;
import org.ilrt.mca.domain.transport.DepartureInfoImpl;
import org.ilrt.mca.domain.transport.DepartureLocation;
import org.ilrt.mca.domain.transport.DepartureLocationImpl;
import org.ilrt.mca.domain.transport.bristol.DepartureServiceImpl;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

/**
 * @author ecjet
 *
 */
public class DirectoryServiceImpl implements DirectoryService {

	private final Logger log = Logger.getLogger(DirectoryServiceImpl.class);
	
	private XPath xpath = XPathFactory.newInstance().newXPath();

	private String proxy_query_url;
	private String proxy_details_url;

	@Override
	public void init(Properties props) {
		proxy_query_url = props.getProperty(DirectoryService.PROXY_QUERY_URL_KEY);
		proxy_details_url = props.getProperty(DirectoryService.PROXY_DETAILS_URL_KEY);
	}

	@Override
	public PersonInfo getDetails(String personKey) throws Exception {
		// fetch content
		Document content = proxyFetch(proxy_details_url, personKey);
	
		// parse for person info
		PersonInfo info = extractPersonInfo(content);

		return info;
	}

	@Override
	public List<PersonInfo> getList(String query) throws Exception {
		// fetch content
		Document content = proxyFetch(proxy_query_url, query);
	
		// parse for person info
		List<PersonInfo> infoList = extractListPersonInfo(content);

		return infoList;
	}
	
	private Document proxyFetch(String proxy_stem, String value) throws Exception {
		
		// build url
		String urlString = proxy_stem + value;
		
		Document doc;
		try {
			URL proxyUrl = new URL(urlString);
			URLConnection conn = proxyUrl.openConnection();
			
			// Tidy input
			doc = tidy(conn.getInputStream());
			
		} catch (MalformedURLException mfue) {
			log.error(this.getClass().getName() + ". Proxy fetch failed. "
					+ mfue.getLocalizedMessage());
			throw (new Exception()); // TODO
			
		} catch (IOException ioe) {
			log.error(this.getClass().getName() + ". Proxy fetch failed. "
					+ ioe.getLocalizedMessage());
			throw (new Exception()); // TODO
			
		}
		return doc;
	}
	
	private PersonInfo extractPersonInfo(Document content) {
		
		PersonInfoImpl info = new PersonInfoImpl();
		
		info.setEmail(doXPath("email", content));
		info.setFamilyName(doXPath("family-name", content));
		info.setGivenName(doXPath("given-name", content));
		info.setTitle(doXPath("honorific-prefix", content));
		info.setJobTitle(doXPath("title", content));
		info.setOrganizationUnit(doXPath("organization-unit", content));
		info.setPostCode(doXPath("postal-code", content));
		info.setTelephone(doTelephoneXPath(content));
		info.setStreetAddress(doAddressXPath(content));
		return info;
	}
	
	private String doXPath(String name, Document doc) {
		String xpath_pre = "//*[contains(concat(' ',@class,' '), ' ";
		String xpath_post = " ')]/text()";
		
		try {
			XPathExpression expr = xpath.compile(xpath_pre + name + xpath_post);
			Object result = expr.evaluate(doc, XPathConstants.STRING);
			return((String) result);
		} catch (XPathExpressionException e) {
			log.error(e.getLocalizedMessage());
			return "";
		}
	}

	private String doTelephoneXPath(Document doc) {
		try {
			XPathExpression expr = xpath.compile("//*[contains(concat(' ',@class,' '), ' tel ')]");
			Object result = expr.evaluate(doc, XPathConstants.NODE);
			expr = xpath.compile("//*[contains(concat(' ',@class,' '), ' value ')]/text()");
			result = expr.evaluate(result, XPathConstants.STRING);
			return((String) result);
		} catch (XPathExpressionException e) {
			log.error(e.getLocalizedMessage());
			return "";
		}
	}

	private String[] doAddressXPath(Document doc) {
		List<String> address = new ArrayList<String>();
		try {
			XPathExpression expr = xpath.compile("//*[contains(concat(' ',@class,' '), ' street-address ')]/text()");
			Object result = expr.evaluate(doc, XPathConstants.NODESET);
			NodeList nodes = (NodeList) result;
			for (int i = 0; i < nodes.getLength(); i++) {
				address.add(nodes.item(i).getNodeValue());
			}
			return address.toArray(new String[]{});
		} catch (XPathExpressionException e) {
			log.error(e.getLocalizedMessage());
			return Collections.<String>emptyList().toArray(new String[]{});
		}
	}

	private List<PersonInfo> extractListPersonInfo(Document content) {
		
		List<PersonInfo> infoList = new ArrayList<PersonInfo>();
		
		PersonInfoImpl stop = new PersonInfoImpl();
		
		try {
			XPathExpression expr = xpath.compile("//p[starts-with(text(),'Departure')]/b[1]/text()");
			Object result = expr.evaluate(content, XPathConstants.STRING);
			
		} catch (XPathExpressionException e) {
			log.error(e.getLocalizedMessage());
		}
		
		return infoList;
	}

	private Document tidy(InputStream in) {
		
		Tidy tidy = new Tidy();
		tidy.setQuiet(true);
		try {
			tidy.setErrout(new PrintWriter("/dev/null")); // FIXME
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Document doc = tidy.parseDOM(in, null);
		
		//tidy.pprint(doc, System.out);
		return doc;
	}

	
}
