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
package si.setcce.societies.taskposting.comms;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.interfaces.IFeatureServer;
import org.societies.api.ext3p.schema.taskposting.BackendBean;
import org.societies.api.ext3p.schema.taskposting.BackendBeanResult;
import org.societies.api.ext3p.schema.taskposting.MethodType;
import org.societies.api.ext3p.schema.taskposting.ResultBean;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;

import si.setcce.societies.taskposting.api.IBackend;

public class CommsServer implements IFeatureServer {

	private static Logger LOG = LoggerFactory.getLogger(CommsServer.class);

	private static final List<String> NAMESPACES = Collections.unmodifiableList(
			  Arrays.asList("http://societies.org/api/ext3p/schema/taskposting"
					  ));
	private static final List<String> PACKAGES = Collections.unmodifiableList(
			  Arrays.asList("org.societies.api.ext3p.schema.taskposting"
					  ));
	
	private ICommManager commMgr;
	private IBackend backend;

	public CommsServer() {
		LOG.info("CommsServer()");
	}
	
	public void init() {
		
		LOG.debug("init(): commMgr = {}", commMgr.toString());
		
		try {
			commMgr.register(this);
			LOG.debug("init(): commMgr registered");
		} catch (CommunicationException e) {
			LOG.error("init(): ", e);
		}
	}

	// Getters and setters for beans
	public IBackend getBackend() {
		return backend;
	}
	public void setBackend(IBackend backend) {
		this.backend = backend;
		//LOG.debug("setBackend()");
		//LOG.debug("setBackend({})", backend);
	}
	public ICommManager getCommMgr() {
		return commMgr;
	}
	public void setCommMgr(ICommManager commMgr) {
		this.commMgr = commMgr;
		//LOG.debug("setCommManager()");
		//LOG.debug("setCommManager({})", commManager);
	}
	
	/* (non-Javadoc)
	 * @see org.societies.api.comm.xmpp.interfaces.IFeatureServer#getJavaPackages()
	 */
	@Override
	public List<String> getJavaPackages() {
		
		LOG.debug("getJavaPackages()");
		
		return PACKAGES;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.comm.xmpp.interfaces.IFeatureServer#getQuery(
	 * org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public Object getQuery(Stanza stanza, Object messageBean) throws XMPPError {

		// Put your functionality here if there IS a return object
		
		LOG.debug("getQuery({}, {})", stanza, messageBean);
		LOG.debug("getQuery(): stanza.id   = {}", stanza.getId());
		LOG.debug("getQuery(): stanza.from = {}", stanza.getFrom());
		LOG.debug("getQuery(): stanza.to   = {}", stanza.getTo());
		
		Future<ResultBean> resultFuture;
		ResultBean resultBean;
		BackendBeanResult result = new BackendBeanResult();
		
		if (messageBean != null && messageBean instanceof BackendBean) {
			
			// Method parameters
			BackendBean backendBean = (BackendBean) messageBean;
			String userStr = backendBean.getUserIdentity();
			String location = backendBean.getLocation();
			
			MethodType method = backendBean.getMethod();
			
			LOG.debug("getQuery(): Backend. Method: " + method);
			LOG.debug("getQuery(): Backend. Params: " + location + ", " +
					userStr);

			IIdentity user;
			try {
				user = commMgr.getIdManager().fromJid(userStr);
			} catch (InvalidFormatException e) {
				LOG.warn("getQuery()", e);
				return null;
			}
			
				switch (method) {
				case SET_USER_LOCATION:
					LOG.debug("getQuery(): Backend.setUserLocation({})", location);
					resultFuture = backend.setUserLocation(user, location);
					break;
				default:
					LOG.warn("getQuery(): unrecognized method: {}", method);
					return null;
				}
			try {
				resultBean = resultFuture.get();
				result.setResultBean(resultBean);
			} catch (InterruptedException e) {
				LOG.warn("getQuery()", e);
			} catch (ExecutionException e) {
				LOG.warn("getQuery()", e);
			}
		}
		
		return result;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.comm.xmpp.interfaces.IFeatureServer#getXMLNamespaces()
	 */
	@Override
	public List<String> getXMLNamespaces() {
		
		LOG.debug("getXMLNamespaces()");
		
		return NAMESPACES;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.comm.xmpp.interfaces.IFeatureServer#receiveMessage(
	 * org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public void receiveMessage(Stanza stanza, Object messageBean) {
		
		// Put your functionality here if there is NO return object, ie, VOID
		
		LOG.debug("receiveMessage({}, {})", stanza, messageBean);
		
		if (messageBean instanceof BackendBean) {
			
			// Method parameters
			BackendBean backendBean = (BackendBean) messageBean;
			String userStr = backendBean.getUserIdentity();
			String location = backendBean.getLocation();
			
			MethodType method = backendBean.getMethod();
			
			LOG.debug("receiveMessage(): Backend. Method: " + method);
			LOG.debug("receiveMessage(): Backend. Params: " + userStr + ", " +
					location);
			
			switch (method) {
			case SET_USER_LOCATION:
				LOG.warn("receiveMessage(): Method {} returns a value and should not be handled here.", method);
				break;
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.societies.api.comm.xmpp.interfaces.IFeatureServer#setQuery(
	 * org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public Object setQuery(Stanza stanza, Object messageBean) throws XMPPError {
		
		LOG.debug("setQuery()");
		
		return null;
	}
}
