package si.setcce.societies.crowdtasking.model;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.*;

import java.util.*;

@Entity
public class Task {
    @Id
    private Long id;
    private String title;
    @Index
    private boolean societiesTask;
    private String description;
    private Date created;
    @Index
    private Long ownerId;
    private String postedBy;    // userNickName
    private List<String> tagList;
    @Ignore
    private boolean myTask;
    @Ignore
    private String tags;
    @Deprecated
    private String status; // open (new), inprogress, finished, closed?
    @Index
    private TaskStatus taskStatus;
    private Set<Long> involvedUsers;
    private List<String> informChannels;
    private String executeMessage;
    @Index
    private Long score;
    @Ignore
    private Long interestScore;
    @Load
    private List<Ref<Meeting>> meetingsRefs;
    @Index
    @Load
    private List<Ref<Community>> communityRefs;
    @Index
    private List<String> communityJids;
    @Index
    private List<Ref<Community>> communityJidRefs;
    @Ignore
    private Set<CollaborativeSpace> spaces;

    public Task() {
    }

    public Long getId() {
        return id;
    }

    public Task(String title, String description, Long ownerId, String postedBy, List<Long> communityIds, List<String> tagList) {
        super();
        this.id = null;
        this.title = title;
        this.description = description;
        this.created = new Date();
        this.ownerId = ownerId;
        this.postedBy = postedBy;
        this.tagList = tagList;
        this.taskStatus = TaskStatus.OPEN;
        if (communityIds != null) {
            communityRefs = new ArrayList<>();
            for (Long communityId : communityIds) {
                communityRefs.add(Ref.create(Key.create(Community.class, communityId)));
            }
        }
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTags() {
        if (tagList != null && tagList.size() > 0) {
            tags = tagList.get(0);
            for (int i = 1; i < tagList.size(); i++) {
                tags += ", " + tagList.get(i).trim();
            }
        }
    }

    public String getTags() {
        return tags;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public boolean isMyTask() {
        return myTask;
    }

    public void setMyTask(boolean myTask) {
        this.myTask = myTask;
    }

    public Date getCreated() {
        return created;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getPostedBy() {
        return postedBy;
    }

    @Deprecated
    public String getStatus() {
        return status;
    }

    @Deprecated
    public void setStatus(String status) {
        this.status = status;
    }

    public Set<Long> getInvolvedUsers() {
        return involvedUsers;
    }

    public void addInvolvedUser(Long userId) {
        if (involvedUsers == null) {
            involvedUsers = new HashSet<>();
            setTaskStatus(TaskStatus.IN_PROGRESS);
        }
        this.involvedUsers.add(userId);
    }

    public List<String> getInformChannels() {
        return informChannels;
    }

    public void setInformChannels(List<String> informChannels) {
        this.informChannels = informChannels;
    }

    public String getExecuteMessage() {
        return executeMessage;
    }

    public void setExecuteMessage(String executeMessage) {
        this.executeMessage = executeMessage;
    }

    public List<String> getTagList() {
        return tagList;
    }

    public void setTagList(List<String> tagList) {
        this.tagList = tagList;
    }

    public Long getScore() {
        return score;
    }

    public void setScore(Long score) {
        this.score = score;
    }

    public Long getInterestScore() {
        return interestScore;
    }

    public void setInterestScore(Long interestScore) {
        this.interestScore = interestScore;
    }

    public void addInterestScore(Long interestScore) {
        if (this.interestScore != null) {
            this.interestScore += interestScore;
        } else {
            this.interestScore = interestScore;
        }
    }

    public void addMeeting(Key<Meeting> meetingKey) {
        if (meetingsRefs == null) {
            meetingsRefs = new ArrayList<Ref<Meeting>>();
        }
        meetingsRefs.add(Ref.create(meetingKey));
    }

    public List<Meeting> getMeetings() {
        if (meetingsRefs == null) {
            return new ArrayList<>();
        }
        List<Meeting> meetings = new ArrayList<>();
        for (Ref<Meeting> meetingRef : meetingsRefs) {
            if (meetingRef.get() != null) {
                meetings.add(meetingRef.get());
            }
        }
        return meetings;
    }

    public List<Community> getCommunities() {
        if (communityRefs == null) {
            return null;
        }
        List<Community> communities = new ArrayList<>();
        for (Ref<Community> communitiesRef : communityRefs) {
            communities.add(communitiesRef.get());
        }
        return communities;
    }

    public Set<CollaborativeSpace> getSpaces() {
        return spaces;
    }

    public List<String> getCommunityJids() {
        return communityJids;
    }

    public void setSpaces(Set<CollaborativeSpace> spaces) {
        this.spaces = spaces;
    }

    public List<Ref<Community>> getCommunitiesRefs() {
        return communityRefs;
    }

    public void setCommunityRefs(List<Ref<Community>> communityRefs) {
        this.communityRefs = communityRefs;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public List<Ref<Community>> getCommunityJidRefs() {
        return communityJidRefs;
    }

    public void setCommunityJidRefs(List<Ref<Community>> communityJidRefs) {
        this.communityJidRefs = communityJidRefs;
    }
}