/**
 * 
 */
package org.ilrt.mca.domain.transport.bristol;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.ilrt.mca.domain.transport.Departure;
import org.ilrt.mca.domain.transport.DepartureImpl;
import org.ilrt.mca.domain.transport.DepartureInfo;
import org.ilrt.mca.domain.transport.DepartureInfoImpl;
import org.ilrt.mca.domain.transport.DepartureLocation;
import org.ilrt.mca.domain.transport.DepartureLocationImpl;
import org.ilrt.mca.domain.transport.DepartureService;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

/**
 * @author ecjet
 *
 */
public class DepartureServiceImpl implements DepartureService {

	private final Logger log = Logger.getLogger(DepartureServiceImpl.class);
	
	private XPath xpath = XPathFactory.newInstance().newXPath();
	
	private String proxy_url = "";
	
	/* (non-Javadoc)
	 * @see org.ilrt.mca.domain.transport.DepartureService#getDepartureInfo(java.lang.String)
	 */
	@Override
	public DepartureInfo getDepartureInfo(String locationId) throws Exception {
		// fetch content
		Document content = proxyFetch(locationId);
	
		// parse for departure info
		List<Departure> deps = extractDepartureInfo(content);

		// parse for departure info
		DepartureLocation stop = extractStopInfo(content);

		// parse for departure info
		String base_time = extractBaseTimeInfo(content);
		
		DepartureInfoImpl info = new DepartureInfoImpl();
		
		info.setBaseTime(base_time);
		info.setDepartures(deps);
		info.setLocation(stop);
		
		return info;
	}

	private Document proxyFetch(String naptan) throws Exception {
		
		// build url
		String urlString = proxy_url + naptan;
		
		Document doc;
		try {
			URL live_bus = new URL(urlString);
			URLConnection lbc = live_bus.openConnection();
			
			// Tidy input
			doc = tidy(lbc.getInputStream());
			
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

	private String extractBaseTimeInfo(Document content) {

		String time = "";
		
		try {
			XPathExpression expr = xpath.compile("//p[starts-with(text(),'Departure')]/b[2]/text()");
			Object result = expr.evaluate(content, XPathConstants.STRING);
			time = (String) result;
			
		} catch (XPathExpressionException e) {
			log.error(e.getLocalizedMessage());
		}
		
		return time;
	}


	private DepartureLocation extractStopInfo(Document content) {
		
		DepartureLocationImpl stop = new DepartureLocationImpl();
		
		try {
			XPathExpression expr = xpath.compile("//p[starts-with(text(),'Departure')]/b[1]/text()");
			Object result = expr.evaluate(content, XPathConstants.STRING);
			stop.setName((String) result);
			
			expr = xpath.compile("//p[starts-with(text(),'Stop')]/b[1]/text()");
			result = expr.evaluate(content, XPathConstants.STRING);
			stop.setId((String) result);
			
		} catch (XPathExpressionException e) {
			log.error(e.getLocalizedMessage());
		}
		
		return stop;
	}

	private List<Departure> extractDepartureInfo(Document content) {

		List<Departure> buses = new ArrayList<Departure>();

// After tidying, assumed format
//		<tr>
//		<td width='25%' nowrap>8&nbsp;</td>
//		<td width='35%' class='destination' nowrap>
//		Clifton-Zoo-Redland&nbsp;</td>
//		<td width='20%' align='right' nowrap>40 mins&nbsp;</td>
//		<td width='20%' nowrap>&nbsp;&nbsp;</td>
//		</tr>
		
		try {
			// FIXME assumption that all td elements contain departure data
			XPathExpression expr = xpath.compile("//td/text()");
			Object result = expr.evaluate(content, XPathConstants.NODESET);
			NodeList nodes = (NodeList) result;
			for (int i = 0; i < nodes.getLength(); i+=4) {
				DepartureImpl bus = new DepartureImpl();
				bus.setId(getTidyValue(nodes.item(i)));
				bus.setDestination(getTidyValue(nodes.item(i+1)));
				bus.setOffsetTime(getTidyValue(nodes.item(i+2)));
				buses.add(bus);
			}
		} catch (XPathExpressionException e) {
			log.error(e.getLocalizedMessage());
		}
		
		return buses;
	}
	
	private String getTidyValue(Node item) {
		
		return item.getNodeValue().replaceAll("\\W$", " ").trim();
		
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

	@Override
	public void init(Properties props) {
		proxy_url = props.getProperty(DepartureService.PROXY_URL_KEY);
	}

}
