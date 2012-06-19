package com.asocom.components;

import com.asocom.activities.R;
import com.asocom.tools.Tools;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PartList01 extends LinearLayout {

	private ImageView image, status;
	private TextView name, newMessage;

	/**
	 * Esta variable copia la id del layout clikable util para los listener
	 */
	private int code;

	public PartList01(Context context) {
		super(context);
		init();
	}

	public PartList01(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

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

	public ImageView getImage() {
		return image;
	}

	public void setImage(int image) {
		this.image.setImageResource(Tools.userImage(image));
	}

	public ImageView getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status.setImageResource(Tools.statusImage(status));
	}

	public TextView getName() {
		return name;
	}

	public void setName(String namee) {
		this.name.setText(namee);
	}

	public TextView getNewMessage() {
		return newMessage;
	}

	public void setNewMessage(int value) {
		this.newMessage.setText(value + "");
	}

	public int getCode() {
		return code;
	}

	public void deleteNewMessage() {
		newMessage.setBackgroundResource(R.drawable.white_circle);
		newMessage.setText("");
	}

}
