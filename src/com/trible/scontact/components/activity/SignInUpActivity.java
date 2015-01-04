package com.trible.scontact.components.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.RadioGroup;
import android.widget.TextView;

import com.actionbarsherlock.view.Window;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVMobilePhoneVerifyCallback;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.RequestMobileCodeCallback;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.SignUpCallback;
import com.avos.avoscloud.UpdatePasswordCallback;
import com.trible.scontact.R;
import com.trible.scontact.components.widgets.CustomPasswordInput;
import com.trible.scontact.components.widgets.CustomTextInput;
import com.trible.scontact.components.widgets.LoadingDialog;
import com.trible.scontact.pojo.AccountInfo;
import com.trible.scontact.pojo.ContactInfo;
import com.trible.scontact.pojo.ContactTypes;
import com.trible.scontact.thirdparty.umeng.UmengController;
import com.trible.scontact.utils.Bog;
import com.trible.scontact.utils.DeviceUtil;
import com.trible.scontact.utils.SecurityMethod;
import com.trible.scontact.utils.StringUtil;
import com.trible.scontact.value.GlobalValue;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SnsAccount;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.SocializeUser;
import com.umeng.socialize.controller.listener.SocializeListeners.FetchUserListener;
import com.umeng.socialize.controller.listener.SocializeListeners.SocializeClientListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.exception.SocializeException;

/**
 * @author Trible Chen
 * 登录与注册只需要简单的用户名/手机号与密码 不使用第三方登录
 * 这里负责注册，登录，找回密码。
 *{@link https://cn.avoscloud.com/docs/android_guide.html#用户}
 */
public class SignInUpActivity extends CustomSherlockFragmentActivity 
								implements OnClickListener,OnPageChangeListener {
	
	LayoutInflater mInflater;
	ViewPager mViewPager;
	SignInUpAdapter mViewPagerAdapter;
	SignInHandler mSignInHandler;
	SignUpHandler mSignUpHandler,mForgetPwdHandler;//忘记密码共用一个类
	LoginByThirdParty mThirdParty;
	
	String device_token;
	
	LoadingDialog mDialog;
	RadioGroup mIndicator;
	TextView mTipText;
	Map<String, Boolean> isNumberFetchedValidCodeAndSignedUp = new HashMap<String, Boolean>();
	int refetchValidCodeInterval = 60;//重新获取验证码时间间隔（秒）
	List<View> pagerViews = new ArrayList<View>();
	String signUpSuccessPhoneNumber = "";
	
	@Override
	protected void onCreate(Bundle arg0) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(arg0);
		setContentView(R.layout.activity_sign_in_out_layout);
		setBarBackground(R.color.transparent);
		setLogo(R.color.transparent);
		DeviceUtil.setAlpha(findViewById(R.id.pager), 0.9f);
		mInflater = LayoutInflater.from(this);
		mIndicator = (RadioGroup) findViewById(R.id.indicators);
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPagerAdapter = new SignInUpAdapter();
		mViewPager.setOnPageChangeListener(this);
		
		setTitle(R.string.sign_in,R.color.white_f0);
		mTipText = (TextView) findViewById(R.id.tip);
		mTipText.setOnClickListener(this);
		mIndicator.setVisibility(View.GONE);
		View v1 = mInflater.inflate(R.layout.fragment_sign_in, null);
		mSignInHandler = new SignInHandler(v1);
		View v2 = mInflater.inflate(R.layout.fragment_sign_up, null);
		mSignUpHandler = new SignUpHandler(v2);
		View v3 = mInflater.inflate(R.layout.fragment_sign_up, null);
		mForgetPwdHandler = new SignUpHandler(v3);
		pagerViews.add(v1);
		pagerViews.add(v2);
		pagerViews.add(v3);
		mViewPager.setAdapter(mViewPagerAdapter);
		hideTip();
		UmengController.getService().getConfig()
		.supportQQPlatform(this, GlobalValue.QQAPPID, GlobalValue.QQAPPKEY, "");

	}
	private void showTip(String tip){
		mTipText.setText(tip);
		mTipText.setVisibility(View.VISIBLE);
//		mThirdParty.enableButtons();
		if ( mDialog != null && mDialog.getDialog() != null){
			mDialog.getDialog().dismissDialogger();
		}
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
			container.removeView(pagerViews.get(position));
		}

		@Override
		public int getItemPosition(Object object) {
//			return super.getItemPosition(object);
			return pagerViews.indexOf(object);
		}

		@Override
		public int getCount() {
			return pagerViews.size();
		}
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			ViewPager mViewPager = (ViewPager) container;
			View v = pagerViews.get(position);
			mViewPager.addView(v);
			return v;
		}
	}
	void goIntoMain(){
		ContactInfo.clear();
		if ( !AccountInfo.getInstance().isSignOut() ){
			if ( !AccountInfo.getInstance().isMobilePhoneVerified() ){
				showTip("Account is not active.");
				return;
			}
			final AVInstallation install = AVInstallation.getCurrentInstallation();
			install.saveInBackground();
			AccountInfo.getInstance().setInstallationId(install.getInstallationId());
			AccountInfo.getInstance().setFetchWhenSave(true);
			AccountInfo.getInstance().saveInBackground();
			simpleDisplayActivity(SContactMainActivity.class);
			finish();
		} else {
			showTip(getString(R.string.invalid_account));
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
			v.findViewById(R.id.go_sign_up).setOnClickListener(SignInUpActivity.this);
			mNameInput = new CustomTextInput(v.findViewById(R.id.username_layout));
			mPwdInput = new CustomPasswordInput(v.findViewById(R.id.password_layout));
			mSignInBtn = (Button) v.findViewById(R.id.btn_sign_in);
			mForget = (TextView) v.findViewById(R.id.forget_password);
			mSignInBtn.setOnClickListener(SignInUpActivity.this);
			mNameInput.getImageButton().setVisibility(View.GONE);
			mNameInput.getmEditText().setHint(R.string.hint_login_username);
			mPwdInput.getmEditText().setHint(R.string.hint_password);
			mSignInBtn.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					return true;
				}
			});
			mForget.setOnClickListener(SignInUpActivity.this);
//			mForget.setVisibility(View.GONE);
		}
		public void signIn(){
			final String s = mNameInput.getmEditText().getText().toString();
			final String p = mPwdInput.getmEditText().getText().toString();
			
			if ( !StringUtil.isValidName(s) ){
				Bog.toast(StringUtil.catStringFromResId(
						R.string.username_lable,R.string.invalid));
				return;
			}
			if ( !StringUtil.isValidPwd(p) ){
				Bog.toast(StringUtil.catStringFromResId(
						R.string.password_lable,R.string.invalid));
				return;
			}
			mDialog = new LoadingDialog(SignInUpActivity.this);
			mDialog.setTipText(R.string.waiting);
			hideTip();
			mDialog.show();
			AVUser.loginByMobilePhoneNumberInBackground(s, p, 
					new LogInCallback<AccountInfo>() {
				@Override
				public void done(AccountInfo arg0, AVException arg1) {
					mDialog.getDialog().dismissDialogger();
					if ( arg1 != null ){//尝试使用用户名登录
						AVUser.logInInBackground(s, p, new LogInCallback<AccountInfo>() {
							@Override
							public void done(AccountInfo arg0, AVException arg1) {
								if( arg1 == null ){
									AccountInfo.setAccountInfo(arg0);
									goIntoMain();
								} else {
									showTip(arg1.getMessage());
								}
							}
						},AccountInfo.class);
					} else {
						AccountInfo.setAccountInfo(arg0);
						goIntoMain();
					}
				}
			},AccountInfo.class);
		}
	}
	class SignUpHandler{
		Button mSignUpBtn,mFetchValidCodeBtn;
		CustomTextInput mNameInput,mPhoneInput,mValidCodeInput;
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
			mFetchValidCodeBtn = (Button) v.findViewById(R.id.btn_fetch_valid_code);
			mSignUpBtn.setText(R.string.sign_up);
			mSignUpBtn.setOnClickListener(SignInUpActivity.this);
			mFetchValidCodeBtn.setOnClickListener(SignInUpActivity.this);
			v.findViewById(R.id.go_sign_in).setOnClickListener(SignInUpActivity.this);
			mNameInput = new CustomTextInput(v.findViewById(R.id.username_layout));
			mPhoneInput = new CustomTextInput(v.findViewById(R.id.phone_input_layout));
			mPwdInput = new CustomPasswordInput(v.findViewById(R.id.password_layout));
			mValidCodeInput = new CustomTextInput(v.findViewById(R.id.valid_code_layout));
			
			mNameInput.getImageButton().setVisibility(View.GONE);
			mPhoneInput.getImageButton().setVisibility(View.GONE);
			mValidCodeInput.getImageButton().setVisibility(View.GONE);
			mNameInput.getmEditText().setHint(R.string.hint_signup_username);
			mPwdInput.getmEditText().setHint(R.string.hint_signup_password);
			mPhoneInput.getmEditText().setHint(R.string.hint_phone);
			mValidCodeInput.getmEditText().setHint(R.string.hint_valid_code);
		}
//		注册并获取验证码
		public void signUpWithPhoneNumberAndFetchValidCode(){
			
			final String mobilePhoneNumber = mPhoneInput.getmEditText().getText().toString();
			if ( !StringUtil.isValidPhoneNumber(mobilePhoneNumber)){
				Bog.toast(R.string.pls_input_correct_phone);
				return;
			}
			String s = mNameInput.getmEditText().getText().toString();
			String p = mPwdInput.getmEditText().getText().toString();
			
			if ( !StringUtil.isValidName(s) ){
				Bog.toast(StringUtil.catStringFromResId(
						R.string.username_lable,R.string.invalid,R.string.username_rule));
				return;
			}
			if ( !StringUtil.isValidPwd(p) ){
				Bog.toast(StringUtil.catStringFromResId(
						R.string.password_lable,R.string.invalid,R.string.password_rule));
				return;
			}
			signUpSuccessPhoneNumber = "";
			mDialog = new LoadingDialog(SignInUpActivity.this);
			mDialog.setTipText(R.string.waiting);
			hideTip();
			mDialog.show();
			
			final AccountInfo user = AccountInfo.getInstance();
			user.setFetchWhenSave(true);
			user.setUsername(s);
			user.setPassword(p);
			user.setPhoneNumber(mobilePhoneNumber);
			user.setDescription("");
			user.setPinyinname(StringUtil.converterToSpell(s));
			
			user.setStatus(GlobalValue.USTATUS_NORMAL);
			mSignUpBtn.setEnabled(false);
//			注册并且发送验证码
			AVQuery<AccountInfo> deleteNotVerify = AVQuery.getQuery(AccountInfo.class);
			deleteNotVerify.whereEqualTo("mobilePhoneNumber",mobilePhoneNumber);
			deleteNotVerify.whereEqualTo("mobilePhoneVerified", false);
			deleteNotVerify.deleteAllInBackground(new DeleteCallback() {
				@Override
				public void done(AVException arg0) {
					
				}
			});
			user.signUpInBackground(new SignUpCallback() {
				@Override
				public void done(AVException arg0) {
					mSignUpBtn.setEnabled(true);
					mDialog.getDialog().dismissDialogger();
					if ( arg0 == null ){
						showTip(getString(R.string.send_valid_code));
						countFetchValidCodeSeconds();
						mValidCodeInput.show();
						isNumberFetchedValidCodeAndSignedUp.put(mobilePhoneNumber, true);
						mSignUpBtn.setText(R.string.sign_up);
						AccountInfo.logOut();
					} else {
						showTip(arg0.getMessage());
					}
				}
			});
		}
//		重新获取验证码
		void reFetchValidCode(){
			String mobilePhoneNumber = mPhoneInput.getmEditText().getText().toString();
			if ( !StringUtil.isValidPhoneNumber(mobilePhoneNumber)){
				Bog.toast(R.string.pls_input_correct_phone);
				return;
			}
			mFetchValidCodeBtn.setEnabled(false);
			AVUser.requestMobilePhoneVerifyInBackground(
					mobilePhoneNumber,new RequestMobileCodeCallback() {
			      @Override
			      public void done(AVException e) {
			    	  mFetchValidCodeBtn.setEnabled(true);
			    	  if ( e == null ){
			    		  countFetchValidCodeSeconds();
			    		  showTip(getString(R.string.send_valid_code));
			    	  } else {
			    		  showTip(e.getMessage());
			    	  }
			      }
			});
		}
		void countFetchValidCodeSeconds(){
			Handler hander = new Handler(){
				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					int left = msg.what;
					if ( left == 0 ){
						mFetchValidCodeBtn.setText(R.string.fetch_valid_code);
						mFetchValidCodeBtn.setEnabled(true);
					} else {
						mFetchValidCodeBtn.setEnabled(false);
						mFetchValidCodeBtn.setText(
								String.format(getString(R.string.refetch_valid_code),
										left));
						sendEmptyMessageDelayed(--left, 1000);
					}
				}
			};
			hander.sendEmptyMessageDelayed(refetchValidCodeInterval, 0);
		}
		public void valifyPhoneNumber(){
			final String mobilePhoneNumber = mPhoneInput.getmEditText().getText().toString();
			String smsCode = mValidCodeInput.getmEditText().getText().toString();
			if ( TextUtils.isEmpty(smsCode) ){
				return;
			}
			mDialog = new LoadingDialog(SignInUpActivity.this);
			mDialog.setTipText(R.string.waiting);
			hideTip();
			mDialog.show();
			AVUser.verifyMobilePhoneInBackground(smsCode, new AVMobilePhoneVerifyCallback() {

			      @Override
			      public void done(AVException arg0) {
			    	  	mSignUpBtn.setEnabled(true);
						mDialog.getDialog().dismissDialogger();
						if ( arg0 == null ){
							ContactInfo nc = new ContactInfo();
							nc.setContact(mobilePhoneNumber);
							nc.setOwner(AccountInfo.getInstance());
							nc.setStatus(GlobalValue.CSTATUS_USED);
							nc.setType(ContactTypes.getInstance().getCellPhoneType());
//							nc.saveEventually();
							nc.saveInBackground(new SaveCallback() {
								@Override
								public void done(AVException arg0) {
									if ( arg0 == null ){
										
									} else {
										
									}
								}
							});
							signUpSuccessPhoneNumber = mobilePhoneNumber;
							mViewPager.setCurrentItem(0, true);
							Bog.toast(StringUtil.catStringFromResId(
									R.string.sign_up,R.string.success));
						} else {
							showTip(arg0.getMessage());
						}
			      }
			});
		}
		public void requestPasswordResetBySmsCode(){
			
			String mobilePhoneNumber = mPhoneInput.getmEditText().getText().toString();
			if ( !StringUtil.isValidPhoneNumber(mobilePhoneNumber)){
				Bog.toast(R.string.pls_input_correct_phone);
				return;
			}
			
			mDialog = new LoadingDialog(SignInUpActivity.this);
			mDialog.setTipText(R.string.waiting);
			hideTip();
			mDialog.show();
			mFetchValidCodeBtn.setEnabled(false);
			AVUser.requestPasswordResetBySmsCodeInBackground(
					mobilePhoneNumber,new RequestMobileCodeCallback() {
		          @Override
		          public void done(AVException e) {
		        	  mFetchValidCodeBtn.setEnabled(true);
			    	  if ( e == null ){
			    		  countFetchValidCodeSeconds();
			    		  showTip(getString(R.string.send_valid_code));
			    	  } else {
			    		  showTip(e.getMessage());
			    	  }
		          }
	        });
		}
		public void resetPasswordBySmsCode(){
			String newPassword = mPwdInput.getmEditText().getText().toString();
			String smsCode = mValidCodeInput.getmEditText().getText().toString();
			if ( TextUtils.isEmpty(smsCode) ){
				return;
			}
			if ( !StringUtil.isValidPwd(newPassword) ){
				Bog.toast(StringUtil.catStringFromResId(
						R.string.password_lable,R.string.invalid,R.string.password_rule));
				return;
			}
			mDialog = new LoadingDialog(SignInUpActivity.this);
			mDialog.setTipText(R.string.waiting);
			hideTip();
			mDialog.show();
			AVUser.resetPasswordBySmsCodeInBackground(smsCode,newPassword,
					new UpdatePasswordCallback() {
			      @Override
			      public void done(AVException e) {
			    	  mDialog.getDialog().dismissDialogger();
			        if(e == null){
			        	String s = StringUtil.catStringFromResId(R.string.reset_pwd,R.string.success);
			        	Bog.toast(s);
			        	showTip(s);
			        } else {
			        	showTip(e.getMessage());
			        	Bog.toast(e.getMessage());
			        }
			      }
			});
		}
	}
	@Deprecated
	class LoginByThirdParty{
		
		View mLoginQQView;
		View mLoginSinaView;
		
		public LoginByThirdParty(View root){
			mLoginQQView = (View) root.findViewById(R.id.login_tencent_qq);
			mLoginSinaView = (View) root.findViewById(R.id.login_sina);
			mLoginSinaView.setOnClickListener(SignInUpActivity.this);
			mLoginQQView.setOnClickListener(SignInUpActivity.this);
		}
		public void enableButtons(){
			mLoginQQView.setEnabled(true);
			mLoginSinaView.setEnabled(true);
		}
		public void disableButtons(){
			mLoginQQView.setEnabled(false);
			mLoginSinaView.setEnabled(false);
		}
	}
	void loginThirdParty(final SHARE_MEDIA m){
		hideTip();
		mThirdParty.disableButtons();
		mDialog = new LoadingDialog(SignInUpActivity.this);
		mDialog.setTipText(R.string.loading);
		mDialog.show();
		UmengController.getLoginService().doOauthVerify(
				SignInUpActivity.this, m, new UMAuthListener() {
					
			@Override
			public void onStart(SHARE_MEDIA arg0) {
				mThirdParty.disableButtons();
			}
			@Override
			public void onError(SocializeException arg0, SHARE_MEDIA arg1) {
				showTip(arg0.getErrorCode() + ":" + arg1.name());
				mThirdParty.enableButtons();
			}
			
			@Override
			public void onComplete(Bundle value, SHARE_MEDIA arg1) {
				mThirdParty.enableButtons();
				if (value != null && !TextUtils.isEmpty(value.getString("uid"))) {
					UmengController.getService().login(
							SignInUpActivity.this,
							m,
							new SocializeClientListener() {
						
						@Override
						public void onStart() {
							mThirdParty.disableButtons();
						}
						
						@Override
						public void onComplete(int arg0, SocializeEntity arg1) {
							mThirdParty.enableButtons();
							if ( arg0 == 200 ){
								UmengController.getService().getUserInfo(
										SignInUpActivity.this, new FetchUserListener() {
									@Override
									public void onStart() {
										mThirdParty.disableButtons();
									}
									
									@Override
									public void onComplete(int code, SocializeUser user) {
										mThirdParty.enableButtons();
										if ( code != 200 || user.mLoginAccount == null ){
											showTip(StringUtil.catStringFromResId(
													R.string.sign_in,R.string.failed));
											return;
										}
//										device_token = UmengRegistrar.getRegistrationId(SignInUpActivity.this);
										SnsAccount a = user.mLoginAccount;
										AccountInfo wrap = new AccountInfo();
										wrap.setDisplayName(a.getUserName());
//										wrap.setThirdPartyId(a.getUsid());
										if ( TextUtils.isEmpty(device_token) ){
											Bog.toast(
													StringUtil.catStringFromResId(
															R.string.notify_register,R.string.failed));
										} else {
//											wrap.setNotifyId(device_token);
										}
										wrap.setPhotoUrl(a.getAccountIconUrl());
										wrap.setDescription(a.getProfileUrl());
										wrap.setPassword(
												SecurityMethod.encryptSHA(a.getUsid()+a.getUserName()));
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
									}
								});
							}
						}
					});
					
				} else {
					showTip(getString(R.string.failed));
					return;
				}
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
				if ( mViewPager.getCurrentItem() == 2 ){
					mForgetPwdHandler.resetPasswordBySmsCode();
				} else {
					mSignUpHandler.valifyPhoneNumber();
				}
				break;
			case R.id.btn_fetch_valid_code:
				if ( mViewPager.getCurrentItem() == 2) {
					mForgetPwdHandler.requestPasswordResetBySmsCode();
				} else {
					String mobilePhoneNumber = mSignUpHandler.mPhoneInput.getmEditText().getText().toString();
					if ( isNumberFetchedValidCodeAndSignedUp.containsKey(mobilePhoneNumber) ){
						mSignUpHandler.reFetchValidCode();
					} else {
						mSignUpHandler.signUpWithPhoneNumberAndFetchValidCode();
					}
				}
				break;
			case R.id.btn_sign_in:
				mSignInHandler.signIn();
				break;
			case R.id.go_sign_in:
				mViewPager.setCurrentItem(0, true);
				break;
			case R.id.go_sign_up:
				mViewPager.setCurrentItem(1, true);
				break;
			case R.id.forget_password:
				mViewPager.setCurrentItem(2,true);
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
				mSignInHandler.setmName(signUpSuccessPhoneNumber);
//				setTitle(R.string.sign_up,R.color.white_f0);
//				mIndicator.check(R.id.radio1);
				break;
			case 1:
//				setTitle(R.string.sign_in,R.color.white_f0);
//				mIndicator.check(R.id.radio0);
				break;
			case 2:
				mForgetPwdHandler.mSignUpBtn.setText(R.string.reset_pwd);
				mForgetPwdHandler.mNameInput.hide();
				break;
			default:
				break;
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}
}
