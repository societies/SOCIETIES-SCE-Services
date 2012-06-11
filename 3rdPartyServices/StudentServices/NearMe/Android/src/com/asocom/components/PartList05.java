package com.asocom.components;

import com.asocom.activities.R;
import com.asocom.tools.Tools;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PartList05 extends LinearLayout {

	private ImageView image;
	private TextView date, communityName, numberOfMembers, category;
	private Button addCommunityButton;
	/**
	 * Esta variable copia la id del layout clikable util para los listener
	 */
	private int code;

	public PartList05(Context context) {
		super(context);
		init();
	}

	public PartList05(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li = (LayoutInflater) getContext().getSystemService(
				infService);
		li.inflate(R.layout.part_list05, this, true);
		LinearLayout ll = (LinearLayout) findViewById(R.id.part_list05_layout);
		code = ll.hashCode();
		date = (TextView) findViewById(R.id.part_list05_date);
		communityName = (TextView) findViewById(R.id.part_list05_community_name);
		numberOfMembers = (TextView) findViewById(R.id.part_list05_number_of_members);
		category = (TextView) findViewById(R.id.part_list05_category);
		addCommunityButton = (Button) findViewById(R.id.part_list05_add_button);
		image = (ImageView) findViewById(R.id.part_list05_image);
		addCommunityButton.setEnabled(false);
	}

	public void setDate(String text) {
		this.date.setText(Html.fromHtml(text));
	}

	public void setCommunityName(String text) {
		this.communityName.setText(Html.fromHtml("<b>" + text + "</b>"));
	}

	public void setNumberOfMembers(String text) {
		this.numberOfMembers.setText(Html.fromHtml("Members: " + text));
	}

	public void setCategory(String text) {
		this.category.setText(Html.fromHtml("Profile: " + text));
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
