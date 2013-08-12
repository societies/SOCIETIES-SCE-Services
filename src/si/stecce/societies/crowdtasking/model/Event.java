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
package si.stecce.societies.crowdtasking.model;

import java.util.Date;
import java.util.List;

import si.stecce.societies.crowdtasking.Util;
import si.stecce.societies.crowdtasking.api.RESTful.CommentAPI;
import si.stecce.societies.crowdtasking.api.RESTful.SpaceAPI;
import si.stecce.societies.crowdtasking.api.RESTful.UsersAPI;
import si.stecce.societies.crowdtasking.model.dao.MeetingDAO;
import si.stecce.societies.crowdtasking.model.dao.TaskDao;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Index;

/**
 * Describe your class here...
 *
 * @author Simon Jureša
 *
 */
@Entity
public class Event {
	@Id private Long id;
	private EventType type;
	private Ref<CTUser> userRef;
	@Index private Ref<CollaborativeSpace> collaborativeSpaceRef;
	@Index private List<Ref<Community>> communityRefs;
	private Ref<Task> taskRef;
	private Ref<Comment> commentRef;
	@Index private Date eventDate;
    private String eventText;
    private String eventTextHTML;
	@Ignore private CTUser user;
	private Ref<Meeting> meetingRef;
	@Ignore private Meeting meeting;
	@Ignore private String communityName;
	
	public Event() {}
	
	public Event(EventType type, CTUser user, List<Ref<Community>> communityRefs,
			Ref<CollaborativeSpace> collaborativeSpaceRef, Long taskId, Long commentId,
			Date eventDate, Meeting meeting, String communityName) {

		this.id = null;
		this.type = type;
		this.user = user;
		this.userRef = Ref.create(Key.create(CTUser.class, user.getId()));
		this.communityRefs = communityRefs;
		this.collaborativeSpaceRef = collaborativeSpaceRef;
		this.taskRef = taskId == null ? null : Ref.create(Key.create(Task.class, taskId));
		this.commentRef = commentId == null ? null : Ref.create(Key.create(Comment.class, commentId));
		this.eventDate = eventDate;
		this.meeting = meeting;
		this.meetingRef = meeting == null ? null : Ref.create(Key.create(Meeting.class, meeting.getId()));
		this.communityName = communityName;
		setEventText();
	}

	public Long getId() {
		return id;
	}

	public EventType getType() {
		return type;
	}

	public Long getCollaborativeSpaceId() {
		return collaborativeSpaceRef.getKey().getId();
	}

	public Long getTaskId() {
		if (taskRef == null) {
			return null;
		}
		
		return taskRef.getKey().getId();
	}

	public Long getCommentId() {
		return commentRef.getKey().getId();
	}

	public Date getEventDate() {
		return eventDate;
	}

	private String when(Date eventDate) {
		return " at "+Util.formatDate(eventDate);
	}
	
	public Ref<CTUser> getUserRef() {
		return userRef;
	}

	public Ref<Meeting> getMeetingRef() {
		return meetingRef;
	}

	private void setEventText() {
		String commentOwner;
		
		String userName = user.getUserName();
		Task task = null;
		if (taskRef != null) {
			task = TaskDao.loadTask(taskRef);
		}

		switch (type) {
		case CREATE_TASK:
            eventText = userName + " created a new task " + task.getTitle()  + when(eventDate);
            eventTextHTML = userName + " created a new task " + Util.taskHTMLLink(task)  + when(eventDate);
			break;
		case TASK_COMMENT:
            eventText = userName + " commented on " + task.getTitle() + when(eventDate);
            eventTextHTML = userName + " commented on " + Util.taskHTMLLink(task) + when(eventDate);
			break;
		case EXECUTE_TASK:
            eventText = userName + " started task execution of " + task.getTitle() + when(eventDate);
            eventTextHTML = userName + " started task execution of " + Util.taskHTMLLink(task) + when(eventDate);
			break;
		case FINALIZE_TASK:
            eventText = userName + " finalized " + task.getTitle() + when(eventDate);
            eventTextHTML = userName + " finalized " + Util.taskHTMLLink(task) + when(eventDate);
			break;
		case ENTER_COLLABORATIVE_SPACE:
			eventText = userName + " entered '" + SpaceAPI.load(collaborativeSpaceRef).getName() + "'" + when(eventDate);
            eventTextHTML = eventText;
			break;
		case LEAVE_COLLABORATIVE_SPACE:
			eventText = userName + " left '" + SpaceAPI.load(collaborativeSpaceRef).getName() + "'" + when(eventDate);
            eventTextHTML = eventText;
			break;
		case LIKE_TASK:
            eventText = userName + " liked task " + task.getTitle() + when(eventDate);
            eventTextHTML = userName + " liked task " + Util.taskHTMLLink(task) + when(eventDate);
			break;
		case UNLIKE_TASK:
            eventText = userName + " unliked task " + task.getTitle() + when(eventDate);
            eventTextHTML = userName + " unliked task " + Util.taskHTMLLink(task) + when(eventDate);
			break;
		case LIKE_COMMENT:
			commentOwner = CommentAPI.getCommentById(getCommentId()).getOwner().getUserName();
			eventText = userName + " liked " + commentOwner + "'s comment on task " + task.getTitle() + when(eventDate);
            eventTextHTML = userName + " liked " + commentOwner + "'s comment on task " + Util.taskHTMLLink(task) + when(eventDate);
			break;
		case UNLIKE_COMMENT:
			commentOwner = CommentAPI.getCommentById(getCommentId()).getOwner().getUserName();
			eventText = userName + " unliked " + commentOwner + "'s comment on task " + task.getTitle() + when(eventDate);
            eventTextHTML = userName + " unliked " + commentOwner + "'s comment on task " + Util.taskHTMLLink(task) + when(eventDate);
			break;
		case NEW_MEETING:
			eventText = userName + " created new meeting "+meeting.getSubject()+" in "+meeting.getCollaborativeSpace().getName()+when(eventDate);
            eventTextHTML = userName + " created new meeting "+Util.taskHTMLLink(meeting, task)+" in "+meeting.getCollaborativeSpace().getName()+when(eventDate);
			break;
		case SHOW_TASK_ON_PD:
			eventText = userName + " showed task " + task.getTitle() + " on public display" + when(eventDate);
            eventTextHTML = userName + " showed task " + Util.taskHTMLLink(task) + " on public display" + when(eventDate);
			break;
		case PICK_TASK_FROM_PD:
			eventText = userName + " picked task " + task.getTitle() + " from public display" + when(eventDate);
            eventTextHTML = userName + " picked task " + Util.taskHTMLLink(task) + " from public display" + when(eventDate);
			break;
		case REQUEST_TO_JOIN_COMMUNITY:
			eventText = userName + " requested to join the community " + communityName + when(eventDate);
            eventTextHTML = eventText;
			break;
		case JOINED_COMMUNITY:
			eventText = userName + " joined the community " + communityName + when(eventDate);
            eventTextHTML = eventText;
			break;
		default:
			break;
		}		
	}

    public void convertEventText() {
        String commentOwner;
        Meeting meeting=null;

        CTUser user = UsersAPI.getUser(userRef);
        if (meetingRef != null) {
            meeting = MeetingDAO.loadMeeting(meetingRef.getKey().getId());
        }
        String userName = user.getUserName();
        Task task = null;
        if (taskRef != null) {
            task = TaskDao.loadTask(taskRef);
        }

        try {
        switch (type) {
            case CREATE_TASK:
                eventText = userName + " created a new task " + task.getTitle()  + when(eventDate);
                eventTextHTML = userName + " created a new task " + Util.taskHTMLLink(task)  + when(eventDate);
                break;
            case TASK_COMMENT:
                eventText = userName + " commented on " + task.getTitle() + when(eventDate);
                eventTextHTML = userName + " commented on " + Util.taskHTMLLink(task) + when(eventDate);
                break;
            case EXECUTE_TASK:
                eventText = userName + " started task execution of " + task.getTitle() + when(eventDate);
                eventTextHTML = userName + " started task execution of " + Util.taskHTMLLink(task) + when(eventDate);
                break;
            case FINALIZE_TASK:
                eventText = userName + " finalized " + task.getTitle() + when(eventDate);
                eventTextHTML = userName + " finalized " + Util.taskHTMLLink(task) + when(eventDate);
                break;
            case ENTER_COLLABORATIVE_SPACE:
                eventText = userName + " entered '" + SpaceAPI.load(collaborativeSpaceRef).getName() + "'" + when(eventDate);
                eventTextHTML = eventText;
                break;
            case LEAVE_COLLABORATIVE_SPACE:
                eventText = userName + " left '" + SpaceAPI.load(collaborativeSpaceRef).getName() + "'" + when(eventDate);
                eventTextHTML = eventText;
                break;
            case LIKE_TASK:
                eventText = userName + " liked task " + task.getTitle() + when(eventDate);
                eventTextHTML = userName + " liked task " + Util.taskHTMLLink(task) + when(eventDate);
                break;
            case UNLIKE_TASK:
                eventText = userName + " unliked task " + task.getTitle() + when(eventDate);
                eventTextHTML = userName + " unliked task " + Util.taskHTMLLink(task) + when(eventDate);
                break;
            case LIKE_COMMENT:
                commentOwner = CommentAPI.getCommentById(getCommentId()).getOwner().getUserName();
                eventText = userName + " liked " + commentOwner + "'s comment on task " + task.getTitle() + when(eventDate);
                eventTextHTML = userName + " liked " + commentOwner + "'s comment on task " + Util.taskHTMLLink(task) + when(eventDate);
                break;
            case UNLIKE_COMMENT:
                commentOwner = CommentAPI.getCommentById(getCommentId()).getOwner().getUserName();
                eventText = userName + " unliked " + commentOwner + "'s comment on task " + task.getTitle() + when(eventDate);
                eventTextHTML = userName + " unliked " + commentOwner + "'s comment on task " + Util.taskHTMLLink(task) + when(eventDate);
                break;
            case NEW_MEETING:
                eventText = userName + " created new meeting "+meeting.getSubject()+" in "+meeting.getCollaborativeSpace().getName()+when(eventDate);
                eventTextHTML = userName + " created new meeting "+Util.taskHTMLLink(meeting, task)+" in "+meeting.getCollaborativeSpace().getName()+when(eventDate);
                break;
            case SHOW_TASK_ON_PD:
                eventText = userName + " showed task " + task.getTitle() + " on public display" + when(eventDate);
                eventTextHTML = userName + " showed task " + Util.taskHTMLLink(task) + " on public display" + when(eventDate);
                break;
            case PICK_TASK_FROM_PD:
                eventText = userName + " picked task " + task.getTitle() + " from public display" + when(eventDate);
                eventTextHTML = userName + " picked task " + Util.taskHTMLLink(task) + " from public display" + when(eventDate);
                break;
            case REQUEST_TO_JOIN_COMMUNITY:
                eventText = userName + " requested to join the community " + communityName + when(eventDate);
                eventTextHTML = eventText;
                break;
            case JOINED_COMMUNITY:
                eventText = userName + " joined the community " + communityName + when(eventDate);
                eventTextHTML = eventText;
                break;
            default:
                break;
        }

        } catch (NullPointerException e) {
        }
    }

    public String getEventText() {
        return eventText;
    }

    public String getEventTextHTML() {
        return eventTextHTML;
    }

    public List<Ref<Community>> getCommunityRefs() {
		return communityRefs;
	}
}