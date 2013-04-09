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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.UnavailableException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.ext3p.schema.sharedcalendar.SharedCalendarResult;
import org.societies.thirdparty.sharedCalendar.api.ICalendarResultCallback;;


/**
 * Describe your class here...
 *
 * @author solutanet
 *
 */
public class SharedCalendarCallBack implements ICommCallback{
	private static final List<String> NAMESPACES = Collections.unmodifiableList(
			Arrays.asList("http://societies.org/api/ext3p/schema/sharedCalendar"));
	private static final List<String> PACKAGES = Collections.unmodifiableList(
			Arrays.asList("org.societies.api.ext3p.schema.sharedcalendar"));
	private static final Logger log = LoggerFactory.getLogger(SharedCalendarCallBack.class);
	
	//MAP TO STORE THE ALL THE CLIENT CONNECTIONS
	private final Map<String, ICalendarResultCallback> calendarClients = new HashMap<String, ICalendarResultCallback>();
	
	/** Constructor for callback
	* @param clientID unique ID of send request to comms framework
	* @param calcClient callback from originating client
	*/
	public SharedCalendarCallBack(String clientID, ICalendarResultCallback calendarClient) {
		//STORE THIS CALLBACK WITH THIS REQUEST ID
		calendarClients.put(clientID, calendarClient);
		if(log.isDebugEnabled())
			log.debug("Insert new callback entry in the map");
	}
		
	/**Returns the correct calendar client callback for this request 
	* @param requestID the id of the initiating request
	* @return
	* @throws UnavailableException
	*/
	private ICalendarResultCallback getRequestingClient(String requestID) {
		
		ICalendarResultCallback requestingClient = (ICalendarResultCallback) calendarClients.get(requestID);
		calendarClients.remove(requestID);
		
		if(log.isDebugEnabled())
			log.debug("Requested client is retrieved: "+(requestingClient==null?false:true)+".");
		
		return requestingClient;
	}

	@Override
	public List<String> getXMLNamespaces() {
		// TODO Auto-generated method stub
		return NAMESPACES;
	}

	@Override
	public List<String> getJavaPackages() {
		// TODO Auto-generated method stub
		return PACKAGES;
	}


	@Override
	public void receiveResult(Stanza stanza, Object payload) {
		if (payload.getClass().equals(SharedCalendarResult.class)) {
			
			if(log.isDebugEnabled())
				log.debug("receivedResult, it's a SharedCalendarResult!");
			
			SharedCalendarResult calendarResult = (SharedCalendarResult) payload;
			
			ICalendarResultCallback calendarClient = getRequestingClient(stanza.getId());
			calendarClient.receiveResult(calendarResult);	
		}
		
	}

	@Override
	public void receiveError(Stanza stanza, XMPPError error) {
		// TODO Auto-generated method stub
		log.debug("Receive Error");
		
	}

	@Override
	public void receiveInfo(Stanza stanza, String node, XMPPInfo info) {
		if(log.isDebugEnabled())
			log.debug("Receive Info");
	}

	@Override
	public void receiveItems(Stanza stanza, String node, List<String> items) {
		if(log.isDebugEnabled())
			log.debug("Receive Items");
	}

	@Override
	public void receiveMessage(Stanza stanza, Object payload) {
		if(log.isDebugEnabled())
			log.debug("Receive Message");
	}

}
