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
 package org.societies.thirdpartyservices.networking;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.societies.thirdpartyservices.api.internal.networking.INetworkingDirectory;
import org.societies.thirdpartyservices.networking.model.NetworkingRecordEntry;
import org.societies.thirdpartyservices.networking.model.UserRecordEntry;
import org.societies.thirdpartyservices.schema.networking.NetworkingRecord;
import org.societies.thirdpartyservices.schema.networking.UserRecord;
import org.societies.thirdpartyservices.schema.networking.UserRecordResult;
import org.societies.thirdpartyservices.schema.networking.UserResult;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

public class NetworkingDirectory implements INetworkingDirectory {
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

	
	
	
	
	

	/* (non-Javadoc)
	 * @see org.societies.thirdpartyservices.api.internal.networking.INetworkingDirectory#getUserRecord(int)
	 */
	@Override
	public Future<UserRecordResult> getUserRecord(int id) {
		
		Session session = sessionFactory.openSession();
		UserRecordResult result = new UserRecordResult();
		UserRecord userRec = new UserRecord();
		//List<UserRecordEntry> returnedUserList = new ArrayList<UserRecordEntry>();
		
		try {
			
			UserRecordEntry tmpUserEntry = 	(UserRecordEntry) session.load(UserRecordEntry.class,
					id);
			
			userRec.setCompany(tmpUserEntry.getCompany());
			userRec.setDisplayName(tmpUserEntry.getDisplayName());
			userRec.setId(tmpUserEntry.getId());
			userRec.setLogin(tmpUserEntry.getLogin());
			
			result.setUserRec(userRec);
			result.setResult(UserResult.USER_OK);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			result.setResult(UserResult.USER_NOT_FOUND);

		} finally {
			if (session != null) {
				session.close();
			}
		}
		
		
		return new AsyncResult<UserRecordResult>(result);
	}


	/* (non-Javadoc)
	 * @see org.societies.thirdpartyservices.api.internal.networking.INetworkingDirectory#getNetworkingRecord(int, int)
	 */
	@Override
	public Future<NetworkingRecord> getNetworkingRecord(int id, int friendid) {
		Session session = sessionFactory.openSession();
		
		NetworkingRecord netRec = new NetworkingRecord();
		//List<UserRecordEntry> returnedUserList = new ArrayList<UserRecordEntry>();
		
		try {
			
			NetworkingRecordEntry tmpNetEntry = 	(NetworkingRecordEntry) session.load(NetworkingRecordEntry.class,
					id);
			
			netRec.setId(tmpNetEntry.getId());
			netRec.setFriendid(tmpNetEntry.getFriendid());
			netRec.setShareHash(tmpNetEntry.getShareHash());
			
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if (session != null) {
				session.close();
			}
		}
		
		
		return new AsyncResult<NetworkingRecord>(netRec);
	}


	/* (non-Javadoc)
	 * @see org.societies.thirdpartyservices.api.internal.networking.INetworkingDirectory#updateUserRecord(org.societies.thirdpartyservices.api.internal.networking.UserRecord)
	 */
	@Override
	public Future<UserRecordResult> updateUserRecord(UserRecord userRec) {
		UserRecordResult result = new UserRecordResult();

		Session session = sessionFactory.openSession();
		UserRecordEntry userRecordEntry = new UserRecordEntry();

		Transaction t = session.beginTransaction();
		try {

			//Delete old record
			Object obj = session.load(UserRecordEntry.class,
					userRec.getId());
			session.delete(obj);

			//Add old record
			userRecordEntry.setCompany(userRec.getCompany());
			userRecordEntry.setDisplayName(userRec.getDisplayName());
			userRecordEntry.setId(userRec.getId());
			userRecordEntry.setLogin(userRec.getLogin());
			
			session.save(userRecordEntry);

			t.commit();
			log.debug("User Details saved.");
			
			result.setUserRec(userRec);
			result.setResult(UserResult.USER_OK);
			
		} catch (Exception e) {
			t.rollback();
			e.printStackTrace();
			result.setResult(UserResult.USER_NOT_FOUND);
			
		} finally {
			if (session != null) {
				session.close();
			}
		}

		return new AsyncResult<UserRecordResult>(result);
	}


	/* (non-Javadoc)
	 * @see org.societies.thirdpartyservices.api.internal.networking.INetworkingDirectory#updateNetworkingRecord(org.societies.thirdpartyservices.api.internal.networking.NetworkingRecord)
	 */
	@Override
	public Future<NetworkingRecord> updateNetworkingRecord(
			NetworkingRecord netRec) {
		
		Session session = sessionFactory.openSession();
		NetworkingRecordEntry netRecordEntry = new NetworkingRecordEntry();

		Transaction t = session.beginTransaction();
		try {

			//Delete old record
			Object obj = session.load(NetworkingRecordEntry.class,
					netRec.getId());
			session.delete(obj);

			//Add old record
			netRecordEntry.setId(netRec.getId());
			netRecordEntry.setFriendid(netRec.getFriendid());
			netRecordEntry.setShareHash(netRec.getShareHash());
			
			session.save(netRecordEntry);

			t.commit();
			log.debug("User Networking Details saved.");
			
		} catch (Exception e) {
			t.rollback();
			e.printStackTrace();
			
		} finally {
			if (session != null) {
				session.close();
			}
		}

		return new AsyncResult<NetworkingRecord>(netRec);
	}

	
}
