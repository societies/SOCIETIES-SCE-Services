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
	private TextView serviceStatusView;
	
	private ContentResolver resolver;

	Uri serviceUri = SocialContract.Services.CONTENT_URI;
	Cursor serviceCursor;
	private String serviceID;
	private String serviceName;
	private String serviceDescription;
	private String serviceAvailable;
	
// Used temporarily - waiting for extension of Social Provider
	private String serviceType;
	private String serviceAppType;
	private String serviceDependency;
	private String serviceConfig;
	private String serviceURL;
	
	private String requestContext;

// Status information for the user. Not stored in SocialProvider 
	private String serviceStatus;
	private String SERVICE_STATUS_RECOMMENDED = "This service is recommended in the team.";
	private String SERVICE_STATUS_INSTALLED = "This service is installed on your device. You can share it with the team members.";
	private String SERVICE_STATUS_REMOVED = "This service was not properly or is no longer installed on your device.";
	private String SERVICE_STATUS_SHARED_BY_ME = "You have shared this service with the team members.";
	private String SERVICE_STATUS_SHARED_IN_CIS = "This service is shared by other team members.";

	private String SERVICE_STATUS_UNKNOWN = "No more information available.";
	
	

	
	
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



		Intent intent= getIntent(); 							// Get the intent that created activity		
		requestContext = intent.getStringExtra("REQUEST_CONTEXT");	// Retrieve first parameter (context for request of details)
		serviceID = intent.getStringExtra("SERVICE_ID"); 		// Retrieve second parameter: service ID
		
		if (iDisasterApplication.testDataUsed) {
			int position = Integer.parseInt(serviceID);
			serviceName = iDisasterApplication.getInstance().CISserviceNameList.get (position);
			serviceDescription = iDisasterApplication.getInstance().CISserviceDescriptionList.get (position);
			serviceAvailable = iDisasterApplication.getInstance().SERVICE_NOT_INSTALLED;
			serviceAction = iDisasterApplication.getInstance().SERVICE_RECOMMEND;
		} else {
			// Retrieve service information from SocialProvider
			if (!(getServiceInformation ()
					.equals(iDisasterApplication.getInstance().QUERY_SUCCESS))) {
				showQueryExceptionDialog ();	// Exception: Display dialog and terminates activity
			}
			
			// Check that availability status in SocialProvider is consistent with 
			// service availability	on the device		
			if (!(checkServiceInstallStatus ()
					.equals (iDisasterApplication.getInstance().UPDATE_SUCCESS))) {
				showQueryExceptionDialog ();	// Exception: Display dialog and terminates activity			
			}	
		}
		
		// Get text fields
		serviceNameView = (TextView) findViewById(R.id.serviceDetailsName);
		serviceDescriptionView = (TextView) findViewById(R.id.serviceDetailsDescription);
		serviceStatusView = (TextView) findViewById(R.id.serviceDetailsStatus);

		// Set name and description
		serviceNameView.setText(serviceName);
		serviceDescriptionView.setText(serviceDescription);
		
		// Set status (on UI) to temporary value
		serviceStatusView.setText(SERVICE_STATUS_UNKNOWN);

				
		final Button button = (Button) findViewById(R.id.serviceDetailsButton);

		if (requestContext.equals 
				(iDisasterApplication.getInstance().SERVICE_RECOMMENDED)) {			// Details request for a service recommended in the CIS
			
			// Set status (on UI)
			serviceStatusView.setText(SERVICE_STATUS_RECOMMENDED);
			
			if (serviceAvailable															// If the service is installed on the device
					.equals(iDisasterApplication.getInstance().SERVICE_INSTALLED)) {
				serviceAction = iDisasterApplication.getInstance().SERVICE_LAUNCH;	// Action available to user is Launch
				
			} else {																		// If the service is NOT installed on the device
				serviceAction = iDisasterApplication.getInstance().SERVICE_INSTALL; // Action available to user is Install
			}
			
		} else if (requestContext.equals 
				(iDisasterApplication.getInstance().SERVICE_INSTALLED)) {			// Details request for a service ins the list of installed services
			
			if (serviceAvailable															// If the service is really installed on the device
					.equals(iDisasterApplication.getInstance().SERVICE_INSTALLED)) {
			
				// Set status (on UI)
				serviceStatusView.setText(SERVICE_STATUS_INSTALLED);
// TODO: check if the service is already shared...				
//				serviceStatusView.setText(SERVICE_STATUS_SHARED_BY_ME);
				
				serviceAction = iDisasterApplication.getInstance().SERVICE_SHARE;	// Action available to user is Share
// TODO: check if the service is already shared...				
//				serviceAction = iDisasterApplication.getInstance().SERVICE_UNSHARE;	// Action available to user is Unshare

			} else {																		// If the service is NOT installed on the device

				// Set status (on UI)
				serviceStatusView.setText(SERVICE_STATUS_REMOVED);				
				
				serviceAction = iDisasterApplication.getInstance().SERVICE_INSTALL;	// Action available to user is Install
				
			}
			
		} else if (requestContext.equals (iDisasterApplication.getInstance().SERVICE_SHARED)) {
// TODO: Check it the client is installed. If not ACtion is "Install client" - otherwise "Access"
			// Set status (on UI)
			serviceStatusView.setText(SERVICE_STATUS_SHARED_IN_CIS);
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
								
		String[] serviceProjection = new String[] {
							SocialContract.Services._ID,
							SocialContract.Services.GLOBAL_ID,
							SocialContract.Services.NAME,
							SocialContract.Services.DESCRIPTION,
							SocialContract.Services.AVAILABLE							
// TODO: remove - Used temporarily - waiting for extension of Social Provider							
							,
							SocialContract.Services.TYPE,
							SocialContract.Services.APP_TYPE,
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
		serviceAvailable = serviceCursor.getString(serviceCursor
				.getColumnIndex(SocialContract.Services.AVAILABLE));

// TODO: If there are several matches for the service, the duplications should be removed
		
//TODO: Remove - Used temporarily - waiting for extension of Social Provider
//		serviceType = serviceCursor.getString(serviceCursor
//					.getColumnIndex(SocialContract.Services.TYPE));
//		serviceAppType=serviceCursor.getString(serviceCursor
//					.getColumnIndex(SocialContract.Services.APP_TYPE));
//		serviceDependency = serviceCursor.getString(serviceCursor
//				.getColumnIndex(SocialContract.Services.DEPENDENCY));
//		serviceConfig = serviceCursor.getString(serviceCursor
//				.getColumnIndex(SocialContract.Services.CONFIG));
//		serviceURL = serviceCursor.getString(serviceCursor
//				.getColumnIndex(SocialContract.Services.APP_TYPE));
		
		return iDisasterApplication.getInstance().QUERY_SUCCESS;
	}


/**
 * checkServiceInstallStatus
 * - checks the consistency between the availability status of the service in SocialProvider
 *   and the availability on device.
 * - triggers status update in SocialProvider, if not consistent.
 */

	private String checkServiceInstallStatus () {

		// Assume that information about the service has already been 
		// retrieved by getServiceInformation
		
		if (serviceCursor == null) {
			return iDisasterApplication.getInstance().QUERY_EXCEPTION;
		}
		
		if (serviceCursor.getCount() == 0) {
			return iDisasterApplication.getInstance().QUERY_EMPTY;
		}
		
		serviceCursor.moveToFirst();		// Should not be several matches; anyway only select the first
		
		if (serviceInstalled(serviceCursor.getString(serviceCursor	// If the service is installed on the device
				.getColumnIndex(SocialContract.Services.GLOBAL_ID)))) {
			
			if (serviceAvailable.equals (iDisasterApplication.getInstance().SERVICE_NOT_INSTALLED)){
				return (updateServiceInstallStatus (iDisasterApplication.getInstance().SERVICE_INSTALLED));
			}
		
		} else {													// If the service is NOT installed on the device
		
			if (serviceAvailable.equals (iDisasterApplication.getInstance().SERVICE_INSTALLED)){
				return (updateServiceInstallStatus (iDisasterApplication.getInstance().SERVICE_NOT_INSTALLED));
			}
		}		

		return iDisasterApplication.getInstance().UPDATE_SUCCESS;
	}

		
/**
 * updateServiceInstallStatus updates availability status in SocialProvider.
 */

	private String updateServiceInstallStatus (String status) {
		
		// Assume that information about the service has already been 
		// retrieved by getServiceInformation
		
		if (serviceCursor == null) {
			return iDisasterApplication.getInstance().QUERY_EXCEPTION;
		}
		
		if (serviceCursor.getCount() == 0) {
			return iDisasterApplication.getInstance().QUERY_EMPTY;
		}
		
		serviceCursor.moveToFirst();		// Should not be several matches; anyway only select the first	
		
		Uri recordUri = serviceUri.withAppendedPath(serviceUri, "/" +
				serviceCursor.getString(serviceCursor.getColumnIndex(SocialContract.Services._ID)));
		ContentValues values = new ContentValues();
        values.put(SocialContract.Services.AVAILABLE, status);

        try {
        	getContentResolver().update(recordUri, values, null, null);		
		} catch (Exception e) {
			iDisasterApplication.getInstance().debug (2, "Update to "+ 
										SocialContract.Services.CONTENT_URI + "causes an exception");
			return iDisasterApplication.getInstance().UPDATE_EXCEPTION;
		}

        serviceAvailable = status;
        return iDisasterApplication.getInstance().UPDATE_SUCCESS;
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
