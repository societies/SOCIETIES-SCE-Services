package org.societies.thirdpartyservices.networking.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.societies.api.ext3p.schema.networking.UserDetails;
import org.societies.thirdpartyservices.networking.client.NetworkClient;
import org.societies.thirdpartyservices.networking.model.FrontPageForm;
import org.societies.thirdpartyservices.networking.model.LoginForm;
import org.societies.thirdpartyservices.networking.model.NewAccountForm;
import org.societies.thirdpartyservices.networking.model.ZonePageForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.ModelAndView;



@Controller
public class FrontPageController {

	@Autowired
	NetworkClient networkClient;
	
	public NetworkClient getNetworkClient() {
		return networkClient;
	}
	public void setNetworkClient(NetworkClient networkClient) {
		this.networkClient = networkClient;
	}
	
	
	@RequestMapping(value="/front.html",method=RequestMethod.GET)
	public ModelAndView frontPage() {
		//model is nothing but a standard Map object
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("message", "Select a Zone to Join");
		//data model object to be used for displaying form in html page
		
		
		FrontPageForm frontForm = new FrontPageForm();
		frontForm.setZoneList(getNetworkClient().getZoneList());
		
		
		model.put("frontForm", frontForm);	
		model.put("zoneList", getNetworkClient().getZoneList());
		
		/*return modelandview object and passing login (jsp page name) and model object as
		 constructor */
		return new ModelAndView("front", model) ;
	}
	
	/**
	 * This method get called when user submit the login page using submit button	
	 * @param loginForm java object with data entered by user
	 * @param result boolean result to check the data binding with object 
	 * @param model Map object passed to login page.
	 * @return loginsuccess page if sucess or login page for retry if failed
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/front.html", method = RequestMethod.POST)
	public ModelAndView processLogin(@Valid FrontPageForm frontForm, BindingResult result,
			  Map model_in) {	  
	
				
		
		// Go and join the cis
		getNetworkClient().joinZoneCis(frontForm.getSelectedCis());
		
		Map<String, Object> model = new HashMap<String, Object>();
		ZonePageForm zoneForm  = new ZonePageForm();
		
		
		UserDetails userDet = getNetworkClient().getMyDetails();
		
		zoneForm.setDisplayName(userDet.getDisplayName());
		zoneForm.setCompanyName(userDet.getCompany());
		zoneForm.setDeptName(userDet.getDept());
		
		model.put("zoneForm", zoneForm);
	
		model.put("displayname", userDet.getDisplayName());
		model.put("companyname", userDet.getCompany());
		model.put("dept", userDet.getDept());
		
		
		
		return new ModelAndView("zone", model) ;
	}	
	
	
}
