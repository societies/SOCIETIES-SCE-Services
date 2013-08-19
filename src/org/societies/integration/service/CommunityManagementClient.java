package org.societies.integration.service;

import org.societies.android.api.cis.management.ICisManager;
import org.societies.android.api.services.ICoreSocietiesServices;
import org.societies.api.schema.cis.community.Community;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

public class CommunityManagementClient extends ServiceClientBase {
	private final static String LOG_TAG = "CommunityManagementClient";

	public CommunityManagementClient(Context context) {
		super(context);
        serviceName = "CommunityManagement";
	}

	@Override
	protected Intent getServiceIntent() {
		return new Intent(ICoreSocietiesServices.CIS_MANAGER_SERVICE_INTENT);
	}

	@Override
	protected IntentFilter createIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(ICisManager.CREATE_CIS);
        intentFilter.addAction(ICisManager.GET_CIS_LIST);
        intentFilter.addAction(ICisManager.JOIN_CIS);
        intentFilter.addAction(ICisManager.LEAVE_CIS);
        intentFilter.addAction(ICisManager.INTENT_NOTSTARTED_EXCEPTION);
        return intentFilter;
	}

	@Override
	protected BroadcastReceiver getBroadcastReceiver() {
		return new ClientReceiver();
	}

	private class ClientReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ICisManager.GET_CIS_LIST)) {
				Object[] objekti = intent.getParcelableArrayExtra(ICisManager.INTENT_RETURN_VALUE);
				Log.i(LOG_TAG,"package: "+intent.getPackage());
	        	for(Object objekt:objekti) {
	        		//Log.i(LOG_TAG, objekt.toString());
	        		Log.i(LOG_TAG, "objekt name: "+objekt.getClass().getName());
	        		Log.i(LOG_TAG, "objekt simple name: "+objekt.getClass().getSimpleName());
	        		Log.i(LOG_TAG, "objekt declared fields: "+objekt.getClass().getDeclaredFields());
	        		Community cis = (Community)objekt;
	        		Log.i(LOG_TAG, cis.getCommunityJid());
	        		Log.i(LOG_TAG, cis.getCommunityName());

	        	}
				Log.i(LOG_TAG, "on recieve: "+ICisManager.GET_CIS_LIST);
	        	/*Community[] listing = (Community[]) intent.getParcelableArrayExtra(ICisManager.INTENT_RETURN_VALUE);
	        	for(Community cis: listing) {
	        		Log.i(LOG_TAG, cis.getCommunityJid());
	        		Log.i(LOG_TAG, cis.getCommunityName());
	        	}*/
	        }
		}
	}
	
	public void listCommunities() {
		//cisManager.getCisList(CLIENT, "all");
		if (this.connectedToContextClient) {
			android.os.Message outMessage = android.os.Message.obtain(null, 2, 0, 0);
			Bundle outBundle = new Bundle();
			outBundle.putString("client", context.getPackageName());
			outBundle.putString("query", "all");
			outMessage.setData(outBundle);
			try {
				this.targetService.send(outMessage);
			} catch (RemoteException e) {
				Log.e(LOG_TAG, "Could not send remote method invocation", e);
			}
		}
	}

}
