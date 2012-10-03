/*
 * 
 */
package com.asocom.components;

import com.asocom.activities.R;

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
 * The Class PartList02.
 */
public class PartList02 extends LinearLayout {

	/** The image. */
	private ImageView image;
	
	/** The text. */
	private TextView text;
	
	/** The button01. */
	private Button button01;
	
	/** The button02. */
	private Button button02;

	/**
	 * Instantiates a new part list02.
	 *
	 * @param context the context
	 */
	public PartList02(Context context) {
		super(context);
		init();
	}

	/**
	 * Instantiates a new part list02.
	 *
	 * @param context the context
	 * @param attrs the attrs
	 */
	public PartList02(Context context, AttributeSet attrs) {
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
		li.inflate(R.layout.part_list02, this, true);
		image = (ImageView) findViewById(R.id.part_list_02_image_01);
		text = (TextView) findViewById(R.id.part_list_02_text_01);
		button01 = (Button) findViewById(R.id.part_list_02_Button_01);
		button02 = (Button) findViewById(R.id.part_list_02_Button_01);
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
	 * Gets the button01.
	 *
	 * @return the button01
	 */
	public Button getButton01() {
		return button01;
	}

	/**
	 * Sets the button01.
	 *
	 * @param button01 the new button01
	 */
	public void setButton01(Button button01) {
		this.button01 = button01;
	}

	/**
	 * Gets the button02.
	 *
	 * @return the button02
	 */
	public Button getButton02() {
		return button02;
	}

	/**
	 * Sets the button02.
	 *
	 * @param button02 the new button02
	 */
	public void setButton02(Button button02) {
		this.button02 = button02;
	}

}
