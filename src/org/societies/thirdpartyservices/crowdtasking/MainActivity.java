package org.societies.thirdpartyservices.crowdtasking;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.societies.android.api.comms.IMethodCallback;
import org.societies.android.api.contentproviders.CSSContentProvider;
import org.societies.android.api.privacytrust.trust.ITrustClientCallback;
import org.societies.android.api.privacytrust.trust.TrustException;
import org.societies.android.remote.helper.TrustClientHelper;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.trust.model.TrustRelationshipBean;
import org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean;
import org.societies.api.schema.privacytrust.trust.model.TrustedEntityTypeBean;

import si.setcce.societies.android.rest.RestTask;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.provider.CalendarContract.Events;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JsResult;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

@SuppressLint("SimpleDateFormat")
public class MainActivity extends Activity implements SensorEventListener {
	private static final String TEST_ACTION = "si.setcce.societies.android.rest.TEST";
	private static final String CHECK_IN_OUT = "si.setcce.societies.android.rest.CHECK_IN_OUT";
	private static final String LOG_EVENT = "si.setcce.societies.android.rest.LOG_EVENT";
	private static final String GET_MEETING_ACTION = "si.setcce.societies.android.rest.meeting";
	private static final String APPLICATION_URL = "http://crowdtasking.appspot.com";
	//private static final String APPLICATION_URL = "http://192.168.1.102:8888";
	private static final String MEETING_URL = APPLICATION_URL + "/android/meeting/";
	private static final String MEETING_REST_API_URL = APPLICATION_URL + "/rest/meeting";
	private static final String SCAN_QR_URL = APPLICATION_URL + "/android/scanQR";
	private static final String PICK_TASK_URL = APPLICATION_URL + "/task/view?id=";
	private static final String EVENT_API_URL = APPLICATION_URL + "/rest/event";
	private static final String SHARE_CS_URL = APPLICATION_URL + "/android/shareCsUrl";
	private String startUrl;
	public String nfcUrl=null;
	private WebView webView;
	private ProgressDialog progress;
	private SensorManager sensorManager;
	private Sensor accelerometer;
	private float mAccel;			// acceleration apart from gravity
	private float mAccelCurrent;	// current acceleration including gravity
	private float mAccelLast;		// last acceleration including gravity
/*	//CIS MANAGER REMOTE
    private boolean servCisMgrRemoteConnected = false;
	private Messenger cisMgrMessenger = null;
	//CIS SUBSCRIBED REMOTE
	private boolean servCisSubscribeRemoteConnected = false;
	private Messenger cisSubscribeMessenger = null;
	//CIS DIRECTORY REMOTE
	private boolean servCisDirRemoteConnected = false;
	private Messenger cisDirMessenger = null;
	private static final String LOG_TAG = MainActivity.class.getName();*/
	
	public MainActivity() {
	}

	//@SuppressLint("SetJavaScriptEnabled")
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
		accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	    mAccel = 0.00f;
	    mAccelCurrent = SensorManager.GRAVITY_EARTH;
	    mAccelLast = SensorManager.GRAVITY_EARTH;

	    startUrl = APPLICATION_URL+"/menu";
        /*AccountManager am = AccountManager.get(this);
        Account[] accounts = am.getAccounts();
        for(Account ac: accounts)
        {
	        String acname=ac.name;
	        String actype = ac.type;
	        Log.d("accountInfo", acname + ":" + actype);
        }*/
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        
        webViewSetup();
        checkIntent(getIntent());
        CheckUpdateTask checkUpdateTask = new CheckUpdateTask(this);
        checkUpdateTask.execute();
        //long start = new Date().getTime();
        System.out.println("CheckTrust before");
        
        TrustTask task = new TrustTask(this);
		task.execute();

		//checkTrustClient();
        System.out.println("CheckTrust after");
	}
	
	private void checkTrustClient() {
		final CountDownLatch latch = new CountDownLatch(1);
		final RequestorBean requestor = new RequestorBean();
		requestor.setRequestorId("whatever.setcce.si");
		final TrustedEntityIdBean trustorId = new TrustedEntityIdBean();
		trustorId.setEntityId("user1.research.setcce.si");
		trustorId.setEntityType(TrustedEntityTypeBean.CSS);
		
		final TrustClientHelper helper = new TrustClientHelper(getApplicationContext());
		helper.setUpService(new IMethodCallback() {
			
			/*
			 * @see org.societies.android.api.comms.IMethodCallback#returnException(java.lang.String)
			 */
			@Override
			public void returnException(String exception) {
				
			}

			/*
			 * @see org.societies.android.api.comms.IMethodCallback#returnAction(java.lang.String)
			 */
			@Override
			public void returnAction(String result) {
				
			}
			
			@Override
			public void returnAction(boolean resultFlag) {
				
				helper.retrieveTrustRelationships(requestor, trustorId, 
						new ITrustClientCallback() {

					/*
					 * @see org.societies.android.api.privacytrust.trust.ITrustClientCallback#onAddedDirectTrustEvidence()
					 */
					@Override
					public void onAddedDirectTrustEvidence() {
						System.out.println("onAddedDirectTrustEvidence");
						// should not be called!
					}

					/*
					 * @see org.societies.android.api.privacytrust.trust.ITrustClientCallback#onException(org.societies.android.api.privacytrust.trust.TrustException)
					 */
					@Override
					public void onException(TrustException exception) {
						System.out.println("onException");
						exception.printStackTrace();
						// should not be called!
					}

					/*
					 * @see org.societies.android.api.privacytrust.trust.ITrustClientCallback#onRetrievedTrustRelationship(org.societies.api.schema.privacytrust.trust.model.TrustRelationshipBean)
					 */
					@Override
					public void onRetrievedTrustRelationship(
							TrustRelationshipBean trustRelationship) {
						System.out.println("onRetrievedTrustRelationship");
						// should not be called!
					}

					/*
					 * @see org.societies.android.api.privacytrust.trust.ITrustClientCallback#onRetrievedTrustRelationships(java.util.Set)
					 */
					@Override
					public void onRetrievedTrustRelationships(
							Set<TrustRelationshipBean> trustRelationships) {

						// success!
						for (final TrustRelationshipBean trustRelationship : trustRelationships) {
							System.out.println("TrustorId.entityId:"+trustRelationship.getTrustorId().getEntityId());
							System.out.println("TrustorId.entityType:"+trustRelationship.getTrustorId().getEntityType());
							System.out.println("TrusteeId.entityId:"+trustRelationship.getTrusteeId().getEntityId());
							System.out.println("TrusteeId.entityType:"+trustRelationship.getTrusteeId().getEntityType());
							
							/*if (TEST_TRUSTEE_ID.equals(trustRelationship.getTrusteeId().getEntityId())
									&& TrustedEntityTypeBean.CSS.equals(trustRelationship.getTrusteeId().getEntityType()))
									assertTrue(trustRelationship.getTrustValue() > TEST_TRUST_VALUE_THRESHOLD);*/
							System.out.println("TrustValueType:"+trustRelationship.getTrustValueType());
							System.out.println("TrustValue:"+trustRelationship.getTrustValue());
							
						}
						helper.tearDownService(new IMethodCallback() {

							/*
							 * @see org.societies.android.api.comms.IMethodCallback#returnException(java.lang.String)
							 */
							@Override
							public void returnException(String exception) {
								System.out.println("returnException");
							}

							/*
							 * @see org.societies.android.api.comms.IMethodCallback#returnAction(java.lang.String)
							 */
							@Override
							public void returnAction(String result) {
								System.out.println("returnAction");
							}

							/*
							 * @see org.societies.android.api.comms.IMethodCallback#returnAction(boolean)
							 */
							@Override
							public void returnAction(boolean resultFlag) {
								System.out.println("returnAction");
								latch.countDown();
							}
						});
					}

					@Override
					public void onRetrievedTrustValue(Double trustValue) {
						System.out.println("onRetrievedTrustValue");
						// should not be called!
					}
				});
			}
		});
		
		//latch.await(10000, TimeUnit.MILLISECONDS);
		
	}
/*
	private void checkCISManagerService() {
		Log.d(LOG_TAG, ">>>>>>>>checkCISManagerService start");
        //CREATE INTENT FOR CIS MANAGER
        Intent intentCisMgrRemote = new Intent(this.getApplicationContext(), CisManagerRemote.class);
        this.getApplicationContext().bindService(intentCisMgrRemote, remoteServiceCisManager, Context.BIND_AUTO_CREATE);
        //CREATE INTENT FOR CIS SUBSCRIBED
        Intent intentCisSubscribeRemote = new Intent(this.getApplicationContext(), CisManagerRemote.class);
        this.getApplicationContext().bindService(intentCisSubscribeRemote, remoteServiceCisSubscribe, Context.BIND_AUTO_CREATE);
        //CREATE INTENT FOR CIS DIRECTORY
        Intent intentCisDirRemote = new Intent(this.getApplicationContext(), CisManagerRemote.class);
        this.getApplicationContext().bindService(intentCisDirRemote, remoteServiceCisDir, Context.BIND_AUTO_CREATE);

        //REGISTER BROADCAST
        IntentFilter intentFilter = new IntentFilter() ;
        //CIS Manager
        intentFilter.addAction(ICisManager.CREATE_CIS);
        intentFilter.addAction(ICisManager.DELETE_CIS);
        intentFilter.addAction(ICisManager.GET_CIS_LIST);
        intentFilter.addAction(ICisManager.JOIN_CIS);
        //CIS Subscriber
        intentFilter.addAction(ICisSubscribed.GET_MEMBERS);
        intentFilter.addAction(ICisSubscribed.GET_ACTIVITY_FEED);
        intentFilter.addAction(ICisSubscribed.ADD_ACTIVITY);
        //CIS DIRECTORY
        intentFilter.addAction(ICisDirectory.FIND_ALL_CIS);
        intentFilter.addAction(ICisDirectory.FILTER_CIS);
        intentFilter.addAction(ICisDirectory.FIND_CIS_ID);

        //TEST THE SLM COMPONENT
        TestRemoteUtility task = new TestRemoteUtility(this);
        task.execute();
		Log.d(LOG_TAG, ">>>>>>>>checkCISManagerService end");
	}
	
	private class TestRemoteUtility extends AsyncTask<Void, Void, Void> {
    	
    	private Context context;
    	private final String CLIENT_PACKAGE = "org.societies.android.platform.cis.cistester";
    	
    	public TestRemoteUtility(Context context) {
    		this.context = context;
    	}
    	
    	protected Void doInBackground(Void... args) {
    		
    		for (int i=1; i<6; i++) {
        		Log.d(LOG_TAG, ">>>>>>>>ServiceUtilitiesRemote connected: " + servCisMgrRemoteConnected + "("+i+")");
	    		if (! servCisMgrRemoteConnected) {
		    		try {//	WAIT TILL SERVICE IS CONNECTED - OCCURS ASYNCHRONOUSLY
						Thread.currentThread();
						Thread.sleep(10 * 1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	    		}
	    		else break;
    		}
    		
    		if (servCisMgrRemoteConnected) {
	    		//TEST CIS DIRECTORY REMOTE
	    		try {
	    			//TEST: GET ALL CIS ADVERTISEMENTS FROM DIRECTORY
	    			Log.d(LOG_TAG, "GET ALL CIS ADVERTISEMENTS FROM DIRECTORY");
	    			String targetMethod = ICisDirectory.methodsArray[0]; // "findAllCisAdvertisementRecords(String client)"
	        		Message outMessage = Message.obtain(null, ServiceMethodTranslator.getMethodIndex(ICisDirectory.methodsArray, targetMethod), 0, 0);
	        		Bundle outBundle = new Bundle();
	        		//PARAMETERS
	        		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 0), CLIENT_PACKAGE);
	        		outMessage.setData(outBundle);	
	        		cisDirMessenger.send(outMessage);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
    		} else {
    			Log.d(LOG_TAG, "Still not connected to servCisMgrRemoteConnected");
    		}
 
    		return null;
    	}
    }
	
	private ServiceConnection remoteServiceCisManager = new ServiceConnection() {
		
		public void onServiceDisconnected(ComponentName name) {
			servCisMgrRemoteConnected = false;
        	Log.d(LOG_TAG, "Disconnecting from remoteServiceCisManager");
			
		}
		
		public void onServiceConnected(ComponentName name, IBinder service) {
			servCisMgrRemoteConnected = true;
			cisMgrMessenger = new Messenger(service);
	    	Log.d(LOG_TAG, "remoteServiceCisManager connected");
		}
	};

	private ServiceConnection remoteServiceCisSubscribe = new ServiceConnection() {
		
		public void onServiceDisconnected(ComponentName name) {
			servCisMgrRemoteConnected = false;
        	Log.d(LOG_TAG, "Disconnecting from remoteServiceCisManager");
		}
		
		public void onServiceConnected(ComponentName name, IBinder service) {
			servCisMgrRemoteConnected = true;
			cisMgrMessenger = new Messenger(service);
	    	Log.d(LOG_TAG, "remoteServiceCisManager connected");
		}
	};
	
	private ServiceConnection remoteServiceCisDir = new ServiceConnection() {
		
		public void onServiceDisconnected(ComponentName name) {
			servCisDirRemoteConnected = false;
        	Log.d(LOG_TAG, "Disconnecting from remoteServiceCisDir");
		}
		
		public void onServiceConnected(ComponentName name, IBinder service) {
			servCisDirRemoteConnected = true;
			cisDirMessenger = new Messenger(service);
	    	Log.d(LOG_TAG, "remoteServiceCisDir connected");
		}
	};
*/	
	@SuppressLint("SetJavaScriptEnabled")
	private void webViewSetup() {
		webView = (WebView) findViewById(R.id.webView);
		webView.setInitialScale(1);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(false);
        webView.setWebViewClient(new MyWebViewClient(this));
        webView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
        webView.addJavascriptInterface(new JSInterface(this, webView), "android");
        webView.setWebChromeClient(new WebChromeClient() {
			public boolean onConsoleMessage(ConsoleMessage cm) {
				Log.d("Crowd Tasking", cm.message() + 
						" -- From line " + cm.lineNumber() 
						+ " of " + cm.sourceId());
				return true;
			}

			@Override
			public boolean onJsAlert(WebView view, String url, String message,
					JsResult result) {
				Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
				toast.show();
				return false;
			}
    	});
        webView.loadUrl(startUrl);
	}

	private void checkIntent(Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_VIEW.equals(action)) {
        	startUrl = intent.getData().toString();
        }
        if ("android.nfc.action.NDEF_DISCOVERED".equals(action)) {
        	nfcUrl = intent.getData().toString();
        }		
    	if (nfcUrl != null) {
    		checkInOut(nfcUrl.replaceFirst("cs", "http"));
			nfcUrl = null;
    	}
	}
	
    @Override
	protected void onNewIntent(Intent intent) {
    	checkIntent(intent);
	}

	@Override
    public void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(TEST_ACTION));
        registerReceiver(receiver, new IntentFilter(GET_MEETING_ACTION));
        registerReceiver(receiver, new IntentFilter(CHECK_IN_OUT));
        registerReceiver(receiver, new IntentFilter("android.nfc.action.NDEF_DISCOVERED"));
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        /*progress = ProgressDialog.show(this, "Connecting", "Waiting For GAE...", true);
        
        try {
			HttpGet searchRequest = new HttpGet(new URI(APPLICATION_URL));
			RestTask task = new RestTask(this,TEST_ACTION,"");
			task.execute(searchRequest);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}*/
    	if (getIntent().getAction().equalsIgnoreCase(CHECK_IN_OUT)) {
    		String response = getIntent().getStringExtra(RestTask.HTTP_RESPONSE);
    		System.out.println(response);
			Toast toast = Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT);
			toast.show();
    	}
}
    
    @Override
    public void onPause() {
        super.onPause();
    	unregisterReceiver(receiver);
    	sensorManager.unregisterListener(this);
    }
  	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public void onBackPressed()
    {
        if(webView.canGoBack()) {
        	WebBackForwardList webBackForwardList = webView.copyBackForwardList();
        	//int i = webBackForwardList.getCurrentIndex();
        	String historyUrl = webBackForwardList.getItemAtIndex(webBackForwardList.getCurrentIndex()).getUrl();
        	if (historyUrl.equalsIgnoreCase(APPLICATION_URL+"/menu") ||
        			historyUrl.startsWith(APPLICATION_URL+"/login")) {
        		super.onBackPressed();
        		return;
        	}/*
        	if (historyUrl.equalsIgnoreCase(APPLICATION_URL+"/task/new#&ui-state=dialog")) {
        		// ignore back press from multi select because it clears all fields
        		//return;
        	}*/
        	if (historyUrl.equalsIgnoreCase(APPLICATION_URL+"/task/new") ||
        			historyUrl.startsWith(APPLICATION_URL+"/community/edit")) {
        		//webView.loadUrl("javascript:CrowdTaskingApp.cancelTask();");
    			Toast toast = Toast.makeText(getApplicationContext(), "Use Save or Cancel button.", Toast.LENGTH_SHORT);
    			toast.show();
        	}
        	else {
            	webView.goBack();
        	}
        }
        else {
            super.onBackPressed();
        }
    }    

	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.scanQRcode:
                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                integrator.initiateScan(IntentIntegrator.QR_CODE_TYPES);
                return true;

            case R.id.home:
            	webView.loadUrl(APPLICATION_URL);
            	// reload
            	//webView.loadUrl(webView.getUrl());
            	return true;
            
            case R.id.checkUpdate:
            	//refreshData();
            	/*Intent startIntent=new Intent(this.getApplicationContext(),RemoteControlActivity.class);
            	startActivity(startIntent);*/
                CheckUpdateTask checkUpdateTask = new CheckUpdateTask(this, true);
                checkUpdateTask.execute();
            	/*if(checkUpdate.isAlive()){
            		checkUpdate.interrupt(); 
            	}
            	checkUpdate = new CheckUpdate(true);
            	checkUpdate.start();*/
                return true;

            case R.id.logout:
            	CookieSyncManager.createInstance(this);
            	CookieManager cookieManager = CookieManager.getInstance();
            	cookieManager.removeAllCookie();
            	webView.loadUrl(APPLICATION_URL);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
	
	private void checkInOut(String url) {
    	/*if (resultQR.startsWith("http")) {
        	webView.loadUrl(resultQR);
    	}
    	if (resultQR.startsWith("cs:")) {
    		webView.loadUrl(resultQR.replaceFirst("cs", "http"));
    	}
		if (url.startsWith("cs:")) {
			url = url.replaceFirst("cs", "http");
		}*/
		
		HttpGet searchRequest;
		try {
			searchRequest = new HttpGet(new URI(url));
			RestTask task = new RestTask(getApplicationContext(), CHECK_IN_OUT, CookieManager.getInstance().getCookie("crowdtasking.appspot.com"));
			task.execute(searchRequest);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			Toast toast = Toast.makeText(getApplicationContext(), "Error: "+e.getMessage(), Toast.LENGTH_LONG);
			toast.show();
		}

	}

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
      IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
      if (result != null) {
        String contents = result.getContents();
        if (contents != null) {
        	if (contents.startsWith("http")) {
            	if (contents.startsWith(PICK_TASK_URL)) {
            		HttpPost eventRequest;
					try {
						String taskId = contents.substring(PICK_TASK_URL.length());
						eventRequest = new HttpPost(new URI(EVENT_API_URL));
						List<NameValuePair> parameters = new ArrayList<NameValuePair>();
						parameters.add(new BasicNameValuePair("taskId",taskId));
						eventRequest.setEntity(new UrlEncodedFormEntity(parameters));
	        			RestTask task = new RestTask(getApplicationContext(), LOG_EVENT, CookieManager.getInstance().getCookie("crowdtasking.appspot.com"));
	        			task.execute(eventRequest);
					} catch (URISyntaxException e) {
						Log.e("CT4A", "Can't log event: "+e.getMessage());
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						Log.e("CT4A", "Can't log event: "+e.getMessage());
					}
            	}
        		webView.loadUrl(contents);
	    	}
	    	if (contents.startsWith("cs:")) {
	    		checkInOut(contents.replaceFirst("cs", "http"));
	    	}
        	//Toast toast = Toast.makeText(getApplicationContext(), R.string.result_succeeded, Toast.LENGTH_SHORT);
        	//toast.show();
        } else {
        	Toast.makeText(getApplicationContext(), R.string.result_failed, Toast.LENGTH_SHORT).show();
        	Toast.makeText(getApplicationContext(), getString(R.string.result_failed_why), Toast.LENGTH_LONG).show();
        }
      }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
			if (progress != null) {
				progress.dismiss();
			}
        	if (intent.getAction().equalsIgnoreCase(TEST_ACTION)) {
            	//webView.loadUrl("javascript:window.location.replace('"+startUrl+"')");
        		if(!webView.canGoBack()) {
           			webView.loadUrl(APPLICATION_URL+"/menu");
        		}
            	if (nfcUrl != null) {
            		checkInOut(nfcUrl.replaceFirst("cs", "http"));
					nfcUrl = null;
            	}
        	}
        	if (intent.getAction().equalsIgnoreCase(GET_MEETING_ACTION)) {
        		String response = intent.getStringExtra(RestTask.HTTP_RESPONSE);
        		sendMeetingEvent(response);
        	}
        	if (intent.getAction().equalsIgnoreCase(CHECK_IN_OUT)) {
        		String response = intent.getStringExtra(RestTask.HTTP_RESPONSE);
        		if (!response.startsWith("Check")) {
        			response = "Please sign in first.";
        		}
        		System.out.println(response);
				Toast toast = Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT);
				toast.show();
        	}
        	if (intent.getAction().equalsIgnoreCase(LOG_EVENT)) {
        		String response = intent.getStringExtra(RestTask.HTTP_RESPONSE);
        		System.out.println(response);
				Toast toast = Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT);
				toast.show();
        	}
        }
    };

    private void sendMeetingEvent(String meetingJSON) {
        //String JSON_STRING = "{ \"id\":212001,\"subject\":\"nujen sestanek\",\"description\":\"Urgent!!!\",\"cs\":{\"id\":179001,\"name\":\"test\",\"urlMapping\":\"test\"},\"startTime\":\"Jan 31, 2013 12:00:02 PM\",\"endTime\":\"Jan 31, 2013 2:00:03 PM\",\"organizer\":\"Setcce Research\"}";
    	JSONObject meeting;
    	String space;
    	try {
			meeting = new JSONObject(meetingJSON);
			space = meeting.getJSONObject("cs").optString("name");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		//String description = meeting.optString("description");
    	
        
        Calendar beginTime = Calendar.getInstance();
        beginTime.setTime(parseDateString(meeting.optString("startTime").toString()));
		//beginTime.set(2013, 1, 15, 17, 00);
		Calendar endTime = Calendar.getInstance();
		endTime.setTime(parseDateString(meeting.optString("endTime").toString()));
		//endTime.set(2013, 1, 15, 17, 15);
		Intent intent = new Intent(Intent.ACTION_EDIT);
		intent.setType("vnd.android.cursor.item/event");
		intent.putExtra("title", meeting.optString("subject"));
		intent.putExtra("description", meeting.optString("description"));
		intent.putExtra(Events.EVENT_LOCATION, space);
		//intent.putExtra(Events.DESCRIPTION, meeting.optString("description"));
		//intent.putExtra(Events.DTSTART, beginTime.getTimeInMillis());
		//intent.putExtra(Events.DTEND, endTime.getTimeInMillis());
		intent.putExtra("beginTime", beginTime.getTimeInMillis());
		intent.putExtra("endTime", endTime.getTimeInMillis());
		//intent.putExtra("allDay", true);
		//intent.putExtra("rrule", "FREQ=YEARLY");
		
		/*intent.putExtra(Intent.EXTRA_EMAIL,
				"sjuresa@gmail.com");*/
//				"simon.juresa@setcce.si, helena.halas@setcce.si, jan.porekar@setcce.si");
		startActivity(intent);

		//startActivityForResult(intent, RESULT_OK );


		/*Intent intent = new Intent(Intent.ACTION_INSERT)
		.setData(Events.CONTENT_URI)
		.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
				beginTime.getTimeInMillis())
		.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
				endTime.getTimeInMillis())
		.putExtra(Events.TITLE, "SOCIETIES Crowd Tasking - evaluation - final steps")
		.putExtra(Events.DESCRIPTION, "Kako popraviti navodila + evaluacijo, ...")
		.putExtra(Events.EVENT_LOCATION, "Laboratorij za Am I in wisdom of the crowds")
		.putExtra(Events.AVAILABILITY, Events.AVAILABILITY_BUSY)
		.putExtra(Intent.EXTRA_EMAIL,
				"helena.halas@setcce.si, jan.porekar@setcce.si, simon.juresa@setcce.si");
		startActivity(intent);*/
    }

	private Date parseDateString(String dateString) {
		DateFormat formatter = new SimpleDateFormat("MMM d, yyyy HH:mm:ss a");
		Date datum;
		try {
			datum = formatter.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
			datum = new Date();
		}
		return datum;
	}
    
    public class JSInterface {
		private WebView mAppView;
		private Context context;
		//@JavascriptInterface for API 17
		public JSInterface(Context context, WebView appView) {
			this.context = context;
			this.mAppView = appView;
		}
		
		public void toast(String echo) {
			Toast toast = Toast.makeText(mAppView.getContext(), echo,
					Toast.LENGTH_SHORT);
			toast.show();
		}
		
		public void goBack() {
			mAppView.goBack();
		}
		
		public String loginData() {
			//final String JS_SETELEMENT = "javascript:document.getElementById('%s').value='%s'";

            /*String columns [] = {CSSContentProvider.CssRecord.CSS_RECORD_CSS_IDENTITY,
                    CSSContentProvider.CssRecord.CSS_RECORD_EMAILID,
                    CSSContentProvider.CssRecord.CSS_RECORD_FORENAME,
                    CSSContentProvider.CssRecord.CSS_RECORD_NAME};*/
    		JSONObject societiesUser = new JSONObject();
        	try {
        		//Cursor cursor = context.getContentResolver().query(CSSContentProvider.CssRecord.CONTENT_URI, columns, null, null, null);
        		Cursor cursor = context.getContentResolver().query(CSSContentProvider.CssRecord.CONTENT_URI, null, null, null, null);
    	    	cursor.moveToFirst();

    	    	/*toastIt(cursor.getCount());
    	    	toastIt(cursor.getColumnCount());
    	    	cursor.moveToFirst();
    	    	for (int i=0; i<cursor.getColumnCount(); i++) {
    	    		System.out.println(cursor.getColumnName(i)+": "+cursor.getString(i));
    	    	}*/

        		societiesUser.put("userId", cursor.getString(cursor.getColumnIndex(CSSContentProvider.CssRecord.CSS_RECORD_CSS_IDENTITY)));
        		societiesUser.put("name", cursor.getString(cursor.getColumnIndex(CSSContentProvider.CssRecord.CSS_RECORD_NAME)));
        		societiesUser.put("foreName", cursor.getString(cursor.getColumnIndex(CSSContentProvider.CssRecord.CSS_RECORD_FORENAME)));
        		societiesUser.put("email", cursor.getString(cursor.getColumnIndex(CSSContentProvider.CssRecord.CSS_RECORD_EMAILID)));
        		cursor.close();
        		societiesUser.put("status", "ok");
        		
        		/*webView.loadUrl(String.format(JS_SETELEMENT, "userId", cursor.getString(cursor.getColumnIndex(CSSContentProvider.CssRecord.CSS_RECORD_CSS_IDENTITY))));
    			webView.loadUrl(String.format(JS_SETELEMENT, "name", cursor.getString(cursor.getColumnIndex(CSSContentProvider.CssRecord.CSS_RECORD_NAME))));
    			webView.loadUrl(String.format(JS_SETELEMENT, "foreName", cursor.getString(cursor.getColumnIndex(CSSContentProvider.CssRecord.CSS_RECORD_FORENAME))));
    			webView.loadUrl(String.format(JS_SETELEMENT, "email", cursor.getString(cursor.getColumnIndex(CSSContentProvider.CssRecord.CSS_RECORD_EMAILID))));*/
    	    } catch (Exception e) {
    	        e.printStackTrace();
    	        return "";
    	    }
        	System.out.println(societiesUser.toString());
			return societiesUser.toString();
		}
		
		public void share(String spejs, String action) {
			if (spejs == null || "".equalsIgnoreCase(spejs)) {
				Toast toast = Toast.makeText(mAppView.getContext(), "Enter URL mapping", Toast.LENGTH_SHORT);
				toast.show();
				return;
			}
			if (action == null || "".equalsIgnoreCase(action) && !("PD".equalsIgnoreCase(action) || "CI".equalsIgnoreCase(action) || "CO".equalsIgnoreCase(action))) {
				return;
			}
			String schema="cs:";
			if ("PD".equalsIgnoreCase(action)) {
				schema="http:";
			}
			String text = schema+"//crowdtasking.appspot.com/cs/"+spejs;
			if ("CI".equalsIgnoreCase(action)) {
				text+="/enter";
			}
			if ("CO".equalsIgnoreCase(action)) {
				text+="/leave";
			}
			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
			sendIntent.putExtra(Intent.EXTRA_TEXT, text);
			sendIntent.setType("text/plain");
			startActivity(sendIntent);
		}
	}

    @Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent sensor) {
        float x = sensor.values[0];
        float y = sensor.values[1];
        float z = sensor.values[2];
        mAccelLast = mAccelCurrent;
        mAccelCurrent = (float) Math.sqrt((double) (x*x + y*y + z*z));
        float delta = mAccelCurrent - mAccelLast;
        mAccel = mAccel * 0.9f + delta; // perform low-cut filter
        if (mAccel > 10) {
        	mAccel = 0;
        	refreshData();
        }
	}

	private void refreshData() {
		webView.loadUrl("javascript:refreshOnShake()");
	}

	private class MyWebViewClient extends WebViewClient {
    	Activity parentActivity;
    	
		public MyWebViewClient(Activity activity) {
			parentActivity = activity;
		}
				
		@SuppressWarnings("unused")
		public void onRequestFocus(WebView view) {
			synchronized (this) {
				this.notify();
			}
		}

		private void addMeetingToCalendar(String url) {
    		String meetingID = url.substring(MEETING_URL.length());
    		System.out.println(meetingID);
    		try
            {
    			HttpGet searchRequest = new HttpGet(new URI(MEETING_REST_API_URL+"?id="+meetingID));
    			RestTask task = new RestTask(parentActivity, GET_MEETING_ACTION, CookieManager.getInstance().getCookie("crowdtasking.appspot.com"));
    			task.execute(searchRequest);
    		} catch (URISyntaxException e) {
    			e.printStackTrace();
    		}

		}
		
    	@Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
        	if (url.equalsIgnoreCase(SCAN_QR_URL)) {
                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                integrator.initiateScan(IntentIntegrator.QR_CODE_TYPES);
        		return true;
        	}
        	if (url.equalsIgnoreCase(SHARE_CS_URL)) {
                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                integrator.initiateScan(IntentIntegrator.QR_CODE_TYPES);
        		return true;
        	}
        	if (url.startsWith(MEETING_URL)) {
        		addMeetingToCalendar(url);
        		return true;
        	}
        	/*System.out.println(String.format("Url: %s",url));
        	if (url.contains("/rest/")) {
        		return false;
        	}
        	if (url.contains("?")) {
        		url += "&android=true";
        	}
        	else {
        		url += "?android=true";
        	}*/
            view.loadUrl(url);
            return false;
        }
        
        public void onPageFinished(WebView view, String url) {
        	if (url.contains("enter") || url.contains("leave")) {
        		webView.goBack();
        	}
    		if (!startUrl.equalsIgnoreCase(APPLICATION_URL+"/menu")) {
            	webView.loadUrl(startUrl);
            	startUrl = APPLICATION_URL+"/menu";
    		}
        	/*
        	if (url.contains("/menu")) {
        		//webView.loadUrl("javascript:$('#androidMenu').show()");
        		webView.loadUrl("javascript:window.document.getElementById(androidMenu).style.display = 'block';");
            	webView.loadUrl("javascript:window.location.alert('bu!')");
        	}*/
            //view.setInitialScale((int)(25*view.getScale()));
    		//webView.loadUrl("javascript:bu()");
       }
    }
}
