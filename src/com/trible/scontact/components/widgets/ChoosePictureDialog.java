package com.trible.scontact.components.widgets;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.trible.scontact.R;
import com.trible.scontact.managers.SDCardManager;
import com.trible.scontact.networks.NetWorkEvent;
import com.trible.scontact.networks.SimpleAsynTask;
import com.trible.scontact.networks.SimpleAsynTask.AsynTaskListner;
import com.trible.scontact.pojo.AccountInfo;
import com.trible.scontact.utils.Bog;
import com.trible.scontact.utils.DeviceUtil;
import com.trible.scontact.utils.ImageUtil;

public class ChoosePictureDialog {
	
	public static final int WAY_PIC_FROM_UNKNOW = 0;
	public static final int WAY_PIC_FROM_CAMERA = WAY_PIC_FROM_UNKNOW + 1;
	public static final int WAY_PIC_FROM_LOCAL = WAY_PIC_FROM_CAMERA + 1;
	PopupDialogger dialogger;

	Activity mContext;
	String mTitleString;
	View contentView;
	public TextView picFromCamera,picFromLocal;
	OnPickListener mListner;
	
	public int mActionType;
	public String imgName;
	public int MAX_SIZE_IMAGE = 500;
	public float mWantWidth,mWantHeight;
	
	public static final int kADD_PICTURE_FROM_LOCAL = 1;
	public static final int kADD_IMAGE_FROM_CAMERA = kADD_PICTURE_FROM_LOCAL + 1;
	
	public static final int CODE_TAKE_PICTURE = 1000;
	public static final int CODE_PICK_PICTURE = CODE_TAKE_PICTURE + 1;
	
	public interface OnPickListener{
		void onPickedImage(Bitmap bm,File fullSource);
	}
	
	public void setOnPickListener(OnPickListener listner) {
		mListner = listner;
	}
	
	public void setOnPickListener(OnPickListener listner,float w,float h) {
		mListner = listner;
		mWantWidth = w;
		mWantHeight = h;
	}
	
	public PopupDialogger getDialog() {
		return dialogger;
	}


	public  ChoosePictureDialog(Activity context,String title) {
		mContext = context;
		mTitleString = title;
		mWantHeight = DeviceUtil.getDeviceHeight(context);
		mWantWidth = DeviceUtil.getDeviceWidth(context);
		contentView = createContentView();
	}
	
	private View createContentView(){
		
		View view = LayoutInflater.from(mContext).inflate(R.layout.popup_choose_picture, null);
		picFromCamera = (TextView) view.findViewById(R.id.picture_from_camera);
		picFromLocal = (TextView) view.findViewById(R.id.picture_from_local);
		OnClickListener listener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.picture_from_camera:
					mActionType = kADD_IMAGE_FROM_CAMERA;
					if (SDCardManager.getInstance().hasStorage) {
						Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
						intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri
								.fromFile(SDCardManager.getInstance()
										.getFileOf(SDCardManager.PATH_IMAGECACHE, GeneraImageName(true))));
						try {
							mContext.startActivityForResult(intent, CODE_TAKE_PICTURE);
						} catch (ActivityNotFoundException e) {
							Bog.toast(R.string.cannot_take_photo);
						}
					} else {
						Bog.toast("no storage");
					}
					dialogger.dismissDialogger();
					break;
				case R.id.picture_from_local:
					mActionType = kADD_PICTURE_FROM_LOCAL;
					Intent intent = new Intent(Intent.ACTION_PICK);
					intent.setDataAndType(
							MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*");
					// intent.putExtra("crop", "true");
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri
							.fromFile(SDCardManager.getInstance()
									.getFileOf(SDCardManager.PATH_IMAGECACHE, GeneraImageName(false))));
					try {
						mContext.startActivityForResult(intent, CODE_PICK_PICTURE);
					} catch (ActivityNotFoundException e) {
						Bog.toast(R.string.cannot_pick_picture);
					}
					dialogger.dismissDialogger();
					break;
				default:
					break;
				}
			}
		};
		picFromCamera.setOnClickListener(listener);
		picFromLocal.setOnClickListener(listener);
		
		dialogger = PopupDialogger.createDialog(mContext);
		return view;
	}
	public void show(){
		dialogger.setTitleText(mTitleString);
		dialogger.showDialog(mContext,contentView);
	}
	public void performClickOnWay(int way){
		if ( way == WAY_PIC_FROM_CAMERA ){
			picFromCamera.performClick();
		} else if ( way == WAY_PIC_FROM_LOCAL ){
			picFromLocal.performClick();
		}
	}
	
	private String GeneraImageName(boolean isNew) {
		if ( !TextUtils.isEmpty(imgName) ){
			return imgName;
		}
		return imgName = new SimpleDateFormat("yyyyMMdd_HHmmss_"
				+ AccountInfo.getInstance().getId()).format(new Date());
	}
	
	public void onActivityResult(int requestCode, int resultCode, final Intent data) {
		if (resultCode != Activity.RESULT_OK){
			return;
		}
		switch (requestCode) {
		case CODE_PICK_PICTURE: {
			if (imgName != null) {
				noCropImage(data.getData(),data);
			}
			break;
		}
		case CODE_TAKE_PICTURE: {
			noCropImage(
					Uri.fromFile(
							SDCardManager.getInstance().getFileOf(
									SDCardManager.PATH_IMAGECACHE, imgName)),
									data);
			break;

		}
		default:
			break;
		}

	}
	private void noCropImage(final Uri uri,final Intent data) {
		final float scaleOfWinWidth = 0.382f;
		final SimpleAsynTask cmp = new SimpleAsynTask();
		final File f = SDCardManager.getInstance().getFileOf(SDCardManager.PATH_IMAGECACHE, imgName);
		cmp.doTask(new AsynTaskListner() {
			Bitmap m = null;
			@Override
			public void onTaskDone(NetWorkEvent event) {
				if (m != null) {
					if ( mListner != null ){
						mListner.onPickedImage(m,f);
					}
				}
				if (m == null && data != null) {
					Bundle extras = data.getExtras();
					if (extras != null) {
						Bitmap photo = extras.getParcelable("data");
						if ( mListner != null ){
							mListner.onPickedImage(photo,f);
						}
					}
				}
			}
			
			@Override
			public void doInBackground() {
				Bitmap bm = getBitmapFromUri(uri);
				try {
					SDCardManager.compressImage(bm, MAX_SIZE_IMAGE, new FileOutputStream(f));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				m = ImageUtil.getOptionBitmap(f, (int)mWantWidth, (int)mWantHeight);
			}
		});

	}
	private Bitmap getBitmapFromUri(Uri uri)
    {
    	if ( uri == null )return null;
	      try
	      {
	    	  Bitmap bitmap = null;
	    	   if ( uri.getScheme().startsWith("content")){
	    		   bitmap = ImageUtil.getOptionBitmapByScaleOfWinWidth(mContext, uri, 1f);
	    	   } else {
	    		   bitmap = ImageUtil.getScaleOptionImageByScaleOfWinWidth(mContext, new File(uri.getPath()), 1);
	    	   }
		       
		       return bitmap;
	      }
	      catch (Exception e)
	      {
	       e.printStackTrace();
	       return null;
	      }
	 }
}
