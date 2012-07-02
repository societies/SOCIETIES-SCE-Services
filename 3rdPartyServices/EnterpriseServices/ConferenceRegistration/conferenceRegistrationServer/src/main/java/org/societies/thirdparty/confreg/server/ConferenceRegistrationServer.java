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

import java.util.List;

import org.societies.thirdparty.conferenceregistration.schema.Conference;
import org.societies.thirdparty.conferenceregistration.schema.Session;
import org.societies.thirdparty.confreg.api.IConferenceRegistration;
import org.societies.api.identity.IIdentity;

/**
 * Describe your class here...
 *
 * @author Sancho
 *
 */
public class ConferenceRegistrationServer implements IConferenceRegistration {

	/* (non-Javadoc)
	 * @see org.societies.thirdparty.confreg.api.IConferenceRegistration#getConference(java.lang.String)
	 */
	@Override
	public Conference getConference(String conferenceId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.thirdparty.confreg.api.IConferenceRegistration#getConferenceList()
	 */
	@Override
	public List<Conference> getConferenceList() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.thirdparty.confreg.api.IConferenceRegistration#updateConference(org.societies.thirdparty.conferenceregistration.schema.Conference)
	 */
	@Override
	public void updateConference(Conference updatedConference) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.societies.thirdparty.confreg.api.IConferenceRegistration#deleteConference(java.lang.String)
	 */
	@Override
	public void deleteConference(String conferenceId) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.societies.thirdparty.confreg.api.IConferenceRegistration#joinConference(java.lang.String, java.lang.String)
	 */
	@Override
	public void joinConference(String conferenceId, String memberId) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.societies.thirdparty.confreg.api.IConferenceRegistration#leaveConference(java.lang.String, java.lang.String)
	 */
	@Override
	public void leaveConference(String conferenceId, String memberId) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.societies.thirdparty.confreg.api.IConferenceRegistration#addSession(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public Session addSession(String conferenceId, String name,
			String description, String startDate, String endDate,
			String location) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.thirdparty.confreg.api.IConferenceRegistration#removeSession(java.lang.String)
	 */
	@Override
	public void removeSession(String sessionId) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.societies.thirdparty.confreg.api.IConferenceRegistration#updateSession(org.societies.thirdparty.conferenceregistration.schema.Session)
	 */
	@Override
	public void updateSession(Session session) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.societies.thirdparty.confreg.api.IConferenceRegistration#getSession(java.lang.String, java.lang.String)
	 */
	@Override
	public Session getSession(String sessionId, String conferenceId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.thirdparty.confreg.api.IConferenceRegistration#getConferenceSessions(java.lang.String)
	 */
	@Override
	public List<Session> getConferenceSessions(String conferenceId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Conference createConference(String name, String description,
			String startDate, String endDate, String location, IIdentity creator) {
		// TODO Auto-generated method stub
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

}
