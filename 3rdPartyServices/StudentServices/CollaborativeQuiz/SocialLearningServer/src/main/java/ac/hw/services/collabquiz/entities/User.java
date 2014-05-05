package ac.hw.services.collabquiz.entities;

import java.util.HashSet;
import javax.persistence.*;

@Entity
@Table(name="User")
public class User {
	
	@Id
	@Column(name = "userJid")
	private String userJid;
	
	@Column(name = "cisList")
	private HashSet<String> cisList;

	@Column(name = "score")
	private int score;
	
	
	public User(){
		
	}
	
	public String getUserJid() {
		return userJid;
	}
	public void setUserJid(String userJid) {
		this.userJid = userJid;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}

	public HashSet<String> getCisList() {
		return cisList;
	}

	public void setCisList(HashSet<String> cisList) {
		this.cisList = cisList;
	}
	
	

}
