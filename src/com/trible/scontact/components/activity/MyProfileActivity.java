package com.trible.scontact.components.activity;

import java.io.File;

import org.apache.http.Header;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.trible.scontact.R;
import com.trible.scontact.components.widgets.ChoosePictureDialog;
import com.trible.scontact.components.widgets.ChoosePictureDialog.OnPickListener;
import com.trible.scontact.components.widgets.LoadingDialog;
import com.trible.scontact.components.widgets.PropertyKeyValue;
import com.trible.scontact.components.widgets.SimpleInputDialog;
import com.trible.scontact.components.widgets.SimpleInputDialog.OnSubmitInputListener;
import com.trible.scontact.networks.ListImageAsynTask;
import com.trible.scontact.networks.ListImageAsynTask.ItemImageLoadingListner;
import com.trible.scontact.networks.SContactAsyncHttpClient;
import com.trible.scontact.networks.params.AccountParams;
import com.trible.scontact.pojo.AccountInfo;
import com.trible.scontact.utils.Bog;
import com.trible.scontact.utils.DeviceUtil;
import com.trible.scontact.utils.StringUtil;
import com.trible.scontact.value.GlobalValue;

public class MyProfileActivity extends CustomSherlockFragmentActivity implements OnPickListener {

	PropertyKeyValue mPhoto,mDisplayname,mRealname,mGender,mDesription,mMyContacts;
	AccountInfo mUserInfo;
	ChoosePictureDialog mPictureDialog;
	SimpleInputDialog mInputDialog;
	LoadingDialog mLoadingDialog;
	Button mUseQQButton;
	Drawable mPhotoBitmap;
	boolean mPhotoLoaded;
	
	String unset;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_my_profile);
		unset =  getString(R.string.unset);
		setTitle(R.string.action_my_profile, R.color.blue_qq);
		mPhotoLoaded = false; 
		initView();
	}
	
	void initView(){
		mPhotoBitmap = getResources().getDrawable(R.drawable.icon_logo);
		mInputDialog = new SimpleInputDialog(this);
		mInputDialog.setmListener(new OnSubmitInputListener() {
			@Override
			public void onSubmit(String input) {
				if ( TextUtils.isEmpty(input) )return;
				String title = mInputDialog.getDialog().getTitleText();
				String key = null;
				String value = null;
				if ( getString(R.string.realname_lable).equals(title) ){
					key = AccountInfo.FieldName.REALNAME;
					value = input;
				} else if ( getString(R.string.description_lable).equals(title) ){
					key = AccountInfo.FieldName.DESCRIPTION;
					value = input;
				} else if ( getString(R.string.gender_lable).equals(title) ){
					key = AccountInfo.FieldName.GENDER;
					value = input;
				}
				updateAccountToServer(key,value);
				mInputDialog.getDialog().dismissDialogger();
			}
		});
		mUseQQButton = (Button) findViewById(R.id.use_qq_profile);
		mUseQQButton.setVisibility(View.GONE);
		mUseQQButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			}
		});
		
		mPictureDialog = new ChoosePictureDialog(this, null);
		mPictureDialog.setOnPickListener(this);
		
		mPhoto = new PropertyKeyValue(findViewById(R.id.photo));
		mPhoto.mValue.setWidth(DeviceUtil.dip2px(this, 64));
		mPhoto.mValue.setHeight(DeviceUtil.dip2px(this, 64));
		mDisplayname = new PropertyKeyValue(findViewById(R.id.dispalyname));
		mDesription = new PropertyKeyValue(findViewById(R.id.description));
		mRealname = new PropertyKeyValue(findViewById(R.id.realname));
		mGender = new PropertyKeyValue(findViewById(R.id.gender));
		mMyContacts = new PropertyKeyValue(findViewById(R.id.mycontacts));

		mPhoto.mNextIcon.setBackgroundResource(R.drawable.bg_white_border);
		mPhoto.mNextIcon.setImageDrawable(mPhotoBitmap);
//		mPhoto.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Bog.toast(R.string.unsupport_action);
//			}
//		});
		
		mDesription.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mInputDialog.getDialog().setTitleText(getString(R.string.description_lable));
				mInputDialog.mEditText.setHint("");
				String va = mDesription.mValue.getText().toString();
				if ( !unset.equals(va) ){
					mInputDialog.mEditText.setText(va);
				} else {
					mInputDialog.mEditText.setText("");
					mInputDialog.mEditText.setHint(unset);
				}
				mInputDialog.show();
			}
		});
		
		mRealname.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mInputDialog.getDialog().setTitleText(getString(R.string.realname_lable));
				mInputDialog.mEditText.setHint("");
				String va = mRealname.mValue.getText().toString();
				if ( !unset.equals(va) ){
					mInputDialog.mEditText.setText(va);
				} else {
					mInputDialog.mEditText.setText("");
					mInputDialog.mEditText.setHint(unset);
				}
				mInputDialog.show();
			}
		});
		
//		mGender.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				mInputDialog.getDialog().setTitleText(getString(R.string.gender_lable));
//				mInputDialog.mEditText.setHint("");
//				String va = mGender.mValue.getText().toString();
//				if ( !unset.equals(va) ){
//					mInputDialog.mEditText.setText(va);
//				} else {
//					mInputDialog.mEditText.setText("");
//					mInputDialog.mEditText.setHint(unset);
//				}
//				mInputDialog.show();
//			}
//		});
		mMyContacts.setKeyText(getString(R.string.action_my_contacts));
		mMyContacts.mRoot.setVisibility(View.GONE);
//		mMyContacts.setValueText(
//				SContactMainActivity.myAllContacts == null
//				? "0" : SContactMainActivity.myAllContacts.size() + "");
//		mMyContacts.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				simpleDisplayActivity(MyContactsActivity.class);
//			}
//		});
		refreshInfo();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mPictureDialog.onActivityResult(requestCode, resultCode, data);
	}
	@Override
	public void onPickedImage(Bitmap bm,File fullSource) {
		mPhotoBitmap = new BitmapDrawable(getResources(), bm);
		refreshInfo();
	}
	
	void refreshInfo(){
		mUserInfo = AccountInfo.getInstance();
		mPhoto.setKeyText(getString(R.string.photo_lable));
		mPhoto.setValueText("");
		if ( mPhotoLoaded ){
			mPhoto.mNextIcon.setImageDrawable(mPhotoBitmap);
		} else {
			ListImageAsynTask phototask = new ListImageAsynTask();
			phototask.setmLoadingListner(new ItemImageLoadingListner() {
				@Override
				public void onPreLoad() {
				}
				@Override
				public void onLoadDone(Bitmap doneBm) {
					mPhotoBitmap = new BitmapDrawable(getResources(),doneBm);
					if ( doneBm != null && mPhotoBitmap != null ){
						mPhotoLoaded = true;
						refreshInfo();
					}
				}
			});
			phototask.loadBitmapByScaleOfWinWidthForWidget(this, AccountInfo.getInstance().getPhotoUrl(), 0.5f);
		}
		
		mDisplayname.setKeyText(getString(R.string.username_lable));
		mDisplayname.setValueText(
				TextUtils.isEmpty(mUserInfo.getDisplayName())
				? unset : mUserInfo.getDisplayName());
		
		mDesription.setKeyText(getString(R.string.description_lable));
		mDesription.setValueText(
				TextUtils.isEmpty(mUserInfo.getDescription())
				? unset : mUserInfo.getDescription());
		
		mDesription.setKeyText(getString(R.string.description_lable));
		mDesription.setValueText(
				TextUtils.isEmpty(mUserInfo.getDescription())
				? unset : mUserInfo.getDescription());
		
		mRealname.setKeyText(getString(R.string.realname_lable));
		mRealname.setValueText(
				TextUtils.isEmpty(mUserInfo.getRealName())
				? unset : mUserInfo.getRealName());
		
		String gender = getString(R.string.unset);
		if ( GlobalValue.UGENDER_FEMALE.equals(mUserInfo.getGender()) ){
			gender = getString(R.string.gender_female);
		} else if ( GlobalValue.UGENDER_MALE.equals(mUserInfo.getGender()) ){
			gender = getString(R.string.gender_male);
		} else if ( GlobalValue.UGENDER_OTHER.equals(mUserInfo.getGender()) ){
			gender = getString(R.string.gender_other);
		}
		mGender.setKeyText(getString(R.string.gender_lable));
		mGender.setValueText(gender);
	}
	
	void updateAccountToServer(final String key,final Object value){
		mLoadingDialog = new LoadingDialog(this);
		mLoadingDialog.show();
		final Object oldv = AccountInfo.getInstance().get(key);
		AccountInfo.getInstance().put(key, value);
		AccountInfo.getInstance().setFetchWhenSave(true);
		AccountInfo.getInstance().saveInBackground(new SaveCallback() {
			@Override
			public void done(AVException arg0) {
				mLoadingDialog.getDialog().dismissDialogger();
				if (arg0 == null){
					AccountInfo.getInstance().put(key, value);
					refreshInfo();
				} else {
					AccountInfo.getInstance().put(key, oldv);
					refreshInfo();
					Bog.toast(
					StringUtil.catStringFromResId(
							R.string.update,R.string.failed));
					Bog.v(arg0.getMessage());
				}
			}
		});
	}
}
