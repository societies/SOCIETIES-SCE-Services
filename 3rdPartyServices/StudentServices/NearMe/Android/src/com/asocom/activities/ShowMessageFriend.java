package com.asocom.activities;

import com.asocom.model.Manager;
import com.asocom.tools.Json;
import com.asocom.tools.Server;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

/**
 * 
 */
public class ShowMessageFriend extends Activity implements View.OnClickListener {

	/**
	 * 
	 */
	private Button accept;

	/**
	 * 
	 */
	private Button cancel;

	/**
     * 
     */
	private TextView text;

	/**
     * 
     */
	private static final String ACTIVITY_NAME = "Message";

	/**
	 * 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message);
		//
		accept = (Button) findViewById(R.id.message_accept);
		cancel = (Button) findViewById(R.id.message_cancel);
		text = (TextView) findViewById(R.id.message_message);

		Manager.setNameCurrentActivity(ACTIVITY_NAME);
		Manager.setCurrentActivity(this);
		accept.setOnClickListener(this);
		cancel.setOnClickListener(this);
		cancel.setText("Reject");
		Manager.setNameCurrentActivity(ACTIVITY_NAME);
		Manager.setCurrentActivity(this);
		text.setText(Manager.getAllUsers().get(Manager.getIntValue()).getName()
				+ ": Do you want to be my friend?");
	}

	/**
	*
	*/
	public void onClick(View v) {

		switch (v.getId()) {
		case (R.id.message_accept):
			finish();
			Manager.addFriend(Manager.getAllUsers().get(Manager.getIntValue()));
			try {
				Server.sendData(Json.friendRequestResponse("yes",
						Manager.getStringValue()));
				Activity act1 = (Activity) Manager.getCurrentActivity();
				act1.clearWallpaper();
			} catch (Exception e) {

			}

			break;
		case (R.id.message_cancel):
			Server.sendData(Json.friendRequestResponse("not",
					Manager.getStringValue()));
			finish();
			break;
		}

	}

	public void setMessage(String text) {
		this.text.setText(text);
	}

	public void clearWallpaper() {

	}

	public static String getActivityName() {
		return ACTIVITY_NAME;
	}

} // end class
