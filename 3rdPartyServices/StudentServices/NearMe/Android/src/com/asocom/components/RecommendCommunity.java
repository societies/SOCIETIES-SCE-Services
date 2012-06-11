package com.asocom.components;

import android.content.Context;
import android.widget.LinearLayout;

public class RecommendCommunity extends ChatComponent {

	public RecommendCommunity(Context context) {
		super(context);
	}

	public void addPartList03(PartList03 pl03) {
		layout.addView(pl03);
		scrollView.removeAllViews();
		scrollView.addView(layout);
	}

	public void setLayout(LinearLayout ll) {
		layout = ll;
	}

}
