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

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.rdpartyservice.enterprise.sharedcalendar.Calendar;
import org.societies.rdpartyservice.enterprise.sharedcalendar.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

/**
 * Describe your class here...
 * 
 * @author solutanet
 * 
 */
@ContextConfiguration(locations = { "../../../../../META-INF/SharedCalendarTest-context.xml" })
public class TestSharedCalendar extends AbstractTransactionalJUnit4SpringContextTests {
@Autowired

	private  SharedCalendar sharedCalendar;

	private static Logger log = LoggerFactory
			.getLogger(TestSharedCalendar.class);
	private static String testCalendarId="Unassigned";
	private static String testEventId="Unassigned";
	private static final String _testCisId = "TestCIS";
	private static final String _testCisSummary = "Test CIS calendar";

	
	@Test
	@Rollback(false)
	public void createCISCalendar(){
		List<Calendar> cisCalendars = sharedCalendar.retrieveCISCalendarList(_testCisId);
		boolean isCisCalendarAvailable = !cisCalendars.isEmpty();
		boolean result=false;
		if (!isCisCalendarAvailable){
			log.info(cisCalendars.size()+" Calendars retrieved");
			result = sharedCalendar.createCISCalendar(_testCisSummary,_testCisId);
		}else{			
//			for (Calendar calendar : cisCalendars) {
//				if(calendar.getSummary().equalsIgnoreCase(_testCisSummary)){
//					result = sharedCalendar.deleteCISCalendar(calendar.getCalendarId());
//					break;
//				}
//			}
		}
		assert(isCisCalendarAvailable || result);
	}
	
	@Test
	@Rollback(false)
	public void testRetrieveAllCalendar() {
		sharedCalendar.setUtil(new SharedCalendarUtil());
		List<Calendar> calendarList = sharedCalendar.retrieveCISCalendarList(_testCisId);
		log.info(calendarList.size()+" Calendars retrieved");
		
		for (Calendar calendar : calendarList) {
			log.info("Calendar id: " + calendar.getCalendarId()+" - Calendar summary: "+calendar.getSummary());
			testCalendarId = calendar.getCalendarId();
		}	
		//Add an event to the calendar if none is found
		List<Event> availableEvents = sharedCalendar.retrieveCalendarEvents(testCalendarId);
		if (availableEvents.isEmpty()){
			Event newEvt = new Event();
			newEvt.setEventDescription("SOCIETIES GA");
			newEvt.setLocation("Heriot Watt University Edinburgh Campus");
			Date start = new GregorianCalendar(2012, 5, 25).getTime();
			Date stop = new GregorianCalendar(2012, 5, 29).getTime();
			newEvt.setStartDate(new XMLGregorianCalendarConverter().asXMLGregorianCalendar(start));
			newEvt.setEndDate(new XMLGregorianCalendarConverter().asXMLGregorianCalendar(stop));			
			newEvt.setEventSummary("Integration Meeting");
			sharedCalendar.createEventOnPrivateCalendarUsingCSSId(testCalendarId, newEvt);
		}		
	}
	
	
	
	@Test
	@Rollback(false)
	public void retrieveCalendarEvent(){
		List<Event> eventList=sharedCalendar.retrieveCalendarEvents(testCalendarId);
		log.info("Events retrieved for calendar with id '"+testCalendarId+"' :");
		for (Event event : eventList) {
			log.info("Event id: "+event.getEventId()+" - Event Summary:"+event.getEventSummary());
			testEventId = event.getEventId();			
		}
	}
	
	@Test
	@Rollback(false)
	public void addSubscriber(){
		 sharedCalendar.subscribeToEvent(testCalendarId, testEventId, "xxxx");
	}
	
	@Test
	@Rollback(false)
	public void unsubscribe(){
		sharedCalendar.unsubscribeFromEvent(testCalendarId, testEventId, "xxxx");
	}
	
	@Test
	@Rollback(false)
	public void createPrivateCalendar(){
		boolean result=sharedCalendar.createPrivateCalendarUsingCSSId("TestCSS", "Test private calendar");
		assert(result);
	}
	
	
	
	/*
	@Test
	public void insertRemoveCalendar() throws InterruptedException{
		int max=50;
		for(int i=0;i<max;i++){
		sharedCalendar.createPrivateCalendarUsingCSSId("TestCISIs"+i, "Test private calendar");
		Thread.sleep(100);}
		for(int i=0;i<max;i++){
			sharedCalendar.deletePrivateCalendarUsingCSSId("TestCISIs"+i);
			Thread.sleep(100);}
	}*/

}
