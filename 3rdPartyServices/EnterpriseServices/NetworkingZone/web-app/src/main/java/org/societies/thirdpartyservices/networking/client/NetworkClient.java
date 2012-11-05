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
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.directory.ICisDirectoryCallback;
import org.societies.api.cis.directory.ICisDirectoryRemote;
import org.societies.api.cis.management.ICis;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisManagerCallback;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.broker.ICtxBroker;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationTypes;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.ext3p.networking.MemberDetails;
import org.societies.api.ext3p.networking.NetworkingBeanResult;
import org.societies.api.ext3p.networking.ShareInfo;
import org.societies.api.ext3p.networking.UserDetails;
import org.societies.api.ext3p.networking.ZoneDetails;
import org.societies.api.ext3p.networking.ZoneEvent;

import org.societies.api.identity.Requestor;
import org.societies.api.schema.cis.community.Community;
import org.societies.api.schema.cis.community.CommunityMethods;
import org.societies.api.schema.cis.community.Criteria;
import org.societies.api.schema.cis.community.MembershipCrit;
import org.societies.api.schema.cis.community.Participant;
import org.societies.api.schema.cis.directory.CisAdvertisementRecord;

import org.societies.activity.PersistedActivityFeed;




public class NetworkClient {

	private static Logger log = LoggerFactory.getLogger(NetworkClient.class);

	String userID;
	UserDetails myUserDetails;
	private ICis netCis;
	private ICis currentZoneCis;
	private List<CisAdvertisementRecord> netZoneCisAds = new ArrayList<CisAdvertisementRecord>();
	protected List<CisAdvertisementRecord> cisAdvertList;
	List<Participant> memberList;
	public ShareInfo currentShareInfo;
	public ShareInfo defaultShareInfo;
	Requestor requestor;
	public List<ZoneDetails> zoneDetails;
	boolean bJoinResponseReceived = false;
	boolean bLeaveResponseReceived = false;
	
	List<String> cssFriends = new ArrayList<String>();
	List<String> zoneMembers = new ArrayList<String>();
	
	public PersistedActivityFeed activityFeed = new PersistedActivityFeed();
	
	
	
	
	//TODO : Probably move to somehwere
	private String schmoozerUser = "schmoozer";
	//TODO : Temporary while testing
	private int TEST_TIME_MULTIPLER = 10;
	private int WAIT_TIME_SECS = 100;
	
	NetworkingClientComms commsClient;
	private ICommManager commManager;
	private ICtxBroker ctxBroker;
	public ICisManager cisManager;
	public ICisDirectoryRemote cisDirectoryClient;
	public boolean bInitialise = false;

	
	public NetworkClient()  {
		log.info("NetworkClient bundle instantiated.");
		
	}
		
	
	public void InitService() 
	{
		if (requestor == null)
			requestor = new Requestor(getCommManager().getIdManager().getThisNetworkNode());
		
		getContextAtributeNetUser();
		getContextCssRecordEntries();
		getContextCssFriends();
		
		NetworkingBeanResult startupinfo = getCommsClient().getStartupInfo();
		
		
		
		String netCisID = startupinfo.getNetworkingCis();
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
					Thread.sleep(WAIT_TIME_SECS);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				loop++;
			} while ((cisAdvertList == null) && (loop < (5 * TEST_TIME_MULTIPLER)));
			
			// should be only 1
			if (cisAdvertList == null)
				return;
		
		
		
			CisAdvertisementRecord netCisDetails = new CisAdvertisementRecord();
			// should be only 1	
			netCisDetails = cisAdvertList.get(0);
			bJoinResponseReceived = false;
			getCisManager().joinRemoteCIS(netCisDetails, iCisManagerCallback);
		
			loop = 0;
			
			do {
				try {
					Thread.sleep(WAIT_TIME_SECS);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				loop++;
			} while ((bJoinResponseReceived == false) && (loop < (5 * TEST_TIME_MULTIPLER)));
			
			netCis = getCisManager().getCis(netCisID);
		
			if (netCis == null)
				return;
	}
		
	log.info("NetworkClient init_service : Get Zones");
	
	zoneDetails = startupinfo.getZonedetails();
	 
	
	// Now find the details of the zone
	List<String> netZoneCisID = startupinfo.getZones();
		
	if (netZoneCisID == null)
		return;
		
	for ( int i = 0; i < netZoneCisID.size(); i++)
	{
		cisAdvertList = null;
		// Temporary until filtering by id is implemented
		getCisDirectoryClient().searchByID(netZoneCisID.get(i), iCisDirectoryCallback);
			
		int	loop = 0;
		do
		{
			try {
				Thread.sleep(WAIT_TIME_SECS);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			loop++;
		} while ((cisAdvertList == null) && (loop < (5 * TEST_TIME_MULTIPLER)));
			
		// should be only 1
		if (cisAdvertList == null)
			return;
			
		// should be only 1	
		netZoneCisAds.add(cisAdvertList.get(0));
			
		bInitialise = true;
		log.info("getZoneList adding" +cisAdvertList.get(0).getName());
			
	}
	
	//Temp comment out as it takes too long to timeout
//	doSocialConnectorTestStuff();
	

	}




	// 
	public boolean bProfileExists() {
		
		// Check with the Networking backend that this user , has created a profile
		
		if (myUserDetails == null)
		{
			myUserDetails = getCommsClient().getMyDetails();
		}
		
		if ((myUserDetails == null) || 
			(myUserDetails.getDisplayName() == null) || 
			myUserDetails.getDisplayName().isEmpty())
			
			
			return false;
		
		return true;
	}

	public UserDetails getFriendDetails(String friendid)
	{
		return getCommsClient().getFriendDetails(friendid);
	}
	
	public UserDetails getMyDetails(){
		
		if (myUserDetails == null)
		{
			myUserDetails = getCommsClient().getMyDetails();
			if (myUserDetails == null)
			{
				// New so create empty one
				myUserDetails = new UserDetails();
			}
		}
		return myUserDetails;
		
	}

	
	public void setMyDetails(UserDetails newDetails){
		
		if (myUserDetails == null)
			myUserDetails = new UserDetails();

		myUserDetails.setDisplayName(newDetails.getDisplayName());
		myUserDetails.setEmail(newDetails.getEmail());
		myUserDetails.setHomelocation(newDetails.getHomelocation());
		myUserDetails.setSex(newDetails.getSex());
		myUserDetails.setDept(newDetails.getDept());
		myUserDetails.setCompany(newDetails.getCompany());
		myUserDetails.setPosition(newDetails.getPosition());
		myUserDetails.setAbout(newDetails.getAbout());
		
		myUserDetails.setEducationhistory(newDetails.getEducationhistory());
		myUserDetails.setEmploymenthistory(newDetails.getEmploymenthistory());
		
		getCommsClient().updateMyDetails(myUserDetails);
	}

	

	private void updateContextAtributeLocation(String zone){
		log.info("updateContextAtributeLocation Start");
			
		try {
			//retrieve the CtxEntityIdentifier of the CSS owner context entity
			CtxEntityIdentifier ownerEntityId = this.getCtxBroker().retrieveIndividualEntityId(requestor, getCommManager().getIdManager().getThisNetworkNode()).get();
			// create a context attribute under the CSS owner context entity
			
			
			Future<List<CtxIdentifier>> memberCtxLookupFut = this.getCtxBroker().lookup(requestor, ownerEntityId, CtxModelType.ATTRIBUTE, "ZONE_LOCATION_SYMBOLIC");
		//	Thread.sleep(1000);
			List<CtxIdentifier> memberCtxLookup =  memberCtxLookupFut.get();
			CtxIdentifier memberCtx  = null;
			CtxAttribute netUserAttr = null;
			
			if ((memberCtxLookup != null) && (memberCtxLookup.size() > 0))
				memberCtx = memberCtxLookup.get(0);
		
			
			if (memberCtx == null)
			{
				netUserAttr = this.getCtxBroker().createAttribute(requestor, ownerEntityId, "ZONE_LOCATION_SYMBOLIC").get();
			}
			else 
			{
				Future<CtxModelObject> netUserAttrFut = this.getCtxBroker().retrieve(requestor, memberCtx);
		//		Thread.sleep(1000);
				netUserAttr = (CtxAttribute) netUserAttrFut.get();
			}
			 // assign a String value to the attribute 
			 netUserAttr.setStringValue(zone);
			 netUserAttr.setValueType(CtxAttributeValueType.STRING);
		
			 // update the attribute in the Context DB
			 netUserAttr = (CtxAttribute) this.getCtxBroker().update(requestor, netUserAttr).get();

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
	
	
	public List<ZoneEvent> getCurrentZoneEvents()
	{
		return getCommsClient().getCisActivity(currentZoneCis.getCisId());
	}
	
	public ShareInfo getFriendShareInfo(String friendid)
	{
		return getCommsClient().getFriendShareInfo(friendid);
	}
	
	public ShareInfo getShareInfo(String friendid)
	{
		return getCommsClient().getShareInfo(friendid);
	}
	
	public ShareInfo getDefaultShareInfo()
	{
		return this.getShareInfo("0");
	}
	
	public List<ZoneDetails> getZoneDetails()
	{
		return getCommsClient().getZoneDetails();
	}
	
	
	public ShareInfo updateShareInfo(String friendid, int hash)
	{
		
		if (currentShareInfo == null)
		{
			currentShareInfo = new ShareInfo();
		}
		currentShareInfo.setFriendid(friendid);
		currentShareInfo.setShareHash(hash);
		
		return getCommsClient().updateShareInfo(currentShareInfo);
	}
	
	public ShareInfo updateDefaultShareInfo(int hash)
	{
		if (defaultShareInfo == null)
		{
			defaultShareInfo = new ShareInfo();
		}
		defaultShareInfo.setShareHash(hash);
		defaultShareInfo.setFriendid("0");
		return getCommsClient().updateShareInfo(defaultShareInfo);
	}
		
	
	private void getContextCssRecordEntries()
	{
		
		
		log.info("getContextCssRecordEntries Start");

		
		try {
			//retrieve the CtxEntityIdentifier of the CSS owner context entity
			CtxEntityIdentifier ownerEntityId = this.getCtxBroker().retrieveIndividualEntityId(requestor, getCommManager().getIdManager().getThisNetworkNode()).get();
			// create a context attribute under the CSS owner context entity
			
			
			Future<List<CtxIdentifier>> memberCtxLookupNameFut = this.getCtxBroker().lookup(requestor, ownerEntityId, CtxModelType.ATTRIBUTE, CtxAttributeTypes.NAME);
			Future<List<CtxIdentifier>> memberCtxLookupEmailFut = this.getCtxBroker().lookup(requestor, ownerEntityId, CtxModelType.ATTRIBUTE, CtxAttributeTypes.EMAIL);
			Future<List<CtxIdentifier>> memberCtxLookupHomeFut = this.getCtxBroker().lookup(requestor, ownerEntityId, CtxModelType.ATTRIBUTE, CtxAttributeTypes.ADDRESS_HOME_CITY);
			
			List<CtxIdentifier> memberCtxLookupName =  memberCtxLookupNameFut.get();
			List<CtxIdentifier> memberCtxLookupEmail =  memberCtxLookupEmailFut.get();
			List<CtxIdentifier> memberCtxLookupHome =  memberCtxLookupHomeFut.get();
			CtxIdentifier memberCtxName  = null;
			CtxIdentifier memberCtxEmail  = null;
			CtxIdentifier memberCtxHome  = null;
			CtxAttribute cssNameAttr = null;
			CtxAttribute cssEmailAttr = null;
			CtxAttribute cssHomeAttr = null;
			
			Future<CtxModelObject> cssNameAttrFut = null;
			Future<CtxModelObject> cssEmailAttrFut = null;
			Future<CtxModelObject> cssHomeAttrFut = null;
			
		//	Thread.sleep(1000);
			
			
			if ((memberCtxLookupName != null) && (memberCtxLookupName.size() > 0))
			{
				memberCtxName = memberCtxLookupName.get(0);
		
				log.info("getContextCssRecordEntries got memberCtxName");
				if (memberCtxName != null)
				{
					log.info("getContextCssRecordEntries : memberCtxName not null");
					cssNameAttrFut = this.getCtxBroker().retrieve(requestor, memberCtxName);
				}
			}
			
			if ((memberCtxLookupEmail != null) && (memberCtxLookupEmail.size() > 0))
			{
				memberCtxEmail = memberCtxLookupEmail.get(0);
		
				log.info("getContextCssRecordEntries got memberCtx");
				if (memberCtxEmail != null)
				{
					log.info("getContextCssRecordEntries : memberCtx not null");
					cssEmailAttrFut = this.getCtxBroker().retrieve(requestor, memberCtxEmail);
				}
			}
			
			if ((memberCtxLookupHome != null) && (memberCtxLookupHome.size() > 0))
			{
				memberCtxHome = memberCtxLookupHome.get(0);
		
				log.info("getContextCssRecordEntries got memberCtxHome");
				if (memberCtxHome != null)
				{
					log.info("getContextCssRecordEntries : memberCtxHome not null");
					cssHomeAttrFut = this.getCtxBroker().retrieve(requestor, memberCtxHome);
				}
			}
			
	//		Thread.sleep(1000);
			
			if (cssNameAttrFut != null)
			{
				cssNameAttr = (CtxAttribute) cssNameAttrFut.get();
				log.info("getContextCssRecordEntries :cssNameAttr.getStringValue()" + cssNameAttr.getStringValue());
				getMyDetails().setDisplayName(cssNameAttr.getStringValue());
			}
			
			if (cssEmailAttrFut != null)
			{
				cssEmailAttr = (CtxAttribute) cssEmailAttrFut.get();
				log.info("getContextCssRecordEntries :cssEmailAttr.getStringValue()" + cssEmailAttr.getStringValue());
				getMyDetails().setEmail(cssEmailAttr.getStringValue());
			}
			
			if (cssHomeAttrFut != null)
			{
				cssHomeAttr = (CtxAttribute) cssHomeAttrFut.get();
				log.info("getContextCssRecordEntries :cssHomeAttr.getStringValue()" + cssHomeAttr.getStringValue());
				getMyDetails().setHomelocation(cssHomeAttr.getStringValue());
			}
			
			setMyDetails(getMyDetails());

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

		
		log.info("getContextCssRecordName End");

		
		
	}
	
	private void getContextCssFriends()
	{
		
		
		log.info("getContextCssFriends Start");

		this.cssFriends.clear();
		
		try {
			//retrieve the CtxEntityIdentifier of the CSS owner context entity
			CtxEntityIdentifier ownerEntityId = this.getCtxBroker().retrieveIndividualEntityId(requestor, getCommManager().getIdManager().getThisNetworkNode()).get();
			// create a context attribute under the CSS owner context entity
			
			
			Future<List<CtxIdentifier>> memberCtxLookupFriendsFut = this.getCtxBroker().lookup(requestor, ownerEntityId, CtxModelType.ASSOCIATION, CtxAssociationTypes.IS_FRIENDS_WITH);
	//		Thread.sleep(1000);
			List<CtxIdentifier> memberCtxLookupFriends =  memberCtxLookupFriendsFut.get();
			CtxIdentifier memberCtxFriends  = null;
			CtxAssociation cssFriendAttr = null;
			List<CtxEntityIdentifier> cssFriendList = null;
			
			Future<CtxModelObject> cssFriendAttrFut = null;

			
			
			
			if ((memberCtxLookupFriends != null) && (memberCtxLookupFriends.size() > 0))
			{
				for ( int iFriendCount = 0; iFriendCount < memberCtxLookupFriends.size(); iFriendCount++) 
				{
					memberCtxFriends = memberCtxLookupFriends.get(iFriendCount);

					if (memberCtxFriends != null)
					{
						cssFriendAttrFut = this.getCtxBroker().retrieve(requestor,memberCtxFriends);
	//					Thread.sleep(1000);
						cssFriendAttr = (CtxAssociation)cssFriendAttrFut.get();
						cssFriendList = new ArrayList<CtxEntityIdentifier>( cssFriendAttr.getChildEntities());
						
						for ( int friendIndex = 0; friendIndex < cssFriendList.size(); friendIndex++)
						{
							this.cssFriends.add(cssFriendList.get(friendIndex).getOwnerId());
						}
					}
					
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

		for ( int iFriend = 0; iFriend < this.cssFriends.size(); iFriend++ )
		{
			log.info("getContextCssFriends : Friends with: " + this.cssFriends.get(iFriend));
		}
		
		log.info("getContextCssFriends End");

		
		
	}
	

	private void getContextAtributeNetUser(){
		log.info("updateContextAtribute Start");
		
		try {
			//retrieve the CtxEntityIdentifier of the CSS owner context entity
			CtxEntityIdentifier ownerEntityId = this.getCtxBroker().retrieveIndividualEntityId(requestor, getCommManager().getIdManager().getThisNetworkNode()).get();
			// create a context attribute under the CSS owner context entity
			
			
			Future<List<CtxIdentifier>> memberCtxLookupFut = this.getCtxBroker().lookup(requestor, ownerEntityId, CtxModelType.ATTRIBUTE, "MEMBER_OF");
	//		Thread.sleep(1000);
			List<CtxIdentifier> memberCtxLookup =  memberCtxLookupFut.get();
			CtxIdentifier memberCtx  = null;
			CtxAttribute netUserAttr = null;
			
			if ((memberCtxLookup != null) && (memberCtxLookup.size() > 0))
				memberCtx = memberCtxLookup.get(0);
		
			
			if (memberCtx == null)
			{
				netUserAttr = this.getCtxBroker().createAttribute(requestor, ownerEntityId, "MEMBER_OF").get();
			}
			else 
			{
				Future<CtxModelObject> netUserAttrFut = this.getCtxBroker().retrieve(requestor, memberCtx);
	//			Thread.sleep(1000);
				netUserAttr = (CtxAttribute) netUserAttrFut.get();
			}
			 // assign a String value to the attribute 
			 netUserAttr.setStringValue(schmoozerUser);
			 netUserAttr.setValueType(CtxAttributeValueType.STRING);
		
			 // update the attribute in the Context DB
			 netUserAttr = (CtxAttribute) this.getCtxBroker().update(requestor, netUserAttr).get();

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
	
	
	// callback
		ICisDirectoryCallback iCisDirectoryCallback = new ICisDirectoryCallback() {

			@Override
			public void getResult(List<CisAdvertisementRecord> list) {
				if (list != null)
					cisAdvertList = list;
			}

		};
		
		// callback
		ICisManagerCallback iCisManagerCallback = new ICisManagerCallback() {

			@Override
			public void receiveResult(CommunityMethods communityResultObject) {
				if (communityResultObject != null) {
					if (communityResultObject.getJoinResponse() != null) {
						if (communityResultObject.getJoinResponse().isResult()) {
							bJoinResponseReceived = true;
							Community remoteCommunity = communityResultObject
									.getJoinResponse().getCommunity();
						}
						;
					}
					if (communityResultObject.getLeaveResponse() != null) {
						if (communityResultObject.getLeaveResponse().isResult()) {
							bLeaveResponseReceived = true;
						}
						;
					}
					
					if(communityResultObject.getWhoResponse() != null){
						log.info("Got response to getmembers " + communityResultObject.getWhoResponse().getParticipant().size());
						memberList = communityResultObject.getWhoResponse().getParticipant();					
						
					}
				}
				
			}


		};

			
	public NetworkingClientComms getCommsClient() {
		return commsClient;
	}



	public void setCommsClient(NetworkingClientComms commsClient) {
		this.commsClient = commsClient;
	}

	public ICommManager getCommManager() {
		return commManager;
	}

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}
	
	public ICtxBroker getCtxBroker() {
		return ctxBroker;
	}

	public void setCtxBroker(ICtxBroker ctxBroker) {
		this.ctxBroker = ctxBroker;
	}
	
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

	

	public List<UserDetails> getCurrentZoneMemberList()
	{
		log.info("getCurrentZoneMemberList Start");
		
		List<UserDetails> userdet = null; 
		
		zoneMembers.clear();
		memberList = null;
		
		if (currentZoneCis != null)
		{
			currentZoneCis.getListOfMembers(requestor, iCisManagerCallback);
			
			int loop = 0;
			do
			{
					
				try
				{
					Thread.sleep(WAIT_TIME_SECS);
				} catch (InterruptedException e) {
				// TODO Auto-generated catch block
					e.printStackTrace();
				};
				loop++;
			} while ((memberList == null) && (loop < (5 *TEST_TIME_MULTIPLER)));
		
			if (memberList != null)
			{
				for (Participant part :  memberList)
				{
					log.info("getMembers : Zone " + currentZoneCis.getName() + " Member  [" +part.getJid()  + " ]");
					zoneMembers.add(part.getJid());
				}
			}
			
		}
		
		if (zoneMembers != null)
		{
			userdet = getCommsClient().getUserDetailsList(zoneMembers);
		}
		return userdet;
		
	}
	
	public void joinZoneCis(String selectedCis) {
		
		log.info("joinZoneCis Start");
		//TODO : unjoin first
		
		if (currentZoneCis != null)
		{
			// leave zone
			bLeaveResponseReceived = false;
			getCisManager().leaveRemoteCIS(currentZoneCis.getCisId(), iCisManagerCallback);
			int loop = 0;
			
			do {
				try {
					Thread.sleep(WAIT_TIME_SECS);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				loop++;
			} while ((bLeaveResponseReceived == false) && (loop < (5 * TEST_TIME_MULTIPLER)));
			
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
				bJoinResponseReceived = false;
				getCisManager().joinRemoteCIS(netZoneCisAds.get(i), iCisManagerCallback);
				int loop = 0;
				do {
					try {
						Thread.sleep(WAIT_TIME_SECS);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					loop++;
				} while ((bJoinResponseReceived == false) && (loop < (5 * TEST_TIME_MULTIPLER)));
				
				currentZoneCis = getCisManager().getCis(netZoneCisAds.get(i).getId());
				
			//	getCurrentZoneMemberList();
				return;
			}
		}
		
		log.info("joinZoneCis End");
		return;
	}


	public void addnote(String friendid, String note) {
		
		getCommsClient().addnote(friendid, note);
		
	}


	public List<String> getnotes(String friendid) {
		
		return getCommsClient().getnotes(friendid);
	}
	
}
