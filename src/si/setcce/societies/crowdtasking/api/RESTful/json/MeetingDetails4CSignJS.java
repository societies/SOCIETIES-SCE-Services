package si.setcce.societies.crowdtasking.api.RESTful.json;

import com.googlecode.objectify.Ref;
import si.setcce.societies.crowdtasking.api.RESTful.impl.UsersAPI;
import si.setcce.societies.crowdtasking.model.CTUser;
import si.setcce.societies.crowdtasking.model.Meeting;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: juresas
 * Date: 29.10.2013
 * Time: 9:14
 */
public class MeetingDetails4CSignJS extends MeetingJS {
    public List<BasicUserJS> users;

    public MeetingDetails4CSignJS(Meeting meeting) {
        super(meeting);
        users = new ArrayList<>();
        for (Ref<CTUser> ctUserRef : meeting.getInvitedUsers()) {
            CTUser ctUser = UsersAPI.getUser(ctUserRef);
            users.add(new BasicUserJS(ctUser));
        }
    }
}
