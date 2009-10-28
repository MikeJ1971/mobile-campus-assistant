package org.ilrt.mca.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.ilrt.mca.domain.transport.DepartureInfo;
import org.ilrt.mca.domain.transport.DepartureService;
import org.ilrt.mca.domain.transport.DepartureUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TransportProxyServlet extends HttpServlet {

	public static final String TRANSPORT_SERVICE_KEY = "org.ilrt.mca.transport.class";

	private final Logger log = Logger.getLogger(TransportProxyServlet.class);

	private DepartureService departureService;

	@Override
	public void init(ServletConfig config) throws ServletException {

		log.info("TransportProxyServlet started.");

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
			
			String class_name = props.getProperty(TRANSPORT_SERVICE_KEY);
			if(class_name != null) {
				try {
					Object instance = Class.forName(class_name).newInstance();
					if(instance instanceof DepartureService) {
						departureService = (DepartureService) instance;
						departureService.init(props);
					}
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		} catch (IOException e) {
			log.error("TransportProxyServlet. Failed to load properties. "
					+ e.getLocalizedMessage());
		}
	}

	@Override
	public void destroy() {
		log.info("TransportProxyServlet shutdown.");
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		
		String dep_loc = request.getParameter("id");

		if (dep_loc == null || dep_loc.trim().length() == 0) {
			response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE); // TODO check correct response
			return;
		}

		try {
			
			DepartureInfo info = departureService.getDepartureInfo(dep_loc);
			
			// return json
			Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
			
			response.setContentType("application/json");
			response.getWriter().print(gson.toJson(DepartureUtils.toMap(info)));
			return;

		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
	}
}

