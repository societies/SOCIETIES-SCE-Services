package laura.rest.server.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Challenge {
	
	Student challenger;
	Student challenged;
	String category;
	int challengerScore;
	int challengedScore;
	int id;
	
	public int getId(){
		return id;
	}
	public void setId(int id){
		this.id = id;
	}
	public Student getChallenger() {
		return challenger;
	}
	public void setChallenger(Student challenger){
		this.challenger = challenger;
	}
	public Student getChallenged(){
		return challenged;
	}
	public void setChallenged(Student challenged){
		this.challenged = challenged;
	}
	public String getCategory(){
		return category;
	}
	public void setCategory(String category){
		this.category = category;
	}
	public int getChallengerScore(){
		return challengerScore;
	}
	public void setChallengerScore(int challengerScore){
		this.challengerScore = challengerScore;
	}
	public int getChallengedScore(){
		return challengedScore;
	}
	public void setChallengedScore(int challengedScore){
		this.challengedScore = challengedScore;
	}
	

}
