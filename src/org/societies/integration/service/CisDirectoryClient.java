package org.societies.integration.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import org.societies.android.api.cis.directory.ICisDirectory;
import org.societies.android.api.cis.management.ICisManager;
import org.societies.android.api.services.ICoreSocietiesServices;

public class CisDirectoryClient extends ServiceClientBase {
	private final static String LOG_TAG = "CommunityManagementClient";
    private BroadcastReceiver receiver;

	public CisDirectoryClient(Context context, BroadcastReceiver receiver) {
		super(context);
        this.receiver = receiver;
        serviceName = "CisDirectoryClient";
	}

	@Override
	protected Intent getServiceIntent() {
		return new Intent(ICoreSocietiesServices.CIS_DIRECTORY_SERVICE_INTENT);
	}

	@Override
	protected IntentFilter createIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(ICisDirectory.FIND_ALL_CIS);
        intentFilter.addAction(ICisDirectory.FILTER_CIS);
        intentFilter.addAction(ICisDirectory.FIND_CIS_ID);
        return intentFilter;
	}

	@Override
	protected BroadcastReceiver getBroadcastReceiver() {
		return receiver;
	}

	public void findAllCisAdvertismentRecords() {
		//cisManager.getCisList(CLIENT, "all");
		if (this.connectedToContextClient) {
			android.os.Message outMessage = android.os.Message.obtain(null, 0, 0, 0);
			Bundle outBundle = new Bundle();
			outBundle.putString("client", context.getPackageName());
			//outBundle.putString("query", "all");
			outMessage.setData(outBundle);
			try {
				this.targetService.send(outMessage);
			} catch (RemoteException e) {
				Log.e(LOG_TAG, "Could not send remote method invocation", e);
			}
		}
	}

}
