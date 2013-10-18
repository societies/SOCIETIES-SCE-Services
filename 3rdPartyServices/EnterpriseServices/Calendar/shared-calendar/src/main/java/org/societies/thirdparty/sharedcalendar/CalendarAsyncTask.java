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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentity;
import org.societies.thirdparty.sharedcalendar.api.schema.Calendar;
import org.societies.thirdparty.sharedcalendar.api.schema.Event;

/**
 * Describe your class here...
 *
 * @author <a href="mailto:sanchocsa@gmail.com">Sancho Rêgo</a> (PTIN)
 *
 */
public class CalendarAsyncTask implements Runnable {

	static final Logger log=LoggerFactory.getLogger(CalendarAsyncTask.class);
	
	public enum Task{
		EVENTS,
		CALENDAR,
		SEARCH_CALENDAR
	}


	private SharedCalendar parent;
	private Task task;
	private Event event;
	private BlockingQueue<List<Event>> eventListResult;
	private IIdentity node;
	
	protected IIdentity getNode() {
		return node;
	}

	private BlockingQueue<Calendar> calendarQueue;
	private IIdentity requestor;
	
	public CalendarAsyncTask(SharedCalendar parent, Event event, IIdentity node, IIdentity requestor,Task task) {
		this.parent = parent;
		this.event = event;
		this.task = task;
		this.node = node;
		this.requestor = requestor;
		if(task.equals(Task.CALENDAR)){
			this.calendarQueue = new ArrayBlockingQueue<Calendar>(1);
		} else
			this.eventListResult = new ArrayBlockingQueue<List<Event>>(1);
	}
	
	public List<Event> getEventResult(){
		
		try {
			return eventListResult.take();
		} catch (InterruptedException ex) {
			log.error("Exception executing Calendar Async Task!: {}", ex);
			ex.printStackTrace();
			return new ArrayList<Event>();
		}
		
	}
	
	public Calendar getCalendarResult(){
		try {
			Calendar result = calendarQueue.take();
			if(result.getCalendarId() != null)
				return result;
			else
				return null;
		} catch (InterruptedException ex) {
			log.error("Exception executing Calendar Async Task!: {}", ex);
			ex.printStackTrace();
			return null;
		}
	}


	@Override
	public void run() {
		switch(task){
			case EVENTS: 
				getEvents(); break;
			case SEARCH_CALENDAR: 
				searchEvents(); break;
			case CALENDAR: 
				getCalendar(); break;
		}
	}


	private void getCalendar() {
		log.debug("Async method to get Calendar for node: {}", node);
		Calendar result = parent.retrieveCalendar(node, requestor);
		log.debug("Result of Async CalendarGet for node {} is: {}", node, result);
		try {
			if(result != null)
				calendarQueue.put(result);
			else
				calendarQueue.put(new Calendar());
		} catch (InterruptedException e) {
			log.error("Exception trying to put Calendar in queue: {}",e.getMessage());
			e.printStackTrace();
		}
		
		
	}

	private void searchEvents() {
		log.debug("Async method to get Search for calendar: {}", node);
		List<Event> result = parent.findEventsInCalendar(node, event, requestor);
		log.debug("Result of Async Search for node {} is: {} events", node,result.size());
		try {
			eventListResult.put(result);
		} catch (InterruptedException e) {
			log.error("Exception trying to put Calendar in queue: {}",e.getMessage());
			e.printStackTrace();
		}		
	}

	private void getEvents() {
		log.debug("Async method to get Events for calendar: {}", node);
		List<Event> result = parent.retrieveEvents(node, requestor);
		log.debug("Result of Async Event Retrieve for node {} is: {} events", node,result.size());
		try {
			eventListResult.put(result);
		} catch (InterruptedException e) {
			log.error("Exception trying to put Calendar in queue: {}",e.getMessage());
			e.printStackTrace();
		}		
	}

}
