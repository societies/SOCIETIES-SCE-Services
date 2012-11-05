package org.societies.thirdpartyservices.networking.android;

import org.societies.thirdpartyservices.networking.android.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

	private Button button;

	public void onCreate(Bundle savedInstanceState) {
		final Context context = this;

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

		button = (Button) findViewById(R.id.buttonUrl);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				Intent intent = new Intent(context, WebViewActivity.class);
				startActivity(intent);
			}
		});

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
}