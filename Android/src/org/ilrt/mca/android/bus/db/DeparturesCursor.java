package org.ilrt.mca.android.bus.db;


import org.ilrt.mca.android.bus.Common;

import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;

public class DeparturesCursor extends SQLiteCursor
{
	
	private static final String QUERY = 
		"SELECT _id, stop_id, service, due, destination, last_update "+
	    "FROM " + BusTimesDatabase.DEPARTURE_TABLE_NAME + " "+
	    "WHERE stop_id = ";
	
    private DeparturesCursor(SQLiteDatabase db, SQLiteCursorDriver driver, String editTable, SQLiteQuery query) {
		super(db, driver, editTable, query);
	}
    
    static class Factory implements SQLiteDatabase.CursorFactory{
		public Cursor newCursor(SQLiteDatabase db, SQLiteCursorDriver driver, String editTable, SQLiteQuery query) {
			return new DeparturesCursor(db, driver, editTable, query);
		}
    }
    
    /** Generates the SQL string needed to query the database **/
    static String constructQuery(String busStopId)
    {
    	// TODO: use query parameter rather then hardcoded id
    	String sql = DeparturesCursor.QUERY + "\"" + busStopId + "\" " +
	    "ORDER BY _id";
    	return sql;
    }
    
    public String getColStopId(){return getString(getColumnIndexOrThrow("stop_id"));}
    public String getColService(){return getString(getColumnIndexOrThrow("service"));}
    public String getColDue(){return getString(getColumnIndexOrThrow("due"));}
    public String getColDestination(){return getString(getColumnIndexOrThrow("destination"));}
    public Long getColLastUopdate(){return getLong(getColumnIndexOrThrow("last_update"));}
}	
