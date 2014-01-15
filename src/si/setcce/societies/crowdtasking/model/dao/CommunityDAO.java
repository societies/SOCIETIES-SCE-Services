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
import si.setcce.societies.crowdtasking.model.CollaborativeSpace;
import si.setcce.societies.crowdtasking.model.Community;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static si.setcce.societies.crowdtasking.model.dao.OfyService.ofy;

/**
 * Describe your class here...
 *
 * @author Simon Jureša
 */
public final class CommunityDAO {
    private CommunityDAO() {
    }

    public static Community loadCommunity(Long id) {
        Community community = null;
        try {
            community = ofy().load().type(Community.class).id(id).get();
        } catch (NotFoundException ignored) {
        }
        return community;
    }

    public static Community loadCommunity(String jid) {
        Community community = null;
        try {
            community = ofy().load().type(Community.class).filter("jid", jid).first().get();
        } catch (NotFoundException ignored) {
        }
        return community;
    }

    public static List<Community> loadCommunities4CSS(String ownerJid) {
        return ofy().load().type(Community.class).filter("ownerJid", ownerJid).list();
    }

    public static Community loadCommunity(Ref<Community> communityRef) {
        Community community = null;
        try {
            community = ofy().load().ref(communityRef).get();
        } catch (NotFoundException ignored) {
        }
        return community;
    }

    public static List<Community> loadCommunities() {
        return ofy().load().type(Community.class).filter("jid", null).list();
    }

    public static List<Community> loadCommunities4User(CTUser user) {
        return ofy().load().type(Community.class).filter("members", Ref.create(Key.create(CTUser.class, user.getId()))).list();
    }

    public static Query<Community> loadCommunities(int limit) {
        return ofy().load().type(Community.class).limit(limit);
    }

    public static Set<CTUser> loadMembers(List<Long> communities) {
        Set<CTUser> members = new HashSet<CTUser>();
        for (Long communityId : communities) {
            Community communitiy = ofy().load().type(Community.class).id(communityId).get();
            for (Ref<CTUser> userRef : communitiy.getMembers()) {
                members.add(userRef.get());
            }
        }
        return members;
    }

    public static Query<Community> findCommunities(Long spaceId) {
        return ofy().load().type(Community.class).filter("collaborativeSpaceRefs", Ref.create(Key.create(CollaborativeSpace.class, spaceId)));
    }

    public static Query<Community> findCommunities(CollaborativeSpace space, CTUser user) {
        return findCommunities(space.getId(), user);
    }

    public static Query<Community> findCommunities(Long spaceId, CTUser user) {
        return ofy().load().type(Community.class).filter("collaborativeSpaceRefs", Ref.create(Key.create(CollaborativeSpace.class, spaceId)))
                .filter("members", Ref.create(Key.create(CTUser.class, user.getId())));
    }

    public static Key<Community> saveCommunity(Community community) {
        return ofy().save().entity(community).now();
    }
}
