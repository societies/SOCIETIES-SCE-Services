package org.societies.thirdpartyservices.networking.controller;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.thirdpartyservices.networking.client.NetworkClient;
import org.societies.thirdpartyservices.networking.model.FrontPageForm;
import org.societies.thirdpartyservices.networking.model.ZonePageForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;



@Controller
public class ZoneController {

	private static Logger log = LoggerFactory.getLogger(ZoneController.class);
	
	@Autowired
	NetworkClient networkClient;
	
	public NetworkClient getNetworkClient() {
		return networkClient;
	}
	public void setNetworkClient(NetworkClient networkClient) {
		this.networkClient = networkClient;
	}
	
	@RequestMapping(value="/zone.html",method=RequestMethod.GET)
	public ModelAndView zonePage() {
		
		log.info("ZoneController : GET start");
		Map<String, Object> model = new HashMap<String, Object>();
		ZonePageForm zoneForm  = new ZonePageForm();
		
		
		zoneForm.setDisplayName(getNetworkClient().getDisplayName());
		zoneForm.setCompanyName(getNetworkClient().getCompanyName());
		zoneForm.setDeptName(getNetworkClient().getDeptName());
		
		model.put("zoneForm", zoneForm);
		log.info("ZoneController getNetworkClient().getDisplayName() " + getNetworkClient().getDisplayName());
		model.put("displayname", getNetworkClient().getDisplayName());
		model.put("companyname", getNetworkClient().getCompanyName());
		model.put("dept", getNetworkClient().getDeptName());
		
		log.info("ZoneController : GET end");
		
		return new ModelAndView("zone", model) ;
	}
	
	/**
	 * This method get called when user submit the login page using submit button	
	 * @param loginForm java object with data entered by user
	 * @param result boolean result to check the data binding with object 
	 * @param model Map object passed to login page.
	 * @return loginsuccess page if sucess or login page for retry if failed
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/zone.html", method = RequestMethod.GET)
	public ModelAndView processGET(@Valid ZonePageForm frontForm, BindingResult result,
			  Map model_in) {	  
		log.info("ZoneController : processGET start");
		Map<String, Object> model = new HashMap<String, Object>();
		ZonePageForm zoneForm  = new ZonePageForm();
		
		
		zoneForm.setDisplayName(getNetworkClient().getDisplayName());
		zoneForm.setCompanyName(getNetworkClient().getCompanyName());
		zoneForm.setDeptName(getNetworkClient().getDeptName());
		
		model.put("zoneForm", zoneForm);
		log.info("ZoneController getNetworkClient().getDisplayName() " + getNetworkClient().getDisplayName());
		model.put("displayname", getNetworkClient().getDisplayName());
		model.put("companyname", getNetworkClient().getCompanyName());
		model.put("dept", getNetworkClient().getDeptName());
		
		log.info("ZoneController : processGET end");
		
		return new ModelAndView("zone", model) ;
	}	
	

	/**
	 * This method get called when user submit the login page using submit button	
	 * @param loginForm java object with data entered by user
	 * @param result boolean result to check the data binding with object 
	 * @param model Map object passed to login page.
	 * @return loginsuccess page if sucess or login page for retry if failed
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/zone.html", method = RequestMethod.POST)
	public ModelAndView processLogin(@Valid ZonePageForm frontForm, BindingResult result,
			  Map model_in) {	  
		log.info("ZoneController : POST start");
		Map<String, Object> model = new HashMap<String, Object>();
		ZonePageForm zoneForm  = new ZonePageForm();
		
		
		zoneForm.setDisplayName(getNetworkClient().getDisplayName());
		zoneForm.setCompanyName(getNetworkClient().getCompanyName());
		zoneForm.setDeptName(getNetworkClient().getDeptName());
		
		model.put("zoneForm", zoneForm);
		log.info("ZoneController getNetworkClient().getDisplayName() " + getNetworkClient().getDisplayName());
		model.put("displayname", getNetworkClient().getDisplayName());
		model.put("companyname", getNetworkClient().getCompanyName());
		model.put("dept", getNetworkClient().getDeptName());
		
		log.info("ZoneController : POST end");
		
		return new ModelAndView("zone", model) ;
	}	
	
}
