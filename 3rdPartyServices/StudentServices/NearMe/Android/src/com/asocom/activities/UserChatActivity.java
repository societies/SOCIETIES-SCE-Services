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
 * The Class UserChatActivity.
 */
public class UserChatActivity extends Activity implements View.OnClickListener {

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
	private static final String ACTIVITY_NAME = "UserChatActivity";

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
			topTitre.setTextTitre(Manager.getAllUsers()
					.get(Manager.getCurrentUserPos()).getName()
					+ "'s Chat");
			topLayout.addView(topTitre);

			chatComponent = new ChatComponent(this);
			topLayout.addView(chatComponent);
			chatComponent.getButtonSend().setOnClickListener(this);
			//
			loadAllUsers();
		} catch (Exception e) {
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
	 * @throws NonExistentUserException the non existent user exception
	 */
	public void loadAllUsers() throws NonExistentUserException {
		chatComponent.resetChat();

		for (int i = Manager.getAllUsers().get(Manager.getCurrentUserPos())
				.getChat().getSmsList().size() - 1; i >= 0; i--) {
			ChatMsgComponent cm = new ChatMsgComponent(this);
			cm.setDate(Manager.getAllUsers().get(Manager.getCurrentUserPos())
					.getChat().getSmsList().get(i).getDateSms());
			cm.setTextChat(
					Manager.getAllUsers().get(Manager.getCurrentUserPos())
							.getChat().getSmsList().get(i).getUser(), Manager
							.getAllUsers().get(Manager.getCurrentUserPos())
							.getChat().getSmsList().get(i).getMessage());
			cm.setImageUser(Manager.getAllUsers()
					.get(Manager.getCurrentUserPos()).getChat().getSmsList()
					.get(i).getImage());
			chatComponent.addChatMsg(cm);
		}
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	public void onClick(View v) {

		if (v.getId() == R.id.chat_button
				&& !chatComponent.getEditText().toString().equals("")) {
			String dt;
			Date cal = Calendar.getInstance().getTime();
			dt = cal.toLocaleString();
			ChatMsg smsChat = new ChatMsg(Integer.parseInt(Manager
					.getCurrentPhoneUser().get(6)), dt, Manager
					.getCurrentPhoneUser().get(0), chatComponent.getText());

			try {
				Server.sendData(Json.sendPrivateChat(
						Manager.getCurrentUserIP(), chatComponent.getText()));
				Manager.getAllUsers().get(Manager.getCurrentUserPos())
						.getChat().addSms(smsChat);
				chatComponent.getEditText().setText("");
				loadAllUsers();
			} catch (Exception e) {
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
		Intent i = new Intent(this, UserChatActivity.class);
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