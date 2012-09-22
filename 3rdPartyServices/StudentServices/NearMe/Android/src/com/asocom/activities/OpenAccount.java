/*
 * 
 */
package com.asocom.activities;

import java.util.ArrayList;
import com.asocom.model.Manager;
import com.asocom.model.User;
import com.asocom.tools.Tools;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

// TODO: Auto-generated Javadoc
/**
 * The Class OpenAccount.
 */
public class OpenAccount extends Activity implements View.OnClickListener {

	/** The list. */
	private Spinner list;

	/** The password. */
	private EditText password;

	/** The ok. */
	private Button ok;

	/** The cancel. */
	private Button cancel;

	/** The delete. */
	private Button delete;

	//
	/** The Constant ACTIVITY_NAME. */
	private static final String ACTIVITY_NAME = "OpenAccount";

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.open_account);
		//
		list = (Spinner) findViewById(R.id.open_account_list);
		password = (EditText) findViewById(R.id.open_account_password);
		ok = (Button) findViewById(R.id.open_account_ok);
		cancel = (Button) findViewById(R.id.open_account_cancel);
		delete = (Button) findViewById(R.id.open_account_delete);

		//
		ok.setOnClickListener(this);
		cancel.setOnClickListener(this);
		delete.setOnClickListener(this);

		//
		refreshList();

		//
		Manager.setNameCurrentActivity(ACTIVITY_NAME);
		Manager.setCurrentActivity(this);

	}

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	public void onClick(View v) {
		switch (v.getId()) {
		case (R.id.open_account_ok):
			openAccount();
			break;
		case (R.id.open_account_cancel):
			exitActivity();
			break;
		case (R.id.open_account_delete):
			deleteAccount();
			break;
		}

	}

	/**
	 * Open account.
	 */
	private void openAccount() {

		if (this.password.getText().toString().equals("")) {
			Tools.alertDialog(this, "Authentication", "Your password is empty");
			return;
		}

		try {
			((String) list.getSelectedItem()).equals(null);
		} catch (Exception e) {
			Tools.alertDialog(this, "Authentication", "Crear una cuenta");
			return;
		}

		ArrayList<String> phoneUser = Manager.getDb().getUserAsArray(
				(String) list.getSelectedItem());
		if (phoneUser.get(5).equals(this.password.getText().toString())) {
			Manager.setUser((String) list.getSelectedItem());

			Intent i = new Intent(OpenAccount.this, ProfileConfiguration.class);
			startActivity(i);
			finish();

			if (Manager.getAllUsers().size() == 0) {
				User user = new User(Manager.getCurrentPhoneUser().get(0),
						Manager.getCurrentPhoneUser().get(1), Manager
								.getCurrentPhoneUser().get(2), Manager
								.getCurrentPhoneUser().get(3), Manager
								.getCurrentPhoneUser().get(4),
						Integer.parseInt(Manager.getCurrentPhoneUser().get(6)),
						0);
				user.setIp(Manager.getCurrentUserIP());
				Manager.addUsers(user);
			}

		} else {
			Tools.alertDialog(this, "Authentication",
					"The password you entered is incorrect");
			this.password.setText("");
		}
	}

	/**
	 * Delete account.
	 */
	private void deleteAccount() {
		new AlertDialog.Builder(this)
				.setTitle("Confirm user deletion")
				.setMessage("Are you sure you want to delete this user?")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Manager.getDb().deleteUser(
								(String) list.getSelectedItem());
						refreshList();
					}
				})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
							}
						}).show();
	}

	/**
	 * Refresh list.
	 */
	private void refreshList() {
		String array_spinner[] = new String[Manager.getPhoneUsers().size()
				/ Manager.getDb().NUMBER_OF_FIELD];
		for (int i = 0; i < Manager.getPhoneUsers().size()
				/ Manager.getDb().NUMBER_OF_FIELD; i++) {
			array_spinner[i] = Manager.getPhoneUsers().get(
					i * Manager.getDb().NUMBER_OF_FIELD + 4);
		}
		ArrayAdapter<Object> adapter = new ArrayAdapter<Object>(this,
				android.R.layout.simple_spinner_item, array_spinner);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		list.setAdapter(adapter);
	}

	/**
	 * cerrar la actividad actual.
	 */
	private void exitActivity() {
		finish();
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
