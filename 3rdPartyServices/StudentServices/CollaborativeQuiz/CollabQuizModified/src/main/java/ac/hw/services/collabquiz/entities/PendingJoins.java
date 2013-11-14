package ac.hw.services.collabquiz.entities;

/*    <class name="ac.hw.services.collabquiz.entities.PendingJoins"
           table="pendingJoins">

       <id name="pendingID" type="int" column="pending_id">
            <generator class="native"/>
        </id>

        <property name="toUser" column="to_user" />
        <property name="fromUser" column="from_user" />
        <property name="groupID" column="group_id" /> */

public class PendingJoins {
	
	private int joinID;

	private String toUser;
	private String fromUser;
	private String groupName;
	
	public PendingJoins(){
		
	}
	public int getJoinID() {
		return joinID;
	}

	public void setJoinID(int joinID) {
		this.joinID = joinID;
	}
	public String getToUser() {
		return toUser;
	}
	public void setToUser(String toUser) {
		this.toUser = toUser;
	}
	public String getFromUser() {
		return fromUser;
	}
	public void setFromUser(String fromUser) {
		this.fromUser = fromUser;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}


}
