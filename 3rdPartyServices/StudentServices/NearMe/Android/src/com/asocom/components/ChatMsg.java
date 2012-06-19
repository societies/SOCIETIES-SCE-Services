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

public class ChatMsg extends LinearLayout {

	private TextView date, text;
	private ImageView image;

	public ChatMsg(Context context) {
		super(context);
		init();
	}

	public ChatMsg(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li = (LayoutInflater) getContext().getSystemService(
				infService);
		li.inflate(R.layout.chat_msg, this, true);
		date = (TextView) findViewById(R.id.chat_msg_date);
		text = (TextView) findViewById(R.id.chat_msg_text);
		image = (ImageView) findViewById(R.id.chat_msg_image);
	}

	public void setDate(String text) {
		this.date.setText(Html.fromHtml(text));
	}

	public void setTextChat(String user, String text) {
		this.text.setText(Html.fromHtml("<b>" + user + ": </b>" + text));
	}

	public void setImageUser(int icon) {
		image.setImageResource(Tools.userImage(icon));
	}
}
