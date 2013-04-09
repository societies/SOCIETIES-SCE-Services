/**
Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 

(SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp (IBM),
INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
ITALIA S.p.a.(TI), TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
   disclaimer in the documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
 package org.societies.ext3p.nzone.server;


import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.ext3p.schema.nzone.UserDetails;
import org.societies.api.ext3p.schema.nzone.ZoneDetails;
import org.societies.ext3p.nzone.server.model.NZPreferences;
import org.societies.ext3p.nzone.server.model.NZSharePreferences;
import org.societies.ext3p.nzone.server.model.NZUser;
import org.societies.ext3p.nzone.server.model.NZZones;
import org.societies.api.ext3p.schema.nzone.ShareInfo;
import org.societies.ext3p.nzone.server.model.NZShareInfo;




public class NZoneDirectory {
	private SessionFactory sessionFactory;
	private static Logger log = LoggerFactory.getLogger(NZoneDirectory.class);

	
	/**
	 * @return the sessionFactory
	 */
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}


	/**
	 * @param sessionFactory the sessionFactory to set
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}


	public NZoneDirectory() {
		log.info("NZoneDirectory bundle instantiated.");
	}

	
	
	
	
	
	@SuppressWarnings("unchecked")
	public List<ZoneDetails> getZoneDetails() {
		
		Session session = null;
		ZoneDetails zoneRec = null;
		List<ZoneDetails> zoneList = null;
		
		log.info("NZoneDirectory getZoneDetails called.");
		
		try {
			session = sessionFactory.openSession();
			zoneList = new ArrayList<ZoneDetails>();
			
			
			List<NZZones> tmpList = session.createCriteria(NZZones.class).list();
			 
			
			if (tmpList != null && tmpList.size() > 0)
				
			{
				for ( int i = 0; i < tmpList.size(); i++)
				{
					zoneRec = new ZoneDetails();
					zoneRec.setZonename(tmpList.get(i).getZonename());
					zoneRec.setZonelocation(tmpList.get(i).getZonelocation());
					zoneRec.setZonelocationdisplay(tmpList.get(i).getZonelocdisplay());
					zoneRec.setZonetopics(tmpList.get(i).getZonetopics());
					zoneRec.setMainzone(tmpList.get(i).getMainzone());
					zoneList.add(zoneRec);
				}		
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}
		}
		
		return zoneList;
	}
	
	@SuppressWarnings("unchecked")
	public UserDetails getUserRecord(String userid) {
		
		Session session = null;
		UserDetails userRec = null;
		
		log.info("NZoneDirectory getUserRecord called.");
		
		try {
			session = sessionFactory.openSession();
			userRec = new UserDetails();
			
			
			
			List<NZUser> tmpRegistryEntryList = session.createCriteria(NZUser.class)
				.add(Restrictions.eq("userid", userid).ignoreCase()).list();
			 
			
			if (tmpRegistryEntryList != null && tmpRegistryEntryList.size() > 0)
			{
				userRec.setUserid(tmpRegistryEntryList.get(0).getUserid());
				userRec.setCompany(tmpRegistryEntryList.get(0).getCompany());
				userRec.setDisplayName(tmpRegistryEntryList.get(0).getDisplayName());
				userRec.setEmail(tmpRegistryEntryList.get(0).getEmail());
				userRec.setHomelocation(tmpRegistryEntryList.get(0).getHomelocation());
				userRec.setSex(tmpRegistryEntryList.get(0).getSex());
				userRec.setPosition(tmpRegistryEntryList.get(0).getPosition());
				userRec.setFacebookID(tmpRegistryEntryList.get(0).getFacebookID());
				userRec.setLinkedInID(tmpRegistryEntryList.get(0).getLinkedInID());
				userRec.setTwitterID(tmpRegistryEntryList.get(0).getTwitterID());
				userRec.setGoogleplusID(tmpRegistryEntryList.get(0).getGoogleplusID());
				userRec.setFoursqID(tmpRegistryEntryList.get(0).getFoursqID());
				userRec.setAbout(tmpRegistryEntryList.get(0).getAbout());
				List<String> interestList = new ArrayList<String>();
				String[] tokens = tmpRegistryEntryList.get(0).getInterests().split(",");
				for (int i = 0; i < tokens.length; i++)
					interestList.add(tokens[i]);
				userRec.setInterests(interestList);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}
		}
		
		
		return userRec;
	}
	
	public boolean updateUserRecord(UserDetails userRec) {
		
		Session session = null;
		Transaction t = null;
		boolean result = false;
		
		log.info("NZoneDirectory updateUserRecord called.");
		
		
		try {
			session = sessionFactory.openSession();
			t = session.beginTransaction();
			
			NZUser userDb = new NZUser();
			
			userDb.setUserid(userRec.getUserid());
			if (userRec.getCompany() != null)
				userDb.setCompany(userRec.getCompany());
			
			if (userRec.getDisplayName() != null)
				userDb.setDisplayName(userRec.getDisplayName());
			
			if (userRec.getEmail() != null)
				userDb.setEmail(userRec.getEmail());
			
			if (userRec.getHomelocation() != null)
				userDb.setHomelocation(userRec.getHomelocation());
			
			if (userRec.getSex() != null)
				userDb.setSex(userRec.getSex());
			
			if (userRec.getPosition() != null)
				userDb.setPosition(userRec.getPosition());
			
			if (userRec.getFacebookID() != null)
				userDb.setFacebookID(userRec.getFacebookID());
			
			if (userRec.getLinkedInID() != null)
				userDb.setLinkedInID(userRec.getLinkedInID());
			
			if (userRec.getTwitterID() != null)
				userDb.setTwitterID(userRec.getTwitterID());
			
			if (userRec.getGoogleplusID() != null)
				userDb.setGoogleplusID(userRec.getGoogleplusID());
			
			if (userRec.getFoursqID() != null)
				userDb.setFoursqID(userRec.getFoursqID());
			
			if (userRec.getAbout() != null)
				userDb.setAbout(userRec.getAbout());
			
			if (userRec.getAbout() != null)
				userDb.setAbout(userRec.getAbout());
			
			if (userRec.getInterests() != null)
			{
				String interestString = new String();
				interestString = "";
				for (int i = 0; i < userRec.getInterests().size(); i++)
				{
					interestString += userRec.getInterests().get(i);
					if (i < userRec.getInterests().size()-1)
							interestString += ",";
				}
				userDb.setInterests(interestString);

			}
			
			session.saveOrUpdate(userDb);
			t.commit();
			result = true;
			
		} catch (Exception e) {
			e.printStackTrace();
			t.rollback();
		} finally {
			if (session != null) {
				session.close();
			}
		}
		
		
		return result;
	}
	
	
	@SuppressWarnings("unchecked")
	public ShareInfo getShareInfo(String userid, String friendid) {
		
		Session session = sessionFactory.openSession();
		ShareInfo info = new ShareInfo();
		
		// Default to everything just in case
		info.setUserid(userid);
		info.setFriendid(friendid);
		info.setShareHash(0);
		info.setDefaultShareValue(true);
		try {
			
			
			List<NZShareInfo> tmpRegistryEntryList = session.createCriteria(NZShareInfo.class)
				.add(Restrictions.eq("myuserid", userid).ignoreCase())
				.add(Restrictions.eq("friendid", friendid).ignoreCase()).list();
			 
			
			if (tmpRegistryEntryList != null && tmpRegistryEntryList.size() > 0)
			{
				info.setShareHash(tmpRegistryEntryList.get(0).getSharehash());
				
				if (friendid.contentEquals("0"))
					info.setDefaultShareValue(true);
				else
					info.setDefaultShareValue(false);
			} else
			{
				// check for default
				session.clear();
				tmpRegistryEntryList = session.createCriteria(NZShareInfo.class)
						.add(Restrictions.eq("myuserid", userid).ignoreCase())
						.add(Restrictions.eq("friendid", "0").ignoreCase()).list();
				
				if (tmpRegistryEntryList != null && tmpRegistryEntryList.size() > 0)
				{
					info.setShareHash(tmpRegistryEntryList.get(0).getSharehash());
					info.setDefaultShareValue(true);
				
					
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}
		}
		
		
		return info;
	}
	
	@SuppressWarnings("unchecked")
	public boolean updateShareInfo(String userid, String friendid, int value) {
		
		Session session = null;
		Transaction t = null;
		boolean result = false;
		
		try {
			
			session = sessionFactory.openSession();
			t = session.beginTransaction();
			
			List<NZShareInfo> tmpRegistryEntryList = session.createCriteria(NZShareInfo.class)
					.add(Restrictions.eq("myuserid", userid).ignoreCase())
					.add(Restrictions.eq("friendid", friendid).ignoreCase()).list();
			
			NZShareInfo info = null;
			
			if (tmpRegistryEntryList != null && tmpRegistryEntryList.size() > 0)
			{
				info = tmpRegistryEntryList.get(0);
			} else
			{	
				info = new NZShareInfo();
				info.setMyuserid(userid);
				info.setFriendid(friendid);
			}
			
			info.setSharehash(value);
			
			session.saveOrUpdate(info);
			t.commit();
			result = true;
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}
		}
		
		
		return result;
	}

	@SuppressWarnings("unchecked")
	public String getPreferences(String userid) {
		
		Session session = null;
		String prefString = null;
		try {
			session = sessionFactory.openSession();
			
			List<NZPreferences> tmpRegistryEntryList = session.createCriteria(NZPreferences.class)
				.add(Restrictions.eq("myuserid", userid).ignoreCase()).list();
			 
			
			if (tmpRegistryEntryList != null && tmpRegistryEntryList.size() > 0)
			{
				prefString = tmpRegistryEntryList.get(0).getPref();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}
		}
		
		
		return prefString;
	}

	@SuppressWarnings("unchecked")
	public boolean savePreferences(String userid,String prefString) {
		
		Session session = null;
		Transaction t = null;
		boolean result = false;
		try {
			session = sessionFactory.openSession();
			NZPreferences userPref = new NZPreferences();
			List<NZPreferences> tmpRegistryEntryList = session.createCriteria(NZPreferences.class)
				.add(Restrictions.eq("myuserid", userid).ignoreCase()).list();
			 
			
			if (tmpRegistryEntryList != null && tmpRegistryEntryList.size() > 0)
			{
				userPref = tmpRegistryEntryList.get(0);
			}
			else
			{
				userPref.setMyuserid(userid);
			}
			
			userPref.setPref(prefString);
			t = session.beginTransaction();
			session.saveOrUpdate(userPref);
			t.commit();
			result = true;
			
		} catch (Exception e) {
			e.printStackTrace();
			if (t != null)
				t.rollback();
		} finally {
			if (session != null) {
				session.close();
			}
		}
		
		
		return result;
	}

	@SuppressWarnings("unchecked")
	public String getSharePreferences(String userid) {
		
		Session session = null;
		String prefString = null;
		try {
			session = sessionFactory.openSession();
			
			List<NZSharePreferences> tmpRegistryEntryList = session.createCriteria(NZSharePreferences.class)
				.add(Restrictions.eq("myuserid", userid).ignoreCase()).list();
			 
			
			if (tmpRegistryEntryList != null && tmpRegistryEntryList.size() > 0)
			{
				prefString = tmpRegistryEntryList.get(0).getPref();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}
		}
		
		
		return prefString;
	}

	@SuppressWarnings("unchecked")
	public boolean saveSharePreferences(String userid,String prefString) {
		
		Session session = null;
		Transaction t = null;
		boolean result = false;
		try {
			session = sessionFactory.openSession();
			NZSharePreferences userPref = new NZSharePreferences();
			List<NZSharePreferences> tmpRegistryEntryList = session.createCriteria(NZSharePreferences.class)
				.add(Restrictions.eq("myuserid", userid).ignoreCase()).list();
			 
			
			if (tmpRegistryEntryList != null && tmpRegistryEntryList.size() > 0)
			{
				userPref = tmpRegistryEntryList.get(0);
			}
			else
			{
				userPref.setMyuserid(userid);
			}
			
			userPref.setPref(prefString);
			t = session.beginTransaction();
			session.saveOrUpdate(userPref);
			t.commit();
			result = true;
			
		} catch (Exception e) {
			e.printStackTrace();
			if (t != null)
				t.rollback();
		} finally {
			if (session != null) {
				session.close();
			}
		}
		
		
		return result;
	}

	
}
