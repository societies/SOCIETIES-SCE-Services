package org.societies.thirdpartyservices.networking.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.ext3p.networking.UserDetails;
import org.societies.thirdpartyservices.networking.client.NetworkClient;
import org.societies.thirdpartyservices.networking.model.ProfileForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;



@Controller
public class MyProfileController {

	private static Logger log = LoggerFactory.getLogger(MyProfileController.class);

	@Autowired
	private NetworkClient myNetClient;
	
	
	@RequestMapping(value="/myprofile.html",method=RequestMethod.GET)
	public ModelAndView myprofile() {
		Map<String, Object> model = new HashMap<String, Object>();
		ProfileForm profileForm = new ProfileForm();
		UserDetails userDets = getMyNetClient().getMyDetails();
		
		profileForm.setName(userDets.getDisplayName());
		profileForm.setDepartment(userDets.getDept());
		profileForm.setCompany(userDets.getCompany());
		profileForm.setPosition(userDets.getPosition());
		profileForm.setAbout(userDets.getAbout());
		
		model.put("profileForm", profileForm);
		
		return  new ModelAndView("myprofile", model);
	}
	
	@RequestMapping(value="/editprofile.html",method=RequestMethod.GET)
	public ModelAndView editprofile() {
		Map<String, Object> model = new HashMap<String, Object>();
		
		ProfileForm profileForm = new ProfileForm();
				
		UserDetails userDets = getMyNetClient().getMyDetails();
		profileForm.setName(userDets.getDisplayName());
		profileForm.setDepartment(userDets.getDept());
		profileForm.setCompany(userDets.getCompany());
		profileForm.setPosition(userDets.getPosition());
		profileForm.setAbout(userDets.getAbout());
		
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
		userDets.setDept(profileForm.getDepartment());
		userDets.setCompany(profileForm.getCompany());
		userDets.setPosition(profileForm.getPosition());
		userDets.setAbout(profileForm.getAbout());
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
	

}
