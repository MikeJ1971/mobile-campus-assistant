package org.ilrt.mca.android.bus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mvMap = (MapView) findViewById(R.id.mapmain);
        
        final MapController mc = mvMap.getController();
        
        mMyLocationOverlay = new MyLocationOverlay(this, mvMap);
        mMyLocationOverlay.runOnFirstFix(
        		new Runnable() {
        			public void run()
        			{
        				mc.animateTo(mMyLocationOverlay.getMyLocation());
        				mc.setZoom(16);
        			}
        		});
        
        Button btnNavigateToMe = (Button) findViewById(R.id.btnNavigateHere);
        btnNavigateToMe.setOnClickListener(new Button.OnClickListener() {
        	public void onClick(View v)
        	{
        		try
        		{
        			mc.animateTo(mMyLocationOverlay.getMyLocation());
        			mvMap.invalidate();
        		}
        		catch (Exception e)
        		{
        			Log.i("BusTimes","Unable to animate map",e);
        		}
        	}
        });
        
        // Load a hashmap with location and positions
        List <String> lsLocations = new ArrayList<String>();
        final HashMap<String, GeoPoint> hmLocations = new HashMap<String, GeoPoint>();
        hmLocations.put("Current Location", new GeoPoint((int) latitute, (int) longitude));
        lsLocations.add("Current Location");
        
        //worker = db.getWorker();
        
        
        
    }

	@Override
	protected boolean isRouteDisplayed()
	{
		// TODO Auto-generated method stub
		return false;
	}
}