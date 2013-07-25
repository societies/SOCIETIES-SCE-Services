package si.stecce.societies.crowdtasking.model;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class Tag {
	@Id private Long id;
	private String tagName;
	private long tagFrequency = 0;
	private long interestFrequency = 0;

	public Tag() {}
	
	public Tag(String tagName) {
		super();
		this.id = null;
		this.tagName = tagName;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public long getTagFrequency() {
		return tagFrequency;
	}

	public void setTagFrequency(long tagFrequency) {
		this.tagFrequency = tagFrequency;
	}

	public long getInterestFrequency() {
		return interestFrequency;
	}

	public void setInterestFrequency(long interestFrequency) {
		this.interestFrequency = interestFrequency;
	}
}
