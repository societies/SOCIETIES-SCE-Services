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

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.broker.ICtxBroker;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.RequestorService;
import org.societies.api.internal.servicelifecycle.IServiceDiscovery;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.services.IServices;

import ac.hw.display.client.api.IDisplayPortalClient;
import ac.hw.display.server.api.remote.IDisplayPortalServer;

/**
 * Describe your class here...
 *
 * @author Eliza
 *
 */
public class DisplayPortalClient implements IDisplayPortalClient{

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
	
	private String currentUsedScreen = "";
	
	private IServiceDiscovery serviceDiscovery;
	
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
			//request access
			String reply = this.portalServerRemote.requestAccess(serverIdentity, userIdentity.getJid(), location);
			//if access refused do nothing
			if (reply=="REFUSED"){
				System.out.println("Refused access to screen.");
			}
			else //if access is granted 
			{
				//check if the user is already using another screen
				if (this.hasSession){
					//release currently used screen
					this.portalServerRemote.releaseResource(serverIdentity, userIdentity.getJid(), currentUsedScreen);
				}
				//now setup new screen
				SocketClient socketClient = new SocketClient(reply);
				UserSession userSession = new UserSession(this.userIdentity.getJid());
		
				//TODO: send services TO DISPLAY
				this.currentUsedScreen = location;
				this.hasSession = true;
			}
		}
		//user is not near a screen
		else{
			//if he's using a screen
			if (this.hasSession){
				//release resource
				this.portalServerRemote.releaseResource(serverIdentity, userIdentity.getJid(), currentUsedScreen);
				this.currentUsedScreen = "";
				this.hasSession = false;
			}
		}
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
	 * @return the serviceDiscovery
	 */
	public IServiceDiscovery getServiceDiscovery() {
		return serviceDiscovery;
	}

	/**
	 * @param serviceDiscovery the serviceDiscovery to set
	 */
	public void setServiceDiscovery(IServiceDiscovery serviceDiscovery) {
		this.serviceDiscovery = serviceDiscovery;
	}
}
