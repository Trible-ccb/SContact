package com.trible.scontact.components.activity;

import java.io.File;

import com.trible.scontact.R;
import com.trible.scontact.components.widgets.ChoosePictureDialog;
import com.trible.scontact.components.widgets.PropertyKeyValue;
import com.trible.scontact.components.widgets.ChoosePictureDialog.OnPickListener;
import com.trible.scontact.pojo.AccountInfo;
import com.trible.scontact.utils.DeviceUtil;
import com.trible.scontact.utils.ImageUtil;
import com.trible.scontact.value.GlobalValue;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

public class MyProfileActivity extends CustomSherlockFragmentActivity implements OnPickListener {

	PropertyKeyValue mPhoto,mDisplayname,mRealname,mGender,mDesription,mMyContacts;
	AccountInfo mUserInfo;
	ChoosePictureDialog mPictureDialog;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_my_profile);
		mUserInfo = AccountInfo.getInstance();
		initView();
	}
	
	void initView(){
		
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
		
		String unset =  getString(R.string.unset);
		
		mPhoto.setKeyText(getString(R.string.photo_lable));
		mPhoto.setValueText("");
		mPhoto.mValue.setCompoundDrawablesWithIntrinsicBounds(
				R.drawable.ic_launcher, 0, 0, 0);
		mPhoto.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPictureDialog.show();
			}
		});
		
		mDisplayname.setKeyText(getString(R.string.username_lable));
		mDisplayname.setValueText(
				TextUtils.isEmpty(mUserInfo.getDisplayName())
				? unset : mUserInfo.getDisplayName());
		mDisplayname.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		
		mDesription.setKeyText(getString(R.string.description_lable));
		mDesription.setValueText(
				TextUtils.isEmpty(mUserInfo.getDescription())
				? unset : mUserInfo.getDescription());
		mDesription.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		
		mRealname.setKeyText(getString(R.string.realname_lable));
		mRealname.setValueText(
				TextUtils.isEmpty(mUserInfo.getRealName())
				? unset : mUserInfo.getRealName());
		mRealname.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		
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
		mGender.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		mMyContacts.setKeyText(getString(R.string.action_my_contacts));
		mMyContacts.setValueText(
				SContactMainActivity.myAllContacts == null
				? "0" : SContactMainActivity.myAllContacts.size() + "");
		mMyContacts.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				simpleDisplayActivity(MyContactsActivity.class);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mPictureDialog.onActivityResult(requestCode, resultCode, data);
	}
	@Override
	public void onPickedImage(Bitmap bm,File fullSource) {
		mPhoto.mValue.setCompoundDrawablesWithIntrinsicBounds(
				new BitmapDrawable(getResources(), bm), null, null, null);
	}
	
}
