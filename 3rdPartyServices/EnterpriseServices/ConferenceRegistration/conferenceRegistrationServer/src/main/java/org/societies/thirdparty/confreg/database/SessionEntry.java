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
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.datatype.DatatypeFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.thirdparty.conferenceregistration.schema.ConferenceSession;

/**
 * The database representation of a Conference.
 *
 * @author <a href="mailto:sanchocsa@gmail.com">Sancho Rêgo</a> (PTIN)
 *
 */

@Entity
@Table(name = "ConferenceSessions")
public class SessionEntry implements Serializable {

	private static final long serialVersionUID = -928826757914525734L;
	private static Logger logger = LoggerFactory.getLogger(SessionEntry.class);
	
	private String description;
	private String name;
	private String location;
	private String startDate;
	private String endDate;
	private String cisId;
	private String conferenceId;
	private String keywords;
	private String sessionId;
	private String eventId;
	
	public SessionEntry(ConferenceSession mySession) {
		
		this.cisId = mySession.getCisId();
		this.conferenceId = mySession.getConferenceId();
		this.description = mySession.getDescription();
		this.name = mySession.getName();
		this.sessionId = mySession.getSessionId();
		this.eventId = mySession.getEventId();
		this.location = mySession.getLocation();

		if(mySession.getStartDate() != null){
			startDate = mySession.getStartDate().toXMLFormat();
		} else
			startDate = null;
		
		if(mySession.getEndDate() != null){
			endDate = mySession.getEndDate().toXMLFormat();
		} else
			endDate = null;
		
		List<String> keywords = mySession.getKeywords();
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
	
	@Column(name = "EventId")
	public String getEventId(){
		return this.eventId;
	}
	
	public void setEventId(String eventId){
		this.eventId = eventId;
	}
	
	@Id
	public String getSessionId(){
		return this.sessionId;
	}
	
	public void setSessionId(String sessionId){
		this.sessionId = sessionId;
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
	
	@Column(name = "ConferenceId")
	public String getConferenceId(){
		return this.conferenceId;
	}
	
	public void setConferenceId(String conferenceId){
		this.conferenceId = conferenceId;
	}
	
	public void updateSession(ConferenceSession session){
		
		if(session.getCisId() != null){
			this.setCisId(session.getCisId());
		}

		if(session.getEventId() != null){
			this.setEventId(session.getEventId());
		}

		if(session.getName() != null){
			this.setName(session.getName());
		}
		
		if(session.getDescription() != null){
			this.setDescription(session.getDescription());
		}

		if(session.getLocation() != null){
			this.setLocation(session.getLocation());
		}
		
		if(session.getStartDate() != null){
			this.setStartDate(session.getStartDate().toXMLFormat());
		}
		
		if(session.getEndDate() != null){
			this.setEndDate(session.getEndDate().toXMLFormat());
		}
		
		List<String> keywords = session.getKeywords();
		if(keywords != null && !keywords.isEmpty()){
			String keywordDB = "";
			for(String keyword: keywords){
				keywordDB+=(';' + keyword);
			}
			this.setKeywords(keywordDB.substring(1));
		}
		
	}
	
	public ConferenceSession createSessionFromRegistry(){
		ConferenceSession returnedSession = null;
		
		try{
			
			returnedSession = new ConferenceSession();
			returnedSession.setEventId(this.getEventId());
			returnedSession.setCisId(this.getCisId());
			returnedSession.setConferenceId(this.getConferenceId());
			returnedSession.setSessionId(this.getSessionId());
			returnedSession.setDescription(this.getDescription());
			returnedSession.setLocation(this.getLocation());
			returnedSession.setName(this.getName());
			
			if(this.getStartDate() != null)
				returnedSession.setStartDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(this.getStartDate()));
			else
				returnedSession.setStartDate(null);
	
			if(this.getEndDate() != null)
				returnedSession.setEndDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(this.getEndDate()));
			else
				returnedSession.setEndDate(null);
			
			List<String> keywords = returnedSession.getKeywords();
			
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
		
		return returnedSession;
	}

}
