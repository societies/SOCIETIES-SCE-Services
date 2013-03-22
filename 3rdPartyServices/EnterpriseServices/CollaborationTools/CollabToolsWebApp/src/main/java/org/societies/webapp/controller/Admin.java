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
import org.societies.context.externalBrokerConnector.ExternalCtxBrokerConnector;
import org.societies.enterprise.collabtools.acquisition.*;
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
			int size = cisList.size();
			String result = "<br>"+"There are "+size+" CIS: "+"<br>";
			for (ICis list : cisList) {   
				result +="<table><tr><td><B>Name</B></td><td><B>Owner</B></td><td><B>ID</B></td></tr><tr>";
				result +="<td>"+ list.getName() +"</td>";
				result +="<td>"+list.getOwnerId()+"</td>";
				result +="<td>"+list.getCisId()+"</td>";
//	        	<td><input type="button" value="Services" onclick="updateForm('cis-fe7e5118-ab0c-4a5e-bf4e-230762f466dc.societies.local', 'GetServicesCis', 'servicediscovery.html')" ></td>
//	        </tr>

			}
//			result = context.getClass().toString();
			model.put("message", result);

			return new ModelAndView("default", model) ;
		}
		
		
//		@RequestMapping(value = "/time.html", method = RequestMethod.GET)
//		  public @ResponseBody String getTime(@RequestParam String name) {
//		    String result = "Time for " + name + " is " + new Date().toString();
//		    return result;
//		  }
		
		@RequestMapping(value = "/time.html", method = RequestMethod.GET)
		public @ResponseBody String getTime(@RequestParam String name) {
			String result;
			if (getCollabTools() == null){
				return result="Collabtools is not running";
			}
			if (!name.isEmpty()) {
				result = "Starting CollabTools with cisID: "+name;
				getCollabTools().initialCtx(name);
			}
			else {
				result = "cisID is empty, please check again";
			}
			return result;
		}

		@RequestMapping(value="/test.html",method = RequestMethod.GET)
		public ModelAndView pageLoad() {
			//model is nothing but a standard Map object
			Map<String, Object> model = new HashMap<String, Object>();

			return new ModelAndView("mobile", model) ;
		}
		
//		
//		@RequestMapping(value="/postMsg.html",method = RequestMethod.POST)
//		public @ResponseBody String page(@Valid MessageForm form ,BindingResult result, Map model) {
//				//model is nothing but a standard Map object
//				String result1 = "guy";
//				return result1;
//		}
//		
//	
			
			
		public void setICisManager(ICisManager cisManager){
			this.cisManager = cisManager;
		}
			
		public ICisManager getICisManager(){
			return this.cisManager;
		}


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
		
		public ContextSubscriber getCollabTools(){
			BundleContext bc = FrameworkUtil.getBundle(this.getClass()).getBundleContext();
			ServiceReference ref= bc.getServiceReference(ContextSubscriber.class.getName());
			return (ContextSubscriber)bc.getService(ref);
		}
		 
}