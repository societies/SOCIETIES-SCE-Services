package si.setcce.collaborativemeeting;

import si.setcce.collaborativemeeting.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;


public class MainActivity extends Activity {

	private static final String TAG = MainActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		Log.i(TAG, "onCreate");

		super.onCreate(savedInstanceState);
		
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		TextView lblTitle = (TextView) findViewById(R.id.lblTitle);
		TextView lblCreated = (TextView) findViewById(R.id.lblCreated);
		TextView lblDeveloped = (TextView) findViewById(R.id.lblDeveloped);

		Animation animationTitle = AnimationUtils.loadAnimation(this, R.animator.textview_title_animation);
		Animation animationCreated = AnimationUtils.loadAnimation(this, R.animator.textview_created_animation);
		Animation animationDeveloped = AnimationUtils.loadAnimation(this, R.animator.textview_developed_animation);

		lblTitle.startAnimation(animationTitle);
		lblCreated.startAnimation(animationCreated);
		lblDeveloped.startAnimation(animationDeveloped);
		
		findViewById(R.id.mainLayout).performClick();  // TODO: remove (for development only)
	}

	public void flipSide(View v) {

		Log.i(TAG, "flipSide");

		Intent intent = new Intent(this, SecondActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		(this).overridePendingTransition(R.animator.activity_in, R.animator.activity_out);
	}
}



