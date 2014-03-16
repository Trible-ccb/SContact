package com.trible.scontact.components.activity;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.trible.scontact.R;
import com.trible.scontact.models.AccountInfo;

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
	}
	
	@Override
	public void onAnimationStart(Animation animation) {
		
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		int r = (int) (( Math.random() * 100) % 2);
		if ( r == 0 ){
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