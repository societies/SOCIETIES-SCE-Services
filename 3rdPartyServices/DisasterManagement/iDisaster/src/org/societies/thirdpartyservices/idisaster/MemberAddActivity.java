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

import org.societies.android.api.cis.SocialContract;
import org.societies.thirdpartyservices.idisaster.R;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
    	setContentView (R.layout.member_add_layout);
    	listView = getListView();
    	resolver = getContentResolver();

//    	// Add listener for short click.
//    	listView.setOnItemClickListener(new OnItemClickListener() {
//    		public void onItemClick (AdapterView<?> parent, View view,
//    			int position, long id) {
//    			
//    			if ((position) < people) {			// Retrieve information from members in the team
//    				peopleCursor.moveToPosition(position);
//    				String memberGlobalId = peopleCursor.getString(peopleCursor
//    						.getColumnIndex(SocialContract.People.GLOBAL_ID));
//     				String memberName =  peopleCursor.getString(peopleCursor
//    						.getColumnIndex(SocialContract.People.NAME));
//    
//// The activity is kept on stack (check also that "noHistory" is not set in Manifest)
//// Should it be removed?
////       				finish();
//    					
//    					// Test code
//            			Toast.makeText(getApplicationContext(),
//                				"Selected: " + memberName + " " + memberGlobalId, Toast.LENGTH_LONG)
//                				.show();    				
//
//    				} else {
//    					// Should never happen..
//    				}
//    			}
//
//    	});

    	// TODO: Add SAVE button in the view and click listener to the button
//    	final Button button = (Button) findViewById(R.id.feedAddButton);
//    	button.setOnClickListener(this);

    	
    	
    	//TODO:  Add listener for long click
    	// listView.setOnItemLongClickListener(new DrawPopup());
		// TODO: Start the MemberDetails Activity
		// TODO: Add parameters to Intent
		// startActivity (new Intent(MemberListActivity.this, MemberDetailsActivity.class));

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

//    		memberCursor.close();
    	}


/**
 * getFeed retrieves the list of activity feeds for the selected disaster team
 * from Social Provider.
 */
    private String getPeople () {
    	
		if (peopleCursor != null) {
			peopleCursor.close();		// "close" releases data but does not set to null
			peopleCursor = null;
		}
    		
    	Uri peopleUri = SocialContract.People.CONTENT_URI;
    					
    	String[] peopleProjection = new String[] {
    				SocialContract.People.GLOBAL_ID,
    				SocialContract.People.NAME};

    	//TODO: Add filter. Remove those already members in the selected CIS...
    	
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
    				
    		ArrayList<String> peopleList = new ArrayList<String> ();

//    			An empty List will be assigned to Adapter
//    			if (peopleCursor == null) {

//    			An empty List will be assigned to Adapter
//    				if (peopleCursor == null) {
//    					if (peopleCursor.getCount() == 0) {
    				
    		if (peopleCursor != null) {
    			if (peopleCursor.getCount() != 0) {
    				while (peopleCursor.moveToNext()) {
    		    		people++;
    					String displayName = peopleCursor.getString(peopleCursor
    							.getColumnIndex(SocialContract.People.NAME));
    					peopleList.add (displayName);
    				}
    			}
    		}
//    		peopleAdapter = new ArrayAdapter<String> (this,
//    				R.layout.disaster_list_item, R.id.disaster_item, peopleList);

    	    listView.setAdapter(new ArrayAdapter<String>(this,
    	            android.R.layout.simple_list_item_multiple_choice, peopleList));
    	    
    	    listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

    	    
//            <CheckBox android:id="@+id/checkbox"
//            android:layout_width="wrap_content" android:layout_height="wrap_content"
//            android:checked="false"
//            android:text="test">
//            </CheckBox>

    		
//    		listView.setAdapter(peopleAdapter);

    	}


/**
 * onClick is called when button is clicked because
 * the OnClickListener is assigned to the button
 * */

	public void onClick(View view) {

    }


/* (non-Javadoc)
 * @see android.content.DialogInterface.OnMultiChoiceClickListener#onClick(android.content.DialogInterface, int, boolean)
 */
//@Override
//public void onClick (DialogInterface arg0, int arg1, boolean arg2) {
//	// TODO Auto-generated method stub
//	
//}

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
          					return;
          				}
          			});
        AlertDialog alert = alertBuilder.create();
        alert.show();	
	}

}
