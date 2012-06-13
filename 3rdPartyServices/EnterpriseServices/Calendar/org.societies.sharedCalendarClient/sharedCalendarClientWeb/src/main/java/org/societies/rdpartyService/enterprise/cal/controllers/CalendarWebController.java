package org.societies.rdpartyService.enterprise.cal.controllers;

import java.util.HashMap;
import java.util.Map;

import org.societies.rdpartyService.enterprise.interfaces.IReturnedResultCallback;
import org.societies.rdpartyService.enterprise.interfaces.ISharedCalendarClientRich;
import org.societies.rdpartyservice.enterprise.sharedcalendar.SharedCalendarResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CalendarWebController {
	
	public CalendarWebController() {
		super();
		// TODO Auto-generated constructor stub
	}

	private SharedCalendarResult result = null;
	
	@Autowired
	private ISharedCalendarClientRich calClientService = null;
	
	private CalendarWebResultCallback cb = null;

	public ISharedCalendarClientRich getCalClientService() {
		return calClientService;
	}
	
	//@ServiceR eference(cardinality=ServiceReferenceCardinality.C0__1)
	public void setCalClientService(ISharedCalendarClientRich calClientService) {
		this.calClientService = calClientService;
	}
	
	/*
	 <xs:enumeration value="createCISCalendar"/>
      <xs:enumeration value="deleteCISCalendar"/>
      <xs:enumeration value="retrieveCISCalendarList"/>
      <xs:enumeration value="retrieveCISCalendarEvents"/>
      <xs:enumeration value="createEventOnCISCalendar"/>
      <xs:enumeration value="deleteEventOnCISCalendar"/>
      <xs:enumeration value="subscribeToEvent"/>
      <xs:enumeration value="findEvents"/>
      <xs:enumeration value="unsubscribeFromEvent"/>
      <xs:enumeration value="createPrivateCalendar"/>
      <xs:enumeration value="deletePrivateCalendar"/>
      <xs:enumeration value="createEventOnPrivateCalendar"/>
      <xs:enumeration value="retrieveEventsOnPrivateCalendar"/>
      <xs:enumeration value="deleteEventOnPrivateCalendar"/>
    */
	
	@RequestMapping("/getAllCisCalendars.do")
	public @ResponseBody String getContacts() {
		//calClientService.retrieveEventsPrivateCalendar(this.cb);
		this.cb = new CalendarWebResultCallback(this);
		this.calClientService.retrieveCISCalendars(this.cb, "jane.societies.local");
		return new String("{invoked: true}");
	}

	public SharedCalendarResult getResult() {
		return result;
	}

	public void setResult(SharedCalendarResult result) {
		this.result = result;
	}

}
