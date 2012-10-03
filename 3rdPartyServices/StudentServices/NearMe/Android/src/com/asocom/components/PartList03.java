/*
 * 
 */
package com.asocom.components;

import com.asocom.activities.R;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

// TODO: Auto-generated Javadoc
/**
 * The Class PartList03.
 */
public class PartList03 extends LinearLayout {

	/** The image. */
	private ImageView image;
	
	/** The text. */
	private TextView text;
	
	/** The check box. */
	private CheckBox checkBox;

	/**
	 * Instantiates a new part list03.
	 *
	 * @param context the context
	 */
	public PartList03(Context context) {
		super(context);
		init();
	}

	/**
	 * Instantiates a new part list03.
	 *
	 * @param context the context
	 * @param attrs the attrs
	 */
	public PartList03(Context context, AttributeSet attrs) {
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
		li.inflate(R.layout.part_list03, this, true);
		image = (ImageView) findViewById(R.id.part_list_03_image_01);
		text = (TextView) findViewById(R.id.part_list_03_text_01);
		checkBox = (CheckBox) findViewById(R.id.part_list_03_check_box);

	}

	/**
	 * Sets the text name user.
	 *
	 * @param text the new text name user
	 */
	public void setTextNameUser(String text) {
		this.text.setText(Html.fromHtml(text));
	}

	/**
	 * Sets the image user.
	 *
	 * @param icon the new image user
	 */
	public void setImageUser(int icon) {
		image.setImageResource(icon);
	}

	/**
	 * Gets the check box.
	 *
	 * @return the check box
	 */
	public CheckBox getCheckBox() {
		return checkBox;
	}

	/**
	 * Sets the check box.
	 *
	 * @param checkBox the new check box
	 */
	public void setCheckBox(CheckBox checkBox) {
		this.checkBox = checkBox;
	}

}
