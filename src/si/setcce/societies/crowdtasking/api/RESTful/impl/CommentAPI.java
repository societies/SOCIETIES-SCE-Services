package si.setcce.societies.crowdtasking.api.RESTful.impl;

import com.google.gson.Gson;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.NotFoundException;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.cmd.Query;
import si.setcce.societies.crowdtasking.NotificationsSender;
import si.setcce.societies.crowdtasking.api.RESTful.ICommentAPI;
import si.setcce.societies.crowdtasking.api.RESTful.json.CommentJS;
import si.setcce.societies.crowdtasking.model.CTUser;
import si.setcce.societies.crowdtasking.model.Comment;
import si.setcce.societies.crowdtasking.model.Task;
import si.setcce.societies.crowdtasking.model.dao.TaskDao;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import static si.setcce.societies.crowdtasking.model.dao.OfyService.ofy;

@Path("/comment")
public class CommentAPI implements ICommentAPI {

    @Override
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getComment(@QueryParam("taskId") Long taskId,
                             @Context HttpServletRequest request) {

        ArrayList<CommentJS> list = new ArrayList<>();
        Gson gson = new Gson();

        if (taskId == null) {
            return gson.toJson(list);
        }

        Query<Comment> q;
        // get comments for task
        q = ofy().load().type(Comment.class).filter("taskRef", Ref.create(Key.create(Task.class, taskId))).order("posted");
        CTUser user = UsersAPI.getLoggedInUser(request.getSession());
        for (Comment comment : q) {
            CommentJS commentJS = new CommentJS(comment, user);
            if (user.getId().longValue() == comment.getOwner().getId().longValue()) {
                commentJS.setMyComment(true);
            } else {
                commentJS.setLiked(LikeAPI.getLike4Comment(user.getId(), comment.getId()) != null);
                commentJS.setMyComment(false);
            }
            list.add(commentJS);
        }

        return gson.toJson(list);
    }

    @Override
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response newComment(@FormParam("vwTaskId") Long taskId,
                               @FormParam("commentText") String commentText,
                               @FormParam("execution") boolean execution,
                               @Context HttpServletRequest request,
                               @Context HttpServletResponse servletResponse) throws IOException, URISyntaxException {

        if (commentText == null || "".equalsIgnoreCase(commentText)) {
            return Response.status(Status.BAD_REQUEST).build();
        }

        CTUser user = UsersAPI.getLoggedInUser(request.getSession());

        Comment comment = new Comment(taskId, user, commentText);
//		comment.setExecution(execution);
        Long commentId = saveComment(comment);
        Task task = TaskDao.getTaskById(taskId);
        task.addInvolvedUser(user.getId());
        TaskDao.save(task);

        EventAPI.logTaskComment(taskId, commentId, comment.getPosted(), user);
//        Map<Long, CTUser> usersMap = UsersAPI.getUsersMap(task.getInvolvedUsers().toArray(new Long[task.getInvolvedUsers().size()]));
        NotificationsSender.commentOnTaskIParticipate(comment.getTask(), user.getId());
        return Response.ok().build();
    }

    public static Long saveComment(Comment comment) {
        return ofy().save().entity(comment).now().getId();
    }

/*
    public static void saveComments(List<Comment> comments) {
        ofy().save().entities(comments);
    }

    public static List<Comment> getComments(List<Long> commentsIds) {
        Map<Long, Comment> commentsMap = ofy().load().type(Comment.class).ids(commentsIds);

        List<Comment> comments = new ArrayList<Comment>();
        for (Long id : commentsIds) {
            comments.add(commentsMap.get(id));
        }
        return comments;
    }

    public static Query<Comment> getComments() {
        return ofy().load().type(Comment.class);
    }
*/

    public static void changeCommentScore(Long commentId, int change) {
        Comment comment = getCommentById(commentId);
        if (comment != null) {
            changeCommentScore(comment, change);
        }
    }

    public static void changeCommentScore(Comment comment, int change) {
        comment.setScore(comment.getScore() + change);
        saveComment(comment);
    }

    public static Comment getCommentById(Long id) {
        Comment comment = null;
        try {
            comment = ofy().load().type(Comment.class).id(id).get();
        } catch (NotFoundException ignored) {

        }
        return comment;
    }

    public static Query<Comment> findCommentsByUser(Long userId) {
        return ofy().load().type(Comment.class).filter("owner", userId);
    }
}
