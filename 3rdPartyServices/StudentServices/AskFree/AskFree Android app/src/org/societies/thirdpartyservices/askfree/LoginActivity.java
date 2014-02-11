package org.societies.thirdpartyservices.askfree;

import org.societies.thirdpartyservices.askfree.R;
import org.societies.thirdpartyservices.askfree.tools.CheckUpdateTask;

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
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

	SessionManager session;


	private static final String LOGIN_URL ="http://54.218.113.176/askfree/login.php";

	//private static final int FIRST_OPTION = Menu.FIRST;


	//JSON element ids from repsonse of php script:
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";

	//Called when the Activity is first created
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//create a session object
		session = new SessionManager(getApplicationContext());
		if (session.isLoggedIn() == true){ 
			Intent intent = new Intent(this, MainActivity.class);
			this.startActivity (intent);
			this.finish();
		}else{

			//Assign Activities UI
			setContentView(R.layout.activity_login);

			//Get references to UI widgets
			username = (EditText)findViewById(R.id.usernameEditText);
			pass = (EditText)findViewById(R.id.passwordEditText);
			loginButton= (Button)findViewById(R.id.loginButton);
			register=(Button)findViewById(R.id.registerButton);
			status=(TextView)findViewById(R.id.loginStatus);

			// Initialize preferences
			PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

			String uname = getIntent().getStringExtra("username");

			if(uname != null){
				username.setText(uname);
				username.setKeyListener(null);
			}


			if(isOnline()){
				
				//check if there is an updated version of AskFree
				CheckUpdateTask checkUpdateTask= new CheckUpdateTask(this);
				checkUpdateTask.execute();
				
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
	}

	/* ************************ LOGIN TASK ************************************** */
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

					Intent i = new Intent(LoginActivity.this,MainActivity.class);
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
	
	/* ************************ CHECK INTERNET CONNECTIVITY ************************************** */
	public boolean isOnline() {
		ConnectivityManager cm =
				(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	/* ************************ OPTIONS MENU ************************************** */

	public boolean onOptionsItemSelected(MenuItem item){
		super.onOptionsItemSelected(item);

		//Find which menu item has been selected
		switch(item.getItemId()){
		//check for each known Menu Item
		case(0):
			//perform menu handler actions
			Intent intent = new Intent(LoginActivity.this,SettingsActivity.class);
			startActivity(intent);
			return true;
		//Return false if you have not handled the Menu Item
		default: return false;
		}
	}

	/* **************************************************************************************************** */

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		//Group ID
		int groupId=0;
		//Unique Menu Item identifier. Used for event handling
		int menuItemId = 0;
		//The order position of the item
		int menuItemOrder = Menu.NONE;
		//Text to be displayed for this menu item
		int menuItemText = R.string.settings;
		//Create the Menu Item and keep a reference to it
		MenuItem menuItem = menu.add(groupId,menuItemId,menuItemOrder,menuItemText);
		menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		return true;
	}

	public void testSettings(){
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		String SERVER_IP = sharedPref.getString("ipAddress", "");
		String SERVERPORT = sharedPref.getString("port","");
		Toast.makeText(this, SERVER_IP + " :" + SERVERPORT, Toast.LENGTH_LONG).show();
	}
}
