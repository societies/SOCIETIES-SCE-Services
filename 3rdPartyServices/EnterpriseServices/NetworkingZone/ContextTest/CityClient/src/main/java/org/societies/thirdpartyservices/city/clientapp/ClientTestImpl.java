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
package org.societies.thirdpartyservices.city.clientapp;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.broker.ICtxBroker;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationIdentifier;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxEntityTypes;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;

/**
 * This Class is part of a project to test the suitability of using the Societies framework for potential use on other projects
 * ClientTestImpl is for reading in the data created in the class CitiSource, subscribing for changes to the data and reporting 
 * when the data changes
 *
 * @author rdaviesX
 *
 */
public class ClientTestImpl implements IClientTest {
	
	private CtxIdentifier myCtxAttributeStringIdentifier;

	/** The 3P Context Broker service reference. */
	private ICtxBroker ctxBroker;
	
	// The IIdentity of the context data owner, i.e. the target CSS (or CIS)
	IIdentity targetId;
	
	private ICommManager commManagerService;

	private Logger log = LoggerFactory.getLogger(this.getClass());

	
	public void RunClientInit() {
		log.info("CTXTTEST:Client: ClientTestImpl - RunClientInit starting v1");
		
		IIdentity targetId = commManagerService.getIdManager().getThisNetworkNode();

		// The IIdentity of the requestor of context data
		IIdentity requestorId = commManagerService.getIdManager().getThisNetworkNode();
		Requestor requestor = new Requestor(requestorId);

		// retrieve the CtxEntityIdentifier of the CSS owner context entity based on IIdentity
		CtxEntityIdentifier ownerEntityId;
		
		try {			
			ownerEntityId = this.ctxBroker.retrieveIndividualEntityId(requestor, targetId).get();
			CtxEntity retrievedOwnerCtxEntity = (CtxEntity) this.ctxBroker.retrieve(requestor, ownerEntityId).get();
			
			Set<CtxAttribute> ownerAttribs = retrievedOwnerCtxEntity.getAttributes();
			
			log.info("CTXTTEST:Client: RunClientInit: owner: type =  " + retrievedOwnerCtxEntity.getType() + ", id = " + retrievedOwnerCtxEntity.getId() + ", has " + ownerAttribs.size() + " attributes... listing...");
			
			for(CtxAttribute ownerAttribute : ownerAttribs){
				log.info("CTXTTEST:Client: RunClientInit: owner-attribute: type =  " + ownerAttribute.getType() + ", str val = " + ownerAttribute.getStringValue());
			}
			
            listAllEntitesOfType(targetId, requestor, CtxEntityTypes.DEVICE);
            listAllEntitesOfType(targetId, requestor, CtxEntityTypes.PERSON); 
            listAllEntitesOfType(targetId, requestor, CtxEntityTypes.SERVICE); 
            
            // PROBLEM: 
            // The Associations that we tried to create in the SourceTest don't seem to have been created properly
            
            // NEXT STEPS:
            // subscribe for changes in the CtxAssociations
            // report any changes to the user
            
		} catch (InterruptedException e) {
			e.printStackTrace();
			log.info("CTXTTEST:Client: Exception: " + e);
		} catch (ExecutionException e) {
			e.printStackTrace();
			log.info("CTXTTEST:Client: Exception: " + e);
		} catch (CtxException e) {
			e.printStackTrace();
			log.info("CTXTTEST:Client: Exception: " + e);
		}
	
		log.info("CTXTTEST:Client: ClientTestImpl - RunClientInit finished");
	}


	/**
	 * List all of the entities of the specified type, then go into detail and list that entities Association and Attributes
	 * 
	 * @param targetId
	 * @param requestor
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws CtxException
	 */
	private void listAllEntitesOfType(IIdentity targetId, Requestor requestor, String type) throws InterruptedException, ExecutionException, CtxException {
		List<CtxIdentifier> idsEntities = this.ctxBroker.lookup(requestor, targetId, CtxModelType.ENTITY, type).get();
		
		log.info("CTXTTEST:Client: listAllEntitesOfType: Type= " + type + ", idsEntities.size() =  " + idsEntities.size());
		
		for(CtxIdentifier ctxIdentifier : idsEntities){

		    // the retrieved identifier is used in order to retrieve the context model object (CtxEntity)
		    CtxEntity retrievedCtxEntity = (CtxEntity) this.ctxBroker.retrieve(requestor, ctxIdentifier).get();

		    // Now it is possible to access the CtxAttributes assigned to retrieved CtxEntity. In this example the location of the device entity will be retrieved.
		    listEntityAndAttributes(retrievedCtxEntity);
		    
		    Set<CtxAssociationIdentifier> idsAssociations = retrievedCtxEntity.getAssociations();
		    
		    log.info("CTXTTEST:Client: listAllEntitesOfType: have entity: " + retrievedCtxEntity + ", Checking for associations,  have idsAssociations.size() =  " + idsAssociations.size());
		    
		    for(CtxIdentifier ctxAsocIdent: idsAssociations){
		    	CtxModelObject assocObject = this.ctxBroker.retrieve(requestor, ctxAsocIdent).get();
		    	
		    	if(assocObject instanceof CtxAssociation){
		    		CtxAssociation ctxAssociation = (CtxAssociation) assocObject;
		    		
		        	CtxEntityIdentifier parentCtxEntityIdentifier = ctxAssociation.getParentEntity();
		        	Set<CtxEntityIdentifier> childrenEntities = ctxAssociation.getChildEntities();
		        	
		        	log.info("CTXTTEST:Client: listAllEntitesOfType: ctxAssociation, id " + retrievedCtxEntity.getId() + " of type: " + ctxAssociation.getType() + ", parentType = " + parentCtxEntityIdentifier.getType() + ", num children = " + childrenEntities.size());

		        	for(CtxEntityIdentifier childEntityId : childrenEntities){
		        		CtxEntity childCtxEntity = (CtxEntity) this.ctxBroker.retrieve(requestor, childEntityId).get();
		        		
		        		listEntityAndAttributes(childCtxEntity);
		        	}
		    		
		    	} else {
		    		log.info("CTXTTEST:Client: listAllEntitesOfType: ERROR: Should have an association but dont!!! , type = " + assocObject.getType() + ", obj = " + assocObject.toString());
		    	}
		    	
		    }
		}
	}


	/**
	 * show the details of an Entity including all of its Attributes
	 * 
	 * @param someCtxEntity
	 */
	private void listEntityAndAttributes(CtxEntity someCtxEntity) {
		
		Set<CtxAttribute> ctxAttrSet = someCtxEntity.getAttributes();
		
		log.info("CTXTTEST:Client: listEntityAndAttributes: CtxEntity: " + someCtxEntity + ", ctxEntity id " + someCtxEntity.getId() + " of type: " + someCtxEntity.getType() + ", has " + ctxAttrSet.size() + " attributes");

		if(ctxAttrSet.size()>0) {
		    List<CtxAttribute> ctxAttrList = new ArrayList<CtxAttribute>(ctxAttrSet);
		    CtxAttribute ctxAttrLocation = ctxAttrList.get(0);
		    log.info("CTXTTEST:Client: listEntityAndAttributes: CtxEntity type" + ctxAttrLocation.getType()        + " with value: " + ctxAttrLocation.getStringValue());
		}
	}
	
	private void SubscribForAndReactToContextChangeEvents(Requestor requestor, CtxEntityIdentifier myCtxEntityIdentifier) {
		// The ICtxBroker interface provides methods for registering CtxChangeEventListeners in order to listen for context change events.
		// There are two ways to subscriber for context change event notification:

		try {
			// 1a. Register listener by specifying the context attribute identifier
			this.ctxBroker.registerForChanges(requestor, new MyCtxChangeEventListener(), myCtxAttributeStringIdentifier);

			// 1b. Register listener by specifying the context attribute scope and type
			this.ctxBroker.registerForChanges(requestor, new MyCtxChangeEventListener(), myCtxEntityIdentifier, "DeviceID");
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void doSomething() {
		log.info("CTXTTEST:Client: ClientTestImpl doSomething.");
	}

	public ClientTestImpl() {
		log.info("CTXTTEST:Client: ClientTestImpl bundle instantiated.");
	}
	
	/**
	 * @return the cxtBrokerService
	 */
	public ICtxBroker getCxtBrokerService() {
		return ctxBroker;
	}

	/**
	 * @param cxtBrokerService the cxtBrokerService to set
	 */
	public void setCxtBrokerService(ICtxBroker ictxBroker) {
		this.ctxBroker = ictxBroker;
	}
	
	/**
	 * @return the commManagerService
	 */
	public ICommManager getCommManagerService() {
		return commManagerService;
	}

	/**
	 * @param commManagerService the commManagerService to set
	 */
	public void setCommManagerService(ICommManager commManagerService) {
		this.commManagerService = commManagerService;
	}
	
}
