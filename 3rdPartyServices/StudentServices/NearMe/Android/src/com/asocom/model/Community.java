package com.asocom.model;

import java.util.ArrayList;

/**
 * 
 */
public class Community {

	private String communityName;
	private String description;
	private String nameAdministrator;
	private String visibility;
	private String recommendCommunity;
	private String dateOfCreation;
	private String profile;
	private int image;
	private Chat chat;
	private ArrayList<User> users;
	private boolean myCommunity;
	private String id;
	private int code;

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

	public String getCommunityName() {
		return communityName;
	}

	public void setCommunityName(String nameOfCommunity) {
		this.communityName = nameOfCommunity;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getNameAdministrator() {
		return nameAdministrator;
	}

	public void setNameAdministrator(String nameAdministrator) {
		this.nameAdministrator = nameAdministrator;
	}

	public String getVisibility() {
		return visibility;
	}

	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}

	public String getDateOfCreation() {
		return dateOfCreation;
	}

	public void setDateOfCreation(String dateOfCreation) {
		this.dateOfCreation = dateOfCreation;
	}

	public Chat getChat() {
		return chat;
	}

	public void setChat(Chat chat) {
		this.chat = chat;
	}

	public ArrayList<User> getUserList() {
		return users;
	}

	public void setUserList(ArrayList<User> users) {
		this.users = users;
	}

	public void addUser(User user) {
		users.add(user);
	}

	public void deleteUser(String email) {
		int id = Manager.idSearch(users, email);
		if (id != -1)
			users.remove(id);
	}

	public void resetUserList() {
		users = new ArrayList<User>();
	}

	public void setImage(int image) {
		this.image = image;
	}

	public boolean isMyCommunity() {
		return myCommunity;
	}

	public void setMyCommunity(boolean myCommunity) {
		this.myCommunity = myCommunity;
	}

	public int getImage() {
		return this.image;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getId() {
		return id;
	}

	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRecommendCommunity() {
		return recommendCommunity;
	}

	public void setRecommendCommunity(String recommendCommunity) {
		this.recommendCommunity = recommendCommunity;
	}

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
