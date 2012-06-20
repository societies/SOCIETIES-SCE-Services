package org.societies.webapp.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.societies.api.cis.management.ICis;
import org.societies.api.cis.management.ICisManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class TestVG2 {
	/**
	 * This method get called when user request for login page by using
	 * url http://localhost:8080/societies/login.html
	 * @return login jsp page and model object
	 */
	@Autowired
	private ICisManager cisManager;
	
	
	@RequestMapping(value="/d.html",method = RequestMethod.GET)
	public ModelAndView DefaultPage() {
		
		
		
		//model is nothing but a standard Map object
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("message", "!!!!!!!!!");
		
		List<ICis> cisList;
		try{
			cisList = cisManager.getCisList();
			model.put("message2", "??????");
			cisList.size();
		}catch (Exception e){
			e.printStackTrace();
		}

		return new ModelAndView("vg", model) ;
	}
	
	public void setICisManager(ICisManager cisManager){
		this.cisManager = cisManager;
	}
	
	public ICisManager getICisManager(){
		return this.cisManager;
	}
}

