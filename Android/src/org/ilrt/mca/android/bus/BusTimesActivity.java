package org.ilrt.mca.android.bus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.ilrt.mca.android.bus.db.BusStopsCursor;
import org.ilrt.mca.android.bus.db.BusTimesDatabase;
import org.ilrt.mca.android.bus.db.DeparturesCursor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
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
	// static menu indicators
	private static final int MENU_QUIT = 0;
	private static final int MENU_RELOAD_STOP_INFO = 1;
	private static final int MENU_RELOAD_MAP = 2;
	
	// static dialog indicators
	public static final int DIALOG_ERROR = 0;
	public static final int DIALOG_INFORMATION = 1;
	public static final int DIALOG_FIRST_LOAD = 2;
	
	// overlay name
	private static final String OVERLAY_NAME = "busstops";
	
	// debug helper override variables
	private static boolean forceWelcomeScreen = true;
	private static boolean forceReload = false;
	
	// address of the service to obtain depature information
	private String proxyUrl = "";

	// google map ref
	private MapView mvMap;
	
	// database wrapper
	private BusTimesDatabase db;
	
	// handle to class to enable positioning
	private MyLocationOverlay mMyLocationOverlay;
	
	// controller for moving map around
	private MapController mc;
	
	// manager to handle our bus stop overlay
	private OverlayManager overlayManager;
	
	// progress indicator reference
	public ProgressDialog dialog;
	
	// thread to process populating database from mca server
	private LoadBusStopDetailsThread thread;

	// 'disabled' bus stop map marker
	private Drawable disabledmarker; 

    // the maximum number of real markers we will show on screen before marging stop icons together
	private int maxMarkers;
    
    // represents (in ms) how long the departure information is cached before reloading from url
	private int destinationCacheLength = 0;
	
	// dialog-specific variables (used between threads)
    public String dialogMessage;
    public String dialogTitle;
    
    // link to departure cursor so that we can access this information between threads
    public DeparturesCursor departures;
    
    // format to display the last bus stop update time
    SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
	
	/** 
	 * Called when the activity is first created. 
	 */
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
		
		boolean firstLoad = initFirstLoad();
		
		// load bus stop data if required
		if (!firstLoad && (db.getBusStopCount() == 0 || forceReload))
		{
			reloadBusStopInformation();
		} // END if (db.getBusStopCount() == 0 || this.getString(R.string.forcereload).equalsIgnoreCase("true"))	
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		Drawable defaultmarker = getResources().getDrawable(R.drawable.bus);
	    disabledmarker = getResources().getDrawable(R.drawable.bus_unknown);
	    
	    defaultmarker.setBounds(0,0,defaultmarker.getIntrinsicWidth(),defaultmarker.getIntrinsicHeight());
	    disabledmarker.setBounds(0,0,disabledmarker.getIntrinsicWidth(),disabledmarker.getIntrinsicHeight());

	    ManagedOverlay managedOverlay = overlayManager.createOverlay(OVERLAY_NAME,defaultmarker);

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
				Common.info(getClass(), "onScrolled");
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
				Common.info(getClass(), "runOnFirstFix");
				mc.animateTo(mMyLocationOverlay.getMyLocation());
				mc.setZoom(17);
			}
		});
		
		Common.info(getClass(), "onStartFinished");
	}

	@Override
	public void onResume()
	{
		Common.info(getClass(), "onResume");
		
		super.onResume();
		if (mMyLocationOverlay != null) mMyLocationOverlay.enableMyLocation();
    	ManagedOverlay managedOverlay = overlayManager.getOverlay(OVERLAY_NAME);
        managedOverlay.invokeLazyLoad(500);
	}
	
	@Override
	public void onPause()
	{
		 super.onPause();
		 if (mMyLocationOverlay != null) mMyLocationOverlay.disableMyLocation();
	}

	/*
	 * Required to tell the app that we will not bother drawing routes
	 * @see com.google.android.maps.MapActivity#isRouteDisplayed()
	 */
	@Override
	protected boolean isRouteDisplayed()
	{
		// we're not interested in showing routes on our map so set this to false
		return false;
	}

	/**
	 * This function checks if we're loading the app for the first time (or after a new version update)
	 * In which case it loads up a dialog and returns <code>true</code>, <code>false</code> otherwise;
	 * @return
	 */
	private boolean initFirstLoad()
	{
		try
		{
			PackageInfo pi = this.getPackageManager().getPackageInfo("org.ilrt.mca.android.bus", 0);
			
			String version = db.getVariable("version");
			
			Common.warn(getClass(), "Version is " + pi.versionCode);
			
			if (version == null || Integer.parseInt(version) < pi.versionCode || forceWelcomeScreen)
			{
				db.addVariable("version", pi.versionCode+"");

				// show information
				showDialog(DIALOG_FIRST_LOAD);
				
				return true;
			}
		} catch (NameNotFoundException e)
		{
			Common.warn(getClass(), "Unable to find package information", e);
		}
		
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
    		context.dialog = ProgressDialog.show(BusTimesActivity.this, "", "Fetching...", true);

			String busStopId = item.getTitle();
			long lastUpdate = db.getLastUpdate(busStopId);
			long now = new Date().getTime();
			
			if ((now - lastUpdate) > destinationCacheLength)
			{
				Common.info(this.getClass(), (now - lastUpdate) + "ms is larger then default " + destinationCacheLength + "ms, fetching new times");
				updateDepartures(busStopId);
				lastUpdate = new Date().getTime();
			}

			departures = db.getDestinationsForBus(busStopId);

			BusStopsCursor stopCursor = db.getBusDetails(busStopId);
			
			StringBuffer desc = new StringBuffer();
			String title = stopCursor.getColTitle();
			if (title != null && !title.equals("")) title = "Bus Times for " + stopCursor.getColTitle();
			
			stopCursor.close();
			
			Common.info(this.getClass(), "Count is :" +departures.getCount());
			
			Date d = new Date(lastUpdate);
			
			context.dialogMessage = "Last updated at " +sdf.format(d);
			context.dialogTitle =  title;
			context.mvMap.post(new Runnable() {
		        public void run() {
		        	context.dialog.dismiss();
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
				
				long s = System.currentTimeMillis();
				JSONObject json = Common.loadJSON(jsonUrl);
				long e = System.currentTimeMillis();
				Common.warn(BusTimesActivity.class,"Init JSON file took " + (e-s) + "ms");
				
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
						String markerLocation = base_url + json.getString("markersLocation");
						
						// navigate to default pos
						int lat = (int)(Float.parseFloat(json.getString("latitude"))*1E6);
						int lng = (int)(Float.parseFloat(json.getString("longitude"))*1E6);
						
						// store variables in database for future reference
						db.addVariable("defaultLat",lat+"");
						db.addVariable("defaultLng",lng+"");
						db.addVariable("proxyurl",proxyUrl);

						// as we're storing variables in the destination, do the same with cache length
						destinationCacheLength = Integer.parseInt(BusTimesActivity.this.getString(R.string.destinationCacheLength));
						db.addVariable("destinationCacheLength",destinationCacheLength+"");
	
						// allow json object to be garbage collected
						json = null;

						s = System.currentTimeMillis();
						JSONObject json_markers = Common.loadJSON(markerLocation);
						e = System.currentTimeMillis();
						Common.warn(BusTimesActivity.class,"JSON Marker file took " + (e-s) + "ms");						

						
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
	    menu.add(0, MENU_RELOAD_STOP_INFO, 0, "Reload Bus Stop db").setIcon(android.R.drawable.ic_menu_rotate);
	    menu.add(0, MENU_QUIT, 0, "Quit").setIcon(android.R.drawable.ic_lock_power_off);
	    menu.add(0, MENU_RELOAD_MAP, 0, "Redraw");
	    return true;
	}
	
	/* Handles menu item selections */
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case MENU_RELOAD_STOP_INFO:
	    	reloadBusStopInformation();
	        return true;
	    case MENU_QUIT:
	        quit();
	        return true;
	    case MENU_RELOAD_MAP:
	    	Common.info(getClass(),"Lazy loading");
	    	ManagedOverlay managedOverlay = overlayManager.getOverlay(OVERLAY_NAME);
	        managedOverlay.invokeLazyLoad(0);
	        return true;
	    }
	    return false;
	}
	
	public void reloadBusStopInformation()
	{
		dialog = ProgressDialog.show(BusTimesActivity.this, "", "Loading Bus Data. Please wait...", true);
		thread = new LoadBusStopDetailsThread(this);
		thread.start();
	}
	
	/**
	 * Close the application down
	 */
	private void quit()
	{
		mMyLocationOverlay.disableMyLocation();
		if (thread != null) thread.destroy();
		thread = null;
		
		if (dialog != null) dialog.dismiss();
		dialog = null;
		
		finish();
		
		// free resources
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
				
				Common.info(getClass(),"Adding "+json.getString("service") +","+ json.getString("due") +","+ json.getString("destination"));
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
			int lng = Integer.parseInt(db.getVariable("defaultLng"));
			mc.animateTo(new GeoPoint(lat,lng));
		}
		catch (Exception e) { 
			e.printStackTrace();
		}
		
		Common.info(BusTimesActivity.class, "proxyUrl:"+proxyUrl);
	}

	/**
	 * Render specific content in dialogs before being shown
	 */
	@Override
	protected void onPrepareDialog(int id, Dialog dialog)
	{
    	if (id == DIALOG_INFORMATION)
    	{   		
    		TableLayout tl = (TableLayout) dialog.findViewById(R.id.DepartureTable);
    		tl.removeAllViews();
    		
    		Common.info(this.getClass(), "Departure count is :" +departures.getCount());
    		
    		if (departures.getCount() > 0)
    		{
	    		// create header
	            TableRow header = new TableRow(tl.getContext());
	            header.setId(50);
	            header.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT,TableRow.LayoutParams.WRAP_CONTENT));   
	            header.setPadding(0, 0, 0, 10);
	            
	            // Create a TextView to house the service name
	            TextView serviceHeader = new TextView(header.getContext());
	            serviceHeader.setId(51);
	            serviceHeader.setText("Service");
	            serviceHeader.setGravity(Gravity.CENTER_HORIZONTAL);
	            serviceHeader.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT,TableRow.LayoutParams.WRAP_CONTENT));
	            header.addView(serviceHeader);
	
	            // Create a TextView to house the value of the destination info
	            TextView destHeader = new TextView(header.getContext());
	            destHeader.setId(52);
	            destHeader.setText("Final Destination");
	            destHeader.setGravity(Gravity.CENTER_HORIZONTAL);
	            destHeader.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
	            header.addView(destHeader);
	
	            // Create a TextView to house the due time
	            TextView dueHeader = new TextView(header.getContext());
	            dueHeader.setId(53);
	            dueHeader.setText("Due");
	            dueHeader.setGravity(Gravity.CENTER_HORIZONTAL);
	            dueHeader.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
	            header.addView(dueHeader);
	            
	            // Add the TableRow to the TableLayout
	            tl.addView(header,new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
	    		
	    		boolean oddRow = false;
	    		// Go through each item in the array
	            for (int i = 0; i < departures.getCount(); i++)
	            {
	            	Common.info(this.getClass(), "Adding:" +departures.getColDue());
	                // Create a TableRow and give it an ID
	                TableRow tr = new TableRow(tl.getContext());
	                tr.setId(100+i);
	                tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT,TableRow.LayoutParams.WRAP_CONTENT));   
	                if (oddRow) tr.setBackgroundColor(Color.argb(120, 50, 50, 50));
	                oddRow = !oddRow;
	                
	                // Create a TextView to house the service name
	                TextView serviceId = new TextView(tr.getContext());
	                serviceId.setId(200+i);
	                serviceId.setText(departures.getColService());
	                serviceId.setGravity(Gravity.CENTER_HORIZONTAL);
	                serviceId.setTextColor(Color.WHITE);
	                serviceId.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT,TableRow.LayoutParams.WRAP_CONTENT));
	                tr.addView(serviceId);
	
	                // Create a TextView to house the value of the destination info
	                TextView destView = new TextView(tr.getContext());
	                destView.setId(300+i);
	                destView.setText(departures.getColDestination());
	                destView.setGravity(Gravity.CENTER_HORIZONTAL);
	                destView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
	                tr.addView(destView);
	
	                // Create a TextView to house the due time
	                TextView dueView = new TextView(tr.getContext());
	                dueView.setId(400+i);
	                dueView.setText(departures.getColDue());
	                dueView.setGravity(Gravity.CENTER_HORIZONTAL);
	                dueView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
	                tr.addView(dueView);
	                
	                // Add the TableRow to the TableLayout
	                tl.addView(tr,new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
	
	                departures.moveToNext();
	            }
    		} // if (departures.getCount() > 0)
    		else
    		{
    			this.dialogMessage = "No information for this stop";
    		}
            departures.close();
            
    		dialog.setTitle(this.dialogTitle);
    		TextView ta = (TextView) dialog.findViewById(R.id.TopText);
    		ta.setText(this.dialogMessage);            
    	}		
	}
	
	/**
	 * Create the various dialogs this application will use
	 */
    @Override
    protected Dialog onCreateDialog(int id) {
    	Dialog dialog = null;
    	Button btn;
    	
    	switch (id)
    	{
    		case DIALOG_ERROR:
    			return new AlertDialog.Builder(this)
    			.setPositiveButton("OK", null)
    			.setMessage(this.dialogMessage)
    			.create();
    		case DIALOG_INFORMATION:
    			dialog = new Dialog(BusTimesActivity.this); 
				dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
				dialog.setContentView(R.layout.departuredialog); 
				dialog.getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,R.drawable.icon);

    			btn = (Button) dialog.findViewById(R.id.OkButton);
    			btn.setOnClickListener(new OKListener(dialog));

    			break;
    		case DIALOG_FIRST_LOAD:
				dialog = new Dialog(BusTimesActivity.this); 
				
				// request that we want to set an icon
				dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);

				// need to set content view before drawable resource
				dialog.setContentView(R.layout.welcome); 

				dialog.getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,R.drawable.icon);

				dialog.setTitle(BusTimesActivity.this.getString(R.string.app_name));
				
				// set the version number
				TextView ta = (TextView) dialog.findViewById(R.id.current_version);
				String version = "0";
				PackageInfo pi;
				try
				{
					pi = this.getPackageManager().getPackageInfo("org.ilrt.mca.android.bus", 0);
					version = pi.versionName;
				} catch (NameNotFoundException e) {}
	    		ta.setText("Version: "+version);
	    		
				btn = (Button) dialog.findViewById(R.id.OkButton);
				btn.setOnClickListener(new InitLoadListener(dialog,BusTimesActivity.this));
				
				break;
    	}
    	
    	return dialog;	
    }
    
    protected class OKListener implements View.OnClickListener { 

        private Dialog dialog; 

        public OKListener(Dialog dialog) { 
             this.dialog = dialog; 
        } 

        public void onClick(View v) { 
             dialog.dismiss();
        } 
   }
    
    protected class InitLoadListener implements View.OnClickListener { 

        private Dialog dialog; 
        private BusTimesActivity activity;

        public InitLoadListener(Dialog dialog, BusTimesActivity activity) { 
             this.dialog = dialog; 
             this.activity = activity;
        } 

        public void onClick(View v) { 
             dialog.dismiss();
             if (db.getBusStopCount() == 0 || forceReload)
             {
            	 activity.reloadBusStopInformation();
             }
        } 
   }
}