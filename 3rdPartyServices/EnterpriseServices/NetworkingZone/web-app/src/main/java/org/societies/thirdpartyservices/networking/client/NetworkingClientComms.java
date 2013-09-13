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
package org.societies.thirdpartyservices.networking.client;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.comm.xmpp.interfaces.ICommManager;


import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;

import org.societies.api.ext3p.networking.MemberDetails;
import org.societies.api.ext3p.networking.NetworkingBean;
import org.societies.api.ext3p.networking.Method;
import org.societies.api.ext3p.networking.NetworkingBeanResult;
import org.societies.api.ext3p.networking.ShareInfo;
import org.societies.api.ext3p.networking.UserDetails;
import org.societies.api.ext3p.networking.ZoneDetails;
import org.societies.api.ext3p.networking.ZoneEvent;
/**
 * Comms Client that initiates the remote communication for the networking zone
 * 
 * @author Maria Mannion
 * 
 */
public class NetworkingClientComms implements ICommCallback {
	private static final List<String> NAMESPACES = Collections
			.unmodifiableList(Arrays
					.asList("http://societies.org/api/ext3p/networking"));
	private static final List<String> PACKAGES = Collections
			.unmodifiableList(Arrays
					.asList("org.societies.api.ext3p.networking"));

	//TODO : Temporary while testing
	private int TEST_TIME_MULTIPLER = 10;
	private int WAIT_TIME_SECS = 100;
	
	// PRIVATE VARIABLES
	private ICommManager commManager;
	private static Logger LOG = LoggerFactory.getLogger(NetworkingClientComms.class);

	
	private NetworkingBeanResult commsResult;
	
	private String netServerID;
	
	// PROPERTIES
	public ICommManager getCommManager() {
		return commManager;
	}

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}

	public NetworkingClientComms() {
		netServerID = new String("networking.societies.local");
	}
	
	public NetworkingClientComms(String networkingserver) {
		this.netServerID = networkingserver;
	}

	public void InitService() {
		// Registry Netowkring Client with the Comms Manager

		if (LOG.isDebugEnabled())
			LOG.debug("Registering the Networking Client with the XMPP Communication Manager");

		try {
			getCommManager().register(this);
		} catch (CommunicationException e) {
			e.printStackTrace();
		}
	
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.api.comm.xmpp.interfaces.ICommCallback#getJavaPackages()
	 */
	@Override
	public List<String> getJavaPackages() {
		return PACKAGES;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.api.comm.xmpp.interfaces.ICommCallback#getXMLNamespaces()
	 */
	@Override
	public List<String> getXMLNamespaces() {
		return NAMESPACES;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveError(org
	 * .societies.api.comm.xmpp.datatypes.Stanza,
	 * org.societies.api.comm.xmpp.exceptions.XMPPError)
	 */
	@Override
	public void receiveError(Stanza arg0, XMPPError arg1) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveInfo(org.
	 * societies.api.comm.xmpp.datatypes.Stanza, java.lang.String,
	 * org.societies.api.comm.xmpp.datatypes.XMPPInfo)
	 */
	@Override
	public void receiveInfo(Stanza arg0, String arg1, XMPPInfo arg2) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveItems(org
	 * .societies.api.comm.xmpp.datatypes.Stanza, java.lang.String,
	 * java.util.List)
	 */
	@Override
	public void receiveItems(Stanza arg0, String arg1, List<String> arg2) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveMessage(org
	 * .societies.api.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public void receiveMessage(Stanza arg0, Object arg1) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveResult(org
	 * .societies.api.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public void receiveResult(Stanza arg0, Object arg1) {
		// TODO Auto-generated method stub
		if (arg1 instanceof NetworkingBeanResult){
			commsResult = (NetworkingBeanResult) arg1;	
		}
	}

	
	
	public String getServerCisID() {
		// We want to sent all messages for Netowrking Client to the metworking server
		// hardcode for now TODO : Read from properties
		IIdentity toIdentity = null;
		commsResult = null;
		try {
			toIdentity = getCommManager().getIdManager().fromJid(netServerID);
		} catch (InvalidFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Stanza stanza = new Stanza(toIdentity);

		// CREATE MESSAGE BEAN
		NetworkingBean netBean = new NetworkingBean();
		netBean.setMethod(Method.WHOAREYOU);

		try {
			getCommManager().sendIQGet(stanza, netBean, this);
			
		} catch (CommunicationException e) {
			LOG.warn(e.getMessage());
		}
		;

		// TYuck, another TODO this properly, for now, wait up to 5 secs
		int i= 0;
		while (commsResult == null && (i < (5 * TEST_TIME_MULTIPLER))){
			try {
				i++;
				Thread.sleep(WAIT_TIME_SECS);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (commsResult != null)
		{
			return this.commsResult.getNetworkingCis();
		}
		
		return null;
	}

	public List<String> getZoneCisID() {
		// We want to sent all messages for Netowrking Client to the metworking server
		// hardcode for now TODO : Read from properties
		IIdentity toIdentity = null;
		commsResult = null;
		try {
			toIdentity = getCommManager().getIdManager().fromJid(netServerID);
		} catch (InvalidFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Stanza stanza = new Stanza(toIdentity);

		// CREATE MESSAGE BEAN
		NetworkingBean netBean = new NetworkingBean();
		netBean.setMethod(Method.GET_ZONE_LIST);

		try {
			getCommManager().sendIQGet(stanza, netBean, this);
				
		} catch (CommunicationException e) {
			LOG.warn(e.getMessage());
		};

				// TYuck, another TODO this properly, for now, wait up to 5 secs
				int i= 0;
				while (commsResult == null && (i < (5 * TEST_TIME_MULTIPLER))){
					try {
						i++;
						Thread.sleep(WAIT_TIME_SECS);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
		if (commsResult != null)
			return this.commsResult.getZones();
		
		return null;
	}

	public UserDetails getFriendDetails(String friendid)
	{
		// We want to sent all messages for Netowrking Client to the metworking server
		// hardcode for now TODO : Read from properties
		IIdentity toIdentity = null;
		commsResult = null;
		try {
			toIdentity = getCommManager().getIdManager().fromJid(netServerID);
		} catch (InvalidFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Stanza stanza = new Stanza(toIdentity);
		
		// CREATE MESSAGE BEAN
		NetworkingBean netBean = new NetworkingBean();
		netBean.setMethod(Method.GETUSERDETAILS);
		netBean.setMyuserid(getCommManager().getIdManager().getThisNetworkNode().getBareJid());
		netBean.setFrienduserid(friendid);
		try {
			getCommManager().sendIQGet(stanza, netBean, this);
				
		} catch (CommunicationException e) {
			LOG.warn(e.getMessage());
		};

				// TYuck, another TODO this properly, for now, wait up to 5 secs
				int i= 0;
				while (commsResult == null && (i < (5 * TEST_TIME_MULTIPLER))){
					try {
						i++;
						Thread.sleep(WAIT_TIME_SECS);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
		if (commsResult != null)
			return this.commsResult.getUserDetails();
		
		return null;
	}
	
	public UserDetails getMyDetails()
	{
		// We want to sent all messages for Netowrking Client to the metworking server
		// hardcode for now TODO : Read from properties
		IIdentity toIdentity = null;
		commsResult = null;
		try {
			toIdentity = getCommManager().getIdManager().fromJid(netServerID);
		} catch (InvalidFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Stanza stanza = new Stanza(toIdentity);
		
		// CREATE MESSAGE BEAN
		NetworkingBean netBean = new NetworkingBean();
		netBean.setMethod(Method.GETMYDETAILS);
		netBean.setMyuserid(getCommManager().getIdManager().getThisNetworkNode().getBareJid());
		
		try {
			getCommManager().sendIQGet(stanza, netBean, this);
				
		} catch (CommunicationException e) {
			LOG.warn(e.getMessage());
		};

				// TYuck, another TODO this properly, for now, wait up to 5 secs
				int i= 0;
				while (commsResult == null && (i < (5 * TEST_TIME_MULTIPLER))){
					try {
						i++;
						Thread.sleep(WAIT_TIME_SECS);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
		if (commsResult != null)
			return this.commsResult.getUserDetails();
		
		return null;
	}
	
	public UserDetails updateMyDetails(UserDetails newDetails)
	{
		// We want to sent all messages for Netowrking Client to the metworking server
		// hardcode for now TODO : Read from properties
		IIdentity toIdentity = null;
		commsResult = null;
		try {
			toIdentity = getCommManager().getIdManager().fromJid(netServerID);
		} catch (InvalidFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Stanza stanza = new Stanza(toIdentity);
		
		// CREATE MESSAGE BEAN
		NetworkingBean netBean = new NetworkingBean();
		netBean.setMethod(Method.UPDATEMYDETAILS);
		netBean.setMyuserid(getCommManager().getIdManager().getThisNetworkNode().getBareJid());
		
		LOG.info("updateMyDetails : newDetails.getDisplayName() " +newDetails.getDisplayName());
		newDetails.setUserid(netBean.getMyuserid());
		netBean.setMyDetails(newDetails);
		
		try {
			getCommManager().sendIQGet(stanza, netBean, this);
				
		} catch (CommunicationException e) {
			LOG.warn(e.getMessage());
		};

				// TYuck, another TODO this properly, for now, wait up to 5 secs
				int i= 0;
				while (commsResult == null && (i < (5 * TEST_TIME_MULTIPLER))){
					try {
						i++;
						Thread.sleep(WAIT_TIME_SECS);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
		if (commsResult != null)
			return this.commsResult.getUserDetails();
		
		return null;
	}
	
	
	public ShareInfo getFriendShareInfo(String friendid)
	{
		// We want to sent all messages for Netowrking Client to the metworking server
		// hardcode for now TODO : Read from properties
		IIdentity toIdentity = null;
		commsResult = null;
		try {
			toIdentity = getCommManager().getIdManager().fromJid(netServerID);
		} catch (InvalidFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Stanza stanza = new Stanza(toIdentity);
		
		// CREATE MESSAGE BEAN
		NetworkingBean netBean = new NetworkingBean();
		netBean.setMethod(Method.GETFRIENDSHAREINFO);
		netBean.setFrienduserid(friendid);
		netBean.setMyuserid(getCommManager().getIdManager().getThisNetworkNode().getBareJid());
		
		try {
			getCommManager().sendIQGet(stanza, netBean, this);
				
		} catch (CommunicationException e) {
			LOG.warn(e.getMessage());
		};

				// TYuck, another TODO this properly, for now, wait up to 5 secs
				int i= 0;
				while (commsResult == null && (i < (5 * TEST_TIME_MULTIPLER))){
					try {
						i++;
						Thread.sleep(WAIT_TIME_SECS);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
		if (commsResult != null)
			return this.commsResult.getSharedInfo();
		
		return null;
	}
	
	public ShareInfo getShareInfo(String friendid)
	{
		// We want to sent all messages for Netowrking Client to the metworking server
		// hardcode for now TODO : Read from properties
		IIdentity toIdentity = null;
		commsResult = null;
		try {
			toIdentity = getCommManager().getIdManager().fromJid(netServerID);
		} catch (InvalidFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Stanza stanza = new Stanza(toIdentity);
		
		// CREATE MESSAGE BEAN
		NetworkingBean netBean = new NetworkingBean();
		netBean.setMethod(Method.GETSHAREINFO);
		netBean.setFrienduserid(friendid);
		netBean.setMyuserid(getCommManager().getIdManager().getThisNetworkNode().getBareJid());
		
		try {
			getCommManager().sendIQGet(stanza, netBean, this);
				
		} catch (CommunicationException e) {
			LOG.warn(e.getMessage());
		};

				// TYuck, another TODO this properly, for now, wait up to 5 secs
				int i= 0;
				while (commsResult == null && (i < (5 * TEST_TIME_MULTIPLER))){
					try {
						i++;
						Thread.sleep(WAIT_TIME_SECS);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
		if (commsResult != null)
			return this.commsResult.getSharedInfo();
		
		return null;
	}
	
	public ShareInfo updateShareInfo(ShareInfo info)
	{
		// We want to sent all messages for Netowrking Client to the metworking server
		// hardcode for now TODO : Read from properties
		IIdentity toIdentity = null;
		commsResult = null;
		try {
			toIdentity = getCommManager().getIdManager().fromJid(netServerID);
		} catch (InvalidFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Stanza stanza = new Stanza(toIdentity);
		
		// CREATE MESSAGE BEAN
		NetworkingBean netBean = new NetworkingBean();
		netBean.setMethod(Method.UPDATESHAREINFO);
		netBean.setMyuserid(getCommManager().getIdManager().getThisNetworkNode().getBareJid());
		info.setUserid(netBean.getMyuserid());
			
		netBean.setSharedInfo(info);
		
		try {
			getCommManager().sendIQGet(stanza, netBean, this);
				
		} catch (CommunicationException e) {
			LOG.warn(e.getMessage());
		};

				// TYuck, another TODO this properly, for now, wait up to 5 secs
				int i= 0;
				while (commsResult == null && (i < (5 * TEST_TIME_MULTIPLER))){
					try {
						i++;
						Thread.sleep(WAIT_TIME_SECS);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
		if (commsResult != null)
			return this.commsResult.getSharedInfo();
		
		return null;
	}
	

	public List<ZoneEvent> getCisActivity(String cisID)
	{
		
		// We want to sent all messages for Netowrking Client to the metworking server
		// hardcode for now TODO : Read from properties
		IIdentity toIdentity = null;
		commsResult = null;
		try {
			toIdentity = getCommManager().getIdManager().fromJid(netServerID);
		} catch (InvalidFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Stanza stanza = new Stanza(toIdentity);
				
		// CREATE MESSAGE BEAN
		NetworkingBean netBean = new NetworkingBean();
		netBean.setMethod(Method.GETZONEEVENTS);
		netBean.setMyuserid(getCommManager().getIdManager().getThisNetworkNode().getBareJid());
		netBean.setMycurrentzone(cisID);
				
		try {
			getCommManager().sendIQGet(stanza, netBean, this);
						
		} catch (CommunicationException e) {
			LOG.warn(e.getMessage());
		};

		// TYuck, another TODO this properly, for now, wait up to 5 secs
		int i= 0;
		while (commsResult == null && (i < (5 * TEST_TIME_MULTIPLER))){
			try {
				i++;
				Thread.sleep(WAIT_TIME_SECS);
			} catch (InterruptedException e) {
			// 	TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
						
		
		if (commsResult != null)
			return this.commsResult.getZoneeventlist();
		
		return null;
		
	}
	
	public List<MemberDetails> getMemberDetails(List<String> memberIDs)
	{
		
		// We want to sent all messages for Netowrking Client to the metworking server
		// hardcode for now TODO : Read from properties
		IIdentity toIdentity = null;
		commsResult = null;
		try {
			toIdentity = getCommManager().getIdManager().fromJid(netServerID);
		} catch (InvalidFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Stanza stanza = new Stanza(toIdentity);
				
		// CREATE MESSAGE BEAN
		NetworkingBean netBean = new NetworkingBean();
		netBean.setMethod(Method.GETMEMBERDETAILS);
		netBean.setMyuserid(getCommManager().getIdManager().getThisNetworkNode().getBareJid());
		netBean.setUseridlist(memberIDs);
				
		try {
			getCommManager().sendIQGet(stanza, netBean, this);
						
		} catch (CommunicationException e) {
			LOG.warn(e.getMessage());
		};

		// TYuck, another TODO this properly, for now, wait up to 5 secs
		int i= 0;
		while (commsResult == null && (i < (5 * TEST_TIME_MULTIPLER))){
			try {
				i++;
				Thread.sleep(WAIT_TIME_SECS);
			} catch (InterruptedException e) {
			// 	TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
						
		
		if (commsResult != null)
			return this.commsResult.getMemberdetaillist();
		
		return null;
		
	}
	
	public List<UserDetails> getUserDetailsList(List<String> memberIDs)
	{
		
		// We want to sent all messages for Netowrking Client to the metworking server
		// hardcode for now TODO : Read from properties
		IIdentity toIdentity = null;
		commsResult = null;
		try {
			toIdentity = getCommManager().getIdManager().fromJid(netServerID);
		} catch (InvalidFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Stanza stanza = new Stanza(toIdentity);
				
		// CREATE MESSAGE BEAN
		NetworkingBean netBean = new NetworkingBean();
		netBean.setMethod(Method.GETUSERDETAILSLIST);
		netBean.setMyuserid(getCommManager().getIdManager().getThisNetworkNode().getBareJid());
		netBean.setUseridlist(memberIDs);
				
		try {
			getCommManager().sendIQGet(stanza, netBean, this);
						
		} catch (CommunicationException e) {
			LOG.warn(e.getMessage());
		};

		// TYuck, another TODO this properly, for now, wait up to 5 secs
		int i= 0;
		while (commsResult == null && (i < (5 * TEST_TIME_MULTIPLER))){
			try {
				i++;
				Thread.sleep(WAIT_TIME_SECS);
			} catch (InterruptedException e) {
			// 	TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
						
		
		if (commsResult != null)
			return this.commsResult.getUserDetailsList();
		
		return null;
		
	}
	

	public List<String> addnote(String friendid, String note) {
		// We want to sent all messages for Netowrking Client to the metworking server
		// hardcode for now TODO : Read from properties
		IIdentity toIdentity = null;
		commsResult = null;
		try {
			toIdentity = getCommManager().getIdManager().fromJid(netServerID);
		} catch (InvalidFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Stanza stanza = new Stanza(toIdentity);
						
		// CREATE MESSAGE BEAN
		NetworkingBean netBean = new NetworkingBean();
		netBean.setMethod(Method.ADDNOTE);
		netBean.setMyuserid(getCommManager().getIdManager().getThisNetworkNode().getBareJid());
		netBean.setFrienduserid(friendid);
		netBean.setNote(note);
						
		try {
			getCommManager().sendIQGet(stanza, netBean, this);
							
		} catch (CommunicationException e) {
			LOG.warn(e.getMessage());
		};

		// TYuck, another TODO this properly, for now, wait up to 5 secs
		int i= 0;
		while (commsResult == null && (i < (5 * TEST_TIME_MULTIPLER))){
			try {
				i++;
				Thread.sleep(WAIT_TIME_SECS);
			} catch (InterruptedException e) {
			// 	TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
								
				
		if (commsResult != null)
			return this.commsResult.getNotes();
				
		return null;
	}

	public List<String> getnotes(String friendid) {
		// We want to sent all messages for Netowrking Client to the metworking server
				// hardcode for now TODO : Read from properties
				IIdentity toIdentity = null;
				commsResult = null;
				try {
					toIdentity = getCommManager().getIdManager().fromJid(netServerID);
				} catch (InvalidFormatException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Stanza stanza = new Stanza(toIdentity);
								
				// CREATE MESSAGE BEAN
				NetworkingBean netBean = new NetworkingBean();
				netBean.setMethod(Method.GETNOTES);
				netBean.setMyuserid(getCommManager().getIdManager().getThisNetworkNode().getBareJid());
				netBean.setFrienduserid(friendid);
				
								
				try {
					getCommManager().sendIQGet(stanza, netBean, this);
									
				} catch (CommunicationException e) {
					LOG.warn(e.getMessage());
				};

				// TYuck, another TODO this properly, for now, wait up to 5 secs
				int i= 0;
				while (commsResult == null && (i < (5 * TEST_TIME_MULTIPLER))){
					try {
						i++;
						Thread.sleep(WAIT_TIME_SECS);
					} catch (InterruptedException e) {
					// 	TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
										
						
				if (commsResult != null)
					return this.commsResult.getNotes();
						
				return null;
	}
	
	public List<ZoneDetails> getZoneDetails() {

		IIdentity toIdentity = null;
		commsResult = null;
		try {
			toIdentity = getCommManager().getIdManager().fromJid(netServerID);
		} catch (InvalidFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Stanza stanza = new Stanza(toIdentity);
								
		// CREATE MESSAGE BEAN
		NetworkingBean netBean = new NetworkingBean();
		netBean.setMethod(Method.GETZONEDETAILS);
	
		try {
			getCommManager().sendIQGet(stanza, netBean, this);
		} catch (CommunicationException e) {
			LOG.warn(e.getMessage());
		};

		// TYuck, another TODO this properly, for now, wait up to 5 secs
		int i= 0;
		while (commsResult == null && (i < (5 * TEST_TIME_MULTIPLER))){
			try {
				i++;
				Thread.sleep(WAIT_TIME_SECS);
			} catch (InterruptedException e) {
				// 	TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
										
						
		if (commsResult != null)
			return this.commsResult.getZonedetails();
						
		return null;
	}
	
	public NetworkingBeanResult getStartupInfo()
	{
		IIdentity toIdentity = null;
		commsResult = null;
		try {
			toIdentity = getCommManager().getIdManager().fromJid(netServerID);
		} catch (InvalidFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Stanza stanza = new Stanza(toIdentity);
								
		// CREATE MESSAGE BEAN
		NetworkingBean netBean = new NetworkingBean();
		netBean.setMethod(Method.GETSTARTUPINFO);
		netBean.setMyuserid(getCommManager().getIdManager().getThisNetworkNode().getBareJid());
		
		try {
			getCommManager().sendIQGet(stanza, netBean, this);
		} catch (CommunicationException e) {
			LOG.warn(e.getMessage());
		};
		
		// Yuck, another TODO this properly, for now, wait up to 5 secs
		int i= 0;
		while (commsResult == null && (i < (50 * TEST_TIME_MULTIPLER))){
			try {
				i++;
				Thread.sleep(WAIT_TIME_SECS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
				
		// Give commresult change to pupolate fully
		try {
			Thread.sleep(WAIT_TIME_SECS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}						
		
		return this.commsResult;
								
	}

	public String getNetServerID() {
		return netServerID;
	}

	public void setNetServerID(String netServerID) {
		this.netServerID = netServerID;
	}

}
