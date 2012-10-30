package org.societies.thirdpartyservices.ijacket;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeoutException;

import org.societies.android.api.cis.SocialContract;
import org.societies.thirdpartyservices.ijacket.IJackConnManager.IJackConnBinder;
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
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
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
    private Button disconnectButton;
    private Button scanButton;
    private Button preferredJackButton;
    //ContentResolver cr;
    //Loader l;
    //private Spinner spinnerCIS;
    
    

    /** Flag indicating whether we have called bind on the service. */
    boolean mBound = false;
    IJackConnManager mService = null;
    
    private static final String LOG_TAG = IJacketScanActiv.class.getName();

    
	   /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "IJacketScanActiv onCreate");
        

		if(true == mBound && null!= mService){
			if(mService.getConStats()){
				// we are already connected
				Intent intent = new Intent(IJacketScanActiv.this, JacketMenuActivity.class);
				startActivity(intent);
				return;
			}
		}
		// otherwise we draw gui
		
		IJacketApp appState = ((IJacketApp)getApplicationContext());
		//appState.setCurrActiv(this);
		
		Log.d(LOG_TAG, "connection does not exist on IJacketScanActiv onCreate");  
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
            		Intent intent = new Intent("com.google.zxing.client.android.SCAN");
	                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
	                intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
	                startActivityForResult(intent, CUSTOM_REQUEST_QR_SCANNER);
            		
            		// TEMP
                    //Intent intent = new Intent(IJacketScanActiv.this, JacketMenuActivity.class);
                    //startActivity(intent);

            		
            		
            	}
            	catch(ActivityNotFoundException ex) {
            		quickToastMessage("You need to install zxing Barcode scanner!");
            	}
            }
        } );
        
        resetUI();
        
        
        //Initialize disconnect/reconnect button
        disconnectButton = new Button(this);
        disconnectButton.setText("Disconnect");
        disconnectButton.setOnClickListener( new OnClickListener(){

			public void onClick(View v) {
				disconnect();
				resetUI();
			}
        	
        });
        
        super.setContentView(layout);


    }
    

    
    private void addItemsOnCISSpinner(){
    	//if (savedInstanceState == null) {
    	Log.d(LOG_TAG, "start of add items to spinner");
    	Fragment newFragment = new MainActivityCursorLoader();
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
    
    private void resetUI() {
        runOnUiThread(new Runnable() {
             public void run() {
            	 Log.d(LOG_TAG, "resetUI thread");
            	 layout.removeAllViews();
            	 
            	 IJacketScanActiv.super.setTitle("IJacket");

            	 TextView text0 = new TextView(IJacketScanActiv.this);
            	 text0.setText("Please choose the Community to share your jacket updates.");
            	 text0.setTextSize(24);
            	 layout.addView(text0);
            	 
            	 addItemsOnCISSpinner();
            	 
            	 TextView text1 = new TextView(IJacketScanActiv.this);
            	 text1.setText("Please scan the QR tag of your jacket");
            	 text1.setTextSize(24);
            	 layout.addView(text1);
            	 
            	 layout.addView(scanButton);
            	 
            	 TextView text2 = new TextView(IJacketScanActiv.this);
            	 text2.setText("This will connect your Android via Bluetooth to the remote device and retrieve more information of your OSNAP product. You can test the functionality of the remote device and download a more specialized Application (if you are connected to the internet).");
            	 layout.addView(text2);
            	 
            	 SharedPreferences mypref = getPreferences(MODE_PRIVATE);
            	 if(mypref.contains(IJacketApp.MAC_PREFERENCE_TAG)){
            		 	Log.d(LOG_TAG, "adding last jacket button");
            		 	preferredJackButton = new Button(IJacketScanActiv.this);
            		 	preferredJackButton.setText("Connect to last jacket");
            		 	preferredJackButton.setOnClickListener( new OnClickListener(){

            				public void onClick(View v) {
            					SharedPreferences mypref = getPreferences(MODE_PRIVATE);
            					String mac = mypref.getString(IJacketApp.MAC_PREFERENCE_TAG, "");
            					if(!mac.isEmpty())
            						connectToJacket(mac);
            					else
            						quickToastMessage("no prefered jacket found");
            				}
            	        	
            	        });
            	 }
            	 
            	 //ImageView image = new ImageView(IJacketScanActiv.this);
            	 //image.setImageResource(R.drawable.scan);
            	 //layout.addView(image);
             }
        });
    }
        
    private void disconnect(){
    	Log.d(LOG_TAG, "disconnect under IJacketScanActiv");
		//if(mService != null) mService.stopSelf();
    }

 
    
    private void setTitle(final String title) {
        runOnUiThread(new Runnable() {
            public void run() {
            	IJacketScanActiv.super.setTitle("OSNAP - " + title);
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
    	disconnect();
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == CUSTOM_REQUEST_QR_SCANNER) {
        	
        	//Successful scan
            if (resultCode == RESULT_OK) {
            	Log.d("LOG_TAG", "scan OK" );
                String macAddress = intent.getStringExtra("SCAN_RESULT");
                
                SharedPreferences mypref = getPreferences(MODE_PRIVATE);
                Editor e = mypref.edit();
                e.putString(IJacketApp.MAC_PREFERENCE_TAG, macAddress);
                e.commit();
                
                connectToJacket(macAddress);
            }
        }
    }

    private void connectToJacket(String mac){
    	Log.d("LOG_TAG", "sending intent to con manager" );
        Intent in = new Intent(this, IJackConnManager.class);
        in.putExtra("macAddress", mac);
        startService(in);
        bindService(in, mConnection, Context.BIND_AUTO_CREATE);
		quickToastMessage("going to connect soon");
    }
    
    
    /**
     * Class for interacting with the main interface of the service.
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
        	 Log.d("LOG_TAG", "binding done on IJacketScanActiv" );
        	IJackConnBinder binder = (IJackConnBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            mBound = false;
        }
    };
    
}

