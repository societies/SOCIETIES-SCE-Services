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

import com.disaster.idisaster.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Activity for adding a new service to the selected disaster team (community).
 * 
 * @author Jacqueline.Floch@sintef.no
 *
 */

// TODO: At the moment a list of services is provided. Some search functionality should be added
// TODO: Removed the services already in CIS from the list...

public class ServiceAddActivity extends ListActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);

    //TODO: query Content Provider for service repository
	
  	boolean noService = false;
  	
  	if (noService) {
  		//  TextView cannot be used here as the Activity is a ListActivity
  		//  TextView textview = new TextView(this);
  		//	textview.setText("This is the Services tab");
  		//  setContentView(textview);

  		// Create dialog if no service in disaster team						
      	AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
      	alertBuilder.setMessage(getString(R.string.serviceListDialog))
      		.setCancelable(false)
      		.setPositiveButton (getString(R.string.dialogOK), new DialogInterface.OnClickListener() {
      			public void onClick(DialogInterface dialog, int id) {
      				// add code
      				return;
      			}
      		});
  	    AlertDialog alert = alertBuilder.create();
  	    alert.show();
  	    return;

  	} else {
    
    	setContentView (R.layout.service_add_layout);
    	ListView listView = getListView();
    	
    	// Enable filtering for the contents of the list view.
    	// The filtering logic should be provided
    	// listView.setTextFilterEnabled(true);  
    	
    	
// TODO: Get the list from the Societies Content Provider


    	// The Adapter provides access to the data items.
    	// The Adapter is also responsible for making a View for each item in the data set.
    	//  Parameters: Context, Layout for the row, ID of the View to which the data is written, Array of data

//TODO: customize the layout for the row is necessary
// At the moment a simple string is used as for disaster.

    	iDisasterApplication.getInstance().serviceAdapter = new ArrayAdapter<String> (this,
		R.layout.disaster_list_item, R.id.disaster_item, iDisasterApplication.getInstance().serviceNameList);

    	// Assign adapter to ListView

    	listView.setAdapter(iDisasterApplication.getInstance().serviceAdapter);

    	// Add listener for short click.
    	// 
    	listView.setOnItemClickListener(new OnItemClickListener() {
    		public void onItemClick (AdapterView<?> parent, View view,
    			int position, long id) {
// TODO: Remove code for testing the correct setting of preferences 
    			Toast.makeText(getApplicationContext(),
    				"Click ListItem Number   " + (position+1) + "   " + iDisasterApplication.getInstance().serviceNameList.get (position), Toast.LENGTH_LONG)
    				.show();

    			// Start the ServiceDetails Activity
    			startActivity (new Intent(ServiceAddActivity.this, ServiceDetailsActivity.class));

    			// The activity is kept on stack (check also that "noHistory" is not set in Manifest)
//    			finish();
    			}
    		
    		});
  		}

  	
  	
// Test: download City Explorer => https://play.google.com/store/apps/details?id=org.ubicompforall.cityexplorer&feature=search_result#?t=W251bGwsMSwxLDEsIm9yZy51Ymljb21wZm9yYWxsLmNpdHlleHBsb3JlciJd  	
  	
  	
// TODO: Remove the activity from stack when back button?
  	
    }
  

/**
 * onCreateOptionsMenu expands the activity menu for this activity tab.
 */

// No menu yet!
//@Override
//	public boolean onCreateOptionsMenu(Menu menu){
//
//		//The FIXED menu is set by the TabActivity.
//// I am uncertain why the call to the super class leads to the creation
//// of the fixed menu set by the TabActivity (DisasterActivity)
//		super.onCreateOptionsMenu(menu);
//		
//		menu.setGroupVisible(R.id.disasterMenuService, true);
//		return true;
//}

/**
 * onOptionsItemSelected handles the selection of an item in the activity menu. 
 */
 // No menu yet!
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//
//		// The TabActivity handles items in the FIXED menu.
//// I am uncertain why the call to the super class leads to handling
//// of a command in the fixed menu by the TabActivity (DisasterActivity)
//		super.onOptionsItemSelected(item);
//
//		switch (item.getItemId()) {
//
//			case R.id.disasterMenuAddService:
//
//////TODO: Remove code for testing the correct setting of preferences 
////				Toast.makeText(getApplicationContext(),
////						"Menu item chosen: Add service", Toast.LENGTH_LONG)
////						.show();			
//
//				startActivity(new Intent(ServiceListActivity.this, ServiceAddActivity.class));
//			break;
//		
//			default:
//			break;
//		}
//		return true;
//	}

}






// OLD code that assumes a search functionality
//
//public class ServiceAddActivity extends Activity implements OnClickListener {
//
//	private EditText serviceNameView;
//	private EditText serviceDescriptionView;
//	private String serviceName;
//	private String serviceDescription;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.service_add_layout);
//
//		// Get editable fields
//		serviceNameView = (EditText) findViewById(R.id.editServiceAddName);
//		serviceDescriptionView = (EditText) findViewById(R.id.editServiceAddDescription);
//
//    	// Add click listener to button
//    	final Button button = (Button) findViewById(R.id.serviceAddButton);
//    	button.setOnClickListener(this);
//
//    }
//
//
///**
// * onClick is called when button is clicked because
// * the OnClickListener is assigned to the button
// * */
//
//	public void onClick(View view) {
//
//    	if (serviceNameView.getText().length() == 0) {					// check input for service name
//
//    		// Hide the soft keyboard otherwise the toast message does appear more clearly.
//    	    InputMethodManager mgr = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
//    	    mgr.hideSoftInputFromWindow(serviceNameView.getWindowToken(), 0);
//	    
//    		Toast.makeText(this, getString(R.string.toastServiceName), 
//    				Toast.LENGTH_LONG).show();
//    		return;
//
//    	} else if (serviceDescriptionView.getText().length() == 0) {	// check input for description (or any obligatory field)
//
//    		// Hide the soft keyboard otherwise the toast message does appear more clearly.
//    	    InputMethodManager mgr = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
//    	    mgr.hideSoftInputFromWindow(serviceDescriptionView.getWindowToken(), 0);
//
//    	    Toast.makeText(this, getString(R.string.toastServiceDescription), 
//	    			Toast.LENGTH_LONG).show();
//	    	return;
//
//    	} else {														// add to registry
//
//    		serviceName = serviceNameView.getText().toString();
//    		serviceDescription = serviceDescriptionView.getText().toString();
//
//    		//TODO: Add call to the Social Provider 
//
//	    		
////TODO: Refresh list of services? - so it is displayed in the previous activity
//    		
////TODO: remove test code
//    	    iDisasterApplication.getInstance().serviceNameList.add(serviceName);
//    	    
//    	    // report data change to adapter
//// TODO: Add to adapter
////    	    iDisasterApplication.getInstance().serviceAdapter.notifyDataSetChanged();
//
//    		
//// TODO: Remove code for testing the correct setting of preferences 
//    	    Toast.makeText(this, "Debug: "  + serviceName + " " + serviceDescription, 
//    			Toast.LENGTH_LONG).show();
//
//    	    // Hide the soft keyboard:
//			// - the soft keyboard will not appear on next activity window!
//    	    InputMethodManager mgr = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
//    	    mgr.hideSoftInputFromWindow(serviceNameView.getWindowToken(), 0);
//
//	    	finish();
//    	    // Go back to the previous activity
//	    }
//    }
//
//}
