package si.setcce.societies.crowdtasking.api.RESTful.json;

import org.societies.api.schema.cis.community.Community;
import org.societies.integration.model.SocietiesUser;

/**
 * Describe your class here...
 *
 * @author Simon Jureša
 *
 */
@SuppressWarnings("unused")
public class CommunityJS {
    public Long id;
    public String jid;
    private String name = "";
    private String description = "";
//    private List<CollaborativeSpace> spaces;
//    private List<UserJS> members;
//    private List<UserJS> requests;
    private boolean owner;
    private boolean member = false;
    private boolean pending = false;
    private String memberStatus;

    private void setBasicParameters(Community community) {
        jid = community.getCommunityJid();
        name = community.getCommunityName();
        description = community.getDescription();
/*
        if (community.getCollaborativeSpaces() != null) {
            spaces = new ArrayList<CollaborativeSpace>();
            for (CollaborativeSpace cs:community.getCollaborativeSpaces()) {
                spaces.add(cs);
            }
        }
*/
    }

    public CommunityJS(Community community, SocietiesUser societiesUser) {
        setBasicParameters(community);
        owner = community.getOwnerJid().equalsIgnoreCase(societiesUser.getUserId());
/*
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
*/
/*
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
*/
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
