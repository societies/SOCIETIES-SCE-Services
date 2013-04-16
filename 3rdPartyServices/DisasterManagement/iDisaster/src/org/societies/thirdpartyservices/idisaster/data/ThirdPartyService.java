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
package org.societies.thirdpartyservices.idisaster.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.societies.android.api.cis.SocialContract;
import org.societies.android.api.cis.SupportedAccountTypes;
import org.societies.thirdpartyservices.idisaster.iDisasterApplication;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

/**
 *  ThirdPartyService is used for storing information about one service. 
 *  It provides all operations needed for services (launch, share, ...).
 *
 * @author Jacqueline.Floch@sintef.no
 *
 */


public class ThirdPartyService {
	
	public long serviceId;
	public String serviceGlobalId;
	public String serviceName;
	public String serviceDescription;
	private String serviceURL;
	private String serviceType;
	private String serviceConfig;
	private String serviceDependency;
	
	public String serviceInstallStatus;				// Is the service installed on the device?
	public String serviceShareStatus;				// Is the service shared by the user in the selected community?

	Uri serviceUri = SocialContract.Services.CONTENT_URI;
	Cursor serviceCursor;

	Uri sharingUri = SocialContract.Sharing.CONTENT_URI;
	Cursor sharingCursor;
	
//	private ContentResolver resolver;
	
	
//TODO: Remove test code
//	private String servicePackage ="org.ubicompforall.cityexplorer";  // can be found
//	private String serviceIntent =".gui.CalendarActivity";	// can be found
//	private String serviceIntent ="org.ubicompforall.cityexplorer.gui.PlanPoiTab";  // can be found

/**
 * Constructor.
 * 
 */
	public ThirdPartyService (String globalId) {
		serviceGlobalId = globalId;
	}
	
/**	
 * getServiceInformation retrieves the information about the service 
 * from Social Provider.
 */

	public String getServiceInformation (Context cxt, ContentResolver resolver, long meId, long teamId) {
		
		String[] serviceProjection = new String[] {
							SocialContract.Services._ID,
//							SocialContract.Services.GLOBAL_ID,			// already set on class instantiation
							SocialContract.Services.NAME,
							SocialContract.Services.DESCRIPTION,
							SocialContract.Services.URL,				// URL to code to be downloaded
							SocialContract.Services.TYPE,				//	
							SocialContract.Services.CONFIG,				// intent to launch the service
							SocialContract.Services.DEPENDENCY			// dependency on another service

// Not used
//							SocialContract.Services._ID_OWNER,
//							SocialContract.Services.APP_TYPE,
//							SocialContract.Services.AVAILABLE

// TODO: remove - Used temporarily - waiting for extension of Social Provider
//							,
//							SocialContract.Services.DEPENDENCY
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

//  serviceGlobalId is set on class instantiation 
//		serviceGlobalId = serviceCursor.getString(serviceCursor
//				.getColumnIndex(SocialContract.Services.GLOBAL_ID));

		serviceId = serviceCursor.getLong(serviceCursor
				.getColumnIndex(SocialContract.Services._ID));
		
		serviceName = serviceCursor.getString(serviceCursor
						.getColumnIndex(SocialContract.Services.NAME));
		serviceDescription = serviceCursor.getString(serviceCursor
						.getColumnIndex(SocialContract.Services.DESCRIPTION));
// No longer used
//		serviceAvailable = serviceCursor.getString(serviceCursor
//				.getColumnIndex(SocialContract.Services.AVAILABLE));
		serviceURL = serviceCursor.getString(serviceCursor
				.getColumnIndex(SocialContract.Services.URL));
		serviceType=serviceCursor.getString(serviceCursor
				.getColumnIndex(SocialContract.Services.TYPE));
		serviceConfig = serviceCursor.getString(serviceCursor
				.getColumnIndex(SocialContract.Services.CONFIG));
		serviceDependency = serviceCursor.getString(serviceCursor
				.getColumnIndex(SocialContract.Services.DEPENDENCY));

//TODO: If there are several matches for the service, the duplications should be removed
		
		
		// check availability on device
		serviceInstallStatus = serviceInstalled (cxt, serviceGlobalId);
		
		// check consistency with information in sharing table
		String installFlag = checkServiceInstallStatus (cxt, resolver);
		if (!(installFlag.equals(iDisasterApplication.getInstance().QUERY_SUCCESS))) {
			return installFlag;
		}

		// default value
		serviceShareStatus = iDisasterApplication.getInstance().SERVICE_NOT_SHARED;

		// check whether the service is shared or not by the user in the selected community
		String shareFlag = checkServiceShareStatus (cxt, resolver, meId, teamId);
		if (!(shareFlag.equals(iDisasterApplication.getInstance().QUERY_SUCCESS))) {
			return shareFlag;
		}
		
		return iDisasterApplication.getInstance().QUERY_SUCCESS;
	}

		
/**
 * checkServiceInstallStatus updates availability status in SocialProvider.
 * 
 * return QUERY_SUCCESS if success;
 */

	private String checkServiceInstallStatus (Context cxt, ContentResolver resolver) {
		
		// Assume that information about the service has already been 
		// retrieved by getServiceInformation
		
//		testSharing (cxt, resolver);

		// Do not add to service to Social Provider if service client
		if (serviceType.equals(SocialContract.ServiceConstants.SERVICE_TYPE_CLIENT)) {
			return iDisasterApplication.getInstance().QUERY_SUCCESS;	
		}
		
		
		Uri sharingUri = SocialContract.Sharing.CONTENT_URI;

		String[] sharingProjection = new String[] {
				SocialContract.Sharing._ID,
				SocialContract.Sharing._ID_SERVICE,
//				SocialContract.Sharing._ID_COMMUNITY,
				SocialContract.Sharing.TYPE};

		String sharingSelection = SocialContract.Sharing._ID_COMMUNITY + "= ?" +
								"AND " + SocialContract.Sharing._ID_SERVICE + "= ?";

		String[] sharingSelectionArgs = new String[] 
				{"0", 									// "0" if own services
				String.valueOf (serviceId)};			// Retrieve services of that type

		Cursor sharingCursor;
		try {
			sharingCursor= resolver.query(sharingUri, sharingProjection,
					sharingSelection, sharingSelectionArgs, null /* sortOrder*/);
		} catch (Exception e) {
			iDisasterApplication.getInstance().debug (2, "Query to "+ sharingUri + "causes an exception");
			return iDisasterApplication.getInstance().QUERY_EXCEPTION;
		}				
		
		if (sharingCursor == null) {
			return iDisasterApplication.getInstance().QUERY_EXCEPTION;
		}
		
		if (sharingCursor.getCount() == 0) {			// No service found

			if (serviceInstallStatus.equals(SocialContract.ServiceConstants.SERVICE_INSTALLED)) {	// Service is installed and table should be updated

				ContentValues sharingValues = new ContentValues ();

				sharingValues.put(SocialContract.Sharing._ID_SERVICE, serviceId);	// Id of the installed service
				sharingValues.put(SocialContract.Sharing._ID_OWNER, 0);				// 0 if own services
				sharingValues.put(SocialContract.Sharing._ID_COMMUNITY,	0); 		// 0 if own services
				sharingValues.put(SocialContract.Sharing.TYPE, SocialContract.ServiceConstants.SERVICE_INSTALLED);
				sharingValues.put(SocialContract.Sharing.DESCRIPTION, "");

// No sync needed
				
				try {
// The Uri value returned is not used.
//					Uri activityNewUri = getContentResolver().insert( SocialContract.CommunityActivity.CONTENT_URI,
//							sharingValues);
					resolver.insert( SocialContract.Sharing.CONTENT_URI, sharingValues);
				} catch (Exception e) {
					iDisasterApplication.getInstance().debug (2, "Insert to "+ 
										SocialContract.Sharing.CONTENT_URI + "causes an exception");
			    	return iDisasterApplication.getInstance().INSERT_EXCEPTION;
				}				
			} // else - SERVICE_NOT_INSTALLED: no service found is OK
			
			return iDisasterApplication.getInstance().QUERY_SUCCESS;	
			
		}	// getCount == 0

		
		// A (at least one) service is found in the sharing table
		boolean first = true;
		
		while (sharingCursor.moveToNext()) {
			
			Uri recordUri = sharingUri.withAppendedPath(sharingUri, "/" +
					sharingCursor.getString(sharingCursor.getColumnIndex(SocialContract.Sharing._ID)));
			
			if (first) {																			// check that TYPE is properly set
				first = false;
				if (serviceInstallStatus.equals(SocialContract.ServiceConstants.SERVICE_INSTALLED)) {

					if (!(sharingCursor.getString(sharingCursor
							.getColumnIndex(SocialContract.Sharing.TYPE)).equals(serviceInstallStatus))) {		// update info in Sharing table set TYPE to SERVICE_INSTALLED
						ContentValues values = new ContentValues();
				        values.put(SocialContract.Sharing.TYPE, serviceInstallStatus);		
				        try {
				        	resolver.update(recordUri, values, null, null);		
						} catch (Exception e) {
							iDisasterApplication.getInstance().debug (2, "Update to "+ 
														SocialContract.Sharing.CONTENT_URI + "causes an exception");
							return iDisasterApplication.getInstance().UPDATE_EXCEPTION;
						}
					} // else The record is properly set
					
				} else {	// Delete the record: SERVICE_NON_INSTALLED
			        try {
			        	resolver.delete (recordUri, null, null);		
					} catch (Exception e) {
						iDisasterApplication.getInstance().debug (2, "Update to "+ 
													SocialContract.Sharing.CONTENT_URI + "causes an exception");
						return iDisasterApplication.getInstance().UPDATE_EXCEPTION;
					}
				} 
					
			} else {		// Delete all other records: there should be only one record for that service with _ID_COMMUNITY 0
		        try {
		        	resolver.delete (recordUri, null, null);		
				} catch (Exception e) {
					iDisasterApplication.getInstance().debug (2, "Update to "+ 
												SocialContract.Services.CONTENT_URI + "causes an exception");
					return iDisasterApplication.getInstance().UPDATE_EXCEPTION;
				}	
			}
		}
	
		return iDisasterApplication.getInstance().QUERY_SUCCESS;
				
	}

/**
 * setServiceInstallStatus add an entry in the sharing table (Assume the service has been installed; no check).
 * 
 * return QUERY_SUCCESS if success;
 */

	public String setServiceInstallStatus (Context cxt, ContentResolver resolver) {


		// Do not add to service to Social Provider if service client
		if (serviceType.equals(SocialContract.ServiceConstants.SERVICE_TYPE_CLIENT)) {
			return iDisasterApplication.getInstance().QUERY_SUCCESS;	
		}

		
		serviceInstallStatus = SocialContract.ServiceConstants.SERVICE_INSTALLED;

// The following code does not work when  service is being installed. Pb with concurrency of activities?
		// check availability on device
//		serviceInstallStatus = serviceInstalled (cxt, serviceGlobalId);
		// check consistency with information in sharing table
//		String installStatus = checkServiceInstallStatus (cxt, resolver);	
//		if (!(installStatus.equals(iDisasterApplication.getInstance().QUERY_SUCCESS))) {
//			return installStatus;
//		}
		

		if (serviceInstallStatus.equals(SocialContract.ServiceConstants.SERVICE_INSTALLED)) {	// Service is installed and table should be updated

			ContentValues sharingValues = new ContentValues ();

			sharingValues.put(SocialContract.Sharing._ID_SERVICE, serviceId);	// Id of the installed service
			sharingValues.put(SocialContract.Sharing._ID_OWNER, 0);				// 0 if own services
			sharingValues.put(SocialContract.Sharing._ID_COMMUNITY,	0); 		// 0 if own services
			sharingValues.put(SocialContract.Sharing.TYPE, SocialContract.ServiceConstants.SERVICE_INSTALLED);
			sharingValues.put(SocialContract.Sharing.DESCRIPTION, "");

// No sync needed
			
			try {
// The Uri value returned is not used.
//						Uri activityNewUri = getContentResolver().insert( SocialContract.CommunityActivity.CONTENT_URI,
//								sharingValues);
				resolver.insert( SocialContract.Sharing.CONTENT_URI, sharingValues);
			} catch (Exception e) {
				iDisasterApplication.getInstance().debug (2, "Insert to "+ 
									SocialContract.Sharing.CONTENT_URI + "causes an exception");
		    	return iDisasterApplication.getInstance().INSERT_EXCEPTION;
			}
		}	
		
//		testSharing (cxt, resolver);

		return iDisasterApplication.getInstance().QUERY_SUCCESS;		
	}
	
	
/**
 * isCallable checks if any installed App can answer the Intent given as a parameter.
 */
	
	public boolean isCallable (Context cxt, Intent intent1) { 
        List<ResolveInfo> list = cxt.getPackageManager().queryIntentActivities(intent1, PackageManager.MATCH_DEFAULT_ONLY);    
        if(list.size() > 0)  
        	return true ;    
        else  
        	return false;  
  
    }  

/**
 * serviceInstalled checks if any installed App has the package name given as a parameter.
 */

	public String serviceInstalled (Context cxt, String appPackageName) {
    	
    	if (appPackageName == null) {
    		return (SocialContract.ServiceConstants.SERVICE_NOT_INSTALLED);
    	}
    	// Get package names of all installed applications
    	PackageManager pm = cxt.getPackageManager();
    	List <PackageInfo> list = pm.getInstalledPackages(0);
    
    	for (PackageInfo pi : list) {
    	   ApplicationInfo ai;
    	   try {
    		   ai = pm.getApplicationInfo(pi.packageName, 0);
    		   if (!((ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0)) {		// Only consider 3rd party Apps
    			   															// Ignore Apps installed in the device's system image. 
    			   if (appPackageName.equals(pi.packageName)) {
    				   return (SocialContract.ServiceConstants.SERVICE_INSTALLED);
    			   }    	        	 
    		   }  	      
    	   } catch (NameNotFoundException e) {
    		   Log.d(getClass().getSimpleName(), "Name not found", e);
    	   }
    	}
    	return (SocialContract.ServiceConstants.SERVICE_NOT_INSTALLED);
	}
    
/**
 * installService triggers the installation of the service either from 
 * - using Google Play (download vie Google Play)
 * - downloading from a web site
 */
	public String installService (Context cxt) {

// Alternative: NOT USED
//	    	Open Web page from which Apps can be downloaded
//			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://folk.ntnu.no/svarvaa/utils/pro2www/"));
//			startActivity(browserIntent);
		
    	if (serviceURL.equals ("https://play.google.com/store/apps")) {			// Download from Google Play
    		try {
        		Intent goToMarket = new Intent(Intent.ACTION_VIEW)
					.setData(Uri.parse("market://details?id=" + serviceGlobalId));
        		cxt.startActivity(goToMarket);
    		} catch (Exception e) {
				iDisasterApplication.getInstance().debug (2, "Download APK causes an exception");
    			return iDisasterApplication.getInstance().DOWNLOAD_EXCEPTION;
	        }
			return iDisasterApplication.getInstance().DOWNLOAD_SUCCESS;	
 
    	} else {																// Download and install Android
		    try {

	            URL url = new URL(serviceURL);				// Download file URL
//		    	URL url = new URL ("http://folk.ntnu.no/svarvaa/utils/pro2www/apk/Tshirt.apk");
		    	
	            
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
	            cxt.startActivity(intent);

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
	public String launchApplication (Context cxt, long teamId, long peopleId) {

    	try {
        	Intent i = new Intent();
            PackageManager manager = cxt.getPackageManager();
            i = manager.getLaunchIntentForPackage(serviceGlobalId);
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            
            // Alternative use CONFIG - not tested
//            Intent i = new Intent(serviceConfig);
//            see http://stackoverflow.com/questions/8583658/start-activity-with-action-but-no-category
            
            i.putExtra("CIS_ID", teamId);			// Selected community
            i.putExtra("CSS_ID", peopleId);			// When launching a client: member for the shared service            
            cxt.startActivity(i);    		
    	}  catch (Exception e) {
    		return iDisasterApplication.getInstance().LAUNCH_EXCEPTION;
    	}
    	return iDisasterApplication.getInstance().LAUNCH_SUCCESS;
    }

// Alternative code? - not tested
//	    private String launchComponent (String packageName, String clasName) {
//	        Intent launch_intent = new Intent("android.intent.action.MAIN");
//	        launch_intent.addCategory("android.intent.category.LAUNCHER");
//	        launch_intent.setComponent(new ComponentName (packageName, clasName));
//	        launch_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//	        this.startActivity(launch_intent);
//	        return "TO be done";
//	    }
//
//	    public void startApplication(String application_name){
//	        try{
//	            Intent intent = new Intent("android.intent.action.MAIN");
//	            intent.addCategory("android.intent.category.LAUNCHER");
//
//	            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//	            List<ResolveInfo> resolveinfo_list = this.getPackageManager().queryIntentActivities(intent, 0);
//
//	            for(ResolveInfo info:resolveinfo_list){
//	                if(info.activityInfo.packageName.equalsIgnoreCase(application_name)){
//	                    launchComponent(info.activityInfo.packageName, info.activityInfo.name);
//	                    break;
//	                }
//	            }
//	        }
//	        catch (Exception e) {
//	            Toast.makeText(this.getApplicationContext(), "There was a problem loading the application: "
//	            		+ application_name, Toast.LENGTH_SHORT).show();
//	        }
//	    }

/**
 * checkServiceShareStatus checks if the user has shared the service in in the selected CIS.
 * 
 * return QUERY_SUCCESS if success;
 * 
 */

	private String checkServiceShareStatus (Context cxt, ContentResolver resolver, long meId, long teamId) {
		
		if (sharingCursor != null) {
			sharingCursor.close();		// "close" releases data but does not set to null
			sharingCursor = null;
		}
	
//		testSharing (cxt, resolver);
		
		String[] sharingProjection = new String[] {
				SocialContract.Sharing._ID							// Retrieve the _ID of the shared service entry (if any)				
				};
	
		String sharingSelection = 
				SocialContract.Sharing._ID_SERVICE + "= ?" +					// Service selected for the Details activity	
				"AND " + SocialContract.Sharing._ID_COMMUNITY + "= ?" +			// Selected community
				"AND " + SocialContract.Sharing._ID_OWNER + "= ?" +
				"AND " + SocialContract.Sharing.TYPE + "= ?" +
				"AND " + SocialContract.Sharing.DELETED + "= ?";
	
		String[] sharingSelectionArgs = new String[]
				{String.valueOf (serviceId),														// For selected service
				String.valueOf (teamId),	 														// For the selected CIS
				String.valueOf (meId), 															 	// For me
				SocialContract.ServiceConstants.SERVICE_SHARED,
				"0"}; 													// Only care about fields which are not deleted
				
		try {
			sharingCursor= resolver.query(sharingUri, sharingProjection,
					sharingSelection, sharingSelectionArgs, null /* sortOrder*/);
		} catch (Exception e) {
			iDisasterApplication.getInstance().debug (2, "Query to "+ sharingUri + "causes an exception");
			return iDisasterApplication.getInstance().QUERY_EXCEPTION;
		}
	
		if (sharingCursor == null) {			// No cursor was set - should not happen
			iDisasterApplication.getInstance().debug (2, "sharingCursor was not set to any value");
			return iDisasterApplication.getInstance().QUERY_EMPTY;
		}
	
		if (sharingCursor.getCount() == 0) {	// No service is shared in the team community
			// already set before calling
//			serviceShareStatus= iDisasterApplication.getInstance().SERVICE_NOT_SHARED;
			
			return iDisasterApplication.getInstance().QUERY_SUCCESS;
		}			
	

		// Only one entry should be found

		if (sharingCursor.moveToFirst()) {
			serviceShareStatus= SocialContract.ServiceConstants.SERVICE_SHARED;			
		} else {
			return iDisasterApplication.getInstance().QUERY_EMPTY;			
		}
				
		return iDisasterApplication.getInstance().QUERY_SUCCESS;

	}

/**
 * shareService add a new entry in SocialProvider Sharing table
 */
	public String shareService (Context cxt, ContentResolver resolver, long meId, String meUserName, long teamId) {
	
//		testSharing (cxt, resolver);

		
		// Set the values related to the activity to store in SocialProvider
		ContentValues sharingValues = new ContentValues ();
		
		sharingValues.put(SocialContract.Sharing._ID_SERVICE, serviceId);	// Id of the service to be shared
		sharingValues.put(SocialContract.Sharing._ID_OWNER, meId);			// Member recommending the service
		sharingValues.put(SocialContract.Sharing._ID_COMMUNITY, teamId);	// Recommend service in the selected team
		sharingValues.put(SocialContract.Sharing.TYPE, SocialContract.ServiceConstants.SERVICE_SHARED);
		sharingValues.put(SocialContract.Sharing.DESCRIPTION, "");

//Fields for synchronization with box.com
		sharingValues.put(SocialContract.Sharing.ACCOUNT_NAME, meUserName);
		sharingValues.put(SocialContract.Sharing.ACCOUNT_TYPE, SupportedAccountTypes.COM_BOX);
		sharingValues.put(SocialContract.Sharing.DIRTY, 1);

		
		try {
// The Uri value returned is not used.
//							Uri activityNewUri = getContentResolver().insert( SocialContract.CommunityActivity.CONTENT_URI,
//									sharingValues);
			resolver.insert( SocialContract.Sharing.CONTENT_URI, sharingValues);	
			
		} catch (Exception e) {
			iDisasterApplication.getInstance().debug (2, "Insert to "+ 
								SocialContract.Sharing.CONTENT_URI + "causes an exception");
	    	return iDisasterApplication.getInstance().INSERT_EXCEPTION;
		}
		
//		serviceShareStatus = SocialContract.ServiceConstants.SERVICE_SHARED;		// should not be necessary...
		
//		testSharing (cxt, resolver);
		
		return iDisasterApplication.getInstance().INSERT_SUCCESS;
	}
    
/**
 * unshareService removes entry from SocialProvider Sharing table
 */
	public String unshareService (Context cxt, ContentResolver resolver, long meId, long teamId) {
		
		if (sharingCursor != null) {
			sharingCursor.close();		// "close" releases data but does not set to null
			sharingCursor = null;
		}
	
//		testSharing (cxt, resolver);

		
		String[] sharingProjection = new String[] {
				SocialContract.Sharing._ID							// Retrieve the _ID of the shared service entry (if any)				
				};
	
		String sharingSelection = 
				SocialContract.Sharing._ID_SERVICE + "= ?" +					// Service selected for the Details activity	
				"AND " + SocialContract.Sharing._ID_COMMUNITY + "= ?" +			// Selected community
				"AND " + SocialContract.Sharing._ID_OWNER + "= ?" +
				"AND " + SocialContract.Sharing.TYPE + "= ?" +
				"AND " + SocialContract.Sharing.DELETED + "= ?";
	
		String[] sharingSelectionArgs = new String[]
				{String.valueOf (serviceId),										// For selected service
				String.valueOf (teamId),	 										// For the selected CIS
				String.valueOf (meId), 												// For me
				SocialContract.ServiceConstants.SERVICE_SHARED,
				"0"}; 																// Only care about fields which are not deleted
				
		try {
			sharingCursor= resolver.query(sharingUri, sharingProjection,
					sharingSelection, sharingSelectionArgs, null /* sortOrder*/);
		} catch (Exception e) {
			iDisasterApplication.getInstance().debug (2, "Query to "+ sharingUri + "causes an exception");
			return iDisasterApplication.getInstance().QUERY_EXCEPTION;
		}
			
		if (sharingCursor == null) {			
			iDisasterApplication.getInstance().debug (2, "sharingCursor was not set to any value");
			return iDisasterApplication.getInstance().QUERY_EMPTY;
		}
	
		if (sharingCursor.getCount() == 0) {	// No service is shared in the team community
			return iDisasterApplication.getInstance().QUERY_EMPTY;
		}					
		
		if (sharingCursor.moveToFirst()) {
			
			String s = sharingCursor.getString(sharingCursor.getColumnIndex(SocialContract.Sharing._ID));
			Uri recordUri = sharingUri.withAppendedPath(sharingUri, "/" + 
					sharingCursor.getString(sharingCursor.getColumnIndex(SocialContract.Sharing._ID)));
			
			ContentValues values = new ContentValues();
	        values.put(SocialContract.Sharing.DELETED, 1);
			values.put(SocialContract.Sharing.DIRTY, 1);
			
			try {
				resolver.update(recordUri, values, null, null);
				
			} catch (Exception e) {
				iDisasterApplication.getInstance().debug (2, "Delete of "+ recordUri + "causes an exception");
		    	return iDisasterApplication.getInstance().UPDATE_EXCEPTION;
			}			
		}
		
//		testSharing (cxt, resolver);

		return iDisasterApplication.getInstance().UPDATE_SUCCESS;
	}
	

/**
 * testSharing gets all services in the sharing table
 */
	public void testSharing (Context cxt, ContentResolver resolver) {
		
		Cursor testCursor; 
	
		String[] sharingProjection = new String[] {			// Retrieve all entries in table
				SocialContract.Sharing._ID,
				SocialContract.Sharing.GLOBAL_ID,
				SocialContract.Sharing._ID_COMMUNITY,
				SocialContract.Sharing._ID_OWNER,
				SocialContract.Sharing._ID_SERVICE,
				SocialContract.Sharing.DESCRIPTION,
				SocialContract.Sharing.TYPE,
				SocialContract.Sharing.ACCOUNT_NAME,
				SocialContract.Sharing.ACCOUNT_TYPE,
				SocialContract.Sharing.CREATION_DATE,				
				SocialContract.Sharing.DELETED,
				SocialContract.Sharing.DIRTY,
				SocialContract.Sharing.LAST_MODIFIED_DATE
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
			String s06Desc = testCursor.getString(testCursor
					.getColumnIndex(SocialContract.Sharing.DESCRIPTION));
			String s07TYPE = testCursor.getString(testCursor
					.getColumnIndex(SocialContract.Sharing.TYPE));
			String s08AccountName = testCursor.getString(testCursor
					.getColumnIndex(SocialContract.Sharing.ACCOUNT_NAME));
			String s09AccountType = testCursor.getString(testCursor
					.getColumnIndex(SocialContract.Sharing.ACCOUNT_TYPE));
			String s10CreationDate = testCursor.getString(testCursor
					.getColumnIndex(SocialContract.Sharing.CREATION_DATE));
			String s11Deleted = testCursor.getString(testCursor
					.getColumnIndex(SocialContract.Sharing.DELETED));
			String s12Dirty = testCursor.getString(testCursor
					.getColumnIndex(SocialContract.Sharing.DIRTY));
			String s13Last = testCursor.getString(testCursor
					.getColumnIndex(SocialContract.Sharing.LAST_MODIFIED_DATE));
			
			i ++;
			
		}
		
		return;
	}
	
	
}
