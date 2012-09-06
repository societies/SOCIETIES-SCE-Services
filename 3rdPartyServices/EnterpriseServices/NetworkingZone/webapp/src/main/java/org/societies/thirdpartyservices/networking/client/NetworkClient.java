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
package org.societies.thirdpartyservices.networking.client;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.activity.IActivityFeed;
import org.societies.api.cis.management.ICis;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisManagerCallback;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.cis.attributes.MembershipCriteria;
import org.societies.api.cis.attributes.Rule;
import org.societies.api.cis.directory.ICisAdvertisementRecord;
import org.societies.api.cis.directory.ICisDirectoryCallback;
import org.societies.api.cis.directory.ICisDirectoryRemote;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.broker.ICtxBroker;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.identity.Requestor;
import org.societies.api.schema.cis.community.Community;
import org.societies.api.schema.cis.community.CommunityMethods;
import org.societies.api.schema.cis.community.Criteria;
import org.societies.api.schema.cis.community.MembershipCrit;
import org.societies.api.schema.cis.community.Participant;
import org.societies.api.schema.cis.directory.CisAdvertisementRecord;
import org.societies.api.internal.css.management.ICSSLocalManager;

public class NetworkClient {

	//TODO : Temporary while testing
	private int TEST_TIME_MULTIPLER = 10;
	
	
	//TODO : Probably move to somehwere
	private String schmoozerUser = "schmoozer";

	
	public ICisManager cisManager;
	public ICisDirectoryRemote cisDirectoryClient;
	protected Community remoteCommunity;
	private ICtxBroker ctxBroker;
	private ICommManager commManager;
	private ICSSLocalManager cssManager;

	public NetworkingClientComms commsClient;
	

	private ICis netCis;
	private ICis currentZoneCis;
	private List<CisAdvertisementRecord> netZoneCisAds = new ArrayList<CisAdvertisementRecord>();
	List<Participant> memberList;
	

	private static Logger log = LoggerFactory.getLogger(NetworkClient.class);

	
	public ICisManager getCisManager() {
		return cisManager;
	}

	public void setCisManager(ICisManager cisManager) {
		this.cisManager = cisManager;
	}

	public ICisDirectoryRemote getCisDirectoryClient() {
		return cisDirectoryClient;
	}

	public void setCisDirectoryClient(ICisDirectoryRemote cisDirectoryClient) {
		this.cisDirectoryClient = cisDirectoryClient;
	}

	public NetworkingClientComms getCommsClient() {
		return commsClient;
	}

	public void setCommsClient(NetworkingClientComms commsClient) {
		this.commsClient = commsClient;
	}
	
	public ICtxBroker getCtxBroker() {
		return ctxBroker;
	}

	public void setCtxBroker(ICtxBroker ctxBroker) {
		this.ctxBroker = ctxBroker;
	}

	public ICommManager getCommManager() {
		return commManager;
	}

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}
	
	/**
	 * @return the cssManager
	 */
	public ICSSLocalManager getCssManager() {
		return cssManager;
	}

	/**
	 * @param cssManager the cssManager to set
	 */
	public void setCssManager(ICSSLocalManager cssManager) {
		this.cssManager = cssManager;
	}

	public NetworkClient() {
		log.info("NetworkClient bundle instantiated.");
		
	};

	
	
	public void init_service() throws Exception {

		log.info("NetworkClient init_service called.");
		// Check to see if a CIS exists for the networking zone
		// if it doesn't create one
		
		getContextAtributeNetUser();

		String netCisID = getCommsClient().getServerCisID();
		log.info("getServerCisID returned " + netCisID);
		
		if (netCisID != null)
			netCis = getCisManager().getCis(netCisID);

		if (netCis == null) {

			log.info("Not a member of Cis - sending join request");
			
			cisAdvertList = null;
			
			getCisDirectoryClient().searchByID(netCisID, iCisDirectoryCallback);
			
			int loop = 0;
			do
			{
				try {
					Thread.sleep(1 * 1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				loop++;
			} while ((cisAdvertList == null) && (loop < (5 * TEST_TIME_MULTIPLER)));
				
			// should be only 1
			if (cisAdvertList == null)
				throw new Exception();
			
			
			
			CisAdvertisementRecord netCisDetails = new CisAdvertisementRecord();
			// should be only 1	
			netCisDetails = cisAdvertList.get(0);
			
			getCisManager().joinRemoteCIS(netCisDetails, iCisManagerCallback);
			
			try {
				Thread.sleep(5 * 1000  * TEST_TIME_MULTIPLER);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			netCis = getCisManager().getCis(netCisID);
			
			if (netCis == null)
				throw new Exception();	
		}
			
		log.info("NetworkClient init_service : Get Zones");
		
		// Now find the details of the zone
		List<String> netZoneCisID = getCommsClient().getZoneCisID();
			
		if (netZoneCisID == null)
			throw new Exception();
			
		for ( int i = 0; i < netZoneCisID.size(); i++)
		{
			cisAdvertList = null;
			// Temporary until filtering by id is implemented
			getCisDirectoryClient().searchByID(netZoneCisID.get(i), iCisDirectoryCallback);
				
			int	loop = 0;
			do
			{
				try {
					Thread.sleep(1 * 1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				loop++;
			} while ((cisAdvertList == null) && (loop < (5 * TEST_TIME_MULTIPLER)));
				
			// should be only 1
			if (cisAdvertList == null)
				throw new Exception();
				
			// should be only 1	
			netZoneCisAds.add(cisAdvertList.get(0));
				
			log.info("getZoneList adding" +cisAdvertList.get(0).getName());
				
		}
			
	
	}

	public List<String> getZoneList() {
		
		List<String> zoneList = new ArrayList<String>();
		log.info("getZoneList start");
		
		for ( int i = 0; i < netZoneCisAds.size(); i++)
		{
			zoneList.add(netZoneCisAds.get(i).getName());
			log.info("getZoneList adding" +netZoneCisAds.get(i).getName());
		}
		log.info("getZoneList end");
		return zoneList;
	}

	

	public void getEvents() {

	}

	public void getUserDetails(String userid) {
	}

	// callback
	ICisManagerCallback iCisManagerCallback = new ICisManagerCallback() {

		public void receiveResult(CommunityMethods communityResultObject) {
			if (communityResultObject != null) {
				if (communityResultObject.getJoinResponse() != null) {
					if (communityResultObject.getJoinResponse().isResult()) {
						remoteCommunity = communityResultObject
								.getJoinResponse().getCommunity();
					}
					;
				}
				if(communityResultObject.getWho() != null){
					log.info("Got response to getmembers " + communityResultObject.getWho().getParticipant().size());
					memberList = communityResultObject.getWho().getParticipant();					
					
				}
			}
			
		}
	};
	protected List<CisAdvertisementRecord> cisAdvertList;

	// callback
	ICisDirectoryCallback iCisDirectoryCallback = new ICisDirectoryCallback() {

		@Override
		public void getResult(List<CisAdvertisementRecord> list) {
			if (list != null)
				cisAdvertList = list;
		}

	};
	
	private void updateContextAtributeNetUser(){
		log.info("updateContextAtribute Start");
		Requestor requestor = new Requestor(getCommManager().getIdManager().getThisNetworkNode());

		
		try {
			//retrieve the CtxEntityIdentifier of the CSS owner context entity
			CtxEntityIdentifier ownerEntityId = this.ctxBroker.retrieveIndividualEntityId(requestor, getCommManager().getIdManager().getThisNetworkNode()).get();
			// create a context attribute under the CSS owner context entity
			 CtxAttribute netUserAttr = this.ctxBroker.createAttribute(requestor, ownerEntityId, "MEMBER_OF").get();
			 // assign a String value to the attribute 
			 netUserAttr.setStringValue(schmoozerUser);
			 netUserAttr.setValueType(CtxAttributeValueType.STRING);
		
			 // update the attribute in the Context DB
			 netUserAttr = (CtxAttribute) this.ctxBroker.update(requestor, netUserAttr).get();

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

		
		log.info("updateContextAtribute End");

		
	}
	
	private void getContextAtributeNetUser(){
		log.info("updateContextAtribute Start");
		Requestor requestor = new Requestor(getCommManager().getIdManager().getThisNetworkNode());

		
		try {
			//retrieve the CtxEntityIdentifier of the CSS owner context entity
			CtxEntityIdentifier ownerEntityId = this.ctxBroker.retrieveIndividualEntityId(requestor, getCommManager().getIdManager().getThisNetworkNode()).get();
			// create a context attribute under the CSS owner context entity
			
			
			Future<List<CtxIdentifier>> memberCtxLookupFut = this.ctxBroker.lookup(requestor, ownerEntityId, CtxModelType.ATTRIBUTE, "MEMBER_OF");
			Thread.sleep(1000);
			List<CtxIdentifier> memberCtxLookup =  memberCtxLookupFut.get();
			CtxIdentifier memberCtx  = null;
			CtxAttribute netUserAttr = null;
			
			if ((memberCtxLookup != null) && (memberCtxLookup.size() > 0))
				memberCtx = memberCtxLookup.get(0);
		
			
			if (memberCtx == null)
			{
				netUserAttr = this.ctxBroker.createAttribute(requestor, ownerEntityId, "MEMBER_OF").get();
			}
			else 
			{
				Future<CtxModelObject> netUserAttrFut = this.ctxBroker.retrieve(requestor, memberCtx);
				Thread.sleep(1000);
				netUserAttr = (CtxAttribute) netUserAttrFut.get();
			}
			 // assign a String value to the attribute 
			 netUserAttr.setStringValue(schmoozerUser);
			 netUserAttr.setValueType(CtxAttributeValueType.STRING);
		
			 // update the attribute in the Context DB
			 netUserAttr = (CtxAttribute) this.ctxBroker.update(requestor, netUserAttr).get();

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

		
		log.info("updateContextAtribute End");

		
	}

	
	
	private void updateContextAtributeLocation(String zone){
		log.info("updateContextAtribute Start");
		Requestor requestor = new Requestor(getCommManager().getIdManager().getThisNetworkNode());

		
		try {
			//retrieve the CtxEntityIdentifier of the CSS owner context entity
			CtxEntityIdentifier ownerEntityId = this.ctxBroker.retrieveIndividualEntityId(requestor, getCommManager().getIdManager().getThisNetworkNode()).get();
			// create a context attribute under the CSS owner context entity
			
			
			Future<List<CtxIdentifier>> memberCtxLookupFut = this.ctxBroker.lookup(requestor, ownerEntityId, CtxModelType.ATTRIBUTE, "ZONE_LOCATION_SYMBOLIC");
			Thread.sleep(1000);
			List<CtxIdentifier> memberCtxLookup =  memberCtxLookupFut.get();
			CtxIdentifier memberCtx  = null;
			CtxAttribute netUserAttr = null;
			
			if ((memberCtxLookup != null) && (memberCtxLookup.size() > 0))
				memberCtx = memberCtxLookup.get(0);
		
			
			if (memberCtx == null)
			{
				netUserAttr = this.ctxBroker.createAttribute(requestor, ownerEntityId, "ZONE_LOCATION_SYMBOLIC").get();
			}
			else 
			{
				Future<CtxModelObject> netUserAttrFut = this.ctxBroker.retrieve(requestor, memberCtx);
				Thread.sleep(1000);
				netUserAttr = (CtxAttribute) netUserAttrFut.get();
			}
			 // assign a String value to the attribute 
			 netUserAttr.setStringValue(zone);
			 netUserAttr.setValueType(CtxAttributeValueType.STRING);
		
			 // update the attribute in the Context DB
			 netUserAttr = (CtxAttribute) this.ctxBroker.update(requestor, netUserAttr).get();

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
		
		
	}

	public void joinZoneCis(String selectedCis) {
		
		log.info("joinZoneCis Start");
		//TODO : unjoin first
		
		if (currentZoneCis != null)
		{
			// leave zone
			getCisManager().leaveRemoteCIS(currentZoneCis.getCisId(), iCisManagerCallback);
			try {
				Thread.sleep(1000 * TEST_TIME_MULTIPLER);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		currentZoneCis = null;
		
		// Find cis in list
		for ( int i = 0; i < netZoneCisAds.size(); i++)
		{
			if (netZoneCisAds.get(i).getName().contentEquals(selectedCis))
			{
				// First we need to frig out location so we can join
				// Find necessary localtion
				
				MembershipCrit memcrit = netZoneCisAds.get(i).getMembershipCrit();
				List<Criteria> crit = memcrit.getCriteria();
				for (Criteria checkCrit :  crit)
				{
					if (checkCrit.getAttrib().contentEquals("ZONE_LOCATION_SYMBOLIC"))
					{
						log.info("joinZoneCis checkCrit.getValue1()" + checkCrit.getValue1());
						
						updateContextAtributeLocation(checkCrit.getValue1());
						break;
					}
				}
				
				getCisManager().joinRemoteCIS(netZoneCisAds.get(i), iCisManagerCallback);
				try {
					Thread.sleep(5 * 1000  * TEST_TIME_MULTIPLER);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				currentZoneCis = getCisManager().getCis(netZoneCisAds.get(i).getId());
				
				getMembers();
				return;
			}
		}
		
		getMembers();
		
		log.info("joinZoneCis End");
		return;
	}

	
	void getMembers()
	{
		log.info("getMembers Start");
		
		// Find cis in list
		for ( int i = 0; i < netZoneCisAds.size(); i++)
		{
		
			//NOT LOCAL CIS, SO CALL REMOTE
			ICis remoteCIS = getCisManager().getCis(netZoneCisAds.get(i).getId());
			if (remoteCIS != null) {
				memberList = null;
				remoteCIS.getListOfMembers(iCisManagerCallback);
				
				int loop = 0;
				do
				{
					
					try
					{
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					};
					loop++;
				}
				while ((memberList == null) && (loop < (5 *TEST_TIME_MULTIPLER)));
				
				if (memberList != null)
				{
					for (Participant part :  memberList)
					{
						log.info("getMembers : Zone " + netZoneCisAds.get(i).getName() + " Member  [" +part.getJid()  + " ]");
					}
				}
			}
		}
		
		log.info("getMembers end");
	}

	
	
	// User Details
	// TODO: Move to another class, database ??
	private String displayName = "My Display name";
	private String companyName = "My Company";
	private String deptName = "My Department";


	public String getDisplayName() {
		log.info("NetworkClient getDisplayName called." + displayName);
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getCompanyName() {
		log.info("NetworkClient getCompanyName called." + companyName);
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getDeptName() {
		log.info("NetworkClient getDeptName called." + deptName);
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
	
	
	
	
	
}
