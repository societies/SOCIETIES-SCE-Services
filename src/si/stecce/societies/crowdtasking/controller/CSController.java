/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske druĹľbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAĂ‡Ă�O, SA (PTIN), IBM Corp., 
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
package si.stecce.societies.crowdtasking.controller;

import java.io.FileReader;
import java.io.IOException;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import si.stecce.societies.crowdtasking.api.RESTful.impl.EventAPI;
import si.stecce.societies.crowdtasking.api.RESTful.impl.SpaceAPI;
import si.stecce.societies.crowdtasking.api.RESTful.impl.UsersAPI;
import si.stecce.societies.crowdtasking.model.CTUser;
import si.stecce.societies.crowdtasking.model.CollaborativeSpace;
import si.stecce.societies.crowdtasking.model.Community;
import si.stecce.societies.crowdtasking.model.dao.CommunityDAO;

import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.cmd.Query;

/**
 * Servlet for managing collaborative spaces
 *
 * @author Simon Jureša
 *
 */
@SuppressWarnings("serial")
public class CSController extends HttpServlet {
	private static final Logger log = Logger.getLogger(CSController.class.getName());

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	    if (request.getPathInfo() != null) {
		    String[] path = request.getPathInfo().split("/");
		    String page = request.getParameter("p");
		    if (page == null) {
		    	page = "";
		    }
		    if (page.equalsIgnoreCase("1")) {
		    	page = "";
		    }
		    if (path.length > 3 && path[1].equalsIgnoreCase("mapId")) {	// use .../cs/mapId/spaceId/url
		    	mapSpaceToUrl(new Long(path[2]), path[3]);
		    }
		    else {
		    	if (path.length > 1) {
		    		CTUser user = UsersAPI.getLoggedInUser(request.getSession());
		    		CollaborativeSpace space = SpaceAPI.getCollaborativeSpace(path[1]);
		    		if (space == null) {
		    			return;
		    		}
		    		Long spaceId = space.getId();
		    		Query<Community> communities = CommunityDAO.findCommunity(space);
		    		if (communities == null || communities.count() == 0) {
		    			return;
		    		}
		    		Community community = communities.first().get();
		    		if (!isMemeber(user, community)) {
		    		    response.setContentType("text/html");
		    		    response.getWriter().write("You are not a member of this community!");
		    			return;
		    		}

                    List<Ref<Community>> communitiesRefs = new ArrayList<Ref<Community>>();
                    for (Community comm:communities) {
                        communitiesRefs.add(Ref.create(Key.create(Community.class, comm.getId())));
                    }

                    if (path.length > 2) {
                        if ("enter".equalsIgnoreCase(path[2])) {	// .../sc/spaceUrl/enter
                            EventAPI.logEnterCollaborativeSpace(spaceId, new Date(), user, communitiesRefs);
                            response.getWriter().write("Check-in successful");
                            System.out.println(user.getUserName()+ " checked in to "+space.getName());
                            return;
                        }
                        if ("leave".equalsIgnoreCase(path[2])) {	// .../sc/spaceUrl/leave
                            EventAPI.logLeaveCollaborativeSpace(spaceId, new Date(), user, communitiesRefs);
                            response.getWriter().write("Check-out successful");
                            System.out.println(user.getUserName()+ " checked out from "+space.getName());
                            return;
                        }
                        if ("showTask".equalsIgnoreCase(path[2])) {	// .../sc/spaceUrl/showTask?id=x
                            sendMessage(spaceId, "showTask:"+request.getParameter("id"));
                            return;
                        }
                        if ("change".equalsIgnoreCase(path[2])) {	// .../sc/spaceUrl/change?p=x
                            sendMessage(spaceId, "changeTo:/cs/"+path[1]+"?p="+page);
                            return;
                        }
                    }
                    ChannelService channelService = ChannelServiceFactory.getChannelService();
                    String token = channelService.createChannel(spaceId.toString());
                    System.out.println("Channel created with id: "+spaceId.toString());
                    log.info("token created:"+token);

	    			FileReader reader = new FileReader("WEB-INF/html/publicDisplay"+page+".html");
	    		    CharBuffer buffer = CharBuffer.allocate(16384);
	    		    reader.read(buffer);

	    		    String index = new String(buffer.array());
	    		    index = index.replaceAll("\\{\\{ pdHeader \\}\\}", community.getName()+" - "+space.getName());
	    		    index = index.replaceAll("\\{\\{ token \\}\\}", token);
	    		    index = index.replaceAll("\\{\\{ communityId \\}\\}", community.getId().toString());
	    		    index = index.replaceAll("\\{\\{ spaceId \\}\\}", spaceId.toString());

	    		    response.setContentType("text/html");
	    		    response.getWriter().write(index);
	    		    reader.close();
		    	}
		    }

	    }
	}

	private boolean isMemeber(CTUser user, Community community) {
		for (Ref<CTUser> member:community.getMembers()) {
			if (member.equals(Ref.create(Key.create(CTUser.class, user.getId())))) {
				return true;
			}
		}
		return false;
	}

	private void sendMessage(Long spaceId, String message) {
		ChannelService channelService = ChannelServiceFactory.getChannelService();
		channelService.sendMessage(new ChannelMessage(Long.toString(spaceId), message));
	}
	
	private void mapSpaceToUrl(Long spaceId, String url) {
		CollaborativeSpace space = SpaceAPI.getCollaborativeSpace(spaceId);
		space.setUrlMapping(url);
		SpaceAPI.putSpace(space);
	}
}
