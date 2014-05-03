package com.trible.scontact.components.activity;

import java.util.List;

import org.apache.http.Header;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioGroup;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.trible.scontact.R;
import com.trible.scontact.components.widgets.ChooseContactsListDialog;
import com.trible.scontact.components.widgets.CustomTextInput;
import com.trible.scontact.components.widgets.LoadingDialog;
import com.trible.scontact.components.widgets.YesOrNoTipDialog;
import com.trible.scontact.components.widgets.YesOrNoTipDialog.OnButtonClickListner;
import com.trible.scontact.controller.IGroupControl;
import com.trible.scontact.controller.impl.RemoteGroupControlller;
import com.trible.scontact.networks.SContactAsyncHttpClient;
import com.trible.scontact.networks.params.GroupParams;
import com.trible.scontact.pojo.AccountInfo;
import com.trible.scontact.pojo.ContactInfo;
import com.trible.scontact.pojo.ErrorInfo;
import com.trible.scontact.pojo.GroupInfo;
import com.trible.scontact.utils.Bog;
import com.trible.scontact.utils.ListUtil;
import com.trible.scontact.utils.StringUtil;
import com.trible.scontact.value.GlobalValue;

public class CreateOrUpdateGroupActivity extends CustomSherlockFragmentActivity
											implements OnClickListener{

	AccountInfo mUserInfo;
	YesOrNoTipDialog mDiscardDialog;
	IGroupControl mGroupControl;
	LoadingDialog mLoadingDialog;
	
	ChooseContactsListDialog mChooseContactsListDialog;
	CustomTextInput mGroupNameInput;
	RadioGroup mCapatityRadio;
	RadioGroup mVerifyRadio;
	CheckBox isPublic;
	Button mChooseContacts;
	
	GroupInfo mGroupInfo;
	List<ContactInfo> mContactsInGroup;//the contact list already in group
	
	public static final String RESULT_GROUP = "result_group";
	
	public static Bundle getIntentMyself(GroupInfo info){
		Bundle b = new Bundle();
		b.putSerializable("clazz", CreateOrUpdateGroupActivity.class);
		b.putSerializable("EditGroup", info);
		return b;
	}
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_create_group);
		mGroupInfo = (GroupInfo) getIntent().getSerializableExtra("EditGroup");
		mDiscardDialog = new YesOrNoTipDialog(this, getString(R.string.discard_title), "");
		mGroupControl = new RemoteGroupControlller(this);
		mUserInfo = AccountInfo.getInstance();
		initView();
		initViewData();
	}
	
	void initView(){
		mChooseContactsListDialog = new ChooseContactsListDialog(this);
		mLoadingDialog = new LoadingDialog(this);
		mLoadingDialog.setTipText(R.string.waiting);
		mGroupNameInput = new CustomTextInput(findViewById(R.id.custom_edittext_root_view));
		mCapatityRadio = (RadioGroup) findViewById(R.id.group_capatity_root);
		mVerifyRadio = (RadioGroup) findViewById(R.id.group_verify_root);
		isPublic = (CheckBox) findViewById(R.id.group_is_public);
		isPublic.setVisibility(View.GONE);//all group can be search now
		mChooseContacts = (Button) findViewById(R.id.choose_contacts_btn);
		mChooseContacts.setOnClickListener(this);
	}
	
	void initViewData(){
		if ( mGroupInfo != null ){
			mGroupNameInput.getmEditText().setText(mGroupInfo.getDisplayName());
			if ( mGroupInfo.getCapacity() == null ){
				mCapatityRadio.check(R.id.capatity_normal);
			} else {
				if ( mGroupInfo.getCapacity() == getResources().getInteger(R.integer.capatity_normal)){
					mCapatityRadio.check(R.id.capatity_normal);
				} else if (mGroupInfo.getCapacity() == getResources().getInteger(R.integer.capatity_level1)){
					mCapatityRadio.check(R.id.capatity_level1);
				} else if (mGroupInfo.getCapacity() == getResources().getInteger(R.integer.capatity_level2)){
					mCapatityRadio.check(R.id.capatity_level2);
				} else if (mGroupInfo.getCapacity() == getResources().getInteger(R.integer.capatity_dev)){
					mCapatityRadio.check(R.id.capatity_dev);
				}
			}
			if ( mGroupInfo.getIdentify() == GlobalValue.GIDENTIFY_NONE ){
				mVerifyRadio.check(R.id.verify_by_anyone);
			} else if ( mGroupInfo.getIdentify() == GlobalValue.GIDENTIFY_NEEDED){
				mVerifyRadio.check(R.id.verify_by_identify);
			} else if ( mGroupInfo.getIdentify() == GlobalValue.GIDENTIFY_NULL){
				mVerifyRadio.check(R.id.verify_by_anyone);
			}
		} else {
			isPublic.setChecked(true);
			mCapatityRadio.check(R.id.capatity_normal);
			mVerifyRadio.check(R.id.verify_by_identify);
		}
		
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
		List<ContactInfo> chooseContacts = mChooseContactsListDialog.getSelectedContacts();
		if (ListUtil.isEmpty(chooseContacts)){
			Bog.toast(R.string.selected_empty);
			return false;
		}
		return true;
	}
	private GroupInfo getGroupInfoFromInput(){
		mUserInfo = AccountInfo.getInstance();
		GroupInfo gInfo = new GroupInfo();
		gInfo.setDisplayName(mGroupNameInput.getmEditText().getText().toString());
		gInfo.setOwnerId(mUserInfo.getId());
		int capatity = 50;
		switch (mCapatityRadio.getCheckedRadioButtonId()) {
			case R.id.capatity_normal:
				capatity = GlobalValue.GROUP_CAPACITY_RULE.get(GlobalValue.GTYPE_NORMAL);
				break;
			case R.id.capatity_level1:
				capatity = GlobalValue.GROUP_CAPACITY_RULE.get(GlobalValue.GTYPE_LEVE1);
				break;
			case R.id.capatity_level2:
				capatity = GlobalValue.GROUP_CAPACITY_RULE.get(GlobalValue.GTYPE_LEVEL2);
				break;
			case R.id.capatity_dev:
				capatity = GlobalValue.GROUP_CAPACITY_RULE.get(GlobalValue.GTYPE_DEV);
				break;
			default:
				break;
		}
		gInfo.setCapacity(capatity);
		int verifyType = GlobalValue.GIDENTIFY_NEEDED; 
		switch (mVerifyRadio.getCheckedRadioButtonId()) {
			case R.id.verify_by_anyone:
				verifyType = GlobalValue.GIDENTIFY_NONE;
				break;
			case R.id.verify_by_identify:
				verifyType = GlobalValue.GIDENTIFY_NEEDED;
				break;
			default:
				break;
		}
		gInfo.setIdentify(verifyType);
		return gInfo;
	}
	void onSave(){
		if (!verifyInput()){
			return;
		}
		mLoadingDialog.getDialog().dismissDialogger();
		mLoadingDialog.show();
		GroupInfo tmp = getGroupInfoFromInput();
		List<ContactInfo> chooseContacts = mChooseContactsListDialog.getSelectedContacts();
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for ( ContactInfo c : chooseContacts ){
			sb.append(c.getId() + ",");
		}
		int idx = sb.lastIndexOf(",");
		sb.replace(idx, sb.length(), "]");
		String url = mGroupInfo == null 
				? GroupParams.getAddParams(tmp,sb.toString()) 
						: GroupParams.getUpdateParams(tmp);
		SContactAsyncHttpClient.post(
				url,
				null, new AsyncHttpResponseHandler(){
					@Override
					public void onFinish() {
						super.onFinish();
						mLoadingDialog.getDialog().dismissDialogger();
					}
					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						super.onSuccess(arg0, arg1, arg2);
						GroupInfo result = null;
						String content = StringUtil.getStringForByte(arg2);
						
						result = new Gson().fromJson(content, GroupInfo.class);
						if ( result != null && result.getId() != null ){
							Bog.toast(R.string.done);
							Intent intent = new Intent();
							intent.putExtra(RESULT_GROUP, result);
							setResult(RESULT_OK, intent);
							finish();
						} else {
							ErrorInfo info = new Gson().fromJson(content, ErrorInfo.class);
							Bog.toast(info.toString());
						}
					}
					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
						super.onFailure(arg0, arg1, arg2, arg3);
						Bog.toast(getString(R.string.connect_server_err));
					}
				});
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.choose_contacts_btn:
			mChooseContactsListDialog.show();
			break;

		default:
			break;
		}
	}
}