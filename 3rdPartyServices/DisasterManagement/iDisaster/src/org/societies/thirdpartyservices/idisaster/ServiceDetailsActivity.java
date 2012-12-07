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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.societies.android.api.cis.SocialContract;
import org.societies.thirdpartyservices.idisaster.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
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
import android.os.Environment;

import android.widget.ProgressBar;
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
	private Button button1;
	private Button button2;

	private ContentResolver resolver;

	Uri serviceUri = SocialContract.Services.CONTENT_URI;
	Cursor serviceCursor;

	private String serviceId;	
	private String serviceGlobalId;
	private String serviceName;
	private String serviceDescription;
	private String serviceAvailable;
	private String serviceURL;
	private String serviceAppType;
	private String serviceConfig;
	
// Used temporarily - waiting for extension of SocialProvider
//	private String serviceType;
//	private String serviceDependency;
	
	private String requestContext;

// Status information for the user. Not stored in SocialProvider 
	private String serviceStatus;
	private String SERVICE_STATUS_RECOMMENDED = "This service is recommended in the team.";
	private String SERVICE_STATUS_RECOMMENDED_INSTALLED = "This recommended service is already installed on your device. You can share it with the team members.";	
	private String SERVICE_STATUS_INSTALLED = "This service is installed on your device. You can share it with the team members.";
	private String SERVICE_STATUS_REMOVED = "This service was not properly or is no longer installed on your device.";
	private String SERVICE_STATUS_SHARED_BY_ME = "You have shared this service with the team members.";
	private String SERVICE_STATUS_SHARED_IN_CIS = "This service is shared by other team members.";

	private String SERVICE_STATUS_UNKNOWN = "No more information available.";
	
	

	
	
	//TODO: will be fetched from SocialProvider when available...
	private String servicePackage ="org.ubicompforall.cityexplorer";  // can be found
	private String serviceIntent =".gui.CalendarActivity";	// can be found
//	private String serviceIntent ="org.ubicompforall.cityexplorer.gui.PlanPoiTab";  // can be found
	
	private String serviceAction1; 
	private String serviceAction2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.service_details_layout);
    	resolver = getContentResolver();



		Intent intent= getIntent(); 							// Get the intent that created activity		
		requestContext = intent.getStringExtra("REQUEST_CONTEXT");	// Retrieve first parameter (context for request of details)
		serviceGlobalId = intent.getStringExtra("SERVICE_GLOBAL_ID"); 		// Retrieve second parameter: service ID
		
		serviceAction2 = iDisasterApplication.getInstance().SERVICE_NO_ACTION;	// In most cases, only one action is available for a service
		
		if (iDisasterApplication.testDataUsed) {
			int position = Integer.parseInt(serviceGlobalId);
			serviceName = iDisasterApplication.getInstance().CISserviceNameList.get (position);
			serviceDescription = iDisasterApplication.getInstance().CISserviceDescriptionList.get (position);
			serviceAvailable = iDisasterApplication.getInstance().SERVICE_NOT_INSTALLED;
			serviceAction1 = iDisasterApplication.getInstance().SERVICE_INSTALL;
			
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
		
		if (requestContext.equals 
				(iDisasterApplication.getInstance().SERVICE_RECOMMENDED)) {			// Details request for a service recommended in the CIS
			
			
			if (serviceAvailable														// If the service is installed on the device
					.equals(iDisasterApplication.getInstance().SERVICE_INSTALLED)) {
				// Set status (on UI)
				serviceStatusView.setText(SERVICE_STATUS_RECOMMENDED);
				serviceAction1 = iDisasterApplication.getInstance().SERVICE_LAUNCH;			// Action available to user is Launch
				
			} else {																	// If the service is NOT installed on the device
				// Set status (on UI)
				serviceStatusView.setText(SERVICE_STATUS_RECOMMENDED_INSTALLED);
				serviceAction1 = iDisasterApplication.getInstance().SERVICE_INSTALL; 		// Action available to user is Install

			}
			
		} else if (requestContext.equals 
				(iDisasterApplication.getInstance().SERVICE_INSTALLED)) {			// Details request for a service in the list of installed services
			
			if (serviceAvailable															// If the service is really installed on the device
					.equals(iDisasterApplication.getInstance().SERVICE_INSTALLED)) {
			
				// Set status (on UI)
				serviceStatusView.setText(SERVICE_STATUS_INSTALLED);
				serviceAction1 = iDisasterApplication.getInstance().SERVICE_LAUNCH;				// Action available to user is Launch	

			} else {																		// If the service is NOT installed on the device

				// Set status (on UI)
				serviceStatusView.setText(SERVICE_STATUS_REMOVED);				
				serviceAction1 = iDisasterApplication.getInstance().SERVICE_INSTALL;		// Action available to user is Install

			}
			
		} else if (requestContext.equals (iDisasterApplication.getInstance().SERVICE_SHARED)) { 	// Details request for a service in the list of shared services
// TODO: Check it the client is installed. If not Action is "Install client" - otherwise "Access"
			// Set status (on UI)
			serviceStatusView.setText(SERVICE_STATUS_SHARED_IN_CIS);
			serviceAction1 ="Back";
		}
		
		button1 = (Button) findViewById(R.id.serviceDetailsButton1);
		button1.setText(serviceAction1);
		button1.setOnClickListener(this);
		
		button2 = (Button) findViewById(R.id.serviceDetailsButton2);
		if (serviceAction1.equals((iDisasterApplication.getInstance().SERVICE_LAUNCH))) {
			
// TODO: check if the service is already shared...				
//			serviceStatusView.setText(SERVICE_STATUS_SHARED_BY_ME);
// TODO: check if the service is already shared...				
//			serviceAction = iDisasterApplication.getInstance().SERVICE_UNSHARE;	// Action available to user is Unshare

			serviceAction2 = iDisasterApplication.getInstance().SERVICE_SHARE;	// Alternative action to user is Share
			button2.setText(serviceAction2);
			button2.setOnClickListener(this);
		} else {
			button2.setVisibility(android.view.View.GONE);		// Hide button2			
		}

    }


	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
    
    @Override
	public void onClick(View v) {
// TODO: Add check on button

    	if (button1.getId() == ((Button) v).getId()) {									// First button is clicked
    		
    		if (serviceAction1.equals (iDisasterApplication.getInstance().SERVICE_INSTALL)) {				// Request to install
    			if (installService ().equals (iDisasterApplication.getInstance().DOWNLOAD_EXCEPTION)) {
    				showServiceExceptionDialog 			// Install exception; No termination (the user may have to switch on network access)
    					(getString(R.string.dialogServiceDownloadException));		
    				return;	
    			} else {								// Service was correctly installed - Update data in SocialProvider
    				if (!(updateServiceInstallStatus (iDisasterApplication.getInstance().SERVICE_INSTALLED)
    						.equals (iDisasterApplication.getInstance().UPDATE_SUCCESS))) {					// Update of SocialProvider has failed
    					showQueryExceptionDialog ();	// Exception: Display dialog and terminates activity			
    				} else {
    					finish ();
    				}
    				return;
    			}

    		} else if (serviceAction1.equals (iDisasterApplication.getInstance().SERVICE_LAUNCH)) {			// Request to launch
    			if (launchApplication ()
    					.equals (iDisasterApplication.getInstance().LAUNCH_EXCEPTION)) {					// Launch has failed
    				showServiceExceptionDialog			// Launch exception: display dialog
    					(getString(R.string.dialogServiceLaunchException));
    			} 
    			finish ();		// Terminate in any launch result case; exception cannot be repaired
    			
    		} else if (serviceAction1.equals ("Back")) {		// Request to go back to previous activity (temporary functionality)
    			finish ();	
    		} else {					// This should never happened!
    			return;
    		}
    		
    	} else if (button2.getId() == ((Button) v).getId()) {
    
    		if (serviceAction2.equals (iDisasterApplication.getInstance().SERVICE_SHARE)) {					// Request to sahre a service
    			
    			Toast.makeText(getApplicationContext(), "Share: not implemented yet!", Toast.LENGTH_LONG).show();

    		} else if (serviceAction2.equals (iDisasterApplication.getInstance().SERVICE_UNSHARE)) {
    			
    			Toast.makeText(getApplicationContext(), "Unshare: not implemented yet!", Toast.LENGTH_LONG).show();

    		} else {				// should never happen
    			return;
    		}
    	}
	}



/**
 * getServiceInformation retrieves the information about the service 
 * from SocialProvider.
 */

	private String getServiceInformation () {
								
		String[] serviceProjection = new String[] {
							SocialContract.Services._ID,
							SocialContract.Services.GLOBAL_ID,
							SocialContract.Services.NAME,
							SocialContract.Services.DESCRIPTION,
							SocialContract.Services.AVAILABLE,
							SocialContract.Services.URL,
							SocialContract.Services.APP_TYPE,
							SocialContract.Services.CONFIG
// TODO: remove - Used temporarily - waiting for extension of SocialProvider
							,
							SocialContract.Services.TYPE,
							SocialContract.Services.DEPENDENCY
							};
		String serviceSelection = SocialContract.Services.GLOBAL_ID + "= ?";
		String [] serviceSelectionArgs = new String [] {serviceGlobalId} ;
		
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
		
		serviceId = serviceCursor.getString(serviceCursor
				.getColumnIndex(SocialContract.Services._ID));
		serviceName = serviceCursor.getString(serviceCursor
						.getColumnIndex(SocialContract.Services.NAME));
		serviceDescription = serviceCursor.getString(serviceCursor
						.getColumnIndex(SocialContract.Services.DESCRIPTION));
		serviceAvailable = serviceCursor.getString(serviceCursor
				.getColumnIndex(SocialContract.Services.AVAILABLE));
		serviceURL = serviceCursor.getString(serviceCursor
				.getColumnIndex(SocialContract.Services.URL));
		serviceAppType=serviceCursor.getString(serviceCursor
				.getColumnIndex(SocialContract.Services.APP_TYPE));
		serviceConfig = serviceCursor.getString(serviceCursor
				.getColumnIndex(SocialContract.Services.CONFIG));

// TODO: If there are several matches for the service, the duplications should be removed
		
//TODO: Remove - Used temporarily - waiting for extension of SocialProvider
//		serviceType = serviceCursor.getString(serviceCursor
//					.getColumnIndex(SocialContract.Services.TYPE));
//		serviceDependency = serviceCursor.getString(serviceCursor
//				.getColumnIndex(SocialContract.Services.DEPENDENCY));
		
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
 * isCallable checks if any installed App can answer the Intent given as a parameter.
 */
	
    private boolean isCallable (Intent intent1) { 
        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent1, PackageManager.MATCH_DEFAULT_ONLY);    
        if(list.size() > 0)  
        	return true ;    
        else  
        	return false;  
  
    }  

/**
 * serviceInstalled checks if any installed App has the package name given as a parameter.
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
    
/**
 * installService triggers the installation of the service either from 
 * - using Google Play (download vie Google Play)
 * - downloading from a web site
 */
    private String installService () {

// Alternative: NOT USED
//    	Open Web page from which Apps can be downloaded
//		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://folk.ntnu.no/svarvaa/utils/pro2www/"));
//		startActivity(browserIntent);

    	if (serviceURL.equals ("https://play.google.com/store/apps")) {			// Download from Google Play
    		try {
        		Intent goToMarket = new Intent(Intent.ACTION_VIEW)
					.setData(Uri.parse("market://details?id=" + serviceGlobalId));
        		startActivity(goToMarket);
    		} catch (Exception e) {
				iDisasterApplication.getInstance().debug (2, "Download APK causes an exception");
    			return iDisasterApplication.getInstance().DOWNLOAD_EXCEPTION;
	        }
			return iDisasterApplication.getInstance().DOWNLOAD_SUCCESS;	
 
    	} else {																// Download and install Android
		    try {
// TODO: remove test code
// Work on old Samsung Tab - Android 2.1 with 3G - and HTC Android 4.1 - but not on LG Android 2.1
//	            URL url = new URL("http://folk.ntnu.no/svarvaa/utils/pro2www/apk/Tshirt.apk");

// Work on old Samsung Tab - Android 2.1 with 3G - but not on HTC Android 4.1
//	            URL url = new URL ("http://www.sintef.no/project/UbiCompForAll/city_explorer/CityExplorer.apk");
		    	
	            URL url = new URL(serviceURL);				// Download file URL
				String fileName = serviceName + ".apk";		// File for storing download
				
	            HttpURLConnection c = (HttpURLConnection) url.openConnection();
	            c.setRequestMethod("GET");
	            c.setDoOutput(true);
	            c.connect();

	            String PATH = Environment.getExternalStorageDirectory() + "/download/";
	            File file = new File(PATH);
	            file.mkdirs();
	            File outputFile = new File(file, fileName);
	            FileOutputStream fos = new FileOutputStream(outputFile);

	            InputStream is = c.getInputStream();

	            byte[] buffer = new byte[1024];
	            int count = 0;
	            while ((count = is.read(buffer)) != -1) {
	                fos.write(buffer, 0, count);
	            }
	            fos.close();
	            is.close();

	            Intent intent = new Intent(Intent.ACTION_VIEW);
	            intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/download/" + fileName)), "application/vnd.android.package-archive");
	            intent.setPackage("com.android.packageinstaller");
	            startActivity(intent);

	        } catch (Exception e) {
				iDisasterApplication.getInstance().debug (2, "Download APK causes an exception");
    			return iDisasterApplication.getInstance().DOWNLOAD_EXCEPTION;
	        }
			return iDisasterApplication.getInstance().DOWNLOAD_SUCCESS;	    		
    	}
    }

/**
 * launchApplication launches an application.
 * - retrieves the launch intent for the application. 
 */
    private String launchApplication () {

// TODO: ADD parameters depending on serviceAppType
    	try {
        	Intent i = new Intent();
            PackageManager manager = getPackageManager();
            i = manager.getLaunchIntentForPackage(serviceGlobalId);
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            startActivity(i);    		
    	}  catch (Exception e) {
    		return iDisasterApplication.getInstance().LAUNCH_EXCEPTION;
    	}
    	return iDisasterApplication.getInstance().LAUNCH_SUCCESS;
    }

// Alternative code? - not tested
//    private String launchComponent (String packageName, String clasName) {
//        Intent launch_intent = new Intent("android.intent.action.MAIN");
//        launch_intent.addCategory("android.intent.category.LAUNCHER");
//        launch_intent.setComponent(new ComponentName (packageName, clasName));
//        launch_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//        this.startActivity(launch_intent);
//        return "TO be done";
//    }
//
//    public void startApplication(String application_name){
//        try{
//            Intent intent = new Intent("android.intent.action.MAIN");
//            intent.addCategory("android.intent.category.LAUNCHER");
//
//            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//            List<ResolveInfo> resolveinfo_list = this.getPackageManager().queryIntentActivities(intent, 0);
//
//            for(ResolveInfo info:resolveinfo_list){
//                if(info.activityInfo.packageName.equalsIgnoreCase(application_name)){
//                    launchComponent(info.activityInfo.packageName, info.activityInfo.name);
//                    break;
//                }
//            }
//        }
//        catch (Exception e) {
//            Toast.makeText(this.getApplicationContext(), "There was a problem loading the application: "
//            		+ application_name, Toast.LENGTH_SHORT).show();
//        }
//    }

/**
 * checkServiceShareStatus gets the share status of the service in SocialProvider
 * for the selected CIS.
 */

	private String checkServiceShareStatus () {

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
 * showQueryExceptionDialog displays a dialog to the user and 
 * terminates since the activity.
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
