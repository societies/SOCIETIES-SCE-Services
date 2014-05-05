package ac.hw.services.collabquiz.entities;

import java.util.HashSet;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="Cis")
public class Cis {

	@Id
	@Column(name = "cisName")
	private String cisName;

	@Column(name = "score")
	private int score;

	@Column(name = "contributors")
	private HashSet<String> contributors;

	public Cis(){

	}

	public void setCisName(String cisName) {
		this.cisName=cisName;
	}

	public String getCisName() {
		return this.cisName;
	}

	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}

	public HashSet<String> getContributors() {
		return contributors;
	}

	public void setContributors(HashSet<String> contributors) {
		this.contributors = contributors;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Cis) {
			if(this.cisName.equals(((Cis) obj).getCisName())) {
				return true;
			}
		}
		return false;
	}


}
