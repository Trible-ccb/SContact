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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.trible.scontact.utils.Bog;
import com.trible.scontact.utils.MD5FileUtil;

public class SDCardManager {

	private static SDCardManager 	mInstance = null;
	private String 					mBasePath;
	private static String APP_NAME = "";

	//a map store paths key = pathname value = path
	private HashMap<String, String> mPaths;
	
	private static Context mContext;
	public boolean hasStorage  = true;
	private static boolean needClassicPaths = false;
	
	private SDCardManager() {
		mPaths = new HashMap<String, String>();
		initBasePath();
		if ( needClassicPaths )
		initWithClassicPaths();
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
	}
	
	public static void initStorageWithClassicPaths(Context c,String AppName) {
		needClassicPaths = true;
		initStorage(c,AppName);
		
	}
	
    public static boolean compressImage(Bitmap image ,int sizeK,FileOutputStream f) {
    	if ( image == null || f == null )return false;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这100表示不压缩，把压缩后的数据存放到baos�? 
        int options = 100;
        try{
            while ( baos.toByteArray().length > sizeK * 1024) {  //循环判断如果压缩后图片是否大于sizeK kb,大于继续压缩         

            	if ( baos.toByteArray().length > sizeK * 1024 * 4 ){
            		options /= 2;
            	} else {
            		options -= 10;//每次都减10
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

	private String initRootPath() {
		String path = getSDCardPath(APP_NAME);
		if (path == null) {
			path = getInternalBaseDataPath("data/" + APP_NAME);
		}
		return path;
	}
	

	private void initBasePath() {
		mBasePath = initRootPath();
		if (mBasePath != null) {
			hasStorage = true;
		} else {
			hasStorage = false; 
			// TODO handle exception
		}
		
	}

	private File getBasePath(){
		return new File(getBasePathString());
	}
	
	private String getBasePathString(){
		if(mBasePath == null){
			initBasePath();
		}
		assert mBasePath != null;
		return mBasePath;
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
		File f = new File(getBasePathString()  + "/" + pathname);
		if (!f.exists()) {
			f.mkdirs();
		}
		return f.getPath() == null ? false : true;
	}

	public String getStringPath(String pathkey) {
		if ( pathkey == null || mPaths.get(pathkey) == null )return null;
		
		File f = new File(getBasePathString()  + "/" + mPaths.get(pathkey));
		return f.getPath();
	}

	public File getFilePath (String pathkey){
		File f = new File(getStringPath(pathkey));
		return f;
	}
	
	public File getFileOf(String pathkey,String filename){
		if ( filename == null || pathkey == null )return null;
		String md5 = MD5FileUtil.getMD5String(filename);
		return new File(getStringPath(pathkey), md5);
	}
	
	public void createFileTo(String pathkey,String filename,InputStream is){
		File tmp = getFileOf(pathkey, filename);
		OutputStream os = null ;
		try {
//			FileUtils.w(tmp, IOUtils.);
			os = new FileOutputStream(tmp);
			IOUtils.copy(is, os);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(os);
			IOUtils.closeQuietly(is);
		}
	}
	
	public void createFileTo(String pathkey,String filename,String data){
		File tmp = getFileOf(pathkey, filename);
		try {
			FileUtils.writeStringToFile(tmp, data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void appendFileTo(String pathkey,String filename,InputStream is){
		File tmp = getFileOf(pathkey, filename);
		try {
			FileUtils.writeStringToFile(tmp, IOUtils.toString(is),true);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(is);
		}
	}
	
	public void appendFileTo(String pathkey,String filename,String data){
		File tmp = getFileOf(pathkey, filename);
		try {
			FileUtils.writeStringToFile(tmp, data,true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public void deleteAllPaths(){
		try {
			FileUtils.cleanDirectory(getBasePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public long getUsedVolumeInMegaByte(){
		return FileUtils.sizeOfDirectory(getBasePath());
	}
	
	private void initWithClassicPaths(){
		addPath(PATH_DOWNLOAD, PATH_DOWNLOAD);
		addPath(PATH_IMAGECACHE, PATH_IMAGECACHE);
	}
	
	public static final String PATH_DOWNLOAD = "download";
	public static final String PATH_IMAGECACHE = "images";
	
}
