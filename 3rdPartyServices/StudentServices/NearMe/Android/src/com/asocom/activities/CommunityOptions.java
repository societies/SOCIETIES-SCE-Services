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

/**
 * 
 */
public class CommunityOptions extends Activity implements View.OnClickListener {

	private Menu menu;
	private LinearLayout layout;
	private OptionsChat optionsChat;
	private OptionsLeave optionsLeave;
	private OptionsMembers optionsMembers;
	private OptionsRecommend optionsRecommend;
	private final int CONTEXT_MENU_ID = 7;

	//
	private static final String ACTIVITY_NAME = "CommunityOptions";

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

	/**
	* 
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

} // end class