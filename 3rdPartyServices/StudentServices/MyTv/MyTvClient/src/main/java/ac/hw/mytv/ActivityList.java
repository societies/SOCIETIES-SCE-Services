package ac.hw.mytv;

import java.util.List;

import org.societies.api.schema.activity.MarshaledActivity;

public class ActivityList {
	
	private String cisName;
	private List<MarshaledActivity> activities;
	
	public String getCisName() {
		return cisName;
	}
	public void setCisName(String cisName) {
		this.cisName = cisName;
	}
	public List<MarshaledActivity> getActivities() {
		return activities;
	}
	public void setActivities(List<MarshaledActivity> activities) {
		this.activities = activities;
	}
	
	

}
