/*
 * 
 */
package com.asocom.components;

import com.asocom.activities.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

// TODO: Auto-generated Javadoc
/**
 * The Class MemberProfile.
 */
public class MemberProfile extends LinearLayout {

	/** The name. */
	private TextView name;
	
	/** The description. */
	private TextView description;
	
	/** The date. */
	private TextView date;
	
	/** The gender. */
	private TextView gender;
	
	/** The email. */
	private TextView email;
	
	/** The status. */
	private TextView status;

	/**
	 * Instantiates a new member profile.
	 *
	 * @param context the context
	 */
	public MemberProfile(Context context) {
		super(context);
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
		li.inflate(R.layout.member_profile, this, true);

		name = (TextView) findViewById(R.id.member_profile_name);
		description = (TextView) findViewById(R.id.member_profile_description);
		date = (TextView) findViewById(R.id.member_profile_dateofbirth);
		gender = (TextView) findViewById(R.id.member_profile_gender);
		email = (TextView) findViewById(R.id.member_profile_email);
		status = (TextView) findViewById(R.id.member_profile_status);

	}

	/**
	 * Sets the name.
	 *
	 * @param value the new name
	 */
	public void setName(String value) {
		this.name.setText(value);
	}

	/**
	 * Sets the description.
	 *
	 * @param value the new description
	 */
	public void setDescription(String value) {
		this.description.setText(value);

	}

	/**
	 * Sets the gender.
	 *
	 * @param value the new gender
	 */
	public void setGender(String value) {
		this.gender.setText(value);

	}

	/**
	 * Sets the email.
	 *
	 * @param value the new email
	 */
	public void setEmail(String value) {
		this.email.setText(value);

	}

	/**
	 * Sets the status.
	 *
	 * @param value the new status
	 */
	public void setStatus(String value) {
		this.status.setText(value);

	}

	/**
	 * Sets the date.
	 *
	 * @param value the new date
	 */
	public void setDate(String value) {
		this.date.setText(value);
	}

}
