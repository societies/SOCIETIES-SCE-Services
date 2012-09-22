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
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;

// TODO: Auto-generated Javadoc
/**
 * The Class Friends.
 */
public class Friends extends Activity implements View.OnClickListener {

	/** The menu. */
	private Menu menu;
	
	/** The layout. */
	private LinearLayout layout;
	
	/** The CONTEX t_ men u_ id. */
	private final int CONTEXT_MENU_ID = 7;

	//
	/** The Constant ACTIVITY_NAME. */
	private static final String ACTIVITY_NAME = "Friends";

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
		topTitre.setTextTitre("Friends");
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

		for (int i = 0; i < Manager.getAllUsers().size(); i++) {
			if (Manager.getAllUsers().get(i).isFriend()) {
				PartList01 n = new PartList01(this);
				Manager.getAllUsers().get(i).setCode(n.getCode());
				n.setName(Manager.getAllUsers().get(i).getName());
				n.setImage(Manager.getAllUsers().get(i).getImage());
				n.setStatus(Manager.getAllUsers().get(i).getStatus());
				n.setNewMessage(Manager.getAllUsers().get(i).getChat()
						.getNewMessages());
				layout.addView(n);
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
				Intent in = new Intent(this, UserOptions.class);
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

	/* (non-Javadoc)
	 * @see android.content.ContextWrapper#clearWallpaper()
	 */
	public void clearWallpaper() {
		Intent i = new Intent(this, Friends.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		startActivity(i);
		finish();
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