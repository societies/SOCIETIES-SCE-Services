package com.asocom.activities;

import com.asocom.components.MemberProfile;
import com.asocom.components.Menu;
import com.asocom.components.Options;
import com.asocom.model.Manager;
import com.asocom.tools.NonExistentUserException;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;

public class UserProfileActivity extends Activity implements
		View.OnClickListener {

	private Menu menu;
	private MemberProfile mp;
	private final int CONTEXT_MENU_ID = 7;

	//
	private static final String ACTIVITY_NAME = "UserProfileActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_void);

		LinearLayout topLayout = (LinearLayout) findViewById(R.id.main_void_topLayout);
		LinearLayout layout = (LinearLayout) findViewById(R.id.main_void_layout);
		//
		menu = new Menu(this);
		topLayout.addView(menu);

		try {
			Options options = new Options(this);
			options.setImageStatus(Manager.getCurrentStatus());
			options.setTextTop(Manager.getAllUsers()
					.get(Manager.getCurrentUserPos()).getName());
			options.setImageDescription(Manager.getAllUsers()
					.get(Manager.getCurrentUserPos()).getImage());
			options.setTextDescription(Manager.getAllUsers()
					.get(Manager.getCurrentUserPos()).getDescription());
			options.getButton03()
					.setBackgroundResource(R.drawable.list_menu_02);
			options.getButton01().setText("Individual");
			options.getButton01().setBackgroundResource(R.drawable.list_menu);
			options.getButton01().setOnClickListener(this);
			topLayout.addView(options);
			mp = new MemberProfile(this);
			mp.setName(Manager.getAllUsers().get(Manager.getCurrentUserPos())
					.getName());
			mp.setDescription(Manager.getAllUsers()
					.get(Manager.getCurrentUserPos()).getDescription());
			mp.setDate(Manager.getAllUsers().get(Manager.getCurrentUserPos())
					.getDateOfBirth());
			mp.setGender(Manager.getAllUsers().get(Manager.getCurrentUserPos())
					.getGender());
			mp.setEmail(Manager.getAllUsers().get(Manager.getCurrentUserPos())
					.getEmail());
			mp.setStatus(Manager.getAllUsers().get(Manager.getCurrentUserPos())
					.getProfile());

			layout.addView(mp);
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
	*
	*/
	public void onClick(View v) {
		if (v.getId() == R.id.options_button_01) {
			Intent i = new Intent(this, UserOptions.class);
			startActivity(i);
			finish();
			return;
		}
		menu.onClick(v);
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
		}
		return super.onKeyDown(keyCode, event);
	}

	public void clearWallpaper() {

	}

	//
	public static String getActivityName() {
		return ACTIVITY_NAME;
	}
}