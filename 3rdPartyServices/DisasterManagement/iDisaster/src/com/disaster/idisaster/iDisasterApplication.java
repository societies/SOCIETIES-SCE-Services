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

import com.disaster.idisaster.R;

//import org.societies.android.platform.client.SocietiesApp;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.widget.ArrayAdapter;

/**
 * The application for managing common resources used by 
 * iDisaster application components.
 * 
 * @author 	Jacqueline.Floch@sintef.no
 *
 */
public class iDisasterApplication extends Application {
	

	private static iDisasterApplication singleton; // Reference to the single instance of the Application
	
	static final String PREFS_NAME = "iDisasterPreferences"; 	// File for storing preferences
	SharedPreferences preferences;								// Preferences shared with all activities
	Editor editor;												// Editor for changing preferences

	Boolean platformLoggedIn = false;
//	SocietiesApp iDisasterSoc; 							// represents access to the SOCIETIES platform.

//TODO: remove test code
	ArrayList <String> disasterNameList = new ArrayList<String> ();
	ArrayList <String> disasterDescriptionList = new ArrayList<String> ();

	ArrayList <String> feedContentList = new ArrayList<String> ();

	ArrayList <String> memberNameList = new ArrayList<String> ();
	ArrayList <String> memberDescriptionList = new ArrayList<String> ();

	ArrayList <String> serviceNameList = new ArrayList<String> ();
	ArrayList <String> serviceDescriptionList = new ArrayList<String> ();

	
//TODO: discuss design. Are these common resources really needed?	
	ArrayAdapter<String> feedAdapter;
	ArrayAdapter<String> memberAdapter;
	ArrayAdapter<String> serviceAdapter;

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

	    // Restore preferences from preferences file.
		// If the preferences file does not exist, it is created when changes are committed.
		preferences = getSharedPreferences(PREFS_NAME, 0);
	    editor = preferences.edit();
	    editor.putString ("pref.dummy", "");
	    editor.commit ();

//TODO: remove test code
//		disasterNameList.add ("Nicosia Team");
//		disasterNameList.add ("Larnaka Team");
//		disasterNameList.add ("Limassol Team");
//		disasterDescriptionList.add ("Team assigned to the Nicosia region.");
//		disasterDescriptionList.add ("Team assigned to the Larnaka region.");
//		disasterDescriptionList.add ("Team assigned to the Limassol region.");

		memberNameList.add ("Tim");
		memberNameList.add ("Tom");
		memberDescriptionList.add ("Doctor.");
		memberDescriptionList.add ("Civil Engineer.");

		serviceNameList.add ("Share picture");
		serviceDescriptionList.add ("This service allows picture sharing with your team.");
		serviceNameList.add ("Jacket control");
		serviceDescriptionList.add ("This service allows people in your team to remote control your jacket.");
		serviceNameList.add ("Ask for help");
		serviceDescriptionList.add ("This service allows you to request help from volunteers.");
		serviceNameList.add ("Test");
		serviceDescriptionList.add ("This service is a test.");
// end of removed
		
////TODO: CSS_ID should be stored
//	    if (getUserName () != getString(R.string.noPreference)){
//	    	platformLogIn();	// Instantiate the Societies platform
//	    }
	    
	} //onCreate

////TODO: Replace to call to SocialProvider
///**
// * platformLogIn supports checking connection with SocialProvider
// * and retrieving the CSS_ID and name for the user.
// */	  
//
//	public void platformLogIn () {
//
////TODO: catch exception if
////		- no response from SocialProvider.
//		
//		platformLoggedIn = true;
//	}

//TODO: Go through the following code and remove if nor necessary

//	public String getUserName () {
//		return preferences.getString ("pref.username",getString(R.string.noPreference));
//	}
//
//	public void setUserIdentity (String name, String email, String password) {
//    	editor.putString ("pref.username", name);
//    	editor.putString ("pref.email", email);
//    	editor.putString ("pref.password", password);
//    	editor.commit ();    	
//	}
//
//
//	public String getEmail () {
//		return preferences.getString ("pref.email", getString(R.string.noPreference));
//	}
//
//	public String getPassword () {
//		return preferences.getString ("pref.password", getString(R.string.noPreference));
//	}
//

	public String getDisasterName () {
		return preferences.getString ("pref.disastername", getString(R.string.noPreference));
	}

	public void setDisasterName (String name) {
    	editor.putString ("pref.disastername", name);
    	editor.commit ();
		
	}

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
