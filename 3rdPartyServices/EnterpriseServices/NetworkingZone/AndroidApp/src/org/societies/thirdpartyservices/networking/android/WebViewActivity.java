package org.societies.thirdpartyservices.networking.android;

import org.societies.thirdpartyservices.networking.android.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity extends Activity {

	private static final String LOG_TAG = WebViewActivity.class.getName();
	private WebView webView;

	public void onCreate(Bundle savedInstanceState) {
		final Context context = this;
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);

		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
		
		String server = sharedPrefs.getString("webserver", "undefined");
		String port = sharedPrefs.getString("webserverPort", "undefined");
		String appPath = sharedPrefs.getString("appPath", "undefined");
		if (!appPath.startsWith("/"))
			appPath = "/" + appPath;
		
		String url = "http://" + server + ":" + port + appPath;
		
		Log.d(LOG_TAG, "getStringPrefValue for: " + url);
		
		webView = (WebView) findViewById(R.id.webView1);
		webView.setWebViewClient(new WebViewClient());
		webView.getSettings().setJavaScriptEnabled(true);
		webView.loadUrl(url);
		//webView.loadUrl("http://societies.local:9090");
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

	 @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		//case R.id.about:
		//	Intent aboutIntent = new Intent(this, AboutActivity.class);
		//	this.startActivity(aboutIntent);
		//    return true;
		case R.id.preference:
			Intent prefIntent = new Intent(this, MasterPreferences.class);
			this.startActivity(prefIntent);
		    return true;
		default:
		    return super.onOptionsItemSelected(item);
		}
	}
	 
	/**
	 * The final call you receive before your activity is destroyed. 
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public void onBackPressed() {
	   	//DO NOTHING
		return;
	}
	
	private class WebClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
	    	view.loadUrl(url);
	        return true;
		}
	}
}
