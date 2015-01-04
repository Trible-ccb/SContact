package com.trible.scontact.components.activity;

import android.R.mipmap;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.trible.scontact.R;
import com.trible.scontact.pojo.AccountInfo;

public class IntroActivity extends CustomSherlockFragmentActivity implements AnimationListener{

	AccountInfo mInfo;
	ImageView mIntroImage;
	
	Animation mAnimIn;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_intro);
		mAnimIn = AnimationUtils.loadAnimation(this, R.anim.zoom_enter);
		mAnimIn.setDuration(0);
		mAnimIn.setAnimationListener(this);
		mIntroImage = (ImageView) findViewById(R.id.logo_first);
		mIntroImage.startAnimation(mAnimIn);
		openNotifyPusher();
	}
	void refreshUser(){
		if ( mInfo != null && mInfo.getSessionToken() == null ){
			mInfo.refreshInBackground(null);
		}
	}
	void openNotifyPusher(){
//		use umeng message pusher
//		PushAgent.getInstance(this).enable();
	}
	@Override
	public void onAnimationStart(Animation animation) {
		
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		if ( !AccountInfo.getInstance().isSignOut() && AccountInfo.getInstance().isMobilePhoneVerified()){
			simpleDisplayActivity(SContactMainActivity.class);
		} else {
			simpleDisplayActivity(SignInUpActivity.class);
		}
		finish();
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
	}
}
