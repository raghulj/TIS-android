package com.visualizeincode.model;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class AppDatabaseHelper {
	private static final String TAG = "AppDatabaseHelper";
	
	private final static String DB_NAME = "BTIS";
	private final static  int DB_VERSION = 1;

	private final static String CITY_TABLE = "CREATE TABLE city_list (id INTEGER PRIMARY KEY NOT NULL, name VARCHAR(100), latitude DOUBLE,longitude DOUBLE)";
	private final static String BUS_STOP_NAMES = "CREATE TABLE bus_stops (id INTEGER PRIMARY KEY, name VARCHAR(100), city_id INTEGER NOT NULL CONSTRAINT city_id REFERENCES city_list(id) ON DELETE CASCADE)";
	private final static String BUS_NUMBERS = "CREATE TABLE bus_number (id INTEGER PRIMARY KEY, bus_no VARCHAR(20), city_id INTEGER NOT NULL CONSTRAINT city_id REFERENCES city_list(id) ON DELETE CASCADE)";
	private final static String CITY_PLACES = "CREATE TABLE city_places(id INTEGER PRIMARY KEY NOT NULL, name VARCHAR(150),city_id INTEGER NOT NULL CONSTRAINT city_id REFERENCES city_list(id) ON DELETE CASCADE)";
	
	private final static String INSERT_CITY_NAME = "INSERT INTO city_list (name,latitude, longitude) values(?,0,0)";
	private final static String INSERT_BUS_STOP_NAME = "INSERT INTO bus_stops (name,city_id) values(?,?)";
	private final static String INSERT_BUS_NUMBERS = "INSERT INTO bus_number (bus_no,city_id) values(?,?)";
	private final static String INSERT_CITY_PLACE = "INSERT INTO city_places (name,city_id) values(?,?)";
	
	private OpenDBHelper appDBHelper;
	private SQLiteDatabase db;
	
	public AppDatabaseHelper(Context context){
		appDBHelper = new OpenDBHelper(context);
		db = appDBHelper.getWritableDatabase();
		
	}
	
	public void insertCityName(String cityName){
		db.execSQL("INSERT INTO city_list (name,latitude, longitude) values(\""+ cityName +"\",0,0)");
		
	}
	
	public void insertQuery(String query){
		db.execSQL(query);
	}
	

	
	public void insertCityPlace(String placeName, int cityId){
		db.execSQL("INSERT INTO city_places (name,city_id) values(\""+ placeName +"\","+ cityId +")");
	}
	
	public List<String> selectAllCityNames(){
		List<String> cityList = new ArrayList<String>();
		Cursor cur = db.query("city_list", new String[] {"name"}, null, null, null, null, null);
		if(cur.moveToFirst()){
			do{
				cityList.add(cur.getString(0));
				
			}while(cur.moveToNext());
		}
		if(cur != null && !cur.isClosed() ){
			cur.close();
		}
		return cityList;
	}
	
	public List<String> selectAllBusNumbers(int cityId) {
		List<String> busNoList = new ArrayList<String>();
		Cursor cur = db.query("bus_number", new String[] { "bus_no" }, "city_id = "+cityId,
				null, null, null, null);
		if (cur.moveToFirst()) {
			do {
				busNoList.add(cur.getString(0));

			} while (cur.moveToNext());
		}
		if (cur != null && !cur.isClosed()) {
			cur.close();
		}
		return busNoList;
	}
	
	public List<String> selectAllBusStops(int cityId) {
		List<String> busNoList = new ArrayList<String>();
		Cursor cur = db.query("bus_stops", new String[] { "name" }, "city_id = "+cityId,
				null, null, null, null);
		if (cur.moveToFirst()) {
			do {
				busNoList.add(cur.getString(0));

			} while (cur.moveToNext());
		}
		if (cur != null && !cur.isClosed()) {
			cur.close();
		}
		return busNoList;
	}
	
	public List<String> selectAllCityPlaces() {
		List<String> busNoList = new ArrayList<String>();
		Cursor cur = db.query("city_places", new String[] { "name" }, null,
				null, null, null, null);
		if (cur.moveToFirst()) {
			do {
				busNoList.add(cur.getString(0));

			} while (cur.moveToNext());
		}
		if (cur != null && !cur.isClosed()) {
			cur.close();
		}
		return busNoList;
	}
	
	public void closeDB(){
		db.close();
	}
	
	private static class OpenDBHelper extends  SQLiteOpenHelper{
		
		public OpenDBHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
			
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.d(TAG, "Creating tables ");
			db.execSQL(CITY_TABLE);
			db.execSQL(BUS_STOP_NAMES);
			db.execSQL(BUS_NUMBERS);
			db.execSQL(CITY_PLACES);
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.d(TAG, "Upgrading DB");
			db.execSQL("DROP TABLE IF EXISTS  city_list");
			db.execSQL("DROP TABLE IF EXISTS bus_stops");
			db.execSQL("DROP TABLE IF EXISTS bus_number");
			db.execSQL("DROP TABLE IF EXISTS city_places");
			onCreate(db);

		}
		
	}

}
