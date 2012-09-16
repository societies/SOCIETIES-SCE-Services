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

import java.util.ArrayList;

//import org.societies.android.platform.client.SocietiesApp;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.ArrayAdapter;

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


	static final Boolean testDataUsed = true;		// When set to true do not used SocialProvider
	String disasterTeamName = "n/a";

//TODO: remove test code
	ArrayList <String> disasterNameList = new ArrayList<String> ();
	ArrayList <String> disasterDescriptionList = new ArrayList<String> ();

	ArrayList <String> feedContentList = new ArrayList<String> ();

	ArrayList <String> memberNameList = new ArrayList<String> ();
	ArrayList <String> memberDescriptionList = new ArrayList<String> ();

	ArrayList <String> serviceNameList = new ArrayList<String> ();
	ArrayList <String> serviceDescriptionList = new ArrayList<String> ();	

	ArrayList <String> CISserviceNameList = new ArrayList<String> ();
	ArrayList <String> CISserviceDescriptionList = new ArrayList<String> ();	

	
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
	public static final int DEBUG = 0;
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

//TODO: remove test code

	    if (testDataUsed) {   
	    	disasterNameList.add ("Nicosia Team");
	    	disasterNameList.add ("Larnaka Team");
	    	disasterNameList.add ("Limassol Team");
	    	disasterDescriptionList.add ("Team assigned to the Nicosia region.");
	    	disasterDescriptionList.add ("Team assigned to the Larnaka region.");
	    	disasterDescriptionList.add ("Team assigned to the Limassol region.");

	    	memberNameList.add ("Tim");
	    	memberNameList.add ("Tom");
	    	memberDescriptionList.add ("Doctor.");
	    	memberDescriptionList.add ("Civil Engineer.");

	    	CISserviceNameList.add ("Share data");
	    	CISserviceDescriptionList.add ("This service allows data sharing with your team.");
	    	CISserviceNameList.add ("Send alert");
	    	CISserviceDescriptionList.add ("This service allows you to send an alert team members.");
	    	
	    	serviceNameList.add ("Share picture");
	    	serviceDescriptionList.add ("This service allows picture sharing with your team.");
	    	serviceNameList.add ("iJacket");
	    	serviceDescriptionList.add ("This service allows people in your team to remote control your jacket.");
	    	serviceNameList.add ("Ask for help");
	    	serviceDescriptionList.add ("This service allows you to request help from volunteers.");
	    	
	    	
	    	
	    	
	    }
// end of removed
			    
	} //onCreate

// All information is fetched from the Social Provider. Preferences are no longer used.
//
//	public String getDisasterTeamName () {
//		return preferences.getString ("pref.disasterteamname", getString(R.string.noPreference));
//	}
//
//	public void setDisasterTeamName (String name) {
//    	editor.putString ("pref.disasterteamname", name);
//    	editor.commit ();
//		
//	}

/**
* showDialog is used under testing
* parameters: activity context, message to be displayed, button text
*/
	public void showDialog (Context c, String displayMessage, String buttonText) {	
		AlertDialog.Builder builder = new AlertDialog.Builder(c);
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
	public static void debug(int d, String msg) {

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

}
