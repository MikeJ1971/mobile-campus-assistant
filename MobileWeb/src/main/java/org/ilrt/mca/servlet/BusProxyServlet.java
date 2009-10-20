package org.ilrt.mca.servlet;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

public class BusProxyServlet extends HttpServlet {

	public static final String BUS_PROXY_URL_KEY = "org.ilrt.mca.busproxy.url";

	private final Logger log = Logger.getLogger(BusProxyServlet.class);

	private String proxy_url = "";
	private XPath xpath = XPathFactory.newInstance().newXPath();

	@Override
	public void init(ServletConfig config) throws ServletException {

		log.info("BusProxyServlet started.");

		super.init(config);

		// find the configuration files
		String configLocation = config.getInitParameter("config-file");

		Properties props = new Properties();
		try {
			InputStream in = getClass().getResourceAsStream(
					configLocation);
	        if (in == null) {
	            throw new RuntimeException("Config file " + configLocation
	                    + " not found in classpath");
	        }
			props.load(in);
			proxy_url = props.getProperty(BUS_PROXY_URL_KEY);
		} catch (IOException e) {
			log.error("BusProxyServlet. Failed to load properties. "
					+ e.getLocalizedMessage());
		}
	}

	@Override
	public void destroy() {
		log.info("BusProxyServlet shutdown.");
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		
		String naptan = request.getParameter("naptan");

		if (naptan == null || naptan.trim().length() == 0) {
			response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE); // TODO check correct response
			return;
		}

		try {
			// fetch content
			Document content = proxyFetch(naptan);
		
			// parse for departure info
			List<Bus> deps = extractDepartureInfo(content);

			// parse for departure info
			BusStop stop = extractStopInfo(content);

			// parse for departure info
			String base_time = extractBaseTimeInfo(content);

			Map<String,Object> model = new HashMap<String,Object>();
			
			model.put("base_time", base_time);
			model.put("stop", stop);
			model.put("departures", deps);
			
			// return json
			Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
			
			response.setContentType("application/json");
			response.getWriter().print(gson.toJson(model));
			return;

		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
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

	private BusStop extractStopInfo(Document content) {
		
		BusStop stop = new BusStop();
		
		try {
			XPathExpression expr = xpath.compile("//p[starts-with(text(),'Departure')]/b[1]/text()");
			Object result = expr.evaluate(content, XPathConstants.STRING);
			stop.name = (String) result;
			
			expr = xpath.compile("//p[starts-with(text(),'Stop')]/b[1]/text()");
			result = expr.evaluate(content, XPathConstants.STRING);
			stop.id = (String) result;
			
		} catch (XPathExpressionException e) {
			log.error(e.getLocalizedMessage());
		}
		
		return stop;
	}

	private List<Bus> extractDepartureInfo(Document content) {

		List<Bus> buses = new ArrayList<Bus>();

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
				Bus bus = new Bus();
				bus.service = getTidyValue(nodes.item(i));
				bus.destination = getTidyValue(nodes.item(i+1));
				bus.due = getTidyValue(nodes.item(i+2));
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
	
	private class BusStop {
		@Expose String id;
		@Expose String name;
	}

	private class Bus {
		@Expose String destination;
		@Expose String service;
		@Expose String due;
	}
}

