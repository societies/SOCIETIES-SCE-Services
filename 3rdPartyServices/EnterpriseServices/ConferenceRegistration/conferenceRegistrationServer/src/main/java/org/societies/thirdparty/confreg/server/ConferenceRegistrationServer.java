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
package org.societies.thirdparty.confreg.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.rdpartyService.enterprise.interfaces.IReturnedResultCallback;
import org.societies.rdpartyService.enterprise.interfaces.ISharedCalendarClientRich;
import org.societies.thirdparty.conferenceregistration.schema.Conference;
import org.societies.thirdparty.conferenceregistration.schema.ConferenceSession;
import org.societies.thirdparty.confreg.api.IConferenceRegistration;
import org.societies.thirdparty.confreg.database.ConferenceRegistry;
import org.societies.api.cis.attributes.MembershipCriteria;
import org.societies.api.cis.attributes.Rule;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.identity.IIdentity;

/**
 * The cloud/server part of the Conference Registration Service
 *
 * @author <a href="mailto:sanchocsa@gmail.com">Sancho Rêgo</a> (PTIN)
 *
 */
public class ConferenceRegistrationServer implements IConferenceRegistration {

	private static Logger logger = LoggerFactory.getLogger(ConferenceRegistrationServer.class);
	private static ConferenceRegistry conferenceRegistry;
	private ICisManager cisManager;
	private ISharedCalendarClientRich calendarService;
	
	public ICisManager getCisManager(){
		return this.cisManager;
	}

	public void setCisManager(ICisManager cisManager){
		this.cisManager = cisManager;
	}
	
	public ISharedCalendarClientRich getCalendarService(){
		return this.calendarService;
	}
	
	public void setCalendarService(ISharedCalendarClientRich calendarService){
		this.calendarService = calendarService;
	}
	
	public ConferenceRegistrationServer(){
		
		if(logger.isInfoEnabled())
			logger.info("Conference Registration Server starting up...");
		
		if(logger.isDebugEnabled())
			logger.debug("Creating DatabaseRegistry");
		
		conferenceRegistry = new ConferenceRegistry();
		
	}
	
	@Override
	public Conference getConference(String conferenceId) {
		
		if(logger.isDebugEnabled())
			logger.debug("getConference for conferenceId: " + conferenceId );

		Conference myConference = null;
		
		try{
			myConference = conferenceRegistry.retrieveConference(conferenceId);
		} catch(Exception ex){
			ex.printStackTrace();
			logger.error("Conference Registration: exception: " + ex);
		}
		
		return myConference;
	}

	@Override
	public List<Conference> getConferenceList() {
		
		List<Conference> allConferences = null;
		
		try{
			allConferences = conferenceRegistry.getAllConferences();
		} catch(Exception ex){
			ex.printStackTrace();
		}

		if(allConferences == null){
			allConferences = new ArrayList<Conference>();
		}
		
		return allConferences;
	}

	@Override
	public void updateConference(Conference myConference) {

		if(logger.isDebugEnabled())
			logger.debug("updateConference for conferenceId: " + myConference.getConferenceId() );
		
		try{
			conferenceRegistry.storeConference(myConference);
		} catch(Exception ex){
			ex.printStackTrace();
			logger.error("Conference Registration: exception: " + ex);
		}

	}

	/* (non-Javadoc)
	 * @see org.societies.thirdparty.confreg.api.IConferenceRegistration#deleteConference(java.lang.String)
	 */
	@Override
	public void deleteConference(String conferenceId) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.societies.thirdparty.confreg.api.IConferenceRegistration#removeSession(java.lang.String)
	 */
	@Override
	public void removeSession(String sessionId) {
		// TODO Auto-generated method stub

	}


	@Override
	public void updateSession(ConferenceSession session) {
		
		if(logger.isDebugEnabled())
			logger.debug("updateSession for sessionId: " + session.getSessionId() );
		
		try{
			conferenceRegistry.storeConferenceSession(session);
		} catch(Exception ex){
			ex.printStackTrace();
			logger.error("Conference Registration: exception: " + ex);
		}


	}

	@Override
	public ConferenceSession getSession(String sessionId, String conferenceId) {
	
		if(logger.isDebugEnabled())
			logger.debug("getSession() for sessionId: " + sessionId + " and conferenceId: "+ conferenceId);
		
		ConferenceSession confSession = null;
		
		try{
			confSession = conferenceRegistry.retrieveConferenceSession(sessionId, conferenceId);
		} catch(Exception ex){
			logger.error("Exception getSession(): " + ex);
			ex.printStackTrace();
		}
		
		return confSession;
	}

	@Override
	public List<ConferenceSession> getConferenceSessions(String conferenceId) {

		if(logger.isDebugEnabled())
			logger.debug("Getting all Conference Sessions associated with : " + conferenceId);
		
		List<ConferenceSession> confSessionResult = null;
		try{
			confSessionResult = conferenceRegistry.getConferenceSessions(conferenceId);
		} catch(Exception ex){
			logger.error("Exception in Conference Registration Service! :" + ex.getLocalizedMessage());
			ex.printStackTrace();
		}
		
		return confSessionResult;
		
	}

	@Override
	public Conference createConference(String name, String description,
			String startDate, String endDate, String location, List<String> keywords,IIdentity creator) {
		
		if(logger.isInfoEnabled())
			logger.debug("Creating a new Conference!");
		
		if(logger.isDebugEnabled()){
			logger.debug("Conference Name: " + name);
			logger.debug("Conference Description: " + description);
			logger.debug("Conference startDate: " + startDate);
			logger.debug("Conference endDate: " + endDate);
			logger.debug("Conference location: " + location);
			logger.debug("Conference Creator: " + creator.getJid());
		}
		
		// Create conference object
		Conference newConference = new Conference();
		newConference.setName(name);
		newConference.setLocation(location);
		newConference.setEndDate(endDate);
		newConference.setStartDate(startDate);
		newConference.getKeywords().addAll(keywords);
		newConference.setCreatorId(creator.getJid());
		
		// Create CIS
		MembershipCriteria m = new MembershipCriteria();
		try {
			Rule r = new Rule();
			m.setRule(r);
		} catch(Exception ex){
			logger.error("Error creating Conference CIS");
			ex.printStackTrace();
		}
		
		ICisOwned myCis = null;
		
		newConference.setCisId(myCis.getCisId());
		newConference.setConferenceId(conferenceRegistry.getNewId());
		
		// Create Calendar Entry
		if(logger.isDebugEnabled())
			logger.debug("Now we are creating the calendar entry!");
		IReturnedResultCallback returnedResultCallback = null;
		String calendarSummary = "Conference '" + name + "': " + description;
		
		getCalendarService().createCISCalendar(returnedResultCallback, calendarSummary , myCis.getCisId());
		String calendarId = null;
		
		newConference.setCalendarId(calendarId);
		
		return null;
	}

	@Override
	public void joinSession(String sessionId, IIdentity member) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void leaveSession(String sessionId, IIdentity member) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getMyConferencePlan(IIdentity member) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.thirdparty.confreg.api.IConferenceRegistration#joinConference(java.lang.String, org.societies.api.identity.IIdentity)
	 */
	@Override
	public void joinConference(String conferenceId, IIdentity memberId) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.thirdparty.confreg.api.IConferenceRegistration#leaveConference(java.lang.String, org.societies.api.identity.IIdentity)
	 */
	@Override
	public void leaveConference(String conferenceId, IIdentity memberId) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.thirdparty.confreg.api.IConferenceRegistration#addSession(java.lang.String, java.lang.String, java.lang.String, java.util.Date, java.util.Date, java.lang.String)
	 */
	@Override
	public ConferenceSession addSession(String conferenceId, String name,
			String description, Date startDate, Date endDate, String location) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.thirdparty.confreg.api.IConferenceRegistration#getConferenceList(org.societies.api.identity.IIdentity)
	 */
	@Override
	public List<Conference> getConferenceList(IIdentity creator) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.thirdparty.confreg.api.IConferenceRegistration#createConference(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.societies.api.identity.IIdentity)
	 */
	@Override
	public org.societies.thirdparty.confreg.api.Conference createConference(
			String name, String description, String startDate, String endDate,
			String location, IIdentity creator) {
		// TODO Auto-generated method stub
		return null;
	}

}
