/*
 * 
 */
package com.asocom.components;

import android.content.Context;
import android.widget.LinearLayout;

// TODO: Auto-generated Javadoc
/**
 * The Class RecommendCommunity.
 */
public class RecommendCommunity extends ChatComponent {

	/**
	 * Instantiates a new recommend community.
	 *
	 * @param context the context
	 */
	public RecommendCommunity(Context context) {
		super(context);
	}

	/**
	 * Adds the part list03.
	 *
	 * @param pl03 the pl03
	 */
	public void addPartList03(PartList03 pl03) {
		layout.addView(pl03);
		scrollView.removeAllViews();
		scrollView.addView(layout);
	}

	/**
	 * Sets the layout.
	 *
	 * @param ll the new layout
	 */
	public void setLayout(LinearLayout ll) {
		layout = ll;
	}

}
