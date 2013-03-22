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
package org.societies.enterprise.collabtools.runtime;

import java.util.Collection;
import java.util.Iterator;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.societies.enterprise.collabtools.api.ICollabAppIntegrator;


/**
 * Integrator for Chat application, in this case Openfire XMPP
 *
 * @author Christopher Viana Lima
 *
 */
public class ChatAppIntegrator implements ICollabAppIntegrator {

	private static String HOST;
	private XMPPConnection connection;

	/**
	 * 
	 */
	public ChatAppIntegrator(String host) {
		ChatAppIntegrator.HOST = host;
		setup();
	}

	/**
	 * 
	 */
	public ChatAppIntegrator() {
		ChatAppIntegrator.HOST = "societies.local";
		setup();
	}

	/* (non-Javadoc)
	 * @see org.societies.enterprise.collabtools.api.ICollabAppIntegrator#setup()
	 */
	@Override
	public void setup() {
		System.out.println("Openfire setup with host: "+HOST);
		ConnectionConfiguration config = new ConnectionConfiguration(ChatAppIntegrator.HOST, 5222);
		config.setDebuggerEnabled(false);
		XMPPConnection connection = new XMPPConnection(config);
		try {
			connection.connect();
			connection.login("admin", "admin");
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			throw new IllegalArgumentException("Verify host name and if user admin was created in Openfire Server");
			//			e.printStackTrace();
		}
		this.connection = connection;

	}

	/* (non-Javadoc)
	 * @see org.societies.enterprise.collabtools.api.ICollabAppIntegrator#join(java.lang.String)
	 */
	@Override
	public void join(String user, String room) throws XMPPException {
		MultiUserChat muc = new MultiUserChat(this.connection, room+"@conference."+HOST);
		System.out.println("room: "+room);
		Collection<HostedRoom> rooms = MultiUserChat.getHostedRooms(this.connection, "conference."+HOST);
		Iterator<HostedRoom> it = rooms.iterator();
		boolean roomAlreadyExist = false;
		while(it.hasNext()){
			String roomName = it.next().getName();
			System.out.println("room exist: "+roomName);
			if (roomName.equalsIgnoreCase(room)){
				//Room already created
				roomAlreadyExist = true;
				break;
			}
		}
		if (!roomAlreadyExist){
			muc.create(room);
			//Empty form to create instant room 
			muc.sendConfigurationForm(new Form(Form.TYPE_SUBMIT));
		}
		//TODO: Change message to inform which context information trigger the event 
        muc.invite(user+"@"+HOST, "CollabTools is inviting you to join "+room+" room");
	}

	/* (non-Javadoc)
	 * @see org.societies.enterprise.collabtools.api.ICollabAppIntegrator#kick(java.lang.String)
	 */
	@Override
	public void kick(String user, String room) throws XMPPException {
		MultiUserChat muc = new MultiUserChat(this.connection, room+"@conference."+HOST);
		//TODO: Change message to inform which context information trigger the event 
		muc.kickParticipant(user, "Context change");
		muc.kickParticipant(user+"@"+HOST, "Context change");
	}

}
