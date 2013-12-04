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

package org.societies.collabtools.context;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.broker.ICtxBroker;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationIdentifier;
import org.societies.api.context.model.CtxAssociationTypes;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeComplexValue;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorService;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.services.IServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Connector for using Context Broker in CollabTools
 *
 * @author NikosK and Chris Lima
 *
 */
@Service
public class ContextAware3pService implements IContextAware3pService  {


	private Logger LOG = LoggerFactory.getLogger(this.getClass());

	//services
	private ICtxBroker ctxBroker;
	private ICommManager commsMgr;
	private IIdentityManager idMgr;
	private IServices serviceMgmt;

	// identities
	private RequestorService requestorService;
	private IIdentity userIdentity;
	private ServiceResourceIdentifier myServiceID;

	private CtxChangeEventListener myCtxChangeEventListener;

	protected Set<CtxEntityIdentifier> membersList;


	@Autowired(required=true)
	public ContextAware3pService(ICtxBroker ctxBroker, ICommManager commsMgr, IServices serviceMgmt){

		LOG.info("*** ContextAware3pService started");

		//services
		this.ctxBroker = ctxBroker;
		this.commsMgr = commsMgr;
		this.idMgr = commsMgr.getIdManager();
		this.serviceMgmt = serviceMgmt;
		this.membersList = new CopyOnWriteArraySet<CtxEntityIdentifier>();
		
		LOG.info("ctxBroker: "+this.ctxBroker);
		LOG.info("commsMgr : "+this.commsMgr );
		LOG.info("idMgr : "+this.idMgr );
		LOG.info("serviceMgmt : "+this.serviceMgmt );

//		File logFile = new File("databases/collabToolsLogFile.log");
//		if (!logFile.getParentFile().exists()) {
//			logFile.getParentFile().mkdirs();
//		}
//		if (!logFile.exists()) {
//			try {
//				logFile.createNewFile();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}



		//		this.userIdentity = this.idMgr.getThisNetworkNode();
		//		try {
		//			this.serviceIdentity = this.idMgr.fromJid("cviana@societies.org");
		//		} catch (InvalidFormatException e) {
		//			e.printStackTrace();
		//		}
		//
		//		myServiceID = new ServiceResourceIdentifier();
		//		myServiceID.setServiceInstanceIdentifier("css://cviana@societies.org/Context3pServiceConnector");
		//		try {
		//			myServiceID.setIdentifier(new URI("css://cviana@societies.org/Context3pServiceConnector"));
		//		} catch (URISyntaxException e) {
		//			e.printStackTrace();
		//		}
		//		requestorService = new RequestorService(serviceIdentity, myServiceID);
		//
		//		LOG.info("userIdentity : "+ userIdentity.getBareJid());
		//		LOG.info("requestor service : "+requestorService);
	}


	/**
	 * This method register for context events in the context database
	 * @throws InvalidFormatException 
	 */
	@Override
	public void registerForContextChanges(Object communityId) throws InvalidFormatException {
		//Cast IIdentity for the societies platform
		IIdentity cisID = idMgr.fromJid(communityId.toString());
		LOG.info("cisID retrieved: "+ cisID);

		try {
			Set<CtxEntityIdentifier> ctxMembersIDs = this.getCommunityMembers(cisID);
			Iterator<CtxEntityIdentifier> members = ctxMembersIDs.iterator();
			membersList.clear();
			while(members.hasNext()){
				CtxEntityIdentifier member = members.next();
				membersList.add(member);
				LOG.info("*** Registering context changes for member: "+member.getOwnerId());

				//TODO: Include here other ctx updates if necessary. For short term context
				this.ctxBroker.registerForChanges(getRequestor(), this.myCtxChangeEventListener, member, CtxAttributeTypes.LOCATION_SYMBOLIC);
				this.ctxBroker.registerForChanges(getRequestor(), this.myCtxChangeEventListener, member, CtxAttributeTypes.STATUS);
				//				this.ctxBroker.registerForChanges(getRequestor(), this.myCtxChangeEventListener, member, CtxAttributeTypes.OCCUPATION);
				//				this.ctxBroker.registerForChanges(getRequestor(), this.myCtxChangeEventListener, member, CtxAttributeTypes.ADDRESS_WORK_CITY);
				//				this.ctxBroker.registerForChanges(getRequestor(), this.myCtxChangeEventListener, member, CtxAttributeTypes.ADDRESS_WORK_COUNTRY);

				//Trying to register for long term context
				this.ctxBroker.registerForChanges(getRequestor(), this.myCtxChangeEventListener, member, CtxAttributeTypes.INTERESTS);
			}

		} catch (CtxException e1) {
			e1.printStackTrace();
		}

		//Registering for new members in the community
		try {
			CtxEntityIdentifier ctxCommunityEntityIdentifier = this.ctxBroker.retrieveCommunityEntityId(getRequestor(), cisID).get();
			if (ctxCommunityEntityIdentifier == null)
				throw new RuntimeException("Community not created, ctxCommunityEntityIdentifier is null");
			

			List<CtxIdentifier> assocList = ctxBroker.lookup(getRequestor(), ctxCommunityEntityIdentifier, CtxModelType.ASSOCIATION, CtxAssociationTypes.HAS_MEMBERS).get(); 
			if(!assocList.isEmpty()){ 
				CtxAssociationIdentifier assocID =  (CtxAssociationIdentifier) assocList.get(0);  
				this.ctxBroker.registerForChanges(getRequestor(), this.myCtxChangeEventListener, assocID); 
			}
//
//
//			final CommunityCtxEntity communityEnt = (CommunityCtxEntity) ctxBroker.retrieve(getRequestor(), ctxCommunityEntityIdentifier).get();
//			CtxAssociationIdentifier hasMembersId = null;
//			for (final CtxAssociationIdentifier foundHasMembersId : communityEnt.getAssociations(CtxAssociationTypes.HAS_MEMBERS)) {
//				final CtxAssociation foundHasMembers = (CtxAssociation) ctxBroker.retrieve(getRequestor(), foundHasMembersId).get();
//				if (foundHasMembers != null && ctxCommunityEntityIdentifier.equals(foundHasMembers.getParentEntity())) {
//					hasMembersId = foundHasMembersId;
//				}
//				break;
//			}
//			if (hasMembersId == null) {
//				LOG.error("Failed to register for membership changes of CIS '" + ctxCommunityEntityIdentifier.getOwnerId() + "': HAS_MEMBERS association not found");
//			}		
//
//			LOG.debug("CtxAssociationIdentifier retrieved for update: {}", hasMembersId.toString());
//			this.ctxBroker.registerForChanges(getRequestor(), this.myCtxChangeEventListener, hasMembersId);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		LOG.info("*** registerForContextChanges success");
	}

	/**
	 * This method unregister context events in the context database
	 * @throws InvalidFormatException 
	 */
	@Override
	public void unregisterContextChanges(Object communityId) throws InvalidFormatException {
		//Cast IIdentity for the societies platform
		IIdentity cisID = idMgr.fromJid(communityId.toString());
		LOG.info("cisID retrieved: "+ cisID);

		try {

			Set<CtxEntityIdentifier> ctxMembersIDs = this.getCommunityMembers(cisID);
			Iterator<CtxEntityIdentifier> members = ctxMembersIDs.iterator();

			while(members.hasNext()){
				CtxEntityIdentifier member = members.next();
				LOG.info("*** Unregistering  context changes for member: "+member.getOwnerId());

				//TODO: Include here other ctx updates if necessary. For short term context
				this.ctxBroker.unregisterFromChanges(getRequestor(), this.myCtxChangeEventListener, member, CtxAttributeTypes.LOCATION_SYMBOLIC);
				this.ctxBroker.unregisterFromChanges(getRequestor(), this.myCtxChangeEventListener, member, CtxAttributeTypes.STATUS);
				//				this.ctxBroker.unregisterFromChanges(getRequestor(), this.myCtxChangeEventListener, member, CtxAttributeTypes.OCCUPATION);
				//				this.ctxBroker.unregisterFromChanges(getRequestor(), this.myCtxChangeEventListener, member, CtxAttributeTypes.ADDRESS_WORK_CITY);
				//				this.ctxBroker.unregisterFromChanges(getRequestor(), this.myCtxChangeEventListener, member, CtxAttributeTypes.ADDRESS_WORK_COUNTRY);

				//Trying to unregister for long term context
				this.ctxBroker.unregisterFromChanges(getRequestor(), this.myCtxChangeEventListener, member, CtxAttributeTypes.INTERESTS);
			}
		} catch (CtxException e1) {
			e1.printStackTrace();
		}

		//Unregistering members in the community
		try {
			CtxEntityIdentifier ctxCommunityEntityIdentifier = this.ctxBroker.retrieveCommunityEntityId(getRequestor(), cisID).get();
			if (ctxCommunityEntityIdentifier == null)
				throw new RuntimeException("Community not created, ctxCommunityEntityIdentifier is null"); 

			List<CtxIdentifier> assocList = ctxBroker.lookup(getRequestor(), ctxCommunityEntityIdentifier, CtxModelType.ASSOCIATION, CtxAssociationTypes.HAS_MEMBERS).get(); 
			if(!assocList.isEmpty()){ 
				CtxAssociationIdentifier assocID =  (CtxAssociationIdentifier) assocList.get(0);  
				this.ctxBroker.unregisterFromChanges(getRequestor(), this.myCtxChangeEventListener, assocID); 
			}
//
//
//					final CommunityCtxEntity communityEnt = (CommunityCtxEntity) ctxBroker.retrieve(getRequestor(), ctxCommunityEntityIdentifier).get();
//					CtxAssociationIdentifier hasMembersId = null;
//					for (final CtxAssociationIdentifier foundHasMembersId : communityEnt.getAssociations(CtxAssociationTypes.HAS_MEMBERS)) {
//						final CtxAssociation foundHasMembers = (CtxAssociation) ctxBroker.retrieve(getRequestor(), foundHasMembersId).get();
//						if (foundHasMembers != null && ctxCommunityEntityIdentifier.equals(foundHasMembers.getParentEntity())) {
//							hasMembersId = foundHasMembersId;
//						}
//						break;
//					}
//					if (hasMembersId == null) {
//						LOG.error("Failed to register for membership changes of CIS '" + ctxCommunityEntityIdentifier.getOwnerId() + "': HAS_MEMBERS association not found");
//					}		
//
//					LOG.debug("CtxAssociationIdentifier retrieved for update: {}", hasMembersId.toString());
//					this.ctxBroker.unregisterFromChanges(getRequestor(), this.myCtxChangeEventListener, hasMembersId);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		LOG.info("*** unregisterContextChanges success");
	}

	@Override
	public HashMap<String, HashMap<String, String[]>> retrieveLookupMembersCtxAttributes(Object communityId) throws InvalidFormatException{

		//Cast IIdentity for the societies platform
		IIdentity cisID = idMgr.fromJid(communityId.toString());
		LOG.info("cisID retrieved: "+ cisID);

		//Hashmap with format: person, context attributes
		HashMap<String, HashMap<String, String[]>> persons = new HashMap<String, HashMap<String, String[]>>();

		try {			

			Set<CtxEntityIdentifier> ctxMembersIDs = this.getCommunityMembers(cisID);

			//			Old method...

			//			ctxCommunityEntityIdentifier = this.ctxBroker.retrieveCommunityEntityId(getRequestor(), cisID).get();
			//			if (ctxCommunityEntityIdentifier == null)
			//				throw new IllegalArgumentException("Community not created, ctxCommunityEntityIdentifier is null"); 
			//			LOG.debug("communityEntityIdentifier retrieved: {}", ctxCommunityEntityIdentifier.toString());
			//
			//			CommunityCtxEntity communityEntity = (CommunityCtxEntity) this.ctxBroker.retrieve(getRequestor(), ctxCommunityEntityIdentifier).get();
			//
			//			Set<CtxEntityIdentifier> ctxMembersIDs = communityEntity.getMembers();

			LOG.debug("Community Members size: {}", ctxMembersIDs.size());

			List<String> attrTypes = new ArrayList<String>(); 

			attrTypes.add(CtxAttributeTypes.INTERESTS); 
			attrTypes.add(CtxAttributeTypes.OCCUPATION); 
			attrTypes.add(CtxAttributeTypes.WORK_POSITION); 
			attrTypes.add(CtxAttributeTypes.STATUS); 
			attrTypes.add(CtxAttributeTypes.ID); 
			attrTypes.add(CtxAttributeTypes.LOCATION_SYMBOLIC);

			for(CtxEntityIdentifier member : ctxMembersIDs){
				//Hashmap representing the context attributes
				HashMap<String, String[]> othersCtx = new HashMap<String, String[]>();

				final List<CtxIdentifier> attrIdList = new ArrayList<CtxIdentifier>(); 
				for (final String attrType : attrTypes) { 
					final List<CtxIdentifier> attrIds = this.ctxBroker.lookup(getRequestor(), member, CtxModelType.ATTRIBUTE, attrType).get(); 
					attrIdList.addAll(attrIds); 
				} 

				final List<CtxModelObject> ctxModelObjs = this.ctxBroker.retrieve(getRequestor(), attrIdList).get();

				for (final CtxModelObject modelObj : ctxModelObjs) { 
					final CtxAttribute attr = (CtxAttribute) modelObj; 
					if (attr != null) {
						//Interests needs to be split first
						if (attr.getId().getType().contains(CtxAttributeTypes.INTERESTS)){
							String[] interests = attr.getStringValue().split(",");
							othersCtx.put(attr.getId().getType(), interests); 
						}
						othersCtx.put(attr.getId().getType(), new String[]{attr.getStringValue()}); 
					} 
				} 
				//			    
				//				CtxEntity retrievedCtxEntity = (CtxEntity) this.ctxBroker.retrieve(getRequestor(), member).get();
				//				LOG.debug("Retrieved member entity: {}", retrievedCtxEntity.getId());
				//
				//				//Job Position
				//				Set<CtxAttribute> attribute = retrievedCtxEntity.getAttributes(CtxAttributeTypes.OCCUPATION);
				//				for(CtxAttribute occupation : attribute)
				//					othersCtx.put("occupation", new String[]{occupation.getStringValue()});
				//				//Company
				//				attribute = retrievedCtxEntity.getAttributes(CtxAttributeTypes.WORK_POSITION);
				//				for(CtxAttribute company : attribute)
				//					othersCtx.put("workPosition", new String[]{company.getStringValue()});
				//				//Status
				//				attribute = retrievedCtxEntity.getAttributes(CtxAttributeTypes.STATUS);
				//				for(CtxAttribute status : attribute)
				//					othersCtx.put("status", new String[]{status.getStringValue()});
				//				//Location
				//				attribute = retrievedCtxEntity.getAttributes(CtxAttributeTypes.LOCATION_SYMBOLIC);
				//				for(CtxAttribute location : attribute)
				//					othersCtx.put("locationSymbolic", new String[]{location.getStringValue()});
				//				//Interests
				//				attribute = retrievedCtxEntity.getAttributes(CtxAttributeTypes.INTERESTS);		
				//
				//				for(CtxAttribute interest : attribute) {
				//					String[] interests = interest.getStringValue().split(",");					
				//					othersCtx.put("interests", interests);
				//				}
				//
				//				attribute = retrievedCtxEntity.getAttributes(CtxAttributeTypes.NAME);
				//				for(CtxAttribute name : attribute)
				//					othersCtx.put("name", new String[]{name.getStringValue()});

				//Name or ID
				//TODO: Name FIXED!!
				/*
				attribute = retrievedCtxEntity.getAttributes(CtxAttributeTypes.NAME);
				if (attribute.isEmpty()){
					throw new NullPointerException("Name of the person cannot be null! ");
				} 
				else {
					for(CtxAttribute name : attribute) {
						//Associate the the name Jid for the ctx retrieved. Original format is joedoe.societies.local
						String getOnlyNameSubstring[] = name.getStringValue().split("\\.");
						System.out.println("CtxAttributeTypes.ID**********************  "+getOnlyNameSubstring[0]);
						persons.put(getOnlyNameSubstring[0], othersCtx);
					}
				}
				 */
				LOG.debug("Adding context info for {}",member.getOwnerId());
				persons.put(idMgr.fromJid(member.getOwnerId()).getIdentifier(), othersCtx);
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (CtxException e) {
			e.printStackTrace();
		}
		return persons;

	}

	/**
	 * Retrieve community members
	 * 
	 * @return a set of CtxEntityIdentifier of the members
	 */
	private Set<CtxEntityIdentifier> getCommunityMembers(IIdentity cisID) {

		Set<CtxEntityIdentifier> ctxMembersIDs = new HashSet<CtxEntityIdentifier>();;
		try {
			CtxEntityIdentifier ctxCommunityEntityIdentifier = this.ctxBroker.retrieveCommunityEntityId(getRequestor(), cisID).get();

			// null check for communityEntId
			if (ctxCommunityEntityIdentifier == null)
				throw new RuntimeException("Community not created, ctxCommunityEntityIdentifier is null"); 
			LOG.debug("communityEntityIdentifier retrieved: {}", ctxCommunityEntityIdentifier.toString());

			final List<CtxIdentifier> hasMembersIds = ctxBroker.lookup(getRequestor(), ctxCommunityEntityIdentifier, CtxModelType.ASSOCIATION, CtxAssociationTypes.HAS_MEMBERS).get();
			// check hasMembersIds.isEmpty() - should never happen but to be safe
			if (hasMembersIds.isEmpty())
				throw new RuntimeException("Community has no members, hasMembersIds is null"); 

			for (final CtxIdentifier foundHasMembersId : hasMembersIds) {
				final CtxAssociation foundHasMembers = (CtxAssociation) ctxBroker.retrieve(getRequestor(), foundHasMembersId).get();
				if (foundHasMembers != null && ctxCommunityEntityIdentifier.equals(foundHasMembers.getParentEntity())) {
					for (final CtxEntityIdentifier memberEntId : foundHasMembers.getChildEntities()) {
						ctxMembersIDs.add(memberEntId); // Hooray!
					}
					break;
				}
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ctxMembersIDs;
	}


	/* (non-Javadoc)
	 * @see org.societies.collabtools.context.IContextAware3pService#setListener(org.societies.api.context.event.CtxChangeEventListener)
	 */
	@Override
	public void setListener(CtxChangeEventListener myCtxChangeEventListener) {
		this.myCtxChangeEventListener = myCtxChangeEventListener;
	}


	/* (non-Javadoc)
	 * @see org.societies.collabtools.context.IContextAware3pService#getRequestor()
	 */
	@Override
	public Requestor getRequestor() {

		this.userIdentity = this.idMgr.getThisNetworkNode();
		//return  new Requestor(userIdentity);
		if (this.requestorService == null) {
			this.userIdentity = this.idMgr.getThisNetworkNode();
			this.myServiceID = this.serviceMgmt.getMyServiceId(getClass()); 
			this.requestorService = new RequestorService(userIdentity, myServiceID);
			LOG.debug("Requestor service : {}", requestorService);
			LOG.debug("ServiceID : {}", myServiceID);
		}
		return this.requestorService;
	}
	
	/**
	 * @return the idMgr
	 */
	public IIdentityManager getIdMgr() {
		return idMgr;
	}


	/* (non-Javadoc)
	 * @see org.societies.collabtools.context.IContextAware3pService#retrieveCommunityCtxAttributes(java.lang.Object)
	 */
	@Override
	public HashMap<String, String> retrieveCommunityCtxAttributes(Object communityId, final String ctxAttr) throws InvalidFormatException {
		//Cast IIdentity for the societies platform
		IIdentity cisID = idMgr.fromJid(communityId.toString());
		LOG.info("cisID retrieved for ctx attributes: {}", cisID);
		HashMap<String, String> results = new HashMap<String, String>();

		try {
			CtxEntityIdentifier ctxCommunityEntityIdentifier = this.ctxBroker.retrieveCommunityEntityId(getRequestor(), cisID).get();
			// null check for communityEntId
			if (ctxCommunityEntityIdentifier == null)
				throw new RuntimeException("Community not created, ctxCommunityEntityIdentifier is null"); 
			LOG.info("communityEntityIdentifier retrieved: {} based on cisID", ctxCommunityEntityIdentifier.toString(), cisID);			

			final List<CtxIdentifier> communityLanguageIds = ctxBroker.lookup(getRequestor(), ctxCommunityEntityIdentifier, CtxModelType.ATTRIBUTE, ctxAttr).get();
			if (!communityLanguageIds.isEmpty()) {
				CtxAttribute communityLanguages = (CtxAttribute) ctxBroker.retrieve(getRequestor(), communityLanguageIds.get(0)).get();
				// if community context attribute value is expressed as a string 
				if (CtxAttributeValueType.STRING == communityLanguages.getValueType()) {
					String communityLanguagesStringValue = communityLanguages.getStringValue();
					results.put(ctxAttr, communityLanguagesStringValue);
					// if community context attribute value is expressed as a complex value
				} else if (CtxAttributeValueType.COMPLEX == communityLanguages.getValueType()) {
					//TODO: complex values not working, english by default
					CtxAttributeComplexValue communityLanguagesComplexValue = communityLanguages.getComplexValue();
					results.put(ctxAttr, "English");
				}
			}
			//			Old method...
			//			CommunityCtxEntity communityEntity = (CommunityCtxEntity) this.ctxBroker.retrieve(getRequestor(), ctxCommunityEntityIdentifier).get();
			//
			//			Set<CtxAttribute> communityLanguages = communityEntity.getAttributes(ctxAttr);
			//			Iterator<CtxAttribute> languagesIterator = communityLanguages.iterator();
			//			while(languagesIterator.hasNext()){
			//				String ctx = languagesIterator.next().getStringValue();
			//				LOG.info("*** Community Languages: {}", ctx);
			//				results.put(ctxAttr, ctx);
			//			}

		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			e1.printStackTrace();
		} catch (CtxException e1) {
			e1.printStackTrace();
		}
		return results;
	}


	/**
	 * @return the membersList
	 */
	public Set<CtxEntityIdentifier> getMembersList() {
		return membersList;
	}


	/**
	 * @param membersList the membersList to set
	 */
	public void setMembersList(Set<CtxEntityIdentifier> membersList) {
		this.membersList = membersList;
	}
}