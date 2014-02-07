package si.setcce.collaborativemeeting;

import si.setcce.collaborativemeeting.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * Created by Clive on 24.9.2013.
 */
public class MinutesFragment extends Fragment {

	private static final String TAG = MinutesFragment.class.getSimpleName();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		return inflater.inflate(R.layout.fragment_minutes, container, false);
	}

	@Override
	public void onStart() {
		Log.d(TAG, "onStart");
		super.onStart();
		updateList();
	}

	private void updateList()
	{
		Log.d(TAG, "updateList");
		ListView lvMinutes = (ListView) getView().findViewById(R.id.lvMinutes);
		SentencesCustomAdapter listAdapter = new SentencesCustomAdapter(getActivity()
				, SecondActivity.minutesModel);
		lvMinutes.setAdapter(listAdapter);
	}
}
