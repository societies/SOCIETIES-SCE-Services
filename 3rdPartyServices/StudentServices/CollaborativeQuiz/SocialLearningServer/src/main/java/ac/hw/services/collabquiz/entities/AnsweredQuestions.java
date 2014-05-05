package ac.hw.services.collabquiz.entities;

import javax.persistence.*;

@Entity
@Table(name="answeredQuestions")
public class AnsweredQuestions {
	
	@Column(name = "answeredQID")
	private int answeredQID;
	
	@Column(name = "userID")
	private String userID;
	
	@Column(name = "cisName")
	private String cisName;
	
	@Column(name = "questionID")
	private int questionID;
	
	@Column(name = "answeredCorrect")
	private boolean answeredCorrect;
	
	public AnsweredQuestions() {
		
	}
	
	
	
	public int getAnsweredQID() {
		return answeredQID;
	}



	public void setAnsweredQID(int answeredQID) {
		this.answeredQID = answeredQID;
	}



	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getCisName() {
		return cisName;
	}

	public void setCisName(String cisName) {
		this.cisName = cisName;
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
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof AnsweredQuestions) {
			AnsweredQuestions q = (AnsweredQuestions) obj;
			if(q.getAnsweredQID()==this.questionID && q.getUserID().equals(this.getUserID())) {
				if(null!=q.getCisName() && null!=this.cisName && q.getCisName().equals(this.cisName)) {
					return true;
				}
			}
		}
		return false;
	}

}
