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
import org.societies.thirdpartyservices.idisaster.data.SocialActivity;

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
    			SocialActivity socialActivity = 
    					new SocialActivity (iDisasterApplication.getInstance().me.userName); // account for synchronization
    			ContentResolver activityResolver = getContentResolver();
    			
    			if ((socialActivity.addActivity (activityResolver,				// Insert activity to the activity feed
 // TODO: what should be set here? local or global id?
    					iDisasterApplication.getInstance().selectedTeam.id,		// Feed of the the selected team
// TODO: what should be set here? global or local id? - local id seems to not work		
    					iDisasterApplication.getInstance().me.userName,			// Me
    					iDisasterApplication.getInstance().VERB_TEXT,			// Activity intent: Simple text
    					feedContent,											// Text entered by the user
    					iDisasterApplication.getInstance().TARGET_ALL)			// Recipient for Activity
				.equals(iDisasterApplication.getInstance().INSERT_EXCEPTION))) {
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
