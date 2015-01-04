package com.trible.scontact.components.activity;

import java.util.List;

import org.apache.http.Header;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
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
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
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
import com.trible.scontact.pojo.GroupInfo;
import com.trible.scontact.pojo.GsonHelper;
import com.trible.scontact.pojo.UserGroupRelationInfo;
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
	CustomTextInput mGroupNameInput,mGroupDespInput;
	RadioGroup mCapatityRadio;
	RadioGroup mVerifyRadio;
	CheckBox isPublic;
	Button mChooseContacts;
	
	GroupInfo mGroupInfo;
	List<ContactInfo> mContactsInGroup;//the contact list already in group
	
	public static final String RESULT_GROUP = "result_group";
	public static GroupInfo LATEST_GROUP = null;//添加或者更新后的群组
	
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
		setTitle(R.string.action_create_group, R.color.blue_qq);
		mGroupInfo = (GroupInfo) getIntent().getSerializableExtra("EditGroup");
		mDiscardDialog = new YesOrNoTipDialog(this, getString(R.string.discard_title), "");
		mGroupControl = new RemoteGroupControlller(this);
		mUserInfo = AccountInfo.getInstance();
		initView();
		initViewData();
	}
	
	void initView(){
		mChooseContactsListDialog = new ChooseContactsListDialog(this);
		mChooseContactsListDialog.getDialog().setmDismisslistener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				mChooseContacts.setText(getString(R.string.selected_contacts, mChooseContactsListDialog.getSelectedContacts().size()));
			}
		});
		mLoadingDialog = new LoadingDialog(this);
		mLoadingDialog.setTipText(R.string.waiting);
		mGroupNameInput = new CustomTextInput(findViewById(R.id.custom_edittext_root_view));
		mGroupDespInput = new CustomTextInput(findViewById(R.id.layout_custom_edit_text_desc));
		mCapatityRadio = (RadioGroup) findViewById(R.id.group_capatity_root);
		mVerifyRadio = (RadioGroup) findViewById(R.id.group_verify_root);
		isPublic = (CheckBox) findViewById(R.id.group_is_public);
		isPublic.setVisibility(View.GONE);//all group can be search now
		mChooseContacts = (Button) findViewById(R.id.choose_contacts_btn);
		mChooseContacts.setOnClickListener(this);
		mGroupNameInput.getImageButton().setVisibility(View.GONE);
		mGroupDespInput.getImageButton().setVisibility(View.GONE);
		mGroupNameInput.getmEditText().setHint(R.string.hint_group_name);
		mGroupDespInput.getmEditText().setHint(R.string.hint_description);
	}
	
	void initViewData(){
		LATEST_GROUP = null;
		if ( mGroupInfo != null ){//edit
			mGroupNameInput.getmEditText().setText(mGroupInfo.getDisplayName());
			mGroupDespInput.getmEditText().setText(mGroupInfo.getDescription());
//			for ( int i = 0 ; i < mCapatityRadio.getChildCount(); i++ ){
//				mCapatityRadio.getChildAt(i).setEnabled(false);
//			}
//			for ( int i = 0 ; i < mVerifyRadio.getChildCount(); i++ ){
//				mVerifyRadio.getChildAt(i).setEnabled(false);
//			}
			mChooseContacts.setVisibility(View.GONE);
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
			if ( GlobalValue.GIDENTIFY_NONE.equals(mGroupInfo.getIdentify()) ){
				mVerifyRadio.check(R.id.verify_by_anyone);
			} else if ( GlobalValue.GIDENTIFY_NEEDED.equals(mGroupInfo.getIdentify())){
				mVerifyRadio.check(R.id.verify_by_identify);
			} else if ( GlobalValue.GIDENTIFY_NULL.equals(mGroupInfo.getIdentify())){
				mVerifyRadio.check(R.id.verify_by_anyone);
			}
		} else {//add
			
			isPublic.setChecked(true);
			mCapatityRadio.check(R.id.capatity_normal);
			mVerifyRadio.check(R.id.verify_by_identify);
			mChooseContacts.performClick();
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
		if (ListUtil.isEmpty(chooseContacts) && mGroupInfo == null ){
			Bog.toast(R.string.selected_empty);
			return false;
		}
		return true;
	}
	private GroupInfo getGroupInfoFromInput(){
		mUserInfo = AccountInfo.getInstance();
		GroupInfo gInfo = new GroupInfo();
		gInfo.setDisplayName(mGroupNameInput.getmEditText().getText().toString());
		gInfo.setDescription(mGroupDespInput.getmEditText().getText().toString());
		gInfo.setOwner(mUserInfo);
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
	//add or edit group info
	void saveToAVOS(){
		
	}
	void onSave(){
		if (!verifyInput()){
			return;
		}
		final boolean isAdd;
		mLoadingDialog.getDialog().dismissDialogger();
		mLoadingDialog.show();
		final GroupInfo tmp = getGroupInfoFromInput();
		final List<ContactInfo> chooseContacts = mChooseContactsListDialog.getSelectedContacts();
		if ( mGroupInfo != null ){
			isAdd = false;
			if ( TextUtils.isEmpty(mGroupInfo.getType())){
				tmp.setType(GlobalValue.GTYPE_NORMAL);
			} else {
				tmp.setType(mGroupInfo.getType());
			}
			tmp.setObjectId(mGroupInfo.getObjectId());
		} else {
			isAdd = true;
		}
		tmp.setStatus(GlobalValue.GSTATUS_USED);
		tmp.setFetchWhenSave(true);
		tmp.saveInBackground(new SaveCallback() {
			@Override
			public void done(AVException arg0) {
				if ( arg0 == null){
					Bog.toast(R.string.done);
					LATEST_GROUP = tmp;
					Intent intent = new Intent();
					intent.putExtra(RESULT_GROUP, tmp);
					setResult(RESULT_OK, intent);
					finish();
					if ( isAdd ){
						UserGroupRelationInfo ugri = new UserGroupRelationInfo();
						ugri.setUser(AccountInfo.getInstance());
						ugri.setContacts(chooseContacts);
						ugri.setGroup(tmp);
						ugri.saveInBackground();
					}

				} else {
					Bog.toast(StringUtil.catStringFromResId(
							R.string.submit,R.string.failed));
				}
			}
		});
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.choose_contacts_btn:
			if ( mGroupInfo != null ){
				mChooseContactsListDialog.setTileText( 
						getString(
								R.string.format_select_contact,
								mGroupInfo.getDisplayName()));
			} else {
				mChooseContactsListDialog.setTileText( 
						getString(R.string.format_select_contact,
								getString(R.string.group_lable)));
			}
			mChooseContactsListDialog.show();
			break;

		default:
			break;
		}
	}
}