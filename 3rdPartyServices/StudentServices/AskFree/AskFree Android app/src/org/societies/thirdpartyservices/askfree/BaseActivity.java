package org.societies.thirdpartyservices.askfree;

import org.societies.thirdpartyservices.askfree.R;
import android.os.Bundle;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.view.Menu;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;


public class BaseActivity extends TabActivity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base);
		

		TabHost tabHost = getTabHost();
		 
			// Tab for Main
		     TabSpec mainspec = tabHost.newTabSpec("Main");
		     // setting Title and Icon for the Tab
		     mainspec.setIndicator("Ask");
		     Intent mainIntent = new Intent(this, MainActivity.class);
		     mainspec.setContent(mainIntent);
		     
		 
		     
		  // Tab for RepPoints
		     TabSpec rpspec = tabHost.newTabSpec("RP");
		     // setting Title and Icon for the Tab
		     rpspec.setIndicator("Reputation Points");
		     Intent rpIntent = new Intent(this, RepPointsActivity.class);
		     rpspec.setContent(rpIntent);
		     
		  // Adding all TabSpec to TabHost
		     tabHost.addTab(mainspec); 
		     tabHost.addTab(rpspec); 
	}
}
