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
package ac.hw.rfid.server;

import org.societies.api.osgi.event.EventListener;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.interfaces.IFeatureServer;
import org.societies.api.css.devicemgmt.rfid.IRfidDriver;
import org.societies.api.osgi.event.IEventMgr;

import ac.hw.rfid.client.api.remote.IRfidClient;
import ac.hw.rfid.server.api.IRfidServer;
import ac.hw.rfid.server.comm.CommsClient;
import ac.hw.rfid.server.comm.CommsServer;

/**
 * Describe your class here...
 *
 * @author Eliza
 *
 */
public class Activator implements BundleActivator{

	@Override
	public void start(BundleContext context) throws Exception {

		ServiceTracker rfidDriverTracker = new ServiceTracker(context, IRfidDriver.class.getName(), null);
		rfidDriverTracker.open();
		IRfidDriver driver = (IRfidDriver) rfidDriverTracker.getService();
		
		ServiceTracker eventMgrTracker = new ServiceTracker(context, IEventMgr.class.getName(), null);
		eventMgrTracker.open();
		IEventMgr evMgr = (IEventMgr) eventMgrTracker.getService();

		ServiceTracker commManagerTracker = new ServiceTracker(context, ICommManager.class.getName(), null);
		commManagerTracker.open();
		ICommManager commManager = (ICommManager) commManagerTracker.getService();
		
		
		CommsClient rfidClientRemote = new CommsClient();
		rfidClientRemote.setCommManager(commManager);
		rfidClientRemote.InitService();
		context.registerService(new String[]{ICommCallback.class.getName(),IRfidClient.class.getName()}, rfidClientRemote, null);
		
		
		RfidServer rfidServer = new RfidServer();
		rfidServer.setRfidDriver(driver);
		rfidServer.setEventMgr(evMgr);
		rfidServer.initialiseRFIDServer();
		rfidServer.setRfidClientRemote(rfidClientRemote);
		String[] classes = new String[]{IRfidServer.class.getName(), EventListener.class.getName()};
		context.registerService(classes, rfidServer, null);
		
		
		CommsServer commsServer = new CommsServer();
		commsServer.setCommManager(commManager);
		commsServer.setRfidServer(rfidServer);
		commsServer.InitService();
		context.registerService(IFeatureServer.class.getName(), commsServer, null);
		
		
		
		
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
