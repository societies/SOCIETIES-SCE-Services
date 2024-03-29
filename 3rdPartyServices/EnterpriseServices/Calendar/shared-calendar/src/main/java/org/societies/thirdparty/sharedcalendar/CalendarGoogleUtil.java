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
package org.societies.thirdparty.sharedcalendar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;


import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessProtectedResource;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAuthorizationRequestUrl;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.Calendar.Events.Get;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;

import org.societies.api.identity.IIdentity;
import org.societies.thirdparty.sharedcalendar.api.CalendarConverter;


/**
 * Describe your class here...
 * 
 * @author <a href="mailto:sanchocsa@gmail.com">Sancho Rêgo</a> (PTIN)
 * 
 * TODO Google is changing API once a week. Change the deprecated method invocation.
 */
public class CalendarGoogleUtil {

	private  String clientId;
	private  String clientSecret;
	private  String accessToken;
	private  String refreshToken;
	private  Calendar service = null;
	private Properties props;
	
	static final Logger log=LoggerFactory.getLogger(CalendarGoogleUtil.class);


	/**
	 * Constructor that initialize the class field
	 */
	public CalendarGoogleUtil(){
		this.readAndSetProperties();
		this.setUp();
	}

	/**
	 * @param clientId
	 * @param clientSecret
	 * @param accessToken
	 * @param refreshToken
	 */
	public CalendarGoogleUtil(String clientId, String clientSecret,
			String accessToken, String refreshToken) {
		super();
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.setUp();
	}

	public org.societies.thirdparty.sharedcalendar.api.schema.Calendar getCalendar(String calendarId){
		
		if(log.isDebugEnabled())
			log.debug("Getting calendar!");
		
		try {
			CalendarListEntry entry = getCalendarEntry(calendarId);
			return calendarFromCalendarEntry(entry);
		} catch (Exception e) {
			log.error("Couldn't get the calendar!");
			e.printStackTrace();
			return null;
		}
	}
	
	public org.societies.thirdparty.sharedcalendar.api.schema.Calendar getCalendarForNode(String nodeId){
		
		if(log.isDebugEnabled())
			log.debug("Getting calendar for node!");
		
		try {
			List<CalendarListEntry> entryList = retrieveAllCalendars();
			
			for(CalendarListEntry entry: entryList){
				if(entry.getDescription() != null && entry.getDescription().equals(nodeId)){
					if(log.isDebugEnabled())
						log.debug("Found calendar!");
					return calendarFromCalendarEntry(entry);
				}
			}
			
		} catch (Exception e) {
			log.error("Couldn't get the calendar! {}",e);
			e.printStackTrace();
			return null;
		}
		return null;
	}
	
	/**
	 * This method is used to create a calendar
	 * @param calendarSummary
	 * @return the calendarId
	 * @throws IOException
	 */
	public String createCalendar(String calendarSummary, String nodeId) throws IOException{
		com.google.api.services.calendar.model.Calendar calendar = new com.google.api.services.calendar.model.Calendar();
		
		String result = null;
		
		calendar.setSummary(calendarSummary);
		calendar.setDescription(nodeId);
		
		log.debug("Creating calendar : {} : {}", calendar.getSummary(), calendar.getDescription() );
		try {
			com.google.api.services.calendar.model.Calendar createdCalendar = service.calendars().insert(calendar).execute();
			result = createdCalendar.getId();
		} catch (IOException e) {
			log.error("Unable to create Calendar with summary "+calendarSummary, e);
		} 
		return result;
	}
	
	
	/**
	 * Delete an existing calendar
	 * @param calendarId
	 * @throws IOException
	 */
	public void deleteCalendar(String calendarId) throws IOException{
		try {
			service.calendars().delete(calendarId).execute();
		} catch (IOException e) {
			log.error("Unable to delete calendar with id '{}': {}",calendarId, e);
			throw e;
		}
	}
	
	/**
	 * This method is used to create an event inside a specified calendar
	 * the method is not exposed to the Societies clients but can be used only by the 3rd party service provider
	 * @param calendarId
	 * @param eventTitle
	 * @param description
	 * @param startDate
	 * @param endDate
	 * @param attendeeName
	 * @param attendeeEmail
	 * @return
	 * @throws IOException
	 */
	public String createEvent(String calendarId, String eventTitle,
			String description, Date startDate, Date endDate,String location, String creatorId, String nodeId) throws IOException {
		String result = null;
		Event event = new Event();
		event.setSummary(eventTitle);
		event.setDescription(description);

		// set dates

		DateTime start = new DateTime(startDate,TimeZone.getDefault());
		event.setStart(new EventDateTime().setDateTime(start));

		DateTime end = new DateTime(endDate, TimeZone.getDefault());
		event.setEnd(new EventDateTime().setDateTime(end));

		event.setLocation(location);
		
        Event.ExtendedProperties extendedProperties = new Event.ExtendedProperties();
        Map<String, String> sharedExtendedProperties = new HashMap<String, String>();
        sharedExtendedProperties.put("creatorId",creatorId);
        sharedExtendedProperties.put("nodeId", nodeId);
        extendedProperties.setShared(sharedExtendedProperties);
        
        event.setExtendedProperties(extendedProperties);
        
        if(log.isDebugEnabled()){
        	log.debug("Event title: {}",eventTitle);
        	log.debug("Event description: {}",description);
        	log.debug("Event creatorId: {}",creatorId);
        	log.debug("Event calendarId: {}",calendarId);
        	log.debug("Event nodeId: {}",nodeId);
        	log.debug("Event location: {}",location);
        }
        
      //  List<EventAttendee> attendeesList = new ArrayList<EventAttendee>();
       // event.setAttendees(attendeesList);
        
		// Store event
		try {
			Event createdEvent = service.events().insert(calendarId, event)
					.execute();
			result = createdEvent.getId();
		} catch (IOException e) {
			log.error("Unable to create event: {}", e);
		}
		
		return result;
	}
	
	public org.societies.thirdparty.sharedcalendar.api.schema.Event getEvent(String eventId, String calendarId){
		log.debug("Get event {} from Google!", eventId);
		org.societies.thirdparty.sharedcalendar.api.schema.Event returnedEvent = null;
		
		try{
			Event googleEvent = this.findEventUsingId(calendarId, eventId);
			returnedEvent = eventFromGoogleEvent(googleEvent,calendarId);
		} catch(Exception ex){
			log.error("Exception trying to get event from Google!");
			ex.printStackTrace();
		}
		
		return returnedEvent;
	}
	
	public void deleteEvent(String calendarId, String eventId) throws IOException{
		try {
			log.debug("Delete Event {} from Calendar {}", eventId, calendarId);			
			service.events().delete(calendarId, eventId).execute();
		} catch (IOException e) {
			log.error("Unable to delete event with id '{}' from calendar with id '{}': " + e.getMessage(), eventId, calendarId);
			throw e;
		}
	}
	
	/**
	 * Update an existing event
	 * @throws IOException 
	 */
	public org.societies.thirdparty.sharedcalendar.api.schema.Event updateEvent(org.societies.thirdparty.sharedcalendar.api.schema.Event eventToUpdate) throws IOException{
		
		log.debug("Update Event {} from Calendar {}", eventToUpdate.getEventId(), eventToUpdate.getCalendarId());
		
		Event googleEvent = findEventUsingId(eventToUpdate.getCalendarId(), eventToUpdate.getEventId());
		
		googleEvent.setDescription(eventToUpdate.getDescription());
		googleEvent.setSummary(eventToUpdate.getName());
		googleEvent.setLocation(eventToUpdate.getLocation());
		
		Date startDate = eventToUpdate.getStartDate();
		DateTime start = new DateTime(startDate,TimeZone.getDefault());
		googleEvent.setStart(new EventDateTime().setDateTime(start));

		Date endDate = eventToUpdate.getEndDate();
		DateTime end = new DateTime(endDate, TimeZone.getDefault());
		googleEvent.setEnd(new EventDateTime().setDateTime(end));
		
		org.societies.thirdparty.sharedcalendar.api.schema.Event updatedEvent = null;
		
		try {
			Event updateGoogleEvent = service.events().update(eventToUpdate.getCalendarId(), googleEvent.getId(), googleEvent).execute();
			updatedEvent = this.eventFromGoogleEvent(updateGoogleEvent,eventToUpdate.getCalendarId());
		} catch (IOException e) {
			log.error("Unable to update event '"+googleEvent.getId()+"' in calendar with id '"+eventToUpdate.getCalendarId()+"'", e);
			throw e;
			
		} catch(Exception ex){
			log.error("Exception while updating the event: " + ex);
			ex.printStackTrace();
		}
		
		return updatedEvent;
	}
	
	/**
	 * Used to subscribe an event
	 * 
	 */
	
	public boolean subscribeEvent(String calendarId, String eventId, IIdentity subscriber){
		
		log.debug("Subscribing to an event. {}", eventId);
		
		try{			
			Event event = findEventUsingId(calendarId, eventId);
			
			log.debug("Creating event Attendee for {}",subscriber);
			
			EventAttendee eventAttendee = createEventAttendee(subscriber);
			
			if (event.getAttendees() != null) {
				event.getAttendees().add(eventAttendee);
			} else {
				List<EventAttendee> attendeesList = new ArrayList<EventAttendee>();
				attendeesList.add(eventAttendee);
				event.setAttendees(attendeesList);
			}

			updateEvent(calendarId, event);
		} catch(IOException ex){
			log.error("Exception while subscribing!");
			return false;
		}
		
		return true;

	}
	
	public boolean unsubscribeEvent(String calendarId, String eventId, IIdentity subscriber){
		
		log.debug("UnSubscribing to an event {}", eventId);
		
		boolean unsubscriptionOk = false;
		
		try{

			Event event = findEventUsingId(calendarId, eventId);

			List<EventAttendee> tmpAttendeeList = event.getAttendees();
			EventAttendee attendeeToRemove = null;
			
			for (EventAttendee ea : tmpAttendeeList) {
				if (subscriber.getBareJid().equalsIgnoreCase(ea.getDisplayName())) {
					attendeeToRemove = ea;
					break;
				}
			}
			
			boolean foundAndRemoved = (attendeeToRemove != null && tmpAttendeeList.remove(attendeeToRemove));

			
			if(foundAndRemoved){
				event.setAttendees(tmpAttendeeList);
				updateEvent(calendarId, event);
				
				log.debug("Removed subscription to event {}!",event.getSummary());
				
				unsubscriptionOk = true;
			} else{
				log.debug("Removing subscription to event {} failed!",event.getSummary());
			}

		} catch(IOException ex){
			log.error("Exception while unsubscribing!");
			ex.printStackTrace();
		}
		
		return unsubscriptionOk;

	}
	/**
	 * This method returns all calendars of the user associated to the Google account
	 * @return
	 * @throws Exception
	 */
	public List<org.societies.thirdparty.sharedcalendar.api.schema.Event> retrieveAllEvents(String calendarId)
			throws IOException {
		
		List<Event> returnedList = retrieveAllEventsForCalendar(calendarId);
		return eventListFromGoogleEventList(returnedList,calendarId);
		
	}
	
	/**
	 * This methods cleans all events in a calendar
	 */
	public void cleanCalendar(String calendarId) throws Exception{
		log.debug("Cleaning a calendar of all Events! : " + calendarId);
		
		List<Event> allEvents = retrieveAllEventsForCalendar(calendarId);
		
		for(Event event: allEvents)
			deleteEvent(calendarId,event.getId());
		
	}
	
	public List<org.societies.thirdparty.sharedcalendar.api.schema.Event> searchForEvent(String calendarId, org.societies.thirdparty.sharedcalendar.api.schema.Event searchEvent){
	
		log.debug("Searching for events in Google, for calendar: {}", calendarId);
		
		List<Event> eventList = new ArrayList<Event>();
		Events events = null;
		try {

			com.google.api.services.calendar.Calendar.Events.List question = service.events().list(calendarId);
			SimpleDateFormat formatTime=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

			if(searchEvent.getStartDate() != null){

				String fromTime =formatTime.format(searchEvent.getStartDate());
				log.debug("We have a starting date: {}",fromTime);
				question.setTimeMin(fromTime);
			}
			
			if(searchEvent.getEndDate() != null){
				String endTime =formatTime.format(searchEvent.getEndDate());
				log.debug("We have a stopping date: {}",endTime);
				question.setTimeMax(endTime);
			}
			
			if(searchEvent.getName() != null && !searchEvent.getName().isEmpty()){
				log.debug("We have a keyword: {}",searchEvent.getName());
				
				question.setQ(searchEvent.getName());
			}
			
			events = question.execute();
			while (true) {
				List<Event> eventsList = events.getItems();
				if (eventsList == null){
					log.warn("Empty Events List");
					break;
				}
				for (Event event : eventsList) {
					boolean fulfillsCriteria = true;
					if(searchEvent.getCreatorId() != null && !searchEvent.getCreatorId().isEmpty()){
						if(!event.getExtendedProperties().get("creatorId").equals(searchEvent.getCreatorId())){
							fulfillsCriteria = false;
						}
					}

					if(searchEvent.getLocation() != null && !searchEvent.getLocation().isEmpty()){
						if(event.getLocation() != null && !event.getLocation().equals(searchEvent.getLocation())){
							fulfillsCriteria = false;
						}
						
						if(event.getLocation() == null)
							fulfillsCriteria = false;
					}
					
					if(fulfillsCriteria)
						eventList.add(event);
						
				}
				String pageToken = events.getNextPageToken();
				if (pageToken != null && !pageToken.isEmpty()) {
					try {
						events = question.setPageToken(pageToken).execute();
					} catch (IOException e) {
						log.error("Unable to retrieve further events", e);
						break;
					}
				} else {
					break;
				}
			}
			
		} catch (Exception e) {
			log.error("Unable to query calendar with id "+calendarId, e);
		}	
				
		return eventListFromGoogleEventList(eventList,calendarId);
		
	}
	
	/**
	 * This method returns all calendars of the user associated to the Google account
	 * @return
	 * @throws Exception
	 */	
	private CalendarListEntry getCalendarEntry(String calendarId) throws Exception {
				
		CalendarListEntry calendarEntry;
		
		try {
			calendarEntry = service.calendarList().get(calendarId).execute();
		} catch (Exception e1) {
			log.error("Unable to list calendars {}", e1);
			e1.printStackTrace();
			return null;
		}
		
		return calendarEntry;
		
	}
	
	/**
	 * Utility methods
	 */
	
	/**
	 * This method returns all calendars of the user associated to the Google account
	 * @return
	 * @throws Exception
	 */	
	private List<CalendarListEntry> retrieveAllCalendars() throws Exception {
		
		List<CalendarListEntry> returnedCalendarList = new ArrayList<CalendarListEntry>();
		
		CalendarList calendarList;
		
		try {
			calendarList = service.calendarList().list().execute();
		} catch (IOException e1) {
			log.error("Unable to list calendars", e1);
			return returnedCalendarList;
		}
		
		while (true) {
			for (CalendarListEntry calendarListEntry : calendarList.getItems()) {
				returnedCalendarList.add(calendarListEntry);
			}
			String pageToken = calendarList.getNextPageToken();
			if (pageToken != null && !pageToken.isEmpty()) {
				calendarList = service.calendarList().list()
						.setPageToken(pageToken).execute();
			} else {
				break;
			}
		}
		
		return returnedCalendarList;
	}

	/**
	 * This method returns all calendars of the user associated to the Google account
	 * @return
	 * @throws Exception
	 */
	private List<Event> retrieveAllEventsForCalendar(String calendarId)
			throws IOException {
		
		List<Event> returnedList = new ArrayList<Event>();
		
		com.google.api.services.calendar.model.Events events = null;
		try {
			events = service.events().list(calendarId).execute();
		} catch (IOException e) {
			log.error("Unable to list calendars: ", e);
			e.printStackTrace();
			return returnedList;
		}
		while (true) {
			List<Event> eventsList = events.getItems();
			if (eventsList == null){
				log.error("Empty Events List");
				break;
			}
			for (Event event : eventsList) {
				returnedList.add(event);
			}
			String pageToken = events.getNextPageToken();
			if (pageToken != null && !pageToken.isEmpty()) {
				try {
					events = service.events().list(calendarId).setPageToken(pageToken).execute();
				} catch (IOException e) {
					log.error("Unable to retrieve further events", e);
					break;
				}
			} else {
				break;
			}
		}
		return returnedList;
	}
	

	/**
	 * Used to map Societies subscriber to google EventAttendee
	 */

	private EventAttendee createEventAttendee(IIdentity subscriber) {
		EventAttendee attendee = new EventAttendee();
		// Create the MD5 to use in the email field
		MessageDigest messageDigest;
		String mailField = "";
		String subscriberId = subscriber.getBareJid();
		try {
			messageDigest = MessageDigest.getInstance("MD5");

			messageDigest.reset();
			messageDigest
					.update(subscriberId.getBytes(Charset.forName("UTF8")));
			byte[] resultByte = messageDigest.digest();
			mailField = new String(Hex.encodeHex(resultByte));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage());
		}
		//
		attendee.setEmail(mailField + "@societies.eu");
		attendee.setDisplayName(subscriberId);
		return attendee;
	}
	
	private String getSubscriberFromAttendee(EventAttendee attendee) {
		return attendee.getDisplayName();
	}
	


	/**
	 * This method retrieve events inside a calendar using keyword
	 * @param calendarId
	 * @param query
	 * @return
	 * @throws IOException
	 */
	private List<Event> findEventsUsingQuery(String calendarId, String query)
			throws IOException {
		
		List<Event> returnedEventList = new ArrayList<Event>();
		
		Events events = null;
		try {
			events = service.events().list(calendarId).setQ(query).execute();
			for (Event event : events.getItems()) {
				returnedEventList.add(event);
			}
		} catch (IOException e) {
			log.error("Unable to query calendar with id "+calendarId, e);
		}		
		
		return returnedEventList;
	}

	/**
	 * This method finds and event using its id
	 * @param calendarId
	 * @param eventId
	 * @return
	 * @throws IOException
	 */
	private Event findEventUsingId(String calendarId, String eventId)
			throws IOException {
		Event event = null;
		
		try {
			Get request = service.events().get(calendarId, eventId);
			request.setCalendarId(calendarId);
			request.setEventId(eventId);
			event = request.execute();
			///event = service.events().get(calendarId, eventId).execute();
		} catch (IOException e) {
			log.error("Unable to find event '"+eventId+"' in calendar with id '"+calendarId+"'", e);
		} catch(Exception ex){
			ex.printStackTrace();
			log.error("Exception occured!!! Doing alternate");
			List<Event> allEvents = retrieveAllEventsForCalendar(calendarId);
			for(Event myEvent: allEvents){
				if(myEvent.getId().equals(eventId)){
					event = myEvent;
					break;
				}
			}
		}
		

		return event;
	}
	
	/**
	 * Update an existing event
	 * @throws IOException 
	 */
	private Event updateEvent(String calendarId,Event eventToUpdate ) throws IOException{
		
		Event updatedEvent = null;
		
		try {
			updatedEvent = service.events().update(calendarId, eventToUpdate.getId(), eventToUpdate).execute();
		} catch (IOException e) {
			log.error("Unable to update event '"+eventToUpdate.getId()+"' in calendar with id '"+calendarId+"'", e);
			throw e;
		}
		
		return updatedEvent;
	}

	
	private List<org.societies.thirdparty.sharedcalendar.api.schema.Calendar> calendarListFromCalendarEntry(List<CalendarListEntry> inList) {
		List<org.societies.thirdparty.sharedcalendar.api.schema.Calendar> tmpCalendarList = new ArrayList<org.societies.thirdparty.sharedcalendar.api.schema.Calendar>();
		for (CalendarListEntry calendarListEntry : inList) {
			tmpCalendarList.add(calendarFromCalendarEntry(calendarListEntry));
		}
		return tmpCalendarList;
	}
	
	private org.societies.thirdparty.sharedcalendar.api.schema.Calendar calendarFromCalendarEntry(CalendarListEntry calendarEntry){
		org.societies.thirdparty.sharedcalendar.api.schema.Calendar newCalendar = new org.societies.thirdparty.sharedcalendar.api.schema.Calendar();                                                                             
		
		newCalendar.setName(calendarEntry.getSummary());
		newCalendar.setCalendarId(calendarEntry.getId());
		newCalendar.setNodeId(calendarEntry.getDescription());
		
		return newCalendar;
	}

	private List<org.societies.thirdparty.sharedcalendar.api.schema.Event> eventListFromGoogleEventList(
			List<Event> inList, String calendarId) {
		List<org.societies.thirdparty.sharedcalendar.api.schema.Event> tmpEventList = new ArrayList<org.societies.thirdparty.sharedcalendar.api.schema.Event>();
		
		if(inList != null){
			for (com.google.api.services.calendar.model.Event event : inList) {
				org.societies.thirdparty.sharedcalendar.api.schema.Event tmpEvent = eventFromGoogleEvent(event, calendarId);
				tmpEventList.add(tmpEvent);
			}
		}
		
		return tmpEventList;
	}	

	private org.societies.thirdparty.sharedcalendar.api.schema.Event eventFromGoogleEvent(Event googleEvent,String calendarId){
		
		org.societies.thirdparty.sharedcalendar.api.schema.Event tmpEvent = new org.societies.thirdparty.sharedcalendar.api.schema.Event();
			       
		tmpEvent.setEndDate(new Date(googleEvent.getEnd()
						.getDateTime().getValue()));
		tmpEvent.setStartDate(new Date(googleEvent.getStart()
						.getDateTime().getValue()));
		tmpEvent.setEventId(googleEvent.getId());
		tmpEvent.setName(googleEvent.getSummary());
		
		if(googleEvent.getLocation() != null)
			tmpEvent.setLocation(googleEvent.getLocation());

		if(googleEvent.getDescription() != null)
			tmpEvent.setDescription(googleEvent.getDescription());
		
		tmpEvent.setCalendarId(calendarId);
		
		Map<String, String> extendedProps = googleEvent.getExtendedProperties().getShared();
		
		if(extendedProps.get("creatorId") != null)
			tmpEvent.setCreatorId(extendedProps.get("creatorId"));
		
		if(extendedProps.get("nodeId") != null)
			tmpEvent.setNodeId(extendedProps.get("nodeId"));
		
		List<String> attendees = new ArrayList();
		if(googleEvent.getAttendees() != null)
			for(EventAttendee attendee : googleEvent.getAttendees()){
				attendees.add(getSubscriberFromAttendee(attendee));
			}
		
		tmpEvent.setAttendees(attendees);
		return tmpEvent;
		
	}
	
	/**
	 * This method is used to set up the token used to communicate with Google backend
	 * Tokens are read from the properties file backEnd.properties inside Meta-INF/conf
	 * The properties that have to be set up before start the application are clientId and clientSecret taken from
	 * the google API console (https://code.google.com/apis/console).
	 * The first time the application starts the authorization code must be supplied following the instructions on the console. 
	 */
	private void setUp() {

		try {
			HttpTransport httpTransport = new NetHttpTransport();
			JacksonFactory jsonFactory = new JacksonFactory();
			
			if(log.isDebugEnabled())
				log.debug("Trying to login go Google!");
			
			GoogleAccessProtectedResource accessProtectedResource = new GoogleAccessProtectedResource(
					accessToken, httpTransport, jsonFactory, clientId,
					clientSecret, refreshToken);

			Calendar tmpService = Calendar.builder(httpTransport, jsonFactory)
					.setApplicationName("SCalendar")
					.setHttpRequestInitializer(accessProtectedResource).build();
			service = tmpService;
			
			log.debug("Got the calendar service: " + service.getApplicationName() + " : " + service.getBaseUrl());
			
			//cleanAll();
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Unable to initialize remote Calendar Service", e);
		}

	}

	/**
	 * This method is called when tokens are not specified inside the backEnd.properties file.
	 * IMPORTANT This method is now replaced by the tool used to generate the properties file for the calendar.
	 * @param properties
	 * @param path
	 * @throws Exception
	 */
	@Deprecated
	protected void setupAuthorization(Properties properties, URL path) throws Exception {
		HttpTransport httpTransport = new NetHttpTransport();
		JacksonFactory jsonFactory = new JacksonFactory();

		// The clientId and clientSecret are copied from the API Access tab on
		// the Google APIs Console

		// Or your redirect URL for web based applications.
		String redirectUrl = "urn:ietf:wg:oauth:2.0:oob";
		String scope = "https://www.googleapis.com/auth/calendar";

		try {
			// Step 1: Authorize -->
			String authorizationUrl = new GoogleAuthorizationRequestUrl(clientId,
					redirectUrl, scope).build();

			// Point or redirect your user to the authorizationUrl.
			log.info("Go to the following link in your browser: "+authorizationUrl);
			// Read the authorization code from the standard input stream.
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			log.info("What is the authorization code?");
			String code = in.readLine();
			// End of Step 1 <--

			// Step 2: Exchange -->
//			AccessTokenResponse response = new GoogleAuthorizationCodeGrant(
//					httpTransport, jsonFactory, clientId, clientSecret, code,
//					redirectUrl).execute();
			// End of Step 2 <--
			GoogleAuthorizationCodeTokenRequest tokenReq = new GoogleAuthorizationCodeTokenRequest(httpTransport, jsonFactory, clientId, clientSecret, code, redirectUrl);
			GoogleTokenResponse tokenResp = tokenReq.execute();
			TokenResponse returnedToken = new TokenResponse();
			returnedToken.setAccessToken(tokenResp.getAccessToken());
			returnedToken.setRefreshToken(tokenResp.getRefreshToken());
			// Set and store tokens - old
//			accessToken = response.accessToken;
//			refreshToken = response.refreshToken;
			// Set and store tokens - new
			accessToken = returnedToken.getAccessToken();
			refreshToken = returnedToken.getRefreshToken();
			properties.setProperty("accessToken", accessToken);
			properties.setProperty("refreshToken", refreshToken);
			properties.store(new FileOutputStream(new File(path.getPath()), true), null);
		} catch (FileNotFoundException e) {
			log.error("Unable to set-up authorization", e);
		} catch (IOException e) {
			log.error("Unable to set-up authorization", e);
		}

	}
	
	/**
	 * Read properties from a file
	 * Used to test the library outside an Osgi container
	 */
	private void readAndSetProperties(){
		props = new Properties();
		URL url = CalendarGoogleUtil.class.getClassLoader().getResource("backEnd.properties");
		InputStream inputStream = null;
		try {
			inputStream = url.openStream();
			props.load(inputStream);

			clientId = props.getProperty("clientId");
			clientSecret = props.getProperty("clientSecret");
			accessToken = props.getProperty("accessToken");
			refreshToken = props.getProperty("refreshToken");
			
			if(log.isDebugEnabled())
				log.debug("Got the clientId:"+ clientId + " and the rest of the parameters!");
//Replaced by the configurator tool
//			if (accessToken == null || refreshToken == null || accessToken.equalsIgnoreCase("")|| refreshToken.equalsIgnoreCase("")) {
//				setupAuthorization(props, url);
//			}
		}catch (Exception e) {
			e.printStackTrace();
				// TODO: handle exception
			}finally {
				if (inputStream != null)
					try {
						inputStream.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	}}
	
	private void cleanAll(){
		try {
			List<CalendarListEntry> allCalendars = retrieveAllCalendars();
			for(CalendarListEntry allCalendar : allCalendars){
				cleanCalendar(allCalendar.getId());
				if(!allCalendar.getId().equalsIgnoreCase("societiescs@gmail.com")){
					log.debug("Deleting calendar: {} : {} ", allCalendar.getId(), allCalendar.getSummary());
					deleteCalendar(allCalendar.getId());
				}
			}
			List<CalendarListEntry> allCalendarsAfter = retrieveAllCalendars();
			for(CalendarListEntry allCalendar : allCalendarsAfter){
				log.debug("Calendar is {} : {}",allCalendar.getId(), allCalendar.getSummary());
			}
		} catch (Exception e) {
			log.warn("Error while cleaning {}",e);
			e.printStackTrace();
		}
	}
}
