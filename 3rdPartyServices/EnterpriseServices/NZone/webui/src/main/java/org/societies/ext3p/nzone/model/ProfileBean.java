package org.societies.ext3p.nzone.model;  
  
import java.io.Serializable;   

import javax.faces.bean.ApplicationScoped;
import javax.faces.event.ActionEvent;
import javax.faces.event.ComponentSystemEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.ext3p.nzone.client.INZoneClient;
import org.societies.api.ext3p.schema.nzone.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
  
@SuppressWarnings("serial")
@ApplicationScoped
@Controller(value = "profileBean") 
public class ProfileBean implements Serializable {   
	
  
	@Autowired
	INZoneClient nzoneClient; 
	
	private static Logger log = LoggerFactory.getLogger(ProfileBean.class);
	
	
    private String name;
    private String email;
    private String company;
    private String facebookid;
    private String linkedinid;
    private String twitterid;
    private String foursqid;
    private String googleplusid;
    private boolean profilemissing;
       
   
    public INZoneClient getNzoneClient() {
		return nzoneClient;
	}
	public void setNzoneClient(INZoneClient nzoneClient) {
		this.nzoneClient = nzoneClient;
	}
	
	public void intialiseprofilebean(ComponentSystemEvent ev)
	{
		setProfilemissing(!getNzoneClient().isProfileSetup());
		
	}
	
	public boolean isProfilemissing()
	{
		log.info("isProfilemissing called value at start is " + this.profilemissing);
		setProfilemissing(!getNzoneClient().isProfileSetup());
		log.info("isProfilemissing called value at end is " + this.profilemissing);

		return this.profilemissing;
	}
	
	 public void setProfilemissing(boolean profilemissing) {
		 log.info("setProfilemissing called with value" + profilemissing);
		this.profilemissing = profilemissing;
	}
	 
  
	public void loadprofile(ComponentSystemEvent ev)
	{
		log.info("loadProfileDetails called start");
		UserDetails myDets = getNzoneClient().getMyProfile();
		
		if (myDets != null)
		{
			setName(myDets.getDisplayName());
			setCompany(myDets.getCompany());
			setEmail(myDets.getEmail());
			this.setFacebookid(myDets.getFacebookID());
			this.setLinkedinid(myDets.getLinkedInID());
			this.setFoursqid(myDets.getFoursqID());
			this.setGoogleplusid(myDets.getGoogleplusID());
			this.setTwitterid(myDets.getTwitterID());
		}
		log.info("loadProfileDetails called end");
	}
	
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	/**
	 * @return the company
	 */
	public String getCompany() {
		return company;
	}
	/**
	 * @param company the company to set
	 */
	public void setCompany(String company) {
		this.company = company;
	}
	public String getFacebookid() {
		return facebookid;
	}
	public void setFacebookid(String facebookid) {
		this.facebookid = facebookid;
	}
	public String getLinkedinid() {
		return linkedinid;
	}
	public void setLinkedinid(String linkedinid) {
		this.linkedinid = linkedinid;
	}
	public String getTwitterid() {
		return twitterid;
	}
	public void setTwitterid(String twitterid) {
		this.twitterid = twitterid;
	}
	public String getFoursqid() {
		return foursqid;
	}
	public void setFoursqid(String foursqid) {
		this.foursqid = foursqid;
	}
	public String getGoogleplusid() {
		return googleplusid;
	}
	public void setGoogleplusid(String googleplusid) {
		this.googleplusid = googleplusid;
	}
	 

}  
