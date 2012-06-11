package com.asocom.activities;

import com.asocom.components.Home;
import com.asocom.components.Menu;
import com.asocom.components.TopTitre;

import com.asocom.model.Manager;
import com.asocom.tools.Json;
import com.asocom.tools.Server;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;

public class HomeMenu extends Activity implements View.OnClickListener {

	private Home home;
	private Menu menu;
	private final int CONTEXT_MENU_ID = 7;

	//
	private static final String ACTIVITY_NAME = "HomeMenu";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_void);

		LinearLayout topLayout = (LinearLayout) findViewById(R.id.main_void_topLayout);
		LinearLayout layout = (LinearLayout) findViewById(R.id.main_void_layout);
		//
		menu = new Menu(this);
		topLayout.addView(menu);
		TopTitre topTitre = new TopTitre(this);
		topTitre.setTextTitre("Home");
		topLayout.addView(topTitre);

		home = new Home(this);
		layout.addView(home);
		Manager.setNameCurrentActivity(ACTIVITY_NAME);
		Manager.setCurrentActivity(this);

	}

	// private final int CONTEXT_MENU_ID = 7;
	public void onClick(View v) {

		home.onClick(v);
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

	public void clearWallpaper() {

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Server.sendData(Json.disconnect());
			finish();
			Manager.closeAll();
		}
		return super.onKeyDown(keyCode, event);
	}

	//
	public static String getActivityName() {
		return ACTIVITY_NAME;
	}

}