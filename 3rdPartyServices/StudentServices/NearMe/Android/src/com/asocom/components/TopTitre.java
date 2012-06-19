package com.asocom.components;

import com.asocom.activities.R;
import com.asocom.tools.Tools;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TopTitre extends LinearLayout {

	private TextView text;
	private ImageView image;

	public TopTitre(Context context) {
		super(context);
		init();
	}

	public TopTitre(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li = (LayoutInflater) getContext().getSystemService(
				infService);
		li.inflate(R.layout.top_title, this, true);
		text = (TextView) findViewById(R.id.top_titre_text);
		image = (ImageView) findViewById(R.id.top_titre_image);
	}

	public void setTextTitre(String text) {
		this.text.setText(text);
	}

	/**
	 * valores entre 1 y 4
	 * 
	 * @param icon
	 */
	public void setImageTitre(int icon) {
		image.setImageResource(Tools.statusImage(icon));
	}

}