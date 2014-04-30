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
package si.setcce.societies.crowdtasking.model;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Notification settings
 *
 * @author Simon Jureša
 */
@Entity
public class NotificationSettings {
    @Id
    private Long id;

    public Long getId() {
        return id;
    }

    private boolean interestingTask = false;
    private boolean executeTask = true;
    private boolean finalizeTask = true;
    private boolean likeTask = true;
    private boolean likeComment = true;
    private boolean newTaskInCommunity = true;
    private boolean newComment = true;
    private boolean joinCommunityRequest = true;

    public boolean isInterestingTask() {
        return interestingTask;
    }

    public void setInterestingTask(boolean interestingTask) {
        this.interestingTask = interestingTask;
    }

    public boolean isExecuteTask() {
        return executeTask;
    }

    public void setExecuteTask(boolean executeTask) {
        this.executeTask = executeTask;
    }

    public boolean isFinalizeTask() {
        return finalizeTask;
    }

    public void setFinalizeTask(boolean finalizeTask) {
        this.finalizeTask = finalizeTask;
    }

    public boolean isLikeTask() {
        return likeTask;
    }

    public void setLikeTask(boolean likeTask) {
        this.likeTask = likeTask;
    }

    public boolean isLikeComment() {
        return likeComment;
    }

    public void setLikeComment(boolean likeComment) {
        this.likeComment = likeComment;
    }

    public boolean isNewTaskInCommunity() {
        return newTaskInCommunity;
    }

    public void setNewTaskInCommunity(boolean newTaskInCommunity) {
        this.newTaskInCommunity = newTaskInCommunity;
    }

    public boolean isNewComment() {
        return newComment;
    }

    public void setNewComment(boolean newComment) {
        this.newComment = newComment;
    }

    public boolean isJoinCommunityRequest() {
        return joinCommunityRequest;
    }

    public void setJoinCommunityRequest(boolean joinCommunityRequest) {
        this.joinCommunityRequest = joinCommunityRequest;
    }
}

