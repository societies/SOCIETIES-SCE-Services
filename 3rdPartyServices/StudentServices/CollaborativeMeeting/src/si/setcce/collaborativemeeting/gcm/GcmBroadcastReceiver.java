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
package si.setcce.collaborativemeeting.gcm;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import si.setcce.collaborativemeeting.MinutesModel;
import si.setcce.collaborativemeeting.SecondActivity;
import si.setcce.collaborativemeeting.User;
import si.setcce.collaborativemeeting.json.Meeting;
import si.setcce.societies.crowdtasking.gcm.GcmMessage;

/**
 * Created by Simon on 31.12.2013.
 */
public class GcmBroadcastReceiver extends BroadcastReceiver {

	private static final String TAG = GcmBroadcastReceiver.class.getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {

		Bundle extras = intent.getExtras();
		String action = extras.getString(GcmMessage.PARAMETER_ACTION);
		Log.i(TAG, "Received message from GCM: " + extras.getString(GcmMessage.PARAMETER_JSON));

		Intent i;
		
		if (action.equals(GcmMessage.ACTION_CHECK_IN)) {
			
			String userId = extras.getString(GcmMessage.PARAMETER_USER_ID);
			String userName = extras.getString(GcmMessage.PARAMETER_USERNAME);
			Log.i(TAG, "Add user " + userName + " (ID = " + userId + ")");
			
			List<User> users = new ArrayList<User>();
			users.add(new User(userId, userName, true));
			Meeting meeting = new Meeting(null, null, users, null);
			i = SecondActivity.buildIntent(meeting);
			context.sendBroadcast(i);
		}
		else if (action.equals(GcmMessage.ACTION_SET_MEETING)) {
			
			String meetingInfo = extras.getString(GcmMessage.PARAMETER_JSON);
			Log.i(TAG, "New meeting info");
			
			try {
				Meeting meeting = new Meeting(meetingInfo, true);
				i = SecondActivity.buildIntent(meeting);
				context.sendBroadcast(i);
			} catch (JSONException e) {
				Log.w(TAG, "Could not update meeting info", e);
			}
		}
		else if (action.equals(GcmMessage.ACTION_MEETING_MINUTE)) {

			Log.i(TAG, "New meeting minute");
			
			String meetingInfo = extras.getString(GcmMessage.PARAMETER_JSON);
//			try {
//				Meeting meeting = new Meeting(meetingInfo);
//
//				if (meeting.getMinutes() == null) {
//					Log.w(TAG, "JSON document receiver from server does not contain any minutes.");
//					return;
//				}
//				if (meeting.getMeetingId() != null || meeting.getMeetingSubject() != null || meeting.getUsers() != null) {
//					Log.w(TAG, "JSON document receiver from server contains unnecessary information.");
//					meeting.setMeetingSubject(null);
//					meeting.setMeetingId(null);
//					meeting.setUsers(null);
//				}
//				Log.d(TAG, "Received " + meeting.getMinutes().size() + " new minutes");
//				
//				i = SecondActivity.buildIntent(meeting);
//				context.sendBroadcast(i);
//			} catch (JSONException e) {
//				Log.w(TAG, "Could not add meeting minute", e);
//			}

			String userId = extras.getString(GcmMessage.PARAMETER_USER_ID);
			String userName = extras.getString(GcmMessage.PARAMETER_USERNAME);
			String meetingId = extras.getString(GcmMessage.PARAMETER_MEETING_ID);
			String minute = extras.getString(GcmMessage.PARAMETER_MEETING_MINUTE);
			boolean important = false;  // TODO
			Log.i(TAG, "Meeting minute for existing meeting received from the server");
			Toast.makeText(context, userName + " said: " + minute, Toast.LENGTH_LONG).show();
			
			List<MinutesModel> minutes = new ArrayList<MinutesModel>();
			// FIXME: ideally, the received timestamp would be used, but clocks should be synchronized first.
			minutes.add(new MinutesModel(userId, userName, minute, new Date(), important));
			Meeting meeting = new Meeting(null, null, null, minutes);
			i = SecondActivity.buildIntent(meeting);
			context.sendBroadcast(i);
		}
		playNotificationTone(context);
	}

	private void playNotificationTone(Context context) {
		Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		RingtoneManager.getRingtone(context, notification).play();
	}
}
