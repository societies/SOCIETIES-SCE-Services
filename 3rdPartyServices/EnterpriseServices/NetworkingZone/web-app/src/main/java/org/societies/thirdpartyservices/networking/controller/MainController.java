package org.societies.thirdpartyservices.networking.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.ext3p.networking.MemberDetails;
import org.societies.api.ext3p.networking.UserDetails;
import org.societies.api.ext3p.networking.ZoneDetails;
import org.societies.api.ext3p.networking.ZoneEvent;
import org.societies.thirdpartyservices.networking.client.NetworkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;



@Controller
public class MainController {

	private static Logger log = LoggerFactory.getLogger(MainController.class);

	
	@Autowired
	private NetworkClient myNetClient;
	int zoneIndex = 0;
	
	List<ZoneDetails> zoneDets = null;
	
	@RequestMapping(value="/main.html",method=RequestMethod.GET)
	public ModelAndView main() {
		
		Map<String, Object> model = new HashMap<String, Object>();
		// Check if the user has created an profile for Networking Zone
		//If not, direct them to the Profile Page
		
		getMyNetClient().InitService();
		boolean bProfileExists = getMyNetClient().bProfileExists();
		model.put("bProfileExists", bProfileExists);
		
		zoneDets = getMyNetClient().getZoneDetails();
		
		model.put("zonedets", zoneDets);
		
		return  new ModelAndView("main", model);
		
	}
	
	@RequestMapping(value="/m_main.html",method=RequestMethod.GET)
	public ModelAndView m_main() {
		
		Map<String, Object> model = new HashMap<String, Object>();
		// Check if the user has created an profile for Networking Zone
		//If not, direct them to the Profile Page
		
		getMyNetClient().InitService();
		boolean bProfileExists = getMyNetClient().bProfileExists();
		model.put("bProfileExists", bProfileExists);
		
		zoneDets = getMyNetClient().getZoneDetails();
		
		model.put("zonedets", zoneDets);
		
		return  new ModelAndView("m_main", model);
		
	}

	public NetworkClient getMyNetClient() {
		return myNetClient;
	}

	public void setMyNetClient(NetworkClient myNetClient) {
		this.myNetClient = myNetClient;
	}
	

	@RequestMapping(value="/{zonenumber}/gotozone.html",method=RequestMethod.GET)
	public ModelAndView gotozone(@PathVariable(value="zonenumber") String zonenumber, HttpSession session) 
	{
		zoneIndex = Integer.parseInt(zonenumber) - 1;
		if ((zoneIndex < 0) || (zoneIndex >= zoneDets.size()))
				zoneIndex = 0;
		
		Map<String, Object> model = new HashMap<String, Object>();
				
		getMyNetClient().joinZoneCis(zoneDets.get(zoneIndex).getZonename());
		model.put("cisname", zoneDets.get(zoneIndex).getZonename());
		model.put("zonelocation", zoneDets.get(zoneIndex).getZonelocationdisplay());
		model.put("topics", zoneDets.get(zoneIndex).getZonetopics());
		// Get members
		List<UserDetails> memberList = getMyNetClient().getCurrentZoneMemberList();
		model.put("memberlist", memberList);
		
		List<ZoneEvent> zoneeventList = getMyNetClient().getCurrentZoneEvents();
		model.put("zoneeventlist", zoneeventList);
		
		for ( int i = 0; i < memberList.size(); i++)
		{
			log.info(zoneDets.get(zoneIndex).getZonename() + " : member " + memberList.get(i));
		}
		return  new ModelAndView("zone", model);
	}

	@RequestMapping(value="/{zonenumber}/m_gotozone.html",method=RequestMethod.GET)
	public ModelAndView m_gotozone(@PathVariable(value="zonenumber") String zonenumber, HttpSession session) 
	{
		zoneIndex = Integer.parseInt(zonenumber) - 1;
		if ((zoneIndex < 0) || (zoneIndex >= zoneDets.size()))
				zoneIndex = 0;
		
		Map<String, Object> model = new HashMap<String, Object>();
				
		getMyNetClient().joinZoneCis(zoneDets.get(zoneIndex).getZonename());
		model.put("cisname", zoneDets.get(zoneIndex).getZonename());
		model.put("zonelocation", zoneDets.get(zoneIndex).getZonelocationdisplay());
		model.put("topics", zoneDets.get(zoneIndex).getZonetopics());
		// Get members
		List<UserDetails> memberList = getMyNetClient().getCurrentZoneMemberList();
		model.put("memberlist", memberList);
		
		List<ZoneEvent> zoneeventList = getMyNetClient().getCurrentZoneEvents();
		model.put("zoneeventlist", zoneeventList);
		
		for ( int i = 0; i < memberList.size(); i++)
		{
			log.info(zoneDets.get(zoneIndex).getZonename() + " : member " + memberList.get(i));
		}
		return  new ModelAndView("m_zone", model);
	}
	
}
