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

public class PartList02 extends LinearLayout {

	private ImageView image;
	private TextView text;
	private Button button01;
	private Button button02;

	public PartList02(Context context) {
		super(context);
		init();
	}

	public PartList02(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

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

	public void setTextNameUser(String text) {
		this.text.setText(Html.fromHtml(text));
	}

	public void setImageUser(int icon) {
		image.setImageResource(icon);
	}

	public Button getButton01() {
		return button01;
	}

	public void setButton01(Button button01) {
		this.button01 = button01;
	}

	public Button getButton02() {
		return button02;
	}

	public void setButton02(Button button02) {
		this.button02 = button02;
	}

}
