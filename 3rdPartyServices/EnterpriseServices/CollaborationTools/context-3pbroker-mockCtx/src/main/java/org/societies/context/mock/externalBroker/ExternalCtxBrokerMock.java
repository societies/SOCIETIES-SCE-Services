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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.attributes.MembershipCriteria;
import org.societies.api.cis.attributes.Rule;
import org.societies.api.cis.management.ICis;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.broker.ICtxBroker;
import org.societies.api.context.model.CommunityCtxEntity;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationIdentifier;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
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
	private INetworkNode cssNodeId;
	private ICisOwned cisOwned = null;
	private IIdentity cisID;
	private IIdentity cssID;
	private CtxAssociation assocHasMembers;
	private Requestor requestor = null;

	private CtxEntityIdentifier personIndiEnt;
	private CtxAttributeIdentifier ctxAttrLocationIdentifier;

	//    private final static String NAMEPERSON = "admin@societies.local";
	private final static String NAMEPERSON = "Chris Lima";


	@Autowired(required=true)
	public ExternalCtxBrokerMock(ICtxBroker externalCtxBroker, ICommManager commMgr, ICisManager cisManager) throws Exception {

		LOG.info("*** " + getClass() + " instantiated");

		this.ctxBroker = externalCtxBroker;
		this.commMgrService = commMgr;

		this.cssNodeId = commMgr.getIdManager().getThisNetworkNode();
		LOG.info("*** cssNodeId = " + this.cssNodeId);

		final String cssOwnerStr = this.cssNodeId.getBareJid();
		this.cssID = this.commMgrService.getIdManager().fromJid(cssOwnerStr);
		LOG.info("*** cssID = " + this.cssID);

		this.requestor = getRequestor(this.cssID);
		LOG.info("*** requestor = " + this.requestor);

		Hashtable<String,MembershipCriteria> cisCriteria = new Hashtable<String,MembershipCriteria>();


		//		MembershipCriteria m = new MembershipCriteria();
		//		
		//	
		//			Rule r = new Rule("equals",new ArrayList<String>(Arrays.asList("married")));
		//			m.setRule(r);
		//			cisCriteria.put(CtxAttributeTypes.STATUS, m);
		//			r = new Rule("equals",new ArrayList<String>(Arrays.asList("Brazil")));
		//			m.setRule(r);
		//			cisCriteria.put(CtxAttributeTypes.ADDRESS_HOME_COUNTRY, m);
		//		

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
		//create ciss
		//		cisManager.createCis("CIS-CollabTools"+new Random().nextInt(100), "Test", cisCriteria, "CSCW CIS").get();
		//		cisManager.createCis("CIS-CollabTools"+new Random().nextInt(100), "Test", cisCriteria, "CSCW CIS").get();
		//		cisManager.createCis("CIS-CollabTools"+new Random().nextInt(100), "Test", cisCriteria, "CSCW CIS").get();
		//		cisManager.createCis("CIS-CollabTools"+new Random().nextInt(100), "Test", cisCriteria, "CSCW CIS").get();

		List<ICis> cisList = new ArrayList<ICis>();
		cisList = cisManager.getCisList();
		if (cisList.size() > 0){
			int size = cisList.size();
			LOG.info("there are "+size+" CIS - last one is : "+(cisList.get(size-1)).getName() + " \t id " + (cisList.get(size-1)).getCisId());
			this.cisID = commMgr.getIdManager().fromJid(cisList.get(size-1).getCisId());
		}
		else{
			//Creating CIS-CollabTools
			this.cisOwned = cisManager.createCis("CIS-CollabTools"+new Random().nextInt(100), "Test", cisCriteria, "CSCW CIS").get();
			this.cisID = this.commMgrService.getIdManager().fromJid(cisOwned.getCisId());
			LOG.info("*** created CIS = CIS-CollabTools ");
		}

		this.cisOwned = cisManager.createCis("CIS-CollabTools", "Test", cisCriteria, "CSCW CIS").get();
		cisOwned.setCisType("RICH");
		//		List<ICis> listCIS = cisManager.searchCisByName("CIS-CollabTools");
		//		listCIS.get(0).getCisId();
		cisOwned.addMember("admin.societies.local", "participant");
		this.cisID = this.commMgrService.getIdManager().fromJid(cisOwned.getCisId());
		LOG.info("*** created CIS = CIS-CollabTools ");

		//
		//		} catch (InterruptedException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		} catch (ExecutionException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}

		//		String cisIDString  = cisOwned.getCisId();

		LOG.info("*** Starting community context samples...");

		createPersons(NAMEPERSON);

		new Thread()
		{
			public void run() {
				while (true) {
					try {
						//30 seg
						Thread.sleep(10000);
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
		return this.requestor = new Requestor(this.cssID);
	}



	public void createPersons(String namePerson) throws Exception
	{
		//	    this.personIndiEnt = ((CtxEntity)this.ctxBroker.createEntity(this.requestor, this.cssID, "person").get());

		this.personIndiEnt = this.ctxBroker.retrieveIndividualEntityId(this.requestor, this.cssID).get();
		final IndividualCtxEntity operatorCtxEnt = (IndividualCtxEntity) this.ctxBroker.retrieve(this.requestor,personIndiEnt).get();
		
		CtxAttribute interestsAttr1 = (CtxAttribute)this.ctxBroker.createAttribute(this.requestor, this.personIndiEnt, "interests").get();
		String[] interests = getRandomInterests();
		interestsAttr1.setStringValue(interests[0]);

		CtxAttribute interestsAttr2 = this.ctxBroker.createAttribute(requestor, personIndiEnt , CtxAttributeTypes.INTERESTS).get();
		interestsAttr2.setStringValue(interests[1]);

		CtxAttribute interestsAttr3 = this.ctxBroker.createAttribute(requestor, personIndiEnt , CtxAttributeTypes.INTERESTS).get();
		interestsAttr3.setStringValue(interests[2]);
		
		CtxAttribute nameAttr = addAttribute(namePerson, operatorCtxEnt, CtxAttributeTypes.NAME);
		
		CtxAttribute occupationAttr = addAttribute(getRandomOccupation(), operatorCtxEnt, CtxAttributeTypes.OCCUPATION);
		
		CtxAttribute locationAttr = addAttribute(getRandomLocation(), operatorCtxEnt, CtxAttributeTypes.LOCATION_SYMBOLIC);
		
		CtxAttribute statusAttr = addAttribute(getRandomStatus(), operatorCtxEnt, CtxAttributeTypes.STATUS);

		this.ctxBroker.update(requestor, interestsAttr1);
		this.ctxBroker.update(this.requestor, interestsAttr2);
		this.ctxBroker.update(this.requestor, interestsAttr3);
		this.ctxBroker.update(this.requestor, nameAttr);
		this.ctxBroker.update(this.requestor, occupationAttr);
		this.ctxBroker.update(this.requestor, locationAttr);
		this.ctxBroker.update(this.requestor, statusAttr);



		LOG.info("Creating person name: "+nameAttr.getStringValue());
		LOG.info("*** with Interests: "+interestsAttr1.getStringValue()+", "+interestsAttr2.getStringValue()+", "+interestsAttr3.getStringValue());
		LOG.info("*** with Job Position: "+occupationAttr.getStringValue());
		LOG.info("*** with Location: "+locationAttr.getStringValue());
		LOG.info("*** with Status: "+statusAttr.getStringValue());

		LOG.info("*** getting  Associations");
		CtxEntityIdentifier ctxCommunityEntityIdentifier = (CtxEntityIdentifier)this.ctxBroker.retrieveCommunityEntityId(this.requestor, this.cisID).get();
		LOG.info("communityEntityIdentifier retrieved: " + ctxCommunityEntityIdentifier.toString() + " based on cisID: " + this.cisID);
		CommunityCtxEntity communityEntity = (CommunityCtxEntity)this.ctxBroker.retrieve(this.requestor, ctxCommunityEntityIdentifier).get();
		Set<CtxAssociationIdentifier> assocIDSet = communityEntity.getAssociations("hasMembers");
		if (assocIDSet.size() > 0) {
			List<CtxAssociationIdentifier> assocIdList = new ArrayList<CtxAssociationIdentifier>(assocIDSet);
			this.assocHasMembers = ((CtxAssociation)this.ctxBroker.retrieve(this.requestor, (CtxIdentifier)assocIdList.get(0)).get());
		}

		LOG.info("community members (before adding new members) " + this.assocHasMembers.getChildEntities());

		this.assocHasMembers.addChildEntity(this.personIndiEnt);

		this.assocHasMembers = ((CtxAssociation)this.ctxBroker.update(this.requestor, this.assocHasMembers).get());

		LOG.info("community members (after adding new members) " + this.assocHasMembers.getChildEntities());

		LOG.info(" BEFORE UPDATE communityEnt.getID():  " + communityEntity.getId());
		LOG.info(" BEFORE UPDATE communityEnt.getMembers():  " + communityEntity.getMembers());

		//			    this.ctxBroker.update(this.requestor, communityEntity);
	}

	/**
	 * @param attributeValue
	 * @param operatorCtxEnt
	 * @param name 
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws CtxException
	 */
	//Verify if the attribute exist in the ctx broker
	private CtxAttribute addAttribute(String attributeValue, final IndividualCtxEntity operatorCtxEnt, String ctxType)	throws InterruptedException, ExecutionException, CtxException {
		Set<CtxAttribute> attributes = operatorCtxEnt.getAttributes(ctxType);
		CtxAttribute attribute = null;
		if(attributes.size()>0){
			for(CtxAttribute ctxAttr : attributes){
				LOG.info("Old value"+ctxAttr.getStringValue() ) ;
			}
		}
		else {
			attribute = this.ctxBroker.createAttribute(this.requestor, this.personIndiEnt, ctxType).get();
			attribute.setStringValue(attributeValue);
		}
		attribute.setStringValue(attributeValue);
		return attribute;
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

	private String getRandomOccupation() {
		final String[] occupation={"Manager","Developer","Beta Tester"};
		return occupation[r.nextInt(3)];
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