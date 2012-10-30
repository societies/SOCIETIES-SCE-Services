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

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

//Related to the creation of a dialog box
//import android.app.AlertDialog;
//import android.content.DialogInterface;

import org.societies.android.api.cis.SocialContract;
import org.societies.thirdpartyservices.idisaster.R;


/**
 * This activity allows the user to look up members
 * in a directory, more to be added.
 * 
 * @author Jacqueline.Floch@sintef.no
 *
 */
public class MemberListActivity extends ListActivity {
	
	ContentResolver resolver;
	int members;						// keep track of number of members
	
	// Static classes that can be accessed by othe activities e.g. MemberAddActivity
	static Cursor memberCursor;			// used for the members in the selected team
	
	ArrayAdapter<String> memberAdapter;
	ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
    	super.onCreate(savedInstanceState);
    	setContentView (R.layout.member_list_layout);
    	listView = getListView();
    	resolver = getContentResolver();
    	
    	//TODO: customize the layout for the row is necessary
    	// At the moment a simple string is used as for disaster.



		if (iDisasterApplication.testDataUsed) {			// Test data
															// TODO: check this: The adapter should not be set in onResume since the Android Adapter 
															// mechanism is used for update? See DisasterCreateactivity.
	    	iDisasterApplication.getInstance().memberAdapter = new ArrayAdapter<String> (this,
	    			R.layout.disaster_list_item, R.id.disaster_item, iDisasterApplication.getInstance().memberNameList);
			// Assign adapter to ListView
			listView.setAdapter(iDisasterApplication.getInstance().memberAdapter);
			}

    	// Add listener for short click.
    	listView.setOnItemClickListener(new OnItemClickListener() {
    		public void onItemClick (AdapterView<?> parent, View view,
    			int position, long id) {
    			
    			if (iDisasterApplication.testDataUsed) {			// Test data
    				// TODO: Remove code for testing the correct setting of preferences 
        			Toast.makeText(getApplicationContext(),
        				"Click ListItem Number   " + (position+1) + "   " + iDisasterApplication.getInstance().memberNameList.get (position), Toast.LENGTH_LONG)
        				.show();    				
    			} else {
    				if ((position) < members) {			// Retrieve information from members in the team
    					memberCursor.moveToPosition(position);
     					String memberGlobalId = memberCursor.getString(memberCursor
    							.getColumnIndex(SocialContract.People.GLOBAL_ID));
       					String memberName =  memberCursor.getString(memberCursor
    							.getColumnIndex(SocialContract.People.NAME));
        				// TODO: Start the MemberDetails Activity
    					// TODO: Add parameters to Intent
        				// startActivity (new Intent(MemberListActivity.this, MemberDetailsActivity.class));

// The activity is kept on stack (check also that "noHistory" is not set in Manifest)
// Should it be removed?
//       				finish();
    					
    					// Test code
            			Toast.makeText(getApplicationContext(),
                				"Selected: " + memberName + " " + memberGlobalId, Toast.LENGTH_LONG)
                				.show();    				

    				} else {
    					// Should never happen..
    				}
    			}
    		}
    	});
    	
    	//TODO:  Add listener for long click
    	// listView.setOnItemLongClickListener(new DrawPopup());

    } // onCreate
    
    
/**
 * onResume is called at start of the active lifetime.
 * The list of members is retrieved from SocialProvider and assigned to 
 * view.
 * The data are fetched each time the activity becomes visible as these data
 * may be changed by other users (info fetched from the Cloud)
 */
            
	@Override
	protected void onResume() {
		super.onResume();
    	
		// Test data are set in onCreate - see explanation above
    		
        if (! iDisasterApplication.testDataUsed) {
        	if (memberAdapter!= null) memberAdapter.clear();
			if (getMembers()						// Retrieve members from the selected team
					.equals(iDisasterApplication.getInstance().QUERY_EXCEPTION)) {
				showQueryExceptionDialog ();	// Exception: Display dialog
			}

        assignAdapter ();
        
        }
	}

/**
 * onPause releases all data.
 * Called when resuming a previous activity (for instance using back button)
 */		       
	@Override
	protected void onPause() {
			
		super.onPause ();
			
// TODO: check the following:
	// When using managedQuery(), the activity keeps a reference to the cursor and close it
    // whenever needed (in onDestroy() for instance.) 
    // When using a contentResolver's query(), the developer has to manage the cursor as a sensitive
    // resource. If you forget, for instance, to close() it in onDestroy(), you will leak 
    // underlying resources (logcat will warn you about it.)
    //

//		memberCursor.close();
	}


/**
 * getFeed retrieves the list of activity feeds for the selected disaster team
 * from Social Provider.
 */
	private String getMembers () {
		
		if (memberCursor != null) {
			memberCursor.close();		// "close" releases data but does not set to null
			memberCursor = null;
		}
		
		// Step 1: get GLOBAL_ID_MEMBERs for members in the selected CIS
		Uri membershipUri = SocialContract.Membership.CONTENT_URI;
					
		String[] membershipProjection = new String[] {
				SocialContract.Membership.GLOBAL_ID_MEMBER};

		String membershipSelection = SocialContract.Membership.GLOBAL_ID_COMMUNITY + "= ?";
		String[] membershipSelectionArgs = new String[] {iDisasterApplication.getInstance().selectedTeam.globalId};	// For the selected CIS
	
		Cursor membershipCursor;
		try {
			membershipCursor= resolver.query(membershipUri, membershipProjection,
					membershipSelection, membershipSelectionArgs, null /* sortOrder*/);			
		} catch (Exception e) {
			iDisasterApplication.getInstance().debug (2, "Query to "+ membershipUri + "causes an exception");
			return iDisasterApplication.getInstance().QUERY_EXCEPTION;
		}
	
		// Step 2: retrieve the members with the GLOBAL_ID_MEMBERs retrieved above
		
		if (membershipCursor == null) {		// No cursor was set
			iDisasterApplication.getInstance().debug (2, "membershipCursor was not set to any value");
			return iDisasterApplication.getInstance().QUERY_EMPTY;
		}
		
		if (membershipCursor.getCount() == 0) {		// The user is not member of any community
			return iDisasterApplication.getInstance().QUERY_EMPTY;
		}
		
		// Get GLOBAL_ID and NAME for members in the selected CIS
			 
		Uri peopleUri = SocialContract.People.CONTENT_URI;
				
		String[] membersProjection = new String[] {
					SocialContract.People.GLOBAL_ID,
					SocialContract.People.NAME};
			
		// Build selection string and selectionArgs string
			
		boolean first = true;
		String membersSelection = new String();
		ArrayList <String> membersSelectionArgs = new ArrayList <String> ();
		while (membershipCursor.moveToNext()) {
			if (first) {
				first = false;
				membersSelection = SocialContract.People.GLOBAL_ID + "= ?";
				membersSelectionArgs.add (membershipCursor.getString(
								(membershipCursor.getColumnIndex(SocialContract.Membership.GLOBAL_ID_MEMBER))));
			} 
			else {
				membersSelection = membersSelection + " OR " +
											   SocialContract.People.GLOBAL_ID + "= ?";
				membersSelectionArgs.add (membershipCursor.getString(
						(membershipCursor.getColumnIndex(SocialContract.Membership.GLOBAL_ID_MEMBER))));
			}
		}
		
		try {
			memberCursor = resolver.query (peopleUri, membersProjection,membersSelection, 
					membersSelectionArgs.toArray(new String[membersSelectionArgs.size()]),
					SocialContract.People.NAME + " COLLATE LOCALIZED ASC" );
		} catch (Exception e) {
			iDisasterApplication.getInstance().debug (2, "Query to "+ peopleUri + "causes an exception");
			return iDisasterApplication.getInstance().QUERY_EXCEPTION;
		}
		
		return iDisasterApplication.getInstance().QUERY_SUCCESS;

	}

/**
 * assignAdapter assigns data to display to adapter and adapter to view.
 */
	private void assignAdapter () {
				
		members= 0;
				
		ArrayList<String> memberList = new ArrayList<String> ();

//			An empty List will be assigned to Adapter
//			if (feedCursor == null) {

//			An empty List will be assigned to Adapter
//				if (feedCursor == null) {
//					if (feedCursor.getCount() == 0) {
				
		if (memberCursor != null) {
			if (memberCursor.getCount() != 0) {
				while (memberCursor.moveToNext()) {
					members++;
					String displayName = memberCursor.getString(memberCursor
							.getColumnIndex(SocialContract.People.NAME));
					memberList.add (displayName);
				}
			}
		}
		memberAdapter = new ArrayAdapter<String> (this,
				R.layout.disaster_list_item, R.id.disaster_item, memberList);
				
		listView.setAdapter(memberAdapter);

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
    	
    	menu.setGroupVisible(R.id.disasterMenuMember, true);
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

    		case R.id.disasterMenuAddMember:
////TODO: Remove code for testing the correct setting of preferences 
//    			Toast.makeText(getApplicationContext(),
//    				"Menu item chosen: Add member", Toast.LENGTH_LONG)
//    				.show();
    			
    			startActivity(new Intent(MemberListActivity.this, MemberAddActivity.class));
    		break;
    		
    		default:
    		break;
    	}
    	return true;
    }

/**
 * showQueryExceptionDialog displays a dialog to the user.
 * In this case, the activity does not terminate since the other
 * activities in the TAB may still work.
 */
        			
	private void showQueryExceptionDialog () {
    	AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setMessage(getString(R.string.dialogMemberQueryException))
        			.setCancelable(false)
          			.setPositiveButton (getString(R.string.dialogOK), new DialogInterface.OnClickListener() {
          				public void onClick(DialogInterface dialog, int id) {
          					return;
          				}
          			});
        AlertDialog alert = alertBuilder.create();
        alert.show();	
	}

}
