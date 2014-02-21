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
package ac.hw.askfree;

import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.broker.ICtxBroker;
import org.societies.api.css.devicemgmt.display.IDisplayDriver;
import org.societies.api.css.devicemgmt.display.IDisplayableService;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;
import org.societies.api.osgi.event.CSSEvent;
import org.societies.api.osgi.event.CSSEventConstants;
import org.societies.api.osgi.event.EventListener;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.services.IServices;
import org.societies.api.services.ServiceMgmtEvent;
import org.societies.api.services.ServiceMgmtEventType;



/**
 * Describe your class here...
 *
 * @author Ioannis Mimtsoudis
 *
 */
public class AskFree extends EventListener implements IAskFreeClient, IDisplayableService{

	Logger logging = LoggerFactory.getLogger(this.getClass());

	private IDisplayDriver displayDriverService;
	private IServices serviceMgmt;
	private IEventMgr eventMgr;
	private IAskFreeServerRemote askFreeCommsClient;
	private IIdentity serverIdentity;
	private IIdentity userIdentity;
	private ICtxBroker ctxBroker;
	private ICommManager commManager;
	private Requestor requestor;


	public void init(){

		this.registerForServiceEvents();

		//REGISTER AS A DISPLAYABLE SERVICE
		URL askFreeURL = null;
		try {
			askFreeURL = new URL("http://54.218.113.176/AF-S/screen.php");
		} catch (MalformedURLException e) {
			logging.error("Error making URL", e);
		}

		if(askFreeURL!=null)
		{
			this.displayDriverService.registerDisplayableService(this, "AskFree", askFreeURL, false);
		}

		//REGISTER FOR SYMLOC CHANGES
		ContextEventListener listener = new ContextEventListener(this, this.commManager.getIdManager().getThisNetworkNode(), this.ctxBroker);
		listener.registerForSymLocChanges();
	}
	
	
	/*
	 * Register for events from SLM so I can get my service parameters and finish initialising
	 */
	private void registerForServiceEvents(){
		String eventFilter = "(&" + 
				"(" + CSSEventConstants.EVENT_NAME + "="+ServiceMgmtEventType.SERVICE_STARTED+")" +
				"(" + CSSEventConstants.EVENT_SOURCE + "=org/societies/servicelifecycle)" +
				")";
		this.eventMgr.subscribeInternalEvent(this, new String[]{EventTypes.SERVICE_LIFECYCLE_EVENT}, eventFilter);
		this.logging.debug("Subscribed to "+EventTypes.SERVICE_LIFECYCLE_EVENT+" events");
	}


	private void unregisterForServiceEvents()
	{
		String eventFilter = "(&" + 
				"(" + CSSEventConstants.EVENT_NAME + "="+ServiceMgmtEventType.SERVICE_STARTED+")" +
				"(" + CSSEventConstants.EVENT_SOURCE + "=org/societies/servicelifecycle)" +
				")";

		this.eventMgr.unSubscribeInternalEvent(this, new String[]{EventTypes.SERVICE_LIFECYCLE_EVENT}, eventFilter);
		this.logging.debug("Unsubscribed from "+EventTypes.SERVICE_LIFECYCLE_EVENT+" events");
	}

	
	@Override
	public void handleInternalEvent(InternalEvent event) {
		// This method is called after the bundle has been successfully installed on virgo
		//and we need this to receive the AskFree service identifier (ServiceResourceIdentifier)

		logging.debug("Received internal event: "+event.geteventName());

		logging.debug("Received SLM event");
		ServiceMgmtEvent slmEvent = (ServiceMgmtEvent) event.geteventInfo();
		this.logging.debug("SLM event Bundle Symbol Name" + slmEvent.getBundleSymbolName());
		if (slmEvent.getBundleSymbolName().equalsIgnoreCase("ac.hw.askfree.AskFreeClient")){
			this.logging.debug("Received SLM event for my bundle");
			if (slmEvent.getEventType().equals(ServiceMgmtEventType.SERVICE_STARTED)){

				this.logging.info("AskFree User Client Started!!!");
				this.serverIdentity = this.serviceMgmt.getServer(slmEvent.getServiceId());
				this.unregisterForServiceEvents();
			}
		}
	}

	public void updateUserLocation(String location)
	{
		//SEND MESSAGE TO SERVER
		if(this.serverIdentity!=null)
		{
			askFreeCommsClient.sendLocation(this.serverIdentity, location);
		}
		else
		{
			if(logging.isDebugEnabled()) logging.debug("Opps! Something went wrong, serverID is not set!!!");
		}
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

	/**
	 * @return the userIdentity
	 */
	public IIdentity getUserIdentity() {
		return userIdentity;
	}

	/**
	 * @param userIdentity the userIdentity to set
	 */
	public void setUserIdentity(IIdentity userIdentity) {
		this.userIdentity = userIdentity;
	}


	/* (non-Javadoc)
	 * @see org.societies.api.css.devicemgmt.display.IDisplayableService#serviceStarted(java.lang.String)
	 */
	@Override
	public void serviceStarted(String arg0) {
		logging.info("service askfree started");		
	}

	/* (non-Javadoc)
	 * @see org.societies.api.css.devicemgmt.display.IDisplayableService#serviceStopped(java.lang.String)
	 */
	@Override
	public void serviceStopped(String arg0) {
		// TODO Auto-generated method stub
		logging.info("service askfree stopped");	

	}

	/**
	 * @return the displayDriverService
	 */
	public IDisplayDriver getDisplayDriverService() {
		return displayDriverService;
	}

	/**
	 * @param displayDriverService the displayDriverService to set
	 */
	public void setDisplayDriverService(IDisplayDriver displayDriverService) {
		this.displayDriverService = displayDriverService;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.osgi.event.EventListener#handleExternalEvent(org.societies.api.osgi.event.CSSEvent)
	 */
	@Override
	public void handleExternalEvent(CSSEvent arg0) {
		// TODO Auto-generated method stub

	}

	/**
	 * @return the askFreeCommsClient
	 */
	public IAskFreeServerRemote getAskFreeCommsClient() {
		return askFreeCommsClient;
	}

	/**
	 * @param askFreeCommsClient the askFreeCommsClient to set
	 */
	public void setAskFreeCommsClient(IAskFreeServerRemote askFreeCommsClient) {
		this.askFreeCommsClient = askFreeCommsClient;
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
	}

	/**
	 * @return the ctxBroker
	 */
	public ICtxBroker getCtxBroker() {
		return ctxBroker;
	}

	/**
	 * @param ctxBroker the ctxBroker to set
	 */
	public void setCtxBroker(ICtxBroker ctxBroker) {
		this.ctxBroker = ctxBroker;
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
}
