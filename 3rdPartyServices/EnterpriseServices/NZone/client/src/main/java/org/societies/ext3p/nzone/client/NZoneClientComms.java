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
package org.societies.ext3p.nzone.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.comm.xmpp.interfaces.ICommManager;


import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;

import org.societies.api.ext3p.schema.nzone.Method;
import org.societies.api.ext3p.schema.nzone.NzoneBean;
import org.societies.api.ext3p.schema.nzone.NzoneBeanResult;
import org.societies.api.ext3p.schema.nzone.ZoneDetails;
import org.societies.api.ext3p.schema.nzone.UserDetails;

/**
 * Comms Client that initiates the remote communication for the networking zone
 * 
 * @author Maria Mannion
 * 
 */
public class NZoneClientComms implements ICommCallback {
	private static final List<String> NAMESPACES = Collections
			.unmodifiableList(Arrays
					.asList("http://societies.org/api/ext3p/schema/nzone"));
	private static final List<String> PACKAGES = Collections
			.unmodifiableList(Arrays
					.asList("org.societies.api.ext3p.schema.nzone"));

	//TODO : Temporary while testing
	private int TEST_TIME_MULTIPLER = 10;
	private int WAIT_TIME_SECS = 100;
	
	// PRIVATE VARIABLES
	private ICommManager commManager;
	private static Logger LOG = LoggerFactory.getLogger(NZoneClientComms.class);
	private NzoneBeanResult commsResult;
	CountDownLatch startSignal;


	
	private String netServerID;
	
	// PROPERTIES
	public ICommManager getCommManager() {
		return commManager;
	}

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}

	public NZoneClientComms() {
		netServerID = new String("networking.societies.local");
	}
	
	public NZoneClientComms(String networkingserver) {
		this.netServerID = networkingserver;
	}

	public void InitService() {
		// Registry Netowkring Client with the Comms Manager

		if (LOG.isDebugEnabled())
			LOG.debug("Registering the Networking Client with the XMPP Communication Manager");

		try {
			getCommManager().register(this);
		} catch (CommunicationException e) {
			e.printStackTrace();
		}
	
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.api.comm.xmpp.interfaces.ICommCallback#getJavaPackages()
	 */
	@Override
	public List<String> getJavaPackages() {
		return PACKAGES;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.api.comm.xmpp.interfaces.ICommCallback#getXMLNamespaces()
	 */
	@Override
	public List<String> getXMLNamespaces() {
		return NAMESPACES;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveError(org
	 * .societies.api.comm.xmpp.datatypes.Stanza,
	 * org.societies.api.comm.xmpp.exceptions.XMPPError)
	 */
	@Override
	public void receiveError(Stanza arg0, XMPPError arg1) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveInfo(org.
	 * societies.api.comm.xmpp.datatypes.Stanza, java.lang.String,
	 * org.societies.api.comm.xmpp.datatypes.XMPPInfo)
	 */
	@Override
	public void receiveInfo(Stanza arg0, String arg1, XMPPInfo arg2) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveItems(org
	 * .societies.api.comm.xmpp.datatypes.Stanza, java.lang.String,
	 * java.util.List)
	 */
	@Override
	public void receiveItems(Stanza arg0, String arg1, List<String> arg2) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveMessage(org
	 * .societies.api.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public void receiveMessage(Stanza arg0, Object arg1) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveResult(org
	 * .societies.api.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public void receiveResult(Stanza arg0, Object arg1) {
		// TODO Auto-generated method stub
		if (arg1 instanceof NzoneBeanResult){
			commsResult = (NzoneBeanResult) arg1;
			startSignal.countDown();
		}
	}

	
	
	public String getMainZoneCisID() {
		// We want to sent all messages for Netowrking Client to the metworking server
		// hardcode for now TODO : Read from properties
		IIdentity toIdentity = null;
		commsResult = null;
		try {
			toIdentity = getCommManager().getIdManager().fromJid(netServerID);
		} catch (InvalidFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Stanza stanza = new Stanza(toIdentity);

		// CREATE MESSAGE BEAN
		NzoneBean netBean = new NzoneBean();
		netBean.setMethod(Method.GET_MAIN_ZONE);
		
		startSignal = new CountDownLatch(1);

		try {
			getCommManager().sendIQGet(stanza, netBean, this);
			
		} catch (CommunicationException e) {
			LOG.warn(e.getMessage());
		}
		;

		try {
			startSignal.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (commsResult != null)
		{
			return this.commsResult.getData().get(0);
		}
		
		return null;
	}

	public List<String> getZoneCisID() {
		// We want to sent all messages for Netowrking Client to the metworking server
		// hardcode for now TODO : Read from properties
		IIdentity toIdentity = null;
		commsResult = null;
		startSignal = new CountDownLatch(1);
		
		try {
			toIdentity = getCommManager().getIdManager().fromJid(netServerID);
		} catch (InvalidFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Stanza stanza = new Stanza(toIdentity);

		// CREATE MESSAGE BEAN
		NzoneBean netBean = new NzoneBean();
		netBean.setMethod(Method.GET_ZONE_LIST);

		try {
			getCommManager().sendIQGet(stanza, netBean, this);
				
		} catch (CommunicationException e) {
			LOG.warn(e.getMessage());
		};

		try {
			startSignal.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		if (commsResult != null)
			return this.commsResult.getData();
		
		return null;
	}

	
	


	
	
	public List<ZoneDetails> getZoneDetails() {

		IIdentity toIdentity = null;
		commsResult = null;
		try {
			toIdentity = getCommManager().getIdManager().fromJid(netServerID);
		} catch (InvalidFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Stanza stanza = new Stanza(toIdentity);
								
		// CREATE MESSAGE BEAN
		NzoneBean netBean = new NzoneBean();
		netBean.setMethod(Method.GET_ZONE_DETAILS);
		startSignal = new CountDownLatch(1);
	
		try {
			getCommManager().sendIQGet(stanza, netBean, this);
		} catch (CommunicationException e) {
			LOG.warn(e.getMessage());
		};

		try {
			startSignal.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
										
						
		if (commsResult != null)
			return this.commsResult.getZonedata();
						
		return null;
	}
	
	
	public UserDetails getUserDetails(String userID)
	{
		
		// We want to sent all messages for Netowrking Client to the metworking server
		// hardcode for now TODO : Read from properties
		IIdentity toIdentity = null;
		commsResult = null;
				
		try {
			toIdentity = getCommManager().getIdManager().fromJid(netServerID);
		} catch (InvalidFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Stanza stanza = new Stanza(toIdentity);
		List<String> userLst = new ArrayList<String>();
		userLst.add(userID);
		// CREATE MESSAGE BEAN
		NzoneBean netBean = new NzoneBean();
		netBean.setMethod(Method.GET_USER_DETAILS);
		netBean.setData(userLst);
		startSignal = new CountDownLatch(1);
				
		try {
			getCommManager().sendIQGet(stanza, netBean, this);
						
		} catch (CommunicationException e) {
			LOG.warn(e.getMessage());
		};

		try {
			startSignal.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
						
		
		if ((commsResult != null) && (this.commsResult.getUserdata() != null))
			return this.commsResult.getUserdata().get(0);
		
		return null;
		
	}
	
	
	public List<UserDetails> getUserDetailsList(List<String> userIDs)
	{
		
		// We want to sent all messages for Netowrking Client to the metworking server
		// hardcode for now TODO : Read from properties
		IIdentity toIdentity = null;
		commsResult = null;
		try {
			toIdentity = getCommManager().getIdManager().fromJid(netServerID);
		} catch (InvalidFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Stanza stanza = new Stanza(toIdentity);
				
		// CREATE MESSAGE BEAN
		NzoneBean netBean = new NzoneBean();
		netBean.setMethod(Method.GET_USER_DETAILS);
		netBean.setData(userIDs);
		startSignal = new CountDownLatch(1);
				
		try {
			getCommManager().sendIQGet(stanza, netBean, this);
						
		} catch (CommunicationException e) {
			LOG.warn(e.getMessage());
		};

		try {
			startSignal.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
						
		
		if (commsResult != null)
			return this.commsResult.getUserdata();
		
		return null;
		
	}
	
	public boolean savePreferences(String preferences)
	{
		
		// We want to sent all messages for Netowrking Client to the metworking server
		// hardcode for now TODO : Read from properties
		IIdentity toIdentity = null;
		commsResult = null;
		
		try {
			toIdentity = getCommManager().getIdManager().fromJid(netServerID);
		} catch (InvalidFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Stanza stanza = new Stanza(toIdentity);
				
		// CREATE MESSAGE BEAN
		NzoneBean netBean = new NzoneBean();
		netBean.setMethod(Method.SAVE_PREFERENCES);
		List<String> dataToSend = new ArrayList<String>();
		dataToSend.add(preferences);
		netBean.setData(dataToSend);
		startSignal = new CountDownLatch(1);		
		try {
			getCommManager().sendIQGet(stanza, netBean, this);
						
		} catch (CommunicationException e) {
			LOG.warn(e.getMessage());
		};

		try {
			startSignal.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
						
		
		if (commsResult != null)
			return this.commsResult.isResult();
		
		return false;
		
	}
	
	public String getPreferences()
	{
		
		// We want to sent all messages for Netowrking Client to the metworking server
		// hardcode for now TODO : Read from properties
		IIdentity toIdentity = null;
		commsResult = null;
		try {
			toIdentity = getCommManager().getIdManager().fromJid(netServerID);
		} catch (InvalidFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Stanza stanza = new Stanza(toIdentity);
				
		// CREATE MESSAGE BEAN
		NzoneBean netBean = new NzoneBean();
		netBean.setMethod(Method.GET_PREFERENCES);
				
		startSignal = new CountDownLatch(1);
		try {
			
			getCommManager().sendIQGet(stanza, netBean, this);
						
		} catch (CommunicationException e) {
			LOG.warn(e.getMessage());
		};

		try {
			startSignal.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
						
		
		if (commsResult != null)
		{
			if (commsResult.getData() != null && commsResult.getData().size() > 0)
				return this.commsResult.getData().get(0);
		}
		
		
		return null;
		
	}

	public String getNetServerID() {
		return netServerID;
	}

	public void setNetServerID(String netServerID) {
		this.netServerID = netServerID;
	}

	public UserDetails getMyDetails() {
		IIdentity toIdentity = null;
		commsResult = null;
		try {
			toIdentity = getCommManager().getIdManager().fromJid(netServerID);
		} catch (InvalidFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Stanza stanza = new Stanza(toIdentity);
						
		// CREATE MESSAGE BEAN
		NzoneBean netBean = new NzoneBean();
		netBean.setMethod(Method.GET_MY_DETAILS);
			
		startSignal = new CountDownLatch(1);
						
		try {
			getCommManager().sendIQGet(stanza, netBean, this);
							
		} catch (CommunicationException e) {
			LOG.warn(e.getMessage());
		};

		try {
			startSignal.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
								
				
		if ((commsResult != null) && (this.commsResult.getUserdata() != null))
			return this.commsResult.getUserdata().get(0);
				
		return null;
	}

	
	public boolean updateMyDetails(UserDetails dets) {
		IIdentity toIdentity = null;
		commsResult = null;
		try {
			toIdentity = getCommManager().getIdManager().fromJid(netServerID);
		} catch (InvalidFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Stanza stanza = new Stanza(toIdentity);
						
		// CREATE MESSAGE BEAN
		NzoneBean netBean = new NzoneBean();
		netBean.setMethod(Method.UPDATE_MY_DETAILS);
		netBean.setDetails(dets);
			
		startSignal = new CountDownLatch(1);
						
		try {
			getCommManager().sendIQGet(stanza, netBean, this);
							
		} catch (CommunicationException e) {
			LOG.warn(e.getMessage());
		};

		try {
			startSignal.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
								
				
		if (commsResult != null) 
			return this.commsResult.isResult();
				
		return false;
	}

	
}
