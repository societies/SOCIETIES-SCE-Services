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

import org.json.JSONArray;
import org.json.JSONObject;
import org.societies.thirdpartyservices.askfree.JSONParser;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Describe your class here...
 *
 * @author Ioannis Mimtsoudis
 *
 */
public class GetLocationDataTask extends AsyncTask<String, String, String> {
	
	protected static final String LOG_TAG = GetLocationDataTask.class.getSimpleName();	
	private static final String LOCATION_DATA_URL = "http://54.218.113.176/askfree/getLocationData.php";
	private static final String TAG_RES = "res";
	private static final String TAG_LOC_ID = "locationID";
	private static final String TAG_LOC_NAME = "locationName";
	
	JSONParser jsonParser = new JSONParser();
	
	JSONArray locDataArray = null;
	
	ArrayList<Location> locationList = new ArrayList<Location>();

	
	@Override
	protected String doInBackground(String... arg0) {
		
		JSONObject json = jsonParser.getJSONObject(LOCATION_DATA_URL);
		
		Log.d(LOG_TAG, "Location Data from db: " + json.toString());
		
		try{
			locDataArray = json.getJSONArray(TAG_RES);
			for (int i = 0; i < locDataArray.length(); i++) {
                JSONObject locationDataObject = locDataArray.getJSONObject(i);
                
                Location location = new Location();
                location.setLocationID(locationDataObject.getString(TAG_LOC_ID));
                location.setLocationName(locationDataObject.getString(TAG_LOC_NAME));
                
                locationList.add(location);
			}
			
		}catch(Exception e){
			e.getMessage();
		}
		
		return null;
	}



}
