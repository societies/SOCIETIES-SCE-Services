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
 * The Class CommunityProfile.
 */
public class CommunityProfile extends LinearLayout {

	/** The name. */
	private TextView name;
	
	/** The description. */
	private TextView description;
	
	/** The administrator. */
	private TextView administrator;
	
	/** The category. */
	private TextView category;
	
	/** The visibility. */
	private TextView visibility;
	
	/** The date. */
	private TextView date;
	
	/** The members. */
	private TextView members;

	/**
	 * Instantiates a new community profile.
	 *
	 * @param context the context
	 */
	public CommunityProfile(Context context) {
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
		li.inflate(R.layout.community_profile, this, true);

		name = (TextView) findViewById(R.id.community_profile_name);
		description = (TextView) findViewById(R.id.community_profile_description);
		administrator = (TextView) findViewById(R.id.community_profile_administrator);
		category = (TextView) findViewById(R.id.community_profile_category);
		visibility = (TextView) findViewById(R.id.community_profile_visibility);
		date = (TextView) findViewById(R.id.community_profile_date);
		members = (TextView) findViewById(R.id.community_profile_members);
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
	 * Sets the administrator.
	 *
	 * @param value the new administrator
	 */
	public void setAdministrator(String value) {
		this.administrator.setText(value);

	}

	/**
	 * Sets the category.
	 *
	 * @param value the new category
	 */
	public void setCategory(String value) {
		this.category.setText(value);

	}

	/**
	 * Sets the visibility.
	 *
	 * @param value the new visibility
	 */
	public void setVisibility(String value) {
		this.visibility.setText(value);

	}

	/**
	 * Sets the date.
	 *
	 * @param value the new date
	 */
	public void setDate(String value) {
		this.date.setText(value);

	}

	/**
	 * Sets the members.
	 *
	 * @param value the new members
	 */
	public void setMembers(String value) {
		this.members.setText(value);

	}

}
