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
package org.societies.thirdparty.sharedCalendar.api;

import java.util.List;
import org.societies.api.ext3p.schema.sharedcalendar.Event;


/**
 * This is the interface used in the client node to interact with the backend of the calendar service.
 *
 * @author solutanet
 * @param <ICalendarResultCallback>
 *
 */
public interface ISharedCalendarClient {

	/**
	 * This operation returns all the calendars of a CIS.
	 * @param calendarResultCallback
	 * @param CIS Id
	 */
	public void retrieveCalendar(ICalendarResultCallback calendarResultCallback, String nodeId);
	
	/**
	 * 
	 * @param calendarId
	 * @return the list of events inside a CIS Calendar
	 */
	public void retrieveEvents(ICalendarResultCallback calendarResultCallback, String nodeId);
	
	/**
	 * 
	 * @param calendarId
	 * @return the list of events inside a CIS Calendar
	 */
	public void viewEvent(ICalendarResultCallback calendarResultCallback, String eventId, String nodeId);
	
	/**
	 * This method create an Event on CIS Calendar
	 * @param newEvent
	 * @param calendarId
	 * @return the id of the event
	 */
	public void createEvent(ICalendarResultCallback calendarResultCallback,Event newEvent, String nodeId);

	/**
	 * This method create an Event on CIS Calendar
	 * @param newEvent
	 * @param calendarId
	 * @return the id of the event
	 */
	public void updateEvent(ICalendarResultCallback calendarResultCallback, Event updatedEvent);
	
	/**
	 * 
	 * @param eventId
	 * @param calendarId
	 * @return true if the event is deleted successfully
	 */
	public void deleteEvent(ICalendarResultCallback calendarResultCallback, String eventId, String nodeId);
	
	/**
	 * This method is used to subscribe to an event in a CIS calendar.
	 * @param calendarId
	 * @param eventId
	 * @param subscriberId the identifier for the subscriber
	 * @return true if the subscription is performed correctly false otherwise
	 */
	public void subscribeToEvent(ICalendarResultCallback calendarResultCallback, String eventId, String nodeId, String subscriberId);
	
	/**
	 * This method is used to retrieve events in a CIS calendar that match a keyword
	 * @param calendarId
	 * @param keyWord
	 * @return the list of events that match the input keyword
	 */
	public void findEventsInCalendar(ICalendarResultCallback calendarResultCallback, String nodeId, Event searchEvent);
	
	/**
	 * This method is used to retrieve events in that match a parameter
	 * @param calendarId
	 * @param keyWord
	 * @return the list of events that match the input keyword
	 */
	public void findEventsAll(ICalendarResultCallback calendarResultCallback, Event searchEvent);
	
	/**
	 * This method is used to unsubscribe to an event in a CIS calendar.
	 * @param calendarId
	 * @param eventId
	 * @param subscriberId
	 * @return true if the unsubscription is performed correctly, false otherwise
	 */
	public void unsubscribeFromEvent(ICalendarResultCallback calendarResultCallback, String eventId, String subscriberId);

	/**
	 * Gets the events that are recommended for the subscriber
	 */
	public void getRecommendedEvents(ICalendarResultCallback calendarResultCallback, String subscriberId);

	/**
	 * @return
	 */
	public List<UserWarning> getUserWarnings();

	/**
	 * @param calendarResultCallback
	 */
	void getAllCalendars(ICalendarResultCallback calendarResultCallback);

	
}
