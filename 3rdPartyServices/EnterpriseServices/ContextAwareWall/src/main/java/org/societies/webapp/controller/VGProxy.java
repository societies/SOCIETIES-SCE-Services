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
package org.societies.webapp.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.management.ICis;
import org.societies.api.cis.management.ICisManager;
import org.societies.webapp.model.MessageForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * 
 * This is the context aware wall proxy service
 *
 * @author guy feigenblat IBM Haifa research lab
 *
 */
@Controller
public class VGProxy {
	
	private static final String LOG_PREFIX = "CONTEXT_AWARE_WALL:\t"; 
	
	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(VGProxy.class);
	
	//http://ta-proj02:9082/VG4SWeb/vg/message
	/*
	 * {"userId" : "unknown7",  "cis": "111", "msg": "GuyGuyGuy", "style" : "", "zoneId":"1","messageId":"5","ts":null}
	 * 
	 * {"userId" : "unknown7",  "cis": "111", "msg": "GuyGuyGuy", "style" : "", "zoneId":null,"messageId":null,"ts":null}
	 * 
	 * 
	 * 
	 * 
	 * http://ta-proj02.haifa.ibm.com:9082/QueriesGatewayREST/RT/allActiveEntitiesIds
	 * 
	 */
	
	@Autowired
	private ICisManager cisManager;
	
	public VGProxy(){
		LOG.info(LOG_PREFIX + "Ctor");
	}
	private String serverURL;
	
	
	
	/**
	 * Posting user message to the centrelized context aware wall server
	 * 
	 * @param form
	 * @param result
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/postMsg1.html",method = RequestMethod.POST)
	public @ResponseBody String page(@Valid MessageForm form ,BindingResult result, Map model) {
		
		LOG.debug(LOG_PREFIX + " enter postMsg1.html");
		
		String jsonMsg="";
		try{
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("cis", form.getCisBox());
			jsonObject.put("userId", form.getUserId());
			jsonObject.put("style", form.getStyle());
			jsonObject.put("msg", form.getMsg());
			jsonMsg = jsonObject.toString();
			
			LOG.info(LOG_PREFIX + " created JSON msg object :\t" +jsonMsg);
			
			postMessageInternal(jsonMsg);
		}catch (Exception e) {
			LOG.error(LOG_PREFIX +"exception caught while posting the following msg: \n"+jsonMsg+"\n "+e.getMessage(),e);
		}
		
		LOG.debug(LOG_PREFIX + " finish postMsg1.html");
		
		return "";
	} 
	
	@RequestMapping(value="/initialUserDetails.html",method = RequestMethod.GET)
	public @ResponseBody String getCIS() {
	
		List<ICis> cisList = new ArrayList<ICis>();
		//List<ICis> cisList = cisManager.getCisList();
		JSONObject userJsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject; 
		/*
		for (ICis cis : cisList){
			jsonObject = new JSONObject();
			try {
				jsonObject.put("name", cis.getName());
				jsonObject.put("id", cis.getCisId());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			jsonArray.put(jsonObject);
		}
		*/
		if (cisList.isEmpty()){
			try {
				for (int i=1; i <= 4; i++){
					jsonObject = new JSONObject();
					jsonObject.put("name", "name"+Integer.toString(i));
					jsonObject.put("id", Integer.toString(i));
					jsonArray.put(jsonObject);
				}
			} catch (JSONException e) {
				LOG.error(LOG_PREFIX +"exception caught while getting user details",e);
			}
			
		}
		
		try{
			userJsonObject.put("messages", jsonArray);			
			userJsonObject.put("userId", "guy-phone");
		}catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		return userJsonObject.toString();
	}
	
	/**
	 * Executing rest call to post the given message (as Json object) 
	 * @param jsonMsg
	 */
	private void postMessageInternal(String jsonMsg){
		
		LOG.debug(LOG_PREFIX + " enter postMessageInternal");
		
		String responseString="";
		int statusCode;
		try {
			HttpPost httpPostRequest = new HttpPost("http://ta-proj02:9082/VG4SWeb/vg/message");
			
			StringEntity entity = new StringEntity(jsonMsg, HTTP.UTF_8);
			entity.setContentType("application/json");
			httpPostRequest.setEntity(entity);
			HttpClient client = new DefaultHttpClient();
			
			LOG.debug(LOG_PREFIX +"before executing rest call to: "+httpPostRequest.getURI());
			HttpResponse response = client.execute(httpPostRequest);
			statusCode = response.getStatusLine().getStatusCode();
			
			if (response.getEntity() != null && response.getEntity().getContent() != null){
				BufferedReader bf = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				while (bf.ready()){
					responseString += bf.readLine();
				}
			}
			LOG.info(LOG_PREFIX +"after executing rest call to: "+httpPostRequest.getURI()+ "\t Json" +jsonMsg+"\t status code- "+statusCode+"\t response: "+responseString);
			
			
		} catch (UnsupportedEncodingException e) {
			LOG.error(LOG_PREFIX +"exception caught while posting the following msg: \n"+jsonMsg+"\n "+e.getMessage(),e);
		} catch (IOException e) {
			LOG.error(LOG_PREFIX +"exception caught while posting the following msg: \n"+jsonMsg+"\n "+e.getMessage(),e);
		}
		
		LOG.debug(LOG_PREFIX + " finish postMessageInternal");
	}
	
	/**
	 * Getting all messages filtered with the given parameters
	 * @param userID - user id (JID)
	 * @param cis - CIS the user want to filter by
	 * @param number - message id
	 * @return
	 */
	@RequestMapping(value="/getMsg.html",method = RequestMethod.GET)
	public @ResponseBody String getMessages(@RequestParam String userID,@RequestParam String cis, @RequestParam String number){
		return getMessagesInternal(userID,cis,number);
	}
	
	/**
	 * Getting messages from the centralized VG server
	 * @param userId
	 * @param cis
	 * @param number
	 * @return
	 */
	private String getMessagesInternal(String userId, String cis, String number){
		LOG.debug(LOG_PREFIX + " enter postMessageInternal");
		
		String responseString="";
		int statusCode;
		try {
	    	
			HttpGet httpGetRequest = new HttpGet("http://ta-proj02:9082/VG4SWeb/vg/message/"+userId+"/"+ cis+ "/"+number);
			httpGetRequest.addHeader("accept", "application/json");
	    	HttpClient httpclient = new DefaultHttpClient();
	    	HttpResponse response = httpclient.execute(httpGetRequest);
	    	statusCode = response.getStatusLine().getStatusCode();
	    	if (response.getEntity() != null && response.getEntity().getContent() != null){
				BufferedReader bf = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				String temp;
				while ((temp = bf.readLine()) != null) {
					responseString+= temp;	
				}
			}
	    	LOG.info(LOG_PREFIX +"after executing rest call to: "+httpGetRequest.getURI()+ "\t status code- "+statusCode+"\t response: "+responseString);
	    }catch (Exception e) {
			e.printStackTrace();
		}
		
		LOG.debug(LOG_PREFIX + " finish postMessageInternal");
	    return responseString;
	}
	
	public void setICisManager(ICisManager cisManager){
		this.cisManager = cisManager;
	}
	
	public void setServerURL(String serverURL) {
		this.serverURL = serverURL;
		
	}
	
	/*
	void action(final HttpServletRequest request) {
	    final String paramValue = request.getSession().getServletContext().getInitParameter("paramName");
	    System.out.println(paramValue);
	}*/

	
	
}
