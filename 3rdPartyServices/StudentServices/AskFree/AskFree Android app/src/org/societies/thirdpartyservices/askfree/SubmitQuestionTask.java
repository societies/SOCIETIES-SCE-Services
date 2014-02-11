package org.societies.thirdpartyservices.askfree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

public class SubmitQuestionTask extends AsyncTask<String,String,String>{
	
	private Context mContext;

	public SubmitQuestionTask(Context context) {
		mContext = context;
	} 
	    
	ProgressDialog dialog = null;
	
	JSONParser jsonParser= new JSONParser();
	
	private static final String SUBMIT_URL ="http://54.218.113.176/askfree/submit_question.php";
	
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";
	
	@Override
	protected void onPreExecute(){
		super.onPreExecute();
		dialog = new ProgressDialog(mContext);
		dialog.setMessage("Sending question...");
		dialog.setIndeterminate(false);
		dialog.setCancelable(true);
		dialog.show();
	}
	
	@Override 
	protected String doInBackground(String...args){
		int success;
		
		String question = args[0];
		String s_id = args[1];
		String m_id = args[2];
		String location = args[3];
		
		try{
			List<NameValuePair>parameters = new ArrayList<NameValuePair>();
			parameters.add(new BasicNameValuePair("question",question));
			parameters.add(new BasicNameValuePair("s_id_email",s_id));
			parameters.add(new BasicNameValuePair("m_id",m_id));
			parameters.add(new BasicNameValuePair("location",location));
			
			Log.d("request", "starting");
			
			//getting submission details by making HTTP request
			JSONObject json = jsonParser.makeHttpRequest(SUBMIT_URL, parameters);
			
			//check your log for json response
			Log.d("Submit Question attempt", json.toString());
			
			//json success tag
			success = json.getInt(TAG_SUCCESS);
			if(success == 1){
				Log.d("Submission Successful!", json.toString());
				//Toast.makeText(mContext, "Submission Failure", Toast.LENGTH_LONG).show();
				return json.getString(TAG_MESSAGE);
			}
			else{
				Log.d("Submission Failure!", json.getString(TAG_MESSAGE));
				//Toast.makeText(mContext, "Submission Failure", Toast.LENGTH_LONG).show();
				return json.getString(TAG_MESSAGE);
			}
		}catch(JSONException e){
			e.printStackTrace();
		}
		return null;
	}
	
	protected void onPostExecute(String message){
		dialog.dismiss();
		/*if(message !=null){
			Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
		}*/
	}

}
