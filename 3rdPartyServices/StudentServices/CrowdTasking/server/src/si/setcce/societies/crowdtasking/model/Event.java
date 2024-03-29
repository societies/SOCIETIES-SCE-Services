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
package si.setcce.societies.crowdtasking.model;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Index;
import si.setcce.societies.crowdtasking.Util;
import si.setcce.societies.crowdtasking.api.RESTful.impl.CommentAPI;
import si.setcce.societies.crowdtasking.api.RESTful.impl.SpaceAPI;
import si.setcce.societies.crowdtasking.api.RESTful.impl.UsersAPI;
import si.setcce.societies.crowdtasking.model.dao.CommunityDAO;
import si.setcce.societies.crowdtasking.model.dao.MeetingDAO;
import si.setcce.societies.crowdtasking.model.dao.TaskDao;

import java.util.Date;
import java.util.List;

/**
 * Describe your class here...
 *
 * @author Simon Jureša
 */
@Entity
public class Event {
    @Id
    private Long id;
    private EventType type;
    private Ref<CTUser> userRef;
    @Index
    private Ref<CollaborativeSpace> collaborativeSpaceRef;
    @Index
    private List<Ref<Community>> communityRefs;
    private Ref<Task> taskRef;
    @Ignore
    private Task task;
    private Ref<Comment> commentRef;
    @Index
    private Date eventDate;
    private String eventText;
    private String eventTextHTML;
    @Ignore
    private CTUser user;
    private Ref<Meeting> meetingRef;
    @Ignore
    private Meeting meeting;
    @Ignore
    private Community community;
    @Index
    private List<String> communityJids;

    public Event() {
    }

    public Event(EventType type, CTUser user, List<Ref<Community>> communityRefs,
                 Ref<CollaborativeSpace> collaborativeSpaceRef, Long taskId, Long commentId,
                 Date eventDate, Meeting meeting, Community community, List<String> communityJids) {

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
        this.community = community;
        this.communityJids = communityJids;
//        task = null;
        if (taskRef != null) {
            task = TaskDao.loadTask(taskRef);
        }
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
        return " at " + Util.formatDate(eventDate);
    }

    public Ref<CTUser> getUserRef() {
        return userRef;
    }

    public Ref<Meeting> getMeetingRef() {
        return meetingRef;
    }

    private void setEventText() {
        String commentOwner;

        try {
            String userName = user.getUserName();
            switch (type) {
                case ACCOUNT_CREATED:
                    eventText = userName + " created a new Crowd Tasking account " + when(eventDate);
                    eventTextHTML = eventText;
                    break;
                case COMMUNITY_CREATED:
                    eventText = userName + " created community '" + community.getName() + "'" + when(eventDate);
                    eventTextHTML = userName + " created community " + Util.communityHTMLLinkRelative(community) + when(eventDate);
                    break;
                case CREATE_TASK:
                    eventText = userName + " created a new task '" + task.getTitle() + "'" + when(eventDate);
                    eventTextHTML = userName + " created the new task " + Util.taskHTMLLinkRelative(task) + when(eventDate);
                    break;
                case TASK_COMMENT:
                    eventText = userName + " commented on '" + task.getTitle() + "'" + when(eventDate);
                    eventTextHTML = userName + " commented on " + Util.taskHTMLLinkRelative(task) + when(eventDate);
                    break;
/*
                case EXECUTE_TASK:
                    eventText = userName + " started task execution of " + task.getTitle() + when(eventDate);
                    eventTextHTML = userName + " started task execution of " + Util.taskHTMLLinkRelative(task) + when(eventDate);
                    break;
*/
                case FINALIZE_TASK:
                    eventText = userName + " finalized task '" + task.getTitle() + "'" + when(eventDate);
                    eventTextHTML = userName + " finalized task " + Util.taskHTMLLinkRelative(task) + when(eventDate);
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
                    eventText = userName + " liked the task '" + task.getTitle() + "'" + when(eventDate);
                    eventTextHTML = userName + " liked the task " + Util.taskHTMLLinkRelative(task) + when(eventDate);
                    break;
                case UNLIKE_TASK:
                    eventText = userName + " unliked the task' " + task.getTitle() + "'" + when(eventDate);
                    eventTextHTML = userName + " unliked the task " + Util.taskHTMLLinkRelative(task) + when(eventDate);
                    break;
                case LIKE_COMMENT:
                    commentOwner = CommentAPI.getCommentById(getCommentId()).getOwner().getUserName();
                    eventText = userName + " liked " + commentOwner + "'s comment on the task '" + task.getTitle() + "'" + when(eventDate);
                    eventTextHTML = userName + " liked " + commentOwner + "'s comment on the task " + Util.taskHTMLLinkRelative(task) + when(eventDate);
                    break;
                case UNLIKE_COMMENT:
                    commentOwner = CommentAPI.getCommentById(getCommentId()).getOwner().getUserName();
                    eventText = userName + " unliked " + commentOwner + "'s comment on the task '" + task.getTitle() + "'" + when(eventDate);
                    eventTextHTML = userName + " unliked " + commentOwner + "'s comment on the task " + Util.taskHTMLLinkRelative(task) + when(eventDate);
                    break;
                case NEW_MEETING:
                    eventText = userName + " created a new meeting '" + meeting.getSubject() + "' in " + meeting.getCollaborativeSpace().getName() + when(eventDate);
                    eventTextHTML = userName + " created a meeting " + Util.taskHTMLLinkRealtive(meeting, task) + " in " + meeting.getCollaborativeSpace().getName() + when(eventDate);
                    break;
                case SHOW_TASK_ON_PD:
                    eventText = userName + " showed the task '" + task.getTitle() + "'" + " on public display" + when(eventDate);
                    eventTextHTML = userName + " showed task " + Util.taskHTMLLinkRelative(task) + " on public display" + when(eventDate);
                    break;
                case PICK_TASK_FROM_PD:
                    eventText = userName + " picked the task '" + task.getTitle() + "'" + " from public display" + when(eventDate);
                    eventTextHTML = userName + " picked task " + Util.taskHTMLLinkRelative(task) + " from public display" + when(eventDate);
                    break;
                case REQUEST_TO_JOIN_COMMUNITY:
                    eventText = userName + " requested to join the community '" + community.getName() + "'" + when(eventDate);
                    eventTextHTML = userName + " requested to join the community " + Util.communityHTMLLinkRelative(community) + when(eventDate);
                    break;
                case JOINED_COMMUNITY:
                    eventText = userName + " joined the community '" + community.getName() + "'" + when(eventDate);
                    eventTextHTML = userName + " joined the community " + Util.communityHTMLLinkRelative(community) + when(eventDate);
                    break;
                default:
                    break;
            }
        } catch (NullPointerException e) {
        }
    }

    public void convertEventText() {
        String commentOwner;
        Meeting meeting = null;

        user = UsersAPI.getUser(userRef);
        if (meetingRef != null) {
            meeting = MeetingDAO.loadMeeting(meetingRef.getKey().getId());
        }
        if (taskRef != null) {
            task = TaskDao.loadTask(taskRef);
        }
        if (communityRefs != null) {
            community = CommunityDAO.loadCommunity(communityRefs.get(0).getKey().getId());
        }
        setEventText();
    }

    public List<String> getCommunityJids() {
        return communityJids;
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