package org.societies.thirdpartyservices.idisaster;

//import org.societies.api.css.management.ICssRecord;
//import org.societies.api.css.management.ISocietiesApp;
// import org.societies.cis.android.client.SocietiesApp;

import java.util.ArrayList;

import org.societies.android.api.cis.SocialContract;
import org.societies.thirdpartyservices.idisaster.R;
import org.societies.thirdpartyservices.idisaster.data.Me;
import org.societies.thirdpartyservices.idisaster.data.SelectedTeam;

import android.app.AlertDialog;
import android.app.Application;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This activity is responsible for interaction with the
 * main home page for iDisaster.
 * The home page relates to a specific Disaster team (community)
 * and provide access to community feed (activities), members and
 * services.
 * 
 * @authors Jacqueline.Floch@sintef.no
 *
 */
public class DisasterActivity extends TabActivity {
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        
// TODO: Add check that 1) a disaster is selected 2) the selected disaster still exists...
        
        setContentView (R.layout.disaster_layout);

		TextView title = (TextView)findViewById(R.id.disasterLabel);

		title.setText (iDisasterApplication.getInstance().selectedTeam.name);
        
        Resources res = getResources();		// Resource object to get Drawables
        TabHost tabHost = getTabHost();		// The activity TabHost
        TabHost.TabSpec spec;				// Reusable TabSpec for each tab
        Intent intent;						// Reusable Intent for each tab

        // For each hosted Activity
        // - create an Intent to launch an Activity (to be reused)
        // - initialize a TabSpec for each tab and add it to the TabHost

        // Create Feed Activity Tab
        intent = new Intent().setClass(this, FeedListActivity.class);
        spec = tabHost.newTabSpec("activities").setIndicator("Activities",
                          res.getDrawable(R.drawable.ic_tab_activities))
                      .setContent(intent);
        tabHost.addTab(spec);

        // Create Member Activity Tab
        intent = new Intent().setClass(this, MemberListActivity.class);
        spec = tabHost.newTabSpec("members").setIndicator("Members",
                          res.getDrawable(R.drawable.ic_tab_members))
                      .setContent(intent);
        tabHost.addTab(spec);

        // Create Service Activity Tab
        intent = new Intent().setClass(this, ServiceListActivity.class);
        spec = tabHost.newTabSpec("services").setIndicator("Services",
                          res.getDrawable(R.drawable.ic_tab_services))
                      .setContent(intent);
        tabHost.addTab(spec);

        // Start with Feed tab visible:
        tabHost.setCurrentTab(0);

    }

    /** Called at start of the active lifetime. */
//    @Override
//	protected void onResume() {
//		super.onResume();
//	}//onResume

    /** Called when activity goes in foreground */
// Reset of user preferences is now done in onResume in DisasterList Activity
//    @Override
//    protected void onPause () {
//    	super.onPause ();
//    	iDisasterApplication.getInstance().setDisasterName 
//		(getString(R.string.noPreference));					// reset user preferences
//    }
    
/**
 * onCreateOptionsMenu creates the FIXED activity menu for the TabActivity.
 * Each TabHost will add a variable menu.
 */  
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
    	
    	menu.clear();
    	getMenuInflater().inflate(R.menu.disaster_menu, menu);
    	
    	// Disable all variable menus; a variable menu is set by each hosted Activity
		menu.setGroupVisible(R.id.disasterMenuFeed, false);
		menu.setGroupVisible(R.id.disasterMenuMember, false);
		menu.setGroupVisible(R.id.disasterMenuService, false);

// Alternative code: set variable menu in this Activity 
//    	String currentTab = tabHost.getCurrentTabTag ();
//    	if (currentTab == "activities") {
//    		menu.setGroupVisible(R.id.disasterMenuFeed, true);
//    		menu.setGroupVisible(R.id.disasterMenuUser, false);
//    		menu.setGroupVisible(R.id.disasterMenuService, false);
//    	} else ...

    	return true;
    }

 /**
  * onOptionsItemSelected handles the selection of an item in the activity menu.
  * Each TabHost handles the selection of the items defined for that TabHost.
  */
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {

			case R.id.disasterMenuSelectDisaster:
// Reset of user preferences is done in onResume in DisasterList Activity
//            	iDisasterApplication.getInstance().setDisasterName 
//            		(getString(R.string.noPreference));					// reset user preferences

				// Start the Disaster Activity
    			Intent intent = new Intent(DisasterActivity.this, DisasterListActivity.class);  
    			intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT); // Put activity in the front of stack if it is already running.
    			startActivity(intent);

           	 	finish();

			break;

// Alternative code: handle commands in the variable menu ins this activity
// (currently done in the hosted activity)			
//			case R.id.disasterMenuAddFeed:
//				...
//			break;
//			
//			case ...

			default:
    		break;
    	}
    	return true;
    }
  
}