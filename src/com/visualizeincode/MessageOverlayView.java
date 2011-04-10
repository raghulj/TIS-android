package com.visualizeincode;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.maps.OverlayItem;
import com.visualizeincode.R;
import com.visualizeincode.Utils.AppConstants;
import com.visualizeincode.Utils.HTTPConnect;

/**
 * Reference : https://github.com/galex/android-mapviewballoons.git
 * 
 * @author raghul
 * 
 */
public class MessageOverlayView extends FrameLayout {

	private LinearLayout layout;
	private LinearLayout cameraLayout;
	private TextView title;
	private TextView snippet;
	private TextView cameraView;
	private Context mContext;
	private ImageView cameraImage;
	private String camNumber;
	private TextView cameraImageViewTitle;
	private boolean showCamera;

	public MessageOverlayView(Context context, int offset, boolean showCam,
			String camNum) {
		super(context);
		mContext = context;
		showCamera = showCam;
		camNumber = camNum;
		// setPadding(10, 0, 10, offset);
		layout = new LinearLayout(context);
		layout.setVisibility(VISIBLE);

		cameraLayout = new LinearLayout(context);

		cameraLayout.setVisibility(GONE);

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.message_overlay, layout);
		title = (TextView) v.findViewById(R.id.balloon_item_title);
		snippet = (TextView) v.findViewById(R.id.balloon_item_snippet);
		cameraView = (TextView) v.findViewById(R.id.camera_view);
		if (showCam) {
			initCam(v);
		} else {

			cameraView.setVisibility(INVISIBLE);
		}

		v.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				layout.setVisibility(GONE);
			}
		});

		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.NO_GRAVITY;

		FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		params1.gravity = Gravity.BOTTOM;

		addView(layout, params);

	}

	private void initCam(View mainView) {
		LayoutInflater inflater1 = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v1 = inflater1.inflate(R.layout.camera_image, cameraLayout);
		cameraImageViewTitle = (TextView) v1
				.findViewById(R.id.balloon_item_title);
		cameraImage = (ImageView) v1.findViewById(R.id.camera_image_view);

		cameraView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				cameraLayout.setVisibility(VISIBLE);
				layout.setVisibility(GONE);
				CameraPicDownloaderTask camtask = new CameraPicDownloaderTask();
				camtask.execute(camNumber);
			}
		});

		v1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				cameraLayout.setVisibility(GONE);
			}
		});
		addView(cameraLayout);

	}

	public void setData(OverlayItem item) {
		layout.setVisibility(VISIBLE);
		if (item.getTitle() != null) {
			title.setVisibility(VISIBLE);
			title.setText(item.getTitle());
			if (cameraImageViewTitle != null) {
				cameraImageViewTitle.setText(item.getTitle());
			}
		} else {
			title.setVisibility(INVISIBLE);
		}

		if (showCamera != true) {
			if (item.getSnippet() != null) {
				snippet.setVisibility(VISIBLE);
				snippet.setText(item.getSnippet());
			} else {
				snippet.setVisibility(GONE);
			}
		}

	}

	class CameraPicDownloaderTask extends AsyncTask<String, Void, Bitmap> {
		private String url;
		String option;

		public CameraPicDownloaderTask() {

		}

		@Override
		protected Bitmap doInBackground(String... params) {

			try {

				Bitmap bt = HTTPConnect
						.downloadBitmap(AppConstants.CAMERA_PICTURE + params[0]
								+ ".jpg");
				return bt;
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Bitmap camera) {
			if (isCancelled()) {
				camera = null;
			}
			if (camera == null) {
				cameraImage.setImageResource(R.drawable.no_image);
			} else {
				cameraImage.setImageBitmap(camera);
			}

		}
	}

}
