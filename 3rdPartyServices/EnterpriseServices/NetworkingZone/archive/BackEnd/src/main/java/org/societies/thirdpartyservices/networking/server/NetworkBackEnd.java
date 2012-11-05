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
import java.util.Iterator;
import java.util.List;
import java.util.Hashtable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.thirdpartyservices.networking.directory.NetworkingDirectory;
import org.societies.api.activity.IActivityFeedCallback;
import org.societies.api.cis.attributes.MembershipCriteria;
import org.societies.api.cis.attributes.Rule;
import org.societies.api.cis.management.ICis;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.ext3p.schema.networking.ShareInfo;
import org.societies.api.ext3p.schema.networking.UserDetails;
import org.societies.api.schema.activity.Activity;
import org.societies.api.schema.activityfeed.Activityfeed;



public class NetworkBackEnd {

	public NetworkingDirectory networkingDirectory;
	public ICisManager cisManager;

	//TODO : Probably move to somehwere
	private String schmoozerUser = "schmoozer";
	// TODO : have to have all critieria the same until
	// cis fixed ( issue :  Bug #1459)
	private String locationZoneA = "ZoneA";
	private String locationZoneB = "ZoneA";
	private String locationZoneC = "ZoneA";
	private String locationZoneD = "ZoneA";
	

	//private ICis netCis;
	private List<ICis> netZoneCis;
	private List<Activity> cisActivities;
		 
	private static Logger log = LoggerFactory.getLogger(NetworkBackEnd.class);
	
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
		List<ICis> netCisList  = getCisManager().searchCisByName("Networking Zone Main CIS");
		
		if ((netCisList != null) && (netCisList.size()> 0))
		{
			netZoneCis.add(netCisList.get(0));
			
			netCisList  = getCisManager().searchCisByName("Networking Zone");
			for ( int i = 0; i < netCisList.size(); i++)
			{
				netZoneCis.add(netCisList.get(i));
			}
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
					"Networking Zone",
					"RICH",cisCriteria,""
					);	
			try {
				
				netZoneCis.add(cisResultFut.get());
			
				// Now do the zones 
				//TODO Clean this up
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
				
				symbolicLocations.clear();
				symbolicLocations.add(locationZoneD);
				r2.setValues(symbolicLocations);
				m2.setRule(r2);
				cisCriteria.remove("ZONE_LOCATION_SYMBOLIC");		
				cisCriteria.put("ZONE_LOCATION_SYMBOLIC", m2);
				
				cisResultFut = getCisManager().createCis(
						"Networking Zone D",
						"RICH",cisCriteria,""
						);	
				
				netZoneCis.add(cisResultFut.get());
				
			
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		
		}
		
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
		cisNet.getActivityFeed().getActivities(0 + " " + System.currentTimeMillis(), actCallBack);
		
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
				log.info("NetworkBackEnd activities . element.getActor : " + element.getActor());
				log.info("NetworkBackEnd activities . element.getObject : " + element.getObject());
				log.info("NetworkBackEnd activities . element.getPublished : " + element.getPublished());
				log.info("NetworkBackEnd activities . element.getTarget : " + element.getTarget());
				log.info("NetworkBackEnd activities . element.getVerb : " + element.getVerb());

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



	public List<String> getZoneCisIDs() {
		
		List<String> resultList = new ArrayList<String>();
		
		// don't return first one, it's the main cis
		for ( int i = 1; i < netZoneCis.size(); i++)
		{
			resultList.add(netZoneCis.get(i).getCisId());
		}
		
		return resultList;
	}
	

	public UserDetails getMyDetails(String userid)
	{
		return getNetworkingDirectory().getUserRecord(userid);
		
	}
	
	public UserDetails updateMyDetails(UserDetails newDetails)
	{
		return getNetworkingDirectory().updateUserRecord(newDetails);
	} 
	
	public ShareInfo getShareInfo(String userid, String friendid)
	{
		return getNetworkingDirectory().getShareInfo(userid, friendid);
		
	}
	
	public ShareInfo updateShareInfo(ShareInfo info)
	{
		return getNetworkingDirectory().updateshareInfoUser(info);
	} 
	
}

