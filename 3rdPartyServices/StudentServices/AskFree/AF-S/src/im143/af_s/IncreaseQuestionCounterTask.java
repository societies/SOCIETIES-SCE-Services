package im143.af_s;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class IncreaseQuestionCounterTask extends AsyncTask<String,String,String>{
	
	private static final String UPDATE_COUNTER_URL ="http://54.218.113.176/askfree/updateQuestionCounter.php";
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";
	
	JSONParser jsonParser= new JSONParser();
	
	@Override 
	protected String doInBackground(String...args){
		int success;
		
		String q_id = args[0]; 
        
		try{
			List<NameValuePair>parameters = new ArrayList<NameValuePair>();
			parameters.add(new BasicNameValuePair("q_id",q_id));
			
			Log.d("update counter request", "starting");
			
			//getting submission details by making HTTP request
			JSONObject json = jsonParser.makeHttpRequest(UPDATE_COUNTER_URL, parameters);
			
			//check your log for json response
			Log.d("Update Counter attempt", json.toString());
			
			//json success tag
			success = json.getInt(TAG_SUCCESS);
			if(success == 1){
				Log.d("Updating Successful!", json.toString());
				System.out.println("Successful");
				return json.getString(TAG_MESSAGE);
			}
			else{
				Log.d("Updating Failure!", json.getString(TAG_MESSAGE));
				System.out.println("Unsuccessful");
				return json.getString(TAG_MESSAGE);
			}
		}catch(JSONException e){
			e.printStackTrace();
		}
		return null;
	}

}
