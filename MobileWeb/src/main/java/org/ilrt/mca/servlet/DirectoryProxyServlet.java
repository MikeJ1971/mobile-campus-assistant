package org.ilrt.mca.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.ilrt.mca.domain.directory.DirectoryService;
import org.ilrt.mca.domain.directory.PersonInfo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class DirectoryProxyServlet extends HttpServlet {

	public static final String DIRECTORY_SERVICE_KEY = "org.ilrt.mca.directory.class";
	
	private static final int PERSON_KEY = 1;
	private static final int QUERY = 2;

	private final Logger log = Logger.getLogger(DirectoryProxyServlet.class);

	private DirectoryService directoryService;

	@Override
	public void init(ServletConfig config) throws ServletException {

		log.info("DirectoryProxyServlet started.");

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
			
			String class_name = props.getProperty(DIRECTORY_SERVICE_KEY);
			if(class_name != null) {
				try {
					Object instance = Class.forName(class_name).newInstance();
					if(instance instanceof DirectoryService) {
						directoryService = (DirectoryService) instance;
						directoryService.init(props);
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
			log.error("DirectoryProxyServlet. Failed to load properties. "
					+ e.getLocalizedMessage());
		}
	}

	@Override
	public void destroy() {
		log.info("DirectoryProxyServlet shutdown.");
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		
		String query = request.getParameter("q");
		String personKey = request.getParameter("pk");
		
		int searchType;
		
		if (personKey != null && personKey.trim().length() != 0) {
			searchType = PERSON_KEY;
		} else if (query != null && query.trim().length() != 0) {
			searchType = QUERY;
		} else {
			response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE); // TODO check correct response
			return;
		}

		try {

			switch(searchType) {
		case PERSON_KEY:
			PersonInfo info = directoryService.getDetails(personKey);
			// return json
			Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
			
			response.setContentType("application/json");
			response.getWriter().print(gson.toJson(info));
			break;
		case QUERY:
			StringBuilder message = new StringBuilder();
			
			List<PersonInfo> infoList = directoryService.getList(query, message);
			
			if(infoList.size() == 1) {
				// one result
				info = infoList.get(0);
				// return json
				gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
				
				response.setContentType("application/json");
				response.getWriter().print(gson.toJson(info));
			} else {
				// > 1 result
				Map<String,Object> data = new HashMap<String,Object>();
				data.put("message", message.toString());
				data.put("results", infoList);
				
				// return json
				gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
				
				response.setContentType("application/json");
				response.getWriter().print(gson.toJson(data));
			}
			break;
		}
			
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}

	}

}

