/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.thirdpartyservices.askfree.remotedbdata;

import java.util.ArrayList;
import java.util.HashMap;

import org.codehaus.jackson.map.Module;
import org.json.JSONArray;
import org.json.JSONObject;
import org.societies.thirdpartyservices.askfree.JSONParser;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

/**
 * Describe your class here...
 *
 * @author Ioannis Mimtsoudis
 *
 */
public class GetTopicDataTask extends AsyncTask<String,String,ArrayList<Topic>> {
	
	protected static final String LOG_TAG = GetTopicDataTask.class.getSimpleName();	
	private static final String TOPIC_DATA_URL = "http://54.218.113.176/askfree/getTopicData.php";
	private static final String TAG_RES = "res";
	private static final String TAG_T_ID = "m_id";
	private static final String TAG_TOPIC = "module";
	private static final String TAG_URI = "uri";
	private static final String TAG_LOC_ID = "locationID";
	protected static final String TOPIC_DATA = "org.societies.thirdpartyservices.askfree.remotedbdata.TOPIC_DATA";
	
	
	JSONParser jsonParser = new JSONParser();
	
	JSONArray topicDataArray = null;
	
	ArrayList<Topic> topicList = new ArrayList<Topic>();
	HashMap<String,String> locationToURI = new HashMap<String,String>();
	
	ProgressDialog dialog = null;
	
	private Context context;
	private Context mContext;
	
	public GetTopicDataTask(Context context,Context mContext) {
		this.context = context;
		this.mContext = mContext;
	}

	@Override
	protected void onPreExecute(){
		super.onPreExecute();
		dialog = new ProgressDialog(mContext);
		dialog.setMessage("Loading data...");
		dialog.setIndeterminate(false);
		dialog.setCancelable(true);
		dialog.show();
	}
	
	@Override
	protected ArrayList<Topic> doInBackground(String... arg0) {
		
		JSONObject json = jsonParser.getJSONObject(TOPIC_DATA_URL);
		
		Log.d(LOG_TAG, "Location Data from db: " + json.toString());
		
		try{
			topicDataArray = json.getJSONArray(TAG_RES);
			for (int i = 0; i < topicDataArray.length(); i++) {
                JSONObject topicDataObject = topicDataArray.getJSONObject(i);
                
                Topic topic = new Topic();
                topic.setTopicID(topicDataObject.getString(TAG_T_ID));
                topic.setTopicName(topicDataObject.getString(TAG_TOPIC));
                topic.setTopicURI(topicDataObject.getString(TAG_URI));
                topic.setLocationID(topicDataObject.getString(TAG_LOC_ID));
                
                topicList.add(topic);
                //locationToURI.put(topic.getLocationID(), topic.getTopicURI());
			}
			
		}catch(Exception e){
			e.getMessage();
		}
		
		return topicList;
	}
	
	@Override
	protected void onPostExecute(ArrayList<Topic> topicList) {
		//Broadcast Intent with topicList
		Log.d(LOG_TAG, "Topic list: " + topicList.size());
		Bundle b = new Bundle();
		Log.d(LOG_TAG, "Bundle to send list created ");
		b.putParcelableArrayList("topicList", topicList);
		Log.d(LOG_TAG, "Topic list in the bundle: ");
		Intent i = new Intent(TOPIC_DATA);
		Log.d(LOG_TAG, "Intent created");
		i.putExtra("topicList", b);
		Log.d(LOG_TAG, "Bundle in the intent");
		context.sendBroadcast(i);
		Log.d(LOG_TAG, "Intent" + i + "Broadcasted");
		
		dialog.dismiss();
	}
}
