/*
 * 
 */
package com.asocom.activities;

import com.asocom.components.Menu;
import com.asocom.components.Options;
import com.asocom.components.OptionsChat;
import com.asocom.components.OptionsLeave;
import com.asocom.components.OptionsMembers;
import com.asocom.components.OptionsRecommend;
import com.asocom.model.Manager;
import com.asocom.tools.Json;
import com.asocom.tools.NonExistentCommunityException;
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
 * The Class CommunityOptions.
 */
public class CommunityOptions extends Activity implements View.OnClickListener {

	/** The menu. */
	private Menu menu;
	
	/** The layout. */
	private LinearLayout layout;
	
	/** The options chat. */
	private OptionsChat optionsChat;
	
	/** The options leave. */
	private OptionsLeave optionsLeave;
	
	/** The options members. */
	private OptionsMembers optionsMembers;
	
	/** The options recommend. */
	private OptionsRecommend optionsRecommend;
	
	/** The CONTEX t_ men u_ id. */
	private final int CONTEXT_MENU_ID = 7;

	//
	/** The Constant ACTIVITY_NAME. */
	private static final String ACTIVITY_NAME = "CommunityOptions";

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

			Options options = new Options(this);
			options.setImageStatus(Manager.getCurrentStatus());
			options.setTextTop(Manager.getCommunities()
					.get(Manager.getCurrentCommunityPos()).getCommunityName());
			options.setImageDescription(Manager.getCommunities()
					.get(Manager.getCurrentCommunityPos()).getImage());
			options.setTextDescription(Manager.getCommunities()
					.get(Manager.getCurrentCommunityPos()).getDescription());
			layout.addView(options);
			optionsChat = new OptionsChat(this);
			layout.addView(optionsChat);
			optionsLeave = new OptionsLeave(this);
			layout.addView(optionsLeave);
			optionsMembers = new OptionsMembers(this);
			layout.addView(optionsMembers);
			optionsRecommend = new OptionsRecommend(this);
			layout.addView(optionsRecommend);

			options.getButton03().setOnClickListener(this);
		} catch (NonExistentCommunityException e) {
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
			Intent in = new Intent(this, ComunityChatActivity.class);
			startActivity(in);
			finish();
			Manager.getCurrentCommunity().getChat().resetNewMessages();
			return;
		}

		try {

			if (optionsLeave.getCode() == v.hashCode()) {

				new AlertDialog.Builder(this)
						.setTitle("Confirm Leave Community")
						.setMessage(
								"Are you sure you want to leave this community?")
						.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {

										try {
											Manager.getCommunities()
													.get(Manager
															.getCurrentCommunityPos())
													.deleteUser(
															Manager.getCurrentPhoneUser()
																	.get(4));
											Manager.getCommunities()
													.get(Manager
															.getCurrentCommunityPos())
													.setMyCommunity(false);
											Server.sendData(Json.leaveCommunity(Manager
													.getCommunities()
													.get(Manager
															.getCurrentCommunityPos())
													.getId()));
											Intent in = new Intent(Manager
													.getCurrentActivity(),
													Communities.class);
											startActivity(in);
											finish();
										} catch (NonExistentCommunityException e) {
											e.printStackTrace();
										}
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

		} catch (Exception e) {
			finish();
			Intent i = new Intent(this, HomeMenu.class);
			startActivity(i);
		}

		if (optionsMembers.getCode() == v.hashCode()) {
			Intent in = new Intent(this, CommunityMembers.class);
			startActivity(in);
			finish();
			return;
		}

		if (optionsRecommend.getCode() == v.hashCode()) {
			Toast.makeText(this, "Recommend", 5).show();
			return;
		}
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	public void onClick(View v) {
		if (v.getId() == R.id.options_button_03) {
			Intent in = new Intent(this, CommunityProfileActivity.class);
			startActivity(in);
			finish();
		}

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