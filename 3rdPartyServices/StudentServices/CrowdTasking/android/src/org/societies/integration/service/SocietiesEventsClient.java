package org.societies.integration.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import org.societies.android.api.cis.directory.ICisDirectory;
import org.societies.android.api.css.manager.IServiceManager;
import org.societies.android.api.events.IAndroidSocietiesEvents;
import org.societies.android.api.services.ICoreSocietiesServices;

public class SocietiesEventsClient extends ServiceClientBase {
	private final static String LOG_TAG = "SocietiesEventsClient";
    private BroadcastReceiver receiver;
	String CONTEXT_MODIFIED_LOCATION_SYMBOLIC_INTENT = "org.societies.android.context.MODIFIED.locationSymbolic";
	String CONTEXT_MODIFIED_LOCATION_SYMBOLIC_EVENT = "org/societies/context/change/event/MODIFIED/locationSymbolic";

	public SocietiesEventsClient(Context context, BroadcastReceiver receiver) {
		super(context);
        this.receiver = receiver;
        serviceName = "SocietiesEventsClient";
	}

	@Override
	protected Intent getServiceIntent() {
		return new Intent(ICoreSocietiesServices.EVENTS_SERVICE_INTENT);
	}

	@Override
	protected IntentFilter createIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(IAndroidSocietiesEvents.SUBSCRIBE_TO_ALL_EVENTS);
        intentFilter.addAction(IAndroidSocietiesEvents.SUBSCRIBE_TO_EVENT);
        intentFilter.addAction(IAndroidSocietiesEvents.SUBSCRIBE_TO_EVENTS);
        intentFilter.addAction(IAndroidSocietiesEvents.UNSUBSCRIBE_FROM_ALL_EVENTS);
        intentFilter.addAction(IAndroidSocietiesEvents.UNSUBSCRIBE_FROM_EVENT);
        intentFilter.addAction(IAndroidSocietiesEvents.UNSUBSCRIBE_FROM_EVENTS);
        intentFilter.addAction(IAndroidSocietiesEvents.PUBLISH_EVENT);
        intentFilter.addAction(IAndroidSocietiesEvents.NUM_EVENT_LISTENERS);
        intentFilter.addAction(IServiceManager.INTENT_SERVICE_STOPPED_STATUS);
        intentFilter.addAction(IServiceManager.INTENT_SERVICE_STARTED_STATUS);
        intentFilter.addAction(CONTEXT_MODIFIED_LOCATION_SYMBOLIC_INTENT);
        intentFilter.addAction(CONTEXT_MODIFIED_LOCATION_SYMBOLIC_EVENT);
		//org.societies.android.platform.context.UPDATE
        return intentFilter;
	}

	@Override
	protected BroadcastReceiver getBroadcastReceiver() {
		return receiver;
	}

    public void subcribeToLocationChangedEvent() {

	    Bundle outBundle = getBundle();
	    outBundle.putString("societiesIntent", CONTEXT_MODIFIED_LOCATION_SYMBOLIC_INTENT);
        callMethod(0, outBundle);
    }

    public void test() {
//	    final String CONTEXT_MODIFIED_LOCATION_SYMBOLIC_EVENT = "org/societies/context/change/event/MODIFIED/locationSymbolic";

	    Bundle outBundle = getBundle();
	    outBundle.putString("intent", IAndroidSocietiesEvents.CSS_MANAGER_ADD_CSS_NODE_INTENT);
        callMethod(0, outBundle);
    }
}
