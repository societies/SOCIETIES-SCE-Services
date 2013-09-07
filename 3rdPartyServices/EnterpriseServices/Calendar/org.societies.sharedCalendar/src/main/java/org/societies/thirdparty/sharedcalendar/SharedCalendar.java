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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Future;

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
import org.societies.api.css.devicemgmt.IDevice;
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
import org.societies.api.identity.RequestorService;
import org.societies.api.osgi.event.CSSEvent;
import org.societies.api.osgi.event.CSSEventConstants;
import org.societies.api.osgi.event.EventListener;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.personalisation.mgmt.IPersonalisationManager;
import org.societies.api.personalisation.model.Action;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.personalisation.model.IActionConsumer;
import org.societies.api.personalisation.model.PersonalisablePreferenceIdentifier;
import org.societies.api.schema.activityfeed.MarshaledActivityFeed;
import org.societies.api.schema.cis.community.Community;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.services.IServices;
import org.societies.api.useragent.monitoring.IUserActionMonitor;
import org.societies.thirdparty.sharedcalendar.api.CalendarPreference;
import org.societies.thirdparty.sharedcalendar.api.UserWarning;
import org.societies.thirdparty.sharedcalendar.api.CalendarConverter;
import org.societies.thirdparty.sharedcalendar.commsServer.SharedCalendarCallBack;
import org.societies.thirdparty.sharedcalendar.persistence.CalendarDAO;
import org.societies.thirdparty.sharedcalendar.persistence.EventDAO;

/**
 * Back-end class that implements the calendar management logic.
 * 
 * @author <a href="mailto:sanchocsa@gmail.com">Sancho Rêgo</a> (PTIN)
 * 
 */
public class SharedCalendar extends EventListener implements ISharedCalendarServer, IActionConsumer, Subscriber {

	static final Logger log = LoggerFactory.getLogger(SharedCalendar.class);

	private CalendarGoogleUtil googleUtil;
	private SessionFactory sessionFactory;
	private IEventMgr evtMgr;

	private IServices serviceMgmt;
	private ICisManager cisManager;
	private ICommManager commManager;
	private IIdentity myId;
	private IPersonalisationManager personalisation;
	private ICtxBroker ctxBroker;
	private IUserActionMonitor userAction;
	private PubsubClient pubSub;
	
	private List<UserWarning> userWarnings;
	private HashMap<String,Event> recommendedEvents;
	private HashMap<String,Event> recentEvents;
	private HashMap<String,Calendar> recentCalendars;
	
	private String myCalendarId;
	private CalendarDatabase database;
	private CalendarContextUtils context;

	private RequestorService myrequestor;

	private ServiceResourceIdentifier mySRI;

	private String serviceType;
	
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
		mySRI = null;
		myrequestor = null;
		serviceType = null;
		database = new CalendarDatabase();
		context = new CalendarContextUtils(ctxBroker, myId, commManager);

		recommendedEvents = null;
		recentCalendars =  new HashMap<String,Calendar>();
		recentEvents = new HashMap<String,Event>();
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
		
		//getGoogleUtil().cleanCalendar(myCalendarId);
		
		recentCalendars.put(myId.getBareJid(), myCalendar);
		
		// Now checks the CIS list and creates CIS 
		List<ICisOwned> ourCisList = getCisManager().getListOfOwnedCis();
		for(ICisOwned ourCis : ourCisList ){
			if(log.isDebugEnabled())
				log.debug("Checking " + ourCis.getName() + " for a Calendar");
			Calendar cisCalendar = getMyCalendarFromNodeId(ourCis.getCisId());
			if(cisCalendar != null){
				if(log.isDebugEnabled())
					log.debug(ourCis.getName() + " has a Calendar");
				recentCalendars.put(ourCis.getCisId(), cisCalendar);
				try{
					IIdentity cisNode = getIIdentityFromJid(ourCis.getCisId());
					List<String> myNode = getPubSub().discoItems(cisNode, cisCalendar.getCalendarId().replace('@', '-'));
					if(myNode.isEmpty()){
					//if(true){
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
				//getGoogleUtil().cleanCalendar(cisCalendar.getCalendarId());

			} else{
				if(log.isDebugEnabled())
					log.debug(ourCis.getName() + " doesn't have a Calendar, creating.");
				IIdentity cisId = getCommManager().getIdManager().fromJid(ourCis.getCisId());
				String calendarId = createCalendar(ourCis.getName(),cisId);
				if(calendarId != null)
					if(log.isDebugEnabled())
						log.debug("Created calendar for " + ourCis.getName());
				
				//getGoogleUtil().cleanCalendar(calendarId);
			}
		}
		
		if(log.isDebugEnabled())
			log.debug("Now getting the list of all subscribed CIS!");
		List<ICis> otherCisList = getCisManager().getRemoteCis();
		for(ICis otherCis : otherCisList){
			if(log.isDebugEnabled())
				log.debug("We are subscribed to CIS: " + otherCis.getName() + ". Does it have a calendar?");
				IIdentity otherCisId = getIIdentityFromJid(otherCis.getCisId());
				Calendar cisCalendar = retrieveCalendar(otherCisId,myId);
				if(cisCalendar != null){
					if(log.isDebugEnabled())
						log.debug("The CIS has a calendar, so we register for its events!");
					recentCalendars.put(otherCis.getCisId(), cisCalendar);
					getPubSub().subscriberSubscribe(otherCisId, cisCalendar.getCalendarId().replace('@', '-'), this);
					
				} else{
					if(log.isDebugEnabled())
						log.debug("CIS does not have a calendar!");
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
		getEvtMgr().subscribeInternalEvent(this, eventTypes, null);
		
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
						if(log.isDebugEnabled())
							log.debug("Deleted a calendar!");
						
						if(log.isDebugEnabled())
							log.debug("Now trying to delete it from Google...");
						
						getGoogleUtil().deleteCalendar(calendarId);
						
						if(log.isDebugEnabled())
							log.debug("Publishing it on PubSub");
						
						CalendarMessage message = new CalendarMessage();
						message.setMessage(Message.CALENDAR_DELETED);
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
					
					returnedCalendar = callback.getResult().getCalendar();
					
					log.debug("Received result: {}",returnedCalendar.getName());
					
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
		
		if(returnedCalendar != null){
			recentCalendars.put(node.getBareJid(), returnedCalendar);
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
					
					if(log.isDebugEnabled())
						log.debug("Received result: [}", returnedEvent.getName());
					
					}

					
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
			
			returnedEvent = getGoogleUtil().getEvent(eventId, calendarId);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
		
		if(returnedEvent != null)
			recentEvents.put(eventId, returnedEvent);
		
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
					log.debug("Event: " + event.getName() + " : " + event.getDescription() + " : " + event.getStartDate() + " : " + event.getEndDate());
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
				
				returnedEventId = getGoogleUtil().createEvent(myCalendarId, newEvent.getName(), newEvent.getDescription(),
						CalendarConverter.asDate(newEvent.getStartDate()), 
						CalendarConverter.asDate(newEvent.getEndDate()), newEvent.getLocation(), requestor.getBareJid(), node.getBareJid());

				if(returnedEventId != null){
					
					if(log.isDebugEnabled())
							log.debug("Created a new Google Event: " + returnedEventId);
					
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
							CalendarConverter.asDate(newEvent.getStartDate()), 
							CalendarConverter.asDate(newEvent.getEndDate()), newEvent.getLocation(), requestor.getBareJid(), node.getBareJid());
						
					if(returnedEventId != null){
						
						boolean subscribeOk = database.subscribeEvent(returnedEventId, calendarId, node.getJid(), requestor.getBareJid());
						
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
							
							String pubSubMessage = getPubSub().publisherPublish(node, calendarId, messageId, message);
							log.debug("pubSub: " + pubSubMessage);
							
							if(log.isDebugEnabled())
								log.debug("Publishing event in activity feed!");
							
							this.notifyCisActivity(node.getJid(), requestor.getIdentifier(), VERB_CIS_CALENDAR_EVENT_CREATED, newEvent.getName(), calendarId);

							processNewEvent(newEvent);
							
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
						
					returnedEventId = (String) callback.getResult().getEventId();
			
					if(returnedEventId != null){
						if(log.isDebugEnabled())
							log.debug("Event was correctly created in remote node, so now we need to register it in the database!");
						
						newEvent = callback.getResult().getEvent();
						boolean subscribeOk = database.subscribeEvent(returnedEventId, newEvent.getCalendarId(), node.getJid(), requestor.getBareJid());
						
						if(subscribeOk){
							recentEvents.put(returnedEventId, newEvent);
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
					
					recommendedEvents.remove(myEvent.getEventId());
					
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
						
					deletionOk = (Boolean) callback.getResult().isLastOperationSuccessful();
					if(deletionOk){
						deletionOk = database.deleteEvent(eventId);
					}
				}
			}
					
		} catch(Exception ex){
			ex.printStackTrace();
			log.error("Exception while deleting event: " + ex);
		}		

		if(deletionOk){
			recentEvents.remove(eventId);
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
						
					Event eventUpdate = callback.getResult().getEvent();
			
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
						log.debug("It's our own CIS, so we'll do the subscription for " + subscriber.getJid());
					
					subscriptionOk = getGoogleUtil().subscribeEvent(calendarId, eventId, subscriber);

					if(subscriptionOk){
						if(log.isDebugEnabled())
							log.debug("We subscribed to the event on Google, now we create the internal stuff!");
						database.subscribeEvent(eventId, calendarId, node.getJid(), subscriber.getBareJid());
						
						theEvent = getGoogleUtil().getEvent(eventId, calendarId);
						recentEvents.put(eventId, theEvent);
						
						if(log.isDebugEnabled())
							log.debug("Publishing event in pubsub!");
						
						CalendarMessage message = new CalendarMessage();
						message.setMessage(Message.NEW_ATTENDEE);
						message.setRequestorId(subscriber.getBareJid());
						message.setCalendarId(calendarId);
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
					
					
					Boolean localSub = database.subscribeEvent(eventId, calendar.getCalendarId(), node.getJid(), subscriber.getBareJid());

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
						subscriptionOk = requestResult.isSubscribingResult();
						
						if(log.isDebugEnabled())
							log.debug("Received result: " + subscriptionOk);
						
						if(subscriptionOk){
							if(log.isDebugEnabled())
								log.debug("Subscribed event: " + eventId);
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
		
			if(subscriptionOk && theEvent != null){
				if(log.isDebugEnabled())
					log.debug("We correctly subscribed to the event, so we should register the preferences.");
				
				setPreference(CalendarPreference.SUB_CREATOR,theEvent.getCreatorId());
				setPreference(CalendarPreference.SUB_CALENDARNODE,theEvent.getNodeId());
				if(theEvent.getLocation() != null && !theEvent.getLocation().isEmpty() )
					setPreference(CalendarPreference.SUB_LOCATION,theEvent.getLocation());
				if(theEvent.getAttendees() != null)
					setPreference(CalendarPreference.SUB_ATTENDEENUMBER,""+theEvent.getAttendees().size());
				
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
			
			if(log.isDebugEnabled())
				log.debug("First we need to see if we're subscribed!");
			
			String subscriberId = subscriber.getBareJid();
			EventDAO databaseEvent = database.getEventSubscription(eventId, subscriberId);
			
			if(databaseEvent == null){
				if(log.isDebugEnabled())
					log.debug("Couldn't find subscription in database! Aborting!");
				return false;
			}
			
			IIdentity node = getIIdentityFromJid(databaseEvent.getNodeId());
				
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
						database.unsubscribeEvent(eventId, subscriber.getBareJid());
						
						Event theEvent = getGoogleUtil().getEvent(eventId, calendarId);
						recentEvents.put(eventId, theEvent);
						
						if(log.isDebugEnabled())
							log.debug("Publishing event in pubsub!");
						
						CalendarMessage message = new CalendarMessage();
						message.setMessage(Message.REMOVED_ATTENDEE);
						message.setRequestorId(subscriber.getBareJid());
						message.setCalendarId(calendarId);
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
					
					Boolean localSub = database.unsubscribeEvent(eventId, subscriber.getBareJid());

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
						subscriptionOk = requestResult.isSubscribingResult();
						
						if(log.isDebugEnabled())
							log.debug("Received result: " + subscriptionOk);
						
						if(subscriptionOk){
							if(log.isDebugEnabled())
								log.debug("unSubscribed event: " + eventId);
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
		
		if(log.isDebugEnabled())
			log.debug("Getting an event from a Database event");
		
		if(log.isDebugEnabled())
			log.debug("First we search in our local cache!");

		Event result = recentEvents.get(databaseEvent.getEventId());
		
		if(result != null){
			if(log.isDebugEnabled())
				log.debug("Found event in local cache, returning: " + result.getName());
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

	private String getPreference(CalendarPreference preference){
		if(log.isDebugEnabled())
			log.debug("Getting Preference: " + preference);
		
		String result = null;
		
		try{
			
			if(myrequestor == null){
				myrequestor = new RequestorService(myId, getServiceIdentifier());
			}
			
			Future<IAction> actionAsync = getPersonalisation().getPreference(myrequestor, myId, getServiceType(), mySRI, preference.toString());
			IAction action =actionAsync.get();
			
			if(action != null){
				if(log.isDebugEnabled())
					log.debug("Preference retrieved: " + action.getparameterName() + " =>  " + action.getvalue());
				
				result = action.getvalue();
			} else{
				if(log.isDebugEnabled())
					log.debug("Preference was not retrieved");
			}
		} catch(Exception ex){
			log.error("There was an exception getting a Preference from the Personalisation Manager :" + ex);
			ex.printStackTrace();
		}
		
		return result;
	}
	
	private void setPreference(CalendarPreference preferenceName, String preferenceValue){
		if(log.isDebugEnabled())
			log.debug("Set a Preference ("+preferenceName+','+preferenceValue+')');
		
		IAction myAction = new Action(getServiceIdentifier(), getServiceType(), preferenceName.toString(), preferenceValue, false, true, false);
		getUserAction().monitor(myId, myAction);
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
	
	private void initRecommendedEvents(){
		log.debug("Init the recommendedEvents!");
		
		Iterator<Calendar> calendars = recentCalendars.values().iterator();
		while(calendars.hasNext())
			processRecommendedEvents(calendars.next());

	}
	
	private void processRecommendedEvents(Calendar cisCalendar){
		log.debug("Processing recommended Events for Calendar: {}", cisCalendar.getName());
		
		try{
			List<Event> events = retrieveEvents(getIIdentityFromJid(cisCalendar.getNodeId()),myId);
			for(Event event : events){
				processNewEvent(event);
			}
		} catch(Exception ex){
			log.error("Oh no!");
			ex.printStackTrace();
		}
		
	}
	
	private void processNewEvent(Event newEvent){
		if(log.isDebugEnabled())
			log.debug("We have a event to process and see if we should recommend it: " + newEvent.getName());
		
		int recommendPoints = 0;
		
		// DON'T RECOMMEND OUR OWN EVENTS!
		if(log.isDebugEnabled())
			log.debug("If we created the event, we don't recommend it to ourselves of course!");
		IIdentity creatorId = null;
		try{
			creatorId = getIIdentityFromJid(newEvent.getCreatorId());
			/*if(creatorId.equals(myId)){
				if(log.isDebugEnabled())
					log.debug("We created the event, no need to process it!");
				return;
			}*/
		} catch(Exception ex){
			log.error("Didn't manage to compare to creatorId! Exception: " + ex);
			ex.printStackTrace();
		}
		
		// DON'T RECOMMEND EVENTS THAT HAVE ENDED!
		Date endDate = CalendarConverter.asDate(newEvent.getEndDate());
		if(endDate.before(new Date())){
			if(log.isDebugEnabled())
				log.debug("Event is past... don't recommended it!");
			return;
		}
		
		// ATTENDING FRIENDS
		if(log.isDebugEnabled())
			log.debug("Next step is checking which friends are attending");
		
		try{
			List<IIdentity> friendList = context.getMyFriends();
			for(String attendeeJid : newEvent.getAttendees()){
				IIdentity attendee = getIIdentityFromJid(attendeeJid);
				if(friendList.contains(attendee)){
					if(log.isDebugEnabled())
						log.debug("Friend " + attendee.getJid() + " is attending the event!");
					recommendPoints+=FRIEND_POINTS;
				} else{
					if(log.isDebugEnabled())
						log.debug("Attendee " + attendee.getJid() + " is not our friend. :( ");
				}
			}
			
			if(creatorId != null && friendList.contains(creatorId)){
				if(log.isDebugEnabled())
					log.debug("Creator is our friend!");
				recommendPoints+=CREATOR_FRIEND;
			}
			
			if(log.isDebugEnabled())
				log.debug("Now checking the preferred creator");
			String prefCreator = getPreference(CalendarPreference.SUB_CREATOR);
			if(prefCreator != null && prefCreator.equals(creatorId)){
				if(log.isDebugEnabled())
					log.debug("Event is from a favoured creator, so adding points!");
				recommendPoints+=CREATOR_POINTS;
			}
			
		} catch(Exception ex){
			log.error("Exception ocurred!");
			ex.printStackTrace();
		}
		
		// LOCATION
		if(log.isDebugEnabled())
			log.debug("Now we check the location!");
		if(newEvent.getLocation() != null){
			String myLocation = context.getMyLocation();
			String homeCity = context.getHomeCity();
			String workCity = context.getWorkCity();
			
			log.debug("myLocation = " + myLocation);
			log.debug("homeCity = " + homeCity);
			log.debug("workCity = " + workCity);
			
			if(myLocation != null && myLocation.equals(newEvent.getLocation())){
				if(log.isDebugEnabled())
					log.debug("Event is in our location!");
				recommendPoints+=LOCATION_POINTS;
			} else{
			
				if(homeCity != null && homeCity.equals(newEvent.getLocation())){
					if(log.isDebugEnabled())
						log.debug("Event is in our home city!");
					recommendPoints+=LOCATION_POINTS;
				}
				else{
					
					if(workCity != null && workCity.equals(newEvent.getLocation())){
						if(log.isDebugEnabled())
							log.debug("Event is in our work city!");
						recommendPoints+=LOCATION_POINTS;
					}
				}
			}
			
		}
		
		
		// INTERESTS
		List<String> myInterests = context.getMyInterests();
		for(String interest: myInterests){
			if(log.isDebugEnabled())
				log.debug("Checking event for interest: " + interest);
			if(newEvent.getName().regionMatches(true, 0, interest, 0, interest.length())){
				if(log.isDebugEnabled())
					log.debug("Found interest in event: " + interest);
				recommendPoints+=INTEREST_POINTS;
			} else{
				if(newEvent.getDescription() != null && newEvent.getDescription().regionMatches(true, 0, interest, 0, interest.length())){
					if(log.isDebugEnabled())
						log.debug("Found interest in event: " + interest);
					recommendPoints+=INTEREST_POINTS;
				}
			}
		}
		
		// Now we get the keyword we most
		if(recommendPoints >= RECOMMEND_THRESHOLD){
			if(log.isDebugEnabled())
				log.debug("We're going to recommend this event!");
			recommendedEvents.put(newEvent.getEventId(), newEvent);
			recentEvents.put(newEvent.getEventId(), newEvent);

		} else{
			if(log.isDebugEnabled())
				log.debug("No need to recommend this event...");
			if(recommendedEvents.get(newEvent.getEventId()) != null){
				recommendedEvents.remove(newEvent.getEventId());
			}
		}
		
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
				createCalendar(newCommunity.getCommunityName(),cisIdentity);
				
			} catch(Exception ex){
				log.error("Exception while processing CIS_CREATION: " + ex);
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
					String calendarId = createCalendar(newCommunity.getCommunityName(),cisId);
					if(calendarId != null)
						if(log.isDebugEnabled())
							log.debug("Created calendar for " + newCommunity.getCommunityName());
				}
				
			} catch(Exception ex){
				log.error("Exception while processing CIS_RESTORE: " + ex);
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
				String calendarId = database.getCalendarIdFromNodeId(newCommunity.getCommunityJid());
				
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
				IIdentity cisIdentity = getIIdentityFromJid(newCommunity.getCommunityJid());
				Calendar newCalendar = this.retrieveCalendar(cisIdentity, myId);
				if(newCalendar == null){
					if(log.isDebugEnabled())
						log.debug("CIS does not have a calendar, so we do nothing.");
					return;
				} else{
					if(log.isDebugEnabled())
						log.debug("Got the Calendar for that CIS!");
					recentCalendars.put(cisIdentity.getBareJid(), newCalendar);
					//TODO
					// Get events, see if we should add them!
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

	@Override
	public ServiceResourceIdentifier getServiceIdentifier() {
		if(log.isDebugEnabled())
			log.debug("getServiceIdentifier called by Personalisation Manager.");
		if(mySRI == null)
			mySRI = getServiceMgmt().getMyServiceId(getClass());
		
		return mySRI;
	}

	@Override
	public String getServiceType() {
		if(log.isDebugEnabled())
			log.debug("getServiceType called by Personalisation Manager");
		if(serviceType == null){
			serviceType = getServiceMgmt().getMyCategory(mySRI);
		}
		return serviceType;
	}

	@Override
	public List<String> getServiceTypes() {
		if(log.isDebugEnabled())
			log.debug("getServiceTypes called by Personalisation Manager");
		List<String> result = new ArrayList<String>();
		result.add(serviceType);
		return result;
	}

	@Override
	public boolean setIAction(IIdentity userId, IAction action) {
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
				session = sessionFactory.openSession();

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
						.createCriteria(EventDAO.class)
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
				log.error(he.getMessage());

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
					log.debug("A new event arrived: " + newEvent.getName());
				
				processNewEvent(newEvent);
			}
					
			if(calendarMessage.getMessage().equals(Message.UPDATED_EVENT)){
				
				Event updatedEvent = calendarMessage.getEvent();
				
				if(log.isDebugEnabled())
					log.debug("Updated Event: " + updatedEvent.getName());
				
				// First we need to see if we are subscribed to the event, if we are we should tell
				// the user the event has changed!
				try{
					IIdentity requestor = getIIdentityFromJid(calendarMessage.getRequestorId());

					// Don't need to be alerted if I'm the creator => CHECK LATER IF THIS ALTERS
					if(!requestor.equals(myId)){
						
						// Are we subscribed?
						List<String> attendees = updatedEvent.getAttendees();
						if(attendees != null && attendees.contains(myId)){
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
					log.error("Exception occurred while processing updated event from PubSub");
				}
				
				
			}
			
			if(calendarMessage.getMessage().equals(Message.DELETED_EVENT)){
				Event deletedEvent = calendarMessage.getEvent();
				
				if(log.isDebugEnabled())
					log.debug("Event was deleted: " + deletedEvent.getName());
				
				// First we check if the event was in the recommended events, and we remove it if so
				if(recommendedEvents.get(deletedEvent.getEventId()) != null){
					if(log.isDebugEnabled())
						log.debug("Event was in the recommended Events, so we need to remove it!");
					
					recommendedEvents.remove(deletedEvent.getEventId());
				}
				
				// Oh, and we do the same for the recent Events
				// First we check if the event was in the recent events, and we remove it if so
				if(recentEvents.get(deletedEvent.getEventId()) != null){
					if(log.isDebugEnabled())
						log.debug("Event was in the recentEvents , so we need to remove it!");
					
					recentEvents.remove(deletedEvent.getEventId());
				}
				
				// Next we check to see if we are already subscribed to that event, if so we need to
				if(log.isDebugEnabled())
					log.debug("Checking to see if we were subscribed!");
				List<String> attendees = deletedEvent.getAttendees();
				if(attendees != null && attendees.contains(myId)){
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
	
					if(context.getMyFriends().contains(newAttendee)){
						if(log.isDebugEnabled())
							log.debug("Attendee is a friend of ours, so we should process the event to see if it's recommended!");
							newUserWarning("Friend joined event!", getCssName(attendeeId) + " has joined the event " + event.getName());
							
							processNewEvent(event);
					} else{
						if(log.isDebugEnabled())
							log.debug("Attendee isn't a friend of ours, so we do nothing!");
					}
				
				} catch (InvalidFormatException e) {
					log.error("Error getting IIdentity from attendee JID");
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
	
					if(context.getMyFriends().contains(newAttendee)){
						if(log.isDebugEnabled())
							log.debug("Attendee is a friend of ours, so we should process the event to see if it's (still) recommended!");
							newUserWarning("Friend left event!", getCssName(attendeeId) + " has left the event " + event.getName());

							processNewEvent(event);
					} else{
						if(log.isDebugEnabled())
							log.debug("Attendee isn't a friend of ours, so we do nothing!");
					}
				
				} catch (InvalidFormatException e) {
					log.error("Error getting IIdentity from attendee JID");
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
		if(recommendedEvents == null){
			recommendedEvents = new HashMap<String,Event>();
			initRecommendedEvents();
		}
		
		Collection<Event> eventCol = recommendedEvents.values();
		List<Event> resultEvents = new ArrayList<Event>();
		Date now = new Date();
				
		for(Event recomEvent: eventCol){
			if(log.isDebugEnabled())
				log.debug("Recommended Event: " + recomEvent.getName() );
			
			Date startDate = CalendarConverter.asDate(recomEvent.getStartDate());
			Date endDate = CalendarConverter.asDate(recomEvent.getEndDate());
			
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
		
		Collections.sort(resultEvents, new EventSorter());
		return resultEvents;
	}


	@Override
	public List<Event> findEventsInCalendar(IIdentity node, Event searchEvent, IIdentity requestor) {
		if(log.isDebugEnabled())
			log.debug("Searching for events in calendar for node: " + node.getJid());
		
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

					calendarBean.setMethod(MethodType.FIND_EVENTS);
					calendarBean.setNodeId(node.getJid());
					calendarBean.setRequestor(requestor.getJid());
					calendarBean.setEvent(searchEvent);
					
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
			log.error(e.getMessage());
		}
		
		return eventList;
		
	}


	@Override
	public List<Event> findEventsAll(Event searchEvent, IIdentity requestor) {
		if(log.isDebugEnabled())
			log.debug("Searching for events in ALL Calendars");
		
		List<Event> eventList = new ArrayList<Event>();
		
		if(log.isDebugEnabled())
			log.debug("Searching in local calendar!");
		
		List<Event> localEvents = findEventsInCalendar(myId,searchEvent,requestor);
		eventList.addAll(localEvents);
		
		if(log.isDebugEnabled())
			log.debug("Searching in CIS Calendards!");
				
		 List<ICis> cisList = getCisManager().getCisList();
		 
		 for(ICis cis : cisList){
			 if(log.isDebugEnabled())
				 log.debug("Searching for CIS: " + cis.getCisId());
			 try{
				 IIdentity cisId = getIIdentityFromJid(cis.getCisId());
				 eventList.addAll(findEventsInCalendar(cisId,searchEvent,requestor));
			 } catch(Exception ex){
				 log.error("Couldn't get IIdentity from CIS");
				 ex.printStackTrace();
			 }
		 }
		
		return eventList;
	}

	@Override
	public List<UserWarning> getUserWarnings(){
		
		// Create the result List
		List<UserWarning> result = new ArrayList<UserWarning>(userWarnings);
		// Clears the current warnings
		userWarnings.clear();
		// Returns result
		return result;
	}
	

	
	public List<Calendar> getAllCalendars(){
		
		List<Calendar> calendarList = new ArrayList<Calendar>();
		
		if(log.isDebugEnabled())
			log.debug("Retrieving all Calendars...");
		
		calendarList.add(retrieveCalendar(myId,myId));
		
		List<ICis> allCis = getCisManager().getCisList();
		
		for(ICis cis : allCis){
			IIdentity cisId;
			try {
				cisId = getIIdentityFromJid(cis.getCisId());
				Calendar calendar = retrieveCalendar(cisId,myId);
				if(calendar != null)
					calendarList.add(calendar);
			} catch (InvalidFormatException e) {
				log.warn("Couldn't get CIS Id");
				e.printStackTrace();
			}
		}
		
		return calendarList;
		
	}
	
}
