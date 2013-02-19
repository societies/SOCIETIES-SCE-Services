package com.ibm.hrl.ms.pz;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;

public class CustomCameraView  extends SurfaceView{
	private SurfaceHolder previewHolder;
	
	private Camera camera= null;
	private boolean surfaceCreated = false;
	private ImageView imageView; 
	
	SurfaceHolder.Callback surfaceHolderListener = new SurfaceHolder.Callback() {
	     

		public void surfaceCreated(SurfaceHolder holder) {
				surfaceCreated = true;
				openCamera();
			}
	   public void surfaceChanged(SurfaceHolder holder, int format, int w,int h) {
	     try{
	    	 /*   //This throws exception from some reason
			  Parameters params = camera.getParameters();
		      params.setPreviewSize(w, h);
		      params.setPictureFormat(PixelFormat.JPEG);
		      camera.setParameters(params);
		      */
		      camera.startPreview();
	     }catch (Exception e) {
	    	 Log.e("error", e.getMessage()+ " ; cause: "+e.getCause(), e);
		}
	   }
	   
	   
	   public void surfaceDestroyed(SurfaceHolder holder) {
	   }
	   
	 };
	 /*
	 @Override
	 public void onDraw(Canvas canvas) {

	             Paint paint = new Paint();
	             paint.setStyle(Paint.Style.FILL_AND_STROKE);
	             paint.setStrokeWidth(3);
	             paint.setAntiAlias(true);
	             paint.setColor(Color.BLUE);
	             canvas.drawRect(0, 0, this.getWidth(), this.getHeight(), paint);

	             
	     if (hasData) {

	         resetColor();
	         try {

	             canvas.drawColor(getResources().getColor(R.color.graphbg_color));

	             graphDraw(canvas);
	         } catch (ValicException ex) {

	         }

	     }
	 }*/
	 
	 
	 private void init(){
		 try{
			 previewHolder = this.getHolder();
			 previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			 previewHolder.addCallback(surfaceHolderListener);
			 
			 
			 
		 }catch (Exception e) {
			 Log.e("error", e.getMessage()+ " ; cause: "+e.getCause(), e);
		}
	 }
	public CustomCameraView(Context ctx){
	   super(ctx);
	  init();
	}
	
     public CustomCameraView(Context context, AttributeSet attrs) {
         super(context, attrs);
         init();
     }
     public CustomCameraView(Context context, AttributeSet attrs, int defStyle) {
         super(context, attrs, defStyle);
         init();
     }
		
	 public void onResumeMySurfaceView(){
		 openCamera();
	 }
		   
	 public void onPauseMySurfaceView(){
		 releaseCamera();
	 }
	
	 private void releaseCamera(){
		 try{
			 if (camera!= null){
				 camera.stopPreview();
				 camera.release();
				 Log.d("debug","Stopping camaera on pause");
			 }
		 }catch (Exception e) {
			   Log.e("error", e.getMessage()+ " ; cause: "+e.getCause(), e);
		 }
	 }
	 
	 private void openCamera(){
		 try{
			 if (surfaceCreated == false){
				 return;
			 }
			 camera=Camera.open();
			 camera.setPreviewDisplay(previewHolder);
			 camera.startPreview();
			 camera.setDisplayOrientation(90);
			 Log.d("debug","starting camera on resume");
		 }catch (Exception e) {
			   Log.e("error", e.getMessage()+ " ; cause: "+e.getCause(), e);
		 }
	 }
	 
}
