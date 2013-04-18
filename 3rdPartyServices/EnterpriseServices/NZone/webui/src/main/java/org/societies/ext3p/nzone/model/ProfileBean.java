package org.societies.ext3p.nzone.model;  
  
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.ext3p.nzone.NZoneConsts;
import org.societies.api.ext3p.nzone.client.INZoneClient;
import org.societies.api.ext3p.schema.nzone.ShareInfo;
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
    private String sex;
    private String company;
    private String position;
    private String facebookid;
    private String linkedinid;
    private String twitterid;
    private String foursqid;
    private String googleplusid;
    private List<String> selectedInterests;
    private boolean profilemissing;
    
    private boolean sharecompany;
    private boolean sharesns;
    private boolean shareinterests;
    private boolean sharepersonal;
    
    
    
   
    public ProfileBean() {
		super();
		selectedInterests = new ArrayList<String>();
	}
	public boolean isSharecompany() {
		return sharecompany;
	}
	public void setSharecompany(boolean sharecompany) {
		this.sharecompany = sharecompany;
	}
	/**
	 * @return the sharesns
	 */
	public boolean isSharesns() {
		return sharesns;
	}
	/**
	 * @param sharesns the sharesns to set
	 */
	public void setSharesns(boolean sharesns) {
		this.sharesns = sharesns;
	}
	/**
	 * @return the shareinterests
	 */
	public boolean isShareinterests() {
		return shareinterests;
	}
	/**
	 * @param shareinterests the shareinterests to set
	 */
	public void setShareinterests(boolean shareinterests) {
		this.shareinterests = shareinterests;
	}
	/**
	 * @return the sharepersonal
	 */
	public boolean isSharepersonal() {
		return sharepersonal;
	}
	/**
	 * @param sharepersonal the sharepersonal to set
	 */
	public void setSharepersonal(boolean sharepersonal) {
		this.sharepersonal = sharepersonal;
	}
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
			setPosition(myDets.getPosition());
			setSex(myDets.getSex());
			
			if (myDets.getFacebookID() == null)
				this.setFacebookid("");
			else
				this.setFacebookid(myDets.getFacebookID());
			
			if (myDets.getLinkedInID() == null)
				this.setLinkedinid("");
			else
				this.setLinkedinid(myDets.getLinkedInID());
			
			if (myDets.getFoursqID() == null)
				this.setFoursqid("");
			else
				this.setFoursqid(myDets.getFoursqID());
			
			if (myDets.getGoogleplusID() == null)
				this.setGoogleplusid("");
			else
				this.setGoogleplusid(myDets.getGoogleplusID());
			
			if (myDets.getTwitterID() == null)
				this.setTwitterid("");
			else
				this.setTwitterid(myDets.getTwitterID());
			
			if (myDets.getInterests() != null)
				setSelectedInterests(myDets.getInterests());
			
		}
		
		//Default to showing nothing until we read value
		this.setSharepersonal(false);
		this.setSharecompany(false);
		this.setSharesns(false);
		this.setShareinterests(false);
		
		ShareInfo info = getNzoneClient().getShareInfo("0");
		if ((info != null) && (info.getShareHash() != null))
		{
			
			 if ((info.getShareHash() & NZoneConsts.SHARE_PERSONAL) == NZoneConsts.SHARE_PERSONAL)
				 this.setSharepersonal(true);
			
			 if ((info.getShareHash() & NZoneConsts.SHARE_EMPLOYMENT)  == NZoneConsts.SHARE_EMPLOYMENT)
				 this.setSharecompany(true);
			 
			 if ((info.getShareHash() & NZoneConsts.SHARE_SOCIAL)  == NZoneConsts.SHARE_SOCIAL)
				 this.setSharesns(true);
			
			 if ((info.getShareHash() & NZoneConsts.SHARE_INTERESTS)  == NZoneConsts.SHARE_INTERESTS)
				 this.setShareinterests(true);
			
				 
		}
			
		
		
		log.info("loadProfileDetails called end");
	//	getNzoneClient().userViewingPreferredProfile();
	//	getNzoneClient().userSharedWithViewPreferredProfile();
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
	/**
	 * @return the position
	 */
	public String getPosition() {
		return position;
	}
	/**
	 * @param position the position to set
	 */
	public void setPosition(String position) {
		this.position = position;
	}
	/**
	 * @return the sex
	 */
	public String getSex() {
		return sex;
	}
	/**
	 * @param sex the sex to set
	 */
	public void setSex(String sex) {
		this.sex = sex;
	}
	/**
	 * @return the selectedInterests
	 */
	public List<String> getSelectedInterests() {
		return selectedInterests;
	}
	/**
	 * @param selectedInterests the selectedInterests to set
	 */
	public void setSelectedInterests(List<String> selectedInterests) {
		this.selectedInterests = selectedInterests;
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
	 
	
	
	public void addMessage() {  
		 	log.info("addMessage called");
	        String summary = this.isSharecompany() ? "Checked" : "Unchecked";  
	  
	        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(summary));  
	   }  
	 

	public String saveProfile()
	{
		saveShareInfo();
		saveInterests();
		return "gotomain";
	}
	 public void saveShareInfo() {
		 ShareInfo info = new ShareInfo();
		 int shareflag = 0;
		 
		 if (this.isSharepersonal())
			 shareflag += NZoneConsts.SHARE_PERSONAL;
		 if (this.isSharecompany())
			 shareflag += NZoneConsts.SHARE_EMPLOYMENT;
		 if (this.isSharesns())
			 shareflag += NZoneConsts.SHARE_SOCIAL;
		 if (this.isShareinterests())
			 shareflag += NZoneConsts.SHARE_INTERESTS;
		 
		 
		 info.setFriendid("0");		
		 info.setShareHash(shareflag);
		 
		 getNzoneClient().updateShareInfo(info);
		 
	 }
	 
	 public void saveInterests() {
		 
		getNzoneClient().updateMyInterests(this.getSelectedInterests());
			
	 }
	 
	 public String getInterestString(int index)
	 {
		 if (selectedInterests == null)
			 return " ";
		 if (selectedInterests.size() == 0)
			 return " ";
		 if (index >= selectedInterests.size())
			 return " ";
		 
		 if (selectedInterests.get(index) == null)
			 return " ";
		 
		if (selectedInterests.get(index).contains("cloud"))
			 return "Cloud Computing";
		if (selectedInterests.get(index).contains("internet"))
			 return "Internet Of Things"; 
		if (selectedInterests.get(index).contains("future"))
			 return "Future of the Internet"; 
		if (selectedInterests.get(index).contains("entre"))
			 return "Green IT";  
		 return " ";
	 }
	 
	 

	
}  
