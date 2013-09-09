package im143.af_s;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JSONParser {
	
	static JSONObject jObj = null;
	static String json = "";
	
	InputStream content;
	
	public JSONObject writeJSON(String value, String id) {

		JSONObject json  = new JSONObject();
		
		try {
			
			JSONObject jObj = new JSONObject();
			jObj.put("value", value);
			
			json.put(id, jObj);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}
	
	public JSONObject makeHttpRequest(String url, List<NameValuePair>parameters){
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost=new HttpPost(url);
			httpPost.setEntity(new UrlEncodedFormEntity(parameters));

			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			content=httpEntity.getContent();
		}
		catch(UnsupportedEncodingException e){
			e.printStackTrace();
		}catch(ClientProtocolException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}

		try{
			BufferedReader reader = new BufferedReader(new InputStreamReader(content,"iso-8859-1"),8);
			StringBuilder sb = new StringBuilder();
			String line=null;
			while((line = reader.readLine())!= null){
				sb.append(line + "\n");
			}
			content.close();
			json = sb.toString();
		}catch(Exception e){
			Log.e("Buffer error", "Error converting result "+e.toString());
		}

		//try parse the string to a json object
		try{
			jObj=new JSONObject(json);
		}catch(JSONException e){
			Log.e("JSON Parser", "Error parsing data "+e.toString());
		}

		return jObj;
	}

	public JSONObject getJSONObject(String URL){

		StringBuilder stringBuilder=new StringBuilder();
		HttpClient client=new DefaultHttpClient();
		HttpPost httpPost=new HttpPost(URL);

		try{
			HttpResponse response=client.execute(httpPost);
			StatusLine statusLine=response.getStatusLine();
			int statusCode=statusLine.getStatusCode();

			if(statusCode == 200){
				HttpEntity entity=response.getEntity();
				InputStream content=entity.getContent();
				BufferedReader reader=new BufferedReader(new InputStreamReader(content));
				String line=null;
				while((line=reader.readLine())!=null){
					stringBuilder.append(line + "\n");
				}
				content.close();
				json = stringBuilder.toString();
			}else{
				Log.e("JSON", "Failed to authenticate");
			}
		}catch(ClientProtocolException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}

		// try parse the string to a JSON object
		try {
			jObj = new JSONObject(json);
		} catch (JSONException e) {
			Log.e("JSON Parser", "Error parsing data " + e.toString());
		}
		return jObj;
	}
}
