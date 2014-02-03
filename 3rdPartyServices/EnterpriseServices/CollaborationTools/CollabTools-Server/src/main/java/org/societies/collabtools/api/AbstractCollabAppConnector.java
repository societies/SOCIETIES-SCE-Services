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
package org.societies.collabtools.api;

import java.util.Observable;

/**
 * Interface to integrate SYNCHRONOUS collaborative applications. 1..1
 *
 * @author Chris Lima
 *
 */
public abstract class AbstractCollabAppConnector extends Observable {
	
	/**
	 * Name of integrated application
	 */
	public abstract String getAppName();
	
	/**
	 * Name of integrated application host
	 */
	public abstract String getAppServerName();
	
	/**
	 * Set the name of integrated application
	 */
	public abstract void setAppName(String app_name);
	
	/**
	 * Set the host of integrated application
	 */
	public abstract void setAppServerName(String host);
	
	/**
	 * Setup the initial configuration for the Collaborative application
	 */
	public abstract void setup();
	
	/**
	 * @param user individual who wants to join the room
	 * @param room conference room to join
	 * @param language default language for discussion
	 */
	public abstract void join(String user, String room, String language, String msg);
	
	/**
	 * @param user individual who wants to leave the room
	 * @param room conference room 
	 */
	public abstract void kick(String user, String room);
	
	/**
	 * Trigger some event if a user joins the conference
	 * 
	 * @param room conference room 
	 * @param participant individual who wants to leave the room
	 */
	protected void joinEvent(String room, String participant) {
		setChanged();
		String[] response = {"joinEvent", room, getAppName(), participant};
		notifyObservers(response);
	}
	
	/**
	 * Trigger some event if a user leaves the conference
	 * 
	 * @param room conference room 
	 * @param participant individual who wants to leave the room
	 */
	protected void leaveEvent(String room, String participant) {
		setChanged();
		String[] response = {"leaveEvent", room, getAppName(), participant};
		notifyObservers(response);
	}

}
