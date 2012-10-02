/*
 * 
 */
package com.asocom.activities;

import com.asocom.components.Menu;
import com.asocom.components.PartList01;
import com.asocom.components.TopTitre;
import com.asocom.model.Manager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;

// TODO: Auto-generated Javadoc
/**
 * The Class AllUsers.
 */
public class AllUsers extends Activity implements View.OnClickListener {

	/** The menu. */
	private Menu menu;
	
	/** The layout. */
	private LinearLayout layout;

	/** The CONTEX t_ men u_ id. */
	private final int CONTEXT_MENU_ID = 7;

	//
	/** The Constant ACTIVITY_NAME. */
	private static final String ACTIVITY_NAME = "AllUsers";

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
		TopTitre topTitre = new TopTitre(this);
		topTitre.setTextTitre("All Users");
		topLayout.addView(topTitre);

		//
		loadAllUsers();

		//
		Manager.setNameCurrentActivity(ACTIVITY_NAME);
		Manager.setCurrentActivity(this);
	}

	/**
	 * Load all users.
	 */
	public void loadAllUsers() {
		Log.v("loadAllUsers()", "entro loadAllUsers()");

		Log.v("loadAllUsers()", "loadAllUsers() for friends");
		Log.v("loadAllUsers()", "tamano del array allusers="
				+ Manager.getAllUsers().size());
		for (int i = 0; i < Manager.getAllUsers().size(); i++) {
			Log.v("loadAllUsers()", "entro en el for friend i= " + i);
			if (Manager.getAllUsers().get(i).isFriend()) {
				Log.v("loadAllUsers()", "entro en el if friend i= " + i);
				Log.v("all user", "loadAllUsers() for friends");
				PartList01 n = new PartList01(this);
				Manager.getAllUsers().get(i).setCode(n.getCode());
				n.setName(Manager.getAllUsers().get(i).getName());
				n.setImage(Manager.getAllUsers().get(i).getImage());
				n.setStatus(Manager.getAllUsers().get(i).getStatus());
				Log.v("all user", "name: "
						+ Manager.getAllUsers().get(i).getName());
				layout.addView(n);
				Log.v("all user", "guardado friend: " + n);
			}
		}

		Log.v("loadAllUsers()", "loadAllUsers() for allusers");
		Log.v("loadAllUsers()", "tamano del array allusers="
				+ Manager.getAllUsers().size());
		for (int i = 0; i < Manager.getAllUsers().size(); i++) {
			Log.v("loadAllUsers()", "entro en el for allusers i= " + i);
			if (!Manager.getAllUsers().get(i).isFriend()) {
				Log.v("loadAllUsers()", "entro en el if de for allusers i= "
						+ i);
				PartList01 n = new PartList01(this);
				Manager.getAllUsers().get(i).setCode(n.getCode());
				n.setName(Manager.getAllUsers().get(i).getName());
				n.setImage(Manager.getAllUsers().get(i).getImage());
				n.setStatus(Manager.getAllUsers().get(i).getStatus());
				n.deleteNewMessage();
				Log.v("all user", "name: "
						+ Manager.getAllUsers().get(i).getName());
				layout.addView(n);
				Log.v("all user", "guardado all user: ");
			}
		}
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	public void onClick(View v) {
		menu.onClick(v);
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

	/**
	 * Part list01.
	 *
	 * @param v the v
	 */
	public void PartList01(View v) {
		for (int i = 0; i < Manager.getAllUsers().size(); i++) {
			if (Manager.getAllUsers().get(i).getCode() == v.hashCode()) {
				Manager.setCurrentUser(Manager.getAllUsers().get(i));
				Intent in = new Intent(this, UserOptionProfileAllUser.class);
				startActivity(in);
				finish();
				return;
			}
		}
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

		}
		return super.onKeyDown(keyCode, event);
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

	/* (non-Javadoc)
	 * @see android.content.ContextWrapper#clearWallpaper()
	 */
	@Override
	public void clearWallpaper() {

		Intent allUsers = new Intent(this, AllUsers.class);
		allUsers.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		startActivity(allUsers);
		finish();

	}

} // end class