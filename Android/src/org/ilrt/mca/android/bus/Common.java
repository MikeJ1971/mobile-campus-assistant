package org.ilrt.mca.android.bus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;

public class Common
{
	static SSLContext sc;
	static boolean loggerEnabled = false;
	
	public static JSONObject loadJSON(String requestUrl)
	{
		JSONObject pos = new JSONObject();
		try
		{
			URL url = new URL(requestUrl);
			InputStream in;
			try
			{
				installAllTrustManager();
				HttpsURLConnection sUrl = (HttpsURLConnection) url.openConnection();
				sUrl.addRequestProperty("Accept","application/json");
				in = sUrl.getInputStream();
			}
			catch (ClassCastException cce)
			{
				in = url.openStream();
			}
			String result = convertStreamToString(in);
			
			pos = new JSONObject(result);
		} catch (JSONException e)
		{
			warn(BusTimesActivity.class.getName(),"Unable to parse JSON file", e);
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
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
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
	
	public static void showMessage(Context context, String msg)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(msg).setCancelable(true);
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	
	public static void error(Object source, String msg, Throwable error)
	{
		if (loggerEnabled()) Log.e(source.toString(), msg, error);
	}
	
	
	public static void error(Object source, String msg)
	{
		if (loggerEnabled()) error(source, msg, null);
	}
	
	public static void warn(Object source, String msg, Throwable error)
	{
		if (loggerEnabled()) Log.w(source.toString(),msg, error);
	}

	public static void warn(Object source, String msg)
	{
		if (loggerEnabled()) warn(source, msg, null);
	}
	
	public static void info(Object source, String msg)
	{
		if (loggerEnabled()) Log.i(source.toString(),msg);
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
