package com.asocom.activities;

import com.asocom.model.Manager;
import com.asocom.tools.Json;
import com.asocom.tools.Server;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

/**
 * 
 */
public class ProfileConfiguration extends Activity implements
		View.OnClickListener {

	/**
	 * 
	 */
	private Spinner list;

	/**
	 * 
	 */
	private Button addProfile;

	/**
	 * 
	 */
	private Button cancel;

	/**
	 * 
	 */
	private Button connect;

	/**
     * 
     */
	private TextView useridentifier;

	/**
     * 
     */
	private static final String ACTIVITY_NAME = "ProfileConfiguration";

	/**
	 * 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile_configuration);
		//
		list = (Spinner) findViewById(R.id.profile_configuration_list);
		addProfile = (Button) findViewById(R.id.profile_configuration_add);
		cancel = (Button) findViewById(R.id.profile_configuration_cancel);
		connect = (Button) findViewById(R.id.profile_configuration_connect);
		useridentifier = (TextView) findViewById(R.id.profile_configuration_user_identifier);

		Manager.setNameCurrentActivity(ACTIVITY_NAME);
		Manager.setCurrentActivity(this);
		addProfile.setOnClickListener(this);
		cancel.setOnClickListener(this);
		connect.setOnClickListener(this);
		Manager.setNameCurrentActivity(ACTIVITY_NAME);
		Manager.setCurrentActivity(this);

		if ((Manager.getDb().getUserAsArray(Manager.getCurrentPhoneUser()
				.get(4))).get(7).equals("")) {
			list.setEnabled(false);
			connect.setEnabled(false);
		} else {
			setList();
		}
		;

		useridentifier.setText(Manager.getCurrentPhoneUser().get(0));

	}

	/**
	*
	*/
	public void onClick(View v) {

		switch (v.getId()) {
		case (R.id.profile_configuration_add):
			Intent i = new Intent(this, UserProfile.class);
			startActivity(i);
			finish();
			break;
		case (R.id.profile_configuration_cancel):
			Intent j = new Intent(this, OpenAccount.class);
			startActivity(j);
			finish();
			break;

		case (R.id.profile_configuration_connect):

			Log.v("select", select);
			Manager.setCurrentUserProfile(select);
			Manager.getAllUsers().get(0).setProfile(select);
			Server.start();
			Server.sendData(Json.connect());
			Intent k = new Intent(this, HomeMenu.class);
			startActivity(k);
			finish();
			break;
		}

	}

	private String[] profilesString;
	private String select;

	private void setList() {

		String profileString = (Manager.getDb().getUserAsArray(Manager
				.getCurrentPhoneUser().get(4))).get(7);
		profilesString = profileString.split("-");
		select = profilesString[1];
		String[] names = new String[profilesString.length - 1];
		for (int i = 1; i < profilesString.length; i++) {
			String[] profile = profilesString[i].split(":");
			names[i - 1] = profile[0];
		}

		ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, names);

		adaptador
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		list.setAdapter(adaptador);

		list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent,
					android.view.View v, int position, long id) {
				select = profilesString[position + 1];
				Log.v("select", select);
			}

			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

	}

	public void clearWallpaper() {

	}

	public static String getActivityName() {
		return ACTIVITY_NAME;
	}

} // end class
