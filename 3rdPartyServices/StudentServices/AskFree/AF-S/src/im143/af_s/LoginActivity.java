package im143.af_s;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {

	private EditText username;
	private EditText pass;
	private Button loginButton;
	private Button register;
	private TextView status;
	
	HttpPost httppost;
	StringBuffer buffer;
	HttpResponse response;
	HttpClient httpclient;
	List<NameValuePair> nameValuePair; 
	ProgressDialog dialog = null;
	
	JSONParser jsonParser= new JSONParser();
	
	private static final String LOGIN_URL ="http://54.218.113.176/askfree/login.php";

		
	//JSON element ids from repsonse of php script:
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";

	//Called when the Activity is first created
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Assign Activities UI
		setContentView(R.layout.activity_login);
		
		//Get references to UI widgets
		username = (EditText)findViewById(R.id.usernameEditText);
		pass = (EditText)findViewById(R.id.passwordEditText);
		loginButton= (Button)findViewById(R.id.loginButton);
		register=(Button)findViewById(R.id.registerButton);
		status=(TextView)findViewById(R.id.loginStatus);
		
		if(isOnline()){
			loginButton.setOnClickListener(new OnClickListener() {
	            @Override
	            public void onClick(View v) {
	                    new LoginTask().execute();
	            }
	        });
			
			register.setOnClickListener(new OnClickListener() {
				 @Override
		            public void onClick(View v){
					 startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
				 }
			});
			
		}else{
			loginButton.setEnabled(false);
			register.setEnabled(false);
			Toast.makeText(getApplicationContext(), "Please Connect to the Internet...", Toast.LENGTH_LONG).show();
			status.setText("Status: No Internet Connection");
		}
	}

	
	private class LoginTask extends AsyncTask<String,String,String>{
		
		
		@Override
		protected void onPreExecute(){
			super.onPreExecute();
			dialog=new ProgressDialog(LoginActivity.this);
			dialog.setMessage("Logging In...");
			dialog.setIndeterminate(false);
			dialog.setCancelable(true);
			dialog.show();
		}
		
		@Override 
		protected String doInBackground(String...args){
			
			int success;
			String email = username.getText().toString().trim();
			String password = pass.getText().toString().trim();
			
			try{
				List<NameValuePair>parameters = new ArrayList<NameValuePair>();
				parameters.add(new BasicNameValuePair("email",email));
				parameters.add(new BasicNameValuePair("password",password));
				Log.d("request", "starting");
				
				//getting product details by making HTTP request
				JSONObject json = jsonParser.makeHttpRequest(LOGIN_URL, parameters);
				
				//check your log for json response
				Log.d("Login attempt", json.toString());
				
				//json success tag
				success = json.getInt(TAG_SUCCESS);
				if(success == 1){
					Log.d("Login Successful!", json.toString());
					
					//create login session
					SessionManager session=new SessionManager(getApplicationContext());
					session.createLoginSession(email, password);
					
					Intent i = new Intent(LoginActivity.this,BaseActivity.class);
					finish();
					startActivity(i);
					return json.getString(TAG_MESSAGE);
				}else{
					Log.d("Login Failure!", json.getString(TAG_MESSAGE));
					return json.getString(TAG_MESSAGE);
				}
			}catch(JSONException e){
				e.printStackTrace();
			}
			return null;
		}
		
		protected void onPostExecute(String file_url){
			dialog.dismiss();
			if(file_url !=null){
				Toast.makeText(LoginActivity.this, file_url, Toast.LENGTH_LONG).show();
			}
		}
		
	}
	
	public boolean isOnline() {
	    ConnectivityManager cm =
	        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	        return true;
	    }
	    return false;
	}

}
