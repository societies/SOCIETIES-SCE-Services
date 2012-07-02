package com.asocom.model;

import java.util.ArrayList;
import android.content.Context;

import com.asocom.tools.DatabaseManager;
import com.asocom.tools.NonExistentCommunityException;
import com.asocom.tools.NonExistentUserException;
import com.asocom.tools.Server;
import com.asocom.tools.Tools;

/**
 *
 */
public class Manager {

	/**
     *
     */
	private static DatabaseManager db;

	/**
	 * Todos los usuarios registrados en el telefono
	 */
	private static ArrayList<String> phoneUsers = new ArrayList<String>();

	/**
	 * Datos del usuario registrado en el telefono que esta activo DataUSER:
	 * get(0) = name get(1) = description get(2) = DATE_OF_BIRTH get(3) = GENDER
	 * get(4) = EMAIL get(5) = PASSWORD get(6) = IMAGE get(7) = PROFILE
	 */
	private static ArrayList<String> currentPhoneUser = new ArrayList<String>();

	private static String currentUserProfile;
	/**
     * 
     */
	private static ArrayList<User> friends = new ArrayList<User>();

	/**
     * 
     */
	private static ArrayList<User> allUsers = new ArrayList<User>();

	/**
     * 
     */
	private static ArrayList<Community> communities = new ArrayList<Community>();

	/**
	 * status de la cuenta: 0 icono azul 1 icono amarillo 2 icono rojo 3 icono
	 * blanco y negro
	 */
	private static int currentStatus;

	/**
	 * 
	 */
	private static int intValue;

	/**
	 * 
	 */
	private static String StringValue;

	/**
	 * 
	 */
	private static String nameCurrentActivity;

	/**
	 * 
	 */
	private static Context currentActivity;

	/**
	 * 
	 */
	private static User currentUser;

	/**
	 * 
	 */
	private static Community currentCommunity;

	/**
	 * 
	 */
	private static String currentUserIP;

	/**
     * 
     */
	public Manager() {

	}

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
     * 
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
     * 
     */
	public static DatabaseManager getDb() {
		return db;
	}

	/**
     *
     */
	public static ArrayList<String> getPhoneUsers() {
		phoneUsers = db.getUsers();
		return phoneUsers;
	}

	/**
	 * name, description, DATE_OF_BIRTH, GENDER, EMAIL, PASSWORD, IMAGE, PROFILE
	 */
	public static ArrayList<String> getCurrentPhoneUser() {
		return currentPhoneUser;
	}

	/**
     *
     */
	public static void setUser(String email) {
		currentPhoneUser = Manager.getDb().getUserAsArray(email);
		ArrayList<String> friendsString = Manager.getDb().getFriendAsArray(
				email);
		currentUserIP = Tools.getLocalIpAddress() + phoneUsers.get(0);

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

	public static ArrayList<User> getAllUsers() {
		return allUsers;
	}

	public static void addUsers(User user) {
		allUsers.add(user);
		for (int i = 0; i < friends.size(); i++) {
			if (friends.get(i).getEmail().equals(user.getEmail())) {
				user.setFriend(true);
			}
		}
	}

	public static ArrayList<User> getFriends() {
		return friends;
	}

	/**
	 * 	
	 */
	public static void addFriend(User friend) {
		friend.setFriend(true);
		friends.add(friend);
		db.addFriend(friend.getName(), friend.getDescription(),
				friend.getDateOfBirth(), friend.getGender(), friend.getEmail(),
				currentPhoneUser.get(4), "" + friend.getImage());
		// Main.getAllUsers().get(Main.getIntValue()).setFriend(true);
	}

	public static void removeFriend(User friend) {
		friend.setFriend(false);
		db.deleteFriend(friend.getEmail());
		Manager.getFriends().remove(friend);
	}

	public static ArrayList<Community> getCommunities() {
		return communities;
	}

	public static void addCommunity(Community community) {
		communities.add(community);
	}

	public static int getIntValue() {
		return intValue;
	}

	public static void setIntValue(int i) {
		intValue = i;
	}

	public static int getCurrentStatus() {
		return currentStatus;
	}

	public static void setCurrentStatus(int value) {
		currentStatus = value;
	}

	/**
	 * dado una lista de usuarios y un email me retorna la posicion del usuario
	 * con ese email dentro de la lista si el usuario no existe retorna -1
	 */
	public static int idSearch(ArrayList<User> otherUser, String value) {
		for (int i = 0; i < otherUser.size(); i++) {
			if (otherUser.get(i).getEmail().equals(value))
				return i;
		}
		return -1;
	}

	public static String getNameCurrentActivity() {
		return nameCurrentActivity;
	}

	public static void setNameCurrentActivity(String currentActivity) {
		Manager.nameCurrentActivity = currentActivity;
	}

	public static User getCurrentUser() {
		return currentUser;
	}

	public static int getCurrentUserPos() throws NonExistentUserException {

		if (allUsers.contains(currentUser)) {
			return allUsers.indexOf(currentUser);
		}
		throw new NonExistentUserException();

	}

	public static void setCurrentUser(User currentUser) {
		Manager.currentUser = currentUser;
	}

	public static Community getCurrentCommunity() {
		return currentCommunity;
	}

	public static int getCurrentCommunityPos()
			throws NonExistentCommunityException {

		if (communities.contains(currentCommunity)) {
			return communities.indexOf(currentCommunity);
		}

		throw new NonExistentCommunityException();
	}

	public static void setCurrentCommunity(Community currentCommunity) {
		Manager.currentCommunity = currentCommunity;
	}

	public static Context getCurrentActivity() {
		return currentActivity;
	}

	public static void setCurrentActivity(Context currentActivity) {
		Manager.currentActivity = currentActivity;
	}

	public static String getCurrentUserIP() {
		return currentUserIP;
	}

	public static String getCurrentUserProfile() {
		return currentUserProfile;
	}

	public static void setCurrentUserProfile(String currentUserProfile) {
		Manager.currentUserProfile = currentUserProfile;
	}

	public static String getStringValue() {
		return StringValue;
	}

	public static void setStringValue(String stringValue) {
		StringValue = stringValue;
	}

}