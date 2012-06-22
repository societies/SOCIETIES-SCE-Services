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
package ac.hw.rfid.client;

import java.util.Enumeration;

import javax.swing.UIDefaults;
import javax.swing.UIManager;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.util.tracker.ServiceTracker;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.interfaces.IFeatureServer;
import org.societies.api.context.broker.ICtxBroker;
import org.societies.api.context.source.ICtxSourceMgr;

import ac.hw.rfid.client.api.IRfidClient;
import ac.hw.rfid.client.comm.CommsClient;
import ac.hw.rfid.client.comm.CommsServer;
import ac.hw.rfid.server.api.remote.IRfidServer;

/**
 * Describe your class here...
 *
 * @author Eliza
 *
 */
public class Activator implements BundleActivator{

	@Override
	public void start(BundleContext context) throws Exception {
		
		UIManager.put("ClassLoader", ClassLoader.getSystemClassLoader());
		UIDefaults uidefs = UIManager.getDefaults();
		Enumeration e = uidefs.keys();
		while(e.hasMoreElements()){
			Object key = e.nextElement();
			System.out.println("Found: "+key.toString()+" -> "+uidefs.getInt(key));
		}
		ServiceTracker commManagerTracker = new ServiceTracker(context, ICommManager.class.getName(), null);
		commManagerTracker.open();
		ICommManager commManager = (ICommManager) commManagerTracker.getService();
		
		ServiceTracker ctxSourceMgrTracker = new ServiceTracker(context, ICtxSourceMgr.class.getName(), null);
		ctxSourceMgrTracker.open();
		ICtxSourceMgr ctxSourceMgr = (ICtxSourceMgr) ctxSourceMgrTracker.getService();
		
		ServiceTracker ctxBrokerTracker = new ServiceTracker(context, ICtxBroker.class.getName(), null);
		ctxBrokerTracker.open();
		ICtxBroker ctxBroker = (ICtxBroker) ctxBrokerTracker.getService();

		CommsClient rfidServerRemote = new CommsClient();
		rfidServerRemote.setCommManager(commManager);
		rfidServerRemote.InitService();
		
		context.registerService(new String[]{IRfidServer.class.getName()}, rfidServerRemote, null);
		RfidClient rfidClient = new RfidClient();
		
		
		rfidClient.setCommManager(commManager);
		rfidClient.setCtxSourceMgr(ctxSourceMgr);
		rfidClient.setRfidServerRemote(rfidServerRemote);
		rfidClient.setBroker(ctxBroker);
		rfidClient.initialiseRFIDClient();
		
		context.registerService(new String[]{IRfidClient.class.getName()}, rfidClient, null);
		
		CommsServer commsServer = new CommsServer();
		commsServer.setCommManager(commManager);
		commsServer.InitService();
		commsServer.setRfidClient(rfidClient);
		
		context.registerService(new String[]{IFeatureServer.class.getName()}, commsServer, null);
		
		
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
