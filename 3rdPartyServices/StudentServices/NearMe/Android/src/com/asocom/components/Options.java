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

public class Options extends LinearLayout {

	private TextView textTop, textDescription;
	private ImageView imageStatus, ImageDescription;
	private Button button01, button02, button03;

	public Options(Context context) {
		super(context);
		init(context);
	}

	public Options(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

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

	public TextView getTextTop() {
		return textTop;
	}

	public void setTextTop(String text) {
		this.textTop.setText(text);
	}

	public TextView getTextDescription() {
		return textDescription;
	}

	public void setTextDescription(String textDescription) {
		this.textDescription.setText(textDescription);
	}

	public ImageView getImageStatus() {
		return imageStatus;
	}

	public void setImageStatus(int icon) {
		imageStatus.setImageResource(Tools.statusImage(icon));
	}

	public ImageView getImageDescription() {
		return ImageDescription;
	}

	public void setImageDescription(int icon) {
		ImageDescription.setImageResource(Tools.userImage(icon));
	}

	public Button getButton01() {
		return button01;
	}

	public Button getButton02() {
		return button02;
	}

	public Button getButton03() {
		return button03;
	}

}
