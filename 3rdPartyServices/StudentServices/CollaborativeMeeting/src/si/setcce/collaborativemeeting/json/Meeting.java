/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package si.setcce.collaborativemeeting.json;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import si.setcce.collaborativemeeting.MinutesModel;
import si.setcce.collaborativemeeting.User;
import android.util.Log;

/**
 * JSON parser
 *
 * @author Mitja Vardjan
 *
 */
public class Meeting {
	
	private static final String TAG = Meeting.class.getSimpleName();

	private static final String JSON_CHECKED_IN_USERS = "attendees";
	
	private String id;
	private String subject;
	private List<User> users;
	private List<MinutesModel> minutes;
	private boolean newMeeting;
	
	/**
	 * Constructor
	 * @throws JSONException 
	 */
	public Meeting(JSONObject json, boolean isNew) throws JSONException {
		extractAll(json);
		this.newMeeting = isNew;
	}
	
	/**
	 * Constructor
	 * @throws JSONException see {@link JSONObject#JSONObject(String)}
	 */
	public Meeting(String jsonStr, boolean isNew) throws JSONException {
		this.newMeeting = isNew;
		JSONObject json = new JSONObject(jsonStr);
		extractAll(json);
	}
	
	/**
	 * Constructor
	 */
	public Meeting(String id, String subject, List<User> users, List<MinutesModel> minutes) {
		this.id = id;
		this.subject = subject;
		this.users = users;
		this.minutes = minutes;
	}

	public String getMeetingId() {
		return id;
	}
	
	public List<User> getUsers() {
		return users;
	}
	
	public String getMeetingSubject() {
		return subject;
	}
	
	public List<MinutesModel> getMinutes() {
		return minutes;
	}
	
	public boolean isNew() {
		return newMeeting;
	}

	public void setMeetingId(String id) {
		this.id = id;
	}
	
	public void setUsers(List<User> users) {
		this.users = users;
	}
	
	public void setMeetingSubject(String subject) {
		this.subject = subject;
	}
	
	public void setMinutes(List<MinutesModel> minutes) {
		this.minutes = minutes;
	}

	private void extractAll(JSONObject json) throws JSONException {

		this.id = extractMeetingId(json);
		this.subject = extractMeetingSubject(json);
		this.users = extractUsers(json, JSON_CHECKED_IN_USERS, true);
		this.minutes = extractMinutes(json);
	}
	
	private String extractMeetingId(JSONObject json) throws JSONException {
		
		String id = json.getString("id");
		Log.d(TAG, "JSON: meeting ID = " + id);
		return id;
	}
	
	private String extractMeetingSubject(JSONObject json) throws JSONException {
		
		String subject = json.getString("subject");
		Log.d(TAG, "JSON: meeting subject = " + subject);
		return subject;
	}
	
	private List<User> extractUsers(JSONObject json, String keyword, boolean checkedIn) throws JSONException {
		
		JSONArray usersJson;
		List<User> extracted = new ArrayList<User>();
		
		usersJson = json.getJSONArray(keyword);

		for (int k = 0; k < usersJson.length(); k++) {
			JSONObject user = usersJson.getJSONObject(k);
			String name = user.getString("username");
			String id = user.getString("id");
			extracted.add(new User(id, name, checkedIn));
			Log.d(TAG, "JSON: extracted user " + user);
		}
		return extracted;
	}
	
	private List<MinutesModel> extractMinutes(JSONObject json) throws JSONException {
		
		JSONArray minutesJson;
		List<MinutesModel> minutes = new ArrayList<MinutesModel>();
		
		minutesJson = json.getJSONArray("meetingMinutes");
		
		if (this.users == null) {
			Log.w(TAG, "No users defined. Extracting now.");
			this.users = extractUsers(json, JSON_CHECKED_IN_USERS, true);
		}

		for (int k = 0; k < minutesJson.length(); k++) {
			
			JSONObject minuteJson = minutesJson.getJSONObject(k);
			String userName = minuteJson.getString("postedBy");
			String userId = minuteJson.getString("userId");
//			String userId = getUserId(userName);
			String minute = minuteJson.getString("text");
			
			// FIXME: ideally, the received timestamp would be used, but clocks should be synchronized first.
			String dateStr = minuteJson.getString("timestamp");
			Date date = new Date(dateStr);
//			Date date = new Date();
			
			boolean important;
			try {
				String importantStr = minuteJson.getString("important");
				important = !"false".equalsIgnoreCase(importantStr);
			} catch (JSONException e) {
				important = false;
			}
			if (!contains(this.users, userId)) {
				this.users.add(new User(userId, userName, false));
			}
			MinutesModel minuteModel = new MinutesModel(userId, userName, minute, date, important);
			Log.d(TAG, "extractMinutes: " + minuteModel);
			minutes.add(minuteModel);
		}
		return minutes;
	}
	
	private boolean contains(List<User> users, String userId) {
		
		if (userId == null) {
			Log.w(TAG, "User ID is null");
			return false;
		}
		for (User user : users) {
			if (userId.equals(user.getId())) {
				return true;
			}
		}
		return false;
	}
	
	private String getUserId(String userName) {
		
		if (userName == null) {
			Log.w(TAG, "User name is null");
			return null;
		}
		for (User user : users) {
			if (userName.equals(user.getName())) {
				String userId = user.getId();
				Log.d(TAG, "User " + userName + " has ID " + userId);
				return userId;
			}
		}
		Log.w(TAG, "User name \"" + userName + "\" not found");
		return null;
	}
}
