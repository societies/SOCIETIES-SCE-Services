package org.societies.thirdpartyservices.crowdtasking;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.CalendarContract.Events;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.societies.android.api.cis.directory.ICisDirectory;
import org.societies.android.api.cis.management.ICisManager;
import org.societies.android.api.contentproviders.CSSContentProvider;
import org.societies.api.schema.cis.community.Community;
import org.societies.api.schema.cis.directory.CisAdvertisementRecord;
import org.societies.integration.model.SocietiesUser;
import org.societies.integration.service.CisDirectoryClient;
import org.societies.integration.service.CommunityManagementClient;
import org.societies.integration.service.ContextClient;
import org.societies.integration.service.SocietiesEventsClient;

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
import java.util.Timer;
import java.util.TimerTask;

import si.setcce.societies.android.rest.RestTask;
import si.setcce.societies.crowdtasking.api.RESTful.json.CommunityJS;

@SuppressLint("SimpleDateFormat")
public class MainActivity extends Activity implements SensorEventListener {
	//private static final String TEST_ACTION = "si.setcce.societies.android.rest.TEST";
    private static final String CHECK_IN_OUT = "si.setcce.societies.android.rest.CHECK_IN_OUT";
    private static final String GET_USER = "si.setcce.societies.android.rest.GET_USER";
	private static final String LOG_EVENT = "si.setcce.societies.android.rest.LOG_EVENT";
	private static final String LOGIN_USER = "si.setcce.societies.android.rest.LOGIN_USER";
	private static final String C4CSS_INTENT = "si.setcce.societies.android.rest.C4CSS";
	private static final String GET_MEETING_ACTION = "si.setcce.societies.android.rest.MEETING";
	private static final String TAKE_CONTROL = "si.setcce.societies.android.rest.remote.TAKE_CONTROL";
    private static final String SCHEME ="http";
//    private static final String DOMAIN = "crowdtaskingtest.appspot.com";
//    private static final String DOMAIN = "crowdtasking.appspot.com";
   	private static final String DOMAIN = "192.168.1.102";
//   	private static final String DOMAIN = "192.168.1.66";
//   	private static final String DOMAIN = "192.168.1.78";
//    private static final String PORT = "";
//    private static final String PORT = ":80";
    private static final String PORT = ":8888";
    private static final String APPLICATION_URL = SCHEME +"://" + DOMAIN + PORT;
    private static final String LOGIN_URL = APPLICATION_URL + "/login";
    private static final String MEETING_URL = APPLICATION_URL + "/android/meeting/";
    private static final String C4CSS_API_URL = APPLICATION_URL + "/rest/community/4CSS";
    private static final String PD_TAKE_CONTROL = APPLICATION_URL + "/rest/remote/takeControl";
    //    private static final String GET_USER_REST_API_URL = APPLICATION_URL + "/rest/users/me";
	private static final String MEETING_REST_API_URL = APPLICATION_URL + "/rest/meeting";
    private static final String SCAN_QR_URL = APPLICATION_URL + "/android/scanQR";
    private static final String PICK_TASK_URL = APPLICATION_URL + "/task/view?id=";
    private static final String EVENT_API_URL = APPLICATION_URL + "/rest/event";
    private static final String SHARE_CS_URL = APPLICATION_URL + "/android/shareCsUrl";
    private static final String SERVICE_CONNECTED = "org.societies.integration.service.CONNECTED";
    private String startUrl;
	public String nfcUrl=null;
	private WebView webView;
	private ProgressDialog progress;
	private SensorManager sensorManager;
	private Sensor accelerometer;
	private float mAccel;			// acceleration apart from gravity
	private float mAccelCurrent;	// current acceleration including gravity
	private float mAccelLast;		// last acceleration including gravity
    private CommunityManagementClient communityManagementClient;
    private CisDirectoryClient cisDirectoryClient;
    private ContextClient contextClient;
    private SocietiesEventsClient societiesEventsClient;
    private boolean isSocietiesUser=false, societiesServicesRunning, contextClientRunning, communityManagementClientConnected, cisDirectoryClientConnected, societiesEventsClientConnected;
    private SocietiesUser societiesUser = null;
    private final static String LOG_TAG = "Crowd Tasking";

    int checkedItem = 0;
    JSONArray communitiesForPublicDisplay;
    public MainActivity() {
	}

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
        if (isSocietiesServiceRunning()) {
            System.out.println("Societies services are running?");
            Toast.makeText(getApplicationContext(), "SOCIETIES services are running", Toast.LENGTH_LONG).show();
            societiesUser = getSocietiesUserData();
            TrustTask task = new TrustTask(this, DOMAIN, APPLICATION_URL);
    		task.execute(societiesUser.getUserId());
            societiesServicesRunning = true;
            communityManagementClient = new CommunityManagementClient(this, communityClientReceiver);
            communityManagementClient.setUpService();
            checkLocation();
//            contextClient = new ContextClient(this);
//            contextClient.setUpService();
//            societiesEventsClient = new SocietiesEventsClient(this, eventsClientReceiver);
//            societiesEventsClient.setUpService();
//            cisDirectoryClient = new CisDirectoryClient(this, cisDirectoryClientReceiver);
//            cisDirectoryClient.setUpService();
        }
        else {
            System.out.println("Societies services are not running?");
            Toast.makeText(getApplicationContext(), "SOCIETIES services are not running", Toast.LENGTH_LONG).show();
        }
        loginUser();
        checkIntent(getIntent());
        CheckUpdateTask checkUpdateTask = new CheckUpdateTask(this);
        checkUpdateTask.execute();
    }

    private void checkLocation() {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            if (contextClientRunning) {
                                contextClient.getSymbolicLocation(societiesUser.getUserId());
                            } else {
                                contextClient = new ContextClient(getApplicationContext());
                                contextClient.setUpService();
                            }
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 10000); //execute in every 10000 ms
    }

/*
    private void checkSession() {
        try {
            RestTask task = new RestTask(getApplicationContext(), GET_USER, CookieManager.getInstance().getCookie("crowdtasking.appspot.com"));
            task.execute(new HttpGet(new URI(GET_USER_REST_API_URL)));
        } catch (URISyntaxException e) {
            e.printStackTrace();
            Toast toast = Toast.makeText(getApplicationContext(), "Error: "+e.getMessage(), Toast.LENGTH_LONG);
            toast.show();
        }
    }
*/

    private boolean isSocietiesServiceRunning() {
        boolean trust=false;
        boolean context=false;
        boolean cis=false;

	    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        System.out.println("Are Societies services running?");
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	    	//System.out.println(service.service.getClassName());
            if ("org.societies.android.privacytrust.trust.TrustClientRemote".equals(service.service.getClassName())) {
                System.out.println("Trust client service is running");
                trust = true;
            }
            if ("org.societies.android.platform.context.ServiceContextBrokerRemote".equals(service.service.getClassName())) {
                System.out.println("Context client service is running");
                context = true;
            }
            if ("org.societies.android.platform.cis.CisManagerRemote".equals(service.service.getClassName())) {
                System.out.println("CisManager client service is running");
                cis = true;
            }
	    }
	    return trust && context && cis;
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	private void webViewSetup() {
		webView = (WebView) findViewById(R.id.webView);

/*
        CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
*/

		webView.setInitialScale(1);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(false);
        webView.setWebViewClient(new MyWebViewClient(this));
        webView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
        webView.addJavascriptInterface(new JSInterface(webView), "android");
        webView.setWebChromeClient(new WebChromeClient() {
			public boolean onConsoleMessage(ConsoleMessage cm) {
				Log.i(LOG_TAG, cm.message() +
						" -- From the line " + cm.lineNumber()
						+ " of " + cm.sourceId());
				return true;
			}

			@Override
			public boolean onJsAlert(WebView view, String url, String message,
					JsResult result) {
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
				return false;
			}
    	});
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
        registerReceiver(receiver, new IntentFilter(C4CSS_INTENT));
        registerReceiver(receiver, new IntentFilter(GET_MEETING_ACTION));
        registerReceiver(receiver, new IntentFilter(CHECK_IN_OUT));
        registerReceiver(receiver, new IntentFilter(GET_USER));
        registerReceiver(receiver, new IntentFilter(LOGIN_USER));
        registerReceiver(receiver, new IntentFilter(TAKE_CONTROL));
        registerReceiver(receiver, new IntentFilter("android.nfc.action.NDEF_DISCOVERED"));
        registerReceiver(receiver, new IntentFilter(SERVICE_CONNECTED));
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

    	if (getIntent().getAction().equalsIgnoreCase(CHECK_IN_OUT)) {
    		String[] response = getIntent().getStringArrayExtra(RestTask.HTTP_RESPONSE);
    		System.out.println(response[1]);
			Toast toast = Toast.makeText(getApplicationContext(), response[1], Toast.LENGTH_SHORT);
			toast.show();
    	}
/*
        SocietiesUser societiesUser = getSocietiesUserData();
        JSONObject societiesUserJSON = new JSONObject();
        try {
            societiesUserJSON.put("userId", societiesUser.getUserId());
            societiesUserJSON.put("name", societiesUser.getName());
            societiesUserJSON.put("foreName", societiesUser.getForeName());
            societiesUserJSON.put("email", societiesUser.getEmail());
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
*/
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
        	}
        	if (historyUrl.equalsIgnoreCase(APPLICATION_URL+"/task/new") ||
        			historyUrl.startsWith(APPLICATION_URL+"/community/edit")) {
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
		try {
			RestTask task = new RestTask(getApplicationContext(), CHECK_IN_OUT, CookieManager.getInstance().getCookie(DOMAIN), DOMAIN);
			task.execute(new HttpGet(new URI(url)));
		} catch (URISyntaxException e) {
			e.printStackTrace();
			Toast toast = Toast.makeText(getApplicationContext(), "Error: "+e.getMessage(), Toast.LENGTH_LONG);
			toast.show();
		}
	}

    private void loginUser() {
//        HttpPost eventRequest;
//        try {
//            eventRequest = new HttpPost(new URI(LOGIN_URL));
        if (societiesServicesRunning) {
            SocietiesUser user = getSocietiesUserData();
            if (user != null) {
/*
                List<NameValuePair> parameters = new ArrayList<NameValuePair>();
                parameters.add(new BasicNameValuePair("federatedIdentity","SOCIETIES"));
                parameters.add(new BasicNameValuePair("userId", user.getUserId()));
                parameters.add(new BasicNameValuePair("name", user.getName()));
                parameters.add(new BasicNameValuePair("foreName", user.getForeName()));
                parameters.add(new BasicNameValuePair("email", user.getEmail()));
                parameters.add(new BasicNameValuePair("continue", "/"));
*/
                // TODO: fix this!!
                startUrl = LOGIN_URL+"?continue=/&federatedIdentity=SOCIETIES&userId="+user.getUserId()+
                        "&name="+user.getName()+"&foreName="+user.getForeName()+"&email="+user.getEmail();
            }
        }
//            eventRequest.setEntity(new UrlEncodedFormEntity(parameters));
//            RestTask task = new RestTask(getApplicationContext(), LOGIN_USER, null);
//            task.execute(eventRequest);
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        webView.loadUrl(startUrl);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
      IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
      if (result != null) {
        String contents = result.getContents();
        if (contents != null) {
            // TODO: leave this?
            Toast.makeText(getApplicationContext(), R.string.result_succeeded, Toast.LENGTH_SHORT).show();
        	if (contents.startsWith("http")) {
            	if (contents.startsWith(PICK_TASK_URL)) {
            		HttpPost eventRequest;
					try {
						String taskId = contents.substring(PICK_TASK_URL.length());
						eventRequest = new HttpPost(new URI(EVENT_API_URL));
						List<NameValuePair> parameters = new ArrayList<NameValuePair>();
						parameters.add(new BasicNameValuePair("taskId",taskId));
						eventRequest.setEntity(new UrlEncodedFormEntity(parameters));
	        			RestTask task = new RestTask(getApplicationContext(), LOG_EVENT, CookieManager.getInstance().getCookie(DOMAIN), DOMAIN);
	        			task.execute(eventRequest);
					} catch (URISyntaxException e) {
						Log.e(LOG_TAG, "Can't log event: "+e.getMessage());
					} catch (UnsupportedEncodingException e) {
						Log.e(LOG_TAG, "Can't log event: "+e.getMessage());
					}
            	}
        		webView.loadUrl(contents);
	    	}
	    	if (contents.startsWith("cs:")) {
	    		checkInOut(contents.replaceFirst("cs", "http"));
	    	}
	    	if (contents.startsWith("channel:")) {
                try {
                    RestTask task = new RestTask(getApplicationContext(), TAKE_CONTROL, CookieManager.getInstance().getCookie(DOMAIN), DOMAIN);
                    String url = PD_TAKE_CONTROL+"?channelId="+contents.substring("channel:".length());
                    task.execute(new HttpGet(new URI(url)));
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                    Toast toast = Toast.makeText(getApplicationContext(), "Error: "+e.getMessage(), Toast.LENGTH_LONG);
                    toast.show();
                }
	    	}
        } else {
        	Toast.makeText(getApplicationContext(), R.string.result_failed, Toast.LENGTH_SHORT).show();
        	Toast.makeText(getApplicationContext(), getString(R.string.result_failed_why), Toast.LENGTH_LONG).show();
        }
      }
    }

    private BroadcastReceiver cisDirectoryClientReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ICisDirectory.FIND_ALL_CIS)) {
                Parcelable[] objects = (Parcelable[])intent.getParcelableArrayExtra(ICisDirectory.INTENT_RETURN_VALUE);
                for(Parcelable object: objects) {
                    CisAdvertisementRecord advert = (CisAdvertisementRecord) object;
                    Log.i(LOG_TAG, advert.getId());
                    Log.i(LOG_TAG, advert.getName());
                }

                //Object[] advertismentRecords = intent.getParcelableArrayExtra(ICisDirectory.INTENT_RETURN_VALUE);
            }
        }
    };

    private BroadcastReceiver eventsClientReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("intent.getAction():"+intent.getAction());
            System.out.println("intent:"+intent);
        }
    };

    private BroadcastReceiver communityClientReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ICisManager.GET_CIS_LIST)) {
                Object[] communities = intent.getParcelableArrayExtra(ICisManager.INTENT_RETURN_VALUE);
                Log.i(LOG_TAG,"package: "+intent.getPackage());
                List<CommunityJS> societiesCommunities = new ArrayList<CommunityJS>();
                for(Object community:communities) {
                    societiesCommunities.add(new CommunityJS((Community)community, getSocietiesUserData()));
                }
                ((CrowdTasking)getApplication()).setSocietiesCommunities(societiesCommunities);
                getCommunitiesForUserFromGAE();
            }
        }
    };

    private void getCommunitiesForUserFromGAE() {
        try {
            RestTask task = new RestTask(getApplicationContext(), C4CSS_INTENT, CookieManager.getInstance().getCookie(DOMAIN), DOMAIN);
            task.execute(new HttpGet(new URI(C4CSS_API_URL)));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
			if (progress != null) {
				progress.dismiss();
			}
/*
            if (intent.getAction().equalsIgnoreCase(GET_USER)) {
                String response = intent.getStringExtra(RestTask.HTTP_RESPONSE);
                if ("\"Not authorized!\"".equalsIgnoreCase(response)) {
                    // user is not logged in
                    Toast.makeText(getApplicationContext(), "Logging in user", Toast.LENGTH_SHORT).show();
                    loginUser();
                }
                else {
                    webView.loadUrl(startUrl);
                }
            }
            if (intent.getAction().equalsIgnoreCase(LOGIN_USER)) {
                String kuki = intent.getStringExtra("cookie");
                CookieManager.getInstance().setCookie("crowdtasking.appspot.com", kuki);
                checkSession();
            }
*/
            if (intent.getAction().equalsIgnoreCase(SERVICE_CONNECTED)) {
                String response = intent.getStringExtra("serviceName");
                if (response.equalsIgnoreCase("CommunityManagement")) {
                    communityManagementClientConnected = true;
                    communityManagementClient.listCommunities();
                }
                if (response.equalsIgnoreCase("ContextClient")) {
                    contextClientRunning = true;
                    contextClient.getSymbolicLocation(societiesUser.getUserId()); // todo: dokončaj
                }
                if (response.equalsIgnoreCase("CisDirectoryClient")) {
                    cisDirectoryClientConnected = true;
                    cisDirectoryClient.findAllCisAdvertismentRecords();
                }
                if (response.equalsIgnoreCase("SocietiesEventsClient")) {
                    societiesEventsClientConnected = true;
                    societiesEventsClient.subcribeToAllEvents();
                }
                System.out.println(response);
            }
            if (intent.getAction().equalsIgnoreCase(C4CSS_INTENT)) {
                String[] response = intent.getStringArrayExtra(RestTask.HTTP_RESPONSE);
                ((CrowdTasking)getApplication()).setSpaces(response[1]);
            }
            if (intent.getAction().equalsIgnoreCase(GET_MEETING_ACTION)) {
                String[] response = intent.getStringArrayExtra(RestTask.HTTP_RESPONSE);
                sendMeetingEvent(response[1]);
            }
        	if (intent.getAction().equalsIgnoreCase(CHECK_IN_OUT)) {
                String[] response = intent.getStringArrayExtra(RestTask.HTTP_RESPONSE);
        		if (!response[1].startsWith("Check") && !response[1].startsWith("You are")) {
                    response[1] = "Please sign in first.";
        		}
        		System.out.println(response);
                Toast.makeText(getApplicationContext(), response[1], Toast.LENGTH_SHORT).show();
        	}
        	if (intent.getAction().equalsIgnoreCase(TAKE_CONTROL)) {
//        		String response = intent.getStringExtra(RestTask.HTTP_RESPONSE);
        		String[] response = intent.getStringArrayExtra(RestTask.HTTP_RESPONSE);
                if ("HTTP/1.1 409 Conflict".equalsIgnoreCase(response[0])) {
                    selectCommunityForPD(response);
                } else {
                    Toast.makeText(getApplicationContext(), response[1], Toast.LENGTH_LONG).show();
                }
        	}
        	if (intent.getAction().equalsIgnoreCase(LOG_EVENT)) {
                String[] response = intent.getStringArrayExtra(RestTask.HTTP_RESPONSE);
        		System.out.println(response);
				Toast.makeText(getApplicationContext(), response[1], Toast.LENGTH_SHORT).show();
        	}
        }
    };

    private void selectCommunityForPD(String[] response) {
        try {
            final String[] channelId = { "" };
            JSONArray responseJSON = new JSONArray(response[1]);
            communitiesForPublicDisplay = responseJSON.getJSONArray(0);
            channelId[0] = responseJSON.getString(1);
            String[] communityNames = new String[communitiesForPublicDisplay.length()];

            for (int i = 0; i < communitiesForPublicDisplay.length(); i++) {
                communityNames[i] = communitiesForPublicDisplay.getJSONObject(i).getString("name");
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // Set the dialog title
            builder.setTitle("Select community")
                    // Specify the list array, the items to be selected by default (null for none),
                    // and the listener through which to receive callbacks when items are selected
                    .setSingleChoiceItems(communityNames, checkedItem,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    checkedItem = which;
                                }
                            })
                            // Set the action buttons
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            showCommunity(channelId[0]);
                        }
                    });

            builder.show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showCommunity(String channelId) {
        try {
            Long id = communitiesForPublicDisplay.getJSONObject(checkedItem).getLong("id");
            String name = communitiesForPublicDisplay.getJSONObject(checkedItem).getString("name");
//            Toast.makeText(getApplicationContext(), checkedItem+". community selected. Id="+id+", name="+name, Toast.LENGTH_LONG).show();
            String url = PD_TAKE_CONTROL+"?channelId="+channelId+"&communityId="+communitiesForPublicDisplay.getJSONObject(checkedItem).getString("id");
            try {
                RestTask task = new RestTask(getApplicationContext(), TAKE_CONTROL, CookieManager.getInstance().getCookie(DOMAIN), DOMAIN);
                task.execute(new HttpGet(new URI(url)));
            } catch (URISyntaxException e) {
                e.printStackTrace();
                Toast toast = Toast.makeText(getApplicationContext(), "Error: "+e.getMessage(), Toast.LENGTH_LONG);
                toast.show();
            }
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Exception: "+e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void sendMeetingEvent(String meetingJSON) {
        //String JSON_STRING = "{ \"id\":212001,\"subject\":\"nujen sestanek\",\"description\":\"Urgent!!!\",\"cs\":{\"id\":179001,\"name\":\"test\",\"urlMapping\":\"test\"},\"startTime\":\"Jan 31, 2013 12:00:02 PM\",\"endTime\":\"Jan 31, 2013 2:00:03 PM\",\"organizer\":\"Setcce Research\"}";
    	JSONObject meeting;
    	String space;
    	try {
			meeting = new JSONObject(meetingJSON);
			space = meeting.getJSONObject("cs").optString("name");
		} catch (JSONException e) {
			e.printStackTrace();
			return;
		}
		//String description = meeting.optString("description");
    	
        
        Calendar beginTime = Calendar.getInstance();
        beginTime.setTime(parseDateString(meeting.optString("startTime")));
		//beginTime.set(2013, 1, 15, 17, 00);
		Calendar endTime = Calendar.getInstance();
		endTime.setTime(parseDateString(meeting.optString("endTime")));
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

    public SocietiesUser getSocietiesUserData() {
            /*String columns [] = {CSSContentProvider.CssRecord.CSS_RECORD_CSS_IDENTITY,
                    CSSContentProvider.CssRecord.CSS_RECORD_EMAILID,
                    CSSContentProvider.CssRecord.CSS_RECORD_FORENAME,
                    CSSContentProvider.CssRecord.CSS_RECORD_NAME};*/
        if (societiesUser != null) {
            return societiesUser;
        }
        societiesUser = new SocietiesUser();
        try {
            //Cursor cursor = context.getContentResolver().query(CSSContentProvider.CssRecord.CONTENT_URI, columns, null, null, null);
            Cursor cursor = getContentResolver().query(CSSContentProvider.CssRecord.CONTENT_URI, null, null, null, null);
            cursor.moveToFirst();

            societiesUser.setUserId(cursor.getString(cursor.getColumnIndex(CSSContentProvider.CssRecord.CSS_RECORD_CSS_IDENTITY)));
            societiesUser.setName(cursor.getString(cursor.getColumnIndex(CSSContentProvider.CssRecord.CSS_RECORD_NAME)));
            societiesUser.setForeName(cursor.getString(cursor.getColumnIndex(CSSContentProvider.CssRecord.CSS_RECORD_FORENAME)));
            societiesUser.setEmail(cursor.getString(cursor.getColumnIndex(CSSContentProvider.CssRecord.CSS_RECORD_EMAILID)));
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        isSocietiesUser = societiesServicesRunning;
        return societiesUser;
    }

    public class JSInterface {
		private WebView mWebView;
		//@JavascriptInterface for API 17
		public JSInterface(WebView appView) {
			this.mWebView = appView;
		}

        @JavascriptInterface
		public void toast(String echo) {
			Toast toast = Toast.makeText(mWebView.getContext(), echo,
                    Toast.LENGTH_SHORT);
			toast.show();
		}

        @JavascriptInterface
		public void goBack() {
			mWebView.goBack();
		}

        @JavascriptInterface
        public boolean isSocietiesUser() {
            Log.i("isSocietiesUser()", "isSocietiesUser: " + isSocietiesUser);
            return isSocietiesUser;
        }

        @JavascriptInterface
        public String socUser() {
            Log.i("isSocietiesUser()", "isSocietiesUser: " + isSocietiesUser);
            return Boolean.valueOf(isSocietiesUser).toString();
        }

        @JavascriptInterface
		public String getSocietiesUser() {
            SocietiesUser societiesUser = getSocietiesUserData();
            JSONObject societiesUserJSON = new JSONObject();
            try {
                societiesUserJSON.put("userId", societiesUser.getUserId());
                societiesUserJSON.put("name", societiesUser.getName());
                societiesUserJSON.put("foreName", societiesUser.getForeName());
                societiesUserJSON.put("email", societiesUser.getEmail());
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
            return societiesUserJSON.toString();
        }

        @JavascriptInterface
        public String getSocietiesCommunities() {
            communityManagementClient.listCommunities();
            return ((CrowdTasking)getApplication()).getSocietiesCommunitiesJSON();
        }

        @JavascriptInterface
        public void setCommunitySpaces(String community) {
            ((CrowdTasking)getApplication()).setCommunitySpaces(community);
        }

        @JavascriptInterface
        public void refreshCommunities() {
            getCommunitiesForUserFromGAE();
        }

        @JavascriptInterface
        public String getSocietiesCommunitiesByJids(String[] jids) {
            return ((CrowdTasking)getApplication()).getSocietiesCommunitiesByJids(jids);
        }

        @JavascriptInterface
        public void share(String spejs, String action) {
            if (spejs == null || "".equalsIgnoreCase(spejs)) {
				Toast toast = Toast.makeText(mWebView.getContext(), "Enter URL mapping", Toast.LENGTH_SHORT);
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
			String text = APPLICATION_URL+"/cs/"+spejs;
//			String text = schema+"//crowdtasking.appspot.com/cs/"+spejs;
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
        if (mAccel > 5) {
            Toast.makeText(getApplicationContext(), "shake detected ("+mAccel+")", Toast.LENGTH_SHORT).show();
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
    			RestTask task = new RestTask(parentActivity, GET_MEETING_ACTION, CookieManager.getInstance().getCookie(DOMAIN), DOMAIN);
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
            view.loadUrl(url);
            return false;
        }
        
        public void onPageFinished(WebView view, String url) {
        	if (url.contains("enter") || url.contains("leave")) {
        		webView.goBack();
        	}
/*
    		if (!startUrl.equalsIgnoreCase(APPLICATION_URL+"/menu")) {
            	webView.loadUrl(startUrl);
            	startUrl = APPLICATION_URL+"/menu";
    		}
*/
       }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (communityManagementClientConnected) {
            communityManagementClient.tearDownService();
        }
        if (cisDirectoryClientConnected) {
            cisDirectoryClient.tearDownService();
        }
        if (societiesEventsClientConnected) {
            societiesEventsClient.tearDownService();
        }
    }
}