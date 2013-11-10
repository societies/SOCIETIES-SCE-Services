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
package org.societies.thirdpartyservices.city.sourceapp;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.interfaces.IFeatureServer;
import org.societies.api.context.CtxException;
import org.societies.api.context.broker.ICtxBroker;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationTypes;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxEntityTypes;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;

/**
 * This Class is part of a project to test the suitability of using the Societies framework for potential use on other projects
 * SourceTestImpl is used to write test data to the DB and for creating associations between the data.
 * Later it could be used to change the associations between the data and the Client Test class could be used to see if those changes are picked up
 *
 * @author rdaviesX
 *
 */
public class SourceTestImpl implements ISourceTest, IFeatureServer {

	private static final List<String> NAMESPACES = Collections.unmodifiableList(
			Arrays.asList("http://societies.org/api/schema/contexttest/monitoring",
					"http://societies.org/api/schema/contexttest/feedback"));
	private static final List<String> PACKAGES = Collections.unmodifiableList(
			Arrays.asList("org.societies.api.schema.contexttest.monitoring",
					"org.societies.api.schema.contexttest.feedback"));
	
	private static final String USER_DESC = "a cool CSS user";
	
	/** The 3P Context Broker service reference. */
	private ICtxBroker ctxBroker;
	
	private ICommManager commManagerService;
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	public void RunInit() {
		log.info("CTXTTEST:Source: runInit starting v1");
		
		try {
			getCommManagerService().register(this);
		} catch (CommunicationException e1) {
			log.info("CTXTTEST:Source: CommunicationException: " + e1);
		}
		
		IIdentity targetId = commManagerService.getIdManager().getThisNetworkNode();

		// The IIdentity of the requestor of context data
		IIdentity requestorId = commManagerService.getIdManager().getThisNetworkNode();
		
		Requestor requestor = new Requestor(requestorId);

		// retrieve the CtxEntityIdentifier of the CSS owner context entity based on IIdentity
		CtxEntityIdentifier ownerEntityId;
		try {
			Future<CtxEntityIdentifier> futureCtxEntId = this.ctxBroker.retrieveIndividualEntityId(requestor, targetId);
			ownerEntityId = futureCtxEntId.get();

			CtxEntity retrievedOwnerCtxEntity = (CtxEntity) this.ctxBroker.retrieve(requestor, ownerEntityId).get();
			
			boolean haveAboutMe = false;
			for (CtxAttribute ownerAttribute : retrievedOwnerCtxEntity.getAttributes()) {
				if (ownerAttribute.getType().equalsIgnoreCase(CtxAttributeTypes.ABOUT)) {
					if (USER_DESC.equals(ownerAttribute.getStringValue())) {
						if (haveAboutMe == false) {
							log.info("CTXTTEST:Source: found one user desc");
							haveAboutMe = true;
						} else {
							log.info("CTXTTEST:Source: found another user desc, removing");
							this.ctxBroker.remove(requestor, ownerAttribute.getId());
						}
					}
				}
			}
			
			if (haveAboutMe == false) {
				log.info("CTXTTEST:Source: found no user desc, adding");
				// create a context attribute under the CSS owner context entity	
				CtxAttribute aboutMeAttr = this.ctxBroker.createAttribute(requestor, ownerEntityId, CtxAttributeTypes.ABOUT).get();
				// assign a String value to the attribute
				aboutMeAttr.setStringValue(USER_DESC);
				aboutMeAttr.setValueType(CtxAttributeValueType.STRING);
				// update the attribute in the Context DB
				aboutMeAttr = (CtxAttribute) this.ctxBroker.update(requestor, aboutMeAttr).get();
			}
			
			// create an initial set of context entity data
			CtxEntity initialDevice = createEntityTypeWithNameAttribute(targetId, requestor, CtxEntityTypes.DEVICE, "device1234");
			
			CtxEntity user1 = createEntityTypeWithNameAttribute(targetId, requestor, CtxEntityTypes.PERSON, "Joe Bloggs");
			CtxEntity user2 = createEntityTypeWithNameAttribute(targetId, requestor, CtxEntityTypes.PERSON, "John Doe");
			
			CtxEntity garden1 = createEntityTypeWithNameAttribute(targetId, requestor, CtxEntityTypes.SERVICE, "Community Garden 1");
			CtxEntity garden2 = createEntityTypeWithNameAttribute(targetId, requestor, CtxEntityTypes.SERVICE, "Community Garden 2");
			
			CtxEntity area1 = createEntityTypeWithNameAttribute(targetId, requestor, CtxEntityTypes.DEVICE, "Dublin 1");
			CtxEntity area2 = createEntityTypeWithNameAttribute(targetId, requestor, CtxEntityTypes.DEVICE, "Dublin 2");
			CtxEntity area3 = createEntityTypeWithNameAttribute(targetId, requestor, CtxEntityTypes.DEVICE, "Dublin 3");
			CtxEntity area4 = createEntityTypeWithNameAttribute(targetId, requestor, CtxEntityTypes.DEVICE, "Dublin 4");
			
			// create an initial set of context associations
			CtxAssociation isLocatedInAssoc1 = this.ctxBroker.createAssociation(requestor, targetId, CtxAssociationTypes.HAS_MEMBERS).get();
			isLocatedInAssoc1.setParentEntity(area1.getId());
			isLocatedInAssoc1.addChildEntity(user1.getId());
			
			CtxAssociation isLocatedInAssoc2 = this.ctxBroker.createAssociation(requestor, targetId, CtxAssociationTypes.HAS_MEMBERS).get();
			isLocatedInAssoc2.setParentEntity(area2.getId());
			isLocatedInAssoc2.addChildEntity(user2.getId());
			
			CtxAssociation isLocatedInAssoc3 = this.ctxBroker.createAssociation(requestor, targetId, CtxAssociationTypes.HAS_MEMBERS).get();
			isLocatedInAssoc3.setParentEntity(area3.getId());
			isLocatedInAssoc3.addChildEntity(garden1.getId());
			
			CtxAssociation isLocatedInAssoc4 = this.ctxBroker.createAssociation(requestor, targetId, CtxAssociationTypes.HAS_MEMBERS).get();
			isLocatedInAssoc4.setParentEntity(area4.getId());
			isLocatedInAssoc4.addChildEntity(garden2.getId());
			
			
			// The idea here would be to now change those associations, for example to represent a user moving from one Dublin Area to the next, 
			// and seeing if those changes are picked up by ClientTest
			
			
			// PROBLEM: it looks like the associations that were added here are not being added properly
			
			
		} catch (InterruptedException e) {
			e.printStackTrace();
			log.info("CTXTTEST:Source: Exception: " + e);
		} catch (ExecutionException e) {
			e.printStackTrace();
			log.info("CTXTTEST:Source: Exception: " + e);
		} catch (CtxException e) {
			e.printStackTrace();
			log.info("CTXTTEST:Source: Exception: " + e);
		}

		log.info("CTXTTEST:Source: runInit end");
	}


	/**
	 * Creates a context Entity with the supplied Name attribute, This method also checks if there is already such an entity, and does not insert a 
	 * new one if one exists already
	 * 
	 * @param targetId
	 * @param requestor
	 * @return 
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws CtxException
	 */
	private CtxEntity createEntityTypeWithNameAttribute(IIdentity targetId, Requestor requestor, String entityType, String deviceNameString) throws InterruptedException, ExecutionException, CtxException {
		
		boolean haveAlready = false;
		CtxEntity retrievedCtxEntity = null;
		
		List<CtxIdentifier> idsEntities = this.ctxBroker.lookup(requestor, targetId, CtxModelType.ENTITY, entityType).get();
		
		// first look through the existing context entities for a possible match
		for(CtxIdentifier ctxIdentifier : idsEntities){
			CtxEntityIdentifier deviceCtxEntIdentifier = (CtxEntityIdentifier) ctxIdentifier;
			retrievedCtxEntity = (CtxEntity) this.ctxBroker.retrieve(requestor, deviceCtxEntIdentifier).get();
			Set<CtxAttribute> ctxAttrSet = retrievedCtxEntity.getAttributes();
			
			for(CtxAttribute attrib : ctxAttrSet){
				if(CtxAttributeTypes.NAME.equals(attrib.getType()) && (attrib.getStringValue() != null) && (attrib.getStringValue().equals(deviceNameString))){
					if(haveAlready == false){
						haveAlready = true;
						log.info("CTXTTEST:Source: Have an entitytype: " + entityType + ", with name " + deviceNameString + " already");
					} else {
						// if more than one entity of this type exists, then delete the additional ones
						log.info("CTXTTEST:Source: Have second entitytype: " + entityType + ", with name " + deviceNameString + " Removing...");
						this.ctxBroker.remove(requestor, attrib.getId());
						this.ctxBroker.remove(requestor, retrievedCtxEntity.getId());
					}

				}
			}
		}
		
		CtxEntity deviceEntity = null;
		
		// only insert a new entity if one does not already exist
		if(haveAlready == false){
			log.info("CTXTTEST:Source: Have NO entitytype: " + entityType + ", with name " + deviceNameString + " so far, so adding");
			
			// create a context entity that represents a device
			CtxEntity createdDeviceEntity = this.ctxBroker.createEntity(requestor, targetId, entityType).get();
			// get the context identifier of the created entity
			CtxEntityIdentifier deviceEntityId = createdDeviceEntity.getId();
	
			// create an attribute to model the name of the device entity
			CtxAttribute deviceNameAttr = this.ctxBroker.createAttribute(requestor, deviceEntityId, CtxAttributeTypes.NAME).get();
	
			// assign a String value to the attribute 
			deviceNameAttr.setStringValue(deviceNameString);
			deviceNameAttr.setValueType(CtxAttributeValueType.STRING);
			// update the attribute in the Context DB
			deviceNameAttr = (CtxAttribute) this.ctxBroker.update(requestor, deviceNameAttr).get();
			
			deviceEntity = createdDeviceEntity;
		} else {
			deviceEntity = retrievedCtxEntity;
		}
		
		return deviceEntity;
	}
	

	/* 
	 * method exposed by the interface, not currently being used
	 * @see org.societies.thirdpartyservices.city.sourceapp.ISourceTest#doSomething()
	 */
	public void doSomething() {
		log.info("CTXTTEST:Source: SourceTest doSomething method");
		
		String ownerId = "ownerId";
		String type = "type";
		Long objectNumber = (long) 1234;
		CtxEntityIdentifier ceId = new CtxEntityIdentifier(ownerId, type, objectNumber);
		
		CtxEntity entity = new CtxEntity(ceId);
	}

	public SourceTestImpl() {
		log.info("CTXTTEST:Source: SourceTest bundle instantiated.");
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

	/** Put your functionality here if there IS a return object */
	@Override
	public Object getQuery(Stanza stanza, Object messageBean) throws XMPPError {
		// TODO Auto-generated method stub
		return null;
	}

	/**Returns the list of package names of the message beans you'll be passing*/
	@Override
	public List<String> getJavaPackages() {
		return PACKAGES;
	}
	
	/**Returns the list of namespaces for the message beans you'll be passing*/
	@Override
	public List<String> getXMLNamespaces() {
		return NAMESPACES;
	}

	/** Put your functionality here if there is NO return object, ie, VOID */
	@Override
	public void receiveMessage(Stanza arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}

	/** Put your functionality here if there IS a return object and you are updating also */
	@Override
	public Object setQuery(Stanza arg0, Object arg1) throws XMPPError {
		// TODO Auto-generated method stub
		return null;
	}
	
}
