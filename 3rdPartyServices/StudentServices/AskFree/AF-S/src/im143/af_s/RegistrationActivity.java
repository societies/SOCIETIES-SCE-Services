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

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegistrationActivity extends Activity {

	private Button register;
	private EditText email;
	private EditText password;
	private EditText fname;
	private EditText lname;
	
	ProgressDialog dialog = null;
	StringBuffer buffer;
	HttpResponse response;
	HttpPost httppost;
	HttpClient httpclient;
	List<NameValuePair> nameValuePair; 
	
	String _email;
	String _password;
	String _passwordRe;
	String _fname;
	String _lname;
	
	JSONParser jsonParser = new JSONParser();
	
	private static final String REGISTER_URL ="http://54.218.113.176/askfree/register.php";
	
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registration);
		
		email=(EditText)findViewById(R.id.emailRegEditText);
		password=(EditText)findViewById(R.id.passRegEditText);
		fname=(EditText)findViewById(R.id.fnameRegEditText);
		lname=(EditText)findViewById(R.id.lnameRegEditText);
		register=(Button)findViewById(R.id.registerRegButton);
			
		register.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	            	
            	new CreateUserTask().execute();
           		    
            }
        });
	}

/*	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.registration, menu);
		return true;
	}*/
	
	//Register a new User
	private class CreateUserTask extends AsyncTask<String,String,String>{
		
		boolean failure = false;
		
		@Override
		protected void onPreExecute(){
			super.onPreExecute();
			dialog = new ProgressDialog(RegistrationActivity.this);
			dialog.setMessage("Adding new user...");
			dialog.setIndeterminate(false);
			dialog.setCancelable(true);
			dialog.show();
		}
		
		@Override
		protected String doInBackground(String...args){
			int success;
			
			_email=email.getText().toString().trim();
        	_password=password.getText().toString().trim();
        	_fname=fname.getText().toString().trim();
        	_lname=lname.getText().toString().trim();
        	
        	try{
        		List<NameValuePair>parameters =new ArrayList<NameValuePair>();
        		parameters.add(new BasicNameValuePair("s_email",_email));
        		parameters.add(new BasicNameValuePair("password",_password));
        		parameters.add(new BasicNameValuePair("fname",_fname));
        		parameters.add(new BasicNameValuePair("lname",_lname));
        		
        		Log.d("request!","starting");
        		
        		//posting user data to script
        		JSONObject json=jsonParser.makeHttpRequest(REGISTER_URL, parameters);
        		
        		//full json response
        		Log.d("Registration attempt", json.toString());
        		
        		//json success elemet
        		success = json.getInt(TAG_SUCCESS);
        		if(success == 1){
        			Log.d("User added!", json.toString());
        			finish();
        			return json.getString(TAG_MESSAGE);
        		}else{
        			Log.d("Registration Failure!",json.getString(TAG_MESSAGE));
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
				Toast.makeText(RegistrationActivity.this, file_url, Toast.LENGTH_LONG).show();
			}
		}
	}

}
