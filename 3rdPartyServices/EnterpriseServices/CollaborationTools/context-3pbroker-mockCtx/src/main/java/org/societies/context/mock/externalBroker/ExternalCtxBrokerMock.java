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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.attributes.MembershipCriteria;
import org.societies.api.cis.management.ICis;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.broker.ICtxBroker;
import org.societies.api.context.model.CommunityCtxEntity;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxEntityTypes;
import org.societies.api.context.model.CtxIdentifier;
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
    private static final Random r = new Random(System.currentTimeMillis());


	/** The 3P Context Broker service reference. */
    private ICommManager commMgrService;
    private ICtxBroker ctxBroker;
    private IIdentity cssOwnerId;
    private INetworkNode cssNodeId;
    private ICisOwned cisOwned = null;
    private IIdentity cisID;
    private IIdentity cssID;
    private CtxAssociation assocHasMembers;
    private Requestor requestor = null;

    private CtxEntity personIndiEnt;
    private CtxAttributeIdentifier ctxAttrLocationIdentifier;
    
    private final static String NAMEPERSON = "person#0@societies.local";


	@Autowired(required=true)
	public ExternalCtxBrokerMock(ICtxBroker externalCtxBroker, ICommManager commMgr, ICisManager cisManager) throws Exception {

	    LOG.info("*** " + getClass() + " instantiated");

	    this.ctxBroker = externalCtxBroker;
	    this.commMgrService = commMgr;

		this.cssNodeId = commMgr.getIdManager().getThisNetworkNode();
		LOG.info("*** cssNodeId = " + this.cssNodeId);

		final String cssOwnerStr = this.cssNodeId.getBareJid();
		this.cssOwnerId = commMgr.getIdManager().fromJid(cssOwnerStr);
		LOG.info("*** cssOwnerId = " + this.cssOwnerId);

		this.requestor = new Requestor(this.cssOwnerId);
		LOG.info("*** requestor = " + this.requestor);

		Hashtable<String,MembershipCriteria> cisCriteria = new Hashtable<String,MembershipCriteria>();
		
//		try {
//			//Deleting CIS-CollabTools if already exist 
//			List<ICis> listCIS = cisManager.searchCisByName("CIS-CollabTools");
////			LOG.info("*** listCIS: "+listCIS.size());
//			Iterator<ICis> itr = listCIS.iterator();
//			while (itr.hasNext()) {
//				String cisToRemove = itr.next().getCisId();
//				LOG.info("*** Deleting CIS-CollabTools = " + cisToRemove);
//				cisManager.deleteCis(cisToRemove);
//			}
//			
//			
			//Creating CIS-CollabTools
			cisOwned = cisManager.createCis("CIS-CollabTools"+new Random().nextInt(100), "Test", cisCriteria, "CSCW CIS").get();
			LOG.info("*** Cis created = CIS-CollabTools ");
//
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ExecutionException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		LOG.info("*** cisOwned " +cisOwned);
		LOG.info("*** cisOwned.getCisId() " +cisOwned.getCisId());
		String cisIDString  = cisOwned.getCisId();

	    LOG.info("*** Starting community context samples...");

	    createPersons(NAMEPERSON);	
	
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
	
	  private Requestor getRequestor(IIdentity cssOwnerId)
	  {
	    return this.requestor = new Requestor(this.cssOwnerId);
	  }


	
	public void createPersons(String namePerson) throws Exception
	    {
	
		LOG.info("*****cssID: " + this.cssID.toString());

	    this.personIndiEnt = ((CtxEntity)this.ctxBroker.createEntity(this.requestor, this.cssID, "person").get());
	    CtxAttribute interestsAttr1 = (CtxAttribute)this.ctxBroker.createAttribute(this.requestor, this.personIndiEnt.getId(), "interests").get();
	    String[] interests = getRandomInterests();
	    interestsAttr1.setStringValue(interests[0]);
				
				CtxAttribute interestsAttr2 = this.ctxBroker.createAttribute(requestor, personIndiEnt.getId() , CtxAttributeTypes.INTERESTS).get();
				interestsAttr2.setStringValue(interests[1]);
				
				CtxAttribute interestsAttr3 = this.ctxBroker.createAttribute(requestor, personIndiEnt.getId() , CtxAttributeTypes.INTERESTS).get();
				interestsAttr3.setStringValue(interests[2]);
				
				CtxAttribute nameAttr = this.ctxBroker.createAttribute(requestor, personIndiEnt.getId() , CtxAttributeTypes.NAME).get();
				nameAttr.setStringValue(namePerson);
				
				CtxAttribute aboutMeAttr = this.ctxBroker.createAttribute(requestor, personIndiEnt.getId() , CtxAttributeTypes.ABOUT).get();
				aboutMeAttr.setStringValue(getRandomWork());
				
				CtxAttribute locationAttr = this.ctxBroker.createAttribute(requestor, personIndiEnt.getId(), CtxAttributeTypes.LOCATION_SYMBOLIC).get();
			    this.ctxAttrLocationIdentifier = locationAttr.getId();
				locationAttr.setStringValue(getRandomLocation());
				
				CtxAttribute statusAttr = this.ctxBroker.createAttribute(requestor, personIndiEnt.getId(), CtxAttributeTypes.STATUS).get();
				statusAttr.setStringValue(getRandomStatus());
	
				this.ctxBroker.update(requestor, interestsAttr1);
			    this.ctxBroker.update(this.requestor, interestsAttr2);
			    this.ctxBroker.update(this.requestor, interestsAttr3);
			    this.ctxBroker.update(this.requestor, nameAttr);
			    this.ctxBroker.update(this.requestor, aboutMeAttr);
			    this.ctxBroker.update(this.requestor, locationAttr);
			    this.ctxBroker.update(this.requestor, statusAttr);

				
				 
				LOG.info("Creating person name: "+nameAttr.getStringValue());
				LOG.info("*** with Interests: "+interestsAttr1.getStringValue()+", "+interestsAttr2.getStringValue()+", "+interestsAttr3.getStringValue());
				LOG.info("*** with Job Position: "+aboutMeAttr.getStringValue());
				LOG.info("*** with Location: "+locationAttr.getStringValue());
				LOG.info("*** with Status: "+statusAttr.getStringValue());
				
			    LOG.info("*** getting  Associations");
			    String cisIDString = this.cisOwned.getCisId();
			    this.cisID = this.commMgrService.getIdManager().fromJid(cisIDString);
			    CtxEntityIdentifier ctxCommunityEntityIdentifier = (CtxEntityIdentifier)this.ctxBroker.retrieveCommunityEntityId(this.requestor, this.cisID).get();
			    LOG.info("communityEntityIdentifier retrieved: " + ctxCommunityEntityIdentifier.toString() + " based on cisID: " + this.cisID);
			    CommunityCtxEntity communityEntity = (CommunityCtxEntity)this.ctxBroker.retrieve(this.requestor, ctxCommunityEntityIdentifier).get();
			    Set assocIDSet = communityEntity.getAssociations("hasMembers");
			    if (assocIDSet.size() > 0) {
			      List assocIdList = new ArrayList(assocIDSet);
			      this.assocHasMembers = ((CtxAssociation)this.ctxBroker.retrieve(this.requestor, (CtxIdentifier)assocIdList.get(0)).get());
			    }

			    LOG.info("community members (after adding new members) " + this.assocHasMembers.getChildEntities());

			    this.assocHasMembers.addChildEntity(this.personIndiEnt.getId());

			    this.assocHasMembers = ((CtxAssociation)this.ctxBroker.update(this.requestor, this.assocHasMembers).get());

			    LOG.info("community members (after adding new members) " + this.assocHasMembers.getChildEntities());

			    LOG.info(" BEFORE UPDATE communityEnt.getID():  " + communityEntity.getId());
			    LOG.info(" BEFORE UPDATE communityEnt.getMembers():  " + communityEntity.getMembers());

			    this.ctxBroker.update(this.requestor, communityEntity);
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
	
	/**
	 * @return
	 */
	private static String getRandomLocation() {
		final String[] location={"Work","Home","Gym"};
		return location[r.nextInt(3)];
	}

	//Randomly change location for users
	private  void changeLocation() throws InvalidFormatException, InterruptedException, ExecutionException, CtxException {
	    CtxAttribute locationAttr = (CtxAttribute)this.ctxBroker.retrieve(this.requestor, this.ctxAttrLocationIdentifier).get();
	    locationAttr.setStringValue(getRandomLocation());
	    this.ctxBroker.update(this.requestor, locationAttr);

	    LOG.info("*** Change location for person: " + NAMEPERSON + ": " + locationAttr.getStringValue());
	}	
}