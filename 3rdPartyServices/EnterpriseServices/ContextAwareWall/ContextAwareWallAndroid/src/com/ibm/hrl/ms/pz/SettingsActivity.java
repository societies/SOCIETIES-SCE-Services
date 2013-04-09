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
	    entityText.setOnEditorActionListener(new OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId,	KeyEvent event) {
				getCIS();
				return false;
			}
			
	    });
	    
	    entityText.setText(preferences.getString("vg.entity", ""));
	  
	    EditText urlEditText = (EditText) findViewById(R.id.txt_url);

	    urlEditText.setText(preferences.getString("vg.url", ""));
	    urlEditText.setOnEditorActionListener(new OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId,	KeyEvent event) {
				getCIS();
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
	              Spinner spinner = (Spinner) findViewById(R.id.cis_list);
	              
	              String entitySelected = entityText.getText().toString();
	              String urlSelected = urlEditText.getText().toString();
	              String cisSelected = (String) spinner.getSelectedItem();
	              
	              String entityStored = preferences.getString("vg.entity","");
	              String urlStored  = preferences.getString("vg.url","");
	              String cisStored  = preferences.getString("vg.cis","");
	              
	              if("".equals(entitySelected)    || "".equals(urlSelected) || cisSelected == null || "".equals(cisSelected) ){
	            	  Toast.makeText(v.getContext(), "Missing entity, url or CIS", Toast.LENGTH_SHORT).show();
	              
	              }else{
	            	  if (!entitySelected.equalsIgnoreCase(entityStored) || !urlSelected.equalsIgnoreCase(urlStored) || 
	            		  !cisSelected.equalsIgnoreCase(cisStored)){
		            		  SharedPreferences.Editor editor = preferences.edit();
			            	  editor.putString("vg.entity", entitySelected.trim());
			            	  editor.putString("vg.url", urlSelected.trim());
			            	  editor.putString("vg.cis", cisSelected);
			            	  editor.commit();
			              
			            	  Toast.makeText(v.getContext(), "Preferences stored", Toast.LENGTH_SHORT).show();  
	            	  }
	            	  startActivity(new Intent("com.ibm.hrl.ms.pz.CameraPreview"));
	              }
			}
		}; 
	    launch.setOnClickListener(l);
	    
	    
	    String entityId = preferences.getString("vg.entity","");
	    String url = preferences.getString("vg.url","");
	    
	    if (entityId.length() > 0 && url.length() > 0){
	    	 getAllCisRestCall(entityId,url); 
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
	
	private void getCIS() {
		EditText entityText = (EditText) findViewById(R.id.txt_entity);
		EditText urlEditText = (EditText) findViewById(R.id.txt_url);
		
		String entityStr = entityText.getText().toString(); 
		String urlString = urlEditText.getText().toString();
		
		if (entityStr != null && urlString != null && entityStr.length() > 0 && urlString.length() > 0){
			getAllCisRestCall(entityStr,urlString);
		}
	}
	
	private ProgressDialog progressDialog=null;
	private void showDialog(){
		 progressDialog = ProgressDialog.show(this, "", "Loading...");
	}
	
	private void dismissDialog(){
		 if (progressDialog != null){
			 progressDialog.dismiss();
		 }
	}
	
	private void getAllCisRestCall(String entityStr, String urlString )  {
		
		 new AsyncTask<String, Void,JSONObject> (){
			Throwable error = null;
			 
			 @Override
			protected void onPreExecute(){
				showDialog();
			 }
			 
			 @Override
			protected void onPostExecute(JSONObject result) {
				 if (error != null){
					 Toast.makeText(getBaseContext(), "Error retrieving CIS for the given entity", Toast.LENGTH_SHORT).show();
					 
				 }else if (result != null){
					try {
						JSONArray cis = (JSONArray)result.get("messages");
						createSpinner(cis);
					} catch (JSONException e) {
						
						e.printStackTrace();
					}
				}
				dismissDialog();
			  }
			 
			@Override
			protected JSONObject doInBackground(String... paramters) {
				// TODO Auto-generated method stub
				
					JSONObject result = null;
					String url = (String) paramters[0];
					String entity = (String) paramters[1];
					Log.v("tag", url);
					Log.v("tag", entity);
					
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
			            Log.v("tag", sb.toString());
			            
			            result = new JSONObject( sb.toString());
			            
					}  catch (Exception e) {
						error = e;
						Log.e("error", "can't get setting for entity: "+entity+" ; "+e.getMessage()+ " ; cause: "+e.getCause(), e);
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
	
	
	/*
	 
	private static final int EXIT = 1;
	private static final int SPRAY = 2;
	 
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		menu.add(1, SPRAY, Menu.FIRST, "Spray Graffiti");
		menu.add(1, EXIT, Menu.FIRST, "exit");	
		return true;
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
     
		switch(item.getItemId()){
			case EXIT:
				System.exit(0);
				break;
			case SPRAY:
				break;
		}
		
		return super.onOptionsItemSelected(item);
	}*/
}