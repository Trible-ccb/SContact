package com.trible.scontact.components.activity;

import org.apache.http.Header;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.trible.scontact.BuildConfig;
import com.trible.scontact.R;
import com.trible.scontact.components.widgets.CustomPasswordInput;
import com.trible.scontact.components.widgets.CustomTextInput;
import com.trible.scontact.components.widgets.LoadingDialog;
import com.trible.scontact.networks.SContactAsyncHttpClient;
import com.trible.scontact.networks.params.AccountParams;
import com.trible.scontact.pojo.AccountInfo;
import com.trible.scontact.pojo.ErrorInfo;
import com.trible.scontact.pojo.GsonHelper;
import com.trible.scontact.utils.Bog;
import com.trible.scontact.utils.StringUtil;

public class SignInUpActivity extends CustomSherlockFragmentActivity 
								implements OnClickListener,OnPageChangeListener {
	
	LayoutInflater mInflater;
	ViewPager mViewPager;
	SignInUpAdapter mViewPagerAdapter;
	SignInHandler mSignInHandler;
	SignUpHandler mSignUpHandler;
	LoadingDialog mDialog;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_sign_in_out_layout);
		setTitle(R.string.sign_title);
		
		mInflater = LayoutInflater.from(this);
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPagerAdapter = new SignInUpAdapter();
		mViewPager.setAdapter(mViewPagerAdapter);
		mViewPager.setOnPageChangeListener(this);
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
		TextView mForget;
		CustomTextInput mNameInput;
		CustomPasswordInput mPwdInput;
		
		public CustomTextInput getmNameInput() {
			return mNameInput;
		}
		public void setmName(String name) {
			mNameInput.getmEditText().setText(name);
		}
		
		public SignInHandler(View v) {
			mNameInput = new CustomTextInput(v.findViewById(R.id.username_layout));
			mPwdInput = new CustomPasswordInput(v.findViewById(R.id.password_layout));
			mSignInBtn = (Button) v.findViewById(R.id.btn_sign_in);
			mForget = (TextView) v.findViewById(R.id.forget_password);
			mSignInBtn.setOnClickListener(SignInUpActivity.this);
			mNameInput.getImageButton().setVisibility(View.GONE);
			mNameInput.getmEditText().setHint(R.string.hint_username);
			mPwdInput.getmEditText().setHint(R.string.hint_password);
			mSignInBtn.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					if ( BuildConfig.DEBUG ){
						String s = mNameInput.getmEditText().getText().toString();
						SContactApplication.setIP(s);
						Bog.toast(s);
					}
					return false;
				}
			});
		}
		public void signIn(){
			String s = mNameInput.getmEditText().getText().toString();
			String p = mPwdInput.getmEditText().getText().toString();
			if ( !StringUtil.isValidName(s) ){
				Bog.toast(StringUtil.catStringFromResId(
						SignInUpActivity.this, R.string.username_lable,R.string.invalid));
				return;
			}
			if ( !StringUtil.isValidPwd(p) ){
				Bog.toast(StringUtil.catStringFromResId(
						SignInUpActivity.this, R.string.password_lable,R.string.invalid));
				return;
			}
			mDialog = new LoadingDialog(SignInUpActivity.this);
			mDialog.setTipText(R.string.waiting);
			SContactAsyncHttpClient.post(
					AccountParams.getLoginParams(s, StringUtil.MD5(p)),
					null, 
					new AsyncHttpResponseHandler(){
						@Override
						public void onStart() {
							super.onStart();
							mDialog.show();
						}
						@Override
						public void onFinish() {
							super.onFinish();
							mDialog.getDialog().dismissDialogger();
							if ( AccountInfo.getInstance().getId() == null
									|| AccountInfo.getInstance().isSignOut()){
								Bog.toast(R.string.invalid);
							} else {
								simpleDisplayActivity(SContactMainActivity.class);
								finish();
							}
						}
						@Override
						public void onSuccess(int arg0, Header[] arg1,
								byte[] arg2) {
							super.onSuccess(arg0, arg1, arg2);
							try {
								AccountInfo result = GsonHelper.getInfoFromJson(arg2, AccountInfo.class);
								if (result != null ) {
									Long id = result.getId();
									if ( id != null ){
										AccountInfo.setAccountInfo(result);
										result.saveToPref();
									} else {
										ErrorInfo msg = GsonHelper.getInfoFromJson(arg2, ErrorInfo.class);
										Bog.toast("failed!\n" + msg);
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] arg2, Throwable arg3) {
							super.onFailure(arg0, arg1, arg2, arg3);
							Bog.toast(getString(R.string.connect_server_err));
						}
					});
		}
	}
	
	class SignUpHandler{
		Button mSignUpBtn;
		CustomTextInput mNameInput,mPhoneInput;
		CustomPasswordInput mPwdInput;
		
		public CustomTextInput getmNameInput() {
			return mNameInput;
		}
		public void setmName(String name) {
			mNameInput.getmEditText().setText(name);
		}
		public CustomTextInput getmPhoneInput() {
			return mPhoneInput;
		}
		public void setmPhoneInput(CustomTextInput mPhoneInput) {
			this.mPhoneInput = mPhoneInput;
		}
		public SignUpHandler(View v) {
			mSignUpBtn = (Button) v.findViewById(R.id.btn_sign_up);
			mSignUpBtn.setOnClickListener(SignInUpActivity.this);
			mNameInput = new CustomTextInput(v.findViewById(R.id.username_layout));
			mPhoneInput = new CustomTextInput(v.findViewById(R.id.email_layout));
			mPwdInput = new CustomPasswordInput(v.findViewById(R.id.password_layout));
			mNameInput.getImageButton().setVisibility(View.GONE);
			mPhoneInput.getImageButton().setVisibility(View.GONE);
			mNameInput.getmEditText().setHint(R.string.hint_username);
			mPwdInput.getmEditText().setHint(R.string.hint_password);
			mPhoneInput.getmEditText().setHint(R.string.hint_phone);
		}
		public void signUp(){
			String s = mNameInput.getmEditText().getText().toString();
			String p = mPwdInput.getmEditText().getText().toString();
			String e = mPhoneInput.getmEditText().getText().toString();
			
			if ( !StringUtil.isValidPwd(s) ){
				Bog.toast(StringUtil.catStringFromResId(
						SignInUpActivity.this, R.string.username_lable,R.string.invalid,R.string.username_rule));
				return;
			}
			if ( !StringUtil.isValidPwd(p) ){
				Bog.toast(StringUtil.catStringFromResId(
						SignInUpActivity.this, R.string.password_lable,R.string.invalid,R.string.password_rule));
				return;
			}
			mDialog = new LoadingDialog(SignInUpActivity.this);
			mDialog.setTipText(R.string.waiting);
			SContactAsyncHttpClient.post(
					AccountParams.getRegisterParams(s, StringUtil.MD5(p), e), 
					null, 
					new AsyncHttpResponseHandler(){
						@Override
						public void onStart() {
							super.onStart();
							mDialog.show();
						}
						@Override
						public void onFinish() {
							super.onFinish();
							mDialog.getDialog().dismissDialogger();
						}
						@Override
						public void onSuccess(int arg0, Header[] arg1,
								byte[] arg2) {
							super.onSuccess(arg0, arg1, arg2);
							try {
								AccountInfo result = GsonHelper.getInfoFromJson(arg2, AccountInfo.class);
								if (result != null ) {
									Long id = result.getId();
									if ( id != null ){
										AccountInfo.setAccountInfo(result);
										mViewPager.setCurrentItem(0,true);
									} else {
										ErrorInfo msg = GsonHelper.getInfoFromJson(arg2, ErrorInfo.class);
										Bog.toast("failed!\n" + msg);
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] arg2, Throwable arg3) {
							super.onFailure(arg0, arg1, arg2, arg3);
							Bog.toast(getString(R.string.connect_server_err));
						}
					});
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_sign_up:
			mSignUpHandler.signUp();
			break;
		case R.id.btn_sign_in:
			mSignInHandler.signIn();
			break;
		default:
			break;
		}

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int arg0) {
		switch (arg0) {
			case 0:
				String tmp = AccountInfo.getInstance().getDisplayName();
				if ( tmp == null ){
					tmp = mSignUpHandler.getmNameInput().getmEditText().getText().toString();
				}
				mSignInHandler.setmName(tmp);
				break;
			case 1:
				String tmp2 = mSignInHandler.getmNameInput().getmEditText().getText().toString();
				mSignUpHandler.setmName(tmp2);
				break;
			default:
				break;
		}
	}
}
