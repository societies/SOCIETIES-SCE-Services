/*
 * 
 */
package com.asocom.components;

import com.asocom.activities.R;
import com.asocom.tools.Tools;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

// TODO: Auto-generated Javadoc
/**
 * The Class PartList04.
 */
public class PartList04 extends LinearLayout {

	/** The image. */
	private ImageView image;
	
	/** The number of sms. */
	private TextView date, community_name, numberOfMembers, category,
			numberOfSms;

	/** Esta variable copia la id del layout clikable util para los listener. */
	private int code;

	/**
	 * Instantiates a new part list04.
	 *
	 * @param context the context
	 */
	public PartList04(Context context) {
		super(context);
		init();
	}

	/**
	 * Instantiates a new part list04.
	 *
	 * @param context the context
	 * @param attrs the attrs
	 */
	public PartList04(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	/**
	 * Inits the.
	 */
	private void init() {
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li = (LayoutInflater) getContext().getSystemService(
				infService);
		li.inflate(R.layout.part_list04, this, true);
		LinearLayout ll = (LinearLayout) findViewById(R.id.part_list04_layout);
		code = ll.hashCode();
		date = (TextView) findViewById(R.id.part_list04_date);
		community_name = (TextView) findViewById(R.id.part_list04_community_name);
		numberOfMembers = (TextView) findViewById(R.id.part_list04_number_of_members);
		category = (TextView) findViewById(R.id.part_list04_category);
		numberOfSms = (TextView) findViewById(R.id.part_list04_number_of_new_msm);
		image = (ImageView) findViewById(R.id.part_list04_image);

	}

	/**
	 * Sets the date.
	 *
	 * @param date the new date
	 */
	public void setDate(String date) {
		this.date.setText(Html.fromHtml(date));
	}

	/**
	 * Sets the community name.
	 *
	 * @param text the new community name
	 */
	public void setCommunityName(String text) {
		this.community_name.setText(Html.fromHtml("<b>" + text + "</b>"));
	}

	/**
	 * Sets the number of members.
	 *
	 * @param text the new number of members
	 */
	public void setNumberOfMembers(String text) {
		this.numberOfMembers.setText(Html.fromHtml("Members: " + text));
	}

	/**
	 * Sets the category.
	 *
	 * @param text the new category
	 */
	public void setCategory(String text) {
		this.category.setText(Html.fromHtml("Profile: " + text));
	}

	/**
	 * Sets the number of sms.
	 *
	 * @param text the new number of sms
	 */
	public void setNumberOfSms(String text) {
		this.numberOfSms.setText(Html.fromHtml(text));
	}

	/**
	 * Sets the image user.
	 *
	 * @param icon the new image user
	 */
	public void setImageUser(int icon) {
		image.setImageResource(Tools.userImage(icon));
	}

	/**
	 * Gets the code.
	 *
	 * @return the code
	 */
	public int getCode() {
		return code;
	}

	/**
	 * Sets the code.
	 *
	 * @param code the new code
	 */
	public void setCode(int code) {
		this.code = code;
	}

}
