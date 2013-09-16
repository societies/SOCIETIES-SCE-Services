package si.stecce.societies.crowdtasking.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import si.stecce.societies.crowdtasking.api.RESTful.impl.TagAPI;
import si.stecce.societies.crowdtasking.api.RESTful.impl.UsersAPI;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Embed;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;

@Embed
class TrustRelationship {
	public String entityType;
	public String entityId;
	public String valueType;
	public Double trustValue;
	//public Date timestamp;
}

@Embed
class ConnectedAccount {
	@Index private AuthenticationPrvider provider;
	@Index private String federatedIdentity;
	@Index private String userId;
	private String nickName;
	private String email;
	
	public ConnectedAccount() {
	}
	
	public ConnectedAccount(AuthenticatedUser authenticatedUser) {
    	this.federatedIdentity = authenticatedUser.getFederatedIdentity() == null ? "gugl" : authenticatedUser.getFederatedIdentity();
		this.provider = AuthenticationPrvider.getAuthenticationPrvider(federatedIdentity);
		this.userId = authenticatedUser.getUserId();
		this.nickName = authenticatedUser.getNickName();
		this.email = authenticatedUser.getEmail();
	}
	
	@Override
	public String toString() {
		if ("SOCIETIES".equalsIgnoreCase(federatedIdentity)) {
			return federatedIdentity+":"+userId;
		}
		return federatedIdentity+":"+nickName;
	}

	@Override
	public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
		if (obj instanceof ConnectedAccount) {
			ConnectedAccount ca = (ConnectedAccount)obj;
			return (ca.getUserId().equalsIgnoreCase(userId) && ca.getNickName().equalsIgnoreCase(nickName));
		}
		return false;
	}

	@Override
    public int hashCode() {
    	int iConstant = 37;
        int iTotal = 17;
        for (int i = 0; i < userId.length(); i++) {
        	iTotal = iTotal * iConstant + userId.charAt(i);
        }
        for (int i = 0; i < nickName.length(); i++) {
        	iTotal = iTotal * iConstant + nickName.charAt(i);
        }
        return iTotal;
    }
    
	public String getFederatedIdentity() {
		return federatedIdentity;
	}
	
	public void setFederatedIdentity(String federatedIdentity) {
		this.federatedIdentity = federatedIdentity;
	}
	
	public AuthenticationPrvider getProvider() {
		return provider;
	}

	public void setProvider(AuthenticationPrvider provider) {
		this.provider = provider;
	}
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getNickName() {
		return nickName;
	}
	
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}

@Entity
public class CTUser {
	@Id private Long id = null;
	// twitter, fb, openId, ...
	private String firstName, lastName;
	private String nickname;
	private String email;
	private List<String> interests;
	@Index private Long karma = 1L;
	private List<ConnectedAccount> connectedAccounts;
	private List<TrustRelationship> trustRelationships;
	private Date lastLogin;
	@Load private Ref<NotificationSettings> notificationSettingsRef;
	@Ignore private NotificationSettings notifications;
	private String picUrl;
	private Date checkIn;
	private Long spaceId;	// kar String?
	private boolean admin=false;
	@Ignore private ApplicationSettings applicationSettings;

	public CTUser() {
	}

	public CTUser(AuthenticatedUser authenticatedUser) {
		this.email = authenticatedUser.getEmail();
		this.nickname = authenticatedUser.getNickName();
		this.firstName = authenticatedUser.getFirstName();
		this.lastName = authenticatedUser.getLastName();
		connectAccount(authenticatedUser);
	}

	private void connectAccount(AuthenticatedUser authenticatedUser) {
		ConnectedAccount connectedAccount = new ConnectedAccount(authenticatedUser);
		if (connectedAccounts == null) {
			connectedAccounts = new ArrayList<ConnectedAccount>();
		}
		// add if not exist
		if (!connectedAccounts.contains(connectedAccount)) {
			connectedAccounts.add(connectedAccount);
		}
	}

    public void correctProvider() {
    	if (connectedAccounts != null && connectedAccounts.get(0).getProvider() == AuthenticationPrvider.UNKNOWN) {
    		ConnectedAccount ca = connectedAccounts.get(0);
    		ca.setProvider(AuthenticationPrvider.getAuthenticationPrvider(ca.getFederatedIdentity()));
    	}
    }
   
    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public void setInterests(String[] interestStrings) {
/**		interestsKeys = new ArrayList<Key<Tag>>();
		ArrayList<Tag> tagList = new ArrayList<Tag>();
		HashMap<String, Tag> tagMap = TagAPI.getTagsMap();
		for (int i=0; i<interestStrings.length; i++) {
			Tag tag = tagMap.get(interestStrings[i].trim());
			if (tag == null) {
				tag = new Tag(interestStrings[i].trim());
			}
			tag.setInterestFrequency(tag.getInterestFrequency()+1);
			tagList.add(tag);
		}
		TagAPI.updateTags(tagList);
		for (Tag tag:tagList) {
			Long id = tag.getId();
			if (id == null) {
				System.out.println("nimam id-ja");
			}
			interestsKeys.add(new Key<Tag>(Tag.class, tag.getId()));
		}*/
		
		// decrease frequency for old interests
		if (interests != null) {
			for (String interest:interests) {
				Tag tag = TagAPI.getTag(interest);
				if (tag != null) {
					tag.setInterestFrequency(tag.getInterestFrequency()-1);
					TagAPI.updateTag(tag);
				}
			}
		}
		
		interests = new ArrayList<String>();
		ArrayList<Tag> tagList = new ArrayList<Tag>();
		Map<String, Tag> tagMap = TagAPI.getTagsMap();
		for (int i=0; i<interestStrings.length; i++) {
			String interest = interestStrings[i].trim();
			Tag tag = tagMap.get(interest);
			if (tag == null) {
				tag = new Tag(interest);
			}
			tag.setInterestFrequency(tag.getInterestFrequency()+1);
			interests.add(interest);
			tagList.add(tag);
		}
		TagAPI.updateTags(tagList);
	}

	public List<String> getInterests() {
		return interests;
	}

	public void setInterests(List<String> interests) {
		this.interests = interests;
	}

	public Long getKarma() {
		return karma;
	}

	public void setKarma(Long karma) {
		this.karma = karma;
	}

	public List<ConnectedAccount> getConnectedAccounts() {
		return connectedAccounts;
	}

	public void setConnectedAccounts(List<ConnectedAccount> connectedAccounts) {
		this.connectedAccounts = connectedAccounts;
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	public NotificationSettings getNotifications() {
		if (notifications != null) {
			return notifications;
		}
		if (notificationSettingsRef != null) {
			notifications = notificationSettingsRef.get();
		}
		if (notifications == null) {
			notifications = new NotificationSettings();
		}
		return notifications;
	}

	public void setNotifications(NotificationSettings notifications) {
		notificationSettingsRef = UsersAPI.saveNotifications(notifications);
	}

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

    public String getUserName() {
    	String name = "";
    	if (getFirstName() != null && !"".equalsIgnoreCase(getFirstName())) {
    		name = getFirstName();
    	}
    	if (getLastName() != null && !"".equalsIgnoreCase(getLastName())) {
    		if (!"".equalsIgnoreCase(name)) {
    			name += " ";
    		}
   			name += getLastName();
    	}
		if ("".equalsIgnoreCase(name)) {
	    	if (getNickname() != null && !"".equalsIgnoreCase(getNickname())) {
	    		name = getNickname();
	    	}
	    	else {
	    		name = getEmail();
	    	}
		}
    	return name;
    }

	public Date getCheckIn() {
		return checkIn;
	}

	public void setCheckIn(Date lastCheckIn) {
		this.checkIn = lastCheckIn;
	}

	public Long getSpaceId() {
		return spaceId;
	}

	public void setSpaceId(Long spaceId) {
		this.spaceId = spaceId;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public ApplicationSettings getApplicationSettings() {
		return applicationSettings;
	}

	public void setApplicationSettings(ApplicationSettings appSettings) {
		this.applicationSettings = appSettings;
	}

	public List<TrustRelationship> getTrustRelationships() {
		return trustRelationships;
	}

	public void setTrustRelationships(String trustRelationshipsJSON) {
		Gson gson = new Gson();
		List<TrustRelationship> trustRelationships = gson.fromJson(trustRelationshipsJSON, new TypeToken<List<TrustRelationship>>(){}.getType());
		this.trustRelationships = trustRelationships;
	}

	public Double getTrustValueForIdentity(String id) {
		// TODO: to je bilo na brzino (v petek popoldan), izboljšaj!
		if (trustRelationships == null) {
			return -1.0;
		}
		for (TrustRelationship trustRelationship:trustRelationships) {
			if (trustRelationship.entityId.equalsIgnoreCase(id))
				return trustRelationship.trustValue;
		}
		return -1.0;
	}
	
	public String getSocietiesEntityId() {
		// TODO: tudi to je bilo na brzino (v petek še bolj pozno popoldan), izboljšaj!
		if (connectedAccounts != null) {
			for (ConnectedAccount connectedAccount:connectedAccounts) {
				if (connectedAccount.getFederatedIdentity().equalsIgnoreCase("SOCIETIES")) {
					return connectedAccount.getUserId();
				}
			}
		}
		return "";
	}
}