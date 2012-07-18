/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFOD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
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
package ac.hw.display.server;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.services.IServices;


import ac.hw.display.server.api.IDisplayPortalServer;
import ac.hw.display.server.comm.CommsServer;
import ac.hw.display.server.gui.ScreenConfigurationDialog;
import ac.hw.display.server.model.ScreenConfiguration;
/**
 * Describe your class here...
 *
 * @author Eliza
 *
 */
public class DisplayPortalServer implements IDisplayPortalServer{

	
	
	List<String> screenIPAddresses;

	private static Logger LOG = LoggerFactory.getLogger(CommsServer.class);


	private Hashtable<String, String> currentlyUsedScreens;
	
	private ScreenConfiguration screenconfig;

	private IServices services;


	private ServiceResourceIdentifier myServiceId;
	
	public DisplayPortalServer(){
		screenIPAddresses = new ArrayList<String>();
		
	}
	
	public void initialiseServer(){
		UIManager.put("ClassLoader", ClassLoader.getSystemClassLoader());
		
		this.getScreenConfigurationFromUser();
	}
	
	private void getScreenConfigurationFromUser() {
		ScreenConfigurationDialog dialog = new ScreenConfigurationDialog();
	
		screenconfig = dialog.getScreens();
		if (dialog!=null){
			dialog.dispose();
		}
		this.LOG.debug(screenconfig.toString());
		this.LOG.debug("initialised");
	}

	

	
	@Override
	public String requestAccess(String identity, String location) {
		if (this.currentlyUsedScreens.containsKey(location)){
			return "REFUSED";
		}else{
			return this.screenconfig.getScreenBasedOnLocation(location).getIpAddress();
		}
	}



	@Override
	public void releaseResource(String identity, String location) {
		if (this.currentlyUsedScreens.containsKey(location)){
			String currentUserId = this.currentlyUsedScreens.get(location);
			if (identity.startsWith(currentUserId) || (currentUserId.startsWith(identity))){
				this.currentlyUsedScreens.remove(location);
			}
		}
		
	}

	@Override
	public String[] getScreenLocations() {
		return this.screenconfig.getLocations();
	}
	
	public static void main(String[] args){
		DisplayPortalServer server = new DisplayPortalServer();
		server.initialiseServer();
		
	}

	@Override
	public ServiceResourceIdentifier getServerServiceId() {
		if (this.myServiceId==null){
			this.myServiceId = this.getServices().getMyServiceId(this.getClass());
			
		}
		return this.myServiceId;
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


}
