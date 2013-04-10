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
package org.societies.enterprise.collabtools;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.muc.DefaultParticipantStatusListener;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.societies.enterprise.collabtools.api.ICollabAppConnector;


/**
 * Connector for Chat application, in this case Openfire XMPP
 *
 * @author Christopher Viana Lima
 *
 */
public class ChatAppIntegrator implements ICollabAppConnector {

	private static String APP_NAME;
	private static String HOST;
	private XMPPConnection connection;
	private MultiUserChat muc;

	/**
	 * 
	 */
	public ChatAppIntegrator(final String appName, final String host) {
		ChatAppIntegrator.APP_NAME = appName;
		ChatAppIntegrator.HOST = host;
		setup();
	}

	/**
	 * 
	 */
	public ChatAppIntegrator() {
		//Default server
		ChatAppIntegrator.HOST = "societies.local";
		setup();
	}

	/* (non-Javadoc)
	 * @see org.societies.enterprise.collabtools.api.ICollabAppIntegrator#setup()
	 */
	@Override
	public void setup() {
		System.out.println("Openfire setup with host: "+ChatAppIntegrator.HOST);
		ConnectionConfiguration config = new ConnectionConfiguration(ChatAppIntegrator.HOST, 5222);
		config.setDebuggerEnabled(false);
		XMPPConnection connection = new XMPPConnection(config);
		try {
			connection.connect();
			connection.loginAnonymously();
		} catch (XMPPException e) {
			//try as admin
			try {
				connection.login("admin", "admin");
			} catch (XMPPException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new IllegalArgumentException("Verify host name and if you can login anonymously in Openfire Server");
			//			e.printStackTrace();
		}
		this.connection = connection;
	}

	/* (non-Javadoc)
	 * @see org.societies.enterprise.collabtools.api.ICollabAppIntegrator#join(java.lang.String)
	 */
	@Override
	public void join(String user, String room) {
		muc = new MultiUserChat(this.connection, room+"@conference."+HOST);
		System.out.println("room: "+room);
		Collection<HostedRoom> rooms = null;
		try {
			rooms = MultiUserChat.getHostedRooms(this.connection, "conference."+HOST);
		} catch (XMPPException e) {
			e.printStackTrace();
		}
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
			try {
		        muc = new MultiUserChat(connection, room+"@conference."+HOST);
		        muc.addParticipantStatusListener(new DefaultParticipantStatusListener() 
		        {       
		            @Override
		            public void joined(String participant)
		            {
		            	joined(participant);
		            }
		        
		            public void kicked(String participant, String actor, String reason)
		            {
		            	leaveEvent(participant);
		            }
		        
		            public void left(String participant)
		            {
		            	leaveEvent(participant);
		            }
		        });
				muc.create(room);
				//Empty form to create instant room 
				muc.sendConfigurationForm(new Form(Form.TYPE_SUBMIT));
			} catch (XMPPException e) {
				e.printStackTrace();
			}
		}
		//TODO: Change message to inform which context information trigger the event 
        muc.invite(user+"@"+HOST, "CollabTools is inviting you to join "+room+" room");
	}

	/* (non-Javadoc)
	 * @see org.societies.enterprise.collabtools.api.ICollabAppIntegrator#kick(java.lang.String)
	 */
	@Override
	public void kick(String user, String room) {
		muc = new MultiUserChat(this.connection, room+"@conference."+HOST);
		//TODO: Change message to inform which context information trigger the event 
		try {
			// TODO Insert context reason!E.g. location changed
			muc.kickParticipant(user, "Context change");
			muc.kickParticipant(user+"@"+HOST, "Context change");
		} catch (XMPPException e) {
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.societies.enterprise.collabtools.api.ICollabAppConnector#getAppName()
	 */
	@Override
	public String getAppName() {
		return APP_NAME;
	}

	/* (non-Javadoc)
	 * @see org.societies.enterprise.collabtools.api.ICollabAppConnector#getAppServerName()
	 */
	@Override
	public String getAppServerName() {
		return HOST;
	}

	/* (non-Javadoc)
	 * @see org.societies.enterprise.collabtools.api.ICollabAppConnector#joinEvent()
	 */
	@Override
	public void joinEvent(String participant) {
		// TODO Auto-generated method stub
		System.out.println("Participant joined: "+ participant);
		
	}

	/* (non-Javadoc)
	 * @see org.societies.enterprise.collabtools.api.ICollabAppConnector#leaveEvent()
	 */
	@Override
	public void leaveEvent(String participant) {
		// TODO Auto-generated method stub
		System.out.println("Participant left: "+ participant);
	}

}
