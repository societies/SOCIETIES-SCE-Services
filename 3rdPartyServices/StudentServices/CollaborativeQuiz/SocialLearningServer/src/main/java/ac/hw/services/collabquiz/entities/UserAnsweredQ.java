package ac.hw.services.collabquiz.entities;

import java.util.HashMap;

import javax.persistence.*;

@Entity
@Table(name="UserAnsweredQ")
public class UserAnsweredQ {


	private int id;
	private String userJid;
	private int questionID;
	private boolean answeredCorrect;
	
	public UserAnsweredQ() {
		
	}
	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	
	public String getUserJid() {
		return userJid;
	}
	public void setUserJid(String userJid) {
		this.userJid = userJid;
	}
	public int getQuestionID() {
		return questionID;
	}

	public void setQuestionID(int questionID) {
		this.questionID = questionID;
	}

	public boolean isAnsweredCorrect() {
		return answeredCorrect;
	}

	public void setAnsweredCorrect(boolean answeredCorrect) {
		this.answeredCorrect = answeredCorrect;
	}

}
