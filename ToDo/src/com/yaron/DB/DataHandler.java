package com.yaron.DB;

//import java.sql.Date;
import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DataHandler {
	public enum OPEN_DB_FOR { READ, WRITE } 
	
	public static final String _MSG="msg";
	public static final String _DB_NAME="databasename";
	public static final String _TABLE_NAME="mytable";
	public static final int _DB_VERSION=2;
	public static final String _TABLE_CREATE="create table "+_TABLE_NAME+" (" +
			"ID integer primary key autoincrement, " +
			"msg text not null, d_date date not null, " +
			"finished integer not null, " +
			"geofence_address text not null"+
			");";
	
	private Context context;
	private DBHelper dbhelper;
	private SQLiteDatabase db;
	
	public DataHandler(Context context) {
		this.context=context;
		dbhelper = new DBHelper(context);
	}
	
	public DataHandler open(OPEN_DB_FOR open_for) {
		if(open_for.READ != null)
			db = dbhelper.getReadableDatabase();
		else
			db = dbhelper.getWritableDatabase();
		return(this);
	}
	
	public void close() {
		dbhelper.close();
	}
	
	/*public long insertData(String msg, Calendar d_date) {
		return(insertData(msg, d_date, ""));
	}*/
	
	public long insertData(String msg, Calendar d_date, String geofence_address) {
		ContentValues content = new ContentValues();
		long stamp = d_date.getTimeInMillis();
		content.put(_MSG, msg);
		content.put("d_date", stamp);
		content.put("finished", "0");
		content.put("geofence_address", geofence_address);
		return db.insert(_TABLE_NAME, null, content);
	}
	
	public boolean updateFinsh(int id, boolean finished) {
		ContentValues cv = new ContentValues();
		if(finished)
			cv.put("finished", 1);
		else
			cv.put("finished", 0);
		return(db.update(_TABLE_NAME, cv, "ID="+id, null)>0);
	}
	
	public boolean finishTask(int id, boolean is_finish) {
		ContentValues cv = new ContentValues();
		if(is_finish)
			cv.put("finished", 1);
		else
			cv.put("finished", 0);
		
		return(db.update(_TABLE_NAME, cv, "ID="+id, null)>0);
	}
	
	public boolean updateData(int id, String msg, Calendar d_date, boolean is_finish, String geofence_address) {
		ContentValues cv = new ContentValues();
		long stamp = d_date.getTimeInMillis();
		cv.put(_MSG, msg);
		cv.put("d_date", stamp);
		cv.put("geofence_address", geofence_address);
		if(is_finish)
			cv.put("finished", 1);
		else
			cv.put("finished", 0);
		
		return(db.update(_TABLE_NAME, cv, "ID="+id, null)>0);
	}
	
	public boolean deleteData(int id) {
		return(db.delete(_TABLE_NAME, "ID = "+id, null)>0);
	}
	
	public Cursor returnData(String query) {
		//return(db.query(_TABLE_NAME, new String[] {_NAME , _EMAIL}, null, null, null, null, null));
		return(db.rawQuery(query,null));
	}
	
	/*
	 * This function goods for: insert,update,delete.
	 * It will not return back anything (void)
	 */
	public void createSqlWithoutReturn(String query) {
		db.execSQL(query);
	}
	
	
	private static class DBHelper extends SQLiteOpenHelper {

		public DBHelper(Context context) {
			super(context, _DB_NAME, null, _DB_VERSION);
			
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			try {
				db.execSQL(_TABLE_CREATE);
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE "+_TABLE_NAME);
			onCreate(db);
		}
		
	}
}
