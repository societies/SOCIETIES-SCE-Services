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
package org.societies.collabtools.runtime;

import org.societies.collabtools.api.ICollabAppConnector;
import org.societies.collabtools.api.ICollabApps;

/**
 * Collaborative Applications Manager
 *
 * @author Chris Lima
 *
 */
public class CollabApps implements ICollabApps
{
	private ICollabAppConnector[] collabAppsconnectors;

	public CollabApps(ICollabAppConnector... connectors)
	{
		this.collabAppsconnectors =  connectors;
	}

	//member and applications available from this user
	@Override
	public void sendInvite(String member, String[] collabApps, String sessionName, String language)
	{
		for (String app : collabApps){
			for (ICollabAppConnector connector : collabAppsconnectors) {
				if (connector.getAppName().contains(app)){
					//TODO:Start invitation
					System.out.println("Send invitation to member: " + member + " using app " + connector.getAppName());
					connector.join(member, sessionName, language);
				}
				else {				
					throw new IllegalArgumentException(connector.getAppName()+" application not available");
				}
			}
		}

	}

	/* (non-Javadoc)
	 * @see org.societies.collabtools.api.ICollabApps#sendKick(java.lang.String, java.lang.String[], java.lang.String)
	 */
	@Override
	public void sendKick(String member, String[] collabApps, String sessionName) {
		for (String app : collabApps){
			for (ICollabAppConnector connector : collabAppsconnectors) {
				if (connector.getAppName().contains(app)){
					//TODO:Start invitation
					System.out.println("Kicking member: " + member);
					connector.kick(member, sessionName);
				}
				else {				
					throw new IllegalArgumentException(connector.getAppName()+" application not available");
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.societies.collabtools.api.ICollabApps#joinEvent(java.lang.String, java.lang.String[], java.lang.String)
	 */
	@Override
	public void joinEvent(String member, String[] collabApps, String sessionName) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.societies.collabtools.api.ICollabApps#leaveEvent(java.lang.String, java.lang.String[], java.lang.String)
	 */
	@Override
	public void leaveEvent(String member, String[] collabApps,
			String sessionName) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.societies.collabtools.api.ICollabApps#getCollabAppConnectors()
	 */
	@Override
	public ICollabAppConnector[] getCollabAppConnectors() {
		return collabAppsconnectors;
	}
}
