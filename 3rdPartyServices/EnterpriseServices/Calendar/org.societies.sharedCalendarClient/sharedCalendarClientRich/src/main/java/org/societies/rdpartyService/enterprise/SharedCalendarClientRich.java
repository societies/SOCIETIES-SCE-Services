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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.rdpartyService.enterprise.interfaces.IReturnedResultCallback;
import org.societies.rdpartyService.enterprise.interfaces.ISharedCalendarClientRich;
import org.societies.rdpartyservice.enterprise.sharedcalendar.Calendar;
import org.societies.rdpartyservice.enterprise.sharedcalendar.Event;
import org.societies.rdpartyservice.enterprise.sharedcalendar.SharedCalendarBean;
import org.societies.rdpartyservice.enterprise.sharedcalendar.SharedCalendarResult;

/**
 * Describe your class here...
 * 
 * @author solutanet
 * 
 */
public class SharedCalendarClientRich implements ICommCallback,	ISharedCalendarClientRich {
	
	private static Logger log = LoggerFactory.getLogger(SharedCalendarClientRich.class);
	private ICommManager commManager;
	private IIdentityManager idMgr;
	
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
	
	public SharedCalendarClientRich(String serviceServer) {
		super();
		this.serviceServer = serviceServer;
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

		// Test
		retrieveAllPublicCalendars(new TestCallBackRetrieveAllCalendars());
		// createCSSCalendar(new TestCallBackCreateCSSCalendar(),
		// "Test private calendar from bundle");
		// retrieveCSSCalendarEvents(new
		// TestCallBackRetrieveCSSCalendarEvents());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.api.comm.xmpp.interfaces.ICommCallback#getXMLNamespaces()
	 */
	@Override
	public List<String> getXMLNamespaces() {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

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
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		return result;
	}

	public void retrieveAllPublicCalendars(
			IReturnedResultCallback returnedResultCallback) {
		
		Stanza stanza = new Stanza(retrieveTargetIdentity());

		// SETUP CALENDAR CLIENT RETURN STUFF
		SharedCalendarCallBack callback = new SharedCalendarCallBack(
				stanza.getId(), returnedResultCallback);

		// CREATE MESSAGE BEAN
		SharedCalendarBean calendarBean = new SharedCalendarBean();

		calendarBean
				.setMethod(org.societies.rdpartyservice.enterprise.sharedcalendar.MethodType.RETRIEVE_CALENDAR_LIST);
		try {
			// SEND INFORMATION QUERY - RESPONSE WILL BE IN
			// "callback.RecieveMessage()"
			commManager.sendIQGet(stanza, calendarBean, callback);
			log.info("The message was sent to XMPP server for retrieve all public calendar.");
		} catch (CommunicationException e) {
			log.error("ERROR: " + e.getStackTrace()[0].getMethodName());
		}
	}

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
	public void retrieveCSSCalendarEvents(
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
				.setMethod(org.societies.rdpartyservice.enterprise.sharedcalendar.MethodType.RETRIEVE_EVENTS_PRIVATE_CALENDAR);
		try {
			// SEND INFORMATION QUERY - RESPONSE WILL BE IN
			// "callback.RecieveMessage()"
			commManager.sendIQGet(stanza, calendarBean, callback);
			log.info("The message was sent to XMPP server for retrieve CSS calendar events.");
		} catch (CommunicationException e) {
			log.error("ERROR: " + e.getStackTrace()[0].getMethodName());
		}

	}

	public void createCSSCalendar(
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
		calendarBean.setPrivateCalendarSummary(calendarSummary);
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

	// /////////////////////////////////TESTS CALLBACK
	// CLASSES/////////////////////////////////////////////

	private class TestCallBackRetrieveAllCalendars implements
			IReturnedResultCallback {

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.societies.rdpartyService.enterprise.IReturnedResultCallback#
		 * receiveResult(java.lang.Object)
		 */
		@Override
		public void receiveResult(Object returnValue) {
			log.info("Results returned");
			SharedCalendarResult result = (SharedCalendarResult) returnValue;
			List<Calendar> returnedList = result.getCalendarList();
			for (Calendar calendar : returnedList) {
				log.info(calendar.getDescription());
			}

		}
	}

	private class TestCallBackRetrieveCSSCalendarEvents implements
			IReturnedResultCallback {

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.societies.rdpartyService.enterprise.IReturnedResultCallback#
		 * receiveResult(java.lang.Object)
		 */
		@Override
		public void receiveResult(Object returnValue) {
			log.info("Results returned");
			SharedCalendarResult result = (SharedCalendarResult) returnValue;
			List<Event> returnedList = result.getEventList();
			for (Event event : returnedList) {
				log.info(event.getEventDescription());
			}

		}
	}

	private class TestCallBackCreateCSSCalendar implements
			IReturnedResultCallback {

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.societies.rdpartyService.enterprise.IReturnedResultCallback#
		 * receiveResult(java.lang.Object)
		 */
		@Override
		public void receiveResult(Object returnValue) {
			log.info("Results returned");
			SharedCalendarResult result = (SharedCalendarResult) returnValue;
			log.info("The private calendar is created: "
					+ result.isCreatePrivateCalendarResult());

		}
	}


}