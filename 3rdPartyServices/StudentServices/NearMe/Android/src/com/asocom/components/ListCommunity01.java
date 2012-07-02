package com.asocom.components;

import com.asocom.activities.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class ListCommunity01 extends LinearLayout {

	private TopTitre text;
	private ScrollView scrollView;
	private LinearLayout layout;

	public ListCommunity01(Context context) {
		super(context);
		init(context);
	}

	public ListCommunity01(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li = (LayoutInflater) getContext().getSystemService(
				infService);
		li.inflate(R.layout.list_community_01, this, true);
		text = (TopTitre) findViewById(R.id.list_community_01_top_titre);
		scrollView = (ScrollView) findViewById(R.id.list_community_01_scroll_view);
		layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);
	}

	public void setTextTitre(String text) {
		this.text.setTextTitre(text);
	}

	public void setImageTitre(int icon) {
		this.text.setImageTitre(icon);
	}

	public void addListMember(PartList01 lm) {
		layout.addView(lm);
		scrollView.removeAllViews();
		scrollView.addView(layout);
	}

	public void setChatMsg(LinearLayout ll) {
		layout = ll;
	}

	public void removeAllViews() {
		scrollView.removeAllViews();
	}
}