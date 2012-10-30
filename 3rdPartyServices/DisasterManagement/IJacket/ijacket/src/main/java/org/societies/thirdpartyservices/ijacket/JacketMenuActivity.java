package org.societies.thirdpartyservices.ijacket;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeoutException;


import org.societies.android.api.cis.SocialContract;
import org.societies.thirdpartyservices.ijacket.IJackConnManager.IJackConnBinder;
import org.societies.thirdpartyservices.ijacket.com.BluetoothConnection;



import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.Toast;

public class JacketMenuActivity extends Activity {

	// TODO: update for content uris from babak
	
	
	private TableLayout layout;
	//private Button ledButton;
	//private Button speakerButton;
	//private Button lcdButton;
	
	ContentResolver cr;
	
	ActivityContentObserver actObs;
	private Handler handler = new Handler();
	
	private static final String LOG_TAG = JacketMenuActivity.class.getName();
	
    /** Flag indicating whether we have called bind on the service. */
    boolean mBound = false;
    IJackConnManager mService = null;
    ReconnectButton rc;
	
	   /**
  * Called when the activity is first created.
  */
 @Override
 public void onCreate(Bundle savedInstanceState) {
     super.onCreate(savedInstanceState);

     Log.d(LOG_TAG, "on create JacketMenu");

     registerContentObservers();
     
     layout = new TableLayout(this);
     layout.setLayoutParams( new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT) );
     layout.setOrientation(TableLayout.VERTICAL);
     IJacketApp appState = ((IJacketApp)getApplicationContext());
     cr = this.getApplication().getContentResolver();
     //appState.setCurrActiv(this);
     
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
     rc = new ReconnectButton(JacketMenuActivity.this);
     addButton(rc);
     rc.setEnabled(false);
     
     Log.d(LOG_TAG, "binding with conManager");
     Intent in = new Intent(this, IJackConnManager.class);
     bindService(in, mConnection, Context.BIND_AUTO_CREATE);
     
     Log.d(LOG_TAG, "going to query the activities provider");
     Uri Activity_URI = Uri.parse(SocialContract.AUTHORITY_STRING + SocialContract.UriPathIndex.COMMUNITY_ACTIVITIY);
     try{
    	 Cursor cursor = cr.query(Activity_URI,null,null,null,null);
		if (cursor != null && cursor.getCount() >0) {
			// Determine the column index of the column named "word"
			//int index = cursor.getColumnIndex(SocialContract.Community.DISPLAY_NAME);
			
			/*
		     * Moves to the next row in the cursor. Before the first movement in the cursor, the
		     * "row pointer" is -1, and if you try to retrieve data at that position you will get an
		     * exception.
		     */
		    while (cursor.moveToNext()) {
		        Log.d("LOG_TAG", "found activity " + cursor.getColumnIndex(SocialContract.CommunityActivity.GLOBAL_ID));
		    }
		} else {
			Log.d(LOG_TAG, "empty CIS list query result");
		}
		if(null != cursor) cursor.close();
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
	 unregisterContentObservers();
 }
 
 
 private void addButton(final Button button) {
     runOnUiThread(new Runnable() {
          public void run() {
          	layout.addView(button);
          }
     });
  }
 
 
 private void disconnect(){
		
		//if(mBound) mService.stopSelf();
  }

 
 	private void registerContentObservers() {
	  ContentResolver cr = getContentResolver();
	  
	  actObs = new ActivityContentObserver( handler );
	  Uri activURI = Uri.parse(SocialContract.AUTHORITY_STRING + SocialContract.UriPathIndex.COMMUNITY_ACTIVITIY);
	  cr.registerContentObserver(activURI , true, actObs );
	}

	private void unregisterContentObservers() {
	  ContentResolver cr = getContentResolver();
	  if( actObs != null ) {	
		cr.unregisterContentObserver( actObs );
		actObs = null;
	  }
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
				if(mBound && mService.getConStats()) mService.getConnector().write(pin, ledIsToggled, false);
			} catch (TimeoutException e) {}
		}
 	
 }
 
 
 /**
  * Reconnect_LED
  */
 private class ReconnectButton extends Button implements View.OnClickListener{
 	
		public ReconnectButton(Context context) {
			super(context);
			setOnClickListener(this);
			setText("Reconnect");
		}

		public void onClick(View v) {
			if(mBound && mService.getConStats()) this.setEnabled(false); // is already connected
			else{
				if(null != mService){
					mService.btConnect();
				}
				else{
					Log.d( LOG_TAG, "handler is null, I wont be able to call the service" );
				}
			}
			
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
				if(mBound && mService.getConStats()) mService.getConnector().print("Hello World! (" + timesClicked++ + ")", false);
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
				if(mBound && mService.getConStats()) mService.getConnector()
				.data(new byte[]{100, 75, 52, 15}, false);
				else rc.setEnabled(true);
			} catch (TimeoutException e) {}
		}
 	
 }
 
 /**
  * Activity observer
  */
 
 
	class ActivityContentObserver extends ContentObserver {
		  public ActivityContentObserver( Handler h ) {
			super( h );
		  }

		  public void onChange(boolean selfChange) {
			Log.d( LOG_TAG, "null onchange version called" );
			onChange(selfChange, null);
		  }
		  
		  @Override
		  public void onChange(boolean selfChange, Uri uri) {
			  if(null == uri) {
				  Log.d( LOG_TAG, "null uri" );
				  return;
			  }
			  Log.d( LOG_TAG, "uri path " +  uri.getPath()); 
		     try{
		    	 long row = ContentUris.parseId(uri);
		    	 String mSelectionClause = SocialContract.CommunityActivity._ID + " = ?";
		    	 String[] mSelectionArgs = {Long.toString(row)};
		    	 Uri FEED_URI = Uri.parse(SocialContract.AUTHORITY_STRING + SocialContract.UriPathIndex.COMMUNITY_ACTIVITIY);
		    	 Cursor cursor = cr.query(FEED_URI,null,mSelectionClause,mSelectionArgs,null);
				if (cursor != null && cursor.getCount() >0) {
				    while (cursor.moveToNext()) {
				        String act = "";
						int i = cursor.getColumnIndex(SocialContract.CommunityActivity.GLOBAL_ID_ACTOR); 
				        act +=  cursor.getString(i) + " ";
				        i = cursor.getColumnIndex(SocialContract.CommunityActivity.GLOBAL_ID_VERB); 
				        act +=  cursor.getString(i) + " ";
				        i = cursor.getColumnIndex(SocialContract.CommunityActivity.GLOBAL_ID_OBJECT); 
				        act +=  cursor.getString(i);
				    	
				    	Log.d("LOG_TAG", "found activity " + act);
				    }
				} else {
					Log.d(LOG_TAG, "empty CIS list query result");
				}
				if(null != cursor) cursor.close();
			}catch (Exception e) {
				// TODO Auto-generated catch block
				Log.d(LOG_TAG, "exception in the create");
				e.printStackTrace();
			}
			  
			  
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
			
			if(mBound && mService.getConStats())
     	timer.schedule(new TimerTask(){
     		public void run(){
             	try {
             		
             		//Vibrate mobile
             		Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
             		vib.vibrate(2000);
             		
            		BluetoothConnection con =  mService.getConnector();
             		
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
 
 /**
  * Class for interacting with the main interface of the service.
  */
 private ServiceConnection mConnection = new ServiceConnection() {
     public void onServiceConnected(ComponentName className, IBinder service) {
     	
     	IJackConnBinder binder = (IJackConnBinder) service;
         mService = binder.getService();
         mBound = true;
     }

     public void onServiceDisconnected(ComponentName className) {
         mBound = false;
     }
 };
 
}
