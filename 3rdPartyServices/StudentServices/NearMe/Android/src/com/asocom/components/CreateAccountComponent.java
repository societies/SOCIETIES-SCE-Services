/*
 * 
 */
package com.asocom.components;

import com.asocom.activities.R;
import com.asocom.tools.Tools;

import android.content.Context;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

// TODO: Auto-generated Javadoc
/**
 * The Class CreateAccountComponent.
 */
public class CreateAccountComponent extends LinearLayout {

	/** The password. */
	private EditText name, description, date, email, password;
	
	/** The gender. */
	private RadioGroup gender;
	
	/** The cancel. */
	private Button save, cancel;
	
	/** The image. */
	private ImageView image;

	/** Numero maximo de caracteres del campo nombre. */
	public static final int NAME_MAX_LENGTH = 16;

	/** Numero maximo de caracteres del campo description. */
	public static final int DESCRIPTION_MAX_LENGTH = 200;

	/** Numero maximo de caracteres del campo date. */
	public static final int DATE_MAX_LENGTH = 10;

	/** Numero maximo de caracteres del campo date. */
	public static final int PASSWORD_MAX_LENGTH = 8;

	/**
	 * Instantiates a new creates the account component.
	 *
	 * @param context the context
	 */
	public CreateAccountComponent(Context context) {
		super(context);
		init(context);
	}

	/**
	 * Instantiates a new creates the account component.
	 *
	 * @param context the context
	 * @param attrs the attrs
	 */
	public CreateAccountComponent(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
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
		li.inflate(R.layout.create_account, this, true);

		image = (ImageView) findViewById(R.id.create_account_image);

		name = (EditText) findViewById(R.id.create_account_name);
		this.name.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
				NAME_MAX_LENGTH) });

		description = (EditText) findViewById(R.id.create_account_description);
		this.description
				.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
						DESCRIPTION_MAX_LENGTH) });

		date = (EditText) findViewById(R.id.create_account_date);
		this.date.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
				DATE_MAX_LENGTH) });

		email = (EditText) findViewById(R.id.create_account_email);

		password = (EditText) findViewById(R.id.create_account_password);
		this.password
				.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
						PASSWORD_MAX_LENGTH) });

		gender = (RadioGroup) findViewById(R.id.create_account_radio_group);
		save = (Button) findViewById(R.id.create_account_save);
		cancel = (Button) findViewById(R.id.create_account_cancel);

	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name.getText().toString();
	}

	/**
	 * Sets the name.
	 *
	 * @param text the new name
	 */
	public void setName(String text) {
		name.setText(text);
	}

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description.getText().toString();
	}

	/**
	 * Sets the description.
	 *
	 * @param name the new description
	 */
	public void setDescription(String name) {
		this.description.setText(name);
	}

	/**
	 * Gets the date.
	 *
	 * @return the date
	 */
	public String getDate() {

		return date.getText().toString();
	}

	/**
	 * Sets the date.
	 *
	 * @param name the new date
	 */
	public void setDate(String name) {
		this.date.setText(name);
	}

	/**
	 * Gets the email.
	 *
	 * @return the email
	 */
	public String getEmail() {

		return email.getText().toString();
	}

	/**
	 * Sets the email.
	 *
	 * @param name the new email
	 */
	public void setEmail(String name) {
		this.email.setText(name);
	}

	/**
	 * Gets the email edit text.
	 *
	 * @return the email edit text
	 */
	public EditText getEmailEditText() {
		return email;
	}

	/**
	 * Gets the password.
	 *
	 * @return the password
	 */
	public String getPassword() {

		return password.getText().toString();
	}

	/**
	 * Sets the password.
	 *
	 * @param name the new password
	 */
	public void setPassword(String name) {
		this.password.setText(name);
	}

	/**
	 * Sets the image.
	 *
	 * @param icon the new image
	 */
	public void setImage(int icon) {
		this.image.setImageResource(Tools.userImage(icon));
	}

	/**
	 * Gets the button save.
	 *
	 * @return the button save
	 */
	public Button getButtonSave() {
		return save;
	}

	/**
	 * Gets the button cancel.
	 *
	 * @return the button cancel
	 */
	public Button getButtonCancel() {
		return cancel;
	}

	/**
	 * Gets the radio group gender.
	 *
	 * @return the radio group gender
	 */
	public RadioGroup getRadioGroupGender() {
		return gender;
	}

}
