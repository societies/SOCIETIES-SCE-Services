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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.broker.ICtxBroker;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationTypes;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorService;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.services.IServices;

/**
 * Describe your class here...
 *
 * @author <a href="mailto:sanchocsa@gmail.com">Sancho Rêgo</a> (PTIN)
 *
 */
public class CalendarContextUtils implements CtxChangeEventListener {

	static final Logger log = LoggerFactory.getLogger(CalendarContextUtils.class);
	private ICtxBroker ctxBroker;
	private IServices serviceMgmt;
	private IIdentity myId;
	private RequestorService requestor;
	private ICommManager commManager;
	private HashMap<String, String> contextValues;
	private HashMap<CtxIdentifier, String> identifierValues;
	private boolean contextChange;

	public ICtxBroker getCtxBroker() {
		return ctxBroker;
	}

	public void setCtxBroker(ICtxBroker ctxBroker) {
		this.ctxBroker = ctxBroker;
	}

	public IServices getServiceMgmt() {
		return serviceMgmt;
	}

	public void setServiceMgmt(IServices serviceMgmt) {
		this.serviceMgmt = serviceMgmt;
	}

	public ICommManager getCommManager() {
		return commManager;
	}

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}
	
	public CalendarContextUtils() {
		log.debug("CalendarContextUtil created.");
		requestor = null;
		contextValues = new HashMap<String,String>();
		identifierValues = new HashMap<CtxIdentifier,String>();
	}

	public void init(){
		log.debug("Creating CalendarContextUtils");
		myId = commManager.getIdManager().getThisNetworkNode();
		contextChange = false;
		ServiceResourceIdentifier mySri = serviceMgmt.getMyServiceId(getClass());
		requestor = new RequestorService(myId, mySri);
		getHomeCity();
		getWorkCity();
		getMyLocation();
		getMyFriends();
		getMyInterests();
		
	}
	
	public boolean isContextChange() {
		return contextChange;
	}

	public void setContextChange(boolean contextChange) {
		this.contextChange = contextChange;
	}

	public void destroy(){
		log.debug("Destroying CalendarContextUtils!");
		Iterator<CtxIdentifier> itSet = identifierValues.keySet().iterator();
		while(itSet.hasNext()){
			CtxIdentifier identifier = itSet.next();
			try {
				log.debug("Unregistering for Context Attribute {}",identifierValues.get(identifier));
				ctxBroker.unregisterFromChanges(getRequestor(), this, identifier);
			} catch (Exception e) {
				log.error("Exception unregistering for context changes: {}",e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	public String getHomeCity(){
		return getContextAttribute(CtxAttributeTypes.ADDRESS_HOME_CITY);
		
	}
	
	public String getWorkCity(){

		return getContextAttribute(CtxAttributeTypes.ADDRESS_WORK_CITY);
		
	}
	
	public String getMyLocation(){
		return getContextAttribute(CtxAttributeTypes.LOCATION_SYMBOLIC);
		
	}
	public List<IIdentity> getMyFriends(){
		
		String friendsCtx = getContextAssociation(CtxAssociationTypes.IS_FRIENDS_WITH);
		
		log.debug("Friend Result was: {}", friendsCtx);
		
		List<IIdentity> friendList = new ArrayList<IIdentity>();
		
		if(friendsCtx != null && !friendsCtx.isEmpty()){
			String[] friends = friendsCtx.split(",");
			
			
			for(int i = 0; i < friends.length; i++){
				try{
					friendList.add(commManager.getIdManager().fromJid(friends[i]));
				} catch(Exception ex){
					log.error("Exception getting a friend {}!",friends[i]);
					ex.printStackTrace();
				}
			}

		}

		return friendList;
	}
	
	public List<String> getMyInterests(){
		
		log.debug("Trying to get my interests from context!");
		List<String> interestList = new ArrayList<String>();
		
		String interestCtx = getContextAttribute(CtxAttributeTypes.INTERESTS);
		if(interestCtx != null){
			log.debug("Retrieved interests are {} ",interestCtx);
			String[] interests = interestCtx.split(",");
			
			for(int i = 0; i < interests.length; i++){
				interestList.add(interests[i]);
			}
		}
		
		return interestList;
	}
	
	private String getContextAttribute(String ctxAttribName){
		//First we check if we already have a value, if yes...
		if(contextValues.containsKey(ctxAttribName))
			return contextValues.get(ctxAttribName);
		
		//It doesn't contain it, so we retrieve it and register for changes...
		CtxAttribute ctxAttr = null;

		try {
		
			CtxEntityIdentifier ownerEntityId = this.ctxBroker.retrieveIndividualEntityId(getRequestor(),myId).get();
			
			Future<List<CtxIdentifier>> ctxIdentLookupFut;
			ctxIdentLookupFut = this.ctxBroker.lookup(getRequestor(), ownerEntityId,
							CtxModelType.ATTRIBUTE, ctxAttribName);
			
			List<CtxIdentifier> ctxIdentLookup = ctxIdentLookupFut.get();
			CtxIdentifier ctxIdent = null;

			if ((ctxIdentLookup != null) && (ctxIdentLookup.size() > 0))
				ctxIdent = ctxIdentLookup.get(0);
			
			if (ctxIdent == null) {
				ctxAttr = this.ctxBroker.createAttribute(getRequestor(), ownerEntityId,ctxAttribName).get();
				
			} else {
				Future<CtxModelObject> ctxAttrFut = this.ctxBroker.retrieve(getRequestor(), ctxIdent);
				ctxAttr = (CtxAttribute) ctxAttrFut.get();
				
				//Now we register for events;
				identifierValues.put(ctxIdent, ctxAttribName);
				ctxBroker.registerForChanges(getRequestor(), this, ctxIdent);
				
			}
		

		} catch (Exception e) {
			log.error("Unable to read context atribute {} : {}", ctxAttribName, e.getMessage());
			e.printStackTrace();
			
		}


		if (ctxAttr == null)
			contextValues.put(ctxAttribName, null);
		else
			contextValues.put(ctxAttribName, ctxAttr.getStringValue());
		
		log.debug("Context for {} is {} ",ctxAttribName,contextValues.get(ctxAttribName));
		
		return contextValues.get(ctxAttribName);
		
	}

	private String getContextAssociation(String ctxAssociationName){
		//First we check if we already have a value, if yes...
		if(contextValues.containsKey(ctxAssociationName))
			return contextValues.get(ctxAssociationName);
		
		//It doesn't contain it, so we retrieve it and register for changes...
		CtxAssociation ctxAssoc = null;

		try {
		
			CtxEntityIdentifier ownerEntityId = this.ctxBroker.retrieveIndividualEntityId(getRequestor(),myId).get();
			
			Future<List<CtxIdentifier>> ctxIdentLookupFut;
			ctxIdentLookupFut = this.ctxBroker.lookup(getRequestor(), ownerEntityId,
							CtxModelType.ASSOCIATION, ctxAssociationName);
			
			List<CtxIdentifier> ctxIdentLookup = ctxIdentLookupFut.get();
			CtxIdentifier ctxIdent = null;

			if ((ctxIdentLookup != null) && (ctxIdentLookup.size() > 0))
				ctxIdent = ctxIdentLookup.get(0);
			
			if (ctxIdent != null) {
				Future<CtxModelObject> ctxAssociationFut = this.ctxBroker.retrieve(getRequestor(), ctxIdent);
				ctxAssoc = (CtxAssociation) ctxAssociationFut.get();
				
				//Now we register for events;
				identifierValues.put(ctxIdent, ctxAssociationName);
				ctxBroker.registerForChanges(getRequestor(), this, ctxIdent);
			} else {

				
			}
		

		} catch (Exception e) {
			log.error("Unable to read context association {} : {}", ctxAssociationName, e.getMessage());
			e.printStackTrace();
			
		}

		if (ctxAssoc == null)
			contextValues.put(ctxAssociationName, null);
		else{
			StringBuilder friendBuilder = new StringBuilder();
			for(CtxEntityIdentifier friendId: ctxAssoc.getChildEntities()){
				friendBuilder.append(friendId.getOwnerId()).append(',');
			}
			contextValues.put(ctxAssociationName, friendBuilder.toString());
		}
		return contextValues.get(ctxAssociationName);
		
	}

	
	private void updateContextValue(CtxIdentifier id) {
		log.debug("Updating context value for identifier: {}",id);
		
		if(!identifierValues.containsKey(id)){
			log.warn("We don't know this id {}! Why are we receiving it?!",id);
			return;
		}
		
		if(id.getModelType().equals(CtxModelType.ATTRIBUTE)){
			try{
				Future<CtxModelObject> ctxAttrFut = this.ctxBroker.retrieve(getRequestor(), id);
				CtxAttribute ctxAttr = (CtxAttribute) ctxAttrFut.get();
	
				if (ctxAttr == null)
					contextValues.put(identifierValues.get(id), null);
				else
					contextValues.put(identifierValues.get(id), ctxAttr.getStringValue());
				
			} catch(Exception ex){
				log.error("Exception while trying to update {} : {}", identifierValues.get(id),ex.getMessage());
				ex.printStackTrace();
			}
		} else{
			try{
				Future<CtxModelObject> ctxAssocFut = this.ctxBroker.retrieve(getRequestor(), id);
				CtxAssociation ctxAssoc= (CtxAssociation) ctxAssocFut.get();

				if (ctxAssoc == null)
					contextValues.put(identifierValues.get(id), null);
				else{
					StringBuilder friendBuilder = new StringBuilder();
					for(CtxEntityIdentifier friendId: ctxAssoc.getChildEntities()){
						friendBuilder.append(friendId.getOwnerId()).append(',');
					}
					contextValues.put(identifierValues.get(id), friendBuilder.toString());
				}
			} catch(Exception ex){
				log.error("Exception while trying to update {} : {}", identifierValues.get(id),ex.getMessage());
				ex.printStackTrace();
			}
		}
		
		log.debug("Updated value for {} is {}",identifierValues.get(id),contextValues.get(identifierValues.get(id)));
		setContextChange(true);
		
	}

	/**
	 * @return
	 */
	private Requestor getRequestor() {
		/*if(requestor == null){
			ServiceResourceIdentifier mySri = serviceMgmt.getMyServiceId(getClass());
			requestor = new RequestorService(myId, mySri);
		}*/
		return requestor;
	}

	@Override
	public void onCreation(CtxChangeEvent event) {
		log.debug("onCreation event: {}", event.getId());
	}

	@Override
	public void onModification(CtxChangeEvent event) {
		log.debug("onModification event: {}", event.getId());
		updateContextValue(event.getId());
	}

	@Override
	public void onRemoval(CtxChangeEvent event) {
		log.debug("onRemoval event: {}", event.getId());
	}


	@Override
	public void onUpdate(CtxChangeEvent event) {
		log.debug("onUpdate event: {}", event.getId());
	}

}
