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

//import org.societies.api.cis.management.ICisManager;
//import org.societies.api.cis.management.ICisOwned;

import com.disaster.idisaster.R;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

//TODO: Add import
//import org.societies.android.platform.SocialContract;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;
import android.test.mock.MockContentResolver;


/**
 * This activity allows the users to manage the disaster teams they own and
 * or the disaster teams they subscribe to.
 * 
 * @author Jacqueline.Floch@sintef.no
 *
 */

public class DisasterListActivity extends ListActivity {


    @Override
    protected void onCreate (Bundle savedInstanceState) {

    	super.onCreate(savedInstanceState);
    	setContentView (R.layout.disaster_list_layout);
    	ListView listView = getListView();

    	// Enable filtering for the contents of the list view.
    	// The filtering logic should be provided
    	// listView.setTextFilterEnabled(true);  
    	
    	
// TODO: Get the list from the Societies Content Provider instead of static data
    	
    	iDisasterApplication.getInstance().disasterAdapter = new ArrayAdapter<String> (this,
		R.layout.disaster_list_item, R.id.disaster_item, iDisasterApplication.getInstance().disasterNameList);

// TODO: Remove test code
//    			//1- Create local ContentValues to hold the community data.
//    			ContentValues initialValues = new ContentValues();
//    			initialValues.put(SocialContract.Community.NAME , "XYZ");
//    			initialValues.put(SocialContract.Community.OWNER_ID , "babak@societies.org");
//    			initialValues.put(SocialContract.Community.CREATION_DATE , "today");
    			
//    			// Call insert in SocialProvider to initiate insertion
//    			Uri newCommunityUri= resolver.insert(SocialContract.Community.CONTENT_URI , 
//    					initialValues);
// TODO: End of test code

// TODO: Define query
//    			//What to get:
//    			String[] projection ={
//    					SocialContract.Community._ID,
//    					SocialContract.Community.NAME
//    				};
//    			//WHERE CIS type = disaster
//    			String selection = SocialContract.Community.TYPE + " = " + "disaster";
//    			Cursor cursor = resolver.query(SocialContract.Community.CONTENT_URI,
//    					projection, selection, null, null);
//
    	
// TODO: check query result
//
//    			if (cursor == null) {						// An error has occurred.
//  The activity is not visible yet. To display text, it should be made visible first
//     	    	// Assign a "fake" adapter to ListView?
//	
//				listView.setAdapter(iDisasterApplication.getInstance().disasterAdapter);
//				display error dialog and close application?
//
//    	
//    			} else if (cursor.getCount () <1) {			// The cursor is empty
//  The activity is not visible yet. To display text, it should be made visible first
//	    	// Assign adapter (related to empty cursor) to ListView?
//			// when list is empty the text message in layout is displayed...    	
//
//				listView.setAdapter(iDisasterApplication.getInstance().disasterAdapter);
//
//
//    			} else {									// Display data
    	
//		
////		if (!cursor.moveToFirst()) return;
//


    	// The Adapter provides access to the data items.
    	// The Adapter is also responsible for making a View for each item in the data set.
    	//  Parameters: Context, Layout for the row, ID of the View to which the data is written, Array of data

    	// Assign adapter to ListView

    	listView.setAdapter(iDisasterApplication.getInstance().disasterAdapter);

    	// Add listener for short click.

       	listView.setOnItemClickListener(new OnItemClickListener() {
    		public void onItemClick (AdapterView<?> parent, View view,
    			int position, long id) {
    			// Store the selected disaster in preferences
//TODO: Add unique ID to CIS instead of name since name can be duplicated 
            	iDisasterApplication.getInstance().setDisasterName (iDisasterApplication.getInstance().disasterNameList.get (position));

// TODO: Remove code for testing the correct setting of preferences 
    			Toast.makeText(getApplicationContext(),
    				"Click ListItem Number   " + (position+1) + "   " + iDisasterApplication.getInstance().disasterNameList.get (position), Toast.LENGTH_LONG)
    				.show();

    			// Start the Disaster Activity
    			startActivity (new Intent(DisasterListActivity.this, DisasterActivity.class));
    			
// The activity is kept on stack (check also that "noHistory" is not set in Manifest
//    			finish();

    		}
    	});	
       	
//		}


       	
    	// Add listener for long click
    	// listView.setOnItemLongClickListener(new DrawPopup());


    }

    /** Called at start of the active lifetime. */
    @Override
	protected void onResume() {
		super.onResume();
    	iDisasterApplication.getInstance().setDisasterName 
		(getString(R.string.noPreference));					// reset user preferences

	}//onResume

    /** Called when resuming a previous activity (for instance using back button) */
//    @Override
//    protected void onPause () {
//    	super.onPause ();
//    }

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
	public boolean onOptionsItemSelected(MenuItem item) {

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