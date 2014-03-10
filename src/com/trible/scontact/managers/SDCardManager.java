package com.trible.scontact.managers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.trible.scontact.utils.Bog;

public class SDCardManager {

	private static SDCardManager 	mInstance = null;
	private String 					mBasePath;
	private static String APP_NAME = "";

	//a map store paths key = pathname value = path
	private HashMap<String, String> mPaths;
	
	private static Context mContext;
	public boolean hasStorage  = true;
	
	private SDCardManager() {
		initBasePath();

//		deleteDirectory(new File(getDownloadCacheDir()));
	}

	public static SDCardManager getInstance() {
		if (mInstance == null  ) {
			if(mInstance != null){
				mInstance = null;
			}
			mInstance = new SDCardManager();
		}
		return mInstance;
	}

	public static void initStorage(Context c,String AppName) {
		
		APP_NAME = AppName;
		
		getInstance();
		mContext = c;
//		s.deleteDirectory(new File(s.getImageCacheDir()));
	}
	
	public static void destory() {
		
	}
	
	public File getDownLoadCache(String name) {
		return new File(getDownloadCacheDir(), name);
	}

	public File setDownLoadCache(int id, InputStream is) {
		File tmp = new File(getDownloadCacheDir(), String.valueOf(id) + ".tmp");
		try {
			if (!tmp.exists()) {
				tmp.createNewFile();
				tmp.deleteOnExit();
			}

			byte[] data = new byte[1024 * 10];
			OutputStream os = new FileOutputStream(tmp);
			int len;
			while ((len = is.read(data, 0, data.length)) != -1) {
				os.write(data, 0, len);
			}
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return tmp;
	}

	public InputStream getDownLoadCache(int id) {
		File tmp = new File(getDownloadCacheDir(), String.valueOf(id) + ".tmp");
		InputStream is = null;
		try {
			is = new FileInputStream(tmp);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return is;
	}

	/**
	 * Image cache I/O
	 * 
	 * @param imgURL
	 *            image URL
	 * @return null if no cache
	 */
	// TODO 
	// catch the out of memory exception
	public Bitmap getImageCache(String imgURL, int width, int height) {
		File img = getImageCachePath(imgURL);
		if (img == null) {
			return null;
		}

		Bitmap ret = null;
		try {
			if (img.exists()) {
				ret = BitmapFactory.decodeFile(img.getPath());
				if(ret != null && width!= -1 && height != -1){
					return Bitmap.createScaledBitmap(ret, width, height, false);
				}
			}
		} catch (Exception e){
			img.delete();
			return null;
		}
		return ret;
	}
    public static boolean compressImage(Bitmap image ,int sizeK,FileOutputStream f) {
    	if ( image == null || f == null )return false;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这�?00表示不压缩，把压缩后的数据存放到baos�? 
        int options = 100;
        try{
            while ( baos.toByteArray().length > sizeK * 1024) {  //循环判断如果压缩后图片是否大于sizeK kb,大于继续压缩         

            	if ( baos.toByteArray().length > sizeK * 1024 * 4 ){
            		options /= 2;
            	} else {
            		options -= 10;//每次都减�?0
            	}
            	if ( options < 0 ){
            		break;
            	}
                baos.reset();//重置baos即清空baos  
                image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos�?    
            } 
        } catch (OutOfMemoryError e){
        	Bog.toast("image_is_too_big");
        	return false;
        }
        try {
			baos.writeTo(f);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
	        if ( f != null ){
	        	IOUtils.closeQuietly(f);
	        }
		}

        image.recycle();
        return true;
    }
	
	public boolean setImageCache(String imgURL,InputStream is) {
		File img = getImageCachePath(imgURL);
		if (img != null) {
			if (!img.exists()) {
				return setFile(img, is, false);
			}
			return true;
		}
		return false;
	}

	public File getImageCachePath(String imagURL) {
		if (imagURL != null && imagURL.trim().length() > 0) {
			File path = new File(getImageCacheDir(), imagURL.replaceAll(
					"\\/|\\:|@|\\?|&|=", "_")+"_");
			return path;
		} else
			return null;
	}
	public String getImgCacheFullPath(String imgUrlName){
		File f = getImageCachePath(imgUrlName);
		if (f == null){
			return "";
			
		} else {
			return f.getPath();
		}
	}
	public InputStream getImgCacheStream(String imagURL){
		File tmp = getImageCachePath(imagURL);
		InputStream is = null;
		try {
			is = new FileInputStream(tmp);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return is;
	}
	/**
	 * 
	 * @param path
	 * @return
	 */
	public boolean deleteFile(File path) {
		if ( path == null || (!path.isFile() && !path.isDirectory())){
			return false;
		}
		if (path.exists() && path.isDirectory() ) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteFile(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}

	private boolean setFile(File f, InputStream is, boolean append) {
		if (f.exists()) {
			String name = f.getName();
			String path = f.getPath();
			File tmp = new File(path + name + "_tmp");
			if (tmp.exists()) {
				tmp.delete();
			}

			try {
				writeFile(tmp, is, append);
			} catch (IOException e) {
				
				return false;
			}

			if (!f.delete()) {
				tmp.delete();
				return false;
			} else {
				if (!tmp.renameTo(f)) {
					tmp.delete();
					return false;
				}
			}

		} else {
			try {
				f.createNewFile();
				writeFile(f, is, false);

			} catch (IOException e) {
				
				return false;
			}
		}
		return true;
	}

	private void writeFile(File dest, InputStream is, boolean append)
			throws IOException {
		FileOutputStream writer = null;
		writer = new FileOutputStream(dest, append);
		byte[] buffer = new byte[1024 * 10];
		int byteCount = 0;
		while ((byteCount = is.read(buffer, 0, buffer.length)) >= 0) {
			writer.write(buffer, 0, byteCount);
		}
		writer.flush();
		writer.close();
	}

	private String initRootPath() {

		String path = getSDCardPath(APP_NAME);
		if (path == null) {
			path = getInternalBaseDataPath("data/" + APP_NAME);
		}
		return path;
	}
	
	 public String getAPPCachePath(){
		return mInstance.getCachePath();
	}

	private void initBasePath() {

		mBasePath = initRootPath();
		if (mBasePath != null) {
			hasStorage = true;
			getDataPath();
			getCachePath();
			getImageCacheDir();
		} else {
			hasStorage = false; 
			// TODO handle exception
		}
		
	}

	private String getBasePath(){
		if(mBasePath == null){
			initBasePath();
		}
		
		assert mBasePath != null;
		
		return mBasePath;
	}
	private String getDataPath() {
		File f = new File(getBasePath() );
		if (!f.exists()) {
			f.mkdirs();
		}
		
		return f.getPath();
	}

	private String getCachePath() {
		File f = new File(getBasePath()  + "/cache");
		if (!f.exists()) {
			f.mkdirs();
		}
		
		return f.getPath();
	}
	public String getDebugPath() {
		File f = new File(getBasePath()  + "/debugfile");
		if (!f.exists()) {
			f.mkdirs();
		}
		
		return f.getPath();
	}
	private String getDownloadCacheDir() {
		File f = new File(getBasePath()  + "/download");
		if (!f.exists()) {
			f.mkdirs();
		}
		return f.getPath();
	}

	public String getImageCacheDir() {
		File f = new File(getBasePath()  + "/imagecache");
		if (!f.exists()) {
			f.mkdirs();
		}
		return f.getPath();
	}

	private static String getSDCardPath(String path) {

		File sdPath = new File(Environment.getExternalStorageDirectory(), path);
		sdPath.deleteOnExit();
		
		if (!sdPath.exists()) {
			
			if (!sdPath.mkdirs()){
				
				return null;
			}

		}

		return sdPath.getPath();
	}

	public String getInternalBaseDataPath(String path) {

		File internalPath = new File(Environment.getDataDirectory(), path);
		internalPath.deleteOnExit();
		
		if (!internalPath.exists()) {
			
			if (!internalPath.mkdirs()) {
				
				return null;
			}
		}
		return internalPath.getPath();
	}

	public boolean addPath(String path_key,String pathname) {
		mPaths.put(path_key, pathname);
		if ( path_key == null || pathname == null )return false;
		
		File f = new File(getBasePath()  + "/" + pathname);
		if (!f.exists()) {
			f.mkdirs();
		}
		return f.getPath() == null ? false : true;
	}

	public String getPath(String key) {
		if ( key == null || mPaths.get(key) == null )return null;
		
		File f = new File(getBasePath()  + "/" + mPaths.get(key));
		return f.getPath();
	}
	
	 public static void wipeOldDataOnSDCard() {
//	 File dir = getSDCardPath(APP_NAME);
//	 deleteDirectory(dir);
	 }


}
