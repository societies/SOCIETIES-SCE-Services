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
package org.societies.thirdpartyservices.crowdtasking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.broker.ICtxBroker;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorService;
import org.societies.api.osgi.event.CSSEvent;
import org.societies.api.osgi.event.CSSEventConstants;
import org.societies.api.osgi.event.EventListener;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.services.IServices;
import org.societies.api.services.ServiceMgmtEvent;
import org.societies.api.services.ServiceMgmtEventType;
import org.societies.thirdpartyservices.crowdtasking.tools.SocketServer;


/**
 * Describe your class here...
 *
 * @author Simon Jureša
 *
 */
public class CrowdTasking extends EventListener implements ICrowdTasking{

	Logger logging = LoggerFactory.getLogger(this.getClass());
	private ServiceResourceIdentifier myServiceID;
	private IIdentity serverIdentity;
	private Requestor requestor;
	private IIdentityManager idMgr;
	private IServices serviceMgmt;
	private IEventMgr eventMgr;
	private ICtxBroker ctxBroker;
	private ICommManager commManager;

	public void init(){
		this.logging.info("init CrowdTaskingClient:");
		this.registerForServiceEvents();
	}
	
	/*
	 * Register for events from SLM so I can get my service parameters and finish initialising
	 */
	private void registerForServiceEvents(){
		String eventFilter = "(&" + 
				"(" + CSSEventConstants.EVENT_NAME + "="+ServiceMgmtEventType.NEW_SERVICE+")" +
				"(" + CSSEventConstants.EVENT_SOURCE + "=org/societies/servicelifecycle)" +
				")";
		this.eventMgr.subscribeInternalEvent(this, new String[]{EventTypes.SERVICE_LIFECYCLE_EVENT}, eventFilter);
		this.logging.debug("Subscribed to "+EventTypes.SERVICE_LIFECYCLE_EVENT+" events");
	}
	
	/* (non-Javadoc)
	 * @see org.societies.api.osgi.event.EventListener#handleInternalEvent(org.societies.api.osgi.event.InternalEvent)
	 */
	@Override
	public void handleInternalEvent(InternalEvent event) {
		// This method is called after the bundle has been successfully installed on virgo
		//and we need this to receive the AskFree service identifier (ServiceResourceIdentifier)

		logging.debug("CrowdTaskingClient Received internal event: "+event.geteventName());

		if(event.geteventName().equalsIgnoreCase("NEW_SERVICE")){
			logging.info("Received SLM event");
			ServiceMgmtEvent slmEvent = (ServiceMgmtEvent) event.geteventInfo();
			this.logging.info("SLM event Bundle Symbol Name: " + slmEvent.getBundleSymbolName());
			if (slmEvent.getBundleSymbolName().equalsIgnoreCase("org.societies.thirdpartyservices.crowdtasking.CrowdTaskingClient")){
				this.logging.info("Received SLM event for my bundle");
				if (slmEvent.getEventType().equals(ServiceMgmtEventType.NEW_SERVICE)){

					setMyServiceID(slmEvent.getServiceId());
					this.logging.debug("1. Service id:" + slmEvent.getServiceId().toString());
					//GET ID OF SERVER
					this.setServerIdentity(this.idMgr.getThisNetworkNode());
					//this.setServerIdentity(serviceMgmt.getServer(slmEvent.getServiceId()));
					logging.debug("2. Servers identity: " + getServerIdentity());

					//SocketServer server = new SocketServer(getMyServiceID(), getServerIdentity());
					SocketServer server = new SocketServer(this);
					new Thread(server).start();
					/////////////////////////////////////////////////////////////////////////////
					this.requestor = new RequestorService(serverIdentity, myServiceID);
					logging.debug("3. Requestor service: " + getRequestor().toString());
					logging.debug("3a. Requestor service: " + getRequestor().getRequestorId().toString());
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.societies.api.osgi.event.EventListener#handleExternalEvent(org.societies.api.osgi.event.CSSEvent)
	 */
	@Override
	public void handleExternalEvent(CSSEvent arg0) {
		// TODO Auto-generated method stub

	}

	/**
	 * @param myServiceID the myServiceID to set
	 */
	public void setMyServiceID(ServiceResourceIdentifier myServiceID) {
		this.myServiceID = myServiceID;
		this.logging.debug("MyServiceId is set");
	}

	
	public ServiceResourceIdentifier getMyServiceID() {
		return myServiceID;
	}

	/**
	 * @return the serverIdentity
	 */
	public IIdentity getServerIdentity() {
		this.logging.debug("Get ServerId:" +serverIdentity.toString());
		return serverIdentity;
	}

	/**
	 * @param serverIdentity the serverIdentity to set
	 */
	public void setServerIdentity(IIdentity serverIdentity) {
		this.logging.debug("ServerId is set");
		this.serverIdentity = serverIdentity;
	}

	/**
	 * @return the serviceMgmt
	 */
	public IServices getServiceMgmt() {
		return serviceMgmt;
	}

	/**
	 * @param serviceMgmt the serviceMgmt to set
	 */
	public void setServiceMgmt(IServices serviceMgmt) {
		this.serviceMgmt = serviceMgmt;
	}

	/**
	 * @return the eventMgr
	 */
	public IEventMgr getEventMgr() {
		return eventMgr;
	}

	/**
	 * @param eventMgr the eventMgr to set
	 */
	public void setEventMgr(IEventMgr eventMgr) {
		this.eventMgr = eventMgr;
	}

	/**
	 * @return the commManager
	 */
	public ICommManager getCommManager() {
		return commManager;
	}

	/**
	 * @param commManager the commManager to set
	 */
	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
		this.idMgr = this.commManager.getIdManager();
	}

	/**
	 * @return the ctxBroker
	 */
	public ICtxBroker getCtxBroker() {
		this.logging.debug("get ContextBroker");
		return ctxBroker;
	}

	/**
	 * @param ctxBroker the ctxBroker to set
	 */
	public void setCtxBroker(ICtxBroker ctxBroker) {
		this.ctxBroker = ctxBroker;
	}

	/**
	 * @return the requestor
	 */
	public Requestor getRequestor() {
		this.logging.debug("get Requestor");
		return requestor;
	}

	/**
	 * @param requestor the requestor to set
	 */
	public void setRequestor(Requestor requestor) {
		this.requestor = requestor;
	}
}
