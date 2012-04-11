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

 package org.societies.thirdpartyservices.networking;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBException;

import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
//import org.societies.comm.xmpp.event.PubsubEvent;
//import org.societies.comm.xmpp.event.PubsubEventFactory;
//import org.societies.comm.xmpp.event.PubsubEventStream;

import org.societies.thirdpartyservices.schema.networking.NetworkingEventBean;

/**
 * 
 * Describe your class here...
 *
 * @author guyf@il.ibm.com
 *
 */
public class NetworkServerEventMgr {

	public static final String SCHEMA = "org.societies.thirdpartyservices.schema.networking";
	
	private static final String PUBSUB_NODE_NAME = "Networking_Events";
	

	private IIdentityManager idManager;
	private ICommManager commManager;
	private PubsubClient pubSubManager;
	
	
	
	public void fireNewUser(NetworkingEventBean netEvent) {
		sendEvent(netEvent);
	}


	
	/*private DmEvent generateEvent(String deviceId, DeviceCommonInfo deviceCommonInfo){
		DmEvent dmEvent = new DmEvent();
		dmEvent.setDeviceId(deviceId);
		dmEvent.setDescription(deviceCommonInfo.getDeviceDescription());
		dmEvent.setType(deviceCommonInfo.getDeviceType());
		return dmEvent;
	}
	*/
	@PostConstruct
	private void init(){
		idManager = commManager.getIdManager();
		try {
			IIdentity pubsubID = idManager.getThisNetworkNode();
			pubSubManager.ownerCreate(pubsubID, PUBSUB_NODE_NAME);
			
			
			//Should this be down here?
			List<String> packageList = new ArrayList<String>();
			packageList.add(SCHEMA);
			pubSubManager.addJaxbPackages(packageList);
			
		} catch (XMPPError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void sendEvent(NetworkingEventBean netEvent){
		try{
			idManager = commManager.getIdManager();
			IIdentity pubsubID = idManager.getThisNetworkNode();
			
			
			String published = pubSubManager.publisherPublish(pubsubID, PUBSUB_NODE_NAME , netEvent.getUserDetails().getDisplayName(), (Object)netEvent);
			
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public ICommManager getCommManager() {
		return commManager;
	}

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}
	
	public PubsubClient getPubSubManager() {
		return pubSubManager;
	}

	public void setPubSubManager(PubsubClient pubSubManager) {
		this.pubSubManager = pubSubManager;
	}

}

