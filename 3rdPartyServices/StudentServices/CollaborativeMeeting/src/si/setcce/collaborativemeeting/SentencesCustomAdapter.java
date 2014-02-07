package si.setcce.collaborativemeeting;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import si.setcce.collaborativemeeting.R;

/**
 * Created by Clive on 24.9.2013.
 */
public class SentencesCustomAdapter extends BaseAdapter /*implements View.OnClickListener*/ {

	private static final String TAG = SentencesCustomAdapter.class.getSimpleName();

	private Activity activity;
	private ArrayList<MinutesModel> data;
	private static LayoutInflater inflater = null;
	MinutesModel minutesModel = null;
	int i = 0;

	public SentencesCustomAdapter(Activity a, ArrayList<MinutesModel> d) {
		activity = a;
		data = d;

		inflater = (LayoutInflater) activity.
				getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	@Override
	public int getCount() {

		if (data.size() <= 0)
			return 1;
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater infalInflater = (LayoutInflater) this.activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = infalInflater.inflate(R.layout.minutes_layout, null);

		minutesModel = data.get(position);

		TextView lblTime = (TextView) convertView.findViewById(R.id.lblTime);
		TextView lblName = (TextView) convertView.findViewById(R.id.lblName);
		TextView lblMinute = (TextView) convertView.findViewById(R.id.lblMinute);

		lblTime.setText(minutesModel.getDateString());
		lblName.setText(minutesModel.getUserName());
		lblMinute.setText(minutesModel.getMinute());

		return convertView;
	}
}
