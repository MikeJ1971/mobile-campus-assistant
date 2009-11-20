package org.ilrt.mca.android.bus.db;

import java.util.Date;

import org.ilrt.mca.android.bus.R;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BusTimesDatabase extends SQLiteOpenHelper
{
	/** The name of the database file on the file system */
    private static final String DATABASE_NAME = "BusTimes";
    /** The version of the database that this class understands. */
    private static final int DATABASE_VERSION = 4;
    
    public static final String BUS_TABLE_NAME = "busstops";
    public static final String DEPARTURE_TABLE_NAME = "departures";
    
    /** Keep track of context so that we can load SQL from string resources */
    private final Context mContext;
    
	public BusTimesDatabase(Context context)
	{
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
	}
	
	public BusStopsCursor getBusStops(BusStopsCursor.SortBy sortBy)
	{
		SQLiteDatabase d = getReadableDatabase();
		BusStopsCursor c = (BusStopsCursor) d.rawQueryWithFactory(
				new BusStopsCursor.Factory(),
				BusStopsCursor.queryAllBusStops(sortBy),
				null,
				null);
		
		c.moveToFirst();
		
		return c;
	}
	
	public DeparturesCursor getDestinationsForBus(String busStopId)
	{
		SQLiteDatabase d = getReadableDatabase();
		DeparturesCursor c = (DeparturesCursor) d.rawQueryWithFactory(
				new DeparturesCursor.Factory(),
				DeparturesCursor.constructQuery(busStopId),
				null,
				null);
		
		c.moveToFirst();
		
		return c;
	}
	
    public BusStopsCursor getBusDetails(String bus_id) {
    	SQLiteDatabase d = getReadableDatabase();
    	BusStopsCursor c = (BusStopsCursor) d.rawQueryWithFactory(
			new BusStopsCursor.Factory(),
			BusStopsCursor.querySpecificBusStop(bus_id),
			null,
			null);
    	c.moveToFirst();
        return c;
    }
    
    public BusStopsCursor getBusDetails(int i) {
    	SQLiteDatabase d = getReadableDatabase();
    	BusStopsCursor c = (BusStopsCursor) d.rawQueryWithFactory(
			new BusStopsCursor.Factory(),
			BusStopsCursor.querySpecificBusStop(i),
			null,
			null);
    	c.moveToFirst();
        return c;
    }
    
    public void removeAllBusStops()
    {
		try
		{
			getWritableDatabase().delete(BUS_TABLE_NAME, "", null);
		}
		catch (SQLiteException e)
		{
			Log.e("Error deleting all bus stops", e.toString());
		}
    }
    
	public void addBus(String id, String title, double lat, double lng)
	{
		ContentValues map = new ContentValues();
		map.put("stop_id", id);
		map.put("title", id);
		map.put("lat", lat);
		map.put("lng", lng);
		map.put("last_update", 0);
		
		try
		{
			getWritableDatabase().insert(BUS_TABLE_NAME, null, map);
		}
		catch (SQLException e)
		{
			Log.e("Error writing new bus to database",e.toString());
		}
	}
	
	public void addDeparture(String stop_id, String service, String due, String destination)
	{
		ContentValues map = new ContentValues();
		map.put("stop_id", stop_id);
		map.put("service", service);
		map.put("due", destination);
		map.put("last_update", (new Date()).getTime());
		
		try
		{
			getWritableDatabase().insert(DEPARTURE_TABLE_NAME, null, map);
			// update the busstop table to reflect the change
			updateBusStopAccessTime(stop_id);
		}
		catch (SQLException e)
		{
			Log.e("Error writing departure info to database",e.toString());
		}
	}
	
    
	/**
	 * Updates the busstop information with the current timestamp
	 * @param stop_id
	 */
	private void updateBusStopAccessTime(String stop_id)
	{
		ContentValues map = new ContentValues();
		map.put("last_update", (new Date()).getTime());
		String [] whereArgs = new String[]{stop_id};
		try
		{
			getWritableDatabase().update(BUS_TABLE_NAME, map, "stop_id=?",whereArgs);
		}
		catch (SQLException e)
		{
			Log.e("Error updating the busstop table",e.toString());
		}
	}
	
	
	private long getLastUpdate(String stop_id)
	{
		String [] cols = new String[]{"last_update"};
		String [] whereArgs = new String[]{stop_id};
		try
		{
			Cursor c = getWritableDatabase().query(BUS_TABLE_NAME, cols, "stop_id=?",whereArgs, null, null, null);
			return c.getLong(0);
		}
		catch (SQLException e)
		{
			Log.e("Error updating the busstop table",e.toString());
		}
		
		return 0;
	}
	
	public void deleteAllDeparturesForStop(String stop_id)
	{
		String [] whereArgs = new String[]{stop_id};
		
		try
		{
			getWritableDatabase().delete(DEPARTURE_TABLE_NAME, "stop_id=?", whereArgs);
		}
		catch (SQLiteException e)
		{
			Log.e("Error deleting departure information for stop " + stop_id, e.toString());
		}
	}
	
	public BusStopsCursor getBusStopsForRegion(double lat, double lng, double width, double height)
	{

    	SQLiteDatabase d = getReadableDatabase();
    	BusStopsCursor c = (BusStopsCursor) d.rawQueryWithFactory(
			new BusStopsCursor.Factory(),
			BusStopsCursor.queryBusStopInRegion(lat, lng, width, height),
			null,
			null);
    	c.moveToFirst();
        return c;
	}
	
	public int getBusStopCount()
	{
		return countTable(BUS_TABLE_NAME);
	}
	
	private int countTable(String tableName)
	{
		Cursor c = null;
        try {
            c = getReadableDatabase().rawQuery("SELECT count(*) FROM "+tableName, null);
            if (0 >= c.getCount()) { return 0; }
            c.moveToFirst();
            return c.getInt(0);
        }
        finally {
            if (null != c) {
                try { c.close(); }
                catch (SQLException e) { return 0; }
            }
        }
	}
	
	
    /** Called when it is time to create the database */
	@Override
	public void onCreate(SQLiteDatabase db) {
		String[] sql = mContext.getString(R.string.BusTimesDatabase_onCreate).split("\n");
		boolean success = execMultipleSQL(db, sql);
		if (!success) Log.e(BusTimesDatabase.class.getName(),"Unable to create database");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		String[] sql = mContext.getString(R.string.BusTimesDatabase_onUpgrade).split("\n");
		boolean success = execMultipleSQL(db, sql);
		onCreate(db);
		if (!success) Log.e(BusTimesDatabase.class.getName(),"Unable to upgrade the database");
	}
	
    /**
     * Execute all of the SQL statements in the String[] array
     * @param db The database on which to execute the statements
     * @param sql An array of SQL statements to execute
     * @return boolean indicating success state
     */
    private boolean execMultipleSQL(SQLiteDatabase db, String[] sql)
    {
    	boolean returnFlag = false;
    	
		db.beginTransaction();
		try {
	    	for( String s : sql )
	    	{
	    		if (s.trim().length()>0) db.execSQL(s);
	    	}
			db.setTransactionSuccessful();
			
			returnFlag = true;
		} catch (SQLException e) {
            Log.e("Error with sql statement", e.toString());
        } finally {
        	db.endTransaction();
        }
        
        return returnFlag;
    }
}
