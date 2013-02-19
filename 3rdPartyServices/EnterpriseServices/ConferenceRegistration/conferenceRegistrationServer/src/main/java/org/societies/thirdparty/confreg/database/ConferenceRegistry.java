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

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.thirdparty.conferenceregistration.schema.Conference;
import org.societies.thirdparty.conferenceregistration.schema.ConferenceSession;

/**
 * Deal with the Conference Registration persistence
 *
 * @author <a href="mailto:sanchocsa@gmail.com">Sancho Rêgo</a> (PTIN)
 *  
 */
public class ConferenceRegistry {

	private static Logger logger = LoggerFactory.getLogger(ConferenceRegistry.class);
	private SessionFactory sessionFactory;
	private static int conferences = 0;
	
	public ConferenceRegistry() {
		logger.info("ConferenceRegistry");
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public Conference retrieveConference(String conferenceId){
		
		Session session = null;
		Conference result = null;
		
		try {
			session = sessionFactory.openSession();
			
			ConferenceEntry tmpConference = (ConferenceEntry) session.get(ConferenceEntry.class, conferenceId);
			
			if(tmpConference != null){
				result = tmpConference.createConferenceFromRegistry();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}
		}
		
		return result;
	}
	
	public ConferenceSession retrieveConferenceSession(String sessionId){
		
		Session session = null;
		ConferenceSession result = null;
		
		try {
			session = sessionFactory.openSession();
			
			SessionEntry tmpConferenceSession = (SessionEntry) session.get(SessionEntry.class, sessionId);
			
			if(tmpConferenceSession != null){
				result = tmpConferenceSession.createSessionFromRegistry();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}
		}
		
		return result;
	}
	
	public boolean storeConference(Conference conference){
		
		Session session = null;
		Transaction t = null;
		boolean result = false;
		
		try {
			session = sessionFactory.openSession();
			ConferenceEntry tmpConference = (ConferenceEntry) session.get(ConferenceEntry.class, conference.getConferenceId());
			
			t = session.beginTransaction();
			
			if(tmpConference != null){
				
				if(logger.isDebugEnabled())
					logger.debug("Conference already exists, updating instead!");		
				tmpConference.updateConference(conference);
				session.update(tmpConference);
				
			} else{
				if(logger.isDebugEnabled())
					logger.debug("Conference doesn't exist, testing!");
				tmpConference = new ConferenceEntry(conference);
				session.save(tmpConference);
			}
			
			t.commit();
			
			if(logger.isDebugEnabled())
				logger.debug("Stored Conference: " + conference.getName());

			result = true;
			
		} catch (Exception e) {
			if(t != null)
				t.rollback();
			e.printStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}
			return result;
		}
	}
	
	public boolean storeConferenceSession(ConferenceSession conferenceSession){
		
		Session session = null;
		Transaction t = null;
		boolean result = false;
		
		try {
			session = sessionFactory.openSession();
			SessionEntry tmpSession = (SessionEntry) session.get(SessionEntry.class, conferenceSession.getSessionId());
			
			t = session.beginTransaction();
			
			if(tmpSession != null){
				
				if(logger.isDebugEnabled())
					logger.debug("ConferenceSession already exists, updating instead!");		
				tmpSession.updateSession(conferenceSession);
				session.update(tmpSession);
				
			} else{
				if(logger.isDebugEnabled())
					logger.debug("ConferenceSession doesn't exist, testing!");
				tmpSession = new SessionEntry(conferenceSession);
				session.save(tmpSession);
			}
			
			t.commit();
			
			if(logger.isDebugEnabled())
				logger.debug("Stored ConferenceSession: " + conferenceSession.getName());

			result = true;
			
		} catch (Exception e) {
			if(t != null)
				t.rollback();
			e.printStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}
			return result;
		}
	}
	
	public boolean removeConference(String conferenceId){
		
		Session session = null;
		Transaction t = null;
		boolean result = false;
		
		if(logger.isDebugEnabled())
			logger.debug("Need to delete a Conference...");
		
		try {
			session = sessionFactory.openSession();

			t = session.beginTransaction();
			ConferenceEntry confEntry = (ConferenceEntry) session.get(ConferenceEntry.class, conferenceId);
			String hql = "delete from SessionRegistration where ConferenceId= :conferenceId";
			session.createQuery(hql).setString("conferenceId", conferenceId).executeUpdate();
			String hql_Sessions = "delete from SessionEntry where ConferenceId= :conferenceId";
			session.createQuery(hql_Sessions);
			session.delete(confEntry);
			t.commit();
			result = true;
			
		} catch (Exception e) {
			if(t != null)
				t.rollback();
			
			logger.error("Error removing the Conference.");
			e.printStackTrace();
			
		} finally {
			if (session != null) {
				session.close();
			}
			return result;
		}
	}
	
	public boolean removeConferenceSession(String conferenceSessionId, String sessionId){
		
		Session session = null;
		Transaction t = null;
		boolean result = false;
		
		if(logger.isDebugEnabled())
			logger.debug("Need to delete a Session...");
		
		try {
			session = sessionFactory.openSession();

			t = session.beginTransaction();
			ConferenceEntry confEntry = (ConferenceEntry) session.get(ConferenceEntry.class, sessionId);
			String hql = "delete from SessionRegistration where SessionId= :sessionId";
			session.createQuery(hql).setString("conferenceId", sessionId).executeUpdate();
			String hql_Sessions = "delete from SessionEntry where SessionId= :sessionId";
			session.createQuery(hql_Sessions);
			session.delete(confEntry);
			t.commit();
			result = true;
			
		} catch (Exception e) {
			if(t != null)
				t.rollback();
			
			logger.error("Error removing the Conference.");
			e.printStackTrace();
			
		} finally {
			if (session != null) {
				session.close();
			}
			return result;
		}
		
	}
	
	public List<ConferenceSession> getConferenceSessions(String conferenceId){

		List<ConferenceSession> returnedConferenceSession= new ArrayList<ConferenceSession>();
		Session session = null;
		
		try {
			session = sessionFactory.openSession();
			
			List<SessionEntry> tmpConferenceSessionList = (List<SessionEntry>) session
					.createCriteria(SessionEntry.class).add(Restrictions.eq("conferenceId", conferenceId)).list();
						
			for (SessionEntry sessionEntry : tmpConferenceSessionList) {
				returnedConferenceSession.add(sessionEntry.createSessionFromRegistry());
			}
			
		} catch (Exception e) {
			e.getStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return returnedConferenceSession;
	}

	public List<Conference> getAllConferences(){
	
		List<Conference> returnedConference= new ArrayList<Conference>();
		Session session = null;
		
		try {
			session = sessionFactory.openSession();
			
			List<ConferenceEntry> tmpConferenceList = (List<ConferenceEntry>) session
					.createCriteria(ConferenceEntry.class).list();
						
			for (ConferenceEntry conferenceEntry : tmpConferenceList) {
				returnedConference.add(conferenceEntry.createConferenceFromRegistry());
			}
			
		} catch (Exception e) {
			e.getStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return returnedConference;
	}
	
	public List<Conference> getMyConferences(String creatorId){

		List<Conference> returnedConference= new ArrayList<Conference>();
		Session session = null;
		
		try {
			session = sessionFactory.openSession();
			
			List<ConferenceEntry> tmpConferenceList = (List<ConferenceEntry>) session
					.createCriteria(ConferenceEntry.class).add(Restrictions.eq("creatorId", creatorId)).list();
						
			for (ConferenceEntry conferenceEntry : tmpConferenceList) {
				returnedConference.add(conferenceEntry.createConferenceFromRegistry());
			}
			
		} catch (Exception e) {
			e.getStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return returnedConference;
	}
	
	public List<Conference> getConferencesForParticipant(String userId){
		
		List<Conference> returnedConference= new ArrayList<Conference>();
		Session session = null;
		
		try {
			session = sessionFactory.openSession();
			
			List<ConferenceEntry> tmpConferenceList = (List<ConferenceEntry>) session
					.createCriteria(SessionRegistration.class).add(Restrictions.eq("CssId", userId)).list();
						
			for(ConferenceEntry confEntry: tmpConferenceList){
				confEntry.getConferenceId();
				session.get(arg0, arg1)
			}
			for (ConferenceEntry conferenceEntry : tmpConferenceList) {
				returnedConference.add(conferenceEntry.createConferenceFromRegistry());
			}
			
		} catch (Exception e) {
			e.getStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return returnedConference;
		
	}
	
	
	public boolean joinConference(String conferenceId, String participantId){
		
		boolean joinConf = false;
		
		Session session = null;
		Transaction t = null;
		
		try {
			session = sessionFactory.openSession();
			
			if(session.get(ConferenceEntry.class, conferenceId) != null){
				if(logger.isDebugEnabled())
					logger.debug("Found conference "+ conferenceId +" to join!");
				
				SessionRegistration newReg = new SessionRegistration(participantId, "N/A", conferenceId);
				t = session.beginTransaction();
				session.save(newReg);
				t.commit();
				
				joinConf = true;
				
				if(logger.isDebugEnabled())
					logger.debug("Joined conference!");
				
			} else {
				if(logger.isDebugEnabled())
					logger.debug("Couldn't find conference to join!");
			}
				
		} catch(Exception ex){
			logger.error("Error in joinConference: " + ex.getLocalizedMessage());
			ex.printStackTrace();
			if(t != null)
				t.rollback();
		} finally {
			if (session != null) {
				session.close();
			}
		}
		
		return joinConf;
		
	}
	
	public boolean leaveConference(String conferenceId, String participantId){
		
		boolean leaveConf = false;
		
		Session session = null;
		Transaction t = null;
		
		try {
			session = sessionFactory.openSession();
			
			if(session.get(ConferenceEntry.class, conferenceId) != null){
				if(logger.isDebugEnabled())
					logger.debug("Found conference "+ conferenceId +" to join!");
				
				SessionRegistration newReg = new SessionRegistration(participantId, "N/A", conferenceId);
				t = session.beginTransaction();
				session.save(newReg);
				t.commit();
				
				leaveConf = true;
				
				if(logger.isDebugEnabled())
					logger.debug("Joined conference!");
				
			} else {
				if(logger.isDebugEnabled())
					logger.debug("Couldn't find conference to join!");
			}
				
		} catch(Exception ex){
			logger.error("Error in joinConference: " + ex.getLocalizedMessage());
			ex.printStackTrace();
			if(t != null)
				t.rollback();
		} finally {
			if (session != null) {
				session.close();
			}
		}
		
		return leaveConf;
	}
	
	public boolean joinSession(String conferenceId, String sessionId, String participantId){
		
		boolean result = false;
		
		Session session = null;
		Transaction t = null;
		
		try {
			session = sessionFactory.openSession();
			
			if(session.get(ConferenceEntry.class, conferenceId) != null){
				if(logger.isDebugEnabled())
					logger.debug("Found conference "+ conferenceId +" to join!");
				
				if(session.get(SessionEntry.class, sessionId) != null){
					
					if(logger.isDebugEnabled())
						logger.debug("Found session "+ sessionId +" to join!");
					
					SessionRegistration newReg = new SessionRegistration(participantId, sessionId, conferenceId);
					t = session.beginTransaction();
					session.save(newReg);
					t.commit();
					
					result = true;
					
					if(logger.isDebugEnabled())
						logger.debug("Joined conference and session!");
				} else{
					if(logger.isDebugEnabled())
						logger.debug("Couldn't find the session "+ sessionId +" to join!");
				}
				
			} else {
				if(logger.isDebugEnabled())
					logger.debug("Couldn't find the conference "+ conferenceId +" to join!");
			}
				
		} catch(Exception ex){
			logger.error("Error in joinSession: " + ex.getLocalizedMessage());
			ex.printStackTrace();
			if(t != null)
				t.rollback();
		} finally {
			if (session != null) {
				session.close();
			}
		}
		
		return result;
		
	}
	
	public boolean leaveSession(String conferenceId, String sessionId, String participantId){
		
		boolean result = false;
		
		Session session = null;
		Transaction t = null;
		
		try {
			session = sessionFactory.openSession();
			
			if(session.get(ConferenceEntry.class, conferenceId) != null){
				if(logger.isDebugEnabled())
					logger.debug("Found conference "+ conferenceId +" to leave!");
				
				if(session.get(SessionEntry.class, sessionId) != null){
					
					if(logger.isDebugEnabled())
						logger.debug("Found session "+ sessionId +" to leave!");
					
					SessionRegistration newReg = new SessionRegistration(participantId, sessionId, conferenceId);
					t = session.beginTransaction();
					session.save(newReg);
					t.commit();
					
					result = true;
					
					if(logger.isDebugEnabled())
						logger.debug("Joined conference and session!");
				} else{
					if(logger.isDebugEnabled())
						logger.debug("Couldn't find the session "+ sessionId +" to join!");
				}
				
			} else {
				if(logger.isDebugEnabled())
					logger.debug("Couldn't find the conference "+ conferenceId +" to join!");
			}
				
		} catch(Exception ex){
			logger.error("Error in joinSession: " + ex.getLocalizedMessage());
			ex.printStackTrace();
			if(t != null)
				t.rollback();
		} finally {
			if (session != null) {
				session.close();
			}
		}
		
		return result;
	}
}
