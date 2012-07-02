package org.societies.thirdparty.confreg.api;

import java.util.List;

import org.societies.api.identity.IIdentity;
import org.societies.thirdparty.conferenceregistration.schema.Conference;
import org.societies.thirdparty.conferenceregistration.schema.Session;

public interface IConferenceRegistration {
	
	/**
	 * This method returns a specific conference
	 * 
	 * @param conferenceId
	 * @return the requested conference
	 */
	public Conference getConference(String conferenceId);
	
	/**
	 * This method is used to get a full list of conferences available;
	 * 
	 * @return
	 */
	public List<Conference> getConferenceList();
	
	/**
	 * This method may be used to create a new Conference
	 * 
	 * @param name
	 * @param description
	 * @param startDate
	 * @param endDate
	 * @param location
	 * @param creator
	 * @return Conference object
	 */
	public Conference createConference(String name, String description, String startDate, String endDate, String location, IIdentity creator);
	
	/**
	 * This method is used to update the information on a conference
	 * 
	 * @param updatedConference
	 */
	public void updateConference(Conference updatedConference);
	
	/**
	 * This method is used to remove a conference
	 * 
	 * @param conferenceId
	 */
	public void deleteConference(String conferenceId);
	
	/**
	 * This method is used to register a given user to a conference
	 * 
	 * @param conferenceId
	 * @param memberId
	 */
	public void joinConference(String conferenceId, String memberId);
	
	/**
	 * This method is used to remove a user from a conference
	 * 
	 * @param conferenceId
	 * @param memberId
	 */
	public void leaveConference(String conferenceId, String memberId);
	
	/**
	 * This method is used to add a Session to a Conference
	 * 
	 * @param conferenceId
	 * @param name
	 * @param description
	 * @param startDate
	 * @param endDate
	 * @param location
	 * @return
	 */
	public Session addSession (String conferenceId, String name, String description, String startDate, String endDate, String location);

	/**
	 * This method is used to remove a Session from a Conference
	 * 
	 * @param sessionId
	 */
	public void removeSession(String sessionId);
	
	/**
	 * This method is used to update a Session in a conference
	 * 
	 * @param session
	 */
	public void updateSession(Session session);
	
	/**
	 * This method is used to retrieve a session from a Conference
	 * 
	 * @param sessionId
 	 * @param conferenceId
	 * @return
	 */
	public Session getSession(String sessionId, String conferenceId);
	
	/**
	 * This method is used to get a list of sessions in a conference
	 * 
	 * @param conferenceId
	 * @return
	 */
	public List<Session> getConferenceSessions(String conferenceId);
	
	/**
	 * This method is used to join a Session
	 * 
	 * @param sessionId
	 * @param member
	 */
	public void joinSession(String sessionId, IIdentity member);
	
	/**
	 * This method is used to leave a Session
	 * 
	 * @param sessionId
	 * @param member
	 */
	public void leaveSession(String sessionId, IIdentity member);
	
	public void getMyConferencePlan(IIdentity member);
}
