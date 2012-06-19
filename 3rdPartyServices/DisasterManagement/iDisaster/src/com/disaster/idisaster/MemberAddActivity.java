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
package com.disaster.idisaster;

import com.disaster.idisaster.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

/**
 * Activity for creating a new disaster community.
 * 
 * @author Jacqueline.Floch@sintef.no
 *
 */
public class MemberAddActivity extends Activity implements OnClickListener {

	private EditText memberNameView;
	private EditText memberDescriptionView;
	private String memberName;
	private String memberDescription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.member_add_layout);

		// Get editable fields
		memberNameView = (EditText) findViewById(R.id.editMemberAddName);
		memberDescriptionView = (EditText) findViewById(R.id.editMemberAddDescription);

    	// Add click listener to button
    	final Button button = (Button) findViewById(R.id.memberAddButton);
    	button.setOnClickListener(this);

//	    Test dialog
//    	iDisasterApplication.getinstance().showDialog (this, getString(R.string.newDisasterTestDialog), getString(R.string.dialogOK));

    }


/**
 * onClick is called when button is clicked because
 * the OnClickListener is assigned to the button
 * */

	public void onClick(View view) {

    	if (memberNameView.getText().length() == 0) {					// check input for disaster name

    		// Hide the soft keyboard otherwise the toast message does appear more clearly.
    	    InputMethodManager mgr = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
    	    mgr.hideSoftInputFromWindow(memberNameView.getWindowToken(), 0);
	    
    		Toast.makeText(this, getString(R.string.toastMemberName), 
    				Toast.LENGTH_LONG).show();
    		return;

    	} else if (memberDescriptionView.getText().length() == 0) {	// check input for description (or any obligatory field

    		// Hide the soft keyboard otherwise the toast message does appear more clearly.
    	    InputMethodManager mgr = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
    	    mgr.hideSoftInputFromWindow(memberDescriptionView.getWindowToken(), 0);

    	    Toast.makeText(this, getString(R.string.toastMemberDescription), 
	    			Toast.LENGTH_LONG).show();
	    	return;

    	} else {

    		memberName = memberNameView.getText().toString();
    		memberDescription = memberDescriptionView.getText().toString();

    		//TODO: Add call for search to the Social Provider
	    		
//TODO: Refresh list of disasters? - so it is displayed in the previous activity
    		
//TODO: remove test code
    	    iDisasterApplication.getInstance().memberNameList.add(memberName);
    	    
    	    // report data change to adapter
    	    iDisasterApplication.getInstance().disasterAdapter.notifyDataSetChanged();

    		
// TODO: Remove code for testing the correct setting of preferences 
    	    Toast.makeText(this, "Debug: "  + memberName + " " + memberDescription, Toast.LENGTH_LONG).show();

    	    // Hide the soft keyboard:
			// - the soft keyboard will not appear on next activity window!
    	    InputMethodManager mgr = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
    	    mgr.hideSoftInputFromWindow(memberNameView.getWindowToken(), 0);


//	    	finish();	// noHistory=true in Manifest => the activity is removed from the activity stack and finished.

    	    // Go back to the list of disasters
//	    	startActivity(new Intent(NewDisasterActivity.this, DisasterListActivity.class));
	    }
    }

}
