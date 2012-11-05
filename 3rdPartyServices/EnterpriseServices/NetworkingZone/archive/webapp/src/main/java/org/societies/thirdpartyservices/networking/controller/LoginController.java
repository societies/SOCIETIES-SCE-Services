package org.societies.thirdpartyservices.networking.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.societies.thirdpartyservices.networking.model.LoginForm;
import org.societies.thirdpartyservices.networking.model.NewAccountForm;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.ModelAndView;



@Controller
public class LoginController {

	
	
	
	@RequestMapping(value="/index.html",method=RequestMethod.GET)
	public ModelAndView defaultPage() {
		//model is nothing but a standard Map object
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("message", "Please login to your Societies account");
		//data model object to be used for displaying form in html page 
		LoginForm loginForm = new LoginForm();
		model.put("loginForm", loginForm);		
		/*return modelandview object and passing login (jsp page name) and model object as
		 constructor */
		return new ModelAndView("login", model) ;
	}
	
	
	/**
	 * This method get called when user request for login page by using
	 * url http://localhost:8080/societies/login.html
	 * @return login jsp page and model object
	 */
	@RequestMapping(value="/login.html",method = RequestMethod.GET)
	public ModelAndView login() {
		//model is nothing but a standard Map object
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("message", "Please login to your Societies account");
		//data model object to be used for displaying form in html page 
		LoginForm loginForm = new LoginForm();
		model.put("loginForm", loginForm);		
		/*return modelandview object and passing login (jsp page name) and model object as
		 constructor */
		return new ModelAndView("login", model) ;
	}
	/**
	 * This method get called when user submit the login page using submit button	
	 * @param loginForm java object with data entered by user
	 * @param result boolean result to check the data binding with object 
	 * @param model Map object passed to login page.
	 * @return loginsuccess page if sucess or login page for retry if failed
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/login.html", method = RequestMethod.POST)
	public ModelAndView processLogin(@Valid LoginForm loginForm, BindingResult result,
			  Map model) {	  
	
		
		
		//TODO : Fix this
		return new ModelAndView("main");
		
		/**
		  HttpServletRequest request = (HttpServletRequest) RequestContextHolder     .currentRequestAttributes()     .resolveReference(RequestAttributes.REFERENCE_REQUEST);  
		 
		
		
		/**
		  	if (!loginForm.getButtonLabel().contains("Login"))
		 
			{
				// go to create new account screen
				return new ModelAndView("newaccount", model);
			}
			
			String userid = loginForm.getUserId();
			String password = loginForm.getPassword();

			model.put("name", userid);
			boolean isAuthenticated=true;
			//TODO : do validation
			if(isAuthenticated){
				model.put("result", "Login Successfull");
				return new ModelAndView("main", model);	
			}else{					
				model.put("result", "Login UnSuccessfull");
				return new ModelAndView("login", model);
			}
			**/
		
	}	
	
	
	
}
