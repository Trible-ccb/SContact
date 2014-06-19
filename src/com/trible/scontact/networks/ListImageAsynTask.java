package com.trible.scontact.networks;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.ClipData.Item;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

import com.loopj.android.http.AsyncHttpClient;
import com.trible.scontact.R;
import com.trible.scontact.managers.SDCardManager;
import com.trible.scontact.utils.Bog;
import com.trible.scontact.utils.DeviceUtil;
import com.trible.scontact.utils.ImageUtil;
import com.trible.scontact.utils.StringUtil;

public class ListImageAsynTask{
	
	String realLoadedPath;
	ListImageTask mAsyncTask;
	public LruCache<String, Bitmap> mCache;
	ItemImageLoadingListner mLoadingListner;
	
	public String getRealLoadedPath(){
		return realLoadedPath;
	}
	public void cancelTask(){
		if ( mAsyncTask != null && !mAsyncTask.isCancelled() ){
			mAsyncTask.cancel(true);
		}
	}
	public ItemImageLoadingListner getmLoadingListner() {
		return mLoadingListner;
	}
	public void setmLoadingListner(ItemImageLoadingListner mLoadingListner) {
		this.mLoadingListner = mLoadingListner;
	}
	
	public interface ItemImageLoadingListner{
		void onPreLoad();
		void onLoadDone(Bitmap doneBm);
	}

	public class ListImageTask extends AsyncTask<String, Object, Bitmap>{

		WeakReference<ImageView> mImageViewReference;
//		LruCache<Integer, Bitmap> bitmapCache = new LruCache<Integer, Bitmap>(4);
//		long workId = 0;
		String url;
		int preImgWidth;
		int preImgHeight;
		int fullScreenWidth;
		int fullScreenHeight;

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public WeakReference<ImageView> getmImageViewReference() {
			return mImageViewReference;
		}

		public void setmImageViewReference(WeakReference<ImageView> mImageViewReference) {
			this.mImageViewReference = mImageViewReference;
		}
		
	    public ListImageTask(ImageView imageView) {
	        // Use a WeakReference to ensure the ImageView can be garbage collected
	    	mImageViewReference = new WeakReference<ImageView>(imageView);
	    }
	    
		public ListImageTask() {
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if ( mLoadingListner != null ){
				mLoadingListner.onPreLoad();
			}
		}


		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			if (isCancelled()) {
	        	return null;
	        }
			File imgFile;
			Bitmap bitmap = null;

			if ( mCache != null ){
				bitmap = mCache.get(url);
			}
			if ( bitmap != null ){
				return bitmap;
			}
			imgFile = SDCardManager.getInstance().getFileOf(SDCardManager.PATH_IMAGECACHE, url);//load from local
		    bitmap = ImageUtil.getOptionBitmap(imgFile,preImgWidth,preImgHeight);
		    if (bitmap == null) {// load from net
				String imgUrl = url;
				if ( !StringUtil.isValidURL(imgUrl) ){
					return null;
				}
				InputStream inputStream = null;
				try {
					URL myFileUrl = new URL(imgUrl);
					HttpURLConnection conn = (HttpURLConnection) myFileUrl
							.openConnection();
					conn.setDoInput(true);
					conn.connect();
					InputStream is = conn.getInputStream();
	                inputStream = is;
//	                bitmap = BitmapFactory.decodeStream(is);
	                String sufFix = "tmp";
	                SDCardManager.getInstance().createFileTo(SDCardManager.PATH_IMAGECACHE,imgUrl + sufFix, inputStream);
	                File tmpFile = SDCardManager.getInstance().getFileOf(SDCardManager.PATH_IMAGECACHE, imgUrl + sufFix);
	                imgFile = SDCardManager.getInstance().getFileOf(SDCardManager.PATH_IMAGECACHE, imgUrl);
	                if ( tmpFile.length() > 500 * 1024 ){
		                Bitmap tmpmap = ImageUtil.getOptionBitmap(tmpFile, fullScreenWidth, fullScreenHeight);
		                SDCardManager.getInstance().deleteFile(tmpFile);
		                //store to local 
		                SDCardManager.compressImage(tmpmap, 500, new FileOutputStream(imgFile));
		                tmpmap.recycle();
	                } else {
	                	tmpFile.renameTo(imgFile);
	                }
	                bitmap = ImageUtil.getOptionBitmap(tmpFile,preImgWidth,preImgHeight);
	                
	                
				} catch (OutOfMemoryError e){
					Bog.toast(e.getLocalizedMessage());
				}	catch (Exception e) {
					e.printStackTrace();
				} finally {
					IOUtils.closeQuietly(inputStream);
				}
			}
		    if ( mCache != null && bitmap != null ){
		    	mCache.put(url, bitmap);
		    }
		    if ( bitmap != null ){
		    	realLoadedPath = url;
		    } else {
		    	imgFile.deleteOnExit();
		    }
			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			boolean done = result != null ;
	        if (isCancelled()) {
	        	result = null;
	        }
	        if (mImageViewReference != null && result != null) {
	            final ImageView imageView = mImageViewReference.get();
	            final ListImageTask bitmapWorkerTask =
	                    getBitmapWorkerTask(imageView);
	            if (this == bitmapWorkerTask && imageView != null) {
	                imageView.setImageBitmap(result);
	                done = true;
	            }
	        }
			super.onPostExecute(result);
			if ( mLoadingListner != null ){
				mLoadingListner.onLoadDone(result);
			}
		}

	}
	static class AsyncDrawable extends BitmapDrawable {
	    private final WeakReference<ListImageTask> ListImageTaskReference;

	    public AsyncDrawable(Resources res, Bitmap bitmap,
	            ListImageTask ListImageTask) {
	        super(res, bitmap);
	        ListImageTaskReference =
	            new WeakReference<ListImageTask>(ListImageTask);
	    }

	    public ListImageTask getListImageTask() {
	        return ListImageTaskReference.get();
	    }
	}
	public static boolean cancelPotentialWork(String url, ImageView imageView) {
	    final ListImageTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

	    if (bitmapWorkerTask != null) {
	        final String bitmapData = bitmapWorkerTask.url;
	        if (bitmapData != url) {
	            // Cancel previous task
	            bitmapWorkerTask.cancel(true);
	        } else {
	            // The same work is already in progress
	            return false;
	        }
	    }
	    // No task associated with the ImageView, or an existing task was cancelled
	    return true;
	}
	private static ListImageTask getBitmapWorkerTask(ImageView imageView) {
		   if (imageView != null) {
		       final Drawable drawable = imageView.getDrawable();
		       if (drawable instanceof AsyncDrawable) {
		           final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
		           return asyncDrawable.getListImageTask();
		       }
		    }
		    return null;
		}
	public void loadBitmap(String url, ImageView imageView,int defaultResId) {
	    if (cancelPotentialWork(url, imageView)) {
	        final ListImageTask task = new ListImageTask(imageView);
	        task.setUrl(url);
	        Bitmap defaultRes = ImageUtil.convertDrawable2BitmapByCanvas(imageView.getResources().getDrawable(defaultResId));
	        task.preImgHeight = defaultRes.getHeight();
	        task.preImgWidth = defaultRes.getWidth();
	        final AsyncDrawable asyncDrawable =
	                new AsyncDrawable(imageView.getResources(), defaultRes, task);
	        imageView.setImageDrawable(asyncDrawable);
	        
	        task.execute("");
	    }
	}
	public void loadBitmapByScaleOfWinWidth(String url, ImageView imageView,int defaultResId,float scale) {
	    if (cancelPotentialWork(url, imageView)) {
	        final ListImageTask task = new ListImageTask(imageView);
	        task.setUrl(url);
	        Bitmap defaultRes = ImageUtil.convertDrawable2BitmapByCanvas(imageView.getResources().getDrawable(defaultResId));
	        int th = (int) (DeviceUtil.getDeviceHeight(imageView.getContext()));
	        int tw = (int) (DeviceUtil.getDeviceWidth(imageView.getContext()));
	        task.fullScreenHeight = th;
	        task.fullScreenWidth = tw;
	        if ( tw > th ){
	        	task.preImgHeight = (int) (tw * scale);
	        	task.preImgWidth = (int) (th * scale);
	        } else {
	        	task.preImgHeight = (int) (th * scale);
	        	task.preImgWidth = (int) (tw * scale);
	        }
	        final AsyncDrawable asyncDrawable =
	                new AsyncDrawable(imageView.getResources(), defaultRes, task);
	        imageView.setImageDrawable(asyncDrawable);
	        
	        task.execute("");
	    }
	}
	public void loadBitmapByScaleOfWinWidthForWidget(Context c,String url,float scale) {
	        final ListImageTask task = new ListImageTask();
	        task.setUrl(url);
	        int th = (int) (DeviceUtil.getDeviceHeight(c));
	        int tw = (int) (DeviceUtil.getDeviceWidth(c));
	        task.fullScreenHeight =  (th);
	        task.fullScreenWidth =  (tw);      
	        
	        if ( tw > th ){
	        	task.preImgHeight = tw;
	        	task.preImgWidth = (int) (th * scale);
	        } else {
	        	task.preImgHeight = th;
	        	task.preImgWidth = (int) (tw * scale);
	        }
	        task.execute(""); 
	}
}
