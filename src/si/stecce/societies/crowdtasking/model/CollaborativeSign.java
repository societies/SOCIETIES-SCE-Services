package si.stecce.societies.crowdtasking.model;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Created with IntelliJ IDEA.
 * User: juresas
 * Date: 28.10.2013
 * Time: 14:35
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class CollaborativeSign {
    @Id
    private Long collaborativeSignId;
    Long meetingId;
    String downloadUrl;

    public CollaborativeSign() {}

    public Long getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(Long meetingId) {
        this.meetingId = meetingId;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
}
