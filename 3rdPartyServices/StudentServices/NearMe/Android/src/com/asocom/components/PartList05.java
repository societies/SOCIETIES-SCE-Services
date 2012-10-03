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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

// TODO: Auto-generated Javadoc
/**
 * The Class PartList05.
 */
public class PartList05 extends LinearLayout {

	/** The image. */
	private ImageView image;
	
	/** The category. */
	private TextView date, communityName, numberOfMembers, category;
	
	/** The add community button. */
	private Button addCommunityButton;
	
	/** Esta variable copia la id del layout clikable util para los listener. */
	private int code;

	/**
	 * Instantiates a new part list05.
	 *
	 * @param context the context
	 */
	public PartList05(Context context) {
		super(context);
		init();
	}

	/**
	 * Instantiates a new part list05.
	 *
	 * @param context the context
	 * @param attrs the attrs
	 */
	public PartList05(Context context, AttributeSet attrs) {
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

	/**
	 * Sets the date.
	 *
	 * @param text the new date
	 */
	public void setDate(String text) {
		this.date.setText(Html.fromHtml(text));
	}

	/**
	 * Sets the community name.
	 *
	 * @param text the new community name
	 */
	public void setCommunityName(String text) {
		this.communityName.setText(Html.fromHtml("<b>" + text + "</b>"));
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
