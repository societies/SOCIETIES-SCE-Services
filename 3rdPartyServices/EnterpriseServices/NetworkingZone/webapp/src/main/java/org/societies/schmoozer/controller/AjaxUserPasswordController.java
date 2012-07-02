package org.societies.schmoozer.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.societies.schmoozer.model.AjaxUserDetails;
import org.societies.schmoozer.model.AjaxUserPasswordDetails;
import org.societies.schmoozer.model.AjaxUserPublicDetails;
import org.societies.schmoozer.model.JsonResponse;
import org.societies.thirdpartyservices.networking.NetworkBackEnd;
import org.societies.thirdpartyservices.schema.networking.directory.UserRecord;
import org.societies.thirdpartyservices.schema.networking.directory.UserRecordResult;
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
	NetworkBackEnd networkingBackEnd;
	
	public NetworkBackEnd getNetworkingBackEnd() {
		return networkingBackEnd;
	}
	public void setNetworkingBackEnd(NetworkBackEnd networkingBackEnd) {
		this.networkingBackEnd = networkingBackEnd;
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
			
			Future<UserRecordResult> userRecResFut = getNetworkingBackEnd().getNetworkingDirectory().getUserRecord(details.getUserid());
			try {
				UserRecordResult userRecRes = userRecResFut.get();
				userRecRes.getUserRec().setPassword(details.getFirstPassword());
				getNetworkingBackEnd().getNetworkingDirectory().updateUserRecord(userRecRes.getUserRec());
			
			
			res.setStatus("SUCCESS");
			
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				res.setStatus("FAIL");
				res.setResult(result.getAllErrors());
			} catch (ExecutionException e) {
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
		
		 Future<List<UserRecord>> dbUserListfut = getNetworkingBackEnd().getNetworkingDirectory().getRecords();
		List<UserRecord> dbuserList = null;
		try {
			dbuserList = dbUserListfut.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
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
		
		res.setStatus("SUCCESS");
		res.setResult(userList);
		return res;
	}

}
