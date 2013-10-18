package org.societies.thirdpartyservices.ijacket;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeoutException;

import org.societies.android.api.cis.SocialContract;
import org.societies.thirdpartyservices.ijacket.com.BluetoothConnection;
import org.societies.thirdpartyservices.ijacket.com.ComLibException;
import org.societies.thirdpartyservices.ijacket.com.ConnectionListener;
import org.societies.thirdpartyservices.ijacket.com.ConnectionMetadata;
import org.societies.thirdpartyservices.ijacket.com.ConnectionMetadata.DefaultServices;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;


public class IJacketScanActiv extends Activity{// implements OnItemSelectedListener {

    private static final int CUSTOM_REQUEST_QR_SCANNER = 0;
    
    //private BluetoothConnection con;
    private static final int IJACK_SCAN_ACTV_CONTENT_VIEW_ID = 10101010;
    private TableLayout layout;
    private Button scanButton;
    private Button testButton;
    
    
    private Button preferredJackButton;
    //Loader l;
    //private Spinner spinnerCIS;
	
    private static final String LOG_TAG = IJacketScanActiv.class.getName();

	   /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "IJacketScanActiv onCreate");
        
        
/*      WE ARE NOW LISTENING TO ALL COMMUNTIES  
 		Intent intent = getIntent();
        long cisId = intent.getLongExtra(org.societies.thirdpartyservices.ijacketlib.IJacketDefines.IjacketIntentExtras.CIS_ID,-1);// TODO: set the intent extra
        if(-1 != cisId){
            SharedPreferences mypref = getSharedPreferences(IJacketApp.PREF_FILE_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor e = mypref.edit();
    		e.putLong(IJacketApp.CIS_JID_PREFERENCE_TAG, cisId);
    		e.commit();

        }else{
        	Log.d(LOG_TAG, "IJacketScanActiv has not received intent argument on startup");
        } */

        getAppInfoFromProvider();
        Log.d(LOG_TAG, "got app info from provider");
        
	        layout = new TableLayout(this);
	        layout.setId(IJACK_SCAN_ACTV_CONTENT_VIEW_ID);
	        layout.setLayoutParams( new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT) );
	        layout.setOrientation(TableLayout.VERTICAL);
	
	        
	        
	        
	        //Initialize scan button
	        scanButton = new Button(this);
	        scanButton.setText("Scan QR Tag");
	        scanButton.setOnClickListener( new View.OnClickListener() {
	            public void onClick(View view) {
	            	try{
	            		
	            		/* it was decided that it wont be needed to choose a team
	            		SharedPreferences mypref = getSharedPreferences(IJacketApp.PREF_FILE_NAME, MODE_PRIVATE);
	        			long comId = mypref.getLong(IJacketApp.CIS_JID_PREFERENCE_TAG, -1);
	        			if(-1 == comId){
	        				quickToastMessage("Please first select a team");
	        				Log.d("LOG_TAG", "no community selected" );
	        				return;
	        			}*/
	            		
	            		
		                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
		                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		                intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
		                startActivityForResult(intent, CUSTOM_REQUEST_QR_SCANNER);
	            	}
	            	catch(ActivityNotFoundException ex) {
	            		quickToastMessage("You need to install zxing Barcode scanner!");
	            	}
	            }
	        } );
	        
	      //Initialize test button
	        testButton = new Button(this);
	        testButton.setText("Jacketless test");
	        testButton.setOnClickListener( new View.OnClickListener() {
	            public void onClick(View view) {
	            	try{
	            		
	            		/* it was decided that it wont be needed to choose a team
	            		SharedPreferences mypref = getSharedPreferences(IJacketApp.PREF_FILE_NAME, MODE_PRIVATE);
	        			long comId = mypref.getLong(IJacketApp.CIS_JID_PREFERENCE_TAG, -1);
	        			if(-1 == comId){
	        				quickToastMessage("Please first select a team");
	        				Log.d("LOG_TAG", "no community selected" );
	        				return;
	        			}*/
	        			IJacketApp appState = (IJacketApp) (getApplication());
	        			appState.setTestMode(true);
	        			// now we trigger the Menu Activity
	    				Intent i = new Intent(IJacketScanActiv.this, JacketMenuActivity.class);
	    				startActivity(i);
	            		
	            	}
	            	catch(ActivityNotFoundException ex) {
	            		quickToastMessage("You need to install zxing Barcode scanner!");
	            	}
	            }
	        } );
	        
	       // resetUI();
	        
	        
	        
	        //super.setContentView(layout);

    }
    

    
    private void addItemsOnCISSpinner(){
    	//if (savedInstanceState == null) {
    	Log.d(LOG_TAG, "start of add items to spinner");
		IJacketApp appState = (IJacketApp) (getApplication());
		long tempServId =	appState.getiJacketSevId();
    	Fragment newFragment = new MainActivityCursorLoader(tempServId);
    	FragmentTransaction ft = getFragmentManager().beginTransaction();
    	ft.add(IJACK_SCAN_ACTV_CONTENT_VIEW_ID, newFragment).commit();
    	Log.d(LOG_TAG, "end of add items to spinner");
    	//}
    
    /*   		spinnerCIS = new Spinner(IJacketScanActiv.this);
    		spinnerCIS.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
    		
    		//List<String> SpinnerArray = new ArrayList<String>();	
    	        Log.d(LOG_TAG, "going to query the social provider");
    	        cr = this.getApplication().getContentResolver();
    	       
    	        Uri COMUNITIES_URI = Uri.parse(SocialContract.AUTHORITY_STRING + SocialContract.UriPathIndex.COMMINITIES);
    	        try{      
    	        	
	    	       	 Cursor cursor = cr.query(COMUNITIES_URI,null,null,null,null);
	    	       	startManagingCursor(cursor);

	    	       	String[] columns = new String[] {SocialContract.Communities.NAME}; // field to display
	    	       	int to[] = new int[] {android.R.id.text1}; // display item to bind the data
	    	       	SimpleCursorAdapter ad =  new SimpleCursorAdapter(this,android.R.layout.simple_spinner_item,cursor ,columns ,to);
	    	        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    	       	spinnerCIS.setAdapter(ad);
	    	  
	    	        spinnerCIS.setOnItemSelectedListener(this);
	    	        Log.d(LOG_TAG, "spiner filled up");
	    	        
	    	        layout.addView(spinnerCIS);  		
	    	   		
	    	   		//if(null != cursor) cursor.close();
    		   	}catch (Exception e) {
    		   		// TODO Auto-generated catch block
    		   		Log.d(LOG_TAG, "exception in the create");
    		   		e.printStackTrace();
    		   	}

    	        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, SpinnerArray);
    	        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

*/
    }
    
    private void clearUI() {
        runOnUiThread(new Runnable() {
             public void run() {
            	 layout.removeAllViews();
             }
        });
    }
    
   protected void onResume(){
	   super.onResume();
	   //clearUI();
    	resetUI();
    	super.setContentView(layout);
    }
    
    private void resetUI() {
        runOnUiThread(new Runnable() {
             public void run() {
            	 Log.d(LOG_TAG, "resetUI thread");
            	 layout.removeAllViews();
            	 
            	 IJacketScanActiv.super.setTitle("IJacket");


            	 
            	 TextView text1 = new TextView(IJacketScanActiv.this);
            	 text1.setText("Please scan the QR tag of your jacket");
            	 text1.setTextSize(24);
            	 layout.addView(text1);
            	 
            	 layout.addView(scanButton);
            	 
            	 layout.addView(testButton);
            	 
            	 
            	 //ImageView image = new ImageView(IJacketScanActiv.this);
            	 //image.setImageResource(R.drawable.scan);
            	 //layout.addView(image);
            	 
            	 SharedPreferences mypref = getSharedPreferences(IJacketApp.PREF_FILE_NAME, MODE_PRIVATE);
            	 if(mypref.contains(IJacketApp.MAC_PREFERENCE_TAG)){
            		 
                	 TextView text2 = new TextView(IJacketScanActiv.this);
                	 text2.setText("Or connect to previous jacket");
                	 text2.setTextSize(24);
                	 layout.addView(text2);
            		 
            		 Log.d(LOG_TAG, "adding last jacket button");
            		 preferredJackButton = new Button(IJacketScanActiv.this);
				 	 preferredJackButton.setText("Connect to previous jacket");
		 		 	 preferredJackButton.setOnClickListener( new OnClickListener(){

		 				public void onClick(View v) {
		 					Intent intent = new Intent(IJacketScanActiv.this, JacketMenuActivity.class);
		 					startActivity(intent);};
		 		 	 });
		 		 	layout.addView(preferredJackButton);
		 		 }
            	 
            	 TextView text0 = new TextView(IJacketScanActiv.this);
            	 text0.setText("You are listening to the teams:");
            	 text0.setTextSize(24);
            	 layout.addView(text0);
            	 
            	 addItemsOnCISSpinner();

             }
        });
    }
        




    private void quickToastMessage(final String message) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(IJacketScanActiv.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == CUSTOM_REQUEST_QR_SCANNER) {
        	
        	//Successful scan
            if (resultCode == RESULT_OK) {
            	Log.d("LOG_TAG", "scan OK" );
                String macAddress = intent.getStringExtra("SCAN_RESULT");
                
                SharedPreferences mypref = getSharedPreferences(IJacketApp.PREF_FILE_NAME, MODE_PRIVATE);
                SharedPreferences.Editor e = mypref.edit();
				e.putString(IJacketApp.MAC_PREFERENCE_TAG, macAddress);
				e.commit();
                
                // Handle successful scan
				Intent i = new Intent(IJacketScanActiv.this, JacketMenuActivity.class);
				startActivity(i);

            } 
            
            //Failed scan
            else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
            	resetUI();
                quickToastMessage("Failed to scan!");
            }
        }
    }


    private void getAppInfoFromProvider(){

    	Log.d(LOG_TAG, "going to retrieve our service ID");
    	ContentResolver cr = this.getApplication().getContentResolver();
		IJacketApp appState = (IJacketApp) (getApplication());
    	
    	Uri uri = Uri.parse(SocialContract.AUTHORITY_STRING + SocialContract.UriPathIndex.SERVICES);
        try{
        	//String[] mProjection ={SocialContract.Me.GLOBAL_ID};
        	String mSelectionClause = SocialContract.Services.GLOBAL_ID + " = ?";
        	String[] mSelectionArgs = {org.societies.thirdpartyservices.ijacketlib.IJacketDefines.AccountData.IJACKET_SERVICE_NAME};

	       	 Cursor cursor = cr.query(uri,null,mSelectionClause,mSelectionArgs,null);
	       	if (null == cursor || cursor.getCount() < 1){
	       		Log.d(LOG_TAG, "could not find the service id");
	       		return;
	       	}
	       	else{
       			int i  = cursor.getColumnIndex(SocialContract.Services._ID);
       			cursor.moveToNext();
       			long ServiceID = cursor.getLong(i);
       			appState.setiJacketSevId(ServiceID);
   		    	Log.d(LOG_TAG, "serviceID is " + ServiceID);
	       	}
    	}catch (Exception e) {
	   		// TODO Auto-generated catch block
	   		Log.d(LOG_TAG, "exception in the serviceID");
	   		e.printStackTrace();
	   		return ;
	   	}
    



    	
    	// get my credential
        long cisId = utilCursor.retrieveUserID(cr);
        appState.setUserId(cisId);
        
        return ;
        
    }


	
}

