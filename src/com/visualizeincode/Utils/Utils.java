package com.visualizeincode.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class Utils {
	
	private static final String TAG = "Utils";

	
	public static String downloadContents(String url){
		try{
			final URL webURL = new URL(url);
			final BufferedReader reader = new BufferedReader(new InputStreamReader(webURL.openStream()));
			String line;
			StringBuffer sb = new StringBuffer();
			while((line = reader.readLine()) != null){
				sb.append(line);
			}
			Log.d(TAG,"Data in string is "+sb.toString());
			return sb.toString();
		}catch(MalformedURLException mUrl){
			Log.e(TAG,"Malformed URL Exception "+mUrl);
		}catch( IOException io){
			Log.e(TAG,"IOException URL Exception "+io);
		}
		return null;
	}
	
	public static HashMap parseCityURL(String cityURL){
		String data = downloadContents(cityURL);
		String index = ""; 
		String cityName = "";
		JSONObject cityObj;
		HashMap cityList = new HashMap();
		try{
			if(data == null)
				return null;
			
			Log.d(TAG,"String dataq is "+data);
			JSONObject jb = new JSONObject(data);
			JSONArray ar = jb.getJSONArray("cities");
			for(int i=0;i< ar.length();i++){
				cityObj = ar.getJSONObject(i);
				index = cityObj.getString("id");
				cityName = cityObj.getString("name");
				cityList.put(index, cityName);
			}
			return cityList;
		}catch(JSONException je){
			Log.e(TAG, "JSonException "+je);
		}
		
		return null;
	}
	
	
	
	
}
