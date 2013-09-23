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
package si.stecce.societies.crowdtasking.api.RESTful.impl;

import java.util.ArrayList;
import java.util.Collection;
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
import si.stecce.societies.crowdtasking.api.RESTful.ICommunityAPI;
import si.stecce.societies.crowdtasking.api.RESTful.json.CommunityJS;
import si.stecce.societies.crowdtasking.model.CTUser;
import si.stecce.societies.crowdtasking.model.Community;
import si.stecce.societies.crowdtasking.model.dao.CommunityDAO;

import com.google.gson.Gson;
import com.googlecode.objectify.cmd.Query;

@Path("/community/{querytype}")
public class CommunityAPI implements ICommunityAPI {
	@Override
    @GET
	@Produces({ MediaType.APPLICATION_JSON })
	public String getCommunity(@PathParam("querytype") String querytype,
                               @QueryParam("communityId") Long communityId,
                               @QueryParam("ownerJid") String ownerJid,
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
		if ("4CSS".equalsIgnoreCase(querytype)) {
            System.out.println("4CSS");
            System.out.println("ownerJid:"+ownerJid);
            if (ownerJid == null) {
                ownerJid = UsersAPI.getLoggedInUser(request.getSession()).getSocietiesEntityId();
            }
            System.out.println("ownerJid:"+ownerJid);
            List<Community> communities = CommunityDAO.loadCommunities4CSS(ownerJid);
			return toJson(communities, UsersAPI.getLoggedInUser(request.getSession())); // todo: is it ok?
		}
		return null;
	}
	
	private String getCommunities4User(CTUser loggedInUser) {
        return toJson(CommunityDAO.loadCommunities4User(loggedInUser), loggedInUser);
	}

    public static String toJson(Collection<Community> communities, CTUser user) {
        Gson gson = new Gson();
        ArrayList<CommunityJS> list = getCommunityJSes(communities, user);
        return gson.toJson(list);
    }

    public static ArrayList<CommunityJS> getCommunityJSes(Collection<Community> communities, CTUser user) {
        ArrayList<CommunityJS> list = new ArrayList<CommunityJS>();
        for (Community community:communities) {
			CommunityJS communityJS = new CommunityJS(community, user);
			list.add(communityJS);
		}
        return list;
    }

    @Override
    @POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response createCommunity(
            @PathParam("querytype") String querytype,
            @FormParam("communityId") String communityId,
            @FormParam("communityJid") String communityJid,
            @FormParam("ownerJid") String ownerJid,
            @FormParam("name") String name,
            @FormParam("description") String description,
            @FormParam("spacesCombo") List<Long> csIds,
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
            System.out.println("ownerJid:"+ownerJid);
            System.out.println("csIds:"+csIds);
            if ("".equalsIgnoreCase(communityJid)) {
                if (name == null || "".equalsIgnoreCase(name)) {
                    return Response.status(Status.NOT_ACCEPTABLE).entity("Name is required.").type("text/plain").build();
                }
                createOrUpdateCommunity(communityId, name, description, csIds, members, user);
            } else {
                community = createOrUpdateCIS(communityJid, name, description, csIds, ownerJid);
                Gson gson = new Gson();
                return Response.ok().entity(gson.toJson(new CommunityJS(community, user))).build();
            }
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

	private void createOrUpdateCommunity(String communityId, String name, String description, List<Long> csIds, List<Long> members, CTUser user) {
		Community community;
		if ("".equalsIgnoreCase(communityId)) {
			community = new Community(name, description, user, csIds, members);
		}
		else {
			community = CommunityDAO.loadCommunity(new Long(communityId));
			community.setName(name);
			community.setDescription(description);
			community.setCollaborativeSpaces(csIds);
			community.addMembers(members);
		}
		CommunityDAO.saveCommunity(community);
        if ("".equalsIgnoreCase(communityId)) {
            EventAPI.logNewCommunity(community.getId(), null, user);
        }
	}

	private Community createOrUpdateCIS(String communityJid, String name, String description, List<Long> csIds, String ownerJid) {
		Community community = CommunityDAO.loadCommunity(communityJid);
		if (community == null) {
			community = new Community(communityJid, name, description, csIds, ownerJid);
		}
		else {
			community.setCollaborativeSpaces(csIds);
		}
		CommunityDAO.saveCommunity(community);
        return community;
	}

	private String getCommunity(Long communityId, CTUser user) {
		Gson gson = new Gson();
		Community community = CommunityDAO.loadCommunity(communityId);
		CommunityJS communityJS = new CommunityJS(community, user);
		return gson.toJson(communityJS);
	}

	private String getCommunities(CTUser user) {
        return toJson(CommunityDAO.loadCommunities(), user);
	}
}
