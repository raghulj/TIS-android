package com.visualizeincode;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.visualizeincode.R;
import com.visualizeincode.Utils.AppConstants;
import com.visualizeincode.Utils.HTTPConnect;
import com.visualizeincode.controller.AppController;

public class MapViewActivity extends MapActivity {
	private static final String TAG = "MapViewActivity";

	private LinearLayout mLinearLayout;
	private MapView mapView;
	private ItemOverlay highTrafficOverlayItem;
	private ItemOverlay mediumTrafficOverlayItem;
	private ItemOverlay smoothTrafficOverlayItem;
	private ItemOverlay cameraOverlayItem;
	private ItemOverlay busStopOverlayItem;
	private ItemOverlay progressNotification;
	private List<Overlay> mTrafficOverlays;

	private Drawable highTraffic;
	private Drawable mediumTraffic;
	private Drawable smoothTraffic;
	private Drawable cameraImage;
	private Drawable busImage;
	private Drawable progressBox;
	private int currentOption;
	private Context mContext;
	private LinearLayout layout;
	private MapController mapController;

	private AppController appController;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		currentOption = getIntent().getExtras().getInt("MAP_OPTION");
		setContentView(R.layout.main);

		appController = AppController.getInstance(getApplicationContext());
		mContext = getApplicationContext();

		mapView = (MapView) findViewById(R.id.mapview);
		progressBox = this.getResources().getDrawable(R.drawable.popup_frame);

		mapView.setBuiltInZoomControls(true);
		

		mTrafficOverlays = mapView.getOverlays();
		progressNotification = new ItemOverlay(progressBox, mapView);

		// Loading bitmap based on the view selection
		if (currentOption == AppConstants.SHOW_HOTSPOT) {
			highTraffic = this.getResources().getDrawable(R.drawable.red);
			mediumTraffic = this.getResources().getDrawable(R.drawable.orange);
			smoothTraffic = this.getResources().getDrawable(R.drawable.green);

			highTrafficOverlayItem = new ItemOverlay(highTraffic, mapView);
			mediumTrafficOverlayItem = new ItemOverlay(mediumTraffic, mapView);
			smoothTrafficOverlayItem = new ItemOverlay(smoothTraffic, mapView);

		} else if (currentOption == AppConstants.SHOW_BUSSTOP) {
			busImage = this.getResources().getDrawable(R.drawable.bus);

			busStopOverlayItem = new ItemOverlay(busImage, mapView);

		} else if (currentOption == AppConstants.SHOW_CAMERA) {
			cameraImage = this.getResources().getDrawable(R.drawable.camera);
			cameraOverlayItem = new ItemOverlay(cameraImage, mapView);

		}

		if (currentOption != AppConstants.SHOW_BUSSTOP) {

			layout = new LinearLayout(mContext);
			layout.setVisibility(View.VISIBLE);
			layout.setGravity(Gravity.BOTTOM);
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View v = inflater.inflate(R.layout.progress_layout, layout);
			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			params.gravity = Gravity.BOTTOM | Gravity.RIGHT;
			params.rightMargin = 0;
			params.bottomMargin = 0;

			mapView.addView(layout, params);
		}
		
		// TODO - more specific to bangalore.. Has to be changed
		GeoPoint point = new GeoPoint( (int)(12.977 * 1E6), (int)(77.592 * 1E6));
		mapController = mapView.getController();
		mapController.animateTo(point);
		mapController.setZoom(12);
		

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (currentOption == AppConstants.SHOW_BUSSTOP) {
			mHandler.sendEmptyMessageDelayed(AppConstants.SHOW_BUSSTOP, 2000);
		}
		if (currentOption == AppConstants.SHOW_HOTSPOT) {
			mHandler.sendEmptyMessageDelayed(AppConstants.SHOW_HOTSPOT, 2000);
		}
		if (currentOption == AppConstants.SHOW_CAMERA) {
			mHandler.sendEmptyMessageDelayed(AppConstants.SHOW_CAMERA, 2000);
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case AppConstants.SHOW_HOTSPOT:
				DataDownloaderTask vv = new DataDownloaderTask(
						AppConstants.TRAFFIC_HOTSPOTS);
				vv.execute("HOTSPOT");
				break;
			case AppConstants.SHOW_CAMERA:
				DataDownloaderTask cameraTask = new DataDownloaderTask(
						AppConstants.BASE_URL);
				cameraTask.execute("CAMERA");
				break;
			case AppConstants.SHOW_BUSSTOP:
				if (appController.getBusStopOverlayItems() != null) {
					busStopOverlayItem.addOverlay(
							appController.getBusStopOverlayItems(), false);
				}
				mTrafficOverlays.add(busStopOverlayItem);
				break;
			}

		}
	};

	class DataDownloaderTask extends AsyncTask<String, Void, String> {
		private String url;
		String option;

		public DataDownloaderTask(String site) {
			url = site;
		}

		@Override
		protected String doInBackground(String... params) {

			try {

				String httpResp;
				Log.d(TAG, url);
				httpResp = HTTPConnect.getData(url);
				if (httpResp != null) {
					if (params[0].equals("HOTSPOT")) {
						populateHotSpot(httpResp);
					} else if (params[0].equals("CAMERA")) {
						populateCamera(httpResp);
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(String vehicleText) {
			if (isCancelled()) {
				vehicleText = null;
			}
			mapView.removeView(layout);

		}
	}

	private void populateHotSpot(String httpResp) {
		appController.clearHighHotSpotOverlayItem();
		appController.clearMediumHotSpotOverlayItem();
		appController.clearSmoothHotSpotOverlayItem();

		appController.parseHotSpotInformation(httpResp);

		if (appController.getHighHotSpotOverlayItems() != null) {
			highTrafficOverlayItem.addOverlay(
					appController.getHighHotSpotOverlayItems(), false);
		}
		if (appController.getMediumHotSpotOverlayItems() != null) {
			mediumTrafficOverlayItem.addOverlay(
					appController.getMediumHotSpotOverlayItems(), false);
		}
		if (appController.getSmoothHotSpotOverlayItems() != null) {
			smoothTrafficOverlayItem.addOverlay(
					appController.getSmoothHotSpotOverlayItems(), false);
		}
		mTrafficOverlays.add(highTrafficOverlayItem);
		mTrafficOverlays.add(mediumTrafficOverlayItem);
		mTrafficOverlays.add(smoothTrafficOverlayItem);

	}

	private void populateCamera(String resp) {
		appController.clearCameraOverlayItem();

		appController.parseCameraInformation(resp);

		if (appController.getCameraOverlayItems() != null) {
			Log.d(TAG, "placing in map");
			cameraOverlayItem.addOverlay(appController.getCameraOverlayItems(),
					true);
		}
		mTrafficOverlays.add(cameraOverlayItem);

	}

}
