package com.trible.scontact.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;


public class ImageUtil {

	public static boolean inRangeOfView(View view, MotionEvent ev){
		int[] location = new int[2];
		view.getLocationOnScreen(location);
		int x = location[0];
		int y = location[1];
		if(ev.getX() < x || ev.getX() > (x + view.getWidth()) || ev.getY() < y || ev.getY() > (y + view.getHeight())){
		return false;
		}
		return true;
	}
	/**
	 * 
	 * @param c
	 * @param resId image res id
	 * @param p the percentage of device width
	 * @return a bitmap is scale to a width that p = bimap.width/device.width
	 */
	public static Bitmap getScaleImageByScaleOfWinWidth(Context c ,int resId,float p){
		if ( resId == 0)return null;
		Drawable d = c.getResources().getDrawable(resId);
		if ( d == null)return null;
		BitmapDrawable defaultImg = (BitmapDrawable)(d);
		Bitmap bmp = defaultImg.getBitmap();
	
		return getScaleImageByScaleOfWinWidth(c,bmp,p);
	}
	private static int calInSampleSize (Context c,Options opt,float p){
		DisplayMetrics mDisplayMetrics = new DisplayMetrics();
		((Activity)c).getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
		int h = opt.outHeight;
		int w = opt.outWidth;
		int inSampleSize = 1;
		int reqW = (int) (mDisplayMetrics.widthPixels * p);
		int reqH = (int) (reqW * ((double)h / w));
		
		if (reqH < h || reqW < w){
	        if (w > h) {  
	            inSampleSize = Math.round((float)h / (float)reqH);  
	        } else {  
	            inSampleSize = Math.round((float)w / (float)reqW);  
	        }
		}
		return inSampleSize;
	}
	/**
	 * 
	 * @param drawable the drawable to convert
	 * @return a bitmap converted by a drawable
	 */
	public static Bitmap convertDrawable2BitmapByCanvas(Drawable drawable) {
		Bitmap bitmap = Bitmap
		.createBitmap(
		drawable.getIntrinsicWidth(),
		drawable.getIntrinsicHeight(),
		drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
		: Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		// canvas.setBitmap(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
		drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}
	
	/**
	 * 
	 * @param c
	 * @param bm image res
	 * @param scaleOfWinWidth the percentage of device width
	 * @return a bitmap is scale to a width that scaleOfWinWidth = bimap.width/device.width
	 */
	public static Bitmap getScaleImageByScaleOfWinWidth(Context c ,Bitmap bm,float scaleOfWinWidth){
		if (bm == null)return null;
		DisplayMetrics mDisplayMetrics = new DisplayMetrics();
		((Activity)c).getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
		Bitmap bmp = bm;
		int bmpW = bmp.getWidth();
		int bmpH = bmp.getHeight();
		float scaleW = 0,scaleH;
		int imgVisualW = (int) (mDisplayMetrics.widthPixels * scaleOfWinWidth);
		int imgVisualH = (int) (imgVisualW * ((double)bmpH / bmpW));
		scaleW = ((float)imgVisualW / bmpW);
		scaleH = ((float)imgVisualH / bmpH);
		
		Matrix m = new Matrix();
		m.postScale(scaleW, scaleH);
		
		Bitmap newBitMap = Bitmap.createBitmap(bmp, 0, 0, bmpW, bmpH, m, true);
		bmp.recycle();
		return newBitMap;
	}
	
	/**
	 * 
	 * @param c
	 * @param bm image res
	 * @param scaleOfWinWidth the percentage of device width
	 * @param scaleOfWinHeight the percentage of device height
	 * @return a bitmap is scale to a bitmap which its width that 
	 * scaleOfWinWidth = bimap.width/device.width and its height that scaleOfWinHeight = bimap.height/device.height
	 */
	public static Bitmap getScaleImageByScaleOfWinWidthAndHeight(Context c ,Bitmap bm,float scaleOfWinWidth,float scaleOfWinHeight){
		if (bm == null)return null;
		DisplayMetrics mDisplayMetrics = new DisplayMetrics();
		((Activity)c).getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
		Bitmap bmp = bm;
		int bmpW = bmp.getWidth();
		int bmpH = bmp.getHeight();
		float scaleW = 0,scaleH;
		int imgVisualW = (int) (mDisplayMetrics.widthPixels * scaleOfWinWidth);
		int imgVisualH = (int) (mDisplayMetrics.widthPixels * scaleOfWinHeight);
		scaleW = ((float)imgVisualW / bmpW);
		scaleH = ((float)imgVisualH / bmpH);
		
		Matrix m = new Matrix();
		m.postScale(scaleW, scaleH);
		
		Bitmap newBitMap = Bitmap.createBitmap(bmp, 0, 0, bmpW, bmpH, m, true);
		bmp.recycle();
		return newBitMap;
	}
	
	/**
	 * 
	 * @param context
	 * @param input a bitmap which want round corner
	 * @param pixels corner's R
	 * @param w corner's width
	 * @param h corner's height
	 * @param squareTL has top left corner
	 * @param squareTR has top right corner
	 * @param squareBL has bottom left corner
	 * @param squareBR has bottom right corner
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Context context, Bitmap input, int pixels , int w , int h , boolean squareTL, boolean squareTR, boolean squareBL, boolean squareBR  ) {

	    Bitmap output = Bitmap.createBitmap(w, h, Config.ARGB_8888);
	    Canvas canvas = new Canvas(output);
	    final float densityMultiplier = context.getResources().getDisplayMetrics().density;

	    final int color = 0xff424242;
	    final Paint paint = new Paint();
	    final Rect rect = new Rect(0, 0, w, h);
	    final RectF rectF = new RectF(rect);

	    //make sure that our rounded corner is scaled appropriately
	    final float roundPx = pixels*densityMultiplier;

	    paint.setAntiAlias(true);
	    canvas.drawARGB(0, 0, 0, 0);
	    paint.setColor(color);
	    canvas.drawRoundRect(rectF, roundPx, roundPx, paint);


	    //draw rectangles over the corners we want to be square
	    if (squareTL ){
	        canvas.drawRect(0, 0, w/2, h/2, paint);
	    }
	    if (squareTR ){
	        canvas.drawRect(w/2, 0, w, h/2, paint);
	    }
	    if (squareBL ){
	        canvas.drawRect(0, h/2, w/2, h, paint);
	    }
	    if (squareBR ){
	        canvas.drawRect(w/2, h/2, w, h, paint);
	    }

	    paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	    canvas.drawBitmap(input, 0,0, paint);

	    return output;
	}
	
	public static Bitmap getScaleOptionImageByScaleOfWinWidthAndHeight(Context c ,File imgFile,float scaleOfWinWidth,float scaleOfWinHeight){
		if ( imgFile == null )return null;
	    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
	    bmOptions.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(imgFile.getAbsolutePath(), bmOptions);
	    int photoW = bmOptions.outWidth;
	    int photoH = bmOptions.outHeight;
	    if ( photoW == 0 ){
	    	return null;
	    }
        int th = (int) (DeviceUtil.getDeviceHeight(c) * scaleOfWinHeight);
        int tw = (int) (DeviceUtil.getDeviceWidth(c));
	    tw = (int)( tw * scaleOfWinWidth );
	    if ( tw == 0 || th == 0){
	    	return null;
	    }
	    int scaleFactor = Math.max(photoW/tw, photoH/th);
	    bmOptions.inJustDecodeBounds = false;
	    bmOptions.inSampleSize = scaleFactor;
	    bmOptions.inPurgeable = true;
	  
	    Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), bmOptions);
	    return bitmap;
	}
	public static Bitmap getScaleOptionImageByScaleOfWinWidth(Context context,File imgFile,float scaleOfWinWidth){
		if ( imgFile == null )return null;
	    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
	    bmOptions.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(imgFile.getAbsolutePath(), bmOptions);
	    int photoW = bmOptions.outWidth;
	    int photoH = bmOptions.outHeight;
	    if ( photoW == 0 ){
	    	return null;
	    }
        int th = (int) (DeviceUtil.getDeviceHeight(context));
        int tw = (int) (DeviceUtil.getDeviceWidth(context));
	    tw = (int)( tw * scaleOfWinWidth );
	    if ( tw == 0 ){
	    	return null;
	    }
	    th = (int) (tw * ((double)photoH / photoW));
	    int scaleFactor = Math.min(photoW/tw, photoH/th);
	    bmOptions.inJustDecodeBounds = false;
	    bmOptions.inSampleSize = scaleFactor;
	    bmOptions.inPurgeable = true;
	  
	    Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), bmOptions);
	    return bitmap;
	}
//	let a image file to fit ImageView,it depend on ImageView width and height;
	public static  boolean bingOptionImageFileToImageView(ImageView mImageView,File imgFile,int tw,int th) {
	  
	    Bitmap bitmap = getOptionBitmap(imgFile,tw,th);
	    if ( bitmap != null ){
	    	 mImageView.setImageBitmap(bitmap);
	    	 return true;
	    }
	    return false;
	   
	}

	public static  Bitmap getOptionBitmap(File imgFile,int tw,int th) {
		if (imgFile == null || !imgFile.exists() || !imgFile.isFile())return null;
	    // Get the dimensions of the View
//	    int targetW = mImageView.getMeasuredWidth();
//	    int targetH = mImageView.getMeasuredHeight();
	    int targetW = tw;
	    int targetH = th;
	    // Get the dimensions of the bitmap
	    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
	    bmOptions.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(imgFile.getAbsolutePath(), bmOptions);
	    int photoW = bmOptions.outWidth;
	    int photoH = bmOptions.outHeight;
	  
	    // Determine how much to scale down the image
	    int scaleFactor = Math.max(photoW/targetW, photoH/targetH);
	  
	    // Decode the image file into a Bitmap sized to fill the View
	    bmOptions.inJustDecodeBounds = false;
	    bmOptions.inSampleSize = scaleFactor;
	    bmOptions.inPurgeable = true;
	  
	    Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), bmOptions);
	    
	    return bitmap;
	   
	}

	
	public static  Bitmap getOptionBitmapByScaleOfWinWidth(Context context,Uri imgUri,float scaleOfWinWidth) throws FileNotFoundException {

	    if ( imgUri == null )return null;
	    // Get the dimensions of the bitmap
	    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
	    bmOptions.inJustDecodeBounds = true;
	    InputStream is = null;
	    try {
		    is = context.getContentResolver().openInputStream(imgUri);
		    BitmapFactory.decodeStream(is, null, bmOptions);
		} catch (Exception e) {
		} finally {
			IOUtils.closeQuietly(is);
		}

	    int photoW = bmOptions.outWidth;
	    int photoH = bmOptions.outHeight;
	    int tw = (int)( DeviceUtil.getDeviceWidth(context) * scaleOfWinWidth );
	    if ( tw == 0 ){
	    	return null;
	    }
	    int th = (int) (tw * ((double)photoH / photoW));
	    int targetW = tw;
	    int targetH = th;
	    // Determine how much to scale down the image
	    int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
	  
	    // Decode the image file into a Bitmap sized to fill the View
	    bmOptions.inJustDecodeBounds = false;
	    bmOptions.inSampleSize = scaleFactor;
	    bmOptions.inPurgeable = true;
	    InputStream nis = null;
	    Bitmap bitmap = null;
	    try {
	    	nis = context.getContentResolver().openInputStream(imgUri);
	    	bitmap = BitmapFactory.decodeStream(nis, null, bmOptions);
		} catch (Exception e) {
		} finally {
			IOUtils.closeQuietly(nis);
		}
	    
	    return bitmap;
	   
	}
	public static  Bitmap getOptionBitmap(ContentResolver cr,Uri imgUri,int tw,int th) throws FileNotFoundException {
	    int targetW = tw;
	    int targetH = th;
	    if ( imgUri == null )return null;
	    // Get the dimensions of the bitmap
	    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
	    bmOptions.inJustDecodeBounds = true;
	    InputStream is = null;
	    try {
		    is = cr.openInputStream(imgUri);
		    BitmapFactory.decodeStream(is, null, bmOptions);
		} catch (Exception e) {
		} finally {
			IOUtils.closeQuietly(is);
		}
	    
	    int photoW = bmOptions.outWidth;
	    int photoH = bmOptions.outHeight;
	  
	    // Determine how much to scale down the image
	    int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
	  
	    // Decode the image file into a Bitmap sized to fill the View
	    bmOptions.inJustDecodeBounds = false;
	    bmOptions.inSampleSize = scaleFactor;
	    bmOptions.inPurgeable = true;
	    InputStream nis = null;
	    Bitmap bitmap = null;
	    try {
		    nis = cr.openInputStream(imgUri);
		    bitmap = BitmapFactory.decodeStream(nis, null, bmOptions);
		} catch (Exception e) {
		}finally{
			IOUtils.closeQuietly(nis);
		}
	    return bitmap;
	   
	}
//	let a Bitmap to fit ImageView,it depend on ImageView width and height;
	public static  boolean bindBitmapToImageView(ImageView mImageView,Bitmap bm) {
		if ( bm == null || mImageView == null )return false;
	    // Get the dimensions of the View
	    int targetW = mImageView.getWidth();
	    int targetH = mImageView.getHeight();
	  
	    int photoW = bm.getWidth();
	    int photoH = bm.getHeight();
	  
	    // Determine how much to scale down the image
		Matrix m = new Matrix();
		m.postScale((float)targetW / photoW, (float)targetH / photoH);
	    Bitmap bitmap = Bitmap.createBitmap(bm, 0, 0, photoW , photoH , m, true);
	    if ( bm != null ){
	    	bm.recycle();
	    }
	    if ( bitmap != null ){
	    	 mImageView.setImageBitmap(bitmap);
	    	 return true;
	    }
	    return false;
		
	}
	
	@SuppressLint("NewApi")
	public static int getBitmapSize(Bitmap bitmap){

	    if (Build.VERSION.SDK_INT >= 12) { 
			return bitmap.getByteCount();
		} else {
			return bitmap.getRowBytes() * bitmap.getHeight();
		}
		
	
	}
}
