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
package org.societies.thirdpartyservices.networking.server;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.activity.IActivityFeedCallback;
import org.societies.api.cis.attributes.MembershipCriteria;
import org.societies.api.cis.attributes.Rule;
import org.societies.api.cis.management.ICis;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.cis.management.ICisParticipant;
import org.societies.api.ext3p.networking.MemberDetails;
import org.societies.api.ext3p.networking.ShareInfo;
import org.societies.api.ext3p.networking.UserDetails;
import org.societies.api.ext3p.networking.ZoneDetails;
import org.societies.api.ext3p.networking.ZoneEvent;
import org.societies.api.schema.activity.Activity;
import org.societies.api.schema.activityfeed.Activityfeed;
import org.societies.thirdpartyservices.networking.directory.NetworkingDirectory;



public class NetworkBackEnd {

	private static int SHARE_PERSONAL = 0x00001;
	private static int SHARE_EMPLOYMENT = 0x00010;
	private static int SHARE_EMPLOY_HISTORY = 0x00100;
	private static int SHARE_EDU_HISTORY = 0x01000;
	private static int SHARE_ABOUT = 0x10000;
	
	public NetworkingDirectory networkingDirectory;
	public ICisManager cisManager;

	//TODO : Probably move to somehwere
	private String schmoozerUser = "schmoozer";
	private String locationZoneA = "ZoneA";
	private String locationZoneB = "ZoneB";
	private String locationZoneC = "ZoneC";
	 
	private static Logger log = LoggerFactory.getLogger(NetworkBackEnd.class);
	
	//private ICis netCis;
	private List<ICis> netZoneCis;
	private List<Activity> cisActivities;
	private List<ZoneDetails> netZoneDetails;
	


	public NetworkBackEnd()	
	{
		log.info("NetworkBackEnd bundle instantiated.");
		netZoneCis = new ArrayList<ICis>();	
	};
	
	
	public void init_service()
	{
		log.info("NetworkBackEnd init_service called.");
		
		// Check to see if a CIS exists for the networking zone
		// if it doesn't create one
		List<ICis> netCisList  = getCisManager().searchCisByName("Networking Zone-Main CIS");
		
		if ((netCisList != null) && (netCisList.size()> 0))
		{
			netZoneCis.add(netCisList.get(0));
		
			/*
			log.info("NetworkBackEnd init_service - Getting list of CIS's.");
			
			netCisList  = getCisManager().searchCisByName("Networking Zone ");
			for ( int i = 0; i < netCisList.size(); i++)
			{
				netZoneCis.add(netCisList.get(i));
				log.info("NetworkBackEnd init_service - Getting list of CIS's : Adding : " + netCisList.get(i));
			} */
		}
			
			
		// TODO: We want to create a CIS based on location and ??, 
		//for now we'll just create a CIS
		
		if (netZoneCis.size() == 0)
		{
			log.info("NetworkBackEnd netCis doesn't exist .");
			
			Hashtable<String, MembershipCriteria> cisCriteria = new Hashtable<String, MembershipCriteria> (); 
			MembershipCriteria m = new MembershipCriteria();
			MembershipCriteria m2 = new MembershipCriteria();
			
			List<String> memberOf = new ArrayList<String>();
			memberOf.add(schmoozerUser);
			Rule r = new Rule("equals",memberOf);
			m.setRule(r);
			cisCriteria.put("MEMBER_OF", m);
			
			Future<ICisOwned> cisResultFut = getCisManager().createCis(
					"Networking Zone-Main CIS",
					"RICH",cisCriteria,""
					);	
			try {
				
				netZoneCis.add(cisResultFut.get());
			
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		
		
		netZoneDetails = getNetworkingDirectory().getZoneDetails();
		
		for ( int nZoneCount = 0; nZoneCount < netZoneDetails.size(); nZoneCount++)
		{
			List<ICis> exitingCisList  = getCisManager().searchCisByName(netZoneDetails.get(nZoneCount).getZonename());
			
			if (exitingCisList != null && exitingCisList.size() > 0)
					netZoneCis.add(exitingCisList.get(0));
			else
			{
				// doesn't exist, must create it
				Hashtable<String, MembershipCriteria> cisCriteria = new Hashtable<String, MembershipCriteria> (); 
				MembershipCriteria m = new MembershipCriteria();
				MembershipCriteria m2 = new MembershipCriteria();
				List<String> memberOf = new ArrayList<String>();
				memberOf.add(schmoozerUser);
				Rule r = new Rule("equals",memberOf);
				m.setRule(r);
				cisCriteria.put("MEMBER_OF", m);
				List<String> symbolicLocations = new ArrayList<String>();
				symbolicLocations.add(netZoneDetails.get(nZoneCount).getZonelocation());
				Rule r2 = new Rule("equals",symbolicLocations);
				m2.setRule(r2);
				cisCriteria.put("ZONE_LOCATION_SYMBOLIC", m2);
				
				Future<ICisOwned> cisResultFut = getCisManager().createCis(
						netZoneDetails.get(nZoneCount).getZonename(),
						"RICH",cisCriteria,""
						);	
				
				try {
					
					netZoneCis.add(cisResultFut.get());
				
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}	
			
		}
		
		
				// Now do the zones 
				//TODO Clean this up
				/*
				List<String> symbolicLocations = new ArrayList<String>();
				symbolicLocations.add(locationZoneA);
				Rule r2 = new Rule("equals",symbolicLocations);
				m2.setRule(r2);
				cisCriteria.put("ZONE_LOCATION_SYMBOLIC", m2);
				
				
				
				cisResultFut = getCisManager().createCis(
						"Networking Zone A",
						"RICH",cisCriteria,""
						);	
				
				netZoneCis.add(cisResultFut.get());
				
				symbolicLocations.clear();
				symbolicLocations.add(locationZoneB);
				r2.setValues(symbolicLocations);
				m2.setRule(r2);
				cisCriteria.remove("ZONE_LOCATION_SYMBOLIC");		
				cisCriteria.put("ZONE_LOCATION_SYMBOLIC", m2);
				
				cisResultFut = getCisManager().createCis(
						"Networking Zone B",
						"RICH",cisCriteria,""
						);	
				
				netZoneCis.add(cisResultFut.get());
				
				symbolicLocations.clear();
				symbolicLocations.add(locationZoneC);
				
				r2.setValues(symbolicLocations);
				m2.setRule(r2);
				cisCriteria.remove("ZONE_LOCATION_SYMBOLIC");		
				cisCriteria.put("ZONE_LOCATION_SYMBOLIC", m2);
				
				cisResultFut = getCisManager().createCis(
						"Networking Zone C",
						"RICH",cisCriteria,""
						);	
				
				netZoneCis.add(cisResultFut.get());
				*/
			/*	
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			*/
		
		
		
		for ( int i = 0; i < netZoneCis.size(); i++)
			getCisActivity(netZoneCis.get(i));
			
		log.info("NetworkBackEnd init_service finished.");
	}
	
	public String  getMyMainCisId() {
		
		return netZoneCis.get(0).getCisId();
	}
	
	
	public List<Activity> getCisActivity(ICis cisNet)
	{
		NetZoneActivitiesCallback actCallBack = new NetZoneActivitiesCallback(cisNet.getCisId());
		cisActivities = null;
		cisNet.getActivityFeed().getActivities(0 + " " + System.currentTimeMillis(), (IActivityFeedCallback) actCallBack);
		
		int i = 0;
		do
		{
			i++;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} while ((cisActivities == null) && (i < 5));
		
		
		if (cisActivities != null)
		{
			Iterator<Activity> it = cisActivities.iterator();
			
			while(it.hasNext()){
				Activity element = it.next();
				log.info(cisNet.getName() + " activities . element.getActor : " + element.getActor());
				log.info(cisNet.getName() + " activities . element.getObject : " + element.getObject());
				log.info(cisNet.getName() + " activities . element.getPublished : " + element.getPublished());
				log.info(cisNet.getName() + " activities . element.getTarget : " + element.getTarget());
				log.info(cisNet.getName() + " activities . element.getVerb : " + element.getVerb());

			}
		}
		else
			log.info("NetworkBackEnd activities . None");
		return cisActivities;
		
		
	}
	
	
	
	class NetZoneActivitiesCallback implements IActivityFeedCallback {

		String parentJid = "";
		
		public NetZoneActivitiesCallback (String parentJid){
			super();
			this.parentJid = parentJid;
		}
		
		public void receiveResult(Activityfeed activityFeedObject){

			//int[] check = {0,0};
			
			cisActivities = activityFeedObject.getGetActivitiesResponse().getActivity();
			
			/*
			Iterator<Activity> it = l.iterator();
			
			while(it.hasNext()){
				Activity element = it.next();
				if(element.getActor().equals("act") )
					check[0] = 1;
				if(element.getActor().equals("act2") )
					check[1] = 1;

		     }
		     */
			
			// check if it found all matching CISs
			// for(int i=0;i<check.length;i++){
			//	 assertEquals(check[i], 1);
			//}

			
			
		}
	}



	public ShareInfo getShareInfo(String userid, String friendid)
	{
		return getNetworkingDirectory().getShareInfo(userid, friendid);
		
	}
	
	public ShareInfo updateShareInfo(ShareInfo info)
	{
		return getNetworkingDirectory().updateshareInfoUser(info);
	}


	
	public NetworkingDirectory getNetworkingDirectory() {
		return networkingDirectory;
	}


	public void setNetworkingDirectory(NetworkingDirectory networkingDirectory) {
		this.networkingDirectory = networkingDirectory;
	}

	public ICisManager getCisManager() {
		return cisManager;
	}

	public void setCisManager(ICisManager cisManager) {
		this.cisManager = cisManager;
	}

	
	public List<String> getZoneCisIDs() {
		List<String> resultList = new ArrayList<String>();
		
		// don't return first one, it's the main cis
		for ( int i = 1; i < netZoneCis.size(); i++)
		{
			resultList.add(netZoneCis.get(i).getCisId());
		}
		
		return resultList;
	}


	public UserDetails getMyDetails(String myuserid) {
		return getNetworkingDirectory().getUserRecord(myuserid);
	}


	public UserDetails getUserDetails(String myuserid, String frienduserid) {
		UserDetails friendDets = getNetworkingDirectory().getUserRecord(frienduserid);
		ShareInfo sharedInfo = getNetworkingDirectory().getShareInfo(frienduserid, myuserid);
		
		// Blank of the info they don't want to share with this user
		if ((sharedInfo.getShareHash() & SHARE_ABOUT) != SHARE_ABOUT) 
		{
			friendDets.setAbout("");
			
		}
		
		if ((sharedInfo.getShareHash() & SHARE_PERSONAL) != SHARE_PERSONAL)
		{
			friendDets.setEmail("");
			friendDets.setHomelocation("");
		}
			
		
		if ((sharedInfo.getShareHash() & SHARE_EMPLOYMENT)  != SHARE_EMPLOYMENT)
		{
			friendDets.setCompany("");
			friendDets.setDept("");
		}
		
		
		if ((sharedInfo.getShareHash() & SHARE_EMPLOY_HISTORY)  != SHARE_EMPLOY_HISTORY)
			friendDets.getEmploymenthistory().clear();
			
		
		if ((sharedInfo.getShareHash() & SHARE_EDU_HISTORY)  != SHARE_EDU_HISTORY) 
			friendDets.getEducationhistory().clear();
		
		
		return friendDets;
	}
	
	public List<UserDetails> getUserDetailsList(String myuserid, List<String> friendsid) {
		
		 List<UserDetails> userDetList = new ArrayList<UserDetails>();
		 UserDetails friendDets = null;
		 ShareInfo sharedInfo = null;
		 
		 for ( int userIndex = 0; userIndex < friendsid.size(); userIndex++)
		 {
			 friendDets = getNetworkingDirectory().getUserRecord(friendsid.get(userIndex));
			 sharedInfo = getNetworkingDirectory().getShareInfo(friendsid.get(userIndex), myuserid);
		
			 //Blank of the info they don't want to share with this user
			 if ((sharedInfo.getShareHash() & SHARE_ABOUT) != SHARE_ABOUT) 
			 {
				 friendDets.setAbout("");
	
			 }
		
			 if ((sharedInfo.getShareHash() & SHARE_PERSONAL) != SHARE_PERSONAL)
			 {
				 friendDets.setEmail("");
				 friendDets.setHomelocation("");
			 }
			
		
			 if ((sharedInfo.getShareHash() & SHARE_EMPLOYMENT)  != SHARE_EMPLOYMENT)
			 {
				 friendDets.setCompany("");
				 friendDets.setDept("");
			 }
		
		
			 if ((sharedInfo.getShareHash() & SHARE_EMPLOY_HISTORY)  != SHARE_EMPLOY_HISTORY)
				 friendDets.getEmploymenthistory().clear();
			
		
			 if ((sharedInfo.getShareHash() & SHARE_EDU_HISTORY)  != SHARE_EDU_HISTORY) 
				 friendDets.getEducationhistory().clear();
		
			 userDetList.add(friendDets);
		 }
		 return userDetList;
	}
	
	public UserDetails updateMyDetails(UserDetails myDetails) {
		return getNetworkingDirectory().updateUserRecord(myDetails);
	} 
		
	
	public List<MemberDetails> getMemberNames(List<String> userids)
	{
		List<MemberDetails> memDetList = new ArrayList<MemberDetails>();
		List<UserDetails>  userDetList = getNetworkingDirectory().searchByID(userids);
		MemberDetails memdet = null;
		for ( int i = 0; i < userDetList.size(); i++)
		{
			memdet = new MemberDetails();
			memdet.setUserid(userDetList.get(i).getUserid());
			memdet.setDisplayName(userDetList.get(i).getDisplayName());
			memDetList.add(memdet);
		}
		
		return memDetList;
		
	}
	
	public List<ZoneEvent> getCisActivity(String cisID)
	{
		
		cisActivities = null;
		ICis cisToQuery = null;
		List<ZoneEvent> eventList = new ArrayList<ZoneEvent>();
		
		for ( int iCisCount = 0; iCisCount < netZoneCis.size(); iCisCount++)
		{
			if (cisID.contentEquals(netZoneCis.get(iCisCount).getCisId()))
			{
				cisToQuery = netZoneCis.get(iCisCount);
				iCisCount = netZoneCis.size();
			}
		}
		
		if (cisToQuery != null)
		{
			NetZoneActivitiesCallback actCallBack = new NetZoneActivitiesCallback(cisID);
			cisToQuery.getActivityFeed().getActivities(0 + " " + System.currentTimeMillis(), (IActivityFeedCallback) actCallBack);
		
			int i = 0;
			do
			{
				i++;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} while ((cisActivities == null) && (i < 5));
		}
		
		if (cisActivities != null)
		{
			Iterator<Activity> it = cisActivities.iterator();
			ZoneEvent zoneEvent = null;
			while(it.hasNext()){
				Activity element = it.next();
				log.info(cisToQuery.getName() + " activities . element.getActor : " + element.getActor());
				log.info(cisToQuery.getName() + " activities . element.getObject : " + element.getObject());
				log.info(cisToQuery.getName() + " activities . element.getPublished : " + element.getPublished());
				log.info(cisToQuery.getName() + " activities . element.getTarget : " + element.getTarget());
				log.info(cisToQuery.getName() + " activities . element.getVerb : " + element.getVerb());

				zoneEvent = new ZoneEvent();
				zoneEvent.setUserid(element.getActor());
				zoneEvent.setUseraction(element.getVerb());
				zoneEvent.setUsername(networkingDirectory.getUserName(element.getActor()));
				eventList.add(zoneEvent);
			}
		}
		else
			log.info("NetworkBackEnd activities . None");
		
		
		return eventList;
		
		
	}
	
	
	public List<String> getNotes(String userid, String friendid) {
		return getNetworkingDirectory().getNotes(userid, friendid);
	} 
	
	public List<String> addNote(String userid, String friendid, String note) {
		return getNetworkingDirectory().addNote(userid, friendid, note);
	} 
	
	public List<ZoneDetails> getZoneDetails()
	{
		List<ZoneDetails> zoneDets = getNetworkingDirectory().getZoneDetails();
		
		for ( int i = 0; i < zoneDets.size(); i++)
		{
			try {
				zoneDets.get(i).setZonemembercount(0);
	
				for ( int j = 0; j < netZoneCis.size(); j++)
				{
					if (netZoneCis.get(j).getName().contains(zoneDets.get(i).getZonename()))
					{
						
					
						log.info("NetworkBackEnd getZoneDetails . Getting member count for " + zoneDets.get(i).getZonename());
				
						ICisOwned tempcis = getCisManager().getOwnedCis(netZoneCis.get(j).getCisId());
						if (tempcis !=null)
						{
							Future<Set<ICisParticipant>> tempmemberlistfut = tempcis.getMemberList();
							if (tempmemberlistfut != null) {
								Set<ICisParticipant> tempmemberlist = tempmemberlistfut.get();
								if (tempmemberlist != null) {
									zoneDets.get(i).setZonemembercount(tempmemberlist.size());
										log.info("NetworkBackEnd getZoneDetails . member count for " + zoneDets.get(i).getZonename() + " is " + tempmemberlist.size());
								}
							}
						}
						j = netZoneCis.size();
					}
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		return zoneDets;
		
	}
}

