package org.societies.schmoozer.controller;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;


import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;



@Controller
public class MainController {

	
	
	@RequestMapping(value="/main.html",method=RequestMethod.GET)
	public String showForm(){
		return "main";
	}
	
	
	

}
