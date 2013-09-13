package com.ibm.hrl.ms.pz;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class SettingsActivity extends Activity{
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.settings);
	    
	    Button launch = (Button)findViewById(R.id.set_button);
	   
	    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
	
	    EditText entityText = (EditText) findViewById(R.id.txt_entity);
	    entityText.setText(preferences.getString("vg.entity", ""));
	    entityText.setOnEditorActionListener(new OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId,	KeyEvent event) {
				extractVarsAndRetrieveCIS();
				return false;
			}
			
	    });
	    
	    EditText serverEditText = (EditText) findViewById(R.id.txt_url);
	    serverEditText.setText(preferences.getString("vg.server", ""));
	    serverEditText.setOnEditorActionListener(new OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId,	KeyEvent event) {
				extractVarsAndRetrieveCIS();
				return false;
			}
	    });
	    
	    
	    EditText portEditText = (EditText) findViewById(R.id.editText_port);
	    portEditText.setText(preferences.getString("vg.port", ""));
	    portEditText.setOnEditorActionListener(new OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId,	KeyEvent event) {
				extractVarsAndRetrieveCIS();
				return false;
			}
	    });
	   
	   
	   Spinner spinner = (Spinner) findViewById(R.id.cis_list);
	   spinner.setEnabled(false);
	   
	   
	   OnClickListener l = new OnClickListener() {
			
			public void onClick(View v) {
				  SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
				  EditText entityText = (EditText) findViewById(R.id.txt_entity);
	              EditText urlEditText = (EditText) findViewById(R.id.txt_url);
	              EditText portEditText= (EditText) findViewById(R.id.editText_port);
	              
	              Spinner spinner = (Spinner) findViewById(R.id.cis_list);
	              
	              String entitySelected = entityText.getText().toString();
	              String serverSelected = urlEditText.getText().toString();
	              String cisSelected = (String) spinner.getSelectedItem();
	              String portSelected = (String)portEditText.getText().toString(); 
	              
	              String entityStored = preferences.getString("vg.entity","");
	              String cisStored  = preferences.getString("vg.cis","");
	              String urlStored  = preferences.getString("vg.url","");
	              
	              if("".equals(entitySelected) || "".equals(serverSelected) || cisSelected == null || "".equals(cisSelected) || "".equals(portSelected) ){
	            	  Toast.makeText(v.getContext(), "Missing entity, url, port or CIS", Toast.LENGTH_SHORT).show();
	              
	              }else{
	            	  String fullURL = encodeURL(serverSelected,portSelected);
	            	  
	            	  if (!entitySelected.equalsIgnoreCase(entityStored) || !fullURL.equals(urlStored) || 
	            		  !cisSelected.equalsIgnoreCase(cisStored)){
		            		  SharedPreferences.Editor editor = preferences.edit();
			            	  editor.putString("vg.entity", entitySelected.trim());
			            	  editor.putString("vg.url", fullURL);
			            	  editor.putString("vg.cis", cisSelected);
			            	  
			            	  editor.putString("vg.server", serverSelected);
			            	  editor.putString("vg.port", portSelected);
			            	  
			            	  editor.commit();
			              
			            	  Toast.makeText(v.getContext(), "Preferences stored", Toast.LENGTH_LONG).show();  
	            	  }
	            	  startActivity(new Intent("com.ibm.hrl.ms.pz.CameraPreview"));
	              }
			}
		}; 
	    launch.setOnClickListener(l);
	    
	    if (entityText.getText().length() > 0 && serverEditText.getText().length() > 0 ){
	    	extractVarsAndRetrieveCIS();
	    }else{
	    	String entityId = preferences.getString("vg.entity","");
	 	    String url = preferences.getString("vg.url","");
	 	    
	 	    if (entityId.length() > 0 && url.length() > 0){
	 	    	 getAllCisRestCall(entityId,url); 
	 	    } 	
	    }
    }

	void createSpinner(JSONArray cisList){
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		String pastSelectedCIS = preferences.getString("vg.cis", "");
		Spinner spinner2 = (Spinner) findViewById(R.id.cis_list);
		List<String> list = new ArrayList<String>();
		
	
		for (int i=0; i < cisList.length(); i++){
			JSONObject jsonObject;
			try {
				jsonObject = cisList.getJSONObject(i);
				list.add(jsonObject.getString("name"));
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner2.setAdapter(dataAdapter);
		spinner2.setEnabled(true);
		
		int index = 0;
		for (String cis: list){
			if (cis.equals(pastSelectedCIS)){
				spinner2.setSelection(index);
				break;
			}
			index++;
		}
		
		
	}
	
	private void extractVarsAndRetrieveCIS() {
		EditText entityText = (EditText) findViewById(R.id.txt_entity);
		EditText serverEditText = (EditText) findViewById(R.id.txt_url);
		EditText portEditText = (EditText) findViewById(R.id.editText_port);
		
		String entityStr = entityText.getText().toString(); 
		String serverString = serverEditText.getText().toString();
		String portString = portEditText.getText().toString();
		
		if (entityStr != null && serverString != null && portString != null && entityStr.length() > 0 && serverString.length() > 0 && portString.length() > 0){
			
			String url = encodeURL(serverString, portString);
			getAllCisRestCall(entityStr,url);
		}
	}
	
	private ProgressDialog progressDialog=null;
	private void showDialog(){
		 progressDialog = ProgressDialog.show(this, "", "Loading...");
		 progressDialog.setCancelable(true);
	}
	
	private void dismissDialog(){
		 if (progressDialog != null){
			 progressDialog.dismiss();
		 }
	}
	
	private void getAllCisRestCall(String entityStr, String urlString)  {
		
		 new AsyncTask<String, Void,JSONObject> (){
			Throwable error = null;
			 
			 @Override
			protected void onPreExecute(){
				showDialog();
			 }
			 
			 @Override
			protected void onPostExecute(JSONObject result) {
				 try{
					 if (error != null){
						 Toast.makeText(getBaseContext(), "Error retrieving CIS for the given entity", Toast.LENGTH_SHORT).show();
						 
					 }else if (result != null){
						try {
							JSONArray cis = (JSONArray)result.get("messages");
							createSpinner(cis);
						} catch (JSONException e) {
							Log.e("vg", "Error - Json Exception; cause: "+e.getCause(), e);
						}
					}
				 }catch(Exception e){
					 Log.e("vg", "Error - Json Exception; cause: "+e.getCause(), e);
				 }
				dismissDialog();
			  }
			 
			@Override
			protected JSONObject doInBackground(String... paramters) {
				// TODO Auto-generated method stub
				
					JSONObject result = null;
					String url = (String) paramters[0];
					String entity = (String) paramters[1];
										
					Log.v("vg", "going to get CIS for "+entity);
					
					String request = url + "/initialUserDetails.html";
					HttpGet httpGet = new HttpGet(request);
					
					BufferedReader in = null;
					try{
						HttpClient client = new DefaultHttpClient();
						HttpResponse response = client.execute(httpGet);
						in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			            StringBuffer sb = new StringBuffer("");
			            String line = "";
			            
			            while ((line = in.readLine()) != null) {
			                sb.append(line);
			            }
			            in.close();
			            Log.v("vg", sb.toString());
			            
			            result = new JSONObject( sb.toString());
			            
					}  catch (Exception e) {
						error = e;
						Log.e("vg", "can't get setting for entity: "+entity+" ; "+e.getMessage()+ " ; cause: "+e.getCause(), e);
					}  finally {
			            if (in != null) {
			                try {
			                    in.close();
			                } catch (IOException e) { }
			        }
			}
				return result;
			
		        
			}}.execute(urlString,entityStr);
		
	}
	
	private static String encodeURL(String url,String port){
		String fullURL = "";
		if (!url.startsWith("http://")){
			fullURL = "http://";
		}
		fullURL += url +":"+port+"/VG";
		
		return fullURL;
	}
	
	
}