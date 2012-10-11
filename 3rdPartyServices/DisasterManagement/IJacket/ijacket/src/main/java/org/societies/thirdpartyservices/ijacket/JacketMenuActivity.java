package org.societies.thirdpartyservices.ijacket;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeoutException;


import org.societies.android.api.cis.SocialContract;
import org.societies.thirdpartyservices.ijacket.com.BluetoothConnection;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.Toast;

public class JacketMenuActivity extends Activity {
	
	private TableLayout layout;
	//private Button ledButton;
	//private Button speakerButton;
	//private Button lcdButton;
	
	ContentResolver cr;
	
	private static final String LOG_TAG = JacketMenuActivity.class.getName();
	
	   /**
  * Called when the activity is first created.
  */
 @Override
 public void onCreate(Bundle savedInstanceState) {
     super.onCreate(savedInstanceState);

     Log.d(LOG_TAG, "on create JacketMenu");
     
     layout = new TableLayout(this);
     layout.setLayoutParams( new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.FILL_PARENT) );
     layout.setOrientation(TableLayout.VERTICAL);
     IJacketApp appState = ((IJacketApp)getApplicationContext());
     cr = this.getApplication().getContentResolver();
     
     Intent i = getIntent();
     if(null == i){
    	 quickToastMessage("no intent");
    	 Log.d(LOG_TAG, "no intent on onCreate JacketMenuActivity");
    	 // recreate button based on already stored pin configuration
    	 if(appState.getLedPin() != -1 ) addButton(new LedButton(JacketMenuActivity.this, appState.getLedPin()));
    	 if(appState.getVibrationPin() != -1 ) addButton(new VibrationButton(JacketMenuActivity.this, appState.getVibrationPin()));
    	 if(appState.getSpeakersPin() != -1 ) addButton(new SpeakerButton(JacketMenuActivity.this, appState.getSpeakersPin()));
    	 
     }else{
    	 int pin = -1;
    	 pin = i.getIntExtra("LED PIN",-1);
    	 if(-1 != pin){
    		 addButton(new LedButton(JacketMenuActivity.this, pin));
    		 appState.setLedPin(pin);
    	 }
    	 pin = i.getIntExtra("VIBRATION PIN",-1);
    	 if(-1 != pin){
    		 addButton(new VibrationButton(JacketMenuActivity.this, pin));
    		 appState.setVibrationPin(pin);
    	 }
    	 pin = i.getIntExtra("SPEAKER PIN",-1);
    	 if(-1 != pin){
    		 addButton(new SpeakerButton(JacketMenuActivity.this, pin));
    		 appState.setSpeakersPin(pin);
    	 }
 
    	 addButton(new LCDButton(JacketMenuActivity.this));
    	 
    	 Log.d(LOG_TAG, "JacketMenuActivity buttons added");
     }
     Log.d(LOG_TAG, "going to query the social provider");
     Uri COMUNITIES_URI = Uri.parse(SocialContract.AUTHORITY_STRING + SocialContract.UriPathIndex.COMMINITIES);
     try{
    	 Cursor cursor = cr.query(COMUNITIES_URI,null,null,null,null);
		if (cursor != null && cursor.getCount() >0) {
			// Determine the column index of the column named "word"
			//int index = cursor.getColumnIndex(SocialContract.Community.DISPLAY_NAME);
			
			/*
		     * Moves to the next row in the cursor. Before the first movement in the cursor, the
		     * "row pointer" is -1, and if you try to retrieve data at that position you will get an
		     * exception.
		     */
		    while (cursor.moveToNext()) {
		        Log.d("LOG_TAG", "found community " + cursor.getColumnIndex(SocialContract.Communities.NAME));
		    }
		} else {
			Log.d(LOG_TAG, "empty CIS list query result");
		}
	}catch (Exception e) {
		// TODO Auto-generated catch block
		Log.d(LOG_TAG, "exception in the create");
		e.printStackTrace();
	}

	Log.d(LOG_TAG, "content provider read");

     
     super.setContentView(layout);         
 }
 
 
 @Override
 protected void onStart() {
     super.onStart();
     // The activity is about to become visible.
 }
 @Override
 protected void onResume() {
     super.onResume();
     // The activity has become visible (it is now "resumed").
 }
 @Override
 protected void onPause() {
     super.onPause();
     // Another activity is taking focus (this activity is about to be "paused").
 }
 @Override
 protected void onStop() {
     super.onStop();
     // The activity is no longer visible (it is now "stopped")
 }
 @Override
 protected void onDestroy() {
     super.onDestroy();
     // The activity is about to be destroyed.
 }
 
 
 private void addButton(final Button button) {
     runOnUiThread(new Runnable() {
          public void run() {
          	layout.addView(button);
          }
     });
  }
 
 
 private void disconnect(){
		IJacketApp appState = ((IJacketApp)getApplicationContext());
		BluetoothConnection con = appState.getCon();
		if(con != null) con.disconnect();
  }


 /**
  * SERVICE_LED
  */
 private class LedButton extends Button implements View.OnClickListener{
 	boolean ledIsToggled;
 	int pin;
 	
		public LedButton(Context context, int pin) {
			super(context);
			ledIsToggled = false;
			setOnClickListener(this);
			setText("Toggle LED (" + pin + ")");
			this.pin = pin;
		}

		public void onClick(View v) {
			ledIsToggled = !ledIsToggled;
			try {
				IJacketApp appState = ((IJacketApp)getApplicationContext());
				BluetoothConnection con = appState.getCon();
				con.write(pin, ledIsToggled, false);
			} catch (TimeoutException e) {}
		}
 	
 }
 
 /**
  * SERVICE_SPEAKER
  */
 private class LCDButton extends Button implements View.OnClickListener {
 	int timesClicked;
 	
		public LCDButton(Context context) {
			super(context);
			setOnClickListener(this);
			setText("Print \"Hello World\"");
		}

		public void onClick(View v) {
			try {
				IJacketApp appState = ((IJacketApp)getApplicationContext());
				BluetoothConnection con = appState.getCon();
				con.print("Hello World! (" + timesClicked++ + ")", false);
			} catch (TimeoutException e) {}
		}
 	
 }
 
 /**
  * SERVICE_SPEAKER
  */
 private class SpeakerButton extends Button implements View.OnClickListener {
 	
		public SpeakerButton(Context context, int pin) {
			super(context);
			setOnClickListener(this);
			setText("Play Sound (" + pin + ")");
		}

		public void onClick(View v) {
			try {
				IJacketApp appState = ((IJacketApp)getApplicationContext());
				BluetoothConnection con = appState.getCon();
				con.data(new byte[]{100, 75, 52, 15}, false);
			} catch (TimeoutException e) {}
		}
 	
 }
 
 /**
  * SERVICE_VIBRATION
  */
 private class VibrationButton extends Button implements View.OnClickListener {
 	Timer timer = new Timer();
 	int pin;
 	
		public VibrationButton(Context context, int pin) {
			super(context);
			setOnClickListener(this);
			setText("Vibration (" + pin + ")");
			this.pin = pin;
		}

		public void onClick(View v) {
     	timer.schedule(new TimerTask(){
     		public void run(){
             	try {
             		
             		//Vibrate mobile
             		Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
             		vib.vibrate(2000);
             		
             		IJacketApp appState = ((IJacketApp)getApplicationContext());
            		BluetoothConnection con = appState.getCon();
             		
             		//Vibrate remote module
						con.write(pin, true, false);
                 	Thread.sleep(2000);
                 	con.write(pin, false, false);
                 	
					} 
             	catch (Exception e) {
                     quickToastMessage(e.getMessage());
					}
     		}
     	}, 0);
		}
 }

 
 private void quickToastMessage(final String message) {
     this.runOnUiThread(new Runnable() {
         public void run() {
             Toast.makeText(JacketMenuActivity.this, message, Toast.LENGTH_SHORT).show();
         }
     });
 }
 
 
}
