package org.ilrt.mca.android.bus;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.ilrt.mca.android.bus.db.BusStopsCursor;
import org.ilrt.mca.android.bus.db.BusTimesDatabase;
import org.ilrt.mca.android.bus.db.DeparturesCursor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

import de.android1.overlaymanager.ManagedOverlay;
import de.android1.overlaymanager.ManagedOverlayGestureDetector;
import de.android1.overlaymanager.ManagedOverlayItem;
import de.android1.overlaymanager.OverlayManager;
import de.android1.overlaymanager.ZoomEvent;
import de.android1.overlaymanager.lazyload.LazyLoadCallback;
import de.android1.overlaymanager.lazyload.LazyLoadException;

public class BusTimesActivity extends MapActivity
{
	private static final int MENU_QUIT = 0;
	private static final int MENU_RELOAD_STOP_INFO = 1;
	
	public static final int DIALOG_ERROR = 0;
	public static final int DIALOG_INFORMATION = 1;
	
	String proxyUrl = "";
	String markerLocation = "";
	String iconUrl = "";
	MapView mvMap;
	BusTimesDatabase db;
	MyLocationOverlay mMyLocationOverlay;
	OverlayManager overlayManager;
	MapController mc;
	public ProgressDialog dialog;
	LoadBusStopDetailsThread thread;
	Activity popup;
	TextView popupDescription;
	int destinationCacheLength = 0;
	Drawable defaultmarker;
    Drawable disabledmarker; 
    int maxMarkers;
    Intent intent;
    public String dialogMessage;
    public String dialogTitle;
	
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
		
		initVariables();
		
		maxMarkers = Integer.parseInt(this.getString(R.string.maxMarkersPerScreen));
		
		// load bus stop data if required
		if (db.getBusStopCount() == 0 || this.getString(R.string.forcereload).equalsIgnoreCase("true"))
		{
			reloadBusStopInformation();
		} // END if (db.getBusStopCount() == 0 || this.getString(R.string.forcereload).equalsIgnoreCase("true"))			
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
	    defaultmarker = getResources().getDrawable(R.drawable.bus);
	    disabledmarker = getResources().getDrawable(R.drawable.bus_unknown);
	    
	    disabledmarker.setBounds(0,0,disabledmarker.getIntrinsicWidth(),disabledmarker.getIntrinsicHeight());

	    ManagedOverlay managedOverlay = overlayManager.createOverlay("busstops",defaultmarker);

	    managedOverlay.setLazyLoadCallback(new LazyLoadCallback() {

	    	public List<ManagedOverlayItem> lazyload(GeoPoint topLeft, GeoPoint bottomRight, ManagedOverlay overlay) throws LazyLoadException
	        {
				List<ManagedOverlayItem> results = new LinkedList<ManagedOverlayItem>();

				int width = (bottomRight.getLongitudeE6() - topLeft.getLongitudeE6())/2;
				int height = (topLeft.getLatitudeE6()-bottomRight.getLatitudeE6())/2;
				int lat = bottomRight.getLatitudeE6() + height;
				int lng = topLeft.getLongitudeE6() + width;
								    	
				BusStopsCursor c = db.getBusStopsForRegion(lat,lng,(int)(width*1.1),(int)(height*1.1));
				int count = c.getCount();
				String title;

				Common.info(BusTimesActivity.class,"size:"+count+" lat:"+lat+",lng:"+lng+ " w:"+width + ", h:"+height);

				int minWidthDist = 0;
				int minHeightDist = 0;
				// cap the maximum number of markers to show on the screen
				if (count > maxMarkers) 
				{
					Common.warn(BusTimesActivity.class,count + " items returned exceeds max limit");
											
					
					minWidthDist = overlay.getDefaultMarker().getIntrinsicWidth();
					minWidthDist = width/overlay.getManager().getMapView().getMeasuredWidth() * minWidthDist;
					
					minHeightDist = overlay.getDefaultMarker().getIntrinsicHeight();
					minHeightDist = height/overlay.getManager().getMapView().getMeasuredHeight() * minHeightDist;
					
					Common.info(BusTimesActivity.class,"minWidthDist:"+minWidthDist + "  IntrinsicWidth:"+overlay.getDefaultMarker().getIntrinsicWidth() + " MeasuredWidth:"+overlay.getManager().getMapView().getMeasuredWidth());
					Common.info(BusTimesActivity.class,"minHeightDist:"+minHeightDist + "  IntrinsicHeight:"+overlay.getDefaultMarker().getIntrinsicHeight() +  " MeasuredHeight:"+overlay.getManager().getMapView().getMeasuredHeight());
				}
							
				boolean overMinDist = true;
				int insertedCount = 0;
				for (int rowNum = 0; rowNum < count; rowNum++)
				{
					c.moveToPosition(rowNum);
					title = c.getColStopId();
					lat = (int)c.getColLatitude();
					lng = (int)c.getColLongitude();
					
					// check that we are further than minimum distance of all other markers
					overMinDist = true;
					for (ManagedOverlayItem it : results)
					{
						int otherLat = it.getPoint().getLatitudeE6();
						int otherLng = it.getPoint().getLongitudeE6();
						if ((lat > (otherLat - minHeightDist) && lat < (otherLat + minHeightDist)) && 
								(lng > (otherLng - minWidthDist) && lng < (otherLng + minWidthDist))) 
						{
							overMinDist = false;
							Common.info(BusTimesActivity.class,"Rejecting " +lat + "," + lng);
						}
					}
					
					if (overMinDist)
					{
						insertedCount++;
						Common.info(BusTimesActivity.class,"Adding " +lat + "," + lng);
						GeoPoint point = new GeoPoint(lat,lng);
						if (count > maxMarkers) 
						{
							ManagedOverlayItem item = new ManagedOverlayItem(point, "","");
							item.setMarker(disabledmarker);
							results.add(item);
						}
						else
						{
							ManagedOverlayItem item = new ManagedOverlayItem(point, title,"");
							results.add(item);
						}
					}
				}
				c.close();
				return results;   
	        }
	    });

	    managedOverlay.setOnOverlayGestureListener(new ManagedOverlayGestureDetector.OnOverlayGestureListener() 
	    {
			public boolean onDoubleTap(MotionEvent e, ManagedOverlay overlay, GeoPoint point, ManagedOverlayItem item) {
				mc.animateTo(point);
				mc.zoomIn();
				return true;
			}

			public void onLongPress(MotionEvent arg0, ManagedOverlay arg1)
			{
			}

			public void onLongPressFinished(MotionEvent arg0, ManagedOverlay arg1, GeoPoint arg2, ManagedOverlayItem arg3)
			{
			}

			public boolean onScrolled(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3, ManagedOverlay arg4)
			{
				return false;
			}

			public boolean onSingleTap(MotionEvent arg0, ManagedOverlay overlay, GeoPoint gPoint, ManagedOverlayItem item)
			{	
				if (item != null && !item.getTitle().equals(""))
				{
					mc.animateTo(gPoint, new LoadDepartureDetailsThread((BusTimesActivity)overlay.getMapView().getContext(),item));
				}
				return false;
			}

			public boolean onZoom(ZoomEvent arg0, ManagedOverlay arg1)
			{
				return false;
			}
	    });

	    overlayManager.populate();
	    
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
	}

	@Override
	public void onResume()
	{
		super.onResume();
		if (mMyLocationOverlay != null) mMyLocationOverlay.enableMyLocation();
//		ManagedOverlay managedOverlay = overlayManager.getOverlay("busstops");
//		
//		overlayManager.populate();
//		managedOverlay.invokeLazyLoad(0);
//		mvMap.invalidate();
	}
	
	@Override
	public void onPause()
	{
		 super.onPause();
		 if (mMyLocationOverlay != null) mMyLocationOverlay.disableMyLocation();
	}

	@Override
	protected boolean isRouteDisplayed()
	{
		// we're not interested in showing routes on our map so set this to false
		return false;
	}

	private class LoadDepartureDetailsThread extends Thread {
		ManagedOverlayItem item;
		BusTimesActivity context;
		
		LoadDepartureDetailsThread(BusTimesActivity context, ManagedOverlayItem item) {
            this.item = item;
            this.context = context;
        }
       
        public void run() 
        {	
			String busStopId = item.getTitle();
			long lastUpdate = db.getLastUpdate(busStopId);
			long now = new Date().getTime();
			
			if ((now - lastUpdate) > destinationCacheLength)
			{
				updateDepartures(busStopId);
				lastUpdate = new Date().getTime();
			}

			DeparturesCursor c = db.getDestinationsForBus(busStopId);

			BusStopsCursor stopCursor = db.getBusDetails(busStopId);
			
			StringBuffer desc = new StringBuffer();
			String title = stopCursor.getColTitle();
			if (title != null && !title.equals("")) title = "Bus Times for " + stopCursor.getColTitle();
			
			stopCursor.close();
			
			Common.info(this.getClass(), "Count is :" +c.getCount());
			for (int i = 0; i < c.getCount(); i++)
			{
				desc.append(c.getColService() + " " + c.getColDestination() + " " + c.getColDue()+ "\n");
				c.moveToNext();
			}
			
			if (c.getCount() == 0)
			{
				desc.append("Unable to obtain any bus departures for this stop");
			}
			else
			{
				desc.append("\n\nLast updated:"+new Date(lastUpdate));
			}
			
			c.close();
			
			context.dialogMessage =  desc.toString();
			context.dialogTitle =  title;
			context.mvMap.post(new Runnable() {
		        public void run() {
		        	context.showDialog(DIALOG_INFORMATION);
		        }
		      });
        }
	}
	
	private class LoadBusStopDetailsThread extends Thread {
		BusTimesActivity context;
       
        LoadBusStopDetailsThread(BusTimesActivity context) {
            this.context = context;
        }
       
        public void run() {
            try
            { 
				// populate database with json content
				Common.info(BusTimesActivity.class,"Populating Bus Stops from json");
				
				// load json data
				String base_url = context.getString(R.string.base_url);
				String jsonUrl = base_url + context.getString(R.string.busurl);
				
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
					context.dialogMessage =  "Unable to parse JSON file";
					context.mvMap.post(new Runnable() {
				        public void run() {
				        	context.showDialog(DIALOG_ERROR);
				        }
				      });
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
						
						// store variables in database for future reference
						db.addVariable("defaultLat",lat+"");
						db.addVariable("defaultLng",lng+"");
	
						// allow json object to be garbage collected
						json = null;
						
//						Debug.startMethodTracing("jsonmarkers");
						s = System.currentTimeMillis();
						JSONObject json_markers = Common.loadJSON(markerLocation);
						e = System.currentTimeMillis();
						Common.warn(BusTimesActivity.class,"JSON Marker file took " + (e-s) + "ms");						
//						Debug.stopMethodTracing();
						
						JSONArray markers = json_markers.getJSONArray("markers");
						
						int count = markers.length();
						
						Common.info(BusTimesActivity.class,"markers.length(): " + count);
						
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

	/* Creates the menu items */
	public boolean onCreateOptionsMenu(Menu menu) {
	    menu.add(0, MENU_RELOAD_STOP_INFO, 0, "Reload Bus Stop db");
	    menu.add(0, MENU_QUIT, 0, "Quit");
	    return true;
	}
	
	/* Handles item selections */
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case MENU_RELOAD_STOP_INFO:
	    	reloadBusStopInformation();
	        return true;
	    case MENU_QUIT:
	        quit();
	        return true;
	    }
	    return false;
	}

	private void reloadBusStopInformation()
	{
		dialog = ProgressDialog.show(BusTimesActivity.this, "", "Loading Bus Data. Please wait...", true);
		thread = new LoadBusStopDetailsThread(this);
		thread.start();
	}
	
	private void quit()
	{
		finish();
	}
	
	private void updateDepartures(String busStopId)
	{
		try
		{
			// delete all existing departures
			db.deleteAllDeparturesForStop(busStopId);
			
			String location = proxyUrl + busStopId;

			JSONObject json_departures = Common.loadJSON(location);
		
			String stopDesc = json_departures.getJSONObject("stop").getString("name");
			JSONArray departures = json_departures.getJSONArray("departures");
			
			Common.info(this.getClass(), "Length:"+departures.length());
			for (int i = 0; i < departures.length(); i++)
			{
				JSONObject json = departures.getJSONObject(i);
				
				db.addDeparture(busStopId,json.getString("service"), json.getString("due"), json.getString("destination"));
			}
			
			// update bus stop last update time & description
			db.updateBusStopDetails(busStopId,stopDesc);
			
		} catch (JSONException e)
		{
			e.printStackTrace();
		}
	}
	
	private void initVariables()
	{
		proxyUrl = db.getVariable("proxyurl");
		iconUrl = db.getVariable("iconUrl");

		try
		{
			String cacheLength = db.getVariable("destinationCacheLength");
			if (cacheLength != null)
			{
				destinationCacheLength = Integer.parseInt(cacheLength);
			}
			else
			{
				// load from strings xml file
				destinationCacheLength = Integer.parseInt(this.getString(R.string.destinationCacheLength));
				db.addVariable("destinationCacheLength",destinationCacheLength+"");
			}
			
			int lat = Integer.parseInt(db.getVariable("defaultLat"));
			Common.info(BusTimesActivity.class, "defaultLat:"+lat);
			int lng = Integer.parseInt(db.getVariable("defaultLng"));
			Common.info(BusTimesActivity.class, "defaultLng:"+lng);
			mc.animateTo(new GeoPoint(lat,lng));
		}
		catch (Exception e) { 
			e.printStackTrace();
		}
		
		Common.info(BusTimesActivity.class, "proxyUrl:"+proxyUrl);
		Common.info(BusTimesActivity.class, "iconUrl:"+iconUrl);
	}


    
    @Override
    protected Dialog onCreateDialog(int id) {
    	switch (id)
    	{
    		case DIALOG_ERROR:
    			return new AlertDialog.Builder(this)
    			.setPositiveButton("OK", null)
    			.setMessage(this.dialogMessage)
    			.create();
    		case DIALOG_INFORMATION:
    			return new AlertDialog.Builder(this)
    			.setIcon(R.drawable.icon)
    			.setTitle(dialogTitle)
    			.setPositiveButton("OK", null)
    			.setMessage(this.dialogMessage)
    			.create();
    	}
    	
    	return null;	
    }
}