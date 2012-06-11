package com.asocom.components;

import com.asocom.activities.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CommunityProfile extends LinearLayout {

	private TextView name;
	private TextView description;
	private TextView administrator;
	private TextView category;
	private TextView visibility;
	private TextView date;
	private TextView members;

	public CommunityProfile(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li = (LayoutInflater) getContext().getSystemService(
				infService);
		li.inflate(R.layout.community_profile, this, true);

		name = (TextView) findViewById(R.id.community_profile_name);
		description = (TextView) findViewById(R.id.community_profile_description);
		administrator = (TextView) findViewById(R.id.community_profile_administrator);
		category = (TextView) findViewById(R.id.community_profile_category);
		visibility = (TextView) findViewById(R.id.community_profile_visibility);
		date = (TextView) findViewById(R.id.community_profile_date);
		members = (TextView) findViewById(R.id.community_profile_members);
	}

	public void setName(String value) {
		this.name.setText(value);
	}

	public void setDescription(String value) {
		this.description.setText(value);

	}

	public void setAdministrator(String value) {
		this.administrator.setText(value);

	}

	public void setCategory(String value) {
		this.category.setText(value);

	}

	public void setVisibility(String value) {
		this.visibility.setText(value);

	}

	public void setDate(String value) {
		this.date.setText(value);

	}

	public void setMembers(String value) {
		this.members.setText(value);

	}

}
