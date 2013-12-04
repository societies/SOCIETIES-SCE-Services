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

import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.collabtools.api.AbstractCollabAppConnector;
import org.societies.collabtools.api.ICollabApps;

/**
 * Collaborative Applications Manager
 *
 * @author Chris Lima
 *
 */
public class CollabApps extends Observable implements ICollabApps, Observer
{
	private static final Logger logger  = LoggerFactory.getLogger(CollabApps.class);
	private AbstractCollabAppConnector[] collabAppsconnectors;

	public CollabApps(AbstractCollabAppConnector... connectors)
	{
		for (AbstractCollabAppConnector connector : connectors) {
			connector.addObserver(this);
		}
		this.collabAppsconnectors =  connectors;
	}

	//member and applications available from this user
	@Override
	public void sendInvite(String member, String[] collabApps, String sessionName, String language, String msg)
	{
		for (String app : collabApps){
			for (AbstractCollabAppConnector connector : collabAppsconnectors) {
				if (connector.getAppName().contains(app)){
					//TODO:Start invitation
					System.out.println("Send invitation to member: " + member + " using app " + connector.getAppName());
					connector.join(member, sessionName, language, msg);
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
			for (AbstractCollabAppConnector connector : collabAppsconnectors) {
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
	public void joinEvent(String participant, String collabApp, String room) {
		System.out.println("****Event: Participant "+ participant+" joined room "+ room+" with application "+ collabApp);
		logger.info("****Event: Participant {} joined room {} ", participant, room);
		setChanged();
		String[] response = {"joinEvent", room, participant};
		notifyObservers(response);
	}

	/* (non-Javadoc)
	 * @see org.societies.collabtools.api.ICollabApps#leaveEvent(java.lang.String, java.lang.String[], java.lang.String)
	 */
	@Override
	public void leaveEvent(String participant, String collabApp, String room) {
		System.out.println("****Event: Participant "+ participant+" left room "+ room+" with application "+ collabApp);
		logger.info("****Event: Participant {} left room {} ", participant, room);
		setChanged();
		String[] response = {"leaveEvent", room, participant};
		notifyObservers(response);
	}

	/* (non-Javadoc)
	 * @see org.societies.collabtools.api.ICollabApps#getCollabAppConnectors()
	 */
	@Override
	public AbstractCollabAppConnector[] getCollabAppConnectors() {
		return collabAppsconnectors;
	}

	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof String[]){
			String[] response = (String[]) arg;
			String event = response[0]; 
			String room = response[1]; 
			String collabApp = response[2];
			String participant = response[3]; 
			if (event.equals("joinEvent")){
				this.joinEvent(participant, collabApp, room);
			}
			else if (event.equals("leaveEvent")) {
				this.leaveEvent(participant, collabApp, room);
			}
		}
	}
}
