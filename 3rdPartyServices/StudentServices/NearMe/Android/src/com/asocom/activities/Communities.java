/*
 * 
 */
package com.asocom.activities;

import com.asocom.components.Menu;
import com.asocom.components.PartList04;
import com.asocom.components.PartList05;
import com.asocom.components.TopTitre;
import com.asocom.model.Manager;
import com.asocom.tools.Json;
import com.asocom.tools.Server;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;

// TODO: Auto-generated Javadoc
/**
 * The Class Communities.
 */
public class Communities extends Activity implements View.OnClickListener {

	/** The menu. */
	private Menu menu;
	
	/** The layout. */
	private LinearLayout layout;
	
	/** The CONTEX t_ men u_ id. */
	private final int CONTEXT_MENU_ID = 7;

	//
	/** The Constant ACTIVITY_NAME. */
	private static final String ACTIVITY_NAME = "Communities";

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
		topTitre.setTextTitre("Communities");
		topLayout.addView(topTitre);
		//
		loadCommunities();
		Manager.setNameCurrentActivity(ACTIVITY_NAME);
		Manager.setCurrentActivity(this);

	}

	/**
	 * Load communities.
	 */
	public void loadCommunities() {

		layout.removeAllViews();
		for (int i = 0; i < Manager.getCommunities().size(); i++) {
			if (Manager.getCommunities().get(i).isMyCommunity()) {
				PartList04 n = new PartList04(this);
				Manager.getCommunities().get(i).setCode(n.getCode());
				n.setDate(Manager.getCommunities().get(i).getDateOfCreation());
				n.setCommunityName(Manager.getCommunities().get(i)
						.getCommunityName());
				n.setNumberOfMembers(""
						+ Manager.getCommunities().get(i).getUserList().size());
				n.setCategory("" + Manager.getCommunities().get(i).getProfile());
				n.setNumberOfSms(Manager.getCommunities().get(i).getChat()
						.getNewMessages()
						+ "");
				n.setImageUser(Manager.getCommunities().get(i).getImage());
				layout.addView(n);
			}
		}

		for (int i = 0; i < Manager.getCommunities().size(); i++) {
			if (!Manager.getCommunities().get(i).isMyCommunity()
					&& Manager.getCommunities().get(i).getVisibility()
							.equals("public")) {
				PartList05 n = new PartList05(this);
				Manager.getCommunities().get(i).setCode(n.getCode());
				n.setDate(Manager.getCommunities().get(i).getDateOfCreation());
				n.setCommunityName(Manager.getCommunities().get(i)
						.getCommunityName());
				n.setNumberOfMembers(""
						+ Manager.getCommunities().get(i).getUserList().size());
				n.setCategory("" + Manager.getCommunities().get(i).getProfile());
				n.setImageUser(Manager.getCommunities().get(i).getImage());
				layout.addView(n);
			}
		}

		//
		Manager.setNameCurrentActivity(ACTIVITY_NAME);
		Manager.setCurrentActivity(this);
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
	 * Part list04.
	 *
	 * @param v the v
	 */
	public void PartList04(View v) {
		for (int i = 0; i < Manager.getCommunities().size(); i++) {
			if (Manager.getCommunities().get(i).getCode() == v.hashCode()) {

				Manager.setCurrentCommunity(Manager.getCommunities().get(i));
				finish();
				Intent in = new Intent(this, CommunityOptions.class);
				startActivity(in);

				return;
			}
		}
	}

	/** The community selected. */
	private int communitySelected;

	/**
	 * Part list05.
	 *
	 * @param v the v
	 */
	public void PartList05(View v) {
		for (communitySelected = 0; communitySelected < Manager
				.getCommunities().size(); communitySelected++) {
			if (Manager.getCommunities().get(communitySelected).getCode() == v
					.hashCode()) {

				new AlertDialog.Builder(this)
						.setTitle("Confirm join")
						.setMessage("Are you sure you want to join?")
						.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										Manager.getCommunities()
												.get(communitySelected)
												.setMyCommunity(true);
										Manager.getCommunities()
												.get(communitySelected)
												.addUser(
														Manager.getAllUsers()
																.get(0));
										Server.sendData(Json
												.joinCommunity(Manager
														.getCommunities()
														.get(communitySelected)
														.getId()));
										loadCommunities();

									}
								})
						.setNegativeButton("Cancel",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
									}
								}).show();
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
		Intent i = new Intent(this, Communities.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		startActivity(i);
		finish();
	}

} // end class