package com.trible.scontact.components.activity;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.trible.scontact.R;

public class SignInUpActivity extends CustomSherlockFragmentActivity 
								implements OnClickListener {
	
	LayoutInflater mInflater;
	ViewPager mViewPager;
	SignInUpAdapter mViewPagerAdapter;
	SignInHandler mSignInHandler;
	SignUpHandler mSignUpHandler;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_sign_in_out_layout);
		setTitle(R.string.sign_title);
		
		mInflater = LayoutInflater.from(this);
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPagerAdapter = new SignInUpAdapter();
		mViewPager.setAdapter(mViewPagerAdapter);
	}
	
	class SignInUpAdapter extends PagerAdapter{

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			super.destroyItem(container, position, object);
		}

		@Override
		public int getItemPosition(Object object) {
			return super.getItemPosition(object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			ViewPager mViewPager = (ViewPager) container;
			View v = null;
			switch (position) {
				case 0:
					v = mInflater.inflate(R.layout.fragment_sign_in, null);
					mSignInHandler = new SignInHandler(v);
					break;
				case 1:
					v = mInflater.inflate(R.layout.fragment_sign_up, null);
					mSignUpHandler = new SignUpHandler(v);
					break;
				default:
					break;
			}
			mViewPager.addView(v);
			return v;
		}
		
		
	}
	
	class SignInHandler{
		Button mSignInBtn;
		TextView mName,mPassword,mForget;
		
		public SignInHandler(View v) {
			mSignInBtn = (Button) v.findViewById(R.id.btn_sign_in);
			mName = (TextView) v.findViewById(R.id.edt_input_box);
			mPassword = (TextView) v.findViewById(R.id.edt_input_password);
			mForget = (TextView) v.findViewById(R.id.forget_password);
			mSignInBtn.setOnClickListener(SignInUpActivity.this);
		}
	}
	
	class SignUpHandler{
		Button mSignUpBtn;
		TextView mName,mPassword,mEmail;
		
		public SignUpHandler(View v) {
			mSignUpBtn = (Button) v.findViewById(R.id.btn_sign_up);
			mName = (TextView) v.findViewById(R.id.username_layout).findViewById(R.id.edt_input_box);
			mPassword = (TextView) v.findViewById(R.id.edt_input_password);
			mEmail = (TextView) v.findViewById(R.id.email_layout).findViewById(R.id.edt_input_box);
			mSignUpBtn.setOnClickListener(SignInUpActivity.this);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_sign_up:
			break;
		case R.id.btn_sign_in:
			break;

		default:
			break;
		}
		simpleDisplayActivity(SContactMainActivity.class);
		finish();
	}
}
