package org.societies.rdpartyService.enterprise.cal.controllers;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.Semaphore;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.societies.api.cis.management.ICis;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.ext3p.schema.sharedcalendar.Event;
import org.societies.api.ext3p.schema.sharedcalendar.SharedCalendarResult;
import org.societies.rdpartyService.enterprise.interfaces.ISharedCalendarClientRich;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Controller
public class CalendarWebController {
	
	private static long _DEADLINE = 3000;
	
	public CalendarWebController() {
		super();
		// TODO Auto-generated constructor stub
	}

	private SharedCalendarResult result = null;
	
	private Gson gson = new Gson();
	
	@Autowired
	private ISharedCalendarClientRich calClientService = null;
	@Autowired
	private ICisManager cisManagerService = null;
	
	private CalendarWebResultCallback cb = null;

	public ISharedCalendarClientRich getCalClientService() {
		return calClientService;
	}
	
	public void setCalClientService(ISharedCalendarClientRich calClientService) {
		this.calClientService = calClientService;
	}
	
	public ICisManager getCisManagerService() {
		return cisManagerService;
	}

	public void setCisManagerService(ICisManager cisManagerService) {
		this.cisManagerService = cisManagerService;
	}
	
	/*
	     
      <xs:enumeration value="createEventOnCISCalendar"/>
      <xs:enumeration value="deleteEventOnCISCalendar"/>
      <xs:enumeration value="subscribeToEvent"/>
      <xs:enumeration value="findEvents"/>
      <xs:enumeration value="unsubscribeFromEvent"/>
      <xs:enumeration value="createEventOnPrivateCalendar"/>
      <xs:enumeration value="retrieveEventsOnPrivateCalendar"/>
      <xs:enumeration value="deleteEventOnPrivateCalendar"/>
    */
	
	private boolean inDeadline(long start){
		return (System.currentTimeMillis()-start<CalendarWebController._DEADLINE);
	}
	
	private void wait4semaphore(){
		Semaphore s = this.cb.getSem();
		long start = System.currentTimeMillis();
		//TODO Add a deadline to avoid infinite wait
		while (!s.tryAcquire() && inDeadline(start)){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				//Do nothing
			}
		}		
	}
	
	@RequestMapping("/getAllRelevantCis.do")
	public @ResponseBody String getRelevantCis() {
		this.cb = new CalendarWebResultCallback(this);
		List<ICis> foundCiss = new ArrayList<ICis>();
		List<MyCisRecord> foundCisRecords = new ArrayList<MyCisRecord>();
		Type listType = new TypeToken<List<MyCisRecord>>(){}.getType();
		if (this.cisManagerService!=null){
			foundCiss = this.cisManagerService.getCisList();
		}
		for (ICis currentCis : foundCiss) {
			foundCisRecords.add(new MyCisRecord(currentCis.getName(), currentCis.getCisId()));
		}
		String ajaxResult = this.gson.toJson(foundCisRecords, listType);
		return ajaxResult;
	}
	
	@RequestMapping("/getAllCisCalendarsAjax.do")
	public @ResponseBody String retrieveCISCalendarList(@RequestParam(defaultValue="TestCIS", value="cisId") String cisId) {
		this.cb = new CalendarWebResultCallback(this);
		this.calClientService.retrieveCISCalendars(this.cb, cisId);
		this.wait4semaphore();
		String ajaxResult = this.gson.toJson(this.result);
		return ajaxResult;
	}
	
	@RequestMapping("/createCisCalendarAjax.do")
	public @ResponseBody String createCisCalendarAjax(@RequestParam(defaultValue="TestCIS", value="cisId") String cisId, @RequestParam(defaultValue="Calendar Summary", value="cisSummary") String summary) {
		this.cb = new CalendarWebResultCallback(this);
		this.calClientService.createCISCalendar(this.cb, summary, cisId);
		this.wait4semaphore();		
		String ajaxResult = this.gson.toJson(this.result);
		return ajaxResult;
	}
	
	@RequestMapping("/getCisCalendarEvents.do")
	public @ResponseBody String getCisCalendarEvents(@RequestParam(defaultValue="myCisCalendarId", value="calendarId") String calendarId) {
		this.cb = new CalendarWebResultCallback(this);
		this.calClientService.retrieveCISCalendarEvents(this.cb, calendarId);
		this.wait4semaphore();
		String ajaxResult = this.calClientService.createJSONOEvents(this.result.getEventList());
		return ajaxResult;
	}
	
	@RequestMapping("/createCisCalendarEvent.do")
	public @ResponseBody String createCisCalendarEvent(
			@RequestParam(defaultValue="myCisCalendarId", value="calendarId") String calendarId,
			@RequestParam(defaultValue="2012-09-10T10:00:00+0200", value="evt_start") String startDate,
			@RequestParam(defaultValue="2012-09-10T12:00:00+0200", value="evt_end") String endDate,
			@RequestParam(defaultValue="New Event", value="evtDescr") String evtDescr,
			@RequestParam(defaultValue="New Event Summary", value="evtSummary") String evtSummary,
			@RequestParam(defaultValue="Unknown", value="evtLocation") String evtLocation) {
		this.cb = new CalendarWebResultCallback(this);
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
		Event e = new Event();
		Date sDate = new Date();
		Date eDate = new Date();
		DatatypeFactory df = null;
		try {
            df = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException dce) {
            throw new IllegalStateException(
                "Exception while obtaining DatatypeFactory instance", dce);
        }
		try {
			sDate = (Date) formatter.parse(startDate);
			eDate = (Date) formatter.parse(endDate);
			GregorianCalendar gcStart = new GregorianCalendar();
			GregorianCalendar gcEnd = new GregorianCalendar();
			gcStart.setTimeInMillis(sDate.getTime());
            gcEnd.setTimeInMillis(eDate.getTime());
            e.setStartDate(df.newXMLGregorianCalendar(gcStart));
			e.setEndDate(df.newXMLGregorianCalendar(gcEnd));
			e.setEventDescription(evtDescr);
			e.setEventSummary(evtSummary);
			e.setLocation(evtLocation);			
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			return "{result: 'Invalid date format'}";
		}
		this.calClientService.createEventOnCISCalendar(this.cb, e, calendarId);
		this.wait4semaphore();
		String ajaxResult = this.gson.toJson(this.result.getEventId());
		return ajaxResult;
	}
	
	@RequestMapping("/createCssCalendarAjax.do")
	public @ResponseBody String createCssCalendarAjax(@RequestParam(defaultValue="CSS Calendar Summary", value="cssSummary") String summary) {
		//calClientService.retrieveEventsPrivateCalendar(this.cb);
		this.cb = new CalendarWebResultCallback(this);
		this.calClientService.createPrivateCalendar(this.cb, summary);
		this.wait4semaphore();
		String ajaxResult = this.gson.toJson(this.result);
		return ajaxResult;
	}
		
	@RequestMapping("/getPrivateEvents.do")
	public @ResponseBody String getPrivateEvents() {
		this.cb = new CalendarWebResultCallback(this);
		this.calClientService.retrieveEventsPrivateCalendar(this.cb);
		this.wait4semaphore();
		String ajaxResult = this.calClientService.createJSONOEvents(this.result.getEventList());
		return ajaxResult;
	}
	
	@RequestMapping("/deletePrivateCalendar.do")
	public @ResponseBody String deletePrivateCalendar() {
		this.cb = new CalendarWebResultCallback(this);
		this.calClientService.deletePrivateCalendar(this.cb);
		this.wait4semaphore();
		String ajaxResult = this.gson.toJson(this.result);
		return ajaxResult;
	}
	
	@RequestMapping("/deleteCisCalendar.do")
	public @ResponseBody String deleteCisCalendar(@RequestParam(defaultValue="myCisCalendarId", value="calendarId") String calendarId) {
		this.cb = new CalendarWebResultCallback(this);
		this.calClientService.deleteCISCalendar(this.cb, calendarId);
		this.wait4semaphore();
		String ajaxResult = this.gson.toJson(this.result);
		return ajaxResult;
	}
	
	@RequestMapping("/index.do")
	public String getHome() {
		return "Index";
	}


	public SharedCalendarResult getResult() {
		return result;
	}

	public void setResult(SharedCalendarResult result) {
		this.result = result;
	}

}
