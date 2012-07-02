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

/**
 * 
 */
public class UserOptionsCommunity extends Activity implements
		View.OnClickListener {

	private Menu menu;
	private LinearLayout layout;
	private OptionsChat optionsChat;
	private OptionsLeave optionsLeave;
	private OptionsAddFriend optionsAddFriend;
	private final int CONTEXT_MENU_ID = 7;
	private Options options;

	//
	private static final String ACTIVITY_NAME = "UserOptionsCommunity";

	/**
	 * 
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
			layout.addView(options);

			optionsChat = new OptionsChat(this);
			optionsAddFriend = new OptionsAddFriend(this);
			optionsLeave = new OptionsLeave(this);
			options.getButton01().setText("Individual");
			layout.addView(optionsChat);

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

	public void options_icon(View v) {
		if (optionsChat.getCode() == v.hashCode()) {
			Manager.getCurrentUser().getChat().resetNewMessages();
			Intent in = new Intent(this, UserChatActivity.class);
			startActivity(in);
			finish();
			return;
		}

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

	/**
	*
	*/
	public void onClick(View v) {
		if (v.getId() == R.id.options_button_03) {
			Intent in = new Intent(this, UserProfileActivity.class);
			startActivity(in);
			finish();
		}
		menu.onClick(v);
		options.setImageStatus(Manager.getCurrentStatus());
	}

	/**
	 * create context menu
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == CONTEXT_MENU_ID)
			return menu.getIconContextMenu().createMenu("Select status");
		return super.onCreateDialog(id);
	}

	//
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent homeMenu = new Intent(this, HomeMenu.class);
			startActivity(homeMenu);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void clearWallpaper() {

	}

	//
	public static String getActivityName() {
		return ACTIVITY_NAME;
	}

} // end class