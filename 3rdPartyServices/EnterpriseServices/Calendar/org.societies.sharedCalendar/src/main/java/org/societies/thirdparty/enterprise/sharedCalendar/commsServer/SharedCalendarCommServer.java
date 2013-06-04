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
package org.societies.thirdparty.enterprise.sharedCalendar.commsServer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.interfaces.IFeatureServer;
import org.societies.api.ext3p.schema.sharedcalendar.Calendar;
import org.societies.api.ext3p.schema.sharedcalendar.Event;
import org.societies.api.ext3p.schema.sharedcalendar.SharedCalendarBean;
import org.societies.api.ext3p.schema.sharedcalendar.SharedCalendarResult;
import org.societies.api.identity.IIdentity;
import org.societies.api.osgi.event.CSSEvent;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.schema.cis.community.Community;
import org.societies.thirdparty.enterprise.sharedCalendar.SharedCalendar;

/**
 * This is the Shared Calendar Communication Manager that marshalls / unmarshalls XMPP messages
 * and routes them to/from the correct Shared Calendar Server functionality.
 *
 * @author solutanet
 *
 */
public class SharedCalendarCommServer implements IFeatureServer{
	private ICommManager commManager;
	private SharedCalendar sharedCalendarService;
	private static final Logger log = LoggerFactory.getLogger(SharedCalendarCommServer.class);
	
	//TODO Gather access to namespaces and packages in a single point!
	public static final List<String> NAMESPACES = Collections.unmodifiableList(
			Arrays.asList("http://societies.org/api/ext3p/schema/sharedCalendar"));
	public static final List<String> PACKAGES = Collections.unmodifiableList(
			Arrays.asList("org.societies.api.ext3p.schema.sharedcalendar"));
	
	//PROPERTIES
	public ICommManager getCommManager() {
		return commManager;
	}

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}
	
	public SharedCalendar getSharedCalendarService() {
		return sharedCalendarService;
	}

	public void setSharedCalendarService(SharedCalendar sharedCalendarService) {
		this.sharedCalendarService = sharedCalendarService;
	}

	public void initService() {
		if(log.isDebugEnabled())
			log.debug("initService for SharedCalendar CommServer");
		
		// REGISTER OUR ServiceManager WITH THE XMPP Communication Manager
		ICommManager cm = getCommManager();
		try {
			if(log.isDebugEnabled())
				log.debug("Registering Shared Calendar CommService");
			
			cm.register(this);
		} catch (CommunicationException e) {
			log.error("Exception while registering Shared Calendar:" + e);
			e.printStackTrace();
		}
	}

	@Override
	public List<String> getXMLNamespaces() {
		// TODO Auto-generated method stub
		return SharedCalendarCommServer.NAMESPACES;
	}


	@Override
	public List<String> getJavaPackages() {
		return SharedCalendarCommServer.PACKAGES;
	}

	@Override
	public void receiveMessage(Stanza stanza, Object payload) {
		
		if(log.isDebugEnabled()){		
			log.debug("Stanza:"+stanza);	
			if (payload instanceof Calendar){
				log.debug("Payload:"+(Calendar)payload);
			}
		}
	}

	@Override
	public Object getQuery(Stanza stanza, Object payload) throws XMPPError {
		SharedCalendarBean bean = null;
		SharedCalendarResult resultBean = new SharedCalendarResult();
		
		if(log.isDebugEnabled())
			log.debug("getQuery method of Shared Calendar Comm-Server.");
		try{

		if (payload instanceof SharedCalendarBean){
			bean = (SharedCalendarBean) payload;
			IIdentity requestor;
			String requestorId = bean.getRequestor();
			if(requestorId != null && !requestorId.equals("")){
				if(log.isDebugEnabled())
					log.debug("Requestor was sent: " + requestorId);
				requestor = getCommManager().getIdManager().fromJid(requestorId);
			} else
				requestor = stanza.getFrom();
	
			IIdentity node = getCommManager().getIdManager().fromJid(bean.getNodeId());
			
			String subscriberId = bean.getSubscriberId();
			
			IIdentity subscriber;
			if(subscriberId != null && !subscriberId.equals("")){
				if(log.isDebugEnabled())
					log.debug("subscriberId was sent: " + subscriberId);
				subscriber = getCommManager().getIdManager().fromJid(subscriberId);
			} else
				subscriber = stanza.getFrom();
			
			if(log.isDebugEnabled())
				log.debug("The message came from " + stanza.getFrom().getJid());

			switch (bean.getMethod()) {
			case RETRIEVE_CALENDAR:
				Calendar retrievedCalendar = this.sharedCalendarService.retrieveCalendar(node, requestor);
				resultBean.setCalendar(retrievedCalendar);
				break;
			case RETRIEVE_ALL_EVENTS:
				List<Event> retrievedEvents = this.sharedCalendarService.retrieveEvents(node, requestor);
				resultBean.setEventList(retrievedEvents);
				break;
			case RETRIEVE_EVENT:
				Event retrievedEvent = this.sharedCalendarService.retrieveEvent(bean.getEventId(), node, requestor);
				resultBean.setEvent(retrievedEvent);
				break;
			case SUBSCRIBE_TO_EVENT:
				resultBean.setSubscribingResult(this.sharedCalendarService.subscribeToEvent(bean.getEventId(), node, subscriber));
				break;
			case FIND_EVENTS:
				List<Event> foundEvents = this.sharedCalendarService.findEventsInCalendar(node, bean.getEvent());
				resultBean.setEventList(foundEvents);
				break;
			case UNSUBSCRIBE_FROM_EVENT:
				resultBean.setSubscribingResult(this.sharedCalendarService.unsubscribeFromEvent(bean.getEventId(), subscriber));
				break;
			case UPDATE_EVENT:
				resultBean.setLastOperationSuccessful(this.sharedCalendarService.updateEvent(bean.getEvent(), requestor));
				break;
			case CREATE_EVENT:
				resultBean.setEventId((this.sharedCalendarService.createEvent(bean.getEvent(), node, requestor)));
				break;
			case DELETE_EVENT:
				resultBean.setLastOperationSuccessful(this.sharedCalendarService.deleteEvent(bean.getEventId(), node, requestor));
				break;
				
			default:
				resultBean = null;
				break;
			}

		}
		} catch(Exception ex){
			ex.printStackTrace();
		}
		return resultBean;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.comm.xmpp.interfaces.IFeatureServer#setQuery(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public Object setQuery(Stanza stanza, Object payload) throws XMPPError {
		// TODO Auto-generated method stub
		return null;
	}

	public SharedCalendarCommServer() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	

}
