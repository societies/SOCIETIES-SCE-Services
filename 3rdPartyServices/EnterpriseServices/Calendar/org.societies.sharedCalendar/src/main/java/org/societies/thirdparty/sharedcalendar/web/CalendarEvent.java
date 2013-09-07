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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.eclipse.jetty.util.log.Log;
import org.societies.api.identity.IIdentity;
import org.societies.thirdparty.sharedcalendar.api.CalendarConverter;
import org.societies.thirdparty.sharedcalendar.api.schema.Event;

/**
 * Describe your class here...
 *
 * @author <a href="mailto:sanchocsa@gmail.com">Sancho Rêgo</a> (PTIN)
 *
 */
public class CalendarEvent {

	private Event societiesEvent;
	private String	calendarName;
	private CalendarController parent;
	
	public CalendarEvent(Event societiesEvent,CalendarController parent ) {

		this.societiesEvent = societiesEvent;
		this.calendarName = null;
		this.parent = parent;
	}
	
	public CalendarEvent(CalendarController parent ){
		this.societiesEvent = new Event();
		this.calendarName = null;
		this.parent = parent;
	}
	
	public CalendarEvent(Event societiesEvent, String calendarName, CalendarController parent ) {

		this.societiesEvent = societiesEvent;
		this.calendarName = calendarName;
		this.parent = parent;
	}
	
	/**
	 * @return the calendarName
	 */
	public String getCalendarName() {
		return calendarName;
	}

	/**
	 * @param calendarName the calendarName to set
	 */
	public void setCalendarName(String calendarName) {
		this.calendarName = calendarName;
	}


	
	public void setEventId(String eventId){
		this.societiesEvent.setEventId(eventId);
	}
	public String getEventId(){
		return this.societiesEvent.getEventId();
	}


	public void setAttendess(List<String> attendees){
		this.societiesEvent.setAttendees(attendees);
	}
	public List<String> getAttendees(){
		return this.societiesEvent.getAttendees();
	}
	
	public void setNodeId(String nodeId){
		this.societiesEvent.setNodeId(nodeId);
	}
	public String getNodeId(){
		return this.societiesEvent.getNodeId();
	}
	
	public void setTitle(String title){
		this.societiesEvent.setName(title);
	}
	public String getTitle(){
		return this.societiesEvent.getName();
	}
	
	public void setDescription(String description){
		this.societiesEvent.setDescription(description);
	}
	public String getDescription(){
		return this.societiesEvent.getDescription();
	}
	
	public String getCreatorId(){
		return this.societiesEvent.getCreatorId();
	}
	public void setCreatorId(String creatorId){
		this.societiesEvent.setCreatorId(creatorId);
	}

	public void setCalendarId(String calendarId){
		this.societiesEvent.setCalendarId(calendarId);
	}	
	public String getCalendarId(){
		return this.societiesEvent.getCalendarId();
	}
	
	public void setLocation(String location){
		this.societiesEvent.setLocation(location);
	}
	public String getLocation(){
		return this.societiesEvent.getLocation();
	}
	
	public void setSocietiesEvent(Event event){
		this.societiesEvent = event;
	}
	public Event getSocietiesEvent(){
		return this.societiesEvent;
	}
	
	public void setStartDate(Date startDate){
		this.societiesEvent.setStartDate(CalendarConverter.asXMLGregorianCalendar(startDate));
	}
	public Date getStartDate(){
		return CalendarConverter.asDate(societiesEvent.getStartDate());
	}
	
	public void setEndDate(Date endDate){
		this.societiesEvent.setEndDate(CalendarConverter.asXMLGregorianCalendar(endDate));
	}
	public Date getEndDate(){
		return CalendarConverter.asDate(societiesEvent.getEndDate());
	}
	
	public String getDateText(){
		Calendar startDate = Calendar.getInstance();
		Calendar endDate = Calendar.getInstance();
		Calendar now = Calendar.getInstance();
		if(getStartDate() == null || getEndDate()==null){
			return "";
		}
		
		startDate.setTime(getStartDate());
		endDate.setTime(getEndDate());
		now.setTime(new Date());
		
		if(startDate.before(now)){
			if(endDate.before(now))
				return "The Past!";
			else
			{
				return "Now, until " + dateText(endDate,now);
			}	
		} else{
			return dateText(startDate,now);

						
		}
		
	}
	
	public boolean isMyEvent(){

		return parent.isMyEvent(societiesEvent.getCreatorId());

	}
	
	
	private String dateText(Calendar date, Calendar now){
		
		SimpleDateFormat dateFormat = new SimpleDateFormat();
		
		if(date.get(Calendar.YEAR)!= now.get(Calendar.YEAR)){
			dateFormat.applyPattern("MMM YYYY");
		} else{
			if(date.get(Calendar.MONTH) != now.get(Calendar.MONTH)){
				dateFormat.applyPattern("dd MMM");
			} else{
				if(date.get(Calendar.WEEK_OF_MONTH) != now.get(Calendar.WEEK_OF_MONTH)){
					dateFormat.applyPattern("dd MMM");
				} else{
					if(date.get(Calendar.DAY_OF_MONTH) != now.get(Calendar.DAY_OF_MONTH)){
						dateFormat.applyPattern("EEE HH:mm, z");
					} else{
						dateFormat.applyPattern("HH:mm z");
						return "Today, " + dateFormat.format(date.getTime());
					}
				}
			}
			
		}
		
		return dateFormat.format(date.getTime());
	}

	@Override
	public int hashCode() {
		return societiesEvent.getEventId().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj.getClass().equals(CalendarEvent.class)){
			if(((CalendarEvent) obj).getEventId().equals(this.societiesEvent.getEventId()))
					return true;
		}
		
		return false;
	}
	
}
