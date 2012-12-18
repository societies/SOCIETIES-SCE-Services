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
import java.util.List;

import org.societies.android.api.cis.SocialContract;
import org.societies.thirdpartyservices.idisaster.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import android.widget.TextView;
import android.widget.Toast;

import android.widget.Button;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;


/**
 * Activity for showing service details.
 * 
 * @author Jacqueline.Floch@sintef.no
 *
 */
public class ServiceDetailsActivity extends Activity implements OnClickListener {

	private TextView serviceNameView;
	private TextView serviceDescriptionView;
	
	private ContentResolver resolver;

	Cursor serviceCursor;
	private String serviceID;
	private String serviceName;
	private String serviceDescription;
// Used temporarily - waiting for extension of Social Provider
	private String serviceType;
	private String serviceAppType;
	private String serviceAvailable;
	private String serviceDependency;
	private String serviceConfig;
	private String serviceURL;
	
	private String requestType;

	
	//TODO: will be fetched from SocialProvider when available...
	private String servicePackage ="org.ubicompforall.cityexplorer";  // can be found
	private String serviceIntent =".gui.CalendarActivity";	// can be found
//	private String serviceIntent ="org.ubicompforall.cityexplorer.gui.PlanPoiTab";  // can be found
	
	// Constant keys for servicAction
	public static final String SERVICE_INSTALL = "Install";
	public static final String SERVICE_LAUNCH = "Launch";
	public static final String SERVICE_SHARE = "Share";

	private String serviceAction = SERVICE_INSTALL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.service_details_layout);
    	resolver = getContentResolver();



		Intent intent= getIntent(); 						// Get the intent that created activity		
		requestType = intent.getStringExtra("TYPE"); 	 	// Retrieve first parameter (type of service in the CIS)
		serviceID = intent.getStringExtra("SERVICE_ID"); 	// Retrieve second parameter: service ID
		
		if (iDisasterApplication.testDataUsed) {
			int position = Integer.parseInt(serviceID);
			serviceName = iDisasterApplication.getInstance().CISserviceNameList.get (position);
			serviceDescription = iDisasterApplication.getInstance().CISserviceDescriptionList.get (position);
			serviceAction = iDisasterApplication.getInstance().SERVICE_RECOMMEND;
		} else {
			if (getServiceInformation () 		// Retrieve service information from SocialProvider
					.equals(iDisasterApplication.getInstance().QUERY_EXCEPTION)) {
				showQueryExceptionDialog ();	// Exception: Display dialog and terminates activity
			}

			// TODO: Add a check that some info was retrieved
		}
		
		// Get text fields
		serviceNameView = (TextView) findViewById(R.id.serviceDetailsName);
		serviceNameView.setText(serviceName);

		// Set name and description
		serviceDescriptionView = (TextView) findViewById(R.id.serviceDetailsDescription);
		serviceDescriptionView.setText(serviceDescription);
		
				
		final Button button = (Button) findViewById(R.id.serviceDetailsButton);

// TODO: Check the return code of checkServiceInstallStatus in the following
		
		if (requestType.equals (iDisasterApplication.getInstance().SERVICE_RECOMMENDED)) {	// The service is recommended in the CIS
			
			if (serviceInstalled(serviceCursor.getString(serviceCursor				// If the service is installed on the device
					.getColumnIndex(SocialContract.Services.CONFIG)))) {
				serviceAction = iDisasterApplication.getInstance().SERVICE_LAUNCH;	// Action available to user is Launch			
				checkServiceInstallStatus (true);									// Check consistency with data in SocialProvider				
			} else {																// If the service is NOT installed on the device
				serviceAction = iDisasterApplication.getInstance().SERVICE_INSTALL; // Action available to user is Install
				checkServiceInstallStatus (false);											// Check consistency with data in SocialProvider
			}
			
		} else if (requestType.equals (iDisasterApplication.getInstance().SERVICE_INSTALLED)) {	// The service is marked installed by the user
			if (serviceInstalled(serviceCursor.getString(serviceCursor				// If the service is installed on the device
					.getColumnIndex(SocialContract.Services.CONFIG)))) {
				serviceAction = iDisasterApplication.getInstance().SERVICE_SHARE;	// Action available to user is Share
				checkServiceInstallStatus (true);										// Check consistency with data in SocialProvider
			} else {																// If the service is NOT installed on the device
				serviceAction = iDisasterApplication.getInstance().SERVICE_INSTALL;	// Action available to user is Install
				updateServiceInstallStatus (false);											// Update information in database
			}
			
		} else if (requestType.equals (iDisasterApplication.getInstance().SERVICE_SHARED)) {
// TODO: implement unshare
			serviceAction ="Back";
		}
		
		button.setText(serviceAction);
		button.setOnClickListener(this);
    }


	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View arg0) {

		// TODO: Remove code for testing the correct setting of preferences
//		if (serviceAction == SERVICE_INSTALL) {
//			// check that the service is not yet installed
//		    Intent intent1 = new Intent(serviceIntent);      
		      
//		    if (isCallable(intent1)== true){
//    			Toast.makeText(getApplicationContext(),  serviceIntent + " found" , Toast.LENGTH_LONG).show();		    	
//		    } else {
//    			Toast.makeText(getApplicationContext(),  serviceIntent + " NOT found" , Toast.LENGTH_LONG).show();		    	
//		    	
//		    }
//			
//		    if (serviceInstalled (servicePackage) == true){
//    			Toast.makeText(getApplicationContext(),  servicePackage + " found" , Toast.LENGTH_LONG).show();		    	
//		    } else {
//    			Toast.makeText(getApplicationContext(),  servicePackage + " NOT found" , Toast.LENGTH_LONG).show();
//		    	
//		    }
//
//			
//		} else if (serviceAction == SERVICE_LAUNCH) {
//			
//		} else if (serviceAction == SERVICE_SHARE) {
//			
//		} else {
//			// This should never happened!
//			// TODO: Update data in Content provider and proceeds as for install
//		}
//
//		
		
		Toast.makeText(getApplicationContext(),
			"Not implemented yet!", Toast.LENGTH_LONG).show();
		
	}



/**
 * getServiceInformation retrieves the information about the service 
 * from Social Provider.
 */

	private String getServiceInformation () {
		
		Uri serviceUri = SocialContract.Services.CONTENT_URI;
						
		String[] serviceProjection = new String[] {
							SocialContract.Services.NAME,
							SocialContract.Services.DESCRIPTION
// TODO: remove - Used temporarily - waiting for extension of Social Provider							
							,
							SocialContract.Services.TYPE,
							SocialContract.Services.APP_TYPE,
							SocialContract.Services.AVAILABLE,
							SocialContract.Services.DEPENDENCY,
							SocialContract.Services.CONFIG,
							SocialContract.Services.URL
							};
		String serviceSelection = SocialContract.Services.GLOBAL_ID + "= ?";
		String [] serviceSelectionArgs = new String [] {serviceID} ;
		
		try {
			serviceCursor = resolver.query (serviceUri, serviceProjection, serviceSelection,
					   serviceSelectionArgs,
					   null /* sortOrder*/);
		} catch (Exception e) {
			iDisasterApplication.getInstance().debug (2, "Query to "+ serviceUri + "causes an exception");
			return iDisasterApplication.getInstance().QUERY_EXCEPTION;
		}
		
		if (serviceCursor == null) {
			return iDisasterApplication.getInstance().QUERY_EXCEPTION;
		}
		
		if (serviceCursor.getCount() == 0) {
			return iDisasterApplication.getInstance().QUERY_EMPTY;
		}
		
		serviceCursor.moveToFirst();		// Should not be several matches; anyway only select the first
		serviceName = serviceCursor.getString(serviceCursor
						.getColumnIndex(SocialContract.Services.NAME));
		serviceDescription = serviceCursor.getString(serviceCursor
						.getColumnIndex(SocialContract.Services.DESCRIPTION));
		
//TODO: Remove - Used temporarily - waiting for extension of Social Provider

		serviceType = serviceCursor.getString(serviceCursor
					.getColumnIndex(SocialContract.Services.TYPE));
		serviceAppType=serviceCursor.getString(serviceCursor
					.getColumnIndex(SocialContract.Services.APP_TYPE));
		serviceAvailable = serviceCursor.getString(serviceCursor
				.getColumnIndex(SocialContract.Services.AVAILABLE));
		serviceDependency = serviceCursor.getString(serviceCursor
				.getColumnIndex(SocialContract.Services.DEPENDENCY));
		serviceConfig = serviceCursor.getString(serviceCursor
				.getColumnIndex(SocialContract.Services.CONFIG));
		serviceURL = serviceCursor.getString(serviceCursor
				.getColumnIndex(SocialContract.Services.APP_TYPE));
		

		return iDisasterApplication.getInstance().QUERY_SUCCESS;
	}


/**
 * checkServiceInstallStatus checks that the service status (installed) setting in
 * in Social provider.
 */

	public String checkServiceInstallStatus (boolean installStatus) {
		
		Cursor installedServiceCursor;
		Uri serviceUri = SocialContract.Services.CONTENT_URI;

		String[] serviceProjection = new String[] {
						SocialContract.Services.AVAILABLE
						};
		String serviceSelection = SocialContract.Services.GLOBAL_ID + "= ?" +
						"AND " + SocialContract.Services.OWNER_ID + "= ?";
		
		String [] serviceSelectionArgs = new String [] 
								{serviceID,
								iDisasterApplication.getInstance().me.globalId	} ;

		try {
			installedServiceCursor = resolver.query (serviceUri, serviceProjection, serviceSelection,
					serviceSelectionArgs, null /* sortOrder*/);
		} catch (Exception e) {
			iDisasterApplication.getInstance().debug (2, "Query to "+ serviceUri + "causes an exception");
			return iDisasterApplication.getInstance().QUERY_EXCEPTION;
		}

		if (installedServiceCursor == null) {		// Should never happen
			return iDisasterApplication.getInstance().QUERY_EXCEPTION;
		}

		if (installedServiceCursor.getCount() == 0) {
			if (installStatus) {	// The service is installed on the device and should be added to Social Provider
				
				// Add the service in table. It should be there!

				// Set the values related to the activity to store in Social Provider
				ContentValues serviceValues = new ContentValues ();
				
//TODO: Remove the following once Social Provider has been corrected (Social Provider should insert the GLOBAL_ID)
//				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
//				String currentDateandTime = sdf.format(new Date());
				serviceValues.put(SocialContract.Services.GLOBAL_ID, serviceID);	//TODO: problem ID is duplicated!
// End remove		
//				serviceValues.put(SocialContract.Services.TYPE, "Disaster");
//				serviceValues.put(SocialContract.Services.NAME, serviceName);
//				serviceValues.put(SocialContract.Services.OWNER_ID,
//						iDisasterApplication.getInstance().me.globalId);
//				serviceValues.put(SocialContract.Services.ORIGIN, "Red Cross");
//				serviceValues.put(SocialContract.Services.ORIGIN, "Red Cross");
//				serviceValues.put(SocialContract.Services.ORIGIN, "Red Cross");
//				serviceValues.put(SocialContract.Services.ORIGIN, "Red Cross");
//				serviceValues.put(SocialContract.Services.DESCRIPTION, serviceDescription);
//				serviceValues.put(SocialContract.Services.AVAILABLE, 
//						iDisasterApplication.getInstance().SERVICE_INSTALLED);
//TODO: set to right value!
//				serviceValues.put(SocialContract.Services.DEPENDENCY, "");
//				serviceValues.put(SocialContract.Services.CONFIG, "");
//				serviceValues.put(SocialContract.Services.URL, "");
							
				
//			    GLOBAL_ID (needed temporally until we have a working sync adapter)
//			    NAME
//			    OWNER_ID
//			    TYPE
//			    DESCRIPTION (optional, set to "NA" if not provided)
//			    APP_TYPE
//			    ORIGIN
//			    AVAILABLE
//			    DEPENDENCY (Optional, set to "NA" if not provided)
//			    CONFIG (Optional, set to "NA" if not provided)
//			    URL (Optional, set to "NA" if not provided) 

				
				
				try {
// The Uri value returned is not used.
//					Uri activityNewUri = getContentResolver().insert( SocialContract.CommunityActivity.CONTENT_URI,
//							sharingValues);
					getContentResolver().insert( SocialContract.Services.CONTENT_URI, 
							serviceValues);
				} catch (Exception e) {
					iDisasterApplication.getInstance().debug (2, "Insert to "+ 
										SocialContract.Services.CONTENT_URI + "causes an exception");
			    	return iDisasterApplication.getInstance().INSERT_EXCEPTION;
				}
				
				
				return iDisasterApplication.getInstance().QUERY_SUCCESS;
				
				
			}
			else {					// This is normal. The service is not installed on the device
				return iDisasterApplication.getInstance().QUERY_EMPTY;
			}
		}
		
		installedServiceCursor.moveToFirst();		// Should not be several matches; anyway only select the first
//TODO: check AVAILABLE: it should be set to 
		String available = installedServiceCursor.getString(installedServiceCursor
				.getColumnIndex(SocialContract.Services.AVAILABLE));	
		
		if (installStatus) {	// The service is installed on the device and should marked available in Social Provider
			if (available.equals(iDisasterApplication.getInstance().SERVICE_INSTALLED)) {
				return iDisasterApplication.getInstance().QUERY_SUCCESS;				
			} else {
				// Set the filed AVAILABLE to the right value 
				return iDisasterApplication.getInstance().QUERY_SUCCESS;				
			}
		} else {				// The service is NOT installed on the device and not be stored in Social Provider
			if (available.equals(iDisasterApplication.getInstance().SERVICE_INSTALLED)) {
				// TODO: Remove from Social Provider. The service should not be there
				return iDisasterApplication.getInstance().QUERY_SUCCESS;				
			} else {
				// Set the filed AVAILABLE to the right value 
				return iDisasterApplication.getInstance().QUERY_SUCCESS;				
			}
		}
				
// TODO: Remove any other entries. There should only be a service registration per user!
			
	}

/**
 * updateServiceInstallStatus ...
 */

	public void updateServiceInstallStatus (boolean status) {
		// TODO: Update data in Content provider
		// Check that the service (App) is installed
	}

/**
 * Check if any installed App can answer the Intent given as a parameter.
 */
	
    private boolean isCallable (Intent intent1) {    
        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent1, PackageManager.MATCH_DEFAULT_ONLY);    
        if(list.size() > 0)  
        	return true ;    
        else  
        	return false;  
  
    }  

/**
 * Check if any installed App has the package name given as a parameter.
 */

    private boolean serviceInstalled (String appPackageName) {
    	
    	if (appPackageName == null) {
    		return (false);
    	}
    	// Get package names of all installed applications
    	PackageManager pm = getPackageManager();
    	List <PackageInfo> list = pm.getInstalledPackages(0);
    
    	for (PackageInfo pi : list) {
    	   ApplicationInfo ai;
    	   try {
    		   ai = pm.getApplicationInfo(pi.packageName, 0);
    		   if (!((ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0)) {		// Only consider 3rd party Apps
    			   															// Ignore Apps installed in the device's system image. 
    			   if (appPackageName.equals(pi.packageName)) {
    				   return (true);
    			   }    	        	 
    		   }
    	      
    	   } catch (NameNotFoundException e) {
    		   Log.d(getClass().getSimpleName(), "Name not found", e);
    	   }
    	}
    	return (false);
	}
    
    
//	final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
//	mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//	final List pkgAppsList = context.getPackageManager().queryIntentActivities( mainIntent, 0);


/**
 * showQueryExceptionDialog displays a dialog to the user.
 * In this case, the activity does not terminate since the other
 * activities in the TAB may still work.
 */
    	        			
    private void showQueryExceptionDialog () {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setMessage(getString(R.string.dialogServiceDetailsQueryException))
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
