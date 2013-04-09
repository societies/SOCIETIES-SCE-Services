/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.thirdparty.enterprise.sharedCalendar;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.ext3p.schema.sharedcalendar.Calendar;
import org.societies.api.ext3p.schema.sharedcalendar.Event;
import org.societies.api.ext3p.schema.sharedcalendar.SharedCalendarResult;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.thirdparty.sharedCalendar.api.ICalendarResultCallback;
import org.societies.thirdparty.sharedCalendar.api.ISharedCalendarClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * This is the client-class for the Shared Calendar
 *
 * @author <a href="mailto:sanchocsa@gmail.com">Sancho Rêgo</a> (PTIN)
 *
 */
public class SharedCalendarClient implements ISharedCalendarClient {

	static final Logger log = LoggerFactory.getLogger(SharedCalendarClient.class);
	private SharedCalendar sharedCalendar;
	private ICommManager commManager;
	private IIdentity myId;
	
	public SharedCalendar getSharedCalendar(){
		return this.sharedCalendar;
	}
	
	public void setSharedCalendar(SharedCalendar sharedCalendar){
		this.sharedCalendar = sharedCalendar;
	}
	
	public ICommManager getCommManager() {
		return commManager;
	}

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}
	
	public SharedCalendarClient() {
		log.info("Shared Calendar Client!");	
	}
	
	public void init() throws Exception{
		
		String myJid = getCommManager().getIdManager().getThisNetworkNode().getJid();
		
		if(log.isDebugEnabled())
			log.debug("Shared Calendar Client is running on JID: " + myJid);
		
		try {
			myId = getCommManager().getIdManager().fromJid(myJid);
		} catch (InvalidFormatException e) {
			log.error("Exception while starting the SharedCalendarClient!");
			e.printStackTrace();
		}	
	}
	
	@Async
	@Override
	public void createCISCalendar( ICalendarResultCallback calendarResultCallback, String calendarSummary, String CISId) {
		if(log.isDebugEnabled())
			log.debug("Creating a Calendar for CIS: " + CISId);
		try{
			
			IIdentity node = getCommManager().getIdManager().fromJid(CISId);
			String calendarId = getSharedCalendar().createCalendar(calendarSummary, node, myId);
		
			if(log.isDebugEnabled())
				log.debug("Created a calendar with Id: " + calendarId);
		
			SharedCalendarResult finalResult = new SharedCalendarResult();
			finalResult.setCalendarId(calendarId);
			
			calendarResultCallback.receiveResult(finalResult);	
			
		} catch(Exception ex){
			ex.printStackTrace();
		}
	}

	@Override
	public void retrieveCISCalendars(ICalendarResultCallback calendarResultCallback, String CISId) {

		if(log.isDebugEnabled())
			log.debug("retrieving CIS Calendars for CIS: " + CISId);
		
		try{
			IIdentity node = getCommManager().getIdManager().fromJid(CISId);

			List<Calendar> calendarList = getSharedCalendar().retrieveCalendarList(node, myId);
		
			SharedCalendarResult result = new SharedCalendarResult();
			result.setCalendarList(calendarList);
			
			if(log.isDebugEnabled())
				log.debug("Sending the result back: " + result.getCalendarList().size());
			
			calendarResultCallback.receiveResult(result);
			log.debug("Called callback!");

		} catch(Exception ex){
			ex.printStackTrace();
		}
		
	}

	@Async
	@Override
	public void deleteCISCalendar(
			ICalendarResultCallback calendarResultCallback, String calendarId, String CISId) {

		if(log.isDebugEnabled())
			log.debug("Deleting a Calendar for a CIS");
		
		try{
			
			IIdentity node = getCommManager().getIdManager().fromJid(CISId);
			Boolean deleteOk = getSharedCalendar().deleteCalendar(calendarId, node, myId);
			
			if(log.isDebugEnabled())
				log.debug("Deleted a calendar with Id: " + deleteOk);
			
			SharedCalendarResult result = new SharedCalendarResult();
			result.setLastOperationSuccessful(deleteOk);
			calendarResultCallback.receiveResult(result);
			log.debug("Called callback!");

		} catch(Exception ex){
			ex.printStackTrace();
		}

	}

	@Async
	@Override
	public void retrieveCISCalendarEvents(
			ICalendarResultCallback calendarResultCallback, String calendarId, String CISId) {
		
		if(log.isDebugEnabled())
			log.debug("Retrieving CIS Calendars for a CIS: " + CISId);
		try{
			
			IIdentity node = getCommManager().getIdManager().fromJid(CISId);

			List<Event> eventList = getSharedCalendar().retrieveCalendarEvents(calendarId, node, myId);
		
			if(log.isDebugEnabled())
				log.debug("Got event list!");
		
			SharedCalendarResult result = new SharedCalendarResult();
			result.setEventList(eventList);
			if(log.isDebugEnabled()){
				log.debug("calendarResultCallback: " + calendarResultCallback);
			}
			calendarResultCallback.receiveResult(result);
			
			log.debug("Called callback!");
			
		} catch(Exception ex){
			ex.printStackTrace();
		}
	}


	@Async
	@Override
	public void deleteEventOnCISCalendar(
			ICalendarResultCallback calendarResultCallback, String eventId,
			String calendarId, String CISId) {

		if(log.isDebugEnabled())
			log.debug("Deleting event on calendar!");
		
		try{
			
			IIdentity node = getCommManager().getIdManager().fromJid(CISId);
	
			Boolean deleteOk = getSharedCalendar().deleteEventOnCalendar(eventId, calendarId, node, myId);
			
			if(log.isDebugEnabled())
				log.debug("Deleted an event with Id: " + deleteOk);
			
			SharedCalendarResult result = new SharedCalendarResult();
			result.setLastOperationSuccessful(deleteOk);
			calendarResultCallback.receiveResult(result);
			
		} catch(Exception ex){
			ex.printStackTrace();
		}
	}

	@Async
	@Override
	public void retrieveEventsPrivateCalendar(ICalendarResultCallback calendarResultCallback, String calendarId, String nodeId) {
		
		if(log.isDebugEnabled())
			log.debug("Retrieving CIS Calendars for a CIS: " + nodeId);
		
		try{
			IIdentity node = getCommManager().getIdManager().fromJid(nodeId);

			List<Event> eventList = getSharedCalendar().retrieveCalendarEvents(calendarId, node, myId);
		
			if(log.isDebugEnabled())
				log.debug("Got event list!");
		
			SharedCalendarResult result = new SharedCalendarResult();
			result.setEventList(eventList);
			calendarResultCallback.receiveResult(result);
			
		} catch(Exception ex){
			ex.printStackTrace();
		}
	
	}

	@Async
	@Override
	public void createPrivateCalendar(
			ICalendarResultCallback calendarResultCallback, String calendarSummary) {
		
		if(log.isDebugEnabled())
			log.debug("Creating a Calendar for a My Node!");
		
		String calendarId = getSharedCalendar().createCalendar(calendarSummary, myId, myId);
		
		if(log.isDebugEnabled())
			log.debug("Created a calendar with Id: " + calendarId);
		
		SharedCalendarResult result = new SharedCalendarResult();
		result.setCalendarId(calendarId);
		calendarResultCallback.receiveResult(result);
	}

	@Async
	@Override
	public void deletePrivateCalendar(ICalendarResultCallback calendarResultCallback, String calendarId) {

		if(log.isDebugEnabled())
			log.debug("Deleting a Calendar for a My Node!");
		
		Boolean worked = getSharedCalendar().deleteCalendar(calendarId, myId, myId);
		
		if(log.isDebugEnabled())
			log.debug("Deleted a calendar with Id: " + worked);
		
		SharedCalendarResult result = new SharedCalendarResult();
		result.setLastOperationSuccessful(worked);
		calendarResultCallback.receiveResult(result);	}

	@Async
	@Override
	public void deleteEventOnPrivateCalendar(
			ICalendarResultCallback calendarResultCallback, String eventId, String calendarId) {
		
		Boolean deleteOk = getSharedCalendar().deleteEventOnCalendar(eventId, calendarId, myId, myId);
		
		if(log.isDebugEnabled())
			log.debug("Deleted an event with Id: " + deleteOk);
		
		SharedCalendarResult result = new SharedCalendarResult();
		result.setLastOperationSuccessful(deleteOk);
		calendarResultCallback.receiveResult(result);
	}

	@Async
	@Override
	public void subscribeToEvent(
			ICalendarResultCallback calendarResultCallback, String calendarId,
			String eventId, String nodeId, String subscriberId) {
		
		if(log.isDebugEnabled())
			log.debug("subscribing to an event...");
		
		try{
			
			IIdentity node = getCommManager().getIdManager().fromJid(nodeId);
			IIdentity subscriber = getCommManager().getIdManager().fromJid(subscriberId);
			
			Boolean subscribeOk = getSharedCalendar().subscribeToEvent(calendarId, eventId, node, subscriber);
			
			SharedCalendarResult finalResult = new SharedCalendarResult();
			finalResult.setSubscribingResult(subscribeOk);
			finalResult.setLastOperationSuccessful(subscribeOk);
			calendarResultCallback.receiveResult(finalResult);
			
		} catch(Exception ex){
			ex.printStackTrace();
		}

	}

	@Async
	@Override
	public void findEvents(ICalendarResultCallback calendarResultCallback,
			String calendarId, String keyWord) {
		// TODO Auto-generated method stub

	}

	@Async
	@Override
	public void unsubscribeFromEvent(
			ICalendarResultCallback calendarResultCallback, String calendarId,
			String eventId, String nodeId, String subscriberId) {

		if(log.isDebugEnabled())
			log.debug("unsubscribing to an event...");
		
		try{
			
			IIdentity node = getCommManager().getIdManager().fromJid(nodeId);
			IIdentity subscriber = getCommManager().getIdManager().fromJid(subscriberId);
			
			Boolean subscribeOk = getSharedCalendar().unsubscribeFromEvent(calendarId, eventId, node, subscriber);
			
			SharedCalendarResult finalResult = new SharedCalendarResult();
			finalResult.setSubscribingResult(subscribeOk);
			finalResult.setLastOperationSuccessful(subscribeOk);
			calendarResultCallback.receiveResult(finalResult);		
			
		} catch(Exception ex){
			ex.printStackTrace();
		}

	}

	@Async
	@Override
	public void createEventOnCISCalendar(
			ICalendarResultCallback calendarResultCallback, Event newEvent,
			String calendarId, String CISId) {

		if(log.isDebugEnabled())
			log.debug("Creating event on Calendar!");
		
		try{
			IIdentity node = getCommManager().getIdManager().fromJid(CISId);
			String eventId = getSharedCalendar().createEventOnCalendar(newEvent, calendarId, node, myId);
			
			if(log.isDebugEnabled())
				log.debug("EventId: " + eventId);
			
			SharedCalendarResult finalResult = new SharedCalendarResult();
			finalResult.setEventId(eventId);
			
			calendarResultCallback.receiveResult(finalResult);				
		} catch(Exception ex){
			ex.printStackTrace();
		}
	}

	@Async
	@Override
	public void createEventOnPrivateCalendar(
			ICalendarResultCallback calendarResultCallback, Event newEvent, String calendarId) {

		if(log.isDebugEnabled())
			log.debug("Creating event on Calendar!");
		
		try{
			String eventId = getSharedCalendar().createEventOnCalendar(newEvent, calendarId, myId, myId);
			
			if(log.isDebugEnabled())
				log.debug("EventId: " + eventId);
			
			SharedCalendarResult finalResult = new SharedCalendarResult();
			finalResult.setEventId(eventId);
			calendarResultCallback.receiveResult(finalResult);	
			
		} catch(Exception ex){
			ex.printStackTrace();
		}
		
	}

	/**
	 * This method create the list JSON objects (compatible with the presentation framewok jquery-weekcalendar-1.2.2) starting from a list of events.
	 * @param eventListToRender
	 * @return the String that represent the Json array
	 */
	@Override
	public String createJSONOEvents(List<Event> eventListToRender) {
		/*
		  "id":10182,
	      "start":"2009-05-03T14:00:00.000+10:00",
	      "end":"2009-05-03T15:00:00.000+10:00",
	      "title":"Dev Meeting"
	      */
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSZ");
			Gson gson = new GsonBuilder().create();
			String result = "";//gson.toJson(eventListToRender);
			//log.debug("JSON Representation1:"+result);
			
			JsonArray jsonArray=new JsonArray();
			JsonObject object= null;
			
			for (Event event : eventListToRender) {
				object=new JsonObject();
				object.addProperty("id", event.getEventId().toString());
				object.addProperty("start", sdf.format(XMLGregorianCalendarConverter.asDate(event.getStartDate().getValue())));
				object.addProperty("end", sdf.format(XMLGregorianCalendarConverter.asDate(event.getEndDate().getValue())));
				object.addProperty("title", event.getEventDescription().toString());
				jsonArray.add(object);
			}
			result = jsonArray.toString();
			log.debug("JSON Representation2:"+result);
			return result;
	}

	/* (non-Javadoc)
	 * @see org.societies.thirdparty.sharedCalendar.api.ISharedCalendarClient#retrieveCalendar(org.societies.thirdparty.sharedCalendar.api.ICalendarResultCallback, java.lang.String)
	 */
	@Override
	public void retrieveCalendar(
			ICalendarResultCallback calendarResultCallback, String CISId) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.thirdparty.sharedCalendar.api.ISharedCalendarClient#retrieveEvents(org.societies.thirdparty.sharedCalendar.api.ICalendarResultCallback, java.lang.String)
	 */
	@Override
	public void retrieveEvents(ICalendarResultCallback calendarResultCallback,
			String nodeId) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.thirdparty.sharedCalendar.api.ISharedCalendarClient#viewEvent(org.societies.thirdparty.sharedCalendar.api.ICalendarResultCallback, java.lang.String)
	 */
	@Override
	public void viewEvent(ICalendarResultCallback calendarResultCallback,
			String eventId) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.thirdparty.sharedCalendar.api.ISharedCalendarClient#createEvent(org.societies.thirdparty.sharedCalendar.api.ICalendarResultCallback, org.societies.api.ext3p.schema.sharedcalendar.Event, java.lang.String)
	 */
	@Override
	public void createEvent(ICalendarResultCallback calendarResultCallback,
			Event newEvent, String nodeId) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.thirdparty.sharedCalendar.api.ISharedCalendarClient#updateEvent(org.societies.thirdparty.sharedCalendar.api.ICalendarResultCallback, org.societies.api.ext3p.schema.sharedcalendar.Event)
	 */
	@Override
	public void updateEvent(ICalendarResultCallback calendarResultCallback,
			Event updatedEvent) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.thirdparty.sharedCalendar.api.ISharedCalendarClient#deleteEvent(org.societies.thirdparty.sharedCalendar.api.ICalendarResultCallback, java.lang.String)
	 */
	@Override
	public void deleteEvent(ICalendarResultCallback calendarResultCallback,
			String eventId) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.thirdparty.sharedCalendar.api.ISharedCalendarClient#subscribeToEvent(org.societies.thirdparty.sharedCalendar.api.ICalendarResultCallback, java.lang.String, java.lang.String)
	 */
	@Override
	public void subscribeToEvent(
			ICalendarResultCallback calendarResultCallback, String eventId,
			String subscriberId) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.thirdparty.sharedCalendar.api.ISharedCalendarClient#findEventsInCalendar(org.societies.thirdparty.sharedCalendar.api.ICalendarResultCallback, java.lang.String, org.societies.api.ext3p.schema.sharedcalendar.Event)
	 */
	@Override
	public void findEventsInCalendar(
			ICalendarResultCallback calendarResultCallback, String nodeId,
			Event searchEvent) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.thirdparty.sharedCalendar.api.ISharedCalendarClient#findEventsAll(org.societies.thirdparty.sharedCalendar.api.ICalendarResultCallback, org.societies.api.ext3p.schema.sharedcalendar.Event)
	 */
	@Override
	public void findEventsAll(ICalendarResultCallback calendarResultCallback,
			Event searchEvent) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.thirdparty.sharedCalendar.api.ISharedCalendarClient#unsubscribeFromEvent(org.societies.thirdparty.sharedCalendar.api.ICalendarResultCallback, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void unsubscribeFromEvent(
			ICalendarResultCallback calendarResultCallback, String eventId,
			String nodeId, String subscriberId) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.thirdparty.sharedCalendar.api.ISharedCalendarClient#getRecommendedEvents(org.societies.thirdparty.sharedCalendar.api.ICalendarResultCallback, java.lang.String)
	 */
	@Override
	public void getRecommendedEvents(
			ICalendarResultCallback calendarResultCallback, String subscriberId) {
		// TODO Auto-generated method stub
		
	}
	
	//This method converts a jid into an IIdentity
	private IIdentity getIIdentityFromJid(String jid) throws InvalidFormatException{		
		return getCommManager().getIdManager().fromJid(jid);
	}

}
