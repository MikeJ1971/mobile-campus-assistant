package org.ilrt.mca.android.bus;

import org.ilrt.mca.android.bus.db.BusTimesDatabase;
import org.ilrt.mca.android.bus.map.BusOverlay;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Debug;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class BusTimesActivity extends MapActivity
{
	String proxyUrl = "";
	String markerLocation = "";
	String iconUrl = "";
	MapView mvMap;
	BusTimesDatabase db;
	MyLocationOverlay mMyLocationOverlay;
	int latitute, longitude;
	MapController mc;
	LocationManager myLocationManager;
	LocationListener myLocationListener;
	public ProgressDialog dialog;
	ProgressThread thread;
	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// setup the logger
		Common.setLoggerEnabled(this.getString(R.string.loggerenabled).equalsIgnoreCase("true"));

		// initialise the database
		db = new BusTimesDatabase(this);
		
		mvMap = (MapView) findViewById(R.id.mapmain);

		mc = mvMap.getController();
		mc.setZoom(17);

		mMyLocationOverlay = new MyLocationOverlay(this, mvMap);
		mvMap.getOverlays().add(mMyLocationOverlay);
		mMyLocationOverlay.enableMyLocation();
		mMyLocationOverlay.runOnFirstFix(new Runnable()
		{
			public void run()
			{
				mc.animateTo(mMyLocationOverlay.getMyLocation());
				mc.setZoom(17);
				Common.info(BusTimesActivity.class,"Creating overlay");
				addBusStopIcons(mvMap);
			}
		});

		final Button btnNavigateToMe = (Button) findViewById(R.id.btnNavigateHere);
		btnNavigateToMe.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				Common.info(BusTimesActivity.class, "Clicking button");
				try
				{
					mc.animateTo(mMyLocationOverlay.getMyLocation());
//					mc.setZoom(17);
//					mvMap.invalidate();
				} catch (Exception e)
				{
					Common.warn(BusTimesActivity.class,"Unable to animate map", e);
				}
			}
		});
		
		// set the label text
		final TextView lblHeader = (TextView) findViewById(R.id.topLabel);

		// load bus stop data if required
		if (db.getBusStopCount() == 0 || this.getString(R.string.forcereload).equalsIgnoreCase("true"))
		{
			dialog = ProgressDialog.show(BusTimesActivity.this, "", "Loading Bus Data. Please wait...", true);
			thread = new ProgressThread(this);
			thread.start();
		} // END if (db.getBusStopCount() == 0 || this.getString(R.string.forcereload).equalsIgnoreCase("true"))	
	}
	
    private void addBusStopIcons(MapView mvMap)
    {
    	Common.warn(BusTimesActivity.class,"Adding BusOverlay");
    	
    	try
    	{
    	Drawable marker = getResources().getDrawable(R.drawable.bus);
        marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
        mvMap.getOverlays().add(new BusOverlay(marker, db, mMyLocationOverlay));
    	}
    	catch (Exception e) { e.printStackTrace(); }

        mvMap.setClickable(true);
        mvMap.setEnabled(true);
        mvMap.setSatellite(false);
        mvMap.setTraffic(false);
        mvMap.setStreetView(false);
    }
    
  //Get the current location in start-up
    public void setInitialPoint()
    {
//    	myLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
//
//    	myLocationListener = myLocationManager.();
//
//    	myLocationManager.requestLocationUpdates(
//    			    LocationManager.GPS_PROVIDER,
//    			    0,
//    			    0,
//    			    myLocationListener);
//    			  
//    GeoPoint initGeoPoint = new GeoPoint(
//     (int)(myLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude()*1000000),
//     (int)(myLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude()*1000000));
//    	mc.animateTo(initGeoPoint);
    }
     
	// @Override
	// public void onResume()
	// {
	// if (mMyLocationOverlay != null) mMyLocationOverlay.enableMyLocation();
	// }
	//
	// @Override
	// public void onPause()
	// {
	// if (mMyLocationOverlay != null) mMyLocationOverlay.disableMyLocation();
	// }

	@Override
	protected boolean isRouteDisplayed()
	{
		// we're not interested in showing routes on our map so set this to false
		return false;
	}

	private class ProgressThread extends Thread {
		BusTimesActivity context;
       
        ProgressThread(BusTimesActivity a) {
            this.context = a;
        }
       
        public void run() {
            try
            { 
				// populate database with json content
				Common.info(BusTimesActivity.class,"Populating Bus Stops from json");
				
				// load json data
				String base_url = context.getString(R.string.base_url);
				String jsonUrl = base_url + context.getString(R.string.busurl);
				Common.info(BusTimesActivity.class,"Loading "+jsonUrl);
				
//				Debug.startMethodTracing("jsoninit");
				long s = System.currentTimeMillis();
				JSONObject json = Common.loadJSON(jsonUrl);
				long e = System.currentTimeMillis();
				Common.warn(BusTimesActivity.class,"Init JSON file took " + (e-s) + "ms");
//	            Debug.stopMethodTracing();
				
				if (json.length() == 0)
				{
					dialog.dismiss();
					Common.warn(BusTimesActivity.class,"Unable to parse JSON file");
					Common.showMessage(context, "Unable to parse JSON file");
				}
				else
				{
					try
					{
						proxyUrl = base_url + json.getString("proxyURLStem");
						markerLocation = base_url + json.getString("markersLocation");
						iconUrl = base_url + json.getString("markerIconLocation");
						
						// navigate to default pos
						int lat = (int)(Float.parseFloat(json.getString("latitude"))*1E6);
						int lng = (int)(Float.parseFloat(json.getString("longitude"))*1E6);
						context.mc.animateTo(new GeoPoint(lat,lng));
	
						// allow json object to be garbage collected
						json = null;
						
						markerLocation = "http://www.ilrt.bris.ac.uk/~cmcpb/mca/bustops.json";
						Common.info(BusTimesActivity.class,"Loading "+markerLocation);
						
//						Debug.startMethodTracing("jsonmarkers");
						s = System.currentTimeMillis();
						JSONObject json_markers = Common.loadJSON(markerLocation);
						e = System.currentTimeMillis();
						Common.warn(BusTimesActivity.class,"JSON Marker file took " + (e-s) + "ms");						
//						Debug.stopMethodTracing();
						
						JSONArray markers = json_markers.getJSONArray("markers");
						
						int count = markers.length();
						
						Common.info(BusTimesActivity.class.getName(),"markers.length(): " + count);
						
						if (count > 0) 
						{
							// if we have some valid data to insert, remove existing entries
							Common.info(BusTimesActivity.class,"Removing existing stop data");
							db.removeAllBusStops();
						}
						for (int i=0; i < count; i++)
						{
							JSONObject marker = markers.getJSONObject(i);
							lat = (int)(Float.parseFloat(marker.getString("lat")) * 1E6);
							lng = (int)(Float.parseFloat(marker.getString("lng")) * 1E6);
//							Common.info(BusTimesActivity.class,"Adding " + lat + "," + lng);
							db.addBus(marker.getString("id"), "", lat, lng);
						}
					} catch (JSONException je)
					{
						je.printStackTrace();
					}
				}
            } catch (Exception e) { e.printStackTrace(); }

            Common.info(BusTimesActivity.class,"Database contains " + db.getBusStopCount() + " bus stops");
            context.dialog.dismiss();
        }
	}
}