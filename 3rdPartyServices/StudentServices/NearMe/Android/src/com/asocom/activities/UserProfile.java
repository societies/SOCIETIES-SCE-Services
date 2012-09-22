/*
 * 
 */
package com.asocom.activities;

import com.asocom.components.UserProfileComponent;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import com.asocom.components.TopTitre;
import com.asocom.model.Manager;

// TODO: Auto-generated Javadoc
/**
 * The Class UserProfile.
 */
public class UserProfile extends Activity implements View.OnClickListener {

	/** The user profile component. */
	private UserProfileComponent userProfileComponent;

	//
	/** The Constant ACTIVITY_NAME. */
	private static final String ACTIVITY_NAME = "UserProfile";

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
		topTitre.setTextTitre("User Profile");

		topLayout.addView(topTitre);

		userProfileComponent = new UserProfileComponent(this);
		layout.addView(userProfileComponent);
		Manager.setNameCurrentActivity(ACTIVITY_NAME);
		Manager.setCurrentActivity(this);

	}

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	public void onClick(View v) {
		userProfileComponent.onClick(v);

		if (userProfileComponent.getProfile().equals("-1")) {
			Intent in = new Intent(this, ProfileConfiguration.class);
			startActivity(in);
			finish();
		}

	}

	/* (non-Javadoc)
	 * @see android.content.ContextWrapper#clearWallpaper()
	 */
	public void clearWallpaper() {

	}

	/**
	 * Gets the activity name.
	 *
	 * @return the activity name
	 */
	public static String getActivityName() {
		return ACTIVITY_NAME;
	}

}
