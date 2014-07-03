package com.trible.scontact.components.activity;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.trible.scontact.R;
import com.trible.scontact.networks.SContactAsyncHttpClient;
import com.trible.scontact.pojo.AccountInfo;
import com.trible.scontact.value.GlobalValue;
import com.umeng.message.PushAgent;
import com.umeng.update.UmengUpdateAgent;

public class IntroActivity extends CustomSherlockFragmentActivity implements AnimationListener{

	AccountInfo mInfo;
	ImageView mIntroImage;
	
	Animation mAnimIn;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_intro);
		mAnimIn = AnimationUtils.loadAnimation(this, R.anim.zoom_enter);
		mAnimIn.setAnimationListener(this);
		mIntroImage = (ImageView) findViewById(R.id.logo_first);
		mIntroImage.startAnimation(mAnimIn);
		mInfo = AccountInfo.getInstance();
		openNotifyPusher();
		UmengUpdateAgent.update(this);
		SContactAsyncHttpClient.refreshCookie();
		
	}

	void openNotifyPusher(){
//		use umeng message pusher
		PushAgent.getInstance(this).enable();
	}
	@Override
	public void onAnimationStart(Animation animation) {
		
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		if ( GlobalValue.USTATUS_NORMAL.equals(mInfo.getStatus()) ){
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
