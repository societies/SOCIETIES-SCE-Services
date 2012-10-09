

package com.ibm.hrl.ms.pz;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
 
// ----------------------------------------------------------------------
 
//@SuppressLint("NewApi")
@TargetApi(9)
public class CameraPreview extends Activity {

	CustomCameraView cv;
	TimerTask scanTask;
	boolean scanTaskActive=false;
	
	final Context context = this;
	
	private String DEFAULT_COLOR = "red";
	private int DEFAULT_TEXT_SIZE = 30;
	private int DEFAULT_NUM_MSG = 1;
	private int DEFAULT_MSG_UPDATE_INTERVAL = 3000;
	
	private String color = DEFAULT_COLOR;
	private int textSize = DEFAULT_TEXT_SIZE;
	private int numberOfMsgToDisplay = DEFAULT_NUM_MSG;
	private Typeface textFont;
	
	private int lastProcessedMsgId=0;
	
	private LinkedList<JSONObject> messagesInClient = new LinkedList<JSONObject>();
	private HashSet<Integer> messageIdsInClient = new HashSet<Integer>();
	private HashSet<String> messagesZonesIds = new HashSet<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	try{
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(R.layout.main);
		
		messagesZonesIds = new HashSet<String>();
		
		initialUserPrefVars();
		
		textFont = Typeface.createFromAsset(getAssets(),"fonts/COMIC.TTF");
			
		createTextViewsMsgs(numberOfMsgToDisplay);
		
		cv = (CustomCameraView) findViewById(R.id.surfaceView);
		
		ImageButton settings =(ImageButton) findViewById(R.id.settings);
		
		//add button listener
		settings.setOnClickListener(new OnClickListener() {
			  public void onClick(View arg0) {
				  createPreferencesDialog();
			  }
		});
		
		ImageButton sprayFrame =(ImageButton) findViewById(R.id.sprayFrame);
		sprayFrame.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ImageButton sendNow =(ImageButton) findViewById(R.id.sendNow);
				EditText editText = (EditText) findViewById(R.id.postMsgText);
				
				if (sendNow.getVisibility() == ImageButton.INVISIBLE){
					sendNow.setVisibility(ImageButton.VISIBLE);
					editText.setVisibility(EditText.VISIBLE);
				}else{
					sendNow.setVisibility(ImageButton.INVISIBLE);
					editText.setVisibility(EditText.INVISIBLE);
				}
			}
		});
		
		ImageButton sendNow =(ImageButton) findViewById(R.id.sendNow);
		sendNow.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
				EditText editText = (EditText) findViewById(R.id.postMsgText);
				String sprayText = editText.getText().toString();
				if (sprayText.trim().length() > 0){
					postMessages(sprayText);
				}
				ImageButton sendNow =(ImageButton) findViewById(R.id.sendNow);
				//sendNow.setVisibility(ImageButton.INVISIBLE);
				//editText.setVisibility(EditText.INVISIBLE);
				editText.setText("");	
				
				//hide keyboard
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
				imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
				setTopGravityMsgContainer();
			}
		});
		
		getMessagesTask();
		
	
	}catch (Exception e) {
		 Log.e("error", e.getMessage()+ " ; cause: "+e.getCause(), e);
	}
	}
	
	
	private void initialUserPrefVars() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		color = preferences.getString("vg.textColor",color);
		textSize = preferences.getInt("vg.textSize",textSize);
		numberOfMsgToDisplay = preferences.getInt("vg.numberOfMsgToDisplay",numberOfMsgToDisplay);
	}

	private synchronized void getMessagesTask() {
		if (scanTaskActive){
			return;
		}
		
		final Handler handler = new Handler();
		Timer t = new Timer();
		scanTask = new TimerTask() {
	        public void run() {
	                handler.post(new Runnable() {
	                        public void run() {
	                        	try{
	                        		getMessages();
	                        	}catch (Exception e) {
	                        		 Log.e("error", e.getMessage()+ " ; cause: "+e.getCause(), e);
								}
	                        }
	               });
	        }};
		
	        t.scheduleAtFixedRate(scanTask, 300,DEFAULT_MSG_UPDATE_INTERVAL);

	   synchronized (this) {
		   scanTaskActive = true;
	   }
	}


	private synchronized void createTextViewsMsgs(int number){
		
		LinearLayout messagesContainer = (LinearLayout) findViewById(R.id.messagesContainer);
		messagesContainer.removeAllViewsInLayout();
		TextView textView;
		for (int i=0; i < number ; i++){
			textView = new TextView(context);
			textView.setTypeface(textFont);
			textView.setVisibility(TextView.INVISIBLE);
			messagesContainer.addView(textView);
		}
		messagesContainer.bringToFront();
		displayText();
	}
	
	/*
	private void hideMessagesContainer(){
		LinearLayout messagesLayoutContainer = (LinearLayout) findViewById(R.id.messagesContainer);
		for (int i=0; i < messagesLayoutContainer.getChildCount() ; i++){
			TextView view = (TextView)messagesLayoutContainer.getChildAt(i);
			view.setVisibility(View.INVISIBLE);
		}
	}*/
	
	
	private synchronized void setTopGravityMsgContainer(){
		LinearLayout messagesLayoutContainer = (LinearLayout) findViewById(R.id.messagesContainer);
		messagesLayoutContainer.setGravity(Gravity.TOP);
	}
	
	
	public synchronized void displayText() {
		LinearLayout messagesLayoutContainer = (LinearLayout) findViewById(R.id.messagesContainer);
		setTopGravityMsgContainer();
		
		int iterateOver;
		JSONObject singleMessage;
		try{
			iterateOver = Math.min(messagesInClient.size(),numberOfMsgToDisplay);
			Iterator<JSONObject> iterator = messagesInClient.descendingIterator();
			
			int index=0;
			String delimiter = "\\.";
			while (iterator.hasNext() && index < iterateOver){
				singleMessage = iterator.next();
				TextView tv =(TextView) messagesLayoutContainer.getChildAt(messagesLayoutContainer.getChildCount()-index-1);
				
				String userId = (String)singleMessage.get("userId");
				String[] splitedUserId = userId.split(delimiter);
				
				tv.setText(splitedUserId[0]+": "+singleMessage.get("msg"));
				try{
					tv.setTextColor(Color.parseColor((String)singleMessage.get("style")));
				}catch (Exception e) {
					tv.setTextColor(Color.BLACK);
					Log.e("error", "can't parse color '"+(String)singleMessage.get("style")+"' ; error msg "+e.getMessage(), e);
				}
				tv.setTextSize(textSize);
				tv.setVisibility(View.VISIBLE);
				//Integer currentMsgId =  (Integer) singleMessage.get("messageId");
				//lastProcessedMsgId = Math.max(lastProcessedMsgId, currentMsgId);
				index++;
			}
			
			while (index < messagesLayoutContainer.getChildCount()){
				TextView tv =(TextView) messagesLayoutContainer.getChildAt(messagesLayoutContainer.getChildCount()-index-1);
				tv.setVisibility(View.GONE);
				index++;
			}
			
		}catch (JSONException e) {
			Log.e("error", "JSONException ; msg= "+e.getMessage(), e);
		}
		messagesLayoutContainer.bringToFront();
		
	}
	
	
	private void dialogOnClickAction(Dialog dialog){
		
		boolean paramsHaveChanged = false;
		String tempParamStr;
		int tempParamInt;
		try{
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
			
			EditText textColorEdit = (EditText) dialog.findViewById(R.id.textColor);
			tempParamStr = textColorEdit.getText().toString();
			if (tempParamStr != null && tempParamStr != null &&!tempParamStr.equals(color)){
				color = textColorEdit.getText().toString();
				paramsHaveChanged = true;
			}
			
			EditText textSizeEdit = (EditText) dialog.findViewById(R.id.textSize);
			tempParamInt =Integer.parseInt(textSizeEdit.getText().toString()); 
			if ( tempParamInt != textSize && tempParamInt > 0 ){
				textSize = tempParamInt;
				paramsHaveChanged = true;
			}
				
			EditText numMsgEdit = (EditText) dialog.findViewById(R.id.numOfMsg);
			tempParamInt =  Integer.parseInt(numMsgEdit.getText().toString());
			if (tempParamInt !=  numberOfMsgToDisplay && numberOfMsgToDisplay > 0){
				numberOfMsgToDisplay = tempParamInt;
				paramsHaveChanged = true;
			}
			
			if (paramsHaveChanged){
				createTextViewsMsgs(numberOfMsgToDisplay);
				
				SharedPreferences.Editor editor = preferences.edit();
				editor.putString("vg.textColor", color);
				editor.putInt("vg.numberOfMsgToDisplay", numberOfMsgToDisplay);
				editor.putInt("vg.textSize", textSize);
				editor.commit();
			}
				
		}catch (Exception e) {
			 Log.e("error", e.getMessage()+ " ; cause: "+e.getCause(), e);
		}

		dialog.dismiss();
		setTopGravityMsgContainer();
	}
	
	private void createPreferencesDialog(){
		// custom dialog
		final Dialog dialog = new Dialog(context);
		dialog.setContentView(R.layout.dialog);
		dialog.setTitle("Preferences");
					
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		 
		EditText textColorEdit = (EditText) dialog.findViewById(R.id.textColor);
		color = preferences.getString("vg.textColor",color);
		textColorEdit.setText(color);
				
		EditText textSizeEdit = (EditText) dialog.findViewById(R.id.textSize);
		textSize = preferences.getInt("vg.textSize",textSize);
		textSizeEdit.setText(String.valueOf(textSize));
					
		EditText numMsgEdit = (EditText) dialog.findViewById(R.id.numOfMsg);
		numberOfMsgToDisplay = preferences.getInt("vg.numberOfMsgToDisplay",numberOfMsgToDisplay);
		numMsgEdit.setText(String.valueOf(numberOfMsgToDisplay));
					
		Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
		// if button is clicked, close the custom dialog
		dialogButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialogOnClickAction(dialog);
			}
		});

		dialog.show();
	}
	
	
	/* Backup
	public void displayText(JSONObject message) {
		FrameLayout rl = new FrameLayout(this.getApplicationContext());
		if (message!=null){
			TextView tv =(TextView) findViewById(R.id.messages);
						
			try {
				tv.setText(message.get("userId")+": "+message.get("msg"));
				tv.setTextColor(Color.parseColor((String)message.get(color)));
				tv.setTextSize(textSize);
			} catch (Exception e) {
				 
				Log.e("error", e.getMessage()+ " ; cause: "+e.getCause(), e);
			}
	
			//tv.setGravity(Gravity.CENTER_HORIZONTAL);
		}
	}*/

		
	

	private void getMessages( )  {
	
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
	
		 new AsyncTask<String, Void,JSONArray> (){
			 Throwable error = null;

			 @Override
			protected void onPostExecute(JSONArray result) {
				
				 Log.v("tag", "getMessages - onPostExecute");
				
				try {
					if (error == null && result != null){
						JSONObject lastMsgObject = null;
						int currentMsgId;
						synchronized (messagesInClient) {
							boolean resetFlag = false;
							HashSet<String>currZonesIdsSet = new HashSet<String>();
							
							lastProcessedMsgId = 0;
							if (result.length() > 0){
								JSONArray retZoneIDs = ((JSONObject)result.get(0)).getJSONArray("zoneId");
								if (retZoneIDs != null){
									for (int i=0; i < retZoneIDs.length(); i++){
										currZonesIdsSet.add(retZoneIDs.getString(i));
									}
								}
								
								for (String zoneId : currZonesIdsSet){
									if (!messagesZonesIds.contains(zoneId)){
										resetFlag = true;
										break;
									}
								}
								
								for (String zoneId : messagesZonesIds){
									if (!currZonesIdsSet.contains(zoneId)){
										resetFlag = true;
										break;
									}
								}
								
								if (resetFlag){
									messagesInClient.clear();
									messageIdsInClient.clear();
									messagesZonesIds.clear();
									messagesZonesIds.addAll(currZonesIdsSet);
								}
								
							}else{
								messagesInClient.clear();
								messageIdsInClient.clear();
								messagesZonesIds.clear();
							}
							
							
							for (int i=0; i < result.length(); i++){
								lastMsgObject = (JSONObject) result.get(i);
								currentMsgId = lastMsgObject.getInt("messageId");
								if (!messageIdsInClient.contains(currentMsgId)){
									messagesInClient.addLast(lastMsgObject);
									messageIdsInClient.add(currentMsgId);
								}
							}
							displayText();
						}
						
					}else if (error != null){
						Toast.makeText(getBaseContext(), "connection to server was lost", Toast.LENGTH_SHORT).show();
						Log.e("error", "showing error msg - getMessages - onPostExecute ",error);
					}
				} catch (Exception e) {
					 Log.e("error", e.getMessage()+ " ; cause: "+e.getCause(), e);
				};
				
			  }
			@Override
			protected JSONArray doInBackground(String... paramters) {
				// TODO Auto-generated method stub
				
					JSONArray result = null;
					String url = (String) paramters[0];
					String entity = (String) paramters[1];
					String cis = (String)paramters[2];
					
					Log.v("tag", "on getMessages - url= "+url +" ; entity= "+entity+" ; cis= "+cis);

					//CIS might have white spaces 
					cis = URLEncoder.encode(cis);
					String request = url + "/getMsg.html?userID="+entity+"&cis="+cis+"&number="+lastProcessedMsgId;
					HttpGet httpGet = new HttpGet(request);
					
					BufferedReader in = null;
					try{
						HttpClient client = new DefaultHttpClient();
						HttpResponse response = client.execute(httpGet);
						in = new BufferedReader
			            (new InputStreamReader(response.getEntity().getContent()));
			            StringBuffer sb = new StringBuffer("");
			            String line = "";
			        
			            while ((line = in.readLine()) != null) {
			                sb.append(line);
			            }
			            in.close();
			            Log.v("tag", sb.toString());
			            JSONArray ja = new JSONArray();
			            if (sb.length() > 3){
			            	ja =  new JSONArray(sb.toString());
			            }
			            
			            result =ja;// ja.getJSONObject(ja.length()-1);
			            
					}  catch (Exception e) {
						//if no connection - toast no connection..
						Log.e("error", "error on getMessages ; "+e.getMessage()+ " ; cause: "+e.getCause(), e);
						error = e;
					}  finally {
			            if (in != null) {
			                try {
			                    in.close();
			                } catch (IOException e) {};
			            }
					}
				return result;
			
		        
			}}.execute(preferences.getString("vg.url", ""),preferences.getString("vg.entity", ""), preferences.getString("vg.cis", ""));
		
	}

	
	private void postMessages(String sprayText)  {
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
	
		 new AsyncTask<String, Void,JSONObject> (){
			 Throwable error = null;

			 @Override
			protected void onPostExecute(JSONObject result) {
				 Log.v("tag", "in postMessages -  onPostExecute");
				 if (error != null){
					 Toast.makeText(getBaseContext(), "Server Error while posting message", Toast.LENGTH_SHORT).show();
					 Log.e("error", "showing error msg on postMessages ; ", error);
				 }
			 }
			@Override
			protected JSONObject doInBackground(String... paramters) {
					String url = (String) paramters[0];
					String entity = (String) paramters[1];
					String sprayText = (String) paramters[2];
					String cis =  (String) paramters[3];
					
					Log.v("tag", "on postMessage - url= "+url +" ; entity= "+entity+" ; cis= "+cis +" ; style= "+color+" ; msg= "+sprayText);
		
					String post = url+"/postMsg1.html";
					HttpPost httpPost = new HttpPost(post);
					try{
						HttpClient client = new DefaultHttpClient();
						List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
						nameValuePairs.add(new BasicNameValuePair("cisBox", cis));
						nameValuePairs.add(new BasicNameValuePair("userId", entity));
						nameValuePairs.add(new BasicNameValuePair("style", color));
						nameValuePairs.add(new BasicNameValuePair("msg", sprayText));
						httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
						client.execute(httpPost);
					
					}catch (Exception e) {
						Log.e("error", e.getMessage()+ " ; cause: "+e.getCause(), e);
						error = e;
					}
					return null;

			}}.execute(preferences.getString("vg.url", ""),preferences.getString("vg.entity", ""),sprayText,preferences.getString("vg.cis", ""));
		
	}
	
	
	@Override
	public void onPause() {
	   super.onPause();  // Always call the superclass method first
	   
		   if (cv!= null){
			   cv.onPauseMySurfaceView();
		   }
		      
		   synchronized (this) {
				  scanTaskActive = false;
				  if (scanTask != null){
					  scanTask.cancel();
				  }
		   }
		
	}
	
	@Override
	public void onResume() {
	    super.onResume();  // Always call the superclass method first
		    if (cv!= null){
		    	cv.onResumeMySurfaceView();
		    }
			getMessagesTask();
	}
	
	

}
