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
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
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
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

public class IJacketScanActiv extends Activity implements OnItemSelectedListener {

    private static final int CUSTOM_REQUEST_QR_SCANNER = 0;
    
    //private BluetoothConnection con;

    private TableLayout layout;
    private Button disconnectButton;
    private Button scanButton;
    ContentResolver cr;
    private Spinner spinnerCIS;
	
    private static final String LOG_TAG = IJacketScanActiv.class.getName();

	   /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "IJacketScanActiv onCreate");
        cr = this.getApplication().getContentResolver();
        

        
        
        
        
		IJacketApp appState = ((IJacketApp)getApplicationContext());
		BluetoothConnection con = appState.getCon();
		if(con == null || con.isConnected() == false){
			Log.d(LOG_TAG, "connection does not exist on IJacketScanActiv onCreate");  
	        layout = new TableLayout(this);
	        layout.setLayoutParams( new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.FILL_PARENT) );
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
		else{// connection already exist
			Log.d(LOG_TAG, "connection already exist on IJacketScanActiv onCreate");
			Intent intent = new Intent(IJacketScanActiv.this, JacketMenuActivity.class);
			startActivity(intent);
		}
    }
    

    
    private void addItemsOnCISSpinner(){
    		spinnerCIS = new Spinner(IJacketScanActiv.this);
    		spinnerCIS.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
    		
    		//List<String> SpinnerArray = new ArrayList<String>();	
    	        Log.d(LOG_TAG, "going to query the social provider");
    	       
    	       
    	        Uri COMUNITIES_URI = Uri.parse(SocialContract.AUTHORITY_STRING + SocialContract.UriPathIndex.COMMINITIES);
    	        try{
	    	       	 Cursor cursor = cr.query(COMUNITIES_URI,null,null,null,null);
	    	       	startManagingCursor(cursor);
	    	   		/*if (cursor != null && cursor.getCount() >0) {
	    	   			
	    	   		    while (cursor.moveToNext()) {
	    	   		    	int i  = cursor.getColumnIndex(SocialContract.Communities.NAME);
	    	   		    	String commName = cursor.getString(i);
	    	   		    	SpinnerArray.add(commName);
	    	   		        Log.d("LOG_TAG", "found community " + commName);
	    	   		    }
	    	   		} else {
	    	   			Log.d(LOG_TAG, "empty CIS list query result");
	    		   	}*/
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
            	 
            	 ImageView image = new ImageView(IJacketScanActiv.this);
            	 image.setImageResource(R.drawable.scan);
            	 layout.addView(image);
             }
        });
    }
        
    private void disconnect(){
		IJacketApp appState = ((IJacketApp)getApplicationContext());
		BluetoothConnection con = appState.getCon();
		if(con != null) con.disconnect();
    }

    
    private ConnectionListener getConnectionListener(Activity parentActivity) {
        return new myConList(parentActivity);
    }
    
    public class myConList implements ConnectionListener {

    	
    	private Activity parentActivity;
    	
    	public myConList(Activity parentActivity){
    		super();
    		this.parentActivity = parentActivity;
    		Log.d(LOG_TAG, "conlist created");
    	}
    	
        public void onConnect(BluetoothConnection bluetoothConnection) {
    		IJacketApp appState = ((IJacketApp)getApplicationContext());
    		appState.registerConnect();
        	Log.d(LOG_TAG, "Blueetooth onconnect");
            quickToastMessage("Connected! (" + bluetoothConnection.toString() + ")");
            clearUI();
            //addButton(disconnectButton);
            
            Intent intent = new Intent(IJacketScanActiv.this, JacketMenuActivity.class);
            
            //Add a button for every service found
            ConnectionMetadata meta = bluetoothConnection.getConnectionData();
			for(String service : meta.getServicesSupported()) {
				Integer pins[] = meta.getServicePins(service);
				
				//Pin controlled button
				if(pins.length > 0) {
					// ILL just add one button of each
					if(service.equals(DefaultServices.SERVICE_LED_LAMP.name())) intent.putExtra("LED PIN", pins[0]);  
					if(service.equals(DefaultServices.SERVICE_VIBRATION.name())) intent.putExtra("VIBRATION PIN", pins[0]);
					if(service.equals(DefaultServices.SERVICE_SPEAKER.name())) intent.putExtra("SPEAKER PIN", pins[0]);
				}
			}

            startActivity(intent);

			
            
        }

        public void onConnecting(BluetoothConnection bluetoothConnection) {
            quickToastMessage("Connecting");
        }

        public void onDisconnect(BluetoothConnection bluetoothConnection) {
    		IJacketApp appState = ((IJacketApp)getApplicationContext());

        	if(false == appState.isConectStatus() && false == appState.testMaxRetryCounter()){
        		
				quickToastMessage("Disconnected, going to retry with " + appState.getMacAddress());
				Log.d(LOG_TAG, "Disconnected, going to retry with " + appState.getMacAddress() + ", iteration " + appState.getRetrycounter());
				BluetoothConnection con = appState.getCon();
				
				if (null != con) 
					if(con.isConnected()) // double check if it is really disconnected
						appState.registerConnect();
						
				//while(false == appState.isConectStatus() && false == appState.testMaxRetryCounter()){
	            	try {
						con = new BluetoothConnection(appState.getMacAddress(), parentActivity, this);
						con.connect();
						appState.setCon(con);
						//appState.registerConnect();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ComLibException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            	appState.incRetryCounter();
	            	Log.d(LOG_TAG, "retry counter iterated");
				//}

        	}else{
                quickToastMessage("Disconnected for good");
                setTitle("Not connected");
                
        	}
        }
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
                
                // Handle successful scan
                try {
                	BluetoothConnection con = new BluetoothConnection(macAddress, this, getConnectionListener(this));
                	Log.d("LOG_TAG", "trying to connect" );
                	IJacketApp appState = ((IJacketApp)getApplicationContext());
                	appState.clearRetryCounter();
                	con.connect();
					
					appState.setCon(con);
					appState.setMacAddress(macAddress);
					
				} catch (Exception e) {
					Log.d("LOG_TAG", "exception trying to connect" );
					quickToastMessage(e.getMessage());
	            	resetUI();
				}
            } 
            
            //Failed scan
            else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
            	resetUI();
                quickToastMessage("Failed to scan!");
            }
        }
    }



	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		
		Cursor cursor = (Cursor) parent.getItemAtPosition(pos);
		int i = cursor.getColumnIndex(SocialContract.Communities.GLOBAL_ID); 
		String comJid = cursor.getString(i);
		i = cursor.getColumnIndex(SocialContract.Communities._ID); 
		String comLocalId = cursor.getString(i);
	    Log.d("LOG_TAG", "found community with JID " + comJid + " and id " + comLocalId);
	    
		IJacketApp appState = ((IJacketApp)getApplicationContext());
		appState.setSelectCommunityJid(comJid);
		appState.setSelectedCommunityLocalId(comLocalId);

		
	}



	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}

}

