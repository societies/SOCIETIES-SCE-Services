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

import si.setcce.societies.crowdtasking.Util;
import si.setcce.societies.crowdtasking.model.CTUser;
import si.setcce.societies.crowdtasking.model.Comment;

import java.util.Date;
import java.util.Random;

/**
 * Describe your class here...
 *
 * @author Simon Jureša
 */
@SuppressWarnings("unused")
public class CommentJS {
    public Long id;
    public Long taskId;
    public String postedBy;
    public String commentText;
    public String picUrl;
    public Date posted;
    public boolean execution;
    public boolean liked;
    public boolean myComment;
    public String trustLevel;

    public CommentJS(Comment comment, CTUser loggedinUser) {
        id = comment.getId();
        taskId = comment.getTask().getId();
        picUrl = comment.getOwner().getPicUrl();
        postedBy = comment.getOwner().getUserName();
        commentText = comment.getComment();
        posted = comment.getPosted();
        execution = comment.isExecution();
        this.trustLevel = getTrustLevel();
        if (loggedinUser == null) {
            this.trustLevel = "unknown";
        } else {
            this.trustLevel = Util.getTrustLevelDescription(
                    loggedinUser.getTrustValueForIdentity(comment.getOwner().getSocietiesEntityId()));
        }
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public void setMyComment(boolean myComment) {
        this.myComment = myComment;
    }

    private String getTrustLevel() {
        int random = new Random().nextInt(3);
        if (random == 2) return "trusted";
        if (random == 1) return "marginallytrusted";
        return "distrusted";
    }
}
