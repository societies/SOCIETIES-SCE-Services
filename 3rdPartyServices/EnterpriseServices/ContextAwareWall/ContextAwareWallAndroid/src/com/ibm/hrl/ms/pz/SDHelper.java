package com.ibm.hrl.ms.pz;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;
import android.util.Log;

public class SDHelper {

	public static final String JPEG_FILE_PREFIX = "IMG_";
	public static final String JPEG_FILE_SUFFIX = ".jpg";
	// Standard storage location for digital camera files
	public static final String CAMERA_DIR = "/dcim/";
	
	
	public static File createImageFile(String imageFileName) throws IOException {
		// Create an image file name
		File albumF = getAlbumDir();
		File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
		return imageF;
	}
	
	
	public static String generateImageName(){
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = JPEG_FILE_PREFIX + timeStamp;
		return imageFileName;
	}
	
	
	public static File getAlbumDir() {
		File storageDir = null;

		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			
			storageDir = new File (Environment.getExternalStorageDirectory()+ CAMERA_DIR+ "VG");

			if (storageDir != null) {
				if (! storageDir.mkdirs()) {
					if (! storageDir.exists()){
						Log.d("vg", "failed to create directory");
						return null;
					}
				}
			}
			
		} else {
			Log.v("vg", "External storage is not mounted READ/WRITE.");
		}
		
		return storageDir;
	}
	
	
}
