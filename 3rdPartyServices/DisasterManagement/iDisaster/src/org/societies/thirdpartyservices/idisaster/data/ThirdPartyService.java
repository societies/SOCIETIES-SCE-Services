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
	
	public String serviceId;
	public String serviceGlobalId;
	public String serviceName;
	public String serviceDescription;
	private String serviceURL;
	private String serviceType;
	private String serviceConfig;
	private String serviceDependency;
	
	public String serviceInstallStatus = iDisasterApplication.getInstance().SERVICE_NOT_INSTALLED;

	Uri serviceUri = SocialContract.Services.CONTENT_URI;
	Cursor serviceCursor;

	Uri sharingUri = SocialContract.Sharing.CONTENT_URI;
	Cursor sharingCursor;
	
	private ContentResolver resolver;
	
	
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

	public String getServiceInformation (Context cxt, ContentResolver resolver) {
		
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

		serviceId = serviceCursor.getString(serviceCursor
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
		
//TODO: Remove - Used temporarily - waiting for extension of Social Provider
//			serviceType = serviceCursor.getString(serviceCursor
//						.getColumnIndex(SocialContract.Services.TYPE));
//			serviceDependency = serviceCursor.getString(serviceCursor
//					.getColumnIndex(SocialContract.Services.DEPENDENCY));
		
		return iDisasterApplication.getInstance().QUERY_SUCCESS;
	}


/**
 * ServiceInstallStatus
 * - checks the consistency between the availability status of the service in SocialProvider
 *   and the availability on device.
 * - triggers status update in SocialProvider, if not consistent.
 * 
 * return UPDATE_SUCCESS if checking success;
 *
 */

	public String checkServiceInstallStatus (Context cxt, ContentResolver resolver) {

		// Assume that information about the service has already been 
		// retrieved by getServiceInformation
		
		if (serviceCursor == null) {
			return iDisasterApplication.getInstance().QUERY_EXCEPTION;
		}
		
		if (serviceCursor.getCount() == 0) {
			return iDisasterApplication.getInstance().QUERY_EMPTY;
		}
		
		serviceCursor.moveToFirst();		// Should not be several matches; anyway only select the first
		
		if (serviceInstalled (cxt, serviceGlobalId)) {	//Service installed on the device
			
			// check info stored in SocialProvider
			return updateServiceInstallStatus (cxt, resolver, iDisasterApplication.getInstance().SERVICE_INSTALLED);
					
		} else {													// If the service is NOT installed on the device
			
			// check info stored in SocialProvider
			return updateServiceInstallStatus (cxt, resolver, iDisasterApplication.getInstance().SERVICE_NOT_INSTALLED);			
		}		
	}

		
/**
 * updateServiceInstallStatus updates availability status in SocialProvider.
 * 
 * sets the attribute serviceAvailable
 * 
 * return UPDATE_SUCCESS if update success;
 *
 * 
 */

	public String updateServiceInstallStatus (Context cxt, ContentResolver resolver, String status) {
		
		// Assume that information about the service has already been 
		// retrieved by getServiceInformation
		
		// Reset variable first - Then set it if the service is installed and the table properly updated
		serviceInstallStatus = iDisasterApplication.getInstance().SERVICE_NOT_INSTALLED;
				
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
				serviceId};								// Retrieve services of that type

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

			if (status.equals(iDisasterApplication.getInstance().SERVICE_INSTALLED)) {	// Service is installed and table should be updated

				ContentValues sharingValues = new ContentValues ();

				sharingValues.put(SocialContract.Sharing._ID_SERVICE, serviceId);	// Id of the installed service
				sharingValues.put(SocialContract.Sharing._ID_OWNER, "0");			// "0" if own services
				sharingValues.put(SocialContract.Sharing._ID_COMMUNITY,	"0"); 		// "0" if own services
				sharingValues.put(SocialContract.Sharing.TYPE, iDisasterApplication.getInstance().SERVICE_INSTALLED);
				sharingValues.put(SocialContract.Sharing.DESCRIPTION, "");

// Field needed temporarily
				sharingValues.put(SocialContract.Sharing.GLOBAL_ID,	serviceGlobalId);	// Global id of the service to be recommended
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
				
				serviceInstallStatus = iDisasterApplication.getInstance().SERVICE_INSTALLED;
			}
			
			return iDisasterApplication.getInstance().UPDATE_SUCCESS;	
			
		}	// getCount == 0

		
		// A (at least one) service is found in the sharing table
		sharingCursor.moveToFirst();

		boolean first = true;
		while (sharingCursor.moveToNext()) {
			
			
//			String s_id = sharingCursor.getString(sharingCursor.getColumnIndex(SocialContract.Sharing._ID));
//			String s_id_service = sharingCursor.getString(sharingCursor.getColumnIndex(SocialContract.Sharing._ID_SERVICE));
//			String s_type = sharingCursor.getString(sharingCursor.getColumnIndex(SocialContract.Sharing.TYPE));
//			String s_com = sharingCursor.getString(sharingCursor.getColumnIndex(SocialContract.Sharing._ID_COMMUNITY));

						

			Uri recordUri = sharingUri.withAppendedPath(serviceUri, "/" +
					serviceCursor.getString(serviceCursor.getColumnIndex(SocialContract.Services._ID)));
			
			if (first) {																			// check that TYPE is properly set
				first = false;
				if (status.equals(iDisasterApplication.getInstance().SERVICE_INSTALLED)) {

					if (!(sharingCursor.getString(sharingCursor
							.getColumnIndex(SocialContract.Sharing.TYPE)).equals(status))) {		// update info in Sharing table set TYPE to SERVICE_INSTALLED
						ContentValues values = new ContentValues();
				        values.put(SocialContract.Services.TYPE, status);		
				        try {
				        	resolver.update(recordUri, values, null, null);		
						} catch (Exception e) {
							iDisasterApplication.getInstance().debug (2, "Update to "+ 
														SocialContract.Services.CONTENT_URI + "causes an exception");
							return iDisasterApplication.getInstance().UPDATE_EXCEPTION;
						}
					}
					serviceInstallStatus = iDisasterApplication.getInstance().SERVICE_INSTALLED;

					// otherwise the TYPE is correctly set
					
				} else {				// SERVICE_NOT_INSTALLED. It should be reomved from the table
			        try {
			        	resolver.delete (recordUri, null, null);		
					} catch (Exception e) {
						iDisasterApplication.getInstance().debug (2, "Update to "+ 
													SocialContract.Services.CONTENT_URI + "causes an exception");
						return iDisasterApplication.getInstance().UPDATE_EXCEPTION;
					}
				} 
					
			} else {					// A service installed by the user should be only registered once in the table 
		        try {
		        	resolver.delete (recordUri, null, null);		
				} catch (Exception e) {
					iDisasterApplication.getInstance().debug (2, "Update to "+ 
												SocialContract.Services.CONTENT_URI + "causes an exception");
					return iDisasterApplication.getInstance().UPDATE_EXCEPTION;
				}	
			}
		}
	
		return iDisasterApplication.getInstance().UPDATE_SUCCESS;
				
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

	public boolean serviceInstalled (Context cxt, String appPackageName) {
    	
    	if (appPackageName == null) {
    		return (false);
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
// TODO: remove test code
// Work on old Samsung Tab - Android 2.1 with 3G - and HTC Android 4.1 - but not on LG Android 2.1
//		            URL url = new URL("http://folk.ntnu.no/svarvaa/utils/pro2www/apk/Tshirt.apk");

// Work on old Samsung Tab - Android 2.1 with 3G - but not on HTC Android 4.1
//		            URL url = new URL ("http://www.sintef.no/project/UbiCompForAll/city_explorer/CityExplorer.apk");
		    	
//	            URL url = new URL(serviceURL);				// Download file URL
		    	URL url = new URL ("http://folk.ntnu.no/svarvaa/utils/pro2www/apk/Tshirt.apk");
	            
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
	public String launchApplication (Context cxt) {

// TODO: ADD parameters depending on serviceAppType
    	try {
        	Intent i = new Intent();
            PackageManager manager = cxt.getPackageManager();
            i = manager.getLaunchIntentForPackage(serviceGlobalId);
            i.addCategory(Intent.CATEGORY_LAUNCHER);
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
 * checkServiceShareStatus gets the share status of the service in SocialProvider for the selected CIS.
 */

	public String checkServiceShareStatus (Context cxt, String meId, String teamId) {
		
		if (sharingCursor != null) {
			sharingCursor.close();		// "close" releases data but does not set to null
			sharingCursor = null;
		}
	
		String[] sharingProjection = new String[] {
				SocialContract.Sharing._ID};							// Retrieve the _ID of the shared service entry (if any)
	
		String sharingSelection = 
				SocialContract.Sharing._ID_SERVICE + "= ?" +					// Service selected for the Details activity	
				"AND " + SocialContract.Sharing._ID_COMMUNITY + "= ?" +			// Selected community
				"AND " + SocialContract.Sharing._ID_OWNER + "= ?" +				// shared by ME
				"AND " + SocialContract.Sharing.TYPE + "= ?" +					// Type should be set to "shared"
				"AND " + SocialContract.Sharing.DELETED + "<> ?";				// Delete flag.
	
		String[] sharingSelectionArgs = new String[]
				{serviceId,														// For selected service
				teamId,	 														// For the selected CIS
				meId, 															// For me
				iDisasterApplication.getInstance().SERVICE_SHARED,				// Retrieve shared services
				"1"};
		
		try {
			sharingCursor= resolver.query(sharingUri, sharingProjection,
					sharingSelection, sharingSelectionArgs, null /* sortOrder*/);
		} catch (Exception e) {
			iDisasterApplication.getInstance().debug (2, "Query to "+ sharingUri + "causes an exception");
			return iDisasterApplication.getInstance().QUERY_EXCEPTION;
		}
	
		if (sharingCursor == null) {			// No cursor was set - should not happen?
			iDisasterApplication.getInstance().debug (2, "sharingCursor was not set to any value");
			return iDisasterApplication.getInstance().QUERY_EMPTY;
		}
	
		if (sharingCursor.getCount() == 0) {	// No service is shared in the team community
			return iDisasterApplication.getInstance().QUERY_EMPTY;
		}			
	
		if (sharingCursor.getCount() >1) {		// Multiple entries for that service - should not happen
			Toast.makeText(cxt.getApplicationContext(), 
					"SocialProvider database is corrupted: multiple entries for that shared service",
					Toast.LENGTH_LONG).show();
		}

		return iDisasterApplication.getInstance().QUERY_SUCCESS;
	}

/**
 * shareService add a new entry in SocialProvider Sharing table
 */
	public String shareService (Context cxt, String meId, String meUserName, String teamId) {
		
		// Set the values related to the activity to store in SocialProvider
		ContentValues sharingValues = new ContentValues ();
		
		sharingValues.put(SocialContract.Sharing._ID_SERVICE, serviceId);	// Id of the service to be shared
		sharingValues.put(SocialContract.Sharing._ID_OWNER, meId);			// Member recommending the service
		sharingValues.put(SocialContract.Sharing._ID_COMMUNITY, teamId);	// Recommend service in the selected team
		sharingValues.put(SocialContract.Sharing.TYPE, iDisasterApplication.getInstance().SERVICE_SHARED);
		sharingValues.put(SocialContract.Sharing.DESCRIPTION, "");

//Field needed temporarily
		sharingValues.put(SocialContract.Sharing.GLOBAL_ID,	serviceGlobalId);	// Global id of the service to be recommended
//Fields for synchronization with box.com
		sharingValues.put(SocialContract.CommunityActivity.ACCOUNT_NAME, meUserName);
		sharingValues.put(SocialContract.CommunityActivity.ACCOUNT_TYPE, "com.box");
		sharingValues.put(SocialContract.CommunityActivity.DIRTY, 1);

		
		try {
// The Uri value returned is not used.
//							Uri activityNewUri = getContentResolver().insert( SocialContract.CommunityActivity.CONTENT_URI,
//									sharingValues);
			resolver.insert( SocialContract.Sharing.CONTENT_URI, 
					sharingValues);			
		} catch (Exception e) {
			iDisasterApplication.getInstance().debug (2, "Insert to "+ 
								SocialContract.Sharing.CONTENT_URI + "causes an exception");
	    	return iDisasterApplication.getInstance().INSERT_EXCEPTION;
		}
		
		return iDisasterApplication.getInstance().INSERT_SUCCESS;
	}
    
/**
 * unshareService removes entry from SocialProvider Sharing table
 */
	public String unshareService (Context cxt) {
		
		if (sharingCursor == null) {			// No cursor previously when checking the shared status (in checkServiceShareStatus) 
												// - should not happen as "unshare" action is not available in that case
			iDisasterApplication.getInstance().debug (2, "sharingCursor was not set to any value");
			return iDisasterApplication.getInstance().QUERY_EMPTY;
		}
	
		if (sharingCursor.getCount() == 0) {	// No service is shared in the team community
			return iDisasterApplication.getInstance().QUERY_EMPTY;
		}					
		
		sharingCursor.moveToFirst();
		
		String s = sharingCursor.getString(sharingCursor.getColumnIndex(SocialContract.Sharing._ID));
		Uri recordUri = sharingUri.withAppendedPath(sharingUri, "/" + 
				sharingCursor.getString(sharingCursor.getColumnIndex(SocialContract.Sharing._ID)));
		
		ContentValues values = new ContentValues();
        values.put(SocialContract.Sharing.DELETED, "1");
		
		try {
// The number of rows deleted is not used.
//								Int i activityNewUri = getContentResolver().delete (SocialContract.Sharing.CONTENT_URI, null, null);
//			getContentResolver().delete(recordUri, null, null);
			resolver.update(recordUri, values, null, null);
			
		} catch (Exception e) {
			iDisasterApplication.getInstance().debug (2, "Delete of "+ recordUri + "causes an exception");
	    	return iDisasterApplication.getInstance().UPDATE_EXCEPTION;
		}
		
		return iDisasterApplication.getInstance().UPDATE_SUCCESS;
	}
	
}
