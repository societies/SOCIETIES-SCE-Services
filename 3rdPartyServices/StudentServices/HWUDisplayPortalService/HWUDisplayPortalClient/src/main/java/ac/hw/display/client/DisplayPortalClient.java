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
package ac.hw.display.client;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.broker.ICtxBroker;
import org.societies.api.css.devicemgmt.display.DisplayEvent;
import org.societies.api.css.devicemgmt.display.DisplayEventConstants;
import org.societies.api.css.devicemgmt.display.IDisplayDriver;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.RequestorService;
import org.societies.api.osgi.event.EMSException;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.services.IServices;

import ac.hw.display.server.api.remote.IDisplayPortalServer;

/**
 * Describe your class here...
 *
 * @author Eliza
 *
 */
public class DisplayPortalClient implements IDisplayDriver{

	private ICommManager commManager;
	private IIdentityManager idMgr;
	private IIdentity userIdentity;
	private List<String> screenLocations;
	private IDisplayPortalServer portalServerRemote;
	private IIdentity serverIdentity;
	private ICtxBroker ctxBroker;
	private RequestorService requestor;
	private IServices services;
	private boolean hasSession;
	private IEventMgr evMgr;
	private String currentUsedScreen = "";
	private static Logger LOG = LoggerFactory.getLogger(DisplayPortalClient.class);


	private UserSession userSession;
	
	public DisplayPortalClient(){
		this.screenLocations = new ArrayList<String>();
	}
	
	//this method should be called after the bundle has started so it should be called by the OsgiContextListener when the event has been received
	public void Init(){
		JOptionPane.showMessageDialog(null, "Initialising DisplayPortalClient");
		//ServiceResourceIdentifier serviceId = getServices().getMyServiceId(this.getClass());
		
			this.requestServerIdentityFromUser();
			ServiceResourceIdentifier serviceId = this.portalServerRemote.getServerServiceId(serverIdentity);
		
		//this.serverIdentity = this.services.getServer(serviceId);
		UIManager.put("ClassLoader", ClassLoader.getSystemClassLoader());

		this.requestor = new RequestorService(serverIdentity, serviceId);
		ContextEventListener ctxEvListener = new ContextEventListener(this, getCtxBroker(), serverIdentity, requestor);
		String[] locs = this.portalServerRemote.getScreenLocations(serverIdentity);
		
		for (int i=0; i<locs.length; i++){
			this.screenLocations.add(locs[i]);
		}
		
		userSession = new UserSession(this.userIdentity.getJid());
		
		JOptionPane.showMessageDialog(null, "DisplayPortalClient initialised");
		//return true;
	}


	private void requestServerIdentityFromUser(){
		if (this.serverIdentity==null){
			String serverIdentityStr = JOptionPane.showInputDialog("Please enter the JID of the CSS hosting the server application", "xcmanager.societies.local");
			
			try {
				this.serverIdentity = this.idMgr.fromJid(serverIdentityStr);
				
			} catch (InvalidFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public void updateUserLocation(String location){
		//if near a screen
		if (this.screenLocations.contains(location)){
			this.LOG.debug("Requesting access");
			//request access
			String reply = this.portalServerRemote.requestAccess(serverIdentity, userIdentity.getJid(), location);
			//if access refused do nothing
			if (reply=="REFUSED"){
				this.LOG.debug("Refused access to screen.");
			}
			else //if access is granted 
			{
				this.LOG.debug("Access to screen granted. IP Address is: "+reply);
				//check if the user is already using another screen
				if (this.hasSession){
					this.LOG.debug("Releasing previous screen session");
					//release currently used screen
					this.portalServerRemote.releaseResource(serverIdentity, userIdentity.getJid(), currentUsedScreen);
				}
				//now setup new screen
				SocketClient socketClient = new SocketClient(reply);
				
				socketClient.startSession(userSession);
				//TODO: send services TO DISPLAY
				this.currentUsedScreen = location;
				this.hasSession = true;
				DisplayEvent dEvent = new DisplayEvent(DisplayEventConstants.DEVICE_AVAILABLE);
				InternalEvent iEvent = new InternalEvent(EventTypes.DISPLAY_EVENT, "displayUpdate", "org/societies/css/device", dEvent);
				try {
					this.evMgr.publishInternalEvent(iEvent);
				} catch (EMSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}//user is not near a screen
		else{
			this.LOG.debug("User not near screen");
			//if he's using a screen
			if (this.hasSession){
				//release resource
				this.portalServerRemote.releaseResource(serverIdentity, userIdentity.getJid(), currentUsedScreen);
				this.currentUsedScreen = "";
				this.hasSession = false;
				DisplayEvent dEvent = new DisplayEvent(DisplayEventConstants.DEVICE_UNAVAILABLE);
				InternalEvent iEvent = new InternalEvent(EventTypes.DISPLAY_EVENT, "displayUpdate", "org/societies/css/device", dEvent);
				try {
					this.evMgr.publishInternalEvent(iEvent);
				} catch (EMSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	
	@Override
	public void displayImage(String serviceName, String pathToFile){
		if (this.hasSession){
			
				BinaryDataTransfer dataTransfer = new BinaryDataTransfer(currentUsedScreen);
				dataTransfer.sendImage(this.userIdentity.getJid(), pathToFile);
				
			
		}
		
	}

	@Override
	public void displayImage(String serviceName, URL remoteImageLocation){
		if (this.hasSession){
			
				SocketClient socketClient = new SocketClient(currentUsedScreen);
				
				socketClient.sendImage(userSession, remoteImageLocation);
				
			
		}
		
	}


	@Override
	public void sendNotification(String serviceName, String text){
		if (this.hasSession){
			if (this.userSession.containsService(serviceName)){
				SocketClient socketClient = new SocketClient(currentUsedScreen);
				socketClient.sendText(serviceName, userSession, text);
				
				
			}
		}
		
	}

	@Override
	public void registerDisplayableService(ServiceResourceIdentifier serviceID, String serviceName, URL executableLocation){
		ServiceInfo sInfo  = new ServiceInfo(serviceName, executableLocation.toString());
		this.userSession.addService(sInfo);
	}
	
	/*
	 * get/set methods
	 */
	
	
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
		this.idMgr = commManager.getIdManager();
		this.userIdentity = idMgr.getThisNetworkNode();
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
	 * @return the services
	 */
	public IServices getServices() {
		return services;
	}

	/**
	 * @param services the services to set
	 */
	public void setServices(IServices services) {
		this.services = services;
	}

	/**
	 * @return the portalServerRemote
	 */
	public IDisplayPortalServer getPortalServerRemote() {
		return portalServerRemote;
	}

	/**
	 * @param portalServerRemote the portalServerRemote to set
	 */
	public void setPortalServerRemote(IDisplayPortalServer portalServerRemote) {
		this.portalServerRemote = portalServerRemote;
	}

	/**
	 * @return the evMgr
	 */
	public IEventMgr getEvMgr() {
		return evMgr;
	}

	/**
	 * @param evMgr the evMgr to set
	 */
	public void setEvMgr(IEventMgr evMgr) {
		this.evMgr = evMgr;
	}



}
