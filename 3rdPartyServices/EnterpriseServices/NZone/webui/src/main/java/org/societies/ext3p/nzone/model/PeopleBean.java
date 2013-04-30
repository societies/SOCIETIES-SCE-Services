package org.societies.ext3p.nzone.model;

import java.util.List;

import javax.faces.bean.ApplicationScoped;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialViewContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.ext3p.nzone.client.INZoneClient;
import org.societies.api.ext3p.nzone.model.UserPreview;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@ApplicationScoped
@Controller(value = "peopleBean")
public class PeopleBean {

	@Autowired
	INZoneClient nzoneClient; 
	private static Logger log = LoggerFactory.getLogger(PeopleBean.class);

	
	private List<UserPreview> suggestions;
	
	private boolean currentMainView; 

	
	
	public PeopleBean() {
		setCurrentMainView(false);
		
	}

	
	/**
	 * @return the suggestions
	 */
	public List<UserPreview> getSuggestions() {
		log.info("getSuggestions called");
		return this.suggestions;
	}

	/**
	 * @param suggestions the suggestions to set
	 */
	public void setSuggestions(List<UserPreview> suggestions) {
		log.info("setSuggestions called");
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
	
	public boolean istagged(UserPreview currentdet)
	{
		if (currentdet == null)
			return false;
		
		if (currentdet.getTags() == null)
			return false;
		
		
		if (currentdet.getTags().size() == 0)
			return false;
		
		if (currentdet.getTags().get(0) == null)
			return false;
		
		
		 
		if (currentdet.getTags().get(0).length() > 0)
		{
			return true;
		}
		return false;
		
		
		
	}
	
	public boolean isperftagged(UserPreview currentdet)
	{
		if (currentdet == null)
			return false;
		
		if (currentdet.getTags() == null)
			return false;
		
		
		if (currentdet.getTags().size() == 0)
			return false;
		
		if (currentdet.getTags().get(0) == null)
			return false;
		
		
		 
		for ( int i = 0; i < currentdet.getTags().size() ; i++)
		{
			
			if (currentdet.getTags().get(i).contains("Learned"))
					return true;
		}
		return false;
		
		
	}
	
	public boolean isinteresttagged(UserPreview currentdet)
	{
		if (currentdet == null)
			return false;
		
		if (currentdet.getTags() == null)
			return false;
		
		
		if (currentdet.getTags().size() == 0)
			return false;
		
		 
		for ( int i = 0; i < currentdet.getTags().size() ; i++)
		{
			if (currentdet.getTags().get(i).contains("ommon"))
				return true;
		}
		return false;
	}
	
	
	public boolean isfacebooktagged(UserPreview currentdet)
	{
		if (currentdet == null)
			return false;
		
		if (currentdet.getTags() == null)
			return false;
		
		
		if (currentdet.getTags().size() == 0)
			return false;
		
		 
		for ( int i = 0; i < currentdet.getTags().size() ; i++)
		{
			
				if (currentdet.getTags().get(i).contains("facebook"))
						return true;
		
		}
		return false;
	}
	
	public boolean islinkedtagged(UserPreview currentdet)
	{
		if (currentdet == null)
			return false;
		
		if (currentdet.getTags() == null)
			return false;
		
		
		if (currentdet.getTags().size() == 0)
			return false;
		
		 
		for ( int i = 0; i < currentdet.getTags().size() ; i++)
		{
			
				if (currentdet.getTags().get(i).contains("linked"))
						return true;
		
		}
		return false;
	}
	
	public boolean isfoursqtagged(UserPreview currentdet)
	{
		if (currentdet == null)
			return false;
		
		if (currentdet.getTags() == null)
			return false;
		
		
		if (currentdet.getTags().size() == 0)
			return false;
		
		 
		for ( int i = 0; i < currentdet.getTags().size() ; i++)
		{
			
				if (currentdet.getTags().get(i).contains("foursq"))
						return true;
		
		}
		return false;
	}
	
	public boolean isPersontagged(UserPreview currentdet, String whatTag)
	{
		if (currentdet == null)
			return false;
		
		if (currentdet.getTags() == null)
			return false;
		
		
		if (currentdet.getTags().size() == 0)
			return false;
		
		 
		for ( int i = 0; i < currentdet.getTags().size() ; i++)
		{
			
				if (currentdet.getTags().get(i).contains(whatTag))
						return true;
		
		}
		return false;
		
		
	}
	
	public void initialisepeoplelist()
	{
		log.info("initialisepeoplelist called");
		this.setSuggestions(getNzoneClient().getSuggestedList(this.isCurrentMainView()));
		log.info("initialisepeoplelist end");
	}
	
	

}
                    