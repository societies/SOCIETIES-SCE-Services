/*
 * 
 */
package com.asocom.activities;

import java.util.Calendar;
import java.util.Date;
import com.asocom.components.CreateCommunityComponent;
import com.asocom.components.Menu;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.asocom.components.TopTitre;
import com.asocom.model.Community;
import com.asocom.model.Manager;
import com.asocom.tools.IconContextMenu;
import com.asocom.tools.Json;
import com.asocom.tools.Server;
import com.asocom.tools.Tools;

// TODO: Auto-generated Javadoc
/**
 * The Class CreateCommunity.
 */
public class CreateCommunity extends Activity implements View.OnClickListener {

	/** dialog de imagenes. */
	private IconContextMenu iconContextMenu = null;

	/** identificacion del dialogo de imagenes. */
	private final int CONTEXT_MENU2_ID = 2;

	/** The CONTEX t_ men u_ id. */
	private final int CONTEXT_MENU_ID = 7;

	/** numero de imagen del usuario. */
	private int image;

	/** The cc. */
	private CreateCommunityComponent cc;
	
	/** The menu. */
	private Menu menu;

	//
	/** The Constant ACTIVITY_NAME. */
	private static final String ACTIVITY_NAME = "CreateCommunity";

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_void);

		LinearLayout topLayout = (LinearLayout) findViewById(R.id.main_void_topLayout);
		LinearLayout layout = (LinearLayout) findViewById(R.id.main_void_layout);
		TopTitre topTitre = new TopTitre(this);
		topTitre.setTextTitre("Create Community");

		menu = new Menu(this);
		topLayout.addView(menu);
		topLayout.addView(topTitre);

		cc = new CreateCommunityComponent(this);
		layout.addView(cc);

		cc.getSave().setOnClickListener(this);
		cc.getCancel().setOnClickListener(this);
		image = -1;

		//
		Manager.setNameCurrentActivity(ACTIVITY_NAME);
		Manager.setCurrentActivity(this);

	}

	/**
	 * se llama cuando hace click la imagen para modificarla.
	 *
	 * @param view the view
	 */
	public void communityimage(View view) {
		initDialog();
	}

	/**
	 * eventos de la actividad.
	 *
	 * @param v the v
	 */
	public void onClick(View v) {

		switch (v.getId()) {
		case (R.id.create_community_save):
			createCommunity();
			break;

		case (R.id.create_community_cancel):
			exitActivity();
			break;

		}
		menu.onClick(v);
	}

	/**
	 * Creates the community.
	 */
	private void createCommunity() {
		if (image == -1) {
			Toast.makeText(this, "Select one image", 5).show();
			return;
		}
		if (cc.getCommunityName().equals(new String(""))) {
			Toast.makeText(this, "The field Name is empty", 5).show();
			return;
		}
		if (cc.getCommunityDescription().toString().equals(new String(""))) {
			Toast.makeText(this, "The field community message is empty", 5).show();
			return;
		}
		if (cc.getRadioGroup().getCheckedRadioButtonId() == -1) {
			Toast.makeText(this, "Select visibility", 5).show();
			return;
		}
		String visibility;
		if (cc.getRadioGroup().getCheckedRadioButtonId() == R.id.create_community_radio_public_visibility) {
			visibility = "public";
		} else {
			visibility = "private";
		}

		String recommend;
		if (cc.getRadioGroup2().getCheckedRadioButtonId() == R.id.create_community_radio_automatic) {
			recommend = "automatic";
		} else {
			recommend = "static";
		}

		String dt;
		Date cal = Calendar.getInstance().getTime();
		dt = cal.toLocaleString();

		Community community = new Community(cc.getCommunityName().toString(),
				cc.getCommunityDescription().toString(), Manager
						.getCurrentPhoneUser().get(0), cc.getProfile(),
				visibility, dt, image, recommend);
		community.setId(Manager.getCurrentUserIP()
				+ cc.getCommunityName().toString());
		community.setMyCommunity(true);
		// community.setId(Tools.getLocalIpAddress() + " " +
		// Manager.getCurrentPhoneUser().get(0));
		community.addUser(Manager.getAllUsers().get(0));
		Manager.addCommunity(community);
		Server.sendData(Json.createCommunity());
		Intent i = new Intent(this, HomeMenu.class);
		startActivity(i);
		finish();

	}

	/**
	 * cerrar actividad.
	 */
	private void exitActivity() {
		finish();
	}

	/**
	 * Inicializar Dialog.
	 */
	protected void initDialog() {

		Resources res = getResources();
		iconContextMenu = new IconContextMenu(this, CONTEXT_MENU2_ID);

		for (int i = 0; i < 9; i++)
			iconContextMenu.addItem(res, "Image 0" + (1 + i),
					Tools.userImage(13 + i), 13 + i);

		iconContextMenu
				.setOnClickListener(new IconContextMenu.IconContextMenuOnClickListener() {
					public void onClick(int nbImage) {
						image = nbImage;
						cc.setImage(image);

					}
				});

		showDialog(CONTEXT_MENU2_ID);
	}

	/**
	 * create context menu.
	 *
	 * @param id the id
	 * @return the dialog
	 */
	@Override
	protected Dialog onCreateDialog(int id) {

		switch (id) {
		case CONTEXT_MENU2_ID:
			return iconContextMenu.createMenu("Add an account");
		case CONTEXT_MENU_ID:
			return menu.getIconContextMenu().createMenu("Select status");
		}
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

}
