/*
 * 
 */
package com.asocom.tools;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import com.asocom.activities.ShowMessage;
import com.asocom.activities.ShowMessageFriend;
import com.asocom.model.Community;
import com.asocom.model.Manager;
import com.asocom.model.ChatMsg;
import com.asocom.model.User;

// TODO: Auto-generated Javadoc
/**
 * The Class Json.
 */
public class Json {

	/** The j son object. */
	private static JSONObject jSonObject;

	// opetationType=0 -- datos de coneccion
	/**
	 * Connect.
	 *
	 * @return the string
	 */
	public static String connect() {
		JSONObject jObject = new JSONObject();
		try {
			jObject = new JSONObject();
			// Set 1
			jObject.put("receiverType", 0);
			jObject.put("receiver", 0);
			// Set 2
			jObject.put("senderId", Manager.getCurrentUserIP());
			// Set 3
			jObject.put("opetationType", "0");
			// Set 4
			JSONObject setUserData = new JSONObject();
			setUserData.put("name", Manager.getCurrentPhoneUser().get(0));
			setUserData.put("description", Manager.getCurrentPhoneUser().get(1));
			setUserData.put("dateOfBirth", Manager.getCurrentPhoneUser().get(2));
			setUserData.put("gender", Manager.getCurrentPhoneUser().get(3));
			setUserData.put("email", Manager.getCurrentPhoneUser().get(4));
			setUserData.put("image", Manager.getCurrentPhoneUser().get(6));
			setUserData.put("profile", Manager.getCurrentUserProfile());
			setUserData.put("ip", Manager.getCurrentUserIP());
			setUserData.put("status", Manager.getCurrentStatus());
			jObject.put("userData", setUserData);
			// Set 5
			JSONObject setCommunityData = new JSONObject();
			jObject.put("communitiesData", setCommunityData);
			// Set 6
			jObject.put("chatData", "");
			return jObject.toString();
		} catch (Exception e) {
			return null;
		}
	}
	
	// opetationType=1 -- desconectarse
	/**
	 * Disconnect.
	 *
	 * @return the string
	 */
	public static String disconnect() {
		JSONObject jObject = new JSONObject();
		try {
			jObject = new JSONObject();
			// Set 1
			jObject.put("receiverType", 0);
			jObject.put("receiver", 0);
			// Set 2
			jObject.put("senderId", Manager.getCurrentUserIP());
			// Set 3
			jObject.put("opetationType", "1");
			// Set 4
			JSONObject setUserData = new JSONObject();
			setUserData.put("ip", Manager.getCurrentUserIP());
			jObject.put("userData", setUserData);
			// Set 5
			JSONObject setCommunityData = new JSONObject();
			jObject.put("communitiesData", setCommunityData);
			// Set 6
			jObject.put("chatData", "");
			return jObject.toString();
		} catch (Exception e) {
			return null;
		}
	}

	// opetationType=2 -- Actualizar
	/**
	 * Update user information.
	 *
	 * @return the string
	 */
	public static String updateUserInformation() {
		try {
			JSONObject jObject = new JSONObject();
			// Set 1
			jObject.put("receiverType", 0);
			jObject.put("receiver", 0);
			// Set 2
			jObject.put("senderId", Manager.getCurrentUserIP());
			// Set 3
			jObject.put("opetationType", "2");
			// Set 4
			JSONObject setUserData = new JSONObject();
			setUserData.put("name", Manager.getCurrentPhoneUser().get(0));
			setUserData.put("description", Manager.getCurrentPhoneUser().get(1));
			setUserData.put("dateOfBirth", Manager.getCurrentPhoneUser().get(2));
			setUserData.put("gender", Manager.getCurrentPhoneUser().get(3));
			setUserData.put("email", Manager.getCurrentPhoneUser().get(4));
			setUserData.put("image", Manager.getCurrentPhoneUser().get(6));
			setUserData.put("ip", Manager.getCurrentUserIP());
			setUserData.put("profile", Manager.getCurrentUserIP());
			setUserData.put("status", Manager.getCurrentStatus());
			jObject.put("userData", setUserData);
			// Set 5
			JSONObject setCommunityData = new JSONObject();
			jObject.put("communitiesData", setCommunityData);
			// Set 6
			jObject.put("chatData", "");
			//
			return jObject.toString();
		} catch (Exception e) {
			return null;
		}
	}
	
	// opetationType=3 -- Crear comunidad
	/**
	 * Creates the community.
	 *
	 * @return the string
	 */
	public static String createCommunity() {
		try {
			JSONObject jObject = new JSONObject();
			// Set 1
			jObject.put("receiverType", 0);
			jObject.put("receiver", 0);
			// Set 2
			jObject.put("senderId", Manager.getCurrentUserIP());
			// Set 3
			jObject.put("opetationType", "3");
			// Set 4
			JSONObject setUserData = new JSONObject();
			jObject.put("userData", setUserData);
			// Set 5
			JSONObject setCommunityData = new JSONObject();
			JSONObject[] allUsers = new JSONObject[1];
			JSONObject newCommunity = new JSONObject();

			newCommunity.put("name", Manager.getCommunities().get(Manager.getCommunities().size() - 1).getCommunityName());
			newCommunity.put("description", Manager.getCommunities().get(Manager.getCommunities().size() - 1).getDescription());
			newCommunity.put("nameAdministrator", Manager.getCommunities().get(Manager.getCommunities().size() - 1).getNameAdministrator());
		    newCommunity.put("profile", Manager.getCommunities().get(Manager.getCommunities().size() - 1).getProfile());
			newCommunity.put("visibility", Manager.getCommunities().get(Manager.getCommunities().size() - 1).getVisibility());
			newCommunity.put("dateOfCreation", Manager.getCommunities().get(Manager.getCommunities().size() - 1).getDateOfCreation());
			newCommunity.put("image", Manager.getCommunities().get(Manager.getCommunities().size() - 1).getImage());
			newCommunity.put("id", Manager.getCommunities().get(Manager.getCommunities().size() - 1).getId());
			newCommunity.put("recommend", Manager.getCommunities().get(Manager.getCommunities().size() - 1).getRecommendCommunity());
			String[] users = new String[Manager.getCommunities().get(Manager.getCommunities().size() - 1).getUserList().size()];
			for (int j = 0; j < Manager.getCommunities().get(Manager.getCommunities().size() - 1).getUserList().size(); j++) {
				users[j] = Manager.getCommunities().get(Manager.getCommunities().size() - 1).getUserList().get(j).getIp();
			}

			JSONArray usersArray = new JSONArray(Arrays.asList(users));
			newCommunity.put("users", usersArray);
			allUsers[0] = newCommunity;

			JSONArray allCommunitiesArray = new JSONArray(Arrays.asList(allUsers));
			setCommunityData.put("allCommunitiesArray", allCommunitiesArray);
			jObject.put("communitiesData", setCommunityData);

			// Set 6
			jObject.put("chatData", "");
			//

			return jObject.toString();
		} catch (Exception e) {
			return null;
		}
	}
	
	// opetationType=4 -- Unirse a una comunidad
	/**
	 * Join community.
	 *
	 * @param idComunity the id comunity
	 * @return the string
	 */
	public static String joinCommunity(String idComunity) {
		try {
			JSONObject jObject = new JSONObject();
			// Set 1
			jObject.put("receiverType", 0);
			jObject.put("receiver", 0);
			// Set 2
			jObject.put("senderId", Manager.getCurrentUserIP());
			// Set 3
			jObject.put("opetationType", "4");
			// Set 4
			JSONObject setUserData = new JSONObject();
			jObject.put("userData", setUserData);
			// Set 5
			JSONObject setCommunityData = new JSONObject();
			setCommunityData.put("id", idComunity);
			jObject.put("communitiesData", setCommunityData);
			// Set 6
			jObject.put("chatData", "");
			//
			return jObject.toString();
		} catch (Exception e) {
			return null;
		}
	}

	// opetationType=5 -- dejar comunidad comunidad
	/**
	 * Leave community.
	 *
	 * @param idCoomunity the id coomunity
	 * @return the string
	 */
	public static String leaveCommunity(String idCoomunity) {
		try {
			JSONObject jObject = new JSONObject();
			// Set 1
			jObject.put("receiverType", 0);
			jObject.put("receiver", 0);
			// Set 2
			jObject.put("senderId", Manager.getCurrentUserIP());
			// Set 3
			jObject.put("opetationType", "5");
			// Set 4
			JSONObject setUserData = new JSONObject();
			jObject.put("userData", setUserData);
			// Set 5
			JSONObject setCommunityData = new JSONObject();
			setCommunityData.put("id", idCoomunity);
			jObject.put("communitiesData", setCommunityData);
			// Set 6
			jObject.put("chatData", "");
			//
			return jObject.toString();
		} catch (Exception e) {
			return null;
		}
	}

	// opetationType=6 -- chat de comunidad
	/**
	 * Send community chat.
	 *
	 * @param idCommunity the id community
	 * @param sms the sms
	 * @return the string
	 */
	public static String sendCommunityChat(String idCommunity, String sms) {
		try {
			JSONObject jObject = new JSONObject();
			// Set 1
			jObject.put("receiverType", 1);
			jObject.put("receiver", idCommunity);
			// Set 2
			jObject.put("senderId", Manager.getCurrentUserIP());
			// Set 3
			jObject.put("opetationType", "6");
			// Set 4
			JSONObject setUserData = new JSONObject();
			jObject.put("userData", setUserData);
			// Set 5
			JSONObject setCommunityData = new JSONObject();
			jObject.put("communitiesData", setCommunityData);
			// Set 6
			jObject.put("chatData", sms);
			//
			return jObject.toString();
		} catch (Exception e) {
			return null;
		}
	}

	// opetationType=7 -- chat privado
	/**
	 * Send private chat.
	 *
	 * @param idUser the id user
	 * @param sms the sms
	 * @return the string
	 */
	public static String sendPrivateChat(String idUser, String sms) {
			try {
				JSONObject jObject = new JSONObject();
				// Set 1
				jObject.put("receiverType", 2);
				jObject.put("receiver", idUser);
				// Set 2
				jObject.put("senderId", Manager.getCurrentUserIP());
				// Set 3
				jObject.put("opetationType", "7");
				// Set 4
				JSONObject setUserData = new JSONObject();
				jObject.put("userData", setUserData);
				// Set 5
				JSONObject setCommunityData = new JSONObject();
				jObject.put("communitiesData", setCommunityData);
				// Set 6
				jObject.put("chatData", sms);
				//
				return jObject.toString();
			} catch (Exception e) {
				return null;
			}
		}
	
	
	// opetationType=8 -- pedir amistad
	/**
	 * Friend request.
	 *
	 * @param idUser the id user
	 * @return the string
	 */
	public static String friendRequest(String idUser) {
		try {
			JSONObject jObject = new JSONObject();
			// Set 1
			jObject.put("receiverType", 2);
			jObject.put("receiver", idUser);
			// Set 2
			jObject.put("senderId", Manager.getCurrentUserIP());
			// Set 3
			jObject.put("opetationType", "8");
			// Set 4
			JSONObject setUserData = new JSONObject();
			jObject.put("userData", setUserData);
			// Set 5
			JSONObject setCommunityData = new JSONObject();
			jObject.put("communitiesData", setCommunityData);
			// Set 6
			jObject.put("chatData", "");
			//
			return jObject.toString();
		} catch (Exception e) {
			return null;
		}
	}
	
	// opetationType=9 -- responder amistad	
	/**
	 * Friend request response.
	 *
	 * @param reponse the reponse
	 * @param friendId the friend id
	 * @return the string
	 */
	public static String friendRequestResponse(String reponse, String friendId){
		try {
			JSONObject jObject = new JSONObject();
			// Set 1
			jObject.put("receiverType", 2);
			jObject.put("receiver", friendId);
			// Set 2
			jObject.put("senderId", Manager.getCurrentUserIP());
			// Set 3
			jObject.put("opetationType", "9");
			// Set 4
			JSONObject setUserData = new JSONObject();
			setUserData.put("reponse", reponse);
			jObject.put("userData", setUserData);
			// Set 5
			JSONObject setCommunityData = new JSONObject();
			jObject.put("communitiesData", setCommunityData);
			// Set 6
			jObject.put("chatData", "");
			//
			return jObject.toString();
		} catch (Exception e) {
			return null;
		}
	}
	
	// opetationType=10 -- eliminar amigo	
	/**
	 * Delete friend.
	 *
	 * @param friendId the friend id
	 * @return the string
	 */
	public static String deleteFriend(String friendId){
		return "";
	}	
	
	/**
	 * User data.
	 *
	 * @return the string
	 */
	public static String userData() {
		try {
			JSONObject jObject = new JSONObject();
			// Set 1
			jObject.put("receiverType", 0);
			jObject.put("receiver", 0);
			// Set 2
			jObject.put("senderId", Manager.getCurrentUserIP());
			// Set 3
			jObject.put("opetationType", "11");
			// Set 4
			JSONObject setUserData = new JSONObject();
			setUserData.put("name", Manager.getCurrentPhoneUser().get(0));
			setUserData.put("description", Manager.getCurrentPhoneUser().get(1));
			setUserData.put("dateOfBirth", Manager.getCurrentPhoneUser().get(2));
			setUserData.put("gender", Manager.getCurrentPhoneUser().get(3));
			setUserData.put("email", Manager.getCurrentPhoneUser().get(4));
			setUserData.put("image", Manager.getCurrentPhoneUser().get(6));
			setUserData.put("profile", Manager.getCurrentUserProfile());
			setUserData.put("ip", Manager.getCurrentUserIP());
			setUserData.put("status", Manager.getCurrentStatus());
			jObject.put("userData", setUserData);
			// Set 5
			JSONObject setCommunityData = new JSONObject();
			JSONObject[] comunities = new JSONObject[Manager.getCommunities().size()];
	    	for (int i = 0; i < Manager.getCommunities().size(); i++) {
			JSONObject newCommunity = new JSONObject();
			Community com = Manager.getCommunities().get(i);
			newCommunity.put("name", com.getCommunityName());
			newCommunity.put("description", com.getDescription());
			newCommunity.put("nameAdministrator", com.getNameAdministrator());
			newCommunity.put("profile", com.getProfile());
			newCommunity.put("visibility", com.getVisibility());
			newCommunity.put("dateOfCreation", com.getDateOfCreation());
			newCommunity.put("image", com.getImage());
			newCommunity.put("id", com.getId());
			newCommunity.put("recommend", com.getRecommendCommunity());
			
			String[] users = new String[com.getUserList().size()];
			Log.i("Json", "com.getUserList().size():" + com.getUserList().size());
			for (int j = 0; j < com.getUserList().size(); j++) {
				users[j] = com.getUserList().get(j).getIp();
				Log.i("Json", "users[j]:" + users[j]);
			}
			
			JSONArray usersArray = new JSONArray(Arrays.asList(users));
			Log.i("Json", "usersArray:" + usersArray);
			newCommunity.put("users", usersArray);
			comunities[i] = newCommunity;
	    	}// end for
			JSONArray allCommunitiesArray = new JSONArray(
					Arrays.asList(comunities));
			setCommunityData.put("allCommunitiesArray", allCommunitiesArray);
			jObject.put("communitiesData", setCommunityData);
			// Set 6
			jObject.put("chatData", "");
			//
			return jObject.toString();
		} catch (Exception e) {
			return null;
		}
	}		

// ===========================================================================================================
// ===========================================================================================================
// ===========================================================================================================
// ===========================================================================================================
		
		
	/**
 * Receiver.
 *
 * @param json the json
 * @throws Exception the exception
 */
public static void receiver(String[] json) throws Exception {	
		for (int i = 0; i < json.length-1; i++) {
			Log.i("Json", "Json.receiver(json[i]): " + json[i]);
			process(json[i]);			
		}
	}

public static void reestablishConnection(String[] json) throws Exception {	
	for (int i = 0; i < json.length-1; i++) {
		Log.i("Json", "Json.receiver(json[i]): " + json[i]);
		reProcess(json[i]);			
	}
}
	/**
	 * Process.
	 *
	 * @param json the json
	 * @throws Exception the exception
	 */
	public static void reProcess(String json) throws Exception {		
		switch (jSonObject.getInt("opetationType")) {
		case 0:
			// Connect
			Log.i("Json", "Json.process(): reiverType = connect()");
			processConnect(jSonObject);
			break;
			
		case 1:
			// Disconnect
			if (jSonObject.getString("senderId").equals(Manager.getCurrentUserIP())) 
				break;
			Log.i("Json", "Json.process(): reiverType = disconnect()");
			processDisconnect(jSonObject);
			break;

		case 2:
			// 
			Log.i("Json", "Json.process(): reiverType = processUpdateUserInformation()");
			processUpdateUserInformation(jSonObject);
			break;
			
		case 3:
			// 
			Log.i("Json", "Json.process(): reiverType = processCreateCommunity()");
			processCreateCommunity(jSonObject);
			break;
			
		case 4:
			// 
			Log.i("Json", "Json.process(): reiverType = processJoinCommunity()");
			processJoinCommunity(jSonObject);
			break;
			
		case 5:
			// 
			Log.i("Json", "Json.process(): reiverType = processLeaveCommunity()");
			processLeaveCommunity(jSonObject);
			break;
			
		case 6:
			// 
			Log.i("Json", "Json.process(): reiverType = processCommunityChat()");
			processCommunityChat(jSonObject);
			break;
			
		case 7:
			// 
			Log.i("Json", "Json.process(): reiverType = processPrivateChat()");
			processPrivateChat(jSonObject);
			break;
		
		case 8:
			// 
			Log.i("Json", "Json.process(): reiverType = processfriendRequest()");
			processfriendRequest(jSonObject);
			break;
		
		case 9:
			// 
			Log.i("Json", "Json.process(): reiverType = processfriendRequestResponse()");
			processfriendRequestResponse(jSonObject);
			break;
		
		case 10:
			// 
			Log.i("Json", "Json.process(): reiverType = processDeleteFriend()");
				processDeleteFriend(jSonObject);
			break;
			
		case 11:
			// 
			Log.i("Json", "Json.process(): reiverType = processUserData()");
				processUserData(jSonObject);
			break;
			
			default:
				break;
		}	
		
		
// ********************************************************************************************************

	}
	
	public static void process(String json) throws Exception {		
		Log.i("Json", "Json.process(String json): OK");
		jSonObject = new JSONObject(json);

		if (jSonObject.getString("senderId").equals(Manager.getCurrentUserIP())) {
			return;
		}else if(jSonObject.getInt("receiverType")==1){
			int exist = 0;
			for (int i = 0; i < Manager.getCommunities().size(); i++) {
				if (Manager.getCommunities().get(i).getId().equals(jSonObject.getString("receiver"))) {
					exist = 1;
				}
			}
			if(exist == 0){
				return;
			}
		}else if(jSonObject.getInt("receiverType")==2){
			if((jSonObject.getString("senderId").equals(Manager.getCurrentUserIP()))) {
				return;
			}
		}		
		Log.i("Json", "Json.process(): This json will be process");		

		
// ********************************************************************************************************
		switch (jSonObject.getInt("opetationType")) {
        	
		case 0:
			// Connect
			Log.i("Json", "Json.process(): reiverType = connect()");
			processConnect(jSonObject);
			break;
			
		case 1:
			// Disconnect
			Log.i("Json", "Json.process(): reiverType = disconnect()");
			processDisconnect(jSonObject);
			break;

		case 2:
			// 
			Log.i("Json", "Json.process(): reiverType = processUpdateUserInformation()");
			processUpdateUserInformation(jSonObject);
			break;
			
		case 3:
			// 
			Log.i("Json", "Json.process(): reiverType = processCreateCommunity()");
			processCreateCommunity(jSonObject);
			break;
			
		case 4:
			// 
			Log.i("Json", "Json.process(): reiverType = processJoinCommunity()");
			processJoinCommunity(jSonObject);
			break;
			
		case 5:
			// 
			Log.i("Json", "Json.process(): reiverType = processLeaveCommunity()");
			processLeaveCommunity(jSonObject);
			break;
			
		case 6:
			// 
			Log.i("Json", "Json.process(): reiverType = processCommunityChat()");
			processCommunityChat(jSonObject);
			break;
			
		case 7:
			// 
			Log.i("Json", "Json.process(): reiverType = processPrivateChat()");
			processPrivateChat(jSonObject);
			break;
		
		case 8:
			// 
			Log.i("Json", "Json.process(): reiverType = processfriendRequest()");
			processfriendRequest(jSonObject);
			break;
		
		case 9:
			// 
			Log.i("Json", "Json.process(): reiverType = processfriendRequestResponse()");
			processfriendRequestResponse(jSonObject);
			break;
		
		case 10:
			// 
			Log.i("Json", "Json.process(): reiverType = processDeleteFriend()");
				processDeleteFriend(jSonObject);
			break;
			
		case 11:
			// 
			Log.i("Json", "Json.process(): reiverType = processUserData()");
				processUserData(jSonObject);
			break;
			
			default:
				break;
		}
		}

	/**
	 * Process connect.
	 *
	 * @param jSonObject the j son object
	 * @throws Exception the exception
	 */
	private static void processConnect(JSONObject jSonObject) throws Exception{

		Log.i("Json", "Json.processConnect(): ok");
		JSONObject userData = new JSONObject(jSonObject.get("userData").toString());
	
		for (int i = 1; i <Manager.getAllUsers().size(); i++) {
			if (Manager.getAllUsers().get(i).getIp().equals(userData.getString("ip"))) {
				return;
			}
		}		
		User user = new User(userData.getString("name"),
				userData.getString("description"),
				userData.getString("dateOfBirth"),
				userData.getString("gender"), userData.getString("email"),
				userData.getInt("image"), 0);
		user.setStatus(userData.getInt("status"));
		user.setProfile(userData.getString("profile"));
		user.setIp(userData.getString("ip"));
		Manager.addUsers(user);
		
		Server.sendData(Json.userData());
		//Server.sendData(Json.connect());
		Activity act = (Activity) Manager.getCurrentActivity();
		act.clearWallpaper();

	}	
	
	/**
	 * Process disconnect.
	 *
	 * @param jSonObject the j son object
	 * @throws Exception the exception
	 */
	private static void processDisconnect(JSONObject jSonObject) throws Exception{
		Log.i("Json", "Entro1");
		
		JSONObject userData = new JSONObject(jSonObject.get("userData").toString());	
		for (int i = 1; i <Manager.getAllUsers().size(); i++) {
			if (Manager.getAllUsers().get(i).getIp().equals(userData.getString("ip"))) {
				Manager.getAllUsers().remove(i);
				//Activity act = (Activity) Manager.getCurrentActivity();
				//act.clearWallpaper();
				//return;
			}
		}
		Log.i("Json", "Entro2");	

		for (int i = 0; i < Manager.getCommunities().size(); i++) {
			Log.i("Json", "Manager.getCommunities().get(i) name: " + Manager.getCommunities().get(i).getCommunityName());
			for (int j = 0; j < Manager.getCommunities().get(i).getUserList().size(); j++) {
				Log.i("Json", "Manager.getCommunities().get(i).getUserList().get(j).getIp(): " + Manager.getCommunities().get(i).getUserList().get(j).getIp());
				Log.i("Json", "Comparacion: " + "" + Manager.getCommunities().get(i).getUserList().get(j).getIp().equals(userData.getString("ip")));
				if (Manager.getCommunities().get(i).getUserList().get(j).getIp().equals(userData.getString("ip"))) {					
					Log.i("Json", "Entro1111111");					
					Manager.getCommunities().get(i).getUserList().remove(j);
					j = Manager.getCommunities().get(i).getUserList().size();
				}				
			}			
		}
		
		Log.i("Json", "Entro3");
		
		
		Activity act = (Activity) Manager.getCurrentActivity();
		act.clearWallpaper();
	Log.i("Json", "Json.processDisconnect(): ok");
	}
		
	
	/**
	 * Process update user information.
	 *
	 * @param jSonObject the j son object
	 * @throws Exception the exception
	 */
	public static void processUpdateUserInformation(JSONObject jSonObject) throws Exception {			

		JSONObject updateUserJson = new JSONObject(jSonObject.get("userData").toString());
		int i;
		for (i = 1; i < Manager.getAllUsers().size(); i++) {
			if (Manager.getAllUsers().get(i).getIp().equals(updateUserJson.getString("ip").toString())) {
				Log.v("Json.allReceiver():", "case 4: if user ip= " + updateUserJson.getString("ip").toString());	
				break;
		}				
		}

	Manager.getAllUsers().get(i).setName(updateUserJson.getString("name"));
	Manager.getAllUsers().get(i).setDescription(updateUserJson.getString("description"));
	Manager.getAllUsers().get(i).setDateOfBirth(updateUserJson.getString("dateOfBirth"));
	Manager.getAllUsers().get(i).setGender(updateUserJson.getString("gender"));
	Manager.getAllUsers().get(i).setImage(updateUserJson.getInt("image"));
	Manager.getAllUsers().get(i).setStatus(updateUserJson.getInt("status"));
	Manager.getAllUsers().get(i).setIp(updateUserJson.getString("ip"));
	Activity act = (Activity) Manager.getCurrentActivity();
	act.clearWallpaper();
	Log.i("Json", "processUpdateUserInformation(): ok");
	
	}	
	
	
	/**
	 * Process create community.
	 *
	 * @param jSonObject the j son object
	 * @throws Exception the exception
	 */
	public static void processCreateCommunity(JSONObject jSonObject) throws Exception{

		String sender = jSonObject.getString("senderId");
		
		JSONObject communityData = new JSONObject(jSonObject.get("communitiesData").toString());
		JSONArray communityArray = new JSONArray(communityData.getString("allCommunitiesArray").toString());
		JSONObject community = new JSONObject(communityArray.get(0).toString());

//		for (int i = 0; i < Manager.getCommunities().size(); i++) {
//			if (Manager.getCommunities().get(i).getCommunityName().equals(community.getString("name"))) {
//				return;
//			}
//		}

		Community newCommunity = new Community(community.getString("name"),
				community.getString("description"),
				community.getString("nameAdministrator"),
				community.getString("profile"),
				community.getString("visibility"),
				community.getString("dateOfCreation"),
				community.getInt("image"),community.getString("recommend"));
		newCommunity.setId(community.getString("id"));

		JSONArray communityUsers = new JSONArray(community.getString("users"));
		for (int j = 1; j < Manager.getAllUsers().size(); j++) {
			
			if(sender.equals(Manager.getAllUsers().get(j).getIp())){
				newCommunity.getUserList().add(Manager.getAllUsers().get(j));
			}
			
			for (int i = 1; i < communityUsers.length(); i++) {	
				if (Manager.getAllUsers().get(j).getIp().equals(communityUsers.get(i))) {
					newCommunity.getUserList().add(Manager.getAllUsers().get(j));
				}
			}
		}	
		newCommunity.lastUpdatedTime=Long.parseLong(jSonObject.getString("lastupdate"));
		Manager.addCommunity(newCommunity);
		Activity act1 = (Activity) Manager.getCurrentActivity();
		act1.clearWallpaper();

		if(community.getString("recommend").equals("static")){return;}		
		String stringCommunityProfile = community.getString("profile");
		String stringUserProfile = Manager.getCurrentUserProfile();
		String[] communityProfile = stringCommunityProfile.split(",");
		
		if(stringUserProfile.indexOf(communityProfile[0])==-1){return;}

		stringUserProfile = stringUserProfile.substring(stringUserProfile.indexOf(communityProfile[0]));
		String[] userProfile = stringUserProfile.split(",");
		
		if(!userProfile[1].equals("General")){
			if(!userProfile[1].equals(communityProfile[1])){return;}
			if(!userProfile[2].equals("General")){
				if(!userProfile[2].equals(communityProfile[2])){return;}			
			}
		}
		
		Intent i = new Intent(Manager.getCurrentActivity(), ShowMessage.class);
		Manager.getCurrentActivity().startActivity(i);
		Log.i("Json", "processCreateCommunity(): ok");

	}
	

	/**
	 * Process join community.
	 *
	 * @param jSonObject the j son object
	 * @throws Exception the exception
	 */
	public static void processJoinCommunity(JSONObject jSonObject) throws Exception{
	
	JSONObject joinCommunityJson = new JSONObject(jSonObject.get("communitiesData").toString());
	int joinCommunityUser=-1;
	int joinCommunityNumber=-1;
	
	for (int i = 1; i < Manager.getAllUsers().size(); i++) {
		if (Manager.getAllUsers().get(i).getIp().equals(jSonObject.getString("senderId"))) {
			joinCommunityUser = i;
			break;
		}
	}
	for (int i = 0; i < Manager.getCommunities().size(); i++) {
		Log.i("Json", "Manager.getCommunities().get(i).getId():" + Manager.getCommunities().get(i).getId());
		Log.i("Json", "joinCommunityJson.getString(id):" + joinCommunityJson.getString("id"));
		if (Manager.getCommunities().get(i).getId().equals(joinCommunityJson.getString("id"))) {
			joinCommunityNumber = i;
			break;
		}
	}
	Manager.getCommunities().get(joinCommunityNumber).addUser(Manager.getAllUsers().get(joinCommunityUser));			
	Activity act1 = (Activity) Manager.getCurrentActivity();
	act1.clearWallpaper();
	Log.i("Json", "processJoinCommunity(): ok");
	}
		

	/**
	 * Process leave community.
	 *
	 * @param jSonObject the j son object
	 * @throws Exception the exception
	 */
	public static void processLeaveCommunity(JSONObject jSonObject) throws Exception{

	JSONObject leaveCommunityJson = new JSONObject(jSonObject.get("communitiesData").toString());
	int leaveCommunityUser=-1;
	int leaveCommunityNumber=-1;
	
	for (int i = 1; i < Manager.getAllUsers().size(); i++) {
		if (Manager.getAllUsers().get(i).getIp().equals(jSonObject.getString("senderId"))) {
			leaveCommunityUser = i;
			break;
		}
	}

	for (int i = 0; i < Manager.getCommunities().size(); i++) {
		if (Manager.getCommunities().get(i).getId().equals(leaveCommunityJson.getString("id"))) {
			leaveCommunityNumber = i;
			break;
		}
	}
	
	Manager.getCommunities().get(leaveCommunityNumber).getUserList().remove(Manager.getAllUsers().get(leaveCommunityUser));			
	Activity act3 = (Activity) Manager.getCurrentActivity();
	act3.clearWallpaper();
	Log.i("Json", "processLeaveCommunity(): ok");	
	}
	
	
	
	/**
	 * Process community chat.
	 *
	 * @param jSonObject the j son object
	 * @throws Exception the exception
	 */
	public static void processCommunityChat(JSONObject jSonObject) throws Exception{

		int community = 0;
		int user = 0;		
		for (int i = 1; i < Manager.getAllUsers().size(); i++) {
			if (Manager.getAllUsers().get(i).getIp().equals(jSonObject.getString("senderId"))) {
				user = i;
				Log.v("Json.allReceiver():", "case 6:user = " + i);
				break;
			}
		}
		
		for (int i = 0; i < Manager.getCommunities().size(); i++) {
			if (Manager.getCommunities().get(i).getId().equals(jSonObject.get("receiver").toString())) {
				community = i;
				break;
			}
		}		
        String dt;
        Date cal=Calendar.getInstance().getTime();
        dt=cal.toLocaleString();  
		ChatMsg sms = new ChatMsg(Manager.getAllUsers().get(user).getImage(), dt, Manager.getAllUsers().get(user).getName(), jSonObject.getString("chatData"));
		Manager.getCommunities().get(community).getChat().addSms(sms);
		
		if (!(Manager.getNameCurrentActivity().equals("ComunityChatActivity"))) {
			Manager.getCommunities().get(community).getChat().addNewMessages();
		}
		Activity act1 = (Activity) Manager.getCurrentActivity();
		act1.clearWallpaper();
		Log.i("Json", "processCommunityChat(): ok");	
	}

	/**
	 * Process private chat.
	 *
	 * @param jSonObject the j son object
	 * @throws Exception the exception
	 */
	public static void processPrivateChat(JSONObject jSonObject) throws Exception{

		int user = 0;		
		for (int i = 1; i < Manager.getAllUsers().size(); i++) {
			if (Manager.getAllUsers().get(i).getIp().equals(jSonObject.getString("senderId"))) {
				user = i;
				break;
			}
		}
		
        String dt;
        Date cal=Calendar.getInstance().getTime();
        dt=cal.toLocaleString();  
		ChatMsg sms = new ChatMsg(Manager.getAllUsers().get(user).getImage(), dt, Manager.getAllUsers().get(user).getName(), jSonObject.getString("chatData"));
		Manager.getAllUsers().get(user).getChat().addSms(sms);
		
		if (!(Manager.getNameCurrentActivity().equals("UserChatActivity"))) {
			Manager.getAllUsers().get(user).getChat().addNewMessages();
		}
		Activity act1 = (Activity) Manager.getCurrentActivity();
		act1.clearWallpaper();
		Log.i("Json", "processPrivateChat(): ok");
		
	}
	
	/**
	 * Processfriend request.
	 *
	 * @param jSonObject the j son object
	 * @throws Exception the exception
	 */
	public static void processfriendRequest(JSONObject jSonObject) throws Exception{

		Manager.setIntValue(0);
		for (int i = 1; i < Manager.getAllUsers().size(); i++) {
			if (Manager.getAllUsers().get(i).getIp().equals(jSonObject.getString("senderId"))) {
				Manager.setIntValue(i);
				break;
			}					
		}
		
		Manager.setStringValue(jSonObject.getString("senderId"));
		Intent i = new Intent(Manager.getCurrentActivity(), ShowMessageFriend.class);
		Manager.getCurrentActivity().startActivity(i);
		Log.i("Json", "processfriendRequest(): ok");
		
		
		
	}
	
	/**
	 * Processfriend request response.
	 *
	 * @param jSonObject the j son object
	 * @throws Exception the exception
	 */
	public static void processfriendRequestResponse(JSONObject jSonObject) throws Exception{

		JSONObject userData = new JSONObject(jSonObject.get("userData").toString());
		int userNb=0;
		for (int i = 1; i < Manager.getAllUsers().size(); i++) {
			if (Manager.getAllUsers().get(i).getIp().equals(jSonObject.getString("senderId"))) {
				userNb=i;
				break;
			}							
		}
		
		User friend = Manager.getAllUsers().get(userNb);		
		if(userData.getString("reponse").equals("yes")){
			Manager.addFriend(friend);
		}else{
		}
		Activity act3 = (Activity) Manager.getCurrentActivity();
		act3.clearWallpaper();
		
	}
	
	/**
	 * Process delete friend.
	 *
	 * @param jSonObject the j son object
	 * @throws Exception the exception
	 */
	private static void processDeleteFriend(JSONObject jSonObject) throws Exception{

	}
	
	
	/**
	 * Process user data.
	 *
	 * @param jSonObject the j son object
	 * @throws Exception the exception
	 */
	private static void processUserData(JSONObject jSonObject) throws Exception{
		JSONObject userData = new JSONObject(jSonObject.get("userData").toString());
	
		for (int i = 1; i <Manager.getAllUsers().size(); i++) {
			if (Manager.getAllUsers().get(i).getIp().equals(userData.getString("ip"))) {
				return;
			}
		}

		
		User user = new User(userData.getString("name"),
				userData.getString("description"),
				userData.getString("dateOfBirth"),
				userData.getString("gender"), userData.getString("email"),
				userData.getInt("image"), 0);
		user.setStatus(userData.getInt("status"));
		user.setProfile(userData.getString("profile"));
		user.setIp(userData.getString("ip"));
		Manager.addUsers(user);

		
		String sender = jSonObject.getString("senderId");
		
		JSONObject communityData = new JSONObject(jSonObject.get("communitiesData").toString());
		JSONArray communityArray = new JSONArray(communityData.getString("allCommunitiesArray").toString());

		for (int k = 0; k < communityArray.length(); k++) {
			
			JSONObject community = new JSONObject(communityArray.get(k).toString());

			// existe la comunidad ???
//			int i;
//			for (i = 0; i < Manager.getCommunities().size(); i++) {
//				if (Manager.getCommunities().get(i).getCommunityName().equals(community.getString("name"))) {
//					i = Manager.getCommunities().size() + 10;
//				}
//			}
//
//			if (i == Manager.getCommunities().size() + 10) {
//				continue;
//			}
			
			Community newCommunity = new Community(community.getString("name"),
					community.getString("description"),
					community.getString("nameAdministrator"),
					community.getString("profile"),
					community.getString("visibility"),
					community.getString("dateOfCreation"),
					community.getInt("image"),community.getString("recommend"));
			newCommunity.setId(community.getString("id"));
			newCommunity.lastUpdatedTime=Long.parseLong(jSonObject.getString("lastupdate"));
			Manager.addCommunity(newCommunity);
		} // end for k
		
		for (int k = 0; k < communityArray.length(); k++) {
			JSONObject community = new JSONObject(communityArray.get(k).toString());
			JSONArray communityUsers = new JSONArray(community.getString("users"));
			
			for (int j = 0; j < communityUsers.length(); j++) {	
				if(sender.equals(communityUsers.get(j))){
					
					// Buscar posicion de la comunidad
					int n;
					for (n = 0; n < Manager.getCommunities().size(); n++) {
						if (Manager.getCommunities().get(n).getCommunityName().equals(community.getString("name"))) {
							break;
						}
					}// end for n
					Manager.getCommunities().get(n).addUser(user);
				}
			} // end for j
			
		}

		Activity act = (Activity) Manager.getCurrentActivity();
		act.clearWallpaper();		
		Log.i("Json", "processUserData(): ok");		
	}
	
	
}