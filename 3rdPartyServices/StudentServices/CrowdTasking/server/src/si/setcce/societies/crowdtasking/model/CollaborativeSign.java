package si.setcce.societies.crowdtasking.model;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Created with IntelliJ IDEA.
 * User: Simon Jureša
 * Date: 28.10.2013
 * Time: 14:35
 */
@Entity
public class CollaborativeSign {
    @Id
    private Long collaborativeSignId;
    Long meetingId;
    private String gcmRegistrationId;

    public CollaborativeSign() {
    }

    public Long getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(Long meetingId) {
        this.meetingId = meetingId;
    }

    public String getGcmRegistrationId() {
        return gcmRegistrationId;
    }

    public void setGcmRegistrationId(String gcmRegistrationId) {
        this.gcmRegistrationId = gcmRegistrationId;
    }
}
