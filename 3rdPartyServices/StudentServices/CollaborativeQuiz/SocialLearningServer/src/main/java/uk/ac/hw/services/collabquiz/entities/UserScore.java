package uk.ac.hw.services.collabquiz.entities;

import javax.persistence.*;

@Entity
@Table(name="UserScore")
public class UserScore {
	
	@Id
	@Column(name = "userJid")
	private String userJid;

	@Column(name = "score")
	private int score;
	
	
	public UserScore(){
		
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

}
