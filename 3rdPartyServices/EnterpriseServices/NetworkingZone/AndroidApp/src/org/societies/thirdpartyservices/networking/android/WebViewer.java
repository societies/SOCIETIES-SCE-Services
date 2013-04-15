package org.societies.thirdpartyservices.networking.android;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

public class WebViewer extends Activity {

	protected FrameLayout webViewPlaceholder;
	protected WebView webView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_web_viewer);
        //Initialize the UI
     	initUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    
	protected void initUI()
	{
		// Retrieve UI elements
		webViewPlaceholder = ((FrameLayout)findViewById(R.id.webViewPlaceholder));

		// Initialize the WebView if necessary
		if (webView == null)
		{
			//GET SERVER URL
			SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			String server = sharedPrefs.getString("webserver", "undefined");
			String port = sharedPrefs.getString("webserverPort", "undefined");
			String appPath = sharedPrefs.getString("appPath", "undefined");
			if (!appPath.startsWith("/"))
				appPath = "/" + appPath;
			
			String url = "http://" + server + ":" + port + appPath;
			
			// Create the webview
			webView = new WebView(this);
			webView.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			webView.getSettings().setSupportZoom(true);
			webView.getSettings().setBuiltInZoomControls(true);
			webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
			webView.setScrollbarFadingEnabled(true);
			webView.getSettings().setLoadsImagesAutomatically(true);
			webView.getSettings().setJavaScriptEnabled(true);

			// Load the URLs inside the WebView, not in the external web browser
			webView.setWebViewClient(new WebViewClient() {
				ProgressDialog _dialog;
				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
			    	view.loadUrl(url);
			    	return true;
				}
				
				@Override
				public void onPageStarted(WebView view, String url, Bitmap favicon) {
					_dialog =ProgressDialog.show(WebViewer.this, "Networking Zone", "Initializing...");
					super.onPageStarted(view, url, favicon);
				}
				@Override
				public void onPageFinished(WebView view, String url) {
					super.onPageFinished(view, url);
					_dialog.dismiss();
				}

				@Override
				public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
					super.onReceivedError(view, errorCode, description, failingUrl);
					try{
						_dialog.dismiss();
					}catch (Exception e) { }
				}
			});
			webView.loadUrl(url);
			//webView.loadUrl("http://societies.local:9090"); //USER FOR TESTING
		}

		// Attach the WebView to its placeholder
		webViewPlaceholder.addView(webView);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		if (webView != null)
		{
			// Remove the WebView from the old placeholder
			webViewPlaceholder.removeView(webView);
		}

		super.onConfigurationChanged(newConfig);

		// Load the layout resource for the new configuration
		setContentView(R.layout.activity_web_viewer);

		// Reinitialize the UI
		initUI();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);

		// Save the state of the WebView
		webView.saveState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);

		// Restore the state of the WebView
		webView.restoreState(savedInstanceState);
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
}
