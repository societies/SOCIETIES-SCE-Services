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
package org.societies.thirdparty.sharedcalendar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.broker.ICtxBroker;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;

/**
 * Describe your class here...
 *
 * @author <a href="mailto:sanchocsa@gmail.com">Sancho Rêgo</a> (PTIN)
 *
 */
public class CalendarContextUtils {

	static final Logger log = LoggerFactory.getLogger(CalendarContextUtils.class);
	private ICtxBroker ctxBroker;
	private IIdentity myId;
	private Requestor requestor;
	private ICommManager commManager;
	/**
	 * 
	 */
	public CalendarContextUtils(ICtxBroker ctxBroker, IIdentity myId, ICommManager commManager) {
		if(log.isDebugEnabled())
			log.debug("CalendarContextUtil created.");
		
		this.ctxBroker = ctxBroker;
		this.myId = myId;
		this.requestor = new Requestor(myId);
		this.commManager = commManager;
	}

	public String getHomeCity(){
		if(log.isDebugEnabled())
			log.debug("Trying to get my city!");
		
		return getContextAttribute(CtxAttributeTypes.ADDRESS_HOME_CITY);
		
	}
	
	public String getWorkCity(){
		if(log.isDebugEnabled())
			log.debug("Trying to get my work city!");
		
		return getContextAttribute(CtxAttributeTypes.ADDRESS_WORK_CITY);
		
	}
	
	public String getMyLocation(){
		if(log.isDebugEnabled())
			log.debug("Trying to get location!");
		
		return getContextAttribute(CtxAttributeTypes.LOCATION_SYMBOLIC);
		
	}
	public List<IIdentity> getMyFriends(){
		
		if(log.isDebugEnabled())
			log.debug("Trying to get my friends from context!");
		String friendsCtx = getContextAttribute(CtxAttributeTypes.FRIENDS);
		
		if(log.isDebugEnabled())
			log.debug("Result was: " + friendsCtx);
		
		List<IIdentity> friendList = new ArrayList<IIdentity>();
		
		if(friendsCtx != null && !friendsCtx.isEmpty()){
			String[] friends = friendsCtx.split(",");
			
			try{
				for(int i = 0; i < friends.length; i++){
					friendList.add(commManager.getIdManager().fromJid(friends[i]));
				}
			} catch(Exception ex){
				log.error("Exception getting a friend!");
				ex.printStackTrace();
			}
		}

		return friendList;
	}
	
	public List<String> getMyInterests(){
		
		if(log.isDebugEnabled())
			log.debug("Trying to get my interests from context!");
		String interestCtx = getContextAttribute(CtxAttributeTypes.INTERESTS);
		String[] interests = interestCtx.split(",");
		List<String> interestList = new ArrayList<String>();
		for(int i = 0; i < interests.length; i++){
			interestList.add(interests[i]);
		}
		
		return interestList;
	}
	
	private String getContextAttribute(String ctxAttribName) {
		if(log.isDebugEnabled())
			log.debug("Getting Context Attribute: " + ctxAttribName);
		
		CtxAttribute ctxAttr = null;

		try {
		
			CtxEntityIdentifier ownerEntityId = this.ctxBroker.retrieveIndividualEntityId(requestor,myId).get();
			
			Future<List<CtxIdentifier>> ctxIdentLookupFut = this.ctxBroker.lookup(requestor, ownerEntityId,
							CtxModelType.ATTRIBUTE, ctxAttribName);
			
			// Thread.sleep(1000);
			List<CtxIdentifier> ctxIdentLookup = ctxIdentLookupFut.get();
			CtxIdentifier ctxIdent = null;

			if ((ctxIdentLookup != null) && (ctxIdentLookup.size() > 0))
				ctxIdent = ctxIdentLookup.get(0);
				log.debug("ctxIdent: " + ctxIdent);
			if (ctxIdent == null) {
				ctxAttr = this.ctxBroker.createAttribute(requestor, ownerEntityId,ctxAttribName).get();
				
			} else {
				Future<CtxModelObject> ctxAttrFut = this.ctxBroker.retrieve(requestor, ctxIdent);
				// Thread.sleep(1000);
				ctxAttr = (CtxAttribute) ctxAttrFut.get();
				
			}
		

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			log.error("Unable to read context atribute " + ctxAttribName);
			
		}

		if (ctxAttr == null)
			return null;
		if(log.isDebugEnabled())
			log.debug("getContextAtribute End:" + ctxAttr.getStringValue());

		return ctxAttr.getStringValue();

	}

}
