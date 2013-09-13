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

import org.societies.api.ext3p.schema.networking.Education;
import org.societies.api.ext3p.schema.networking.Employment;
import org.societies.api.ext3p.schema.networking.UserDetails;
import org.societies.api.ext3p.schema.networking.ShareInfo;
import org.societies.thirdpartyservices.networking.directory.model.NZEducation;
import org.societies.thirdpartyservices.networking.directory.model.NZEmployment;
import org.societies.thirdpartyservices.networking.directory.model.NZUserDetails;
import org.societies.thirdpartyservices.networking.directory.model.NZShareInfo;



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
		
		try {
			
			
			List<NZUserDetails> tmpRegistryEntryList = session.createCriteria(NZUserDetails.class)
				.add(Restrictions.eq("userid", userid).ignoreCase()).list();
			 
			
			if (tmpRegistryEntryList != null && tmpRegistryEntryList.size() > 0)
			{
				userRec.setUserid(tmpRegistryEntryList.get(0).getUserid());
				userRec.setCompany(tmpRegistryEntryList.get(0).getCompany());
				userRec.setDisplayName(tmpRegistryEntryList.get(0).getDisplayName());
				userRec.setDept(tmpRegistryEntryList.get(0).getDept());
				userRec.setPosition(tmpRegistryEntryList.get(0).getPosition());
				userRec.setFacebookID(tmpRegistryEntryList.get(0).getFacebookID());
				userRec.setLinkedInID(tmpRegistryEntryList.get(0).getLinkedInID());
				userRec.setTwitterID(tmpRegistryEntryList.get(0).getTwitterID());
				
				List<Education> eduList = new ArrayList<Education>();
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
		
		
		try {
			
			
			
			List<NZUserDetails> tmpRegistryEntryList = session.createCriteria(NZUserDetails.class)
				.add(Restrictions.eq("userid", userRec.getUserid()).ignoreCase()).list();
			 
			
			if (tmpRegistryEntryList != null && tmpRegistryEntryList.size() > 0)
			{
				session.delete(tmpRegistryEntryList.get(0));
			}
			
			NZUserDetails userDb = new NZUserDetails();
			
			userDb.setUserid(userRec.getUserid());
			userDb.setCompany(userRec.getCompany());
			userDb.setDisplayName(userRec.getDisplayName());
			userDb.setDept(userRec.getDept());
			userDb.setPosition(userRec.getPosition());
			userDb.setFacebookID(userRec.getFacebookID());
			userDb.setLinkedInID(userRec.getLinkedInID());
			userDb.setTwitterID(userRec.getTwitterID());
			
			session.save(userDb);
			
			Set<NZEducation> eduDbColl = userDb.education;
			Set<NZEmployment> emplDbColl = userDb.employment;
			
			for (Education edu : userRec.getEducationhistory())
			{
				NZEducation eduDb = new NZEducation();
				eduDb.setCourse(edu.getWhat());
				eduDb.setLevel(edu.getLevel());
				eduDb.setCollege(edu.getWhere());
				eduDb.setUserdetails(userDb);
				eduDbColl.add(eduDb);
				session.save(eduDb);
			}
			
			for (Employment empl : userRec.getEmploymenthistory())
			{
				NZEmployment empDb = new NZEmployment();
				empDb.setPosition(empl.getWhat());
				empDb.setEmployer(empl.getWhere());
				empDb.setUserdetails(userDb);
				emplDbColl.add(empDb);
				session.save(empDb);
			}
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
				.add(Restrictions.eq("userid", userid).ignoreCase())
				.add(Restrictions.eq("friendid", friendid).ignoreCase()).list();
			 
			
			if (tmpRegistryEntryList != null && tmpRegistryEntryList.size() > 0)
			{
				info.setShareHash(tmpRegistryEntryList.get(0).getShareHash());
			} else
			{
				// check for default
				session.clear();
				tmpRegistryEntryList = session.createCriteria(NZShareInfo.class)
						.add(Restrictions.eq("userid", userid).ignoreCase())
						.add(Restrictions.eq("friendid", "0").ignoreCase()).list();
				
				if (tmpRegistryEntryList != null && tmpRegistryEntryList.size() > 0)
				{
					info.setShareHash(tmpRegistryEntryList.get(0).getShareHash());
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
	public ShareInfo updateshareInfoUser(ShareInfo info) {
		
		Session session = sessionFactory.openSession();
		Transaction t = session.beginTransaction();
		
		
		try {
			
			
			
			List<NZShareInfo> tmpRegistryEntryList = session.createCriteria(NZShareInfo.class)
					.add(Restrictions.eq("userid", info.getUserid()).ignoreCase())
					.add(Restrictions.eq("friendid", info.getFriendid()).ignoreCase()).list();
			 
			
			if (tmpRegistryEntryList != null && tmpRegistryEntryList.size() > 0)
			{
				session.delete(tmpRegistryEntryList.get(0));
				t.commit();
				t = session.beginTransaction();
				session.clear();
				session.close();
				 session = sessionFactory.openSession();
			}
			
			
			NZShareInfo infoDB = new NZShareInfo();
			
			log.info("Networking Directory bundle info.getUserid() returns. : " + info.getUserid());
			
			infoDB.setUserid(info.getUserid());
			log.info("Networking Directory bundle infoDB.getUserid returns. : " + infoDB.getUserid());
			infoDB.setFriendid(info.getFriendid());
			infoDB.setShareHash(info.getShareHash());
			
			
			
			session.save(infoDB);
			
			
		
			t.commit();
			
		} catch (Exception e) {
			e.printStackTrace();
			t.rollback();
		} finally {
			if (session != null) {
				session.close();
			}
		}
		
		
		return info;
	}
	
	
	
}
