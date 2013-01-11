package si.setcce.societies.android.crowdtasking;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;

import org.apache.http.client.methods.HttpGet;

import si.setcce.societies.android.rest.RestTask;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebBackForwardList;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
//import android.annotation.SuppressLint;

public class MainActivity extends Activity {
	private static final String TEST_ACTION = "si.setcce.societies.android.rest.TEST";
	private static final String GET_MEETING_ACTION = "si.setcce.societies.android.rest.meeting";
	private static final String APPLICATION_URL = "http://crowdtasking.appspot.com";
	private static final String MEETING_URL = "http://crowdtasking.appspot.com/android/meeting/";
	private static final String MEETING_REST_API_URL = "http://crowdtasking.appspot.com/rest/meeting";
	private static final String SCAN_QR_URL = "http://crowdtasking.appspot.com/android/scanQR";
	private WebView webView;
	
	//@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AccountManager am = AccountManager.get(this);
        Account[] accounts = am.getAccounts();
        for(Account ac: accounts)
        {
	        String acname=ac.name;
	        String actype = ac.type;
	        Log.d("accountInfo", acname + ":" + actype);
        }
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
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
        webView.loadUrl("file:///android_asset/start.html");
        //webView.loadUrl("file:///android_asset/test2.html");

        
        try {
			HttpGet searchRequest = new HttpGet(new URI(APPLICATION_URL));
			RestTask task = new RestTask(this,TEST_ACTION,"");
			task.execute(searchRequest);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
     }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(TEST_ACTION));
        registerReceiver(receiver, new IntentFilter(GET_MEETING_ACTION));
    }
    
    @Override
    public void onPause() {
        super.onPause();
    	unregisterReceiver(receiver);
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
        	String historyUrl = webBackForwardList.getItemAtIndex(webBackForwardList.getCurrentIndex()-1).getUrl();
        	if (historyUrl.contains("start.html")) {
        		super.onBackPressed();
        	}
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
				Calendar beginTime = Calendar.getInstance();
				beginTime.set(2012, 11, 29, 17, 00);
				Calendar endTime = Calendar.getInstance();
				endTime.set(2012, 11, 29, 17, 15);
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
				Intent intent = new Intent(Intent.ACTION_EDIT);
				intent.setType("vnd.android.cursor.item/event");
				/*intent.putExtra("beginTime", beginTime);
				intent.putExtra("allDay", true);
				intent.putExtra("rrule", "FREQ=YEARLY");
				intent.putExtra("endTime", endTime);*/
				intent.putExtra("title", "ICS test");
				intent.putExtra(Intent.EXTRA_EMAIL,
						"sjuresa@gmail.com");
//						"simon.juresa@setcce.si, helena.halas@setcce.si, jan.porekar@setcce.si");
				startActivity(intent);
				//startActivityForResult(intent, RESULT_OK );
				
            	return true;
            
            case R.id.profile:
            	//Intent startIntent=new Intent(this.getApplicationContext(),ProfileActivity.class);
            	Intent startIntent=new Intent(this.getApplicationContext(),SettingsActivity.class);
            	startActivity(startIntent);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
      IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
      if (result != null) {
        String contents = result.getContents();
        if (contents != null) {
        	//Toast toast = Toast.makeText(getApplicationContext(), R.string.result_succeeded, Toast.LENGTH_SHORT);
        	//toast.show();
        	String resultQR = result.getContents();
        	if (resultQR.startsWith("http")) {
            	webView.loadUrl(resultQR);
        	}
        	if (resultQR.startsWith("cs:")) {
        		webView.loadUrl(resultQR.replaceFirst("cs", "http"));
        	}
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
        	if (intent.getAction().equalsIgnoreCase(TEST_ACTION)) {
            	webView.loadUrl("javascript:window.location.replace('"+APPLICATION_URL+"/menu')");
        	}
        	if (intent.getAction().equalsIgnoreCase(GET_MEETING_ACTION)) {
        		String response = intent.getStringExtra(RestTask.HTTP_RESPONSE);
        		System.out.println(response);
        	}
        }
    };

	private class JSInterface {
		private WebView mAppView;
		public JSInterface(WebView appView) {
			this.mAppView = appView;
		}
		public void doEchoTest(String echo) {
			Toast toast = Toast.makeText(mAppView.getContext(), echo,
					Toast.LENGTH_SHORT);
			toast.show();
		}
	}
    
    
    private class MyWebViewClient extends WebViewClient {
    	Activity parentActivity;
    	
		@SuppressWarnings("unused")
		public void onRequestFocus(WebView view) {
			synchronized (this) {
				this.notify();
			}
		}

		public MyWebViewClient(Activity activity) {
			parentActivity = activity;
		}
				
		private void addMeetingToCalendar(String url) {
    		String meetingID = url.substring(MEETING_URL.length());
    		System.out.println(meetingID);
    		try
            {
    			HttpGet searchRequest = new HttpGet(new URI(MEETING_REST_API_URL+"?id="+meetingID));
    			RestTask task = new RestTask(parentActivity,GET_MEETING_ACTION,"");
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
        	}/*
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
