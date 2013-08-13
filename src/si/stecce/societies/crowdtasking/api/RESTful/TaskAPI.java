package si.stecce.societies.crowdtasking.api.RESTful;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

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
import javax.ws.rs.core.Response.Status;

import si.stecce.societies.crowdtasking.NotificationsSender;
import si.stecce.societies.crowdtasking.api.RESTful.json.TaskJS;
import si.stecce.societies.crowdtasking.model.CTUser;
import si.stecce.societies.crowdtasking.model.Comment;
import si.stecce.societies.crowdtasking.model.Tag;
import si.stecce.societies.crowdtasking.model.TagTask;
import si.stecce.societies.crowdtasking.model.Task;
import si.stecce.societies.crowdtasking.model.dao.CommunityDAO;
import si.stecce.societies.crowdtasking.model.dao.TagTaskDao;
import si.stecce.societies.crowdtasking.model.dao.TaskDao;

import com.google.gson.Gson;
import com.googlecode.objectify.cmd.Query;

@Path("/task")
public class TaskAPI {
    private static final Logger log = Logger.getLogger(TaskAPI.class.getName());

	public TaskAPI() {
	}

	@GET
	@Produces({MediaType.APPLICATION_JSON })
	public String getTask(@DefaultValue("0") @QueryParam("id") Long id,
			@Context HttpServletRequest request) {
		CTUser user = UsersAPI.getLoggedInUser(request.getSession());
	    if (user == null) {
	    	return "";
	    }
		Gson gson = new Gson();
		// get task
		if (id != 0) {
			Task task = null;
			task = TaskDao.getTaskById4User(id, user);
			if (task != null) {
				TaskDao.setTransientTaskParams(user, task);
				return gson.toJson(new TaskJS(task, user));
			}
			return null;
		}
		
		// get tasks - deprecated
		Query<Task> q = TaskDao.getTasks();
		ArrayList<Task> list = new ArrayList<Task>();
		for (Task task: q) {
			TaskDao.setTransientTaskParams(user, task);
			list.add(task);
		}
		log.warning("we shouldn't be here!");
		return gson.toJson(list);
	}
	
	public Response newTask(String title, String description, String taskDate,
			Long spaceId, List<Long> communities, String tagsString, CTUser user, List<String> communityJids) throws IOException, URISyntaxException {

		DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
		Date dueDate;
		
		try {
			dueDate = formatter.parse(taskDate);
		} catch (ParseException e) {
			log.severe(e.getLocalizedMessage());
			dueDate = new Date();
		}
		
		Gson gson = new Gson();
		String[] tags = gson.fromJson(tagsString, String[].class);
		List<Tag> tagList = getTagListFromStringList(tags);
		Map<String, Tag> tagMap = TagAPI.getTagsMap();
		for (int i=0; i<tags.length; i++) {
			Tag tag = tagMap.get(tags[i].trim());
			if (tag == null) {
				tag = new Tag(tags[i].trim());
			}
			tag.setTagFrequency(tag.getTagFrequency()+1);
		}
		
	    Task task = new Task(title, description, dueDate, user.getId(), user.getFirstName()+" "+user.getLastName(), communities, Arrays.asList(tags), communityJids);
	    task.setScore(user.getKarma());
	    TaskDao.save(task);
		TagAPI.updateTags(tagList);

		ArrayList<TagTask> tagTasks = new ArrayList<TagTask>();
		for (Tag tag:tagList) {
			tagTasks.add(new TagTask(tag.getTagName(), task.getId()));
		}
		TagTaskDao.saveTagTasks(tagTasks);

		EventAPI.logCreateTask(task, user);
		Set<CTUser> involvedUsers = CommunityDAO.loadMembers(communities);
		NotificationsSender.taskCreated(task, involvedUsers);

		return Response.ok().build();
	}
	
	private List<Tag> getTagListFromStringList(String[] tags) {
		ArrayList<Tag> tagList = new ArrayList<Tag>();
		Map<String, Tag> tagMap = TagAPI.getTagsMap();
		for (int i=0; i<tags.length; i++) {
			Tag tag = tagMap.get(tags[i].trim());
			if (tag == null) {
				tag = new Tag(tags[i].trim());
			}
			tag.setTagFrequency(tag.getTagFrequency()+1);
			tagList.add(tag);
		}
		return tagList;
	}

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response postTask(
			@FormParam("vwTaskId") String taskId,
			@FormParam("activeComments") List<Long> activeComments,
			@FormParam("inform") List<String> informChannels,
			@FormParam("messageBody") String message,
			@FormParam("action") String action,
			@FormParam("title") String title,
			@FormParam("description") String description,
			@DefaultValue("") @FormParam("taskDate") String taskDate,
			@FormParam("spaceId") Long spaceId,
			@FormParam("taskCommunity") List<Long> communities,
			@FormParam("taskCommunityJids") List<String> communityJids,
			@FormParam("taskTags") String tagsString,
			@Context HttpServletRequest request)  throws IOException, URISyntaxException {
			
		CTUser user = UsersAPI.getLoggedInUser(request.getSession());
		if ("create".equalsIgnoreCase(action)) {
			if (title == null || "".equalsIgnoreCase(title)) {
				return Response.status(Status.NOT_ACCEPTABLE).entity("Title is required.").type("text/plain").build();
			}
			return newTask(title, description, taskDate, spaceId, communities, tagsString, user, communityJids);
		}
		if ("execute".equalsIgnoreCase(action)) {
			if (activeComments == null || activeComments.isEmpty()) {
				return Response.status(Status.NOT_ACCEPTABLE).entity("Select at least one relevant comment.").type("text/plain").build();
			}
			return startTaskExecution(taskId, activeComments, informChannels, message, user);
		}
		if ("finalize".equalsIgnoreCase(action)) {
			return finalizeTask(taskId, user);
		}
		
		return Response.ok().build();
	}
	
	private Response startTaskExecution(String taskId, List<Long> activeComments, List<String> informChannels, String message, CTUser user) {
		startExecution(taskId, activeComments, informChannels, message);
		EventAPI.logExecuteTask(new Long(taskId), new Date(), user);
		return Response.ok().build();
	}
	
	private void startExecution(String taskId, List<Long> activeComments, List<String> informChannels, String message) {
		List<Comment> comments = CommentAPI.getComments(activeComments);
		Set<Long> involvedUsers = new HashSet<Long>();
		for (Comment comment:comments) {
			involvedUsers.add(comment.getOwner().getId());
			comment.setExecution(true);
		}
		Task task = TaskDao.getTaskById(new Long(taskId));
		task.setInvolvedUsers(new ArrayList<Long>(involvedUsers));
		task.setInformChannels(informChannels);
		task.setExecuteMessage(message);
		task.setStatus("inprogress");
		TaskDao.save(task);
		CommentAPI.saveComments(comments);
		// inform involved users
		involvedUsers.add(task.getOwnerId());
		NotificationsSender.executionStarted(task, involvedUsers);
	}
	
	private Response finalizeTask(String taskId, CTUser user) {
		// get active comments
		Task task = TaskDao.getTaskById(new Long(taskId));
		task.setStatus("finished");
		ofy().save().entity(task);
		EventAPI.logFinalizeTask(new Long(taskId), new Date(), user);
		
		return Response.ok().build();
	}
}
