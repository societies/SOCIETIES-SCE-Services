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

//import org.societies.api.cis.management.ICisManager;
//import org.societies.api.cis.management.ICisOwned;

import java.util.ArrayList;

import org.societies.android.api.cis.SocialContract;

import org.societies.thirdpartyservices.idisaster.R;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
//import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;



/**
 * This activity allows the users to manage the disaster teams they own and
 * or the disaster teams they subscribe to.
 * 
 * @author Jacqueline.Floch@sintef.no
 *
 */

public class DisasterListActivity extends ListActivity {

	ContentResolver resolver;
	Cursor cursor;
	
	ArrayAdapter<String> disasterAdapter;
	ListView listView;
	

    @Override
    protected void onCreate (Bundle savedInstanceState) {

    	super.onCreate(savedInstanceState);
    	setContentView (R.layout.disaster_list_layout);
    	listView = getListView();
    	resolver = getContentResolver();
    	
		if (iDisasterApplication.testDataUsed) {			// Test data
															// The adapter should not be set in onResume since the Android Adapter 
															// mechanism is used for update. See DisasterCreateactivity.
			iDisasterApplication.getInstance().disasterAdapter = new ArrayAdapter<String> (this,
					R.layout.disaster_list_item, R.id.disaster_item, iDisasterApplication.getInstance().disasterNameList);

			listView.setAdapter(iDisasterApplication.getInstance().disasterAdapter);
		} 													// Otherwise the data are fetched in onResume
    	

    	// Add listener for short click.
       	listView.setOnItemClickListener(new OnItemClickListener() {
    		public void onItemClick (AdapterView<?> parent, View view,
    			int position, long id) {
    			if (iDisasterApplication.testDataUsed) {			// Test data  			
    				iDisasterApplication.getInstance().disasterTeamName = iDisasterApplication.getInstance().disasterNameList.get(position);
// Store the selected disaster in application preferences
//    				iDisasterApplication.getInstance().setDisasterTeamName (name);
    			} else {											// Fetch data from Social Provide
//TODO: get name in from Social Provider
    				iDisasterApplication.getInstance().disasterTeamName = "Name will be be fetched";
    			}
    			
    			Toast.makeText(getApplicationContext(),
        				"Click ListItem Number   " + (position+1) + "   " + iDisasterApplication.getInstance().disasterTeamName,
        				Toast.LENGTH_LONG).show();

    			// Start the Disaster Activity
    			startActivity (new Intent(DisasterListActivity.this, DisasterActivity.class));

// The activity is kept on stack (check also that "noHistory" is not set in Manifest
//    			finish();
    		}
    	});	
       	
    	// Add listener for long click
    	// listView.setOnItemLongClickListener(new DrawPopup());
    }

    /**  */
    
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

			if (disasterAdapter!= null) disasterAdapter.clear();

// All information is fetched from the Social Provider. Preferences are no longer used.
//			iDisasterApplication.getInstance().setDisasterTeamName // reset user preferences
//			(getString(R.string.noPreference));
		
			getDisasterTeams();
			assignAdapter ();
		}
	}


/**
 * onPause releases all data.
 */
       
	@Override
    protected void onPause() {
    	super.onPause ();
    	
//    	cursor.close();    	
    }

    /** Called when resuming a previous activity (for instance using back button) */
//    @Override
//    protected void onPause () {
//    	super.onPause ();
//    }

/**
 * getDisasters retrieves the list of disaster teams from Social Provider.
 */
	
	// TODO: The following retrieve the list of communities - not the ones I am member of
	private void getDisasterTeams () {

		Uri uri = SocialContract.Communities.CONTENT_URI;

		String[] projection = new String[] { 
				SocialContract.Communities.NAME };

//		String[] projection = new String[] { SocialContract.MyCommunity._ID,
//				SocialContract.MyCommunity.DISPLAY_NAME };
//		String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = '"
//				+ ("1") + "'";

// TODO: select only string I am member of		
		String selection = null;

		String[] selectionArgs = null;

// TODO: check that ordering is OK
		String sortOrder = SocialContract.Communities.NAME
				+ " COLLATE LOCALIZED ASC";

		cursor = resolver.query(uri, projection, selection, selectionArgs,sortOrder);

		return;

//
// When using managedQuery(), the activity keeps a reference to the cursor and close it
// whenever needed (in onDestroy() for instance.) 
// When using a contentResolver's query(), the developer has to manage the cursor as a sensitive
// resource. If you forget, for instance, to close() it in onDestroy(), you will leak 
// underlying resources (logcat will warn you about it.)
//
	}

/**
 * assignAdapter assigns data to display to adapter and adapter to view.
 */
	private void assignAdapter () {
		
		if (cursor == null) {
			// assign an empty List to Adapter
	    	disasterAdapter = new ArrayAdapter<String> (this,
	    			R.layout.disaster_list_item, R.id.disaster_item, new ArrayList<String> ());

	    	// associate adapter to list (in order to be able to display text?)
	    	listView.setAdapter(disasterAdapter);    	

	    	iDisasterApplication.getInstance().showDialog (this,
					"Unable to retrieve user information from SocialProvider",
					 getString(R.string.dialogOK));
// TODO: should terminate somehow here
		} else {
			if (cursor.getCount() == 0) {
				// assign an empty List to Adapter
		    	disasterAdapter = new ArrayAdapter<String> (this,
		    			R.layout.disaster_list_item, R.id.disaster_item, new ArrayList<String> ());		
		    	listView.setAdapter(disasterAdapter);
			} else {				
				ArrayList<String> disasterList = new ArrayList<String> ();
				while (cursor.moveToNext()) {
					String displayName = cursor.getString(cursor
							.getColumnIndex(SocialContract.Communities.NAME));
					disasterList.add (displayName);

				}
		    	disasterAdapter = new ArrayAdapter<String> (this,
		    			R.layout.disaster_list_item, R.id.disaster_item, disasterList);
		    	listView.setAdapter(disasterAdapter);
			}
		}
	}

    
/**
 * onCreateOptionsMenu creates the activity menu.
 */
 
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		menu.clear();
		getMenuInflater().inflate(R.menu.disaster_list_menu, menu);

//		It is possible to set up a variable menu		
//			menu.findItem (R.id....).setVisible(true);

		return true;
	}

/**
 * onOptionsItemSelected handles the selection of an item in the activity menu.
 */
	@Override
	public boolean onOptionsItemSelected (MenuItem item) {

		switch (item.getItemId()) {
		case R.id.disasterMenuAdd:
			startActivity(new Intent(DisasterListActivity.this, DisasterCreateActivity.class));
			break;
		default:
			break;
		}
		return true;
	}

}