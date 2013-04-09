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
package org.societies.thirdparty.enterprise.sharedCalendar;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBElement;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.activity.IActivity;
import org.societies.api.activity.IActivityFeed;
import org.societies.api.activity.IActivityFeedCallback;
import org.societies.api.cis.management.ICis;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.comm.xmpp.pubsub.Subscriber;
import org.societies.api.context.broker.ICtxBroker;
import org.societies.api.css.devicemgmt.IDevice;
import org.societies.api.ext3p.schema.sharedcalendar.Calendar;
import org.societies.api.ext3p.schema.sharedcalendar.Event;
import org.societies.api.ext3p.schema.sharedcalendar.MethodType;
import org.societies.api.ext3p.schema.sharedcalendar.Message;
import org.societies.api.ext3p.schema.sharedcalendar.SharedCalendarBean;
import org.societies.api.ext3p.schema.sharedcalendar.CalendarMessage;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IdentityType;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.osgi.event.CSSEvent;
import org.societies.api.osgi.event.CSSEventConstants;
import org.societies.api.osgi.event.EventListener;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.personalisation.mgmt.IPersonalisationManager;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.personalisation.model.IActionConsumer;
import org.societies.api.personalisation.model.PersonalisablePreferenceIdentifier;
import org.societies.api.schema.activityfeed.MarshaledActivityFeed;
import org.societies.api.schema.cis.community.Community;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.services.IServices;
import org.societies.api.useragent.monitoring.IUserActionMonitor;
import org.societies.thirdparty.enterprise.sharedCalendar.persistence.CalendarDAO;
import org.societies.thirdparty.enterprise.sharedCalendar.persistence.EventDAO;
import org.societies.thirdparty.enterprise.sharedCalendar.commsServer.SharedCalendarCallBack;

import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.EventAttendee;

/**
 * Back-end class that implements the calendar management logic.
 * 
 * @author solutanet
 * 
 */
public class SharedCalendar extends EventListener implements ISharedCalendar, IActionConsumer, Subscriber {

	static final Logger log = LoggerFactory.getLogger(SharedCalendar.class);

	private SharedCalendarGoogleUtil googleUtil;
	private SessionFactory sessionFactory;
	private IEventMgr evtMgr;
	/**
	 * This is the set of all available instances of the IDevice interface.
	 * A DeviceListener bean instance tracks whenever a new IDevice is bound or unbound.
	 * See http://static.springsource.org/osgi/docs/1.2.1/reference/html-single/#service-registry:refs:collection:dynamics
	 */
	private Set<IDevice> availableDevices;
	private IServices serviceMgmt;
	private ICisManager cisManager;
	private ICommManager commManager;
	private IIdentity myId;
	private IPersonalisationManager personalisation;
	private ICtxBroker ctxBroker;
	private IUserActionMonitor userAction;
	private PubsubClient pubSub;
	
	private HashMap<String,Event> recommendedEvents;
	private HashMap<String,Event> recentEvents;
	private HashMap<String,Calendar> recentCalendars;
	
	private String myCalendarId;
	private CalendarDatabase database;
	
	//private IActivityFeed activityFeed;
	
	public IUserActionMonitor getUserAction() {
		return userAction;
	}

	public void setUserAction(IUserActionMonitor userAction) {
		this.userAction = userAction;
	}

	public PubsubClient getPubSub(){
		return pubSub;
	}
	
	public void setPubSub(PubsubClient pubSub){
		this.pubSub = pubSub;
	}
	
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public SharedCalendarGoogleUtil getGoogleUtil() {
		return googleUtil;
	}

	public void setGoogleUtil(SharedCalendarGoogleUtil googleUtil) {
		this.googleUtil = googleUtil;
	}
	
	public IEventMgr getEvtMgr() {
		return evtMgr;
	}

	public void setEvtMgr(IEventMgr evtMgr) {
		this.evtMgr = evtMgr;
	}

	public Set<IDevice> getAvailableDevices() {
		return availableDevices;
	}

	public void setAvailableDevices(Set<IDevice> availableDevices) {
		this.availableDevices = availableDevices;
	}
	
	public IServices getServiceMgmt() {
		return serviceMgmt;
	}

	public void setServiceMgmt(IServices serviceMgmt) {
		this.serviceMgmt = serviceMgmt;
	}

	public ICisManager getCisManager() {
		return cisManager;
	}

	public void setCisManager(ICisManager cisManager) {
		this.cisManager = cisManager;
	}

	public ICommManager getCommManager() {
		return commManager;
	}

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}
	
	public IPersonalisationManager getPersonalisation() {
		return personalisation;
	}

	public void setPersonalisation(IPersonalisationManager personalisation) {
		this.personalisation = personalisation;
	}

	public ICtxBroker getCtxBroker() {
		return ctxBroker;
	}

	public void setCtxBroker(ICtxBroker ctxBroker) {
		this.ctxBroker = ctxBroker;
	}

	/*Init method */
	public void init() throws Exception {
		
		log.info("Personal Agenda");
		if(log.isDebugEnabled())
			log.debug("Init method for the Personal Agenda.");
		
		
		String myJid = getCommManager().getIdManager().getThisNetworkNode().getJid();
		
		if(log.isDebugEnabled())
			log.debug("Personal Agenda is running on JID: " + myJid);
		
		myId = getIIdentityFromJid(myJid);
		database = new CalendarDatabase();
		recommendedEvents = new HashMap<String,Event>();
		recentCalendars =  new HashMap<String,Calendar>();
		recentEvents = new HashMap<String,Event>();
		
		// Now checks if we have an internal CSS calendar, if not, creates it
		if(log.isDebugEnabled())
			log.debug("Checking for internal CSS");

		Calendar myCalendar = getMyCalendarFromNodeId(myId.getBareJid());
		
		if(myCalendar != null){
			if(log.isDebugEnabled())
				log.debug("We have a calendar, no need to create.");
			this.myCalendarId = myCalendar.getCalendarId();
		} else{
			if(log.isDebugEnabled())
				log.debug("We don't have a calendar, creating.");
			this.myCalendarId = createCalendar("My Calendar",myId);
		}
		
		// Now checks the CIS list and creates CIS 
		List<ICisOwned> ourCisList = getCisManager().getListOfOwnedCis();
		for(ICisOwned ourCis : ourCisList ){
			if(log.isDebugEnabled())
				log.debug("Checking " + ourCis.getName() + " for a Calendar");
			Calendar cisCalendar = getMyCalendarFromNodeId(ourCis.getCisId());
			if(cisCalendar != null){
				if(log.isDebugEnabled())
					log.debug(ourCis.getName() + " has a Calendar");
			} else{
				if(log.isDebugEnabled())
					log.debug(ourCis.getName() + " doesn't have a Calendar, creating.");
				IIdentity cisId = getCommManager().getIdManager().fromJid(ourCis.getCisId());
				String calendarId = createCalendar("Calendar for CIS " + ourCis.getName(),cisId);
				if(calendarId != null)
					if(log.isDebugEnabled())
						log.debug("Created calendar for " + ourCis.getName());
			}
		}
		
		if(log.isDebugEnabled())
			log.debug("Now getting the list of all subscribed CIS!");
		List<ICis> otherCisList = getCisManager().getRemoteCis();
		for(ICis otherCis : otherCisList){
			if(log.isDebugEnabled())
				log.debug("We are subscribed to CIS: " + otherCis.getName() + ". Does it have a calendar?");
				Calendar cisCalendar = retrieveCalendar(getIIdentityFromJid(otherCis.getCisId()),myId);
				if(cisCalendar != null){
					if(log.isDebugEnabled())
						log.debug("The CIS has a calendar, so we register this!");
					
				}
		}
	
		// Next we register for CIS events
		if(log.isDebugEnabled())
			log.debug("Now registering for CIS events");
		String eventSource = getCommManager().getIdManager().getThisNetworkNode().getBareJid();
		String eventFilter = "(&" +
		"(|(" + CSSEventConstants.EVENT_NAME + "=creation of CIS)("+ CSSEventConstants.EVENT_NAME + "=restore of CIS)(" + 
		CSSEventConstants.EVENT_NAME + "=deletion of CIS)(" + CSSEventConstants.EVENT_NAME + "=subscription of CIS)(" + 		
		CSSEventConstants.EVENT_NAME + "=unsubscription of CIS))"
		 + "("+ CSSEventConstants.EVENT_SOURCE + "="+eventSource+")" +  
		")";
		String[] eventTypes = new String[] {EventTypes.CIS_CREATION, EventTypes.CIS_DELETION, EventTypes.CIS_RESTORE, EventTypes.CIS_SUBS, EventTypes.CIS_UNSUBS};
		getEvtMgr().subscribeInternalEvent(this, eventTypes, eventFilter);
		
	}	
		
	private String createCalendar(String calendarSummary, IIdentity node){
			
		if(log.isDebugEnabled())
			log.debug("Creating a calendar for " + node.getJid() + " ; need to check what type of node it is");
		
		String calendarId = null;
		boolean calendarResult = false;
		
		try{
			
			if(!node.getType().equals(IdentityType.CIS)){
				
				if(log.isDebugEnabled())
					log.debug("It's not a CIS, so we're trying to create our private calendar!");
				
				calendarId = getGoogleUtil().createCalendar(calendarSummary,node.getBareJid());
				if(calendarId != null)
					calendarResult = database.createCalendar(node.getBareJid(),calendarId, calendarSummary);
				
				if(calendarResult){
					if(log.isDebugEnabled())
						log.debug("Created a new Calendar: " + calendarId);
					
				} else{
					if(log.isDebugEnabled())
						log.debug("There was a problem creating the calendar!");
					calendarId = null;
				}
				
			} else{
				
				if(log.isDebugEnabled())
					log.debug("It's a CIS, let's determine if it is ours or not!");
				
				ICisOwned cisOwned = getCisManager().getOwnedCis(node.getJid());
				if(cisOwned != null){
					if(log.isDebugEnabled())
						log.debug("It's our own CIS, so we'll create the calendar!");
					
					calendarId = getGoogleUtil().createCalendar(calendarSummary,node.getBareJid());
					if(calendarId != null)
						calendarResult = database.createCalendar(node.getBareJid(),calendarId, calendarSummary);
						
					if(calendarResult){
						if(log.isDebugEnabled())
							log.debug("Created a new Calendar: " + calendarId + " now creating the pubsub node");
						
						getPubSub().ownerCreate(node, calendarId);
						getPubSub().subscriberSubscribe(node, calendarId, this);
						
						this.notifyCisActivity(node.getJid(), myId.getIdentifier(), VERB_CIS_CALENDAR_CREATED, calendarSummary, null);

					} else{
						if(log.isDebugEnabled())
							log.debug("There was a problem creating the calendar!");
						calendarId = null;
					}
									
				} else{
					if(log.isDebugEnabled())
						log.debug("It's not our own CIS, so we should don't do anything for now");
					
					calendarId = null;
				}
			}
		} catch(Exception ex){
			ex.printStackTrace();
			log.error("Exception while creating calendar: " + ex);
		}
			
		return calendarId;
		
	}

	private boolean deleteCalendar(IIdentity node){
		
		if(log.isDebugEnabled())
			log.debug("Deleting a calendar for " + node.getJid() + " ; need to check what type of node it is");
		
		boolean deleteResult = false;
		
		try{
			String calendarId = database.getCalendarIdFromNodeId(node.getBareJid());
			
			if(calendarId == null){
				if(log.isDebugEnabled())
					log.debug("Couldn't find the Calendar Id");
				
				return false;
			}
			
			if(!node.getType().equals(IdentityType.CIS)){
				
				if(log.isDebugEnabled())
					log.debug("It's not a CIS, so we're trying to delete a private calendar!");
				
				deleteResult = database.deleteCalendar(calendarId);
				
				if(deleteResult){
					if(log.isDebugEnabled())
						log.debug("Deleted a calendar! Now trying to delete it from Google...");
	
					getGoogleUtil().deleteCalendar(calendarId);
					
				} else{
					if(log.isDebugEnabled())
						log.debug("There was a problem deleting the calendar!");
				}
				
			} else{
				
				if(log.isDebugEnabled())
					log.debug("It's a CIS, let's determine if it is ours or not!");
				
				ICisOwned cisOwned = getCisManager().getOwnedCis(node.getJid());
				if(cisOwned != null){
					if(log.isDebugEnabled())
						log.debug("It's our own CIS, so we'll delete the calendar!");
					
					String calendarName = database.getCalendarNameFromCalendarId(calendarId);
					deleteResult = database.deleteCalendar(calendarId);
					
					if(deleteResult){
						if(log.isDebugEnabled())
							log.debug("Deleted a calendar!");
						
						if(log.isDebugEnabled())
							log.debug("Now trying to delete it from Google...");
						
						getGoogleUtil().deleteCalendar(calendarId);
						
						if(log.isDebugEnabled())
							log.debug("Publishing it on PubSub");
						
						CalendarMessage message = new CalendarMessage();
						message.setMessage();
						message.setCalendarId(calendarId);
						String messageId = message.getMessage()+"_"+ message.getCalendarId();
						getPubSub().subscriberUnsubscribe(node, calendarId, this);
						
						getPubSub().publisherPublish(node, calendarId, messageId, message);								
						//getPubSub().ownerDelete(node, calendarId);
						
						//TODO
						//Unsubscribe from all events
						
						this.notifyCisActivity(node.getJid(), myId.getBareJid(), VERB_CIS_CALENDAR_DELETED, calendarName, null);
								
					} else{
						if(log.isDebugEnabled())
							log.debug("There was a problem deleting the calendar!");
					}
	
				} else{
					if(log.isDebugEnabled())
						log.debug("It's not our own CIS, so we don't do anything for now");
					
				}
				
			}
		} catch(Exception ex){
			ex.printStackTrace();
			log.error("Exception while creating calendar: " + ex);
		}
			
		return deleteResult;
		
	}

	@Override
	public Calendar retrieveCalendar(IIdentity node, IIdentity requestor) {
		
		if(log.isDebugEnabled())
			log.debug("Trying to get Calendar for node: " + node.getJid());
		
		Calendar returnedCalendar = null;
		
		try{
			
			if(node.getType().equals(IdentityType.CIS)){
				
				if(log.isDebugEnabled())
					log.debug("It's a CIS, let's determine if it is ours or not!");
				
				ICisOwned cisOwned = getCisManager().getOwnedCis(node.getJid());
				if(cisOwned == null){
					if(log.isDebugEnabled())
						log.debug("It's not our own CIS, so we should send it to that CIS");
					
					ICis targetCis = getCisManager().getCis(node.getJid());
					
					if(targetCis == null){
						if(log.isDebugEnabled())
							log.debug("We couldn't find the Cis!");
							return null;
					}
					
					SharedCalendarBean calendarBean = new SharedCalendarBean();

					calendarBean.setMethod(MethodType.RETRIEVE_CALENDAR);
					calendarBean.setNodeId(node.getJid());
					calendarBean.setRequestor(requestor.getJid());
					
					CalendarResultCallback callback = new CalendarResultCallback();
					this.sendRequestToCIS(targetCis, requestor, calendarBean, callback);
					
					returnedCalendar = callback.getResult().getCalendar();
					
					if(log.isDebugEnabled())
						log.debug("Received result: " + returnedCalendar.getSummary());
					
				} else{
					
					if(log.isDebugEnabled())
						log.debug("The CIS belongs to us, so we proceed normally");
				}
			}
			
			if(log.isDebugEnabled())
				log.debug("Getting the calendar!");

			returnedCalendar = this.getMyCalendarFromNodeId(node.getBareJid());
						
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
		
		
		return returnedCalendar;
	}

	public Event retrieveEvent(String eventId, IIdentity node, IIdentity requestor){
		
		if(log.isDebugEnabled())
			log.debug("Retrieving an event from the calendar");
		
		Event returnedEvent = null;
		
		try{
			
			if(node.getType().equals(IdentityType.CIS)){
				
				if(log.isDebugEnabled())
					log.debug("It's a CIS, let's determine if it is ours or not!");
				
				ICisOwned cisOwned = getCisManager().getOwnedCis(node.getJid());
				if(cisOwned == null){
					if(log.isDebugEnabled())
						log.debug("It's not our own CIS, so we should send it to that CIS");
					
					ICis targetCis = getCisManager().getCis(node.getJid());
					
					if(targetCis == null){
						if(log.isDebugEnabled())
							log.debug("We couldn't find the Cis!");
							return null;
					}
					
					SharedCalendarBean calendarBean = new SharedCalendarBean();

					calendarBean.setMethod(MethodType.RETRIEVE_EVENT);
					calendarBean.setNodeId(node.getJid());
					calendarBean.setEventId(eventId);
					calendarBean.setRequestor(requestor.getJid());
					
					CalendarResultCallback callback = new CalendarResultCallback();
					this.sendRequestToCIS(targetCis, requestor, calendarBean, callback);
					
					returnedEvent = callback.getResult().getEvent();

					}
					if(log.isDebugEnabled())
						log.debug("Received result: " + returnedEvent.getEventSummary());
					
				} else{
					
					if(log.isDebugEnabled())
						log.debug("The CIS belongs to us, so we proceed normally");
				}
			
			if(log.isDebugEnabled())
				log.debug("Getting the calendar!");

			String calendarId = database.getCalendarIdFromNodeId(node.getBareJid());
			if(calendarId == null){
				if(log.isDebugEnabled())
					log.debug("Couldn't find the calendar for this event...");
				return null;
			}
			
			returnedEvent = 
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
		
		return returnedEvent;
		
	}

	@Override
	public List<Event> retrieveEvents(IIdentity node, IIdentity requestor) {
		
		if(log.isDebugEnabled())
			log.debug("Retrieving all events for calendar on node: " + node);
		
		List<Event> eventList = new ArrayList<Event>();
		
		try{
					
			if(node.getType().equals(IdentityType.CIS)){
				
				if(log.isDebugEnabled())
					log.debug("It's a CIS, let's determine if it is ours or not!");
				
				ICisOwned cisOwned = getCisManager().getOwnedCis(node.getJid());
				if(cisOwned == null){
					if(log.isDebugEnabled())
						log.debug("It's not our own CIS, so we should send it to that CIS");
					
					ICis targetCis = getCisManager().getCis(node.getJid());
					
					if(targetCis == null){
						if(log.isDebugEnabled())
							log.debug("We couldn't find the Cis!");
							return null;
					}
					
					SharedCalendarBean calendarBean = new SharedCalendarBean();

					calendarBean.setMethod(MethodType.RETRIEVE_ALL_EVENTS);
					calendarBean.setNodeId(node.getJid());
					calendarBean.setRequestor(requestor.getJid());
					
					CalendarResultCallback callback = new CalendarResultCallback();
					this.sendRequestToCIS(targetCis, requestor, calendarBean, callback);
					
					eventList = (List<Event>) callback.getResult().getEventList();
					
					if(log.isDebugEnabled())
						log.debug("Received result: " + eventList.size());
				} else{
					
					if(log.isDebugEnabled())
						log.debug("The CIS belongs to us, so we proceed normally");
				}
			}
			
			String calendarId = database.getCalendarIdFromNodeId(node.getBareJid());
			
			if(calendarId == null){
				log.warn("Couldn't find calendar!");
				return eventList;
			}
			
			eventList = getGoogleUtil().retrieveAllEvents(calendarId);
			
			if(log.isDebugEnabled()){
				log.debug("We tried to get the eventList for the calendar: " + calendarId);
				log.debug("Number of events: " + eventList.size());
				for(Event event : eventList){
					log.debug("Event: " + event.getEventSummary() + " : " + event.getEventDescription() + " : " + event.getStartDate() + " : " + event.getEndDate());
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
		
		return eventList;
	}

	@Override
	public String createEvent(Event newEvent, IIdentity node, IIdentity requestor) {
		
		String returnedEventId = null;
			
		if(log.isDebugEnabled())
			log.debug("Creating event " + newEvent + " on calendar; need to check what type of node it is.");
			
		try{
			
			if(!node.getType().equals(IdentityType.CIS)){
					
				if(log.isDebugEnabled())
					log.debug("It's not a CIS, so we're trying to create an event in a local node");
				
				returnedEventId = util.createEvent(myCalendarId, newEvent.getEventSummary(), newEvent.getEventDescription(),
						XMLGregorianCalendarConverter.asDate(newEvent.getStartDate()), 
						XMLGregorianCalendarConverter.asDate(newEvent.getEndDate()), newEvent.getLocation());
					
				if(returnedEventId != null){
					if(log.isDebugEnabled())
							log.debug("Created a new Event: " + returnedEventId);
						newEvent.setEventId(returnedEventId);
						retrievedEvents.put(returnedEventId, newEvent);
					} else{
						if(log.isDebugEnabled())
							log.debug("There was a problem creating the event!");
						returnedEventId = null;
					}
					
			} else{
					
				if(log.isDebugEnabled())
					log.debug("It's a CIS, let's determine if it is ours or not!");
					
				ICisOwned cisOwned = getCisManager().getOwnedCis(node.getJid());
				
				if(cisOwned != null){
					if(log.isDebugEnabled())
						log.debug("It's our own CIS, so we'll create the Event!");
					
					String calendarId = database.getCalendarIdFromNodeId(node.getJid());
					if(calendarId == null){
						log.error("Error getting calendar, so aborting");
						return null;
					}
					
					returnedEventId = util.createEvent(calendarId, newEvent.getEventSummary(), newEvent.getEventDescription(),
							XMLGregorianCalendarConverter.asDate(newEvent.getStartDate()), 
							XMLGregorianCalendarConverter.asDate(newEvent.getEndDate()), newEvent.getLocation());
						
					if(returnedEventId != null){
						if(log.isDebugEnabled())
							log.debug("Created a new Event: " + returnedEventId);
						
						newEvent.setEventId(returnedEventId);
						retrievedEvents.put(returnedEventId, newEvent);
						
						if(log.isDebugEnabled())
							log.debug("Publishing event in pubsub!");
						
						CalendarMessage message = new CalendarMessage();
						message.setMessage(VERB_CIS_CALENDAR_EVENT_CREATED);
						message.setEvent(newEvent);
						String messageId = message.getMessage()+"_"+ newEvent.getEventId();
						
						getPubSub().publisherPublish(node, calendarId, messageId, message);
						
						if(log.isDebugEnabled())
							log.debug("Publishing event in activity feed!");
						
						this.notifyCisActivity(node.getJid(), requestor.getIdentifier(), VERB_CIS_CALENDAR_EVENT_CREATED, newEvent.getEventSummary(), calendarId);

					} else{
						if(log.isDebugEnabled())
							log.debug("There was a problem creating the event!");
						returnedEventId = null;
					}
										
				} else{
					
					if(log.isDebugEnabled())
						log.debug("It's not our own CIS, so we should send it to that CIS");
						
					ICis targetCis = getCisManager().getCis(node.getJid());
						
					if(targetCis == null){
						if(log.isDebugEnabled())
							log.debug("We couldn't find the Cis!");
							return null;
					}
						
					SharedCalendarBean calendarBean = new SharedCalendarBean();

					calendarBean.setMethod(MethodType.CREATE_EVENT);
					calendarBean.setEvent(newEvent);
					calendarBean.setNodeId(node.getJid());
					calendarBean.setRequestor(requestor.getJid());
						
					CalendarResultCallback callback = new CalendarResultCallback();
					this.sendRequestToCIS(targetCis, requestor, calendarBean, callback);
						
					returnedEventId = (String) callback.getResult().getEventId();
			
				}
			}
					
		} catch(Exception ex){
			ex.printStackTrace();
			log.error("Exception while creating event: " + ex);
		}
							
		return returnedEventId;
	}
	
	@Override
	public boolean deleteEvent(String eventId, IIdentity node, IIdentity requestor) {
		boolean deletionOk=false;
		
		if(log.isDebugEnabled())
			log.debug("Deleting event " + eventId + " on calendar; need to check what type of node it is.");
			
		try{
				
			if(!node.getType().equals(IdentityType.CIS)){
					
				if(log.isDebugEnabled())
					log.debug("It's not a CIS, so we're trying to delete an event in a local node");
				
				Event myEvent = eventFromGoogleEvent(util.findEventUsingId(calendarId, eventId));
				util.deleteEvent(calendarId, eventId);
				
				deletionOk = true;
				
				if(log.isDebugEnabled())
					log.debug("Deleted an event!");
					
			} else{
					
				if(log.isDebugEnabled())
					log.debug("It's a CIS, let's determine if it is ours or not!");
					
				ICisOwned cisOwned = getCisManager().getOwnedCis(node.getJid());
				
				if(cisOwned != null){
					if(log.isDebugEnabled())
						log.debug("It's our own CIS, so we'll delete the Event!");
					
					Event myEvent = eventFromGoogleEvent(util.findEventUsingId(calendarId, eventId));
					util.deleteEvent(calendarId, eventId);
						
					if(log.isDebugEnabled())
						log.debug("Deleted a new Event: " + eventId);
					
					this.notifyCisActivity(node.getJid(), requestor.getIdentifier(), VERB_CIS_CALENDAR_EVENT_DELETED, myEvent.getEventSummary(), calendarId);

					deletionOk = true;
					
				} else{
					
					if(log.isDebugEnabled())
						log.debug("It's not our own CIS, so we should send it to that CIS");
						
					ICis targetCis = getCisManager().getCis(node.getJid());
						
					if(targetCis == null){
						if(log.isDebugEnabled())
							log.debug("We couldn't find the Cis!");
							return false;
					}
						
					SharedCalendarBean calendarBean = new SharedCalendarBean();

					calendarBean.setMethod(MethodType.DELETE_EVENT_ON_CALENDAR);
					calendarBean.setEventId(eventId);
					calendarBean.setNodeId(node.getJid());
					calendarBean.setCalendarId(calendarId);
					calendarBean.setRequestor(requestor.getJid());
						
					CalendarResultCallback callback = new CalendarResultCallback();
					this.sendRequestToCIS(targetCis, requestor, calendarBean, callback);
						
					deletionOk = (Boolean) callback.getResult().isLastOperationSuccessful();
			
				}
			}
					
		} catch(Exception ex){
			ex.printStackTrace();
			log.error("Exception while creating event: " + ex);
		}		

		return deletionOk;
	}
	

	@Override
	public boolean subscribeToEvent(String eventId, IIdentity node, IIdentity subscriber) {
		
		boolean subscriptionOk = false;
		
		if(log.isDebugEnabled())
			log.debug("Trying to subscribe to an event: " + eventId );
		
		try{
			
			if(!node.getType().equals(IdentityType.CIS)){
				
				if(log.isDebugEnabled())
					log.debug("It's not a CIS, so we're trying to subscribe to an event in a private calendar... we can only do that if it's OUR calendar");
				
				if(node.equals(subscriber)){
					if(log.isDebugEnabled())
						log.debug("We're automatically subscribed to our own events...");
					subscriptionOk = true;
				} else{
					if(log.isDebugEnabled())
						log.debug("For now we can't subscribe to an event that isn't ours");
				}
				
			} else{
				
				if(log.isDebugEnabled())
					log.debug("It's a CIS, let's determine if it is ours or not!");
				
				ICisOwned cisOwned = getCisManager().getOwnedCis(node.getJid());
				if(cisOwned != null){
					if(log.isDebugEnabled())
						log.debug("It's our own CIS, so we'll do the subscription for " + subscriber.getJid());
					
					String calendarId = database.getCalendarIdFromNodeId(node.getBareJid());
					
					if(calendarId==null){
						
					}
					subscriptionOk = getGoogleUtil().subscribeEvent(calendarId, eventId, subscriber);

					if(subscriptionOk){

						this.notifyCisActivity(node.getJid(), subscriber.getIdentifier(), VERB_CALENDAR_EVENT_SUBSCRIPTION, event.getDescription(), calendarId);
						
					} else{
						
						if(log.isDebugEnabled())
							log.debug("Couldn't subscribe to event!");
						
						subscriptionOk = false;
					}
					
				} else{
					if(log.isDebugEnabled())
						log.debug("It's not our own CIS, so we should send it to that CIS");
					
					ICis targetCis = getCisManager().getCis(node.getJid());
					
					if(targetCis == null){
						if(log.isDebugEnabled())
							log.debug("We couldn't find the Cis!");
							return false;
					}
					
					retrieveCalendar()
					Boolean localSub = database.subscribeEvent(node.getJid(), calendarId, eventId, subscriber.getBareJid());

					if(localSub){
						SharedCalendarBean calendarBean = new SharedCalendarBean();

						calendarBean.setMethod(MethodType.SUBSCRIBE_TO_EVENT);
						calendarBean.setNodeId(node.getJid());
						calendarBean.setEventId(eventId);
						calendarBean.setSubscriberId(subscriber.getJid());
						calendarBean.setRequestor(subscriber.getJid());
						
						CalendarResultCallback callback = new CalendarResultCallback();
						this.sendRequestToCIS(targetCis, subscriber, calendarBean, callback);
						
						subscriptionOk = callback.getResult().isSubscribingResult();
						
						if(log.isDebugEnabled())
							log.debug("Received result: " + subscriptionOk);
						
						if(subscriptionOk){
							if(log.isDebugEnabled())
								log.debug("Subscribed event: " + eventId);
						} else{
							if(log.isDebugEnabled())
								log.debug("There was a problem subscribing the event!");
							unsubscribeEvent(calendarId, eventId, subscriber.getBareJid());
						}
					} else{
						if(log.isDebugEnabled())
							log.debug("There was a problem subscribing the event!");
					}

				}
				
			}
			
		} catch(Exception ex){
			ex.printStackTrace();
			log.error("Exception while creating calendar: " + ex);
		}
		
		return subscriptionOk;
	}


	@Override
	public boolean unsubscribeFromEvent(String eventId, IIdentity subscriber) {
		
		boolean subscriptionOk = false;
		
		if(log.isDebugEnabled())
			log.debug("Trying to unsubscribe from an event: " + eventId );
		
		try{
			
			if(!node.getType().equals(IdentityType.CIS)){
				
				if(log.isDebugEnabled())
					log.debug("It's not a CIS, so we're trying to unsubscribe to an event in a private calendar... we can only do that if it's OUR calendar");
				
				if(node.equals(subscriber)){
					if(log.isDebugEnabled())
						log.debug("We can't actually unsubscribe from own events...");
					subscriptionOk = false;
				} else{
					if(log.isDebugEnabled())
						log.debug("For now we can't unsubscribe to an event that isn't ours");
				}
				
			} else{
				
				if(log.isDebugEnabled())
					log.debug("It's a CIS, let's determine if it is ours or not!");
				
				ICisOwned cisOwned = getCisManager().getOwnedCis(node.getJid());
				if(cisOwned != null){
					if(log.isDebugEnabled())
						log.debug("It's our own CIS, so we'll remove the subscription for " + subscriber.getBareJid());
					
					com.google.api.services.calendar.model.Event event = util
							.findEventUsingId(calendarId, eventId);

					List<EventAttendee> tmpAttendeeList = event.getAttendees();
					EventAttendee attendeeToRemove = null;
					
					for (EventAttendee ea : tmpAttendeeList) {
						if (subscriber.getBareJid().equalsIgnoreCase(ea.getDisplayName())) {
							attendeeToRemove = ea;
							break;
						}
					}
					
					boolean foundAndRemoved = attendeeToRemove != null && tmpAttendeeList.remove(attendeeToRemove);

					
					if(foundAndRemoved){
						event.setAttendees(tmpAttendeeList);
						util.updateEvent(calendarId, event);
						
						if(log.isDebugEnabled())
							log.debug("Removed subscription to event!");
						
						this.notifyCisActivity(node.getJid(), subscriber.getIdentifier(), VERB_CALENDAR_EVENT_UNSUBSCRIPTION, event.getDescription(), calendarId);
						subscriptionOk = unsubscribeEvent(calendarId, eventId, subscriber.getBareJid());
						subscriptionOk = true;
					} else{
						if(log.isDebugEnabled())
							log.debug("Removing subscription to event failed!");
					}

					
				} else{
					if(log.isDebugEnabled())
						log.debug("It's not our own CIS, so we should send it to that CIS");
					
					ICis targetCis = getCisManager().getCis(node.getJid());
					
					if(targetCis == null){
						if(log.isDebugEnabled())
							log.debug("We couldn't find the Cis!");
							return false;
					}
					
					SharedCalendarBean calendarBean = new SharedCalendarBean();

					calendarBean.setMethod(MethodType.UNSUBSCRIBE_FROM_EVENT);
					calendarBean.setCalendarId(calendarId);
					calendarBean.setCISId(node.getJid());
					calendarBean.setEventId(eventId);
					calendarBean.setSubscriberId(subscriber.getJid());
					calendarBean.setRequestor(subscriber.getJid());
					
					CalendarResultCallback callback = new CalendarResultCallback();
					this.sendRequestToCIS(targetCis, subscriber, calendarBean, callback);
					
					subscriptionOk = callback.getResult().isSubscribingResult();
					
					if(log.isDebugEnabled())
						log.debug("Received result: " + subscriptionOk);
					
					if(subscriptionOk){
						if(log.isDebugEnabled())
							log.debug("Unsubscribed event: " + eventId);
						subscriptionOk = unsubscribeEvent(calendarId, eventId, subscriber.getBareJid());
						subscriptionOk = true;

					} else{
						if(log.isDebugEnabled())
							log.debug("There was a problem unsubscribing the event!");
						
						subscriptionOk = false;
					}
				}
				
			}
		} catch(Exception ex){
			ex.printStackTrace();
			log.error("Exception while creating calendar: " + ex);
		}
		
		return subscriptionOk;
	}

	@Override
	public List<Event> getEventsForSubscriber(IIdentity subscriber){
		
		if(log.isDebugEnabled())
			log.debug("Trying to get events for subscriber: " + subscriber);
		
		List<Event> finalEventList = new ArrayList<Event>();
		
		if(log.isDebugEnabled())
			log.debug("We get the events we are subscribed to from the database!");
				
		List<EventDAO> eventsDAO = database.getEventsForSubscriberId(subscriber.getBareJid());
		
		for(EventDAO eventDAO : eventsDAO){
			if(log.isDebugEnabled())
				log.debug("Processing event: " + eventDAO.getEventId());
			Event subscribedEvent = eventFromDatabaseEvent(eventDAO);
			finalEventList.add(subscribedEvent);
		}
		
		return finalEventList;
	}

	/**
	 * Utility methods
	 */
	
	private Calendar getMyCalendarFromNodeId(String nodeId){
		
		if(log.isDebugEnabled())
			log.debug("Trying to get one of my calendars for a node: " + nodeId);

		Calendar result = null;
	
		String calendarId = database.getCalendarIdFromNodeId(nodeId);
	
		try{
			if(calendarId != null
					){
				result = getGoogleUtil().getCalendar(calendarId);
			}
		} catch(Exception ex){
			log.error("Exception getting calendar");
			ex.printStackTrace();
		}

		return result;
	}

	
	private Event eventFromDatabaseEvent(EventDAO databaseEvent){
		
		if(log.isDebugEnabled())
			log.debug("Getting an event from a Database event");
		
		if(log.isDebugEnabled())
			log.debug("First we search in our local cache!");
		
		Event result = retrievedEvent.get(databaseEvent.getEventId());
		
		if(result != null){
			if(log.isDebugEnabled())
				log.debug("Found event in local cache, returning: " + result.getEventSummary());
			return result;
		} else{

			
		}
		
		
	}
	
	/**
	 * This method retrieve from the list of all Calendars only ones that belong to the CIS
	 * @param listToFilter
	 * @param CISId
	 * @return
	 */
	private List<CalendarListEntry> filterCISCalendar(List<CalendarListEntry> listToFilter, String nodeId){
		Session session=sessionFactory.openSession();
		try{
			List<String> cisCalendarIdList=(List<String>) session.createCriteria(CalendarDAO.class).add(Restrictions.eq("nodeId", nodeId)).setProjection(Projections.property("calendarId")).list();
			Iterator<CalendarListEntry> iterator=listToFilter.iterator();
			while (iterator.hasNext()){
				if (!(cisCalendarIdList.contains(iterator.next().getId()))){
					iterator.remove();
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (session!=null){
				session.close();
			}
		}
		
		return listToFilter;
	}


	
	private void sendRequestToCIS(ICis cisTarget, IIdentity requestor, SharedCalendarBean calendarBean, CalendarResultCallback callback){
		try {
			
			IIdentity target = getCommManager().getIdManager().fromJid(cisTarget.getOwnerId());
			
			// SEND INFORMATION QUERY - RESPONSE WILL BE IN
			// "callback.RecieveMessage()"
			Stanza stanza = new Stanza(target);
			// SETUP CALENDAR CLIENT RETURN STUFF
			SharedCalendarCallBack calendarCallback = new SharedCalendarCallBack(
					stanza.getId(), callback);
			
			if(log.isDebugEnabled())
				log.debug("Sending message to remote calendar at "+ target.getJid() +" : " + calendarBean.getMethod());
			
			commManager.sendIQGet(stanza, calendarBean, calendarCallback);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error("ERROR: " + e.getStackTrace()[0].getMethodName());
		}
	}
	
	private void notifyCisActivity(String cisId, String actor, String verb, String object, String target){
		
		if (cisManager != null) {
			ICis iCis = cisManager.getCis(cisId);
			
			if (iCis != null) {
				IActivityFeed activityFeed = iCis.getActivityFeed();
				if (activityFeed!=null){
					IActivity notifyActivity = activityFeed.getEmptyIActivity();
					// notifyActivity.setId(new Long(1));
					if(actor != null)
						notifyActivity.setActor(actor);
					
					if(verb != null)
						notifyActivity.setVerb(verb);
					
					if(object != null)
						notifyActivity.setObject(object);
					
					if(target != null)
						notifyActivity.setTarget(target);
										
					if(log.isDebugEnabled())
						log.debug("Trying to add " + verb + " from "+ actor +" to activity for CIS: " + iCis.getName());
	
					activityFeed.addActivity(notifyActivity,
							new IActivityFeedCallback() {
								@Override
								public void receiveResult(MarshaledActivityFeed activityFeedObject) {
									if(log.isDebugEnabled())
										log.debug("Added an activity to the Activity Feed.");
								}
							}
					);					
				}
			}
		} else {
			log.debug("CIS manager or ActivityFeed service not available.");
		}
		
	}	

	private void processNewEvent(Event newEvent){
		if(log.isDebugEnabled())
			log.debug("We have a new event to process and see if we should recommend it!");
	}
	
	@Override
	public void handleInternalEvent(InternalEvent event) {
		if(log.isDebugEnabled())
			log.debug("Received Internal event: " + event.geteventName() + " : " + event.geteventType());
		
		if(event.geteventType().equals(EventTypes.CIS_CREATION)){
			if(log.isDebugEnabled())
				log.debug("A new CIS was created, we must create a new calendar for it");
			try{
				Community newCommunity = (Community) event.geteventInfo();
				IIdentity cisIdentity = getCommManager().getIdManager().fromJid(newCommunity.getCommunityJid());
				createCalendar("Calendar for CIS " +newCommunity.getCommunityName(),cisIdentity);
				
				//and then we ne
			} catch(Exception ex){
				log.error("Couldn't create a calendar for the community.");
				ex.printStackTrace();
			}
		}
		
		if(event.geteventType().equals(EventTypes.CIS_RESTORE)){
			if(log.isDebugEnabled())
				log.debug("A CIS was restored, we need to check if it has a calendar and create it if it doesn't.");
			try{
				Community newCommunity = (Community) event.geteventInfo();
				Calendar cisCalendar = getMyCalendarFromNodeId(newCommunity.getCommunityJid());
				
				if(cisCalendar != null){
					if(log.isDebugEnabled())
						log.debug(newCommunity.getCommunityName() + " has a Calendar");
				} else{
					if(log.isDebugEnabled())
						log.debug(newCommunity.getCommunityName() + " doesn't have a Calendar, creating.");
					IIdentity cisId = getCommManager().getIdManager().fromJid(newCommunity.getCommunityJid());
					String calendarId = createCalendar("Calendar for CIS " + newCommunity.getCommunityName(),cisId);
					if(calendarId != null)
						if(log.isDebugEnabled())
							log.debug("Created calendar for " + newCommunity.getCommunityName());
				}
				
			} catch(Exception ex){
				log.error("Couldn't create a calendar for the community.");
				ex.printStackTrace();
			}
		}
		
		if(event.geteventType().equals(EventTypes.CIS_DELETION)){
			if(log.isDebugEnabled())
				log.debug("A CIS was deleted, we need to remove the calendar and the events associated with it.");
			try{
				Community newCommunity = (Community) event.geteventInfo();
				String calendarId = database.getCalendarIdFromNodeId(newCommunity.getCommunityJid());
				//TODO Complete this.
				IIdentity cisIdentity = getCommManager().getIdManager().fromJid(newCommunity.getCommunityJid());
				
			} catch(Exception ex){
				log.error("Couldn't create a calendar for the community.");
				ex.printStackTrace();
			}
		}
		
		if(event.geteventType().equals(EventTypes.CIS_UNSUBS)){
			if(log.isDebugEnabled())
				log.debug("We unsubscribed from a CIS, so we should unsubscribe from the events in it.");
			
			try{
				Community newCommunity = (Community) event.geteventInfo();
				IIdentity cisIdentity = getIIdentityFromJid(newCommunity.getCommunityJid());
				String calendarId = database.getCalendarIdFromNodeId(newCommunity.getCommunityJid());
				
				if(log.isDebugEnabled())
					log.debug("Remove events from that CIS from the Recommended Events List");
				
				for(Event recommendedEvent : recommendedEvents){
					if(recommendedEvent.g)
						;
				}
				
			} catch(Exception ex){
				log.error("Couldn't create a calendar for the community.");
				ex.printStackTrace();
			}
		}

		if(event.geteventType().equals(EventTypes.CIS_SUBS)){
			if(log.isDebugEnabled())
				log.debug("We subscribed from a CIS, so we should try to get events from it.");
			
			try{
				Community newCommunity = (Community) event.geteventInfo();
				IIdentity cisIdentity = getIIdentityFromJid(newCommunity.getCommunityJid());
				String calendarId = database.getCalendarIdFromNodeId(newCommunity.getCommunityJid());
				
				
				
			} catch(Exception ex){
				log.error("Couldn't create a calendar for the community.");
				ex.printStackTrace();
			}
		}
	}

	@Override
	public void handleExternalEvent(CSSEvent event) {
		if(log.isDebugEnabled())
			log.debug("Received external event!");
	}

	@Override
	public ServiceResourceIdentifier getServiceIdentifier() {
		if(log.isDebugEnabled())
			log.debug("getServiceIdentifier called by Personalisation Manager.");
		
		return getServiceMgmt().getMyServiceId(getClass());
	}

	@Override
	public String getServiceType() {
		if(log.isDebugEnabled())
			log.debug("getServiceType called by Personalisation Manager");
		ServiceResourceIdentifier myIdentifier = getServiceMgmt().getMyServiceId(getClass());
		return getServiceMgmt().getMyServiceType(myIdentifier);
	}

	@Override
	public List<String> getServiceTypes() {
		if(log.isDebugEnabled())
			log.debug("getServiceTypes called by Personalisation Manager");
		List<String> result = new ArrayList<String>();
		ServiceResourceIdentifier myIdentifier = getServiceMgmt().getMyServiceId(getClass());
		result.add(getServiceMgmt().getMyServiceType(myIdentifier));
		return result;
	}

	@Override
	public boolean setIAction(IIdentity userId, IAction obj) {
		if(log.isDebugEnabled())
			log.debug("setIAction called!");
		return false;
	}


	@Override
	public List<PersonalisablePreferenceIdentifier> getPersonalisablePreferences() {
		if(log.isDebugEnabled())
			log.debug("getPersonalisablePreferences()");
		return null;
	}


	@Override
	public List<Event> getSubscribedEvents(IIdentity subscriber) {
		if(log.isDebugEnabled())
			log.debug("Trying to get all subscribed events!");

		List<Event> subscribedEvents = new ArrayList<Event>();
		
		List<EventDAO> eventList = database.getEventsForSubscriberId(subscriber.getBareJid());
		for(EventDAO eventDao : eventList){
			subscribedEvents.add(eventFromDatabaseEvent(eventDao));
		}
		
		return subscribedEvents;
	}
	
	private IIdentity getIIdentityFromJid(String jid) throws InvalidFormatException{		
		return getCommManager().getIdManager().fromJid(jid);
	}
	
	protected class CalendarDatabase {
		
		final Logger log = LoggerFactory.getLogger(CalendarDatabase.class);

		public CalendarDatabase(){
			if(log.isDebugEnabled())
				log.debug("CalendarDatabase sub-class started");
		}
		
		protected boolean subscribeEvent(Event event, String subscriberId){
			
			if(log.isDebugEnabled())
				log.debug("Creating an Event subscription;");
			
			Transaction t = null;
			Session session = null;
			boolean result = false;

			try {
				session = sessionFactory.openSession();

				EventDAO eventDAO = new EventDAO(nodeId, calendarId, eventId, subscriberId);

				t = session.beginTransaction();
				session.save(eventDAO);
				t.commit();
				result = true;
					
			} catch (HibernateException he) {
				log.error(he.getMessage());
				if (t != null) {
					t.rollback();
				}
			} finally {

				if (session != null) {
					session.close();
				}

			}
			
			if(result){
				if(log.isDebugEnabled())
					log.debug("Successfully created an event subscription, with id: " + eventId);
				}
			else{
				if(log.isDebugEnabled())
					log.debug("There was a problem creating the event!");
			}
			
			return result;

		}

		protected boolean unsubscribeEvent(String eventId, String subscriberId) {
			
			if(log.isDebugEnabled())
				log.debug("Deleting an event subscription: " + eventId);
			
			Transaction t = null;
			Session session = null;
			boolean result = false;

			try {
				session = sessionFactory.openSession();
				
				Criteria c = session.createCriteria(EventDAO.class).add(Restrictions.like("eventId", eventId));
				c.add(Restrictions.like("subscriberId", subscriberId));
				
				List<EventDAO> results = c.list();
				
				if (results.size() == 1) {					
					t = session.beginTransaction();
					EventDAO d = results.get(0);
					session.delete(d);
					t.commit();
					result = true;		
				} else {
					if(log.isDebugEnabled())
						log.debug("The event has not been found.");
				}
			} catch (HibernateException he) {
				log.error(he.getMessage());
				if (t != null) {
					t.rollback();
				}
			} finally {
				if (session != null) {
					session.close();
				}
			}
			
			return result;
		
		}
		
		protected boolean createCalendar(String nodeId, String calendarId, String calendarName){
			
			if(log.isDebugEnabled())
				log.debug("Creating a Calendar for " + nodeId + " with name: " + calendarName);
			
			Transaction t = null;
			Session session = null;
			boolean result = false;

			try {

				CalendarDAO cisCalendarDAO = new CalendarDAO(nodeId, calendarId, calendarName);

				t = session.beginTransaction();
				session.save(cisCalendarDAO);
				t.commit();
				result = true;
					
			} catch (HibernateException he) {
				log.error(he.getMessage());
				if (t != null) {
					t.rollback();
					try {
						getGoogleUtil().deleteCalendar(calendarId);
					} catch (IOException e) {
						log.error(e.getMessage());
						e.printStackTrace();
					}
				}
			} finally {

				if (session != null) {
					session.close();
				}

			}
			
			if(result){
				if(log.isDebugEnabled())
					log.debug("Successfully created a calendar, with id: " + calendarId);
				}
			else{
				if(log.isDebugEnabled())
					log.debug("There was a problem creating the calendar!");
			}
			
			return result;

		}

		
		protected List<EventDAO> getEventsForSubscriberId(String subscriberId){

			if(log.isDebugEnabled())
				log.debug("Getting events for: " + subscriberId);
			
			List<EventDAO> results = new ArrayList<EventDAO>();
			Transaction t = null;
			Session session = null;
			try {
				session = sessionFactory.openSession();
				results = (List<EventDAO>) session
						.createCriteria(CalendarDAO.class)
						.add(Restrictions.like("subscriberId", subscriberId)).list();
				
				if(log.isDebugEnabled())
					log.debug("Found " + results.size() + " ");
			} catch (HibernateException he) {
				log.error(he.getMessage());

			} finally {
				if (session != null) {
					session.close();
				}
			}	
			return results;
		}
		
		protected List<EventDAO> getEventsForCalendarId(String calendarId){

			if(log.isDebugEnabled())
				log.debug("Getting events for: " + calendarId);
			
			List<EventDAO> results = new ArrayList<EventDAO>();
			Transaction t = null;
			Session session = null;
			try {
				session = sessionFactory.openSession();

				results = (List<EventDAO>) session
						.createCriteria(CalendarDAO.class)
						.add(Restrictions.like("calendarId", calendarId)).list();
				
				if(log.isDebugEnabled())
					log.debug("Found " + results.size() + " ");
			} catch (HibernateException he) {
				log.error(he.getMessage());

			} finally {
				if (session != null) {
					session.close();
				}
			}	
			return results;
		}
		
		protected boolean deleteCalendar(String calendarId) {
			
			if(log.isDebugEnabled())
				log.debug("Database: Deleting a calendar with calendarId: " + calendarId);
			
			Transaction t = null;
			Session session = null;
			boolean result = false;

			try {
				session = sessionFactory.openSession();
				CalendarDAO template = new CalendarDAO();
				template.setCalendarId(calendarId);
				List<CalendarDAO> results = (List<CalendarDAO>) session.createCriteria(CalendarDAO.class)
						.add(Restrictions.like("calendarId", calendarId)).list();
				if (results.size() == 1) {					
					t = session.beginTransaction();
					CalendarDAO d = results.get(0);
					session.delete(d);
					t.commit();
					
					result = true;		
				} else {
					if(log.isDebugEnabled())
						log.debug("The calendarId has not been found.");
				}
			} catch (HibernateException he) {
				log.error(he.getMessage());
				if (t != null) {
					t.rollback();
				}
			} catch (Exception e) {
				log.error(e.getMessage());
				if (t != null) {
					t.rollback();
				}
			} finally {
				if (session != null) {
					session.close();
				}
			}
			
			return result;
		
		}	

		/**
		 * Returns the CisId of the CIS that owns the calendar with the provided id, or null if not found
		 * @param calendarId the CIS Calendar Id to search for
		 * @return The CisId (or null if not found)
		 */
		protected String getNodeIdFromCalendarId(String calendarId){
			
			if(log.isDebugEnabled())
				log.debug("Getting node Id from CalendarId");
			
			String result = null;
			Transaction t = null;
			Session session = null;
			try {
				session = sessionFactory.openSession();
				CalendarDAO template = new CalendarDAO();
				template.setCalendarId(calendarId);
				List<CalendarDAO> results = session
						.createCriteria(CalendarDAO.class)
						.add(Restrictions.like("calendarId", calendarId)).list();
				if (results.size() == 1) {					
					t = session.beginTransaction();
					CalendarDAO d = results.get(0);
					result = d.getNodeId();
					t.commit();
				} else {
					log.info("The calendar has not been found.");
				}
			} catch (HibernateException he) {
				log.error(he.getMessage());
				if (t != null) {
					t.rollback();
				}
			} finally {
				if (session != null) {
					session.close();
				}
			}	
			return result;
		}
	
		/**
		 * Returns the CisId of the CIS that owns the calendar with the provided id, or null if not found
		 * @param calendarId the CIS Calendar Id to search for
		 * @return The CisId (or null if not found)
		 */
		protected String getCalendarNameFromCalendarId(String calendarId){
			
			if(log.isDebugEnabled())
				log.debug("Getting node Id from CalendarId");
			
			String result = null;
			Transaction t = null;
			Session session = null;
			try {
				session = sessionFactory.openSession();
				CalendarDAO template = new CalendarDAO();
				template.setCalendarId(calendarId);
				List<CalendarDAO> results = session
						.createCriteria(CalendarDAO.class)
						.add(Restrictions.like("calendarId", calendarId)).list();
				if (results.size() == 1) {					
					t = session.beginTransaction();
					CalendarDAO d = results.get(0);
					result = d.getCalendarName();
					t.commit();
				} else {
					log.info("The calendar has not been found.");
				}
			} catch (HibernateException he) {
				log.error(he.getMessage());
				if (t != null) {
					t.rollback();
				}
			} finally {
				if (session != null) {
					session.close();
				}
			}	
			return result;
		}
		
		protected String getCalendarIdFromNodeId(String nodeId){
			
			if(log.isDebugEnabled())
				log.debug("Getting CalendarId from NodeId");
			
			String result = null;
			Transaction t = null;
			Session session = null;
			try {
				session = sessionFactory.openSession();
				List<CalendarDAO> results = session
						.createCriteria(CalendarDAO.class)
						.add(Restrictions.like("nodeId", nodeId)).list();
				if (results.size() == 1) {					
					t = session.beginTransaction();
					CalendarDAO d = results.get(0);
					result = d.getCalendarId();
					t.commit();
				} else {
					log.info("The calendar has not been found.");
				}
			} catch (HibernateException he) {
				log.error(he.getMessage());
				if (t != null) {
					t.rollback();
				}
			} finally {
				if (session != null) {
					session.close();
				}
			}	
			return result;
		}
		
		protected String getCalendarNameFromNodeId(String nodeId){
			
			if(log.isDebugEnabled())
				log.debug("Getting Calendar Name from NodeId");
			
			String result = null;
			Transaction t = null;
			Session session = null;
			try {
				session = sessionFactory.openSession();
				List<CalendarDAO> results = session
						.createCriteria(CalendarDAO.class)
						.add(Restrictions.like("nodeId", nodeId)).list();
				if (results.size() == 1) {					
					t = session.beginTransaction();
					CalendarDAO d = results.get(0);
					result = d.getCalendarName();
					t.commit();
				} else {
					log.info("The calendar has not been found.");
				}
			} catch (HibernateException he) {
				log.error(he.getMessage());
				if (t != null) {
					t.rollback();
				}
			} finally {
				if (session != null) {
					session.close();
				}
			}	
			return result;
		}
	}


	@Override
	public void pubsubEvent(IIdentity node, String calendarId,
			String itemId, Object item) {
		if(log.isDebugEnabled())
			log.debug("Received a Pub-Sub event, from node " + node.getJid() + " for calendar: " + calendarId);
		if(item.getClass().equals(CalendarMessage.class)){
			CalendarMessage calendarMessage = (CalendarMessage) item;
			if(log.isDebugEnabled())
				log.debug("Message was: " + calendarMessage.getMessage());
			
			if(calendarMessage.getMessage().equals(Message.NEW_EVENT)){
				Event newEvent = calendarMessage.getEvent();
		
				if(log.isDebugEnabled())
					log.debug("A new event arrived: " + newEvent.getEventSummary());
				
				processNewEvent(newEvent);
			}
					
			if(calendarMessage.getMessage().equals(Message.UPDATED_EVENT)){
				Event updatedEvent = calendarMessage.getEvent();
				
				if(log.isDebugEnabled())
					log.debug("Updated Event: " + updatedEvent.getEventSummary());
				
				processNewEvent(updatedEvent);
			}
			
			if(calendarMessage.getMessage().equals(Message.DELETED_EVENT)){
				Event updatedEvent = calendarMessage.getEvent();
				
				if(log.isDebugEnabled())
					log.debug("Updated Event: " + updatedEvent.getEventSummary());
				
				processNewEvent(updatedEvent);
			}
			
			if(calendarMessage.getMessage().equals(Message.NEW_ATTENDEE)){
				calendarMessage.getSubscriberId();
				Event event = calendarMessage.getEvent();
				
				if(log.isDebugEnabled())
					log.debug("There is a new attendee to event: " + event);
			}
			
			if(calendarMessage.getMessage().equals(Message.REMOVED_ATTENDEE)){
				calendarMessage.getSubscriberId();
				Event event = calendarMessage.getEvent();
				
				if(log.isDebugEnabled())
					log.debug("There is a new attendee to event: " + event);
			}

		}
		
	}

	@Override
	public List<Event> getRecommendedEvents(IIdentity subscriber) {
		
		if(log.isDebugEnabled())
			log.debug("Not implemented yet!");
		return null;
	}
	
	@Override
	public List<Event> getMyRecommendedEvents() {
		
		if(log.isDebugEnabled())
			log.debug("Getting Recommended Events");
		
		Collection<Event> recEvents = recommendedEvents.values();
		List<Event> resultEvents = new ArrayList<Event>(recEvents);
		
		if(log.isDebugEnabled()){
			for(Event recomEvent: resultEvents){
				log.debug("Recommended Events: " + recomEvent.getEventSummary() );
			}
		}
		
		return resultEvents;
	}


	@Override
	public List<Event> findEventsInCalendar(IIdentity node, Event searchEvent) {
		if(log.isDebugEnabled())
			log.debug("Searching for events in calendar for node: " + node.getJid());
		
		List<Event> eventList = new ArrayList<Event>();
		
		return null;
	}


	@Override
	public List<Event> findEventsAll(Event searchEvent) {
		if(log.isDebugEnabled())
			log.debug("Searching for events in ALL Calendars");
		
		List<Event> eventList = new ArrayList<Event>();
		
		if(log.isDebugEnabled())
			log.debug("Searching in local calendar!");
		
		List<Event> localEvents = findEventsInCalendar(myId,searchEvent);
		eventList.addAll(localEvents);
		
		if(log.isDebugEnabled())
			log.debug("Searching in CIS Calendards!");
				
		 List<ICis> cisList = getCisManager().getCisList();
		 
		 for(ICis cis : cisList){
			 if(log.isDebugEnabled())
				 log.debug("Searching for CIS: " + cis.getCisId());
			 try{
				 IIdentity cisId = getIIdentityFromJid(cis.getCisId());
				 eventList.addAll(findEventsInCalendar(cisId,searchEvent));
			 } catch(Exception ex){
				 log.error("Couldn't get IIdentity from CIS");
				 ex.printStackTrace();
			 }
		 }
		
		return eventList;
	}
	
}
