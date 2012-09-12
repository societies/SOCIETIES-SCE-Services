package org.societies.thirdpartyservices.networking.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.ext3p.schema.networking.Education;
import org.societies.api.ext3p.schema.networking.Employment;
import org.societies.api.ext3p.schema.networking.UserDetails;
import org.societies.thirdpartyservices.networking.client.NetworkClient;
import org.societies.thirdpartyservices.networking.model.EduHistoryItem;
import org.societies.thirdpartyservices.networking.model.EmpHistoryItem;
import org.societies.thirdpartyservices.networking.model.UserPersonalDetails;
import org.societies.thirdpartyservices.networking.model.UserPublicDetails;
import org.societies.thirdpartyservices.networking.model.ZonePageForm;
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
		
		zoneForm.setDisplayName(getNetworkClient().getMyDetails().getDisplayName());
		zoneForm.setCompanyName(getNetworkClient().getMyDetails().getCompany());
		zoneForm.setDeptName(getNetworkClient().getMyDetails().getDept());
		
		model.put("zoneForm", zoneForm);
	
		model.put("displayname", getNetworkClient().getMyDetails().getDisplayName());
		model.put("companyname", getNetworkClient().getMyDetails().getCompany());
		model.put("dept", getNetworkClient().getMyDetails().getDept());
		
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
	@RequestMapping(value="/zone.html", method = RequestMethod.GET)
	public ModelAndView processGET(@Valid ZonePageForm frontForm, BindingResult result,
			  Map model_in) {	  
		log.info("ZoneController : processGET start");
		Map<String, Object> model = new HashMap<String, Object>();
		ZonePageForm zoneForm  = new ZonePageForm();
		
		
		zoneForm.setDisplayName(getNetworkClient().getMyDetails().getDisplayName());
		zoneForm.setCompanyName(getNetworkClient().getMyDetails().getCompany());
		zoneForm.setDeptName(getNetworkClient().getMyDetails().getDept());
		
		model.put("zoneForm", zoneForm);
	
		model.put("displayname", getNetworkClient().getMyDetails().getDisplayName());
		model.put("companyname", getNetworkClient().getMyDetails().getCompany());
		model.put("dept", getNetworkClient().getMyDetails().getDept());
		
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
	@RequestMapping(value="/zone.html", method = RequestMethod.POST)
	public ModelAndView processLogin(@Valid ZonePageForm frontForm, BindingResult result,
			  Map model_in) {	  
		log.info("ZoneController : POST start");
		Map<String, Object> model = new HashMap<String, Object>();
		ZonePageForm zoneForm  = new ZonePageForm();
		
		
		zoneForm.setDisplayName(getNetworkClient().getMyDetails().getDisplayName());
		zoneForm.setCompanyName(getNetworkClient().getMyDetails().getCompany());
		zoneForm.setDeptName(getNetworkClient().getMyDetails().getDept());
		
		model.put("zoneForm", zoneForm);
	
		model.put("displayname", getNetworkClient().getMyDetails().getDisplayName());
		model.put("companyname", getNetworkClient().getMyDetails().getCompany());
		model.put("dept", getNetworkClient().getMyDetails().getDept());
		
		log.info("ZoneController : POST end");
		
		return new ModelAndView("zone", model) ;
	}	
	
	
	@RequestMapping(value="/updateallinfoold.html",method=RequestMethod.POST)
	public @ResponseBody JsonResponse updateAllInfoold(@ModelAttribute(value="details") ZonePageForm details, BindingResult result ){
		JsonResponse res = new JsonResponse();
		
		
		UserDetails userDet = new UserDetails();
		userDet.setDisplayName(details.getDisplayName());
		userDet.setCompany(details.getCompanyName());
		userDet.setDept(details.getDeptName());
		List<Education> eduList = new ArrayList<Education>();
		Education edu = new Education();
		edu.setWhere(details.getEduhistCollege());
		edu.setWhat(details.getEduhistCourse());
		edu.setLevel(details.getEduhistGradYear());
		eduList.add(edu);
		userDet.setEducationhistory(eduList);
		
		List<Employment> empList = new ArrayList<Employment>();
		Employment emp = new Employment();
		emp.setWhere(details.getEmphistCompany());
		emp.setWhat(details.getEmphistDept());
		empList.add(emp);
		userDet.setEmploymenthistory(empList);
		
		//UserDetails retDet = getNetworkClient().updateMyDetails(userDet);
		UserDetails retDet = getNetworkClient().getUserdetails();
		
		if (retDet != null)
			res.setStatus("SUCCESS");
		else
			res.setStatus("FAIL");
		return res;
	}
/*	
	@RequestMapping(value="/updateallinfoold2.html",method=RequestMethod.POST)
	public @ResponseBody JsonResponse updateAllInfo(@ModelAttribute(value="userPublicDetails") UserPublicDetails details, BindingResult result ){
	public @ResponseBody JsonResponse updateAllInfoold2(@RequestBody UserPublicDetails details){

		JsonResponse res = new JsonResponse();
		
		
		UserDetails userDet = new UserDetails();
		userDet.setDisplayName(details.getDisplayName());
		
		log.info("updateAllInfo : details.getDisplayName() " + details.getDisplayName());
		
		userDet.setCompany(details.getCompanyName());
		log.info("updateAllInfo : details.getCompanyName() " + details.getCompanyName());
		
		userDet.setDept(details.getDeptName());
		log.info("updateAllInfo : details.getDeptName() " + details.getDeptName());
		
		List<Education> eduList = new ArrayList<Education>();
		Education edu = new Education();
		edu.setWhere(details.getEduhistCollege());
		
		log.info("updateAllInfo : details.getEduhistCollege() " + details.getEduhistCollege());
		
		edu.setWhat(details.getEduhistCourse());
		log.info("updateAllInfo : details.getEduhistCourse() " + details.getEduhistCourse());
		
		edu.setLevel(details.getEduhistGradYear());
		log.info("updateAllInfo : details.getEduhistGradYear() " + details.getEduhistGradYear());
		
		eduList.add(edu);
		userDet.setEducationhistory(eduList);
		
		List<Employment> empList = new ArrayList<Employment>();
		Employment emp = new Employment();
		emp.setWhere(details.getEmphistCompany());
		log.info("updateAllInfo : details.getEmphistCompany() " + details.getEmphistCompany());
		
		emp.setWhat(details.getEmphistDept());
		log.info("updateAllInfo : details.getEmphistDept() " + details.getEmphistDept());
		
		empList.add(emp);
		userDet.setEmploymenthistory(empList);
		
		UserDetails retDet = getNetworkClient().updateMyDetails(userDet);
		
		
		if (retDet != null)
			res.setStatus("SUCCESS");
		else
			res.setStatus("FAIL");
		return res;
	}
	
*/	
	@RequestMapping(value="/updatedisplayname.html",method=RequestMethod.POST)
	//public @ResponseBody JsonResponse updateDisplayName(@ModelAttribute(value="displayName") String displayName, BindingResult result ){
	public @ResponseBody JsonResponse updateDisplayName(@RequestBody String displayName){

		JsonResponse res = new JsonResponse();
		
		getNetworkClient().getUserdetails().setDisplayName(displayName);
		getNetworkClient().saveMyDetails();
		
		if (getNetworkClient().getUserdetails().getDisplayName() != null)
			res.setStatus("SUCCESS");
		else
			res.setStatus("FAIL");
		return res;
	}
	
	@RequestMapping(value="/updatepersonaldetails.html",method=RequestMethod.POST)
	public @ResponseBody JsonResponse updatePersonalDetails(@ModelAttribute(value="details") UserPersonalDetails details, BindingResult result ){
	//public @ResponseBody JsonResponse updatePersonalDetails(@RequestBody UserPersonDetails details){

		JsonResponse res = new JsonResponse();
		
		
		getNetworkClient().getUserdetails().setDisplayName(details.getDisplayName());
		getNetworkClient().getUserdetails().setCompany(details.getCompanyName());
		getNetworkClient().getUserdetails().setDept(details.getDeptName());
		
		getNetworkClient().saveMyDetails();
		
		
		if (getNetworkClient().getUserdetails().getDisplayName() != null)
			res.setStatus("SUCCESS");
		else
			res.setStatus("FAIL");
		return res;
	}
	
	
	@RequestMapping(value="/addemploymenthistoryitem.html",method=RequestMethod.POST)
	public @ResponseBody JsonResponse addEmploymentHistoryItem(@ModelAttribute(value="details") EmpHistoryItem details, BindingResult result ){
	//public @ResponseBody JsonResponse updatePersonalDetails(@RequestBody UserPersonDetails details){

		JsonResponse res = new JsonResponse();
		
		Employment empHist = new Employment();
		empHist.setWhere(details.getCompany());
		empHist.setWhat(details.getDepartment());
		getNetworkClient().getUserdetails().getEmploymenthistory().add(empHist);
		getNetworkClient().saveMyDetails();
		
		if (getNetworkClient().getUserdetails().getDisplayName() != null)
			res.setStatus("SUCCESS");
		else
			res.setStatus("FAIL");
		return res;
	}
	
	@RequestMapping(value="/addeducationhistoryitem.html",method=RequestMethod.POST)
	public @ResponseBody JsonResponse addEducationHistoryItem(@ModelAttribute(value="details") EduHistoryItem details, BindingResult result ){
	//public @ResponseBody JsonResponse updatePersonalDetails(@RequestBody UserPersonDetails details){

		JsonResponse res = new JsonResponse();
		
		Education eduHist = new Education();
		eduHist.setWhere(details.getCollege());
		eduHist.setWhat(details.getCourse());
		getNetworkClient().getUserdetails().getEducationhistory().add(eduHist);
		getNetworkClient().saveMyDetails();
		
		if (getNetworkClient().getUserdetails().getDisplayName() != null)
			res.setStatus("SUCCESS");
		else
			res.setStatus("FAIL");
		return res;
	}
	
	@RequestMapping(value="/addeducationhistoryitem.html",method=RequestMethod.POST)
	public @ResponseBody JsonResponse changeVisibilityEmployment(@ModelAttribute(value="details") String details, BindingResult result ){
	//public @ResponseBody JsonResponse updatePersonalDetails(@RequestBody UserPersonDetails details){

		JsonResponse res = new JsonResponse();
		
		res.setStatus("SUCCESS");
		
		return res;
	}
	
	@RequestMapping(value="/educationhistoryvisible.html",method=RequestMethod.POST)
	public @ResponseBody JsonResponse changeVisibilityEducation(@ModelAttribute(value="details") String details, BindingResult result ){
	//public @ResponseBody JsonResponse updatePersonalDetails(@RequestBody UserPersonDetails details){

		JsonResponse res = new JsonResponse();
		
		res.setStatus("SUCCESS");
		
		return res;
	}
	
	
}
