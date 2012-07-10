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
package org.societies.rdpartyService.enterprise;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.css.devicemgmt.IDevice;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.rdpartyService.enterprise.interfaces.IReturnedResultCallback;
import org.societies.rdpartyService.enterprise.interfaces.ISharedCalendarClientRich;
import org.societies.rdpartyservice.enterprise.sharedcalendar.Event;
import org.societies.rdpartyservice.enterprise.sharedcalendar.SharedCalendarBean;

/**
 * This class implement functionalities to interact with the server part of the SharedCalendar Service.
 * 
 * @author solutanet
 * 
 */
public class SharedCalendarClientRich implements ICommCallback,	ISharedCalendarClientRich {
	
	private static Logger log = LoggerFactory.getLogger(SharedCalendarClientRich.class);
	private String pathForJSONfile;
	private ICommManager commManager;
	private IIdentityManager idMgr;
	private IEventMgr evtMgr;
	/**
	 * This is the set of all available instances of the IDevice interface.
	 * A DeviceListener bean instance tracks whenever a new IDevice is bound or unbound.
	 * See http://static.springsource.org/osgi/docs/1.2.1/reference/html-single/#service-registry:refs:collection:dynamics
	 */
	private Set<IDevice> availableDevices;
	
	private static final List<String> NAMESPACES = Collections
			.unmodifiableList(Arrays
					.asList("http://societies.org/rdPartyService/enterprise/sharedCalendar"));
	private static final List<String> PACKAGES = Collections
			.unmodifiableList(Arrays
					.asList("org.societies.rdpartyservice.enterprise.sharedcalendar"));

	/** The JID of the server running the calendar service */
	private String serviceServer;
	
	public SharedCalendarClientRich() {
		super();
	}
	
	public SharedCalendarClientRich(String serviceServer, String jsonFilePath) {
		super();
		this.serviceServer = serviceServer;
		this.pathForJSONfile=jsonFilePath;
	}

	// PROPERTIES
	public ICommManager getCommManager() {
		return commManager;
	}

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}
	
	/**
	 * This method is used to retrieve the jid of the Server where the 3rd party
	 * service is deployed. In a future version this property is loaded
	 * dynamically from xml file.
	 * 
	 * @return the jid of the server where the service is deployed
	 */
	public String getServiceServer() {
		return this.serviceServer;
	}

	public void setServiceServer(String serviceServer) {
		this.serviceServer = serviceServer;
	}

	public IIdentityManager getIdMgr() {
		return idMgr;
	}

	public void setIdMgr(IIdentityManager idMgr) {
		this.idMgr = idMgr;
	}

	public void InitService() {
		// REGISTER OUR ServiceManager WITH THE XMPP Communication Manager
		try {
			getCommManager().register(this);
		} catch (CommunicationException e) {
			e.printStackTrace();
		}
		idMgr = commManager.getIdManager();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.api.comm.xmpp.interfaces.ICommCallback#getXMLNamespaces()
	 */
	@Override
	public List<String> getXMLNamespaces() {
		
		return NAMESPACES;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.api.comm.xmpp.interfaces.ICommCallback#getJavaPackages()
	 */
	@Override
	public List<String> getJavaPackages() {
		
		return PACKAGES;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveResult(org
	 * .societies.api.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public void receiveResult(Stanza stanza, Object payload) {
		

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveError(org
	 * .societies.api.comm.xmpp.datatypes.Stanza,
	 * org.societies.api.comm.xmpp.exceptions.XMPPError)
	 */
	@Override
	public void receiveError(Stanza stanza, XMPPError error) {
		

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveInfo(org.
	 * societies.api.comm.xmpp.datatypes.Stanza, java.lang.String,
	 * org.societies.api.comm.xmpp.datatypes.XMPPInfo)
	 */
	@Override
	public void receiveInfo(Stanza stanza, String node, XMPPInfo info) {
		

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveItems(org
	 * .societies.api.comm.xmpp.datatypes.Stanza, java.lang.String,
	 * java.util.List)
	 */
	@Override
	public void receiveItems(Stanza stanza, String node, List<String> items) {
		

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveMessage(org
	 * .societies.api.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public void receiveMessage(Stanza stanza, Object payload) {
		
	}
	
	private IIdentity retrieveTargetIdentity(){
		IIdentity result = null;
		try {
			if (idMgr != null){
				result = idMgr.fromJid(getServiceServer());
			}else{
				log.error("Identity Manager is NOT available.");
			}
		} catch (InvalidFormatException e1) {
			
			e1.printStackTrace();
		} 
		return result;
	}

	//START CIS CALENDAR IMPLEMENTATION METHODS//
	
	public void retrieveCISCalendars(
			IReturnedResultCallback returnedResultCallback, String CISId) {
		
		Stanza stanza = new Stanza(retrieveTargetIdentity());

		// SETUP CALENDAR CLIENT RETURN STUFF
		SharedCalendarCallBack callback = new SharedCalendarCallBack(
				stanza.getId(), returnedResultCallback);

		// CREATE MESSAGE BEAN
		SharedCalendarBean calendarBean = new SharedCalendarBean();

		calendarBean
				.setMethod(org.societies.rdpartyservice.enterprise.sharedcalendar.MethodType.RETRIEVE_CIS_CALENDAR_LIST);
		calendarBean.setCISId(CISId);
		try {
			// SEND INFORMATION QUERY - RESPONSE WILL BE IN
			// "callback.RecieveMessage()"
			commManager.sendIQGet(stanza, calendarBean, callback);
			log.info("The message was sent to XMPP server for retrieve all public calendar.");
		} catch (CommunicationException e) {
			log.error("ERROR: " + e.getStackTrace()[0].getMethodName());
		}
	}
	
	
	/* (non-Javadoc)
	 * @see org.societies.rdpartyService.enterprise.interfaces.ISharedCalendarClientRich#createCISCalendar(org.societies.rdpartyService.enterprise.interfaces.IReturnedResultCallback, java.lang.String, java.lang.String)
	 */
	@Override
	public void createCISCalendar(
			IReturnedResultCallback returnedResultCallback,
			String calendarSummary, String CISId) {
		Stanza stanza = new Stanza(retrieveTargetIdentity());

		// SETUP CALENDAR CLIENT RETURN STUFF
		SharedCalendarCallBack callback = new SharedCalendarCallBack(
				stanza.getId(), returnedResultCallback);

		// CREATE MESSAGE BEAN
		SharedCalendarBean calendarBean = new SharedCalendarBean();

		calendarBean.setMethod(org.societies.rdpartyservice.enterprise.sharedcalendar.MethodType.CREATE_CIS_CALENDAR);
		calendarBean.setCalendarSummary(calendarSummary);
		calendarBean.setCISId(CISId);
		try {
			// SEND INFORMATION QUERY - RESPONSE WILL BE IN
			// "callback.RecieveMessage()"
			commManager.sendIQGet(stanza, calendarBean, callback);
			log.info("The message was sent to XMPP server for create a CSS calendar with summary: "
					+ calendarSummary + ".");
		} catch (CommunicationException e) {
			log.error("ERROR: " + e.getStackTrace()[0].getMethodName());
		}
		
	}

	/* (non-Javadoc)
	 * @see org.societies.rdpartyService.enterprise.interfaces.ISharedCalendarClientRich#deleteCISCalendar(org.societies.rdpartyService.enterprise.interfaces.IReturnedResultCallback, java.lang.String)
	 */
	@Override
	public void deleteCISCalendar(
			IReturnedResultCallback returnedResultCallback, String calendarId) {
		Stanza stanza = new Stanza(retrieveTargetIdentity());

		// SETUP CALENDAR CLIENT RETURN STUFF
		SharedCalendarCallBack callback = new SharedCalendarCallBack(
				stanza.getId(), returnedResultCallback);

		// CREATE MESSAGE BEAN
		SharedCalendarBean calendarBean = new SharedCalendarBean();

		calendarBean
				.setMethod(org.societies.rdpartyservice.enterprise.sharedcalendar.MethodType.DELETE_CIS_CALENDAR);
		calendarBean.setCalendarId(calendarId);
		try {
			// SEND INFORMATION QUERY - RESPONSE WILL BE IN
			// "callback.RecieveMessage()"
			commManager.sendIQGet(stanza, calendarBean, callback);
			log.info("The message was sent to XMPP server for delete a CIS calendar with id: "
					+ calendarId + ".");
		} catch (CommunicationException e) {
			log.error("ERROR: " + e.getStackTrace()[0].getMethodName());
		}
		
	}

	/* (non-Javadoc)
	 * @see org.societies.rdpartyService.enterprise.interfaces.ISharedCalendarClientRich#retrieveCISCalendarEvents(org.societies.rdpartyService.enterprise.interfaces.IReturnedResultCallback, java.lang.String)
	 */
	@Override
	public void retrieveCISCalendarEvents(
			IReturnedResultCallback returnedResultCallback, String calendarId) {
		Stanza stanza = new Stanza(retrieveTargetIdentity());

		// SETUP CALENDAR CLIENT RETURN STUFF
		SharedCalendarCallBack callback = new SharedCalendarCallBack(
				stanza.getId(), returnedResultCallback);

		// CREATE MESSAGE BEAN
		SharedCalendarBean calendarBean = new SharedCalendarBean();

		calendarBean
				.setMethod(org.societies.rdpartyservice.enterprise.sharedcalendar.MethodType.RETRIEVE_CIS_CALENDAR_EVENTS);
		calendarBean.setCalendarId(calendarId);
		try {
			// SEND INFORMATION QUERY - RESPONSE WILL BE IN
			// "callback.RecieveMessage()"
			commManager.sendIQGet(stanza, calendarBean, callback);
			log.info("The message was sent to XMPP server for retrieve CIS calendar events in calendar with id: "
					+ calendarId + ".");
		} catch (CommunicationException e) {
			log.error("ERROR: " + e.getStackTrace()[0].getMethodName());
		}
		
	}

	
	
	/* (non-Javadoc)
	 * @see org.societies.rdpartyService.enterprise.interfaces.ISharedCalendarClientRich#createEventOnCISCalendar(org.societies.rdpartyService.enterprise.interfaces.IReturnedResultCallback, org.societies.rdpartyservice.enterprise.sharedcalendar.Event, java.lang.String)
	 */
	@Override
	public void createEventOnCISCalendar(
			IReturnedResultCallback returnedResultCallback, Event newEvent,
			String calendarId) {
		Stanza stanza = new Stanza(retrieveTargetIdentity());

		// SETUP CALENDAR CLIENT RETURN STUFF
		SharedCalendarCallBack callback = new SharedCalendarCallBack(
				stanza.getId(), returnedResultCallback);

		// CREATE MESSAGE BEAN
		SharedCalendarBean calendarBean = new SharedCalendarBean();

		calendarBean
				.setMethod(org.societies.rdpartyservice.enterprise.sharedcalendar.MethodType.CREATE_EVENT_ON_CIS_CALENDAR);
		calendarBean.setCalendarId(calendarId);
		calendarBean.setNewEvent(newEvent);
		try {
			// SEND INFORMATION QUERY - RESPONSE WILL BE IN
			// "callback.RecieveMessage()"
			commManager.sendIQGet(stanza, calendarBean, callback);
			log.info("The message was sent to XMPP server for create an event on CIS calendar with id: "
					+ calendarId + ".");
		} catch (CommunicationException e) {
			log.error("ERROR: " + e.getStackTrace()[0].getMethodName());
		}
		
	}

	/* (non-Javadoc)
	 * @see org.societies.rdpartyService.enterprise.interfaces.ISharedCalendarClientRich#deleteEventOnCISCalendar(org.societies.rdpartyService.enterprise.interfaces.IReturnedResultCallback, java.lang.String, java.lang.String)
	 */
	@Override
	public void deleteEventOnCISCalendar(
			IReturnedResultCallback returnedResultCallback, String eventId,
			String calendarId) {
		Stanza stanza = new Stanza(retrieveTargetIdentity());

		// SETUP CALENDAR CLIENT RETURN STUFF
		SharedCalendarCallBack callback = new SharedCalendarCallBack(
				stanza.getId(), returnedResultCallback);

		// CREATE MESSAGE BEAN
		SharedCalendarBean calendarBean = new SharedCalendarBean();

		calendarBean
				.setMethod(org.societies.rdpartyservice.enterprise.sharedcalendar.MethodType.DELETE_EVENT_ON_CIS_CALENDAR);
		calendarBean.setCalendarId(calendarId);
		calendarBean.setEventId(eventId);
		
		try {
			// SEND INFORMATION QUERY - RESPONSE WILL BE IN
			// "callback.RecieveMessage()"
			commManager.sendIQGet(stanza, calendarBean, callback);
			log.info("The message was sent to XMPP server for delete an event with id: "+eventId+" on CIS calendar with id: "
					+ calendarId + ".");
		} catch (CommunicationException e) {
			log.error("ERROR: " + e.getStackTrace()[0].getMethodName());
		}
		
	}
	/* (non-Javadoc)
	 * @see org.societies.rdpartyService.enterprise.interfaces.ISharedCalendarClientRich#subscribeToEvent(org.societies.rdpartyService.enterprise.interfaces.IReturnedResultCallback, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void subscribeToEvent(
			IReturnedResultCallback returnedResultCallback, String calendarId,
			String eventId, String subscriberId) {
		Stanza stanza = new Stanza(retrieveTargetIdentity());

		// SETUP CALENDAR CLIENT RETURN STUFF
		SharedCalendarCallBack callback = new SharedCalendarCallBack(
				stanza.getId(), returnedResultCallback);

		// CREATE MESSAGE BEAN
		SharedCalendarBean calendarBean = new SharedCalendarBean();

		calendarBean
				.setMethod(org.societies.rdpartyservice.enterprise.sharedcalendar.MethodType.SUBSCRIBE_TO_EVENT);
		calendarBean.setCalendarId(calendarId);
		calendarBean.setEventId(eventId);
		calendarBean.setSubscriberId(subscriberId);
		
		try {
			// SEND INFORMATION QUERY - RESPONSE WILL BE IN
			// "callback.RecieveMessage()"
			commManager.sendIQGet(stanza, calendarBean, callback);
			log.info("The message was sent to XMPP server for subscribe to an event with id: "+eventId+" on CIS calendar with id: "
					+ calendarId + " and subscriber id: "+subscriberId+".");
		} catch (CommunicationException e) {
			log.error("ERROR: " + e.getStackTrace()[0].getMethodName());
		}
		
		
	}

	/* (non-Javadoc)
	 * @see org.societies.rdpartyService.enterprise.interfaces.ISharedCalendarClientRich#findEvents(org.societies.rdpartyService.enterprise.interfaces.IReturnedResultCallback, java.lang.String, java.lang.String)
	 */
	@Override
	public void findEvents(
			IReturnedResultCallback returnedResultCallback, String calendarId,
			String keyWord) {
		Stanza stanza = new Stanza(retrieveTargetIdentity());

		// SETUP CALENDAR CLIENT RETURN STUFF
		SharedCalendarCallBack callback = new SharedCalendarCallBack(
				stanza.getId(), returnedResultCallback);

		// CREATE MESSAGE BEAN
		SharedCalendarBean calendarBean = new SharedCalendarBean();

		calendarBean
				.setMethod(org.societies.rdpartyservice.enterprise.sharedcalendar.MethodType.FIND_EVENTS);
		calendarBean.setCalendarId(calendarId);
		calendarBean.setKeyWord(keyWord);
		
		try {
			// SEND INFORMATION QUERY - RESPONSE WILL BE IN
			// "callback.RecieveMessage()"
			commManager.sendIQGet(stanza, calendarBean, callback);
			log.info("The message was sent to XMPP server for retrieve events on CIS calendar with id: "
					+ calendarId + " using keyword: "+keyWord+".");
		} catch (CommunicationException e) {
			log.error("ERROR: " + e.getStackTrace()[0].getMethodName());
		}
		
	}

	/* (non-Javadoc)
	 * @see org.societies.rdpartyService.enterprise.interfaces.ISharedCalendarClientRich#unsubscribeFromEvent(org.societies.rdpartyService.enterprise.interfaces.IReturnedResultCallback, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void unsubscribeFromEvent(
			IReturnedResultCallback returnedResultCallback, String calendarId,
			String eventId, String subscriberId) {
		Stanza stanza = new Stanza(retrieveTargetIdentity());

		// SETUP CALENDAR CLIENT RETURN STUFF
		SharedCalendarCallBack callback = new SharedCalendarCallBack(
				stanza.getId(), returnedResultCallback);

		// CREATE MESSAGE BEAN
		SharedCalendarBean calendarBean = new SharedCalendarBean();

		calendarBean
				.setMethod(org.societies.rdpartyservice.enterprise.sharedcalendar.MethodType.UNSUBSCRIBE_FROM_EVENT);
		calendarBean.setCalendarId(calendarId);
		calendarBean.setEventId(eventId);
		calendarBean.setSubscriberId(subscriberId);
		
		try {
			// SEND INFORMATION QUERY - RESPONSE WILL BE IN
			// "callback.RecieveMessage()"
			commManager.sendIQGet(stanza, calendarBean, callback);
			log.info("The message was sent to XMPP server for unsubscribe to an event with id: "+eventId+" on CIS calendar with id: "
					+ calendarId + " and subscriber id: "+subscriberId+".");
		} catch (CommunicationException e) {
			log.error("ERROR: " + e.getStackTrace()[0].getMethodName());
		}
		
	}
	
	//START CSS CALENDAR IMPLEMENTATION METHODS//
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.rdpartyService.enterprise.interfaces.ISharedCalendarClientRich
	 * #
	 * retrieveCSSCalendarEvents(org.societies.rdpartyService.enterprise.interfaces
	 * .IReturnedResultCallback)
	 */
	@Override
	public void retrieveEventsPrivateCalendar(
			IReturnedResultCallback returnedResultCallback) {
		
		// IIdentity toIdentity = new NetworkNodeImpl(
		// retrieve3rdPartyCalendarServerIdentity());
		Stanza stanza = new Stanza(retrieveTargetIdentity());

		// SETUP CALENDAR CLIENT RETURN STUFF
		SharedCalendarCallBack callback = new SharedCalendarCallBack(
				stanza.getId(), returnedResultCallback);

		// CREATE MESSAGE BEAN
		SharedCalendarBean calendarBean = new SharedCalendarBean();

		calendarBean
				.setMethod(org.societies.rdpartyservice.enterprise.sharedcalendar.MethodType.RETRIEVE_EVENTS_ON_PRIVATE_CALENDAR);
		try {
			// SEND INFORMATION QUERY - RESPONSE WILL BE IN
			// "callback.RecieveMessage()"
			commManager.sendIQGet(stanza, calendarBean, callback);
			log.info("The message was sent to XMPP server for retrieve CSS calendar events.");
		} catch (CommunicationException e) {
			log.error("ERROR: " + e.getStackTrace()[0].getMethodName());
		}

	}

	public void createPrivateCalendar(
			IReturnedResultCallback returnedResultCallback,
			String calendarSummary) {
		
		Stanza stanza = new Stanza(retrieveTargetIdentity());

		// SETUP CALENDAR CLIENT RETURN STUFF
		SharedCalendarCallBack callback = new SharedCalendarCallBack(
				stanza.getId(), returnedResultCallback);

		// CREATE MESSAGE BEAN
		SharedCalendarBean calendarBean = new SharedCalendarBean();

		calendarBean
				.setMethod(org.societies.rdpartyservice.enterprise.sharedcalendar.MethodType.CREATE_PRIVATE_CALENDAR);
		calendarBean.setCalendarSummary(calendarSummary);
		try {
			// SEND INFORMATION QUERY - RESPONSE WILL BE IN
			// "callback.RecieveMessage()"
			commManager.sendIQGet(stanza, calendarBean, callback);
			log.info("The message was sent to XMPP server for create a CSS calendar with summary: "
					+ calendarSummary + ".");
		} catch (CommunicationException e) {
			log.error("ERROR: " + e.getStackTrace()[0].getMethodName());
		}
	}
	
	
	

	/* (non-Javadoc)
	 * @see org.societies.rdpartyService.enterprise.interfaces.ISharedCalendarClientRich#deletePrivateCalendar(org.societies.rdpartyService.enterprise.interfaces.IReturnedResultCallback)
	 */
	@Override
	public void deletePrivateCalendar(
			IReturnedResultCallback returnedResultCallback) {
		Stanza stanza = new Stanza(retrieveTargetIdentity());

		// SETUP CALENDAR CLIENT RETURN STUFF
		SharedCalendarCallBack callback = new SharedCalendarCallBack(
				stanza.getId(), returnedResultCallback);

		// CREATE MESSAGE BEAN
		SharedCalendarBean calendarBean = new SharedCalendarBean();

		calendarBean
				.setMethod(org.societies.rdpartyservice.enterprise.sharedcalendar.MethodType.DELETE_PRIVATE_CALENDAR);
		try {
			// SEND INFORMATION QUERY - RESPONSE WILL BE IN
			// "callback.RecieveMessage()"
			commManager.sendIQGet(stanza, calendarBean, callback);
			log.info("The message was sent to XMPP server for delete a CSS calendar.");
		} catch (CommunicationException e) {
			log.error("ERROR: " + e.getStackTrace()[0].getMethodName());
		}
		
	}

	/* (non-Javadoc)
	 * @see org.societies.rdpartyService.enterprise.interfaces.ISharedCalendarClientRich#createEventOnPrivateCalendar(org.societies.rdpartyService.enterprise.interfaces.IReturnedResultCallback, org.societies.rdpartyservice.enterprise.sharedcalendar.Event)
	 */
	@Override
	public void createEventOnPrivateCalendar(
			IReturnedResultCallback returnedResultCallback, Event newEvent) {
		Stanza stanza = new Stanza(retrieveTargetIdentity());

		// SETUP CALENDAR CLIENT RETURN STUFF
		SharedCalendarCallBack callback = new SharedCalendarCallBack(
				stanza.getId(), returnedResultCallback);

		// CREATE MESSAGE BEAN
		SharedCalendarBean calendarBean = new SharedCalendarBean();

		calendarBean
				.setMethod(org.societies.rdpartyservice.enterprise.sharedcalendar.MethodType.CREATE_EVENT_ON_PRIVATE_CALENDAR);
		calendarBean.setNewEvent(newEvent);
		try {
			// SEND INFORMATION QUERY - RESPONSE WILL BE IN
			// "callback.RecieveMessage()"
			commManager.sendIQGet(stanza, calendarBean, callback);
			log.info("The message was sent to XMPP server for create a CSS calendar events.");
		} catch (CommunicationException e) {
			log.error("ERROR: " + e.getStackTrace()[0].getMethodName());
		}
		
	}
	/* (non-Javadoc)
	 * @see org.societies.rdpartyService.enterprise.interfaces.ISharedCalendarClientRich#deleteEventOnPrivateCalendar(java.lang.String)
	 */
	@Override
	public void deleteEventOnPrivateCalendar(IReturnedResultCallback returnedResultCallback,String eventId) {
		Stanza stanza = new Stanza(retrieveTargetIdentity());

		// SETUP CALENDAR CLIENT RETURN STUFF
		SharedCalendarCallBack callback = new SharedCalendarCallBack(
				stanza.getId(), returnedResultCallback);

		// CREATE MESSAGE BEAN
		SharedCalendarBean calendarBean = new SharedCalendarBean();

		calendarBean
				.setMethod(org.societies.rdpartyservice.enterprise.sharedcalendar.MethodType.DELETE_EVENT_ON_PRIVATE_CALENDAR);
		calendarBean.setEventId(eventId);
		try {
			// SEND INFORMATION QUERY - RESPONSE WILL BE IN
			// "callback.RecieveMessage()"
			commManager.sendIQGet(stanza, calendarBean, callback);
			log.info("The message was sent to XMPP server for delete a CSS calendar event.");
		} catch (CommunicationException e) {
			log.error("ERROR: " + e.getStackTrace()[0].getMethodName());
		}
		
	}
	
	//UTILITY METHODS//
	/**
	 * This method create the list JSON objects (compatible with the presentation framewok jquery-weekcalendar-1.2.2) starting from a list of events.
	 * @param eventListToRender
	 * @return the String that represent the Json array
	 */
	public String createJSONOEvents(List<Event> eventListToRender){
		/*
	  "id":10182,
      "start":"2009-05-03T14:00:00.000+10:00",
      "end":"2009-05-03T15:00:00.000+10:00",
      "title":"Dev Meeting"
      */
		JSONArray jsonArray=new JSONArray();
		
		SimpleDateFormat simpleDaeFtormat=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSZ");
		 for (Event event : eventListToRender) {
			 JSONObject object=new JSONObject();
			object.put("id", /*Integer.parseInt(*/event.getEventId()/*)*/);
			object.put("start", simpleDaeFtormat.format(XMLGregorianCalendarConverter.asDate(event.getStartDate())));
			object.put("end", simpleDaeFtormat.format(XMLGregorianCalendarConverter.asDate(event.getEndDate())));
			object.put("title", event.getEventDescription());
			jsonArray.add(object);
		}
		 for (Object object : jsonArray) {
			log.debug(object.toString());
		}
		return jsonArray.toString();
	}

	private boolean writeJsonToFile(String path ,String JSONObjects){
		String tesPath = pathForJSONfile;

        FileWriter fileWriter = null;
        BufferedWriter out=null;
    try{
    	fileWriter = new FileWriter(new File(tesPath));
    	out = new BufferedWriter(fileWriter);
        log.info("Start Writing JsonFile");
        out.write(JSONObjects);
            
      }catch (Exception e){
      log.error("Error: " + e);
      }
    finally{
    	if ((fileWriter !=null)||(out!=null)){
    	try {
    		out.flush();
		out.close();
		fileWriter.close();} 
    	catch (IOException e) {
			e.printStackTrace();
		}}
    }
return true;
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
	
}
