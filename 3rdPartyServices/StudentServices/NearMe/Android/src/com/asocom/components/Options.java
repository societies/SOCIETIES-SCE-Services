/*
 * 
 */
package com.asocom.components;

import com.asocom.activities.R;
import com.asocom.tools.Tools;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

// TODO: Auto-generated Javadoc
/**
 * The Class Options.
 */
public class Options extends LinearLayout {

	/** The text description. */
	private TextView textTop, textDescription;
	
	/** The Image description. */
	private ImageView imageStatus, ImageDescription;
	
	/** The button03. */
	private Button button01, button02, button03;

	/**
	 * Instantiates a new options.
	 *
	 * @param context the context
	 */
	public Options(Context context) {
		super(context);
		init(context);
	}

	/**
	 * Instantiates a new options.
	 *
	 * @param context the context
	 * @param attrs the attrs
	 */
	public Options(Context context, AttributeSet attrs) {
		super(context, attrs);
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
		li.inflate(R.layout.options, this, true);
		textTop = (TextView) findViewById(R.id.options_text_top);
		textDescription = (TextView) findViewById(R.id.options_text_description);
		imageStatus = (ImageView) findViewById(R.id.options_image_status);
		ImageDescription = (ImageView) findViewById(R.id.options_image_description);
		button01 = (Button) findViewById(R.id.options_button_01);
		button02 = (Button) findViewById(R.id.options_button_02);
		button03 = (Button) findViewById(R.id.options_button_03);

	}

	/**
	 * Gets the text top.
	 *
	 * @return the text top
	 */
	public TextView getTextTop() {
		return textTop;
	}

	/**
	 * Sets the text top.
	 *
	 * @param text the new text top
	 */
	public void setTextTop(String text) {
		this.textTop.setText(text);
	}

	/**
	 * Gets the text description.
	 *
	 * @return the text description
	 */
	public TextView getTextDescription() {
		return textDescription;
	}

	/**
	 * Sets the text description.
	 *
	 * @param textDescription the new text description
	 */
	public void setTextDescription(String textDescription) {
		this.textDescription.setText(textDescription);
	}

	/**
	 * Gets the image status.
	 *
	 * @return the image status
	 */
	public ImageView getImageStatus() {
		return imageStatus;
	}

	/**
	 * Sets the image status.
	 *
	 * @param icon the new image status
	 */
	public void setImageStatus(int icon) {
		imageStatus.setImageResource(Tools.statusImage(icon));
	}

	/**
	 * Gets the image description.
	 *
	 * @return the image description
	 */
	public ImageView getImageDescription() {
		return ImageDescription;
	}

	/**
	 * Sets the image description.
	 *
	 * @param icon the new image description
	 */
	public void setImageDescription(int icon) {
		ImageDescription.setImageResource(Tools.userImage(icon));
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
	 * Gets the button02.
	 *
	 * @return the button02
	 */
	public Button getButton02() {
		return button02;
	}

	/**
	 * Gets the button03.
	 *
	 * @return the button03
	 */
	public Button getButton03() {
		return button03;
	}

}
