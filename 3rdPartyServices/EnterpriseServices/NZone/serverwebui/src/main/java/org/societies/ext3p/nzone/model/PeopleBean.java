package org.societies.ext3p.nzone.model;

import java.util.List;

import javax.faces.bean.ApplicationScoped;

import org.societies.api.ext3p.nzone.server.INZoneServer;
import org.societies.api.ext3p.schema.nzone.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@ApplicationScoped
@Controller(value = "peopleBean")
public class PeopleBean {

	@Autowired
	INZoneServer nzoneServer; 
	
	private List<UserDetails> suggestions;
	

	private int zonenumber;
	private String zoneimage;
	private String zoneName;
	private int refreshrate;
	
	public PeopleBean() {
        
		setRefreshrate(60);
	}

	/**
	 * @return the suggestions
	 */
	public List<UserDetails> getSuggestions() {
		
		this.suggestions = getNzoneServer().getZoneMembers(this.zonenumber);
		return this.suggestions;
	}

	/**
	 * @param suggestions the suggestions to set
	 */
	public void setSuggestions(List<UserDetails> suggestions) {
		this.suggestions = suggestions;
	}
	
	public INZoneServer getNzoneServer() {
		return nzoneServer;
	}

	public void setNzoneServer(INZoneServer nzoneServer) {
		this.nzoneServer = nzoneServer;
	}

	
	
	public int getZonenumber() {
		return zonenumber;
	}

	public void setZonenumber(int zonenumber) {
		this.zonenumber = zonenumber;
	}
	
	public String getZoneimage() {
		return zoneimage;
	}

	public void setZoneimage(String zoneimage) {
		this.zoneimage = zoneimage;
	}
	
	public int getRefreshrate() {
		return refreshrate;
	}
	
	public String getZoneName()
	{
		if (this.zoneName == null)
			setZoneName(getNzoneServer().getZoneName(zonenumber));
			
		return this.zoneName;
	}


	public void setZoneName(String zoneName)
	{
		this.zoneName = zoneName;
	}

	
	public void setRefreshrate(int refreshrate) {
		this.refreshrate = refreshrate;
	}
	
		

}
                    