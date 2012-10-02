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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

// TODO: Auto-generated Javadoc
/**
 * The Class ChatMsg.
 */
public class ChatMsgComponent extends LinearLayout {

	/** The text. */
	private TextView date, text;
	
	/** The image. */
	private ImageView image;

	/**
	 * Instantiates a new chat msg.
	 *
	 * @param context the context
	 */
	public ChatMsgComponent(Context context) {
		super(context);
		init();
	}

	/**
	 * Instantiates a new chat msg.
	 *
	 * @param context the context
	 * @param attrs the attrs
	 */
	public ChatMsgComponent(Context context, AttributeSet attrs) {
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
		li.inflate(R.layout.chat_msg, this, true);
		date = (TextView) findViewById(R.id.chat_msg_date);
		text = (TextView) findViewById(R.id.chat_msg_text);
		image = (ImageView) findViewById(R.id.chat_msg_image);
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
	 * Sets the text chat.
	 *
	 * @param user the user
	 * @param text the text
	 */
	public void setTextChat(String user, String text) {
		this.text.setText(Html.fromHtml("<b>" + user + ": </b>" + text));
	}

	/**
	 * Sets the image user.
	 *
	 * @param icon the new image user
	 */
	public void setImageUser(int icon) {
		image.setImageResource(Tools.userImage(icon));
	}
}
