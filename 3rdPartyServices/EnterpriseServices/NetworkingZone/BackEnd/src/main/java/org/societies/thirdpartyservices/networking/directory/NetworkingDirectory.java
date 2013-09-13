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
 package org.societies.thirdpartyservices.networking.directory;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.societies.api.ext3p.networking.Education;
import org.societies.api.ext3p.networking.Employment;
import org.societies.api.ext3p.networking.UserDetails;
import org.societies.api.ext3p.networking.ShareInfo;
import org.societies.api.ext3p.networking.ZoneDetails;
import org.societies.thirdpartyservices.networking.directory.model.NZEducation;
import org.societies.thirdpartyservices.networking.directory.model.NZEmployment;
import org.societies.thirdpartyservices.networking.directory.model.NZNotes;
import org.societies.thirdpartyservices.networking.directory.model.NZUserDetails;
import org.societies.thirdpartyservices.networking.directory.model.NZShareInfo;
import org.societies.thirdpartyservices.networking.directory.model.NZZones;



public class NetworkingDirectory {
	private SessionFactory sessionFactory;
	private static Logger log = LoggerFactory.getLogger(NetworkingDirectory.class);

	
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


	public NetworkingDirectory() {
		log.info("Networking Directory bundle instantiated.");
	}

	
	
	
	
	@SuppressWarnings("unchecked")
	public UserDetails getUserRecord(String userid) {
		
		Session session = sessionFactory.openSession();
		UserDetails userRec = new UserDetails();
		
		log.info("Networking Directory getUserRecord called.");
		
		try {
			
			
			List<NZUserDetails> tmpRegistryEntryList = session.createCriteria(NZUserDetails.class)
				.add(Restrictions.eq("userid", userid).ignoreCase()).list();
			 
			
			if (tmpRegistryEntryList != null && tmpRegistryEntryList.size() > 0)
			{
				userRec.setUserid(tmpRegistryEntryList.get(0).getUserid());
				userRec.setCompany(tmpRegistryEntryList.get(0).getCompany());
				userRec.setDisplayName(tmpRegistryEntryList.get(0).getDisplayName());
				userRec.setEmail(tmpRegistryEntryList.get(0).getEmail());
				userRec.setHomelocation(tmpRegistryEntryList.get(0).getHomelocation());
				userRec.setSex(tmpRegistryEntryList.get(0).getSex());
				userRec.setDept(tmpRegistryEntryList.get(0).getDept());
				userRec.setPosition(tmpRegistryEntryList.get(0).getPosition());
				userRec.setFacebookID(tmpRegistryEntryList.get(0).getFacebookID());
				userRec.setLinkedInID(tmpRegistryEntryList.get(0).getLinkedInID());
				userRec.setTwitterID(tmpRegistryEntryList.get(0).getTwitterID());
				userRec.setAbout(tmpRegistryEntryList.get(0).getAbout());
				
				List<Education> eduList = new ArrayList<Education>();
				if (tmpRegistryEntryList.get(0).education != null)
				{
					Iterator<NZEducation> eduIt = tmpRegistryEntryList.get(0).education.iterator();
					while (eduIt.hasNext())
					{
						Education edu = new Education();
						NZEducation eduDb = eduIt.next();
						edu.setWhere(eduDb.getCollege());
						edu.setWhat(eduDb.getCourse());
						edu.setLevel(eduDb.getLevel());
						eduList.add(edu);
					}
					userRec.setEducationhistory(eduList);
				}
				
				if (tmpRegistryEntryList.get(0).employment != null)
				{
					List<Employment> empList = new ArrayList<Employment>();
					Iterator<NZEmployment> empIt = tmpRegistryEntryList.get(0).employment.iterator();
					while (empIt.hasNext())
					{
						Employment emp = new Employment();
						NZEmployment empDb = empIt.next();
						emp.setWhat(empDb.getPosition());
						emp.setWhere(empDb.getEmployer());
						empList.add(emp);
					}
					userRec.setEmploymenthistory(empList);
				}				
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
	
	@SuppressWarnings("unchecked")
	public UserDetails updateUserRecord(UserDetails userRec) {
		
		Session session = sessionFactory.openSession();
		Transaction t = session.beginTransaction();
		
		log.info("Networking Directory getUserRecord called.");
		
		
		try {
			
			NZUserDetails userDb = new NZUserDetails();
			
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
			
			if (userRec.getDept() != null)
				userDb.setDept(userRec.getDept());
			
			if (userRec.getPosition() != null)
				userDb.setPosition(userRec.getPosition());
			
			if (userRec.getFacebookID() != null)
				userDb.setFacebookID(userRec.getFacebookID());
			
			if (userRec.getLinkedInID() != null)
				userDb.setLinkedInID(userRec.getLinkedInID());
			
			if (userRec.getTwitterID() != null)
				userDb.setTwitterID(userRec.getTwitterID());
			
			if (userRec.getAbout() != null)
				userDb.setAbout(userRec.getAbout());
			
			Set<NZEducation> eduDbColl = userDb.education;
			Set<NZEmployment> emplDbColl = userDb.employment;
			
			if (userRec.getEducationhistory() != null)
			{
				for (Education edu : userRec.getEducationhistory())
				{
					NZEducation eduDb = new NZEducation();
					eduDb.setCourse(edu.getWhat());
					eduDb.setLevel(edu.getLevel());
					eduDb.setCollege(edu.getWhere());
					eduDb.setUserdetails(userDb);
					eduDbColl.add(eduDb);
				}
				userDb.employment = emplDbColl;
			}
			
			if (userRec.getEmploymenthistory() != null)
			{
				for (Employment empl : userRec.getEmploymenthistory())
				{
					NZEmployment empDb = new NZEmployment();
					empDb.setPosition(empl.getWhat());
					empDb.setEmployer(empl.getWhere());
					empDb.setUserdetails(userDb);
					emplDbColl.add(empDb);
				}
				userDb.education = eduDbColl;
			}
			
			session.saveOrUpdate(userDb);
			t.commit();
			
		} catch (Exception e) {
			e.printStackTrace();
			t.rollback();
		} finally {
			if (session != null) {
				session.close();
			}
		}
		
		
		return userRec;
	}
	

	
	@SuppressWarnings("unchecked")
	public ShareInfo getShareInfo(String userid, String friendid) {
		
		Session session = sessionFactory.openSession();
		ShareInfo info = new ShareInfo();
		
		// Default to everything just in case
		info.setUserid(userid);
		info.setFriendid(friendid);
		info.setShareHash(0);
		try {
			
			
			List<NZShareInfo> tmpRegistryEntryList = session.createCriteria(NZShareInfo.class)
				.add(Restrictions.eq("myuserid", userid).ignoreCase())
				.add(Restrictions.eq("friendid", friendid).ignoreCase()).list();
			 
			
			if (tmpRegistryEntryList != null && tmpRegistryEntryList.size() > 0)
			{
				info.setShareHash(tmpRegistryEntryList.get(0).getSharehash());
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
	public List<String> getNotes(String userid, String friendid) {
		
		Session session = sessionFactory.openSession();
		List<String> notes = new ArrayList<String>();
		String retrievednote = null;
		
		try {
			
			
			List<NZNotes> tmpRegistryEntryList = session.createCriteria(NZNotes.class)
				.add(Restrictions.eq("myuserid", userid).ignoreCase())
				.add(Restrictions.eq("friendid", friendid).ignoreCase()).list();
			 
			
			if (tmpRegistryEntryList != null && tmpRegistryEntryList.size() > 0)
			{
				for ( int i = 0; i < tmpRegistryEntryList.size(); i++)
				{
					retrievednote = new String(tmpRegistryEntryList.get(i).getNote());
					notes.add(retrievednote);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}
		}
		
		
		return notes;
	}
	
	@SuppressWarnings("unchecked")
	public List<String> addNote(String userid, String friendid, String note) {
		
		NZNotes newnote = new NZNotes();
		Session session = sessionFactory.openSession();
		Transaction t = session.beginTransaction();
		
		List<String> notes = new ArrayList<String>();
		String retrievednote = null;
		
		try {
			
			newnote.setMyuserid(userid);
			newnote.setFriendid(friendid);
			newnote.setNote(note);
			
			session.save(newnote);
			t.commit();
			
			List<NZNotes> tmpRegistryEntryList = session.createCriteria(NZNotes.class)
					.add(Restrictions.eq("myuserid", userid).ignoreCase())
					.add(Restrictions.eq("friendid", friendid).ignoreCase()).list();
				 
				
				if (tmpRegistryEntryList != null && tmpRegistryEntryList.size() > 0)
				{
					for ( int i = 0; i < tmpRegistryEntryList.size(); i++)
					{
						retrievednote = new String(tmpRegistryEntryList.get(i).getNote());
						notes.add(retrievednote);
					}
				}
				
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}
		}

		return notes;
	}
	
	
	@SuppressWarnings("unchecked")
	public ShareInfo updateshareInfoUser(ShareInfo info) {
		
		Session session =null;
		Transaction t = null;
		
		
		try {
			session = sessionFactory.openSession();
			t = session.beginTransaction();
			
			NZShareInfo infoDB = null;
			
			List<NZShareInfo> tmpRegistryEntryList = session.createCriteria(NZShareInfo.class)
					.add(Restrictions.eq("myuserid", info.getUserid()).ignoreCase())
					.add(Restrictions.eq("friendid", info.getFriendid()).ignoreCase()).list();
			 
			
			if (tmpRegistryEntryList != null && tmpRegistryEntryList.size() > 0)
			{
				infoDB = tmpRegistryEntryList.get(0);
				infoDB.setSharehash(info.getShareHash());
				session.update(infoDB);
			} else {
				infoDB = new NZShareInfo();
				infoDB.setMyuserid(info.getUserid());
				infoDB.setFriendid(info.getFriendid());
				infoDB.setSharehash(info.getShareHash());
				session.save(infoDB);
			}
			t.commit();
			
		} catch (Exception e) {
			e.printStackTrace();
			if (t != null)
				t.rollback();
		} finally {
			if (session != null) {
				session.close();
			}
		}
		
		
		return info;
	}

	
	
	@SuppressWarnings("unchecked")
	public String getUserName(String userid) {
		String result = null;
		Session session = sessionFactory.openSession();
		UserDetails userRec = new UserDetails();
		
		log.info("Networking Directory getUserName called.");
		
		try {
			List<NZUserDetails> tmpRegistryEntryList = session.createCriteria(NZUserDetails.class)
				.add(Restrictions.eq("userid", userid).ignoreCase()).list();
			 
			
			if (tmpRegistryEntryList != null && tmpRegistryEntryList.size() > 0)
			{
				result = new String(tmpRegistryEntryList.get(0).getDisplayName());
			}
			
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
	public List<UserDetails>  searchByID(List<String> memberIds) {
		//filter by id, search directory and return CISs that match the relevant cis id 
		// should only be one, but easier to return a list of one as that is
		// what all other searches will return a list
		Session session = sessionFactory.openSession();
		List<NZUserDetails> tmpuserList = new ArrayList<NZUserDetails>();
		List<UserDetails> returnList = new ArrayList<UserDetails>();
		UserDetails record = null;

		try {
			if (memberIds != null && (memberIds.size() > 0))
			{
				tmpuserList = session.createCriteria(NZUserDetails.class).
						add(Restrictions.in("userid",memberIds)).list();
			
				for (NZUserDetails entry : tmpuserList) {
					record = new UserDetails();
					record.setDisplayName(entry.getDisplayName());
					record.setUserid(entry.getUserid());
					returnList.add(record);

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return returnList;
	}
	
	@SuppressWarnings("unchecked")
	public List<ZoneDetails> getZoneDetails() {
		
		Session session = sessionFactory.openSession();
		ZoneDetails zoneRec = null;
		List<ZoneDetails> zoneList = new ArrayList<ZoneDetails>();
		
		log.info("Networking Directory getZoneDetails called.");
		
		try {
			
			
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
	
}
