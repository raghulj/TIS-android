package com.visualizeincode.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.visualizeincode.R;
import com.visualizeincode.MapViewActivity;
import com.visualizeincode.Utils.AppConstants;
import com.visualizeincode.controller.AppController;

public class CitySelectionListActivity extends Activity {

	private static final String TAG = "CitySelectionListActivity";

	private static final String PREF_NAME = "BTIS";
	private ListView cityListView;

	private String[] CITY = { "Bangalore", "Chennai", "Delhi", "Pune",
			"Mysore", "Mumbai", "Kolkata", "Hyderabad", "Indore", "Ahmedabad" };
	private String[] MENUS = {"Traffic Status", "Traffic Cameras", "Buses", "Fines", "RTO" };

	private Context mContext;
	private AppController appController;
	private Button traffic_hotspot;
	private Button traffic_cameras;
	private Button traffic_buses;
	private Button traffic_fines;
	private Button traffic_rto;
	private Button traffic_directions;
	

	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		this.setContentView(R.layout.main_menu_layout);

		traffic_hotspot = (Button) findViewById(R.id.traffic_hotspot);
		traffic_cameras = (Button) findViewById(R.id.traffic_camera);
		traffic_buses = (Button) findViewById(R.id.bus_information);
		traffic_fines = (Button)findViewById(R.id.traffic_fines);
		traffic_rto = (Button)findViewById(R.id.traffic_rto);
		//traffic_directions = (Button)findViewById(R.id.directions);
		
//		cityListView = this.getListView();
//		
//
		mContext = this.getBaseContext();
		appController = AppController.getInstance(mContext);


		if (appController.isAppLaunched() == -1) {
			Log.d(TAG, "Storing valies for the first time");
			for (int i = 0; i < CITY.length; i++) {
				appController.getDBController().insertCityName(CITY[i]);
			}
			appController.setPreference(AppConstants.APP_LAUNCHED, 1);
		}

		if (appController.getDefaultCityId() == -1) {
			appController.setDefaultCityValues("Bangalore", 1);
		}
		Log.d(TAG, "City name is " + appController.getDefaultCity());
		Toast.makeText(getApplicationContext(),
				"Traffic information for city "+appController.getDefaultCity(), Toast.LENGTH_LONG).show();
		
//		if (appController.getDefaultCity() == null) {
//			ArrayAdapter<String> adapter = new ArrayAdapter(mContext,
//					R.layout.list_item, appController.getDBController().selectAllCityNames());
//			this.setListAdapter(adapter);
//		} else {
//			Log.d(TAG, "City name is " + appController.getDefaultCity());
//			Toast.makeText(getApplicationContext(),
//					"Selected city "+appController.getDefaultCity(), Toast.LENGTH_SHORT).show();
//			
//			loadCityMenu();
//		}

//		cityListView.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View view, int arg2,
//					long id) {
//				if(appController.getDefaultCityId() == -1){
//					appController.setDefaultCityValues(((TextView) view).getText()
//							.toString(), 1);
//					loadCityMenu();
//				}
//				
//				processClickEvents((int)id);
//		
//			}
//
//		});

		registerButtonClicks();

	}
	

	private void processClickEvents(int id){
		Intent intent;
		Bundle bdle = new Bundle();
		switch(id){
		case AppConstants.MENU_HOTSPOT:
			intent = new Intent(this,MapViewActivity.class);
		    bdle.putInt("MAP_OPTION", AppConstants.SHOW_HOTSPOT);
		    intent.putExtras(bdle);
			this.startActivity(intent);
			break;
		case AppConstants.MENU_CAMERA:
			intent = new Intent(this,MapViewActivity.class);
		    bdle.putInt("MAP_OPTION", AppConstants.SHOW_CAMERA);
		    intent.putExtras(bdle);
			this.startActivity(intent);
			break;
		case AppConstants.MENU_BUSES:
		    intent = new Intent(this,BusInformationActivity.class);
			this.startActivity(intent);
			break;
		case AppConstants.MENU_FINES:
		    intent = new Intent(this,VehicleDataActivity.class);
		    bdle.putString("OPTIONS", "FINES");
		    intent.putExtras(bdle);
			this.startActivity(intent);
			break;
		case AppConstants.MENU_RTO:
		    intent = new Intent(this,VehicleDataActivity.class);
		    bdle.putString("OPTIONS", "RTO");
		    intent.putExtras(bdle);
			this.startActivity(intent);
			break;
		}
	}


	private void loadCityMenu(){
//		ArrayAdapter<String> adapter = new ArrayAdapter(mContext,
//				R.layout.list_item, MENUS);
//		this.setListAdapter(adapter);
	}

	private void registerButtonClicks(){
		traffic_hotspot.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				processClickEvents(AppConstants.MENU_HOTSPOT);
			}
			
		});
		traffic_cameras.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				processClickEvents(AppConstants.MENU_CAMERA);
			}
			
		});
		traffic_buses.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				processClickEvents(AppConstants.MENU_BUSES);
			}
			
		});
		traffic_fines.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				processClickEvents(AppConstants.MENU_FINES);
			}
			
		});
		traffic_rto.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				processClickEvents(AppConstants.MENU_RTO);
			}
			
		});


	}

}
