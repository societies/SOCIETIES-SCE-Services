/*
 * 
 */
package com.asocom.components;

import com.asocom.activities.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ScrollView;

// TODO: Auto-generated Javadoc
/**
 * The Class Container01.
 */
public class Container01 extends LinearLayout {

	/** The text. */
	private TopTitre text;
	
	/** The scroll view. */
	private ScrollView scrollView;
	
	/** The layout. */
	private LinearLayout layout;

	/**
	 * Instantiates a new container01.
	 *
	 * @param context the context
	 */
	public Container01(Context context) {
		super(context);
		init(context);
	}

	/**
	 * Instantiates a new container01.
	 *
	 * @param context the context
	 * @param attrs the attrs
	 */
	public Container01(Context context, AttributeSet attrs) {
		super(context, attrs);
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
		li.inflate(R.layout.container_01, this, true);
		text = (TopTitre) findViewById(R.id.container_01_top_titre);
		scrollView = (ScrollView) findViewById(R.id.container_01_scroll_view);
		layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);
	}

	/**
	 * Sets the text titre.
	 *
	 * @param text the new text titre
	 */
	public void setTextTitre(String text) {
		this.text.setTextTitre(text);
	}

	/**
	 * Sets the image titre.
	 *
	 * @param icon the new image titre
	 */
	public void setImageTitre(int icon) {
		this.text.setImageTitre(icon);
	}

	/**
	 * Adds the part list.
	 *
	 * @param lm the lm
	 */
	public void addPartList(PartList01 lm) {
		layout.addView(lm);
		scrollView.removeAllViews();
		scrollView.addView(layout);
		// scrollView.scrollBy(0, 1000000);
		// scrollView.requestFocus();
		// scrollView.fullScroll(ScrollView.FOCUS_UP);
		// scrollView.fullScroll(ScrollView.FOCUS_RIGHT);
	}

	/**
	 * Adds the part list.
	 *
	 * @param lm the lm
	 */
	public void addPartList(PartList02 lm) {
		layout.addView(lm);
		scrollView.removeAllViews();
		scrollView.addView(layout);
		// scrollView.scrollBy(0, 1000000);
		// scrollView.requestFocus();
		// scrollView.fullScroll(ScrollView.FOCUS_UP);
		// scrollView.fullScroll(ScrollView.FOCUS_RIGHT);
	}

	/**
	 * Adds the part list.
	 *
	 * @param lm the lm
	 */
	public void addPartList(PartList04 lm) {
		layout.addView(lm);
		scrollView.removeAllViews();
		scrollView.addView(layout);
		// scrollView.scrollBy(0, 1000000);
		// scrollView.requestFocus();
		// scrollView.fullScroll(ScrollView.FOCUS_UP);
		// scrollView.fullScroll(ScrollView.FOCUS_RIGHT);
	}

	/**
	 * Adds the part list.
	 *
	 * @param lm the lm
	 */
	public void addPartList(PartList05 lm) {
		layout.addView(lm);
		scrollView.removeAllViews();
		scrollView.addView(layout);
		// scrollView.scrollBy(0, 1000000);
		// scrollView.requestFocus();
		// scrollView.fullScroll(ScrollView.FOCUS_UP);
		// scrollView.fullScroll(ScrollView.FOCUS_RIGHT);
	}

	/**
	 * Adds the user profile.
	 *
	 * @param up the up
	 */
	public void addUserProfile(CreateAccountComponent up) {
		layout.addView(up);
		scrollView.removeAllViews();
		scrollView.addView(layout);
	}

	/**
	 * Adds the create community.
	 *
	 * @param cc the cc
	 */
	public void addCreateCommunity(CreateCommunityComponent cc) {
		layout.addView(cc);
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