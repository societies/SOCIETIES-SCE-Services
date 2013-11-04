package org.societies.thirdpartyservices.crowdtasking;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.util.ByteArrayBuffer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

public class CheckUpdateTask extends AsyncTask<String, Void, Boolean> {
	private boolean showIsUpToDate = false;
	private Context context;

	public CheckUpdateTask(Context context) {
		this.context = context;
	}

	public CheckUpdateTask(Context context, boolean showIsUpToDate) {
		this.context = context;
		this.showIsUpToDate = showIsUpToDate;
	}

	@Override
	protected Boolean doInBackground(String... params) {
		int curVersion=0, newVersion=0;
        try {
        	Log.i("checkUpdate", "checkUpdate run");
            URL updateURL = new URL("http://crowdtasking.appspot.com/apk/latest");                
            URLConnection conn = updateURL.openConnection(); 
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            ByteArrayBuffer baf = new ByteArrayBuffer(50);
            
            int current = 0;
            while((current = bis.read()) != -1){
                 baf.append((byte)current);
            }

            curVersion = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        	Log.i("checkUpdate", "curVersion: "+curVersion);
            newVersion = Integer.valueOf(new String(baf.toByteArray()));
        	Log.i("checkUpdate", "newVersion: "+newVersion);
	        MainActivity.version = curVersion;
            
        } catch (Exception e) {
        	Log.e("checkUpdate", "error in check update");
        	Log.e("checkUpdate", e.getMessage());
        }
		return (newVersion > curVersion);
	}

	@Override
	protected void onPostExecute(Boolean newVersion) {
        if (newVersion) {
            new AlertDialog.Builder(context)
            .setTitle("Update Available")
            .setMessage("An update for SCT Android is available! Do you want to download a new version?")
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://crowdtasking.appspot.com/apk/index.html"));
                            context.startActivity(intent);
                    }
            })
            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
            })
            .show();
        }
        else {
        	if (showIsUpToDate) {
      	      new AlertDialog.Builder(context)
    	      //.setIcon(R.drawable.icon)
    	      .setTitle("No Update Available")
    	      .setMessage("You have the latest version of SCT Android.")
    	      .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
    	              public void onClick(DialogInterface dialog, int whichButton) {
    	              }
    	      })
    	      .show();
        	}
        }
	}
}
