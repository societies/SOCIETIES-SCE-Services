package org.societies.thirdpartyservices.networking.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.ext3p.networking.Education;
import org.societies.api.ext3p.networking.ShareInfo;
import org.societies.api.ext3p.networking.UserDetails;
import org.societies.thirdpartyservices.networking.client.NetworkClient;
import org.societies.thirdpartyservices.networking.model.ProfileForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;



@Controller
public class MyProfileController {

	private static Logger log = LoggerFactory.getLogger(MyProfileController.class);

	@Autowired
	private NetworkClient myNetClient;
	
	private static int SHARE_PERSONAL = 0x00001;
	private static int SHARE_EMPLOYMENT = 0x00010;
	private static int SHARE_EMPLOY_HISTORY = 0x00100;
	private static int SHARE_EDU_HISTORY = 0x01000;
	private static int SHARE_ABOUT = 0x10000;
	
	
	@RequestMapping(value="/myprofile.html",method=RequestMethod.GET)
	public ModelAndView myprofile() {
		Map<String, Object> model = new HashMap<String, Object>();
		ProfileForm profileForm = new ProfileForm();
		UserDetails userDets = getMyNetClient().getMyDetails();
		
		profileForm.setName(userDets.getDisplayName());
		profileForm.setEmail(userDets.getEmail());
		profileForm.setHomelocation(userDets.getHomelocation());
		
		profileForm.setDepartment(userDets.getDept());
		profileForm.setCompany(userDets.getCompany());
		profileForm.setPosition(userDets.getPosition());
		profileForm.setAbout(userDets.getAbout());
		profileForm.setEducationHistory(userDets.getEducationhistory());
		profileForm.setEmploymentHistory(userDets.getEmploymenthistory());
		
		
		profileForm.setAboutvisible(0);
		profileForm.setPersonalvisible(0);
		profileForm.setEmployvisible(0);
		profileForm.setEmphistvisible(0);
		profileForm.setEduhistvisible(0);
		
		ShareInfo info = getMyNetClient().getDefaultShareInfo();
		
		if ((info.getShareHash() & SHARE_ABOUT) == SHARE_ABOUT) 
			profileForm.setAboutvisible(1);
		
		if ((info.getShareHash() & SHARE_PERSONAL) == SHARE_PERSONAL) 
			profileForm.setPersonalvisible(1);
		
		if ((info.getShareHash() & SHARE_EMPLOYMENT)  == SHARE_EMPLOYMENT) 
			profileForm.setEmployvisible(1);
		
		
		if ((info.getShareHash() & SHARE_EMPLOY_HISTORY)  == SHARE_EMPLOY_HISTORY) 
			profileForm.setEmphistvisible(1);
		
		if ((info.getShareHash() & SHARE_EDU_HISTORY)  == SHARE_EDU_HISTORY) 
			profileForm.setEduhistvisible(1);
		
		model.put("profileForm", profileForm);
		
		return  new ModelAndView("myprofile", model);
	}
	
	@RequestMapping(value="/editprofile.html",method=RequestMethod.GET)
	public ModelAndView editprofile() {
		Map<String, Object> model = new HashMap<String, Object>();
		
		ProfileForm profileForm = new ProfileForm();
				
		UserDetails userDets = getMyNetClient().getMyDetails();
		profileForm.setName(userDets.getDisplayName());
		profileForm.setEmail(userDets.getEmail());
		profileForm.setHomelocation(userDets.getHomelocation());
		profileForm.setDepartment(userDets.getDept());
		profileForm.setCompany(userDets.getCompany());
		profileForm.setPosition(userDets.getPosition());
		profileForm.setAbout(userDets.getAbout());
			
		profileForm.setEducationHistory(userDets.getEducationhistory());
		profileForm.setEmploymentHistory(userDets.getEmploymenthistory());
		
		model.put("profileForm", profileForm);
		
		return new ModelAndView("editprofile", model);
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/editprofile.html",method=RequestMethod.POST)
	public ModelAndView saveprofile(@Valid ProfileForm profileForm, BindingResult result, Map model, HttpSession session) {
		
		log.info("profileForm.getName() : " + profileForm.getName());
		log.info("profileForm.getName() : " + profileForm.getCompany());
		log.info("profileForm.getDepartment() : " + profileForm.getDepartment());
		log.info("profileForm.getDepartment() : " + profileForm.getPosition());
		log.info("profileForm.getAbout() : " + profileForm.getAbout());
		
		UserDetails userDets = getMyNetClient().getMyDetails();
		
		userDets.setDisplayName(profileForm.getName());
		userDets.setEmail(profileForm.getEmail());
		userDets.setHomelocation(profileForm.getHomelocation());
		
		
		userDets.setDept(profileForm.getDepartment());
		userDets.setCompany(profileForm.getCompany());
		userDets.setPosition(profileForm.getPosition());
		userDets.setAbout(profileForm.getAbout());
		userDets.setEducationhistory(profileForm.getEducationHistory());
		userDets.setEmploymenthistory(profileForm.getEmploymentHistory());
		getMyNetClient().setMyDetails(userDets);
		
		model.put("profileForm", profileForm);
		return new ModelAndView("myprofile", model);
	}
	
	public NetworkClient getMyNetClient() {
		return myNetClient;
	}

	public void setMyNetClient(NetworkClient myNetClient) {
		this.myNetClient = myNetClient;
	}
	
	@RequestMapping(value="/addeducationhistoryitem.html",method=RequestMethod.POST)
	public @ResponseBody JsonResponse addEducationHistoryItem(@ModelAttribute(value="details") Education details, BindingResult result ){
	//public @ResponseBody JsonResponse updatePersonalDetails(@RequestBody UserPersonDetails details){

		JsonResponse res = new JsonResponse();
		
		Education eduHist = new Education();
		eduHist.setWhere(details.getWhere());
		eduHist.setWhat(details.getWhat());
		
		UserDetails usrDet = getMyNetClient().getMyDetails();
		usrDet.getEducationhistory().add(eduHist);
		getMyNetClient().setMyDetails(usrDet);
		
		//if (getMyNetClient().getUserdetails().getDisplayName() != null)
			res.setStatus("SUCCESS");
		//else
		//	res.setStatus("FAIL");
		return res;
	}
	
	@RequestMapping(value="/personalvisible.html",method=RequestMethod.POST)
	public @ResponseBody JsonResponse changeVisibilityPersonal(@RequestBody String visabilityFlag){
	//public @ResponseBody JsonResponse updatePersonalDetails(@RequestBody UserPersonDetails details){

		JsonResponse res = new JsonResponse();
		int hash = 0;
		
		log.info("changeVisibilityPersonal.got details [" + visabilityFlag + "]");
		
		ShareInfo info = getMyNetClient().getDefaultShareInfo();
		hash = info.getShareHash();
		if (visabilityFlag.startsWith("0"))
		{
			
			// turn it off
			if ((info.getShareHash() & SHARE_PERSONAL) == SHARE_PERSONAL) 
				hash = hash  - SHARE_PERSONAL;
				
		}
		else 
		{		// turn it on
			if ((info.getShareHash() & SHARE_PERSONAL) == 0) 
				hash = hash + SHARE_PERSONAL;
		}
		
		
		getMyNetClient().updateDefaultShareInfo(hash);
		res.setStatus("SUCCESS");
		
		return res;
	}
	
	@RequestMapping(value="/employmentvisible.html",method=RequestMethod.POST)
	public @ResponseBody JsonResponse changeVisibilityEmployment(@RequestBody String visabilityFlag)
	{
		JsonResponse res = new JsonResponse();
		int hash = 0;
		
		
		ShareInfo info = getMyNetClient().getDefaultShareInfo();
		hash = info.getShareHash();
		if (visabilityFlag.startsWith("0"))
		{
			
			// turn it off
			if ((info.getShareHash().byteValue() & SHARE_EMPLOYMENT)  == SHARE_EMPLOYMENT) 
				hash = hash  - SHARE_EMPLOYMENT;
				
		}
		else 
		{		// turn it on
			if ((info.getShareHash().byteValue() & SHARE_EMPLOYMENT) == 0) 
				hash = hash + SHARE_EMPLOYMENT;
		}
		
		
		getMyNetClient().updateDefaultShareInfo(hash);
		res.setStatus("SUCCESS");
		
		return res;
	}
	
	@RequestMapping(value="/employhistoryvisible.html",method=RequestMethod.POST)
	public @ResponseBody JsonResponse changeVisibilityEmployHistroy(@RequestBody String visabilityFlag)
	{
	
		JsonResponse res = new JsonResponse();
		int hash = 0;
		
		
		ShareInfo info = getMyNetClient().getDefaultShareInfo();
		hash = info.getShareHash();
		if (visabilityFlag.startsWith("0"))
		{
			
			// turn it off
			if ((info.getShareHash().byteValue() & SHARE_EMPLOY_HISTORY)  == SHARE_EMPLOY_HISTORY) 
				hash = hash  - SHARE_EMPLOY_HISTORY;
				
		}
		else 
		{		// turn it on
			if ((info.getShareHash().byteValue() & SHARE_EMPLOY_HISTORY) == 0) 
				hash = hash + SHARE_EMPLOY_HISTORY;
		}
		
		
		getMyNetClient().updateDefaultShareInfo(hash);
		res.setStatus("SUCCESS");
		
		return res;
	}
	
	@RequestMapping(value="/eduhistoryvisible.html",method=RequestMethod.POST)
	public @ResponseBody JsonResponse changeVisibilityEduHistroy(@RequestBody String visabilityFlag)
	{
	
		JsonResponse res = new JsonResponse();
		int hash = 0;
		
		
		ShareInfo info = getMyNetClient().getDefaultShareInfo();
		hash = info.getShareHash();
		if (visabilityFlag.startsWith("0"))
		{
			
			// turn it off
			if ((info.getShareHash().byteValue() & SHARE_EDU_HISTORY) == SHARE_EDU_HISTORY) 
				hash = hash  - SHARE_EDU_HISTORY;
				
		}
		else 
		{		// turn it on
			if ((info.getShareHash().byteValue() & SHARE_EDU_HISTORY) == 0) 
				hash = hash + SHARE_EDU_HISTORY;
		}
		
		
		getMyNetClient().updateDefaultShareInfo(hash);
		res.setStatus("SUCCESS");
		
		return res;
	}
	
	@RequestMapping(value="/aboutvisible.html",method=RequestMethod.POST)
	public @ResponseBody JsonResponse changeVisibilityAbout(@RequestBody String visabilityFlag)
	{
	
		JsonResponse res = new JsonResponse();
		int hash = 0;
		
		
		ShareInfo info = getMyNetClient().getDefaultShareInfo();
		hash = info.getShareHash();
		if (visabilityFlag.startsWith("0"))
		{
			
			// turn it off
			if ((info.getShareHash() & SHARE_ABOUT)  == SHARE_ABOUT) 
				hash = hash  - SHARE_ABOUT;
				
		}
		else 
		{		// turn it on
			if ((info.getShareHash() & SHARE_ABOUT) == 0) 
				hash = hash + SHARE_ABOUT;
		}
		
		
		getMyNetClient().updateDefaultShareInfo(hash);
		res.setStatus("SUCCESS");
		
		return res;
	}
	

}
