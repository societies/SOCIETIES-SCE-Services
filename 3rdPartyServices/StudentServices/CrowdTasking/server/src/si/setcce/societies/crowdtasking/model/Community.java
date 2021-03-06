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

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.*;
import si.setcce.societies.crowdtasking.api.RESTful.impl.UsersAPI;
import si.setcce.societies.crowdtasking.model.dao.CollaborativeSpaceDAO;

import java.util.*;

/**
 * Describe your class here...
 *
 * @author Simon Jureša
 */
@Entity
public class Community {
    @Index
    @Id
    private Long id;
    @Index
    private String jid;
    private String name;
    private String description;
    @Load
    private Ref<CTUser> ownerRef;
    @Ignore
    private CTUser owner;
    @Index
    private String ownerJid;
    @Index
    @Load
    private List<Ref<CollaborativeSpace>> collaborativeSpaceRefs;
    @Ignore
    private List<CollaborativeSpace> collaborativeSpaces;
    //	private List<Ref<Community>> subCommunities;
    @Load
    @Index
    private Set<Ref<CTUser>> members;
    @Load
    @Index
    private Set<Ref<CTUser>> requests;
    Date created, modified;

    public Community() {
    }

    public Community(String jid, String name, String description, List<Long> csIds, String ownerJid, Long userId, CTUser owner) {
        this.jid = jid;
        this.name = name;
        this.description = description;
        setCollaborativeSpaces(csIds);
        this.ownerJid = ownerJid;
        addMember(userId);
        created = new Date();
    }

    public Community(String name, String description, CTUser owner, List<Long> csIds, List<Long> members) {
        super();
//        this.jid = jid;
        this.name = name;
        this.description = description;
        setOwnerRef(owner.getId());
        setCollaborativeSpaces(csIds);
        addMember(owner);
        addMembers(members);
        created = new Date();
    }

    public void setCollaborativeSpaces(List<Long> csIds) {
        if (csIds != null) {
            collaborativeSpaceRefs = new ArrayList<>();
            for (Long csId : csIds) {
                collaborativeSpaceRefs.add(Ref.create(Key.create(CollaborativeSpace.class, csId)));
            }
        }
    }

    public void addCollaborativeSpace(Long csId) {
        if (collaborativeSpaceRefs == null) {
            collaborativeSpaceRefs = new ArrayList<>();
        }
        collaborativeSpaceRefs.add(Ref.create(Key.create(CollaborativeSpace.class, csId)));
    }

/*
    public void addCommunity(Community community) {
		if (subCommunities == null) {
			subCommunities = new ArrayList<Ref<Community>>();
		}
		subCommunities.add(Ref.create(Key.create(Community.class, community.getId())));
	}
*/

/*
    public void addTask(Task task) {
        if (taskRefs == null) {
            taskRefs = new ArrayList<>();
        }
        taskRefs.add(Ref.create(Key.create(Task.class, task.getId())));
    }

    public List<Task> getTasks() {
        if (taskRefs == null) {
            return null;
        }
        List<Task> tasks = new ArrayList<>();
        for (Ref<Task> communitiesRef:taskRefs) {
            tasks.add(communitiesRef.get());
        }
        return tasks;
    }
*/

    public void addRequest(CTUser user) {
        addRequest(user.getId());
    }

    public void addRequest(Long userId) {
        if (requests == null) {
            requests = new HashSet<>();
        }
        requests.add(Ref.create(Key.create(CTUser.class, userId)));
    }

    public void removeRequest(CTUser user) {
        removeRequest(user.getId());
    }

    public void removeRequest(Long userId) {
        if (requests == null) {
            return;
        }
        requests.remove(Ref.create(Key.create(CTUser.class, userId)));
    }

    public Set<Ref<CTUser>> getRequests() {
        return requests;
    }

    public void addMember(CTUser user) {
        addMember(user.getId());
    }

    public void removeMember(Long userId) {
        if (members == null) {
            return;
        }
        members.remove(Ref.create(Key.create(CTUser.class, userId)));
    }

    public void addMember(Long userId) {
        if (members == null) {
            members = new HashSet<>();
        }
        members.add(Ref.create(Key.create(CTUser.class, userId)));
    }

    public void addMembers(List<Long> memberIDs) {
        if (memberIDs == null) {
            return;
        }
        members = new HashSet<>();
        members.add(ownerRef);
        for (Long userId : memberIDs) {
            addMember(userId);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CollaborativeSpace> getCollaborativeSpaces() {
        if (collaborativeSpaces == null && collaborativeSpaceRefs != null) {
            collaborativeSpaces = new ArrayList<>();
            try {
                for (Ref<CollaborativeSpace> collaborativeSpaceRef : collaborativeSpaceRefs) {
                    try {
                        CollaborativeSpace cs = collaborativeSpaceRef.get();
                        if (cs.getName() == null || "".equalsIgnoreCase(cs.getName())) {
                            cs.setName("no name");
                        }
                        collaborativeSpaces.add(cs);
                    } catch (Exception e) {
                        System.out.println("Error in getCollaborativeSpaces(" + collaborativeSpaceRef.getKey().getId() + ") in Community class: " + e.getMessage());
                        if (e.getMessage() == null) {
                            e.printStackTrace();
                        }
                        CollaborativeSpace cs = CollaborativeSpaceDAO.load(collaborativeSpaceRef.getKey().getId());
                        if (cs != null) {
                            collaborativeSpaces.add(cs);
                        } else {
                            collaborativeSpaceRefs.remove(collaborativeSpaceRef);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return collaborativeSpaces;
    }

/*
	public List<Ref<Community>> getSubCommunities() {
		return subCommunities;
	}
	
	public void setSubCommunities(List<Ref<Community>> subCommunities) {
		this.subCommunities = subCommunities;
	}
*/

    public Set<Ref<CTUser>> getMembers() {
        return members;
    }

    public void setMembers(Set<Ref<CTUser>> members) {
        this.members = members;
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CTUser getOwner() {
        if (owner != null) {
            return owner;
        }
        if (ownerRef != null) {
            owner = UsersAPI.getUser(ownerRef);
        }
        return owner;
    }

    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public Ref<CTUser> getOwnerRef() {
        return ownerRef;
    }

    public void setOwnerRef(Ref<CTUser> ownerRef) {
        this.ownerRef = ownerRef;
    }

    public void setOwnerRef(Long userId) {
        this.ownerRef = Ref.create(Key.create(CTUser.class, userId));
    }
}
