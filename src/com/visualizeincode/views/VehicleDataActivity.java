package com.visualizeincode.views;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.visualizeincode.R;
import com.visualizeincode.Utils.AppConstants;
import com.visualizeincode.Utils.HTTPConnect;

public class VehicleDataActivity extends Activity {

	private static final String TAG = "VehicleDataActivity";

	private ProgressDialog progressDialog;
	private EditText vehicleNumberText;
	private TextView vehicleInformationText;
	private Button submitButton;
	private String vehicleLayout;
	private TextView titleText;
	private VehicleInfoDownloaderTask vehicleTask;

	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);

		this.setContentView(R.layout.vehicle_number);

		vehicleLayout = getIntent().getExtras().getString("OPTIONS");

		vehicleNumberText = (EditText) findViewById(R.id.vehicle_number);
		vehicleInformationText = (TextView) findViewById(R.id.informationArea);
		vehicleNumberText.setText("KA01Z9899");
		submitButton = (Button) findViewById(R.id.vehicle_submit);

		submitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String url;
				if (vehicleNumberText.getText().toString().length() != 0) {
					progressDialog = ProgressDialog.show(v.getContext(),
							"Fetching Data",
							"Please wait while gathering data", false);
					if (vehicleLayout.equals("FINES")) {
						url = AppConstants.TRAFFIC_FINE;
					} else {
						url = AppConstants.TRAFFIC_RTO;
					}

					vehicleTask = new VehicleInfoDownloaderTask(url);

					vehicleTask.execute(vehicleNumberText.getText().toString());
					Log.d(TAG, "Statius " + vehicleTask.getStatus());
				}else{
					Toast.makeText(getApplicationContext(),
							"Please enter the vehicle number", Toast.LENGTH_SHORT).show();
				}
			}

		});

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	class VehicleInfoDownloaderTask extends AsyncTask<String, Void, String> {
		private String url;

		public VehicleInfoDownloaderTask(String site) {
			url = site;
		}

		@Override
		protected String doInBackground(String... params) {
			try {
				String number = params[0];
				String resp = HTTPConnect.postData(url, "?rto_num=" + number);
				if (resp == null) {
					vehicleInformationText.setText("Pblm in net");
				} else {
					if (vehicleLayout.equals("FINES")) {
						String res = resp.substring(resp.indexOf("value"),
								resp.indexOf("$(\'rto_number"));
						String[] arr = res.split("value = \"");
						arr = arr[1].split("\";");
						res = arr[0];
						return res;
					} else {
						String res = resp.substring(
								resp.indexOf("\"rto-result\", \"\\n\\t"),
								resp.indexOf("\\n\\n\");"));
						String r = res.replaceFirst("rto-result\", \"", "");
						r = r.substring(5, r.length());
						r = r.replaceAll("</TR><TR>", "</TR><BR><BR><TR>");
						Log.d(TAG, r);
						return r;
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		// Once the image is downloaded, associates it to the imageView
		protected void onPostExecute(String vehicleText) {
			if (isCancelled()) {
				vehicleText = null;
			}
			Log.d(TAG, "Result is " + vehicleText);
			if (vehicleText != null) {
				if (vehicleLayout.equals("FINES")) {
					vehicleInformationText.setText(vehicleText);
				} else {
					vehicleInformationText.setText(Html.fromHtml(vehicleText));

				}
			} else {
				vehicleInformationText
						.setText("Problem in retrieving data. Try again later. ");
			}

			progressDialog.dismiss();

		}
	}

}
