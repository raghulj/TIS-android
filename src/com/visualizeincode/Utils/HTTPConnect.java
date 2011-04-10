package com.visualizeincode.Utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.util.Log;

public class HTTPConnect {

	private static final String TAG = "HTTPConnect";

	public static String postData(String target, String content) {
		System.out.println("About to post\nURL: " + target + "content: "
				+ content);
		try {
			String response = "";
			URL url = new URL(target + "" + content);
			URLConnection conn = url.openConnection();
			// Set connection parameters.
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			// Make server believe we are form data...
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			conn.connect();
			// Read response from the input stream.
			BufferedReader in = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			String temp;
			while ((temp = in.readLine()) != null) {
				response += temp + "\n";
			}
			temp = null;
			in.close();
			System.out.println("Server response:\n'" + response + "'");
			return response;
		} catch (Exception e) {

		}
		return null;
	}

	public static String getData(String url) {
		try {
			URL webURL = new URL(url);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					webURL.openStream()));
			String line;
			StringBuffer sb = new StringBuffer();
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			return sb.toString();
		} catch (Exception e) {
			Log.e(TAG, "Error in retrieving data " + e);
		}
		return null;
	}

	public static Bitmap downloadBitmap(String url) {
		final AndroidHttpClient client = AndroidHttpClient
				.newInstance("Android");
		final HttpGet getRequest = new HttpGet(url);

		try {
			HttpResponse response = client.execute(getRequest);
			final int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				Log.w("ImageDownloader", "Error " + statusCode
						+ " while retrieving bitmap from " + url);
				return null;
			}

			final HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream inputStream = null;
				try {
					inputStream = entity.getContent();
					final Bitmap bitmap = BitmapFactory
							.decodeStream(inputStream);
					return bitmap;
				} finally {
					if (inputStream != null) {
						inputStream.close();
					}
					entity.consumeContent();
				}
			}
		} catch (Exception e) {
			// Could provide a more explicit error message for IOException or
			// IllegalStateException
			getRequest.abort();
			Log.e("ImageDownloader",
					"Error while retrieving bitmap from " + e.toString());
		} finally {
			if (client != null) {
				client.close();
			}
		}
		return null;
	}

}
