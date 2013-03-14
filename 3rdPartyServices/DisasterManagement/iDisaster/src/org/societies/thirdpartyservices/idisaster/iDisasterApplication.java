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
import org.societies.android.api.cis.SupportedAccountTypes;
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

// All information is fetched from the SocialProvider. Preferences are no longer used.
//
//	static final String PREFS_NAME = "iDisasterPreferences"; 	// File for storing preferences
//	SharedPreferences preferences;								// Preferences shared with all activities
//	Editor editor;												// Editor for changing preferences

	static final Boolean testDataUsed = false;		// When set to true do not used SocialProvider

	Me me = new Me();										// Store user identity - not persistent data (can be retrieved from SocialProvider)
	SelectedTeam selectedTeam = new SelectedTeam ();		// Store team selected by the user - not persistent data (can be retrieved from SocialProvider)
	
	// Constant keys used for user Logging
	public final String USER_NOT_IDENTFIED = "USER_NOT_IDENTFIED";

	// Constant keys used for query
	public final String QUERY_EMPTY = "QUERY_EMPTY";
	public final String QUERY_EXCEPTION = "QUERY_EXCEPTION";
	public final String QUERY_SUCCESS = "QUERY_SUCCESS";
	// Constant keys used for insert
	public final String INSERT_EXCEPTION = "INSERT_EXCEPTION";
	public final String INSERT_SUCCESS = "INSERT_SUCCESS";
	// Constant keys used for update
	public final String UPDATE_EXCEPTION = "UPDATE_EXCEPTION";
	public final String UPDATE_SUCCESS = "UPDATE_SUCCESS";
	// Constant keys for table inconsistency
	public final String NO_ENTRY = "NO_ENTRY";

	// Constant keys used for communities
	public final String COMMUNITY_TYPE = "DISASTER";

	// Constant keys used for activity feeds
	public final String VERB_TEXT = "VERB_TEXT";
	public final String TARGET_ALL = "TARGET_ALL";
	
	// Constant used for services in a community
	public final String SERVICE_NOT_SHARED = "SERVICE_NOT_SHARED";
	

	// Constant used for operations on services
	public final String SERVICE_RECOMMEND = "SERVICE_RECOMMEND";
	public final String SERVICE_INSTALL = "SERVICE_INSTALL";
	public final String SERVICE_LAUNCH = "SERVICE_LAUNCH";
	public final String SERVICE_SHARE = "SERVICE_SHARE";	
	public final String SERVICE_UNSHARE = "SERVICE_UNSHARE";
	public final String SERVICE_NO_ACTION = "SERVICE_NO_ACTION";

	
	// Constant keys used for service download (and install)
	public final String DOWNLOAD_EXCEPTION = "DOWNLOAD_EXCEPTION";
	public final String DOWNLOAD_SUCCESS = "DOWNLOAD_SUCCESS";

	// Constant keys used for service launch
	public final String LAUNCH_EXCEPTION = "LAUNCH_EXCEPTION";
	public final String LAUNCH_SUCCESS = "LAUNCH_SUCCESS";
	

// TODO: Remove this variable - only used while waiting update for SocialProvider
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

// All information is fetched from the SocialProvider. Preferences are no longer used.
//
// Restore preferences from preferences file.
// If the preferences file does not exist, it is created when changes are committed.
//		preferences = getSharedPreferences(PREFS_NAME, 0);
//	    editor = preferences.edit();
//	    editor.putString ("pref.dummy", "");
//	    editor.commit ();

// Test code - to be used only for test when SocialProvider is not avaibale
	    if (testDataUsed) {   
	    	setTestData ();	    	
	    }
	    
// The following code was used for testing purpose while waiting update for SocialProvider
//	    if (!servicesUpdated) {
//	    	servicesUpdated = true;
//	    	updateServices ();
//	    }
			    
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
			SocialContract.Me._ID,
			SocialContract.Me._ID_PEOPLE,
//			SocialContract.Me.GLOBAL_ID,		Not needed is set to PENDING
			SocialContract.Me.NAME,
			SocialContract.Me.DISPLAY_NAME,
			SocialContract.Me.USER_NAME			
		};

		String selection = SocialContract.Me.ACCOUNT_TYPE + "= ?"; // Use the first user identity with Account in box.com
		String[] selectionArgs = new String[] {SupportedAccountTypes.COM_BOX};

//  Alternative query - does not work with new version of SocialProvider
//		String selection = SocialContract.Me.ACCOUNT_TYPE + " = " + SupportedAccountTypes.COM_BOX; // Use the first user identity with Account in box.com
//		String[] selectionArgs = null;

		String sortOrder = null;
	
        try{
        	cursor = getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
        } catch (Exception e) {
			debug (2, "Query to "+ uri + "causes an exception");
        	return QUERY_EXCEPTION;
        }
			
		if (cursor == null) {
// Test code
//			showDialog (ctx, "Unable to retrieve user information from SocialProvider", getString(R.string.dialogOK));
			me.displayName = USER_NOT_IDENTFIED;
			debug (2, "No instance of Me");
			return QUERY_EMPTY;
		}
			
		if (cursor.getCount() == 0) {
// Test code
//			showDialog (ctx, "Unable to retrieve user information from SocialProvider", getString(R.string.dialogOK));
			me.displayName = USER_NOT_IDENTFIED;
			debug (2, "No instance of Me");
			return QUERY_EMPTY;
		}

// Debug code
//		String id;		
//		String idPeople;		
////		String globalId;		
//		String name;
//		String dislayName;
//		String userName;
//		String AccountType;
//		
//		
//		int i =0;
//		while (cursor.moveToNext()) {
//				id = cursor.getString(cursor.getColumnIndex(SocialContract.Me._ID));
//				idPeople = cursor.getLong(cursor.getColumnIndex(SocialContract.Me._ID_PEOPLE));
////				globalId = cursor.getString(cursor.getColumnIndex(SocialContract.Me.GLOBAL_ID));
//				name = cursor.getString(cursor.getColumnIndex(SocialContract.Me.NAME));
//				dislayName = cursor.getString(cursor.getColumnIndex(SocialContract.Me.DISPLAY_NAME));
//				userName = cursor.getString(cursor.getColumnIndex(SocialContract.Me.USER_NAME));
//				
//				i++;				
//		}
		
		if (cursor.moveToFirst()){
			
			me.peopleId = cursor.getLong (cursor.getColumnIndex(SocialContract.Me._ID_PEOPLE));
// Cannot be set here - Me.GLOBAL_ID is set to PENDING
// Should be set in checkPeople
//			me.peopleGlobalId = cursor.getString(cursor
//					.getColumnIndex(SocialContract.Me.GLOBAL_ID));

			// Check that the user can be found in People
			
			// IMPORTANT: if this check is removed, make sure that 
			// me.peopleGlobalId and me.peopleId are set properly
			
			if (checkPeople (cursor).equals(QUERY_SUCCESS)) {
//				me.globalId = cursor.getString(cursor
//								.getColumnIndex(SocialContract.Me.GLOBAL_ID));
				me.name = cursor.getString(cursor
								.getColumnIndex(SocialContract.Me.NAME));
				me.displayName = cursor.getString(cursor
						.getColumnIndex(SocialContract.Me.DISPLAY_NAME));
				if (me.displayName.equals(SocialContract.VALUE_NOT_DEFINED)) {
					me.displayName = me.name;		// use name as display name
				}
				me.userName = cursor.getString(cursor
						.getColumnIndex(SocialContract.Me.USER_NAME));

				return QUERY_SUCCESS;		// The only case where true is returned				
			} else
				return NO_ENTRY;			// No entry matching entry in People
		}
		// should not happen
		return QUERY_EMPTY;
	}

/**
 * 	checkPeople checks that the key that connects the user account to a row in
 * 	the People table is correct. If not it tries to set it.
 * 
 * 	Returns a query code:
 * 			QUERY_SUCCESS if the user account is registered in Me
 * 			QUERY_EMPTY if the user is not registered
 * 			QUERY_EXECPTION if the query fails
 * 
 *  This method is defined by iDisaster such as a check can be made in any
 *  activity. 
 */
	public String checkPeople (Cursor meCursor) {
		
		Cursor peopleCursor = null;
		Uri uri = SocialContract.People.CONTENT_URI;

		
		// Creates query to People
		String[] projection = new String [] {
				SocialContract.People._ID,
				SocialContract.People.GLOBAL_ID,
//				SocialContract.People.NAME,
				SocialContract.People.EMAIL
			};
		String selection = SocialContract.People.EMAIL + "= ?"; // Match users on email address
		String[] selectionArgs = new String[] {meCursor.getString(meCursor.getColumnIndex(SocialContract.Me.USER_NAME))};
		String sortOrder = null;	
        try{
        	peopleCursor = getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
        } catch (Exception e) {
			debug (2, "Query to "+ uri + "causes an exception");
        	return QUERY_EXCEPTION;
        }			
		if (peopleCursor == null) { 			// should not happen
			return QUERY_EMPTY;
		}		
		if (peopleCursor.getCount() == 0) {		// No match in People - Inconsistent tables
			return QUERY_EMPTY;
		}

		if (peopleCursor.moveToFirst()) {			
			long peopleId = peopleCursor.getLong (peopleCursor
					.getColumnIndex(SocialContract.People._ID));

			if (me.peopleId == peopleId) {		// Me and People are consistent
				me.peopleGlobalId =  peopleCursor.getString(peopleCursor
						.getColumnIndex(SocialContract.People.GLOBAL_ID));
				return QUERY_SUCCESS;
			} else {							// Update the table Me
				me.peopleId = -1;
				Uri meUri = SocialContract.Me.CONTENT_URI;
				Uri recordUri = meUri.withAppendedPath(meUri, "/" +
						meCursor.getString(meCursor.getColumnIndex(SocialContract.Me._ID)));
				ContentValues values = new ContentValues();
				values.put(SocialContract.Me._ID_PEOPLE, peopleId);
				try {
					getContentResolver().update(recordUri, values, null, null);
				} catch (Exception e) {
					debug (2, "Query to "+ uri + "causes an exception");
		        	return UPDATE_EXCEPTION;
		        }
				me.peopleId = peopleId;
				me.peopleGlobalId =  peopleCursor.getString(peopleCursor
						.getColumnIndex(SocialContract.People.GLOBAL_ID));
				return QUERY_SUCCESS;
			}
		}		
		return QUERY_EMPTY;
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


// All information is fetched from the SocialProvider. Preferences are no longer used.
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
