package com.trible.scontact.components.activity;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.trible.scontact.R;
import com.trible.scontact.components.widgets.SettingItemCheckable;
import com.trible.scontact.database.DBHelper;
import com.trible.scontact.networks.NetWorkEvent;
import com.trible.scontact.networks.SimpleAsynTask;
import com.trible.scontact.networks.SimpleAsynTask.AsynTaskListner;
import com.trible.scontact.pojo.AccountInfo;
import com.trible.scontact.pojo.GroupInfo;
import com.trible.scontact.thirdparty.umeng.UmengController;
import com.trible.scontact.utils.Bog;
import com.trible.scontact.utils.DeviceUtil;
import com.trible.scontact.utils.IntentUtil;
import com.trible.scontact.value.GlobalValue;

public class SettingsActivity extends  CustomSherlockFragmentActivity implements OnClickListener{
	
	TextView mEmailText;
	
	SettingItemCheckable mSettingContactUs,mSettingRankApp,mSignOutApp,mAccountLevel;
	
	AccountInfo uInfo = AccountInfo.getInstance();
	
	boolean hasGooglePlay = false;
	
	public static final String SIGN_OUT_TAG = "sign_out";
	
	public static void displayMyself(Activity from,int requestCode){
		Intent intent = new Intent(from, SettingsActivity.class);
		from.startActivityForResult(intent, requestCode);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		DeviceUtil.focusePortraitForPhoneButPad(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		setTitle(R.string.action_settings, R.color.blue_qq);
		initView();
		new SimpleAsynTask().doTask(new AsynTaskListner() {
			
			@Override
			public void onTaskDone(NetWorkEvent event) {
			}
			
			@Override
			public void doInBackground() {
				PackageManager mng = getPackageManager();
				if ( mng != null ){
					List<PackageInfo> infos = mng.getInstalledPackages(0);
					Bog.v("package size : " + infos.size() );
					for ( PackageInfo pInfo : infos ){
						if ( pInfo.packageName.equals("com.android.vending") ){//had installed google play in device
							hasGooglePlay = true;
							break;
						}
					}
				}
	
			}
		});
	}
	@Override
	public void onStart() {
	    super.onStart();
	}

	@Override
	public void onStop() {
	    super.onStop();
	}
	public void initView(){
		mSignOutApp = new SettingItemCheckable(findViewById(R.id.setting_sign_out));
		mSettingContactUs = new SettingItemCheckable(findViewById(R.id.setting_contact_us));
		mSettingRankApp = new SettingItemCheckable(findViewById(R.id.setting_rank_app));
		mAccountLevel = new SettingItemCheckable(findViewById(R.id.setting_account_level));
		
		mSignOutApp.setHintText(uInfo.getDisplayName());
		mSignOutApp.setTitle(getString(R.string.sign_out));
		
		mAccountLevel.setTitle(getString(R.string.account_level));
		mAccountLevel.setHintText(uInfo.getType());
		
		mSettingRankApp.setHintText(getString(R.string.rank_app_hint));
		mSettingRankApp.setTitle(getString(R.string.rank_app));
		
		mSettingContactUs.setHintText(getString(R.string.contact_us_hint));
		mSettingContactUs.setTitle(getString(R.string.contact_us));
		
		mSignOutApp.setOnclickListner(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				doSignOut();
			}
		});
		mAccountLevel.setOnclickListner(new OnClickListener() {
			@Override
			public void onClick(View v) {
				upgradeAccount();
			}
		});
		mSettingRankApp.setVisibility(View.GONE);
		mSettingRankApp.setOnclickListner(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				goToRank();
			}
		});
		mSettingContactUs.setOnclickListner( new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendFeedBack();
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		default:
			break;
		}
	}
	
	void doSignOut(){
//		uInfo.setStatus(GlobalValue.USTATUS_SIGN_OUT);
//		uInfo.saveToPref();
		uInfo.signOut();
		DBHelper.reset();
		clearPojoCache();
		simpleDisplayActivity(SignInUpActivity.class);
		Intent in = new Intent();
		in.putExtra(SIGN_OUT_TAG, true);
		setResult(Activity.RESULT_OK,in);
//		PushAgent.getInstance(this).disable();
		UmengController.getService().loginout(this,null);
		finish();
	}
	void upgradeAccount(){
		
	}
	
	public String getVersion() {
		try {
	         PackageManager manager = this.getPackageManager();
	         PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
	         String version = info.versionName;
	         return "" + version;
		} catch (Exception e) {
	         e.printStackTrace();
	         return " null";
		}
	}
	
	void sendFeedBack(){
		String format = "---------\nDevice: %s\nBrand: %s\nOS: %s\nBuild: %s\n---------\n";
		IntentUtil.sendEmail(this,
				GlobalValue.CONTACT_EMAIL
				, "Feedback"+getVersion()
				, String.format(format  
						,Build.MODEL
						,Build.BRAND
						,"Android " + Build.VERSION.RELEASE
						,Build.DISPLAY
						));
	}
	
	void goToRank(){

		if ( hasGooglePlay ){
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName()));
			intent.setPackage("com.android.vending");
			startActivity(intent);
		} else {
			Bog.toast("no google play app.");
		}
	}
	void clearPojoCache(){
		GroupInfo.clearSpf();
	}
	
}
