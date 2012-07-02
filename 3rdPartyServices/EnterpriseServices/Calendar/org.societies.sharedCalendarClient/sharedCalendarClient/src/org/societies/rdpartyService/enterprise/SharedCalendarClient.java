package org.societies.rdpartyService.enterprise;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jivesoftware.smack.packet.IQ;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.comm.xmpp.client.impl.ClientCommunicationMgr;
import org.societies.identity.IdentityManagerImpl;
import org.societies.rdpartyservice.enterprise.sharedcalendar.Calendar;
import org.societies.rdpartyservice.enterprise.sharedcalendar.MethodType;
import org.societies.rdpartyservice.enterprise.sharedcalendar.SharedCalendarBean;
import org.societies.rdpartyservice.enterprise.sharedcalendar.SharedCalendarResult;



import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class SharedCalendarClient extends Activity {
	private static final String LOG_TAG = SharedCalendarClient.class.getName();
	private static String TAG = "sharedCalendarClient";
	private static final List<String> NAMESPACES = Collections
			.unmodifiableList(Arrays
					.asList("http://societies.org/rdPartyService/enterprise/sharedCalendar"));
	private static final List<String> PACKAGES = Collections
			.unmodifiableList(Arrays
					.asList("org.societies.rdpartyservice.enterprise.sharedcalendar"));
	private static final List<String> ELEMENT_NAMES = Arrays.asList(
			"sharedCalendarBean", "sharedCalendarBeanResult");
	private static final String DESTINATION = "xcmanager.societies.local";
	private final IIdentity toXCManager;
	private final ICommCallback callback = createCallback();
	private ClientCommunicationMgr ccm;

	public SharedCalendarClient() {
		try {
			toXCManager = IdentityManagerImpl.staticfromJid(DESTINATION);
		} catch (InvalidFormatException e) {
			Log.e(LOG_TAG, e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Called when the activity is first created.
	 * 
	 * @param savedInstanceState
	 *            If the activity is being re-initialized after previously being
	 *            shut down then this Bundle contains the data it most recently
	 *            supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it
	 *            is null.</b>
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(LOG_TAG, "Activity created");
		setContentView(R.layout.main);
		ExampleTask task = new ExampleTask(this); 
        task.execute();
	}
	
	
	private class ExampleTask extends AsyncTask<Void, Void, Void> {

    	private Context context;
    	
    	public ExampleTask(Context context) {
    		this.context = context;
    	}

    	protected Void doInBackground(Void... args) {
    		ccm = new ClientCommunicationMgr(context);
    		
    		SharedCalendarBean messageBean = new SharedCalendarBean();
    		messageBean.setMethod(MethodType.RETRIEVE_CALENDAR_LIST);
    		

    		Stanza stanza = new Stanza(toXCManager);
    		

            try {
    			ccm.register(ELEMENT_NAMES, callback);
    			ccm.sendIQ(stanza, IQ.Type.GET, messageBean, callback);
    			Log.d(LOG_TAG, "Send stanza");
    		} catch (Exception e) {
    			Log.e(this.getClass().getName(), e.getMessage());
	        }
            return null;
    	}
    }
	
	private ICommCallback createCallback() {
    	return new ICommCallback() {
			
			public void receiveResult(Stanza stanza, Object payload) {
				Log.d(LOG_TAG, "receiveResult");
				Log.d(LOG_TAG, "Payload class of type: " + payload.getClass().getName());
				if (payload instanceof SharedCalendarResult) {
					SharedCalendarResult resultBean = (SharedCalendarResult) payload;
					List<Calendar> calendarList=resultBean.getCalendarList();
					for (Calendar calendar : calendarList) {
						Log.d(LOG_TAG, "calendar description: " +calendar.getDescription() );
					}
					
					
					
				}
				debugStanza(stanza);
				
			}
			
			
			
			public void receiveItems(Stanza stanza, String node, List<String> items) {
				Log.d(LOG_TAG, "receiveItems");
				debugStanza(stanza);
				Log.d(LOG_TAG, "node: "+node);
				Log.d(LOG_TAG, "items:");
				for(String  item:items)
					Log.d(LOG_TAG, item);
			}
			
			public void receiveError(Stanza stanza, XMPPError error) {
				Log.d(LOG_TAG, "receiveError");
			}

			public void receiveInfo(Stanza stanza, String node, XMPPInfo info) {
				Log.d(LOG_TAG, "receiveInfo");
			}

			public void receiveMessage(Stanza stanza, Object payload) {
				Log.d(LOG_TAG, "receiveMessage");
				debugStanza(stanza);
				
			}
			
			private void debugStanza(Stanza stanza) {
				Log.d(LOG_TAG, "id="+stanza.getId());
				Log.d(LOG_TAG, "from="+stanza.getFrom());
				Log.d(LOG_TAG, "to="+stanza.getTo());
			}
			
			public List<String> getXMLNamespaces() {
				return NAMESPACES;
			}
			
			public List<String> getJavaPackages() {
				return PACKAGES;
			}
		};
			
		

	}		
}
