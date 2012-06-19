package org.societies.thirdpartyservices.networking.webapp.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;


import org.societies.thirdpartyservices.networking.webapp.models.NetworkingForm;
import org.societies.thirdpartyservices.networking.webapp.models.NetworkingLoginForm;

//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class NetworkingController {

	/**
	 * OSGI service get auto injected
	 */

	@RequestMapping(value = "/networking.html", method = RequestMethod.GET)
	public ModelAndView networking() {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("message", "Welcome to the Networking Page");

		NetworkingLoginForm cmNetworkingLoginForm = new NetworkingLoginForm();
		model.put("networkingLoginForm", cmNetworkingLoginForm);
		return new ModelAndView("login", model);

	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/networking.html", method = RequestMethod.POST)
	public ModelAndView networking(@Valid NetworkingLoginForm cmLoginForm,
			BindingResult result, @SuppressWarnings("rawtypes") Map model) {

		if (result.hasErrors()) {
			model.put("message", "Netowrking Form error");
			model.put("networkingLoginForm", cmLoginForm);
			return new ModelAndView("login", model);
		}

		NetworkingForm cmNetworkingForm = new NetworkingForm();
		cmNetworkingForm.setUserName(cmLoginForm.getUserName());

		model.put("networkingForm", cmNetworkingForm);

		return new ModelAndView("networking", model);

	}
	
	
	
		       private List<String> userList = new ArrayList<String>();

	        @RequestMapping(value="/AddUser.html",method=RequestMethod.GET)
	        public String showForm(){
	                return "AddUser";
	        }
/**
	        @RequestMapping(value="/AddUser.html",method=RequestMethod.POST)
	        public @ResponseBody JsonResponse addUser(@ModelAttribute(value="name") String name, BindingResult result ){
	        	JsonResponse res = new JsonResponse();
	                   if(!result.hasErrors()){
	                        userList.add(name);
	                        res.setStatus("SUCCESS");
	                        res.setResult(userList);
	                }else{
	                        res.setStatus("FAIL");
	                        res.setResult(result.getAllErrors());
	                }

	                return res;
	        }

	        
	        @RequestMapping(value="/GetUserDetails.html",method=RequestMethod.POST)
	        public @ResponseBody JsonResponse getUserDetails(@ModelAttribute(value="userId") String userId, BindingResult result ){
	        	JsonResponse res = new JsonResponse();
	                   if(!result.hasErrors()){
	                        userList.add(userId);
	                        res.setStatus("SUCCESS");
	                        res.setResult(userList);
	                }else{
	                        res.setStatus("FAIL");
	                        res.setResult(result.getAllErrors());
	                }

	                return res;
	        }
	*/        
}

