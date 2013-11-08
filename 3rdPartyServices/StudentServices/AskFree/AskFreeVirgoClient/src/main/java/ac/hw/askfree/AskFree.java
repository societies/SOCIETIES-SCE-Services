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
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.broker.ICtxBroker;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxEntityTypes;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
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

import ac.hw.askfree.tools.ClientHandler;
//import ac.hw.askfree.tools.ContextEventListener;
import ac.hw.askfree.tools.SocketServer;


/**
 * Describe your class here...
 *
 * @author Ioannis Mimtsoudis
 *
 */
public class AskFree extends EventListener implements IAskFree{

	private IServices serviceMgmt;
	private IEventMgr eventMgr;
	Logger logging = LoggerFactory.getLogger(this.getClass());
	private ServiceResourceIdentifier myServiceID;
	private IIdentity serverIdentity;
	private IIdentity userIdentity;
	private ICtxBroker ctxBroker;
	private HashMap<String, String> userToLocation;
	private HashMap<String, ClientHandler> userToHandler;
	private Requestor requestor;
	private ICommManager commManager;
	private IIdentityManager idMgr;
	private String symbolicLocation;

	public void init(){
		//SET UP NEW MAP FOR USER -> LOCATION
		userToLocation = new HashMap<String, String>();

		//SET UP NEW MAP FOR USER -> HANDLER
		userToHandler = new HashMap<String, ClientHandler>();

		this.registerForServiceEvents();

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

	/* (non-Javadoc)
	 * @see org.societies.api.osgi.event.EventListener#handleInternalEvent(org.societies.api.osgi.event.InternalEvent)
	 */
	@Override
	public void handleInternalEvent(InternalEvent event) {
		// This method is called after the bundle has been successfully installed on virgo
		//and we need this to receive the AskFree service identifier (ServiceResourceIdentifier)

		logging.debug("Received internal event: "+event.geteventName());
		logging.debug("Received SLM event");
		ServiceMgmtEvent slmEvent = (ServiceMgmtEvent) event.geteventInfo();
		this.logging.debug("SLM event Bundle Symbol Name" + slmEvent.getBundleSymbolName());
		if (slmEvent.getBundleSymbolName().equalsIgnoreCase("ac.hw.askfree.AskFreeServer")){
			this.logging.debug("Received SLM event for my bundle");
			if (slmEvent.getEventType().equals(ServiceMgmtEventType.SERVICE_STARTED)){

				logging.info("AskFree Virgo Server STARTED!!!");
				
				setMyServiceID(slmEvent.getServiceId());
				this.logging.info("1.Service id:" + slmEvent.getServiceId().toString());

				//GET ID OF SERVER
				this.setServerIdentity(this.idMgr.getThisNetworkNode());
				logging.info("2.Server Identity: " + getServerIdentity());

				SocketServer server = new SocketServer(this);
				new Thread(server).start();
				this.requestor = new RequestorService(serverIdentity, myServiceID);
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

	public void updateUserLocation(String userIdentity, ClientHandler cHandler, String location){
		this.logging.debug("location of user: "+userIdentity+" is: "+location + " cHandler: " + cHandler);
		this.setSymbolicLocation(userIdentity, location);

		//send the location of the user to the appropriate android client (based on userIdentity)
		cHandler.sendMessage(this.getSymbolicLocation(userIdentity));
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
	 * @return the myServiceID
	 */
	public ServiceResourceIdentifier getMyServiceID() {
		this.logging.debug("Get MyServiceId:" +myServiceID.toString());
		return myServiceID;
	}

	/**
	 * @param myServiceID the myServiceID to set
	 */
	public void setMyServiceID(ServiceResourceIdentifier myServiceID) {
		this.myServiceID = myServiceID;
		this.logging.debug("MyServiceId is set");
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

	/**
	 * @return the symbolicLocation
	 */
	public String getSymbolicLocation(String userIdentity) {
		//this.logging.debug("Get symbolic location: "+symbolicLocation+" for user: " + userIdentity);
		return userToLocation.get(userIdentity);
	}

	/**
	 * @param symbolicLocation the symbolicLocation to set
	 */
	public void setSymbolicLocation(String userIdentity, String symbolicLocation) {
		userToLocation.put(userIdentity, symbolicLocation);
		this.logging.debug("Set symbolic location: "+symbolicLocation+" for user: " + userIdentity);
	}

	/* (non-Javadoc)
	 * @see ac.hw.askfree.IAskFree#setUserLocation(java.lang.String, java.lang.String)
	 */
	@Override
	public void setUserLocation(String userJID, String location) {
		synchronized (userToLocation)
		{
			userToLocation.put(userJID, location);
		}
	}

	public void addHandler(String cssID, ClientHandler clientHandler)
	{
		this.userToHandler.put(cssID, clientHandler);
	}

	/* (non-Javadoc)
	 * @see ac.hw.askfree.IAskFree#pushToAndroid(java.lang.String)
	 */
	@Override
	public void pushToAndroid(String userJID) {
		//need sync blocks here?!
		ClientHandler handler = this.userToHandler.get(userJID);
		if(handler!=null)
		{
			String location = this.userToLocation.get(userJID);
			if(location!=null)
			{
				//SEND MESSSAGE
				handler.sendMessage(location);
			}
			else
			{
				//Perhaps send a error message to Android in future?
				logging.debug("Opps, location is null! Not sending...");
			}
		}
		else
		{
			logging.debug("Opps, handler is null! Cant send message to Android...");
		}

	}

}
