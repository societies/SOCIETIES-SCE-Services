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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import org.societies.android.api.cis.SocialContract;
import org.societies.thirdpartyservices.idisaster.R;

/**
 * This activity allows the users to manage the disaster teams they own and
 * or the disaster teams they subscribe to.
 * 
 * @author Jacqueline.Floch@sintef.no
 *
 */

public class DisasterListActivity extends ListActivity {

	private ContentResolver resolver;
	private Cursor ownTeamCursor;				// used for the teams the user owns
	private int ownTeams;						// keep track of number of own teams
	private Cursor memberTeamCursor;			// used for the teams the user is member of
	private int memberTeams;					// keep track of number of member teams
		
	private ArrayAdapter<String> disasterAdapter;
	private ListView listView;
	

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
    				iDisasterApplication.getInstance().selectedTeam.name = iDisasterApplication.getInstance().disasterNameList.get(position);
    				iDisasterApplication.getInstance().selectedTeam.globalId = Integer.toString(position);
// Store the selected disaster in application preferences
//    				iDisasterApplication.getInstance().setDisasterTeamName (name);
    				
    			} else {
    				if (position < ownTeams) {	// Retrieve information from list of teams the user owns
    					ownTeamCursor.moveToPosition(position);
    					iDisasterApplication.getInstance().selectedTeam.name =  ownTeamCursor.getString(ownTeamCursor
    							.getColumnIndex(SocialContract.Communities.NAME));
    					iDisasterApplication.getInstance().selectedTeam.globalId = ownTeamCursor.getString(ownTeamCursor
    							.getColumnIndex(SocialContract.Communities.GLOBAL_ID));
        				iDisasterApplication.getInstance().selectedTeam.ownFlag = true;
    				} else if ((position-ownTeams) < memberTeams){						// Retrieve information from list of teams the user is member of
    					memberTeamCursor.moveToPosition(position-ownTeams);
    					iDisasterApplication.getInstance().selectedTeam.name =  memberTeamCursor.getString(memberTeamCursor
    							.getColumnIndex(SocialContract.Communities.NAME));
    					iDisasterApplication.getInstance().selectedTeam.globalId = memberTeamCursor.getString(memberTeamCursor
    							.getColumnIndex(SocialContract.Communities.GLOBAL_ID));
        				iDisasterApplication.getInstance().selectedTeam.ownFlag = false;
    				}
    			}
// TODO: Remove test code    			
//    			Toast.makeText(getApplicationContext(),
//        				"Click ListItem Number   " + (position+1) + "   " + iDisasterApplication.getInstance().selectedTeam.name,
//        				Toast.LENGTH_LONG).show();

    			// Start the Disaster Activity
    			startActivity (new Intent(DisasterListActivity.this, DisasterActivity.class));

// The activity is kept on stack (check also that "noHistory" is not set in Manifest
//    			finish();
    		}
    	});	
       	
//TODO:  Add listener for long click
    	// listView.setOnItemLongClickListener(new DrawPopup());
       	
    } // onCreate
    
/**
 * onResume is called at start of the active lifetime.
 * The list of disaster teams is retrieved from SocialProvider and assigned to 
 * view.
 * The data are fetched each time the activity becomes visible as these data
 * may be changed by other users (info fetched from the Cloud)
*/
    
    @Override
	protected void onResume() {
		super.onResume();
	
		// Test data are set in onCreate - see explanation above

		if (! iDisasterApplication.testDataUsed) {
			if (disasterAdapter!= null) disasterAdapter.clear();	
			if (getOwnDisasterTeams()			// Retrieve disaster teams I am owner of
					.equals(iDisasterApplication.getInstance().QUERY_EXCEPTION)) {
				showQueryExceptionDialog ();	 // Exception: Display dialog and terminates activity
			} 
			if (getMemberDisasterTeams()		// Retrieve teams I am member of
					.equals(iDisasterApplication.getInstance().QUERY_EXCEPTION)) {
				showQueryExceptionDialog ();	// Exception: Display dialog and terminates activity
			}
			assignAdapter ();			// Display them
		}		
	}


/**
 * onPause releases all data.
 * Called when resuming a previous activity (for instance using back button)
 */
       
	@Override
    protected void onPause() {
    	super.onPause ();
    	
//    	ownTeamCursor.close();
//    	memberTeamCursor.close();    	

    }

/**
 * getOwnDisasterTeams retrieves the list of disaster teams owned by the user
 * from SocialProvider.
 */
	
	private String getOwnDisasterTeams () {
		
		if (ownTeamCursor != null) {
			ownTeamCursor.close();		// "close" releases data but does not set to null
			ownTeamCursor = null;
		}

		Uri uri = SocialContract.Communities.CONTENT_URI;
		
		String[] projection = new String[] {
				SocialContract.Communities.GLOBAL_ID,	// Retrieve CIS global ID
				SocialContract.Communities.NAME};		// Retrieve CIS name (will be displayed to the user)

//		All communities
//		String selection = null;
//		String[] selectionArgs = null;

//		TEST CODE: communities of type disaster
//		String selection = SocialContract.Communities.TYPE + " = disaster";
//		String[] selectionArgs = null;

//		TEST CODE: communities I am owning
//		String selection = SocialContract.Communities.OWNER_ID + "= ?"; 
//		String[] selectionArgs = new String[] {iDisasterApplication.getInstance().me.globalId};

// TODO: Add a check on user identity?

		String selection = SocialContract.Communities.TYPE + "= ? AND " 
					     + SocialContract.Communities.OWNER_ID + "= ?";
		String[] selectionArgs = new String[] {"disaster",											// The CIS type is "disaster"
												iDisasterApplication.getInstance().me.globalId};	// The user is owner

		String sortOrder = SocialContract.Communities.NAME
				+ " COLLATE LOCALIZED ASC";					// Alphabetic order (to reverse order, use "DESC" instead of "ASC"
		
		try {
			ownTeamCursor = resolver.query(uri, projection, selection, selectionArgs,sortOrder);			
		} catch (Exception e) {
			iDisasterApplication.getInstance().debug (2, "Query to "+ uri + "causes an exception");
    		return iDisasterApplication.getInstance().QUERY_EXCEPTION;
		}

		return iDisasterApplication.getInstance().QUERY_SUCCESS;

	}

	
/**
 * getMemberDisasterTeams retrieves the list of disaster teams the user is
 * member of from SocialProvider.
 */

	private String getMemberDisasterTeams () {
		
		if (memberTeamCursor != null) {
			memberTeamCursor.close();		// "close" releases data but does not set to null
			memberTeamCursor = null;
		}

		// Step 1: get GLOBAL_IDs for CIS I am member of
		Uri membershipUri = SocialContract.Membership.CONTENT_URI;		
		
		String[] membershipProjection = new String[] {
				SocialContract.Membership.GLOBAL_ID_COMMUNITY};

		String membershipSelection = SocialContract.Membership.GLOBAL_ID_MEMBER + "= ?";
		String[] membershipSelectionArgs = new String[] {iDisasterApplication.getInstance().me.globalId};	// The user is owner
		
		Cursor membershipCursor;
		try {
			membershipCursor = resolver.query(membershipUri, membershipProjection,
					membershipSelection, membershipSelectionArgs, null /* sortOrder*/);
		} catch (Exception e) {
			iDisasterApplication.getInstance().debug (2, "Query to "+ membershipUri + "causes an exception");
    		return iDisasterApplication.getInstance().QUERY_EXCEPTION;
		}

		// Step 2: retrieve the communities with the GLOBAL_IDs retrieved above

		if (membershipCursor == null) {		// No cursor was set
			iDisasterApplication.getInstance().debug (2, "No information can be retrieved in membershipCursor");
			return iDisasterApplication.getInstance().QUERY_EMPTY;
		} 
		
		if (membershipCursor.getCount() == 0) {		// The user is not member of any community
			return iDisasterApplication.getInstance().QUERY_EMPTY;
		}
		
		// Get GLOBAL_ID and NAME for CIS I am member of
			 
		Uri communitiesUri = SocialContract.Communities.CONTENT_URI;
				
		String[] communitiesProjection = new String[] {
					SocialContract.Communities.GLOBAL_ID,
					SocialContract.Communities.NAME};
			
		// Build selection string and selectionArgs string
		boolean first = true;
		String communitiesSelection = new String();
		ArrayList <String> communitiesSelectionArgs = new ArrayList <String> ();
		while (membershipCursor.moveToNext()) {
			if (first) {
				first = false;
				communitiesSelection = SocialContract.Communities.GLOBAL_ID + "= ?";
				communitiesSelectionArgs.add (membershipCursor.getString(
								(membershipCursor.getColumnIndex(SocialContract.Membership.GLOBAL_ID_COMMUNITY))));
			} else {
				communitiesSelection = communitiesSelection +
									   " AND " +  SocialContract.Communities.GLOBAL_ID + "= ?";
				communitiesSelectionArgs.add (membershipCursor.getString(
								(membershipCursor.getColumnIndex(SocialContract.Membership.GLOBAL_ID_COMMUNITY))));
			}
		}
		membershipCursor.close();		// "close" releases no more needed data

//TODO: not sure the current cursor, if any, should be close first		
		try {
			memberTeamCursor = resolver.query (communitiesUri, communitiesProjection, communitiesSelection, 
					communitiesSelectionArgs.toArray(new String[communitiesSelectionArgs.size()]),
					null /* sortOrder*/);			
		} catch (Exception e) {
			iDisasterApplication.getInstance().debug (2, "Query to "+ membershipUri + "causes an exception");
    		return iDisasterApplication.getInstance().QUERY_EXCEPTION;
		}

		return iDisasterApplication.getInstance().QUERY_SUCCESS;
		
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
		
		ownTeams= 0;
		memberTeams= 0;
		
		ArrayList<String> disasterList = new ArrayList<String> ();

//		An empty List will be assigned to Adapter
//		if ((ownTeamCursor == null) && (memberTeamCursor == null)) {

//		An empty List will be assigned to Adapter
//		if ((ownTeamCursor != null) && (memberTeamCursor != null)) {
//			if ((ownTeamCursor.getCount() == 0) && (memberTeamCursor.getCount() == 0)) {
		
		if (ownTeamCursor != null) {
			if (ownTeamCursor.getCount() != 0) {
				while (ownTeamCursor.moveToNext()) {
					ownTeams++;
					String displayName = "owned: " + ownTeamCursor.getString(ownTeamCursor
							.getColumnIndex(SocialContract.Communities.NAME));
					disasterList.add (displayName);
				}
			}
		}
		if (memberTeamCursor != null) {
			if (memberTeamCursor.getCount() != 0) {
				while (memberTeamCursor.moveToNext()) {
					memberTeams++;
					String displayName = "member of: " + memberTeamCursor.getString(memberTeamCursor
							.getColumnIndex(SocialContract.Communities.NAME));
					disasterList.add (displayName);
				}
			}
		}
    	disasterAdapter = new ArrayAdapter<String> (this,
	    			R.layout.disaster_list_item, R.id.disaster_item, disasterList);
		
		listView.setAdapter(disasterAdapter);
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

/**
 * showQueryExceptionDialog displays a dialog to the user and terminates activity.
 */
			
	private void showQueryExceptionDialog () {
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
		alertBuilder.setMessage(getString(R.string.dialogQueryException))
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

}