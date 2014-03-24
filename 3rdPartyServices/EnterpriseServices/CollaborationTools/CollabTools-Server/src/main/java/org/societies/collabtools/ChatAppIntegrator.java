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
package org.societies.collabtools;

import java.util.Collection;
import java.util.Iterator;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.packet.MUCUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.collabtools.api.AbstractCollabAppConnector;


/**
 * Connector for Chat application, in this case Openfire XMPP
 *
 * @author Chris Lima
 *
 */
public class ChatAppIntegrator extends AbstractCollabAppConnector {

	private static final Logger logger  = LoggerFactory.getLogger(ChatAppIntegrator.class);
	private String app_name;
	private String host;
	private XMPPConnection connection;
	private MultiUserChat muc;

	/**
	 * 
	 */
	public ChatAppIntegrator(final String appName, final String host) {
		this.app_name = appName;
		this.host = host;
		setup();
	}

	/**
	 * 
	 */
	public ChatAppIntegrator() {
		//Default server
		this.host = "societies.local";
		setup();
	}

	/* (non-Javadoc)
	 * @see org.societies.collabtools.api.ICollabAppIntegrator#setup()
	 */
	@Override
	public void setup() {
		logger.info("Openfire setup with host: {}",this.host);
		ConnectionConfiguration config = new ConnectionConfiguration(this.host, 5222);
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
				e1.printStackTrace();
			}
			throw new IllegalArgumentException("Verify host name and if you can login anonymously in Openfire Server");
			//			e.printStackTrace();
		}
		this.connection = connection;

		//filter to check if user joins or leaves the conference
		connection.addPacketListener(new PacketListener() { 
			public void processPacket(Packet packet) {
				Presence presence = (Presence) packet;
				MUCUser mucUser = getMUCUserExtension(presence);
				String room;
				String participant;
				if (mucUser.getItem().getAffiliation().equals("none")) {
					if (presence.getType() == Presence.Type.available) {
						room = presence.getFrom().split("/")[0];
						participant = presence.getFrom().split("/")[1];
						joinEvent(room.split("@")[0], participant.split("@")[0]);
					}
					if (presence.getType() == Presence.Type.unavailable) {
						room = presence.getFrom().split("/")[0];
						participant = presence.getFrom().split("/")[1];
						leaveEvent(room.split("@")[0], participant.split("@")[0]);
					}
				}
			}

			/**
			 * Returns the MUCUser packet extension included in the packet or <tt>null</tt> if none.
			 *
			 * @param packet the packet that may include the MUCUser extension.
			 * @return the MUCUser found in the packet.
			 */
			private MUCUser getMUCUserExtension(Packet packet) {
				if (null != packet) {
					// Get the MUC User extension
					return (MUCUser) packet.getExtension("x", "http://jabber.org/protocol/muc#user");
				}
				return null;
			}
		}, null);
	}

	/* (non-Javadoc)
	 * @see org.societies.collabtools.api.ICollabAppIntegrator#join(java.lang.String)
	 */
	@Override
	public void join(final String user, String room, final String language, String msg) {
		room = room.replaceAll("\\s+","");
		muc = new MultiUserChat(this.connection, room+"@conference."+this.host);
		logger.debug("room: {}",room);
		Collection<HostedRoom> rooms = null;
		try {
			rooms = MultiUserChat.getHostedRooms(this.connection, "conference."+this.host);
		} catch (XMPPException e) {
			e.printStackTrace();
		}
		//Just to avoid null for safety
		if (null != rooms) {
			Iterator<HostedRoom> roomsIterator = rooms.iterator();
			boolean roomAlreadyExist = false;
			while(roomsIterator.hasNext()){
				String roomName = roomsIterator.next().getName();
				logger.debug("room exist: {}",roomName);
				if (roomName.equalsIgnoreCase(room)){
					//Room already created
					roomAlreadyExist = true;
					break;
				}
			}
			if (!roomAlreadyExist){
				try {
					//				setListeners(muc);
					muc.create(room);
					muc.sendConfigurationForm(new Form(Form.TYPE_SUBMIT));
				} catch (XMPPException e) {
					e.printStackTrace();
				}
			}
			logger.debug(user+"@"+this.host+ " - Interests "+msg+" matches in "+room);


			//TODO: Change message to inform which context information trigger the event
			logger.debug("Language for chat: {}",language);
			if (language.equalsIgnoreCase("German")) {
				muc.invite(user+"@"+host, "SOCIETIES lädt Sie zu "+room+" betreten");
			}
			else if (language.equalsIgnoreCase("French")){
				muc.invite(user+"@"+host, "SOCIETIES vous invite à rejoindre "+room+" chambre");
			}
			else {
				muc.invite(user+"@"+host, " - Interests "+msg+" matches in "+room);

			}
		}
	}

	/* (non-Javadoc)
	 * @see org.societies.collabtools.api.ICollabAppIntegrator#kick(java.lang.String)
	 */
	@Override
	public void kick(String user, String room) {
		room = room.replaceAll("\\s+","");
		muc = new MultiUserChat(this.connection, room+"@conference."+this.host);
		//TODO: Change message to inform which context information trigger the event 
		try {
			// TODO Insert context reason!E.g. location changed
			muc.kickParticipant(user, "Context change");
			muc.kickParticipant(user+"@"+this.host, "Context change");
			//Sending kick event to be triggered by leaveEvent
			Presence leavePresence = new Presence(Presence.Type.unavailable);
			leavePresence.setTo(room + "/" + user);
			connection.sendPacket(leavePresence);
		} catch (XMPPException e) {
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.societies.collabtools.api.ICollabAppConnector#getAppName()
	 */
	@Override
	public String getAppName() {
		return this.app_name;
	}

	/* (non-Javadoc)
	 * @see org.societies.collabtools.api.ICollabAppConnector#getAppServerName()
	 */
	@Override
	public String getAppServerName() {
		return this.host;
	}

	/* (non-Javadoc)
	 * @see org.societies.collabtools.api.ICollabAppConnector#setAppName()
	 */
	@Override
	public void setAppName(String app_name) {
		this.app_name = app_name;
	}

	/* (non-Javadoc)
	 * @see org.societies.collabtools.api.ICollabAppConnector#setAppServerName()
	 */
	@Override
	public void setAppServerName(String host) {
		this.host = host;
	}

}
