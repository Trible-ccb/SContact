package com.trible.scontact.components.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.CheckBox;
import android.widget.RadioGroup;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.trible.scontact.R;
import com.trible.scontact.components.widgets.CustomTextInput;
import com.trible.scontact.components.widgets.YesOrNoTipDialog;
import com.trible.scontact.components.widgets.YesOrNoTipDialog.OnButtonClickListner;
import com.trible.scontact.controller.IGroupControl;
import com.trible.scontact.controller.impl.RemoteGroupControlller;
import com.trible.scontact.models.AccountInfo;
import com.trible.scontact.models.Groupsinfo;
import com.trible.scontact.networks.NetWorkEvent;
import com.trible.scontact.networks.SimpleAsynTask;
import com.trible.scontact.networks.SimpleAsynTask.AsynTaskListner;
import com.trible.scontact.utils.Bog;

public class CreateGroupActivity extends CustomSherlockFragmentActivity{

	AccountInfo mUserInfo;
	YesOrNoTipDialog mDiscardDialog;
	IGroupControl mGroupControl;
	Groupsinfo mGroupInfo;
	
	CustomTextInput mGroupNameInput;
	RadioGroup mCapatityRadio;
	RadioGroup mVerifyRadio;
	CheckBox isPublic;
	
	public static final String RESULT_GROUP = "result_group";
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_create_group);
		mDiscardDialog = new YesOrNoTipDialog(this, getString(R.string.discard_title), "");
		mGroupControl = new RemoteGroupControlller(this);
		mUserInfo = AccountInfo.getInstance();
		initView();
	}
	
	void initView(){
		mGroupNameInput = new CustomTextInput(findViewById(R.id.custom_edittext_root_view));
		mCapatityRadio = (RadioGroup) findViewById(R.id.group_capatity_root);
		mVerifyRadio = (RadioGroup) findViewById(R.id.group_verify_root);
		isPublic = (CheckBox) findViewById(R.id.group_is_public);
		mCapatityRadio.check(R.id.capatity_50);
		mVerifyRadio.check(R.id.verify_by_identify);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_create_group_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_save:
				onSave();
				break;
			case R.id.action_discard:
				mDiscardDialog.setOnButtonClickListner(new OnButtonClickListner() {
					@Override
					public void onYesButton() {
						onBackPressed();
					}
					
					@Override
					public void onNoButton() {
						mDiscardDialog.getDialog().dismissDialogger();
					}
				});
				mDiscardDialog.show();
				break;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	boolean verifyInput(){
		String s = mGroupNameInput.getmEditText().getText().toString();
		if ( TextUtils.isEmpty(s) ){
			Bog.toast(R.string.group_name_empty);
			return false;
		}
		return true;
	}
	private Groupsinfo getGroupInfoFromInput(){
		Groupsinfo gInfo = new Groupsinfo();
		gInfo.setmGroupName(mGroupNameInput.getmEditText().getText().toString());
		gInfo.setmInGroupNumber(1);
		gInfo.setGroupId((long)1);
		int capatity = 50;
		switch (mCapatityRadio.getCheckedRadioButtonId()) {
			case R.id.capatity_50:
				capatity = R.integer.capatity_50;
				break;
			case R.id.capatity_100:
				capatity = R.integer.capatity_100;
				break;
			case R.id.capatity_500:
				capatity = R.integer.capatity_500;
				break;
			case R.id.capatity_1000:
				capatity = R.integer.capatity_1000;
				break;
			default:
				break;
		}
		gInfo.setmGroupCapacity(capatity);
		int verifyType = Groupsinfo.VerifyType.kVerifyType_Indentified.ordinal();
		switch (mVerifyRadio.getCheckedRadioButtonId()) {
			case R.id.verify_by_anyone:
				verifyType = Groupsinfo.VerifyType.kVerifyType_Anyone.ordinal();
				break;
			case R.id.verify_by_identify:
				verifyType = Groupsinfo.VerifyType.kVerifyType_Indentified.ordinal();
				break;
			case R.id.verify_by_none:
				verifyType = Groupsinfo.VerifyType.kVerifyTYpe_None.ordinal();
				break;
			default:
				break;
		}
		gInfo.setmVerifyType(verifyType);
		return gInfo;
	}
	void onSave(){
		if (!verifyInput()){
			return;
		}
		mGroupInfo = getGroupInfoFromInput();
		SimpleAsynTask.doTask2(new AsynTaskListner() {
			boolean saveSuccess;
			@Override
			public void onTaskDone(NetWorkEvent event) {
				saveSuccess = true;
				if ( saveSuccess ){
					Bog.toast(R.string.done);
					Intent intent = new Intent();
					intent.putExtra(RESULT_GROUP, mGroupInfo);
					setResult(RESULT_OK, intent);
					finish();
				} else {
					Bog.toast(R.string.failed);
				}
			}
			@Override
			public void doInBackground() {
				saveSuccess = mGroupControl.createGroup(mGroupInfo, mUserInfo.getmUserId());
			}
		});
	}
}