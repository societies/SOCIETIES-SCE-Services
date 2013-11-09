package si.stecce.societies.crowdtasking.model;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: juresas
 * Date: 23.9.2013
 * Time: 12:30
 */
@Entity
public class Channel {
    @Id
    private Long id;
    @Index
    private Long channelNumber;
    Long userId, communityId, spaceId;
    Date created;

    public Channel() {
    }

    public Channel(Long channelNumber, Long userId, Long communityId, Long spaceId, Date created) {
        this.channelNumber = channelNumber;
        this.userId = userId;
        this.communityId = communityId;
        this.spaceId = spaceId;
        this.created = created;
    }

    public Long getChannelNumber() {
        return channelNumber;
    }

    public void setChannelNumber(Long channelNumber) {
        this.channelNumber = channelNumber;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public Long getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(Long spaceId) {
        this.spaceId = spaceId;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public long getId() {
        return id;
    }
}
