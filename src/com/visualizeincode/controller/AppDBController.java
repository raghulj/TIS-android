package com.visualizeincode.controller;

import java.util.List;

import android.content.Context;

import com.visualizeincode.model.AppDatabaseHelper;

public class AppDBController {

	private static Context mContext;
	private AppDatabaseHelper appDbHelper;
	
	public AppDBController(Context context){
		mContext = context;
		appDbHelper = new AppDatabaseHelper(mContext);
	}
	
	public AppDBController getDBInstance(){
		return this;
	}
	
	public void insertCityName(String cityName){
		appDbHelper.insertQuery("INSERT INTO city_list (name,latitude, longitude) values(\""+ cityName +"\",0,0)");
	}
	
	
	public List<String> selectAllCityNames(){
		return appDbHelper.selectAllCityNames();
	}
	
	public List<String> getAllBusNumbers(){
		return appDbHelper.selectAllBusNumbers(AppController.getInstance(mContext).getDefaultCityId());
	}
	
	public List<String> getAllBusLocations(){
		return appDbHelper.selectAllBusStops(AppController.getInstance(mContext).getDefaultCityId());
	}
	
	public void insertBusStopName(String busStop, int cityId){
		appDbHelper.insertQuery("INSERT INTO bus_stops (name,city_id) values(\""+ busStop +"\","+ cityId +")");
	}
	
	public void insertBusNumber(String busNumber, int cityId){
		appDbHelper.insertQuery("INSERT INTO bus_number (bus_no,city_id) values(\""+ busNumber +"\","+ cityId +")");
	}
	
}
