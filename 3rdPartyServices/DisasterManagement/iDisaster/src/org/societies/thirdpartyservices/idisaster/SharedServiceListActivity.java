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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import org.societies.android.api.cis.SocialContract;
import org.societies.thirdpartyservices.idisaster.R;
import org.societies.thirdpartyservices.idisaster.data.ThirdPartyService;

/**
 * This activity allows the user to list the services
 * shared by a member in the selected disaster team.
 * 
 * @author Jacqueline.Floch@sintef.no
 *
 */

public class SharedServiceListActivity extends ListActivity {

	private String memberGlobalId;
	private String memberName;
	
	private ContentResolver resolver;
	private Cursor sharedServiceCursor;			// used for the shared services
	private int sharedServices;					// keep track of number of own teams
	private String selectedServiceGlobalId;
	private String selectedServiceName;
	private String clientServiceGlobalId;
	
	
	private ArrayAdapter<String> sharedServiceAdapter;
	private ListView listView;
	

    @Override
    protected void onCreate (Bundle savedInstanceState) {

    	super.onCreate(savedInstanceState);
    	
		Intent intent= getIntent(); 					// Get the intent that created activity		
		memberGlobalId = intent.getStringExtra("MEMBER_ID");	// Retrieve first parameter (id of member sharing services)
		memberName = intent.getStringExtra("MEMBER_NAME");		// Retrieve second parameter (name of member sharing services)
    	
    	setContentView (R.layout.shared_service_list_layout);    	
		TextView title = (TextView)findViewById(R.id.sharedServiceLabel);
		title.setText (iDisasterApplication.getInstance().selectedTeam.name + ":\n" + memberName + " services");

    	listView = getListView();
    	resolver = getContentResolver();
    	
		if (iDisasterApplication.testDataUsed) {			// Test data
															// The adapter should not be set in onResume since the Android Adapter 
															// mechanism is used for update. See DisasterCreateactivity.
			iDisasterApplication.getInstance().disasterAdapter = new ArrayAdapter<String> (this,
					R.layout.disaster_list_item, R.id.disaster_item, new ArrayList<String> ());
			listView.setAdapter(iDisasterApplication.getInstance().disasterAdapter);
			Toast.makeText(getApplicationContext(),
					"Test mode: no test data available for this activity" ,
					Toast.LENGTH_LONG).show();
		} 													// Otherwise the data are fetched in onResume
    	

    	// Add listener for short click.
       	listView.setOnItemClickListener(new OnItemClickListener() {
    		public void onItemClick (AdapterView<?> parent, View view,
    			int position, long id) {

    			if (iDisasterApplication.testDataUsed) {			// Test data
    				;    				
    			} else {
    				if (position < sharedServices) {
    					launchClientService (position);
    					return;

				} else {	// should never happen
					iDisasterApplication.getInstance().debug (2, "No shared service id can be retrieved from position in onClickListener");
					return;
				}
    			}
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
			if (sharedServiceAdapter!= null) sharedServiceAdapter.clear();
			
			if (getSharedServices ()		// Retrieve services the member is sharing
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
    	
// TODO: check the following:
    	// When using managedQuery(), the activity keeps a reference to the cursor and close it
    	// whenever needed (in onDestroy() for instance.) 
    	// When using a contentResolver's query(), the developer has to manage the cursor as a sensitive
    	// resource. If you forget, for instance, to close() it in onDestroy(), you will leak 
    	// underlying resources (logcat will warn you about it.)
    	//
    	
//    	ownTeamCursor.close();
//    	memberTeamCursor.close();    	

    }

	
/**
 * getSharedServices retrieves the list of services shared by the selected member.
 */

	private String getSharedServices () {

		// Step 1: get GLOBAL_ID_SERVICES for services shared by the member
		// in the selected CIS
			Uri sharingUri = SocialContract.Sharing.CONTENT_URI;
						
			String[] sharingProjection = new String[] {
					SocialContract.Sharing.GLOBAL_ID_SERVICE};

			String sharingSelection = SocialContract.Sharing.GLOBAL_ID_COMMUNITY + "= ?" +									
									"AND " + SocialContract.Sharing.GLOBAL_ID_OWNER + "= ?" +
									"AND " + SocialContract.Sharing.TYPE + "= ?";

			String[] sharingSelectionArgs = new String[] 
					{iDisasterApplication.getInstance().selectedTeam.globalId,	// For the selected CIS
					memberGlobalId,												// For the member
					iDisasterApplication.getInstance().SERVICE_SHARED};			// Retrieve shared services
	
			Cursor sharingCursor;
			try {
				sharingCursor= resolver.query(sharingUri, sharingProjection,
						sharingSelection, sharingSelectionArgs, null /* sortOrder*/);
			} catch (Exception e) {
				iDisasterApplication.getInstance().debug (2, "Query to "+ sharingUri + "causes an exception");
				return iDisasterApplication.getInstance().QUERY_EXCEPTION;

			}

			// Step 2: retrieve the services with the GLOBAL_ID_SERVICE retrieved above
			if (sharingCursor == null) {			// No cursor was set - should not happen?
				iDisasterApplication.getInstance().debug (2, "sharingCursor was not set to any value");
				return iDisasterApplication.getInstance().QUERY_EMPTY;
			}
			
			if (sharingCursor.getCount() == 0) {	// No service is shared by the member in the team community
				return iDisasterApplication.getInstance().QUERY_EMPTY;
			}			
			
			// Get GLOBAL_ID and NAME for recommended services
				 
			Uri servicesUri = SocialContract.Services.CONTENT_URI;
					
			String[] servicesProjection = new String[] {
						SocialContract.Services.GLOBAL_ID,
						SocialContract.Services.NAME,
						SocialContract.Services.DEPENDENCY};		// Client ID
				
			boolean first = true;
			String servicesSelection = new String();
			ArrayList <String> servicesSelectionArgs = new ArrayList <String> ();
			while (sharingCursor.moveToNext()) {
				if (first) {
					first = false;
					servicesSelection = SocialContract.Services.GLOBAL_ID + "= ?";
					servicesSelectionArgs.add (sharingCursor.getString(
									(sharingCursor.getColumnIndex(SocialContract.Sharing.GLOBAL_ID_SERVICE))));
				} else {
					servicesSelection = servicesSelection + 
										" OR " +  SocialContract.Services.GLOBAL_ID + "= ?";
					servicesSelectionArgs.add (sharingCursor.getString(
							(sharingCursor.getColumnIndex(SocialContract.Sharing.GLOBAL_ID_SERVICE))));
				}
			}
						
			try {
				sharedServiceCursor = resolver.query (servicesUri, servicesProjection, servicesSelection,
						   servicesSelectionArgs.toArray(new String[servicesSelectionArgs.size()]),
						   null /* sortOrder*/);				
			} catch (Exception e) {
				iDisasterApplication.getInstance().debug (2, "Query to "+ servicesUri + "causes an exception");
				return iDisasterApplication.getInstance().QUERY_EXCEPTION;
			}
						
			return iDisasterApplication.getInstance().QUERY_SUCCESS;
		
	}

/**
 * assignAdapter assigns data to display to adapter and adapter to view.
 */
	private void assignAdapter () {
		
		sharedServices = 0;
		
		ArrayList<String> disasterList = new ArrayList<String> ();

//		An empty List will be assigned to Adapter
//		if ((ownTeamCursor == null) && (memberTeamCursor == null)) {

//		An empty List will be assigned to Adapter
//		if ((ownTeamCursor != null) && (memberTeamCursor != null)) {
//			if ((ownTeamCursor.getCount() == 0) && (memberTeamCursor.getCount() == 0)) {
		
		if (sharedServiceCursor != null) {
			if (sharedServiceCursor.getCount() != 0) {
				while (sharedServiceCursor.moveToNext()) {
					sharedServices++;
					String displayName = sharedServiceCursor.getString(sharedServiceCursor
							.getColumnIndex(SocialContract.Services.NAME));
					disasterList.add (displayName);
				}
			}
		}
		sharedServiceAdapter = new ArrayAdapter<String> (this,
	    			R.layout.disaster_list_item, R.id.disaster_item, disasterList);
		
		listView.setAdapter(sharedServiceAdapter);
	}

/**
 * launchClientService retrieves information about the service availability on the device
 * - if installed, the service is launched
 * - if not, the installation is triggered.
 */

	private void launchClientService (int position) {
		
		sharedServiceCursor.moveToPosition(position);
		selectedServiceGlobalId =  sharedServiceCursor.getString(sharedServiceCursor
				.getColumnIndex(SocialContract.Services.GLOBAL_ID));
		selectedServiceName =  sharedServiceCursor.getString(sharedServiceCursor
				.getColumnIndex(SocialContract.Services.NAME));
		
		String selectedServiceName =  sharedServiceCursor.getString(sharedServiceCursor
				.getColumnIndex(SocialContract.Services.NAME));
		
		clientServiceGlobalId = sharedServiceCursor.getString(sharedServiceCursor
				.getColumnIndex(SocialContract.Services.DEPENDENCY));
		ThirdPartyService clientService = new ThirdPartyService
				(clientServiceGlobalId);
		
		// Retrieve client service information from SocialProvider
		if (!(clientService.getServiceInformation (this, getContentResolver ())
				.equals(iDisasterApplication.getInstance().QUERY_SUCCESS))) {
			showQueryExceptionDialog ();	// Exception: Display dialog and terminates activity
			return;
		}
		
		if (!(clientService.checkServiceInstallStatus (this)
				.equals (iDisasterApplication.getInstance().UPDATE_SUCCESS))) {
			showQueryExceptionDialog ();	// Exception: Display dialog and terminates activity
			return;
		}
		
		if (!(clientService.serviceAvailable										// If the service is NOT installed on the device
				.equals(iDisasterApplication.getInstance().SERVICE_INSTALLED))) {
			showInstallDialog (selectedServiceName);

			
		} else {																	// If the service is installed on the device
// TODO: Add intent parameters
			if (clientService.launchApplication (this)									// Launch service
					.equals (iDisasterApplication.getInstance().LAUNCH_EXCEPTION)) {				// Launch has failed
				showServiceExceptionDialog			// Launch exception: display dialog
					(getString(R.string.dialogServiceLaunchException));
			} 
			finish ();		// Terminate in any launch result case; exception cannot be repaired
		}
		
		return;
	}

/**
 * showInstallDialog displays a user dialog for installing the service.
 */
			
	private void showInstallDialog (String name) {
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
		alertBuilder.setMessage(name + " " + getString(R.string.dialogNotInstalled))
  				.setCancelable(false)
  				.setPositiveButton (getString(R.string.dialogInstall), new DialogInterface.OnClickListener() {
  					public void onClick(DialogInterface dialog, int id) {
  						// Start ServiceDetails activity
  		     			Intent intent = new Intent(SharedServiceListActivity.this, ServiceDetailsActivity.class);
           				intent.putExtra("REQUEST_CONTEXT", iDisasterApplication.getInstance().SERVICE_SHARED);
        				intent.putExtra("SERVICE_GLOBAL_ID", clientServiceGlobalId);
                		startActivity(intent);
  						return;
  					}
  				})
  				.setNegativeButton(getString(R.string.dialogCancel), new DialogInterface.OnClickListener() {
  					public void onClick(DialogInterface dialog, int id) {
  						// add termination code code eventually
  						finish ();
  						return;
  					}
  				});
		AlertDialog alert = alertBuilder.create();
	    alert.show();	
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

/**
 * showServiceExceptionDialog displays a dialog to the user.
 * In this case, the activity does not terminate since the user
 * may check Internet and try a second time.
 */
        	        			
	private void showServiceExceptionDialog (String message) {
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setMessage(message)
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