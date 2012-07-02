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

public class PartList04 extends LinearLayout {

	private ImageView image;
	private TextView date, community_name, numberOfMembers, category,
			numberOfSms;

	/**
	 * Esta variable copia la id del layout clikable util para los listener
	 */
	private int code;

	public PartList04(Context context) {
		super(context);
		init();
	}

	public PartList04(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

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

	public void setDate(String date) {
		this.date.setText(Html.fromHtml(date));
	}

	public void setCommunityName(String text) {
		this.community_name.setText(Html.fromHtml("<b>" + text + "</b>"));
	}

	public void setNumberOfMembers(String text) {
		this.numberOfMembers.setText(Html.fromHtml("Members: " + text));
	}

	public void setCategory(String text) {
		this.category.setText(Html.fromHtml("Profile: " + text));
	}

	public void setNumberOfSms(String text) {
		this.numberOfSms.setText(Html.fromHtml(text));
	}

	public void setImageUser(int icon) {
		image.setImageResource(Tools.userImage(icon));
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

}
