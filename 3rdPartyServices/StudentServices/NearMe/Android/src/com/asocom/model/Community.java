/*
 * 
 */
package com.asocom.model;

import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * The Class Community.
 */
public class Community {

	/** The community name. */
	private String communityName;
	
	/** The description. */
	private String description;
	
	/** The name administrator. */
	private String nameAdministrator;
	
	/** The visibility. */
	private String visibility;
	
	/** The recommend community. */
	private String recommendCommunity;
	
	/** The date of creation. */
	private String dateOfCreation;
	
	/** The profile. */
	private String profile;
	
	/** The image. */
	private int image;
	
	/** The chat. */
	private Chat chat;
	
	/** The users. */
	private ArrayList<User> users;
	
	/** The my community. */
	private boolean myCommunity;
	
	/** The id. */
	private String id;
	
	/** The code. */
	private int code;
	
	public long lastUpdatedTime;

	/**
	 * Instantiates a new community.
	 *
	 * @param communityName the community name
	 * @param description the description
	 * @param nameAdministrator the name administrator
	 * @param profile the profile
	 * @param visibility the visibility
	 * @param dateOfCreation the date of creation
	 * @param image the image
	 * @param recommendCommunity the recommend community
	 */
	public Community(String communityName, String description,
			String nameAdministrator, String profile, String visibility,
			String dateOfCreation, int image, String recommendCommunity) {
		super();
		this.communityName = communityName;
		this.description = description;
		this.nameAdministrator = nameAdministrator;
		this.profile = profile;
		this.visibility = visibility;
		this.recommendCommunity = recommendCommunity;
		this.dateOfCreation = dateOfCreation;
		this.image = image;
		this.chat = new Chat();
		this.users = new ArrayList<User>();
		this.myCommunity = false;
		this.code = 0;
		this.id = " ";
	}

	/**
	 * Instantiates a new community.
	 */
	public Community() {
		super();
		this.communityName = "communityName";
		this.description = "description";
		this.nameAdministrator = "nameAdministrator";
		this.profile = "General";
		visibility = "visibility";
		this.dateOfCreation = "dateOfCreation";
		this.image = 21;
		this.chat = new Chat();
		this.users = new ArrayList<User>();
		myCommunity = false;
		this.id = " ";
	}

	/**
	 * Gets the community name.
	 *
	 * @return the community name
	 */
	public String getCommunityName() {
		return communityName;
	}

	/**
	 * Sets the community name.
	 *
	 * @param nameOfCommunity the new community name
	 */
	public void setCommunityName(String nameOfCommunity) {
		this.communityName = nameOfCommunity;
	}

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description.
	 *
	 * @param description the new description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the name administrator.
	 *
	 * @return the name administrator
	 */
	public String getNameAdministrator() {
		return nameAdministrator;
	}

	/**
	 * Sets the name administrator.
	 *
	 * @param nameAdministrator the new name administrator
	 */
	public void setNameAdministrator(String nameAdministrator) {
		this.nameAdministrator = nameAdministrator;
	}

	/**
	 * Gets the visibility.
	 *
	 * @return the visibility
	 */
	public String getVisibility() {
		return visibility;
	}

	/**
	 * Sets the visibility.
	 *
	 * @param visibility the new visibility
	 */
	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}

	/**
	 * Gets the date of creation.
	 *
	 * @return the date of creation
	 */
	public String getDateOfCreation() {
		return dateOfCreation;
	}

	/**
	 * Sets the date of creation.
	 *
	 * @param dateOfCreation the new date of creation
	 */
	public void setDateOfCreation(String dateOfCreation) {
		this.dateOfCreation = dateOfCreation;
	}

	/**
	 * Gets the chat.
	 *
	 * @return the chat
	 */
	public Chat getChat() {
		return chat;
	}

	/**
	 * Sets the chat.
	 *
	 * @param chat the new chat
	 */
	public void setChat(Chat chat) {
		this.chat = chat;
	}

	/**
	 * Gets the user list.
	 *
	 * @return the user list
	 */
	public ArrayList<User> getUserList() {
		return users;
	}

	/**
	 * Sets the user list.
	 *
	 * @param users the new user list
	 */
	public void setUserList(ArrayList<User> users) {
		this.users = users;
	}

	/**
	 * Adds the user.
	 *
	 * @param user the user
	 */
	public void addUser(User user) {
		if(!users.contains(user))
			users.add(user);
	}

	/**
	 * Delete user.
	 *
	 * @param email the email
	 */
	public void deleteUser(String email) {
		int id = Manager.idSearch(users, email);
		if (id != -1)
			users.remove(id);
	}

	/**
	 * Reset user list.
	 */
	public void resetUserList() {
		users = new ArrayList<User>();
	}

	/**
	 * Sets the image.
	 *
	 * @param image the new image
	 */
	public void setImage(int image) {
		this.image = image;
	}

	/**
	 * Checks if is my community.
	 *
	 * @return true, if is my community
	 */
	public boolean isMyCommunity() {
		return myCommunity;
	}

	/**
	 * Sets the my community.
	 *
	 * @param myCommunity the new my community
	 */
	public void setMyCommunity(boolean myCommunity) {
		this.myCommunity = myCommunity;
	}

	/**
	 * Gets the image.
	 *
	 * @return the image
	 */
	public int getImage() {
		return this.image;
	}

	/**
	 * Gets the code.
	 *
	 * @return the code
	 */
	public int getCode() {
		return code;
	}

	/**
	 * Sets the code.
	 *
	 * @param code the new code
	 */
	public void setCode(int code) {
		this.code = code;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Gets the profile.
	 *
	 * @return the profile
	 */
	public String getProfile() {
		return profile;
	}

	/**
	 * Sets the profile.
	 *
	 * @param profile the new profile
	 */
	public void setProfile(String profile) {
		this.profile = profile;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Gets the recommend community.
	 *
	 * @return the recommend community
	 */
	public String getRecommendCommunity() {
		return recommendCommunity;
	}

	/**
	 * Sets the recommend community.
	 *
	 * @param recommendCommunity the new recommend community
	 */
	public void setRecommendCommunity(String recommendCommunity) {
		this.recommendCommunity = recommendCommunity;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Community [communityName=" + communityName + ", description="
				+ description + ", nameAdministrator=" + nameAdministrator
				+ ", Visibility=" + visibility + ", dateOfCreation="
				+ dateOfCreation + ", image=" + image + ", chat=" + chat
				+ ", users=" + users + ", myCommunity=" + myCommunity + ", id="
				+ id + ", code=" + code + "]";
	}

}
