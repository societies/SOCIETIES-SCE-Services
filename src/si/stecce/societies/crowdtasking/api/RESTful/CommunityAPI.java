/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVACAO, SA (PTIN), IBM Corp.,
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
package si.stecce.societies.crowdtasking.api.RESTful;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import si.stecce.societies.crowdtasking.NotificationsSender;
import si.stecce.societies.crowdtasking.api.RESTful.json.CommunityJS;
import si.stecce.societies.crowdtasking.model.CTUser;
import si.stecce.societies.crowdtasking.model.Community;
import si.stecce.societies.crowdtasking.model.dao.CommunityDAO;

import com.google.gson.Gson;
import com.googlecode.objectify.cmd.Query;

@Path("/community/{querytype}")
public class CommunityAPI {
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public String getCommunity(@PathParam("querytype") String querytype,
			@QueryParam("communityId") Long communityId,
			@Context HttpServletRequest request) {

		if ("browse".equalsIgnoreCase(querytype)) {
			return getCommunities(UsersAPI.getLoggedInUser(request.getSession()));
		}
		if ("get".equalsIgnoreCase(querytype)) {
			return getCommunity(communityId, UsersAPI.getLoggedInUser(request.getSession()));
		}
		// for logged in user
		if ("4user".equalsIgnoreCase(querytype)) {
			return getCommunities4User(UsersAPI.getLoggedInUser(request.getSession()));
		}
		return null;
	}
	
	private String getCommunities4User(CTUser loggedInUser) {
		Gson gson = new Gson();
		ArrayList<CommunityJS> list = new ArrayList<CommunityJS>();
		Query<Community> communities = CommunityDAO.loadCommunities4User(loggedInUser);
		for (Community community:communities) {
			CommunityJS communityJS = new CommunityJS(community);
			list.add(communityJS);
		}
		return gson.toJson(list);
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response createCommunity(
			@PathParam("querytype") String querytype,
            @FormParam("communityId") String communityId,
            @FormParam("communityJid") String communityJid,
			@FormParam("name") String name,
			@FormParam("description") String description,
			@FormParam("csName") String csName,
			@FormParam("urlMapping") String urlMapping,
			@FormParam("symbolicLocation") String symbolicLocation,
			@FormParam("members") List<Long> members,
			@FormParam("memberId") Long memberId,
			@Context HttpServletRequest request) {
		CTUser user = UsersAPI.getLoggedInUser(request.getSession());
		if (user == null) {
			return Response.status(Status.UNAUTHORIZED).entity("Not authorized.").type("text/plain").build();	
		}
		if (communityId == null) {
			return Response.serverError().build();
		}

		Community community;
		if ("create".equalsIgnoreCase(querytype)) {
			if (name == null || "".equalsIgnoreCase(name)) {
				return Response.status(Status.NOT_ACCEPTABLE).entity("Name is required.").type("text/plain").build();
			}
			createCommunity(communityId, name, description, csName, urlMapping,
					symbolicLocation, members, user);
		}
		if ("request".equalsIgnoreCase(querytype)) {
			community = CommunityDAO.loadCommunity(new Long(communityId));
			community.addRequest(user.getId());
			CommunityDAO.saveCommunity(community);
			// notify admin
			NotificationsSender.requestToJoinCommunity(community, user);
			EventAPI.logRequestToJoinCommunity(new Long(communityId), communityJid, user);
		}
		if ("confirm".equalsIgnoreCase(querytype)) {
			community = CommunityDAO.loadCommunity(new Long(communityId));
			community.removeRequest(memberId);
			community.addMember(memberId);
			CommunityDAO.saveCommunity(community);
			// notify new member
			CTUser newMember = UsersAPI.getUserById(memberId);
			NotificationsSender.requestToJoinCommunityApproved(community, newMember);
			// add notifcation to news feed
			EventAPI.logNewMemeberJoinedCommunity(new Long(communityId), communityJid, newMember);
		}
		if ("leave".equalsIgnoreCase(querytype)) {
			community = CommunityDAO.loadCommunity(new Long(communityId));
			community.removeMember(user.getId());
			CommunityDAO.saveCommunity(community);
		}
		if ("reject".equalsIgnoreCase(querytype)) {
			community = CommunityDAO.loadCommunity(new Long(communityId));
			community.removeRequest(memberId);
			CommunityDAO.saveCommunity(community);
		}
		
		return Response.ok().build();
	}

	private void createCommunity(String communityId, String name,
			String description, String csName, String urlMapping,
			String symbolicLocation, List<Long> members, CTUser user) {
		Community community;
		if ("".equalsIgnoreCase(communityId)) {
			community = new Community(name, description, user, csName, urlMapping, symbolicLocation, members);
		}
		else {
			community = CommunityDAO.loadCommunity(new Long(communityId));
			community.setName(name);
			community.setDescription(description);
			if (!"".equalsIgnoreCase(csName) && community.getCollaborativeSpaces() == null) {
				community.addCollaborativeSpace(csName, urlMapping, symbolicLocation);
			}
			community.addMembers(members);
		}
		CommunityDAO.saveCommunity(community);
        EventAPI.logNewCommunity(community.getId(), null, user);
	}

	private String getCommunity(Long communityId, CTUser user) {
		Gson gson = new Gson();
		Community community = CommunityDAO.loadCommunity(communityId);
		CommunityJS communityJS = new CommunityJS(community, user.getId());
		return gson.toJson(communityJS);
	}

	private String getCommunities(CTUser user) {
		Gson gson = new Gson();
		ArrayList<CommunityJS> list = new ArrayList<CommunityJS>();
		Query<Community> communities = CommunityDAO.loadCommunities();
		for (Community communitiy:communities) {
			CommunityJS communityJS = new CommunityJS(communitiy, user.getId());
			list.add(communityJS);
		}
		return gson.toJson(list);
	}
}
