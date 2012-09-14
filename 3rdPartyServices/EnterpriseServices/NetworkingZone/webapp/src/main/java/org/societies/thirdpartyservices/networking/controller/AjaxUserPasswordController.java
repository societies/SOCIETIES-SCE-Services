package org.societies.thirdpartyservices.networking.controller;

import java.util.ArrayList;
import java.util.List;



import org.societies.thirdpartyservices.networking.client.NetworkClient;
import org.societies.thirdpartyservices.networking.model.AjaxUserPasswordDetails;
import org.societies.thirdpartyservices.networking.model.AjaxUserDetails;
import org.societies.thirdpartyservices.networking.model.ZonePageForm;
import org.springframework.beans.factory.annotation.Autowired;
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
	
	
	
	@Autowired
	NetworkClient networkClient;
	
	public NetworkClient getNetworkClient() {
		return networkClient;
	}
	public void setNetworkClient(NetworkClient networkClient) {
		this.networkClient = networkClient;
	}
	
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
			
			//TODO : Sort out asynch stuff!
			try {
				
				/*
				UserRecordResult userRecRes = getNetworkingBackEnd().getNetworkingDirectory().getUserRecord(details.getUserid());
				
					userRecRes.getUserRec().setPassword(details.getFirstPassword());
					getNetworkingBackEnd().getNetworkingDirectory().updateUserRecord(userRecRes.getUserRec());
			*/
					res.setStatus("SUCCESS");
			
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				res.setStatus("FAIL");
				res.setResult(result.getAllErrors());
			}
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
	

	
	@RequestMapping(value="/getusers.html",method=RequestMethod.POST)
	public @ResponseBody JsonResponse getUsers( ){
		JsonResponse res = new JsonResponse();
		
		
		// Get users from database
		 List<AjaxUserDetails> userList = new ArrayList<AjaxUserDetails>(); 
		/*
		 List<UserRecord> dbuserList = null;

		try {
			dbuserList = getNetworkingBackEnd().getNetworkingDirectory().getRecords();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		AjaxUserDetails usrDet = null;
		for ( int i = 0; i < dbuserList.size(); i++)
		{
			usrDet = new AjaxUserDetails();
			usrDet.setUserid(dbuserList.get(i).getLogin());
			usrDet.setUsername(dbuserList.get(i).getDisplayName());
			userList.add(usrDet);
		}
		*/
		res.setStatus("SUCCESS");
		res.setResult(userList);
		return res;
	}
	
		

}
