package org.societies.ext3p.nzone.model;

import java.util.List;

import javax.faces.bean.ApplicationScoped;

import org.societies.api.ext3p.nzone.client.INZoneClient;
import org.societies.api.ext3p.nzone.model.UserPreview;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@SuppressWarnings("serial")
@ApplicationScoped
@Controller(value = "peopleBean")
public class PeopleBean {

	@Autowired
	INZoneClient nzoneClient; 
	
	private List<UserPreview> suggestions;
	
	private boolean currentMainView; 
	
	
	public PeopleBean() {
        
		setCurrentMainView(false);
	}

	/**
	 * @return the suggestions
	 */
	public List<UserPreview> getSuggestions() {
		
		this.suggestions = getNzoneClient().getSuggestedList(isCurrentMainView());
		return this.suggestions;
	}

	/**
	 * @param suggestions the suggestions to set
	 */
	public void setSuggestions(List<UserPreview> suggestions) {
		this.suggestions = suggestions;
	}
	
	public INZoneClient getNzoneClient() {
		return nzoneClient;
	}

	public void setNzoneClient(INZoneClient nzoneClient) {
		this.nzoneClient = nzoneClient;
	}

	/**
	 * @return the currentMainView
	 */
	public boolean isCurrentMainView() {
		return currentMainView;
	}

	/**
	 * @param currentMainView the currentMainView to set
	 */
	public void setCurrentMainView(boolean currentMainView) {
		this.currentMainView = currentMainView;
	}
}
                    