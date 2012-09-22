/*
 * 
 */
package com.asocom.components;

import com.asocom.activities.R;
import com.asocom.tools.Tools;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

// TODO: Auto-generated Javadoc
/**
 * The Class PartList01.
 */
public class PartList01 extends LinearLayout {

	/** The status. */
	private ImageView image, status;
	
	/** The new message. */
	private TextView name, newMessage;

	/** Esta variable copia la id del layout clikable util para los listener. */
	private int code;

	/**
	 * Instantiates a new part list01.
	 *
	 * @param context the context
	 */
	public PartList01(Context context) {
		super(context);
		init();
	}

	/**
	 * Instantiates a new part list01.
	 *
	 * @param context the context
	 * @param attrs the attrs
	 */
	public PartList01(Context context, AttributeSet attrs) {
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
		li.inflate(R.layout.part_list01, this, true);
		LinearLayout ll = (LinearLayout) findViewById(R.id.part_list01_layout);
		code = ll.hashCode();
		image = (ImageView) findViewById(R.id.part_list01_image);
		status = (ImageView) findViewById(R.id.part_list_01_status);
		name = (TextView) findViewById(R.id.part_list_01_user_name);
		newMessage = (TextView) findViewById(R.id.part_list_01_new_message);

	}

	/**
	 * Gets the image.
	 *
	 * @return the image
	 */
	public ImageView getImage() {
		return image;
	}

	/**
	 * Sets the image.
	 *
	 * @param image the new image
	 */
	public void setImage(int image) {
		this.image.setImageResource(Tools.userImage(image));
	}

	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public ImageView getStatus() {
		return status;
	}

	/**
	 * Sets the status.
	 *
	 * @param status the new status
	 */
	public void setStatus(int status) {
		this.status.setImageResource(Tools.statusImage(status));
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public TextView getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param namee the new name
	 */
	public void setName(String namee) {
		this.name.setText(namee);
	}

	/**
	 * Gets the new message.
	 *
	 * @return the new message
	 */
	public TextView getNewMessage() {
		return newMessage;
	}

	/**
	 * Sets the new message.
	 *
	 * @param value the new new message
	 */
	public void setNewMessage(int value) {
		this.newMessage.setText(value + "");
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
	 * Delete new message.
	 */
	public void deleteNewMessage() {
		newMessage.setBackgroundResource(R.drawable.white_circle);
		newMessage.setText("");
	}

}
