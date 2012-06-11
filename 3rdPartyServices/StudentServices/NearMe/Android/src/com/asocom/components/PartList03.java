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

public class PartList03 extends LinearLayout {

	private ImageView image;
	private TextView text;
	private CheckBox checkBox;

	public PartList03(Context context) {
		super(context);
		init();
	}

	public PartList03(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li = (LayoutInflater) getContext().getSystemService(
				infService);
		li.inflate(R.layout.part_list03, this, true);
		image = (ImageView) findViewById(R.id.part_list_03_image_01);
		text = (TextView) findViewById(R.id.part_list_03_text_01);
		checkBox = (CheckBox) findViewById(R.id.part_list_03_check_box);

	}

	public void setTextNameUser(String text) {
		this.text.setText(Html.fromHtml(text));
	}

	public void setImageUser(int icon) {
		image.setImageResource(icon);
	}

	public CheckBox getCheckBox() {
		return checkBox;
	}

	public void setCheckBox(CheckBox checkBox) {
		this.checkBox = checkBox;
	}

}
