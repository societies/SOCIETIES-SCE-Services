/*
 * 
 */
package com.asocom.model;

import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * The Class User.
 */
public class User {

	/** The name. */
	private String name;

	/** The description. */
	private String description;

	/** The date of birth. */
	private String dateOfBirth;

	/** The gender. */
	private String gender;

	/** The email. */
	private String email;

	/** The image. */
	private int image;

	/** The status. */
	private int status;

	/** The user communities. */
	private ArrayList<Community> userCommunities;

	/** The friend. */
	private boolean friend;

	/** The chat. */
	private Chat chat;

	/** The code. */
	private int code;

	/** The ip. */
	private String ip;

	/** The profile. */
	private String profile;

	/**
	 * String name, String description, String dateOfBirth, String gender,
	 * String email, int image, int status.
	 *
	 * @param name the name
	 * @param description the description
	 * @param dateOfBirth the date of birth
	 * @param gender the gender
	 * @param email the email
	 * @param image the image
	 * @param status the status
	 */
	public User(String name, String description, String dateOfBirth,
			String gender, String email, int image, int status) {
		this.name = name;
		this.description = description;
		this.dateOfBirth = dateOfBirth;
		this.gender = gender;
		this.email = email;
		this.image = image;
		this.status = status;
		this.userCommunities = new ArrayList<Community>();
		this.friend = false;
		this.chat = new Chat();
		this.code = 0;

	}

	/**
	 * Gets the user name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the user name.
	 *
	 * @param name the new user name
	 */
	public void setName(String name) {
		this.name = name;
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
	 * Gets the user's date of birth.
	 *
	 * @return the date of birth
	 */
	public String getDateOfBirth() {
		return dateOfBirth;
	}

	/**
	 * Sets the user's date of birth.
	 *
	 * @param dateOfBirth the new date of birth
	 */
	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	/**
	 * Gets the user gender.
	 *
	 * @return the gender
	 */
	public String getGender() {
		return gender;
	}

	/**
	 * Sets the user gender.
	 *
	 * @param gender the new user gender
	 */
	public void setGender(String gender) {
		this.gender = gender;
	}

	/**
	 * Gets the email.
	 *
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Sets the email.
	 *
	 * @param email the new email
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Gets the image.
	 *
	 * @return the image
	 */
	public int getImage() {
		return image;
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
	 * Gets the status.
	 *
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * Sets the status.
	 *
	 * @param status the new status
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * Gets the user communities.
	 *
	 * @return the user communities
	 */
	public ArrayList<Community> getUserCommunities() {
		return userCommunities;
	}

	/**
	 * Adds the user communities.
	 *
	 * @param Community the community
	 */
	public void addUserCommunities(Community Community) {
		this.userCommunities.add(Community);
	}

	/**
	 * Checks if is friend.
	 *
	 * @return true, if is friend
	 */
	public boolean isFriend() {
		return friend;
	}

	/**
	 * Sets the friend.
	 *
	 * @param friend the new friend
	 */
	public void setFriend(boolean friend) {
		this.friend = friend;
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
	 * Gets the ip.
	 *
	 * @return the ip
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * Sets the ip.
	 *
	 * @param ip the new ip
	 */
	public void setIp(String ip) {
		this.ip = ip;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "User [name=" + name + ", description=" + description
				+ ", dateOfBirth=" + dateOfBirth + ", gender=" + gender
				+ ", email=" + email + ", image=" + image + ", status="
				+ status + ", friend=" + friend + ", code=" + code + ", ip="
				+ ip + "]";
	}

}