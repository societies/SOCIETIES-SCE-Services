package org.societies.thirdparty.enterprise.sharedCalendar.web;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.Future;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.management.ICis;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.ext3p.schema.sharedcalendar.Event;
import org.societies.api.ext3p.schema.sharedcalendar.SharedCalendarResult;
import org.societies.thirdparty.sharedCalendar.api.ISharedCalendarClient;
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
	static final Logger log = LoggerFactory.getLogger(CalendarWebController.class);
	
	public CalendarWebController() {
		super();
		// TODO Auto-generated constructor stub
	}

	private SharedCalendarResult result = null;
	
	private Gson gson = new Gson();
	
	@Autowired
	private ISharedCalendarClient calClientService = null;
	
	@Autowired
	private ICisManager cisManagerService = null;
	
	@Autowired
	private ICommManager commManager = null;
	
	private CalendarWebResultCallback cb = new CalendarWebResultCallback();

	public ISharedCalendarClient getCalClientService() {
		return calClientService;
	}
	
	public void setCalClientService(ISharedCalendarClient calClientService) {
		this.calClientService = calClientService;
	}

	public ICommManager getCommManager() {
		return commManager;
	}

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}
	
	public ICisManager getCisManagerService() {
		return cisManagerService;
	}

	public void setCisManagerService(ICisManager cisManagerService) {
		this.cisManagerService = cisManagerService;
	}
	
	/*
	     
      <xs:enumeration value="deleteEventOnCISCalendar"/>
      <xs:enumeration value="subscribeToEvent"/>
      <xs:enumeration value="findEvents"/>
      <xs:enumeration value="unsubscribeFromEvent"/>
      <xs:enumeration value="createEventOnPrivateCalendar"/>
      <xs:enumeration value="retrieveEventsOnPrivateCalendar"/>
      <xs:enumeration value="deleteEventOnPrivateCalendar"/>
    */
	
	
	@RequestMapping("/getAllRelevantCis.do")
	public @ResponseBody String getRelevantCis() {
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
		try{
			CalendarWebResultCallback callback = new CalendarWebResultCallback();
			this.calClientService.retrieveCISCalendars(callback, cisId);
			Thread.sleep(30000);
			String ajaxResult = this.gson.toJson(callback.getResult().getCalendarList());
			log.info("ajaxResult: " + ajaxResult);
		
		return ajaxResult;
		} catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
	}
	
	@RequestMapping("/createCisCalendarAjax.do")
	public @ResponseBody String createCisCalendarAjax(@RequestParam(defaultValue="TestCIS", value="cisId") String cisId, @RequestParam(defaultValue="Calendar Summary", value="cisSummary") String summary) {
		CalendarWebResultCallback callback = new CalendarWebResultCallback();
		this.calClientService.createCISCalendar(callback, summary, cisId);
		String ajaxResult = this.gson.toJson(callback.getResult().getCalendarId());
		log.info("ajaxResult: " + ajaxResult);
		return ajaxResult;
	}
	
	@RequestMapping("/getCisCalendarEvents.do")
	public @ResponseBody String getCisCalendarEvents(@RequestParam(defaultValue="TestCIS", value="cisId") String cisId, @RequestParam(defaultValue="myCisCalendarId", value="calendarId") String calendarId) {
		CalendarWebResultCallback callback = new CalendarWebResultCallback();
		this.calClientService.retrieveCISCalendarEvents(callback, calendarId, cisId);
		String ajaxResult = this.calClientService.createJSONOEvents(callback.getResult().getEventList());
		log.info("ajaxResult: " + ajaxResult);
		return ajaxResult;
	}
	
	@RequestMapping("/createCisCalendarEvent.do")
	public @ResponseBody String createCisCalendarEvent(
			@RequestParam(defaultValue="TestCIS", value="cisId") String cisId,
			@RequestParam(defaultValue="myCisCalendarId", value="calendarId") String calendarId,
			@RequestParam(defaultValue="2012-09-10T10:00:00+0200", value="evt_start") String startDate,
			@RequestParam(defaultValue="2012-09-10T12:00:00+0200", value="evt_end") String endDate,
			@RequestParam(defaultValue="New Event", value="evtDescr") String evtDescr,
			@RequestParam(defaultValue="New Event Summary", value="evtSummary") String evtSummary,
			@RequestParam(defaultValue="Unknown", value="evtLocation") String evtLocation) {
		CalendarWebResultCallback callback = new CalendarWebResultCallback();
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
		this.calClientService.createEventOnCISCalendar(callback, e, calendarId, cisId);
		String ajaxResult = this.gson.toJson(callback.getResult().getCalendarId());
		log.info("ajaxResult: "+ ajaxResult);
		return ajaxResult;
	}
	
	@RequestMapping("/deleteCisCalendarEvent.do")
	public @ResponseBody String deleteCisCalendarEvent(
			@RequestParam(defaultValue="TestCIS", value="cisId") String cisId,
			@RequestParam(defaultValue="myCisCalendarId", value="calendarId") String calendarId,
			@RequestParam(value="evtId") String evtId) {
		CalendarWebResultCallback callback = new CalendarWebResultCallback();
		this.calClientService.deleteEventOnCISCalendar(callback, evtId, calendarId,cisId);
		String ajaxResult = this.gson.toJson(callback.getResult().isLastOperationSuccessful());
		log.info("ajaxResult: " + ajaxResult);
		return ajaxResult;
	}
	
	@RequestMapping("/createCssCalendarEvent.do")
	public @ResponseBody String createCssCalendarEvent(
			@RequestParam(defaultValue="2012-09-10T10:00:00+0200", value="evt_start") String startDate,
			@RequestParam(defaultValue="2012-09-10T12:00:00+0200", value="evt_end") String endDate,
			@RequestParam(defaultValue="New Event", value="evtDescr") String evtDescr,
			@RequestParam(defaultValue="New Event Summary", value="evtSummary") String evtSummary,
			@RequestParam(defaultValue="Unknown", value="evtLocation") String evtLocation) {
		CalendarWebResultCallback callback = new CalendarWebResultCallback();
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
		String myId = getCommManager().getIdManager().getThisNetworkNode().getJid();
		this.calClientService.createEventOnPrivateCalendar(callback, e, myId);
		String ajaxResult = this.gson.toJson(callback.getResult().getEventId());
		log.info("ajaxResult: " + ajaxResult);
		return ajaxResult;
	}
	
	@RequestMapping("/deleteCssCalendarEvent.do")
	public @ResponseBody String deleteCssCalendarEvent(@RequestParam(defaultValue="myCisCalendarId", value="calendarId") String calendarId,
			@RequestParam(value="evtId") String evtId) {
		CalendarWebResultCallback callback = new CalendarWebResultCallback();
		this.calClientService.deleteEventOnPrivateCalendar(callback, evtId, calendarId);
		String ajaxResult = this.gson.toJson(callback.getResult().isLastOperationSuccessful());
		return ajaxResult;
	}
	
	@RequestMapping("/createCssCalendarAjax.do")
	public @ResponseBody String createCssCalendarAjax(@RequestParam(defaultValue="CSS Calendar Summary", value="cssSummary") String summary) {
		CalendarWebResultCallback callback = new CalendarWebResultCallback();
		this.calClientService.createPrivateCalendar(callback, summary);
		String ajaxResult = this.gson.toJson(callback.getResult().isLastOperationSuccessful());
		log.info("ajaxResult: " + ajaxResult);
		return ajaxResult;
	}
		
	@RequestMapping("/getPrivateEvents.do")
	public @ResponseBody String getPrivateEvents(@RequestParam(defaultValue="myCisCalendarId", value="calendarId") String calendarId) {
		CalendarWebResultCallback callback = new CalendarWebResultCallback();
		String myId = getCommManager().getIdManager().getThisNetworkNode().getJid();
		this.calClientService.retrieveEventsPrivateCalendar(callback,calendarId,myId);
		String ajaxResult = this.calClientService.createJSONOEvents(callback.getResult().getEventList());
		log.info("ajaxResult: " + ajaxResult);
		return ajaxResult;
	}
	
	@RequestMapping("/deletePrivateCalendar.do")
	public @ResponseBody String deletePrivateCalendar(@RequestParam(defaultValue="myCisCalendarId", value="calendarId") String calendarId) {
		CalendarWebResultCallback callback = new CalendarWebResultCallback();
		this.calClientService.deletePrivateCalendar(callback, calendarId);
		String ajaxResult = this.gson.toJson(callback.getResult().isLastOperationSuccessful());
		log.info("ajaxResult: " + ajaxResult);
		return ajaxResult;
	}
	
	@RequestMapping("/deleteCisCalendar.do")
	public @ResponseBody String deleteCisCalendar(
		@RequestParam(defaultValue="TestCIS", value="cisId") String cisId,
		@RequestParam(defaultValue="myCisCalendarId", value="calendarId") String calendarId) {
		CalendarWebResultCallback callback = new CalendarWebResultCallback();
		this.calClientService.deleteCISCalendar(callback, calendarId, cisId);
		String ajaxResult = this.gson.toJson(callback.getResult().isLastOperationSuccessful());
		log.info("ajaxResult: " + ajaxResult);
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
