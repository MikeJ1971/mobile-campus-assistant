package org.ilrt.mca.android.bus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.entity.BasicHttpEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class BusTimesActivity extends MapActivity {
	
	MapView mvMap;
	MyLocationOverlay mMyLocationOverlay;
	int latitute, longitude;
	static SSLContext sc;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mvMap = (MapView) findViewById(R.id.mapmain);
        
        final MapController mc = mvMap.getController();
        
        mMyLocationOverlay = new MyLocationOverlay(this, mvMap);
        mvMap.getOverlays().add(mMyLocationOverlay);
        mMyLocationOverlay.enableMyLocation();
        mMyLocationOverlay.runOnFirstFix(
        		new Runnable() {
        			public void run()
        			{
        				mc.animateTo(mMyLocationOverlay.getMyLocation());
        				mc.setZoom(10);
        			}
        		});
        
        final Button btnNavigateToMe = (Button) findViewById(R.id.btnNavigateHere);
        btnNavigateToMe.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v)
        	{
        		Log.i(BusTimesActivity.class.getName(),"Clicking button");
        		try
        		{
        			mc.animateTo(mMyLocationOverlay.getMyLocation());
        			mvMap.invalidate();
        		}
        		catch (Exception e)
        		{
        			Log.i(BusTimesActivity.class.getName(),"Unable to animate map",e);
        		}
        	}
        });
        
        // Load a hashmap with location and positions
        List <String> lsLocations = new ArrayList<String>();
        final HashMap<String, GeoPoint> hmLocations = new HashMap<String, GeoPoint>();
        hmLocations.put("Current Location", new GeoPoint((int) latitute, (int) longitude));
        lsLocations.add("Current Location");
        
        // load json data
        try
		{
            URL url = new URL(this.getString(R.string.url));
            installAllTrustManager();
            HttpsURLConnection sUrl = (HttpsURLConnection)url.openConnection();
            HttpsURLConnection.setDefaultSSLSocketFactory( sc.getSocketFactory() );
            Log.i("URL", sUrl.toString());
            BasicHttpEntity entity = new BasicHttpEntity();
            entity.setContentType("text/json");
            entity.setContent(sUrl.getInputStream());
			String result = convertStreamToString(entity.getContent());
			Log.i("REST: result", result);
			JSONObject pos = new JSONObject(result);
		} 
        catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
        catch (MalformedURLException e)
		{
			Log.w(BusTimesActivity.class.getName(),"Unable to read url contents",e);
			e.printStackTrace();
		} 
        catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //worker = db.getWorker();
        
        
        
    }

//    @Override
//    public void onResume()
//    {
//    	if (mMyLocationOverlay != null) mMyLocationOverlay.enableMyLocation();
//    }
//
//    @Override
//    public void onPause()
//    {
//    	if (mMyLocationOverlay != null) mMyLocationOverlay.disableMyLocation();
//    }

	@Override
	protected boolean isRouteDisplayed()
	{
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * Obtained from http://www.kodejava.org/examples/266.html
	 * @param is
	 * @return
	 */
    public String convertStreamToString(InputStream is) {
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
    
    public static void installAllTrustManager() {
    	  TrustManager[] trustAllCerts = new TrustManager[]{
    	            new X509TrustManager() {
    	             public java.security.cert.X509Certificate[] getAcceptedIssuers() {
    	             return null;
    	            }
    	            public void checkClientTrusted(
    	             java.security.cert.X509Certificate[] certs, String authType) {
    	            }
    	            public void checkServerTrusted(
    	             java.security.cert.X509Certificate[] certs, String authType) {
    	            }
    	            }
    	            };
    	  
    	  // Install the all-trusting trust manager
    	  try {
    		  sc = SSLContext.getInstance("SSL");
    	   sc.init(null, trustAllCerts, new java.security.SecureRandom());
    	   HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    	   HttpsURLConnection.setDefaultHostnameVerifier(
    	       new HostnameVerifier() {
    	         public boolean verify(String urlHostname, javax.net.ssl.SSLSession _session) {
    	                                         return true;
    	         }
    	       }
    	    );
    	  } catch (Exception e) {
    	   e.printStackTrace();
    	  }
    	 }
}