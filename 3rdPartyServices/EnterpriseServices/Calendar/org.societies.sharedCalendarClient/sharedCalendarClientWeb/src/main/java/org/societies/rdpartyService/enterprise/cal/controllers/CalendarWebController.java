package org.societies.rdpartyService.enterprise.cal.controllers;

import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;

import org.societies.rdpartyService.enterprise.interfaces.ISharedCalendarClientRich;
import org.societies.rdpartyservice.enterprise.sharedcalendar.SharedCalendarResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

@Controller
public class CalendarWebController {
	
	public CalendarWebController() {
		super();
		// TODO Auto-generated constructor stub
	}

	private SharedCalendarResult result = null;
	
	private Gson gson = new Gson();
	
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
	public  String getContacts() {
		//calClientService.retrieveEventsPrivateCalendar(this.cb);
		this.cb = new CalendarWebResultCallback(this);
		this.calClientService.retrieveCISCalendars(this.cb, "jane.societies.local");
		return "Index";
	}
	
	@RequestMapping("/getAllCisCalendarsAjax.do")
	public @ResponseBody String getContactsAjax(@RequestParam(defaultValue="TestCIS", value="cisId") String cisId) {
		//calClientService.retrieveEventsPrivateCalendar(this.cb);
		this.cb = new CalendarWebResultCallback(this);
		Semaphore s = this.cb.getSem();
		//TODO remove hard-coded string
		this.calClientService.retrieveCISCalendars(this.cb, cisId);
		//TODO Add a deadline to avoid infinite wait
		while (!s.tryAcquire()){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				//Do nothing
			}
		}
		String ajaxResult = this.gson.toJson(this.result);
		return ajaxResult;
	}
	
	@RequestMapping("/createCisCalendarAjax.do")
	public @ResponseBody String createCisCalendarAjax(@RequestParam(defaultValue="TestCIS", value="cisId") String cisId, @RequestParam(defaultValue="Calendar Summary", value="cisSummary") String summary) {
		//calClientService.retrieveEventsPrivateCalendar(this.cb);
		this.cb = new CalendarWebResultCallback(this);
		Semaphore s = this.cb.getSem();
		//TODO remove hard-coded string
		this.calClientService.createCISCalendar(this.cb, summary, cisId);
		//TODO Add a deadline to avoid infinite wait
		while (!s.tryAcquire()){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				//Do nothing
			}
		}
		String ajaxResult = this.gson.toJson(this.result);
		return ajaxResult;
	}

	public SharedCalendarResult getResult() {
		return result;
	}

	public void setResult(SharedCalendarResult result) {
		this.result = result;
	}

}
