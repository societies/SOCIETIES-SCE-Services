/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package si.setcce.collaborativemeeting;

import java.util.ArrayList;

import si.setcce.collaborativemeeting.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by Clive on 24.9.2013.
 */
public class UsersFragment extends ListFragment implements EditUserDialogFragment.EditUserDialogListener {

	private static final String TAG = UsersFragment.class.getSimpleName();

	/**
	 * User names
	 */
	private static ArrayList<String> users = new ArrayList<String>();
	
	/**
	 * User IDs for Crowd Tasking, not for this app
	 */
	private static ArrayList<String> userIds = new ArrayList<String>();
	
	private OnHeadlineSelectedListener mCallback;
	private int selected;

	public interface OnHeadlineSelectedListener {
		public void onUserSelected(int position);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		update();
	}
	
	@Override
	public void onStart() {
		super.onStart();

		if (getFragmentManager().findFragmentById(R.id.users_fragment) != null) {
			getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			registerForContextMenu(getListView());
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		try {
			mCallback = (OnHeadlineSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnHeadlineSelectedListener");
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {

		Log.d(TAG, "onListItemClick: position = " + position);

		mCallback.onUserSelected(position);
		getListView().setItemChecked(position, true);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {

		super.onCreateContextMenu(menu, v, menuInfo);
		
		AdapterView.AdapterContextMenuInfo amenuInfo = (AdapterView.AdapterContextMenuInfo) menuInfo;
		selected = amenuInfo.position;
		Object item = getListAdapter().getItem(amenuInfo.position);

		Log.d(TAG, "Creating context menu for user: " + item);
		menu.setHeaderTitle(getText(R.string.user) + " " + item);
		menu.add(Menu.NONE, 0, Menu.NONE, getText(R.string.action_edit));
		menu.add(Menu.NONE, 1, Menu.NONE, getText(R.string.action_delete));
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {

		String user = (String) getListAdapter().getItem(selected);
		Log.d(TAG, "Context item clicked: " + item.getTitle() + " for user ID: " + selected + ", user: " + user);
		
		switch (item.getItemId()) {
		case 0:
			Log.d(TAG, "Edit user ID " + selected);
			EditUserDialogFragment dialog = new EditUserDialogFragment();
			dialog.show(getFragmentManager(), "tag");
			break;
		case 1:
			Log.d(TAG, "Delete user ID " + selected);
			confirmDeleteUser();
			break;
		default:
			return false;
		}
		return true;
	}

	private void confirmDeleteUser() {
		new AlertDialog.Builder(getActivity())
		.setMessage(getText(R.string.confirmDeleteUser) + " " + users.get(selected) + "?")
		.setPositiveButton(getText(R.string.ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				users.remove(selected);
				userIds.remove(selected);
				update();
			}
		})
		.setNegativeButton(getText(R.string.cancel), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		})
		.show();
	}
	
	public void deleteAllUsers() {
		users.clear();
		userIds.clear();
		update();
		Log.i(TAG, "All users deleted");
	}

	// The dialog fragment receives a reference to this Activity through the
	// Fragment.onAttach() callback, which it uses to call the following methods
	// defined by the NoticeDialogFragment.NoticeDialogListener interface
	@Override
	public void onDialogPositiveClick(DialogFragment dialog, String newName) {
		Log.d(TAG, "onDialogPositiveClick: renamed to " + newName);
		users.set(selected, newName);
		update();
	}

	@Override
	public void onDialogNegativeClick(DialogFragment dialog) {
		Log.d(TAG, "onDialogNegativeClick");
	}

	private void update() {

		// TODO: optimization: do not recreate ListAdapter, but only update
		int layout = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
				android.R.layout.simple_list_item_activated_1 :
				android.R.layout.simple_list_item_1;
		ListAdapter adapter = new ArrayAdapter<String>(getActivity(), layout, users);
		setListAdapter(adapter);
	}
	
	public void addUser(User user) {
		
		String id = user.getId();
		String name = user.getName();
		if (!user.isCheckedIn()) {
			// TODO: show users who are not checked in yet, just disable them
			Log.d(TAG, "User " + name + " (ID = " + id + ") is not checked in yet, will not add him");
			return;
		}
		
		if (userIds.contains(id)) {
			Log.w(TAG, "Duplicate user ID: " + id);
			Toast.makeText(getActivity(), getText(R.string.userExists) + name + ", ID = " + id, Toast.LENGTH_SHORT);
			return;
		}
		if (users.contains(name)) {
			Log.w(TAG, "Duplicate user name: " + name);
			Toast.makeText(getActivity(), getText(R.string.userExists) + name, Toast.LENGTH_SHORT);
		}
		userIds.add(id);
		users.add(name);
		Log.i(TAG, "Added user " + name + ", ID = " + id + ", checked in = " + user.isCheckedIn());
		update();
	}
	
	public String getUserName(int id) {
		return users.get(id);
	}
	
	public String getUserId(int id) {
		return userIds.get(id);
	}

	/**
	 * 
	 * @param userName
	 * @return User ID for Crowd Tasking
	 */
	public String getUserId(String userName) {
		int id = users.indexOf(userName);
		return userIds.get(id);
	}
	
	/**
	 * Get user name
	 * @param userId User ID for Crowd Tasking
	 * @return The user name
	 */
	public String getUserName(String userId) {
		int id = userIds.indexOf(userId);
		return users.get(id);
	}
	
	public int size() {
		return users.size();
	}
}
