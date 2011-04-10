package com.visualizeincode.controller;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;
import com.visualizeincode.Utils.AppConstants;

public class AppController {

	private static final String TAG = "AppController";
	private static Context mContext;
	private SharedPreferences appPreference;
	private static final String PREF_NAME = "BTIS";
	private String defaultCity;
	private int defaultCityId;
	private AppDBController appDBController;
	private static AppController appController = null;
	
	
	private  AppController(Context context){
		mContext = context;
		appDBController = new AppDBController(mContext);
		appPreference = context.getSharedPreferences(PREF_NAME,0);
		loadPreference();
		
	}
	
	public synchronized static AppController getInstance(Context context){
		if(appController == null){
			appController = new AppController(context);
		}
		return appController;
	}
	
	
	public AppDBController getDBController(){
		return appDBController;
	}
	
	public Context getContext(){
		return mContext;
	}
	
	public String getDefaultCity(){
		return defaultCity;
	}
	
	public int getDefaultCityId(){
		return defaultCityId;
	}
	
	public int isAppLaunched(){
		return appPreference.getInt("LAUNCHED", -1);
	}
	
	public void setPreference(String key, String value){
		SharedPreferences.Editor editor = appPreference.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	public void setPreference(String key, int value){
		SharedPreferences.Editor editor = appPreference.edit();
		editor.putInt(key, value);
		editor.commit();
	}
	
	public void setDefaultCityValues(String cityName, int cityId){
		SharedPreferences.Editor editor = appPreference.edit();
		editor.putString(AppConstants.DEFAULT_CITY_NAME, cityName);
		editor.putInt(AppConstants.DEFAULT_CITY_ID, cityId);
		editor.commit();
		loadPreference();
	}
	
	private ArrayList<OverlayItem> busStopOverlayItems = new ArrayList<OverlayItem>();
	private ArrayList<OverlayItem> cameraOverlayItems = new ArrayList<OverlayItem>();
	private ArrayList<OverlayItem> highHotSpotOverlayItems = new ArrayList<OverlayItem>();
	private ArrayList<OverlayItem> mediumHotSpotOverlayItems = new ArrayList<OverlayItem>();
	private ArrayList<OverlayItem> smoothHotSpotOverlayItems = new ArrayList<OverlayItem>();


	public ArrayList<OverlayItem> getBusStopOverlayItems(){
		return busStopOverlayItems;
	}
	
	public void setBusStopOverlayItem(OverlayItem overlay){
		busStopOverlayItems.add(overlay);
	}
	
	public void clearBusStopOverlayItem(){
		busStopOverlayItems.clear();
	}
	
	public ArrayList<OverlayItem> getCameraOverlayItems(){
		return cameraOverlayItems;
	}
	
	public void setCameraOverlayItem(OverlayItem overlay){
		cameraOverlayItems.add(overlay);
	}
	
	public void clearCameraOverlayItem(){
		cameraOverlayItems.clear();
	}
	public ArrayList<OverlayItem> getHighHotSpotOverlayItems(){
		return highHotSpotOverlayItems;
	}
	
	public void setHighHotSpotOverlayItem(OverlayItem overlay){
		highHotSpotOverlayItems.add(overlay);
	}
	
	public void clearHighHotSpotOverlayItem(){
		highHotSpotOverlayItems.clear();
	}
	public ArrayList<OverlayItem> getMediumHotSpotOverlayItems(){
		return mediumHotSpotOverlayItems;
	}
	
	public void setMediumHotSpotOverlayItem(OverlayItem overlay){
		mediumHotSpotOverlayItems.add(overlay);
	}
	
	public void clearMediumHotSpotOverlayItem(){
		mediumHotSpotOverlayItems.clear();
	}
	public ArrayList<OverlayItem> getSmoothHotSpotOverlayItems(){
		return smoothHotSpotOverlayItems;
	}
	
	public void setSmoothHotSpotOverlayItem(OverlayItem overlay){
		smoothHotSpotOverlayItems.add(overlay);
	}
	
	public void clearSmoothHotSpotOverlayItem(){
		smoothHotSpotOverlayItems.clear();
	}
	
	public String getPreference(String key){
		return appPreference.getString(key, "");
	}
	
	public int getPreferenceValue(String key){
		return appPreference.getInt(key, -1);
	}
	
	private void loadPreference(){
		defaultCity = appPreference.getString(AppConstants.DEFAULT_CITY_NAME, null);
		defaultCityId = appPreference.getInt(AppConstants.DEFAULT_CITY_ID, -1);
	}
	
	public void parseCameraInformation(String data){

		try{
			final int cameraStart = data.indexOf("\");load_cameras");
			final int cameraEnd = data.indexOf("\");show_road_congestion");
			final String cameraPosistions = data
					.substring(cameraStart, cameraEnd);

			String arr[] = cameraPosistions.split("load_cameras\\(\"");
			JSONObject cameraObj = new JSONObject(arr[1].replace("\\", ""));

			JSONObject camer = cameraObj.getJSONObject("cams");
			JSONArray cameras = camer.names();

			for (int cam = 0; cam < cameras.length(); cam++) {
				
				JSONObject obj = camer.getJSONObject(cameras.get(cam).toString());
				Log.d(TAG,obj.getString("lat"));
				if (!obj.getString("lat").equals("null")
						&& !obj.getString("lon").equals("null")) {
					GeoPoint point = new GeoPoint(
							(int) (Float.parseFloat(obj.getString("lat")) * 1E6),
							(int) (Float.parseFloat(obj.getString("lon")) * 1E6));
					OverlayItem overlayitem = new OverlayItem(point,
							obj.getString("label"), obj.getString("id"));
					setCameraOverlayItem(overlayitem);
				}
			}
			Log.d(TAG,"Al cameras loaded");

		}catch(Exception e){
			Log.e(TAG, "Error in parsing camer information "+e);
		}
	}
	
	public void parseHotSpotInformation(String data){
		try{
		JSONObject hotObj = new JSONObject(data);
		JSONArray jarr = hotObj.getJSONArray("locations");
		for (int i = 0; i < jarr.length(); i++) {
			JSONObject js = jarr.getJSONObject(i);
			GeoPoint point = new GeoPoint((int) (Float.parseFloat(js
					.getString("lat")) * 1E6), (int) (Float.parseFloat(js
					.getString("lon")) * 1E6));
			OverlayItem overlayitem = new OverlayItem(point,
					js.getString("label"), js.getString("status")+" traffic");
			if (js.getString("status").equals("Delay")) {
				setHighHotSpotOverlayItem(overlayitem);
			} else if (js.getString("status").equals("Slow")) {
				setMediumHotSpotOverlayItem(overlayitem);
			} else if (js.getString("status").equals("Smooth")) {
				setSmoothHotSpotOverlayItem(overlayitem);
			}
		}
		}catch(Exception e){
			Log.e(TAG, "Error in parsing hot spot information "+e);
		}
	}
	

}
