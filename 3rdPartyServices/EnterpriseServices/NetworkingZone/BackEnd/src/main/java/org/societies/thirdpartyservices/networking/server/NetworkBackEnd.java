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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.ext3p.networking.ShareInfo;
import org.societies.api.ext3p.networking.UserDetails;
import org.societies.thirdpartyservices.networking.directory.NetworkingDirectory;



public class NetworkBackEnd {

	public NetworkingDirectory networkingDirectory;

	//TODO : Probably move to somehwere
	/*
	private String schmoozerUser = "schmoozer";
	private String locationZoneA = "ZoneA";
	private String locationZoneB = "ZoneB";
	private String locationZoneC = "ZoneC";
	private String locationZoneD = "ZoneD";
	*/

	 
	private static Logger log = LoggerFactory.getLogger(NetworkBackEnd.class);
	
	
	

	public NetworkBackEnd()	
	{
		log.info("NetworkBackEnd bundle instantiated.");

	};
	
	
	public void init_service()
	{
		log.info("NetworkBackEnd init_service called.");
		
	
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


	public String getMyMainCisId() {
		// TODO Auto-generated method stub
		return null;
	}


	public List<String> getZoneCisIDs() {
		// TODO Auto-generated method stub
		return null;
	}


	public UserDetails getMyDetails(String myuserid) {
		return getNetworkingDirectory().getUserRecord(myuserid);
	}


	public UserDetails updateMyDetails(UserDetails myDetails) {
		return getNetworkingDirectory().updateUserRecord(myDetails);
	} 
			

	
}

