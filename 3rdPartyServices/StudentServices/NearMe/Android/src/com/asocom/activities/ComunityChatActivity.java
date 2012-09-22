/*
 * 
 */
package com.asocom.activities;

import java.util.Calendar;
import java.util.Date;

import com.asocom.components.ChatComponent;
import com.asocom.components.ChatMsgComponent;
import com.asocom.components.Menu;
import com.asocom.components.TopTitre;
import com.asocom.model.Manager;
import com.asocom.model.ChatMsg;
import com.asocom.tools.Json;
import com.asocom.tools.NonExistentCommunityException;
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
 * The Class ComunityChatActivity.
 */
public class ComunityChatActivity extends Activity implements
		View.OnClickListener {

	/** The menu. */
	private Menu menu;
	
	/** The layout. */
	private LinearLayout layout;
	
	/** The chat component. */
	private ChatComponent chatComponent;
	
	/** The CONTEX t_ men u_ id. */
	private final int CONTEXT_MENU_ID = 7;

	//
	/** The Constant ACTIVITY_NAME. */
	private static final String ACTIVITY_NAME = "ComunityChatActivity";

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

		try {
			topTitre.setTextTitre(Manager.getCommunities()
					.get(Manager.getCurrentCommunityPos()).getCommunityName()
					+ "'s Chat");
			topLayout.addView(topTitre);

			chatComponent = new ChatComponent(this);
			topLayout.addView(chatComponent);
			chatComponent.getButtonSend().setOnClickListener(this);
			//
			loadAllUsers();
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
	 * Load all users.
	 *
	 * @throws NonExistentCommunityException the non existent community exception
	 */
	public void loadAllUsers() throws NonExistentCommunityException {
		chatComponent.resetChat();

		for (int i = Manager.getCommunities()
				.get(Manager.getCurrentCommunityPos()).getChat().getSmsList()
				.size() - 1; i >= 0; i--) {

			ChatMsgComponent cm = new ChatMsgComponent(this);
			cm.setDate(Manager.getCommunities()
					.get(Manager.getCurrentCommunityPos()).getChat()
					.getSmsList().get(i).getDateSms());
			cm.setTextChat(
					Manager.getCommunities()
							.get(Manager.getCurrentCommunityPos()).getChat()
							.getSmsList().get(i).getUser(),
					Manager.getCommunities()
							.get(Manager.getCurrentCommunityPos()).getChat()
							.getSmsList().get(i).getMessage());
			cm.setImageUser(Manager.getCommunities()
					.get(Manager.getCurrentCommunityPos()).getChat()
					.getSmsList().get(i).getImage());
			chatComponent.addChatMsg(cm);
		}
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	public void onClick(View v) {

		if (v.getId() == R.id.chat_button
				&& !chatComponent.getEditText().toString().equals("")) {

			try {
				String dt;
				Date cal = Calendar.getInstance().getTime();
				dt = cal.toLocaleString();
				ChatMsg smsChat = new ChatMsg(Integer.parseInt(Manager
						.getCurrentPhoneUser().get(6)), dt, Manager
						.getCurrentPhoneUser().get(0), chatComponent.getText());
				Manager.getCommunities().get(Manager.getCurrentCommunityPos())
						.getChat().addSms(smsChat);
				Server.sendData(Json.sendCommunityChat(Manager
						.getCurrentCommunity().getId(), chatComponent.getText()));
				chatComponent.getEditText().setText("");
				loadAllUsers();
			} catch (NonExistentCommunityException e) {
				finish();
				Intent i = new Intent(this, HomeMenu.class);
				startActivity(i);
			}
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
		Intent i = new Intent(this, ComunityChatActivity.class);
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

	/**
	 * Gets the layout.
	 *
	 * @return the layout
	 */
	public LinearLayout getLayout() {
		return layout;
	}

	/**
	 * Sets the layout.
	 *
	 * @param layout the new layout
	 */
	public void setLayout(LinearLayout layout) {
		this.layout = layout;
	}

} // end class