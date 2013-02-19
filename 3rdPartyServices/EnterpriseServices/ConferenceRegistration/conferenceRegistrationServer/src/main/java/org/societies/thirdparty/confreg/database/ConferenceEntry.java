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
package org.societies.thirdparty.confreg.database;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.thirdparty.conferenceregistration.schema.Conference;

/**
 * The database representation of a Conference.
 *
 * @author <a href="mailto:sanchocsa@gmail.com">Sancho Rêgo</a> (PTIN)
 *
 */

@Entity
@Table(name = "ConferenceRegistrations")
public class ConferenceEntry implements Serializable {

	private static final long serialVersionUID = 8993020100839998732L;
	private static Logger logger = LoggerFactory.getLogger(ConferenceEntry.class);
	private String calendarId;
	private String description;
	private String name;
	private String location;
	private String startDate;
	private String endDate;
	private String creatorId;
	private String cisId;
	private String conferenceId;
	private String keywords;
			
	public ConferenceEntry(Conference myConference) {
		
		this.calendarId = myConference.getCalendarId();
		this.description = myConference.getDescription();
		this.name = myConference.getName();
		this.location = myConference.getLocation();
		myConference.getKeywords();
		
		if(myConference.getStartDate() != null){
			startDate = myConference.getStartDate().toXMLFormat();
		} else
			startDate = null;
		
		if(myConference.getEndDate() != null){
			endDate = myConference.getEndDate().toXMLFormat();
		} else
			endDate = null;
		
		this.creatorId = myConference.getCreatorId();
		this.cisId =  myConference.getCisId();
		this.conferenceId = myConference.getConferenceId();
		
		List<String> keywords = myConference.getKeywords();
		if(keywords != null && !keywords.isEmpty()){
			String keywordDB = "";
			for(String keyword: keywords){
				keywordDB+=(';' + keyword);
			}
			this.keywords = keywordDB.substring(1);
		} else{
			this.keywords = null;
		}
		
	}
	
	
	@Column(name = "Keywords")
	public String getKeywords(){
		return this.keywords;
	}
	
	public void setKeywords(String keywords){
		this.keywords = keywords;
	}
	
	@Column(name = "Name")
	public String getName(){
		return this.name;
	}
	
	public void setName(String name){
		this.name = name;
	}

	@Column(name = "Description")
	public String getDescription(){
		return this.description;
	}
	
	public void setDescription(String description){
		this.description = description;
	}
	
	@Column(name = "Location")
	public String getLocation(){
		return this.location;
	}
	
	public void setLocation(String location){
		this.location = location;
	}
	
	@Column(name = "CalendarId")
	public String getCalendarId(){
		return this.calendarId;
	}
	
	public void setCalendarId(String calendarId){
		this.calendarId = calendarId;
	}
	
	@Column(name = "CreatorId")
	public String getCreatorId(){
		return this.creatorId;
	}
	
	public void setCreatorId(String creatorId){
		this.creatorId = creatorId;
	}
	
	@Column(name = "CisId")
	public String getCisId(){
		return this.cisId;
	}
	
	public void setCisId(String cisId){
		this.cisId = cisId;
	}

	@Column(name = "StartDate")
	public String getStartDate(){
		return this.startDate;
	}
	
	public void setStartDate(String startDate){
		this.startDate = startDate;
	}
	
	@Column(name = "EndDate")
	public String getEndDate(){
		return this.endDate;
	}
	
	public void setEndDate(String endDate){
		this.endDate = endDate;
	}
	
	@Id
	public String getConferenceId(){
		return this.conferenceId;
	}
	
	public void setConferenceId(String conferenceId){
		this.conferenceId = conferenceId;
	}
	
	public void updateConference(Conference conference){
		
		if(conference.getCisId() != null){
			this.setCisId(conference.getCisId());
		}

		if(conference.getCreatorId() != null){
			this.setCreatorId(conference.getCreatorId());
		}
		
		if(conference.getCalendarId() != null){
			this.setCisId(conference.getCisId());
		}
		
		if(conference.getName() != null){
			this.setName(conference.getName());
		}
		
		if(conference.getDescription() != null){
			this.setDescription(conference.getDescription());
		}

		if(conference.getLocation() != null){
			this.setLocation(conference.getLocation());
		}
		
		if(conference.getStartDate() != null){
			this.setStartDate(conference.getStartDate().toXMLFormat());
		}
		
		if(conference.getEndDate() != null){
			this.setEndDate(conference.getEndDate().toXMLFormat());
		}
		
		List<String> keywords = conference.getKeywords();
		if(keywords != null && !keywords.isEmpty()){
			String keywordDB = "";
			for(String keyword: keywords){
				keywordDB+=(';' + keyword);
			}
			this.setKeywords(keywordDB.substring(1));
		}
		
	}
	
	public Conference createConferenceFromRegistry(){
		Conference returnedConference = null;
		
		try{
			
			returnedConference = new Conference();
			returnedConference.setCalendarId(this.getCalendarId());
			returnedConference.setCisId(this.getCisId());
			returnedConference.setConferenceId(this.getConferenceId());
			returnedConference.setCreatorId(this.getCreatorId());
			returnedConference.setDescription(this.getDescription());
			returnedConference.setLocation(this.getLocation());
			returnedConference.setName(this.getName());
			
			if(this.getStartDate() != null)
				returnedConference.setStartDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(this.getStartDate()));
			else
				returnedConference.setStartDate(null);
	
			if(this.getEndDate() != null)
				returnedConference.setEndDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(this.getEndDate()));
			else
				returnedConference.setEndDate(null);
			
			List<String> keywords = returnedConference.getKeywords();
			
			String keywordDB = this.getKeywords();
			if(keywordDB != null){
				String[] keywordArray= keywordDB.split(";");
				for(int i = 0; i < keywordArray.length; i++){
					keywords.add(keywordArray[i]);
				}
			}
			
		} catch(Exception ex){
			ex.printStackTrace();
		}
		
		return returnedConference;
	}
}
