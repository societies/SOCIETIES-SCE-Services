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


import org.societies.thirdpartyservices.idisaster.R;
import org.societies.thirdpartyservices.idisaster.data.ThirdPartyService;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
//import android.widget.Toast;

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
	
	private String requestContext;
	private ThirdPartyService thirdPartyService;

// Status information for the user. Not stored in SocialProvider 
	private String SERVICE_STATUS_RECOMMENDED = "This service is recommended in the team.";
	private String SERVICE_STATUS_RECOMMENDED_INSTALLED = "This recommended service is already installed on your device. You can share it with the team members.";	
	private String SERVICE_STATUS_INSTALLED = "This service is installed on your device. You can share it with the team members.";
	private String SERVICE_STATUS_REMOVED = "This service was not properly or is no longer installed on your device.";
	private String SERVICE_STATUS_SHARED_BY_ME = "You have shared this service with the team members.";
	private String SERVICE_STATUS_SHARED_IN_CIS = "This service is shared by other team members.";

	private String SERVICE_STATUS_UNKNOWN = "No more information available.";
	
	private String serviceAction1; 
	private String serviceAction2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.service_details_layout);
//		resolver = getContentResolver ();

		Intent intent= getIntent(); 					// Get the intent that created activity		
		requestContext = intent.getStringExtra("REQUEST_CONTEXT");	// Retrieve first parameter (context for request of details)
		
		thirdPartyService = new ThirdPartyService
				(intent.getStringExtra("SERVICE_GLOBAL_ID")); 		// Retrieve second parameter: service ID
		
		serviceAction2 = iDisasterApplication.getInstance().SERVICE_NO_ACTION;	// In most cases, only one action is available for a service
		
		if (iDisasterApplication.testDataUsed) {
			int position = Integer.parseInt(thirdPartyService.serviceGlobalId);
			thirdPartyService.serviceName = iDisasterApplication.getInstance().CISserviceNameList.get (position);
			thirdPartyService.serviceDescription = iDisasterApplication.getInstance().CISserviceDescriptionList.get (position);
			thirdPartyService.serviceAvailable = iDisasterApplication.getInstance().SERVICE_NOT_INSTALLED;
			serviceAction1 = iDisasterApplication.getInstance().SERVICE_INSTALL;
			
		} else {
			// Retrieve service information from SocialProvider
			if (!(thirdPartyService.getServiceInformation (this, getContentResolver ())
					.equals(iDisasterApplication.getInstance().QUERY_SUCCESS))) {
				showQueryExceptionDialog ();	// Exception: Display dialog and terminates activity
			}
			
			// Check that availability status in SocialProvider is consistent with 
			// service availability	on the device
			if (!(thirdPartyService.checkServiceInstallStatus (this)
					.equals (iDisasterApplication.getInstance().UPDATE_SUCCESS))) {
				showQueryExceptionDialog ();	// Exception: Display dialog and terminates activity			
			}	
		}
		
		// Get text fields
		serviceNameView = (TextView) findViewById(R.id.serviceDetailsName);
		serviceDescriptionView = (TextView) findViewById(R.id.serviceDetailsDescription);
		serviceStatusView = (TextView) findViewById(R.id.serviceDetailsStatus);

		// Set name and description
		serviceNameView.setText(thirdPartyService.serviceName);
		serviceDescriptionView.setText(thirdPartyService.serviceDescription);
		
		// Set status (on UI) to temporary value
		serviceStatusView.setText(SERVICE_STATUS_UNKNOWN);
		
		if (requestContext.equals 
				(iDisasterApplication.getInstance().SERVICE_RECOMMENDED)) {			// Details request for a service recommended in the CIS
			
			
			if (thirdPartyService.serviceAvailable														// If the service is installed on the device
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
			
			if (thirdPartyService.serviceAvailable															// If the service is really installed on the device
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

			// NB: The service should normally not be installed. If so it was already launched 
			if (thirdPartyService.serviceAvailable													// If the service is installed on the device
					.equals(iDisasterApplication.getInstance().SERVICE_INSTALLED)) {
				// Set status (on UI)
				serviceAction1 = iDisasterApplication.getInstance().SERVICE_LAUNCH;			// Action available to user is Launch
				
			} else {																	// If the service is NOT installed on the device
				// Set status (on UI)
				serviceAction1 = iDisasterApplication.getInstance().SERVICE_INSTALL; 		// Action available to user is Install
			}
		}
		
		button1 = (Button) findViewById(R.id.serviceDetailsButton1);
		button1.setText(serviceAction1);
		button1.setOnClickListener(this);
		
		button2 = (Button) findViewById(R.id.serviceDetailsButton2);
		if (serviceAction1.equals((iDisasterApplication.getInstance().SERVICE_LAUNCH))) {
			
			if (!requestContext.equals (iDisasterApplication.getInstance().SERVICE_SHARED)) {		// Only the request is not about a shared service
				String sharingStatus = thirdPartyService.checkServiceShareStatus(this,
						iDisasterApplication.getInstance().me.globalId,
						iDisasterApplication.getInstance().selectedTeam.globalId) ;
				if (sharingStatus.equals (iDisasterApplication.getInstance().QUERY_EXCEPTION)) {
					showQueryExceptionDialog ();	// SocialProvider exception: Display dialog and terminates activity
					
				} else {
					if (sharingStatus.equals (iDisasterApplication.getInstance().QUERY_SUCCESS)) {
						serviceAction2 = iDisasterApplication.getInstance().SERVICE_UNSHARE;	// Alternative action to user is Unshare
						
					} else {
						serviceAction2 = iDisasterApplication.getInstance().SERVICE_SHARE;		// Alternative action to user is Share
						serviceStatusView.setText (SERVICE_STATUS_SHARED_BY_ME);

					}
				}

				button2.setText(serviceAction2);
				button2.setOnClickListener(this);
				
			} else {
				button2.setVisibility(android.view.View.GONE);		// Hide button2							
			}
			
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
    			if (thirdPartyService.installService (this)
    					.equals (iDisasterApplication.getInstance().DOWNLOAD_EXCEPTION)) {
    				showServiceExceptionDialog 			// Install exception; No termination (the user may have to switch on network access)
    					(getString(R.string.dialogServiceDownloadException));		
    				return;	
    			} else {								// Service was correctly installed - Update data in SocialProvider
    				if (!(thirdPartyService.updateServiceInstallStatus (this, iDisasterApplication.getInstance().SERVICE_INSTALLED)
    						.equals (iDisasterApplication.getInstance().UPDATE_SUCCESS))) {					// Update of SocialProvider has failed
    					showQueryExceptionDialog ();	// Exception: Display dialog and terminates activity			
    				} else {
    					finish ();
    				}
    				return;
    			}

    		} else if (serviceAction1.equals (iDisasterApplication.getInstance().SERVICE_LAUNCH)) {			// Request to launch
    			if (thirdPartyService.launchApplication (this)
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
    
    		if (serviceAction2.equals (iDisasterApplication.getInstance().SERVICE_SHARE)) {					// Request to share a service
    			
    			if (thirdPartyService.shareService (this, iDisasterApplication.getInstance().me.globalId,
    												iDisasterApplication.getInstance().selectedTeam.globalId)
    					.equals (iDisasterApplication.getInstance().INSERT_EXCEPTION)) {			// Update information in SocialProvider 
    				showQueryExceptionDialog (); 			/// SocialProvider exception: Display dialog and terminates activity
    			} else {
    				finish ();
    			}
    			
    		} else if (serviceAction2.equals (iDisasterApplication.getInstance().SERVICE_UNSHARE)) {
    			
    			if (thirdPartyService.unshareService (this)
    					.equals (iDisasterApplication.getInstance().UPDATE_SUCCESS)) {			// Update information in SocialProvider 
    				finish ();
    			} else {
    				showQueryExceptionDialog (); 			/// SocialProvider exception: Display dialog and terminates activity
    			}
 
    		} else {				// should never happen
    			return;
    		}
    	}
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
