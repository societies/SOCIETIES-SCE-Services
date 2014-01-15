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
import si.setcce.societies.crowdtasking.NotificationsSender;
import si.setcce.societies.crowdtasking.api.RESTful.ILikeAPI;
import si.setcce.societies.crowdtasking.model.CTUser;
import si.setcce.societies.crowdtasking.model.Comment;
import si.setcce.societies.crowdtasking.model.Like;
import si.setcce.societies.crowdtasking.model.Task;
import si.setcce.societies.crowdtasking.model.dao.TaskDao;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;

import static si.setcce.societies.crowdtasking.model.dao.OfyService.ofy;

/**
 * Describe your class here...
 *
 * @author Simon Jureša
 */
@Path("/like/{querytype}")
public class LikeAPI implements ILikeAPI {
    @Override
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getLike(@PathParam("querytype") String querytype,
                          @QueryParam("vwTaskId") Long taskId,
                          @QueryParam("commentId") Long commentId,
                          @Context HttpServletRequest request) {

        CTUser user = UsersAPI.getLoggedInUser(request.getSession());
        Like like = null;
        if ("task".equalsIgnoreCase(querytype)) {
            like = getLike4Task(user.getId(), taskId);
        }
        if ("comment".equalsIgnoreCase(querytype)) {
            like = getLike4Comment(user.getId(), commentId);
        }

        Gson gson = new Gson();
        if (like != null) {
            return gson.toJson(true);
        }
        return gson.toJson(false);
    }

    @Override
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response setLike(@PathParam("querytype") String querytype,
                            @FormParam("vwTaskId") Long taskId,
                            MultivaluedMap<String, String> formParams,
                            @Context HttpServletRequest request) {

        CTUser user = UsersAPI.getLoggedInUser(request.getSession());
        if ("comment".equalsIgnoreCase(querytype)) {
            List<String> sliders = formParams.get("slider");
            if (sliders == null) {
                return Response.ok().build();
            }
            Comment comment = null;
            for (String slider : sliders) {
                if (slider.startsWith("off")) {
                    Long commentId = new Long(slider.substring(4));
                    Like like = getLike4Comment(user.getId(), commentId);
                    // dislike
                    if (like != null) {
                        ofy().delete().entity(like);
                        CommentAPI.changeCommentScore(commentId, -1);
                        if (comment == null) {
                            comment = CommentAPI.getCommentById(commentId);
                        }
                        TaskDao.changeTaskScore(comment.getTask().getId(), -1L);    // TODO: fix this
                        changeUserKarma(comment.getOwner(), -1);
                        EventAPI.logUnlikeComment(comment, new Date(), user);
                    }
                }
                if (slider.startsWith("on")) {
                    // slider je nekaj kot on-344
                    Long commentId = new Long(slider.substring(3));
                    Like like = getLike4Comment(user.getId(), commentId);
                    // like
                    if (like == null) {
                        like = new Like(user.getId(), null, commentId);
                        ofy().save().entity(like);
                        CommentAPI.changeCommentScore(commentId, 1);
                        if (comment == null) {
                            comment = CommentAPI.getCommentById(commentId);
                        }
                        TaskDao.changeTaskScore(comment.getTask().getId(), 1L);    // TODO: fix this
                        changeUserKarma(comment.getOwner(), 1);
                        EventAPI.logLikeComment(comment, new Date(), user);
                        NotificationsSender.commentLiked(comment);
                    }
                }
            }
        }

        if ("task".equalsIgnoreCase(querytype)) {
            Like like = getLike4Task(user.getId(), taskId);
            // like
            if (like == null) {
                like = new Like(user.getId(), taskId, null);
                ofy().save().entity(like);
                // increase task's score
                Task task = TaskDao.getTaskById(taskId);
                TaskDao.changeTaskScore(task, user.getKarma());
                // increase owner's karma
                changeUserKarma(UsersAPI.getUserById(task.getOwnerId()), 1);
                EventAPI.logLikeTask(task, new Date(), user);
                NotificationsSender.taskLiked(task);
            }
            // dislike
            else {
                ofy().delete().type(Like.class).id(like.getId());
                // decrease task's score
                Task task = TaskDao.getTaskById(taskId);
                // ??? to je lahko več kot je bil plus
                TaskDao.changeTaskScore(task, -user.getKarma());
                // decrease owner's karma
                changeUserKarma(UsersAPI.getUserById(task.getOwnerId()), -1);
                EventAPI.logUnlikeTask(task, new Date(), user);
            }
        }
        return Response.ok().build();
    }

    private void changeUserKarma(CTUser owner, int change) {
        owner.setKarma(owner.getKarma() + change);
        ofy().save().entity(owner);
    }

    public static Like getLike4Task(Long userId, Long taskId) {
        return ofy().load().type(Like.class).filter("userId", userId).filter("taskId", taskId).first().get();
    }

    public static Like getLike4Comment(Long userId, Long commentId) {
        return ofy().load().type(Like.class).filter("userId", userId).filter("commentId", commentId).first().get();
    }
}
