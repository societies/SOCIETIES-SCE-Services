package org.societies.thirdpartyservices.networking.webapp.controller;

import java.util.HashMap;
import java.util.Map;
import javax.validation.Valid;

import org.societies.thirdpartyservices.networking.webapp.models.NetworkingForm;
import org.societies.thirdpartyservices.networking.webapp.models.NetworkingLoginForm;

//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoginController {

	/**
	 * OSGI service get auto injected
	 */

	@RequestMapping(value = "/login.html", method = RequestMethod.GET)
	public ModelAndView networking() {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("message", "Welcome to the Networking Page");

		NetworkingLoginForm cmNetworkingLoginForm = new NetworkingLoginForm();
		model.put("networkingLoginForm", cmNetworkingLoginForm);
		return new ModelAndView("login", model);

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/login.html", method = RequestMethod.POST)
	public ModelAndView networking(@Valid NetworkingLoginForm cmLoginForm,
			BindingResult result, Map model) {

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

}