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


import android.app.AlertDialog;
import android.app.Application;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.ArrayAdapter;

import org.societies.android.api.cis.SocialContract;
import org.societies.thirdpartyservices.idisaster.data.Me;
import org.societies.thirdpartyservices.idisaster.data.SelectedTeam;

// Previously some information was store in a preference file.
// Currently: If information is not available, it is retrieved from the SocialProvider
//
//import android.content.SharedPreferences;
//import android.content.SharedPreferences.Editor;


/**
 * The application for managing common resources used by 
 * iDisaster application components.
 * 
 * The Android Application class is a base class for maintaining
 * the global application state.
 * The Application class is instantiated when the application/package
 * process is created (providing that the class implementation is 
 * specified in the Manifest). 
 * The Application object is deleted when there is no more instantiated 
 * activity in the application.
 * 
 * @author 	Jacqueline.Floch@sintef.no
 *
 */

public class iDisasterApplication extends Application {
	

	private static iDisasterApplication singleton; // Reference to the single instance of the Application

// All information is fetched from the Social Provider. Preferences are no longer used.
//
//	static final String PREFS_NAME = "iDisasterPreferences"; 	// File for storing preferences
//	SharedPreferences preferences;								// Preferences shared with all activities
//	Editor editor;												// Editor for changing preferences

	static final Boolean testDataUsed = false;		// When set to true do not used SocialProvider

	Me me = new Me();										// Store user identity - not persistent data (can be retrieved from Social Provider)
	SelectedTeam selectedTeam = new SelectedTeam ();		// Store team selected by the user - not persistent data (can be retrieved from Social Provider)
	
	// Constant keys used for user Logging
	public final String USER_NOT_IDENTFIED = "USER_NOT_IDENTFIED";

	// Constant keys used for query
	public final String QUERY_EMPTY = "QUERY_EMPTY";
	public final String QUERY_EXCEPTION = "QUERY_EXCEPTION";
	public final String QUERY_SUCCESS = "QUERY_SUCCESS";
	// Constant keys used for insert
	public final String INSERT_EXCEPTION = "INSERT_EXCEPTION";
	public final String INSERT_SUCCESS = "INSERT_SUCCESS";
	
	// Constant keys used for activity feeds
	public final String FEED_DISPLAY = "DISPLAY";
	
	// Constant used for services 
	public final String SERVICE_RECOMMENDED = "RECOMMENDED";
	public final String SERVICE_SHARED = "SHARED";
	public final String SERVICE_INSTALLED = "INSTALLED";

	// Constant used for service types
	public final String SERVICE_TYPE_PROVIDER = "Provider";
	public final String SERVICE_TYPE_CLIENT = "Client";
	public final String SERVICE_TYPE_APP = "App";

	// Constant used for operations on services
	public final String SERVICE_RECOMMEND = "Recommend";
	public final String SERVICE_INSTALL = "Install";
	public final String SERVICE_LAUNCH = "Launch";
	public final String SERVICE_SHARE = "Share";	

// TODO: Remove this variable - only used while waiting update for Social Provider
	private boolean servicesUpdated = false;

// test data
	ArrayList <String> disasterNameList;
	ArrayList <String> disasterDescriptionList;
	ArrayList <String> feedContentList ;
	ArrayList <String> memberNameList;
	ArrayList <String> memberDescriptionList;
	ArrayList <String> serviceNameList;
	ArrayList <String> serviceDescriptionList;	
	ArrayList <String> CISserviceNameList;
	ArrayList <String> CISserviceDescriptionList;	
	
//TODO: discuss design. Are these common adapter resources needed (performance)?	
	ArrayAdapter<String> disasterAdapter;
	ArrayAdapter<String> feedAdapter;
	ArrayAdapter<String> memberAdapter;
	ArrayAdapter<String> serviceAdapter;
	ArrayAdapter<String> CISserviceAdapter;

// Turn off debugging before RELEASE! Set DEBUG to 0.
//	 -1: ALWAYS PRINT (Not debug, but plain ERROR)
//	 0: NO_DEBUG
//	 1: SOME_DEBUG
//	 2: MORE_DEBUG
//	 3: EVEN_MORE_DEBUG
//	 4: EVEN_EVEN_MORE_DEBUG
//	 5: EVEN_EVEN_EVEN_MORE_DEBUG
//	 6: ...
	public static final int DEBUG = 3;
	public static final String DEBUG_Context = "iDisaster";


/**
 * getInstance returns application instance.
 * Activities used this pointer to access the common resources.
 */	  
	public static iDisasterApplication getInstance () {
		return singleton;
	}
	
	@Override
	public final void onCreate() {

		super.onCreate ();
		singleton = this;

// All information is fetched from the Social Provider. Preferences are no longer used.
//
// Restore preferences from preferences file.
// If the preferences file does not exist, it is created when changes are committed.
//		preferences = getSharedPreferences(PREFS_NAME, 0);
//	    editor = preferences.edit();
//	    editor.putString ("pref.dummy", "");
//	    editor.commit ();

//	    if (testDataUsed) {   
	    	setTestData ();	    	
//	    }
	    
// TODO: Remove following code - only used while waiting update for Social Provider
	    if (!servicesUpdated) {
	    	servicesUpdated = true;
	    	updateServices ();
	    }
			    
	} //onCreate

/**
 * 	checkUserIdentity retrieves the user information from SocialProvider
 * 	Returns a query code:
 * 			QUERY_SUCCESS if the user is registered
 * 			QUERY_EMPTY if the user is not registered
 * 			QUERY_EXECPTION if the query fails
 * 
 *  This method is defined by iDisaster such as a check can be made in any
 *  activity. 
 */
	public String checkUserIdentity (Context ctx) {
		
		Cursor cursor = null;
		
		if (me == null) { // should never happened!
			me = new Me();
			me.displayName = USER_NOT_IDENTFIED;
			debug (2, "No instance of Me");
		}
		
		Uri uri = SocialContract.Me.CONTENT_URI;

		//What to get:
		String[] projection = new String [] {
			SocialContract.Me.GLOBAL_ID,
			SocialContract.Me.NAME,
			SocialContract.Me.DISPLAY_NAME
		};
			

		String selection = SocialContract.Me._ID + " = 1"; // Use the first user identity for Societies
		String[] selectionArgs = null;

//  Alternative query:
//		String selection = SocialContract.Me._ID + "= ?"; // Use the first user identity for Societies
//		String[] selectionArgs = new String[] {"1"};
			
		String sortOrder = null;
	
        try{
        	cursor = getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
        } catch (Exception e) {
			debug (2, "Query to "+ uri + "causes an exception");
        	return QUERY_EXCEPTION;
        }
			
		if (cursor == null) {
			showDialog (ctx, "Unable to retrieve user information from SocialProvider", getString(R.string.dialogOK));
			me.displayName = USER_NOT_IDENTFIED;
			debug (2, "No instance of Me");
			return QUERY_EMPTY;
		}
			
		if (cursor.getCount() == 0) {
			showDialog (ctx, "Unable to retrieve user information from SocialProvider", getString(R.string.dialogOK));
			me.displayName = USER_NOT_IDENTFIED;
			debug (2, "No instance of Me");
			return QUERY_EMPTY;
		}

// TODO: There may be different logging info for user (stored in ContentProvider in Me).
//	 		The first row is always Societies, other rows may be used for Facebook, etc...
//			To get the number of entries, use:
//				int i= cursor.getCount(); 
		if (cursor.moveToFirst()){
			me.globalId = cursor.getString(cursor		// TODO: check that GLOBAL_ID is correct?
							.getColumnIndex(SocialContract.Me.GLOBAL_ID));
			me.name = cursor.getString(cursor
							.getColumnIndex(SocialContract.Me.NAME));
				
			if (cursor.getString(cursor					//TODO: check with Babak: what is returned if no name?
					.getColumnIndex(SocialContract.Me.DISPLAY_NAME)) == null) {
				me.displayName = me.name;		// use name as display name
			} else {
				me.displayName = cursor.getString(cursor
					.getColumnIndex(SocialContract.Me.DISPLAY_NAME));
			}
			return QUERY_SUCCESS;		// The only case where true is returned
		}
		// should not happen
		return QUERY_EMPTY;
	}


/**
 * updateServices is used temporarily. Will be removed after update of the code
 * for Social Provider.
 * It updates the Services table in order to use it as a global service registry 
 * (in addition to a user service registry).
 * - All OWNER_ID are set to ""
 * - set service type (the field "available" is currently used as not filed "type" is defined  
 * - set service CONFIG to a correct package name
 */
	private String updateServices () {
		
		Uri servicesUri = SocialContract.Services.CONTENT_URI;
		
		ContentValues values = new ContentValues ();
		
		// Step 1: get all services 
					
		String[] servicesProjection = new String[] {
				SocialContract.Services._ID,
				SocialContract.Services.GLOBAL_ID,
				SocialContract.Services.NAME
//TODO: remove - Used temporarily - to check data
				,
				SocialContract.Services.DESCRIPTION,
				SocialContract.Services.TYPE,
				SocialContract.Services.APP_TYPE,
				SocialContract.Services.AVAILABLE,
				SocialContract.Services.DEPENDENCY,
				SocialContract.Services.CONFIG,
				SocialContract.Services.URL

				};

		Cursor servicesCursor;
		try {
			servicesCursor= getContentResolver().query(servicesUri, servicesProjection,
					null /* selection */ , null /* selectionArgs */, null /* sortOrder*/);
		} catch (Exception e) {
			debug (2, "Query to "+ servicesUri + "causes an exception");
			return QUERY_EXCEPTION;

		}

		// Step 2: remove owner ID
		if (servicesCursor == null) {			// No cursor was set - should not happen?
			iDisasterApplication.getInstance().debug (2, "servicesCursor was not set to any value");
			return QUERY_EMPTY;
		}
		
		if (servicesCursor.getCount() == 0) {	// No service is recommended in the team community
			return QUERY_EMPTY;
		}		
		
		while (servicesCursor.moveToNext()) {
			
			
//TODO: Remove - Used temporarily			
			String serviceName = servicesCursor.getString(servicesCursor
					.getColumnIndex(SocialContract.Services.NAME));
			String serviceDescription = servicesCursor.getString(servicesCursor
					.getColumnIndex(SocialContract.Services.DESCRIPTION));
			String serviceType = servicesCursor.getString(servicesCursor
				.getColumnIndex(SocialContract.Services.TYPE));
			String serviceAppType=servicesCursor.getString(servicesCursor
				.getColumnIndex(SocialContract.Services.APP_TYPE));
			String serviceAvailable = servicesCursor.getString(servicesCursor
			.getColumnIndex(SocialContract.Services.AVAILABLE));
			String serviceDependency = servicesCursor.getString(servicesCursor
			.getColumnIndex(SocialContract.Services.DEPENDENCY));
			String serviceConfig = servicesCursor.getString(servicesCursor
			.getColumnIndex(SocialContract.Services.CONFIG));
			String serviceURL = servicesCursor.getString(servicesCursor
			.getColumnIndex(SocialContract.Services.APP_TYPE));
	

			
			
			
			
			
			
			
			
			
			Uri recordUri = servicesUri.withAppendedPath(servicesUri, "/" +
					servicesCursor.getString(servicesCursor.getColumnIndex(SocialContract.Services._ID)));
	        values = new ContentValues();
	        values.put(SocialContract.Services.OWNER_ID, "");
	        if (servicesCursor.getString(servicesCursor.getColumnIndex(SocialContract.Services.NAME)).equals("iJacket")) {
		        values.put(SocialContract.Services.AVAILABLE, SERVICE_TYPE_PROVIDER);
		        values.put(SocialContract.Services.CONFIG, "org.ubicompforall.cityexplorer");
	        } else if (servicesCursor.getString(servicesCursor.getColumnIndex(SocialContract.Services.NAME)).equals("iJacketClient")) {
	        	values.put(SocialContract.Services.AVAILABLE, SERVICE_TYPE_CLIENT);
		        values.put(SocialContract.Services.CONFIG, "org.ubicompforall.cityexplorer");
	        }
	        getContentResolver().update(recordUri, values, null, null);		
		}
	
		return QUERY_SUCCESS; 

		
		
		
//		Uri sharingUri = SocialContract.Sharing.CONTENT_URI;
//		
//		ContentValues values = new ContentValues ();
//		
////TODO: Remove the following once Social Provider has been corrected (Social Provider should insert the GLOBAL_ID)
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
//		String currentDateandTime = sdf.format(new Date());
//		values.put(SocialContract.Sharing.GLOBAL_ID, currentDateandTime);
//// End remove		
//
//		values.put(SocialContract.Sharing.GLOBAL_ID_SERVICE,		
//				"s1xyz.societies.org");
//		values.put(SocialContract.Sharing.GLOBAL_ID_COMMUNITY,
//							"c1xyz.societies.org");
//		values.put(SocialContract.Sharing.GLOBAL_ID_OWNER,		
//				"knut@redcross.org");
//		values.put(SocialContract.Sharing.TYPE, "Monitor");
//		values.put(SocialContract.Sharing.ORIGIN, "Red Cross");		
//		 
//		try {
////The Uri value returned is not used.
////			Uri activityNewUri = getContentResolver().insert( SocialContract.CommunityActivity.CONTENT_URI,
////										activityValues);
//			getContentResolver().insert( sharingUri, values);
//		} catch (Exception e) {
//			iDisasterApplication.getInstance().debug (2, "Insert to "+ sharingUri + "causes an exception");
//	    	return iDisasterApplication.getInstance().INSERT_EXCEPTION;
//		}
//
//
//		// Step 1a: get services of type "Monitor"
//					
//		String[] sharingProjection = new String[] {
//				SocialContract.Sharing._ID,
//				SocialContract.Sharing.GLOBAL_ID_SERVICE
////				,
////				SocialContract.Sharing.TYPE
//				};
//
////		String sharingSelection = SocialContract.Sharing.TYPE + "= ?";
////
////		String[] sharingSelectionArgs = new String[] 
////				{"Monitor"};		// Retrieve services of that type
//
//		String sharingSelection = null;
//
//		String[] sharingSelectionArgs = null;
//
//		Cursor sharingCursor;
//		try {
//			sharingCursor= getContentResolver().query(sharingUri, sharingProjection,
//					sharingSelection, sharingSelectionArgs, null /* sortOrder*/);
//		} catch (Exception e) {
//			debug (2, "Query to "+ sharingUri + "causes an exception");
//			return QUERY_EXCEPTION;
//
//		}
//
//		// Step 1b: replace the type to "Recommended"
//		if (sharingCursor == null) {			// No cursor was set - should not happen?
//			iDisasterApplication.getInstance().debug (2, "sharingCursor was not set to any value");
//			return QUERY_EMPTY;
//		}
//		
//		if (sharingCursor.getCount() == 0) {	// No service is recommended in the team community
//			return QUERY_EMPTY;
//		}		
//		
//		while (sharingCursor.moveToNext()) {
//			Uri recordUri = sharingUri.withAppendedPath(sharingUri, "/" +
//					sharingCursor.getString(sharingCursor.getColumnIndex(SocialContract.Sharing._ID)));
//	        values = new ContentValues();
//	        values.put(SocialContract.Sharing.TYPE, RECOMMENDED);
//	        getContentResolver().update(recordUri, values, null, null);		
//		}
//	
//		return QUERY_SUCCESS; 
		
	}
		


/**
* showDialog is used under testing
* parameters: activity context, message to be displayed, button text
*/
	public void showDialog (Context ctx, String displayMessage, String buttonText) {	
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setMessage(displayMessage)
			.setCancelable(false)
			.setPositiveButton (buttonText, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
		    	   return;
		         }
		    });
		AlertDialog dialog = builder.create();
		dialog.show();
	}
//  Example for using Test dialog
//	iDisasterApplication.getInstance().showDialog (this, getString(R.string.loginTestDialog), getString(R.string.dialogOK));
	



/***
 * Debug method to include the filename, line-number and method of the caller
 */
	public void debug(int d, String msg) {

		if (DEBUG >= d) {
			StackTraceElement[] st = Thread.currentThread().getStackTrace();
			int stackLevel = 2;
			while ( stackLevel < st.length-1
					&& ( st[stackLevel].getMethodName().equals("debug") || st[stackLevel].getMethodName().matches("access\\$\\d+") ) ){
				//|| st[stackLevel].getMethodName().matches("run")
				stackLevel++;
			}
			StackTraceElement e = st[stackLevel];
			if ( d < 0 ){ //error
				Log.e (DEBUG_Context, e.getMethodName() + ": " + msg + " at (" + e.getFileName()+":"+e.getLineNumber() +")" );
			} else { //debug
				Log.d (DEBUG_Context, e.getMethodName() + ": " + msg + " at (" + e.getFileName()+":"+e.getLineNumber() +")" );
			}
		}
	}
	
/**
 * setTestData is used for setting test data.
 * Test data are used instead of data provided SoicalProvider
 */
	private void setTestData () {
		
		me.displayName = "Knut";

		disasterNameList = new ArrayList<String> ();
		disasterDescriptionList = new ArrayList<String> ();
		disasterNameList.add ("Nicosia Team");
		disasterNameList.add ("Larnaka Team");
		disasterNameList.add ("Limassol Team");
		disasterDescriptionList.add ("Team assigned to the Nicosia region.");
		disasterDescriptionList.add ("Team assigned to the Larnaka region.");
		disasterDescriptionList.add ("Team assigned to the Limassol region.");


		feedContentList = new ArrayList<String> ();
		feedContentList.add ("Images sent");
		feedContentList.add ("Lakarna assessment postponed");
		feedContentList.add ("Translation Request: Kren-douar");

		memberNameList = new ArrayList<String> ();
		memberDescriptionList = new ArrayList<String> ();
		memberNameList.add ("Tim");
		memberNameList.add ("Tom");
		memberDescriptionList.add ("Doctor.");
		memberDescriptionList.add ("Civil Engineer.");

		serviceNameList = new ArrayList<String> ();
		serviceDescriptionList = new ArrayList<String> ();	
		serviceNameList.add ("Share picture");
		serviceDescriptionList.add ("This service allows picture sharing with your team.");
		serviceNameList.add ("iJacket");
		serviceDescriptionList.add ("This service allows people in your team to remote control your jacket.");
		serviceNameList.add ("Ask for help");
		serviceDescriptionList.add ("This service allows you to request help from volunteers.");
			
		CISserviceNameList = new ArrayList<String> ();
		CISserviceDescriptionList = new ArrayList<String> ();	
		CISserviceNameList.add ("Share data");
		CISserviceDescriptionList.add ("This service allows data sharing with your team.");
		CISserviceNameList.add ("Send alert");
		CISserviceDescriptionList.add ("This service allows you to send an alert team members.");
		
	} // end setTestData


// All information is fetched from the Social Provider. Preferences are no longer used.
//
//		public String getDisasterTeamName () {
//			return preferences.getString ("pref.disasterteamname", getString(R.string.noPreference));
//		}
//
//		public void setDisasterTeamName (String name) {
//	    	editor.putString ("pref.disasterteamname", name);
//	    	editor.commit ();
//			
//		}

}
