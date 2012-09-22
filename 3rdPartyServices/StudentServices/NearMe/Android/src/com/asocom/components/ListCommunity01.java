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
 * The Class ListCommunity01.
 */
public class ListCommunity01 extends LinearLayout {

	/** The text. */
	private TopTitre text;
	
	/** The scroll view. */
	private ScrollView scrollView;
	
	/** The layout. */
	private LinearLayout layout;

	/**
	 * Instantiates a new list community01.
	 *
	 * @param context the context
	 */
	public ListCommunity01(Context context) {
		super(context);
		init(context);
	}

	/**
	 * Instantiates a new list community01.
	 *
	 * @param context the context
	 * @param attrs the attrs
	 */
	public ListCommunity01(Context context, AttributeSet attrs) {
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
		li.inflate(R.layout.list_community_01, this, true);
		text = (TopTitre) findViewById(R.id.list_community_01_top_titre);
		scrollView = (ScrollView) findViewById(R.id.list_community_01_scroll_view);
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
	 * Adds the list member.
	 *
	 * @param lm the lm
	 */
	public void addListMember(PartList01 lm) {
		layout.addView(lm);
		scrollView.removeAllViews();
		scrollView.addView(layout);
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
}