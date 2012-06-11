package com.asocom.components;

import com.asocom.activities.R;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class ChatComponent extends LinearLayout {

	protected ScrollView scrollView;
	private Button button;
	private EditText editText;
	protected LinearLayout layout;

	public ChatComponent(Context context) {
		super(context);
		init(context);
	}

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

	public void setEditText(String text) {
		this.editText.setText(Html.fromHtml(text));
	}

	public void addChatMsg(ChatMsg cm) {
		layout.addView(cm);
		scrollView.removeAllViews();
		scrollView.addView(layout);
		scrollView.fullScroll(ScrollView.FOCUS_UP);
	}

	public void resetChat() {
		layout.removeAllViews();
	}

	public void setChatMsg(LinearLayout ll) {
		layout = ll;
	}

	public void removeAllViews() {
		scrollView.removeAllViews();
	}

	public Button getButtonSend() {
		return button;
	}

	public String getText() {
		return editText.getText().toString();
	}

	public EditText getEditText() {
		return editText;
	}

}
