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
import org.societies.thirdpartyservices.idisaster.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

/**
 * Activity for adding a new feed to the selected disaster team (community).
 * 
 * @author Jacqueline.Floch@sintef.no
 *
 */
public class FeedAddActivity extends Activity implements OnClickListener {

	private EditText feedContentView;
	private String feedContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.feed_add_layout);

		// Get editable fields
		feedContentView = (EditText) findViewById(R.id.editFeedAddContent);

    	// Add click listener to button
    	final Button button = (Button) findViewById(R.id.feedAddButton);
    	button.setOnClickListener(this);
    }


/**
 * onClick is called when button is clicked because
 * the OnClickListener is assigned to the button
 * */

	public void onClick(View view) {

    	// Hide the soft keyboard:
		// - the soft keyboard will not hide any message and will not appear on next activity window!
		InputMethodManager mgr = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
    	mgr.hideSoftInputFromWindow(feedContentView.getWindowToken(), 0);

    	if (feedContentView.getText().length() == 0) {					// check input for content	    
    		Toast.makeText(this, getString(R.string.toastFeedContent), 
    				Toast.LENGTH_SHORT).show();
    		return;

    	} else {														// add activity to feed


    		feedContent = feedContentView.getText().toString();
    		
    		
    		if (iDisasterApplication.testDataUsed) {			// Test data
        	    iDisasterApplication.getInstance().feedContentList.add(feedContent);
        	    iDisasterApplication.getInstance().feedAdapter.notifyDataSetChanged(); // Notify data change to adapter
        	    finish ();							// Terminates and go back to previous activity
    		} else {
    			if (addFeed()						// Insert activity to the activity feed for the selected team
					.equals(iDisasterApplication.getInstance().INSERT_EXCEPTION)) {
    				showQueryExceptionDialog ();	// Exception: Display dialog (and terminates activity)
    				// Go back to the previous activity
    			} else {
    				// The activity will be added to the adapter (and adapter to view) on resume of FeedListActivity 
    				finish ();
    			}
			}
		}
    }

/**
 * addFeed inserts a new activity to the activity feed for the selected disaster team
 * in SocialProvider.
 */
	private String addFeed () {

		// Set the values related to the activity to store in SocialProvider
		ContentValues activityValues = new ContentValues ();
		
//TODO: Remove the following once SocialProvider has been corrected (SocialProvider should insert the GLOBAL_ID)
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String currentDateandTime = sdf.format(new Date());
		activityValues.put(SocialContract.CommunityActivity.GLOBAL_ID, currentDateandTime);
// End remove		

		activityValues.put(SocialContract.CommunityActivity.ORIGIN, "SOCIETIES");	// Social platform iDisaster is plugged into		
		activityValues.put(SocialContract.CommunityActivity.GLOBAL_ID_ACTOR,		// Me
							iDisasterApplication.getInstance().me.globalId);
		activityValues.put(SocialContract.CommunityActivity.GLOBAL_ID_FEED_OWNER,	// Selected team
							iDisasterApplication.getInstance().selectedTeam.globalId);
		activityValues.put(SocialContract.CommunityActivity.GLOBAL_ID_VERB,			// Activity intent
							iDisasterApplication.getInstance().FEED_DISPLAY);
		activityValues.put(SocialContract.CommunityActivity.GLOBAL_ID_OBJECT, feedContent); // Text entered by the user
//		No target - shared with all members in the community
		activityValues.put(SocialContract.CommunityActivity.GLOBAL_ID_TARGET, "ALL"); // Activity target
		 
		try {
// The Uri value returned is not used.
//			Uri activityNewUri = getContentResolver().insert( SocialContract.CommunityActivity.CONTENT_URI,
//										activityValues);
			getContentResolver().insert( SocialContract.CommunityActivity.CONTENT_URI, 
										activityValues);
		} catch (Exception e) {
			iDisasterApplication.getInstance().debug (2, "Insert to "+ 
								SocialContract.CommunityActivity.CONTENT_URI + "causes an exception");
	    	return iDisasterApplication.getInstance().INSERT_EXCEPTION;
		}
		
		return iDisasterApplication.getInstance().INSERT_SUCCESS;
	}

/**
 * showQueryExceptionDialog displays a dialog to the user.
 */
	    			
   	private void showQueryExceptionDialog () {
   		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
   		alertBuilder.setMessage(getString(R.string.dialogFeedInsertException))
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
