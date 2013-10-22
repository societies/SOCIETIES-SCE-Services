package ac.hw.services.collabquiz.entities;

/*    <class name="ac.hw.services.collabquiz.entities.UserGroups"
           table="userGroups">

        <id name="groupID" type="int" column="group_id" />

        <property name="userJid" column="user_jid" />

    </class>
    */

public class UserGroups {
	
	private int groupId;
	private String userJid;
	
	public UserGroups(){
		
	}
	
	public int getGroupId() {
		return groupId;
	}
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
	public String getUserJid() {
		return userJid;
	}
	public void setUserJid(String userJid) {
		this.userJid = userJid;
	}
	
	
	

}
