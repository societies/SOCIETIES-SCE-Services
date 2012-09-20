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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.validation.Valid;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.jfree.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.attributes.MembershipCriteria;
import org.societies.api.cis.management.ICis;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.internal.css.management.ICSSLocalManager;
import org.societies.api.schema.cssmanagement.CssInterfaceResult;
import org.societies.api.schema.cssmanagement.CssRecord;
import org.societies.webapp.model.MessageForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;


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
	
	String SERVER_URL = "http://ta-proj02:9082/VG4SWeb/vg/message";
	String MOCK_ENTITY = "";
	boolean MOCK_ENTITY_ACTIVE = false;
	
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
	
	@Autowired private ICisManager cisManager;
	@Autowired private ICSSLocalManager cssManager;
	@Autowired private ICommManager commManager;
	@Autowired private SocietiesRegistryBean societiesRegistryBean;
	
	public VGProxy(){
		LOG.info(LOG_PREFIX + "Ctor");
	}
	
	private List<ICis> fillCisObjectforTesting(){
		LOG.debug("Start method 'fillCisObjectforTesting'");
		
		List<ICis> cisList = new ArrayList<ICis>();
		try {
			Future<CssInterfaceResult> cssInterfaceResultFuture = cssManager.getCssRecord();
			CssInterfaceResult cssInterfaceResult2 = cssInterfaceResultFuture.get();
			CssRecord cssRecord = cssInterfaceResult2.getProfile();
			
			try{
				LOG.debug("creating CIS for testing (in case no CIS if found):  CSS ID \t\t" + cssRecord.getCssIdentity());
			}catch (Exception e) {
				e.printStackTrace();
			}
			
			Hashtable<String, MembershipCriteria> cisCriteria = new Hashtable<String, MembershipCriteria>();
			/*
			MembershipCriteria m = new MembershipCriteria();
			
		
				Rule r = new Rule("equals",new ArrayList<String>(Arrays.asList("married")));
				m.setRule(r);
				cisCriteria.put(CtxAttributeTypes.STATUS, m);
				r = new Rule("equals",new ArrayList<String>(Arrays.asList("Brazil")));
				m.setRule(r);
				cisCriteria.put(CtxAttributeTypes.ADDRESS_HOME_COUNTRY, m);
			*/
			
			
			
			cisManager.createCis("Guy CIS_"+generateRand(),"",cisCriteria, "description: test guy1");
			cisManager.createCis("Guy CIS_"+generateRand(),"",cisCriteria, "description: test guy2");
			cisList = cisManager.getCisList();
			if (cisList.size() > 0){
				LOG.debug("there are "+cisList.size() +" CIS in the system - first one is : "+(cisList.get(0)).getName() + " \t id " + (cisList.get(0)).getCisId());
			}else{
				LOG.debug("Error! although just been created for testing, there are no CIS in the system");
			}
			
			
		} catch (InterruptedException e) {
			LOG.error(LOG_PREFIX +"exception caught: \t"+e.getMessage() + " \t get cause: "+e.getCause(),e);
		} catch (ExecutionException e) {
			LOG.error(LOG_PREFIX +"exception caught: \t"+e.getMessage() + " \t get cause: "+e.getCause(),e);		
		}catch (Exception e) {
			LOG.error(LOG_PREFIX +"exception caught: \t"+e.getMessage() + " \t get cause: "+e.getCause(),e);
		}
		LOG.debug("Start method 'fillCisObjectforTesting'");
		
		return cisList;
	}
	
	private int generateRand(){
		double rand = Math.random()*1000;
		rand = Math.round(rand);
		return (int)rand;
	}
	
	
	@RequestMapping(value="/test/createCIS.html",method = RequestMethod.GET)
	public void testCreateCIS(@RequestParam String cisName,@RequestParam String description) {
		Future<CssInterfaceResult> cssInterfaceResultFuture = cssManager.getCssRecord();
		CssInterfaceResult cssInterfaceResult2;
		CssRecord cssRecord=null;
		try {
			cssInterfaceResult2 = cssInterfaceResultFuture.get();
			cssRecord = cssInterfaceResult2.getProfile();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Hashtable<String, MembershipCriteria> cisCriteria = new Hashtable<String, MembershipCriteria>();
		cisManager.createCis(cisName,"",cisCriteria, description);	
		
		
	}
	
	
	
	@RequestMapping(value="/contextAwareWall.html",method = RequestMethod.GET)
	public ModelAndView pageLoad() {
		//model is nothing but a standard Map object
		Map<String, Object> model = new HashMap<String, Object>();
		return new ModelAndView("mobile", model) ;
	}
	
	
	/**
	 * Posting user message to the centralized context aware wall server
	 * 
	 * @param form
	 * @param result
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/postMsg1.html",method = RequestMethod.POST)
	public @ResponseBody String page(@Valid MessageForm form ,BindingResult result, Map model) {
		
		LOG.debug(LOG_PREFIX + " enter postMsg1.html");
		
		try{
			if (validateForm(form)){
				String jsonMsg="";
				try{
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("cis", form.getCisBox());
					jsonObject.put("userId", form.getUserId());
					jsonObject.put("style", form.getStyle());
					
					String postedMsg = form.getMsg();
					postedMsg= postedMsg.replace("\n", " ");
					jsonObject.put("msg", postedMsg);
					jsonMsg = jsonObject.toString();
					
					LOG.info(LOG_PREFIX + " created JSON msg object :\t" +jsonMsg);
					
					postMessageInternal(jsonMsg);
				}catch (Exception e) {
					LOG.error(LOG_PREFIX +"exception caught while posting the following msg: \n"+jsonMsg+"\n "+e.getMessage(),e);
				}
			}
			LOG.debug(LOG_PREFIX + " finish postMsg1.html");
			
		}catch (Exception e) {
			LOG.error(LOG_PREFIX +"exception caught: \t"+e.getMessage() + " \t get cause: "+e.getCause(),e);
		}
		
		return "";
	}
	
	private boolean validateForm(MessageForm form){
		boolean flag = true;
		if (form.getCisBox() == null || form.getCisBox().length() == 0){
			Log.warn("Selected CIs can't be null or Empty \t form.getCisBox()='"+form.getCisBox()+"'");
			flag = false;
		}
		if (form.getUserId() == null || form.getUserId().length() == 0){
			Log.warn("UserId can't be null or Empty \t form.getUserId()='"+form.getUserId()+"'");
			flag = false;
		}
		if (form.getMsg() == null || form.getMsg().trim().length() == 0){
			Log.warn("UserId can't be null or Empty \t form.getMsg()='"+form.getMsg()+"'");
			flag = false;
		}
		if (form.getStyle() == null ||  form.getStyle().length() == 0){
			Log.warn("UserId can't be null or Empty \t form.getStyle()='"+form.getStyle()+"'");
			flag = false;
		}
		return flag;
		
	}
	
	@RequestMapping(value="/initialUserDetails.html",method = RequestMethod.GET)
	public @ResponseBody String getCIS() {
		List<ICis> cisList = new ArrayList<ICis>();
		JSONObject userDetailsObject = new JSONObject();
		try{
			cisList = cisManager.getCisList();
			if (cisList.isEmpty()){
				cisList = fillCisObjectforTesting();
			}
			
			JSONArray jsonArray = new JSONArray();
			JSONObject jsonObject; 
			
			for (ICis cis : cisList){
				jsonObject = new JSONObject();
				try {
					jsonObject.put("name", cis.getName());
					jsonObject.put("id", cis.getCisId());
				} catch (JSONException e) {
					LOG.error(LOG_PREFIX + "\t Json exception while iterating over CIS list \t msg: "+e.getMessage() +" cause: "+e.getCause(),e);
				}
				jsonArray.put(jsonObject);
			}
			/*
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
			}*/
			
			try{
				String jid = commManager.getIdManager().getThisNetworkNode().getJid();
				
				userDetailsObject.put("messages", jsonArray);
				
				if (societiesRegistryBean.isMockEntityActive()){
					userDetailsObject.put("userId",societiesRegistryBean.getMockEntity());
				}else{
					userDetailsObject.put("userId",jid);
				}
				
				LOG.debug(LOG_PREFIX+ " \t User details Json object "+userDetailsObject.toString());
			}catch (JSONException e) {
				LOG.error(LOG_PREFIX + "\t Json exception msg: "+e.getMessage() +" cause: "+e.getCause(),e);
			}
			
		}catch (Exception e) {
			LOG.error(LOG_PREFIX + "\t general exception msg: "+e.getMessage() +" cause: "+e.getCause(),e);
		}
		return userDetailsObject.toString();
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
			HttpPost httpPostRequest = new HttpPost(societiesRegistryBean.getServerURL());
			
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
		
		String url = "";
		try {
			
			url =  societiesRegistryBean.getServerURL() + "/";
			String cisParam = URLEncoder.encode(cis, "UTF-8").replace("+", "%20");
			String userIdParam = URLEncoder.encode(userId, "UTF-8").replace("+", "%20");
			url = url + userIdParam+"/"+ cisParam+ "/"+number;
			
			LOG.debug("before executing HTTP get- url:\t "+ url);
			HttpGet httpGetRequest = new HttpGet(url);
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
	    	LOG.error(LOG_PREFIX + " exception in method 'getMessagesInternal' ; generated URL is "+url);
		}
		
		LOG.debug(LOG_PREFIX + " finish postMessageInternal");
	    return responseString;
	}
	
	public void setICisManager(ICisManager cisManager){
		this.cisManager = cisManager;
	}
	
	public void setServerURL(String serverURL) {
		this.SERVER_URL = serverURL;
		
	}
	
	public void setCommManager(ICommManager commManager){
		this.commManager = commManager;
	}


	public SocietiesRegistryBean getSocietiesRegistryBean() {
		return societiesRegistryBean;
	}

	@Autowired
	public void setSocietiesRegistryBean(SocietiesRegistryBean societiesRegistryBean) {
		this.societiesRegistryBean = societiesRegistryBean;
	}
	
	/*
	void action(final HttpServletRequest request) {
	    final String paramValue = request.getSession().getServletContext().getInitParameter("paramName");
	    System.out.println(paramValue);
	}*/

	
	
}
