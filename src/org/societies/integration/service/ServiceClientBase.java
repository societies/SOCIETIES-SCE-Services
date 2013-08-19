package org.societies.integration.service;

import org.societies.api.schema.identity.RequestorBean;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Messenger;
import android.util.Log;

public abstract class ServiceClientBase {
	private final static String LOG_TAG = "ServiceClientBase";
    protected static final String SERVICE_CONNECTED = "org.societies.integration.service.CONNECTED";
	protected boolean connectedToContextClient;
	protected Context context;
	protected Messenger targetService;
	BroadcastReceiver receiver;
	RequestorBean requestor;
    protected String serviceName = "base";

	public ServiceClientBase(Context context) {
		this.context = context;
	}

	abstract protected Intent getServiceIntent();
    abstract protected IntentFilter createIntentFilter();
	
	public boolean setUpService() {
		Log.d(LOG_TAG, "setUpService");
		if (!this.connectedToContextClient) {
			this.setupBroadcastReceiver();
        	context.bindService(getServiceIntent(), this.clientConnection, Context.BIND_AUTO_CREATE);
		}
		return false;
	}

	public boolean tearDownService() {
		Log.d(LOG_TAG, "tearDownService");
		if (this.connectedToContextClient) {
			this.teardownBroadcastReceiver();
			context.unbindService(this.clientConnection);
			Log.d(LOG_TAG, "tearDownService completed");
		}
		return false;
	}	

    private ServiceConnection clientConnection = new ServiceConnection() {

    	@Override
        public void onServiceDisconnected(ComponentName name) {
        	Log.d(LOG_TAG, "Disconnecting from Client service");
        	teardownBroadcastReceiver();
        	connectedToContextClient = false;
        }

    	@Override
        public void onServiceConnected(ComponentName name, IBinder service) {
        	Log.d(LOG_TAG, "Connecting to Client service");

        	connectedToContextClient = true;
        	//get a remote binder
        	targetService = new Messenger(service);
        	Log.d(LOG_TAG, "Target service " + name.getShortClassName() + " acquired: " + targetService.getClass().getName());
			Log.d(LOG_TAG, "Retrieve setup callback");
			//btnGetLocation.setEnabled(true);
            Intent intent = new Intent(SERVICE_CONNECTED);
            intent.putExtra("serviceName", serviceName);
            context.sendBroadcast(intent);
        }
    };
    
    abstract protected BroadcastReceiver getBroadcastReceiver();
    
    /**
     * Create a broadcast receiver
     * 
     * @return the created broadcast receiver
     */
    private BroadcastReceiver setupBroadcastReceiver() {
        Log.d(LOG_TAG, "Set up broadcast receiver");
        
        this.receiver = getBroadcastReceiver();
        context.registerReceiver(this.receiver, createIntentFilter());    
        Log.d(LOG_TAG, "Register broadcast receiver");

        return receiver;
    }
    /**
     * Unregister the broadcast receiver
     */
    private void teardownBroadcastReceiver() {
        Log.d(LOG_TAG, "Tear down broadcast receiver");
        context.unregisterReceiver(this.receiver);
    }
}
