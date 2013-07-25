package si.stecce.societies.crowdtasking.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;

@Entity
public class Task {
	@Id private Long id;
	private String title;
	private String description;
	private Date dueDate;
	private Date created;
	@Index private Long ownerId;
	private String postedBy;	// userNickName
	private List<String> tagList;
	@Ignore private boolean myTask;
	@Ignore private String tags;
	private String status; // open (new), inprogress, finished, closed?
	private List<Long> involvedUsers;
	private List<String> informChannels;
	private String executeMessage;
	@Index private Long score;
	@Ignore private Long interestScore;
	@Load private List<Ref<Meeting>> meetingsRefs;
	@Index @Load private List<Ref<Community>> communityRefs;
	@Ignore private Set<CollaborativeSpace> spaces;

	public Task() {
	}

	public Long getId() {
		return id;
	}

	public Task(String title, String description, Date dueDate,
			Long ownerId, String postedBy, List<Long> communityIds, List<String> tagList) {
		super();
		this.id = null;
		this.title = title;
		this.description = description;
		this.dueDate = dueDate;
		this.created = new Date();
		this.ownerId = ownerId;
		this.postedBy = postedBy;
		this.tagList = tagList;
		this.status = "open";
		if (communityIds != null) {
			communityRefs = new ArrayList<Ref<Community>>();
			for (Long communityId:communityIds) {
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
	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public void setTags() {
		if  (tagList != null && tagList.size() > 0) {
			tags = tagList.get(0);
			for (int i=1; i<tagList.size(); i++) {
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<Long> getInvolvedUsers() {
		return involvedUsers;
	}

	public void setInvolvedUsers(List<Long> involvedUsers) {
		this.involvedUsers = involvedUsers;
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
		}
		else {
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
			return null;
		}
		List<Meeting> meetings = new ArrayList<Meeting>();
		for (Ref<Meeting> meetingRef:meetingsRefs) {
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
		List<Community> communities = new ArrayList<Community>();
		for (Ref<Community> communitiesRef:communityRefs) {
			communities.add(communitiesRef.get());
		}
		return communities;
	}

	public Set<CollaborativeSpace> getSpaces() {
		return spaces;
	}

	public void setSpaces(Set<CollaborativeSpace> spaces) {
		this.spaces = spaces;
	}

	public List<Ref<Community>> getCommunitiesRefs() {
		return communityRefs;
	}
}