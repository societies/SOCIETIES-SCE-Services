package ac.hw.services.collabquiz.entities;

/*        <id name="groupID" type="int" column="group_id">
            <generator class="native"/>
        </id>

        <property name="groupName" column="group_name"/> */
public class Groups {
	
	private int groupID;
	private String groupName;
	private int score;
	private String admin;
	
	public Groups(){
		
	}
	
	public int getGroupID() {
		return groupID;
	}
	public void setGroupID(int groupID) {
		this.groupID = groupID;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	
	public String getAdmin() {
		return admin;
	}
	public void setAdmin(String admin) {
		this.admin = admin;
	}

}
