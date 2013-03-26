package com.ibm.hrl.ms.pz;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
			
			storageDir = new File (getAlbumFolderPath());

			if (storageDir != null) {
				if (! storageDir.mkdirs()) {
					if (! storageDir.exists()){
						Log.d("vg", "failed to create directory");
						return null;
					}
				}
			}
			
		} else {
			Log.i("vg", "External storage is not mounted READ/WRITE.");
		}
		
		return storageDir;
	}
	
	private static String getAlbumFolderPath(){
		String folder = "";
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			
			folder = Environment.getExternalStorageDirectory()+ CAMERA_DIR+ "VG";
		}
		return folder;
	}
	
	
	public static Map<String,ImageDetails> readFileNamesInVgGallery(){
		Map<String,ImageDetails> filesMap = new HashMap<String,ImageDetails>();
		try{
			int kb = 1024;
			File folder = getAlbumDir();
			File[] files = folder.listFiles();
			if (files != null){
				for (File fileObject : files){
					String fileNameNoPrefix = fileObject.getName().split("\\.")[0];
					fileNameNoPrefix = fileNameNoPrefix.split("\\-")[0];
					boolean flag = true;
					if (fileObject.length() > (70*kb)){
						flag = false;
					}
					if (fileNameNoPrefix != null && fileObject.length() >0)
					filesMap.put(fileNameNoPrefix, new ImageDetails(fileNameNoPrefix, fileObject.getAbsolutePath(),flag));
				}
			}
		}catch(Exception e){
			Log.e("vg", "error while reading files list from VG gallery folder",e);
		}
		
		return filesMap;
	}
	
	
}
