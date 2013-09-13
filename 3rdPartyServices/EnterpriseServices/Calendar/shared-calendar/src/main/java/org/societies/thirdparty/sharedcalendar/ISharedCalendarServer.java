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

import java.util.List;

import org.societies.api.identity.IIdentity;
import org.societies.thirdparty.sharedcalendar.api.UserWarning;
import org.societies.thirdparty.sharedcalendar.api.schema.Calendar;
import org.societies.thirdparty.sharedcalendar.api.schema.Event;



/**
 * This is the exposed interface that client developers will use.
 *
 * @author <a href="mailto:sanchocsa@gmail.com">Sancho Rêgo</a> (PTIN)
 *
 */
public interface ISharedCalendarServer {
	
	//The following are the verbs for the Activity Stream Entries
	
	public static final String VERB_CIS_CALENDAR_CREATED = "CIS Calendar Created";
	public static final String VERB_CIS_CALENDAR_DELETED = "CIS Calendar Deleted";
	public static final String VERB_CIS_CALENDAR_EVENT_CREATED = "CIS Calendar Event Created";
	public static final String VERB_CIS_CALENDAR_EVENT_UPDATED = "CIS Calendar Event Updated";
	public static final String VERB_CIS_CALENDAR_EVENT_DELETED = "CIS Calendar Deleted";
	public static final String VERB_CSS_CALENDAR_CREATED = "CSS Calendar Created";
	public static final String VERB_CSS_CALENDAR_DELETED = "CSS Calendar Deleted";
	public static final String VERB_CSS_CALENDAR_EVENT_CREATED = "CSS Calendar Event Created";
	public static final String VERB_CSS_CALENDAR_EVENT_DELETED = "CSS Calendar Event Deleted";
	public static final String VERB_CALENDAR_EVENT_SUBSCRIPTION = "Calendar Event Subscription";
	public static final String VERB_CALENDAR_EVENT_UNSUBSCRIPTION = "Calendar Event Unsubscription";
	
	public static final int RECOMMEND_THRESHOLD = 10;
	public static final int CREATOR_POINTS = 15;
	public static final int CREATOR_FRIEND = 10;
	public static final int FRIEND_POINTS = 5;
	public static final int LOCATION_POINTS = 10;
	public static final int CIS_POINTS = 15;
	public static final int INTEREST_POINTS = 10;
	
	
	public boolean subscribeToEvent(String eventId, IIdentity node, IIdentity subscriberId);

	public boolean unsubscribeFromEvent(String eventId, IIdentity subscriberId);
	
	public Calendar retrieveCalendar(IIdentity node, IIdentity requestor);

	public List<Event> retrieveEvents(IIdentity node, IIdentity requestor);

	public boolean updateEvent(Event updatedEvent, IIdentity requestor);
	
	public String createEvent(Event newEvent, IIdentity node, IIdentity requestor);

	boolean deleteEvent(String eventId, IIdentity node, IIdentity requestor);
	
	public List<Event> getEventsForSubscriber(IIdentity subscriber);
	
	public List<Event> getSubscribedEvents(IIdentity subscriber);
	
	public List<Event> getRecommendedEvents(IIdentity subscriber);
	
	public List<Event> findEventsInCalendar(IIdentity node, Event searchEvent, IIdentity requestor);
	
	public List<Event> findEventsAll(Event searchEvent, IIdentity requestor);

	public List<Event> getMyRecommendedEvents();
	
	public List<Calendar> getAllCalendars();
	
	/**
	 * @return
	 */
	public List<UserWarning> getUserWarnings();
	
}
