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
package si.setcce.societies.crowdtasking.model.dao;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.NotFoundException;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.cmd.Query;
import si.setcce.societies.crowdtasking.model.CTUser;
import si.setcce.societies.crowdtasking.model.Community;
import si.setcce.societies.crowdtasking.model.Task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static si.setcce.societies.crowdtasking.model.dao.OfyService.ofy;

/**
 * Describe your class here...
 *
 * @author Simon Jureša
 */
public final class TaskDao {
    private static final int MAX_HIGHEST_RATED_TASKS = 50;

    private TaskDao() {
    }

    public static Task loadTask(Long id) {
        Task task = null;
        try {
            task = ofy().load().type(Task.class).id(id).get();
        } catch (NotFoundException ignored) {
        }
        return task;
    }

    /**
     * getTaskById4User return task only if user is allowed to see it
     *
     * @param user
     * @return
     */
    public static Task getTaskById4User(Long id, CTUser user) {
        Task task = null;
        try {
            task = loadTask(id);
            if (!taskIsVisibleToUser(task, user)) {
                return null;
            }
        } catch (NotFoundException ignored) {
        }
        return task;
    }

    private static boolean taskIsVisibleToUser(Task task, CTUser user) {
        List<Community> communities4User = CommunityDAO.loadCommunities4User(user);
        return taskIsVisibleToUser(task, communities4User);
    }

    private static boolean taskIsVisibleToUser(Task task, List<Community> communities4User) {
        List<Community> communities = task.getCommunities();
        if (communities == null) {
            return true;
        }
        for (Community community1 : communities) {
            for (Community community2 : communities4User) {
                try {
                    if (community1.getId().longValue() == community2.getId().longValue()) {
                        return true;
                    }
                } catch (NullPointerException ignored) {
                }
            }
        }
        return false;
    }

    public static Task loadTask(Ref<Task> taskRef) {
        return ofy().load().ref(taskRef).get();
    }

/*
    public static Query<Task> getHighestRatedTasks(int limit) {
        return ofy().load().type(Task.class).order("-score").limit(limit);
    }
*/

    public static List<Task> getHighestRatedTasks4User(CTUser user) {
        Query<Task> tasks = ofy().load().type(Task.class).order("-score").limit(MAX_HIGHEST_RATED_TASKS);
        List<Community> communities4User = CommunityDAO.loadCommunities4User(user);
        return filterVisibleToUser(tasks, communities4User);
    }

    private static List<Task> filterVisibleToUser(Query<Task> tasks, List<Community> communities4User) {
        List<Task> filteredTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (taskIsVisibleToUser(task, communities4User)) {
                filteredTasks.add(task);
            }
        }
        return filteredTasks;
    }

    public static Query<Task> getHighestRatedTasksForCommunity(Long communityId, int limit) {
        return ofy().load().type(Task.class).filter("communityRefs", Ref.create(Key.create(Community.class, communityId)))
                .order("-score").limit(limit);
    }

    public static Query<Task> getTasksInCommunities(List<Ref<Community>> communityRefs) {
        return ofy().load().type(Task.class).filter("communityRefs in", communityRefs);
    }

    public static Query<Task> getTasksInSocietiesCommunities(List<Ref<Community>> communityRefs) {
        return ofy().load().type(Task.class).filter("communityJidsRefs in", communityRefs);
    }

    public static Query<Task> getTasksInSocietiesCommunitiesJids(List<String> communityJids) {
        return ofy().load().type(Task.class).filter("communityJids in", communityJids);
    }

    public static Query<Task> getTasks(int limit) {
        return ofy().load().type(Task.class).limit(limit);
    }

    public static Query<Task> getTasks() {
        return ofy().load().type(Task.class);
    }

    public static List<Task> findTasksByUser(CTUser user) {
//		return ofy().load().type(Task.class).filter("ownerId", userId);

        Query<Task> tasks = ofy().load().type(Task.class).filter("ownerId", user.getId());
        List<Community> communities4User = CommunityDAO.loadCommunities4User(user);
        return filterVisibleToUser(tasks, communities4User);

    }

    public static Query<Task> findSocietiesTasksByUser(Long userId) {
        return ofy().load().type(Task.class).filter("ownerId", userId).filter(" !=", null);
    }

    public static Query<Task> findSocietiesTasks() {
        return ofy().load().type(Task.class).filter("communityJids !=", null);
    }

    public static Collection<Task> getTasksByInterests(CTUser user) {
        List<Long> taskIds = TagTaskDao.getTaskIdsForInterests(user
                .getInterests());
        if (taskIds == null) {
            return new ArrayList<>();
        }

        Collection<Task> tasks = ofy().load().type(Task.class).ids(taskIds).values();
        ArrayList<Task> filteredTasks = new ArrayList<>();
        List<Community> communities4User = CommunityDAO.loadCommunities4User(user);
        for (Task task : tasks) {
            if (taskIsVisibleToUser(task, communities4User)) {
                filteredTasks.add(task);
            }
        }
        return filteredTasks;
    }

    public static Key<Task> save(Task task) {
        return ofy().save().entity(task).now();
    }

    public static void setTransientTaskParams(CTUser user, Task task) {
        task.setTags();
        task.setMyTask(task.getOwnerId().longValue() == user.getId().longValue());
        task.setSpaces(CollaborativeSpaceDAO.getCollaborativeSpaces4User(user));
    }

    public static void changeTaskScore(Long taskId, Long change) {
        changeTaskScore(loadTask(taskId), change);
    }

    public static void changeTaskScore(Task task, Long change) {
        task.setScore(task.getScore() + change);
        ofy().save().entity(task);

    }
}
