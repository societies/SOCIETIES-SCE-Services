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