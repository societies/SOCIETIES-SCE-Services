package org.societies.thirdpartyservices.networking.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;


import org.societies.thirdpartyservices.networking.client.NetworkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;



@Controller
public class MainController {

	
	
	@RequestMapping(value="/main.html",method=RequestMethod.GET)
	public String main() {
		return "main";
	}
	

}
