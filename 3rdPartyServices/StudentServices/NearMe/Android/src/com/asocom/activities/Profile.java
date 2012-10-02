/*
 * 
 */
package com.asocom.activities;

import java.util.Calendar;
import com.asocom.components.TopTitre;
import com.asocom.components.CreateAccountComponent;
import com.asocom.model.Manager;
import com.asocom.tools.IconContextMenu;
import com.asocom.tools.Tools;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Toast;

// TODO: Auto-generated Javadoc
/**
 * The Class Profile.
 */
public class Profile extends Activity implements View.OnClickListener {

	/** dialog de imagenes. */
	private IconContextMenu iconContextMenu = null;

	/** identificacion del dialogo de imagenes. */
	private final int CONTEXT_MENU_ID = 1;

	/** dialogo de fecha. */
	private final int DATE_DIALOG_ID = 2;

	/** numero de imagen del usuario. */
	private int image;

	/** contenedor de objeto user profile. */
	CreateAccountComponent createAccount;
	
	/** The m year. */
	private int mYear;
	
	/** The m month. */
	private int mMonth;
	
	/** The m day. */
	private int mDay;

	//
	/** The Constant ACTIVITY_NAME. */
	private static final String ACTIVITY_NAME = "Profile";

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_void);
		//
		LinearLayout topLayout = (LinearLayout) findViewById(R.id.main_void_topLayout);
		LinearLayout layout = (LinearLayout) findViewById(R.id.main_void_layout);
		TopTitre topTitre = new TopTitre(this);
		topLayout.addView(topTitre);
		createAccount = new CreateAccountComponent(this);
		layout.addView(createAccount);
		//
		topTitre.setTextTitre("Create Account");
		createAccount.getButtonSave().setOnClickListener(this);
		createAccount.getButtonCancel().setOnClickListener(this);
		//
		Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
		image = 0;

		//
		Manager.setNameCurrentActivity(ACTIVITY_NAME);
		Manager.setCurrentActivity(this);

	}

	/**
	 * Sets the profile.
	 */
	public void SetProfile() {
		createAccount.setImage(Integer.parseInt(Manager.getCurrentPhoneUser()
				.get(6)));
		createAccount.setName(Manager.getCurrentPhoneUser().get(0));
		createAccount.setDescription(Manager.getCurrentPhoneUser().get(1));
		createAccount.setDate(Manager.getCurrentPhoneUser().get(2));
		// userProfile.getRadioGroupGender().set rg.check(R.id.radio1);
		if (Manager.getCurrentPhoneUser().get(3).equals("male")) {
			createAccount.getRadioGroupGender().check(
					R.id.create_account_radio_group_01);
		} else {
			createAccount.getRadioGroupGender().check(
					R.id.create_account_radio_group_02);
		}
		createAccount.setEmail("prueba email");
		createAccount.getEmailEditText().setEnabled(false);
		createAccount.setPassword(Manager.getCurrentPhoneUser().get(5));
	}

	/**
	 * se llama cuando hace click la imagen para modificarla.
	 *
	 * @param view the view
	 */
	public void userProfileImage(View view) {
		initDialog();
	}

	/**
	 * se llama cuando hace click el cuadro de dialogo date.
	 *
	 * @param view the view
	 */
	public void userProfileDate(View view) {
		showDialog(DATE_DIALOG_ID);
	}

	/**
	 * eventos de la actividad.
	 *
	 * @param v the v
	 */
	public void onClick(View v) {
		switch (v.getId()) {
		case (R.id.create_account_save):
			createAccount();
			break;

		case (R.id.create_account_cancel):
			exitActivity();
			break;

		}
	}

	/**
	 * Creates the account.
	 */
	private void createAccount() {
		if (image == 0) {
			Toast.makeText(this, "Select one image", 5).show();
			return;
		}
		if (createAccount.getName().equals(new String(""))) {
			Toast.makeText(this, "The field Name is empty", 5).show();
			return;
		}
		if (createAccount.getDate().equals("")) {
			Toast.makeText(this, "the file Date of birth is empty", 5).show();
			return;
		}
		if (createAccount.getRadioGroupGender().getCheckedRadioButtonId() == -1) {
			Toast.makeText(this, "Select gender", 5).show();
			return;
		}
		if (createAccount.getEmail().equals("")) {
			Toast.makeText(this, "The fild email is empty", 5).show();
			return;
		}
		if (createAccount.getPassword().equals("")) {
			Toast.makeText(this, "the fild Password is empty", 5).show();
			return;
		}

		for (int i = 0; i < Manager.getPhoneUsers().size()
				/ Manager.getDb().NUMBER_OF_FIELD; i++) {
			if (createAccount.getEmail().equals(
					Manager.getPhoneUsers().get(
							i * Manager.getDb().NUMBER_OF_FIELD + 4))) {
				Toast.makeText(this, "This address email already exist", 5)
						.show();
				return;
			}
		}
		Manager.getDb().addUser(
				createAccount.getName(),
				createAccount.getDescription(),
				createAccount.getDate(),
				""
						+ createAccount.getRadioGroupGender()
								.getCheckedRadioButtonId(),
				createAccount.getEmail(), createAccount.getPassword(),
				"" + image);
		Intent i = new Intent(Profile.this, OpenAccount.class);
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
		iconContextMenu = new IconContextMenu(this, CONTEXT_MENU_ID);

		for (int i = 0; i < 12; i++)
			iconContextMenu.addItem(res, "Image 0" + (1 + i),
					Tools.userImage(i), i);

		iconContextMenu
				.setOnClickListener(new IconContextMenu.IconContextMenuOnClickListener() {

					public void onClick(int nbImage) {
						image = Tools.userImage(nbImage);
						createAccount.setImage(image);
					}
				});

		showDialog(CONTEXT_MENU_ID);
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
		case CONTEXT_MENU_ID:
			return iconContextMenu.createMenu("Add an account");

		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,
					mDay);
		}
		return super.onCreateDialog(id);
	}

	/** The m date set listener. */
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear + 1;
			mDay = dayOfMonth;
			createAccount.setDate(mDay + "/" + mMonth + "/" + mYear);
		}
	};

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
