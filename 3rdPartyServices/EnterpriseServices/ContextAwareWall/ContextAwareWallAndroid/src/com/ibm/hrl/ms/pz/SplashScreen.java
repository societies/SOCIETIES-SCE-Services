package com.ibm.hrl.ms.pz;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class SplashScreen extends Activity{
	protected boolean _active = true;
	protected int _splashTime = 2000; // time to display the splash screen in ms
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
			super.onCreate(savedInstanceState);
				getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);    
				setContentView(R.layout.splash);
			    Log.v("tag", "hi");
			   Button button =(Button) findViewById(R.id.button1);
			   Log.v("tag", "hi");
			    button.setOnClickListener(new View.OnClickListener() {
		            public void onClick(View v) {
		            	startActivity(new Intent("com.ibm.hrl.ms.pz.SettingsActivity"));
		            	Log.v("tag", "click");
		            }
		        });
		
	}
	
}
