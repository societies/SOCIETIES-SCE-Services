/*
 * 
 */
package com.asocom.components;

import com.asocom.activities.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

// TODO: Auto-generated Javadoc
/**
 * The Class OptionsAddFriend.
 */
public class OptionsAddFriend extends LinearLayout {

	/** Esta variable copia la id del layout clikable util para los listener. */
	private int code;

	/**
	 * Instantiates a new options add friend.
	 *
	 * @param context the context
	 */
	public OptionsAddFriend(Context context) {
		super(context);
		init();
	}

	/**
	 * Inits the.
	 */
	private void init() {
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li = (LayoutInflater) getContext().getSystemService(
				infService);
		li.inflate(R.layout.options_add_friend, this, true);
		LinearLayout ll = (LinearLayout) findViewById(R.id.options_icon);
		code = ll.hashCode();
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
