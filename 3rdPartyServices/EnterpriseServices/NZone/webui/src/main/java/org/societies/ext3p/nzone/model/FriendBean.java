package org.societies.ext3p.nzone.model;  
  
import java.io.Serializable;   
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ApplicationScoped;
import javax.faces.event.ValueChangeEvent;
import javax.faces.event.ValueChangeListener;

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
@Controller(value = "friendBean") 
public class FriendBean implements Serializable, ValueChangeListener {
	
	private static Logger log = LoggerFactory.getLogger(FriendBean.class);
	
	@Autowired
	INZoneClient nzoneClient; 
	
	private String friendid; 
	private String name;   
    private String company;
    
    private String email;
    private String sex;
   
    private String position;
    private String facebookid;
    private String linkedinid;
    private String twitterid;
    private String foursqid;
    private String googleplusid;
    
    private List<String> selectedInterests;
    
    
    private String preferredcompany;
    private boolean pref;
    
    private boolean sharecompany;
    private boolean sharesns;
    private boolean shareinterests;
    private boolean sharepersonal;
    
    private String shareinfoMessage;
    private String shareinfoMessageType;
       
    
    public String getShareinfoMessageType() {
		return shareinfoMessageType;
	}

	public void setShareinfoMessageType(String shareinfoMessageType) {
		this.shareinfoMessageType = shareinfoMessageType;
	}

	FriendBean()
    {
    	preferredcompany = new String("0");
    	pref = false;
    	setShareinfoMessage(new String(""));
    	setShareinfoMessageType(new String(""));
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
		this.setEmail(det.getEmail());
		this.setPosition(det.getPosition());
		this.setSex(det.getSex());
				
		if (det.getFacebookID() == null)
			this.setFacebookid("");
		else
			this.setFacebookid(det.getFacebookID());
		
		if (det.getLinkedInID() == null)
			this.setLinkedinid("");
		else
			this.setLinkedinid(det.getLinkedInID());
		
		if (det.getFoursqID() == null)
			this.setFoursqid("");
		else
			this.setFoursqid(det.getFoursqID());
		
		if (det.getGoogleplusID() == null)
			this.setGoogleplusid("");
		else
			this.setGoogleplusid(det.getGoogleplusID());
		
		if (det.getTwitterID() == null)
			this.setTwitterid("");
		else
			this.setTwitterid(det.getTwitterID());
		
		if (det.getInterests() != null)
			setSelectedInterests(det.getInterests());
		else
		{
			List<String> emptyList = new ArrayList<String>();
			setSelectedInterests(emptyList);
		}
			
		
		
		boolean perf = getNzoneClient().isPreferred("company", det.getCompany());
		if (perf == true)
			this.setPreferredcompany("1");
		else
			this.setPreferredcompany("0");
		
		this.loadShareInfo();
		
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

	
	  public boolean isSharecompany() {
			return sharecompany;
		}
		public void setSharecompany(boolean sharecompany) {
			this.sharecompany = sharecompany;
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

		public String getSex() {
			return sex;
		}

		public void setSex(String sex) {
			this.sex = sex;
		}

		public String getPosition() {
			return position;
		}

		public void setPosition(String position) {
			this.position = position;
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

		public List<String> getSelectedInterests() {
			return selectedInterests;
		}

		public void setSelectedInterests(List<String> selectedInterests) {
			this.selectedInterests = selectedInterests;
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

	/**
	 * @return the shareinfoMessage
	 */
	public String getShareinfoMessage() {
		return shareinfoMessage;
	}

	/**
	 * @param shareinfoMessage the shareinfoMessage to set
	 */
	public void setShareinfoMessage(String shareinfoMessage) {
		this.shareinfoMessage = shareinfoMessage;
	}

	public void valueChanged(ValueChangeEvent vce){
		 
		 log.info("Called  valueChanged with [" + vce.toString() + "]");
	   }
	   
	 public void processValueChange(ValueChangeEvent ce){
		 log.info("Called  processValueChange with [" + ce.toString() + "]");
	 }

	 
	 public void loadShareInfo() {
		 log.info("Loading share info for this friend" + this.getFriendid());
		 ShareInfo info = getNzoneClient().getShareInfo(this.getFriendid());
		 this.setSharepersonal(false);
		 this.setSharecompany(false);
		 this.setSharesns(false);
		 this.setShareinterests(false);
		 
		 this.setShareinfoMessageType("");
		 
		 if ((info != null) && (!info.isDefaultShareValue()))
		 {
				this.setShareinfoMessage("This is the information you have previous choosen to share with " + this.getName());
			
				if ((info.getShareHash() & NZoneConsts.SHARE_PERSONAL) == NZoneConsts.SHARE_PERSONAL)
					this.setSharepersonal(true);
					 	
				if ((info.getShareHash() & NZoneConsts.SHARE_EMPLOYMENT)  == NZoneConsts.SHARE_EMPLOYMENT)
					this.setSharecompany(true);
					  
				if ((info.getShareHash() & NZoneConsts.SHARE_SOCIAL)  == NZoneConsts.SHARE_SOCIAL)
					this.setSharesns(true);
					  
				if ((info.getShareHash() & NZoneConsts.SHARE_INTERESTS)  == NZoneConsts.SHARE_INTERESTS)
					this.setShareinterests(true);
				
				return;
					 
		 }
		 
		 if (this.isPref())
		 {
			 log.info("This is a preferred company, see if we have share info for this company");
			 // check if we have 'learnt before what info to share
			 int sharPref = getNzoneClient().isSharePreferred("company", this.getCompany());
			 if (sharPref != -1)
			 {
				 log.info("This is a preferred company, found shared pref");	
				 if ((sharPref & NZoneConsts.SHARE_PERSONAL) == NZoneConsts.SHARE_PERSONAL)
					 this.setSharepersonal(true);
				 	
				 if ((sharPref & NZoneConsts.SHARE_EMPLOYMENT)  == NZoneConsts.SHARE_EMPLOYMENT)
					 this.setSharecompany(true);
				  
				 if ((sharPref & NZoneConsts.SHARE_SOCIAL)  == NZoneConsts.SHARE_SOCIAL)
					 this.setSharesns(true);
				  
				 if ((sharPref & NZoneConsts.SHARE_INTERESTS)  == NZoneConsts.SHARE_INTERESTS)
					 this.setShareinterests(true);
				 
				 this.setShareinfoMessage("This is the information the system learned that you have previously shared with a person from preferred company "+ this.getCompany());
				 this.setShareinfoMessageType("highlight");
				 return;
			 }
		 
			 
		 } 
		 

		
		 
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
			
		 this.setShareinfoMessage("Use the Dafault Share Proflie : No learnt preferences");

	 }
	 
	 public String saveShareInfo() {
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
		 
		 info.setFriendid(friendid);		
		 info.setShareHash(shareflag);
		 
		 getNzoneClient().updateShareInfo(info);
		 
		 if (this.isPref())
		 {
			 log.info("This is a preferred company, saving shared pref");	
			 getNzoneClient().setAsSharePreferred("company", this.getCompany(), info.getShareHash());
		 }
		 
		return "gotozone";
		 
	 }
	 
	 public String getInterestString(int index)
	 {
		 if (selectedInterests == null)
			 return "";
		 if (index >= selectedInterests.size())
			 return "";
		 
		if (selectedInterests.get(index).contains("cloud"))
			 return "Cloud Computing";
		if (selectedInterests.get(index).contains("internet"))
			 return "Internet Of Things"; 
		if (selectedInterests.get(index).contains("future"))
			 return "Future of the Internet"; 
		if (selectedInterests.get(index).contains("entre"))
			 return "Entrepreneur"; 
		 return "";
	 }
	   
}  
