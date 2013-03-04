package org.societies.ext3p.nzone.model;  
  
import java.io.Serializable;   

import javax.faces.bean.ApplicationScoped;
import javax.faces.event.ValueChangeEvent;
import javax.faces.event.ValueChangeListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.ext3p.nzone.client.INZoneClient;
import org.societies.api.ext3p.schema.nzone.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
  
@SuppressWarnings("serial")
@ApplicationScoped
@Controller(value = "friendBean") 
public class FriendBean implements Serializable, ValueChangeListener {
	
	private static Logger log = LoggerFactory.getLogger(FriendBean.class);
	
	@Autowired
	INZoneClient nzoneClient; 
	
	private String friendid; 
	private String name;   
    private String company;
    private String preferredcompany;
    private boolean pref;
       
    
    FriendBean()
    {
    	preferredcompany = new String("0");
    	pref = false;
    }
    
    public String getFriendid() {
    	log.info("Called  getFriendid returning[" + this.friendid + "]");
		return friendid;
	}

	public void setFriendid(String friendid) {
		log.info("Called  setFriendid to [" + friendid + "]");
		UserDetails det = getNzoneClient().getUserProfile(friendid);
		this.friendid = friendid;
		this.setName(det.getDisplayName());
		this.setCompany(det.getCompany());
		
		boolean perf = getNzoneClient().isPreferred("company", det.getCompany());
		if (perf == true)
			this.setPreferredcompany("1");
		else
			this.setPreferredcompany("0");
			
	}

	public String getName() {   
		log.info("Called  getName returning[" + this.name + "]");
        return this.name;   
    }   
  
    public void setName(String name) {  
    	log.info("Called  setName to [" + name + "]");
        this.name = name;   
    }
    
    
    /**
	 * @return the company
	 */
	public String getCompany() {
		log.info("Called  getCompany returning[" + this.company + "]");
		return this.company;
	}

	/**
	 * @param company the company to set
	 */
	public void setCompany(String company) {
		log.info("Called  setCompany to [" + company + "]");
		this.company = company;
	}

	public INZoneClient getNzoneClient() {
		return nzoneClient;
	}
	public void setNzoneClient(INZoneClient nzoneClient) {
		this.nzoneClient = nzoneClient;
	}
	
	public void setAsPreferred()
	{
		log.info("Called  setAsPreferred");
		pref = true;
		getNzoneClient().setAsPreferred("company", this.getCompany());
	}

	public String getPreferredcompany() {
		log.info("Called  isPreferredcompany returning[" + this.preferredcompany + "]");
		return this.preferredcompany;
	}

	public void setPreferredcompany(String preferredcompany) {
		
		log.info("Called  setPreferredcompany with [" + preferredcompany + "]");
		
		this.preferredcompany = preferredcompany;
		if (preferredcompany.contentEquals("1"))
		{
			pref = true;
		
			getNzoneClient().setAsPreferred("company", this.getCompany());
		}
		else
		{
			getNzoneClient().removeAsPreferred("company", this.getCompany());
			pref = false;
		}
	}
	
	 public boolean isPref() {
		return pref;
	}

	public void setPref(boolean pref) {
		this.pref = pref;
	}

	public void valueChanged(ValueChangeEvent vce){
		 
		 log.info("Called  valueChanged with [" + vce.toString() + "]");
	   }
	   
	 public void processValueChange(ValueChangeEvent ce){
		 log.info("Called  processValueChange with [" + ce.toString() + "]");
	 }

	   
}  
