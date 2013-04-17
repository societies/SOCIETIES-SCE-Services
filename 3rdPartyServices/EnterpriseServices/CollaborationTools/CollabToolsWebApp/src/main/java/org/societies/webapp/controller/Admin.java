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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.societies.api.cis.management.ICis;
import org.societies.api.cis.management.ICisManager;
import org.societies.enterprise.collabtools.api.ICollabAppConnector;
import org.societies.enterprise.collabtools.api.ICollabApps;
import org.societies.enterprise.collabtools.api.IContextSubscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.osgi.context.BundleContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class Admin implements BundleContextAware{
	
	@Autowired
	private ICisManager cisManager;
	
	/**
		 * This method get called when user request for login page by using
		 * url http://localhost:8080/societies/login.html
		 * @return login jsp page and model object
		 */
		@RequestMapping(value="/default.html",method = RequestMethod.GET)
		public ModelAndView DefaultPage() {
			//model is nothing but a standard Map object
			Map<String, Object> model = new HashMap<String, Object>();
			
			List<ICis> cisList = new ArrayList<ICis>();
			cisList = cisManager.getCisList();
			List<String> cisNames = new ArrayList<String>();
			List<String> cisIdList = new ArrayList<String>();
			int size = cisList.size();
			String result = "<br>"+"There are "+size+" CIS: "+"<br>";
			result += "<form name=\"radioform\"\" method=\"get\">";
			for (ICis list : cisList) {   
				result +="<input type=\"radio\" id=\""+list.getCisId()+"\" name=\"cisIDListRadio\" onClick=\"setText('"+list.getCisId()+"')\" value=\""+list.getCisId()+"\">"+list.getName()+"   - Owner: "+list.getOwnerId()+"<br>";
				cisNames.add(list.getName());
				cisIdList.add(list.getCisId());
			}
			result += "</form>";
			model.put("message", result);
			model.put("cisNames", cisNames);
			model.put("cisIdList", cisIdList);

			return new ModelAndView("default", model) ;
		}
		
		@RequestMapping(value="/index.html",method = RequestMethod.GET)
		public ModelAndView IndexPage() {
			//model is nothing but a standard Map object
			Map<String, Object> model = new HashMap<String, Object>();
			
			List<ICis> cisList = new ArrayList<ICis>();
			cisList = cisManager.getCisList();
			int size = cisList.size();
			String result = "<br>"+"There are "+size+" CIS: "+"<br>";
			for (ICis list : cisList) {   
				result +="<table><tr><td><B>Name</B></td><td><B>Owner</B></td><td><B>ID</B></td></tr><tr>";
				result +="<td>"+ list.getName() +"</td>";
				result +="<td>"+list.getOwnerId()+"</td>";
				result +="<td>"+list.getCisId()+"</td>";
			}
			model.put("message", result);

			return new ModelAndView("index", model) ;
		}
		
		
		@RequestMapping(value = "/applications.html", method = RequestMethod.GET)
		public ModelAndView collabApps() {
			ICollabAppConnector[] collabAppsConnectors = getCollabAppsConnectors().getCollabAppConnectors();
			List<String> appnames = new ArrayList<String>();
			List<String> appserver = new ArrayList<String>();
			for (ICollabAppConnector apps : collabAppsConnectors){
				appnames.add(apps.getAppName());
				appserver.add(apps.getAppServerName());
			}
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("appnames", appnames);
			model.put("appserver", appserver);
			return new ModelAndView("applications", model) ;
		}
		
		@RequestMapping(value = "/checkcis.html", method = RequestMethod.GET)
		public @ResponseBody String checkCisID(@RequestParam String name) {
			String result;
			if (getCtxSubscriber() == null){
				return result="Collabtools is not running";
			}
			if (!name.isEmpty()) {
				result = "Starting CollabTools with cisID: "+name;
				getCtxSubscriber().initialCtx(name);
			}
			else {
				result = "CisID is empty, please check again";
			}
			return result;
		}

		@RequestMapping(value="/test.html",method = RequestMethod.GET)
		public ModelAndView mobileTest() {
			//model is nothing but a standard Map object
			Map<String, Object> model = new HashMap<String, Object>();

			return new ModelAndView("mobile1", model) ;
		}
		
		@RequestMapping(value="/iphone.html",method = RequestMethod.GET)
		public ModelAndView iphoneTest() {
			//model is nothing but a standard Map object
			Map<String, Object> model = new HashMap<String, Object>();
			List<ICis> cisList = new ArrayList<ICis>();
			cisList = cisManager.getCisList();
			int size = cisList.size();
			String cisname = null;
			String ownerID = null;
			String cisID = null;
			for (ICis list : cisList) {   
				cisname = list.getName();
				ownerID = list.getOwnerId();
				cisID = list.getCisId();
			}

			model.put("cisname", cisname);
			model.put("ownerID", ownerID);
			model.put("cisID", cisID);
			model.put("size", size);

			return new ModelAndView("iphone", model) ;
		}
		
		@RequestMapping(value="/android.html",method = RequestMethod.GET)
		public ModelAndView androidTest() {
			//model is nothing but a standard Map object
			Map<String, Object> model = new HashMap<String, Object>();

			return new ModelAndView("android", model) ;
		}
		
		@RequestMapping(value="/notification.html",method = RequestMethod.GET)
		public ModelAndView notificationTest() {
			//model is nothing but a standard Map object
			Map<String, Object> model = new HashMap<String, Object>();

			return new ModelAndView("notification", model) ;
		}
		
//		
//		@RequestMapping(value="/postMsg.html",method = RequestMethod.POST)
//		public @ResponseBody String page(@Valid MessageForm form ,BindingResult result, Map model) {
//				//model is nothing but a standard Map object
//				String result1 = "guy";
//				return result1;
//		}


		/* (non-Javadoc)
		 * @see org.springframework.osgi.context.BundleContextAware#setBundleContext(org.osgi.framework.BundleContext)
		 */
		@Override
		public void setBundleContext(BundleContext bundleContext) {
			// TODO Auto-generated method stub
//			this.context = bundleContext;
//			ServiceReference ref= bundleContext.getServiceReference(Activator.class.getName());
//			connector =(ExternalCtxBrokerConnector)bundleContext.getService(ref);			
		}
		
		public IContextSubscriber getCtxSubscriber(){
			BundleContext bc = FrameworkUtil.getBundle(this.getClass()).getBundleContext();
			ServiceReference<?> ref= bc.getServiceReference(IContextSubscriber.class.getName());
			return (IContextSubscriber)bc.getService(ref);
		}
		
		public ICollabApps getCollabAppsConnectors(){
			BundleContext bc = FrameworkUtil.getBundle(this.getClass()).getBundleContext();
			ServiceReference<?> ref= bc.getServiceReference(ICollabApps.class.getName());
			return (ICollabApps)bc.getService(ref);
		}
		
		@RequestMapping(value = "/setcollabapps.html", method = RequestMethod.GET)
		public @ResponseBody String setCollabAppsConnectors(@RequestParam String name) {
			BundleContext bc = FrameworkUtil.getBundle(this.getClass()).getBundleContext();
			ServiceReference<?> ref= bc.getServiceReference(ICollabApps.class.getName());
			ICollabApps collabApps = (ICollabApps)bc.getService(ref);
			return name;
		}
		 
}