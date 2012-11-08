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
package org.societies.context.mock.externalBroker;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.attributes.MembershipCriteria;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.broker.ICtxBroker;
import org.societies.api.context.model.CommunityCtxEntity;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityTypes;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This class provides examples for using the internal Context Broker in OSGi. 
 */
@Service
public class ExternalCtxBrokerMock 	{

	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(ExternalCtxBrokerMock.class);
	
	/** Random reference */
    private static final Random r = new Random( System.currentTimeMillis() );


	/** The 3P Context Broker service reference. */
	private ICtxBroker externalCtxBroker;
	private ICommManager commMgrService;
	private org.societies.api.internal.context.broker.ICtxBroker internalCtxBroker;
	private IIdentity cssOwnerId;
	private INetworkNode cssNodeId;
	private ICisOwned cisOwned  = null;
	private IIdentity cisID;
	private CommunityCtxEntity communityEntity;


	private Requestor requestor = null;

	private int nrOfPersons;


	@Autowired(required=true)
	public ExternalCtxBrokerMock(ICtxBroker externalCtxBroker,org.societies.api.internal.context.broker.ICtxBroker internalCtxBroker , ICommManager commMgr,ICisManager cisManager) throws Exception {

		LOG.info("*** " + this.getClass() + " instantiated");

		this.externalCtxBroker = externalCtxBroker;
		this.internalCtxBroker = internalCtxBroker;
		this.commMgrService = commMgr;

		this.cssNodeId = commMgr.getIdManager().getThisNetworkNode();
		LOG.info("*** cssNodeId = " + this.cssNodeId);

		final String cssOwnerStr = this.cssNodeId.getBareJid();
		this.cssOwnerId = commMgr.getIdManager().fromJid(cssOwnerStr);
		LOG.info("*** cssOwnerId = " + this.cssOwnerId);

		this.requestor = new Requestor(this.cssOwnerId);
		LOG.info("*** requestor = " + this.requestor);

		Hashtable<String,MembershipCriteria> cisCriteria = new Hashtable<String,MembershipCriteria>();
		try {
			cisOwned = cisManager.createCis("testCIS-CollabTools", "cisType", cisCriteria, "CSCW CIS").get();
			LOG.info("*** Cis created = testCIS-CollabTools ");

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		LOG.info("*** cisOwned " +cisOwned);
		LOG.info("*** cisOwned.getCisId() " +cisOwned.getCisId());
		String cisIDString  = cisOwned.getCisId();

		this.cisID = commMgr.getIdManager().fromJid(cisIDString);

		//registerForContextChanges()
		communityEntity = this.internalCtxBroker.createCommunityEntity(this.cisID).get();


		LOG.info("*** Starting community context examples...");
		// creation of communities is only allowed by platform services
		try {
			this.createPersons(5);
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		
//		this.ca3pService.registerForContextChanges(cisID);
//
//		this.ca3pService.retrieveLookupCommunityEntAttributes(cisID);
		
	
		new Thread()
		{
			public void run() {
				while (true) {
					try {
						//30 seg
						Thread.sleep(30000);
						changeLocation();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvalidFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (CtxException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}.start();

	}

	
	private String[] getRandomInterests() {
		final String[] interests={"bioinformatics", "web development", "semantic web", "requiremens analysis", "system modeling", 
				"project planning", "project management", "software engineering", "software development", "technical writing"};
		Set<String> finalInterests = new HashSet<String>();
		for(int i=0; i<3; i++){
			String temp = interests[r.nextInt(interests.length)];
			//Check if duplicated
			if (!finalInterests.contains(temp))
				finalInterests.add(temp);
			else
				i--;
		}
		return finalInterests.toArray(new String[0]);
	}

	private String getRandomStatus() {
		final String[] status={"Online","Busy","Away"};
		return status[r.nextInt(3)];
	}
	
	private String getRandomWork() {
		final String[] work={"Manager","Developer","Beta Tester"};
		return work[r.nextInt(3)];
	}
	
	public void createPersons(int nrOfPersons) throws Exception
    {

		this.nrOfPersons = nrOfPersons;
        for ( int i = 0; i < nrOfPersons; i++ )
        {    	
			IIdentity cssID =  this.commMgrService.getIdManager().fromJid("person#"+i+"@societies.local");
			IndividualCtxEntity indiEnt = this.internalCtxBroker.createIndividualEntity(cssID, CtxEntityTypes.PERSON).get();
			CtxAttribute interestsAttr1 = this.internalCtxBroker.createAttribute(indiEnt.getId() , CtxAttributeTypes.INTERESTS).get();
			String [] interests = getRandomInterests();
			interestsAttr1.setStringValue(interests[0]);
			
			CtxAttribute interestsAttr2 = this.internalCtxBroker.createAttribute(indiEnt.getId() , CtxAttributeTypes.INTERESTS).get();
			interestsAttr2.setStringValue(interests[1]);
			
			CtxAttribute interestsAttr3 = this.internalCtxBroker.createAttribute(indiEnt.getId() , CtxAttributeTypes.INTERESTS).get();
			interestsAttr3.setStringValue(interests[2]);
			
			CtxAttribute nameAttr = this.internalCtxBroker.createAttribute(indiEnt.getId() , CtxAttributeTypes.NAME).get();
			nameAttr.setStringValue("person#"+i+"@societies.local");
			
			CtxAttribute aboutMeAttr = this.internalCtxBroker.createAttribute(indiEnt.getId() , CtxAttributeTypes.ABOUT).get();
			aboutMeAttr.setStringValue(getRandomWork());
			
			CtxAttribute locationAttr = this.internalCtxBroker.createAttribute(indiEnt.getId(), CtxAttributeTypes.LOCATION_SYMBOLIC).get();
			locationAttr.setStringValue(getRandomLocation());
			
			CtxAttribute statusAttr = this.internalCtxBroker.createAttribute(indiEnt.getId(), CtxAttributeTypes.STATUS).get();
			statusAttr.setStringValue(getRandomStatus());

			this.internalCtxBroker.update(interestsAttr1);
			this.internalCtxBroker.update(interestsAttr2);
			this.internalCtxBroker.update(interestsAttr3);
			this.internalCtxBroker.update(nameAttr);
			this.internalCtxBroker.update(aboutMeAttr);
			this.internalCtxBroker.update(locationAttr);
			this.internalCtxBroker.update(statusAttr);
			
			 
			LOG.info("Creating person name: "+nameAttr.getStringValue());
			LOG.info("*** with Interests: "+interestsAttr1.getStringValue()+", "+interestsAttr2.getStringValue()+", "+interestsAttr3.getStringValue());
			LOG.info("*** with Job Position: "+aboutMeAttr.getStringValue());
			LOG.info("*** with Location: "+locationAttr.getStringValue());
			LOG.info("*** with Status: "+statusAttr.getStringValue());
			
//			//Setting context for the framework
//			this.ca3pService.getCtxSub().setContext("name", nameAttr.getStringValue(), nameAttr.getStringValue());
//			this.ca3pService.getCtxSub().setContext("work", aboutMeAttr.getStringValue(), nameAttr.getStringValue());
//			this.ca3pService.getCtxSub().setContext("location", locationAttr.getStringValue(), nameAttr.getStringValue());
//			this.ca3pService.getCtxSub().setContext("status", statusAttr.getStringValue(), nameAttr.getStringValue());
//			//Setting Interests
//			String [] interestsArray = new String[]{interestsAttr1.getStringValue(), interestsAttr2.getStringValue(), interestsAttr3.getStringValue()};
//			this.ca3pService.getCtxSub().setContext("interests", interestsArray, nameAttr.getStringValue());
//			
			communityEntity.addMember(indiEnt.getId());
			cisOwned.addMember("person#"+i+"@societies.local", "participant");
        }
		this.internalCtxBroker.update(communityEntity);
    }
	
	//Randomly change location for users
	private  void changeLocation() throws InvalidFormatException, InterruptedException, ExecutionException, CtxException {
		int numberPerson = r.nextInt(nrOfPersons);
		IIdentity cssID =  this.commMgrService.getIdManager().fromJid("person#"+numberPerson +"@societies.local");
		IndividualCtxEntity indiEnt = this.internalCtxBroker.createIndividualEntity(cssID, CtxEntityTypes.PERSON).get();
		CtxAttribute locationAttr = this.internalCtxBroker.createAttribute(indiEnt.getId(), CtxAttributeTypes.LOCATION_SYMBOLIC).get();
		locationAttr.setStringValue(getRandomLocation());
		this.internalCtxBroker.update(locationAttr);
//		try {
//			this.ca3pService.getCtxSub().setContext("location", locationAttr.getStringValue(), "person#"+numberPerson+"@societies.local");
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	
		LOG.info("*** Change location for person#"+numberPerson+": "+locationAttr.getStringValue());
		
//		CtxEntity retrievedCtxEntity = (CtxEntity) this.internalCtxBroker.retrieve(requestor, member).get();
//		Set<CtxAttribute> location = retrievedCtxEntity.getAttributes(CtxAttributeTypes.LOCATION_SYMBOLIC);
//		CtxAttribute ctxAttr = (CtxAttribute) this.ctxBroker.retrieve(requestorService, location.iterator().next().getId()).get();
//
//		ctxAttr.setStringValue("newDeviceLocation");
//		ctxAttr = (CtxAttribute) this.ctxBroker.update(requestorService, ctxAttr).get();

	}

	/**
	 * @return
	 */
	private static String getRandomLocation() {
		final String[] location={"Work","Home","Gym"};
		return location[r.nextInt(3)];
	}
	


//	public void createCtx() {
//		
//		Set<CtxEntityIdentifier> ctxMembersIDs = communityEntity.getMembers();
//		Iterator<CtxEntityIdentifier> members = ctxMembersIDs.iterator();
//		
//		while(members.hasNext()){
//			CtxEntityIdentifier member = members.next();
//			// 1b. Register listener by specifying the context attribute scope and type
//			CtxEntity retrievedCtxEntity = (CtxEntity) this.externalCtxBroker.retrieve(requestor, member).get();
//			Set<CtxAttribute> ctxAtrb = retrievedCtxEntity.getAttributes(CtxAttributeTypes.LOCATION_SYMBOLIC);
//			locationAttr = (CtxAttribute) this.externalCtxBroker.retrieve(requestor, ctxAtrb.iterator().next().getId()).get();
//			
//			retrievedCtxEntity = (CtxEntity) this.externalCtxBroker.retrieve(requestor, member).get();
//			ctxAtrb = retrievedCtxEntity.getAttributes(CtxAttributeTypes.INTERESTS);
//			interestsAttr1 = (CtxAttribute) this.externalCtxBroker.retrieve(requestor, ctxAtrb.iterator().next().getId()).get();
//			
//			retrievedCtxEntity = (CtxEntity) this.externalCtxBroker.retrieve(requestor, member).get();
//			ctxAtrb = retrievedCtxEntity.getAttributes(CtxAttributeTypes.INTERESTS);
//			interestsAttr2 = (CtxAttribute) this.externalCtxBroker.retrieve(requestor, ctxAtrb.iterator().next().getId()).get();
//			
//			retrievedCtxEntity = (CtxEntity) this.externalCtxBroker.retrieve(requestor, member).get();
//			ctxAtrb = retrievedCtxEntity.getAttributes(CtxAttributeTypes.INTERESTS);
//			interestsAttr3 = (CtxAttribute) this.externalCtxBroker.retrieve(requestor, ctxAtrb.iterator().next().getId()).get();
//			
//			retrievedCtxEntity = (CtxEntity) this.externalCtxBroker.retrieve(requestor, member).get();
//			ctxAtrb = retrievedCtxEntity.getAttributes(CtxAttributeTypes.NAME);
//			locationAttr = (CtxAttribute) this.externalCtxBroker.retrieve(requestor, ctxAtrb.iterator().next().getId()).get();
//			
//			retrievedCtxEntity = (CtxEntity) this.externalCtxBroker.retrieve(requestor, member).get();
//			ctxAtrb = retrievedCtxEntity.getAttributes(CtxAttributeTypes.ABOUT);
//			aboutMeAttr = (CtxAttribute) this.externalCtxBroker.retrieve(requestor, ctxAtrb.iterator().next().getId()).get();
//			
//			retrievedCtxEntity = (CtxEntity) this.externalCtxBroker.retrieve(requestor, member).get();
//			ctxAtrb = retrievedCtxEntity.getAttributes(CtxAttributeTypes.STATUS);
//			statusAttr = (CtxAttribute) this.externalCtxBroker.retrieve(requestor, ctxAtrb.iterator().next().getId()).get();
//			
//
//			locationAttr = (CtxAttribute) this.externalCtxBroker.update(requestor, locationAttr).get();
//
//			interestsAttr1 = (CtxAttribute) this.externalCtxBroker.update(requestor, interestsAttr1).get();
//
//			interestsAttr2 = (CtxAttribute) this.externalCtxBroker.update(requestor, interestsAttr2).get();
//			
//			interestsAttr3 = (CtxAttribute) this.externalCtxBroker.update(requestor, interestsAttr3).get();
//
//			aboutMeAttr = (CtxAttribute) this.externalCtxBroker.update(requestor, aboutMeAttr).get();
//			
//			statusAttr = (CtxAttribute) this.externalCtxBroker.update(requestor, statusAttr).get();
//		}
//		
//	}

	
	
}