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

import org.societies.thirdpartyservices.idisaster.R;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This activity allows the users to manage the activity feed in 
 * a selected disaster team (a disaster team the user is member of).
 * 
 * @author Jacqueline.Floch@sintef.no
 *
 */
public class FeedListActivity extends ListActivity {
	
	ContentResolver resolver;
	Cursor cursor;
	
	ArrayAdapter<String> feedAdapter;
	ListView listView;

 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	
    //TODO: query Content Provider for services in the CIS
	
  	boolean noFeed = false;
  	
  	if (noFeed) {
  		//  TextView cannot be used here as the Activity is a ListActivity
  		//  TextView textview = new TextView(this);
  		//	textview.setText("This is the Services tab");
  		//  setContentView(textview);

  		// Create dialog if no service in disaster team						
      	AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
      	alertBuilder.setMessage(getString(R.string.feedListDialogCIS))
      		.setCancelable(false)
      		.setPositiveButton (getString(R.string.dialogOK), new DialogInterface.OnClickListener() {
      			public void onClick(DialogInterface dialog, int id) {
      				// add code
      				return;
      			}
      		});
  	    AlertDialog alert = alertBuilder.create();
  	    alert.show();
  	    return;

  	} else {
    
    	setContentView (R.layout.feed_list_layout);
    	ListView listView = getListView();
    	
    	// Enable filtering for the contents of the list view.
    	// The filtering logic should be provided
    	// listView.setTextFilterEnabled(true);  
    	
    	
// TODO: Get the list from the Societies Content Provider


    	// The Adapter provides access to the data items.
    	// The Adapter is also responsible for making a View for each item in the data set.
    	//  Parameters: Context, Layout for the row, ID of the View to which the data is written, Array of data

//TODO: customize the layout for the row is necessary
// At the moment a simple string is used as for disaster.

    	iDisasterApplication.getInstance().feedAdapter = new ArrayAdapter<String> (this,
		R.layout.disaster_list_item, R.id.disaster_item, iDisasterApplication.getInstance().feedContentList);

    	// Assign adapter to ListView

    	listView.setAdapter(iDisasterApplication.getInstance().feedAdapter);

    	// Add listener for short click.
    	// 
    	listView.setOnItemClickListener(new OnItemClickListener() {
    		public void onItemClick (AdapterView<?> parent, View view,
    			int position, long id) {
// TODO: Remove code for testing the correct setting of preferences 
    			Toast.makeText(getApplicationContext(),
    				"Click ListItem Number   " + (position+1) + "   " + iDisasterApplication.getInstance().feedContentList.get (position), Toast.LENGTH_LONG)
    				.show();

// TODO: Eventually start new activity if something more to show about the feed.    			

// The activity is kept on stack (check also that "noHistory" is not set in Manifest)
// Should it be removed?
//    			finish();
    			}
    		
    		});
  		}
  
	}

    /**
     * onResume is called at start of the active lifetime.
     * The lists of disaster is retrieved from SocialProvider and assigned to 
     * view.
    */

    // TODO: check if it is necessary to use an adaptater as the list of disasters is
    // retrieved anyway.
        
        @Override
    	protected void onResume() {
    		super.onResume();
    	
    		if (! iDisasterApplication.testDataUsed) {			// Test data are set in onCreate - see explanation above
    															// Data from content provider are fetched every time the activity becomes visible

    			if (feedAdapter!= null) feedAdapter.clear();

    // All information is fetched from the Social Provider. Preferences are no longer used.
//    			iDisasterApplication.getInstance().setDisasterTeamName // reset user preferences
//    			(getString(R.string.noPreference));
    		
//    			getFeeds();
//    			assignAdapter ();
    		}
    	}

/**
 * onCreateOptionsMenu expands the activity menu for this activity tab.
*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

    	//The FIXED menu is set by the TabActivity.
// I am uncertain why the call to the super class leads to the creation
// of the fixed menu set by the TabActivity (DisasterActivity)
    	super.onCreateOptionsMenu(menu);
    	
    	menu.setGroupVisible(R.id.disasterMenuFeed, true);
    	return true;
    }

 /**
  * onOptionsItemSelected handles the selection of an item in the activity menu.
  */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

		// The TabActivity handles items in the FIXED menu.
// I am uncertain why the call to the super class leads to handling
// of a command in the fixed menu by the TabActivity (DisasterActivity)
    	super.onOptionsItemSelected(item);
    	
    	switch (item.getItemId()) {

    		case R.id.disasterMenuAddFeed:    			
//TODO: Remove code for testing the correct setting of preferences 
//    			Toast.makeText(getApplicationContext(),
//    				"Menu item chosen: Add feed", Toast.LENGTH_LONG)
//    				.show();
    			startActivity(new Intent(FeedListActivity.this, FeedAddActivity.class));
    		break;
    		
    		default:
    		break;
    	}
    	return true;
    }

}
