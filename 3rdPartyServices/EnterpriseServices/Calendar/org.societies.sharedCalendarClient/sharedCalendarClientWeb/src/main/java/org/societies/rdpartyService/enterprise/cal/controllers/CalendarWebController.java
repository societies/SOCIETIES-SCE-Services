package org.societies.rdpartyService.enterprise.cal.controllers;

import java.util.HashMap;
import java.util.Map;

import org.societies.rdpartyService.enterprise.interfaces.IReturnedResultCallback;
import org.societies.rdpartyService.enterprise.interfaces.ISharedCalendarClientRich;
import org.societies.rdpartyservice.enterprise.sharedcalendar.SharedCalendarResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.osgi.extensions.annotation.ServiceReference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CalendarWebController {
	
	public CalendarWebController() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * Private CallBack Class that handles ISharedCalendarClientRich invocations
	 *
	 * @author gspadotto
	 *
	 */
	private class CalendarWebResultCallback implements IReturnedResultCallback {
		/** The Controller to pass results to */
		private CalendarWebController wc = null;
		
		public CalendarWebResultCallback(CalendarWebController controller) {
			this.wc = controller;
		}

		@Override
		public void receiveResult(Object arg0) {
			if (arg0 instanceof SharedCalendarResult){
				this.wc.result = (SharedCalendarResult) arg0;
			}else{
				//Log some info...
			}
		}
		
	}
	
	
	
	private SharedCalendarResult result = null;
	
	@Autowired
	private ISharedCalendarClientRich calClientService = null;
	
	private CalendarWebResultCallback cb = new CalendarWebResultCallback(this);

	public ISharedCalendarClientRich getCalClientService() {
		return calClientService;
	}
	
	@ServiceReference
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
	public ModelAndView getContacts() {
		//calClientService.retrieveEventsPrivateCalendar(this.cb);
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("message", "Please login to your Societies account");
		return new ModelAndView("index", model);
	}

}
