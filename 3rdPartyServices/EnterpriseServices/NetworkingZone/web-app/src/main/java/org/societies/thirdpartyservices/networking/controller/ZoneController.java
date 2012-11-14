package org.societies.thirdpartyservices.networking.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.ext3p.networking.MemberDetails;
import org.societies.api.ext3p.networking.ShareInfo;
import org.societies.api.ext3p.networking.UserDetails;
import org.societies.thirdpartyservices.networking.client.NetworkClient;
import org.societies.thirdpartyservices.networking.model.ProfileForm;
import org.societies.thirdpartyservices.networking.model.ZoneForm;
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
public class ZoneController {

	private static Logger log = LoggerFactory.getLogger(ZoneController.class);
	
	private static int SHARE_PERSONAL = 0x00001;
	private static int SHARE_EMPLOYMENT = 0x00010;
	private static int SHARE_EMPLOY_HISTORY = 0x00100;
	private static int SHARE_EDU_HISTORY = 0x01000;
	private static int SHARE_ABOUT = 0x10000;
	
	private String currentFriendid = null;
	
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
	
		// Get members
		List<UserDetails> memberList = getNetworkClient().getCurrentZoneMemberList();
		
		model.put("memberlist", memberList);
			
		
		return new ModelAndView("zone", model) ;
	}
	
	@RequestMapping(value="/m_zone.html",method=RequestMethod.GET)
	public ModelAndView m_zonePage() {
		
		log.info("ZoneController : GET start");
		Map<String, Object> model = new HashMap<String, Object>();
	
		// Get members
		List<UserDetails> memberList = getNetworkClient().getCurrentZoneMemberList();
		
		model.put("memberlist", memberList);
			
		
		return new ModelAndView("m_zone", model) ;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/zone.html",method=RequestMethod.POST)
	public ModelAndView showfriendprofile(@Valid ZoneForm zoneform, BindingResult result, Map model, HttpSession session) {
		log.info("showfriendprofile : GET start");
		
		currentFriendid = new String(zoneform.getFriendid());
		ProfileForm profileForm = new ProfileForm();
		UserDetails userDets = getNetworkClient().getFriendDetails(zoneform.getFriendid());
		
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
		
		ShareInfo info = getNetworkClient().getFriendShareInfo(zoneform.getFriendid());
		
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
		
		List<String> notes = getNetworkClient().getnotes(currentFriendid);
		model.put("notes", notes);		
		
		return new ModelAndView("friendprofile", model);
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/m_zone.html",method=RequestMethod.POST)
	public ModelAndView m_showfriendprofile(@Valid ZoneForm zoneform, BindingResult result, Map model, HttpSession session) {
		log.info("showfriendprofile : GET start");
		
		currentFriendid = new String(zoneform.getFriendid());
		ProfileForm profileForm = new ProfileForm();
		UserDetails userDets = getNetworkClient().getFriendDetails(zoneform.getFriendid());
		
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
		
		ShareInfo info = getNetworkClient().getFriendShareInfo(zoneform.getFriendid());
		
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
		
		List<String> notes = getNetworkClient().getnotes(currentFriendid);
		model.put("notes", notes);		
		
		return new ModelAndView("m_friendprofile", model);
	}
	
	
	@RequestMapping(value="/addnote.html",method=RequestMethod.POST)
	public @ResponseBody JsonResponse addnote(@RequestBody String note){
	//public @ResponseBody JsonResponse updatePersonalDetails(@RequestBody UserPersonDetails details){

		JsonResponse res = new JsonResponse();
		int hash = 0;
		
		log.info("addnote.got details [" + note + "]");
		
		getNetworkClient().addnote(currentFriendid, note);
		
		res.setStatus("SUCCESS");
		
		return res;
	}
	
	@RequestMapping(value="/makeconnection.html",method=RequestMethod.GET)
	public ModelAndView  makeconection(){
	//public @ResponseBody JsonResponse updatePersonalDetails(@RequestBody UserPersonDetails details){


		
		getNetworkClient().addfriend(currentFriendid);
		
		log.info("ZoneController : makeconection");
		Map<String, Object> model = new HashMap<String, Object>();
	
		// Get members
		List<UserDetails> memberList = getNetworkClient().getCurrentZoneMemberList();
		
		model.put("memberlist", memberList);
			
		
		return new ModelAndView("zone", model) ;
	}
	
	@RequestMapping(value="/m_makeconnection.html",method=RequestMethod.GET)
	public ModelAndView  m_makeconection(){
	//public @ResponseBody JsonResponse updatePersonalDetails(@RequestBody UserPersonDetails details){


		
		getNetworkClient().addfriend(currentFriendid);
		
		log.info("ZoneController : makeconection");
		Map<String, Object> model = new HashMap<String, Object>();
	
		// Get members
		List<UserDetails> memberList = getNetworkClient().getCurrentZoneMemberList();
		
		model.put("memberlist", memberList);
			
		
		return new ModelAndView("m_zone", model) ;
	}
}
