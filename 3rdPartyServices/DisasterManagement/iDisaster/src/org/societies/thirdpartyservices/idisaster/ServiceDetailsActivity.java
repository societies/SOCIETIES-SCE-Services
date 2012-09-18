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
import java.util.List;

import org.societies.android.api.cis.SocialContract;
import org.societies.thirdpartyservices.idisaster.R;

import android.app.Activity;
import android.content.ContentResolver;
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
	
	private String serviceGlobalId;
	private String serviceName;
	private String serviceDescription;
	
	//TODO: will be fetched from SocialProvider when available...
	private String servicePackage ="org.ubicompforall.cityexplorer";  // can be found
	private String serviceIntent =".gui.CalendarActivity";	// can be found
//	privat String serviceIntent ="org.ubicompforall.cityexplorer.gui.PlanPoiTab";  // can be found
	
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
		String actionContext = intent.getStringExtra("CONTEXT"); 	// Retrieve first parameter (service related to a CIS?)
		
		String action = intent.getStringExtra("ACTION"); 			// Retrieve second parameter (what kind of relation?)
		
// Needed? This is a global resource that is retrieved from iDisaster App
//		String cisID = intent.getStringExtra("CIS_ID");
		String serviceID = intent.getStringExtra("SERVICE_ID"); // Retrieve third parameter: service ID
		
		
		
		if (iDisasterApplication.testDataUsed) {
			int position = Integer.parseInt(serviceID);
			if (actionContext.equals("ADD_TO_CIS")){
				serviceName = iDisasterApplication.getInstance().serviceNameList.get (position);
				serviceDescription = iDisasterApplication.getInstance().serviceDescriptionList.get (position);
				} else if (actionContext.equals("RELATED_TO_CIS")) {
				serviceName = iDisasterApplication.getInstance().CISserviceNameList.get (position);
				serviceDescription = iDisasterApplication.getInstance().CISserviceDescriptionList.get (position);
			} // else: should not happened. Feil action code was used
		} else {
			getServiceInformation (serviceID);
			// TODO: Add a check that some info was retrieved
		}
		
		// Get text fields
		serviceNameView = (TextView) findViewById(R.id.showServiceDetailsName);
		serviceNameView.setText(serviceName);

		//TODO: get parameters from intent
		//TODO: get service information from content provider

		// Set name and description
		serviceDescriptionView = (TextView) findViewById(R.id.showServiceDetailsDescription);
		serviceDescriptionView.setText(serviceDescription);
		
		// Add button: 3 options (install, launch, share depending of settings)
		// Define what action can be performed on the service
		
		
		final Button button = (Button) findViewById(R.id.showServiceDetailsButton);
		
		if ((serviceAction != SERVICE_INSTALL) && (serviceAction != SERVICE_LAUNCH) && 
				(serviceAction != SERVICE_SHARE)) {
			updateServiceAction ();							// This should normally not happen!
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
		if (serviceAction == SERVICE_INSTALL) {
			// check that the service is not yet installed
		    Intent intent1 = new Intent(serviceIntent);      
		      
//		    if (isCallable(intent1)== true){
//    			Toast.makeText(getApplicationContext(),  serviceIntent + " found" , Toast.LENGTH_LONG).show();		    	
//		    } else {
//    			Toast.makeText(getApplicationContext(),  serviceIntent + " NOT found" , Toast.LENGTH_LONG).show();		    	
//		    	
//		    }
			
		    if (isInstalled (servicePackage) == true){
    			Toast.makeText(getApplicationContext(),  servicePackage + " found" , Toast.LENGTH_LONG).show();		    	
		    } else {
    			Toast.makeText(getApplicationContext(),  servicePackage + " NOT found" , Toast.LENGTH_LONG).show();		    	
		    	
		    }

			
		} else if (serviceAction == SERVICE_LAUNCH) {
			
		} else if (serviceAction == SERVICE_SHARE) {
			
		} else {
			// This should never happened!
			// TODO: Update data in Content provider and proceeds as for install
		}

		
		
		Toast.makeText(getApplicationContext(),
			"Not implemented yet!", Toast.LENGTH_LONG).show();
		
	}



/**
 * getServiceInformation retrieves the information about the service 
 * from Social Provider.
 */

	private void getServiceInformation (String id) {

		serviceGlobalId ="";
		serviceName ="";
		serviceDescription ="";

		Uri serviceUri = SocialContract.Services.CONTENT_URI;
						
		String[] serviceProjection = new String[] {
							SocialContract.Services.GLOBAL_ID,
							SocialContract.Services.NAME,
							SocialContract.Services.DESCRIPTION};
		String serviceSelection = SocialContract.Services.GLOBAL_ID + "= ?";
		String [] serviceSelectionArgs = new String [] {id} ;
		
		Cursor serviceCursor = resolver.query (serviceUri, serviceProjection, serviceSelection,
									   serviceSelectionArgs,
									   null /* sortOrder*/);
		if (serviceCursor != null) {
			if (serviceCursor.moveToFirst()){		// Should not be several matches; anyway only select the first
				serviceGlobalId = serviceCursor.getString(serviceCursor		// should be the same as "id"
						.getColumnIndex(SocialContract.Services.GLOBAL_ID));
				serviceName = serviceCursor.getString(serviceCursor
						.getColumnIndex(SocialContract.Services.NAME));
				serviceDescription = serviceCursor.getString(serviceCursor
						.getColumnIndex(SocialContract.Services.DESCRIPTION));				
			}
		}
	}

/**
 * Update service data in Content provider.
 */

	public void updateServiceAction () {
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

    private boolean isInstalled (String appName) {
    	
    	// Get package names of all installed applications
    	PackageManager pm = getPackageManager();
    	List <PackageInfo> list = pm.getInstalledPackages(0);
    
    	for (PackageInfo pi : list) {
    	   ApplicationInfo ai;
    	   try {
    		   ai = pm.getApplicationInfo(pi.packageName, 0);
//    		   System.out.println(">>>>>>packages is<<<<<<<<" + ai.publicSourceDir);   		   
    		   if (!((ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0)) {			// Only consider 3rd party Apps
    			   																// Ignore Apps installed in the device's system image. 
//    			   Log.d(getClass().getSimpleName(), ">>>>>>packages is NOT system package " + pi.packageName);
//    			   System.out.println(">>>>>>packages is<<<<<<<<" + ai.publicSourceDir);
    			   if (appName.equals(pi.packageName)) {
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

    
}
