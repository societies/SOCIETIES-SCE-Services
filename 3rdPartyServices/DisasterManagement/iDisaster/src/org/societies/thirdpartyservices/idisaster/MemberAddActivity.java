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
import java.util.ArrayList;
import java.util.Date;

import org.societies.android.api.cis.SocialContract;
import org.societies.thirdpartyservices.idisaster.R;
import org.societies.thirdpartyservices.idisaster.data.SocialActivity;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * Activity for adding members to the selected disaster team (community).
 * 
 * @author Jacqueline.Floch@sintef.no
 *
 */
public class MemberAddActivity extends ListActivity implements OnClickListener {
//implements OnMultiChoiceClickListener {

	ContentResolver resolver;
	Cursor peopleCursor;			// used for the persons in the registry
	int people;						// keep track of number of persons
	
	ArrayAdapter<String> peopleAdapter;
	ListView listView;
	
	ArrayList <Integer> peopleMap = new ArrayList <Integer> ();		// Maps person position in list on UI with person cursor position
																	// (Persons already members are not displayed)

	Button memberAddButton;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
    	setContentView (R.layout.member_add_layout);
    	listView = getListView();
    	resolver = getContentResolver();

    	// Add listener for long click on an item in the list
    	listView.setOnItemLongClickListener (new OnItemLongClickListener() {
    		public boolean onItemLongClick (AdapterView<?> parent, View view,
    			int position, long id) {
    			
    			if ((position) < people) {			// Retrieve information for selected person
    				peopleCursor.moveToPosition(peopleMap.get(position));
    				String personName = peopleCursor.getString(peopleCursor.getColumnIndex(SocialContract.People.NAME));
        			Toast.makeText(getApplicationContext(),
            				"No more member information for " + personName, Toast.LENGTH_LONG)
            				.show();    				
    			} else {
//    					// Should never happen..
    			}
    			
    			return true;
    		}
    	});

	    // Add click listener to button
    	memberAddButton = (Button) findViewById(R.id.memberButton);
    	memberAddButton.setOnClickListener(this);
    } 
    
/**
 * onResume is called at start of the active lifetime.
 * The list of people is retrieved from SocialProvider and assigned to 
 * view.
 * The data are fetched each time the activity becomes visible as these data
 * may be changed by other users (info fetched from the Cloud)
 */
                
    @Override
    protected void onResume() {
    	super.onResume();
    	
        if (peopleAdapter!= null) peopleAdapter.clear();
    	if (getPeople()						// Retrieve people from the registry
    				.equals(iDisasterApplication.getInstance().QUERY_EXCEPTION)) {
    		showQueryExceptionDialog ();	// Exception: Display dialog and terminates activity
   		}
    	assignAdapter ();
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

//    	memberCursor.close();
    }

/**
 * onClick is called when button is clicked because
 * the OnClickListener is assigned to the button
 */

	public void onClick(View view) {

    	if(view == memberAddButton){
    		// check if any member selected
    		
			if (addNewMembers()						// Insert members for the selected team
				.equals(iDisasterApplication.getInstance().INSERT_EXCEPTION)) {
				showQueryExceptionDialog ();	// Exception: Display dialog (and terminates activity)
				// Go back to the previous activity
			} else {
				// The members will be added to the adapter (and adapter to view) on resume of MemberListActivity 
				finish ();
			}
 		
    	} else { 				// Unknown button was clicked: should never happen
			iDisasterApplication.getInstance().debug (2, "Button click error");
    		finish();
    		return;
    	}
    }


/**
 * getPeople retrieves the list of people in the people directory.
 */
    private String getPeople () {
    	
		if (peopleCursor != null) {
			peopleCursor.close();		// "close" releases data but does not set to null
			peopleCursor = null;
		}
    		
    	Uri peopleUri = SocialContract.People.CONTENT_URI;
    					
    	String[] peopleProjection = new String[] {
    				SocialContract.People._ID,
    				SocialContract.People.NAME};
    	
    	try {
    		peopleCursor= resolver.query(peopleUri, peopleProjection,
    					null /* selection */, null /* selectionArgs */, null /* sortOrder*/);			
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
    				
    		people= 0;
    		if (peopleMap != null) {
    			peopleMap.clear();	// Reset mapping between UI list and poeple list
    			
    		} else {				// Should never happen...
    			peopleMap = new ArrayList <Integer> ();
    		}
    				
    		ArrayList<String> peopleList = new ArrayList<String> ();

//    			An empty List will be assigned to Adapter
//    			if (peopleCursor == null) {

//    			An empty List will be assigned to Adapter
//    				if (peopleCursor == null) {
//    					if (peopleCursor.getCount() == 0) {
    				
    		if (peopleCursor != null) {
    			if (peopleCursor.getCount() != 0) {
    				while (peopleCursor.moveToNext()) {
    					// Only display people that are not members in the selected team	
    					if (! memberInTeam (peopleCursor.getString(peopleCursor
    							.getColumnIndex(SocialContract.People._ID)))) {
	    						
    						String displayName = peopleCursor.getString(peopleCursor
    								.getColumnIndex(SocialContract.People.NAME));
    						peopleList.add (displayName);
    						people++;
    						peopleMap.add (peopleCursor.getPosition()); // Maps person in UI list and cursor position 	
    					}
    				}
    			}
    		}

    	    listView.setAdapter(new ArrayAdapter<String>(this,
    	            R.layout.disaster_list_item_multiple_choice, peopleList));
    	    
    	    listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

    	}

    	
    	
/**
 * check whether or not a person is already member in a team.
 */
    private boolean memberInTeam (String m) {
    	
    	if (MemberListActivity.memberCursor == null) {
    		return false;    		
    	}
    	if (MemberListActivity.memberCursor.getCount() ==0) {
    		return false;
    	}
    	
    	MemberListActivity.memberCursor.moveToFirst();
    	if (m.equals(MemberListActivity.memberCursor.getString(
    			MemberListActivity.memberCursor.getColumnIndex(SocialContract.People._ID)))) {
    			return true;
    	}
    	
    	while (MemberListActivity.memberCursor.moveToNext()) {
    		if (m.equals(MemberListActivity.memberCursor.getString(
    				MemberListActivity.memberCursor.getColumnIndex(SocialContract.People._ID)))) {
    			return true;
    		}
    	}
    	return false;
     }



/**
 * add selected members to SocialProvider
 */
	private String addNewMembers () {
    
		SparseBooleanArray checkedRows = listView.getCheckedItemPositions();

// Used for Activity Feed
			String selectedMembers = "Welcome to new members:";
			Boolean selectedFlag = false;

		for (int i=0; i<people; ++i) {
			if (checkedRows.get(i)) {
				selectedFlag = true;
				peopleCursor.moveToPosition(peopleMap.get(i));
				selectedMembers = selectedMembers + " " + peopleCursor.
						getString(peopleCursor.getColumnIndex(SocialContract.People.NAME));
				
				// Set the values related to the activity to store in SocialProvider
				ContentValues membershipValues = new ContentValues ();
				
				membershipValues.put(SocialContract.Membership._ID_COMMUNITY,	// Add member to the selected team
						iDisasterApplication.getInstance().selectedTeam.id);				
				membershipValues.put(SocialContract.Membership._ID_MEMBER,		// Id of the member to be added
						peopleCursor.getString(peopleCursor.getColumnIndex(SocialContract.People._ID)));
				membershipValues.put(SocialContract.Membership.DESCRIPTION,	"");// Application-provided description: none
				membershipValues.put(SocialContract.Membership.TYPE, "member");	// Application-provided

				// Fields for synchronization with box.com
				membershipValues.put(SocialContract.CommunityActivity.ACCOUNT_NAME, iDisasterApplication.getInstance().me.userName);
				membershipValues.put(SocialContract.CommunityActivity.ACCOUNT_TYPE, "com.box");
				membershipValues.put(SocialContract.CommunityActivity.DIRTY, 1);

				try {
// The Uri value returned is not used.
//					Uri activityNewUri = getContentResolver().insert( SocialContract.CommunityActivity.CONTENT_URI,
//												activityValues);
					getContentResolver().insert( SocialContract.Membership.CONTENT_URI, 
							membershipValues);
				} catch (Exception e) {
					iDisasterApplication.getInstance().debug (2, "Insert to "+ 
										SocialContract.Membership.CONTENT_URI + "causes an exception");
			    	return iDisasterApplication.getInstance().INSERT_EXCEPTION;
				}
			}
		}
				
//		Toast.makeText(this, "selected:" + selected, Toast.LENGTH_SHORT).show();
				
		if (selectedFlag) {			// New selecte member: sdd an activity to the feed
					
			SocialActivity socialActivity = 
					new SocialActivity (iDisasterApplication.getInstance().me.userName); // account for synchronization
			ContentResolver activityResolver = getContentResolver();
			
// The return code from addActivity is not exploited here		
			socialActivity.addActivity (activityResolver,			// Insert activity to the activity feed
// TODO: what should be set here? local or global id?
					iDisasterApplication.getInstance().selectedTeam.id,		// Feed of the the selected team
//TODO: what should be set here? global or local id? - local id seems to not work		
					iDisasterApplication.getInstance().me.peopleGlobalId,	// Me
					iDisasterApplication.getInstance().VERB_TEXT,			// Activity intent: Simple text
					selectedMembers,												// List of new members
					iDisasterApplication.getInstance().TARGET_ALL);			// Recipient for Activity
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
