package com.visualizeincode.views;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;
import com.visualizeincode.R;
import com.visualizeincode.MapViewActivity;
import com.visualizeincode.Utils.AppConstants;
import com.visualizeincode.Utils.HTTPConnect;
import com.visualizeincode.controller.AppController;

public class BusInformationActivity extends Activity {

	private static final String TAG = "BusInformationActivity";
	// private AutoCompleteTextView fromPoint;
	// private AutoCompleteTextView toPoint;
	// private TextView fromText;
	// private TextView toText;
	// private RadioGroup busSelection;
	private AutoCompleteTextView busNumber;
	// private RadioButton busNumButton;
	// private RadioButton busLocButton;
	private Button submitButton;

	private LinearLayout busNumberLayout;
	// private LinearLayout busLocationLayout;
	private ProgressDialog progressDialog;
	private AppController appController;
	private Context mContext;

	private boolean isBusNumberSelected = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bus_layout);
		mContext = this.getApplicationContext();

		busNumberLayout = (LinearLayout) findViewById(R.id.BusNumberLayout);
		// busLocationLayout = (LinearLayout)
		// findViewById(R.id.BusLocationLayout);
		//
		// fromPoint = (AutoCompleteTextView) findViewById(R.id.fromPoint);
		// toPoint = (AutoCompleteTextView) findViewById(R.id.toPoint);
		busNumber = (AutoCompleteTextView) findViewById(R.id.bus_number);

		// busSelection = (RadioGroup) findViewById(R.id.radioGroup1);
		// busNumButton = (RadioButton) findViewById(R.id.bus_num);
		// busLocButton = (RadioButton) findViewById(R.id.bus_loc);

		submitButton = (Button) findViewById(R.id.bus_submit);

		// busNumButton.setChecked(true);
		// busLocationLayout.setVisibility(LinearLayout.INVISIBLE);

		appController = AppController.getInstance(getApplicationContext());

		// busNumButton.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// isBusNumberSelected = true;
		// busNumberLayout.setVisibility(LinearLayout.VISIBLE);
		// busLocationLayout.setVisibility(LinearLayout.INVISIBLE);
		// }
		// });
		//
		// busLocButton.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// isBusNumberSelected = false;
		// busNumberLayout.setVisibility(LinearLayout.INVISIBLE);
		// busLocationLayout.setVisibility(LinearLayout.VISIBLE);
		// }
		// });

		submitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d(TAG, "click event");

				if (busNumber.getText().toString().length() != 0) {
					if (isBusNumberSelected) {
						Log.d(TAG, "bus no selected");
						getBusNumberInfo(busNumber.getText().toString());
					} else {
						// getBusNumberInfo();
					}
				} else {
					Toast.makeText(getApplicationContext(),
							"Please enter the bus number", Toast.LENGTH_SHORT)
							.show();
				}
			}

		});

	}

	@Override
	protected void onResume() {
		super.onResume();

		List<String> busNumbers = appController.getDBController()
				.getAllBusNumbers();
		if (busNumbers.size() == 0) {
			mHandler.sendEmptyMessage(0);
			BusInformationDownloaderTask vv = new BusInformationDownloaderTask(
					AppConstants.BUS_URL);
			vv.execute("INITIALDOWNLOAD");
		}
		Log.d(TAG, "Bs list size " + busNumbers.size());
		ArrayAdapter<String> adapter = new ArrayAdapter(this,
				android.R.layout.simple_dropdown_item_1line, busNumbers);
		busNumber.setAdapter(adapter);
		// List<String> loc =
		// appController.getDBController().getAllBusLocations();
		// Log.d(TAG, "Bs list size " + loc.size());

		// ArrayAdapter<String> locations = new ArrayAdapter(this,
		// android.R.layout.simple_dropdown_item_1line, loc);
		// fromPoint.setAdapter(locations);
		// toPoint.setAdapter(locations);

	}

	private void loadBusList(){
		List<String> busNumbers = appController.getDBController()
		.getAllBusNumbers();
		ArrayAdapter<String> adapter = new ArrayAdapter(this,
				android.R.layout.simple_dropdown_item_1line, busNumbers);
		busNumber.setAdapter(adapter);
	}
	private void getBusNumberInfo(String busno) {
		Log.d(TAG, "STarted task");
		BusInformationDownloaderTask vv = new BusInformationDownloaderTask(
				AppConstants.BUS_NORESULT + busno);
		vv.execute("BUSDATA");
	}

	private void getBusLocationInfo(String locat) {
		BusInformationDownloaderTask vv = new BusInformationDownloaderTask(
				AppConstants.BUS_LOCRESULT);
		vv.execute("BUSLOC");
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				progressDialog = ProgressDialog.show(
						BusInformationActivity.this,
						"Fetching and Storing data", "Downloading data", false);
				break;
			case 1:
				progressDialog.setMessage(msg.obj.toString());
				break;
			}
		}
	};

	class BusInformationDownloaderTask extends
			AsyncTask<String, Integer, String> {
		private String url;
		String option;
		boolean busNum = true;

		public BusInformationDownloaderTask(String site) {
			url = site;
		}

		@Override
		protected String doInBackground(String... params) {

			try {

				String httpResp;
				Log.d(TAG, url);
				option = params[0];
				httpResp = getHTTPData(url);
				if (httpResp != null) {
					if (params[0].equals("INITIALDOWNLOAD")) {
						try {
							int cityId = appController.getDefaultCityId();
							String cityName = appController.getDefaultCity();
							Message ms = mHandler.obtainMessage();
							ms.what = 1;
							ms.obj = "Storing bus number to database for " + cityName;
							mHandler.sendMessage(ms);

							String busNumbers[] = httpResp.split("var BUS_ROUTES_LIST = ");
							busNumbers = busNumbers[1].split("\\;");

							Log.d(TAG, busNumbers[1]);
							String places[] = busNumbers[1].split("var BUS_STOPS_LIST = ");
							places = places[1].split("\\;");
							JSONArray jso = new JSONArray(busNumbers[0]);
							for (int i = 0; i < jso.length(); i++) {
								publishProgress((int)(( i/(float)jso.length())*100));
								appController.getDBController().insertBusNumber(
										jso.getString(i), cityId);
								//Log.d(TAG, jso.getString(i));
							}
							jso = new JSONArray(places[0]);
							/*ms = mHandler.obtainMessage();
							ms.what = 1;
							ms.obj = "Storing  Bus locations data to database " + cityName;
							mHandler.sendMessage(ms);
							busNum = false;
							for (int i = 0; i < jso.length(); i++) {
								publishProgress((int) ((i/(float)jso.length())*100));
								appController.getDBController().insertBusStopName(
										jso.getString(i), cityId);
								//Log.d(TAG, jso.getString(i));
							}*/
						} catch (Exception e) {

						}
					} else if (params[0].equals("BUSDATA")) {
						parseBusNumberData(httpResp);
					} else if (params[0].equals("BUSLOC")) {
						parseBusLocationData(httpResp);
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			if (busNum) {
				progressDialog.setMessage("Initial setup - Storing bus number to database "
						+ progress[0] + "% done");
			} else {
				progressDialog.setMessage("Initial setup - Storing bus Location information"
						+ progress[0] + "% done");

			}

		}

		@Override
		// Once the image is downloaded, associates it to the imageView
		protected void onPostExecute(String vehicleText) {
			if (isCancelled()) {
				vehicleText = null;
			}
			Log.d(TAG, "option is " + option);
			if (!option.equals("INITIALDOWNLOAD")) {
				Intent intent = new Intent(BusInformationActivity.this,
						MapViewActivity.class);
				Bundle bdle = new Bundle();
				bdle.putInt("MAP_OPTION", AppConstants.SHOW_BUSSTOP);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtras(bdle);
				mContext.startActivity(intent);
				// vehicleInformationText.setText(vehicleText);
				// progressDialog.dismiss();
			} else {
				loadBusList();	
				progressDialog.dismiss();
			}

		}
	}

	private void parseDownloadData(String downloadData) {
		try {
			int cityId = appController.getDefaultCityId();
			String cityName = appController.getDefaultCity();
			Message ms = mHandler.obtainMessage();
			ms.what = 1;
			ms.obj = "Storing bus number to database for " + cityName;
			mHandler.sendMessage(ms);

			String busNumbers[] = downloadData.split("var BUS_ROUTES_LIST = ");
			busNumbers = busNumbers[1].split("\\;");

			Log.d(TAG, busNumbers[1]);
			String places[] = busNumbers[1].split("var BUS_STOPS_LIST = ");
			places = places[1].split("\\;");
			JSONArray jso = new JSONArray(busNumbers[0]);
			for (int i = 0; i < jso.length(); i++) {
				appController.getDBController().insertBusNumber(
						jso.getString(i), cityId);
				Log.d(TAG, jso.getString(i));
			}
			jso = new JSONArray(places[0]);
			ms = mHandler.obtainMessage();
			ms.what = 1;
			ms.obj = "Storing  Bus locations data to database " + cityName;
			mHandler.sendMessage(ms);
			for (int i = 0; i < jso.length(); i++) {
				appController.getDBController().insertBusStopName(
						jso.getString(i), cityId);
				Log.d(TAG, jso.getString(i));
			}
		} catch (Exception e) {

		}
	}

	private void parseBusNumberData(String data) {
		try {
			appController.clearBusStopOverlayItem();
			Log.d(TAG, data);
			JSONObject jobj = new JSONObject(data);
			JSONObject locationData = new JSONObject();
			float lat;
			float lon;
			JSONArray jso = new JSONArray(jobj.getString("stagepts"));
			for (int i = 0; i < jso.length(); i++) {
				locationData = jso.getJSONObject(i);
				if (!locationData.getString("lat").equals("null")
						&& !locationData.getString("lon").equals("null")) {
					lat = Float.parseFloat(locationData.getString("lat"));
					lon = Float.parseFloat(locationData.getString("lon"));

					GeoPoint point = new GeoPoint((int) (lat * 1E6),
							(int) (lon * 1E6));
					OverlayItem overlayitem = new OverlayItem(point,
							locationData.getString("info"), "");
					Log.d(TAG, point.toString());
					appController.setBusStopOverlayItem(overlayitem);
				}

				// cameraOverlayItem.addOverlayItem(overlayitem);
			}
		} catch (Exception e) {

		}
	}

	private void parseBusLocationData(String data) {

	}

	private String getHTTPData(String url) {
		return HTTPConnect.getData(url);
	}

}
