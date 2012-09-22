/*
 * 
 */
package com.asocom.activities;

import com.asocom.components.Menu;
import com.asocom.components.Options;
import com.asocom.components.OptionsAddFriend;
import com.asocom.components.OptionsChat;
import com.asocom.components.OptionsLeave;
import com.asocom.model.Manager;
import com.asocom.tools.Json;
import com.asocom.tools.NonExistentUserException;
import com.asocom.tools.Server;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;

// TODO: Auto-generated Javadoc
/**
 * The Class UserOptions.
 */
public class UserOptions extends Activity implements View.OnClickListener {

	/** The menu. */
	private Menu menu;
	
	/** The layout. */
	private LinearLayout layout;
	
	/** The options chat. */
	private OptionsChat optionsChat;
	
	/** The options leave. */
	private OptionsLeave optionsLeave;
	
	/** The options add friend. */
	private OptionsAddFriend optionsAddFriend;
	
	/** The CONTEX t_ men u_ id. */
	private final int CONTEXT_MENU_ID = 7;
	
	/** The options. */
	private Options options;

	//
	/** The Constant ACTIVITY_NAME. */
	private static final String ACTIVITY_NAME = "UserOptions";

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_void);
		//
		LinearLayout topLayout = (LinearLayout) findViewById(R.id.main_void_topLayout);
		layout = (LinearLayout) findViewById(R.id.main_void_layout);
		//
		menu = new Menu(this);
		topLayout.addView(menu);

		//

		try {

			options = new Options(this);
			options.setImageStatus(Manager.getCurrentStatus());
			options.setTextTop(Manager.getAllUsers()
					.get(Manager.getCurrentUserPos()).getName());
			options.setImageDescription(Manager.getAllUsers()
					.get(Manager.getCurrentUserPos()).getImage());
			options.setTextDescription(Manager.getAllUsers()
					.get(Manager.getCurrentUserPos()).getDescription());
			options.getButton01().setText("Individual");
			layout.addView(options);

			optionsChat = new OptionsChat(this);
			optionsAddFriend = new OptionsAddFriend(this);
			optionsLeave = new OptionsLeave(this);

			if (Manager.getCurrentUser().isFriend()) {
				layout.addView(optionsChat);
			}

			if (Manager.getAllUsers().get(Manager.getCurrentUserPos())
					.isFriend()) {
				optionsLeave = new OptionsLeave(this);
				layout.addView(optionsLeave);

			} else {
				optionsAddFriend = new OptionsAddFriend(this);
				layout.addView(optionsAddFriend);
			}

			options.getButton03().setOnClickListener(this);

		} catch (NonExistentUserException e) {
			finish();
			Intent i = new Intent(this, HomeMenu.class);
			startActivity(i);
		}

		//
		Manager.setNameCurrentActivity(ACTIVITY_NAME);
		Manager.setCurrentActivity(this);
	}

	/**
	 * Options_icon.
	 *
	 * @param v the v
	 */
	public void options_icon(View v) {
		if (optionsChat.getCode() == v.hashCode()) {
			Manager.getCurrentUser().getChat().resetNewMessages();
			Intent in = new Intent(this, UserChatActivity.class);
			startActivity(in);
			finish();
			return;
		}
		Manager.setIntValue(-1);

		try {

			if (optionsLeave.getCode() == v.hashCode()) {
				// TODO avisar a s2
				Manager.removeFriend(Manager.getAllUsers().get(
						Manager.getCurrentUserPos()));
				Intent in = new Intent(this, Friends.class);
				startActivity(in);
				finish();
				return;
			}

		} catch (NonExistentUserException e) {
			finish();
			Intent i = new Intent(this, HomeMenu.class);
			startActivity(i);
		}

		if (optionsAddFriend.getCode() == v.hashCode()) {
			Server.sendData(Json
					.friendRequest(Manager.getCurrentUser().getIp()));
			Intent in = new Intent(this, HomeMenu.class);
			startActivity(in);
			finish();
			return;
		}
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	public void onClick(View v) {
		if (v.getId() == R.id.options_button_03) {
			Manager.setIntValue(-1);
			Intent in = new Intent(this, UserProfileActivity.class);
			startActivity(in);
			finish();
		}
		menu.onClick(v);
		options.setImageStatus(Manager.getCurrentStatus());
	}

	/**
	 * create context menu.
	 *
	 * @param id the id
	 * @return the dialog
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == CONTEXT_MENU_ID)
			return menu.getIconContextMenu().createMenu("Select status");
		return super.onCreateDialog(id);
	}

	//
	/* (non-Javadoc)
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent homeMenu = new Intent(this, HomeMenu.class);
			startActivity(homeMenu);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/* (non-Javadoc)
	 * @see android.content.ContextWrapper#clearWallpaper()
	 */
	public void clearWallpaper() {

	}

	//
	/**
	 * Gets the activity name.
	 *
	 * @return the activity name
	 */
	public static String getActivityName() {
		return ACTIVITY_NAME;
	}

} // end class