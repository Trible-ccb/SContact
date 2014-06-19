package com.trible.scontact.components.activity;

import java.util.List;

import org.apache.http.Header;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.trible.scontact.R;
import com.trible.scontact.components.widgets.ChooseContactsListDialog;
import com.trible.scontact.components.widgets.ChooseGroupActionDialog;
import com.trible.scontact.components.widgets.LoadingDialog;
import com.trible.scontact.components.widgets.YesOrNoTipDialog;
import com.trible.scontact.components.widgets.YesOrNoTipDialog.OnButtonClickListner;
import com.trible.scontact.networks.SContactAsyncHttpClient;
import com.trible.scontact.networks.params.AccountParams;
import com.trible.scontact.networks.params.PhoneAndGroupParams;
import com.trible.scontact.networks.params.ValidationParams;
import com.trible.scontact.pojo.AccountInfo;
import com.trible.scontact.pojo.ContactInfo;
import com.trible.scontact.pojo.GroupInfo;
import com.trible.scontact.pojo.ValidateInfo;
import com.trible.scontact.pojo.GsonHelper;
import com.trible.scontact.pojo.PhoneAndGroupInfo;
import com.trible.scontact.pojo.UserRelationInfo;
import com.trible.scontact.utils.Bog;
import com.trible.scontact.utils.ListUtil;
import com.trible.scontact.utils.StringUtil;
import com.trible.scontact.value.GlobalValue;
import com.trible.scontact.value.RequestCode;


/**
 * @author Trible Chen
 * here you can see the details of a special group and do some action;
 */
public class ViewGroupDetailsActivity extends CustomSherlockFragmentActivity
										implements OnClickListener{

	Button mGroupAction;
	ImageView mGroupImg,mOwnerImg;
	TextView mGroupName,mOwnerName,mGroupDesc,mOwnerDesc;
	
	GroupInfo mGroupInfo;
	AccountInfo mOwnerInfo;
	Boolean mUserInGroup;
	ChooseContactsListDialog mContactsListDialog;
	ChooseGroupActionDialog mGroupActionDialog;
	LoadingDialog mLoadingDialog;
	YesOrNoTipDialog mAskDialog;
	
	boolean isGroupInfoChange;
	
	public static Bundle getInentMyself(GroupInfo info) {
		Bundle b = new Bundle();
		b.putSerializable("clazz", ViewGroupDetailsActivity.class);
		b.putSerializable("ViewGroup", info);
		return b;
	}
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		mGroupInfo = (GroupInfo) getIntent().getSerializableExtra("ViewGroup");
		if ( mGroupInfo == null )finish();
		setContentView(R.layout.activity_view_group);
		setTitle(R.string.action_group_profile, R.color.blue_qq);
		initView();
	}

	void initView(){
		mGroupAction = (Button) findViewById(R.id.group_action);
		mGroupAction.setOnClickListener(this);
		mGroupAction.setVisibility(View.GONE);
		mGroupImg = (ImageView) findViewById(R.id.group_img);
		mGroupName = (TextView) findViewById(R.id.group_name);
		mGroupDesc = (TextView) findViewById(R.id.group_desc);
		mOwnerDesc = (TextView) findViewById(R.id.owner_desc);
		mOwnerImg = (ImageView) findViewById(R.id.owner_img);
		mOwnerName = (TextView) findViewById(R.id.owner_name);
		mLoadingDialog = new LoadingDialog(this);
		mAskDialog = new YesOrNoTipDialog(this, "", "");
		mContactsListDialog = new ChooseContactsListDialog(this);
		initViewData();
		
	}
	void initViewData(){

		refreshGroupInfo();
		loadOwnerData();
		checkAccountInGroup();
	}
	
	void loadOwnerData(){
		SContactAsyncHttpClient.post(
				AccountParams.getAccountByIdParams(mGroupInfo.getOwnerId()),
				null, new AsyncHttpResponseHandler(){
					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						mOwnerInfo = GsonHelper.getInfoFromJson(arg2, AccountInfo.class);
						if ( mOwnerInfo != null && mOwnerInfo.getId() != null ){
							refreshGroupInfo();
						} else {
							Bog.toastErrorInfo(arg2);
						}
					}
					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
						Bog.toast(R.string.connect_server_err);
					}
		});
	}
	void checkAccountInGroup(){
		SContactAsyncHttpClient.post(
				PhoneAndGroupParams.getCheckUserInGroupParams(
						mGroupInfo.getId(),AccountInfo.getInstance().getId()),
				null, new AsyncHttpResponseHandler(){
					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						PhoneAndGroupInfo result = GsonHelper.getInfoFromJson(arg2, PhoneAndGroupInfo.class);
						if ( result != null && result.getId() != null){
							mUserInGroup = true;
						} else if ( result == null ){
							mUserInGroup = false;
						} else {
							Bog.toastErrorInfo(arg2);
						}
						mGroupAction.setVisibility(View.VISIBLE);
						if ( AccountInfo.getInstance().getId().equals( mGroupInfo.getOwnerId() )){
							mGroupAction.setText(R.string.apart_group_lable);
							mGroupAction.setTextColor(getResources().getColor(R.color.red));
						} else {
							if ( mUserInGroup != null ){
								if( mUserInGroup ){
									mGroupAction.setText(R.string.exit_group_lable);
									mGroupAction.setTextColor(getResources().getColor(R.color.red));
								} else {
									mGroupAction.setText(R.string.join_group_lable);
								}
							}
						}
					}
					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
						Bog.toast(R.string.connect_server_err);
						
					}
		});
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_view_group_menu, menu);
		if ( AccountInfo.getInstance().getId().equals(mGroupInfo.getOwnerId()) ){
			menu.findItem(R.id.action_edit).setVisible(true);
		} else {
			menu.findItem(R.id.action_edit).setVisible(false);
		}
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_edit:
				simpleGetResultFromActivityWithData(RequestCode.EDIT_GROUP,
						CreateOrUpdateGroupActivity.getIntentMyself(mGroupInfo));
				
				break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	void refreshGroupInfo(){
		mGroupDesc.setText(mGroupInfo.getDescription());
		mGroupName.setText(mGroupInfo.getDisplayName());
		String name = "";
		String desp = "";
		if ( mOwnerInfo != null ){
			name = mOwnerInfo.getDisplayName();
			desp = mOwnerInfo.getDescription();
		}
		mOwnerName.setText(
				String.format(getString(R.string.format_creator),
						name));
		mOwnerDesc.setText(desp);
	}
	void onSureToJoin(String contactids){
		if ( TextUtils.isEmpty(contactids) ){
			Bog.toast(R.string.selected_empty);
		} else {
			mLoadingDialog.show();
			ValidateInfo info = new ValidateInfo();
			info.setContact_ids(contactids);
			info.setGroupId(mGroupInfo.getId());
			info.setIs_group_to_user(0);
			info.setStart_user_id(AccountInfo.getInstance().getId());
			info.setEnd_user_id(mGroupInfo.getOwnerId());
			SContactAsyncHttpClient.post(
					ValidationParams.getAddRelationshipParams(info)
					, null
					, new AsyncHttpResponseHandler(){
						@Override
						public void onSuccess(int arg0, Header[] arg1,
								byte[] arg2) {
							super.onSuccess(arg0, arg1, arg2);
							UserRelationInfo result = GsonHelper.getInfoFromJson(arg2, UserRelationInfo.class);
							if ( result != null && result.getId() != null){
								
								Intent itent = new Intent();
								if (GlobalValue.GIDENTIFY_NEEDED.equals(mGroupInfo.getIdentify())){
									itent.putExtra("SendGroupValidation", result);
									Bog.toast(R.string.request_have_send);
								} else {
									itent.putExtra("JoinGroup", mGroupInfo);
									Bog.toast(StringUtil.catStringFromResId(
											ViewGroupDetailsActivity.this, R.string.join_group_lable,R.string.success));
								}
								setResult(RESULT_OK, itent);
								finish();
							} else {
								Bog.toastErrorInfo(arg2);
							}
						}
						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] arg2, Throwable arg3) {
							super.onFailure(arg0, arg1, arg2, arg3);
							Bog.toast(R.string.connect_server_err);
						}
						@Override
						public void onFinish() {
							super.onFinish();
							mLoadingDialog.getDialog().dismissDialogger();
						}
					});
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if ( resultCode !=  RESULT_OK )return;
		switch (requestCode) {
			case RequestCode.EDIT_GROUP:
				mGroupInfo = (GroupInfo) data.getSerializableExtra(CreateOrUpdateGroupActivity.RESULT_GROUP);
				refreshGroupInfo();
				isGroupInfoChange = true;
				break;
	
			default:
				break;
		}
	}
	@Override
	public void onBackPressed() {
		if ( isGroupInfoChange ){
			Intent intent = new Intent();
			intent.putExtra(CreateOrUpdateGroupActivity.RESULT_GROUP, mGroupInfo);
			setResult(RESULT_OK, intent);
			finish();
		} else {
			super.onBackPressed();	
		}
		
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.group_action:
				Button tmp = (Button) v;
				mGroupActionDialog = new ChooseGroupActionDialog(this,mGroupInfo);
				String tmptitle = tmp.getText().toString();
				if (getString(R.string.join_group_lable).equals(tmptitle)){
					mContactsListDialog.setTileText(
							getString(R.string.format_select_contact, mGroupInfo.getDisplayName()));
					mContactsListDialog.sureBtn.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							List<ContactInfo> c = mContactsListDialog.getSelectedContacts();
							if ( ListUtil.isEmpty(c) ){
								Bog.toast(R.string.selected_empty);
							} else {
								onSureToJoin(ContactInfo.arrayToString(c));
								mContactsListDialog.getDialog().dismissDialogger();
							}
						}
					});
					mContactsListDialog.show();
				} else if (getString(R.string.exit_group_lable).equals(tmptitle)){
					mAskDialog.setmTipString(getString(R.string.promote_exit_group));
					mAskDialog.setmTitleString(tmptitle);
					mAskDialog.setOnButtonClickListner(new OnButtonClickListner() {
						@Override
						public void onYesButton() {
							mGroupActionDialog.exitGroup();
							mAskDialog.getDialog().dismissDialogger();
						}
						@Override
						public void onNoButton() {
						}
					});
					mAskDialog.show();
				} else if (getString(R.string.apart_group_lable).equals(tmptitle)){
					mAskDialog.setmTipString(getString(R.string.promote_apart_group));
					mAskDialog.setmTitleString(tmptitle);
					mAskDialog.setOnButtonClickListner(new OnButtonClickListner() {
						
						@Override
						public void onYesButton() {
							mGroupActionDialog.apartGroup();
							mAskDialog.getDialog().dismissDialogger();
						}
						@Override
						public void onNoButton() {
						}
					});
					mAskDialog.show();
				}
				break;

		default:
			break;
		}
	}
}
