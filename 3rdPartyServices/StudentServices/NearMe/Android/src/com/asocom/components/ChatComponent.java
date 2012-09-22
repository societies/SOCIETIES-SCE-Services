/*
 * 
 */
package com.asocom.components;

import com.asocom.activities.R;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

// TODO: Auto-generated Javadoc
/**
 * The Class ChatComponent.
 */
public class ChatComponent extends LinearLayout {

	/** The scroll view. */
	protected ScrollView scrollView;
	
	/** The button. */
	private Button button;
	
	/** The edit text. */
	private EditText editText;
	
	/** The layout. */
	protected LinearLayout layout;

	/**
	 * Instantiates a new chat component.
	 *
	 * @param context the context
	 */
	public ChatComponent(Context context) {
		super(context);
		init(context);
	}

	/**
	 * Inits the.
	 *
	 * @param context the context
	 */
	private void init(Context context) {
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li = (LayoutInflater) getContext().getSystemService(
				infService);
		li.inflate(R.layout.chat, this, true);
		scrollView = ((ScrollView) findViewById(R.id.chat_scroll_view));
		button = (Button) findViewById(R.id.chat_button);
		editText = (EditText) findViewById(R.id.chat_edit_text);
		layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);

	}

	/**
	 * Sets the edits the text.
	 *
	 * @param text the new edits the text
	 */
	public void setEditText(String text) {
		this.editText.setText(Html.fromHtml(text));
	}

	/**
	 * Adds the chat msg.
	 *
	 * @param cm the cm
	 */
	public void addChatMsg(ChatMsgComponent cm) {
		layout.addView(cm);
		scrollView.removeAllViews();
		scrollView.addView(layout);
		scrollView.fullScroll(ScrollView.FOCUS_UP);
	}

	/**
	 * Reset chat.
	 */
	public void resetChat() {
		layout.removeAllViews();
	}

	/**
	 * Sets the chat msg.
	 *
	 * @param ll the new chat msg
	 */
	public void setChatMsg(LinearLayout ll) {
		layout = ll;
	}

	/* (non-Javadoc)
	 * @see android.view.ViewGroup#removeAllViews()
	 */
	public void removeAllViews() {
		scrollView.removeAllViews();
	}

	/**
	 * Gets the button send.
	 *
	 * @return the button send
	 */
	public Button getButtonSend() {
		return button;
	}

	/**
	 * Gets the text.
	 *
	 * @return the text
	 */
	public String getText() {
		return editText.getText().toString();
	}

	/**
	 * Gets the edits the text.
	 *
	 * @return the edits the text
	 */
	public EditText getEditText() {
		return editText;
	}

}
