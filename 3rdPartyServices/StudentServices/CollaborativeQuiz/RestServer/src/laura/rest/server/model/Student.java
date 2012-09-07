package laura.rest.server.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Student {

	String id;
	String name;
	int score;
	int first;
	
	//set up student
	public String getId(){
		return id;
	}
	public void setId(String id){
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name){
		this.name = name;
	}
	
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public int getFirst(){
		return first;
	}
	public void setFirst(int first){
		this.first = first;
	}

	
}
