package com.asocom.activities;

import com.asocom.components.UserProfileComponent;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import com.asocom.components.TopTitre;
import com.asocom.model.Manager;

public class UserProfile extends Activity implements View.OnClickListener {

	private UserProfileComponent userProfileComponent;

	//
	private static final String ACTIVITY_NAME = "UserProfile";

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

	/**
	*
	*/
	public void onClick(View v) {
		userProfileComponent.onClick(v);

		if (userProfileComponent.getProfile().equals("-1")) {
			Intent in = new Intent(this, ProfileConfiguration.class);
			startActivity(in);
			finish();
		}

	}

	public void clearWallpaper() {

	}

	public static String getActivityName() {
		return ACTIVITY_NAME;
	}

}
