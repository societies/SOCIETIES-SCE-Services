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
package si.setcce.societies.crowdtasking.api.RESTful.impl;


import com.google.gson.Gson;
import com.googlecode.objectify.NotFoundException;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.cmd.Query;
import si.setcce.societies.crowdtasking.api.RESTful.ISpaceAPI;
import si.setcce.societies.crowdtasking.model.CTUser;
import si.setcce.societies.crowdtasking.model.CollaborativeSpace;
import si.setcce.societies.crowdtasking.model.dao.CollaborativeSpaceDAO;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static si.setcce.societies.crowdtasking.model.dao.OfyService.ofy;

/**
 * Describe your class here...
 *
 * @author Simon Jureša
 */
@Path("/space")
public class SpaceAPI implements ISpaceAPI {
    static {
        ObjectifyService.register(CollaborativeSpace.class);
    }

    @Override
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getSpace(@DefaultValue("0") @QueryParam("id") Long id,
                           @DefaultValue("0") @QueryParam("userId") Long userId,
                           @QueryParam("scope") String scope,
                           @Context HttpServletRequest request) {
        if ("".equalsIgnoreCase(scope)) {
            scope = null;
        }
        Gson gson = new Gson();
        if (id != 0) {
            return gson.toJson(getCollaborativeSpace(id));
        }
        if (userId != 0) {
            return gson.toJson(CollaborativeSpaceDAO.getCollaborativeSpaces4User(UsersAPI.getLoggedInUser(request.getSession())));
        }
        return gson.toJson(getCollaborativeSpaces(scope));
    }

    public static CollaborativeSpace getCollaborativeSpace(Long id) {
        CollaborativeSpace collaborativeSpace = null;
        try {
            collaborativeSpace = ofy().load().type(CollaborativeSpace.class).id(id).get();
        } catch (NotFoundException e) {
        }
        return collaborativeSpace;
    }

    public static CollaborativeSpace load(Ref<CollaborativeSpace> collaborativeSpaceRef) {
        return ofy().load().ref(collaborativeSpaceRef).get();
    }

    public static CollaborativeSpace getCollaborativeSpace(String symbolicLocation) {
        return ofy().load().type(CollaborativeSpace.class).filter("symbolicLocation", symbolicLocation).first().get();
    }

    public static List<CollaborativeSpace> getCollaborativeSpaces(String scope) {
        Query<CollaborativeSpace> q;
        if (scope == null) {
            q = ofy().load().type(CollaborativeSpace.class);
        } else {
            q = ofy().load().type(CollaborativeSpace.class).filter("scope", scope);
        }
        ArrayList<CollaborativeSpace> list = new ArrayList<CollaborativeSpace>();
        for (CollaborativeSpace collaborativeSpace : q) {
            list.add(collaborativeSpace);
        }
        return list;
    }

    public static Map<Long, String> getCollaborativeSpacesMap() {
        HashMap<Long, String> spacesMap = new HashMap<Long, String>();
        Query<CollaborativeSpace> q = ofy().load().type(CollaborativeSpace.class);
        for (CollaborativeSpace collaborativeSpace : q) {
            spacesMap.put(collaborativeSpace.getId(), collaborativeSpace.getName());
        }
        return spacesMap;
    }

    @Override
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response newSpace(@FormParam("communityId") Long communityId,
                             @FormParam("spaceId") Long spaceId,
                             @FormParam("spaceName") String name,
                             @FormParam("scope") String scope,
                             @FormParam("symbolicLocation") String symbolicLocation,
                             @Context HttpServletRequest request) throws IOException, URISyntaxException {


        if ("".equals(name)) {
            return Response.status(Status.BAD_REQUEST).entity("Name is required.").type("text/plain").build();
        }
        if ("".equalsIgnoreCase(scope)) {
            scope = null;
        }
        CTUser user = UsersAPI.getLoggedInUser(request.getSession());
        if (spaceId.longValue() == 0L) {
            if (ofy().load().type(CollaborativeSpace.class).filter("name", name).count() != 0) {
                return Response.status(Status.BAD_REQUEST).entity("Collaborative space " + name + " already exist.").type("text/plain").build();
            }
            CollaborativeSpace collaborativeSpace = new CollaborativeSpace(name, symbolicLocation, scope);
            CollaborativeSpaceDAO.save(collaborativeSpace);
        } else {
            CollaborativeSpace space = getCollaborativeSpace(spaceId);
            if (!space.getName().equalsIgnoreCase(name)) {
                if (ofy().load().type(CollaborativeSpace.class).filter("name", name).count() != 0) {
                    return Response.status(Status.BAD_REQUEST).entity("Collaborative space \"+name+\" already exist.").type("text/plain").build();
                }
            }
            space.setName(name);
            space.setSymbolicLocation(symbolicLocation);
            space.setScope(scope);
            CollaborativeSpaceDAO.save(space);
        }

        return Response.ok().build();
    }
}