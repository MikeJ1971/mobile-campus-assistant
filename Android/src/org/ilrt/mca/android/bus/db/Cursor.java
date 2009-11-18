package org.ilrt.mca.android.bus.db;

import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

abstract class Cursor extends SQLiteCursor
{    
	public Cursor(SQLiteDatabase db, SQLiteCursorDriver driver, String editTable, SQLiteQuery query)
	{
		super(db, driver, editTable, query);
	}
    
    abstract String constructQuery();
}
