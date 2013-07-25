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


import static si.stecce.societies.crowdtasking.model.dao.OfyService.ofy;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import si.stecce.societies.crowdtasking.model.CollaborativeSpace;
import si.stecce.societies.crowdtasking.model.Community;
import si.stecce.societies.crowdtasking.model.dao.CollaborativeSpaceDAO;
import si.stecce.societies.crowdtasking.model.dao.CommunityDAO;

import com.google.gson.Gson;
import com.googlecode.objectify.NotFoundException;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.cmd.Query;

/**
 * Describe your class here...
 *
 * @author Simon Jureša
 *
 */
@Path("/space")
public class SpaceAPI {
    static {
		ObjectifyService.register(CollaborativeSpace.class);
    }

	@GET
	@Produces({MediaType.APPLICATION_JSON })
	public String getSpace(@DefaultValue("0") @QueryParam("id") Long id,
			@DefaultValue("0") @QueryParam("userId") Long userId,
			@Context HttpServletRequest request) {
		Gson gson = new Gson();
		if (id != 0) {
			return gson.toJson(getCollaborativeSpace(id));
		}
		if (userId != 0) {
			return gson.toJson(CollaborativeSpaceDAO.getCollaborativeSpaces4User(UsersAPI.getLoggedInUser(request.getSession())));
		}
		return gson.toJson(getCollaborativeSpaces());
	}
	
	public static CollaborativeSpace getCollaborativeSpace(Long id) {
		CollaborativeSpace collaborativeSpace = null;
		try {
			collaborativeSpace = ofy().load().type(CollaborativeSpace.class).id(id).get();
		} catch (NotFoundException e) {}
		return collaborativeSpace;
	}

	public static CollaborativeSpace load(Ref<CollaborativeSpace> collaborativeSpaceRef) {
		return ofy().load().ref(collaborativeSpaceRef).get();
	}

	public static CollaborativeSpace getCollaborativeSpace(String urlMapping) {
		return ofy().load().type(CollaborativeSpace.class).filter("urlMapping", urlMapping).first().get();
	}

	public static CollaborativeSpace putSpace(CollaborativeSpace collaborativeSpace) {
		ofy().save().entity(collaborativeSpace);
		return collaborativeSpace;
	}

	public static List<CollaborativeSpace> getCollaborativeSpaces() {
		Query<CollaborativeSpace> q = ofy().load().type(CollaborativeSpace.class);
		ArrayList<CollaborativeSpace> list = new ArrayList<CollaborativeSpace>();
		for (CollaborativeSpace collaborativeSpace: q) {
			list.add(collaborativeSpace);
		}
		return list;
	}
	
	public static Map<Long, String> getCollaborativeSpacesMap() {
		HashMap<Long, String> spacesMap = new HashMap<Long, String>();
		Query<CollaborativeSpace> q = ofy().load().type(CollaborativeSpace.class);
		for (CollaborativeSpace collaborativeSpace: q) {
			spacesMap.put(collaborativeSpace.getId(), collaborativeSpace.getName());
		}
		return spacesMap;
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response newSpace(@FormParam("communityId") Long communityId, 
			@FormParam("spaceId") Long spaceId,
			@FormParam("spaceName") String name,
			@FormParam("urlMapping") String urlMapping,
			@FormParam("symbolicLocation") String symbolicLocation) throws IOException, URISyntaxException {
		
		if ("".equals(communityId)) {
			return Response.status(Status.BAD_REQUEST).entity("Unknown error.").type("text/plain").build();
		}
		
		if ("".equals(name)) {
			return Response.status(Status.BAD_REQUEST).entity("Name is required.").type("text/plain").build();
		}
		if (spaceId.longValue() == 0L) {
			Community community = CommunityDAO.loadCommunity(communityId);
			if (community == null) {
				return Response.status(Status.BAD_REQUEST).entity("Can't get the community.").type("text/plain").build();
			}
			if (ofy().load().type(CollaborativeSpace.class).filter("urlMapping", urlMapping).count() != 0){
				return Response.status(Status.BAD_REQUEST).entity("URL mapping already exist.").type("text/plain").build();
			}
			community.addCollaborativeSpace(name, urlMapping, symbolicLocation);
			CommunityDAO.saveCommunity(community);
		}
		else {
			CollaborativeSpace space = getCollaborativeSpace(spaceId);
			space.setName(name);
			if (!space.getUrlMapping().equalsIgnoreCase(urlMapping)) {
				if (ofy().load().type(CollaborativeSpace.class).filter("urlMapping", urlMapping).count() != 0){
					return Response.status(Status.BAD_REQUEST).entity("URL mapping already exist.").type("text/plain").build();
				}
			}
			space.setUrlMapping(urlMapping);
			space.setSymbolicLocation(symbolicLocation);
			putSpace(space);
		}
		
		return Response.ok().build();
	}
}