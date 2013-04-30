package org.societies.ext3p.nzone.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.activity.IActivityFeedManager;
import org.societies.api.cis.directory.ICisDirectoryRemote;
import org.societies.api.cis.management.ICis;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.broker.ICtxBroker;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxEntityTypes;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.css.BitCompareUtil;
import org.societies.api.css.FriendFilter;
import org.societies.api.css.ICSSManager;
import org.societies.api.ext3p.nzone.client.INZoneClient;
import org.societies.api.ext3p.nzone.model.NZonePreferences;
import org.societies.api.ext3p.nzone.model.NZoneSharePreferences;
import org.societies.api.ext3p.nzone.model.UserPreview;
import org.societies.api.ext3p.nzone.model.ZoneDisplayDetail;
import org.societies.api.ext3p.schema.nzone.ShareInfo;
import org.societies.api.ext3p.schema.nzone.UserDetails;
import org.societies.api.ext3p.schema.nzone.ZoneDetails;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorService;
import org.societies.api.personalisation.mgmt.IPersonalisationManager;
import org.societies.api.personalisation.model.Action;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.personalisation.model.IActionConsumer;
import org.societies.api.personalisation.model.PersonalisablePreferenceIdentifier;
import org.societies.api.schema.cis.community.Participant;
import org.societies.api.schema.cis.directory.CisAdvertisementRecord;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.services.IServices;
import org.societies.api.useragent.monitoring.IUserActionMonitor;

public class NZoneClient implements INZoneClient, IActionConsumer {

	
	
	
	final Semaphore waitingToStart = new Semaphore(1, true);
	final Semaphore busy = new Semaphore(1, true);
	
	
	private static Logger log = LoggerFactory.getLogger(NZoneClient.class);

	private ICtxBroker ctxBroker;
	private ICommManager commManager;
	private NZoneClientComms nzoneClientComms;
	public ICisManager cisManager;
	public ICisDirectoryRemote cisDirectoryClient;
	private IUserActionMonitor uam;
	private IPersonalisationManager persoMgr;
	IServices serviceMgmt;
	private IActivityFeedManager actFeedMgr;
	public ICSSManager cssManager;

	private ServiceResourceIdentifier myServiceID;
	private String myServiceType;
	private IIdentity myIdentity;

	private RequestorService requestorService;
	private Requestor platformRequestor;
	private List<CisAdvertisementRecord> localCisAds = new ArrayList<CisAdvertisementRecord>();

	List<Participant> memberList;

	private List<ZoneDetails> zoneDetails;
	
	protected HashMap<String,String> avatarMap = new HashMap<String,String>();
	final Semaphore avatarMapLock = new Semaphore(1, true);
	
	private List<String> myInterestsCached; 
	

	private ICis mainCis;
	private ICis currentZoneCis;
	private String nzoneServerCssID;
	
	private boolean bInitialising = false;
	private boolean bInitialised = false;

	private NZonePreferences preferences;
	private NZoneSharePreferences sharepreferences;

	private List<CisAdvertisementRecord> cisDirCallbackResult;

	private NZoneCxtChangeList cxtChangeList;
	private String openfireIpAddress;
	
	private UserDetails myDets;
	
	public String getOpenfireIpAddress() {
		return openfireIpAddress;
	}

	public void setOpenfireIpAddress(String openfireIpAddress) {
		this.openfireIpAddress = openfireIpAddress;
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
	 * @param uam
	 *            the uam to set
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
	 * @param persoMgr
	 *            the persoMgr to set
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

	/**
	 * @return the actFeedMgr
	 */
	public IActivityFeedManager getActFeedMgr() {
		return actFeedMgr;
	}

	/**
	 * @param actFeedMgr
	 *            the actFeedMgr to set
	 */
	public void setActFeedMgr(IActivityFeedManager actFeedMgr) {
		this.actFeedMgr = actFeedMgr;
	}

	public ICSSManager getCssManager() {
		return cssManager;
	}

	public void setCssManager(ICSSManager cssManager) {
		this.cssManager = cssManager;
	}

	public NZoneClient(String openfireip, String networkingserver) {
		this.nzoneServerCssID = networkingserver;
		openfireIpAddress = openfireip;
		myServiceType = "client";
		
		

	};

	public NZoneClient() {
		this.nzoneServerCssID = new String("cssconf.ict-societies.eu");

		openfireIpAddress = new String("127.0.0.1");
		myServiceType = "client";
		this.zoneDetails = null;

	};

	public void initialize() {
		
		log.info("NZoneClient bundle initializing. start");

		myIdentity = getCommManager().getIdManager().getThisNetworkNode();

		log.info("NZoneClient bundle myIdentity is." + myIdentity.getJid());

		try {
			cxtChangeList = new NZoneCxtChangeList();
			cxtChangeList.client = this;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		sharepreferences = new NZoneSharePreferences(getNzoneClientComms().getSharePreferences());
		preferences = new NZonePreferences(getNzoneClientComms().getPreferences());
		log.info("NZoneClient bundle initializing.end");
		
	}
	
	// We need to do this because we can't access our service id etc until we are started
	@Override
	public void delayedInit() {
		
		if (bInitialised)
			return;
		if (bInitialising)
			return;
		bInitialising = true;
		
		log.info("NZoneClient bundle delayedInit called.");
		
		
		if (myServiceID == null)
			myServiceID = getServiceMgmt().getMyServiceId(this.getClass());

		if (requestorService == null)
			requestorService = new RequestorService(myIdentity, myServiceID);
		
		
		// this should already exist (but not guaranteed), so just in case we have kicked off too soon
		if (this.getContextAtributeAsPlatform(CtxAttributeTypes.LOCATION_SYMBOLIC.toString()) == null)
			updateContextAtributeAsPlatform(CtxAttributeTypes.LOCATION_SYMBOLIC.toString(), "");

		
		if (this.getZoneDetails() == null) 
		{
			log.error("NZoneClient : Invalid configuration, No Data for Main Cis");
			bInitialising = false;
			return;
		}
		
		//for each zone, go ahead a check that the context attribute exists
		// if it doesn't create it before we go any further, or we won't be able to join any of the zones
		for (int index=0; index < this.zoneDetails.size(); index++)
		{
			if (this.zoneDetails.get(index).getCtxAttribName() != null && !(this.zoneDetails.get(index).getCtxAttribName().isEmpty()))
				updateContextAtributeAsPlatform(this.zoneDetails.get(index).getCtxAttribName(), "true");
		}
		
		String mainCisId = getNzoneClientComms().getMainZoneCisID();

		// Now we need to check that we are a member of this cis,
		// if not we need to join it
		if ((mainCisId == null) || (mainCisId.isEmpty())) {
			log.error("NZoneClient : Invalid configuration, No Data for Main Cis");
			bInitialising= false;
			return;
		}

	//	getNzoneClientComms().
		
		
		
		mainCis = joinCis(mainCisId, true);
		
		// TODO:  kick off the threads to get the avators for each of the members of main zone
		
		// Now see if we should be part of a zone already
		checkMembershipOnStartup();
		
		getMyProfile();
		
		registerForContextChanges(new Object());
		
		bInitialised = true;
		bInitialising= false;
	};

	public void cleanUp() {
		log.debug("NZoneClient bundle initialized.");

		if (currentZoneCis != null)
			leaveCis(currentZoneCis.getCisId());
	//	if (mainCis != null)
	//		leaveCis(mainCis.getCisId());

	};

	private ICis joinCis(String cisJid, boolean retry) {
		

		ICis joinedCis;
		log.debug("joinCis Start");

		// We can't join cis's we are already a member of, so check that first
		// if we are a member already, no need to do anything
		joinedCis = getCisManager().getCis(cisJid);

		if (joinedCis != null) {
			log.debug("joinCis already a member");
			return joinedCis;
		}

		CisAdvertisementRecord adRec = getCisAdvert(cisJid);
		
		

		do
		{
			NZoneCisCallback cisCallback = new NZoneCisCallback();
			
			getCisManager().joinRemoteCIS(adRec, cisCallback.iCisManagerCallback);

			try {
				cisCallback.cisManagerCallbackSignal.await(60L, TimeUnit.SECONDS); // Is 180 seconds enough??
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}

			if (cisCallback.bResponseReceived)
				retry = false;

			cisCallback = null;
			
			joinedCis = getCisManager().getCis(cisJid);

			log.debug("joinZoneCis finsihed");
			if (joinedCis != null)
				return joinedCis;
		} while (retry); // if retry then keep on retrying until we are able to join, 
	
		return null; // problem joining
	}

	private void leaveCis(String cisJid) {

		log.debug("leaveCis Start");
		
		// We can't leave cis's we are not a member of, so check that first
		// if we are not a member , no need to do anything
		ICis cisToLeave = getCisManager().getCis(cisJid);

		if (cisToLeave == null)
			return;

		NZoneCisCallback cisCallback = new NZoneCisCallback();
		getCisManager().leaveRemoteCIS(cisJid, cisCallback.iCisManagerCallback);
		try {
			cisCallback.cisManagerCallbackSignal.await();
		} catch (InterruptedException e) {
			log.error("Problem leaving Cis" + e.getLocalizedMessage());
			return;
		}

		cisToLeave = getCisManager().getCis(cisJid);

		if (cisToLeave != null) {
			// Problem leave cis
			log.error("Problem leaving Cis");
		}
		cisCallback = null;
		log.debug("leaveCis End");
		return;
	}

	@Override
	public boolean bJoinZone(String zoneID) {


		if (currentZoneCis != null) {
			if (currentZoneCis.getCisId().contentEquals(zoneID) == true) {
				// We are already a member of this zone, just return true
				return true;
			}
			// if we get this far, we need to leave current zone before entered
			// new zone
			this.leaveCis(currentZoneCis.getCisId());
		}
		
		// Now we are ready to join
		currentZoneCis = this.joinCis(zoneID, true);

		
		if (currentZoneCis == null)
		{
			getNzoneClientComms().updateMyZone("");
			return false;
		}
		
		return true;

	}

	
	@Override
	public List<UserPreview> getSuggestedList(boolean bMainZone) {

		// Return a list of suggested contacts from the current zone,
		// if no current zone, return a list from main zone
		
		List<UserPreview> list = new ArrayList<UserPreview>();

		HashMap<String, Integer> csslist = null; // what is returned from cssmanager
	
		try {
				
			IIdentity[] identities = new IIdentity[1];
			try {
				if (bMainZone)
					identities[0] = getCommManager().getIdManager().fromJid(mainCis.getCisId());
				else
					identities[0] = getCommManager().getIdManager().fromJid(currentZoneCis.getCisId());
			} catch (Exception  e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			FriendFilter filter = new FriendFilter();
			filter.setCis_Jids(identities);
			int nzonefilterflag = BitCompareUtil.CIS_MEMBERS_BIT + BitCompareUtil.LINKEDIN_BIT;
			
			filter.setFilterFlag(nzonefilterflag);
			log.info("Calling cssmanager : " );
				
			Future<HashMap<String, Integer>> csslistfut = getCssManager().getUserSNSDetails(filter);
			if (csslistfut != null)
				csslist = csslistfut.get();
			
			if (csslist == null || csslist.size() == 0)
				return list; // return empty list
				
		} catch (InterruptedException e) {
			log.error("Problem getting suggested friends list for css mnager " + e.getLocalizedMessage());
		} catch (ExecutionException e) {
			log.error("Problem getting suggested friends list for css mnager " + e.getLocalizedMessage());
		}
			
		List<String>  userList = new ArrayList<String>();
		userList.addAll(csslist.keySet());
		
		if ((nzoneServerCssID != null) && !(nzoneServerCssID.isEmpty()))// don't show conf admin on member list of gui
			if (userList.contains(nzoneServerCssID))
				userList.remove(nzoneServerCssID);
			
		List<UserDetails> userDets = this.getNzoneClientComms().getUserDetailsList(userList);
		
		Integer cssFlag = 0;
		int rank = 0;
		
		// Now populate UserPreview
		for (int i = 0; i < userDets.size(); i++) {
			if ((userDets.get(i).getUserid() != null) && !(userDets.get(i).getUserid().contains(myIdentity.getBareJid()))) 
			{
					
				UserPreview userPre = new UserPreview();
				userPre.setUserid(userDets.get(i).getUserid());
				
				userPre.setImageSrc(this.getAvatar(userPre.getUserid()));
				if ((userPre.getImageSrc() != null) && !(userPre.getImageSrc().isEmpty()))
					userPre.setUseDefaultImage(false); 
				else
					userPre.setUseDefaultImage(true); 
					
				cssFlag = 0;
				rank = 0;
				List<String> tags = new ArrayList<String>();
				if (csslist != null)
				{
						try {
							if (csslist.containsKey(userPre.getUserid()))
								cssFlag = csslist.get(userPre.getUserid()).intValue();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}
					
				if ((cssFlag & BitCompareUtil.LINKEDIN_BIT) == BitCompareUtil.LINKEDIN_BIT)
				{
					log.debug(" cssmanager : linked set " + userPre.getUserid() + " " + cssFlag);
					tags.add("linked");
					rank +=1;
				}
					
					
				
				userPre.setCompany(userDets.get(i).getCompany());
				userPre.setDisplayName(userDets.get(i).getDisplayName());
				userPre.setZone(userDets.get(i).getCurrentzone());
				if (userDets.get(i).getCompany() != "") {
					// Check is we have 'tagged' this
					// company
					// get UserPrefences
					if (preferences != null) {
						if (preferences.isPreferred("company", userDets.get(i).getCompany())) {
							log.debug("Tagging user as preferred");
							tags.add("Learned Preference");
							rank +=10;
						}
					}
					
					// check if we have shared interests
					log.debug("checking shared interests");
					if ((userDets.get(i).getInterests() != null) &&  (getMyInterestsCached() != null))
					{
						if ((userDets.get(i).getInterests().size() > 0) && (getMyInterestsCached().size() > 0))
						{
							for (int index = 0; index < getMyInterestsCached().size(); index++)
							{
								if (userDets.get(i).getInterests().contains(getMyInterestsCached().get(index)))
								{
									rank +=3;
									if (!tags.contains("Common Interest"))
										tags.add("Common Interest");
								}
							}
						}
					}
					
				}
				userPre.setTags(tags);
				userPre.setRank(rank);
		
				list.add(userPre);
			}
		}


		Collections.sort(list, new Comparator<UserPreview>() {

		        public int compare(UserPreview o1, UserPreview o2) {
		            return o2.getRank().compareTo(o1.getRank());
		        }
		    });
		 
		return list;
	}

	@Override
	public void getActivityFeed(boolean bMainZone) {
	};

	@Override
	public void sendSocFR() {
	};

	@Override
	public ShareInfo getShareInfo(String friendid) {
		return getNzoneClientComms().getShareInfo(friendid);
	};

	@Override
	public void updateShareInfo(ShareInfo info)
	{
		getNzoneClientComms().updateShareInfo(info);
	};
	
	@Override
	public void saveMyProfile() {
	};

	@Override
	public void saveExtraInfo() {
	};

	@Override
	public void posttoSN() {
	};

	@Override
	public void setAsPreferred(String type, String value) {

		// get UserPrefences
		if (preferences == null) {
			preferences = new NZonePreferences(getNzoneClientComms().getPreferences());
			log.debug("Loaded prefererces are " + preferences.toString());
		}
		preferences.addPreferred(type, value);
		log.debug("New prefererces are " + preferences.toString());
		//Platform preferences not suitable for our scenario for trial
		//processUserPreference(PREF_TAG, preferences.toString());
		getNzoneClientComms().savePreferences(preferences.toString());

	}

	@Override
	public boolean isPreferred(String type, String value) {

		// get UserPrefences
		if (preferences == null) {
			return false;
		}

		return preferences.isPreferred(type, value);

	}

	@Override
	public void removeAsPreferred(String type, String value) {

	//	IActivity act = actFeed.getEmptyIActivity();
	//	act.setActor("User");
	//	act.setVerb("removed");
	//	act.setObject(value + " removed as a preferred" + type);
	//	actFeed.addActivity(act);

	
		// get UserPrefences
		if (preferences == null) {
			preferences = new NZonePreferences(getNzoneClientComms().getPreferences());
			log.debug("Loaded prefererces are " + preferences.toString());
		}
		preferences.removePreferred(type, value);
		log.debug("New prefenreces are " + preferences.toString());

		//Platform preferences not suitable for our scenario for trial
		//processUserPreference(PREF_TAG, preferences.toString());

		getNzoneClientComms().savePreferences(preferences.toString());

	}

	@Override
	public void setAsSharePreferred(String type, String value, int sharevalue) {

		
		
		// get UserPrefences
		if (sharepreferences == null) {
			sharepreferences = new NZoneSharePreferences(getNzoneClientComms().getSharePreferences());
			log.info("Loaded prefererces are " + preferences.toString());
		}
		sharepreferences.addSharePreferred(type, value,sharevalue);
		log.info("New share prefererces are " + sharepreferences.toString());
		getNzoneClientComms().saveSharePreferences(sharepreferences.toString());
	}

	@Override
	public int isSharePreferred(String type, String value) {

		// get UserPrefences
		if (sharepreferences == null) 
			sharepreferences = new NZoneSharePreferences(getNzoneClientComms().getSharePreferences());


		return sharepreferences.getSharePreferred(type, value);

	}

	@Override
	public void removeAsSharePreferred(String type, String value) {

		// get UserPrefences
		if (sharepreferences == null) {
			sharepreferences = new NZoneSharePreferences(getNzoneClientComms().getSharePreferences());
			log.debug("Loaded prefererces are " + preferences.toString());
		}
		sharepreferences.removePreferred(type, value);
		log.debug("New sharepreferences are " + sharepreferences.toString());

		getNzoneClientComms().saveSharePreferences(sharepreferences.toString());

	}

	@SuppressWarnings("unused") // Not using until access control fixed on platform
	private void updateContextAtribute(String ctxAttribName, String value) {
		log.debug("updateContextAtribute Start");

		if (myServiceID == null)
			myServiceID = getServiceMgmt().getMyServiceId(this.getClass());

		if (requestorService == null)
			requestorService = new RequestorService(myIdentity, myServiceID);

		try {
			// retrieve the CtxEntityIdentifier of the CSS owner context entity
			CtxEntityIdentifier ownerEntityId = this
					.getCtxBroker()
					.retrieveIndividualEntityId(
							requestorService,
							getCommManager().getIdManager()
									.getThisNetworkNode()).get();
			// create a context attribute under the CSS owner context entity

			Future<List<CtxIdentifier>> ctxIdentLookupFut = this.getCtxBroker()
					.lookup(requestorService, ownerEntityId,
							CtxModelType.ATTRIBUTE, ctxAttribName);
			// Thread.sleep(1000);
			List<CtxIdentifier> ctxIdentLookup = ctxIdentLookupFut.get();
			CtxIdentifier ctxIdent = null;
			CtxAttribute ctxAttr = null;

			if ((ctxIdentLookup != null) && (ctxIdentLookup.size() > 0))
				ctxIdent = ctxIdentLookup.get(0);

			if (ctxIdent == null) {
				ctxAttr = this
						.getCtxBroker()
						.createAttribute(requestorService, ownerEntityId,
								ctxAttribName).get();
			} else {
				Future<CtxModelObject> netUserAttrFut = this.getCtxBroker()
						.retrieve(requestorService, ctxIdent);
				// Thread.sleep(1000);
				ctxAttr = (CtxAttribute) netUserAttrFut.get();
			}
			// assign a String value to the attribute
			ctxAttr.setStringValue(value);
			ctxAttr.setValueType(CtxAttributeValueType.STRING);

			// update the attribute in the Context DB
			ctxAttr = (CtxAttribute) this.getCtxBroker()
					.update(requestorService, ctxAttr).get();

		} catch (InterruptedException e) {
			log.error("Error updating " + ctxAttribName);
		} catch (ExecutionException e) {
			log.error("Error updating " + ctxAttribName);
		} catch (CtxException e) {
			
			log.error("Error updating " + ctxAttribName);
		}

	}

	@SuppressWarnings("unused") // Not using until access control fixed on platform
	private String getContextAtribute(String ctxAttribName) {
		log.debug("getContextAtribute Start");
		CtxAttribute ctxAttr = null;

		if (requestorService == null) {
			if (myServiceID == null)
				myServiceID = getServiceMgmt().getMyServiceId(this.getClass());
			requestorService = new RequestorService(myIdentity, myServiceID);
		}

		try {
			// retrieve the CtxEntityIdentifier of the CSS owner context entity
			CtxEntityIdentifier ownerEntityId = this
					.getCtxBroker()
					.retrieveIndividualEntityId(
							requestorService,
							getCommManager().getIdManager()
									.getThisNetworkNode()).get();
			// create a context attribute under the CSS owner context entity

			Future<List<CtxIdentifier>> ctxIdentLookupFut = this.getCtxBroker()
					.lookup(requestorService, ownerEntityId,
							CtxModelType.ATTRIBUTE, ctxAttribName);
			// Thread.sleep(1000);
			List<CtxIdentifier> ctxIdentLookup = ctxIdentLookupFut.get();
			CtxIdentifier ctxIdent = null;

			if ((ctxIdentLookup != null) && (ctxIdentLookup.size() > 0))
				ctxIdent = ctxIdentLookup.get(0);

			if (ctxIdent == null) {
				ctxAttr = this
						.getCtxBroker()
						.createAttribute(requestorService, ownerEntityId,
								ctxAttribName).get();
			} else {
				Future<CtxModelObject> ctxAttrFut = this.getCtxBroker()
						.retrieve(requestorService, ctxIdent);
				// Thread.sleep(1000);
				ctxAttr = (CtxAttribute) ctxAttrFut.get();
				
			}
		

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CtxException e) {
			log.info("Error reading " + ctxAttribName + " from context broker");
		}

		log.debug("getContextAtribute End");

		if (ctxAttr == null)
			return null;
		
		return ctxAttr.getStringValue();

	}

	private void loadCisAdverts(List<String> cisIdToRetrieve) {

		cisDirCallbackResult = null;

		NZoneCisDirCallback cisDirCb = new NZoneCisDirCallback();

		getCisDirectoryClient().searchByIDS(cisIdToRetrieve,cisDirCb.iCisDirectoryCallback);

		try {
			cisDirCb.cisDirCallbackSignal.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		cisDirCallbackResult = cisDirCb.adList;

		if (cisDirCallbackResult != null) {
			for (int index = 0; index < cisDirCallbackResult.size(); index++) {
				// Todo : add check in here to see if should replace
				localCisAds.add(cisDirCallbackResult.get(index));
			}
		}
	}

	private CisAdvertisementRecord getCisAdvert(String cisIdToRetrieve) {

		// Check if we have a copy locally
		for (int i = 0; i < localCisAds.size(); i++) {
			if (localCisAds.get(i).getId().compareTo(cisIdToRetrieve) == 0)
				return localCisAds.get(i);
		}
		int newStartPos = localCisAds.size();

		List<String> lIdList = new ArrayList<String>();
		lIdList.add(cisIdToRetrieve);
		loadCisAdverts(lIdList);

		// Check if we have a copy locally, don't bother checking ones we have
		// checked already
		for (; newStartPos < localCisAds.size(); newStartPos++) {
			if (localCisAds.get(newStartPos).getId().compareTo(cisIdToRetrieve) == 0)
				return localCisAds.get(newStartPos);
		}

		return null;

	}

	@Override
	public List<ZoneDetails> getZoneDetails() {

		
		if (this.zoneDetails == null)
		{
			log.info("Getting Zone details");
			this.zoneDetails = getNzoneClientComms().getZoneDetails();
		}
		return this.zoneDetails;
		
		
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

	//Platform preferences not suitable for our scenario for trial
	@SuppressWarnings("unused")
	private void processUserPreference(String parameterName, String value) {
		log.debug("processUserPreference: " + parameterName + " = " + value);

		if (myServiceID == null)
			myServiceID = getServiceMgmt().getMyServiceId(this.getClass());

		// create action object and send to uam
		IAction action = new Action(myServiceID, myServiceType, parameterName,
				value, true, false, true);
		if (getUam() != null) {
			log.info("processUserPreference calling uam start ");
			getUam().monitor(myIdentity, action);
			log.info("processUserPreference calling uam end");
		}
	}

	@SuppressWarnings("unused")
	private String getUserPreference(String parameterName) {
		String result = "";
		try {
			if (myServiceID == null)
				myServiceID = getServiceMgmt().getMyServiceId(this.getClass());
			log.info("getUserPreference : calling getPersoMgr().getPreference start");
			Future<IAction> futureOutcome = getPersoMgr().getPreference(
					requestorService, myIdentity, myServiceType, myServiceID,
					parameterName);
			IAction outcome = futureOutcome.get();
			log.info("getUserPreference : calling getPersoMgr().getPreference end");
			if (outcome != null) {
				log.info("Successfully retrieved preference outcome: "
						+ outcome.getvalue());
				result = outcome.getvalue();
			} else {
				log.info("No preference was found");
			}
		} catch (Exception e) {
			log.error("Error retrieving preference");
			e.printStackTrace();
		}
		log.debug("Preference request result = " + result);
		return result;
	}

	// Put in to test implicit decision making
	
	@SuppressWarnings("unused")
	private void recordActionShowProfile() {

		String parameterName = new String("nzuseraction");
		String value = new String("showprofile");

		if (myServiceID == null)
			myServiceID = getServiceMgmt().getMyServiceId(this.getClass());

		// create action object and send to uam
		IAction action = new Action(myServiceID, myServiceType, parameterName,
				value, true, false, true);

		log.info("recordActionShowProfile : Sending action to UAM: "
				+ action.toString());
		getUam().monitor(myIdentity, action);
		log.info("recordActionShowProfile : Sent action to UAM: "
				+ action.toString());
	}

	
	@SuppressWarnings("unused")
	private void recordActionEnterZone() {

		String parameterName = new String("nzuseraction");
		String value = new String("enterzone");

		if (myServiceID == null)
			myServiceID = getServiceMgmt().getMyServiceId(this.getClass());
		// create action object and send to uam
		IAction action = new Action(myServiceID, myServiceType, parameterName,
				value, true, false, true);
		log.info("recordActionEnterZone : Sending action to UAM: "
				+ action.toString());
		getUam().monitor(myIdentity, action);
		log.info("recordActionEnterZone : Sent action to UAM: "
				+ action.toString());
	}

	@Override
	public UserDetails getMyProfile() {

		// check if we have a profile saved, if not create a new profile with
		// info from css record in context

		
		if (this.myDets == null)
			this. myDets = getNzoneClientComms().getMyDetails();
		
		if (this.myDets == null)
			myDets = new UserDetails();

		if (myDets.getDisplayName() == null
				|| (myDets.getDisplayName().length() == 0)) {
			// profile not populated from context yet
			myDets.setDisplayName(getContextAtributeAsPlatform(CtxAttributeTypes.NAME.toString()));
			myDets.setCompany(getContextAtributeAsPlatform(CtxAttributeTypes.ADDRESS_WORK_CITY.toString()));
			myDets.setPosition(getContextAtributeAsPlatform(CtxAttributeTypes.WORK_POSITION.toString()));
			myDets.setEmail(getContextAtributeAsPlatform(CtxAttributeTypes.EMAIL.toString()));
			myDets.setSex(getContextAtributeAsPlatform(CtxAttributeTypes.SEX.toString()));

			//Social Data Context kicks off access control
			// access control not suitable for our scerani for trial to bypass for now
			/*
			NZoneSocialData data =	new NZoneSocialData();
			data = getSnsData("facebook");
			myDets.setFacebookID(data.getSnsid());
			
			data = getSnsData("twitter");
			myDets.setTwitterID(data.getSnsid());
			
			data = getSnsData("linkedin");
			myDets.setLinkedInID(data.getSnsid());
			
			data = getSnsData("googleplus");
			myDets.setGoogleplusID(data.getSnsid());
			
			data = getSnsData("foursquare");
			myDets.setFoursqID(data.getSnsid());
			*/
			myDets.setFacebookID("");
			myDets.setTwitterID("");
			myDets.setLinkedInID("");
			myDets.setGoogleplusID("");
			myDets.setFoursqID("");
			
			
			
			getNzoneClientComms().updateMyDetails(myDets);
			
			
		}

		this.setMyInterestsCached(myDets.getInterests());
		
		this.getAvatar();
		
		return myDets;

		// getContextAtribute(CtxAttributeTypes.NAME.toString());
		// getContextAtribute(CtxAttributeTypes.EMAIL.toString());

	};

	@Override
	public ZoneDisplayDetail getCurrentZone() {

		ZoneDisplayDetail displayDets = new ZoneDisplayDetail();
		if (currentZoneCis != null) {
			for (int i = 0; i < this.getZoneDetails().size(); i++) {
				// Find our one
				if (this.getZoneDetails().get(i).getCisid()
						.contains(currentZoneCis.getCisId()))
				{
					displayDets.setZoneNo(i);
					displayDets.setImageOffsetTopProfile(this.getZoneDetails().get(i).getImageoffsettopprofile());
					displayDets.setImageOffsetLeftProfile(this.getZoneDetails().get(i).getImageoffsetleftprofile());
					displayDets.setImageOffsetTopOther(this.getZoneDetails().get(i).getImageoffsettopother());
					displayDets.setImageOffsetLeftOther(this.getZoneDetails().get(i).getImageoffsetleftother());
					displayDets.setZoneName(this.getZoneDetails().get(i).getZonename());
					return displayDets;
				}
			}
		}

		return displayDets;

	};

	@Override
	public UserDetails getUserProfile(String userID) {
		return getNzoneClientComms().getUserDetails(userID);
	}

	@Override
	public boolean isProfileSetup() {
		// check if we have a profile saved, if not create a new profile with
		// info from css record in context

		UserDetails userDets = getNzoneClientComms().getMyDetails();
		if ((userDets != null) && (userDets.getDisplayName() != null)) {
			if (userDets.getDisplayName().length() > 0)
				return true;
		}
		return false;
	};

		
	@SuppressWarnings("unused")
	private NZoneSocialData getSnsData(String whatSns) {

		NZoneSocialData data = new NZoneSocialData();
		

		log.debug("getSnsData Start");

		if (requestorService == null) {
			if (myServiceID == null)
				myServiceID = getServiceMgmt().getMyServiceId(this.getClass());
			requestorService = new RequestorService(myIdentity, myServiceID);
		}

		try {

			Future<List<CtxEntityIdentifier>> ctxIdentLookupFut = this
					.getCtxBroker().lookupEntities(
							this.requestorService,
							getCommManager().getIdManager()
									.getThisNetworkNode(),
							CtxEntityTypes.SOCIAL_NETWORK,
							CtxAttributeTypes.TYPE, whatSns, whatSns);

			// Thread.sleep(1000);
			List<CtxEntityIdentifier> ctxIdentLookup = ctxIdentLookupFut.get();
			CtxEntityIdentifier ctxIdent = null;
			if ((ctxIdentLookup == null) || (ctxIdentLookup.size() == 0))
				return data;

			ctxIdent = ctxIdentLookup.get(0);

			// the retrieved identifier is used in order to retrieve the context
			// model object (CtxEntity)
			CtxEntity retrievedCtxEntity = (CtxEntity) this.getCtxBroker()
					.retrieve(requestorService, ctxIdent).get();

			// Retrieve CtxAttributes assigned to retrievedCtxEntity
			Set<CtxAttribute> ctxAttrSet = retrievedCtxEntity
					.getAttributes(CtxAttributeTypes.PROFILE_IMAGE_URL);

			if (ctxAttrSet.size() > 0) {
				List<CtxAttribute> ctxAttrList = new ArrayList<CtxAttribute>(
						ctxAttrSet);
				data.setSnsid(ctxAttrList.get(0).getStringValue());
			}
			
			ctxAttrSet = retrievedCtxEntity
					.getAttributes(CtxAttributeTypes.INTERESTS);
			if (ctxAttrSet.size() > 0) {
				List<CtxAttribute> ctxAttrList = new ArrayList<CtxAttribute>(
						ctxAttrSet);
				data.setInterests(ctxAttrList.get(0).getStringValue());
			}


		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			log.info("Unable to retrieve SNS data from context broker");
		}

		log.debug("getSnsData End");

		return data;

	}

	@Override
	public List<PersonalisablePreferenceIdentifier> getPersonalisablePreferences() {
		log.info("..............setIAction called ................");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void registerForContextChanges(Object arg0) {
	
		log.info("registerForContextChanges start");
		
		if (requestorService == null) {
			if (myServiceID == null)
				myServiceID = getServiceMgmt().getMyServiceId(this.getClass());
			requestorService = new RequestorService(myIdentity, myServiceID);
		}

		try {
			// retrieve the CtxEntityIdentifier of the CSS owner context entity
			CtxEntityIdentifier ownerEntityId = this
					.getCtxBroker()
					.retrieveIndividualEntityId(
							requestorService,
							getCommManager().getIdManager()
									.getThisNetworkNode()).get();
			// create a context attribute under the CSS owner context entity

			Future<List<CtxIdentifier>> ctxIdentLookupFut = this.getCtxBroker()
					.lookup(requestorService, ownerEntityId,CtxModelType.ATTRIBUTE, CtxAttributeTypes.LOCATION_SYMBOLIC);
 
			List<CtxIdentifier> ctxIdentLookup = ctxIdentLookupFut.get();
			CtxIdentifier ctxIdent = null;

			if ((ctxIdentLookup != null) && (ctxIdentLookup.size() > 0))
				ctxIdent = ctxIdentLookup.get(0);

			if (ctxIdent != null) {
				this.getCtxBroker().registerForChanges(requestorService, this.cxtChangeList, ctxIdent);
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
			log.error("Unable to register for context changes");
		}

		log.info("registerForContextChanges End");
	};

	@Override
	public void updateLocationManual(String zoneLoc)
	{
		log.info("updateLocationManual Start zoneLoc is " + zoneLoc);
		updateContextAtributeAsPlatform(CtxAttributeTypes.LOCATION_SYMBOLIC.toString(),zoneLoc);
		log.info("updateLocationManual End");
	}

	public void locationChanged() {
		log.info("locationChanged Start ");
		
		String newLoc = this.getContextAtributeAsPlatform(CtxAttributeTypes.LOCATION_SYMBOLIC.toString());
		log.info("locationChanged newLoc is " + newLoc);
		
		if ((this.getZoneDetails() != null) && (newLoc != null))
		{
			
			log.info("locationChanged checking for matching locations ");
			
			for ( int i = 0 ; i < this.getZoneDetails().size(); i++)
			{
				if (this.getZoneDetails().get(i).getZonelocation() != null)
				{
					log.info("zoneDetails.get(i).getZonelocation()  = " + this.getZoneDetails().get(i).getZonelocation());
					if (newLoc.contains(this.getZoneDetails().get(i).getZonelocation()))
					{
						log.info("Found it : Joining " );
						this.bJoinZone(this.getZoneDetails().get(i).getCisid());
						getNzoneClientComms().updateMyZone(this.getZoneDetails().get(i).getZonename());
						
						return;
					}
				}
			}
		}

	}
	
	
	public void checkMembershipOnStartup() {
		
		String currentLoc = this.getContextAtributeAsPlatform(CtxAttributeTypes.LOCATION_SYMBOLIC.toString());
		
		if ((this.getZoneDetails() != null) && (currentLoc != null))
		{
			for ( int i = 0 ; i < this.getZoneDetails().size(); i++)
			{
				if ((this.getZoneDetails().get(i).getZonelocation() != null) && (currentLoc.contains(this.getZoneDetails().get(i).getZonelocation())))
				{
					if (currentLoc.contains(this.getZoneDetails().get(i).getZonelocation()))
					{
						log.info("Found it " );
						// We have found the zone we should be part of, make sure it is the only one
						this.bJoinZone(this.getZoneDetails().get(i).getCisid());
						getNzoneClientComms().updateMyZone(this.getZoneDetails().get(i).getZonename());
					}
				}
				else
				{
					// make sure we are not a member of a old zone, if we are, leave!
					if ((this.getZoneDetails().get(i).getZonelocation() != null)  && (this.getZoneDetails().get(i).getMainzone() == 0))
					{	
						this.leaveCis(this.getZoneDetails().get(i).getCisid());
					}
				}
			}
		}
		
		
	}
	
	
	
	private void updateContextAtributeAsPlatform(String ctxAttribName, String value) {
		log.info("updateContextAtribute Start :" + ctxAttribName + " : value " + value);

		if (platformRequestor == null)
			platformRequestor = new Requestor(getCommManager().getIdManager().getThisNetworkNode());

		try {
			// retrieve the CtxEntityIdentifier of the CSS owner context entity
			CtxEntityIdentifier ownerEntityId = this.getCtxBroker().retrieveIndividualEntityId(platformRequestor,getCommManager().getIdManager().getThisNetworkNode()).get();
			// create a context attribute under the CSS owner context entity

			Future<List<CtxIdentifier>> ctxIdentLookupFut = this.getCtxBroker().lookup(platformRequestor, ownerEntityId, CtxModelType.ATTRIBUTE, ctxAttribName);
			// Thread.sleep(1000);
			List<CtxIdentifier> ctxIdentLookup = ctxIdentLookupFut.get();
			CtxIdentifier ctxIdent = null;
			CtxAttribute ctxAttr = null;

			if ((ctxIdentLookup != null) && (ctxIdentLookup.size() > 0))
				ctxIdent = ctxIdentLookup.get(0);

			if (ctxIdent == null) {
				log.info("updateContextAtribute " + ctxAttribName + " doesn't exist : creating");
				ctxAttr = this.getCtxBroker().createAttribute(platformRequestor, ownerEntityId,ctxAttribName).get();
			} else {
				Future<CtxModelObject> netUserAttrFut = this.getCtxBroker().retrieve(platformRequestor, ctxIdent);
				ctxAttr = (CtxAttribute) netUserAttrFut.get();
			}
			// assign a String value to the attribute
			ctxAttr.setStringValue(value);
			ctxAttr.setValueType(CtxAttributeValueType.STRING);

			// update the attribute in the Context DB
			ctxAttr = (CtxAttribute) this.getCtxBroker().update(platformRequestor, ctxAttr).get();

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			log.error("Unable to update context atribute " + ctxAttribName);
		}
		
		log.info("updateContextAtribute End :" + ctxAttribName + " : value " + value);

	}

	private String getContextAtributeAsPlatform(String ctxAttribName) {
		log.debug("getContextAtribute Start");
		CtxAttribute ctxAttr = null;

		if (platformRequestor == null)
			platformRequestor = new Requestor(getCommManager().getIdManager().getThisNetworkNode());

		

		try {
			// retrieve the CtxEntityIdentifier of the CSS owner context entity
			CtxEntityIdentifier ownerEntityId = this
					.getCtxBroker()
					.retrieveIndividualEntityId(
							platformRequestor,
							getCommManager().getIdManager()
									.getThisNetworkNode()).get();
			// create a context attribute under the CSS owner context entity

			Future<List<CtxIdentifier>> ctxIdentLookupFut = this.getCtxBroker()
					.lookup(platformRequestor, ownerEntityId,
							CtxModelType.ATTRIBUTE, ctxAttribName);
			// Thread.sleep(1000);
			List<CtxIdentifier> ctxIdentLookup = ctxIdentLookupFut.get();
			CtxIdentifier ctxIdent = null;

			if ((ctxIdentLookup != null) && (ctxIdentLookup.size() > 0))
				ctxIdent = ctxIdentLookup.get(0);

			if (ctxIdent == null) {
				ctxAttr = this
						.getCtxBroker()
						.createAttribute(platformRequestor, ownerEntityId,
								ctxAttribName).get();
			} else {
				Future<CtxModelObject> ctxAttrFut = this.getCtxBroker()
						.retrieve(platformRequestor, ctxIdent);
				// Thread.sleep(1000);
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
			//e.printStackTrace();
			log.error("Unable to read context atribute " + ctxAttribName);
			
		}

		if (ctxAttr == null)
			return null;
		log.debug("getContextAtribute End");

		return ctxAttr.getStringValue();

	}
	
	
	@SuppressWarnings("unused")
	private void userViewingPreferredProfile() {

		String parameterName = new String("nzuseraction");
		String value = new String("viewpreferredprofile");

		if (myServiceID == null)
			myServiceID = getServiceMgmt().getMyServiceId(this.getClass());

		// create action object and send to uam
		IAction action = new Action(myServiceID, myServiceType, parameterName,
				value, true, true, true);

		log.info("userViewingPreferredProfile : Sending action to UAM: "
				+ action.toString());
		getUam().monitor(myIdentity, action);
	}
	
	
	@SuppressWarnings("unused")
	private void userSharedWithViewPreferredProfile() {

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // temp to test
		String parameterName = new String("nzuseraction");
		String value = new String("sharedwithpreferredprofile");

		if (myServiceID == null)
			myServiceID = getServiceMgmt().getMyServiceId(this.getClass());

		// create action object and send to uam
		IAction action = new Action(myServiceID, myServiceType, parameterName,
				value, true, true, true);

		log.info("userSharedWithViewPreferredProfile : Sending action to UAM: "
				+ action.toString());
		getUam().monitor(myIdentity, action);
	}

	@Override
	public void updateMyInterests(List<String> interests) 
	{
		getNzoneClientComms().updateMyInterests(interests);
		this.setMyInterestsCached(interests);
	}

	/**
	 * @return the myInterests
	 */
	private List<String> getMyInterestsCached() {
		return myInterestsCached;
	}

	/**
	 * @param myInterests the myInterests to set
	 */
	private void setMyInterestsCached(List<String> myInterests) {
		this.myInterestsCached = myInterests;
	}

	@Override
	public String getAvatar(String jid) {
		// check if we have already a profile pic for this user
		if (avatarMap.containsKey(jid))
			return avatarMap.get(jid);
		// otherwise, kick of thread to retrieve it from openfire, and return default image for now
		new Thread(new AvatorHandler(this, jid)).start();
		return "/images/profile_pic.png";
	}
	
	@Override
	public String getAvatar() {
		// check if we have already a profile pic for this user
		//TODO FIX this, replace john with myid!
		
		if (myIdentity != null)
			myIdentity = getCommManager().getIdManager().getThisNetworkNode();

		if (avatarMap.containsKey(myIdentity.getBareJid()))
			return avatarMap.get(myIdentity.getBareJid());
		// otherwise, kick of thread to retrieve it from openfire, and return default image for now
		new Thread(new AvatorHandler(this, myIdentity.getBareJid())).start();
		
		return "/images/profile_pic.png";
	}
	
	
	
}
