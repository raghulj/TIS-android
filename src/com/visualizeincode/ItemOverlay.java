package com.visualizeincode;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.visualizeincode.R;

public class ItemOverlay extends ItemizedOverlay {

	private static final String TAG = "ItemOverlay";
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private MessageOverlayView messagePopup;
	private View clickRegion;
	private int viewOffset;
	private MapView mapView;
	private boolean showCam = false;
	private boolean showProgress = false;

	public ItemOverlay(Drawable defaultMarker, MapView mapView) {
		super(boundCenterBottom(defaultMarker));
		this.mapView = mapView;
		viewOffset = 0;
	}

	public ItemOverlay(Drawable defaultMarker, MapView mapView, boolean progress) {
		super(boundCenterBottom(defaultMarker));
		this.mapView = mapView;
		viewOffset = 0;
		showProgress = progress;
	}

	public void addOverlayItem(OverlayItem overlay, boolean camLink) {
		showCam = camLink;
		mOverlays.add(overlay);
		populate();
	}

	public void addOverlay(ArrayList<OverlayItem> overlays, boolean camLink) {
		showCam = camLink;
		for (OverlayItem olt : overlays) {
			mOverlays.add(olt);
		}
		populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}

	public void setMessageOffset(int pixel) {
		viewOffset = pixel;
	}

	private void hideMessage() {

	}

	@Override
	protected final boolean onTap(int index) {
		if (!showProgress) {
			Log.d(TAG, "Index tap " + index);
			GeoPoint point;
			final OverlayItem item = createItem(index);
			point = item.getPoint();
			messagePopup = new MessageOverlayView(mapView.getContext(),
					viewOffset, showCam, item.getSnippet());
			clickRegion = (View) messagePopup
					.findViewById(R.drawable.balloon_overlay_unfocused);
			messagePopup.setData(item);

			MapView.LayoutParams params = new MapView.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
					point, MapView.LayoutParams.BOTTOM_CENTER);
			params.mode = MapView.LayoutParams.MODE_MAP;

			messagePopup.setVisibility(View.KEEP_SCREEN_ON);
			messagePopup.setLayoutParams(params);
			mapView.addView(messagePopup, params);
			return true;
		}
		return false;
	}

}
