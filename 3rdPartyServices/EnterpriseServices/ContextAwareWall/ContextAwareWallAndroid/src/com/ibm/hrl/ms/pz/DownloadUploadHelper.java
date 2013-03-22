package com.ibm.hrl.ms.pz;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

public class DownloadUploadHelper {

	private static ReentrantLock downloadLock = new ReentrantLock();
	private static ReentrantLock updateImageLock = new ReentrantLock();
	private static AtomicBoolean updateStarted = new AtomicBoolean(); 
	
	public void uploadImage(File data, String fileName,String cis,String url){
		 (new UploadFilesTask(data,fileName,cis)).execute(url+"/upload.html");
	}
	
	public void downloadImage(String url, String downloadedImageName, CameraPreview callback){
		 (new DownloadFileTask(callback,downloadLock,updateImageLock)).execute(url+"/download.html",downloadedImageName);
	}
	
	
	class UploadFilesTask extends AsyncTask<String, Void,String>{
		 Throwable error = null;
		 File file;
		 String fileName;
		 String cis;
		 
		 UploadFilesTask(File file,String fileName,String cis){
			 this.file = file;
			 this.fileName = fileName;
			 this.cis = cis;
		 }
		
		@Override
		protected void onPostExecute(String result) {
			
		 }
		@Override
		protected String doInBackground(String... params) {
			String url = params[0];
			//postImageInternal(file,fileName,url);
			upload(this.file, this.fileName,this.cis,url);
			return null;
		}
		
	}
	
	
	private byte[] decodeFile(File f){
		byte[] byteArray= null;
		try {
	        //Decode image size
	        BitmapFactory.Options o = new BitmapFactory.Options();
	        o.inJustDecodeBounds = true;
	        BitmapFactory.decodeStream(new FileInputStream(f),null,o);

	        //Decode with inSampleSize
	        BitmapFactory.Options o2 = new BitmapFactory.Options();
	        o2.inSampleSize=16;
	        Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
	        
	        ByteArrayOutputStream stream = new ByteArrayOutputStream();
	        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
	        byteArray = stream.toByteArray();
	        
	    } catch (FileNotFoundException e) {
	    	e.printStackTrace();
	    }
		return byteArray;
	    
	}
	
	class DownloadFileTask extends AsyncTask<String, Void,String>{
		 Throwable error = null;
		String imagePath;
		CameraPreview callback;
		String imageName;
		Throwable exception;
		ReentrantLock downloadLock;
		ReentrantLock updateImageLock;
		
		public DownloadFileTask(CameraPreview callback,ReentrantLock downloadLock,ReentrantLock updateImageLock ){
			this.callback = callback;
			this.downloadLock = downloadLock;
			this.updateImageLock = updateImageLock;
		}
		
		@Override
		protected void onPostExecute(String result) {
			
			try{
				updateImageLock.lock();
				if (callback != null && result != null && result.length() > 0 && exception == null && !callback.getLocalBackgroundImgName().equals(imageName)){
					if (imagePath != null && imagePath.length() > 0 ){
						callback.displayBackgroundImage(imageName,imagePath,false,true);
					}
				}
			}finally{
				updateStarted.set(false);
				updateImageLock.unlock();
			}
			
			
		 }
		@Override
		protected String doInBackground(String... params) {
			try{
				downloadLock.lock();
				
				if (updateStarted.get()){
					return "";
				}
				
				updateStarted.set(true);
				
				String url = params[0];
				imageName = params[1];
				try {
					if (callback != null && !callback.getLocalBackgroundImgName().equals(imageName)){
						imagePath = download(url,imageName);
					}
				} catch (IOException e) {
					this.exception = e;
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				downloadLock.unlock();
			}	
			
			return imagePath;
		}

		
		
	}
	
	
	
	
	
	
	private String download(String urlString, String fileName) throws IOException {
		FileOutputStream fos = null;
		String imagePath=null;
	       try {
	              
	               URL url = new URL(urlString+ "?fileName="+fileName); //you can write here any link
	           
	               long startTime = System.currentTimeMillis();
	               Log.i("vg", "download begining");
	               Log.i("vg", "download url:" + url);
	               Log.i("vg", "downloaded file name:" + fileName);

	               /* Open a connection to that URL. */
	               URLConnection ucon = url.openConnection();

	               /*
	                * Define InputStreams to read from the URLConnection.
	                */
	               InputStream is = ucon.getInputStream();
	               BufferedInputStream bis = new BufferedInputStream(is);

	               /*
	                * Read bytes to the Buffer until there is nothing more to read(-1).
	                */
	               ByteArrayBuffer baf = new ByteArrayBuffer(5000);
	               int current = 0;
	               while ((current = bis.read()) != -1) {
	                  baf.append((byte) current);
	               }
	               
	               File albumF = SDHelper.getAlbumDir();
	       		   File imageF = File.createTempFile(fileName, SDHelper.JPEG_FILE_SUFFIX, albumF);
	       		   
	       		   /* Convert the Bytes read to a String. */
	               fos = new FileOutputStream(imageF);
	               byte[] byteArr = baf.toByteArray();
	               fos.write(byteArr);
	               fos.flush();
	               fos.close();
	       		   
	               Log.i("vg", "download ready in" + ((System.currentTimeMillis() - startTime) / 1000) + " sec");
	               if (byteArr.length > 0){ 
	            	   imagePath = imageF.getAbsolutePath();
		               Log.i("vg", "download size" + byteArr.length +" bytes");
	               }else{
	            	   Log.w("vg", "an empty image was downloaded to server; image name "+fileName);
	               }
	               

	       } catch (IOException e) {
	           Log.e("DownloadManager", "Error: ",e);
	           throw e;
	       }
	       return imagePath;
		    
		
	}
	
	
	
	private void postImageInternal(File data, String fileName, String url) {

	    try{
		    HttpClient httpClient = new DefaultHttpClient();
		    
		    //httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.RFC_2109);
	
		    HttpPost httpPost = new HttpPost(url);
		    HttpResponse response = null;
	
		    FileEntity tmp = new FileEntity(data,"UTF-8");
		    httpPost.setEntity(tmp);
	
		    try {
		        response = httpClient.execute(httpPost);
		    } catch (ClientProtocolException e) {
		        System.out.println("HTTPHelp : ClientProtocolException : "+e);
		    } catch (IOException e) {
		        System.out.println("HTTPHelp : IOException : "+e);
		    } 
		    System.out.println(response.getStatusLine().toString());
	    }catch (Exception e){
	    	e.printStackTrace();
	    }

	            
	}
	
    private void upload(File data, String fileName,String cis, String urlServer){
    	HttpURLConnection connection = null;
    	DataOutputStream outputStream = null;
    	//DataInputStream inputStream = null;

    	//String pathToOurFile = "/mnt/sdcard/media/audio/notifications/preview.mp3";
    	//String urlServer = "http://ta-proj02.haifa.ibm.com:9081/RecieverWeb/FileReceiver";
    	String lineEnd = "\r\n";
    	String twoHyphens = "--";
    	String boundary =  "*****";

    	int bytesRead, bytesAvailable, bufferSize;
    	byte[] buffer;
    	int maxBufferSize = 1*1024*1024;

    	try
    	{
    	
    	//FileInputStream fileInputStream = new FileInputStream(new File(pathToOurFile) );
    	FileInputStream fileInputStream = new FileInputStream(data);

    	try {
			cis = URLEncoder.encode(cis,"utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
    	
    	URL url = new URL(urlServer +"?cis="+cis);
    	connection = (HttpURLConnection) url.openConnection();
    
    	// Allow Inputs & Outputs
    	connection.setDoInput(true);
    	connection.setDoOutput(true);
    	connection.setUseCaches(false);

    	// Enable POST method
    	connection.setRequestMethod("POST");

    	connection.setRequestProperty("Connection", "Keep-Alive");
    	connection.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
    
    	outputStream = new DataOutputStream( connection.getOutputStream() );
    
    	outputStream.writeBytes(twoHyphens + boundary + lineEnd);
    
    	
    	outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + fileName +"\"" + lineEnd);
    	outputStream.writeBytes(lineEnd);
    
    	
    	/*
    	bytesAvailable = fileInputStream.available();
    	bufferSize = Math.min(bytesAvailable, maxBufferSize);
    	buffer = new byte[bufferSize];
    	*/
    	
    	buffer = decodeFile(data); 
    	bufferSize = Math.min(buffer.length, maxBufferSize);
    	bytesAvailable  = buffer.length; 
    	
    	outputStream.write(buffer, 0, buffer.length);
    	
    /*
    	// Read file
    	bytesRead = fileInputStream.read(buffer, 0, bufferSize);
    	while (bytesRead > 0)  	{
	    	outputStream.write(buffer, 0, bufferSize);
	    	bytesAvailable = fileInputStream.available();
	    	bufferSize = Math.min(bytesAvailable, maxBufferSize);
	    	bytesRead = fileInputStream.read(buffer, 0, bufferSize);
    	}
    */
    	outputStream.writeBytes(lineEnd);
    	outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

    	// Responses from the server (code and message)
    	int serverResponseCode = connection.getResponseCode();
    	String serverResponseMessage = connection.getResponseMessage();
    	//Toast.makeText(getBaseContext(), serverResponseMessage, Toast.LENGTH_LONG).show();
    	fileInputStream.close();
    	outputStream.flush();
    	outputStream.close();
    	}
    	catch (Exception ex)
    	{
    		System.out.println(ex);
    		//Toast.makeText(getBaseContext(), "exception!"+ex.getMessage(), Toast.LENGTH_LONG).show();
    	//Exception handling
    	}
    }

}
