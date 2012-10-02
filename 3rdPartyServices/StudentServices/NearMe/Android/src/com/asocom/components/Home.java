/*
 * 
 */
package com.asocom.components;

import com.asocom.activities.AllUsers;
import com.asocom.activities.Communities;
import com.asocom.activities.CreateCommunity;
import com.asocom.activities.CreateAccount;
import com.asocom.activities.Friends;
import com.asocom.activities.MyCommunities;
import com.asocom.activities.R;
import com.asocom.model.Manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

// TODO: Auto-generated Javadoc
/**
 * Displays a set of six icons which allows users to explore and use all PVC
 * application functions.
 * 
 * @author Marisnel OLIVARES
 * 
 */
public class Home extends LinearLayout {
	
	/** Allows to accede to the friends activity {@link}. */
	private Button friends_btn;
	
	/** Allows to accede to the allUsers activity {@link}. */
	private Button allUsers_btn;
	
	/** Allows to accede to the communities activity {@link}. */
	private Button communities_btn;
	
	/** Allows to accede to the profile activity {@link}. */
	private Button profile_btn;
	
	/** Allows to accede to the myCommunities activity {@link}. */
	private Button my_communities_btn;
	
	/** Allows to accede to the createCommunity activity {@link}. */
	private Button createCommunity_btn;

	/** The context. */
	private Context context;

	/**
	 * Construct the home component, initializes all layout components.
	 * 
	 * @param context
	 *            The <code>Context</code> identifies the activity who is
	 *            calling this component.
	 */
	public Home(Context context) {
		super(context);
		init(context);
		this.context = context;
	}

	/**
	 * Creates and sets view components in screen home.
	 * 
	 * @param context
	 *            The <code>Context</code> identifies the activity who is
	 *            calling this component.
	 */
	private void init(Context context) {
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li = (LayoutInflater) getContext().getSystemService(
				infService);
		li.inflate(R.layout.home, this, true);
		friends_btn = (Button) findViewById(R.id.home_friends);
		allUsers_btn = (Button) findViewById(R.id.home_all_users);
		communities_btn = (Button) findViewById(R.id.home_communities);
		profile_btn = (Button) findViewById(R.id.home_profile);
		my_communities_btn = (Button) findViewById(R.id.home_my_communities);
		createCommunity_btn = (Button) findViewById(R.id.home_create_community);

		friends_btn.setOnClickListener((OnClickListener) context);
		allUsers_btn.setOnClickListener((OnClickListener) context);
		communities_btn.setOnClickListener((OnClickListener) context);
		profile_btn.setOnClickListener((OnClickListener) context);
		my_communities_btn.setOnClickListener((OnClickListener) context);
		createCommunity_btn.setOnClickListener((OnClickListener) context);
	}

	/**
	 * On click.
	 *
	 * @param v the v
	 */
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case (R.id.home_profile):
			Manager.setIntValue(1);
			intent = new Intent(context, CreateAccount.class);
			context.startActivity(intent);
			break;
		case (R.id.home_communities):
			((Activity) context).finish();
			intent = new Intent(context, Communities.class);
			context.startActivity(intent);
			break;
		case (R.id.home_my_communities):
			((Activity) context).finish();
			intent = new Intent(context, MyCommunities.class);
			context.startActivity(intent);
			break;
		case (R.id.home_create_community):
			((Activity) context).finish();
			intent = new Intent(context, CreateCommunity.class);
			context.startActivity(intent);
			break;
		case (R.id.home_friends):
			((Activity) context).finish();
			intent = new Intent(context, Friends.class);
			context.startActivity(intent);
			break;
		case (R.id.home_all_users):
			((Activity) context).finish();
			intent = new Intent(context, AllUsers.class);
			context.startActivity(intent);
			break;
		}
	}

}
