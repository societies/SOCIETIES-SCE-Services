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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;

import org.societies.thirdpartyservices.api.internal.networking.INetworkingDirectoryCallback;
import org.societies.thirdpartyservices.api.internal.networking.INetworkingDirectoryRemote;
import org.societies.thirdpartyservices.schema.networking.MethodType;
import org.societies.thirdpartyservices.schema.networking.NetworkingBean;
import org.societies.thirdpartyservices.schema.networking.NetworkingRecord;
import org.societies.thirdpartyservices.schema.networking.UserRecord;

/**
 * Comms Client that initiates the remote communication for the css discovery
 * 
 * @author Maria Mannion
 * 
 */
public class NetworkCommsClient implements INetworkingDirectoryRemote,
		ICommCallback {
	private static final List<String> NAMESPACES = Collections
			.unmodifiableList(Arrays
					.asList("http://societies.org/thirdpartyservices/schema/networking"));
	private static final List<String> PACKAGES = Collections
			.unmodifiableList(Arrays
					.asList("org.societies.thirdpartyservices.schema.networking"));

	// PRIVATE VARIABLES
	private ICommManager commManager;
	private static Logger LOG = LoggerFactory
			.getLogger(NetworkCommsClient.class);
	private IIdentityManager idMgr;

	// PROPERTIES
	public ICommManager getCommManager() {
		return commManager;
	}

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}

	public NetworkCommsClient() {
	}

	public void InitService() {
		// Registry Networking client Directory with the Comms Manager

		if (LOG.isDebugEnabled())
			LOG.debug("Registering the Networking Client with the XMPP Communication Manager");

		try {
			getCommManager().register(this);
		} catch (CommunicationException e) {
			e.printStackTrace();
		}
		idMgr = commManager.getIdManager();
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

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.societies.thirdpartyservices.api.internal.networking.
	 * INetworkingDirectoryRemote#getUserRecord(int,
	 * org.societies.thirdpartyservices
	 * .api.internal.networking.INetworkingDirectoryCallback)
	 */
	@Override
	public void getUserRecord(int id, INetworkingDirectoryCallback callback) {

		// TODO Read DA node address
		IIdentity toIdentity = idMgr.getThisNetworkNode();
		Stanza stanza = new Stanza(toIdentity);

		// SETUP CALC CLIENT RETURN STUFF
		NetworkingDirectoryCallback netCallback = new NetworkingDirectoryCallback(
				stanza.getId(), callback);

		// CREATE MESSAGE BEAN
		NetworkingBean netBean = new NetworkingBean();
		netBean.setId(id);
		netBean.setMethod(MethodType.GET_USER_RECORD);
		try {
			commManager.sendIQGet(stanza, netBean, netCallback);
		} catch (CommunicationException e) {
			LOG.warn(e.getMessage());
		}
		;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.societies.thirdpartyservices.api.internal.networking.
	 * INetworkingDirectoryRemote#getNetworkingRecord(int, int,
	 * org.societies.thirdpartyservices
	 * .api.internal.networking.INetworkingDirectoryCallback)
	 */
	@Override
	public void getNetworkingRecord(int id, int friendid,
			INetworkingDirectoryCallback callback) {
		// TODO Read DA node address
		IIdentity toIdentity = idMgr.getThisNetworkNode();
		Stanza stanza = new Stanza(toIdentity);

		// SETUP CALC CLIENT RETURN STUFF
		NetworkingDirectoryCallback netCallback = new NetworkingDirectoryCallback(
				stanza.getId(), callback);

		// CREATE MESSAGE BEAN
		NetworkingBean netBean = new NetworkingBean();
		netBean.setId(id);
		netBean.setFriendid(friendid);
		netBean.setMethod(MethodType.GET_NETWORKING_RECORD);
		try {
			commManager.sendIQGet(stanza, netBean, netCallback);
		} catch (CommunicationException e) {
			LOG.warn(e.getMessage());
		}
		;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.societies.thirdpartyservices.api.internal.networking.
	 * INetworkingDirectoryRemote
	 * #updateNetworkingRecord(org.societies.thirdpartyservices
	 * .schema.networking.NetworkingRecord,
	 * org.societies.thirdpartyservices.api.
	 * internal.networking.INetworkingDirectoryCallback)
	 */
	@Override
	public void updateNetworkingRecord(NetworkingRecord netRec,
			INetworkingDirectoryCallback callback) {
		// TODO Read DA node address
		IIdentity toIdentity = idMgr.getThisNetworkNode();
		Stanza stanza = new Stanza(toIdentity);

		// SETUP CALC CLIENT RETURN STUFF
		NetworkingDirectoryCallback netCallback = new NetworkingDirectoryCallback(
				stanza.getId(), callback);

		// CREATE MESSAGE BEAN
		NetworkingBean netBean = new NetworkingBean();
		netBean.setNetworkRec(netRec);

		netBean.setMethod(MethodType.UPDATE_NETWORKING_RECORD);
		try {
			commManager.sendIQGet(stanza, netBean, netCallback);
		} catch (CommunicationException e) {
			LOG.warn(e.getMessage());
		}
		;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.societies.thirdpartyservices.api.internal.networking.
	 * INetworkingDirectoryRemote
	 * #updateUserRecord(org.societies.thirdpartyservices
	 * .schema.networking.UserRecord,
	 * org.societies.thirdpartyservices.api.internal
	 * .networking.INetworkingDirectoryCallback)
	 */
	@Override
	public void updateUserRecord(UserRecord userRec,
			INetworkingDirectoryCallback callback) {
		// TODO Read DA node address
		IIdentity toIdentity = idMgr.getThisNetworkNode();
		Stanza stanza = new Stanza(toIdentity);

		// SETUP CALC CLIENT RETURN STUFF
		NetworkingDirectoryCallback netCallback = new NetworkingDirectoryCallback(
				stanza.getId(), callback);

		// CREATE MESSAGE BEAN
		NetworkingBean netBean = new NetworkingBean();
		netBean.setUserRec(userRec);

		netBean.setMethod(MethodType.UPDATE_USER_RECORD);
		try {
			commManager.sendIQGet(stanza, netBean, netCallback);
		} catch (CommunicationException e) {
			LOG.warn(e.getMessage());
		}
		;

	}

}
