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
package si.setcce.collaborativemeeting;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.speech.RecognizerIntent;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.societies.security.digsig.api.Verify;
import org.xmlpull.v1.XmlSerializer;

import si.setcce.collaborativemeeting.json.Meeting;
import si.setcce.collaborativemeeting.net.GetMeetingInfoTask;
import si.setcce.collaborativemeeting.net.UploadMinutesTask;
import si.setcce.collaborativemeeting.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * Created by Clive on 23.9.2013.
 */
public class SecondActivity extends FragmentActivity implements
		UsersFragment.OnHeadlineSelectedListener {

	private static final String TAG = SecondActivity.class.getSimpleName();

	private static final int RESULT_SPEECH = 1;

	private static final String ACTION_MEETING_INFO = "MEETING_INFO";
	private static final String MEETING_INFO_MEETING_ID = "MEETING_INFO_MEETING_ID";
	private static final String MEETING_INFO_MEETING_SUBJECT = "MEETING_INFO_MEETING_SUBJECT";
	private static final String MEETING_INFO_USERS = "MEETING_INFO_USERS";
	private static final String MEETING_INFO_MINUTES = "MEETING_INFO_MINUTES";
	private static final String MEETING_INFO_NEW_MEETING = "MEETING_INFO_NEW_MEETING";
	
	private String member;
	private String filename;
	private int minutePosition = 0;
	private ListView lvMinutes;
	private SentencesCustomAdapter listAdapter = null;
	private MinutesModel model;
	private String data = "";

	/** True if there are any minutes inserted. False if minutes are empty. */
	private boolean minutesInserted;
	private boolean minuteSelected = false;

	protected static ArrayList<MinutesModel> minutesModel = new ArrayList<MinutesModel>();

	public String meetingId;
	public UsersFragment fragmentUsers;

	/** Messenger for communicating with the service. */
	private Messenger mService = null;

	/** Flag indicating whether we have called bind on the service. */

	private boolean mBound;
	/**
	 * Target we publish for clients to send messages to IncomingHandler.
	 */
	private final Messenger mMessenger = new Messenger(new IncomingHandler(this));
	
	private String downloadUri = null;
	
	private static ProgressDialog mBusyDialog;
	private MeetingInfoReceiver receiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_second);

		Log.d(TAG, "onCreate()");
		
		fragmentUsers = (UsersFragment) getSupportFragmentManager().findFragmentById(R.id.users_fragment);

		lvMinutes = (ListView) findViewById(R.id.lvMinutes);
		if (minutesModel.size() == 0) {
			lvMinutes.setVisibility(View.GONE);
		}
		lvMinutes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View item, int position, long id) {
                showOptions(position);
                if (!minuteSelected) {
                    minuteSelected = true;
                    invalidateOptionsMenu();
                }
            }
        });
//		lvMinutes.setOnItemLongClickListener(new OnItemLongClickListener() {
//			@Override
//			public boolean onItemLongClick(AdapterView<?> parent, View item, int position, long id) {
//				Log.d(TAG, "onItemLongClick, position = " + position);
//				// TODO
//				return true;
//			}
//		});
		
		if (fragmentUsers.size() == 0) {
			updateMeetingInfoFromServer();
		}
		new GcmRegistration(getApplicationContext(), this).register();
	}
	
	@Override
	protected void onStart() {
		
		super.onStart();
		
		Log.d(TAG, "onStart()");
		
		restore();
		reloadMinutes();

//		// TODO: remove (for testing only, so you don't have to talk every time you test)
//		Intent data = new Intent();
//		ArrayList<String> al = new ArrayList<String>();
//		al.add("bla bla");
//		data.putStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS, al);
//		onActivityResult(RESULT_SPEECH, RESULT_OK, data);
//		al.clear();
//		al.add("this meeting is a load of rubbish");
//		onActivityResult(RESULT_SPEECH, RESULT_OK, data);
	}
	
	@Override
	protected void onResume() {
	
		super.onResume();
		
		Log.d(TAG, "onResume()");
		IntentFilter filter = new IntentFilter(ACTION_MEETING_INFO);
		receiver = new MeetingInfoReceiver();
		registerReceiver(receiver, filter);
		Log.d(TAG, "Receiver registered");
	}
	
	@Override
	protected void onPause() {
		
		super.onPause();
		
		Log.d(TAG, "onPause()");

		if (mBound) {
			unbindService(mConnection);
			Log.d(TAG, "Service unbound");
			mBound = false;
		}

		if (receiver != null) {
			unregisterReceiver(receiver);
			receiver = null;
			Log.d(TAG, "Receiver unregistered");
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		Log.d(TAG, "onStop()");
		store();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		MenuItem miFinalize = menu.findItem(R.id.menu_finalize);
		MenuItem miEdit = menu.findItem(R.id.menu_edit);
		MenuItem miDelete = menu.findItem(R.id.menu_delete);
		MenuItem miShowQr = menu.findItem(R.id.menu_showQrCodeOfDownloadUri);

		Log.d(TAG, "onPrepareOptionsMenu: minutesInserted = " + minutesInserted + ", minuteSelected = " + minuteSelected);
		
		miFinalize.setVisible(minutesInserted);
		miEdit.setVisible(minutesInserted && minuteSelected);
		miDelete.setVisible(minutesInserted && minuteSelected);
		miShowQr.setVisible(downloadUri != null);
		
		return true;
	}

	private void store() {
		SharedPreferences preferences = getPreferences(MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString("member", member);
		editor.putString("meetingId", meetingId);
		editor.putBoolean("minutesInserted", minutesInserted);
		editor.putString("fragmentMinutesTextView",
				((TextView) findViewById(R.id.fragmentMinutesTextView)).getText().toString());
		editor.commit();
		Log.d(TAG, "Stored member = " + member + ", meetingId = " + meetingId + ", minutesInserted = " + minutesInserted);
	}
	
	private void restore() {
		
		SharedPreferences preferences = getPreferences(MODE_PRIVATE);
		
		member = preferences.getString("member", "");
		meetingId = preferences.getString("meetingId", "");
		minutesInserted = preferences.getBoolean("minutesInserted", false);
		
		TextView tv = ((TextView) findViewById(R.id.fragmentMinutesTextView));
		String str = preferences.getString("fragmentMinutesTextView", getText(R.string.defaultMeetingSubject).toString());
		tv.setText(str);
		
		Log.d(TAG, "Restored member = " + member + ", meetingId = " + meetingId + ", minutesInserted = " + minutesInserted);
	}

	private void speak(int id) {

		member = fragmentUsers.getUserName(id);

		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

		try {
			startActivityForResult(intent, RESULT_SPEECH);
		} catch (ActivityNotFoundException a) {
			Toast t = Toast.makeText(getApplicationContext(),
					"This device doesn't support Speech to Text",
					Toast.LENGTH_SHORT);
			t.show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d(TAG, "onActivityResult(" + requestCode + ", " + resultCode + ", ...)");

		switch (requestCode) {
		case RESULT_SPEECH:
			if (resultCode == RESULT_OK && null != data) {

				Log.d(TAG, "Speech received");
				ArrayList<String> text = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

				MinutesModel model = new MinutesModel();
				model.setDate(new Date());
				model.setUserId(fragmentUsers.getUserId(member));
				model.setUserName(member);
				model.setMinute(text.get(0));
				addMinute(model);
			}
			Log.d(TAG, "Setting minutesInserted to true");
			minutesInserted = true;
			break;
		}
	}
	
	private void reloadMinutes() {
		
		if (lvMinutes != null) {
			
			Log.d(TAG, "Reloading minutes");
			
			lvMinutes = (ListView) findViewById(R.id.lvMinutes);
			listAdapter = new SentencesCustomAdapter(this, minutesModel);
			lvMinutes.setAdapter(listAdapter);
			minuteSelected = false;
		}
		minutesInserted = (minutesModel.size() > 0);
	}

	private void showOptions(int position) {
		minutePosition = position;
		lvMinutes.setAdapter(listAdapter);
		lvMinutes.setItemChecked(position, true);
		lvMinutes.setSelection(position);
		Log.d(TAG, "Minute #" + position + " checked");
	}

	private void confirmDeleteMinute() {
		new AlertDialog.Builder(this)
		.setTitle(getText(R.string.confirmDeleteMinute))
		.setPositiveButton(getText(R.string.ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				removeMinute();
			}
		})
		.setNegativeButton(getText(R.string.cancel), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		})
		.show();
	}

	private void confirmNewMeeting() {
		new AlertDialog.Builder(this)
		.setTitle(getText(R.string.confirmNewMeeting))
		.setPositiveButton(getText(R.string.ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				fragmentUsers.deleteAllUsers();
				updateMeetingInfoFromServer();
				new GcmRegistration(SecondActivity.this.getApplicationContext(), SecondActivity.this).register();
			}
		})
		.setNegativeButton(getText(R.string.cancel), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		})
		.show();
	}

	private void editMinute() {
		model = minutesModel.get(minutePosition);

		LayoutInflater inflater = this.getLayoutInflater();

		final View textEntryView = inflater.inflate(R.layout.edit_minute, null);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(textEntryView);
		builder.setTitle(getText(R.string.editMinute));
		EditText txtMinute = (EditText) textEntryView.findViewById(R.id.txtMinute);
		txtMinute.setText(model.getMinute());
		builder.setPositiveButton(getText(R.string.ok), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				EditText txtMinute = (EditText) textEntryView.findViewById(R.id.txtMinute);
				model.setMinute(txtMinute.getText().toString());
				minutesModel.set(minutePosition, model);
				reloadMinutes();
			}
		});
		builder.setNegativeButton(getText(R.string.cancel), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		});
		builder.show();

		minuteSelected = false;
		invalidateOptionsMenu();
	}

	private void removeMinute() {
		minutesModel.remove(minutePosition);
		if (minutesModel.size() == 0) {
			lvMinutes.setVisibility(View.GONE);
			Log.d(TAG, "Setting minutesInserted to false");
			minutesInserted = false;
		} else {
			reloadMinutes();
		}
		minuteSelected = false;
		invalidateOptionsMenu();
	}
	
	public void removeAllMinutes() {
		
		Log.d(TAG, "Removing all minutes");

		minutesModel.clear();
		lvMinutes.setVisibility(View.GONE);
		minutesInserted = false;
		minuteSelected = false;
		invalidateOptionsMenu();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_addUser:
			Log.d(TAG, "Menu: add user clicked");
			fragmentUsers.addUser(new User("", "New User", true));
			return true;
		case R.id.menu_finalize:
			Log.d(TAG, "Menu: finalize clicked");
			if (minutesModel.size() == 0) {
				Toast.makeText(this, "Cannot finalize since no minutes have been recorded", Toast.LENGTH_LONG).show();
			} else {
				finalizeDocument();
				readFile(true, true);
				obtainCommunitySignatureUris();
			}
			return true;
		case R.id.menu_edit:
			Log.d(TAG, "Menu: edit clicked");
//			confirmEdit();
			editMinute();
			return true;
		case R.id.menu_delete:
			Log.d(TAG, "Menu: delete clicked");
			confirmDeleteMinute();
			return true;
		case R.id.menu_newMeeting:
			Log.d(TAG, "Menu: new meeting clicked");
			confirmNewMeeting();
			return true;
		case R.id.menu_showQrCodeOfDownloadUri:
			Log.d(TAG, "Menu: show QR code of download URI clicked");
			showQrCode(downloadUri);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void finalizeDocument() {
		try {
			Calendar c = Calendar.getInstance();
			int date = c.get(Calendar.DATE);
			int month = c.get(Calendar.MONTH) + 1;
			int year = c.get(Calendar.YEAR);

			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);
			int second = c.get(Calendar.SECOND);

			filename = year + "-" + month + "-" + date + "T" + hour + ":" + minute + ":" + second + ".xml";

			File sdCard = Environment.getExternalStorageDirectory();
			File dir = new File(sdCard.getAbsolutePath() + "/Meetings");
			dir.mkdirs();
			File file = new File(dir, filename);

			FileOutputStream fos = new FileOutputStream(file);

			XmlSerializer serializer = Xml.newSerializer();
			serializer.setOutput(fos, "UTF-8");
			serializer.startDocument(null, true);
			//serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);

			serializer.startTag(null, "meetings");
			serializer.startTag(null, "meeting").attribute(null, "Id", meetingId);

			for (int i = 0; i < minutesModel.size(); i++) {

				MinutesModel mm = minutesModel.get(i);

				serializer.startTag(null, "conversation")

				.startTag(null, "user")
				.startTag(null, "id")
				.text(mm.getUserId())
				.endTag(null, "id")
				.startTag(null, "username")
				.text(mm.getUserName())
				.endTag(null, "username")
				.endTag(null, "user")

				.startTag(null, "minute")
				.text(mm.getMinute())
				.endTag(null, "minute")
				.startTag(null, "time")
				.text(mm.getDateString())
				.endTag(null, "time")
				.startTag(null, "important")
				.text(String.valueOf(mm.isImportant()))
				.endTag(null, "important")

				.endTag(null, "conversation");
			}
			serializer.endDocument();

			serializer.flush();

			fos.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void readFile(final boolean showParsedXml, final boolean showPopUp) {
		try {
			File sdCard = Environment.getExternalStorageDirectory();
			File dir = new File(sdCard.getAbsolutePath() + "/Meetings");
			dir.mkdirs();
			File file = new File(dir, filename);

			FileInputStream fis = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fis);

			char[] inputBuffer = new char[fis.available()];
			isr.read(inputBuffer);
			data = new String(inputBuffer);
			isr.close();
			fis.close();

			if (showPopUp) {
				showXml();
			}
		} catch (Exception e) {
			Log.w(TAG, e);
		}
	}

	private void showXml() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
//		builder.setMessage(data)
		builder.setMessage(getText(R.string.minutesAreDistributed).toString())
		.setPositiveButton(getText(R.string.ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
			}
		}).show();
	}

	@Override
	public void onUserSelected(int position) {
		speak(position);
	}

	private void obtainCommunitySignatureUris() {
		if (mBound) {
			generateUris();
		} else {
			Intent intent = new Intent(Verify.ACTION);
			bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
		}
	}

	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			mService = new Messenger(service);
			mBound = true;
			generateUris();
		}

		public void onServiceDisconnected(ComponentName className) {
			mService = null;
			mBound = false;
		}
	};
	
	private void generateUris() {
		if (!mBound) return;
		// Create and send a message to the service, using a supported 'what' value
		Message msg = Message.obtain(null, Verify.Methods.GENERATE_URIS, 0, 0);
		Bundle data = new Bundle();
		String notificationEndpoint = getText(R.string.communitySignatureNotificationUrl).toString();
		data.putString(Verify.Params.NOTIFICATION_ENDPOINT, notificationEndpoint);
		int numSignersThreshold = Integer.valueOf(getText(R.string.communitySignatureNumSignersThreshold).toString());
		data.putInt(Verify.Params.NUM_SIGNERS_THRESHOLD, numSignersThreshold);
		msg.setData(data);
		msg.replyTo = mMessenger;
		try {
			Log.i(TAG, "Sending message to service");
			mService.send(msg);
			Log.i(TAG, "Message sent to service");
		} catch (Exception e) {
			Log.e(TAG, "sayHello", e);
		}
	}
	
	/**
	 * Handler of receiving replies.
	 */
	static class IncomingHandler extends Handler {

		private final WeakReference<SecondActivity> mService;

		IncomingHandler(SecondActivity service) {
			mService = new WeakReference<SecondActivity>(service);
		}

		@Override
		public void handleMessage(Message msg) {

			Log.i(TAG, "handleMessage: msg.what = " + msg.what);

			if (!msg.getData().getBoolean(Verify.Params.SUCCESS)) {
				Log.i(TAG, "handleMessage: an error occurred in service");
				Toast.makeText(mService.get(), "Could not get URIs from DigSig service", Toast.LENGTH_LONG).show();
				return;
			}
			switch (msg.what) {
			case Verify.Methods.GENERATE_URIS:
					
				// Now upload your XML document with this URI.
				// Optionally, you can sign your XML document yourself before or after upload.
				String uploadUri = msg.getData().getString(Verify.Params.UPLOAD_URI);
				Log.i(TAG, "handleMessage: GENERATE_URIS: upload URI = " + uploadUri);

				// After you upload the XML document, distribute the download URI to others to sign it.
				String downloadUri = msg.getData().getString(Verify.Params.DOWNLOAD_URI);
				Log.i(TAG, "handleMessage: GENERATE_URIS: download URI = " + downloadUri);
				
				new UploadMinutesTask(mService.get().getApplicationContext()).execute(
						uploadUri,
						mService.get().data,
						mService.get().getText(R.string.crowdTaskingServerMinutes).toString(),
						mService.get().meetingId,
						downloadUri);
				mService.get().downloadUri = downloadUri;
				mService.get().invalidateOptionsMenu();
				break;
			default:
				super.handleMessage(msg);
			}
		}
	}
	
	private void showQrCode(String data) {
		
		String action = "com.google.zxing.client.android.ENCODE";
		
		if (OtherApps.isActivityAvailable(this, action)) {
			String type = "TEXT_TYPE";
			Intent intent = new Intent(action);
			intent.putExtra("ENCODE_TYPE", type);
			intent.putExtra("ENCODE_DATA", data);
			startActivity(intent);
		}
		else {
			Toast.makeText(getApplicationContext(), "ZXing Barcode Scanner not installed!",
					Toast.LENGTH_SHORT).show();
		}
	}
	
	public void setMeetingSubject(String subject) {
		
		Log.i(TAG, "Setting meeting subject to: " + subject);

		TextView v = (TextView) findViewById(R.id.fragmentMinutesTextView);
		v.setText(subject);
	}
	
	private class MeetingInfoReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			
			Log.i(TAG, "Broadcast received");
			
			if (mBusyDialog != null) {
				mBusyDialog.cancel();
			}
			mBusyDialog = null;

			if (intent.getBooleanExtra(MEETING_INFO_NEW_MEETING, false)) {
				Log.d(TAG, "Removing previous users and minutes");
				removeAllMinutes();
				fragmentUsers.deleteAllUsers();
			}
			
			String value = intent.getStringExtra(MEETING_INFO_MEETING_ID);
			if (value != null) {
				Log.d(TAG, "Setting meeting ID to " + value);
				meetingId = value;
			}

			value = intent.getStringExtra(MEETING_INFO_MEETING_SUBJECT);
			if (value != null) {
				Log.d(TAG, "Setting meeting subject to " + value);
				setMeetingSubject(value);
			}

			List<User> users = (List<User>) intent.getSerializableExtra(MEETING_INFO_USERS);
			if (users != null) {
				for (User user : users) {
					Log.d(TAG, "Adding user " + user);
					fragmentUsers.addUser(user);
				}
			}

			List<MinutesModel> minutes = (List<MinutesModel>) intent.getSerializableExtra(MEETING_INFO_MINUTES);
			if (minutes != null) {
				addMinutes(minutes);
			}
		}
	}
	
	private void addMinute(MinutesModel minute) {
		List<MinutesModel> minutes = new ArrayList<MinutesModel>();
		minutes.add(minute);
		addMinutes(minutes);
	}
	
	private void addMinutes(List<MinutesModel> minutes) {

		for (MinutesModel minute : minutes) {
			Log.d(TAG, "Adding minute " + minute);
			minutesModel.add(minute);
		}
		if (lvMinutes != null) {
			lvMinutes.setVisibility(View.VISIBLE);
		}
		invalidateOptionsMenu();
		reloadMinutes();
	}
	
	private void updateMeetingInfoFromServer() {
		
		if (mBusyDialog != null) {
			Log.d(TAG, "Cancelling previous busy dialog");
			mBusyDialog.cancel();
		}
		mBusyDialog = new ProgressDialog(this);
		mBusyDialog.setMessage(getText(R.string.retrievingDataFromNetwork));
		mBusyDialog.show();

		setMeetingSubject("");
		new GetMeetingInfoTask(getApplicationContext()).execute(getText(R.string.crowdTaskingServerUsers).toString());
	}
	
	/**
	 * Create {@link Intent} to be received by {@link MeetingInfoReceiver}
	 * @param meeting
	 * @return
	 */
	public static Intent buildIntent(Meeting meeting) {

		Intent intent = new Intent();

		intent.setAction(SecondActivity.ACTION_MEETING_INFO);
		
		String id = meeting.getMeetingId();
		if (id != null) {
			intent.putExtra(SecondActivity.MEETING_INFO_MEETING_ID, id);
		}
		
		String subject = meeting.getMeetingSubject();
		if (subject != null) {
			intent.putExtra(SecondActivity.MEETING_INFO_MEETING_SUBJECT, subject);
		}
		
		intent.putExtra(SecondActivity.MEETING_INFO_NEW_MEETING, meeting.isNew());
		
		List<User> users = meeting.getUsers();
		if (users != null && users.size() > 0) {
			intent.putExtra(SecondActivity.MEETING_INFO_USERS, (Serializable) users);
		}
		
		List<MinutesModel> minutes = meeting.getMinutes();
		if (minutes != null && minutes.size() > 0) {
			intent.putExtra(SecondActivity.MEETING_INFO_MINUTES, (Serializable) minutes);
		}
		
		return intent;
	}
}
