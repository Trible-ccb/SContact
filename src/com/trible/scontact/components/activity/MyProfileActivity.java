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

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tencent.connect.UserInfo;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.trible.scontact.R;
import com.trible.scontact.components.widgets.ChoosePictureDialog;
import com.trible.scontact.components.widgets.ChoosePictureDialog.OnPickListener;
import com.trible.scontact.components.widgets.PropertyKeyValue;
import com.trible.scontact.managers.PrefManager;
import com.trible.scontact.networks.ListImageAsynTask;
import com.trible.scontact.networks.ListImageAsynTask.ItemImageLoadingListner;
import com.trible.scontact.networks.SContactAsyncHttpClient;
import com.trible.scontact.networks.params.AccountParams;
import com.trible.scontact.pojo.AccountInfo;
import com.trible.scontact.pojo.GsonHelper;
import com.trible.scontact.thirdparty.QQLoginListener;
import com.trible.scontact.thirdparty.TencentHelper;
import com.trible.scontact.thirdparty.beans.LoginResponse;
import com.trible.scontact.thirdparty.beans.QQUserInfo;
import com.trible.scontact.utils.Bog;
import com.trible.scontact.utils.DeviceUtil;
import com.trible.scontact.value.GlobalValue;

public class MyProfileActivity extends CustomSherlockFragmentActivity implements OnPickListener {

	PropertyKeyValue mPhoto,mDisplayname,mRealname,mGender,mDesription,mMyContacts;
	AccountInfo mUserInfo;
	ChoosePictureDialog mPictureDialog;
	Button mUseQQButton;
	Drawable mPhotoBitmap;
	boolean mPhotoLoaded;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_my_profile);

		setTitle(R.string.action_my_profile, R.color.blue_qq);
		mPhotoLoaded = false; 
		initView();
	}
	
	void initView(){
		mPhotoBitmap = getResources().getDrawable(R.drawable.ic_launcher);
		
		mUseQQButton = (Button) findViewById(R.id.use_qq_profile);
		mUseQQButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Tencent t = TencentHelper.getTencent();
				if ( true ){
					t.login(MyProfileActivity.this,
							"get_user_info,get_simple_userinfo",
							new IUiListener() {
								@Override
								public void onCancel() {
								}
								@Override
								public void onComplete(Object json) {
									mPhotoLoaded = false;
									getQQUserInfo(json);
								}
								@Override
								public void onError(UiError arg0) {
									Bog.toast(arg0.errorMessage);
								}
		
							}
					);
				}
//				else {
//					LoginResponse re = LoginResponse.getFromSpf();
//					getQQUserInfo(re);
//				}
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
		mPhoto.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				mPictureDialog.show();
			}
		});
		
		mDisplayname.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		
		mDesription.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		
		mRealname.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		
		mGender.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
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

	void getQQUserInfo(Object json){
		LoginResponse re;
		if ( json instanceof LoginResponse ){
			re = (LoginResponse) json;
		} else {
			re = LoginResponse.getFromJson(json.toString());
		}
		TencentHelper.setData(re);
		re.saveToSpf();
		if ( re.access_token != null && re.ret == 0 ){
			re.saveToSpf();
			UserInfo qquser = new UserInfo(getApplicationContext(), TencentHelper.getTencent().getQQToken());
			qquser.getUserInfo(new IUiListener() {
				@Override
				public void onError(UiError arg0) {
					Bog.toast(arg0.errorMessage);
				}
				@Override
				public void onComplete(Object arg0) {
					QQUserInfo inforeqp = GsonHelper.getInfosFromJson(arg0.toString(), QQUserInfo.class);
					if ( inforeqp.nickname != null && inforeqp.ret == 0 ){
						AccountInfo tmp = AccountInfo.getInstance().copy();
						inforeqp.saveToAccountInfo(tmp);
						updateAccountToServer(tmp);
					} else {
						Bog.toast(R.string.failed);
					}
				}
				@Override
				public void onCancel() {
				}
			});
		} else {
			Bog.toast(R.string.failed);
		}
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
		String unset =  getString(R.string.unset);
		
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
	
	void updateAccountToServer(final AccountInfo tmp){
		SContactAsyncHttpClient.post(AccountParams.getUpdateParams(tmp), null,
				new AsyncHttpResponseHandler(){
					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
						Bog.toast(R.string.failed);
					}
					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						AccountInfo.setAccountInfo(tmp);
						AccountInfo.getInstance().saveToPref();
						refreshInfo();
					}
		});
	}
}
