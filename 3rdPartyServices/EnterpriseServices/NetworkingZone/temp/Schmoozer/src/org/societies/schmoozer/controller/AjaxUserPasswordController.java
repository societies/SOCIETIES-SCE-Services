package org.societies.schmoozer.controller;

import java.util.ArrayList;
import java.util.List;

import org.societies.schmoozer.model.AjaxUserPasswordDetails;
import org.societies.schmoozer.model.JsonResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class AjaxUserPasswordController {
	@RequestMapping(value="/updatepassword.html",method=RequestMethod.GET)
	public String showForm(){
		return "updatepassword";
	}
	
	@RequestMapping(value="/updatepassword.html",method=RequestMethod.POST)
	public @ResponseBody JsonResponse updatePassword(@ModelAttribute(value="details") AjaxUserPasswordDetails details, BindingResult result ){
		JsonResponse res = new JsonResponse();
		ValidationUtils.rejectIfEmpty(result, "userid", "Userid can not be empty.");
		ValidationUtils.rejectIfEmpty(result, "firstPassword", "First Password cannot be empty.");
		ValidationUtils.rejectIfEmpty(result, "secondPassword", "Second Password cannot be empty.");
		
		if(!result.hasErrors()){
			res.setStatus("SUCCESS");
		}else{
			res.setStatus("FAIL");
			res.setResult(result.getAllErrors());
		}
		
		return res;
	}
	
	@RequestMapping(value="/checkmatchingpassword.html",method=RequestMethod.GET)
	public String checkMatchingPassword(){
		return "checkmatchingpassword";
	}
	
	@RequestMapping(value="/checkmatchingpassword.html",method=RequestMethod.POST)
	public @ResponseBody JsonResponse checkMatchingPasswordPost(@ModelAttribute(value="details") AjaxUserPasswordDetails details, BindingResult result ){
		JsonResponse res = new JsonResponse();
		
		if (details.getFirstPassword().contentEquals(details.getSecondPassword()))
			res.setStatus("SUCCESS");
		else
			res.setStatus("FAIL");
		return res;
	}
	

}
