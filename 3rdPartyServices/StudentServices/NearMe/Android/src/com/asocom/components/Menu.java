/*
 * 
 */
package com.asocom.components;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.asocom.activities.AllUsers;
import com.asocom.activities.CommunityMembers;
import com.asocom.activities.Friends;
import com.asocom.activities.HomeMenu;
import com.asocom.activities.MyCommunities;
import com.asocom.activities.R;
import com.asocom.model.Manager;
import com.asocom.tools.IconContextMenu;
import com.asocom.tools.Json;
import com.asocom.tools.Server;
import com.asocom.tools.Tools;

// TODO: Auto-generated Javadoc
/**
 * The Class Menu.
 */
public class Menu extends LinearLayout {

	/** The my_communities. */
	private Button my_communities;
	
	/** The menu_friends. */
	private Button menu_friends;
	
	/** The menu_home. */
	private Button menu_home;
	
	/** The status. */
	private Button status;
	
	/** The context. */
	private Context context;
	
	/** The icon context menu. */
	private IconContextMenu iconContextMenu;
	
	/** identificacion del dialogo de imagenes. */
	private final int CONTEXT_MENU_ID = 7;

	/**
	 * Instantiates a new menu.
	 *
	 * @param context the context
	 */
	public Menu(Context context) {
		super(context);
		init(context);
		this.context = context;
	}

	/**
	 * Inits the.
	 *
	 * @param context the context
	 */
	private void init(Context context) {
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li = (LayoutInflater) getContext().getSystemService(
				infService);
		li.inflate(R.layout.main, this, true);
		my_communities = (Button) findViewById(R.id.menu_my_communities);
		menu_friends = (Button) findViewById(R.id.menu_friends);
		menu_home = (Button) findViewById(R.id.menu_home);
		status = (Button) findViewById(R.id.menu_my_status);
		//
		my_communities.setOnClickListener((OnClickListener) context);
		menu_friends.setOnClickListener((OnClickListener) context);
		menu_home.setOnClickListener((OnClickListener) context);
		status.setOnClickListener((OnClickListener) context);
		status.setBackgroundResource(Tools.statusImage(Manager
				.getCurrentStatus()));
		Resources res = getResources();
		iconContextMenu = new IconContextMenu((Activity) context,
				CONTEXT_MENU_ID);
		String[] icon = { "Available", "Not Available", "Busy", "Invisible " };
		for (int i = 0; i < 3; i++)
			iconContextMenu.addItem(res, icon[i], Tools.statusImage(i), i);
		iconContextMenu
				.setOnClickListener(new IconContextMenu.IconContextMenuOnClickListener() {

					public void onClick(int nbImage) {
						status.setBackgroundResource(Tools.statusImage(nbImage));

						if (Manager.getNameCurrentActivity().equals(
								"CommunityMembers")) {
							Activity activity = (Activity) Manager
									.getCurrentActivity();
							Intent communityMembers = new Intent(activity,
									CommunityMembers.class);
							communityMembers
									.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
							activity.startActivity(communityMembers);
							activity.finish();
						}

						if (Manager.getNameCurrentActivity().equals("AllUsers")) {
							Activity activity = (Activity) Manager
									.getCurrentActivity();
							Intent communityMembers = new Intent(activity,
									AllUsers.class);
							communityMembers
									.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
							activity.startActivity(communityMembers);
							activity.finish();
						}

						Manager.getAllUsers().get(0).setStatus(nbImage);
						Manager.setCurrentStatus(nbImage);
						Server.sendData(Json.updateUserInformation());
					}
				});

	}

	/**
	 * On click.
	 *
	 * @param v the v
	 */
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case (R.id.menu_my_communities):
			((Activity) context).finish();
			intent = new Intent(context, MyCommunities.class);
			context.startActivity(intent);
			break;
		case (R.id.menu_friends):
			((Activity) context).finish();
			intent = new Intent(context, Friends.class);
			context.startActivity(intent);
			break;
		case (R.id.menu_home):
			intent = new Intent(context, HomeMenu.class);
			context.startActivity(intent);
			((Activity) context).finish();
			// try {
			// Json.receiver(Server.getData());
			// } catch (Exception e) {
			//
			// }
			break;
		case (R.id.menu_my_status):
			((Activity) context).showDialog(CONTEXT_MENU_ID);
			break;
		}
	}

	/**
	 * Gets the icon context menu.
	 *
	 * @return the icon context menu
	 */
	public IconContextMenu getIconContextMenu() {
		return iconContextMenu;
	}

	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public Button getStatus() {
		return status;
	}

}