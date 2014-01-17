package si.setcce.societies.crowdtasking.model;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Embed;

import java.util.Date;

/**
 * Author:  Simon Jureša
 * Date:    17.1.2014
 * Time:    13:20
 */
@Embed
public class MeetingMinute {
    public Date timestamp;
    public Ref<CTUser> userRef;
    public String minute;

    MeetingMinute() {
    }

    MeetingMinute(CTUser user, String minute) {
        timestamp = new Date();
        this.userRef = Ref.create(Key.create(CTUser.class, user.getId()));
        ;
        this.minute = minute;
    }
}