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
public final class CollaborativeSpaceDAO {
    private CollaborativeSpaceDAO() {
    }

    public static CollaborativeSpace load(Long id) {
        CollaborativeSpace cs = null;
        try {
            Object obj = ofy().load().type(CollaborativeSpace.class).id(id).get();
            if (obj != null) {
                cs = (CollaborativeSpace) obj;
            } else {
                ofy().clear();
                cs = ofy().load().type(CollaborativeSpace.class).id(id).get();
            }
        } catch (NotFoundException ignored) {
        }
        return cs;
    }

    public static CollaborativeSpace load(Ref<CollaborativeSpace> csRef) {
        return ofy().load().ref(csRef).get();
    }

    public static List<CollaborativeSpace> load() {
        return ofy().load().type(CollaborativeSpace.class).list();
    }

    public static void delete(CollaborativeSpace collaborativeSpace) {
        ofy().delete().entity(collaborativeSpace).now();    // synchronous, without now() would be asynchronous
    }

    public static void delete(Long id) {
        ofy().delete().type(CollaborativeSpace.class).id(id).now(); // synchronous
    }

/*
    public static Query<CollaborativeSpace> loadCollaborativeSpaces() {
        return ofy().load().type(CollaborativeSpace.class);
    }

    public static Query<CollaborativeSpace> loadCollaborativeSpaces(int limit) {
        return ofy().load().type(CollaborativeSpace.class).limit(limit);
    }
*/

    public static Key<CollaborativeSpace> save(CollaborativeSpace cs) {
        return ofy().save().entity(cs).now();
    }

    public static Set<CollaborativeSpace> getCollaborativeSpaces4User(CTUser user) {
        List<Community> communities = CommunityDAO.loadCommunities4User(user);
        Set<CollaborativeSpace> cses = new HashSet<>();
        for (Community community : communities) {
            if (community.getCollaborativeSpaces() != null) {
                for (CollaborativeSpace cs : community.getCollaborativeSpaces()) {
                    cses.add(cs);
                }
            }
        }
        return cses;
    }
}
