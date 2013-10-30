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

import com.google.gson.Gson;
import com.googlecode.objectify.Key;
import si.stecce.societies.crowdtasking.NotificationsSender;
import si.stecce.societies.crowdtasking.api.RESTful.IMeetingAPI;
import si.stecce.societies.crowdtasking.api.RESTful.json.MeetingDetails4CSignJS;
import si.stecce.societies.crowdtasking.api.RESTful.json.MeetingJS;
import si.stecce.societies.crowdtasking.api.RESTful.json.TaskJS;
import si.stecce.societies.crowdtasking.model.CTUser;
import si.stecce.societies.crowdtasking.model.CollaborativeSign;
import si.stecce.societies.crowdtasking.model.Meeting;
import si.stecce.societies.crowdtasking.model.Task;
import si.stecce.societies.crowdtasking.model.dao.MeetingDAO;
import si.stecce.societies.crowdtasking.model.dao.TaskDao;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static si.stecce.societies.crowdtasking.model.dao.OfyService.ofy;

@Path("/meeting/{querytype}")
public class MeetingAPI implements IMeetingAPI {
    private static final int MEETINGS_ON_PD = 10;

    @Override
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getMeeting(@PathParam("querytype") String querytype,
                             @QueryParam("id") Long meetingId,
                             @QueryParam("csId") Long csId) {
        if ("data".equalsIgnoreCase(querytype)) {
            if (meetingId == null) {
                return "Missing id.";
            }
            Gson gson = new Gson();
            // get meeting
            Meeting meeting = null;
            meeting = MeetingDAO.loadMeeting(meetingId);
            return gson.toJson(new MeetingJS(meeting));
        }
        if ("communitysign".equalsIgnoreCase(querytype)) {
            CollaborativeSign collaborativeSign = getCollaborativeSign();
            if (collaborativeSign.getMeetingId() != null) {
                Meeting meeting = MeetingDAO.loadMeeting(collaborativeSign.getMeetingId());
                Gson gson = new Gson();
                return gson.toJson(new MeetingDetails4CSignJS(meeting));
            }
        }
        if ("cs".equalsIgnoreCase(querytype)) {
            return getMeetingsForCS(csId);
        }
        return Response.status(Status.BAD_REQUEST).entity("Request not understood.").type("text/plain").build().toString();
    }

    private String getMeetingsForCS(Long csId) {
        Gson gson = new Gson();
        ArrayList<MeetingJS> meetingsJS = new ArrayList<MeetingJS>();
        for (Meeting meeting : MeetingDAO.getMeetingsForCS(csId, MEETINGS_ON_PD)) {
            meetingsJS.add(new MeetingJS(meeting));
        }
        return gson.toJson(meetingsJS);
    }

    @Override
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response newMeeting(@PathParam("querytype") String querytype,
                               @FormParam("taskId") Long taskId,
                               @FormParam("meetingSubject") String meetingSubject,
                               @FormParam("meetingDescription") String meetingDescription,
                               @FormParam("meetingCS") Long csId,
                               @FormParam("taskStart") String taskStart,
                               @FormParam("taskEnd") String taskEnd,
                               @FormParam("meetingIdToSign") String meetingIdToSignStr,
                               @FormParam("downloadUrl") String downloadUrl,
                               @Context HttpServletRequest request) {

        Long meetingIdToSign;
        CTUser user = UsersAPI.getLoggedInUser(request.getSession());
        try {
            meetingIdToSign = Long.parseLong(meetingIdToSignStr);
        } catch (Exception e) {
            meetingIdToSign = null;
        }
        if ("".equals(meetingSubject)) {
            return Response.status(Status.BAD_REQUEST).entity("Subject is required.").type("text/plain").build();
        }
        if ("create".equalsIgnoreCase(querytype)) {
            Task task = TaskDao.getTaskById(taskId);
            DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            Date startTime = null;
            try {
                startTime = formatter.parse(taskStart);
            } catch (ParseException e) {
                return Response.status(Status.BAD_REQUEST).entity("Start time error.").type("text/plain").build();
            }
            if (startTime.before(new Date())) {
                return Response.status(Status.BAD_REQUEST).entity("Strat time is in the past.").type("text/plain").build();
            }
            Date endTime = null;
            try {
                endTime = formatter.parse(taskEnd);
            } catch (ParseException e) {
                return Response.status(Status.BAD_REQUEST).entity("End time error.").type("text/plain").build();
            }
            if (startTime.after(endTime)) {
                return Response.status(Status.BAD_REQUEST).entity("Strat time is after end time.").type("text/plain").build();
            }

            Meeting meeting = new Meeting(meetingSubject, meetingDescription, csId, startTime, endTime, user, task.getInvolvedUsers());
            Key<Meeting> meetingKey = MeetingDAO.saveMeeting(meeting);
            task.addMeeting(meetingKey);
            TaskDao.save(task);
            NotificationsSender.newMeeting(meeting, task);
            TaskDao.setTransientTaskParams(user, task);
            EventAPI.logNewMeeting(task, meeting, new Date(), user);
            // because of Ref<?> value has not been initialized
            task = TaskDao.getTaskById(task.getId());
            Gson gson = new Gson();
            return Response.ok().entity(gson.toJson(new TaskJS(task, user))).build();
        }
        if ("communitysign".equalsIgnoreCase(querytype)) {
            Meeting meeting = null;
            try {
                meeting = MeetingDAO.loadMeeting(meetingIdToSign);
            } catch (Exception e) {
                return Response.status(Status.BAD_REQUEST).entity("Wrong meeting id.").type("text/plain").build();
            }
            CollaborativeSign collaborativeSign = getCollaborativeSign();
            collaborativeSign.setMeetingId(meetingIdToSign);
            setCollaborativeSign(collaborativeSign);
            if (downloadUrl == null || "".equalsIgnoreCase(downloadUrl)) {
                return Response.ok().entity("The meeting is set.").build();
            }
            meeting.setDownloadUrl(downloadUrl);
            NotificationsSender.meetingIsReadyToBeSigned(meeting);
            return Response.ok().entity("The meeting minutes are ready to be signed.").build();

        }
        return Response.status(Status.BAD_REQUEST).entity("Request not understood.").type("text/plain").build();
    }

    public static CollaborativeSign getCollaborativeSign() {
        CollaborativeSign collaborativeSign = ofy().load().type(CollaborativeSign.class).first().get();
        if (collaborativeSign == null) {
            collaborativeSign = new CollaborativeSign();
        }
        return collaborativeSign;
    }

    public static Key<CollaborativeSign> setCollaborativeSign(CollaborativeSign collaborativeSign) {
        return ofy().save().entity(collaborativeSign).now();
    }

}
