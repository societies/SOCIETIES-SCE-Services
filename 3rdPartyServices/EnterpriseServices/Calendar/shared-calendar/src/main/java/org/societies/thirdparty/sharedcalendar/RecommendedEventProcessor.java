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

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentity;
import org.societies.thirdparty.sharedcalendar.api.CalendarPreference;
import org.societies.thirdparty.sharedcalendar.api.schema.Calendar;
import org.societies.thirdparty.sharedcalendar.api.schema.Event;

/**
 * Describe your class here...
 *
 * @author <a href="mailto:sanchocsa@gmail.com">Sancho Rêgo</a> (PTIN)
 *
 */
public class RecommendedEventProcessor implements Runnable {

	public enum Task{
		EVENT,
		EVENTS,
		CALENDAR,
	}
	
	private static final int RECOMMEND_THRESHOLD = 30;
	private static final int CREATOR_POINTS = 15;
	private static final int CREATOR_FRIEND = 10;
	private static final int FRIEND_POINTS = 5;
	private static final int LOCATION_POINTS = 10;
	private static final int CIS_POINTS = 15;
	private static final int INTEREST_POINTS = 10;
	private static final int EVENTS_IN_CALENDAR_POINTS = 1;
	
	static final Logger log=LoggerFactory.getLogger(RecommendedEventProcessor.class);
	
	private SharedCalendar parent;
	private Task task;
	private String homeCity;
	private List<IIdentity> myFriends;
	private List<String> myInterests;
	private String myLocation;
	private String myWorkCity;
	private Calendar calendar;
	private Event event;
	private List<Event> eventList;

	/**
	 * 
	 */
	public RecommendedEventProcessor(SharedCalendar parent,Calendar calendar, Event event, List<Event> eventList, Task task) {
		this.parent = parent;
		this.task = task;
		this.homeCity = null;
		this.myFriends = null;
		this.myLocation = null;
		this.myWorkCity = null;
		this.myInterests = null;
		this.eventList = eventList;
		this.calendar = calendar;
		this.event = event;
	}


	@Override
	public void run() {
		log.debug("RecommendedEvent Processor running! Our task is {}",task);
		
		switch(task){
			case EVENT: 
				getContextInfo();
				processEvent(event);
				break;
			case CALENDAR:
				log.debug("Processing recommended Events for Calendar: {}", calendar.getName());
				try{
					Event searchEvent = new Event();
					searchEvent.setStartDate(new Date());
					eventList = parent.findEventsInCalendar(parent.getIIdentityFromJid(calendar.getNodeId()), searchEvent, parent.getMyId());
				} catch(Exception ex){
					log.error("Exception while trying to process events for a Calendar {} : {}",calendar.getName(),ex.getMessage());
					ex.printStackTrace();
				}
			case EVENTS:
				if(!eventList.isEmpty())
					getContextInfo();
				for(Event event: eventList){
					processEvent(event);
				}
				break;
		}

	}
	
	
	private void getContextInfo() {
		homeCity = parent.getContext().getHomeCity();
		myFriends = parent.getContext().getMyFriends();
		myInterests = parent.getContext().getMyInterests();
		myLocation = parent.getContext().getMyLocation();
		myWorkCity = parent.getContext().getWorkCity();
		
	}
	
	private void processEvent(Event newEvent){
		log.debug("We have a event to process and see if we should recommend it: {}", newEvent.getName());
		
		int recommendPoints = 0;
		
		// DON'T RECOMMEND OUR OWN EVENTS!
		IIdentity creatorId = null;
		try{
			creatorId = parent.getIIdentityFromJid(newEvent.getCreatorId());
			if(creatorId.equals(parent.getMyId())){
				log.debug("{}: We created the event, no need to process it!",newEvent.getName());
				parent.getRecommendedEvents().remove(newEvent.getEventId());
				return;
			}
		} catch(Exception ex){
			log.error("{}: Didn't manage to compare to creatorId! Exception: {}",newEvent.getName(),ex.getMessage());
			ex.printStackTrace();
		}
		
		// DON'T RECOMMEND EVENTS THAT HAVE ENDED!
		Date endDate = newEvent.getEndDate();
		if(endDate.before(new Date())){
			log.debug("{}: happened in the past, don't recommended it!", newEvent.getName());
			parent.getRecommendedEvents().remove(newEvent.getEventId());
			return;
		}
		
		
		// ATTENDING FRIENDS
		log.debug("{}: Next step is checking which friends are attending", newEvent.getName());
		
		try{
			
			for(String attendeeJid : newEvent.getAttendees()){
				IIdentity attendee = parent.getIIdentityFromJid(attendeeJid);
				if(attendee.equals(parent.getMyId())){
					log.debug("We're already attending this event!");
					parent.getRecommendedEvents().remove(newEvent.getEventId());	
					return;
				}
				if(myFriends.contains(attendee)){
					log.debug("{}: Friend {} is attending the event!",newEvent.getName(), attendee.getJid());
					recommendPoints+=FRIEND_POINTS;
				} else{
					log.debug("{}: Attendee {} is not our friend. :( ",newEvent.getName(),attendee.getJid());
				}
			}
			
			if(creatorId != null && myFriends.contains(creatorId)){
				log.debug("{}: Creator is our friend!",newEvent.getName());
				recommendPoints+=CREATOR_FRIEND;
			}
			
			log.debug("{}: Now checking the preferred creator",newEvent.getName());
			String prefCreator = parent.getPreferences().getPreference(CalendarPreference.SUB_CREATOR);
			if(prefCreator != null && prefCreator.equals(creatorId)){
				log.debug("{}: Event is from a favoured creator, so adding points!",newEvent.getName());
				recommendPoints+=CREATOR_POINTS;
			} else{
				String prefSearchCreator = parent.getPreferences().getPreference(CalendarPreference.SEARCH_CREATOR);
				if(prefSearchCreator != null && prefSearchCreator.equals(creatorId)){
					log.debug("{}: Event is from a favoured Search creator, so adding points!",newEvent.getName());
					recommendPoints+=CREATOR_POINTS;
				} 
			}
			
		} catch(Exception ex){
			log.error("{}: Exception ocurred while trying to process friends for event: {}",newEvent.getName(),ex.getMessage());
			ex.printStackTrace();
		}
		
		// LOCATION
		log.debug("{}: Now we check the location!",newEvent.getName());
		if(newEvent.getLocation() != null){
			
			if(myLocation != null && myLocation.equalsIgnoreCase(newEvent.getLocation())){
				log.debug("{}: Event is in our location: {}",newEvent.getName(),myLocation);
				recommendPoints+=LOCATION_POINTS;
			} else{
			
				if(homeCity != null && homeCity.equalsIgnoreCase(newEvent.getLocation())){
					log.debug("{}: Event is in our home city: {}",newEvent.getName(),homeCity);
					recommendPoints+=LOCATION_POINTS;
				}
				else{
					if(myWorkCity != null && myWorkCity.equalsIgnoreCase(newEvent.getLocation())){
						log.debug("{}: Event is in our work city: {}",newEvent.getName(),myWorkCity);
						recommendPoints+=LOCATION_POINTS;
					} else{
						String locationPref = parent.getPreferences().getPreference(CalendarPreference.SUB_LOCATION);
						if(locationPref != null && locationPref.equalsIgnoreCase(locationPref)){
							log.debug("{}: Event is a favourite location: {}",newEvent.getName(),locationPref);
							recommendPoints+=LOCATION_POINTS;
						} else{
							String locationSearchPref = parent.getPreferences().getPreference(CalendarPreference.SEARCH_LOCATION);
							if(locationSearchPref != null && locationSearchPref.equalsIgnoreCase(locationPref)){
								log.debug("{}: Event is a favourite location to search: {}",newEvent.getName(),locationSearchPref);
								recommendPoints+=LOCATION_POINTS;
							} else{
								
							}
						}
						
					}
				}
			}
			
		}
			
		// INTERESTS
		for(String interest: myInterests){
			log.debug("{}: Checking event for interest: {}",newEvent.getName(), interest);
			if(newEvent.getName().regionMatches(true, 0, interest, 0, interest.length())){
				log.debug("{}: Found interest {} in event.", newEvent.getName(), interest );
				recommendPoints+=INTEREST_POINTS;
			} else{
				if(newEvent.getDescription() != null && newEvent.getDescription().regionMatches(true, 0, interest, 0, interest.length())){
					log.debug("{}: Found interest {} in event.", newEvent.getName(), interest );
					recommendPoints+=INTEREST_POINTS;
				}
			}
		}
		
		// FAVOURITE NDOE
		String prefNode = parent.getPreferences().getPreference(CalendarPreference.SUB_CALENDAR);
		if(prefNode != null && prefNode.equalsIgnoreCase(newEvent.getNodeId())){
			log.debug("{}: event is from a favourite calendar!",newEvent.getName());
			recommendPoints+=CIS_POINTS;
		} else{
			String prefSearchNode = parent.getPreferences().getPreference(CalendarPreference.SEARCH_CALENDAR);
			if(prefSearchNode != null && prefSearchNode.equalsIgnoreCase(newEvent.getNodeId())){
				log.debug("{}: event is from a calendar we search a lot...!",newEvent.getName());
				recommendPoints+=CIS_POINTS/2;
			}
		}
		
		int subEvent = parent.getDatabase().getEventsForCalendarId(newEvent.getCalendarId()).size();
		log.debug("{} : We have subscribed {} other events in this calendar.",newEvent.getName(),subEvent);
		recommendPoints+=subEvent*EVENTS_IN_CALENDAR_POINTS;
		
		// Now we get the keyword we most
		if(recommendPoints >= RECOMMEND_THRESHOLD){
			
			log.debug("{}: We're going to recommend this event.", newEvent.getName());
			parent.getRecommendedEvents().put(newEvent.getEventId(), newEvent);
			parent.getRecentEvents().put(newEvent.getEventId(), newEvent);

		} else{
			log.debug("{}: No need to recommend this event.", newEvent.getName());
			parent.getRecommendedEvents().remove(newEvent.getEventId());
			
		}
		
	}


}
