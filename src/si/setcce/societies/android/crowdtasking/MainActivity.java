package si.setcce.societies.android.crowdtasking;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
//import android.annotation.SuppressLint;

public class MainActivity extends Activity {
	private WebView webView;
	
	//@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
		webView = (WebView) findViewById(R.id.webView1);
		webView.getSettings().setJavaScriptEnabled(true);
        webView.setInitialScale(1);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(false);
        webView.setWebViewClient(new MyWebViewClient());
        webView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
        webView.loadUrl("http://crowdtasking.appspot.com/mobile");
     }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public void onBackPressed()
    {
        if(webView.canGoBack())
            webView.goBack();
        else
            super.onBackPressed();
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
            	webView.loadUrl("http://crowdtasking.appspot.com/index.html");
                return true;
            
            case R.id.profile:
            	webView.loadUrl("http://crowdtasking.appspot.com/profile.html");
                return true;

            case R.id.logout:
            	webView.clearCache(true);
            	webView.loadUrl("http://crowdtasking.appspot.com/");
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
    
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
        
        public void onPageFinished(WebView view, String url) {
        	if (url.contains("enter") || url.contains("leave")) {
        		webView.goBack();
        	}
            view.setInitialScale((int)(25*view.getScale()));
        }
    }
}
