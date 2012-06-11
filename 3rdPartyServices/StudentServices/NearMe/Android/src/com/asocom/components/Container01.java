package com.asocom.components;

import com.asocom.activities.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class Container01 extends LinearLayout {

	private TopTitre text;
	private ScrollView scrollView;
	private LinearLayout layout;

	public Container01(Context context) {
		super(context);
		init(context);
	}

	public Container01(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li = (LayoutInflater) getContext().getSystemService(
				infService);
		li.inflate(R.layout.container_01, this, true);
		text = (TopTitre) findViewById(R.id.container_01_top_titre);
		scrollView = (ScrollView) findViewById(R.id.container_01_scroll_view);
		layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);
	}

	public void setTextTitre(String text) {
		this.text.setTextTitre(text);
	}

	public void setImageTitre(int icon) {
		this.text.setImageTitre(icon);
	}

	public void addPartList(PartList01 lm) {
		layout.addView(lm);
		scrollView.removeAllViews();
		scrollView.addView(layout);
		// scrollView.scrollBy(0, 1000000);
		// scrollView.requestFocus();
		// scrollView.fullScroll(ScrollView.FOCUS_UP);
		// scrollView.fullScroll(ScrollView.FOCUS_RIGHT);
	}

	public void addPartList(PartList02 lm) {
		layout.addView(lm);
		scrollView.removeAllViews();
		scrollView.addView(layout);
		// scrollView.scrollBy(0, 1000000);
		// scrollView.requestFocus();
		// scrollView.fullScroll(ScrollView.FOCUS_UP);
		// scrollView.fullScroll(ScrollView.FOCUS_RIGHT);
	}

	public void addPartList(PartList04 lm) {
		layout.addView(lm);
		scrollView.removeAllViews();
		scrollView.addView(layout);
		// scrollView.scrollBy(0, 1000000);
		// scrollView.requestFocus();
		// scrollView.fullScroll(ScrollView.FOCUS_UP);
		// scrollView.fullScroll(ScrollView.FOCUS_RIGHT);
	}

	public void addPartList(PartList05 lm) {
		layout.addView(lm);
		scrollView.removeAllViews();
		scrollView.addView(layout);
		// scrollView.scrollBy(0, 1000000);
		// scrollView.requestFocus();
		// scrollView.fullScroll(ScrollView.FOCUS_UP);
		// scrollView.fullScroll(ScrollView.FOCUS_RIGHT);
	}

	public void addUserProfile(CreateAccountComponent up) {
		layout.addView(up);
		scrollView.removeAllViews();
		scrollView.addView(layout);
	}

	public void addCreateCommunity(CreateCommunityComponent cc) {
		layout.addView(cc);
		scrollView.removeAllViews();
		scrollView.addView(layout);
	}

	public void setLayout(LinearLayout ll) {
		layout = ll;
	}

}