

package com.ibm.hrl.ms.pz;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
 
// ----------------------------------------------------------------------
 
//@SuppressLint("NewApi")
@TargetApi(9)
public class CameraPreview extends Activity {

	private CustomCameraView mCustomCameraView;
	private ImageView mBackGroundImageView;
	
	private TimerTask mScanTask;
	boolean mScanTaskActive=false;
	
	final Context context = this;
	
	private String DEFAULT_COLOR = "red";
	private int DEFAULT_TEXT_SIZE = 30;
	private int DEFAULT_NUM_MSG = 1;
	private int DEFAULT_MSG_UPDATE_INTERVAL = 1000*10;
	
	private String mColor = DEFAULT_COLOR;
	private int mTextSize = DEFAULT_TEXT_SIZE;
	private int mNumOfMsgToDisplay = DEFAULT_NUM_MSG;
	private Typeface mTextFont;
	
	private int mLastProcessedMsgId=0;
	
	private LinkedList<JSONObject> mMsgInClient = new LinkedList<JSONObject>();
	private HashSet<Integer> mMsgIdsInClient = new HashSet<Integer>();
	private HashSet<String> mMsgZonesIds = new HashSet<String>();
	String mBackgroundImgName = "";
	
	private String mServerUrl;
	private String mEntityId;
	private String mCisId;
	private boolean mInteractiveBackgroundActive; 
	private String mCurrentPhotoPath;
	
	
	private long mBackgroundImageTakenTS=0;
	private static final int ACTION_TAKE_PHOTO_B = 1;
	
	private Map<String,ImageDetails> mBackgroundImagesNames = new HashMap<String,ImageDetails>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	try{
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(R.layout.main);
		
		mMsgZonesIds = new HashSet<String>();
		mBackgroundImagesNames.clear();
		initialUserPrefVars();
		
		mTextFont = Typeface.createFromAsset(getAssets(),"fonts/COMIC.TTF");
			
		createTextViewsMsgs(mNumOfMsgToDisplay);
		
		mCustomCameraView = (CustomCameraView) findViewById(R.id.surfaceView);
		mBackGroundImageView = (ImageView)findViewById(R.id.imageBackground);
		mBackgroundImgName = "";
		
		ImageButton settings =(ImageButton) findViewById(R.id.settings);
		
		//add button listener
		settings.setOnClickListener(new OnClickListener() {
			  public void onClick(View arg0) {
				  createPreferencesDialog();
			  }
		});
		
		ImageButton sprayFrame =(ImageButton) findViewById(R.id.sprayFrame);
		sprayFrame.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ImageButton sendNow =(ImageButton) findViewById(R.id.sendNow);
				EditText postMsgText = (EditText) findViewById(R.id.postMsgText);
				EditText messageDest = (EditText) findViewById(R.id.messageDest);
				
				if (sendNow.getVisibility() == ImageButton.INVISIBLE){
					sendNow.setVisibility(ImageButton.VISIBLE);
					postMsgText.setVisibility(EditText.VISIBLE);
					messageDest.setVisibility(EditText.VISIBLE);
				}else{
					sendNow.setVisibility(ImageButton.INVISIBLE);
					postMsgText.setVisibility(EditText.INVISIBLE);
					messageDest.setVisibility(EditText.INVISIBLE);
				}
			}
		});
		
		ImageButton sendNow =(ImageButton) findViewById(R.id.sendNow);
		sendNow.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
				EditText editText = (EditText) findViewById(R.id.postMsgText);
				String sprayText = editText.getText().toString();
				
				EditText editTextMsgDest = (EditText)findViewById(R.id.messageDest);
				String msgDest = editTextMsgDest.getText().toString();
				msgDest = msgDest == null ? "": msgDest.trim();
				
				if (sprayText.trim().length() > 0){
					postMessages(sprayText,msgDest);
				}
				//ImageButton sendNow =(ImageButton) findViewById(R.id.sendNow);
				//sendNow.setVisibility(ImageButton.INVISIBLE);
				//editText.setVisibility(EditText.INVISIBLE);
				editText.setText("");	
				editTextMsgDest.setText("");
				
				//hide keyboard
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
				imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(editTextMsgDest.getWindowToken(), 0);
				setTopGravityMsgContainer();
			}
		});
		
		if (!mInteractiveBackgroundActive ){
			disableInteractiveBackground();
		}else{
			enableInteractiveBackground();
		}
		ImageButton picSBtn = (ImageButton) findViewById(R.id.camera);
		picSBtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				dispatchTakePictureIntent();				
			}
			
		});
		
		getMessagesTask();
		
		Map<String,ImageDetails> filesOnSD = SDHelper.readFileNamesInVgGallery();
		mBackgroundImagesNames.putAll(filesOnSD);
		 
	}catch (Exception e) {
		 Log.e("error", e.getMessage()+ " ; cause: "+e.getCause(), e);
	}
	}
	
	
	private void disableInteractiveBackground(){
		ImageButton picSBtn = (ImageButton) findViewById(R.id.camera);
		picSBtn.setVisibility(View.GONE);
		mBackGroundImageView.setVisibility(View.GONE);
		mCustomCameraView.onPauseMySurfaceView();
		mCustomCameraView.onResumeMySurfaceView();
		mCustomCameraView.setVisibility(View.VISIBLE);
	}
	
	private void enableInteractiveBackground(){
		ImageButton picSBtn = (ImageButton) findViewById(R.id.camera);
		picSBtn.setVisibility(View.VISIBLE);
		mCustomCameraView.setVisibility(View.GONE);
		mBackGroundImageView.setImageResource(R.drawable.black);
		mBackGroundImageView.setVisibility(View.VISIBLE);
		mBackgroundImgName = ""; 
		/*
		if (mBackGroundImageView.getVisibility() != View.VISIBLE){
			mBackGroundImageView.setVisibility(View.VISIBLE);
			mCustomCameraView.setVisibility(View.GONE);
			mCustomCameraView.onPauseMySurfaceView();
		}else{
			mCustomCameraView.onPauseMySurfaceView();
			mCustomCameraView.onResumeMySurfaceView();	
			mCustomCameraView.setVisibility(View.VISIBLE);
			mBackGroundImageView.setVisibility(View.GONE);
			
		}*/
	}
	
	private void dispatchTakePictureIntent() {

		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File file = null;
			
		try {
			String tempGeneratedImgName = SDHelper.generateImageName();
			file = SDHelper.createImageFile(tempGeneratedImgName);
			mCurrentPhotoPath = file.getAbsolutePath();
			setBackgroundImageName(tempGeneratedImgName);
			takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
			//mCustomCameraView.onPauseMySurfaceView();
		} catch (IOException e) {
			Log.e("error", e.getMessage()+ " ; cause: "+e.getCause(), e);
			file = null;
			mCurrentPhotoPath = null;
		}
			
		startActivityForResult(takePictureIntent,ACTION_TAKE_PHOTO_B);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case ACTION_TAKE_PHOTO_B: {
			if (resultCode == RESULT_OK) {
				if (mCurrentPhotoPath != null) {
					//mCustomCameraView.onResumeMySurfaceView();
					File image = displayBackgroundImage(getLocalBackgroundImgName(),mCurrentPhotoPath,true,false);
					DownloadUploadHelper dh = new DownloadUploadHelper();
					dh.uploadImage(image, getLocalBackgroundImgName(), this.mCisId,mServerUrl);
					mCurrentPhotoPath = null;
					mBackgroundImageTakenTS = System.currentTimeMillis();
				}
			}else{
				setBackgroundImageName("");
				Log.e("vg", "failure on activity result 'ACTION_TAKE_PHOTO_B'");
			}
			break;
		} // ACTION_TAKE_PHOTO_B

		} // switch
	}
	
	public synchronized File displayBackgroundImage(String imageName, String imagePath, boolean reduceImageSize,boolean downloaded){
		File image = null;
		try{
			if (mInteractiveBackgroundActive){
				mCustomCameraView.setVisibility(View.GONE);
				setPic(imagePath,reduceImageSize);
				String generatedImageName = setBackgroundImageName(imageName);
				
				if (!mBackgroundImagesNames.containsKey(generatedImageName)){
					image = galleryAddPic(imagePath);
					mBackgroundImagesNames.put(generatedImageName,new ImageDetails(generatedImageName,imagePath, downloaded));
				}
			}
			
		}catch(Exception e){
			mCustomCameraView.setVisibility(View.VISIBLE);
			mBackGroundImageView.setVisibility(View.GONE);
			Log.e("vg", e.getMessage() ,e);
		}
		
		return image;
	}

	private String setBackgroundImageName(String originalName){
		//first remove the file type (e.g. jpg) 
		mBackgroundImgName = originalName.split("\\.")[0];
		//then remove the unique android suffix (if exists)
		mBackgroundImgName = mBackgroundImgName.split("\\-")[0];
		return mBackgroundImgName;
	}
	
	
	private void setPic(String photoPath, boolean reduceImageSize) {

		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
		//mBackGroundImageView.setScaleType(ScaleType.FIT_XY);
		int targetW = mBackGroundImageView.getWidth();
		int targetH = mBackGroundImageView.getHeight();

		/* Get the size of the image */
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;

		BitmapFactory.decodeFile(photoPath, bmOptions);
		
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;
		
		/* Figure out which way needs to be reduced less */
		int scaleFactor = 1;
		if ((targetW > 0) || (targetH > 0)) {
			scaleFactor = Math.min(photoW/targetW, photoH/targetH);	
		}

		/* Set bitmap options to scale the image decode target */
		bmOptions.inJustDecodeBounds = false;
		//bmOptions.inSampleSize = scaleFactor;
		if (reduceImageSize){
			bmOptions.inSampleSize = 16;
		}else{
			bmOptions.inSampleSize = 1;
		}
		bmOptions.inPurgeable = true;
				/* Decode the JPEG file into a Bitmap */
		Bitmap bitmap = BitmapFactory.decodeFile(photoPath, bmOptions);
		
		Matrix matrix = rotateImage(photoPath);
		/*
		ExifInterface exif=null;
		try {
			exif = new ExifInterface(photoPath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
		Matrix matrix = new Matrix();
		matrix.postRotate(90);
		*/
		/*if (orientation == 6 ){
			matrix.postRotate(90);
		}*/
		bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		
		/* Associate the Bitmap to the ImageView */
		mBackGroundImageView.setImageBitmap(bitmap);
		
		mBackGroundImageView.setVisibility(View.VISIBLE);
		
	}

	private Matrix rotateImage(String filePath){
		Matrix matrix = new Matrix();
		try{
			ExifInterface exifReader = new ExifInterface(filePath);
			int orientation = exifReader.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
	
			if (orientation ==ExifInterface.ORIENTATION_NORMAL) {
			      // Do nothing. The original image is fine.
			} else if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
			       matrix.postRotate(90);
			} else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
			       matrix.postRotate(180);
			} else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
			      matrix.postRotate(270);
			}else if (orientation == 0 ){
				//NEXUS s problem   
				matrix.postRotate(90);
			}
		}catch(IOException e){
			Log.e("vg",e.getMessage() , e);
		}
		return matrix;
	}
	
	
	private File galleryAddPic(String currentPhotoPath) {
		File f = null;    
			try{
				Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
				f = new File(currentPhotoPath);
			    Uri contentUri = Uri.fromFile(f);
			    mediaScanIntent.setData(contentUri);
			    this.sendBroadcast(mediaScanIntent);
		    }catch(Exception e){
		    	Log.e("vg",e.getMessage() , e);
		    }
		    return f;
	}
	
	
	private void initialUserPrefVars() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		mColor = preferences.getString("vg.textColor",mColor);
		mTextSize = preferences.getInt("vg.textSize",mTextSize);
		mNumOfMsgToDisplay = preferences.getInt("vg.numberOfMsgToDisplay",mNumOfMsgToDisplay);
		
		mServerUrl = preferences.getString("vg.url", "");
		mEntityId = preferences.getString("vg.entity", "");
		mCisId = preferences.getString("vg.cis", "");
		mInteractiveBackgroundActive = preferences.getBoolean("vg.interactiveBackgroundActive", false);
	}

	private synchronized void getMessagesTask() {
		if (mScanTaskActive){
			return;
		}
		
		final Handler handler = new Handler();
		Timer t = new Timer();
		mScanTask = new TimerTask() {
	        public void run() {
	                handler.post(new Runnable() {
	                        public void run() {
	                        	try{
	                        		getMessages();
	                        	}catch (Exception e) {
	                        		 Log.e("vg", e.getMessage()+ " ; cause: "+e.getCause(), e);
								}
	                        }
	               });
	        }};
		
	        t.scheduleAtFixedRate(mScanTask, 300,DEFAULT_MSG_UPDATE_INTERVAL);

	   synchronized (this) {
		   mScanTaskActive = true;
	   }
	}


	private synchronized void createTextViewsMsgs(int number){
		
		LinearLayout messagesContainer = (LinearLayout) findViewById(R.id.messagesContainer);
		messagesContainer.removeAllViewsInLayout();
		TextView textView;
		for (int i=0; i < number ; i++){
			textView = new TextView(context);
			textView.setTypeface(mTextFont);
			textView.setVisibility(TextView.INVISIBLE);
			messagesContainer.addView(textView);
		}
		messagesContainer.bringToFront();
		displayText();
	}
	
	/*
	private void hideMessagesContainer(){
		LinearLayout messagesLayoutContainer = (LinearLayout) findViewById(R.id.messagesContainer);
		for (int i=0; i < messagesLayoutContainer.getChildCount() ; i++){
			TextView view = (TextView)messagesLayoutContainer.getChildAt(i);
			view.setVisibility(View.INVISIBLE);
		}
	}*/
	
	
	private synchronized void setTopGravityMsgContainer(){
		LinearLayout messagesLayoutContainer = (LinearLayout) findViewById(R.id.messagesContainer);
		messagesLayoutContainer.setGravity(Gravity.TOP);
	}
	
	
	public synchronized void displayText() {
		LinearLayout messagesLayoutContainer = (LinearLayout) findViewById(R.id.messagesContainer);
		setTopGravityMsgContainer();
		
		int iterateOver;
		JSONObject singleMessage;
		try{
			iterateOver = Math.min(mMsgInClient.size(),mNumOfMsgToDisplay);
			Iterator<JSONObject> iterator = mMsgInClient.descendingIterator();
			
			int index=0;
			String delimiter = "\\.";
			while (iterator.hasNext() && index < iterateOver){
				singleMessage = iterator.next();
				TextView tv =(TextView) messagesLayoutContainer.getChildAt(messagesLayoutContainer.getChildCount()-index-1);
				
				String userId = (String)singleMessage.get("userId");
				String[] splitedUserId = userId.split(delimiter);
				
				tv.setText(splitedUserId[0]+": "+singleMessage.get("msg"));
				try{
					tv.setTextColor(Color.parseColor((String)singleMessage.get("style")));
				}catch (Exception e) {
					tv.setTextColor(Color.BLACK);
					Log.e("vg", "can't parse color '"+(String)singleMessage.get("style")+"' ; error msg "+e.getMessage(), e);
				}
				tv.setTextSize(mTextSize);
				tv.setVisibility(View.VISIBLE);
				//Integer currentMsgId =  (Integer) singleMessage.get("messageId");
				//lastProcessedMsgId = Math.max(lastProcessedMsgId, currentMsgId);
				index++;
			}
			
			while (index < messagesLayoutContainer.getChildCount()){
				TextView tv =(TextView) messagesLayoutContainer.getChildAt(messagesLayoutContainer.getChildCount()-index-1);
				tv.setVisibility(View.GONE);
				index++;
			}
			
		}catch (JSONException e) {
			Log.e("vg", "JSONException ; msg= "+e.getMessage(), e);
		}
		messagesLayoutContainer.bringToFront();
		
	}
	
	
	private void dialogOnClickAction(Dialog dialog){
		
		boolean paramsHaveChanged = false;
		String tempParamStr;
		int tempParamInt;
		try{
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
			
			EditText textColorEdit = (EditText) dialog.findViewById(R.id.textColor);
			tempParamStr = textColorEdit.getText().toString();
			if (tempParamStr != null && tempParamStr != null &&!tempParamStr.equals(mColor)){
				mColor = textColorEdit.getText().toString();
				paramsHaveChanged = true;
			}
			
			EditText textSizeEdit = (EditText) dialog.findViewById(R.id.textSize);
			tempParamInt =Integer.parseInt(textSizeEdit.getText().toString()); 
			if ( tempParamInt != mTextSize && tempParamInt > 0 ){
				mTextSize = tempParamInt;
				paramsHaveChanged = true;
			}
				
			EditText numMsgEdit = (EditText) dialog.findViewById(R.id.numOfMsg);
			tempParamInt =  Integer.parseInt(numMsgEdit.getText().toString());
			if (tempParamInt !=  mNumOfMsgToDisplay && mNumOfMsgToDisplay > 0){
				mNumOfMsgToDisplay = tempParamInt;
				paramsHaveChanged = true;
			}
			
			RadioButton btn = (RadioButton)dialog.findViewById(R.id.radioYes);
			if (btn.isChecked() && !mInteractiveBackgroundActive){
				mInteractiveBackgroundActive = true;
				enableInteractiveBackground();
				paramsHaveChanged = true;
			}
			
			btn = (RadioButton)dialog.findViewById(R.id.radioNo);
			if (btn.isChecked() && mInteractiveBackgroundActive){
				mInteractiveBackgroundActive = false;
				disableInteractiveBackground();
				paramsHaveChanged = true;
			}
			
			
			if (paramsHaveChanged){
				createTextViewsMsgs(mNumOfMsgToDisplay);
				
				SharedPreferences.Editor editor = preferences.edit();
				editor.putString("vg.textColor", mColor);
				editor.putInt("vg.numberOfMsgToDisplay", mNumOfMsgToDisplay);
				editor.putInt("vg.textSize", mTextSize);
				editor.putBoolean("vg.interactiveBackgroundActive", mInteractiveBackgroundActive);
				editor.commit();
			}
				
		}catch (Exception e) {
			 Log.e("vg", e.getMessage()+ " ; cause: "+e.getCause(), e);
		}

		dialog.dismiss();
		setTopGravityMsgContainer();
	}
	
	private void createPreferencesDialog(){
		// custom dialog
		final Dialog dialog = new Dialog(context);
		dialog.setContentView(R.layout.dialog);
		dialog.setTitle("Preferences");
					
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		 
		EditText textColorEdit = (EditText) dialog.findViewById(R.id.textColor);
		mColor = preferences.getString("vg.textColor",mColor);
		textColorEdit.setText(mColor);
				
		EditText textSizeEdit = (EditText) dialog.findViewById(R.id.textSize);
		mTextSize = preferences.getInt("vg.textSize",mTextSize);
		textSizeEdit.setText(String.valueOf(mTextSize));
					
		EditText numMsgEdit = (EditText) dialog.findViewById(R.id.numOfMsg);
		mNumOfMsgToDisplay = preferences.getInt("vg.numberOfMsgToDisplay",mNumOfMsgToDisplay);
		numMsgEdit.setText(String.valueOf(mNumOfMsgToDisplay));
		
		RadioButton btn = (RadioButton)dialog.findViewById(R.id.radioYes);
		btn.setChecked(mInteractiveBackgroundActive);
		btn = (RadioButton)dialog.findViewById(R.id.radioNo);
		btn.setChecked(!mInteractiveBackgroundActive);
		
		Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
		// if button is clicked, close the custom dialog
		dialogButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialogOnClickAction(dialog);
			}
		});

		dialog.show();
	}
	
	
	/* Backup
	public void displayText(JSONObject message) {
		FrameLayout rl = new FrameLayout(this.getApplicationContext());
		if (message!=null){
			TextView tv =(TextView) findViewById(R.id.messages);
						
			try {
				tv.setText(message.get("userId")+": "+message.get("msg"));
				tv.setTextColor(Color.parseColor((String)message.get(color)));
				tv.setTextSize(textSize);
			} catch (Exception e) {
				 
				Log.e("error", e.getMessage()+ " ; cause: "+e.getCause(), e);
			}
	
			//tv.setGravity(Gravity.CENTER_HORIZONTAL);
		}
	}*/

		
	private void downloadNewBackgroundImage(String url, String imageName) {
		(new DownloadUploadHelper()).downloadImage(url, imageName,this);
		
	}

	private void getMessages( )  {
		
		 new AsyncTask<String, Void,JSONArray> (){
			 Throwable error = null;

			 String url;
			 String thisEntityId;
			 String cis;
			 String imageNameServer;
			 
			 @Override
			protected void onPostExecute(JSONArray result) {
				
				 Log.i("vg", "getMessages - onPostExecute");
				
				try {
					if (error != null){
						Toast.makeText(getBaseContext(), "connection to server was lost", Toast.LENGTH_SHORT).show();
						Log.e("vg", "showing error msg - getMessages - onPostExecute ",error);
						return;
					}

					
					JSONObject lastMsgObject = null;
					int currentMsgId;
						
					synchronized (mMsgInClient) {		
							
						mMsgInClient.clear();
						boolean alertOnNewMessagePresented = false;
						String postedBy;
						if (result != null){
							for (int i=0; i < result.length(); i++){
								lastMsgObject = (JSONObject) result.get(i);
								currentMsgId = lastMsgObject.getInt("messageId");
								postedBy = lastMsgObject.getString("userId");
								mMsgInClient.addLast(lastMsgObject);
								if (!mMsgIdsInClient.contains(currentMsgId)  ){
									mMsgIdsInClient.add(currentMsgId);
									if (!postedBy.equals(thisEntityId)){
										alertOnNewMessagePresented = true;
									}
								}
							}
						}
						displayText();
						
						if (alertOnNewMessagePresented){
							playSoundAlert();	
						}
					}
						
					
					if (shouldUpdateBackgroundImage(imageNameServer,getLocalBackgroundImgName() )){
						ImageDetails imageDetails = null;
						synchronized (mBackgroundImagesNames) {
							imageDetails = mBackgroundImagesNames.get(imageNameServer);
						}
						
						if (imageDetails != null){
							//image exists in file System
							displayBackgroundImage(imageNameServer, imageDetails.imagePath, !imageDetails.downloaded, imageDetails.downloaded);
						}else{
							//need to download
							downloadNewBackgroundImage(url,imageNameServer);	
						}
		            }
					
					if (shouldHideBackgroundImage(imageNameServer,getLocalBackgroundImgName())){
		            	mBackGroundImageView.setImageResource(R.drawable.black);
						
		            	mBackgroundImgName = "";
		        		/*mCustomCameraView.onPauseMySurfaceView();
		        		mCustomCameraView.onResumeMySurfaceView();
		        		mCustomCameraView.setVisibility(View.VISIBLE);
		        		*/
		        		
		            }
					
				} catch (Exception e) {
					 Log.e("vg", e.getMessage()+ " ; cause: "+e.getCause(), e);
				};
				
			  }
			
			

			private boolean shouldHideBackgroundImage(String imageNameServer,String localImageName) {
				if ( imageNameServer.length() == 0 && System.currentTimeMillis() - mBackgroundImageTakenTS > (1000*10)){
					return true;
				}else if (imageNameServer.length() > 0 && !imageNameServer.equals(localImageName)){
					return true;
				}
				return false;
			}

			private boolean shouldUpdateBackgroundImage(String imageNameFromServer, String localImage){
				 if (imageNameFromServer.length() > 0 && !imageNameFromServer.equals(localImage)){
					 Log.i("vg","local image name != image name on server; local: " + localImage +"  server: "+imageNameFromServer);
					 if (System.currentTimeMillis() - mBackgroundImageTakenTS < (1000*15)){
						 Log.i("vg","file was just recently updated in the client side, waiting 15 seconds to let it be upated on the server");
						 return false;
					 }else{
						 Log.i("vg","need to download img " + imageNameFromServer + " from the server, there might be a tasking already running");
						 return true;
					 }
					 
				 }else if (imageNameFromServer.length() == 0){
					 Log.i("vg","imageNameFromServer is empty - no need to perform update");
					 return false;
				 }else{
					 Log.i("vg","local image name equals image name on the server ("+imageNameFromServer +") - no need to perform update");
					 return false;
				 }
			 }
			 
			@Override
			protected JSONArray doInBackground(String... paramters) {
				
					JSONArray result = null;
					url = (String) paramters[0];
					thisEntityId = (String) paramters[1];
					cis = (String)paramters[2];
					
					Log.d("vg", "on getMessages - url= "+url +" ; entity= "+thisEntityId+" ; cis= "+cis);

					//CIS might have white spaces 
					try {
						cis = URLEncoder.encode(cis,"utf-8");
					} catch (UnsupportedEncodingException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					String request = url + "/getMsg.html?userID="+thisEntityId+"&cis="+cis+"&number="+mLastProcessedMsgId;
					HttpGet httpGet = new HttpGet(request);
					
					BufferedReader in = null;
					try{
						HttpClient client = new DefaultHttpClient();
						HttpResponse response = client.execute(httpGet);
						in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			            String jsonBuffer = "";
			            String line = "";
			        
			            while ((line = in.readLine()) != null) {
			                jsonBuffer+= line;
			            }
			            in.close();
			            Log.d("vg", jsonBuffer.toString());
			            
			            JSONObject jsonObject = new JSONObject(jsonBuffer);
			            
			            JSONArray ja = null;
			            if (jsonObject.getString("data") != null && jsonObject.getString("data").length() > 3){
			            	ja =  new JSONArray(jsonObject.getString("data"));
			            	imageNameServer = jsonObject.getString("imgName");	
			            }else{
			            	imageNameServer = "";
			            }
			            
			            result =ja;
			            
					}  catch (Exception e) {
						//if no connection - toast no connection..
						Log.e("vg", "error on getMessages ; "+e.getMessage()+ " ; cause: "+e.getCause(), e);
						error = e;
					}  finally {
			            if (in != null) {
			                try {
			                    in.close();
			                } catch (IOException e) {};
			            }
					}
				return result;
			
		        
			}}.execute(mServerUrl,mEntityId, mCisId);
		
	}
	
	
	private void playSoundAlert(){
		try{
			Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			v.vibrate(600);
		
	        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
	        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
	        r.play();
	    } catch (Exception e) {
	    	Log.e("vg", "Error while attempting to play sound and vibrate upon new message",e);
	    }
	}
	
	private void postMessages(String sprayText, String msgDest)  {
		
		 new AsyncTask<String, Void,JSONObject> (){
			 Throwable error = null;
			 
			 @Override
			protected void onPostExecute(JSONObject result) {
				 Log.i("tag", "in postMessages -  onPostExecute");
				 if (error != null){
					 Toast.makeText(getBaseContext(), "Server Error while posting message", Toast.LENGTH_SHORT).show();
					 Log.e("vg", "showing error msg on postMessages ; ", error);
				 }
			 }
			@Override
			protected JSONObject doInBackground(String... paramters) {
					String url = (String) paramters[0];
					String entity = (String) paramters[1];
					String sprayText = (String) paramters[2];
					String cis =  (String) paramters[3];
					String msgDest =  (String) paramters[4];
					
					Log.v("vg", "on postMessage - url= "+url +" ; entity= "+entity+" ; cis= "+cis +" ; style= "+mColor+" ; msg= "+sprayText);
		
					String post = url+"/postMsg1.html";
					HttpPost httpPost = new HttpPost(post);
					try{
						HttpClient client = new DefaultHttpClient();
						List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
						nameValuePairs.add(new BasicNameValuePair("cisBox",cis));
						nameValuePairs.add(new BasicNameValuePair("userId",entity));
						nameValuePairs.add(new BasicNameValuePair("style",mColor));
						nameValuePairs.add(new BasicNameValuePair("msg",sprayText));
						nameValuePairs.add(new BasicNameValuePair("msgDest",msgDest));
						httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
						client.execute(httpPost);
											
					}catch (Exception e) {
						Log.e("vg", e.getMessage()+ " ; cause: "+e.getCause(), e);
						error = e;
					}
					return null;

			}}.execute(mServerUrl,mEntityId,sprayText,mCisId,msgDest);
	}
	
	
	@Override
	public void onPause() {
		try{
			super.onPause();  // Always call the superclass method first
		   
		   Log.i("vg","onPause called");
		   
			   if (mCustomCameraView!= null){
				   mCustomCameraView.onPauseMySurfaceView();
			   }
			      /*
			   synchronized (this) {
					
				   	scanTaskActive = false;
					  if (scanTask != null){
						  scanTask.cancel();
					  }
			   }*/
		}catch(Exception e){
			Log.e("vg", "exception in pause");
		}
		
	}
	
	
	@Override
	public void onStop(){
		super.onStop();
		Log.i("vg","onStop called");
		mScanTaskActive = false;
		  if (mScanTask != null){
			  mScanTask.cancel();
		  }
	}
	
	@Override
	public void onResume() {
	    super.onResume();  // Always call the superclass method first
	    
	    Log.i("vg","onResume called");
		    if (mCustomCameraView!= null){
		    	mCustomCameraView.onResumeMySurfaceView();
		    }
			getMessagesTask();
	}
	
	
	public String getLocalBackgroundImgName(){
		return mBackgroundImgName;
	}
	
	
}
