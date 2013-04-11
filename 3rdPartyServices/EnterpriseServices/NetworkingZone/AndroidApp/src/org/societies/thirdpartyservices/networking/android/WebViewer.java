package org.societies.thirdpartyservices.networking.android;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.ViewGroup;
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

			// Load the URLs inside the WebView, not in the external web browser
			webView.setWebViewClient(new WebViewClient());
			webView.loadUrl(url);
			//webView.loadUrl("http://www.google.com");
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
}
