/*
 * 
 */
package com.asocom.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.asocom.model.Manager;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

// TODO: Auto-generated Javadoc
/** 
 * The Class Server.
 */
public class Server {

	/** The address. */
	public static String address = "http://192.168.42.67:8080/WebAPPNearMe/MockCIS";
	// public static String address = "http://192.168.191.1/asocom.php";
	// public static String address = "http://todotest.netau.net/index2.php";
	
	/** The timer. */
	public static Timer timer;

	/**
	 * Start.
	 */
	public static void start() {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(address);
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("operation", "3"));
			nameValuePairs.add(new BasicNameValuePair("json", ""));
			nameValuePairs.add(new BasicNameValuePair("ssid", Manager.getSSID()));
			Log.i("ssid", Manager.getSSID());
			Log.i("ssid", ""+Manager.getRegisteredUserEmail());
			nameValuePairs.add(new BasicNameValuePair
					("uid", Manager.getRegisteredUserEmail()));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			String str = inputStreamToString(response.getEntity().getContent())
					.toString();
			initSession();
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		}

		timer = new Timer();

		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				Log.i("Server",
						"Server.timer.scheduleAtFixedRate: ---------------------------------- ok ----------------------------------");
				try {
					Json.receiver(Server.getData());
				} catch (Exception e) {
				}

			}
		}, 0, 5000);
	}

	/**
	 * Send data.
	 *
	 * @param json the json
	 */
	public static void sendData(String json) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(address);

		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("operation", "1"));
			nameValuePairs.add(new BasicNameValuePair("json", json));
			nameValuePairs.add(new BasicNameValuePair("ssid", Manager.getSSID()));
			nameValuePairs.add(new BasicNameValuePair
					("uid", Manager.getRegisteredUserEmail()));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpResponse response = httpclient.execute(httppost);

			String str = inputStreamToString(response.getEntity().getContent())
					.toString();
			String[] resp = str.split("<");
			Log.i("Server", "Server.sendData(): new Message: " + resp[0]);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Send data.
	 *
	 * @param initial session
	 */
	public static void initSession() {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(address);

		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("operation", "4"));
			nameValuePairs.add(new BasicNameValuePair("json", ""));
			nameValuePairs.add(new BasicNameValuePair("ssid", Manager.getSSID()));
			nameValuePairs.add(new BasicNameValuePair
					("uid", Manager.getRegisteredUserEmail()));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpResponse response = httpclient.execute(httppost);
			String str = inputStreamToString(response.getEntity().getContent())
					.toString();
			String[] resp1 = str.split("<");
			String[] resp2 = resp1[0].split("x1Z7w");
			Log.i("Server.getData()", str);
			Json.reestablishConnection(resp2);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Gets the data.
	 *
	 * @return the data
	 */
	public static String[] getData() {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(address);

		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("operation", "2"));
			nameValuePairs.add(new BasicNameValuePair("json", ""));
			nameValuePairs.add(new BasicNameValuePair("ssid", Manager.getSSID()));
			nameValuePairs.add(new BasicNameValuePair
					("uid", Manager.getRegisteredUserEmail()));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpResponse response = httpclient.execute(httppost);

			String str = inputStreamToString(response.getEntity().getContent())
					.toString();
			String[] resp1 = str.split("<");
			String[] resp2 = resp1[0].split("x1Z7w");
			Log.i("Server.getData()", str);
			return resp2;

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Input stream to string.
	 *
	 * @param is the is
	 * @return the string builder
	 */
	private static StringBuilder inputStreamToString(InputStream is) {
		String line = "";
		StringBuilder total = new StringBuilder();
		// Wrap a BufferedReader around the InputStream
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		// Read response until the end
		try {
			while ((line = rd.readLine()) != null) {
				total.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Return full string
		return total;
	}

}