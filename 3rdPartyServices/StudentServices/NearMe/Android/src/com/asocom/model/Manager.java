/*
 * 
 */
package com.asocom.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;

import com.asocom.tools.DatabaseManager;
import com.asocom.tools.NonExistentCommunityException;
import com.asocom.tools.NonExistentUserException;
import com.asocom.tools.Server;
import com.asocom.tools.Tools;

// TODO: Auto-generated Javadoc
/**
 * The Class Manager.
 */
public class Manager {

	/** The db. */
	private static DatabaseManager db;

	/** Todos los usuarios registrados en el telefono. */
	private static ArrayList<String> phoneUsers = new ArrayList<String>();

	/**
	 * Datos del usuario registrado en el telefono que esta activo DataUSER:
	 * get(0) = name get(1) = description get(2) = DATE_OF_BIRTH get(3) = GENDER
	 * get(4) = EMAIL get(5) = PASSWORD get(6) = IMAGE get(7) = PROFILE.
	 */
	private static ArrayList<String> currentPhoneUser = new ArrayList<String>();

	/** The current user profile. */
	private static String currentUserProfile;

	/** The friends. */
	private static ArrayList<User> friends = new ArrayList<User>();

	/** The all users. */
	private static ArrayList<User> allUsers = new ArrayList<User>();

	/** The communities. */
	private static List<Community> communities = new ArrayList<Community>();
	/**
	 * status de la cuenta: 0 icono azul 1 icono amarillo 2 icono rojo 3 icono
	 * blanco y negro.
	 */
	private static int currentStatus;

	/** The int value. */
	private static int intValue;

	/** The String value. */
	private static String StringValue;

	/** The name current activity. */
	private static String nameCurrentActivity;

	/** The current activity. */
	private static Context currentActivity;

	/** The current user. */
	private static User currentUser;

	/** The current community. */
	private static Community currentCommunity;

	/** The current user ip. */
	private static String currentUserIP;

	/* the current ssid */
	public static String getSSID() {
		if (getCurrentActivity() == null)
			return "dame";
		String res = ((WifiManager) getCurrentActivity().getSystemService(
				Activity.WIFI_SERVICE)).getConnectionInfo().getBSSID();
		if (res == null)
			res = "Online Social Network";
		return res;
	}

	/**
	 * Instantiates a new manager.
	 */
	public Manager() {

	}

	/**
	 * Close all.
	 */

	public static void closeAll() {
		Server.timer.cancel();
		phoneUsers = new ArrayList<String>();
		currentPhoneUser = new ArrayList<String>();
		friends = new ArrayList<User>();
		allUsers = new ArrayList<User>();
		communities = new ArrayList<Community>();
		currentStatus = 0;
	}

	/**
	 * Inits the.
	 * 
	 * @param context
	 *            the context
	 */
	public static void init(Context context) {
		db = new DatabaseManager(context);
		phoneUsers = new ArrayList<String>();
		currentPhoneUser = new ArrayList<String>();
		friends = new ArrayList<User>();
		allUsers = new ArrayList<User>();
		communities = new ArrayList<Community>();
		currentStatus = 0;

	}

	/**
	 * Gets the db.
	 * 
	 * @return the db
	 */
	public static DatabaseManager getDb() {
		return db;
	}

	/**
	 * Gets the phone users.
	 * 
	 * @return the phone users
	 */
	public static ArrayList<String> getPhoneUsers() {
		phoneUsers = db.getUsers();
		return phoneUsers;
	}

	/**
	 * name, description, DATE_OF_BIRTH, GENDER, EMAIL, PASSWORD, IMAGE,
	 * PROFILE.
	 * 
	 * @return the current phone user
	 */
	public static ArrayList<String> getCurrentPhoneUser() {
		return currentPhoneUser;
	}

	/**
	 * Sets the user.
	 * 
	 * @param email
	 *            the new user
	 */
	public static void setUser(String email) {
		currentPhoneUser = Manager.getDb().getUserAsArray(email);
		ArrayList<String> friendsString = Manager.getDb().getFriendAsArray(
				email);
		//currentUserIP = Tools.getLocalIpAddress() + phoneUsers.get(0);
		currentUserIP=email;
		for (int i = 0; i < friendsString.size() / db.NUMBER_OF_FIELD; i++) {
			User friend = new User(friendsString.get(i * db.NUMBER_OF_FIELD),
					friendsString.get(i * db.NUMBER_OF_FIELD + 1),
					friendsString.get(i * db.NUMBER_OF_FIELD + 2),
					friendsString.get(i * db.NUMBER_OF_FIELD + 3),
					friendsString.get(i * db.NUMBER_OF_FIELD + 4),
					Integer.parseInt(friendsString.get(i * db.NUMBER_OF_FIELD
							+ 6)), 0);
			friends.add(friend);
		}
	}

	/**
	 * Gets the all users.
	 * 
	 * @return the all users
	 */
	public static ArrayList<User> getAllUsers() {
		return allUsers;
	}

	/**
	 * Adds the users.
	 * 
	 * @param user
	 *            the user
	 */
	public static void addUsers(User user) {
		allUsers.add(user);
		for (int i = 0; i < friends.size(); i++) {
			if (friends.get(i).getEmail().equals(user.getEmail())) {
				user.setFriend(true);
			}
		}
	}

	/**
	 * Gets the friends.
	 * 
	 * @return the friends
	 */
	public static ArrayList<User> getFriends() {
		return friends;
	}

	/**
	 * Adds the friend.
	 * 
	 * @param friend
	 *            the friend
	 */
	public static void addFriend(User friend) {
		friend.setFriend(true);
		friends.add(friend);
		db.addFriend(friend.getName(), friend.getDescription(),
				friend.getDateOfBirth(), friend.getGender(), friend.getEmail(),
				currentPhoneUser.get(4), "" + friend.getImage());
		// Main.getAllUsers().get(Main.getIntValue()).setFriend(true);
	}

	/**
	 * Removes the friend.
	 * 
	 * @param friend
	 *            the friend
	 */
	public static void removeFriend(User friend) {
		friend.setFriend(false);
		db.deleteFriend(friend.getEmail());
		Manager.getFriends().remove(friend);
	}

	/**
	 * Gets the communities.
	 * 
	 * @return the communities
	 */
	public static List<Community> getCommunities() {
		return communities;
	}

	/**
	 * Adds the community.
	 * 
	 * @param community
	 *            the community
	 */
	public static void addCommunity(Community community) {
		Community prev=null;
		for(Community com:communities){
			if(com.getCommunityName().equals(community.getCommunityName())){
				prev=com;
				break;
			}
		}
		if(prev==null)
			communities.add(community);
		else{
			if(prev.lastUpdatedTime<community.lastUpdatedTime){
				communities.remove(prev);
				communities.add(community);
			}
		}
	}

	/**
	 * Gets the int value.
	 * 
	 * @return the int value
	 */
	public static int getIntValue() {
		return intValue;
	}

	/**
	 * Sets the int value.
	 * 
	 * @param i
	 *            the new int value
	 */
	public static void setIntValue(int i) {
		intValue = i;
	}

	/**
	 * Gets the current status.
	 * 
	 * @return the current status
	 */
	public static int getCurrentStatus() {
		return currentStatus;
	}

	/**
	 * Sets the current status.
	 * 
	 * @param value
	 *            the new current status
	 */
	public static void setCurrentStatus(int value) {
		currentStatus = value;
	}

	/**
	 * dado una lista de usuarios y un email me retorna la posicion del usuario
	 * con ese email dentro de la lista si el usuario no existe retorna -1.
	 * 
	 * @param otherUser
	 *            the other user
	 * @param value
	 *            the value
	 * @return the int
	 */
	public static int idSearch(ArrayList<User> otherUser, String value) {
		for (int i = 0; i < otherUser.size(); i++) {
			if (otherUser.get(i).getEmail().equals(value))
				return i;
		}
		return -1;
	}

	/**
	 * Gets the name current activity.
	 * 
	 * @return the name current activity
	 */
	public static String getNameCurrentActivity() {
		return nameCurrentActivity;
	}

	/**
	 * Sets the name current activity.
	 * 
	 * @param currentActivity
	 *            the new name current activity
	 */
	public static void setNameCurrentActivity(String currentActivity) {
		Manager.nameCurrentActivity = currentActivity;
	}

	/**
	 * Gets the current user.
	 * 
	 * @return the current user
	 */
	public static User getCurrentUser() {
		return currentUser;
	}

	public static String getRegisteredUserEmail() {
		return allUsers.get(0).getEmail();
	}

	/**
	 * Gets the current user pos.
	 * 
	 * @return the current user pos
	 * @throws NonExistentUserException
	 *             the non existent user exception
	 */
	public static int getCurrentUserPos() throws NonExistentUserException {

		if (allUsers.contains(currentUser)) {
			return allUsers.indexOf(currentUser);
		}
		throw new NonExistentUserException();

	}

	/**
	 * Sets the current user.
	 * 
	 * @param currentUser
	 *            the new current user
	 */
	public static void setCurrentUser(User currentUser) {
		Manager.currentUser = currentUser;
	}

	/**
	 * Gets the current community.
	 * 
	 * @return the current community
	 */
	public static Community getCurrentCommunity() {
		return currentCommunity;
	}

	/**
	 * Gets the current community pos.
	 * 
	 * @return the current community pos
	 * @throws NonExistentCommunityException
	 *             the non existent community exception
	 */
	public static int getCurrentCommunityPos()
			throws NonExistentCommunityException {

		if (communities.contains(currentCommunity)) {
			return communities.indexOf(currentCommunity);
		}

		throw new NonExistentCommunityException();
	}

	/**
	 * Sets the current community.
	 * 
	 * @param currentCommunity
	 *            the new current community
	 */
	public static void setCurrentCommunity(Community currentCommunity) {
		Manager.currentCommunity = currentCommunity;
	}

	/**
	 * Gets the current activity.
	 * 
	 * @return the current activity
	 */
	public static Context getCurrentActivity() {
		return currentActivity;
	}

	/**
	 * Sets the current activity.
	 * 
	 * @param currentActivity
	 *            the new current activity
	 */
	public static void setCurrentActivity(Context currentActivity) {
		Manager.currentActivity = currentActivity;
	}

	/**
	 * Gets the current user ip.
	 * 
	 * @return the current user ip
	 */
	public static String getCurrentUserIP() {
		return currentUserIP;
	}

	/**
	 * Gets the current user profile.
	 * 
	 * @return the current user profile
	 */
	public static String getCurrentUserProfile() {
		return currentUserProfile;
	}

	/**
	 * Sets the current user profile.
	 * 
	 * @param currentUserProfile
	 *            the new current user profile
	 */
	public static void setCurrentUserProfile(String currentUserProfile) {
		Manager.currentUserProfile = currentUserProfile;
	}

	/**
	 * Gets the string value.
	 * 
	 * @return the string value
	 */
	public static String getStringValue() {
		return StringValue;
	}

	/**
	 * Sets the string value.
	 * 
	 * @param stringValue
	 *            the new string value
	 */
	public static void setStringValue(String stringValue) {
		StringValue = stringValue;
	}

}