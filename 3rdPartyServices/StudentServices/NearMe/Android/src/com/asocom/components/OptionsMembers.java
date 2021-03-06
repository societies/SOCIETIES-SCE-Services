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
 * The Class OptionsMembers.
 */
public class OptionsMembers extends LinearLayout {

	/** Esta variable copia la id del layout clikable util para los listener. */
	private int code;

	/**
	 * Instantiates a new options members.
	 *
	 * @param context the context
	 */
	public OptionsMembers(Context context) {
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
		li.inflate(R.layout.options_members, this, true);
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
