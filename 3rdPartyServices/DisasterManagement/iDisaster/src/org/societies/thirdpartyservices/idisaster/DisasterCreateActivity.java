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

import java.text.SimpleDateFormat;
import java.util.Date;

import org.societies.android.api.cis.SocialContract;
import org.societies.android.api.cis.SupportedAccountTypes;
import org.societies.thirdpartyservices.idisaster.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

/**
 * Activity for creating a new disaster team (community).
 * 
 * @author Jacqueline.Floch@sintef.no
 *
 */
public class DisasterCreateActivity extends Activity implements OnClickListener {

	private EditText disasterNameView;
	private EditText disasterDescriptionView;
	private String disasterName;
	private String disasterDescription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.disaster_create_layout);

		// Get editable fields
		disasterNameView = (EditText) findViewById(R.id.editDisasterCreateName);
		disasterDescriptionView = (EditText) findViewById(R.id.editDisasterCreateDescription);

    	// Add click listener to button
    	final Button button = (Button) findViewById(R.id.disasterCreateButton);
    	button.setOnClickListener(this);

//	    Test dialog
//    	iDisasterApplication.getInstance().showDialog (this, getString(R.string.DisasterCreateTestDialog), getString(R.string.dialogOK));

    }


/**
 * onClick is called when button is clicked because
 * the OnClickListener is assigned to the button
 * */

	public void onClick(View view) {

	    // Hide the soft keyboard:
		// - the soft keyboard does not hide the toast message
		// - the soft keyboard will not appear on next activity window!
	    InputMethodManager mgr = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
	    mgr.hideSoftInputFromWindow(disasterNameView.getWindowToken(), 0);

    	if (disasterNameView.getText().length() == 0) {					// check input for disaster name
	    
    		Toast.makeText(this, getString(R.string.toastDisasterName), 
    				Toast.LENGTH_LONG).show();
    		return;

    	} else if (disasterDescriptionView.getText().length() == 0) {	// check input for description (or any obligatory field)

    	    Toast.makeText(this, getString(R.string.toastDisasterDescription), 
	    			Toast.LENGTH_LONG).show();
	    	return;

    	} 
    	
		disasterName = disasterNameView.getText().toString();
		disasterDescription = disasterDescriptionView.getText().toString();
		
		if (iDisasterApplication.testDataUsed) {				// Test case: Refresh list of disasters for display in the DisasterListActivity
	   	    iDisasterApplication.getInstance().disasterNameList.add(disasterName);
    	    // report data change to adapter
    	    iDisasterApplication.getInstance().disasterAdapter.notifyDataSetChanged();

	    	finish();
    	    // Go back to the previous activity
	    
		} else {
			if (addNewTeam()						// add disaster to SocialProvider
					.equals(iDisasterApplication.getInstance().INSERT_EXCEPTION)) {
				showQueryExceptionDialog ();	// Exception: Display dialog (and terminates activity)
    			// Go back to the previous activity
    		} else {
    			// The team was added to SocialProvider; The adapter (and adapter to view) is updated on resume of MemberListActivity 
    			finish ();
    		}
    	}
    }
	
/**
 * add team to SocialProvider
 */
	private String addNewTeam () {
    
		// Set the values related to the activity to store in SocialProvider
		ContentValues teamValues = new ContentValues ();
		
		teamValues.put(SocialContract.Communities.NAME, disasterName);
		teamValues.put(SocialContract.Communities.DESCRIPTION, disasterDescription);
// TODO: Remove debug code
//		String s = iDisasterApplication.getInstance().me.peopleId;
		teamValues.put(SocialContract.Communities._ID_OWNER,iDisasterApplication.getInstance().me.peopleId);
		teamValues.put(SocialContract.Communities.TYPE, iDisasterApplication.getInstance().COMMUNITY_TYPE);

		// Fields for synchronization with box.com
		teamValues.put(SocialContract.Communities.ACCOUNT_NAME, iDisasterApplication.getInstance().me.userName);
		teamValues.put(SocialContract.Communities.ACCOUNT_TYPE, SupportedAccountTypes.COM_BOX);
		teamValues.put(SocialContract.Communities.DIRTY, 1);
		
		try {
// The Uri value returned is not used.
//						Uri activityNewUri = getContentResolver().insert( SocialContract.CommunityActivity.CONTENT_URI,
//													activityValues);
			getContentResolver().insert( SocialContract.Communities.CONTENT_URI, 
					teamValues);
		} catch (Exception e) {
			iDisasterApplication.getInstance().debug (2, "Insert to "+ 
								SocialContract.Communities.CONTENT_URI + "causes an exception");
	    	return iDisasterApplication.getInstance().INSERT_EXCEPTION;
		}
				
		return iDisasterApplication.getInstance().INSERT_SUCCESS;
	}
	
/**
 * showQueryExceptionDialog displays a dialog to the user.
 * In this case, the activity does not terminate since the other
 * activities in the TAB may still work.
 */
        			
	private void showQueryExceptionDialog () {
    	AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setMessage(getString(R.string.dialogPeopleQueryException))
        			.setCancelable(false)
          			.setPositiveButton (getString(R.string.dialogOK), new DialogInterface.OnClickListener() {
          				public void onClick(DialogInterface dialog, int id) {
          					finish ();
          					return;
          				}
          			});
        AlertDialog alert = alertBuilder.create();
        alert.show();	
	}

}
