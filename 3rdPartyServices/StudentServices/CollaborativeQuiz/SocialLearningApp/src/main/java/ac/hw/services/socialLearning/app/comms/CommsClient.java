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
package ac.hw.services.socialLearning.app.comms;

import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
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
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.schema.css.devicemgmt.display.displayportalserverbean.DisplayPortalServerBean;
import org.societies.api.schema.css.devicemgmt.display.displayportalserverbean.DisplayPortalServerMethodType;
import org.societies.api.sociallearning.schema.serverbean.SocialLearningMethodType;
import org.societies.api.sociallearning.schema.serverbean.SocialLearningServerBean;




/**
 * Describe your class here...
 *
 * @author Eliza
 *
 */
public class CommsClient implements ISocialLearningServer, ICommCallback{


	private static final List<String> NAMESPACES = Collections.unmodifiableList(
			Arrays.asList("http://societies.org/api/sociallearning/schema/serverbean"));
	private static final List<String> PACKAGES = Collections.unmodifiableList(
			Arrays.asList("org.societies.api.sociallearning.schema.serverbean"));
				  
	private ICommManager commManager;
	private static Logger logging = LoggerFactory.getLogger(CommsClient.class);
	private IIdentityManager idMgr;
	
	private String[] addressPort = new String[2];
	

	
	private Hashtable<SocialLearningMethodType, SocialLearningServerBean> serviceIDResults;
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
	}

	public CommsClient() 
	{	
	//	this.serviceIDResults = new Hashtable<SociallearningMethodType, SociallearningResultBean>();
		
	}

	public void InitService() {
		this.serviceIDResults = new Hashtable<SocialLearningMethodType, SocialLearningServerBean>();
		//REGISTER OUR ServiceManager WITH THE XMPP Communication Manager
		try {
			getCommManager().register(this); 
		} catch (CommunicationException e) {
			e.printStackTrace();
		}
		logging.debug("Registered with comms manager!");
	}
	
	
	@Override
	public List<String> getJavaPackages() {
		// TODO Auto-generated method stub
		return PACKAGES;
	}

	@Override
	public List<String> getXMLNamespaces() {
		// TODO Auto-generated method stub
		return NAMESPACES;
	}

	@Override
	public void receiveError(Stanza arg0, XMPPError arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveInfo(Stanza arg0, String arg1, XMPPInfo arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveItems(Stanza arg0, String arg1, List<String> arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveMessage(Stanza arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveResult(Stanza stanza, Object result) {
		this.logging.debug("Recieved Result");
		if(result instanceof SocialLearningServerBean)
		{
			SocialLearningServerBean bean = (SocialLearningServerBean) result;
			if(bean.getMethod().equals(SocialLearningMethodType.SERVER_SOCKET_INFO))
			{
				this.serviceIDResults.put(SocialLearningMethodType.SERVER_SOCKET_INFO, (SocialLearningServerBean) result);
				synchronized (this.serviceIDResults){
					this.serviceIDResults.notifyAll();
				}
				//addressPort[0]=bean.getAddress();
				//addressPort[1]=String.valueOf(bean.getPort());
				//synchronized (this.addressPort){
				//	this.addressPort.notifyAll();
				//}
				
			}
		}
		/*this.logging.debug("Received resultBean");
		if (result instanceof SociallearningResultBean)
		{
			this.serviceIDResults.put(SociallearningMethodType.GET_SERVER_SERVICE_ID, (SociallearningResultBean) result);
			synchronized (this.serviceIDResults){
				this.serviceIDResults.notifyAll();
			}
				
		}*/

	}


	/*@Override
	public ServiceResourceIdentifier getServerServiceId(IIdentity serverIdentity) {
		SociallearningBean bean = new SociallearningBean();
		bean.setMethod(SociallearningMethodType.GET_SERVER_SERVICE_ID);
		
		Stanza stanza = new Stanza(serverIdentity);
		
		try {
			this.logging.debug("Requesting serviceID");
			this.commManager.sendIQGet(stanza, bean, this);
			while (!this.serviceIDResults.containsKey(SociallearningMethodType.GET_SERVER_SERVICE_ID)){
				try {
					this.logging.debug("waiting for results");
					synchronized(this.serviceIDResults){
						this.serviceIDResults.wait();
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			this.logging.debug("Received serviceID");
			SociallearningResultBean resultBean = this.serviceIDResults.get(SociallearningMethodType.GET_SERVER_SERVICE_ID);
			ServiceResourceIdentifier serviceId = resultBean.getServiceID();
			this.serviceIDResults.remove(SociallearningMethodType.GET_SERVER_SERVICE_ID);
			return serviceId;
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		return null;
		
		
	}*/

	@Override
	public String[] getServerPortAddress(IIdentity serverIdentity) {
		logging.debug("SENDING MESSAGE TO: " + serverIdentity +"!");
		
		Stanza stanza = new Stanza(serverIdentity);
		SocialLearningServerBean bean = new SocialLearningServerBean();
		bean.setMethod(SocialLearningMethodType.SERVER_SOCKET_INFO_REQUEST);
		logging.debug(bean.toString());
		try {

			//this.commManager.sendMessage(stanza, bean);
			//this.commManager.sendIQGet(stanza, bean, this);//, this);
			getCommManager().sendIQGet(stanza, bean, this);
		} catch (CommunicationException e) {
			logging.debug("FAILED SENDING MESSAGE!");
			logging.debug(e.toString());
			StackTraceElement[] x = e.getStackTrace();
			logging.debug(e.toString());
			for(int i=0;i < x.length; i++)
			{
			logging.debug(x[i].toString());	
			}
		}


		
		//WAIT FOR RESULTS
		
		while (!this.serviceIDResults.containsKey(SocialLearningMethodType.SERVER_SOCKET_INFO)){
			try {
				this.logging.debug("waiting for results");
				synchronized(this.serviceIDResults){
					this.serviceIDResults.wait();
				}
			} catch (InterruptedException e) {
	
				e.printStackTrace();


			}
		}
		addressPort[0] = serviceIDResults.get(SocialLearningMethodType.SERVER_SOCKET_INFO).getAddress();
		addressPort[1] = String.valueOf(serviceIDResults.get(SocialLearningMethodType.SERVER_SOCKET_INFO).getPort());
		return addressPort;		
		
	}

}
