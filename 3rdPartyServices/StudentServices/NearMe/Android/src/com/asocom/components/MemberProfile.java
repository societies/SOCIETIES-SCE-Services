package com.asocom.components;

import com.asocom.activities.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MemberProfile extends LinearLayout {

	private TextView name;
	private TextView description;
	private TextView date;
	private TextView gender;
	private TextView email;
	private TextView status;

	public MemberProfile(Context context) {
		super(context);
		init(context);
	}

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

	public void setName(String value) {
		this.name.setText(value);
	}

	public void setDescription(String value) {
		this.description.setText(value);

	}

	public void setGender(String value) {
		this.gender.setText(value);

	}

	public void setEmail(String value) {
		this.email.setText(value);

	}

	public void setStatus(String value) {
		this.status.setText(value);

	}

	public void setDate(String value) {
		this.date.setText(value);
	}

}
