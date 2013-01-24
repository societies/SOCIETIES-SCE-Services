package si.setcce.societies.android.crowdtasking;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.client.methods.HttpGet;
import org.json.JSONException;
import org.json.JSONObject;

import si.setcce.societies.android.rest.RestTask;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.provider.CalendarContract.Events;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
//import android.annotation.SuppressLint;

@SuppressLint("SimpleDateFormat")
public class MainActivity extends Activity implements SensorEventListener {
	private static final String TEST_ACTION = "si.setcce.societies.android.rest.TEST";
	private static final String CHECK_IN_OUT = "si.setcce.societies.android.rest.CHECK_IN_OUT";
	private static final String GET_MEETING_ACTION = "si.setcce.societies.android.rest.meeting";
	private static final String APPLICATION_URL = "http://crowdtasking.appspot.com";
	private static final String MEETING_URL = "http://crowdtasking.appspot.com/android/meeting/";
	private static final String MEETING_REST_API_URL = "http://crowdtasking.appspot.com/rest/meeting";
	private static final String SCAN_QR_URL = "http://crowdtasking.appspot.com/android/scanQR";
	private String startUrl;
	public String nfcUrl=null;
	private WebView webView;
	private ProgressDialog progress;
	private SensorManager sensorManager;
	private Sensor accelerometer;
	private float mAccel; // acceleration apart from gravity
	private float mAccelCurrent; // current acceleration including gravity
	private float mAccelLast; // last acceleration including gravity
	
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
        /*
        webView.setWebChromeClient(new WebChromeClient() {
			public boolean onConsoleMessage(ConsoleMessage cm) {
				Log.d("Crowd TAsking", cm.message() + 
						" -- From line " + cm.lineNumber() 
						+ " of " + cm.sourceId());
				return true;
			}
    	});*/
        
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
        webView.addJavascriptInterface(new JSInterface(webView), "android");
        //webView.loadData("<h1>Application is loading...</h1>", "text/html", "utf-8");
        //webView.loadUrl("file:///android_asset/start.html");
        //webView.loadUrl("file:///android_asset/test2.html");
        checkIntent(getIntent());
    }

	private void checkIntent(Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_VIEW.equals(action)) {
        	startUrl = intent.getData().toString();
        }
        if ("android.nfc.action.NDEF_DISCOVERED".equals(action)) {
        	nfcUrl = intent.getData().toString();
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

        progress = ProgressDialog.show(this, "Connecting", "Waiting For GAE...", true);
        
        try {
			HttpGet searchRequest = new HttpGet(new URI(APPLICATION_URL));
			RestTask task = new RestTask(this,TEST_ACTION,"");
			task.execute(searchRequest);
		} catch (URISyntaxException e) {
			e.printStackTrace();
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
        	//WebBackForwardList webBackForwardList = webView.copyBackForwardList();
        	//int i = webBackForwardList.getCurrentIndex();
        	/*String historyUrl = webBackForwardList.getItemAtIndex(webBackForwardList.getCurrentIndex()-1).getUrl();
        	if (historyUrl.contains("start.html")) {
        		super.onBackPressed();
        	}*/
        	webView.goBack();
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
            	return true;
            
            case R.id.remote:
            	refreshData();
            	//Intent startIntent=new Intent(this.getApplicationContext(),RemoteControlActivity.class);
            	//startActivity(startIntent);
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
        		webView.loadUrl(contents);
	    	}
	    	if (contents.startsWith("cs:")) {
	    		checkInOut(contents.replaceFirst("cs", "http"));
	    	}
        	//Toast toast = Toast.makeText(getApplicationContext(), R.string.result_succeeded, Toast.LENGTH_SHORT);
        	//toast.show();
        } else {
        	Toast toast = Toast.makeText(getApplicationContext(), R.string.result_failed, Toast.LENGTH_SHORT);
        	toast.show();
        	toast = Toast.makeText(getApplicationContext(), getString(R.string.result_failed_why), Toast.LENGTH_LONG);
        	toast.show();
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
		public JSInterface(WebView appView) {
			this.mAppView = appView;
		}
		
		public void toast(String echo) {
			Toast toast = Toast.makeText(mAppView.getContext(), echo,
					Toast.LENGTH_SHORT);
			toast.show();
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
