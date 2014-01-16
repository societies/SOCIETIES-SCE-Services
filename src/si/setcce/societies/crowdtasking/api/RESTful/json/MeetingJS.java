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
package si.setcce.societies.crowdtasking.api.RESTful.json;

import com.googlecode.objectify.Ref;
import si.setcce.societies.crowdtasking.api.RESTful.impl.UsersAPI;
import si.setcce.societies.crowdtasking.model.CTUser;
import si.setcce.societies.crowdtasking.model.CollaborativeSpace;
import si.setcce.societies.crowdtasking.model.Meeting;
import si.setcce.societies.crowdtasking.model.MeetingStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Describe your class here...
 *
 * @author Simon Jureša
 */
@SuppressWarnings("unused")
public class MeetingJS {
    private Long id;
    private String subject;
    private String description;
    private CollaborativeSpace cs;
    private Date startTime, endTime, created;
    private String organizer;
    private MeetingStatus meetingStatus;
    private String userStatus;
    private List<UserJS> attendees;
    public List<UserJS> invitedUser;

    public MeetingJS(Meeting meeting, Long loggedInUserId) {
        init(meeting);
        setAttendees(meeting, loggedInUserId);
    }

    public MeetingJS(Meeting meeting) {
        init(meeting);
        setAttendees(meeting, (long) 0);
    }

    private void init(Meeting meeting) {
        this.id = meeting.getId();
        this.subject = meeting.getSubject();
        this.description = meeting.getDescription();
        this.cs = meeting.getCollaborativeSpace();
        this.startTime = meeting.getStartTime();
        this.endTime = meeting.getEndTime();
        this.created = meeting.getCreated();
        this.organizer = meeting.getOrganizer() == null ? "" : meeting.getOrganizer().getUserName();
        invitedUser = new ArrayList<>();
        for (Ref<CTUser> ctUserRef : meeting.getInvitedUser()) {
            CTUser ctUser = UsersAPI.getUser(ctUserRef);
            invitedUser.add(new UserJS(ctUser, 0L));
        }
    }

    private void setAttendees(Meeting meeting, Long loggedInUserId) {
        attendees = new ArrayList<>();
        for (CTUser ctUser : meeting.getAttendes()) {
            attendees.add(new UserJS(ctUser, 0L));
            if (ctUser.getId().longValue() == loggedInUserId.longValue()) {
                userStatus = "Checked in.";
            }
        }

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<UserJS> getAttendees() {
        return attendees;
    }

    public void setAttendees(List<UserJS> attendees) {
        this.attendees = attendees;
    }

    public List<UserJS> getInvitedUser() {
        return invitedUser;
    }

    public void setInvitedUser(List<UserJS> invitedUser) {
        this.invitedUser = invitedUser;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CollaborativeSpace getCs() {
        return cs;
    }

    public void setCs(CollaborativeSpace cs) {
        this.cs = cs;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public MeetingStatus getMeetingStatus() {
        return meetingStatus;
    }

    public void setMeetingStatus(MeetingStatus meetingStatus) {
        this.meetingStatus = meetingStatus;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }
}
