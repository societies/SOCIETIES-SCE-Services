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
package si.stecce.societies.crowdtasking.api.RESTful;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import si.stecce.societies.crowdtasking.api.RESTful.json.TaskJS;
import si.stecce.societies.crowdtasking.model.CTUser;
import si.stecce.societies.crowdtasking.model.Task;
import si.stecce.societies.crowdtasking.model.TaskInterestScoreComparator;
import si.stecce.societies.crowdtasking.model.dao.TaskDao;

import com.google.gson.Gson;
import com.googlecode.objectify.cmd.Query;

/**
 * Nisem prepričan, če je to pravi pristop - TaskAPI in TasksAPI. TasksAPI je namenjen za "tasks queries only".
 * Za my tasks, followed tasks, interesting tasks, search...
 * 
 * /rest/tasks/mytasks
 * /rest/tasks/followed
 * /rest/tasks/interesting
 * ...
 *
 * @author Simon Jureša
 *
 */
@Path("/tasks/{querytype}")
public class TasksAPI {
	private static final int HIGHEST_RATED_NUM = 5;

	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public String getTasks(@PathParam("querytype") String querytype, 
			@QueryParam("searchString") String searchString,
			@QueryParam("communityId") Long communityId,
			@Context HttpServletRequest request) {
		CTUser user = UsersAPI.getLoggedInUser(request.getSession());
		if ("my".equalsIgnoreCase(querytype)) {
			return getMyTasks(user);
		}
		if ("followed".equalsIgnoreCase(querytype)) {
			return getFollowedTasks();
		}
		if ("interesting".equalsIgnoreCase(querytype)) {
			return getInerestingTasks(user);
		}
		if ("search".equalsIgnoreCase(querytype)) {
			return getSearchedTasks(searchString);
		}
		if ("cs".equalsIgnoreCase(querytype)) {
			return getInerestingTasksForCS(communityId, user);
		}
		return null;
	}

	/**
	 * @param communityId
	 * @return
	 */
	private String getInerestingTasksForCS(Long communityId, CTUser user) {
		Query<Task> highestRatedCommunityTasks = TaskDao.getHighestRatedTasksForCommunity(communityId, HIGHEST_RATED_NUM);
		ArrayList<Task> list = new ArrayList<Task>();
		for (Task task: highestRatedCommunityTasks) {
			TaskDao.setTransientTaskParams(user, task);
			list.add(task);
		}
		/**
		 if (list.size() < 5) {	// tasks outside community are not visible anymore http://research.setcce.si:8080/societies/ticket/74
			Query<Task> highestRatedTasks = TaskDao.getHighestRatedTasks(5);
			for (Task task: highestRatedTasks) {
				if (taskNotInList(task.getId(), list)) {
					TaskDao.setTransientTaskParams(user, task);
					list.add(task);
				}
				if (list.size() == 5) break;
			}
		}*/
		ArrayList<TaskJS> tasksJS = new ArrayList<TaskJS>();
		for (Task task:list) {
			tasksJS.add(new TaskJS(task, null));
		}
		Gson gson = new Gson();
		return gson.toJson(tasksJS);
	}

	/**
	 * @param task
	 * @param list
	 * @return
	 * /
	private boolean taskNotInList(Long taskId, ArrayList<Task> list) {
		for (Task task:list) {
			if (taskId.longValue() == task.getId().longValue())
				return false;
		}
		return true;
	}
*/
	/**
	 * @param searchString
	 * @return
	 */
	private String getSearchedTasks(String searchString) {
		return searchString;
	}

	/**
	 * @return
	 */
	private String getInerestingTasks(CTUser user) {
		// get tasks
		Gson gson = new Gson();
		Collection<Task> tasksByInterest = TaskDao.getTasksByInterests(user);
		// get friend's related tasks
		// get similar user's related tasks
		// get highest rated task
		List<Task> highestRatedTasks = TaskDao.getHighestRatedTasks4User(user);
		List<Task> list = rateTaskByInterest(tasksByInterest, highestRatedTasks);
		ArrayList<TaskJS> tasksJS = new ArrayList<TaskJS>();
		for (Task task:list) {
			TaskDao.setTransientTaskParams(user, task);
			tasksJS.add(new TaskJS(task, user));
		}
		return gson.toJson(tasksJS);
	}

	private List<Task> rateTaskByInterest(Collection<Task> tasksByInterest, 
			List<Task> highestRatedTasks) {
		HashMap<Long, Task> taskMap = new HashMap<Long, Task>();
		long INTEREST_POINTS = 10L;
		int NUM_TASKS = 500;	// TODO:  fix this, ther is no more interesting task, just tasks in my communities
		
		if (highestRatedTasks != null) {
			for (Task task: highestRatedTasks) {
				task.setInterestScore(task.getScore());
				taskMap.put(task.getId(), task);
			}
		}
		for (Task task: tasksByInterest) {
			Task task1 = taskMap.get(task.getId());
			if (task1 != null) {
				task1.addInterestScore(INTEREST_POINTS);
			}
			else {
				task.addInterestScore(INTEREST_POINTS);	// samo en interest per tag, povečat score, če jih je več?
				taskMap.put(task.getId(), task);
			}
		}
		
		List<Task> taskList = new ArrayList<Task>(taskMap.values());
		Collections.sort(taskList, new TaskInterestScoreComparator());

		ArrayList<Task> list = new ArrayList<Task>();
		int last = taskList.size()-1;
		for (int i=0; i<NUM_TASKS && i<=last; i++) {
			Task task = taskList.get(i);
			list.add(task);
		}
		return list;
	}

	/**
	 * @return
	 */
	private String getFollowedTasks() {
		return null;
	}

	/**
	 * @return
	 */
	private String getMyTasks(CTUser user) {
		Query<Task> qt = TaskDao.findTasksByUser(user.getId());
		Gson gson = new Gson();
		ArrayList<TaskJS> list = new ArrayList<TaskJS>();
		for (Task task:qt) {
			TaskDao.setTransientTaskParams(user, task);
			list.add(new TaskJS(task, user));
		}
		return gson.toJson(list);
	}
}
