package com.trible.scontact.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.io.IOUtils;

public class FileUtil {

	public boolean createFileTo(File f,InputStream is,boolean append){
		if ( f == null )return false;
		File tmp = f ;
		OutputStream os = null ;
		try {
			if ( f.exists() ){
				if ( !append ){
					f.deleteOnExit();
					f.createNewFile();
				}
			} else {
				f.createNewFile();
			}

			byte[] data = new byte[1024 * 10];
			os = new FileOutputStream(tmp, append);
			int len;
			while ((len = is.read(data, 0, data.length)) != -1) {
				os.write(data, 0, len);
			}

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			IOUtils.closeQuietly(os);
			IOUtils.closeQuietly(is);
		}
		return true;
	}
	static public String convertStreamToString(InputStream is, String encoiding) {

		StringBuilder sb = new StringBuilder();
		char[] buff = new char[100 * 1024];
		int ret;
		try {
			InputStreamReader reader = new InputStreamReader(is, encoiding);

			while ((ret = reader.read(buff)) != -1) {
				sb.append(buff, 0, ret);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return sb.toString();
	}
	
	 public static String getMD5(File file) {
	        FileInputStream fis = null;
	        try {
	            MessageDigest md = MessageDigest.getInstance("MD5");
	            fis = new FileInputStream(file);
	            byte[] buffer = new byte[8192];
	            int length = -1;
	            while ((length = fis.read(buffer)) != -1) {
	                md.update(buffer, 0, length);
	            }
	           
	            return new String(md.digest(),"UTF-8");
	        } catch (IOException ex) {
	            return null;
	        } catch (NoSuchAlgorithmException ex) {
	            return null;
	        } finally {
	           IOUtils.closeQuietly(fis);
	        }
	    }

}
