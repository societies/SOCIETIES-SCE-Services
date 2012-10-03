/*
 * 
 */
package com.asocom.activities;

import java.io.IOException;

import com.asocom.model.Manager;
import com.asocom.tools.Json;
import com.asocom.tools.Server;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

// TODO: Auto-generated Javadoc
/**
 * The Class ShowMessage.
 */
public class ShowMessage extends Activity implements View.OnClickListener {

	/** The accept. */
	private Button accept;

	/** The cancel. */
	private Button cancel;

	/** The text. */
	private TextView text;

	/** The Constant ACTIVITY_NAME. */
	private static final String ACTIVITY_NAME = "Message";

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
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

		Manager.setNameCurrentActivity(ACTIVITY_NAME);
		Manager.setCurrentActivity(this);
		text.setText("Do you want to join "
				+ Manager.getCommunities()
						.get(Manager.getCommunities().size() - 1)
						.getCommunityName() + " community ?");
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	public void onClick(View v) {

		switch (v.getId()) {
		case (R.id.message_accept):
			finish();
			Manager.getCommunities().get(Manager.getCommunities().size() - 1)
					.setMyCommunity(true);
			Manager.getCommunities().get(Manager.getCommunities().size() - 1)
					.addUser(Manager.getAllUsers().get(0));
			Activity act1 = (Activity) Manager.getCurrentActivity();
			try {
				act1.clearWallpaper();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			Server.sendData(Json.joinCommunity(Manager.getCommunities()
					.get(Manager.getCommunities().size() - 1).getId()));
			break;
		case (R.id.message_cancel):
			finish();
			break;
		}

	}

	/**
	 * Sets the message.
	 *
	 * @param text the new message
	 */
	public void setMessage(String text) {
		this.text.setText(text);
	}

	/* (non-Javadoc)
	 * @see android.content.ContextWrapper#clearWallpaper()
	 */
	public void clearWallpaper() {

	}

	/**
	 * Gets the activity name.
	 *
	 * @return the activity name
	 */
	public static String getActivityName() {
		return ACTIVITY_NAME;
	}

} // end class
