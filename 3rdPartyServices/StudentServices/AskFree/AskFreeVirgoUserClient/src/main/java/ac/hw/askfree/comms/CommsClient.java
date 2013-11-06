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
package ac.hw.askfree.comms;

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
import org.societies.api.schema.askfree.serverbean.AskFreeMethodType;
import org.societies.api.schema.askfree.serverbean.AskFreeServerBean;

import ac.hw.askfree.IAskFreeServerRemote;

/**
 * Describe your class here...
 *
 * @author Jiannis
 *
 */
public class CommsClient implements ICommCallback, IAskFreeServerRemote{


	private static final List<String> NAMESPACES = Collections.unmodifiableList(

			Arrays.asList("http://societies.org/api/schema/askfree/serverbean"));
	private static final List<String> PACKAGES = Collections.unmodifiableList(
			Arrays.asList("org.societies.api.schema.askfree.serverbean"));

	Logger logging = LoggerFactory.getLogger(this.getClass());


	private ICommManager commManager;

	public void InitService() {
		//REGISTER OUR ServiceManager WITH THE XMPP Communication Manager
		try {
			if(this.logging.isDebugEnabled()) this.logging.debug("Registering with comms manager");
			getCommManager().register(this);
		} catch (CommunicationException e) {
			e.printStackTrace();
		}
	}


	/* (non-Javadoc)
	 * @see ac.hw.askfree.IAskFreeServerRemote#registerAfterRestart(java.lang.String, java.lang.String)
	 */
	@Override
	public void sendLocation(IIdentity destinationIdentity,
			String location) {
		if(this.logging.isDebugEnabled()) this.logging.debug("Sending new locagtion to AskFreeServer");
		AskFreeServerBean serverBean = new AskFreeServerBean();
		serverBean.setMethod(AskFreeMethodType.UPDATE_USER_LOCATION);
		serverBean.setUserLocation(location);
		Stanza stanza = new Stanza(destinationIdentity);
		try {
			this.commManager.sendMessage(stanza, serverBean);
			if(this.logging.isDebugEnabled()) this.logging.debug("AskFreeBean has sent successfully!");
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#getJavaPackages()
	 */
	@Override
	public List<String> getJavaPackages() {
		// TODO Auto-generated method stub
		return PACKAGES;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#getXMLNamespaces()
	 */
	@Override
	public List<String> getXMLNamespaces() {
		// TODO Auto-generated method stub
		return NAMESPACES;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveError(org.societies.api.comm.xmpp.datatypes.Stanza, org.societies.api.comm.xmpp.exceptions.XMPPError)
	 */
	@Override
	public void receiveError(Stanza arg0, XMPPError arg1) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveInfo(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.String, org.societies.api.comm.xmpp.datatypes.XMPPInfo)
	 */
	@Override
	public void receiveInfo(Stanza arg0, String arg1, XMPPInfo arg2) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveItems(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.String, java.util.List)
	 */
	@Override
	public void receiveItems(Stanza arg0, String arg1, List<String> arg2) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveMessage(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public void receiveMessage(Stanza arg0, Object arg1) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveResult(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public void receiveResult(Stanza arg0, Object arg1) {
		// TODO Auto-generated method stub

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

}
