package org.societies.schmoozer.controller;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.societies.schmoozer.model.LoginForm;
import org.societies.schmoozer.model.NewAccountForm;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;



@Controller
public class NewAccountController {

	
	
	
	
	/**
	 * This method get called when user request for login page by using
	 * url http://localhost:8080/societies/login.html
	 * @return login jsp page and model object
	 */
	@RequestMapping(value="/newaccount.html",method = RequestMethod.GET)
	public ModelAndView login() {
		//model is nothing but a standard Map object
		Map<String, Object> model = new HashMap<String, Object>();
		
		//data model object to be used for displaying form in html page 
		NewAccountForm newAccountForm = new NewAccountForm();
		model.put("newAccountForm", newAccountForm);		
		/*return modelandview object and passing login (jsp page name) and model object as
		 constructor */
		return new ModelAndView("newaccount", model) ;
	}
	/**
	 * This method get called when user submit the login page using submit button	
	 * @param loginForm java object with data entered by user
	 * @param result boolean result to check the data binding with object 
	 * @param model Map object passed to login page.
	 * @return loginsuccess page if sucess or login page for retry if failed
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/newaccount.html", method = RequestMethod.POST)
	public ModelAndView processLogin(@Valid NewAccountForm newAccountForm, BindingResult result,
    Map model) {

			if (result.hasErrors()) {
				model.put("result", "Login form error");
				return new ModelAndView("login", model);
			}			
			String userid = newAccountForm.getUserid();
			String password = newAccountForm.getPassword();
			String displayName = newAccountForm.getDisplayName();

			//TODO: We want to go and call the DA and make sure socieites id exists
			// Then if ok, we want to create a network account
			// then we want the users to enter more details
			model.put("name", displayName);
			boolean isAuthenticated=true;
			//TODO : do validation
			if(isAuthenticated){
				model.put("result", "New Account Created Successfull");
				return new ModelAndView("main", model);	
			}else{					
				model.put("result", "New Account Creation UnSuccessfull");
				return new ModelAndView("newaccount", model);
			}			
	}	

}
