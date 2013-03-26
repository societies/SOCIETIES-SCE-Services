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

/**
 * Describe your class here...
 *
 * @author aleckey
 *
 */
package org.societies.ext3p.nzone.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.interfaces.IFeatureServer;
import org.societies.api.ext3p.schema.nzone.NzoneBean;
import org.societies.api.ext3p.schema.nzone.NzoneBeanResult;
import org.societies.api.ext3p.schema.nzone.UserDetails;


public class NZoneCommsServer implements IFeatureServer {

	
	private static final List<String> NAMESPACES = Collections
			.unmodifiableList(Arrays
					.asList("http://societies.org/api/ext3p/schema/nzone"));
	private static final List<String> PACKAGES = Collections
			.unmodifiableList(Arrays
					.asList("org.societies.api.ext3p.schema.nzone"));

	// PRIVATE VARIABLES
	private ICommManager commManager;
	private NZoneServer nzoneServer;

	private static Logger LOG = LoggerFactory
			.getLogger(NZoneCommsServer.class);

	public ICommManager getCommManager() {
		return commManager;
	}

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}

	// METHODS
	public NZoneServer getNzoneServer() {
		return nzoneServer;
	}

	public void setNzoneServer(NZoneServer nzoneServer) {
		this.nzoneServer = nzoneServer;
	}

	public NZoneCommsServer() {
		
	}

	public void InitService() {
		// Registry Networking Directory with the Comms Manager
		try {
			getCommManager().register(this);
			LOG.info("Registered NZoneCommsServer with the xc manager");
		} catch (CommunicationException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public List<String> getJavaPackages() {
		return PACKAGES;
	}

	@Override
	public List<String> getXMLNamespaces() {
		return NAMESPACES;
	}

	/* Put your functionality here if there is NO return object, ie, VOID */
	@Override
	public void receiveMessage(Stanza stanza, Object payload) {

	}

	/* Put your functionality here if there IS a return object */
	@Override
	public Object getQuery(Stanza stanza, Object payload) throws XMPPError {

		if (LOG.isDebugEnabled())
			LOG.debug("getQuery: Received a message!");

		if (payload.getClass().equals(NzoneBean.class)) {

			if (LOG.isDebugEnabled())
				LOG.debug("Remote call to NZone Comms Server");

			NzoneBean messageBean = (NzoneBean) payload;
			NzoneBeanResult messageResult = new NzoneBeanResult();
			messageResult.setResult(false);

			try {
				switch (messageBean.getMethod()) {

					case GET_ZONE_DETAILS: 
					{
						messageResult.setZonedata(getNzoneServer().getNetZoneDetails());
						messageResult.setResult(true);
						break;
					}
					case GET_MAIN_ZONE: {
						List<String> returnData = new ArrayList<String>();
						returnData.add(getNzoneServer().getMainZoneCisID());
						messageResult.setData(returnData);
						messageResult.setResult(true);
						break;
					}
					case GET_ZONE_LIST: {
						messageResult.setData(getNzoneServer().getZoneCisIDs());
						messageResult.setResult(true);
						break;
					}
					case GET_USER_DETAILS:	{
						messageResult.setUserdata(getNzoneServer().getUserDetails(stanza.getFrom().getBareJid(), messageBean.getData()));
						messageResult.setResult(true);
						
						break;
					}
					case GET_PREFERENCES: 
					{
						List<String> returnData = new ArrayList<String>();
						returnData.add(getNzoneServer().getPreferences(stanza.getFrom().getBareJid()));
						messageResult.setData(returnData);
						messageResult.setResult(true);
						break;
					}
					case SAVE_PREFERENCES: 
					{
						messageResult.setResult(getNzoneServer().savePreferences(stanza.getFrom().getBareJid(), messageBean.getData().get(0)));
						break;
					}
					case GET_MY_DETAILS:	
					{
						List<UserDetails> returnData = new ArrayList<UserDetails>();
						returnData.add(getNzoneServer().getProfileDetails(stanza.getFrom().getBareJid()));
						messageResult.setUserdata(returnData);
						messageResult.setResult(true);
						break;
					}
					
					/**
					 * case GETUSERDETAILS:	{
						// We need to check that the user only is allowed update their own record 
						if (stanza.getFrom().getBareJid().contains(messageBean.getMyuserid()))
						{
							messageResult.setUserDetails(netServer.getUserDetails(messageBean.getMyuserid(), messageBean.getFrienduserid()));
							messageResult.setResult(true);
						}
						break;
					}
					**/
					case UPDATE_MY_DETAILS:
					{
						LOG.info("UPDATEMYDETAILS for user " + stanza.getFrom().getBareJid());
						messageBean.getDetails().setUserid(stanza.getFrom().getBareJid());
						// We need to check that the user only is allowed update their own record 
						messageResult.setResult(getNzoneServer().updateMyDetails(messageBean.getDetails()));
						break;
					}
					/**
					case GETSHAREINFO:	{
						// We need to check that the user only is allowed update their own record 
						if (stanza.getFrom().getBareJid().contains(messageBean.getMyuserid()))
						{
							messageResult.setSharedInfo(netServer.getShareInfo(messageBean.getMyuserid(), messageBean.getFrienduserid()));
							messageResult.setResult(true);
						}
						break;
					}
					case GETFRIENDSHAREINFO: {
						
						if (stanza.getFrom().getBareJid().contains(messageBean.getMyuserid()))
						{
							messageResult.setSharedInfo(netServer.getShareInfo(messageBean.getFrienduserid(), messageBean.getMyuserid()));
							messageResult.setResult(true);
						}
						break;
					}
					case UPDATESHAREINFO:	{
						// We need to check that the user only is allowed update their own record 
						if (stanza.getFrom().getBareJid().contains(messageBean.getMyuserid()))
						{
							messageResult.setSharedInfo(netServer.updateShareInfo(messageBean.getSharedInfo()));
							messageResult.setResult(true);
						}
						break;
					}
					case GETZONEEVENTS:
					{
						messageResult.setZoneeventlist(netServer.getCisActivity(messageBean.getMycurrentzone()));
						messageResult.setResult(true);
						break;
					}
					case GETMEMBERDETAILS:
					{
						messageResult.setMemberdetaillist(netServer.getMemberNames(messageBean.getUseridlist()));
						messageResult.setResult(true);
						break;
					}
					case GETNOTES: 
					{
						if (stanza.getFrom().getBareJid().contains(messageBean.getMyuserid()))
						{
							messageResult.setNotes(netServer.getNotes(messageBean.getMyuserid(), messageBean.getFrienduserid()));
							messageResult.setResult(true);
						}
						break;
					}
					case ADDNOTE: 
					{
						if (stanza.getFrom().getBareJid().contains(messageBean.getMyuserid()))
						{
							messageResult.setNotes(netServer.addNote(messageBean.getMyuserid(), messageBean.getFrienduserid(), messageBean.getNote()));
							messageResult.setResult(true);
						}
						break;
					}
					case GETSTARTUPINFO: 
					{
						if (stanza.getFrom().getBareJid().contains(messageBean.getMyuserid()))
						{
							messageResult.setNetworkingCis(netServer.getMyMainCisId());
							messageResult.setZones(netServer.getZoneCisIDs());
							messageResult.setZonedetails(netServer.getZoneDetails());
							messageResult.setUserDetails(netServer.getMyDetails(messageBean.getMyuserid()));
							messageResult.setResult(true);
						}
						break;
					}
					case GETUSERDETAILSLIST:
					{
						if (stanza.getFrom().getBareJid().contains(messageBean.getMyuserid()))
						{
							messageResult.setUserDetailsList(netServer.getUserDetailsList(messageBean.getMyuserid(), messageBean.getUseridlist()));
							messageResult.setResult(true);
						}
					}
					break;
					*/
				}
			} catch (Exception e) {
				e.printStackTrace();
			};

			return messageResult;

		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.comm.xmpp.interfaces.FeatureServer#setQuery(org.societies
	 * .comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public Object setQuery(Stanza arg0, Object arg1) throws XMPPError {
		// TODO Auto-generated method stub
		return null;
	}

}
