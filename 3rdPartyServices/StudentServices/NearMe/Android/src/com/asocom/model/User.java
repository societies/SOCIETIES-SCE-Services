package com.asocom.model;

import java.util.ArrayList;

/**
 * 
 */
public class User {

	/**
	 * 
	 */
	private String name;

	/**
	 * 
	 */
	private String description;

	/**
	 * 
	 */
	private String dateOfBirth;

	/**
	 * 
	 */
	private String gender;

	/**
	 * 
	 */
	private String email;

	/**
	 * 
	 */
	private int image;

	/**
	 * 
	 */
	private int status;

	/**
	 * 
	 */
	private ArrayList<Community> userCommunities;

	/**
	 * 
	 */
	private boolean friend;

	/**
	 * 
	 */
	private Chat chat;

	/**
	 * 
	 */
	private int code;

	private String ip;

	private String profile;

	/**
	 * String name, String description, String dateOfBirth, String gender,
	 * String email, int image, int status
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
	 * 
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * 
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * 
	 */
	public String getDateOfBirth() {
		return dateOfBirth;
	}

	/**
	 * 
	 */
	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	/**
	 * 
	 */
	public String getGender() {
		return gender;
	}

	/**
	 * 
	 */
	public void setGender(String gender) {
		this.gender = gender;
	}

	/**
	 * 
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * 
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * 
	 */
	public int getImage() {
		return image;
	}

	/**
	 * 
	 */
	public void setImage(int image) {
		this.image = image;
	}

	/**
	 * 
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * 
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * 
	 */
	public ArrayList<Community> getUserCommunities() {
		return userCommunities;
	}

	/**
	 * 
	 */
	public void addUserCommunities(Community Community) {
		this.userCommunities.add(Community);
	}

	/**
	 * 
	 */
	public boolean isFriend() {
		return friend;
	}

	/**
	 * 
	 */
	public void setFriend(boolean friend) {
		this.friend = friend;
	}

	/**
	 * 
	 */
	public Chat getChat() {
		return chat;
	}

	/**
	 * 
	 */
	public void setChat(Chat chat) {
		this.chat = chat;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	@Override
	public String toString() {
		return "User [name=" + name + ", description=" + description
				+ ", dateOfBirth=" + dateOfBirth + ", gender=" + gender
				+ ", email=" + email + ", image=" + image + ", status="
				+ status + ", friend=" + friend + ", code=" + code + ", ip="
				+ ip + "]";
	}

}