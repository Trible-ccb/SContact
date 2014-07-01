package com.trible.scontact.components.activity;

import org.apache.http.Header;

import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import ccb.java.android.utils.encoder.SecurityMethod;

import com.actionbarsherlock.view.Window;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tencent.tauth.Tencent;
import com.trible.scontact.BuildConfig;
import com.trible.scontact.R;
import com.trible.scontact.components.widgets.CustomPasswordInput;
import com.trible.scontact.components.widgets.CustomTextInput;
import com.trible.scontact.components.widgets.LoadingDialog;
import com.trible.scontact.components.widgets.NotifyHelper;
import com.trible.scontact.networks.SContactAsyncHttpClient;
import com.trible.scontact.networks.params.AccountParams;
import com.trible.scontact.pojo.AccountInfo;
import com.trible.scontact.pojo.ContactInfo;
import com.trible.scontact.pojo.ErrorInfo;
import com.trible.scontact.pojo.GsonHelper;
import com.trible.scontact.thirdparty.qq.QQLoginListener;
import com.trible.scontact.thirdparty.qq.TencentQQHelper;
import com.trible.scontact.thirdparty.umeng.UmengController;
import com.trible.scontact.utils.Bog;
import com.trible.scontact.utils.DeviceUtil;
import com.trible.scontact.utils.StringUtil;
import com.trible.scontact.value.GlobalValue;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengRegistrar;
import com.umeng.socialize.bean.Gender;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SnsAccount;
import com.umeng.socialize.bean.SocializeConfig;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.SocializeUser;
import com.umeng.socialize.controller.listener.SocializeListeners.FetchUserListener;
import com.umeng.socialize.controller.listener.SocializeListeners.SocializeClientListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.net.w;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;

public class SignInUpActivity extends CustomSherlockFragmentActivity 
								implements OnClickListener,OnPageChangeListener {
	
	LayoutInflater mInflater;
	ViewPager mViewPager;
	SignInUpAdapter mViewPagerAdapter;
	SignInHandler mSignInHandler;
	SignUpHandler mSignUpHandler;
	LoginByThirdParty mThirdParty;
	
	LoadingDialog mDialog;
	RadioGroup mIndicator;
	TextView mTipText;
	
	@Override
	protected void onCreate(Bundle arg0) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(arg0);
		setContentView(R.layout.activity_sign_in_out_layout);
		setBarBackgroup(R.color.transparent);
		setLogo(R.color.transparent);
		DeviceUtil.setAlpha(findViewById(R.id.pager), 0.9f);
		mInflater = LayoutInflater.from(this);
		mIndicator = (RadioGroup) findViewById(R.id.indicators);
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPagerAdapter = new SignInUpAdapter();
		mViewPager.setOnPageChangeListener(this);
		mViewPager.setAdapter(mViewPagerAdapter);
		setTitle(R.string.sign_in,R.color.white_f0);
		mTipText = (TextView) findViewById(R.id.tip);
		mTipText.setOnClickListener(this);
		mIndicator.setVisibility(View.GONE);
		hideTip();
		UmengController.getService().getConfig()
		.supportQQPlatform(this, GlobalValue.QQAPPID, GlobalValue.QQAPPKEY, "");

	}
	private void showTip(String tip){
		mTipText.setText(tip);
		mTipText.setVisibility(View.VISIBLE);
	}
	private void hideTip(){
		mTipText.setVisibility(View.GONE);
	}
	class SignInUpAdapter extends PagerAdapter{
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
//			super.destroyItem(container, position, object);
		}

		@Override
		public int getItemPosition(Object object) {
			return super.getItemPosition(object);
		}

		@Override
		public int getCount() {
			return 1;
		}
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			ViewPager mViewPager = (ViewPager) container;
			View v = null;
			switch (position) {
				case 0:
					v = mInflater.inflate(R.layout.fragment_thirdparty, null);
					mThirdParty =  new LoginByThirdParty(v);
					break;
				case 1:
					v = mInflater.inflate(R.layout.fragment_sign_up, null);
					mSignUpHandler = new SignUpHandler(v);
					break;
				case 2:
					v = mInflater.inflate(R.layout.fragment_sign_in, null);
					mSignInHandler = new SignInHandler(v);
					break;
				default:
					break;
			}
			mViewPager.addView(v);
			return v;
		}
		
		
	}
	@Deprecated
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
					return true;
				}
			});
			mForget.setOnClickListener(SignInUpActivity.this);
			mForget.setVisibility(View.GONE);
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
			final String device_token = UmengRegistrar.getRegistrationId(SignInUpActivity.this);
			if ( TextUtils.isEmpty(device_token) ){
				Bog.toast(
						StringUtil.catStringFromResId(
								SignInUpActivity.this,
								R.string.notify_register,R.string.failed));
			}
			mDialog = new LoadingDialog(SignInUpActivity.this);
			mDialog.setTipText(R.string.waiting);
			hideTip();
			SContactAsyncHttpClient.post(
					AccountParams.getLoginParams(s, SecurityMethod.encryptSHA(p+s),device_token),
					null, 
					new AsyncHttpResponseHandler(){
						@Override
						public void onStart() {
							mDialog.show();
						}
						@Override
						public void onFinish() {
							mDialog.getDialog().dismissDialogger();
						}
						@Override
						public void onSuccess(int arg0, Header[] arg1,
								byte[] arg2) {
							try {
								AccountInfo result = GsonHelper.getInfoFromJson(arg2, AccountInfo.class);
								if (result != null ) {
									Long id = result.getId();
									if ( id != null ){
										AccountInfo.setAccountInfo(result);
										result.saveToPref();
										ContactInfo.clear();
										if ( !AccountInfo.getInstance().isSignOut() ){
											simpleDisplayActivity(SContactMainActivity.class);
											finish();
										}
									} else {
										showTip(getString(R.string.invalid_account));
									}
								} else {
									Bog.toastErrorInfo(arg2);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] arg2, Throwable arg3) {
							showTip(getString(R.string.connect_server_err));
						}
					});
		}
	}
	@Deprecated
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
			
			if ( !StringUtil.isValidName(s) ){
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
			hideTip();
			SContactAsyncHttpClient.post(
					AccountParams.getRegisterParams(
							s,SecurityMethod.encryptSHA(p+s), e), 
					null, 
					new AsyncHttpResponseHandler(){
						@Override
						public void onStart() {
							mDialog.show();
						}
						@Override
						public void onFinish() {
							mDialog.getDialog().dismissDialogger();
						}
						@Override
						public void onSuccess(int arg0, Header[] arg1,
								byte[] arg2) {
							try {
								AccountInfo result = GsonHelper.getInfoFromJson(arg2, AccountInfo.class);
								if (result != null ) {
									Long id = result.getId();
									if ( id != null ){
										AccountInfo.setAccountInfo(result);
										mViewPager.setCurrentItem(0,true);
										Bog.toast(StringUtil.catStringFromResId(
												SignInUpActivity.this, R.string.sign_up,R.string.success));
									} else {
										ErrorInfo err = GsonHelper.getInfoFromJson(arg2, ErrorInfo.class);
										showTip(err.getMessgae().toLowerCase());
									}
								} else {
									Bog.toastErrorInfo(arg2);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] arg2, Throwable arg3) {
							showTip(getString(R.string.connect_server_err));
						}
					});
		}
	}

	class LoginByThirdParty{
		
		View mLoginQQView;
		View mLoginSinaView;
		
		public LoginByThirdParty(View root){
			mLoginQQView = (View) root.findViewById(R.id.login_tencent_qq);
			mLoginSinaView = (View) root.findViewById(R.id.login_sina);
			mLoginSinaView.setOnClickListener(SignInUpActivity.this);
			mLoginQQView.setOnClickListener(SignInUpActivity.this);
		}
	}
	void loginThirdParty(final SHARE_MEDIA m){
		hideTip();
		UmengController.getLoginService().doOauthVerify(
				SignInUpActivity.this, m, new UMAuthListener() {
			@Override
			public void onStart(SHARE_MEDIA arg0) {
			}
			@Override
			public void onError(SocializeException arg0, SHARE_MEDIA arg1) {
				showTip(arg0.getErrorCode() + ":" +arg0.getMessage());
			}
			
			@Override
			public void onComplete(Bundle value, SHARE_MEDIA arg1) {
				if (value != null && !TextUtils.isEmpty(value.getString("uid"))) {
				} else {
					showTip(getString(R.string.failed));
					return;
				}
				UmengController.getLoginService().login(SignInUpActivity.this, m, new SocializeClientListener() {
					@Override
					public void onStart() {
						PushAgent.getInstance(SignInUpActivity.this).enable();
					}
					@Override
					public void onComplete(int arg0, SocializeEntity arg1) {
						if ( arg0 == 200 ){
							UmengController.getService().getUserInfo(
									SignInUpActivity.this, new FetchUserListener() {
								
								@Override
								public void onStart() {
									mDialog = new LoadingDialog(SignInUpActivity.this);
									mDialog.setTipText(R.string.loading);
									mDialog.show();
								}
								
								@Override
								public void onComplete(int arg0, SocializeUser arg1) {
									if ( arg0 != 200 ){
										showTip(StringUtil.catStringFromResId(
												SignInUpActivity.this,
												R.string.sign_in,R.string.failed));
										return;
									}
									final String device_token = UmengRegistrar.getRegistrationId(SignInUpActivity.this);
									SnsAccount a = arg1.mLoginAccount;
									AccountInfo wrap = new AccountInfo();
									wrap.setDisplayName(a.getUserName());
//									wrap.setBirthday(Long.valueOf(a.getBirthday()));
									wrap.setThirdPartyId(a.getUsid());
									if ( TextUtils.isEmpty(device_token) ){
										Bog.toast(
												StringUtil.catStringFromResId(
														SignInUpActivity.this,
														R.string.notify_register,R.string.failed));
									} else {
										wrap.setNotifyId(device_token);
									}
									
									wrap.setPhotoUrl(a.getAccountIconUrl());
									wrap.setDescription(a.getProfileUrl());
									wrap.setPassword(SecurityMethod.encryptSHA(a.getUsid()+a.getUserName()));
									int g = GlobalValue.UGENDER_UNSET;
									//友盟bug,如果是新浪微博用户，用户性别搞反了。
									if ( SHARE_MEDIA.SINA.equals(m) ){
										switch (a.getGender()) {
										case FEMALE:
											g = GlobalValue.UGENDER_MALE;
											break;
										case MALE:
											g = GlobalValue.UGENDER_FEMALE;
											break;
										default:
											break;
										}
									} else {
										switch (a.getGender()) {
										case FEMALE:
											g = GlobalValue.UGENDER_FEMALE;
											break;
										case MALE:
											g = GlobalValue.UGENDER_MALE;
											break;
										default:
											break;
										}
									}
									
									wrap.setGender(g);

									SContactAsyncHttpClient.post(
											AccountParams.getLoginWithSocialParams(wrap),
											null,
											new AsyncHttpResponseHandler(){
												@Override
												public void onFailure(int arg0,
														Header[] arg1, byte[] arg2,
														Throwable arg3) {
													showTip(getString(R.string.connect_server_err));
												}
												@Override
												public void onFinish() {
													mDialog.getDialog().dismissDialogger();
												}
												@Override
												public void onSuccess(int arg0,
														Header[] arg1, byte[] arg2) {
													if ( !mDialog.getDialog().isShowing() )return;
													AccountInfo result = GsonHelper.getInfoFromJson(arg2, AccountInfo.class);
													if ( result != null ){
														Long id = result.getId();
														if ( id != null ){
															AccountInfo.setAccountInfo(result);
															result.saveToPref();
															ContactInfo.clear();
															if ( !AccountInfo.getInstance().isSignOut() ){
																simpleDisplayActivity(SContactMainActivity.class);
																finish();
															}
														} else {
															showTip(getString(R.string.invalid_account));
														}
													} else {
														Bog.toastErrorInfo(arg2);
													}
													
												}
									});
									
								}
							});
						}
					}
				});
			}
			
			@Override
			public void onCancel(SHARE_MEDIA arg0) {
				showTip(getString(R.string.cancel));
			}
		});
		
		
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.login_tencent_qq:
				loginThirdParty(SHARE_MEDIA.QQ);
				break;
			case R.id.login_sina:
				loginThirdParty(SHARE_MEDIA.SINA);
				break;
			case R.id.btn_sign_up:
				mSignUpHandler.signUp();
				break;
			case R.id.btn_sign_in:
				mSignInHandler.signIn();
				break;
			case R.id.forget_password:
				break;
			case R.id.tip:
				hideTip();
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
				setTitle(R.string.sign_in,R.color.white_f0);
				mIndicator.check(R.id.radio0);
				break;
			case 1:
				String tmp2 = mSignInHandler.getmNameInput().getmEditText().getText().toString();
				mSignUpHandler.setmName(tmp2);
				setTitle(R.string.sign_up,R.color.white_f0);
				mIndicator.check(R.id.radio1);
				break;
			default:
				break;
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		/**使用SSO授权必须添加如下代码 */
	    UMSsoHandler ssoHandler = UmengController.getService().getConfig().getSsoHandler(requestCode) ;
	    if(ssoHandler != null){
	       ssoHandler.authorizeCallBack(requestCode, resultCode, data);
	    }
	}
}
