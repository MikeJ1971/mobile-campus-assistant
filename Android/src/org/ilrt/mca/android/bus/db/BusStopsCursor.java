package org.ilrt.mca.android.bus.db;


import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;

public class BusStopsCursor extends SQLiteCursor
{	
	static enum SortBy{
		title,
	}
	
	private static final String QUERY = 
		"SELECT _id, stop_id, title, lat, lng, last_update "+
	    "FROM " + BusTimesDatabase.BUS_TABLE_NAME + " ";

    private BusStopsCursor(SQLiteDatabase db, SQLiteCursorDriver driver, String editTable, SQLiteQuery query) {
		super(db, driver, editTable, query);
	}
    
	static class Factory implements SQLiteDatabase.CursorFactory{
		@Override
		public Cursor newCursor(SQLiteDatabase db, SQLiteCursorDriver driver, String editTable, SQLiteQuery query) {
			return new BusStopsCursor(db, driver, editTable, query);
		}
    }
    
    /** Generates the SQL string needed to query the database **/
    static String queryAllBusStops(SortBy sortBy)
    {
    	// TODO: use query parameter rather then hardcoded id
    	String sql = QUERY + " ORDER BY " + sortBy.toString();
    	return sql;
    }
    
    /** Generates the SQL string needed to query the database for a specific bus stop **/
    static String querySpecificBusStop(String stop_id)
    {
    	// TODO: use query parameter rather then hardcoded id
    	String sql = QUERY + " WHERE stop_id = \"" + stop_id + "\"";
    	return sql;
    }
    
    /** Generates the SQL string needed to query the database for a specific bus stop **/
    static String querySpecificBusStop(int i)
    {
    	String sql = QUERY + " LIMIT 1 OFFSET "+(i);
    	return sql;
    }
    
    
    
    public String getColStopId(){return getString(getColumnIndexOrThrow("stop_id"));}
	public String getColTitle(){return getString(getColumnIndexOrThrow("title"));}
	public long getColLastUpdate(){return getLong(getColumnIndexOrThrow("last_update"));}
	public float getColLatitude(){return getFloat(getColumnIndexOrThrow("lat"));}
	public float getColLongitude(){return getFloat(getColumnIndexOrThrow("lng"));}
}	