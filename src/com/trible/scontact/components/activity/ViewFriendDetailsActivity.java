package com.trible.scontact.components.activity;

import java.util.List;

import org.apache.http.Header;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.trible.scontact.R;
import com.trible.scontact.components.adpater.FriendContactsListAdapter;
import com.trible.scontact.components.adpater.TypeHandler;
import com.trible.scontact.components.widgets.ChooseContactActionDialog;
import com.trible.scontact.components.widgets.ChooseContactsListDialog;
import com.trible.scontact.components.widgets.LoadingDialog;
import com.trible.scontact.components.widgets.YesOrNoTipDialog;
import com.trible.scontact.components.widgets.YesOrNoTipDialog.OnButtonClickListner;
import com.trible.scontact.networks.SContactAsyncHttpClient;
import com.trible.scontact.networks.params.AccountParams;
import com.trible.scontact.networks.params.ValidationParams;
import com.trible.scontact.pojo.AccountInfo;
import com.trible.scontact.pojo.ContactInfo;
import com.trible.scontact.pojo.GroupInfo;
import com.trible.scontact.pojo.GsonHelper;
import com.trible.scontact.pojo.UserRelationInfo;
import com.trible.scontact.pojo.ValidateInfo;
import com.trible.scontact.utils.Bog;
import com.trible.scontact.utils.ListUtil;
import com.trible.scontact.value.GlobalValue;


/**
 * @author Trible Chen
 *here you can see the details of a special friend and do some action;
 */
public class ViewFriendDetailsActivity extends CustomSherlockFragmentActivity 
										implements OnItemClickListener
										,OnClickListener
												{

	Button mFriendAction;
	ListView mContactsListView;
	FriendContactsListAdapter mAdapter;
	TextView mFriendName,mFriendDesp;
	
	ChooseContactsListDialog mContactsListDialog;
	ChooseContactActionDialog mChooseFriendActionDialog;
	LoadingDialog mLoadingDialog;
	
	List<ContactInfo> mContacts;
	AccountInfo mFriend;
	GroupInfo mGroupInfo;//which the friend belong to
	int mFirendFlag;//0 stranger,1 friend,2 local friend,3 myself
	
	public static Bundle getInentMyself(AccountInfo f,GroupInfo gf) {
		Bundle b = new Bundle();
		b.putSerializable("clazz", ViewFriendDetailsActivity.class);
		b.putSerializable("ViewFriend", f);
		b.putSerializable("InGroup", gf);
		return b;
	}
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		mFriend = (AccountInfo) getIntent().getSerializableExtra("ViewFriend");
		mGroupInfo = (GroupInfo) getIntent().getSerializableExtra("InGroup");
		setContentView(R.layout.activity_view_friend);
		setTitle(R.string.details_title, R.color.blue_qq);
		initView();
		initViewData();
		checkIsFriend();
	}

	void initView(){
		mContactsListDialog = new ChooseContactsListDialog(this);
		mFriendAction = (Button) findViewById(R.id.friend_action);
		mFriendAction.setOnClickListener(this);
		mFriendName = (TextView) findViewById(R.id.friend_name);
		mFriendDesp = (TextView) findViewById(R.id.friend_desp);
		mContactsListView = (ListView) findViewById(R.id.friend_contacts_list_view);
		mLoadingDialog = new LoadingDialog(this);
	}
	void initViewData(){
		mAdapter = new FriendContactsListAdapter(this);
		mContactsListView.setAdapter(mAdapter);
		mContactsListView.setOnItemClickListener(this);
		mContacts = mFriend.getContactsList();
		mAdapter.setData(mContacts);
		String gender = "";
		if ( GlobalValue.UGENDER_FEMALE.equals(mFriend.getGender()) ){
			gender = "(" + getString(R.string.gender_female) + ")";
		} else if ( GlobalValue.UGENDER_FEMALE.equals(mFriend.getGender()) ){
			gender = "(" + getString(R.string.gender_male) + ")";
		}
		mFriendName.setText(mFriend.getDisplayName() + gender);
		mFriendDesp.setText(mFriend.getDescription());
		
	}
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_view_friend_menu, menu);
		if ( mFirendFlag == 3 || mFirendFlag == 1 ){
			menu.findItem(R.id.action_edit).setVisible(true);
		} else {
			menu.findItem(R.id.action_edit).setVisible(false);
		}
		return super.onPrepareOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_edit:
				if ( mFirendFlag == 3 ){
					mContactsListDialog.setTileText(
							getString(R.string.format_select_contact,
									getString(R.string.group_lable) 
									+ mGroupInfo.getDisplayName()));
				} else if ( mFirendFlag == 1 ){
					mContactsListDialog.setTileText(
							getString(R.string.format_select_contact, 
									mFriend.getDisplayName()));
				} else {
					return false;
				}
				mContactsListDialog.sureBtn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						List<ContactInfo> c = mContactsListDialog.getSelectedContacts();
						if ( ListUtil.isEmpty(c) ){
							Bog.toast(R.string.selected_empty);
						} else {
							ValidateInfo info = new ValidateInfo();
							info.setStart_user_id(AccountInfo.getInstance().getId());
							info.setContact_ids(ContactInfo.arrayToString(c));
							if ( mFirendFlag == 3 ){
								info.setGroupId(mGroupInfo.getId());
								info.setEnd_user_id(mGroupInfo.getOwnerId());
							} else if ( mFirendFlag == 1 ){
								info.setEnd_user_id(mFriend.getId());
							}
							onSureToUpdateVisibleContacts(info);
							mContactsListDialog.getDialog().dismissDialogger();
						}
					}
				});
				mContactsListDialog.show();
				mContactsListDialog.setSelectedContacts(mContacts);
				break;
	
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		String type = mAdapter.getContact(position).getType();
		String contact = mAdapter.getContact(position).getContact();
		mChooseFriendActionDialog = new ChooseContactActionDialog(
				this, mAdapter.getContact(position));
		new TypeHandler(this) {
			@Override
			protected void onPhone() {
				mChooseFriendActionDialog.addAction(getString(R.string.call_lable));
				mChooseFriendActionDialog.addAction(getString(R.string.message_lable));
				if ( mFirendFlag == 2 ){
					mChooseFriendActionDialog.addAction(getString(R.string.invite_lable));
				} else {
					mChooseFriendActionDialog.name = mFriend.getDisplayName();
					mChooseFriendActionDialog.addAction(getString(R.string.add_to_local_lable));
				}
			}
			@Override
			protected void onEmail() {
				mChooseFriendActionDialog.addAction(getString(R.string.send_email_lable));
				if ( mFirendFlag == 2 ){
					mChooseFriendActionDialog.addAction(getString(R.string.invite_lable));
				} else {
					mChooseFriendActionDialog.name = mFriend.getDisplayName();
					mChooseFriendActionDialog.addAction(getString(R.string.add_to_local_lable));
				}
			}
			
		}.handle(type);
//		if ( GlobalValue.CTYPE_EMAIL.equals(type) ){
//		} else if ( GlobalValue.CTYPE_PHONE.equals(type) ){
//		} else if ( GlobalValue.CTYPE_IM.equals(type) ){
//		} 
		mChooseFriendActionDialog.addAction(getString(R.string.copy_lable));
		mChooseFriendActionDialog.show();
	}
	void checkIsFriend(){
		if ( mGroupInfo != null ){
			mFriendAction.setVisibility(View.GONE);
			if ( SContactMainActivity.GROUP_OF_FRIEND.equals(mGroupInfo.getId()) ){
				mFirendFlag = 1;
				mFriendAction.setText(R.string.remove_friend_lable);
				mFriendAction.setTextColor(getResources().getColor(R.color.red));
				mFriendAction.setVisibility(View.VISIBLE);
				return;
			} else if ( SContactMainActivity.GROUP_OF_LOCAL_FRIEND.equals(mGroupInfo.getId()) ){
				mFirendFlag = 2;
				return;
			} else {
				mFirendFlag = 0;
			}
		}
		if (AccountInfo.getInstance().getId().equals(mFriend.getId()) ){
			mFirendFlag = 3;
			return;
		} 
		SContactAsyncHttpClient.post(
				AccountParams.getCheckIsFriendsParams(
						AccountInfo.getInstance().getId(),mFriend.getId()),
				null, new AsyncHttpResponseHandler(){
					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						UserRelationInfo result = GsonHelper.getInfoFromJson(arg2, UserRelationInfo.class);
						if ( result != null && result.getId() != null){
							mFirendFlag = 1;
							mFriendAction.setText(R.string.remove_friend_lable);
							mFriendAction.setTextColor(getResources().getColor(R.color.red));
						} else if ( result == null ){
							mFirendFlag = 0;
							mFriendAction.setText(R.string.add_friend_lable);
						} else {
							Bog.toastErrorInfo(arg2);
						}
						mFriendAction.setVisibility(View.VISIBLE);
					}
					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
						Bog.toast(R.string.connect_server_err);
					}
					@Override
					public void onFinish() {
						supportInvalidateOptionsMenu();
					}
		});
	}
	
	void onSureToUpdateVisibleContacts(ValidateInfo info){
		mLoadingDialog.show();
		SContactAsyncHttpClient.post(
				ValidationParams.getUpdateRelationshipParams(info)
				, null
				, new AsyncHttpResponseHandler(){
					@Override
					public void onSuccess(int arg0, Header[] arg1,
							byte[] arg2) {
						UserRelationInfo result = GsonHelper.getInfoFromJson(arg2, UserRelationInfo.class);
						if ( result != null && result.getId() != null){
							Bog.toast(R.string.success);
							SContactMainActivity.needRefreshFriendList = true;
						} else {
							Bog.toastErrorInfo(arg2);
						}
					}
					@Override
					public void onFailure(int arg0, Header[] arg1,
							byte[] arg2, Throwable arg3) {
						Bog.toast(R.string.connect_server_err);
					}
					@Override
					public void onFinish() {
						mLoadingDialog.getDialog().dismissDialogger();
					}
				});
	}
	void onSureToAddFriend(String contactids){
		if ( TextUtils.isEmpty(contactids) ){
			Bog.toast(R.string.selected_empty);
		} else {
			mLoadingDialog.show();
			ValidateInfo info = new ValidateInfo();
			info.setContact_ids(contactids);
			info.setIs_group_to_user(0);
			info.setStart_user_id(AccountInfo.getInstance().getId());
			info.setEnd_user_id(mFriend.getId());
			SContactAsyncHttpClient.post(
					ValidationParams.getAddRelationshipParams(info)
					, null
					, new AsyncHttpResponseHandler(){
						@Override
						public void onSuccess(int arg0, Header[] arg1,
								byte[] arg2) {
							UserRelationInfo result = GsonHelper.getInfoFromJson(arg2, UserRelationInfo.class);
							if ( result != null && result.getId() != null){
								Bog.toast(R.string.request_have_send);
								finish();
							} else {
								Bog.toastErrorInfo(arg2);
							}
						}
						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] arg2, Throwable arg3) {
							Bog.toast(R.string.connect_server_err);
						}
						@Override
						public void onFinish() {
							mLoadingDialog.getDialog().dismissDialogger();
						}
					});
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.friend_action:
				String ts = ((TextView) v).getText().toString();
				if ( getString(R.string.remove_friend_lable).equals(ts) ){//remove friend relationship
					final YesOrNoTipDialog dialog = new YesOrNoTipDialog(
							this,
							getString(R.string.remove_friend_lable),
							getText(R.string.promote_remove_friend).toString());
					dialog.setOnButtonClickListner(new OnButtonClickListner() {
						@Override
						public void onYesButton() {
							dialog.getDialog().dismissDialogger();
							ValidateInfo info = new ValidateInfo();
							info.setEnd_user_id(mFriend.getId());
							info.setStart_user_id(AccountInfo.getInstance().getId());
							final LoadingDialog ldialog = new LoadingDialog(ViewFriendDetailsActivity.this);
							ldialog.getDialog().setmDismisslistener(new OnDismissListener() {
								
								@Override
								public void onDismiss(DialogInterface dialog) {
									SContactAsyncHttpClient.cancel(ViewFriendDetailsActivity.this, true);
								}
							});
							ldialog.show();
							SContactAsyncHttpClient.post(
									ValidationParams.getRemoveRelationshipParams(info),
									null, 
									new AsyncHttpResponseHandler(){
										@Override
										public void onSuccess(int arg0,
												Header[] arg1, byte[] arg2) {
											UserRelationInfo result = GsonHelper.getInfoFromJson(arg2, UserRelationInfo.class);
											if ( result != null && result.getId() != null ){
												Bog.toast(R.string.success);
												SContactMainActivity.needRefreshFriendList = true;
												finish();
											} else {
												Bog.toast(R.string.failed);
											}
										}
										@Override
										public void onFailure(int arg0,
												Header[] arg1, byte[] arg2,
												Throwable arg3) {
											Bog.toast(R.string.connect_server_err);
										}
										@Override
										public void onFinish() {
											ldialog.getDialog().dismissDialogger();
										}
									});
							dialog.getDialog().dismissDialogger();
						}
						@Override
						public void onNoButton() {
						}
					});
					dialog.show();
				} else if (getString(R.string.add_friend_lable).equals(ts) ){// add friend relationship
					
					mContactsListDialog.setTileText(
							getString(R.string.format_select_contact,
									mFriend.getDisplayName()));
					mContactsListDialog.sureBtn.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							List<ContactInfo> c = mContactsListDialog.getSelectedContacts();
							if ( ListUtil.isEmpty(c) ){
								Bog.toast(R.string.selected_empty);
							} else {
								onSureToAddFriend(ContactInfo.arrayToString(c));
								mContactsListDialog.getDialog().dismissDialogger();
							}
						}
					});
					mContactsListDialog.show();
				}
				break;
	
			default:
				break;
		}
	}
}
