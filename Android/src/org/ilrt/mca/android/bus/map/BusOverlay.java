package org.ilrt.mca.android.bus.map;

import java.util.ArrayList;

import org.ilrt.mca.android.bus.Common;
import org.ilrt.mca.android.bus.db.BusStopsCursor;
import org.ilrt.mca.android.bus.db.BusTimesDatabase;

import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;

public class BusOverlay extends ItemizedOverlay<OverlayItem>
{
	BusTimesDatabase db;
	MyLocationOverlay mMyLocationOverlay;
	GeoPoint currPos;
	boolean init = true;
	ArrayList<ArrayList<String>> stopIdMappingList;
	ArrayList<Integer> latitudeList;
	ArrayList<Integer> longitudeList;
	
    /**
     * @param marker the push-pin
     */
    public BusOverlay(Drawable marker, BusTimesDatabase db, MyLocationOverlay mMyLocationOverlay) throws Exception {
        super(boundCenter(marker));
    	
        this.db = db;
        this.mMyLocationOverlay = mMyLocationOverlay;
        init = true;
//        populate();
        init = false;
    }

    /**
     * @see com.google.android.maps.ItemizedOverlay#size()
     */
    @Override
    public int size() {
    	GeoPoint currPos = null;
    	
//    	if (!init) currPos = mMyLocationOverlay.getMyLocation();
    	if (currPos != null)
    	{
    		try
    		{
	    	Common.info(BusOverlay.class,(currPos != null ? currPos.toString() : "null"));
	    	
	    	int lat = currPos.getLatitudeE6();
	    	int lng = currPos.getLongitudeE6();
	    	int width = this.getLatSpanE6();
	    	int height = this.getLonSpanE6();
	    	
	    	Common.info(BusOverlay.class,"lat:"+lat);
	    	Common.info(BusOverlay.class,"lng:"+lng);
	    	    	
	    	BusStopsCursor c = db.getBusStopsForRegion(lat,lng,width,height);
	    	int count = c.getCount();
	    	
	    	Common.info(BusOverlay.class,"size:"+count);
	    	
	    	// reset arrays
	    	stopIdMappingList = new ArrayList<ArrayList<String>>(count);
	    	latitudeList = new ArrayList<Integer>(count);
	    	longitudeList = new ArrayList<Integer>(count);
	    	
	    	for (int rowNum = 0; rowNum < count; rowNum++)
	    	{
	    		c.moveToPosition(rowNum);
	    		ArrayList<String> item = new ArrayList<String>();
	    		item.add(c.getColStopId());
	    		item.add(c.getColTitle());
	    		stopIdMappingList.add(item);
	    		latitudeList.add((int)c.getColLatitude());
	    		longitudeList.add((int)c.getColLongitude());
	    	}
	    	c.close();

//	    	stopIdMappingList.trimToSize();
//	    	latitudeList.trimToSize();
//	    	longitudeList.trimToSize();
	    	
	    	Common.info(BusOverlay.class,"asking for size");
	    	
	    	return 0;
    		}
    		catch (Exception e)
    		{
    			e.printStackTrace();
    		}
    	}

		Common.info(BusOverlay.class,"size is 0");
		return 0;
    }

    /**
     * @see com.google.android.maps.ItemizedOverlay#createItem(int)
     */
    @Override
    protected OverlayItem createItem(int i) {
    	ArrayList<String> item = stopIdMappingList.get(i);
    	
    	//BusStopsCursor c = db.getBusDetails(i);
    	String title = item.get(0);
    	String description = item.get(1);
    	int lat = latitudeList.get(i);
    	int lon = longitudeList.get(i);

    	return new OverlayItem(new GeoPoint(lat, lon), title, description);
    }

    /**
     * React to tap events on Map by showing an appropriate detail activity
     *
     * @see com.google.android.maps.ItemizedOverlay#onTap(com.google.android.maps.GeoPoint, com.google.android.maps.MapView)
     */
    @Override
    public boolean onTap(GeoPoint p, MapView mvMap1) {
        long lat = p.getLatitudeE6();
        long lon = p.getLongitudeE6();
      
//        long rowid = -1;
//        JobsCursor c = db.getJobs(JobsCursor.SortBy.title);
//        for( int i=0; i<c.getCount(); i++){
//        	if (Math.abs(c.getColLatitude()-lat)<1000 && Math.abs(c.getColLongitude()-lon)<1000){
//        		rowid = c.getColJobsId();
//        		break;
//        	} else {
//        		c.moveToNext();
//        	}
//        }
//        
//        if (0 > rowid) { return false; }
//        
//        Bundle b = new Bundle();
//        b.putLong("_id", rowid);
//        Intent i = new Intent(MicroJobs.this, MicroJobsDetail.class);
//        i.putExtras(b);
//        startActivity(i);

        return false;
    }
    
    public void draw(android.graphics.Canvas canvas, MapView mapView, boolean shadow)
    {
    	try
    	{
	    	Common.info(BusOverlay.class,"Calling draw");
	    	super.draw(canvas, mapView, false);
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
    }
}