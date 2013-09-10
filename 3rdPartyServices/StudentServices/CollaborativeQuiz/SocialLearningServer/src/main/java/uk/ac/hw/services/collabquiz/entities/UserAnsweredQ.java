package uk.ac.hw.services.collabquiz.entities;

import java.util.HashMap;

import javax.persistence.*;

@Entity
@Table(name="UserAnsweredQ")
public class UserAnsweredQ {
	private int id;

	private String userJid;

	private Question question;
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
	public Question getQuestion() {
		return question;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}

	public boolean isAnsweredCorrect() {
		return answeredCorrect;
	}

	public void setAnsweredCorrect(boolean answeredCorrect) {
		this.answeredCorrect = answeredCorrect;
	}

}
