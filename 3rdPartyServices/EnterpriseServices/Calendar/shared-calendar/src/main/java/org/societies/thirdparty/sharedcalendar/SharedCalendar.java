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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
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
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.comm.xmpp.pubsub.Subscriber;
import org.societies.api.context.broker.ICtxBroker;
import org.societies.thirdparty.sharedcalendar.CalendarAsyncTask.Task;
import org.societies.thirdparty.sharedcalendar.api.schema.Calendar;
import org.societies.thirdparty.sharedcalendar.api.schema.Event;
import org.societies.thirdparty.sharedcalendar.api.schema.MethodType;
import org.societies.thirdparty.sharedcalendar.api.schema.Message;
import org.societies.thirdparty.sharedcalendar.api.schema.SharedCalendarBean;
import org.societies.thirdparty.sharedcalendar.api.schema.CalendarMessage;
import org.societies.thirdparty.sharedcalendar.api.schema.SharedCalendarResult;
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
import org.societies.api.services.ServiceMgmtEvent;
import org.societies.api.services.ServiceMgmtEventType;
import org.societies.api.services.ServiceUtils;
import org.societies.api.useragent.monitoring.IUserActionMonitor;
import org.societies.thirdparty.sharedcalendar.api.CalendarPreference;
import org.societies.thirdparty.sharedcalendar.api.UserWarning;
import org.societies.thirdparty.sharedcalendar.commsServer.SharedCalendarCallBack;
import org.societies.thirdparty.sharedcalendar.persistence.CalendarDAO;
import org.societies.thirdparty.sharedcalendar.persistence.EventDAO;

/**
 * Back-end class that implements the calendar management logic.
 * 
 * @author <a href="mailto:sanchocsa@gmail.com">Sancho Rêgo</a> (PTIN)
 * 
 */
public class SharedCalendar extends EventListener implements ISharedCalendarServer, Subscriber {

	static final Logger log = LoggerFactory.getLogger(SharedCalendar.class);

	private CalendarGoogleUtil googleUtil;
	private SessionFactory sessionFactory;
	private IEventMgr evtMgr;

	private IServices serviceMgmt;
	private ICisManager cisManager;
	private ICommManager commManager;
	private IIdentity myId;
	private IPersonalisationManager personalisation;
	private IUserActionMonitor userAction;
	private PubsubClient pubSub;
    private CalendarPreferenceManager preferences;
	
	private List<UserWarning> userWarnings;
	private ConcurrentHashMap<String,Event> recommendedEvents;
	private ConcurrentHashMap<String,Event> recentEvents;
	private ConcurrentHashMap<String,Calendar> recentCalendars;
	
	private String myCalendarId;
	private CalendarDatabase database;
	private CalendarContextUtils context;


	protected static ExecutorService executor;
	
	private ServiceResourceIdentifier mySRI;

	private CalendarCleanerUpdater cleaner;
	
	// NORMAL ACCESSORS

	protected CalendarDatabase getDatabase() {
		return database;
	}

	protected void setDatabase(CalendarDatabase database) {
		this.database = database;
	}


	protected IIdentity getMyId() {
		return myId;
	}

	protected void setMyId(IIdentity myId) {
		this.myId = myId;
	}

	protected ConcurrentHashMap<String, Event> getRecommendedEvents() {
		return recommendedEvents;
	}

	protected void setRecommendedEvents(
			ConcurrentHashMap<String, Event> recommendedEvents) {
		this.recommendedEvents = recommendedEvents;
	}

	protected ConcurrentHashMap<String, Calendar> getRecentCalendars() {
		return recentCalendars;
	}

	protected void setRecentCalendars(
			ConcurrentHashMap<String, Calendar> recentCalendars) {
		this.recentCalendars = recentCalendars;
	}

	protected ServiceResourceIdentifier getMySRI() {
		return mySRI;
	}

	protected void setMySRI(ServiceResourceIdentifier mySRI) {
		this.mySRI = mySRI;
	}
	
	protected ConcurrentHashMap<String, Event> getRecentEvents() {
		return recentEvents;
	}

	protected void setRecentEvents(ConcurrentHashMap<String, Event> recentEvents) {
		this.recentEvents = recentEvents;
	}
	
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

	public CalendarGoogleUtil getGoogleUtil() {
		return googleUtil;
	}

	public void setGoogleUtil(CalendarGoogleUtil googleUtil) {
		this.googleUtil = googleUtil;
	}
	
	public IEventMgr getEvtMgr() {
		return evtMgr;
	}

	public void setEvtMgr(IEventMgr evtMgr) {
		this.evtMgr = evtMgr;
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

	public CalendarContextUtils getContext() {
		return context;
	}

	public void setContext(CalendarContextUtils context) {
		this.context = context;
	}
    
	public CalendarPreferenceManager getPreferences() {
		return preferences;
	}

	public void setPreferences(CalendarPreferenceManager preferences) {
		this.preferences = preferences;
	}


	public void init() throws Exception {
		
		log.info("Personal Agenda");
		if(log.isDebugEnabled())
			log.debug("Init method for the Personal Agenda.");
		
		
		String myJid = getCommManager().getIdManager().getThisNetworkNode().getJid();
		
		log.debug("Personal Agenda is running on JID: {}", myJid);
		
		myId = getIIdentityFromJid(myJid);
		mySRI = null;
		database = new CalendarDatabase();
		executor = Executors.newCachedThreadPool();

		recommendedEvents = new ConcurrentHashMap<String,Event>(25,0.8f,1);
		recentCalendars =  new ConcurrentHashMap<String,Calendar>(16,0.9f,1);
		recentEvents = new ConcurrentHashMap<String,Event>(60,0.8f,2);
		userWarnings = new ArrayList<UserWarning>();
		
		// Registering our class in pubSub
		List<String> classList = new ArrayList<String>();
		classList.add("org.societies.thirdparty.sharedcalendar.api.schema.CalendarMessage");
		getPubSub().addSimpleClasses(classList);
		
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
			
			myCalendar = getMyCalendarFromNodeId(myId.getBareJid());
		}
		
		recentCalendars.put(myId.getBareJid(), myCalendar);
		
		// Now checks the CIS list and creates CIS 
		List<CalendarDAO> myCalendars = database.getAllCalendars();
		for(CalendarDAO myCal: myCalendars){
			log.debug("Checking if {} still has a calendar...",myCal.getCalendarName());
			
			if(myCal.getNodeId().equals(myId.getBareJid()))
				continue;
			
			ICisOwned calCis = getCisManager().getOwnedCis(myCal.getNodeId());
			if(calCis == null){
				log.debug("We have calendar {} but the CIS no longer exists. Deleting!",myCal.getCalendarName());
				database.deleteEventsForCalendarId(myCal.getCalendarId());
				database.deleteCalendar(myCal.getCalendarId());
				googleUtil.deleteCalendar(myCal.getCalendarId());
			}
		}
		// TODO Now we clean up calendars for CIS that no longer exist...
		
		// Next we register for CIS events
		log.debug("Now registering for CIS events");
		String[] eventTypes = new String[] {EventTypes.CIS_CREATION, EventTypes.CIS_DELETION,
				EventTypes.CIS_RESTORE, EventTypes.CIS_SUBS, EventTypes.CIS_UNSUBS, EventTypes.SERVICE_LIFECYCLE_EVENT};
		getEvtMgr().subscribeInternalEvent(this, eventTypes, null);
		
		this.cleaner = new CalendarCleanerUpdater(this,60*5);
		
	}	
	
	public void destroy(){
		log.debug("Calendar Cleanup thread!");
		cleaner.shutdown();
		String[] eventTypes = new String[] {EventTypes.CIS_CREATION, EventTypes.CIS_DELETION, EventTypes.CIS_RESTORE, EventTypes.CIS_SUBS, EventTypes.CIS_UNSUBS,EventTypes.SERVICE_LIFECYCLE_EVENT};
		getEvtMgr().unSubscribeInternalEvent(this, eventTypes, null);
	}
	
	protected String createCalendar(String calendarSummary, IIdentity node){
			
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
					log.debug("Created a new Calendar: {}", calendarId);
					Calendar myCalendar = getMyCalendarFromNodeId(node.getBareJid());
					recentCalendars.put(node.getBareJid(), myCalendar);
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
						Calendar myCalendar = getMyCalendarFromNodeId(node.getBareJid());
						recentCalendars.put(node.getBareJid(), myCalendar);
						
						log.debug("Created a new Calendar: {} now creating the pubsub node",calendarId);
						
						getPubSub().ownerCreate(node, calendarId.replace('@', '-'));
						getPubSub().subscriberSubscribe(node, calendarId.replace('@', '-'), this);
						
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
			Calendar calendar = retrieveCalendar(node,myId);
			String calendarId = calendar.getCalendarId();
			
			if(calendarId == null){
				if(log.isDebugEnabled())
					log.debug("Couldn't find the Calendar Id");
				
				return false;
			}
			
			log.debug("Removing events from this calendar from recommended and recents");
			List<Event> events = retrieveEvents(node,myId);
			for(Event oldEvent : events){
				recentEvents.remove(oldEvent.getEventId());
				recommendedEvents.remove(oldEvent.getEventId());
			}
			
			recentCalendars.remove(node.getBareJid());
			
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
						
						log.debug("Deleted a calendar: {}",calendarName);
						
						log.debug("Now trying to delete {} it from Google...",calendarName);
						
						getGoogleUtil().deleteCalendar(calendarId);
						
						if(log.isDebugEnabled())
							log.debug("Publishing it on PubSub");
						
						CalendarMessage message = new CalendarMessage();
						message.setMessage(Message.CALENDAR_DELETED);
						message.setCalendarId(calendarId);
						String messageId = message.getMessage()+"_"+ message.getCalendarId();
						getPubSub().subscriberUnsubscribe(node, calendarId.replace('@', '-'), this);
						
						getPubSub().publisherPublish(node, calendarId.replace('@', '-'), messageId, message);								
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
						log.debug("It's not our own CIS, so we just unsubscribe from every event there!");
					List<EventDAO> subEvents = database.getEventsForCalendarId(calendarId);
					for(EventDAO event: subEvents){
						this.unsubscribeFromEvent(event.getEventId(),node, myId);
					}
				}
				
			}
		} catch(Exception ex){
			ex.printStackTrace();
			log.error("Exception while creating calendar: {}",ex);
		}
		
		return deleteResult;
		
	}

	@Override
	public Calendar retrieveCalendar(IIdentity node, IIdentity requestor) {
				
		Calendar returnedCalendar = recentCalendars.get(node.getBareJid());
		
		if(returnedCalendar != null){
			if(log.isDebugEnabled())
				log.debug("Found the calendar in our cache, so we can return it!");
			return returnedCalendar;
		}
		
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
					
					SharedCalendarResult beanResult = callback.getResult();
					if(beanResult == null) 
						returnedCalendar = null;
					else
						returnedCalendar = beanResult.getCalendar();
				
					if(returnedCalendar != null){
						log.debug("Received result: {}",returnedCalendar.getName());
						getPubSub().subscriberSubscribe(node, returnedCalendar.getCalendarId().replace('@', '-'), this);
						recentCalendars.put(node.getBareJid(), returnedCalendar);
					}
					
					return returnedCalendar;
					
				} else{
					
					if(log.isDebugEnabled())
						log.debug("The CIS belongs to us, so we proceed normally");
					
					returnedCalendar = this.getMyCalendarFromNodeId(node.getBareJid());
					if(returnedCalendar != null){
						log.debug("We have a calendar, so we check and subscribe to the pubsub!");
						recentCalendars.put(node.getBareJid(), returnedCalendar);
						try{
							List<String> myNode = getPubSub().discoItems(node, null);
							if(!myNode.contains(returnedCalendar.getCalendarId().replace('@', '-'))){
								log.debug("Creating pub-sub node for calendar {}!",returnedCalendar.getName());
								getPubSub().ownerCreate(node, returnedCalendar.getCalendarId().replace('@', '-'));
							} else{
								log.debug("PubSub node for calendar {} already Created!",returnedCalendar.getName());
							}
							getPubSub().subscriberSubscribe(node, returnedCalendar.getCalendarId().replace('@', '-'), this);
							
						} catch(Exception ex){
							log.error("Error creating pubsub node for calendar {}! {}", returnedCalendar.getName(),ex.getMessage());
							ex.printStackTrace();
						}
					}
				}
			} else{
			
				if(log.isDebugEnabled())
					log.debug("Getting the calendar!");

				returnedCalendar = this.getMyCalendarFromNodeId(node.getBareJid());
				recentCalendars.put(node.getBareJid(), returnedCalendar);
			}	
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Exception retrieving calendar for node {}: {}",node.getBareJid(),e.getMessage());
		}
		
		return returnedCalendar;
	}

	public Event retrieveEvent(String eventId, IIdentity node, IIdentity requestor){
		
		if(log.isDebugEnabled())
			log.debug("Retrieving an event from the calendar");
		
		Event returnedEvent = recentEvents.get(eventId);
		
		if(returnedEvent != null){
			if(log.isDebugEnabled())
				log.debug("Already had the event in our local cache!");
			return returnedEvent;
		}
		
		//Quicker way, for the trials
		Calendar searchCalendar = retrieveCalendar(node,requestor);
		if(searchCalendar != null){

			try{
				returnedEvent = getGoogleUtil().getEvent(eventId, searchCalendar.getCalendarId());
			
				if(returnedEvent != null)
					recentEvents.put(returnedEvent.getEventId(), returnedEvent);
	
			} catch(Exception ex){
				log.error("Exception retrieving events! {}",ex.getMessage());
				ex.printStackTrace();
			}
		}
		
		return returnedEvent;
		
		/*
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
					SharedCalendarResult beanResult = callback.getResult();
					if(beanResult == null) 
						returnedEvent = null;
					else
						returnedEvent = beanResult.getEvent();
					
					if(returnedEvent != null){
						log.debug("Received result: [}", returnedEvent.getName());
						recentEvents.put(eventId, returnedEvent);
					} else
						log.debug("Received null!");
					
					return returnedEvent;
							
				} else{
					
					if(log.isDebugEnabled())
						log.debug("The CIS belongs to us, so we proceed normally");
				}
			}
			if(log.isDebugEnabled())
				log.debug("Getting the calendar!");

			String calendarId = database.getCalendarIdFromNodeId(node.getBareJid());
			if(calendarId == null){
				if(log.isDebugEnabled())
					log.debug("Couldn't find the calendar for this event...");
				return null;
			}
			
			returnedEvent = getGoogleUtil().getEvent(eventId, calendarId);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Exception retrieving event {} : {}",eventId,e.getMessage());
		}
		
		if(returnedEvent != null)
			recentEvents.put(eventId, returnedEvent);
		
		return returnedEvent;
		*/
	}

	@Override
	public List<Event> retrieveEvents(IIdentity node, IIdentity requestor) {
		
		log.debug("Retrieving all events for calendar on node:{} ", node);
		
		List<Event> eventList = new ArrayList<Event>();
		//Quicker way, for the trials
		Calendar searchCalendar = retrieveCalendar(node,requestor);
		if(searchCalendar == null)
			return eventList;
		try{
		eventList = getGoogleUtil().retrieveAllEvents(searchCalendar.getCalendarId());
		
		Date now = new Date();
		for(Event recentEvent: eventList){
			if(recentEvent.getEndDate().after(now))
				recentEvents.put(recentEvent.getEventId(), recentEvent);
		}
		} catch(Exception ex){
			log.error("Exception retrieving events! {}",ex.getMessage());
			ex.printStackTrace();
		}
		
		return eventList;
		/*
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
					
					SharedCalendarResult beanResult = callback.getResult();
					if(beanResult != null) 
						eventList = beanResult.getEventList();
									
					Date now = new Date();
					for(Event recentEvent: eventList){
						if(recentEvent.getEndDate().after(now))
							recentEvents.put(recentEvent.getEventId(), recentEvent);
					}
					
					return eventList;
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
					log.debug("Event: " + event.getName() + " : " + event.getDescription() + " : " + event.getStartDate() + " : " + event.getEndDate());
				}
			}
			
			Date now = new Date();
			for(Event recentEvent: eventList){
				if(recentEvent.getEndDate().after(now))
					recentEvents.put(recentEvent.getEventId(), recentEvent);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Exception retrieving events for node {} : {}",node.getJid(),e.getMessage());
		}
		
		return eventList;
		*/
	}

	@Override
	public String createEvent(Event newEvent, IIdentity node, IIdentity requestor) {
		
		String returnedEventId = null;
			
		log.debug("Creating event {} on calendar {}; need to check what type of node it is.",newEvent.getName(),recentCalendars.get(node.getBareJid()).getName());
			
		try{
			
			if(!node.getType().equals(IdentityType.CIS)){
					
				if(log.isDebugEnabled())
					log.debug("It's not a CIS, so we're trying to create an event in a local node");
				
				returnedEventId = getGoogleUtil().createEvent(myCalendarId, newEvent.getName(), newEvent.getDescription(),
						newEvent.getStartDate(),newEvent.getEndDate(), newEvent.getLocation(), requestor.getBareJid(), node.getBareJid());

				if(returnedEventId != null){
					
					boolean subscribeOk = database.subscribeEvent(returnedEventId, myCalendarId, node.getJid(), requestor.getBareJid());

					if(subscribeOk){
						newEvent.setEventId(returnedEventId);
						newEvent.setNodeId(node.getJid());
						newEvent.setCreatorId(requestor.getBareJid());
						newEvent.setCalendarId(myCalendarId);
												
						recentEvents.put(returnedEventId, newEvent);
					} else{
						if(log.isDebugEnabled())
							log.debug("Had a problem putting the event in the database");
						getGoogleUtil().deleteEvent(myCalendarId, returnedEventId);
						returnedEventId = null;
					}
				}else{
					if(log.isDebugEnabled())
						log.debug("There was a problem creating the Google Event!");
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
					
					returnedEventId = getGoogleUtil().createEvent(calendarId, newEvent.getName(), newEvent.getDescription(),
							newEvent.getStartDate(),newEvent.getEndDate(), newEvent.getLocation(), requestor.getBareJid(), node.getBareJid());
						
					if(returnedEventId != null){
						
						boolean subscribeOk;
						if(requestor.equals(myId))
							subscribeOk = database.subscribeEvent(returnedEventId, calendarId, node.getJid(), requestor.getBareJid());
						else
							subscribeOk = true;
						
						if(subscribeOk){
							
							newEvent.setEventId(returnedEventId);
							newEvent.setNodeId(node.getJid());
							newEvent.setCreatorId(requestor.getBareJid());
							newEvent.setCalendarId(calendarId);
							
							recentEvents.put(returnedEventId, newEvent);
							
							if(log.isDebugEnabled())
								log.debug("Publishing event in pubsub!");
							
							CalendarMessage message = new CalendarMessage();
							message.setMessage(Message.NEW_EVENT);
							message.setEvent(newEvent);
							message.setRequestorId(requestor.getBareJid());
							message.setCalendarId(newEvent.getCalendarId());
							String messageId = message.getMessage()+"_"+ newEvent.getEventId();
							
							getPubSub().publisherPublish(node, calendarId.replace('@', '-'), messageId, message);
							
							if(log.isDebugEnabled())
								log.debug("Publishing event in activity feed!");
							
							this.notifyCisActivity(node.getJid(), requestor.getIdentifier(), VERB_CIS_CALENDAR_EVENT_CREATED, newEvent.getName(), calendarId);
							
						} else{
							if(log.isDebugEnabled())
								log.debug("Had a problem putting the event in the database");
							getGoogleUtil().deleteEvent(calendarId, returnedEventId);
							returnedEventId = null;
						}

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
					
					SharedCalendarResult beanResult = callback.getResult();
					if(beanResult != null) 	
						returnedEventId = beanResult.getEventId();
			
					if(returnedEventId != null){
						log.debug("Event {} was correctly created in remote node, so now we need to register it in the database!",newEvent.getName());
						
						newEvent = beanResult.getEvent();
						boolean subscribeOk;
						
						if(requestor.equals(myId))
							subscribeOk = database.subscribeEvent(returnedEventId, newEvent.getCalendarId(), node.getJid(), requestor.getBareJid());
						else
							subscribeOk = true;
						
						if(subscribeOk){
							recentEvents.put(returnedEventId, newEvent);
							log.debug("Event {} registered in the database, everything ok, so we continue...",newEvent.getName());
						} else{
							if(log.isDebugEnabled())
								log.debug("Problem with the database, we warn that the event will be deleted!");

							calendarBean.setMethod(MethodType.DELETE_EVENT);
							calendarBean.setEventId(newEvent.getEventId());
							calendarBean.setNodeId(node.getJid());
							calendarBean.setRequestor(requestor.getJid());
							
							callback = new CalendarResultCallback();
							this.sendRequestToCIS(targetCis, requestor, calendarBean, callback);
						}
					}
				}
			}
				
		} catch(Exception ex){
			ex.printStackTrace();
			log.error("Exception while creating event: {}",ex);
		}
				
		
		return returnedEventId;
	}
	
	@Override
	public boolean deleteEvent(String eventId, IIdentity node, IIdentity requestor) {
		boolean deletionOk=false;
		
		log.debug("Deleting event {} on calendar; need to check what type of node it is.",eventId);
			
		try{
			
			if(!node.getType().equals(IdentityType.CIS)){
					
				if(log.isDebugEnabled())
					log.debug("It's not a CIS, so we're trying to delete an event in a local node");
				
				Event myEvent = retrieveEvent(eventId,node,requestor);
				if(myEvent != null){
					getGoogleUtil().deleteEvent(myEvent.getCalendarId(), myEvent.getEventId());
					deletionOk = database.deleteEvent(myEvent.getEventId());
					
					if(log.isDebugEnabled())
						log.debug("Deleted an event!");
				} else{
					if(log.isDebugEnabled())
						log.debug("Event was null, so it didn't exist to delete!");
					deletionOk = true;
				}
								
			} else{
					
				if(log.isDebugEnabled())
					log.debug("It's a CIS, let's determine if it is ours or not!");
					
				ICisOwned cisOwned = getCisManager().getOwnedCis(node.getJid());
				
				if(cisOwned != null){
					if(log.isDebugEnabled())
						log.debug("It's our own CIS, so we'll delete the Event!");
					
					Event myEvent = retrieveEvent(eventId,node,requestor);
					if(myEvent != null){
						getGoogleUtil().deleteEvent(myEvent.getCalendarId(), myEvent.getEventId());
						deletionOk = database.deleteEvent(myEvent.getEventId());
						
						if(log.isDebugEnabled())
							log.debug("Deleted an event!");
					} else{
						if(log.isDebugEnabled())
							log.debug("Event was null, so it didn't exist to delete!");
						deletionOk = true;
					}
					
					if(log.isDebugEnabled())
						log.debug("Publishing event in pubsub!");
					
					CalendarMessage message = new CalendarMessage();
					message.setMessage(Message.DELETED_EVENT);
					message.setEvent(myEvent);
					message.setRequestorId(requestor.getBareJid());
					message.setCalendarId(myEvent.getCalendarId());
					String messageId = message.getMessage()+"_"+ myEvent.getEventId();
					
					getPubSub().publisherPublish(node, myEvent.getCalendarId().replace('@', '-'), messageId, message);
					
					this.notifyCisActivity(node.getJid(), requestor.getIdentifier(), VERB_CIS_CALENDAR_EVENT_DELETED, myEvent.getName(), myEvent.getCalendarId());
					
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

					calendarBean.setMethod(MethodType.DELETE_EVENT);
					calendarBean.setEventId(eventId);
					calendarBean.setNodeId(node.getJid());
					calendarBean.setRequestor(requestor.getJid());
						
					CalendarResultCallback callback = new CalendarResultCallback();
					this.sendRequestToCIS(targetCis, requestor, calendarBean, callback);
					
					SharedCalendarResult beanResult = callback.getResult();
					if(beanResult != null) 	
						deletionOk = beanResult.isLastOperationSuccessful();
					
					if(deletionOk){
						deletionOk = database.deleteEvent(eventId);
					}
				}
			}
					
		} catch(Exception ex){
			ex.printStackTrace();
			log.error("Exception while deleting event: {}",ex);
		}		

		if(deletionOk){
			recentEvents.remove(eventId);
			recommendedEvents.remove(eventId);
		}
		return deletionOk;
	}
	
	@Override
	public boolean updateEvent(Event updatedEvent, IIdentity requestor) {

		if(log.isDebugEnabled())
			log.debug("Request to update an event!");
		
		boolean updateResult = false;
		
		try{
			IIdentity node = getIIdentityFromJid(updatedEvent.getNodeId());
			
			if(!node.getType().equals(IdentityType.CIS)){
				
				if(log.isDebugEnabled())
					log.debug("It's not a CIS, so we're trying to update an event in a local node");
				
				Event eventUpdate =  getGoogleUtil().updateEvent(updatedEvent);
					
				if(eventUpdate != null){
					if(log.isDebugEnabled())
							log.debug("Updated the event: " + eventUpdate.getEventId());
					recentEvents.put(eventUpdate.getEventId(), eventUpdate);
					updateResult = true;
				} else{
					if(log.isDebugEnabled())
						log.debug("There was a problem updating the event!");
				}
					
			} else{
					
				if(log.isDebugEnabled())
					log.debug("It's a CIS, let's determine if it is ours or not!");
					
				ICisOwned cisOwned = getCisManager().getOwnedCis(node.getJid());
				
				if(cisOwned != null){
					if(log.isDebugEnabled())
						log.debug("It's our own CIS, so we'll update the Event!");
					
					Event eventUpdate =  getGoogleUtil().updateEvent(updatedEvent);
					
					if(eventUpdate != null){
						if(log.isDebugEnabled())
							log.debug("Updated an Event: " + eventUpdate.getEventId());
						updateResult = true;
						recentEvents.put(eventUpdate.getEventId(), eventUpdate);
						
						if(log.isDebugEnabled())
							log.debug("Publishing event in pubsub!");
						
						CalendarMessage message = new CalendarMessage();
						message.setMessage(Message.UPDATED_EVENT);
						message.setEvent(eventUpdate);
						message.setRequestorId(requestor.getBareJid());
						message.setCalendarId(eventUpdate.getCalendarId());
						String messageId = message.getMessage()+"_"+ eventUpdate.getEventId();
						
						getPubSub().publisherPublish(node, eventUpdate.getCalendarId().replace('@', '-'), messageId, message);
						
						if(log.isDebugEnabled())
							log.debug("Publishing event in activity feed!");
						
						this.notifyCisActivity(node.getJid(), requestor.getIdentifier(), VERB_CIS_CALENDAR_EVENT_UPDATED, eventUpdate.getName(), eventUpdate.getCalendarId());
						
						processNewEvent(eventUpdate);
					} else{
						if(log.isDebugEnabled())
							log.debug("There was a problem updating the event!");
						updateResult = false;
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

					calendarBean.setMethod(MethodType.UPDATE_EVENT);
					calendarBean.setEvent(updatedEvent);
					calendarBean.setNodeId(node.getJid());
					calendarBean.setCalendarId(updatedEvent.getCalendarId());
					calendarBean.setRequestor(requestor.getJid());
						
					CalendarResultCallback callback = new CalendarResultCallback();
					this.sendRequestToCIS(targetCis, requestor, calendarBean, callback);
					
					Event eventUpdate = null;
					SharedCalendarResult beanResult = callback.getResult();
					if(beanResult != null) 	
						eventUpdate = beanResult.getEvent();
						
					if(eventUpdate != null){
						updateResult = true;
						recentEvents.put(eventUpdate.getEventId(), eventUpdate);
					}
				}
			}

			
		} catch(Exception ex){
			log.error("Exception while updating an event!");
			ex.printStackTrace();
		}
		
		return updateResult;
	}
	
	@Override
	public boolean subscribeToEvent(String eventId, IIdentity node, IIdentity subscriber) {
		
		boolean subscriptionOk = false;
		Event theEvent = null;
		
		log.debug("{} trying to subscribe to an event: {}", node.getBareJid(), eventId );
		
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
				
				Calendar calendar = retrieveCalendar(node,myId);
				
				if(calendar== null){
					if(log.isDebugEnabled())
						log.debug("Couldn't find calendar, so can't subscribe!");
					return false;
				}
				
				String calendarId = calendar.getCalendarId();
				
				if(log.isDebugEnabled())
					log.debug("It's a CIS, let's determine if it is ours or not!");
				
				ICisOwned cisOwned = getCisManager().getOwnedCis(node.getJid());

				if(cisOwned != null){
					if(log.isDebugEnabled())
						log.debug("It's our own CIS, so we'll do the subscription for {} ", subscriber.getJid());
					
					subscriptionOk = getGoogleUtil().subscribeEvent(calendarId, eventId, subscriber);

					if(subscriptionOk){
						if(log.isDebugEnabled())
							log.debug("We subscribed to the event on Google, now we create the internal stuff!");
						
						if(subscriber.equals(myId)){
							database.subscribeEvent(eventId, calendarId, node.getJid(), subscriber.getBareJid());
							recommendedEvents.remove(eventId);
						}
						theEvent = getGoogleUtil().getEvent(eventId, calendarId);
						recentEvents.put(eventId, theEvent);
						
						if(log.isDebugEnabled())
							log.debug("Publishing event in pubsub!");
						
						CalendarMessage message = new CalendarMessage();
						message.setMessage(Message.NEW_ATTENDEE);
						message.setRequestorId(subscriber.getBareJid());
						message.setCalendarId(calendarId);
						message.setEvent(theEvent);
						String messageId = message.getMessage()+"_"+ theEvent.getEventId()+"_"+subscriber.getBareJid();
						
						getPubSub().publisherPublish(node, calendarId.replace('@', '-'), messageId, message);
						
						if(log.isDebugEnabled())
							log.debug("Publishing event in activity feed!");
						
						this.notifyCisActivity(node.getJid(), subscriber.getIdentifier(), VERB_CALENDAR_EVENT_SUBSCRIPTION, theEvent.getName(), calendar.getName());
						
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
					
					Boolean localSub = false;
					if(subscriber.equals(myId)){
						localSub = database.subscribeEvent(eventId, calendar.getCalendarId(), node.getJid(), subscriber.getBareJid());
						recommendedEvents.remove(eventId);
					}
					else
						localSub = true;
					
					if(localSub){
						SharedCalendarBean calendarBean = new SharedCalendarBean();

						calendarBean.setMethod(MethodType.SUBSCRIBE_TO_EVENT);
						calendarBean.setNodeId(node.getJid());
						calendarBean.setEventId(eventId);
						calendarBean.setSubscriberId(subscriber.getBareJid());
						calendarBean.setRequestor(subscriber.getBareJid());
						
						CalendarResultCallback callback = new CalendarResultCallback();
						this.sendRequestToCIS(targetCis, subscriber, calendarBean, callback);

						SharedCalendarResult requestResult = callback.getResult();
						if(requestResult != null) 	
							subscriptionOk = requestResult.isSubscribingResult();
						
						if(log.isDebugEnabled())
							log.debug("Received result: {}", subscriptionOk);
						
						if(subscriptionOk){
							if(log.isDebugEnabled())
								log.debug("Subscribed event: {}", eventId);
							theEvent = requestResult.getEvent();
							recentEvents.put(eventId, theEvent);
							
						} else{
							if(log.isDebugEnabled())
								log.debug("There was a problem subscribing the event!");
							database.unsubscribeEvent(eventId, subscriber.getBareJid());
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
	public boolean unsubscribeFromEvent(String eventId, IIdentity node,IIdentity subscriber) {
		
		boolean subscriptionOk = false;
		
		if(log.isDebugEnabled())
			log.debug("Trying to unsubscribe from an event: " + eventId );
		
		try{
			
			if(log.isDebugEnabled())
				log.debug("First we need to see if we're subscribed!");
							
			if(!node.getType().equals(IdentityType.CIS)){
				
				if(log.isDebugEnabled())
					log.debug("It's not a CIS, so we're trying to unsubscribe to an event in a private calendar... we can only do that if it's OUR calendar");
				
				if(node.equals(subscriber)){
					if(log.isDebugEnabled())
						log.debug("We're automatically subscribed to our own events...");
					subscriptionOk = true;
				} else{
					if(log.isDebugEnabled())
						log.debug("For now we can't subscribe to an event that isn't ours");
				}
				
			} else{
				
				Calendar calendar = retrieveCalendar(node,myId);
				
				if(calendar== null){
					if(log.isDebugEnabled())
						log.debug("Couldn't find calendar, so can't unsubscribe!");
					return false;
				}
				
				String calendarId = calendar.getCalendarId();
				
				if(log.isDebugEnabled())
					log.debug("It's a CIS, let's determine if it is ours or not!");
				
				ICisOwned cisOwned = getCisManager().getOwnedCis(node.getJid());

				if(cisOwned != null){
					if(log.isDebugEnabled())
						log.debug("It's our own CIS, so we'll do the unsubscription for " + subscriber.getBareJid());
					
					subscriptionOk = getGoogleUtil().unsubscribeEvent(calendarId, eventId, subscriber);

					if(subscriptionOk){
						if(log.isDebugEnabled())
							log.debug("We unsubscribed to the event on Google, now we create the internal stuff!");
						if(subscriber.equals(myId))
							database.unsubscribeEvent(eventId, subscriber.getBareJid());
						
						Event theEvent = getGoogleUtil().getEvent(eventId, calendarId);
						recentEvents.put(eventId, theEvent);
						
						if(log.isDebugEnabled())
							log.debug("Publishing event in pubsub!");
						
						CalendarMessage message = new CalendarMessage();
						message.setMessage(Message.REMOVED_ATTENDEE);
						message.setRequestorId(subscriber.getBareJid());
						message.setCalendarId(calendarId);
						message.setEvent(theEvent);
						String messageId = message.getMessage()+"_"+ theEvent.getEventId()+"_"+subscriber.getBareJid();
						
						getPubSub().publisherPublish(node, calendarId.replace('@', '-'), messageId, message);
						
						if(log.isDebugEnabled())
							log.debug("Publishing event in activity feed!");
						
						this.notifyCisActivity(node.getJid(), subscriber.getIdentifier(), VERB_CALENDAR_EVENT_UNSUBSCRIPTION, theEvent.getName(), calendar.getName());
						
					} else{
						
						if(log.isDebugEnabled())
							log.debug("Couldn't unsubscribe to event!");
						
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
					
					boolean localSub;
					if(subscriber.equals(myId))
						localSub = database.unsubscribeEvent(eventId, subscriber.getBareJid());
					else
						localSub = true;
					
					if(localSub){
						SharedCalendarBean calendarBean = new SharedCalendarBean();

						calendarBean.setMethod(MethodType.UNSUBSCRIBE_FROM_EVENT);
						calendarBean.setNodeId(node.getJid());
						calendarBean.setEventId(eventId);
						calendarBean.setSubscriberId(subscriber.getBareJid());
						calendarBean.setRequestor(subscriber.getBareJid());
						
						CalendarResultCallback callback = new CalendarResultCallback();
						this.sendRequestToCIS(targetCis, subscriber, calendarBean, callback);
						SharedCalendarResult requestResult = callback.getResult();

						if(requestResult != null) 	
							subscriptionOk = requestResult.isSubscribingResult();
						
						log.debug("Received result: {}",subscriptionOk);
						
						if(subscriptionOk){
							log.debug("unSubscribed event: {}",eventId);
							recentEvents.put(eventId, requestResult.getEvent());
						} else{
							if(log.isDebugEnabled())
								log.debug("There was a problem unsubscribing the event!");
						}
					} else{
						if(log.isDebugEnabled())
							log.debug("There was a problem unsubscribing the event!");
					}

				}
				
			}
			
		} catch(Exception ex){
			ex.printStackTrace();
			log.error("Exception while unsubscribing from calendar: " + ex);
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
			if(subscribedEvent != null)
				finalEventList.add(subscribedEvent);
			else{
				log.debug("Couldn't find event {}!", eventDAO.getEventId());
			}
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
			if(calendarId != null){
				result = getGoogleUtil().getCalendar(calendarId);
			} else{
				result = getGoogleUtil().getCalendarForNode(nodeId);
				if(result != null)
					database.createCalendar(nodeId, result.getCalendarId(), result.getName());
			}
			
		} catch(Exception ex){
			log.error("Exception getting calendar");
			ex.printStackTrace();
		}

		return result;
	}

	
	private Event eventFromDatabaseEvent(EventDAO databaseEvent){
		
		Event result = recentEvents.get(databaseEvent.getEventId());
		
		if(result != null){
			log.debug("Found event in local cache, returning: {}", result.getName());
			return result;
		} else{
			try{
			IIdentity eventNode = getIIdentityFromJid(databaseEvent.getNodeId());
			
			result = retrieveEvent(databaseEvent.getEventId(),eventNode,myId);
			} catch(Exception ex){
				log.error("Exception getting eventNode Identity");
				ex.printStackTrace();
			}
		}
		
		return result;
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
			

			log.debug("Sending message to remote calendar at {} : {}", target.getJid(), calendarBean.getMethod());
			
			commManager.sendIQGet(stanza, calendarBean, calendarCallback);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error("ERROR: {}",e.getStackTrace()[0].getMethodName());
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
	
	private void newUserWarning(String title, String detail){
		
		log.debug("New User Warning: {} : {}", title, detail);
		UserWarning newWarning = new UserWarning(title, detail);
		userWarnings.add(newWarning);
		
	}
	
	private String getCssName(String cssId){
		String cssName = cssId;
		
		log.debug("Trying to get CSS Name for {}",cssId);
		
		return cssName;
	}
		
	protected void processRecommendedEvents(Calendar cisCalendar){
		
		RecommendedEventProcessor processor = new RecommendedEventProcessor(this, cisCalendar, null, null, RecommendedEventProcessor.Task.CALENDAR);
		executor.execute(processor);
		
	}
	
	protected void processNewEvent(Event newEvent){
		
		RecommendedEventProcessor processor = new RecommendedEventProcessor(this, null, newEvent, null, RecommendedEventProcessor.Task.EVENT);
		executor.execute(processor);
		
	}
	
	protected void processEvents(List<Event> eventList){
		
		RecommendedEventProcessor processor = new RecommendedEventProcessor(this, null, null, eventList, RecommendedEventProcessor.Task.EVENT);
		executor.execute(processor);
		
	}
	
	@Override
	public void handleInternalEvent(InternalEvent event) {
		log.debug("Received Internal event: {} : {}",event.geteventName(),event.geteventType());
		
		if(event.geteventType().equals(EventTypes.SERVICE_LIFECYCLE_EVENT)){
			ServiceMgmtEvent serviceEvent = (ServiceMgmtEvent) event.geteventInfo();
			if(serviceEvent.getEventType().equals(ServiceMgmtEventType.SERVICE_STARTED)){
				try{

					if(serviceEvent.getBundleSymbolName().equals("org.societies.thirdparty.sharedcalendar.sharedCalendar")){
						log.debug("Our service has started, we need to do some init stuff!");
						this.mySRI = serviceEvent.getServiceId();
						getContext().init();
						executor.execute(cleaner);
						return;
					}
					log.debug("Service {} is not our service, so we ignore the event!",serviceEvent.getServiceName());
				} catch(Exception ex){
					log.warn("Exception processing new event!: {}",ex);
				}
			}
		}
		
		if(event.geteventType().equals(EventTypes.CIS_CREATION) || event.geteventType().equals(EventTypes.CIS_RESTORE)){
			if(log.isDebugEnabled())
				log.debug("A new CIS was created or restored, we might need to create a  calendar for it");
			
			try{
				Community newCommunity = (Community) event.geteventInfo();
				IIdentity cisNode = getIIdentityFromJid(newCommunity.getCommunityJid());
				
				Calendar cisCalendar = retrieveCalendar(cisNode,myId);
				if(cisCalendar != null){
					log.debug("{} has a Calendar",newCommunity);
					
					try{
						List<String> myNode = getPubSub().discoItems(cisNode, null);
						if(!myNode.contains(cisCalendar.getCalendarId().replace('@', '-'))){
							if(log.isDebugEnabled())
								log.debug("Creating pub-sub node!");
							getPubSub().ownerCreate(cisNode, cisCalendar.getCalendarId().replace('@', '-'));
						} else{
							if(log.isDebugEnabled())
								log.debug("PubSub node already Created!");
						}
						getPubSub().subscriberSubscribe(cisNode, cisCalendar.getCalendarId().replace('@', '-'), this);
						
					} catch(Exception ex){
						log.error("Error creating pubsub node!");
						ex.printStackTrace();
					}


				} else{
					log.debug("We must create a new calendar for this CIS");
					this.createCalendar(newCommunity.getCommunityName(), cisNode);
				}

			} catch(Exception ex){
				log.error("Exception while processing CIS_CREATION: " + ex);
				ex.printStackTrace();
			}
		}
				
		if(event.geteventType().equals(EventTypes.CIS_DELETION)){
			if(log.isDebugEnabled())
				log.debug("A CIS was deleted, we need to remove the calendar and the events associated with it.");
			try{
				Community newCommunity = (Community) event.geteventInfo();
				IIdentity cisIdentity = getIIdentityFromJid(newCommunity.getCommunityJid());
				this.deleteCalendar(cisIdentity);
				
			} catch(Exception ex){
				log.error("Exception while processing CIS_DELETION: " + ex);
				ex.printStackTrace();
			}
		}
		
		if(event.geteventType().equals(EventTypes.CIS_UNSUBS)){
			if(log.isDebugEnabled())
				log.debug("We unsubscribed from a CIS, so we should unsubscribe from the events in it.");
			
			try{
				Community newCommunity = (Community) event.geteventInfo();
				IIdentity cisIdentity = getIIdentityFromJid(newCommunity.getCommunityJid());
				this.deleteCalendar(cisIdentity);
				
				if(log.isDebugEnabled())
					log.debug("Remove events from that CIS from the Recommended Events List");
				
			} catch(Exception ex){
				log.error("Exception while processing CIS_UNSUBS: " + ex);
				ex.printStackTrace();
			}
		}

		if(event.geteventType().equals(EventTypes.CIS_SUBS)){
			if(log.isDebugEnabled())
				log.debug("We subscribed from a CIS, so we should try to get events from it.");
			
			try{
				Community newCommunity = (Community) event.geteventInfo();
				IIdentity cisNode = getIIdentityFromJid(newCommunity.getCommunityJid());
				Calendar newCalendar = this.retrieveCalendar(cisNode, myId);
				if(newCalendar == null){
					if(log.isDebugEnabled())
						log.debug("CIS does not have a calendar, so we do nothing.");
					return;
				} else{
					if(log.isDebugEnabled())
						log.debug("Got the Calendar for that CIS!");
					processRecommendedEvents(newCalendar);
				}				
				
			} catch(Exception ex){
				log.error("Exception while processing CIS_SUBS: " + ex);
				ex.printStackTrace();
			}
		}
	}

	@Override
	public void handleExternalEvent(CSSEvent event) {
		if(log.isDebugEnabled())
			log.debug("Received external event!");
	}

	public ServiceResourceIdentifier getServiceIdentifier() {

		if(mySRI == null)
			mySRI = getServiceMgmt().getMyServiceId(getClass());
		
		return mySRI;
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
	
	protected IIdentity getIIdentityFromJid(String jid) throws InvalidFormatException{		
		return getCommManager().getIdManager().fromJid(jid);
	}
	
	
	protected class CalendarDatabase {
		
		final Logger log = LoggerFactory.getLogger(CalendarDatabase.class);

		public CalendarDatabase(){
			if(log.isDebugEnabled())
				log.debug("CalendarDatabase sub-class started");
		}
		
		protected boolean deleteEvent(String eventId){
			if(log.isDebugEnabled())
				log.debug("Deleting an event from the database!");

			Transaction t = null;
			Session session = null;
			boolean result = false;

			try {
				session = sessionFactory.openSession();
				List<EventDAO> results = (List<EventDAO>) session.createCriteria(EventDAO.class)
						.add(Restrictions.like("eventId", eventId)).list();
				if (results.size() >= 1) {
					t = session.beginTransaction();
					for(EventDAO d : results){
						session.delete(d);
					}
					t.commit();
					
					result = true;		
				} else {
					if(log.isDebugEnabled())
						log.debug("The eventId has not been found.");
					return true;
				}
			} catch (HibernateException he) {
				log.error("Hibernate Exception: {}",he.getMessage());
				if (t != null) {
					t.rollback();
				}
			} catch (Exception e) {
				log.error("Database Exception: {}",e.getMessage());
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
		
		protected boolean deleteEventsForCalendarId(String calendarId){

			if(log.isDebugEnabled())
				log.debug("Deleting events for: " + calendarId);
			
			boolean result = false;
			Transaction t = null;
			Session session = null;
			try {
				session = sessionFactory.openSession();

				List<EventDAO> results = (List<EventDAO>) session
						.createCriteria(EventDAO.class)
						.add(Restrictions.like("calendarId", calendarId)).list();
				if (results.size() >= 1) {
					t = session.beginTransaction();
					for(EventDAO d : results){
						session.delete(d);
					}
					t.commit();
					
					result = true;		
				} else {
					if(log.isDebugEnabled())
						log.debug("The events for calendar id have not been found.");
				}
				if(log.isDebugEnabled())
					log.debug("Found " + results.size() + " ");
			} catch (HibernateException he) {
				log.error("Hibernate Exception: {}",he.getMessage());

			} catch (Exception e) {
				log.error("Database Exception: {}",e.getMessage());
				if (t != null) {
					t.rollback();
				}
			}finally {
				if (session != null) {
					session.close();
				}
			}	
			return result;
		}
		
		protected boolean subscribeEvent(String eventId, String calendarId, String nodeId, String subscriberId){
			
			if(log.isDebugEnabled())
				log.debug("Creating an Event subscription;");
			
			Transaction t = null;
			Session session = null;
			boolean result = false;

			try {
				session = sessionFactory.openSession();

				EventDAO eventDAO = new EventDAO(eventId, calendarId, nodeId, subscriberId);

				t = session.beginTransaction();
				session.save(eventDAO);
				t.commit();
				result = true;
					
			} catch (HibernateException he) {
				log.error("Hibernate Exception: {}",he.getMessage());
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
				log.error("Hibernate Exception: {}",he.getMessage());
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
		
		protected EventDAO getEventSubscription(String eventId, String subscriberId) {
			
			if(log.isDebugEnabled())
				log.debug("Checking if user is subscribed!");
			
			Transaction t = null;
			Session session = null;
			EventDAO result = null;

			try {
				session = sessionFactory.openSession();
				
				Criteria c = session.createCriteria(EventDAO.class).add(Restrictions.like("eventId", eventId));
				c.add(Restrictions.like("subscriberId", subscriberId));
				
				List<EventDAO> results = c.list();
				
				if (results.size() == 1) {					
					result = results.get(0);		
				} else {
					if(log.isDebugEnabled())
						log.debug("The event has not been found.");
				}
			} catch (HibernateException he) {
				log.error("Hibernate Exception: {}",he.getMessage());
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
				session = sessionFactory.openSession();

				t = session.beginTransaction();
				session.save(cisCalendarDAO);
				t.commit();
				result = true;
					
			} catch (HibernateException he) {
				log.error("Hibernate Exception: {}",he.getMessage());
				if (t != null) {
					t.rollback();
					try {
						getGoogleUtil().deleteCalendar(calendarId);
					} catch (IOException e) {
						log.error("Google Exception: {}",e.getMessage());
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
						.createCriteria(EventDAO.class)
						.add(Restrictions.like("subscriberId", subscriberId)).list();
				
				if(log.isDebugEnabled())
					log.debug("Found " + results.size() + " ");
			} catch (HibernateException he) {
				log.error("Hibernate Exception: {}",he.getMessage());

			} finally {
				if (session != null) {
					session.close();
				}
			}	
			return results;
		}
		
		protected EventDAO getEventDAOByEventId(String eventId){

			if(log.isDebugEnabled())
				log.debug("Getting events for: " + eventId);
			
			EventDAO result = null;
			Transaction t = null;
			Session session = null;
			try {
				session = sessionFactory.openSession();

				List<EventDAO> results = (List<EventDAO>) session
						.createCriteria(EventDAO.class)
						.add(Restrictions.like("eventId", eventId)).list();
				
				if (results.size() == 1) {					
					t = session.beginTransaction();
					result = results.get(0);
					t.commit();
				} else {
					log.info("The event has not been found.");
				}
				
				if(log.isDebugEnabled())
					log.debug("Found " + results.size() + " ");
			} catch (HibernateException he) {
				log.error("Hibernate Exception: {}",he.getMessage());

			} finally {
				if (session != null) {
					session.close();
				}
			}	
			return result;
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
						.createCriteria(EventDAO.class)
						.add(Restrictions.like("calendarId", calendarId)).list();
				
				if(log.isDebugEnabled())
					log.debug("Found " + results.size() + " ");
			} catch (HibernateException he) {
				log.error("Hibernate Exception: {}",he.getMessage());

			} finally {
				if (session != null) {
					session.close();
				}
			}	
			return results;
		}
		
		
		protected boolean deleteCalendar(String calendarId) {
			
			log.debug("Database: Deleting a calendar with calendarId: {}", calendarId);
			
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
					
					result = deleteEventsForCalendarId(calendarId);	
				} else {
					if(log.isDebugEnabled())
						log.debug("The calendarId has not been found.");
				}
			} catch (HibernateException he) {
				log.error("Hibernate Exception: {}",he.getMessage());
				if (t != null) {
					t.rollback();
				}
			} catch (Exception e) {
				log.error("Database Exception: {}",e.getMessage());
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
				log.error("Hibernate Exception: {}",he.getMessage());
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
				log.error("Hibernate Exception: {}",he.getMessage());
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
				log.debug("Getting CalendarId from NodeId {}",nodeId);
			
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
				log.error("Hibernate Exception: {}",he.getMessage());
				if (t != null) {
					t.rollback();
				}
			} finally {
				if (session != null) {
					session.close();
				}
			}
			log.debug("Calendar id {} for node Id{}",result,nodeId);
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
				log.error("Hibernate Exception: {}",he.getMessage());
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
		
		protected List<CalendarDAO> getAllCalendars(){

			
			List<CalendarDAO> results = null;
			Transaction t = null;
			Session session = null;
			try {
				session = sessionFactory.openSession();

				results = (List<CalendarDAO>) session
						.createCriteria(CalendarDAO.class).list();
				
				if(log.isDebugEnabled())
					log.debug("Found " + results.size() + " ");
			} catch (HibernateException he) {
				log.error("Hibernate Exception: {}",he.getMessage());

			} finally {
				if (session != null) {
					session.close();
				}
			}	
			return results;
		}
	}


	@Override
	public void pubsubEvent(IIdentity node, String calendarId,
			String itemId, Object item) {
		if(log.isDebugEnabled())
			log.debug("Received a Pub-Sub event, from node " + node.getJid() + " for calendar: " + calendarId);
		
		if(item.getClass().equals(CalendarMessage.class)){
			CalendarMessage calendarMessage = (CalendarMessage) item;
			
			log.debug("Message was: {}", calendarMessage.getMessage());
			
			if(calendarMessage.getMessage().equals(Message.NEW_EVENT)){
				Event newEvent = calendarMessage.getEvent();
		
				log.debug("A new event arrived: {}", newEvent.getName());
				
				processNewEvent(newEvent);
			}
					
			if(calendarMessage.getMessage().equals(Message.UPDATED_EVENT)){
				
				Event updatedEvent = calendarMessage.getEvent();
				
				log.debug("Updated Event: {}", updatedEvent.getName());
				
				// First we need to see if we are subscribed to the event, if we are we should tell
				// the user the event has changed!
				try{
					IIdentity requestor = getIIdentityFromJid(calendarMessage.getRequestorId());

					// Don't need to be alerted if I'm the creator => CHECK LATER IF THIS ALTERS
					if(!requestor.equals(myId)){
						
						// Are we subscribed?
						List<String> attendees = updatedEvent.getAttendees();
						if(attendees != null && attendees.contains(myId.getBareJid())){
							if(log.isDebugEnabled())
								log.debug("We are subscribed to the event, so we need to alert the user!");
							// Sending message to user
							newUserWarning("Event Changed!",updatedEvent.getName() + " was changed by " + getCssName(requestor.getBareJid()));

						}
						
						//Next up, we should check to see if the event is going to be recommended!
						processNewEvent(updatedEvent);

					} else{
						if(log.isDebugEnabled())
							log.debug("Don't need to process or alert if WE changed the event...");
					}
					
				} catch(Exception ex){
					log.error("Exception occurred while processing updated event from PubSub: {}",ex);
					ex.printStackTrace();
				}
				
				
			}
			
			if(calendarMessage.getMessage().equals(Message.DELETED_EVENT)){
				Event deletedEvent = calendarMessage.getEvent();
				
				log.debug("Event was deleted: {}" + deletedEvent.getName());
				
				// First we check if the event was in the recommended events, and we remove it if so
				if(recommendedEvents.get(deletedEvent.getEventId()) != null){
					log.debug("Event was in the recommended Events, so we need to remove it!");
					
					recommendedEvents.remove(deletedEvent.getEventId());
				}
				
				// Oh, and we do the same for the recent Events
				// First we check if the event was in the recent events, and we remove it if so
				if(recentEvents.get(deletedEvent.getEventId()) != null){
					
					log.debug("Event was in the recentEvents , so we need to remove it!");
					
					recentEvents.remove(deletedEvent.getEventId());
				}
				
				// Next we check to see if we are already subscribed to that event, if so we need to
				if(log.isDebugEnabled())
					log.debug("Checking to see if we were subscribed!");
				List<String> attendees = deletedEvent.getAttendees();
				if(attendees != null && attendees.contains(myId.getBareJid())){
					if(log.isDebugEnabled())
						log.debug("We were already subscribed to this event, so we should notify the user!");
					
					newUserWarning("Event Deleted!", deletedEvent.getName() + " was deleted by " + getCssName(calendarMessage.getRequestorId()));
					
					if(log.isDebugEnabled())
						log.debug("Removing our subscription to that event!");
					
					database.deleteEvent(deletedEvent.getEventId());
				}
				
			}
			
			if(calendarMessage.getMessage().equals(Message.NEW_ATTENDEE)){
				String attendeeId = calendarMessage.getRequestorId();
				Event event = calendarMessage.getEvent();
				
				if(log.isDebugEnabled())
					log.debug("There is a new attendee to event: " + event + " : " + attendeeId);
				
				if(recentEvents.get(event.getEventId()) != null){
					if(log.isDebugEnabled())
						log.debug("Event was in the recentEvents , so we need to update it!");
					
					recentEvents.put(event.getEventId(), event);
				}
				
				try {
					IIdentity newAttendee = getIIdentityFromJid(attendeeId);
	
					if(getContext().getMyFriends().contains(newAttendee)){
						if(log.isDebugEnabled())
							log.debug("Attendee is a friend of ours, so we should process the event to see if it's recommended!");
							newUserWarning("Friend joined event!", getCssName(attendeeId) + " has joined the event " + event.getName());
							
							processNewEvent(event);
					} else{
						if(log.isDebugEnabled())
							log.debug("Attendee isn't a friend of ours, so we do nothing!");
					}
				
				} catch (InvalidFormatException e) {
					log.error("Error getting IIdentity from attendee JID: {}",e.getMessage());
					e.printStackTrace();
				}				
				
			}
			
			if(calendarMessage.getMessage().equals(Message.REMOVED_ATTENDEE)){
				String attendeeId =  calendarMessage.getRequestorId();
				Event event = calendarMessage.getEvent();
				
				if(recentEvents.get(event.getEventId()) != null){
					if(log.isDebugEnabled())
						log.debug("Event was in the recentEvents , so we need to update it!");
					
					recentEvents.put(event.getEventId(), event);
				}
				
				if(log.isDebugEnabled())
					log.debug("An attendee left the event " + event + " : " + attendeeId);
				
				try {
					IIdentity newAttendee = getIIdentityFromJid(attendeeId);
	
					if(getContext().getMyFriends().contains(newAttendee)){
						if(log.isDebugEnabled())
							log.debug("Attendee is a friend of ours, so we should process the event to see if it's (still) recommended!");
							newUserWarning("Friend left event!", getCssName(attendeeId) + " has left the event " + event.getName());

							processNewEvent(event);
					} else{
						if(log.isDebugEnabled())
							log.debug("Attendee isn't a friend of ours, so we do nothing!");
					}
				
				} catch (InvalidFormatException e) {
					log.error("Error getting IIdentity from attendee JID: {}", e.getMessage());
					e.printStackTrace();
				}
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
		
		Collection<Event> eventCol = recommendedEvents.values();
		List<Event> resultEvents = new ArrayList<Event>();
		Date now = new Date();
				
		for(Event recomEvent: eventCol){
			if(log.isDebugEnabled())
				log.debug("Recommended Event: " + recomEvent.getName() );
			
			Date endDate = recomEvent.getEndDate();
			
			if(endDate.before(now)){
				if(log.isDebugEnabled())
					log.debug("The event has already passed, so we remove it from the recommended events!");
				
				recommendedEvents.remove(recomEvent.getEventId());
				recentEvents.remove(recomEvent.getEventId());
			} else{
				if(log.isDebugEnabled())
					log.debug("Adding recommended Event");
				resultEvents.add(recomEvent);
				
			}	
		}
		
		return resultEvents;
	}


	@Override
	public List<Event> findEventsInCalendar(IIdentity node, Event searchEvent, IIdentity requestor) {
		if(log.isDebugEnabled())
			log.debug("Searching for events in calendar for node: " + node.getJid());
		
		List<Event> eventList = new ArrayList<Event>();
		
		//Quicker way, for the trials
		Calendar searchCalendar = retrieveCalendar(node,requestor);
		if(searchCalendar == null)
			return eventList;
		
		eventList = googleUtil.searchForEvent(searchCalendar.getCalendarId(), searchEvent);
		
		for(Event recentEvent: eventList){
			recentEvents.put(recentEvent.getEventId(), recentEvent);
		}
		
		return eventList;
		
		/*
		try{
			
			if(node.getType().equals(IdentityType.CIS)){
				
				if(log.isDebugEnabled())
					log.debug("It's a CIS, let's determine if it is ours or not!");
				
				ICisOwned cisOwned = getCisManager().getOwnedCis(node.getJid());
				if(cisOwned == null){
					if(log.isDebugEnabled())
						log.debug("It's not our own CIS, so we should make sure it has a calendar, and then send it to that CIS");
	
					Calendar cisCalendar = retrieveCalendar(node,myId);
					if(cisCalendar != null){
						ICis targetCis = getCisManager().getCis(node.getJid());
						
						if(targetCis == null){
							if(log.isDebugEnabled())
								log.debug("We couldn't find the Cis!");
								return null;
						}
						
						SharedCalendarBean calendarBean = new SharedCalendarBean();
	
						calendarBean.setMethod(MethodType.FIND_EVENTS);
						calendarBean.setNodeId(node.getJid());
						calendarBean.setRequestor(requestor.getJid());
						calendarBean.setEvent(searchEvent);
						
						CalendarResultCallback callback = new CalendarResultCallback();
						this.sendRequestToCIS(targetCis, requestor, calendarBean, callback);
						SharedCalendarResult beanResult = callback.getResult();
						if(beanResult != null) 
							eventList = beanResult.getEventList();
					
						log.debug("Received result: {}", eventList.size());
						
						return eventList;
					} else{
						log.debug("That CIS does not have a calendar, so it has no events!");
					}
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
			
			eventList = getGoogleUtil().searchForEvent(calendarId, searchEvent);
			
			if(log.isDebugEnabled()){
				log.debug("We tried to search events for the calendar: " + calendarId);
				log.debug("Number of events: " + eventList.size());
				for(Event event : eventList){
					log.debug("Event: " + event.getName() + " : " + event.getDescription() + " : " + event.getStartDate() + " : " + event.getEndDate());
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Finding Events in Calendar Exception: {}",e.getMessage());
		}
		
		return eventList;
		*/
	}


	@Override
	public List<Event> findEventsAll(Event searchEvent, IIdentity requestor) {
		if(log.isDebugEnabled())
			log.debug("Searching for events in ALL Calendars");
		
		List<Event> eventList = new ArrayList<Event>();
		List<ICis> cisList = getCisManager().getCisList();
		List<CalendarAsyncTask> taskList = new ArrayList<CalendarAsyncTask>(cisList.size()+1);
				
		for(ICis cis : cisList){
			
			log.debug("Searching for Calendar in CIS: " + cis.getCisId());
			try{
				IIdentity cisId = getIIdentityFromJid(cis.getCisId());
				CalendarAsyncTask cisTask = new CalendarAsyncTask(this, searchEvent, cisId, requestor ,Task.SEARCH_CALENDAR);
				taskList.add(cisTask);
				executor.execute(cisTask);
			 } catch(Exception ex){
				 log.error("Exception trying to search in a CIS: {}", ex.getMessage());
				 ex.printStackTrace();
			 }
		}
		 
		try{
			log.debug("Searching in local calendar: {}", myId);
			CalendarAsyncTask localTask = new CalendarAsyncTask(this, searchEvent, myId, requestor, Task.SEARCH_CALENDAR);
			taskList.add(localTask);
			executor.execute(localTask);
		} catch(Exception ex){
			log.error("Exception trying to get local events: {}", ex.getMessage());
			ex.printStackTrace();
		}
		 		
		for(CalendarAsyncTask task : taskList){
			log.debug("Trying to get Async Search Result for Node {}",task.getNode());
			eventList.addAll(task.getEventResult());
		}
		
		return eventList;
	}

	@Override
	public List<UserWarning> getUserWarnings(){
		
		// Create the result List
		List<UserWarning> result = new ArrayList<UserWarning>(userWarnings);
		log.debug("There are {} user warnings!",result.size());
		// Clears the current warnings
		userWarnings.clear();
		log.debug("There are {} user warnings!",result.size());
		// Returns result
		return result;
	}
	

	
	public List<Calendar> getAllCalendars(){

		log.debug("Retrieving all Calendars...");
		return new ArrayList<Calendar>(recentCalendars.values());
		
	}
	
}
