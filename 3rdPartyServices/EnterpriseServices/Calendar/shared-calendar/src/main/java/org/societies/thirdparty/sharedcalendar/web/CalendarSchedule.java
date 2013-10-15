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
package org.societies.thirdparty.sharedcalendar.web;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import javax.faces.context.FacesContext;

import org.primefaces.component.schedule.Schedule;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.LazyScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.thirdparty.sharedcalendar.api.schema.Event;
import org.societies.thirdparty.sharedcalendar.api.schema.SharedCalendarResult;

/**
 * Describe your class here...
 *
 * @author <a href="mailto:sanchocsa@gmail.com">Sancho Rêgo</a> (PTIN)
 *
 */
public class CalendarSchedule extends LazyScheduleModel {

	static final Logger log = LoggerFactory.getLogger(CalendarSchedule.class);
	private CalendarController parent;

	/**
	 * 
	 */
	public CalendarSchedule() {
		super();
	}

	public CalendarSchedule(CalendarController parent) {
		super();
		this.setParent(parent);
	}

	@Override
	public void loadEvents(Date start, Date end){
		log.debug("loadEvents called, for {} and {}",start,end);
		this.clear();
		
		String nodeId = parent.getSelectedNode();
		
		CalendarResultCallback callback = new CalendarResultCallback();
		CalendarResultCallback secondCallback = null;
		Event searchEvent = new Event();
		searchEvent.setStartDate(start);
		searchEvent.setEndDate(end);
		
		if(!nodeId.equals("mysubscribedevents")){
			parent.getSharedCalendar().findEventsInCalendar(callback, nodeId, searchEvent);
			if(parent.isAllEvents()){
				secondCallback = new CalendarResultCallback();
				parent.getSharedCalendar().getSubscribedEvents(secondCallback, parent.getId().getBareJid());
			}
		}
		else
			parent.getSharedCalendar().getSubscribedEvents(callback, parent.getId().getBareJid());
		
		SharedCalendarResult result = callback.getResult();
		List<Event> calendarEvents = result.getEventList();
		
		parent.currentEvents = new HashMap<String,CalendarEvent>();
		
		for(Event event: calendarEvents){
			log.debug("Adding event: {}", event.getName());
			
			ScheduleEvent newEvent = new DefaultScheduleEvent(event.getName(),event.getStartDate(),event.getEndDate());
			this.addEvent(newEvent);
			parent.currentEvents.put(newEvent.getId(),new CalendarEvent(event,parent.getCalendarName(event.getNodeId()),parent));
		}
		
		if(secondCallback != null){
			result = secondCallback.getResult();
			List<Event> subscribedEvents = result.getEventList();
			
			for(Event event: subscribedEvents){
				log.debug("Adding subs event: {}", event.getName());
				// First we check if the current event is there...
				CalendarEvent ourEvent = new CalendarEvent(event,parent.getCalendarName(event.getNodeId()),parent);
				Set<Entry<String, CalendarEvent>> eventSet = parent.currentEvents.entrySet();
				if(parent.currentEvents.containsValue(ourEvent)){
					Iterator<Entry<String, CalendarEvent>> myIt = eventSet.iterator();
					while(myIt.hasNext()){
						Entry<String, CalendarEvent> calendarEntry = myIt.next();
						if(calendarEntry.getValue().equals(ourEvent)){
							this.deleteEvent(this.getEvent(calendarEntry.getKey()));
							myIt.remove();
						}
					}
				} 
				
				ScheduleEvent newEvent = new DefaultScheduleEvent(event.getName(),event.getStartDate(),event.getEndDate(),"my-event");
				this.addEvent(newEvent);
				parent.currentEvents.put(newEvent.getId(),new CalendarEvent(event,parent.getCalendarName(event.getNodeId()),parent));
			}
		}
		
		
		
	}
	
	
	/**
	 * @return the parent
	 */
	public CalendarController getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(CalendarController parent) {
		this.parent = parent;
	}
}
