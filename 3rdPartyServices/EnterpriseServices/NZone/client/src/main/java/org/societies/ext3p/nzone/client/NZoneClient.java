package org.societies.ext3p.nzone.client;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.societies.api.cis.directory.ICisDirectoryCallback;
import org.societies.api.cis.directory.ICisDirectoryRemote;
import org.societies.api.cis.management.ICis;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisManagerCallback;
import org.societies.api.cis.management.ICisOwned;
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
import org.societies.api.ext3p.nzone.client.INZoneClient;
import org.societies.api.ext3p.nzone.model.NZonePreferences;
import org.societies.api.ext3p.nzone.model.UserPreview;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.Requestor;
import org.societies.api.personalisation.mgmt.IPersonalisationManager;
import org.societies.api.personalisation.model.Action;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.personalisation.model.IActionConsumer;
import org.societies.api.schema.cis.community.Community;
import org.societies.api.schema.cis.community.CommunityMethods;
import org.societies.api.schema.cis.community.Criteria;
import org.societies.api.schema.cis.community.MembershipCrit;
import org.societies.api.schema.cis.community.Participant;
import org.societies.api.schema.cis.directory.CisAdvertisementRecord;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.services.IServices;
import org.societies.api.useragent.monitoring.IUserActionMonitor;
import org.societies.api.ext3p.schema.nzone.UserDetails;
import org.societies.api.ext3p.schema.nzone.ZoneDetails;

public class NZoneClient implements INZoneClient,IActionConsumer {

	private static Logger log = LoggerFactory.getLogger(NZoneClient.class);
	
	
	private ICtxBroker ctxBroker;
	private ICommManager commManager;
	private NZoneClientComms nzoneClientComms;
	public ICisManager cisManager;
	public ICisDirectoryRemote cisDirectoryClient;
	private IUserActionMonitor uam; 
	private IPersonalisationManager persoMgr;
	IServices serviceMgmt;
	
	
	private ServiceResourceIdentifier myServiceID;
	private String myServiceName;
	private String myServiceType;
	private IIdentity myIdentity;
	
	
	private String nzoneServerCssID;
	
	private Requestor requestor;
	private List<CisAdvertisementRecord> localCisAds = new ArrayList<CisAdvertisementRecord>();
	private boolean bJoinResponseReceived;
	private boolean bLeaveResponseReceived;
	List<Participant> memberList;
	
	private List<ZoneDetails> zoneDetails;
	
	private ICis mainCis;
	private ICis currentZoneCis;
	
	
	private static String nzoneMemberOfCxtAttr = "nzoneMemberOf";
	private static String nzoneLocationCxtAttr = "ZONE_LOCATION_SYMBOLIC";
	
	private static String PREF_TAG = "taggedPreference";
	
	//TODO : Temporary while testing
	private int TEST_TIME_MULTIPLER = 10;
	private int JOIN_WAIT_MULTIPLER = 50; // As the JOIN needs user input, we need to set a long timeout
	private int WAIT_TIME_MILLISECS = 100;
	private int MAX_TRIES = 5;
	
	
	private NZonePreferences preferences;
	
	CountDownLatch cisManagerCallbackSignal;
	
	
	private List<CisAdvertisementRecord> cisDirCallbackResult;
	
	
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

	public NZoneClientComms getNzoneClientComms() {
		return nzoneClientComms;
	}

	public void setNzoneClientComms(NZoneClientComms nzoneClientComms) {
		this.nzoneClientComms = nzoneClientComms;
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

	/**
	 * @return the uam
	 */
	public IUserActionMonitor getUam() {
		return uam;
	}

	/**
	 * @param uam the uam to set
	 */
	public void setUam(IUserActionMonitor uam) {
		this.uam = uam;
	}

	/**
	 * @return the persoMgr
	 */
	public IPersonalisationManager getPersoMgr() {
		return persoMgr;
	}

	/**
	 * @param persoMgr the persoMgr to set
	 */
	public void setPersoMgr(IPersonalisationManager persoMgr) {
		this.persoMgr = persoMgr;
	}

	public IServices getServiceMgmt() {
		return serviceMgmt;
	}

	public void setServiceMgmt(IServices serviceMgmt) {
		this.serviceMgmt = serviceMgmt;
	}

	public NZoneClient(String networkingserver)	
	{
		this.nzoneServerCssID = new String(networkingserver);
		
		myServiceName = "nzoneClient";
		myServiceType = "client";
		
	};
	
	public NZoneClient()	
	{
		this.nzoneServerCssID = new String("admin.societies.local");
		
		myServiceName = "nzoneClient";
		myServiceType = "client";
		
		
	};
	

	public void initialize() 
	{
		log.info("NZoneClient bundle initialized.");
		
		myIdentity = getCommManager().getIdManager().getThisNetworkNode();
		
		log.info("NZoneClient bundle myIdentity is." + myIdentity.getJid());
		if (requestor == null)
			requestor = new Requestor(myIdentity);
		
		myServiceID = getServiceMgmt().getMyServiceId(this.getClass());
		
		
		// To join any nzone zone ( including main zone), we need to that the memberof
		// attribute set
		if (this.getContextAtribute(nzoneMemberOfCxtAttr) == null)
				updateContextAtribute(nzoneMemberOfCxtAttr, "true");
		if (this.getContextAtribute(nzoneLocationCxtAttr) == null)
			updateContextAtribute(nzoneLocationCxtAttr, "");
		
		// The ICAUIPrediction prefiction bombs out if these are sets!??
		// TODO : Check why they are needed
		if (this.getContextAtribute(CtxAttributeTypes.LOCATION_SYMBOLIC.toString()) == null)
			updateContextAtribute(CtxAttributeTypes.LOCATION_SYMBOLIC.toString(), "here");
		if (this.getContextAtribute(CtxAttributeTypes.STATUS.toString()) == null)
			updateContextAtribute(CtxAttributeTypes.STATUS.toString(), "online");

		
		
		
		//TODO : Need to think about if we just retrieve the cis id's from the
		// NzoneServer of the actualy cis objects
		// for now we will get the cis id's  
		// Check that we can talk to NZone Server . Get the cisID of the main zone
		String mainCisId = getNzoneClientComms().getMainZoneCisID();
		
		// Now we need to check that we are a member of this cis,
		//if not we need to join it
		if (mainCisId == null)
		{
			log.error("NZoneClient : Invalid configuration, No Data for Main Cis");
			return;
		}
		
			
		mainCis = joinCis(mainCisId);
		
		zoneDetails = getZoneDetails();
		
		
		
		
	};
	
	public void cleanUp() 
	{
		log.info("NZoneClient bundle initialized.");
		
		if (currentZoneCis != null)
			leaveCis(currentZoneCis.getCisId());
		if (mainCis != null)
			leaveCis(mainCis.getCisId());
		
	};
	
	private ICis joinCis(String cisJid) {
		
		ICis joinedCis;
		log.info("joinCis Start");
		
		
		// We can't join cis's we are already a member of, so check that first
		// if we are a member already, no need to do anything
		joinedCis = getCisManager().getCis(cisJid);
	
		if (joinedCis != null)
			return joinedCis;	
	

		CisAdvertisementRecord adRec = getCisAdvert(cisJid);
			
		// First we need to frig out location so we can join
		// Find necessary localtion
				
		List<Criteria> crit = adRec.getMembershipCrit().getCriteria();
		for (Criteria checkCrit :  crit)
		{
			if (checkCrit.getAttrib().contentEquals(nzoneLocationCxtAttr))
			{
				log.info("joinZoneCis checkCrit.getValue1()" + checkCrit.getValue1());
				updateContextAtribute(nzoneLocationCxtAttr, checkCrit.getValue1());
				break;
			} 
		};
		
		
		
		bJoinResponseReceived = false;
		cisManagerCallbackSignal = new CountDownLatch(1);
		getCisManager().joinRemoteCIS(adRec, iCisManagerCallback);
		
		try {
			cisManagerCallbackSignal.await();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	
		if (bJoinResponseReceived)
			joinedCis = getCisManager().getCis(cisJid);
				
		if (joinedCis != null)
			return joinedCis;	
		
		log.info("joinZoneCis End");
		return null; // problem joining
	}


	private void leaveCis(String cisJid) {
		
		log.info("leaveCis Start");
		ICis cisToLeave;
		
		
		// We can't leave cis's we are not a member of, so check that first
		// if we are not a member , no need to do anything
		cisToLeave = getCisManager().getCis(cisJid);
	
		if (cisToLeave == null)
			return;	
	

		bLeaveResponseReceived = false;
		getCisManager().leaveRemoteCIS(cisJid, iCisManagerCallback);
		int loop = 0;
		do {
			try {
				Thread.sleep(WAIT_TIME_MILLISECS);
			} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			}
			loop++;
		} while ((bLeaveResponseReceived == false) && (loop < (MAX_TRIES * TEST_TIME_MULTIPLER)));
				
		cisToLeave = getCisManager().getCis(cisJid);
				
		if (cisToLeave != null)
		{
			// Problem leave cis
			log.error("Problem leaving Cis");
		}
		log.info("joinZoneCis End");
		return; 
	}


	
	@Override
	public boolean bJoinZone(String zoneID) 
	{
		if (currentZoneCis != null)
		{
			if (currentZoneCis.getCisId().contentEquals(zoneID) == true)
			{
				//We are already a member of this zone, just return true
				return true;
			}
			// if we get this far, we need to leave current zone before entered new zone
			this.leaveCis(currentZoneCis.getCisId());
		}
		// ok, now we can go ahead a join the new zone
		// Until we are connected up to the localtion management system, we
		// need to set our location manually in context
		
		
		// find the zonedetails
		if (zoneDetails == null) // should never happen as we call it on itin, but just in case
			zoneDetails = getZoneDetails(); 
		for ( int i = 0; i < zoneDetails.size(); i++)
		{
			if (zoneDetails.get(i).getCisid().contentEquals(zoneID) == true)
				updateContextAtribute(nzoneLocationCxtAttr, zoneDetails.get(i).getZonelocation());
		}
		
		// Now we are ready to join
		currentZoneCis = this.joinCis(zoneID);
		
		if (currentZoneCis == null)
			return false;
		
		return true;
		
	} 
	@Override
	public List<UserPreview> getSuggestedList()
	{
		// Return a list of suggest contacts from the current zone,
		//if no current zone, return a list from main zone
		List<UserPreview> list = new ArrayList<UserPreview>();
		
		
		//TODO start: This need to change when css mnager suggest friends working
		if (currentZoneCis != null)
		{
			memberList = null;
			currentZoneCis.getListOfMembers(requestor,iCisManagerCallback);
			int loop = 0;
			do {
				try {
					Thread.sleep(WAIT_TIME_MILLISECS);
				} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
				}
				loop++;
			} while ((memberList == null) && (loop < (MAX_TRIES * TEST_TIME_MULTIPLER)));
			
			if (memberList == null)
			{
				log.error("Problem getting member list for cis" + currentZoneCis.getName());
				return null;
			}
			
			//TODO end: This need to change when css mnager suggest friends working
			// otherwise, we now have list, get there details
			List<String> suggestionsIDs = new ArrayList<String>();
			
			for ( int i = 0; i < memberList.size(); i++)
			{
				suggestionsIDs.add(memberList.get(i).getJid());
			}
			
			List<UserDetails> userDets = this.getNzoneClientComms().getUserDetailsList(suggestionsIDs);
			List<String> tags = new ArrayList<String>();
			tags.add("Learned Preference");
			
			// Now populate UserPreview
			for ( int i = 0; i < memberList.size(); i++)
			{
				UserPreview userPre = new UserPreview();
				userPre.setUserid(memberList.get(i).getJid());
				log.debug("userPre.getUserid[" + i + "] id is : " +userPre.getUserid());
				if (userDets != null)
				{	
					for ( int j = 0; j < userDets.size(); j++)
					{
						log.debug("checking userDets.getUserid[" + j + "] : " + userDets.get(j).getUserid());
					
						if ((userDets.get(j) != null) && (userDets.get(j).getUserid() != null))
						{
							if (userPre.getUserid().contentEquals(userDets.get(j).getUserid()))
							{
								userPre.setCompany(userDets.get(j).getCompany());
								userPre.setDisplayName(userDets.get(j).getDisplayName());
								if (userDets.get(j).getCompany() != "")
								{
									// Check is we have 'tagged' this company
									//get UserPrefences
									if 	(preferences != null)
									{
										if (preferences.isPreferred("company", userDets.get(j).getCompany()))
										{
											log.debug("Tagging user as preferred");
											userPre.setTags(tags);
										}
									}
								}
							}
						}
					}
				}
				list.add(userPre);
			}
		} else 
		{
			memberList = null;
			mainCis.getListOfMembers(requestor,iCisManagerCallback);
			int loop = 0;
			do {
				try {
					Thread.sleep(WAIT_TIME_MILLISECS);
				} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
				}
				loop++;
			} while ((memberList == null) && (loop < (MAX_TRIES * TEST_TIME_MULTIPLER)));
			
			if (memberList == null)
			{
				log.error("Problem getting member list for cis" + mainCis.getName());
				return null;
			}
			
			//TODO end: This need to change when css mnager suggest friends working
			// otherwise, we now have list, get there details
			List<String> suggestionsIDs = new ArrayList<String>();
			
			for ( int i = 0; i < memberList.size(); i++)
			{
				suggestionsIDs.add(memberList.get(i).getJid());
			}
			
			List<UserDetails> userDets = this.getNzoneClientComms().getUserDetailsList(suggestionsIDs);
			List<String> tags = new ArrayList<String>();
			tags.add("Learned Preference");
			
			// Now populate UserPreview
			for ( int i = 0; i < memberList.size(); i++)
			{
				UserPreview userPre = new UserPreview();
				userPre.setUserid(memberList.get(i).getJid());
				
				log.debug("userPre.setUserid[" + i + "] id is : " +userPre.getUserid());
				
				if (userDets != null)
				{
					for (int j = 0; j < userDets.size(); j++)
					{
						log.debug("checking userDets.getUserid[" + j + "] : " + userDets.get(j).getUserid());
						
						if ((userDets.get(j) != null) && (userDets.get(j).getUserid() != null))
						{
							if (userPre.getUserid().contentEquals(userDets.get(j).getUserid()))
							{
								userPre.setCompany(userDets.get(j).getCompany());
								userPre.setDisplayName(userDets.get(j).getDisplayName());
								if (userDets.get(j).getCompany() != "")
								{
									// Check is we have 'tagged' this company
									// 	get UserPrefences
									if 	(preferences != null)
									{
										if (preferences.isPreferred("company", userDets.get(j).getCompany()))
										{
											log.debug("Tagging user as preferred");
											userPre.setTags(tags);
										}
									}
								}
							}
						}
					}
				}
				list.add(userPre);
			}
		}
		return list;
	} 
	
	
	@Override
	public void getUserProfile(){} ;
	@Override
	public void saveShareInfo(){} ;
	@Override
	public void getMyProfile(){} ;
	@Override
	public void getActivityFeed(){} ;
	@Override
	public void sendSocFR(){} ;
	@Override
	public void getShareInfo(){} ;
	@Override
	public void saveMyProfile(){} ;
	@Override
	public void saveExtraInfo(){} ;
	@Override
	public void posttoSN(){} ;
	
	@Override
	public void setAsPreferred(String type, String value)
	{
		
		// get UserPrefences
		if 	(preferences == null)
		{
			//preferences = new NZonePreferences(getUserPreference(PREF_TAG));
			//TODO : TEmp, bypassing Personalisation Manager
			preferences = new NZonePreferences(getNzoneClientComms().getPreferences());
			log.info("Loaded prefererces are " + preferences.toString());
		}		
		preferences.addPreferred(type, value);
		log.info("New prefererces are " + preferences.toString());
		processUserPreference(PREF_TAG, preferences.toString());
		getNzoneClientComms().savePreferences(preferences.toString());
		
		NZonePreferences temppreferences = new NZonePreferences(getUserPreference(PREF_TAG));	
		log.info("Loaded temppreferences are " + temppreferences.toString());
		
		// TODO : tempo Save to database
		
	}
	
	@Override
	public void removeAsPreferred(String type, String value)
	{
		// get UserPrefences
		if 	(preferences == null)
		{
			//preferences = new NZonePreferences(getUserPreference(PREF_TAG));
			//TODO : TEmp, bypassing Personalisation Manager
			preferences = new NZonePreferences(getNzoneClientComms().getPreferences());
			log.info("Loaded prefererces are " + preferences.toString());
		}	
		preferences.removePreferred(type, value);
		log.info("New prefenreces are " + preferences.toString());
		
		processUserPreference(PREF_TAG, preferences.toString());
		
		getNzoneClientComms().savePreferences(preferences.toString());
		
		NZonePreferences temppreferences = new NZonePreferences(getUserPreference(PREF_TAG));	
		log.info("Loaded temppreferences are " + temppreferences.toString());
		
	}
	
	
	
/*	
	@Override
	public List<ZoneDetails> getZoneDetails(){
		
		return getNzoneClientComms().getZoneDetails();
	};
	
*/	
	private void updateContextAtribute(String ctxAttribName, String value){
		log.info("updateContextAtribute Start");
			
		try {
			//retrieve the CtxEntityIdentifier of the CSS owner context entity
			CtxEntityIdentifier ownerEntityId = this.getCtxBroker().retrieveIndividualEntityId(requestor, getCommManager().getIdManager().getThisNetworkNode()).get();
			// create a context attribute under the CSS owner context entity
			
			
			Future<List<CtxIdentifier>> ctxIdentLookupFut = this.getCtxBroker().lookup(requestor, ownerEntityId, CtxModelType.ATTRIBUTE, ctxAttribName);
		//	Thread.sleep(1000);
			List<CtxIdentifier> ctxIdentLookup =  ctxIdentLookupFut.get();
			CtxIdentifier ctxIdent  = null;
			CtxAttribute ctxAttr = null;
			
			if ((ctxIdentLookup != null) && (ctxIdentLookup.size() > 0))
				ctxIdent = ctxIdentLookup.get(0);
		
			
			if (ctxIdent == null)
			{
				ctxAttr = this.getCtxBroker().createAttribute(requestor, ownerEntityId, ctxAttribName).get();
			}
			else 
			{
				Future<CtxModelObject> netUserAttrFut = this.getCtxBroker().retrieve(requestor, ctxIdent);
		//		Thread.sleep(1000);
				ctxAttr = (CtxAttribute) netUserAttrFut.get();
			}
			 // assign a String value to the attribute 
			ctxAttr.setStringValue(value);
			ctxAttr.setValueType(CtxAttributeValueType.STRING);
		
			 // update the attribute in the Context DB
			ctxAttr = (CtxAttribute) this.getCtxBroker().update(requestor, ctxAttr).get();

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
	
	private String getContextAtribute(String ctxAttribName){
		log.info("getContextAtribute Start");
		CtxAttribute ctxAttr = null;
		
		try {
			//retrieve the CtxEntityIdentifier of the CSS owner context entity
			CtxEntityIdentifier ownerEntityId = this.getCtxBroker().retrieveIndividualEntityId(requestor, getCommManager().getIdManager().getThisNetworkNode()).get();
			// create a context attribute under the CSS owner context entity
			
			
			Future<List<CtxIdentifier>> ctxIdentLookupFut = this.getCtxBroker().lookup(requestor, ownerEntityId, CtxModelType.ATTRIBUTE, ctxAttribName);
	//		Thread.sleep(1000);
			List<CtxIdentifier> ctxIdentLookup =  ctxIdentLookupFut.get();
			CtxIdentifier ctxIdent  = null;
			
			
			if ((ctxIdentLookup != null) && (ctxIdentLookup.size() > 0))
				ctxIdent = ctxIdentLookup.get(0);
		
			
			if (ctxIdent == null)
			{
				ctxAttr = this.getCtxBroker().createAttribute(requestor, ownerEntityId, ctxAttribName).get();
			}
			else 
			{
				Future<CtxModelObject> ctxAttrFut = this.getCtxBroker().retrieve(requestor, ctxIdent);
	//			Thread.sleep(1000);
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
			e.printStackTrace();
		}

		
		log.info("getContextAtribute End");
		
		return ctxAttr.getStringValue();

		
	}
	

	private void loadCisAdverts(List<String> cisIdToRetrieve)
	{

		
		cisDirCallbackResult = null;

		getCisDirectoryClient().searchByIDS(cisIdToRetrieve, iCisDirectoryCallback);
				
		int	loop = 0;
		do
		{
			try {
				Thread.sleep(WAIT_TIME_MILLISECS);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			loop++;
		} while ((cisDirCallbackResult == null) && (loop < (MAX_TRIES * TEST_TIME_MULTIPLER)));
				
		if (cisDirCallbackResult != null)
		{
			for ( int index = 0; index < cisDirCallbackResult.size(); index++)
			{
				// Todo : add check in here to see if should replace
				localCisAds.add(cisDirCallbackResult.get(index));
			}
		}
	}
	
	private CisAdvertisementRecord getCisAdvert(String cisIdToRetrieve)
	{
		
		// Check if we have a copy locally
		for ( int i = 0; i < localCisAds.size(); i++)
		{
			if (localCisAds.get(i).getId().compareTo(cisIdToRetrieve) == 0)
				return localCisAds.get(i);
		}
		int newStartPos = localCisAds.size();
		
		List<String> lIdList = new ArrayList<String>();
		lIdList.add(cisIdToRetrieve);
		loadCisAdverts(lIdList);
		
		
		// Check if we have a copy locally, don't bother checking ones we have checked already
		for (;newStartPos < localCisAds.size(); newStartPos++)
		{
			if (localCisAds.get(newStartPos).getId().compareTo(cisIdToRetrieve) == 0)
				return localCisAds.get(newStartPos);
		}
		
		return null;
				
	}
		
	
	// callback
	ICisDirectoryCallback iCisDirectoryCallback = new ICisDirectoryCallback() {

		@Override
		public void getResult(List<CisAdvertisementRecord> list) {
			if (list != null)
				cisDirCallbackResult = list;
		};
	};
	
	// callback
	ICisManagerCallback iCisManagerCallback = new ICisManagerCallback() {

		public CountDownLatch startSignal;
		
		@Override
		public void receiveResult(CommunityMethods communityResultObject) {
			if (communityResultObject != null) {
				if (communityResultObject.getJoinResponse() != null) {
					if (communityResultObject.getJoinResponse().isResult()) {
						bJoinResponseReceived = true;
						cisManagerCallbackSignal.countDown();
					}
					;
				}
				if (communityResultObject.getLeaveResponse() != null) {
						if (communityResultObject.getLeaveResponse().isResult()) {
								bLeaveResponseReceived = true;
								cisManagerCallbackSignal.countDown();
						}
					;
				}
						
				if(communityResultObject.getWhoResponse() != null){
					log.info("Got response to getmembers " + communityResultObject.getWhoResponse().getParticipant().size());
					memberList = communityResultObject.getWhoResponse().getParticipant();
					cisManagerCallbackSignal.countDown();
							
				}
			}
				
		}


	};


	@Override
	public List<ZoneDetails> getZoneDetails() {
		return getNzoneClientComms().getZoneDetails();
		
		/*	
		@Override
		public List<ZoneDetails> getZoneDetails(){
			
			return getNzoneClientComms().getZoneDetails();
		};
		
	*/	
		
	}

	@Override
	public ServiceResourceIdentifier getServiceIdentifier() {
		return myServiceID;
	}

	@Override
	public String getServiceType() {
		return myServiceType;
	}

	@Override
	public List<String> getServiceTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setIAction(IIdentity arg0, IAction arg1) {
		log.info("..............setIAction called ................");
		// TODO Auto-generated method stub
		return true;
	}

			
	
	private void processUserPreference(String parameterName, String value){
		log.info("processUserPreference: "+parameterName+" = "+value);

		if (myServiceID == null )
			myServiceID = getServiceMgmt().getMyServiceId(this.getClass());
		
		log.info("myServiceID.getServiceInstanceIdentifier(): "+ myServiceID.getServiceInstanceIdentifier());
		//create action object and send to uam
		IAction action = new Action(myServiceID, myServiceType, parameterName, value);
		log.info("Sending action to UAM: "+action.toString());
		if (getUam() != null)
		{
			getUam().monitor(myIdentity, action);
//			getUam().monitor(myIdentity, action);
//			getUam().monitor(myIdentity, action);
		}
	}

	private String getUserPreference(String parameterName){
		log.info("Getting channel preference from personalisation manager");
		String result = "";
		try {
			if (myServiceID == null )
				myServiceID = getServiceMgmt().getMyServiceId(this.getClass());
			
			log.info("Created RequestorService type for: "+myIdentity.toString()+" with serviceID: "+myServiceID.toString());
			Future<IAction> futureOutcome = getPersoMgr().getPreference(requestor, myIdentity, myServiceType, myServiceID, parameterName);
			log.info("Requested preference from personalisationManager");
			IAction outcome = futureOutcome.get();
			log.info("Called .get()");
			if(outcome!=null){
				log.info("Successfully retrieved preference outcome: "+outcome.getvalue());
				result = outcome.getvalue();
			}else{
				log.info("No preference was found");
			}
		} catch (Exception e){
			log.error("Error retrieving preference");
			e.printStackTrace();
		}
		log.info("Preference request result = "+result);
		return result;
	}
	
	
	// Put in to test implicit decision making
	@Override
		public void recordActionShowProfile()
		{
			String parameterName = new String("nzuseraction");
			String value = new String("showprofile");
			
			if (myServiceID == null )
				myServiceID = getServiceMgmt().getMyServiceId(this.getClass());
			

			//create action object and send to uam
			IAction action = new Action(myServiceID, myServiceType, parameterName, value);
			log.info("recordActionShowProfile : Sending action to UAM my serviceID class: "+ myServiceID.getClass());
			log.info("recordActionShowProfile : Sending action to UAM my serviceID getServiceInstanceIdentifier: "+ myServiceID.getServiceInstanceIdentifier());
			
			log.info("recordActionShowProfile : Sending action to UAM: "+action.toString());
			getUam().monitor(myIdentity, action);
		}
	@Override
		public void recordActionEnterZone()
		{
			String parameterName = new String("nzuseraction");
			String value = new String("enterzone");
			
			if (myServiceID == null )
				myServiceID = getServiceMgmt().getMyServiceId(this.getClass());
			//create action object and send to uam
			IAction action = new Action(myServiceID, myServiceType, parameterName, value);
			log.info("recordActionEnterZone : Sending action to UAM my serviceID: "+ myServiceID);
			log.info("recordActionEnterZone : Sending action to UAM: "+action.toString());
			getUam().monitor(myIdentity, action);
		}
	
}

