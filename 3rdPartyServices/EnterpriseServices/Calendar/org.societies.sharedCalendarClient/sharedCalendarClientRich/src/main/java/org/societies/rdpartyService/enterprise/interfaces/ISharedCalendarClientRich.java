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
package org.societies.rdpartyService.enterprise.interfaces;

import java.util.List;

import org.societies.rdpartyservice.enterprise.sharedcalendar.Event;

/**
 * Describe your class here...
 *
 * @author solutanet
 *
 */
public interface ISharedCalendarClientRich {

	/**
	 * This method create a private calendar for a CIS
	 * @param CISId
	 * @param calendarSummary
	 * @return
	 */
	public void createCISCalendar(IReturnedResultCallback returnedResultCallback,String calendarSummary, String CISId);
	
	
	
	/**
	 * This operation returns all the calendars of a CIS.
	 * @param returnedResultCallback
	 * @param CIS Id
	 */
	public void retrieveCISCalendars(IReturnedResultCallback returnedResultCallback, String CISId);
	
	
	/**
	 * This method deletes the CIS calendar with the provided CalendarId
	 * @param CalendarId
	 * @return true if successful
	 */
	public void deleteCISCalendar(IReturnedResultCallback returnedResultCallback,String calendarId);
	
	/**
	 * 
	 * @param calendarId
	 * @return the list of events inside a CIS Calendar
	 */
	public void retrieveCISCalendarEvents(IReturnedResultCallback returnedResultCallback,String calendarId);
	
	/**
	 * This method create an Event on CIS Calendar
	 * @param newEvent
	 * @param calendarId
	 * @return the id of the event
	 */
	public void createEventOnCISCalendar(IReturnedResultCallback returnedResultCallback,Event newEvent, String calendarId);
	
	/**
	 * 
	 * @param eventId
	 * @param calendarId
	 * @return true if the event is deleted successfully
	 */
	public void deleteEventOnCISCalendar(IReturnedResultCallback returnedResultCallback,String eventId, String calendarId);
	
	/**
	 * This operation is used to retrieve all events in a CSS calendar.
	 * The id of the calendar is not passed to the method because it is retrieved by the server using the Jid in the xmpp message.
	 * @param returnedResultCallback
	 */
	public void retrieveEventsPrivateCalendar(IReturnedResultCallback returnedResultCallback);
	
	/**
	 * This operation is used for create a calendar associated with the CSS
	 * 
	 * @param returnedResultCallback
	 * @param calendarSummary
	 */
	public void createPrivateCalendar(IReturnedResultCallback returnedResultCallback, String calendarSummary);
	
	/**
	 * This method removes a private calendar for a CSS
	 * The id of the calendar is not passed to the method because it is retrieved by the server using the Jid in the xmpp message.
	 * @return
	 */
	public void deletePrivateCalendar(IReturnedResultCallback returnedResultCallback);
	
	
	/**
	 * This method create an event inside a private calendar using as identifier the CSS Jid
	 * The id of the calendar is not passed to the method because it is retrieved by the server using the Jid in the xmpp message.
	 * @param eventTitle
	 * @param description
	 * @param startDate
	 * @param endDate
	 * @param attendeeName
	 * @param attendeeEmail
	 * @return
	 */
	public void createEventOnPrivateCalendar(IReturnedResultCallback returnedResultCallback,Event newEvent);
	
	/**
	 * This method is used to delete an event on a CSS calendar.
	 * @param eventId
	 * @return true if the event is deleted successfully.
	 */
	public void deleteEventOnPrivateCalendar(IReturnedResultCallback returnedResultCallback,String eventId);
	
	/**
	 * This method is used to subscribe to an event in a CIS calendar.
	 * @param calendarId
	 * @param eventId
	 * @param subscriberId the identifier for the subscriber
	 * @return true if the subscription is performed correctly false otherwise
	 */
	public void subscribeToEvent(IReturnedResultCallback returnedResultCallback,String calendarId,String eventId, String subscriberId);
	
	/**
	 * This method is used to retrieve events in a CIS calendar that match a keyword
	 * @param calendarId
	 * @param keyWord
	 * @return the list of events that match the input keyword
	 */
	public void findEvents(IReturnedResultCallback returnedResultCallback,String calendarId,String keyWord);
	
	/**
	 * This method is used to unsubscribe to an event in a CIS calendar.
	 * @param calendarId
	 * @param eventId
	 * @param subscriberId
	 * @return true if the unsubscription is performed correctly, false otherwise
	 */
	public void unsubscribeFromEvent(IReturnedResultCallback returnedResultCallback,String calendarId,String eventId, String subscriberId);
	
}
