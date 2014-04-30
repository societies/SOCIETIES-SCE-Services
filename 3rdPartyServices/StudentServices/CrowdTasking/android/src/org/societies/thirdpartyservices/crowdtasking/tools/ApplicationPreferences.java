package org.societies.thirdpartyservices.crowdtasking.tools;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;

/**
 * Created by Simon on 31.10.2013.
 */
public class ApplicationPreferences {
	private ContextWrapper contextWrapper;
	private SharedPreferences preferences;

	public ApplicationPreferences(ContextWrapper contextWrapper, String prefName) {
		this.contextWrapper = contextWrapper;
		preferences = contextWrapper.getSharedPreferences(prefName, Context.MODE_PRIVATE);
	}

	public void putString(String key, String value) {
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public String getString(String key) {
		return getString(key, "");
	}

	public String getString(String key, String defaultValue) {
		return preferences.getString(key, defaultValue);
	}
}
