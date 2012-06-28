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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.societies.api.cis.management.ICisManager;
import org.societies.webapp.model.MessageForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class TestVG {
	
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
			model.put("message", "Please login to your Societies account");

			return new ModelAndView("default", model) ;
		}
		
		
		@RequestMapping(value = "/time.html", method = RequestMethod.GET)
		  public @ResponseBody String getTime(@RequestParam String name) {
		    String result = "Time for " + name + " is " + new Date().toString();
		    return result;
		  }

		@RequestMapping(value="/test.html",method = RequestMethod.GET)
		public ModelAndView pageLoad() {
			//model is nothing but a standard Map object
			Map<String, Object> model = new HashMap<String, Object>();

			return new ModelAndView("mobile", model) ;
		}
		
		
		@RequestMapping(value="/postMsg.html",method = RequestMethod.POST)
		public @ResponseBody String page(@Valid MessageForm form ,BindingResult result, Map model) {
				//model is nothing but a standard Map object
				String result1 = "guy";
				return result1;
		}
		
		
		

/*			
		@RequestMapping(value="/mobile.html",method = RequestMethod.POST, 
						headers = {"content-type=application/json","content-type=application/xml"})
		public @ResponseBody String page(@RequestBody String body, Writer writer) {
			//model is nothing but a standard Map object
			String result = "guy";
			return result;
		}
	
*/
		/*
		@RequestMapping(value="/mobile.html",method = RequestMethod.POST, 
				 		headers = {"content-type=application/json","content-type=application/xml"})
		public @ResponseBody String page() {
			//model is nothing but a standard Map object
			 String result = "guy";
			return result;
		}*/
			
			
		public void setICisManager(ICisManager cisManager){
			this.cisManager = cisManager;
		}
			
		public ICisManager getICisManager(){
			return this.cisManager;
		}
}