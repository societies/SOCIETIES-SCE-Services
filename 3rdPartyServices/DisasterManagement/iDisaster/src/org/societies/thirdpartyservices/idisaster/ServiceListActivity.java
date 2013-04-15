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
import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

// TODO: Add import
// import org.societies.android.platform.SocialContract;

/**
 * This activity allows the user to list the services
 * recommended in the disaster team or installed by the user.
 * 
 * NB! shared services in the CIS are no longer shown by the ActINTENTivity.
 * They can be accessed through the member list.
 * 
 * @author Jacqueline.Floch@sintef.no
 *
 */

// TODO: Classified services in the list according to whether they are
// recommended, shared or installed.

public class ServiceListActivity extends ListActivity {

	ContentResolver resolver;
	
	int recommendedServices;			// keep track of number of recommended services
	int myServices;						// keep track of number of services used by the user
	// Static classes that can be accessed by other activities e.g. ServiceRecommendActivity, ServiceAddActivity
	static Cursor recommendedServiceCursor;		// used for services that are recommended to the team members
	static Cursor myServiceCursor;				// used for services that are used in the team by the user

	// No longer set by this activity 
	Cursor sharedServiceCursor = null;		// used for services that are shared by team members
	int sharedServices = 0;					// keep track of number of shared services
	
	ArrayAdapter<String> serviceAdapter;
	ListView listView;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {

    	super.onCreate(savedInstanceState);
    	setContentView (R.layout.service_list_layout);
    	listView = getListView();
    	resolver = getContentResolver();
	
    	if (iDisasterApplication.testDataUsed) {			// Test data
															// The adapter should not be set in onResume since the Android Adapter 
															// mechanism is used for update. See DisasterCreateactivity.
        	iDisasterApplication.getInstance().serviceAdapter = new ArrayAdapter<String> (this,
        			R.layout.disaster_list_item, R.id.disaster_item, iDisasterApplication.getInstance().CISserviceNameList);

        	listView.setAdapter(iDisasterApplication.getInstance().serviceAdapter);
    	} 													// Otherwise the data are fetched in onResume
	

    	// Add listener for short click.
    	listView.setOnItemClickListener(new OnItemClickListener() {
    		public void onItemClick (AdapterView<?> parent, View view,
    				int position, long id) {
    			
     			Intent intent = new Intent(ServiceListActivity.this, ServiceDetailsActivity.class);

    			if (iDisasterApplication.testDataUsed) {					// Test data    				
    					// For test: The service is recommended in the CIS
        				intent.putExtra(ServiceDetailsActivity.INTENT_SERVICE_DETAILS_REQUEST_CONTEXT, 									
        						SocialContract.ServiceConstants.SERVICE_RECOMMENDED);
        				// For test: service identity
        				intent.putExtra(ServiceDetailsActivity.INTENT_SERVICE_DETAILS_GLOBAL_ID_SERVICE, Integer.toString(position));        				
        			} else {
        				String serviceGlobalId;
        				if (position < recommendedServices) {							// Retrieve information from list of recommended services in the team
    						recommendedServiceCursor.moveToPosition(position);
    						serviceGlobalId =  recommendedServiceCursor.getString(recommendedServiceCursor
    							.getColumnIndex(SocialContract.Services.GLOBAL_ID));
               				intent.putExtra(ServiceDetailsActivity.INTENT_SERVICE_DETAILS_REQUEST_CONTEXT, 
               						SocialContract.ServiceConstants.SERVICE_RECOMMENDED);
               				
// NB! Shared services in the CIS are no longer shown by the Activity.
// 	If code is restored, the code in this activity and in ServiceDetailsActivituy should be revised.
//  Since there is no information about who is sharing the service, the Launch that requires the ID of the member
//  sharing the service is not straightforward to implement.
//        				} else if ((position-recommendedServices) < sharedServices) {	// Retrieve information from list of shared services in the team
//        					sharedServiceCursor.moveToPosition(position-recommendedServices);
//        					serviceGlobalId =  sharedServiceCursor.getString(sharedServiceCursor
//    							.getColumnIndex(SocialContract.Services.GLOBAL_ID));
//               				intent.putExtra(ServiceDetailsActivity.INTENT_SERVICE_DETAILS_REQUEST_CONTEXT,
//               						SocialContract.ServiceConstants.SERVICE_SHARED);       					
        				} else if ((position-recommendedServices-sharedServices) < myServices) {
        					myServiceCursor.moveToPosition(position-recommendedServices - sharedServices);
        					serviceGlobalId =  myServiceCursor.getString(myServiceCursor
    							.getColumnIndex(SocialContract.Services.GLOBAL_ID));
               				intent.putExtra(ServiceDetailsActivity.INTENT_SERVICE_DETAILS_REQUEST_CONTEXT, 
               						SocialContract.ServiceConstants.SERVICE_INSTALLED);
        				} else {	// should never happen
        					iDisasterApplication.getInstance().debug (2, "No service id can be retrieved from position in onClickListener");
        					return;
        				}
        				intent.putExtra(ServiceDetailsActivity.INTENT_SERVICE_DETAILS_GLOBAL_ID_SERVICE, serviceGlobalId);		    				
        			}
    			// Start the ServiceDetails activity
        		startActivity(intent);
// The activity is kept on stack (check also that "noHistory" is not set in Manifest
//        		finish();
    		}
    	});       	
    } // onCreate
    				
    
/**
 * onResume is called at start of the active lifetime.
 * The list of services in the team is retrieved from SocialProvider
 * and assigned to the view.
 * The data are fetched each time the activity becomes visible as these data
 * may be changed by other users (info fetched from the Cloud)
 */
            
	@Override
	protected void onResume() {
		super.onResume();
    	
		// Test data are set in onCreate - see explanation above
		
//		testSharing(this, getContentResolver ());
    		
        if (! iDisasterApplication.testDataUsed) {
        	if (serviceAdapter!= null) serviceAdapter.clear();
        	
			if (getServices (SocialContract.ServiceConstants.SERVICE_RECOMMENDED)	// Retrieve services recommended in the team
					.equals(iDisasterApplication.getInstance().QUERY_EXCEPTION)) {
				showQueryExceptionDialog ();	// Exception: Display dialog
			}

// NB! Shared services in the CIS are no longer shown by the Activity.
//     They can be accessed through the member list.
//			if (getServices (iDisasterApplication.getInstance().SERVICE_SHARED)			// Retrieve services shared in the selected team
//					.equals(iDisasterApplication.getInstance().QUERY_EXCEPTION)) {
//				showQueryExceptionDialog ();	// Exception: Display dialog
//			}

//			if (getMyServices ()														// Retrieve services installed by the user

			if (getServices (SocialContract.ServiceConstants.SERVICE_INSTALLED)	// Retrieve services installed by the user
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
	}

/**
 * getServices retrieves the list of services related to the selected team
 * from SocialProvider and the list of services installed by the user.
 * Parameter:			Type: "Recommended" or "Shared" for services related to the team
 * 							  "Installed for services installed by the user
 * Return value:		Query status code
 */

//TODO: We assume that a service can only be recommended once in a CIS...

	private String getServices (String serviceType) {
		
		int flagType = 0;
		
		// Will be set to 0 when querying own services
		long selectedTeamId = iDisasterApplication.getInstance().selectedTeam.id;
		
		// Check serviceType
		if (serviceType.equals(SocialContract.ServiceConstants.SERVICE_INSTALLED)){
			flagType = 1;

			selectedTeamId = 0;					// Own services

			if (myServiceCursor != null) {
				myServiceCursor.close();		// "close" releases data but does not set to null
				myServiceCursor = null;
			}
		} else if (serviceType.equals(SocialContract.ServiceConstants.SERVICE_RECOMMENDED)) {
			flagType = 2;
			if (recommendedServiceCursor != null) {
				recommendedServiceCursor.close();		// "close" releases data but does not set to null
				recommendedServiceCursor = null;
			}
		} else if (serviceType.equals(SocialContract.ServiceConstants.SERVICE_SHARED)) {
			flagType = 3;
			if (sharedServiceCursor != null) {
				sharedServiceCursor.close();		// "close" releases data but does not set to null
				sharedServiceCursor = null;
			}
		} else{																	// Should never happen
			return iDisasterApplication.getInstance().QUERY_EXCEPTION;
		}


		// Step 1: get _ID_SERVICES for services in the selected CIS
			Uri sharingUri = SocialContract.Sharing.CONTENT_URI;

			String[] sharingProjection = new String[] {
					SocialContract.Sharing._ID_SERVICE};

			String sharingSelection = SocialContract.Sharing._ID_COMMUNITY + " = ?" +
									" AND " + SocialContract.Sharing.TYPE + " = ?";

			String[] sharingSelectionArgs = new String[] 
					{String.valueOf (selectedTeamId),		// For the selected CIS (0 if own services)
					serviceType};							// Retrieve services of that type
	
			Cursor sharingCursor;
			try {
				sharingCursor= resolver.query(sharingUri, sharingProjection,
						sharingSelection, sharingSelectionArgs, null /* sortOrder*/);
			} catch (Exception e) {
				iDisasterApplication.getInstance().debug (2, "Query to "+ sharingUri + "causes an exception");
				return iDisasterApplication.getInstance().QUERY_EXCEPTION;

			}

			// Step 2: retrieve the services with the _ID_SERVICE retrieved above
			if (sharingCursor == null) {			// No cursor was set - should not happen?
				iDisasterApplication.getInstance().debug (2, "sharingCursor was not set to any value");
				return iDisasterApplication.getInstance().QUERY_EMPTY;
			}
			
			if (sharingCursor.getCount() == 0) {	// No service is recommended or shared in the team community
				return iDisasterApplication.getInstance().QUERY_EMPTY;
			}			
			
			// Get _ID and NAME for recommended services
				 
			Uri servicesUri = SocialContract.Services.CONTENT_URI;
			
			String[] servicesProjection = new String[] {
						SocialContract.Services.GLOBAL_ID,
						SocialContract.Services._ID,
						SocialContract.Services.NAME};
				
			boolean first = true;
			String servicesSelection = new String();
			ArrayList <String> servicesSelectionArgs = new ArrayList <String> ();
			while (sharingCursor.moveToNext()) {
				if (first) {
					first = false;
					servicesSelection = SocialContract.Services._ID + " = ?";
					servicesSelectionArgs.add (String.valueOf (sharingCursor.getLong(
									(sharingCursor.getColumnIndex(SocialContract.Sharing._ID_SERVICE)))));
				} else {
					servicesSelection = servicesSelection + 
										" OR " +  SocialContract.Services._ID + " = ?";
					servicesSelectionArgs.add (String.valueOf (sharingCursor.getLong(
							(sharingCursor.getColumnIndex(SocialContract.Sharing._ID_SERVICE)))));
				}
			}
			
			Cursor serviceCursor;			
			try {
				serviceCursor = resolver.query (servicesUri, servicesProjection, servicesSelection,
						   servicesSelectionArgs.toArray(new String[servicesSelectionArgs.size()]),
						   null /* sortOrder*/);				
			} catch (Exception e) {
				iDisasterApplication.getInstance().debug (2, "Query to "+ servicesUri + "causes an exception");
				return iDisasterApplication.getInstance().QUERY_EXCEPTION;
			}
			
			if (flagType == 1) {
				myServiceCursor = serviceCursor;
			} else if (flagType == 2){
				recommendedServiceCursor = serviceCursor;				
			} else if (flagType == 3){
				sharedServiceCursor = serviceCursor;				
			} else {	// should never happen
				return iDisasterApplication.getInstance().QUERY_EXCEPTION;				
			}

			
			return iDisasterApplication.getInstance().QUERY_SUCCESS;
			
	}

	
/**
 * getMyServices retrieves the list of services installed by the user.
 * Return value:		Query status code
 */

//	private String getMyServices () {
//		
//		if (myServiceCursor != null) {
//			myServiceCursor.close();		// "close" releases data but does not set to null
//			myServiceCursor = null;
//		}
//
//			Uri serviceUri = SocialContract.Services.CONTENT_URI;
//							
//			String[] sharingProjection = new String[] {
//					SocialContract.Services._ID,
//					SocialContract.Services.NAME};
//
//// Note this table is not synchronized. It only contains
//// - the list of services available from the marketplace
//// - the list of services installed by the user
//			
//			String serviceSelection = SocialContract.Services.AVAILABLE + "= ?";
//
//			String[] serviceSelectionArgs = new String[] 
//					{iDisasterApplication.getInstance().SERVICE_INSTALLED}; // Services installed by the user
//		
//			try {
//				myServiceCursor = resolver.query(serviceUri, sharingProjection,
//						serviceSelection, serviceSelectionArgs, null /* sortOrder*/);
//			} catch (Exception e) {
//				iDisasterApplication.getInstance().debug (2, "Query to "+ serviceUri + "causes an exception");
//				return iDisasterApplication.getInstance().QUERY_EXCEPTION;
//
//			}
//
//			if (myServiceCursor == null) {			// No cursor was set - should not happen?
//				iDisasterApplication.getInstance().debug (2, "sharingCursor was not set to any value");
//				return iDisasterApplication.getInstance().QUERY_EMPTY;
//			}
//				
//			if (myServiceCursor.getCount() == 0) {	// No service installed by the user
//				return iDisasterApplication.getInstance().QUERY_EMPTY;
//			}			
//			
//			return iDisasterApplication.getInstance().QUERY_SUCCESS;
//								
//	}
	

/**
 * assignAdapter assigns data to display to adapter and adapter to view.
 */
	private void assignAdapter () {
		recommendedServices = 0;
		sharedServices = 0;
		myServices = 0;
		
		ArrayList<String> serviceList = new ArrayList<String> ();
		

//			An empty List will be assigned to Adapter
//			if ((recommendedServiceCursor == null) && (sharedServiceCursor == null)
//				&& (myServiceCursor == null)) {

//			An empty List will be assigned to Adapter
//			if ((recommendedServiceCursor != null) && (recommendedServiceCursor != null)
//				&& (recommendedServiceCursor != null)) {
//				if ((recommendedServiceCursor.getCount() == 0) && (recommendedServiceCursor.getCount() == 0)
//					&& (myServiceCursor.getCount() == 0)) {
			
			if (recommendedServiceCursor != null) {
				if (recommendedServiceCursor.getCount() != 0) {
					while (recommendedServiceCursor.moveToNext()) {
						recommendedServices++;
						String displayName = "(recommended:) " + recommendedServiceCursor.getString(recommendedServiceCursor
								.getColumnIndex(SocialContract.Services.NAME));
						serviceList.add (displayName);
					}
				}
			}
			
			if (sharedServiceCursor != null) {
				if (sharedServiceCursor.getCount() != 0) {
					while (sharedServiceCursor.moveToNext()) {
						sharedServices++;
						String displayName = "(shared:) " + sharedServiceCursor.getString(sharedServiceCursor
								.getColumnIndex(SocialContract.Services.NAME));
						serviceList.add (displayName);
					}
				}
			}
			
			if (myServiceCursor != null) {
				if (myServiceCursor.getCount() != 0) {
					while (myServiceCursor.moveToNext()) {
						myServices++;
						String displayName = "(installed:) " + myServiceCursor.getString(myServiceCursor
								.getColumnIndex(SocialContract.Services.NAME));
						serviceList.add (displayName);
					}
				}
			}

	    serviceAdapter = new ArrayAdapter<String> (this,
		    			R.layout.disaster_list_item, R.id.disaster_item, serviceList);
			
		listView.setAdapter(serviceAdapter);
	}


/**
 * onCreateOptionsMenu expands the activity menu for this activity tab.
 */

@Override
	public boolean onCreateOptionsMenu(Menu menu){

// The FIXED menu is set by the TabActivity (i.e. DisasterActivity).
// I am uncertain why the call to the super class leads to the creation
// of the fixed menu set by the TabActivity.
		super.onCreateOptionsMenu(menu);
		
		menu.setGroupVisible(R.id.disasterMenuService, true);
		return true;
}


/**
 * onOptionsItemSelected handles the selection of an item in the activity menu. 
 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

// The TabActivity (i.e. DisasterActivity) handles items in the FIXED menu.
// I am uncertain why the call to the super class leads to handling
// of a command in the fixed menu by the TabActivity
		super.onOptionsItemSelected(item);

		switch (item.getItemId()) {

			case R.id.disasterMenuRecommendService:
				startActivity(new Intent(ServiceListActivity.this, ServiceRecommendActivity.class));
			break; 

// Not implemnted
//			case R.id.disasterMenuAddService:
//				Toast.makeText(getApplicationContext(),
//						"Not implemented yet. To add a service, select a recommended service (eventually recommend a service first).", Toast.LENGTH_LONG)
//									.show();
				
				// The ServiceListActivity crashes...
				// startActivity(new Intent(ServiceListActivity.this, ServiceAddActivity.class));
//			break;
		
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
        alertBuilder.setMessage(getString(R.string.dialogServiceQueryException))
        			.setCancelable(false)
          			.setPositiveButton (getString(R.string.dialogOK), new DialogInterface.OnClickListener() {
          				public void onClick(DialogInterface dialog, int id) {
          					return;
          				}
	          			});
	        AlertDialog alert = alertBuilder.create();
	        alert.show();	
		}

/**
 * testSharing gets all services in the sharing table
 */
	public void testSharing (Context cxt, ContentResolver resolver) {
		Uri sharingUri = SocialContract.Sharing.CONTENT_URI;			
		Cursor testCursor; 
	
		String[] sharingProjection = new String[] {			// Retrieve all entries in table
				SocialContract.Sharing._ID,
				SocialContract.Sharing.GLOBAL_ID,
				SocialContract.Sharing._ID_COMMUNITY,
				SocialContract.Sharing._ID_OWNER,
				SocialContract.Sharing._ID_SERVICE,
//				SocialContract.Sharing.DESCRIPTION,
				SocialContract.Sharing.TYPE,
//				SocialContract.Sharing.ACCOUNT_NAME,
//				SocialContract.Sharing.ACCOUNT_TYPE,
//				SocialContract.Sharing.CREATION_DATE,				
//				SocialContract.Sharing.DELETED,
//				SocialContract.Sharing.DIRTY,
//				SocialContract.Sharing.LAST_MODIFIED_DATE
				};
	
		String sharingSelection = null;
	
		String[] sharingSelectionArgs = null;									// Onøy care about fields which are not deleted
				
		try {
			testCursor= resolver.query(sharingUri, sharingProjection, null, null, null);
		} catch (Exception e) {
			return;
		}
			
		if (testCursor == null) {
			return;
		}

		int myCount = testCursor.getCount();
		
		if (testCursor.getCount() == 0) {
			return;
		}		
		
		int i =0;
		
		while (testCursor.moveToNext()) {
			
			long s01Id = testCursor.getLong(testCursor
					.getColumnIndex(SocialContract.Sharing._ID));
			String s02GlobalId = testCursor.getString(testCursor
					.getColumnIndex(SocialContract.Sharing.GLOBAL_ID));
			long s03IdCommunity = testCursor.getLong(testCursor
					.getColumnIndex(SocialContract.Sharing._ID_COMMUNITY));
			long s04IdOwner = testCursor.getLong(testCursor
					.getColumnIndex(SocialContract.Sharing._ID_OWNER));
			long s05IdService = testCursor.getLong(testCursor
					.getColumnIndex(SocialContract.Sharing._ID_SERVICE));
//			String s06Desc = testCursor.getString(testCursor
//					.getColumnIndex(SocialContract.Sharing.DESCRIPTION));
			String s07TYPE = testCursor.getString(testCursor
					.getColumnIndex(SocialContract.Sharing.TYPE));
//			String s08AccountName = testCursor.getString(testCursor
//					.getColumnIndex(SocialContract.Sharing.ACCOUNT_NAME));
//			String s09AccountType = testCursor.getString(testCursor
//					.getColumnIndex(SocialContract.Sharing.ACCOUNT_TYPE));
//			String s10CreationDate = testCursor.getString(testCursor
//					.getColumnIndex(SocialContract.Sharing.CREATION_DATE));
//			String s11Deleted = testCursor.getString(testCursor
//					.getColumnIndex(SocialContract.Sharing.DELETED));
//			String s12Dirty = testCursor.getString(testCursor
//					.getColumnIndex(SocialContract.Sharing.DIRTY));
//			String s13Last = testCursor.getString(testCursor
//					.getColumnIndex(SocialContract.Sharing.LAST_MODIFIED_DATE));

			String s14Service = "ID: " + s01Id + " GLOBAL_ID " + s02GlobalId + " CIS_ID: " + s03IdCommunity + " OWNNER_ID: " + s04IdOwner
								+ " SERVICE_ID: " + s05IdService + " TYPE: " + s07TYPE + " ";
			
			i ++;
			
		}
		
		return;
	}

}
