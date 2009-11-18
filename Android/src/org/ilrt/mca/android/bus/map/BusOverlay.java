package org.ilrt.mca.android.bus.map;

import org.ilrt.mca.android.bus.BusTimesActivity;
import org.ilrt.mca.android.bus.db.BusStopsCursor;
import org.ilrt.mca.android.bus.db.BusTimesDatabase;

import android.graphics.drawable.Drawable;
import android.util.Log;

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
	
    /**
     * @param marker the push-pin
     */
    public BusOverlay(Drawable marker, BusTimesDatabase db, MyLocationOverlay mMyLocationOverlay) {
        super(marker);
        this.db = db;
        this.mMyLocationOverlay = mMyLocationOverlay;
        init = true;
        populate();
        init = false;
    }

    /**
     * @see com.google.android.maps.ItemizedOverlay#size()
     */
    @Override
    public int size() {
    	Log.i(BusOverlay.class.getName(),"asking for size");
    	GeoPoint currPos = null;
    	
    	if (!init) currPos = mMyLocationOverlay.getMyLocation();
    	if (currPos != null)
    	{
	    	Log.i(BusOverlay.class.getName(),(currPos != null ? currPos.toString() : "null"));
	    	Log.i(BusOverlay.class.getName(),"getLatSpanE6:"+currPos.getLatitudeE6());
	    	Log.i(BusOverlay.class.getName(),"getLonSpanE6:"+currPos.getLongitudeE6());
	    	int size = db.getBusStopCount((float)(currPos.getLatitudeE6()/1E6),(float)(currPos.getLongitudeE6()/1E6),(float)0.02,(float)0.02);
	    	Log.i(BusOverlay.class.getName(),"!! size is "+ size);
	    	return 0;
    	}
    	else return 0;
    }

    /**
     * @see com.google.android.maps.ItemizedOverlay#createItem(int)
     */
    @Override
    protected OverlayItem createItem(int i) {
    	BusStopsCursor c = db.getBusDetails(i);
    	String title = c.getColTitle();
    	String description = "Nothing here";
    	Double lat = c.getColLatitude()*1E6;
    	Double lon = c.getColLongitude()*1E6;
    	c.close();
    	return new OverlayItem(new GeoPoint(lat.intValue(), lon.intValue()), title, description);
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

        return true;
    }
}