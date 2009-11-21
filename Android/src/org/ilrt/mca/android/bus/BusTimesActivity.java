package org.ilrt.mca.android.bus;

import java.util.LinkedList;
import java.util.List;

import org.ilrt.mca.android.bus.db.BusStopsCursor;
import org.ilrt.mca.android.bus.db.BusTimesDatabase;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

import de.android1.overlaymanager.ManagedOverlay;
import de.android1.overlaymanager.ManagedOverlayItem;
import de.android1.overlaymanager.OverlayManager;
import de.android1.overlaymanager.lazyload.LazyLoadCallback;
import de.android1.overlaymanager.lazyload.LazyLoadException;

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
	OverlayManager overlayManager;

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
		mvMap.setBuiltInZoomControls(true);

		// create an overlay manager to handle our bus stop points
		overlayManager = new OverlayManager(this, mvMap);
		
		// get a handle on the controller
		mc = mvMap.getController();
		
		// set the default zoom level
		mc.setZoom(17);
		
		// load bus stop data if required
		if (db.getBusStopCount() == 0 || this.getString(R.string.forcereload).equalsIgnoreCase("true"))
		{
			dialog = ProgressDialog.show(BusTimesActivity.this, "", "Loading Bus Data. Please wait...", true);
			thread = new ProgressThread(this);
			thread.start();
		} // END if (db.getBusStopCount() == 0 || this.getString(R.string.forcereload).equalsIgnoreCase("true"))	
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
	    Drawable defaultmarker = getResources().getDrawable(R.drawable.bus);     

	    ManagedOverlay managedOverlay = overlayManager.createOverlay("busstops",defaultmarker);
	    
	    managedOverlay.setLazyLoadCallback(new LazyLoadCallback() {

	    	public List<ManagedOverlayItem> lazyload(GeoPoint topLeft, GeoPoint bottomRight, ManagedOverlay overlay) throws LazyLoadException
	        {
				List<ManagedOverlayItem> results = new LinkedList<ManagedOverlayItem>();

//	    		try
//	    		{
					int width = (bottomRight.getLongitudeE6() - topLeft.getLongitudeE6())/2;
					int height = (topLeft.getLatitudeE6()-bottomRight.getLatitudeE6())/2;
					int lat = bottomRight.getLatitudeE6() + height;
					int lng = topLeft.getLongitudeE6() + width;
									    	
					BusStopsCursor c = db.getBusStopsForRegion(lat,lng,(int)(width*1.1),(int)(height*1.1));
					int count = c.getCount();
					String title;
					
					// cap the maximum number of markers to show on the screen
					if (count > 30) 
					{
						Common.warn(BusTimesActivity.class,count + " items returned, capping at 30");
						
						
						count = 30;
					}
										
					Common.info(BusTimesActivity.class,"size:"+count+" lat:"+lat+",lng:"+lng+ " w:"+width + ", h:"+height);
					
					for (int rowNum = 0; rowNum < count; rowNum++)
					{
						c.moveToPosition(rowNum);
						title = "(" + c.getColStopId() + ")" + c.getColTitle();
						lat = (int)c.getColLatitude();
						lng = (int)c.getColLongitude();
	//					Common.info(BusTimesActivity.class,lat + "," + lng);
						GeoPoint point = new GeoPoint(lat,lng);
						ManagedOverlayItem item = new ManagedOverlayItem(point, title,"Loading...");
						results.add(item);
					}
					c.close();
//	    		}
//	    		catch (Exception e)
//	    		{
//	    			throw new LazyLoadException(e.getMessage());
//	    		}
				return results;   
	        }
	    });

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
			}
		});

	    overlayManager.populate();	    
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
	
	@Override
	public void onRestart()
	{
		 super.onRestart();
	}
}