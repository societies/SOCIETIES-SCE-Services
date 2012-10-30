package org.societies.thirdpartyservices.ijacket;

import java.util.Timer;
import java.util.TimerTask;

import org.societies.thirdpartyservices.ijacket.com.BluetoothConnection;
import org.societies.thirdpartyservices.ijacket.com.ComLibException;
import org.societies.thirdpartyservices.ijacket.com.ConnectionListener;
import org.societies.thirdpartyservices.ijacket.com.ConnectionMetadata;
import org.societies.thirdpartyservices.ijacket.com.ConnectionMetadata.DefaultServices;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class IJackConnManager extends Service implements ConnectionListener {

	
	String macAddress = "";
	//Activity parentActivity = null;
	BluetoothConnection con = null;
	private static final String LOG_TAG = IJackConnManager.class.getName();
	static final int MAX_RETRY = 5;
	int retrycounter = 0;
	
	// Binder given to clients
    private final IBinder mBinder = new IJackConnBinder();
	
    public class IJackConnBinder  extends  Binder {
    	IJackConnManager getService() {
            // Return this instance of LocalService so clients can call public methods
            return IJackConnManager.this;
        }
    }

    
    
	@Override
	public int onStartCommand (Intent intent, int flags, int startId){
		Log.d(LOG_TAG, "IJackConnManager on start");

    	if (null == intent)return START_NOT_STICKY;
    	macAddress = intent.getStringExtra("macAddress");
    	IJacketApp appState = ((IJacketApp)getApplicationContext());
    	//Activity parentAct = appState.getCurrActiv();
		
    	if(null == macAddress) return START_NOT_STICKY;
    	
    	//check if connection already existis
    	if(null != con && con.isConnected()){
    		Log.d(LOG_TAG, "IJackConnManager on start bu connection already existed");
    		return START_REDELIVER_INTENT; 
    	}
    	
    	
    	if(false == btConnect())return START_NOT_STICKY;
    	else	return START_REDELIVER_INTENT; //success
	}
	
	@Override
	  public void onDestroy() {
		Log.d(LOG_TAG, "IJackConnManager destroyed");
		if(null != con) con.disconnect();
	}
	
	public boolean btConnect(){
		retrycounter++;
		try {
			Log.d(LOG_TAG, "creating bt connection");
			con = new BluetoothConnection(macAddress, this, this);
			con.connect();
			return true;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (ComLibException e) {
			e.printStackTrace();			
		}
		return false;
	}
	
	
    public void onConnect(BluetoothConnection bluetoothConnection) {
		//IJacketApp appState = ((IJacketApp)getApplicationContext());
		//appState.registerConnect();
    	Log.d(LOG_TAG, "Blueetooth onconnect");
    	//if(parentActivity.class != JacketMenuActivity)
    		Intent intent = new Intent(IJackConnManager.this, JacketMenuActivity.class);
    		
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
		//Toast.makeText(this, "connected", Toast.LENGTH_SHORT).show();
		Log.d(LOG_TAG, "starting the menu activity");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        retrycounter = 0;
        
    }

    public void onConnecting(BluetoothConnection bluetoothConnection) {
        //quickToastMessage("Connecting");
    }

    public void onDisconnect(BluetoothConnection bluetoothConnection) {

		if(null != con && con.isConnected()) return; // it is already connected
		
    	if(retrycounter < MAX_RETRY){
								
    		BtConnectTask bt = new BtConnectTask();
    		Timer myTimer = new Timer();
    		myTimer.schedule(bt, 10000);

    	}else{
    		Log.d(LOG_TAG, "retry counter exploded");
    		stopSelf();
    		retrycounter = 0; 
    	}
    }

    public BluetoothConnection getConnector(){
    	return con;
    }

    public boolean getConStats(){
    	if(null == con) return false;
    	else return con.isConnected();
    }
    
/*    public Activity getActivity(){
    	IJacketApp appState = ((IJacketApp)getApplicationContext());
    	return appState.getCurrActiv();
    }*/

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	class BtConnectTask extends TimerTask {
		  public void run() {
			  btConnect();

		  }
		}

}
