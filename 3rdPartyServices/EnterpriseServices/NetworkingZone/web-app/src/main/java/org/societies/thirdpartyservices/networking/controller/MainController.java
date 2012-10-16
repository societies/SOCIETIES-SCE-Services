package org.societies.thirdpartyservices.networking.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.thirdpartyservices.networking.client.NetworkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;



@Controller
public class MainController {

	private static Logger log = LoggerFactory.getLogger(MainController.class);

	
	@Autowired
	private NetworkClient myNetClient;
	
	@RequestMapping(value="/main.html",method=RequestMethod.GET)
	public String main() {
		
		// Check if the user has created an profile for Networking Zone
		//If not, direct them to the Profile Page
		boolean bProfileExists = getMyNetClient().bProfileExists();
		
		
		return "main";
	}

	public NetworkClient getMyNetClient() {
		return myNetClient;
	}

	public void setMyNetClient(NetworkClient myNetClient) {
		this.myNetClient = myNetClient;
	}
	

}
