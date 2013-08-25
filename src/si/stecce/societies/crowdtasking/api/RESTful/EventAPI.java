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
package si.stecce.societies.crowdtasking.api.RESTful;

import static si.stecce.societies.crowdtasking.model.dao.OfyService.ofy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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

import si.stecce.societies.crowdtasking.EventsForUserFilter;
import si.stecce.societies.crowdtasking.api.RESTful.json.EventJS;
import si.stecce.societies.crowdtasking.model.CTUser;
import si.stecce.societies.crowdtasking.model.CollaborativeSpace;
import si.stecce.societies.crowdtasking.model.Comment;
import si.stecce.societies.crowdtasking.model.Community;
import si.stecce.societies.crowdtasking.model.Event;
import si.stecce.societies.crowdtasking.model.EventComparator;
import si.stecce.societies.crowdtasking.model.EventType;
import si.stecce.societies.crowdtasking.model.Meeting;
import si.stecce.societies.crowdtasking.model.Task;
import si.stecce.societies.crowdtasking.model.dao.CommunityDAO;
import si.stecce.societies.crowdtasking.model.dao.TaskDao;

import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.gson.Gson;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.cmd.Query;

/**
 *
 * @author Simon Jureša
 *
 */

@Path("/event")
public class EventAPI {
	private static final int MAX_NUM_EVENTS = 100;
	private static final int DEFAULT_NUM_EVENTS = 10;
 	@GET
	@Produces({MediaType.APPLICATION_JSON })
	public String getEvents(@QueryParam("communityId") Long communityId,
			@QueryParam("spaceId") Long spaceId,
			@DefaultValue("8") @QueryParam("limit") int limit,
			@Context HttpServletRequest request) {

 		if (communityId != null) {
			List<Event> events = getEventsForCommunity(communityId, limit);
			if (spaceId != null) {
				List<Event> events4Space = getEventsForCollaborativeSpace(spaceId, limit);
				events.addAll(events4Space);
				Collections.sort(events, new EventComparator());
			}
			Gson gson = new Gson();
            ArrayList<EventJS> list = new ArrayList<EventJS>();
            for (Event event: events) {
                list.add(new EventJS(event));
            }
			return gson.toJson(list);
		}
		else {
			return getEventsForUser(UsersAPI.getLoggedInUser(request.getSession()), limit);
		}
	}

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response newComment(@FormParam("eventType") String eventType,
			@DefaultValue("-1") @FormParam("taskId") Long taskId,
			@Context HttpServletRequest request) {
		
		CTUser user = UsersAPI.getLoggedInUser(request.getSession());
		logPickTaskFromPd(taskId, user);
		return Response.ok().build();
	}
 	
	/**
	 * @param spaceId
	 * @param limit
	 */
	private List<Event>  getEventsForCollaborativeSpace(Long spaceId, int limit) {
		Query<Event> q;
		if (spaceId != null && spaceId != 0) {
			q = ofy().load().type(Event.class).filter("collaborativeSpaceRef", Ref.create(Key.create(CollaborativeSpace.class, spaceId))).order("-eventDate").limit(limit);
		}
		else {
			return null;
		}
		ArrayList<Event> list = new ArrayList<Event>();
		for (Event event: q) {
			if (event.getType() != EventType.REQUEST_TO_JOIN_COMMUNITY) {
				list.add(event);
			}
		}
		return list;
		
	}

	public static List<Event> getEventsForCommunity(Long communityID, int limit) {
		Query<Event> q;
		if (communityID != null && communityID != 0) {
			q = ofy().load().type(Event.class).filter("communityRefs", Ref.create(Key.create(Community.class, communityID))).order("-eventDate").limit(limit);
		}
		else {
			q = ofy().load().type(Event.class).order("-eventDate").limit(DEFAULT_NUM_EVENTS);
		}
		ArrayList<Event> list = new ArrayList<Event>();
		for (Event event: q) {
			list.add(event);
		}
		return list;
	}
	
	// news feed
	private String getEventsForUser(CTUser user, int limit) {
		Gson gson = new Gson();
		Query<Event> q = ofy().load().type(Event.class).order("-eventDate").limit(MAX_NUM_EVENTS);
		ArrayList<EventJS> list = new ArrayList<EventJS>();
		int i=0;
		EventsForUserFilter filter = new EventsForUserFilter(user);
		for (Event event: q) {
			if (filter.isEventForUser(event)) {
				list.add(new EventJS(event));
				i++;
			}
			if (i == limit) {
				break;
			}
		}
		return gson.toJson(list);
	}
	
	public static void logNewAccount(CTUser user) {
		createEvent(EventType.ACCOUNT_CREATED, null, null, null, new Date(), user, null);
	}
	
	public static void logCreateTask(Task task, CTUser user) {
		createEvent(EventType.CREATE_TASK, null, task, null, task.getCreated(), user, null);
	}

	public static void logNewMeeting(Task task, Meeting meeting, Date eventDate, CTUser user) {
		createEvent(EventType.NEW_MEETING, meeting.getCsRef(), task, null, eventDate, user, meeting);
	}
	
	public static void logTaskComment(Long taskId, Long commentId, Date eventDate, CTUser user) {
		createEvent(EventType.TASK_COMMENT, null, TaskDao.getTaskById(taskId), commentId, eventDate, user, null);
	}
	
	public static void logLikeTask(Task task, Date eventDate, CTUser user) {
		createEvent(EventType.LIKE_TASK, null, task, null, eventDate, user, null);
	}
	
	public static void logUnlikeTask(Task task, Date eventDate, CTUser user) {
		createEvent(EventType.UNLIKE_TASK, null, task, null, eventDate, user, null);
	}
	
	public static void logLikeComment(Comment comment, Date eventDate, CTUser user) {
		createEvent(EventType.LIKE_COMMENT, null, comment.getTask(), comment.getId(), eventDate, user, null);
	}
	
	public static void logUnlikeComment(Comment comment, Date eventDate, CTUser user) {
		createEvent(EventType.UNLIKE_COMMENT, null, comment.getTask(), comment.getId(), eventDate, user, null);
	}
	
	public static void logExecuteTask(Long taskId, Date eventDate, CTUser user) {
		createEvent(EventType.EXECUTE_TASK, null, TaskDao.getTaskById(taskId), null, eventDate, user, null);
	}
	
	public static void logFinalizeTask(Long taskId, Date eventDate, CTUser user) {
		createEvent(EventType.FINALIZE_TASK, null, TaskDao.getTaskById(taskId), null, eventDate, user, null);
	}
	
	public static void logEnterCollaborativeSpace(Long collaborativeSpaceId, Date eventDate, CTUser user, List<Ref<Community>> communitiesRefs) {
		user.setCheckIn(new Date());
		user.setSpaceId(collaborativeSpaceId);
		UsersAPI.saveUser(user);
		createEvent(EventType.ENTER_COLLABORATIVE_SPACE, 
			Ref.create(Key.create(CollaborativeSpace.class, collaborativeSpaceId)), 
			null, null, eventDate, user, null, communitiesRefs, null);  // TODO: add communityJid?
	}
	
	public static void logLeaveCollaborativeSpace(Long collaborativeSpaceId, Date eventDate, CTUser user, List<Ref<Community>> communitiesRefs) {
		user.setCheckIn(null);
		user.setSpaceId(null);
		UsersAPI.saveUser(user);
		createEvent(EventType.LEAVE_COLLABORATIVE_SPACE, 
			Ref.create(Key.create(CollaborativeSpace.class, collaborativeSpaceId)), 
			null, null, eventDate, user, null, communitiesRefs, null);  // TODO: add communityJid?
	}

	public static void logShowTaskOnPd(Long taskId, CTUser user) {
		createEvent(EventType.SHOW_TASK_ON_PD, null, TaskDao.getTaskById(taskId), null, new Date(), user, null);
	}
	
	public static void logPickTaskFromPd(Long taskId, CTUser user) {
		createEvent(EventType.PICK_TASK_FROM_PD, null, TaskDao.getTaskById(taskId), null, new Date(), user, null);
	}
	
	public static void logRequestToJoinCommunity(Long communityId, String communityJid, CTUser user) {
		List<Ref<Community>> communityRefs = new ArrayList<Ref<Community>>();
		communityRefs.add(Ref.create(Key.create(Community.class, communityId)));
        List<String> communityJids = new ArrayList<String>();
        communityJids.add(communityJid);
		createEvent(EventType.REQUEST_TO_JOIN_COMMUNITY, null, null, null, new Date(), user, null, communityRefs, communityJids);
	}
	
	public static void logNewMemeberJoinedCommunity(Long communityId, String communityJid, CTUser user) {
		List<Ref<Community>> communityRefs = new ArrayList<Ref<Community>>();
		communityRefs.add(Ref.create(Key.create(Community.class, communityId)));
        List<String> communityJids = new ArrayList<String>();
        communityJids.add(communityJid);
		createEvent(EventType.JOINED_COMMUNITY, null, null, null, new Date(), user, null, communityRefs, communityJids);
	}
	
	public static void createEvent(EventType type, Ref<CollaborativeSpace> collaborativeSpaceRef, Task task, Long commentId,
			Date eventDate, CTUser user, Meeting meeting) {
		List<Ref<Community>> communityRefs = null;
        List<String> communityJids=null;
		if (task != null) {
			communityRefs = task.getCommunitiesRefs();
            communityJids = task.getCommunityJids();
		}
		createEvent(type, collaborativeSpaceRef, task, commentId, eventDate, user, meeting, communityRefs, communityJids);
	}

	public static void createEvent(EventType type, Ref<CollaborativeSpace> collaborativeSpaceRef, Task task, Long commentId,
			Date eventDate, CTUser user, Meeting meeting, List<Ref<Community>> communityRefs, List<String> communityJids) {

		Community community=null;
		Long taskId = null;

		if (task != null) {
			taskId = task.getId();
		}

		if (communityRefs != null && !communityRefs.isEmpty()) {
            community = CommunityDAO.loadCommunity(communityRefs.get(0));
		}
			
		
		Event event = new Event(type, user, communityRefs, collaborativeSpaceRef, 
				taskId, commentId, eventDate, meeting, community, communityJids);
    	ofy().save().entity(event);

    	try {
    		ChannelService channelService = ChannelServiceFactory.getChannelService();
    		if (collaborativeSpaceRef != null) {
        		channelService.sendMessage(new ChannelMessage(Long.toString(collaborativeSpaceRef.getKey().getId()), event.getEventText()));
    		}
    		else {
        		if (communityRefs != null) {
        			for (Ref<Community> communityRef:communityRefs) {
        				community = CommunityDAO.loadCommunity(communityRef);
        				List<CollaborativeSpace> spaces = community.getCollaborativeSpaces();
        				if (spaces != null) {
            				for (CollaborativeSpace space:spaces) {
                        		channelService.sendMessage(new ChannelMessage(Long.toString(space.getId()), event.getEventText()));
            				}
        				}
        			}
        		}
    		}
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    	}
    	//return event;
    }
}
