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
package org.societies.rdPartyService.enterprise.sharedCalendar;

import java.util.List;

import org.societies.rdPartyService.enterprise.sharedCalendar.dataObject.Calendar;
import org.societies.rdPartyService.enterprise.sharedCalendar.dataObject.Event;

/**
 * 
 *
 * @author solutanet
 *
 */
public interface ISharedCalendar {
	/**
	 * This method is used to retrieve all available calendars provided by the 3rd party service
	 * @return the list of Calendars
	 */

	public List<Calendar> retrieveCalendarList();
	
	/**
	 * 
	 * @param calendarId
	 * @return the list of events inside a specific Calendar
	 */
	public List<Event> retrieveCalendarEvents(String calendarId);
	
	/**
	 * 
	 * @param calendarId
	 * @param eventId
	 * @param subscriberId the identifier for the subscriber
	 * @return true if the subscription is performed correctly false otherwise
	 */
	public boolean subscribeToEvent(String calendarId,String eventId, String subscriberId);
	
	/**
	 * 
	 * @param calendarId
	 * @param keyWord
	 * @return the list of events that match the input keyword
	 */
	public List<Event> findEvents(String calendarId,String keyWord);
	
	/**
	 * 
	 * @param calendarId
	 * @param eventId
	 * @param subscriberId
	 * @return true if the unsubscription is performed correctly, false otherwise
	 */
	public boolean unsubscribeFromEvent(String calendarId,String eventId, String subscriberId);
}
