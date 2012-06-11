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

public class CreateAccountComponent extends LinearLayout {

	private EditText name, description, date, email, password;
	private RadioGroup gender;
	private Button save, cancel;
	private ImageView image;

	/**
	 * Numero maximo de caracteres del campo nombre
	 */
	public static final int NAME_MAX_LENGTH = 8;

	/**
	 * Numero maximo de caracteres del campo description
	 */
	public static final int DESCRIPTION_MAX_LENGTH = 200;

	/**
	 * Numero maximo de caracteres del campo date
	 */
	public static final int DATE_MAX_LENGTH = 10;

	/**
	 * Numero maximo de caracteres del campo date
	 */
	public static final int PASSWORD_MAX_LENGTH = 8;

	public CreateAccountComponent(Context context) {
		super(context);
		init(context);
	}

	public CreateAccountComponent(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

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
	 * 
	 * @return
	 */
	public String getName() {
		return name.getText().toString();
	}

	/**
	 * 
	 * @return
	 */
	public void setName(String text) {
		name.setText(text);
	}

	/**
	 * 
	 * @return
	 */
	public String getDescription() {
		return description.getText().toString();
	}

	/**
	 * 
	 * @return
	 */
	public void setDescription(String name) {
		this.description.setText(name);
	}

	/**
	 * 
	 * @return
	 */
	public String getDate() {

		return date.getText().toString();
	}

	/**
	 * 
	 * @return
	 */
	public void setDate(String name) {
		this.date.setText(name);
	}

	/**
	 * 
	 * @return
	 */
	public String getEmail() {

		return email.getText().toString();
	}

	/**
	 * 
	 * @return
	 */
	public void setEmail(String name) {
		this.email.setText(name);
	}

	public EditText getEmailEditText() {
		return email;
	}

	/**
	 * 
	 * @return
	 */
	public String getPassword() {

		return password.getText().toString();
	}

	/**
	 * 
	 * @return
	 */
	public void setPassword(String name) {
		this.password.setText(name);
	}

	/**
	 * 
	 * @return
	 */
	public void setImage(int icon) {
		this.image.setImageResource(Tools.userImage(icon));
	}

	/**
	 * 
	 * @return
	 */
	public Button getButtonSave() {
		return save;
	}

	/**
	 * 
	 * @return
	 */
	public Button getButtonCancel() {
		return cancel;
	}

	/**
	 * 
	 * @return
	 */
	public RadioGroup getRadioGroupGender() {
		return gender;
	}

}
