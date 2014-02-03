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
import si.setcce.societies.crowdtasking.model.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Describe your class here...
 *
 * @author Simon Jureša
 */
class MeetingMinuteJs {
    public Long userId;
    public Date timestamp;
    public String postedBy;
    public String text;
    public String picUrl;

    MeetingMinuteJs(MeetingMinute minute) {
        timestamp = new Date();
        this.text = minute.minute;
        CTUser user = UsersAPI.getUser(minute.userRef);
        userId = user.getId();
        postedBy = user.getUserName();
        picUrl = user.getPicUrl();
    }
}

public class MeetingJS {
    public Long id;
    public String subject;
    public String description;
    public CollaborativeSpace cs;
    public Date startTime, endTime, created;
    public String organizer;
    public MeetingStatus meetingStatus;
    public String userStatus;
    public List<BasicUserJS> attendees;
    public List<BasicUserJS> invitedUsers;
    public List<MeetingMinuteJs> meetingMinutes;

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
        invitedUsers = new ArrayList<>();
        this.meetingStatus = meeting.getMeetingStatus();
        for (Ref<CTUser> ctUserRef : meeting.getInvitedUsers()) {
            CTUser ctUser = UsersAPI.getUser(ctUserRef);
            invitedUsers.add(new BasicUserJS(ctUser));
        }
        meetingMinutes = new ArrayList<>();
        if (meeting.getMeetingMinutes() != null) {
            for (MeetingMinute minute : meeting.getMeetingMinutes()) {
                meetingMinutes.add(new MeetingMinuteJs(minute));
            }
        }
    }

    private void setAttendees(Meeting meeting, Long loggedInUserId) {
        attendees = new ArrayList<>();
        for (Ref<CTUser> ctUserRef : meeting.getAttendees()) {
            CTUser ctUser = UsersAPI.getUser(ctUserRef);
            attendees.add(new BasicUserJS(ctUser));
            if (ctUser.getId().longValue() == loggedInUserId.longValue()) {
                userStatus = "Checked in.";
            }
        }
    }
}
