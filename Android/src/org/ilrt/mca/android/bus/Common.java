package org.ilrt.mca.android.bus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class Common
{
	static SSLContext sc;
	static boolean loggerEnabled = false;
	
	public static JSONObject loadJSON(String requestUrl)
	{
		Common.info(Common.class,"Loading " + requestUrl);
		
		JSONObject pos = new JSONObject();
		try
		{
			URL url = new URL(requestUrl);
			InputStream in;

			HttpURLConnection conn;

			installAllTrustManager();
			try
			{
				conn = (HttpsURLConnection) url.openConnection();
			}
			catch (ClassCastException cce)
			{
				conn = (HttpURLConnection) url.openConnection();
			}
			
			conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
			conn.setRequestProperty("Accept","application/json");
			conn.setConnectTimeout(2000);
			
			//establish connection, get response headers
			conn.connect();

			//obtain the encoding returned by the server
			String encoding = conn.getContentEncoding();

			//create the appropriate stream wrapper based on the encoding type
			if (encoding != null && encoding.equalsIgnoreCase("gzip")) {
				Common.info(Common.class, "Using compressed data source");
				in = new GZIPInputStream(conn.getInputStream());
			}
			else if (encoding != null && encoding.equalsIgnoreCase("deflate")) {
				Common.info(Common.class, "Using deflated data source");
				in = new InflaterInputStream(conn.getInputStream(), new Inflater(true));
			}
			else {
				Common.info(Common.class, "Using uncompressed data source");
				in = conn.getInputStream();
			}

			String result = convertStreamToString(in);
			Common.info(Common.class, "Result:"+result);
			pos = new JSONObject(result);
		} catch (JSONException e)
		{
			warn(BusTimesActivity.class,"Unable to parse JSON file", e);
		} catch (MalformedURLException e)
		{
			warn(BusTimesActivity.class,"Unable to read url contents", e);
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return pos;
	}
	
	/**
	 * Obtained from http://www.kodejava.org/examples/266.html
	 * @param is
	 * @return
	 */
    private static String convertStreamToString(InputStream is) {
        /*
         * To convert the InputStream to String we use the BufferedReader.readLine()
         * method. We iterate until the BufferedReader return null which means
         * there's no more data to read. Each line will appended to a StringBuilder
         * and returned as String.
         */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is), 128);
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }
    
	private static void installAllTrustManager()
	{
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager()
		{
			public java.security.cert.X509Certificate[] getAcceptedIssuers(){return null;}

			public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)	{}

			public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)	{}
		} };

		// Install the all-trusting trust manager
		try
		{
			sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier()
			{
				public boolean verify(String urlHostname, javax.net.ssl.SSLSession _session)
				{
					return true;
				}
			});
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	
	public static void error(Class <?>source, String msg, Throwable error)
	{
		if (loggerEnabled()) Log.e(source.getSimpleName(), msg, error);
	}
	
	
	public static void error(Class <?>source, String msg)
	{
		if (loggerEnabled()) error(source, msg, null);
	}
	
	public static void warn(Class <?>source, String msg, Throwable error)
	{
		if (loggerEnabled()) Log.w(source.getSimpleName(),msg, error);
	}

	public static void warn(Class <?>source, String msg)
	{
		if (loggerEnabled()) warn(source, msg, null);
	}
	
	public static void info(Class <?>source, String msg)
	{
		if (loggerEnabled()) Log.i(source.getSimpleName(),msg);
	}
	
	public static void setLoggerEnabled(boolean b)
	{
		loggerEnabled = b;
	}
	
	private static boolean loggerEnabled()
	{
		return loggerEnabled;
	}
}
