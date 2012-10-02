/*
 * Copyright (C) 2010 Tani Group 
 * http://android-demo.blogspot.com/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.asocom.tools;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

// TODO: Auto-generated Javadoc
/**
 * The Class IconContextMenu.
 *
 * @author nguyendt
 */
public class IconContextMenu implements DialogInterface.OnCancelListener,
		DialogInterface.OnDismissListener {

	/** The Constant LIST_PREFERED_HEIGHT. */
	private static final int LIST_PREFERED_HEIGHT = 65;

	/** The menu adapter. */
	private IconMenuAdapter menuAdapter = null;
	
	/** The parent activity. */
	private Activity parentActivity = null;
	
	/** The dialog id. */
	private int dialogId = 0;

	/** The click handler. */
	private IconContextMenuOnClickListener clickHandler = null;

	/**
	 * constructor.
	 *
	 * @param parent the parent
	 * @param id the id
	 */
	public IconContextMenu(Activity parent, int id) {
		this.parentActivity = parent;
		this.dialogId = id;

		menuAdapter = new IconMenuAdapter(parentActivity);
	}

	/**
	 * Add menu item.
	 *
	 * @param res the res
	 * @param title the title
	 * @param imageResourceId the image resource id
	 * @param actionTag the action tag
	 */
	public void addItem(Resources res, CharSequence title, int imageResourceId,
			int actionTag) {
		menuAdapter.addItem(new IconContextMenuItem(res, title,
				imageResourceId, actionTag));
	}

	/**
	 * Adds the item.
	 *
	 * @param res the res
	 * @param textResourceId the text resource id
	 * @param imageResourceId the image resource id
	 * @param actionTag the action tag
	 */
	public void addItem(Resources res, int textResourceId, int imageResourceId,
			int actionTag) {
		menuAdapter.addItem(new IconContextMenuItem(res, textResourceId,
				imageResourceId, actionTag));
	}

	/**
	 * Set menu onclick listener.
	 *
	 * @param listener the new on click listener
	 */
	public void setOnClickListener(IconContextMenuOnClickListener listener) {
		clickHandler = listener;
	}

	/**
	 * Create menu.
	 *
	 * @param menuItitle the menu ititle
	 * @return the dialog
	 */
	public Dialog createMenu(String menuItitle) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(
				parentActivity);
		builder.setTitle(menuItitle);
		builder.setAdapter(menuAdapter, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialoginterface, int i) {
				IconContextMenuItem item = (IconContextMenuItem) menuAdapter
						.getItem(i);

				if (clickHandler != null) {
					clickHandler.onClick(item.actionTag);
				}
			}
		});

		builder.setInverseBackgroundForced(true);

		AlertDialog dialog = builder.create();
		dialog.setOnCancelListener(this);
		dialog.setOnDismissListener(this);
		return dialog;
	}

	/* (non-Javadoc)
	 * @see android.content.DialogInterface.OnCancelListener#onCancel(android.content.DialogInterface)
	 */
	public void onCancel(DialogInterface dialog) {
		cleanup();
	}

	/* (non-Javadoc)
	 * @see android.content.DialogInterface.OnDismissListener#onDismiss(android.content.DialogInterface)
	 */
	public void onDismiss(DialogInterface dialog) {
	}

	/**
	 * Cleanup.
	 */
	private void cleanup() {
		parentActivity.dismissDialog(dialogId);
	}

	/**
	 * IconContextMenu On Click Listener interface.
	 *
	 * @see IconContextMenuOnClickEvent
	 */
	public interface IconContextMenuOnClickListener {
		
		/**
		 * On click.
		 *
		 * @param menuId the menu id
		 */
		public abstract void onClick(int menuId);
	}

	/**
	 * Menu-like list adapter with icon.
	 */
	protected class IconMenuAdapter extends BaseAdapter {
		
		/** The context. */
		private Context context = null;

		/** The m items. */
		private ArrayList<IconContextMenuItem> mItems = new ArrayList<IconContextMenuItem>();

		/**
		 * Instantiates a new icon menu adapter.
		 *
		 * @param context the context
		 */
		public IconMenuAdapter(Context context) {
			this.context = context;
		}

		/**
		 * add item to adapter.
		 *
		 * @param menuItem the menu item
		 */
		public void addItem(IconContextMenuItem menuItem) {
			mItems.add(menuItem);
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getCount()
		 */
		public int getCount() {
			return mItems.size();
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getItem(int)
		 */
		public Object getItem(int position) {
			return mItems.get(position);
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getItemId(int)
		 */
		public long getItemId(int position) {
			IconContextMenuItem item = (IconContextMenuItem) getItem(position);
			return item.actionTag;
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
		 */
		public View getView(int position, View convertView, ViewGroup parent) {
			IconContextMenuItem item = (IconContextMenuItem) getItem(position);

			Resources res = parentActivity.getResources();

			if (convertView == null) {
				TextView temp = new TextView(context);
				AbsListView.LayoutParams param = new AbsListView.LayoutParams(
						AbsListView.LayoutParams.FILL_PARENT,
						AbsListView.LayoutParams.WRAP_CONTENT);
				temp.setLayoutParams(param);
				temp.setPadding((int) toPixel(res, 15), 0,
						(int) toPixel(res, 15), 0);
				temp.setGravity(android.view.Gravity.CENTER_VERTICAL);

				Theme th = context.getTheme();
				TypedValue tv = new TypedValue();

				if (th.resolveAttribute(
						android.R.attr.textAppearanceLargeInverse, tv, true)) {
					temp.setTextAppearance(context, tv.resourceId);
				}

				temp.setMinHeight(LIST_PREFERED_HEIGHT);
				temp.setCompoundDrawablePadding((int) toPixel(res, 14));
				convertView = temp;
			}

			TextView textView = (TextView) convertView;
			textView.setTag(item);
			textView.setText(item.text);
			textView.setCompoundDrawablesWithIntrinsicBounds(item.image, null,
					null, null);

			return textView;
		}

		/**
		 * To pixel.
		 *
		 * @param res the res
		 * @param dip the dip
		 * @return the float
		 */
		private float toPixel(Resources res, int dip) {
			float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
					dip, res.getDisplayMetrics());
			return px;
		}
	}

	/**
	 * menu-like list item with icon.
	 */
	protected class IconContextMenuItem {
		
		/** The text. */
		public final CharSequence text;
		
		/** The image. */
		public final Drawable image;
		
		/** The action tag. */
		public final int actionTag;

		/**
		 * public constructor.
		 *
		 * @param res resource handler
		 * @param textResourceId id of title in resource
		 * @param imageResourceId id of icon in resource
		 * @param actionTag indicate action of menu item
		 */
		public IconContextMenuItem(Resources res, int textResourceId,
				int imageResourceId, int actionTag) {
			text = res.getString(textResourceId);
			if (imageResourceId != -1) {
				image = res.getDrawable(imageResourceId);
			} else {
				image = null;
			}
			this.actionTag = actionTag;
		}

		/**
		 * public constructor.
		 *
		 * @param res resource handler
		 * @param title menu item title
		 * @param imageResourceId id of icon in resource
		 * @param actionTag indicate action of menu item
		 */
		public IconContextMenuItem(Resources res, CharSequence title,
				int imageResourceId, int actionTag) {
			text = title;
			if (imageResourceId != -1) {
				image = res.getDrawable(imageResourceId);
			} else {
				image = null;
			}
			this.actionTag = actionTag;
		}
	}
}
