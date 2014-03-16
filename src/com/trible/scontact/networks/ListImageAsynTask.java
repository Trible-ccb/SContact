package com.trible.scontact.networks;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;

import android.content.ClipData.Item;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

import com.trible.scontact.R;
import com.trible.scontact.managers.SDCardManager;
import com.trible.scontact.utils.DeviceUtil;
import com.trible.scontact.utils.ImageUtil;
import com.trible.scontact.utils.StringUtil;

public class ListImageAsynTask{
	
	String realLoadedPath;
	ListImageTask mAsyncTask;
	public LruCache<Long, Bitmap> mCache;
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
		Item mItem;
		long workId = 0;
		int preImgWidth;
		int preImgHeight;
		int fullScreenWidth;
		int fullScreenHeight;
		
		public Item getmItem() {
			return mItem;
		}

		public void setmItem(Item mItem) {
			this.mItem = mItem;
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
			// TODO Auto-generated method stub
			super.onCancelled();
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			if (isCancelled()) {
	        	return null;
	        }
			File imgFile;
			Bitmap bitmap = null;

			if ( mItem == null ){
				return null;
			}
//			Item tmp = ItemHelper.getItemById(mItem.getId());
//			workId = mItem.getId();
//			ImageFileUrl fileUrl = ItemHelper.getItemOneImageFileUrl(tmp);
//			if ( mCache != null ){
//				bitmap = mCache.get(workId);
//			}
//			if ( bitmap != null ){
//				return bitmap;
//			}
//			if ( fileUrl == null )return null;
			
//			imgFile = SDCardManager.getInstance().getImageCachePath(fileUrl.getImageName());//load from local
//		    bitmap = ImageUtil.getOptionBitmap(imgFile,preImgWidth,preImgHeight);
//		    if (bitmap == null) {// load from net
//				String imgUrl = null;
//				if ( fileUrl.getUrl() != null ){
//					imgUrl = fileUrl.getUrl().trim().replace(" ", "+");
//				}
//				if ( !StringUtil.isValidURL(imgUrl) ){
//					return null;
//				}
//				InputStream inputStream = null;
//				try {
////					imgUrl = imgUrl.replaceFirst("image_size=[0-9]+&?", "");
//	                HttpResponse ret = DiigoHttpClient.post(imgUrl, null);
//	                inputStream = ret.getEntity().getContent();
//	                String sufFix = "tmp";
//	                SDCardManager.getInstance().setImageCache(fileUrl.getImageName() + sufFix, inputStream);
//	                File tmpFile = SDCardManager.getInstance().getImageCachePath(fileUrl.getImageName() + sufFix);
//	                imgFile = SDCardManager.getInstance().getImageCachePath(fileUrl.getImageName());
//	                if ( tmpFile.length() > CropperActivity.MAX_SIZE_IMAGE * 1024 ){
//		                Bitmap tmpmap = ImageUtil.getOptionBitmap(tmpFile, fullScreenWidth, fullScreenHeight);
//		                SDCardManager.getInstance().deleteFile(tmpFile);
//		                //store to local 
//		                SDCardManager.compressImage(tmpmap, CropperActivity.MAX_SIZE_IMAGE, new FileOutputStream(imgFile));
//		                tmpmap.recycle();
//	                } else {
//	                	tmpFile.renameTo(imgFile);
//	                }
//	                bitmap = ImageUtil.getOptionBitmap(imgFile,preImgWidth,preImgHeight);
//	                
//				} catch (OutOfMemoryError e){
//					Dog.toast(R.string.image_is_too_big);
//					Log.e(e.getLocalizedMessage());
//				}	catch (Exception e) {
//					e.printStackTrace();
//					Log.e(e);
//				} finally {
//					IOUtils.closeQuietly(inputStream);
//				}
//			}
//		    if ( mCache != null && bitmap != null ){
//		    	mCache.put(workId, bitmap);
//		    }
//		    if ( bitmap != null ){
//		    	realLoadedPath = fileUrl.getImageName();
//		    }
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
	public static boolean cancelPotentialWork(long workId, ImageView imageView) {
	    final ListImageTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

	    if (bitmapWorkerTask != null) {
	        final long bitmapData = bitmapWorkerTask.workId;
	        if (bitmapData != workId) {
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
	public void loadBitmap(Item item, ImageView imageView,int defaultResId) {
//	    if (cancelPotentialWork(item.getId(), imageView)) {
//	        final ListImageTask task = new ListImageTask(imageView);
//	        task.setmItem(item);
//	        Bitmap defaultRes = ImageUtil.convertDrawable2BitmapByCanvas(imageView.getResources().getDrawable(defaultResId));
//	        task.preImgHeight = defaultRes.getHeight();
//	        task.preImgWidth = defaultRes.getWidth();
//	        final AsyncDrawable asyncDrawable =
//	                new AsyncDrawable(imageView.getResources(), defaultRes, task);
//	        imageView.setImageDrawable(asyncDrawable);
//	        
//	        task.execute("");
//	    }
	}
	public void loadBitmapByScaleOfWinWidth(Item item, ImageView imageView,int defaultResId,float scale) {
//	    if (cancelPotentialWork(item.getId(), imageView)) {
//	        final ListImageTask task = new ListImageTask(imageView);
//	        task.setmItem(item);
//	        Bitmap defaultRes = ImageUtil.convertDrawable2BitmapByCanvas(imageView.getResources().getDrawable(defaultResId));
//	        int th = (int) (DeviceUtil.getDeviceHeight(imageView.getContext()));
//	        int tw = (int) (DeviceUtil.getDeviceWidth(imageView.getContext()));
//	        task.fullScreenHeight = th;
//	        task.fullScreenWidth = tw;
//	        if ( tw > th ){
//	        	task.preImgHeight = (int) (tw * scale);
//	        	task.preImgWidth = (int) (th * scale);
//	        } else {
//	        	task.preImgHeight = (int) (th * scale);
//	        	task.preImgWidth = (int) (tw * scale);
//	        }
//	        final AsyncDrawable asyncDrawable =
//	                new AsyncDrawable(imageView.getResources(), defaultRes, task);
//	        imageView.setImageDrawable(asyncDrawable);
//	        
//	        task.execute("");
//	    }
	}
	public void loadBitmapByScaleOfWinWidthForWidget(Context c,Item item,float scale) {
	        final ListImageTask task = new ListImageTask();
	        task.setmItem(item);
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
