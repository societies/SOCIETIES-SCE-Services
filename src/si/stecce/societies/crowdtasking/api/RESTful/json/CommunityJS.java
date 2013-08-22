package si.stecce.societies.crowdtasking.api.RESTful.json; /**
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

import java.util.ArrayList;
import java.util.List;

import si.stecce.societies.crowdtasking.api.RESTful.UsersAPI;
import si.stecce.societies.crowdtasking.model.CTUser;
import si.stecce.societies.crowdtasking.model.CollaborativeSpace;
import si.stecce.societies.crowdtasking.model.Community;

import com.googlecode.objectify.Ref;

/**
 * Describe your class here...
 * 
 * @author Simon Jureša
 * 
 */
@SuppressWarnings("unused")
public class CommunityJS {
	private Long id;
    private String jid;
	private String name = "";
	private String description = "";
	private List<CollaborativeSpace> spaces;
	private List<UserJS> members;
	private List<UserJS> requests;
	private boolean owner;
	private boolean member = false;
	private boolean pending = false;
	private String memberStatus;

	public CommunityJS(Community community) {
		setBasicParameters(community);
	}

	private void setBasicParameters(Community community) {
		id = community.getId();
		name = community.getName();
		description = community.getDescription();
		if (community.getCollaborativeSpaces() != null) {
			spaces = new ArrayList<CollaborativeSpace>();
			for (CollaborativeSpace cs:community.getCollaborativeSpaces()) {
				spaces.add(cs);
			}
		}
	}
	
	public CommunityJS(Community community, Long loggedInUserId) {
		setBasicParameters(community);
		owner = community.getOwner().getId().longValue() == loggedInUserId.longValue(); 
				//loggedInUserId.longValue() == 92001L;
		if (community.getMembers() != null) {
			members = new ArrayList<UserJS>();
			for (Ref<CTUser> userRef:community.getMembers()) {
				CTUser user = UsersAPI.getUser(userRef);
				members.add(new UserJS(user, loggedInUserId));
				if (user.getId().longValue() == loggedInUserId.longValue()) {
					member = true;
				}
			}
		}
		if (community.getRequests() != null) {
			requests = new ArrayList<UserJS>();
			for (Ref<CTUser> userRef:community.getRequests()) {
				CTUser user = UsersAPI.getUser(userRef);
				requests.add(new UserJS(user, loggedInUserId));
				if (user.getId().longValue() == loggedInUserId.longValue()) {
					pending = true;
				}
			}
		}
		setMemberStatus();
	}

	private void setMemberStatus() {
		if (member) {
			memberStatus = "You are a memeber.";
		}
		if (owner) {
			memberStatus = "You are the owner.";
		}
		if (pending) {
			memberStatus = "Membership pending";
		}
		if (!pending && !owner && !member) {
			memberStatus = "";
		}
	}
}
