package org.societies.integration.service;

import org.societies.api.schema.identity.RequestorBean;
import org.societies.thirdpartyservices.crowdtasking.MainActivity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.os.RemoteException;
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
        requestor = new RequestorBean();
        requestor.setRequestorId(MainActivity.SERVICE_ID);
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
            try {
                context.unbindService(this.clientConnection);
            }
            catch (IllegalArgumentException e) {

            }
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
        Log.d(LOG_TAG, "Register broadcast receiver: "+receiver.getClass().getName());
        context.registerReceiver(this.receiver, createIntentFilter());

        return receiver;
    }
    /**
     * Unregister the broadcast receiver
     */
    private void teardownBroadcastReceiver() {
        Log.d(LOG_TAG, "Tear down broadcast receiver");
        try {
            context.unregisterReceiver(this.receiver);
        } catch (IllegalArgumentException e) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected Bundle getBundle() {
        Bundle outBundle = new Bundle();
        outBundle.putString("client", context.getPackageName());
        return outBundle;
    }

    protected void callMethod(int methodNumber, Bundle outBundle) {
        android.os.Message outMessage = android.os.Message.obtain(null, methodNumber, 0, 0);
        outMessage.setData(outBundle);
        try {
            this.targetService.send(outMessage);
        } catch (RemoteException e) {
            Log.e(LOG_TAG, "Could not send remote method invocation", e);
        }
    }

    public RequestorBean getRequestor() {
        return requestor;
    }
}
