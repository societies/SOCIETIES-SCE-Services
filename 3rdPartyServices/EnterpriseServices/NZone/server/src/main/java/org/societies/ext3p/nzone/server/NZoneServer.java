package org.societies.ext3p.nzone.server;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.attributes.MembershipCriteria;
import org.societies.api.cis.attributes.Rule;
import org.societies.api.cis.management.ICis;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.cis.management.ICisParticipant;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.broker.ICtxBroker;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.ext3p.nzone.NZoneConsts;
import org.societies.api.ext3p.nzone.server.INZoneServer;
import org.societies.api.ext3p.schema.nzone.ShareInfo;
import org.societies.api.ext3p.schema.nzone.UserDetails;
import org.societies.api.ext3p.schema.nzone.ZoneDetails;
import org.societies.api.identity.Requestor;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.ActionConstants;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.ConditionConstants;
import org.societies.api.schema.identity.DataIdentifierScheme;




@SuppressWarnings("deprecation")
public class NZoneServer implements INZoneServer{
	
	
	private static Logger log = LoggerFactory.getLogger(NZoneServer.class);	

	private NZoneDirectory nzoneDirectory;
	public ICisManager cisManager;
	private ICommManager commManager;
	private ICtxBroker ctxBroker;
	
	
	private List<ZoneDetails> netZoneDetails;
	private ICisOwned mainCis;
	private List<ICisOwned> subZones;
	private Requestor requestor;

	
	
	
	
	public NZoneServer()	
	{
		log.info("NZoneServer bundle instantiated.");
		netZoneDetails = new ArrayList<ZoneDetails>();
		subZones = new ArrayList<ICisOwned>();
	}
	
	public void initialize()
	{
		if (requestor == null)
			requestor = new Requestor(getCommManager().getIdManager().getThisNetworkNode());
		
		
		if (getContextAtribute(CtxAttributeTypes.LOCATION_SYMBOLIC.toString()) == null)
			updateContextAtribute(CtxAttributeTypes.LOCATION_SYMBOLIC.toString(), "");
		
		
		netZoneDetails = getNzoneDirectory().getZoneDetails();
				
		if ((netZoneDetails == null) || (netZoneDetails.size() < 1))
		{
			log.error("NZoneServer : Invalid configuration data");
			return;
		}
		
		for ( int index = 0; index < netZoneDetails.size(); index++)
		{
			
			if (this.getContextAtribute(netZoneDetails.get(index).getCtxAttribName()) == null)
				updateContextAtribute(netZoneDetails.get(index).getCtxAttribName(), "true");
		
			
			// Check to see if a CIS exists for the networking zone
			// if it doesn't create one
			List<ICis> netCisList  = getCisManager().searchCisByName(netZoneDetails.get(index).getZonename());
				
			if ((netCisList == null) || (netCisList.size() < 1))
			{
				// Doesn't exist, we need to create it
					
				Hashtable<String, MembershipCriteria> cisCriteria = new Hashtable<String, MembershipCriteria> (); 
				
				
				
				MembershipCriteria m = new MembershipCriteria();
										
				List<String> memberOf = new ArrayList<String>();
				memberOf.add("true");
				Rule r = new Rule("equals",memberOf);
				m.setRule(r);
				cisCriteria.put(netZoneDetails.get(index).getCtxAttribName(), m);
				
				if (netZoneDetails.get(index).getMainzone() != 1)
				{
					// for subzone, also need to be in the location
					MembershipCriteria m2 = new MembershipCriteria();
					List<String> symbolicLocations = new ArrayList<String>();
					symbolicLocations.add(netZoneDetails.get(index).getZonelocation());
					Rule r2 = new Rule("equals",symbolicLocations);
					m2.setRule(r2);
					cisCriteria.put(CtxAttributeTypes.LOCATION_SYMBOLIC.toString(), m2);
				}
				
				Future<ICisOwned> cisResultFut = null;
				
						if (netZoneDetails.get(index).getCisCategory().isEmpty() == false)
						{
							cisResultFut = getCisManager().createCis(
									netZoneDetails.get(index).getZonename(),
									netZoneDetails.get(index).getCisCategory(),cisCriteria,netZoneDetails.get(index).getZonename(), createPrivacyPolicy(netZoneDetails.get(index).getCtxAttribName()).toXMLString());
						}else{
							cisResultFut = getCisManager().createCis(
							netZoneDetails.get(index).getZonename(),
							"RICH",cisCriteria,netZoneDetails.get(index).getZonename(), createPrivacyPolicy(netZoneDetails.get(index).getCtxAttribName()).toXMLString());
						}
				try {
					ICisOwned netCis = cisResultFut.get();
					netZoneDetails.get(index).setZonemembercount(netCis.getMemberList().size());
					netZoneDetails.get(index).setCisid(netCis.getCisId());
					if (netZoneDetails.get(index).getMainzone() == 1)
						mainCis = netCis;
					else
						subZones.add(netCis);
					
				} catch (Exception e) {
					e.printStackTrace();
				};

			}
			else
			{
				ICisOwned netcis = getCisManager().getOwnedCis(netCisList.get(0).getCisId());
				netZoneDetails.get(index).setZonemembercount(netcis.getMemberList().size());
				netZoneDetails.get(index).setCisid(netcis.getCisId());
				if (netZoneDetails.get(index).getMainzone() == 1)
					mainCis = netcis;
				else
					subZones.add(netcis);
			
			}
				
				
		}
		
		if (mainCis == null)
		{
			log.error("NZoneServer : Invalid configuration, No Data for Main Cis");
			return;
		}
		
	}

	
	
	/**
	 * @return the nzoneDirectory
	 */
	public NZoneDirectory getNzoneDirectory() {
		return nzoneDirectory;
	}

	/**
	 * @param nzoneDirectory the nzoneDirectory to set
	 */
	public void setNzoneDirectory(NZoneDirectory nzoneDirectory) {
		this.nzoneDirectory = nzoneDirectory;
	}

	public ICisManager getCisManager() {
		return cisManager;
	}

	public void setCisManager(ICisManager cisManager) {
		this.cisManager = cisManager;
	}

	public String getMainZoneCisID() {
		return mainCis.getCisId();
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
	
	public List<String> getZoneCisIDs() {
		List<String> returnData = new ArrayList<String>();
		
		for ( int index = 0; index < subZones.size(); index++)
			returnData.add(subZones.get(index).getCisId());
		return returnData;
	};

	
	

	private RequestPolicy createPrivacyPolicy(String contextAttribName)
	{
	
		List<Action> actionsRC = new ArrayList<Action>();
		actionsRC.add(new Action(ActionConstants.READ));
		actionsRC.add(new Action(ActionConstants.CREATE));
		

		List<Condition> conditionsPublic = new ArrayList<Condition>();
		
		conditionsPublic.add(new Condition(ConditionConstants.SHARE_WITH_3RD_PARTIES,  "1", false));
		conditionsPublic.add(new Condition(ConditionConstants.STORE_IN_SECURE_STORAGE,  "1", false));
		
		List<RequestItem> requests = new ArrayList<RequestItem>();
		requests.add(new RequestItem(new Resource(DataIdentifierScheme.CONTEXT, contextAttribName), actionsRC, conditionsPublic));
		requests.add(new RequestItem(new Resource(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.LOCATION_SYMBOLIC.toString()), actionsRC, conditionsPublic));
		requests.add(new RequestItem(new Resource(DataIdentifierScheme.CIS, "cis-member-list"), actionsRC, conditionsPublic));

		RequestPolicy privacyPolicy = new RequestPolicy(requests);
		
		return privacyPolicy;
	}
	
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

		} catch (Exception e) {
			e.printStackTrace();
		};
		
		
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
			
		} catch (Exception e) {
			e.printStackTrace();
		};

		
		log.info("getContextAtribute End");
		
		return ctxAttr.getStringValue();

		
	}
	
	public ShareInfo getShareInfo(String myuserid, String frienduserid)
	{
		 return getNzoneDirectory().getShareInfo(myuserid, frienduserid);
	}
	
	public boolean updateShareInfo(String myuserid, String frienduserid, int value)
	{
		 return getNzoneDirectory().updateShareInfo(myuserid, frienduserid, value);
	}
	
	public List<UserDetails> getUserDetails(String myuserid, List<String> friendsid) {
		
		 List<UserDetails> userDetList = new ArrayList<UserDetails>();
		 UserDetails friendDets = null;
		 ShareInfo sharedInfo = null;
		 
		 
		 //TODO : Make this more efficent!
		 for ( int userIndex = 0; userIndex < friendsid.size(); userIndex++)
		 {
			 friendDets = getNzoneDirectory().getUserRecord(friendsid.get(userIndex));
			 sharedInfo = getNzoneDirectory().getShareInfo(friendsid.get(userIndex), myuserid);
		
			 //Blank of the info they don't want to share with this user
			 if ((sharedInfo.getShareHash() & NZoneConsts.SHARE_ABOUT) != NZoneConsts.SHARE_ABOUT) 
			 {
				 friendDets.setAbout("");
	
			 }
		
			 if ((sharedInfo.getShareHash() & NZoneConsts.SHARE_PERSONAL) != NZoneConsts.SHARE_PERSONAL)
			 {
				 friendDets.setEmail("");
				 friendDets.setHomelocation("");
				 friendDets.setSex("");
			 }
			
		
			 if ((sharedInfo.getShareHash() & NZoneConsts.SHARE_EMPLOYMENT)  != NZoneConsts.SHARE_EMPLOYMENT)
			 {
				 friendDets.setCompany("");
				 friendDets.setPosition("");
			 }
			 
			 if ((sharedInfo.getShareHash() & NZoneConsts.SHARE_SOCIAL)  != NZoneConsts.SHARE_SOCIAL)
			 {
				 friendDets.setAbout("");
				 friendDets.setFacebookID("");
				 friendDets.setTwitterID("");
				 friendDets.setGoogleplusID("");
				 friendDets.setLinkedInID("");
				 friendDets.setFoursqID("");
			 }
			 
			 if ((sharedInfo.getShareHash() & NZoneConsts.SHARE_INTERESTS)  != NZoneConsts.SHARE_INTERESTS)
			 	 friendDets.getInterests().clear();
			 
		
			 userDetList.add(friendDets);
		 }
		 return userDetList;
	}
	
	public UserDetails getProfileDetails(String myuserid) {
		 return getNzoneDirectory().getUserRecord(myuserid);
	}
	

	public List<ZoneDetails> getNetZoneDetails() {
		return netZoneDetails;
	}
	
	
	public String getPreferences(String userid)
	{
		return getNzoneDirectory().getPreferences(userid);
	}
	
	public boolean savePreferences(String userid, String pref)
	{
		return getNzoneDirectory().savePreferences(userid, pref);
	}
	
	public String getSharePreferences(String userid)
	{
		return getNzoneDirectory().getSharePreferences(userid);
	}
	
	public boolean saveSharePreferences(String userid, String pref)
	{
		return getNzoneDirectory().saveSharePreferences(userid, pref);
	}

	public boolean updateMyDetails(UserDetails details) {
		return getNzoneDirectory().updateUserRecord(details);
		
	}

	public boolean updateInterests(String userid, List<String> interests) {
		UserDetails userDets = getNzoneDirectory().getUserRecord(userid);
		userDets.getInterests().clear();
		userDets.setInterests(interests);
		getNzoneDirectory().updateUserRecord(userDets);
		
		return true;
	}

	public boolean updateUserLocation(String userid, String location) {
		UserDetails userDets = getNzoneDirectory().getUserRecord(userid);
		
		if (userDets == null)
			return false;
		
		if (userDets.getCurrentzone() == null)
			userDets.setCurrentzone(new String(location));
		else
			userDets.setCurrentzone(location);
		
		getNzoneDirectory().updateUserRecord(userDets);
		
		return true;
	}

	@Override
	public List<UserDetails> getZoneMembers(int zoneno) {
		
		List<UserDetails> userDets = new ArrayList<UserDetails>();
		 UserDetails friendDets = null;
		 ShareInfo sharedInfo = null;
		
		
		 String cisid = getNetZoneDetails().get(zoneno).getCisid();
		 
		for ( int i = 0; i < subZones.size(); i++)
		{
			if (subZones.get(i).getCisId().contains(cisid))
			{
				Set<ICisParticipant> memberList = subZones.get(i).getMemberList();
				
				for(ICisParticipant mem : memberList)
				{
					if (!requestor.getRequestorId().getBareJid().contains(mem.getMembersJid()))
					{
					friendDets = getNzoneDirectory().getUserRecord(mem.getMembersJid());
					sharedInfo = getNzoneDirectory().getShareInfo(mem.getMembersJid(), "0");
					
					
					 //Blank of the info they don't want to share with this user
					 if ((sharedInfo.getShareHash() & NZoneConsts.SHARE_ABOUT) != NZoneConsts.SHARE_ABOUT) 
						 friendDets.setAbout("");
				
					 if ((sharedInfo.getShareHash() & NZoneConsts.SHARE_PERSONAL) != NZoneConsts.SHARE_PERSONAL)
					 {
						 friendDets.setEmail("");
						 friendDets.setHomelocation("");
						 friendDets.setSex("");
					 }
					
				
					 if ((sharedInfo.getShareHash() & NZoneConsts.SHARE_EMPLOYMENT)  != NZoneConsts.SHARE_EMPLOYMENT)
					 {
						 friendDets.setCompany("");
						 friendDets.setPosition("");
					 }
					 
					 if ((sharedInfo.getShareHash() & NZoneConsts.SHARE_SOCIAL)  != NZoneConsts.SHARE_SOCIAL)
					 {
						 friendDets.setAbout("");
						 friendDets.setFacebookID("");
						 friendDets.setTwitterID("");
						 friendDets.setGoogleplusID("");
						 friendDets.setLinkedInID("");
						 friendDets.setFoursqID("");
					 }
					 
					 if ((sharedInfo.getShareHash() & NZoneConsts.SHARE_INTERESTS)  != NZoneConsts.SHARE_INTERESTS)
					 	 friendDets.getInterests().clear();
					
					 userDets.add(friendDets);
					}
				}
				return userDets;
			}
		}
		
		
		return userDets;
		
		
	}

	@Override
	public String getZoneName(int arg0) {
		return getNetZoneDetails().get(arg0).getZonename();
	}



		
}

