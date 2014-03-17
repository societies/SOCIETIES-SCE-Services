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
package si.setcce.societies.crowdtasking;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.cmd.Query;
import si.setcce.societies.crowdtasking.api.RESTful.impl.CommentAPI;
import si.setcce.societies.crowdtasking.model.*;
import si.setcce.societies.crowdtasking.model.dao.CommunityDAO;
import si.setcce.societies.crowdtasking.model.dao.TaskDao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Describe your class here...
 *
 * @author Simon Jureša
 */
public class EventsForUserFilter {
    private Set<Long> taskIds, communityIds;
    private CTUser user;

    public EventsForUserFilter(CTUser user) {
        this.user = user;
        findTaskIdsByInvolvedUser(user);    // and collaborative spaces
    }

    public boolean isEventForUser(Event event) {
        if (taskIds.contains(event.getTaskId())) {
            return true;
        }
        List<Ref<Community>> communityRefs = event.getCommunityRefs();
        if (communityRefs != null) {
            for (Ref<Community> communityRef : communityRefs) {
                if (communityIds.contains(communityRef.getKey().getId())) {
                    return true;
                }
            }
        }
        if (user.getId() == event.getUserRef().getKey().getId()) {
            return true;
        }
        if (event.getType() == EventType.COMMUNITY_CREATED) {
            return true;
        }

        return false;
    }

    /**
     * get task IDs where useres is involved
     * (tasks's owner, tasks's commenter)
     *
     * @param CTUser user
     */
    private List<Long> findTaskIdsByInvolvedUser(CTUser user) {
        Query<Comment> q = CommentAPI.findCommentsByUser(user.getId());

        taskIds = new HashSet<>();
        for (Comment comment : q) {
            taskIds.add(comment.getTask().getId());
        }

        communityIds = new HashSet<>();
        List<Community> communities = CommunityDAO.loadCommunities4User(user);
        for (Community community : communities) {
            communityIds.add(community.getId());
        }

        List<Task> tasks = TaskDao.findTasksByUser(user);
        for (Task task : tasks) {
            taskIds.add(task.getId());
        }

        return new ArrayList<>(taskIds);
    }

}
