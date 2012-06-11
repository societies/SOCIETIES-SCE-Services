package com.asocom.activities;

import com.asocom.model.Manager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * 
 */
public class UserIdentification extends Activity implements
		View.OnClickListener {

	/**
	 * 
	 */
	private Button createAccount;

	/**
	 * 
	 */
	private Button openAccount;

	/**
	 * 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.init);
		//
		createAccount = (Button) findViewById(R.id.init_create_account);
		openAccount = (Button) findViewById(R.id.init_open_account);
		//
		createAccount.setOnClickListener(this);
		openAccount.setOnClickListener(this);

		Manager.init(this);
	


	}
	
	

	/**
	*
	*/
	public void onClick(View v) {
		switch (v.getId()) {
		case (R.id.init_create_account):
			createAccount();
			break;
		case (R.id.init_open_account):
			openAccount();
			break;
		}
	}

	/**
	 * ir a la actividad crear una cuenta
	 */
	private void createAccount() {
		Manager.setIntValue(0);
		Intent intent = new Intent(UserIdentification.this, CreateAccount.class);
		startActivity(intent);

	}

	public void clearWallpaper() {

	}

	/**
	 * ir a la actividad abrir cuenta
	 */
	private void openAccount() {
		Intent intent = new Intent(UserIdentification.this, OpenAccount.class);
		startActivity(intent);
	}

} // end class