package org.ilrt.mca.android.bus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.ilrt.mca.android.bus.db.BusTimesDatabase;
import org.ilrt.mca.android.bus.map.BusOverlay;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
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

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// initalize the database
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
			}
		});

		final Button btnNavigateToMe = (Button) findViewById(R.id.btnNavigateHere);
		btnNavigateToMe.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				Log.i(BusTimesActivity.class.getName(), "Clicking button");
				try
				{
					mc.animateTo(mMyLocationOverlay.getMyLocation());
					mc.setZoom(17);
					mvMap.invalidate();
				} catch (Exception e)
				{
					Log.i(BusTimesActivity.class.getName(),
							"Unable to animate map", e);
				}
			}
		});

		// Load a hashmap with location and positions
		List<String> lsLocations = new ArrayList<String>();
		final HashMap<String, GeoPoint> hmLocations = new HashMap<String, GeoPoint>();
		hmLocations.put("Current Location", new GeoPoint((int) latitute,(int) longitude));
		lsLocations.add("Current Location");

		if (db.getBusStopCount() == 0)
		{
			ProgressDialog dialog = ProgressDialog.show(this, "BusTimes", "Loading Bus Data. Please wait...", false);
			dialog.show();
			
			// populate database with json content
			Log.i(BusTimesActivity.class.getName(),"No existing bus times, populating from json");
			
			// load json data
			String base_url = this.getString(R.string.base_url);
			JSONObject json = Common.loadJSON(base_url + this.getString(R.string.busurl));
			
			if (json.length() == 0)
			{
				dialog.dismiss();
				Common.showMessage(this, "Unable to parse JSON file");
			}
			else
			{
				// set the label text
				final TextView lblHeader = (TextView) findViewById(R.id.topLabel);
				try
				{
					
					proxyUrl = base_url + json.getString("proxyURLStem");
					markerLocation = base_url + json.getString("markersLocation");
					iconUrl = base_url + json.getString("markerIconLocation");
					
					 // set initial location
					int lat = Integer.parseInt(json.getString("longitude"));
					int lng = Integer.parseInt(json.getString("latitude"));
					
					mc.animateTo(new GeoPoint(lat, lng));
					mvMap.invalidate();
					
					lblHeader.setText(json.getString("label"));
				
					markerLocation = "http://www.ilrt.bris.ac.uk/~cmcpb/mca/bustops.json";
					JSONObject json_markers = Common.loadJSON(markerLocation);
					
					JSONArray markers = json_markers.getJSONArray("markers");
					Log.i(BusTimesActivity.class.getName(),"markers.length(): " + markers.length());
					for (int i=0; i < markers.length(); i++)
					{
						JSONObject marker = markers.getJSONObject(i);
						db.addBus(marker.getString("id"), null, Float.parseFloat(marker.getString("lat")), Float.parseFloat(marker.getString("lng")));
					}
				} catch (JSONException e)
				{
					e.printStackTrace();
				}
			}

			dialog.dismiss();
		} // END if (db.getBusStopCount() == 0)
		
		Log.i(BusTimesActivity.class.getName(),"Creating overlay");
		addBusStopIcons(mvMap);
		
	}
	
    private void addBusStopIcons(MapView mvMap)
    {
        Drawable marker = getResources().getDrawable(R.drawable.bus);
        marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
        mvMap.getOverlays().add(new BusOverlay(marker, db, mMyLocationOverlay));

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
		// TODO Auto-generated method stub
		return false;
	}
}