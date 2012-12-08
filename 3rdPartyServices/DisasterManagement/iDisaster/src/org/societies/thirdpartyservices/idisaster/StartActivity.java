/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.thirdpartyservices.idisaster;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import org.societies.thirdpartyservices.idisaster.R;
import org.societies.thirdpartyservices.idisaster.data.ThirdPartyService;


/**
 * This is the activity that starts when you first click on the icon in Android.
 * 
 * It selects the next activities.
 * See the method onClick for more details.
 * 
 * @author Jacqueline.Floch@sintef.no
 * 
 *
 */

// TODO: will be extended to retrieve the user data in the SocialProvider

public class StartActivity extends Activity implements OnClickListener {

	Boolean loggedIn;							// when true the user is logged
	Boolean socialProviderInstalled = true;		// when true the SocialProvider is installed
												// initial value is true allowing using test data

    @Override
    public void onCreate(Bundle savedInstanceState) {

    	super.onCreate(savedInstanceState);
    				
	    setContentView (R.layout.start_layout);	// create GUI
   	    
	    // Add click listener to button
	    final Button button = (Button) findViewById(R.id.startButton);
	    button.setOnClickListener(this);

    }

/**
 * onResume: Things to do when the activity becomes visible.
 * - check the ContentProvider is answering
 * - check if the user is logged in
 * 
 */
	@Override
    public void onResume () {
    	
    	super.onResume();
    
    	loggedIn = false;	// Always reset when starting activity since the user may have logged out in
    						// this App or another App
    	
    									
    	// check is the user is logged in
    	// if logged in display the user name
    	// if not tell the user he is not logged in.
		TextView startView = (TextView) findViewById(R.id.startInfo);

		if (iDisasterApplication.testDataUsed) {							// Use test data (no SocialProvider)
			startView.setText(getString(R.string.startInfoNotLogged));			
		} else {															// Fetch data from SocialProvider

			socialProviderInstalled = new ThirdPartyService ("")
						.serviceInstalled (this,"org.societies.android.platform");
//			socialProviderInstalled = appInstalled ("org.societies.android.platform");
			if (!socialProviderInstalled) {			// Check whether or nor SocialProvider is installed
				showQueryExceptionDialog (										// Show exception and terminate
						getString(R.string.dialogSocialProviderException));
				
// TODO: Add code to download and start SocialProvider		
			} else {			
				String userQueryCode = iDisasterApplication.getInstance().checkUserIdentity(this);
				if (userQueryCode.equals(iDisasterApplication.getInstance().QUERY_SUCCESS)) {	// User is identified
					startView.setText(getString(R.string.startInfoLogged) 
							+ " " 													// Not sure end space in the predefined string is ignored
							+ iDisasterApplication.getInstance().me.displayName);
					loggedIn = true;
				} else if (userQueryCode.equals(iDisasterApplication.getInstance().QUERY_EMPTY)) {	// No data returned by SocialProvider
					startView.setText(getString(R.string.startInfoNotLogged));
				} else {
					showQueryExceptionDialog (getString(R.string.dialogQueryException));
		        }
				
			}
		}
	}

/**
 * onClick is called when button is clicked because
 * the OnClickListener is assigned to the button
 */
	 public void onClick (View view) {
		 
		if  (!socialProviderInstalled) {			// Wait for installing SocialProvider
													// Currently further queries to SocialProvider will fail...
													// Reason? : permissions are granted at install (as described in Manifest)
			return;
		}
		
		if (!loggedIn) {													// User was not identified
			//TODO: Replace this logging by a call to CSS Management?
			// I am not sure how this should be handled as handling logging
			// should be under the responsibility of SocialProvider

    		startActivity(new Intent(StartActivity.this, LoginActivity.class));
    		return;			
		} else {
	    	startActivity(new Intent(StartActivity.this, DisasterListActivity.class));
	    	return;
		}
   	}


/**
 * onCreateOptionsMenu creates the activity menu.
 */
 
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		menu.clear();
		getMenuInflater().inflate(R.menu.start_menu, menu);

//		It is possible to set up a variable menu		
//			menu.findItem (R.id....).setVisible(true);

		return true;
	}


/**
  * onOptionsItemSelected handles the selection of an item in the activity menu.
  */

	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		
    	case R.id.startMenuLogoff:
    		
//TODO: Call CSS Management?
// I am not sure how this should be handled as many Apps may be "logged in".
// Handling logging should be under the responsibility of SocialProvider

    		break;
    		
    	default:
    		break;
    	}
    	return true;
    }

/**
 * showQueryExceptionDialog displays a dialog to the user and terminates activity.
 */
		
	private void showQueryExceptionDialog (String message) {
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
		alertBuilder.setMessage(message)
  				.setCancelable(false)
  				.setPositiveButton (getString(R.string.dialogOK), new DialogInterface.OnClickListener() {
  					public void onClick(DialogInterface dialog, int id) {
  						// add termination code code eventually
  							finish ();
  						return;
  					}
  				});
		AlertDialog alert = alertBuilder.create();
	    alert.show();	
	}

/**
 * getPreferences retrieves the preferences stored in the preferences file.
 */
// All information is fetched from the SocialProvider. Preferences are no longer used.
//
//    private void getPreferences () {
//
//    	userName = iDisasterApplication.getInstance().getUserName ();
//    	disasterTeamName = iDisasterApplication.getInstance().getDisasterTeamName ();
//	}
		
/**
 * startNextActivity is called when to select the next activity.
 * 
 * If the user is not registered, it starts the LoginActivity,
 * otherwise if no disaster is selected, it starts the DisasterActivity
 * otherwise it starts the HomeActivity.
 */
//	private void startNextActivity () {
//		
//    	if (userName == getString(R.string.noPreference)) {							// no user name (no password)
//    		startActivity(new Intent(StartActivity.this, LoginActivity.class));
//    		return;
//    	} else if (disasterTeamName == getString(R.string.noPreference)) {			// no disaster selected
//    		startActivity(new Intent(StartActivity.this, DisasterListActivity.class));
//    		return;
//    	} else {   		
//    		startActivity(new Intent(StartActivity.this, DisasterActivity.class));
//    		return;
//    	}
//    }

}