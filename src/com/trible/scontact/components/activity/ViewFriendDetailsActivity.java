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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.trible.scontact.R;
import com.trible.scontact.components.adpater.FriendContactsListAdapter;
import com.trible.scontact.components.adpater.SearchResultAdapter.FriendViewHolder;
import com.trible.scontact.components.widgets.ChooseFriendActionDialog;
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
import com.trible.scontact.utils.IntentUtil;
import com.trible.scontact.value.GlobalValue;
import com.trible.scontact.value.RequestCode;


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
	FriendViewHolder mFriendViewHolder;
	
	ChooseFriendActionDialog mChooseFriendActionDialog;
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
		initView();
		initViewData();
		checkIsFriend();
	}

	void initView(){
		mFriendViewHolder = new FriendViewHolder(findViewById(R.id.friend_layout));
		mFriendAction = (Button) findViewById(R.id.friend_action);
		mFriendAction.setOnClickListener(this);
		mContactsListView = (ListView) findViewById(R.id.friend_contacts_list_view);
		mLoadingDialog = new LoadingDialog(this);
	}
	void initViewData(){
		mAdapter = new FriendContactsListAdapter(this);
		mContactsListView.setAdapter(mAdapter);
		mContactsListView.setOnItemClickListener(this);
		mContacts = mFriend.getContactsList();
		mAdapter.setData(mContacts);
		mFriendViewHolder.title.setText(mFriend.getDisplayName());
		mFriendViewHolder.desp.setText(mFriend.getDescription());
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		String type = mContacts.get(position).getType();
		String contact = mContacts.get(position).getContact();
		mChooseFriendActionDialog = new ChooseFriendActionDialog(this, contact);
		if ( GlobalValue.CTYPE_EMAIL.equals(type) ){
			IntentUtil.sendEmail(this, contact, "", "");
			return;
		} else if ( GlobalValue.CTYPE_PHONE.equals(type) ){
			if ( mFirendFlag == 2 ){
				mChooseFriendActionDialog.setMutilVisible(true, true, true);
			} else {
				mChooseFriendActionDialog.setMutilVisible(true, true, false);
			}
			
		} else if ( GlobalValue.CTYPE_IM.equals(type) ){
			
		} 
		mChooseFriendActionDialog.show();
	}
	void checkIsFriend(){
		if ( mGroupInfo != null ){
			mFriendAction.setVisibility(View.GONE);
			if ( SContactMainActivity.GROUP_OF_FRIEND.equals(mGroupInfo.getId()) ){
				mFirendFlag = 1;
				mFriendAction.setText(R.string.remove_friend_lable);
				mFriendAction.setVisibility(View.VISIBLE);
				return;
			} else if ( SContactMainActivity.GROUP_OF_LOCAL_FRIEND.equals(mGroupInfo.getId()) ){
				mFirendFlag = 2;
				return;
			} else if (AccountInfo.getInstance().getId().equals(mFriend.getId()) ){
				mFirendFlag = 3;
				return;
			} else {
				mFirendFlag = 0;
			}
		}
		SContactAsyncHttpClient.post(
				AccountParams.getCheckIsFriendsParams(
						AccountInfo.getInstance().getId(),mFriend.getId()),
				null, new AsyncHttpResponseHandler(){
					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						super.onSuccess(arg0, arg1, arg2);
						UserRelationInfo result = GsonHelper.getInfoFromJson(arg2, UserRelationInfo.class);
						if ( result != null && result.getId() != null){
							mFirendFlag = 1;
							mFriendAction.setText(R.string.remove_friend_lable);
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
						super.onFailure(arg0, arg1, arg2, arg3);
						Bog.toast(R.string.connect_server_err);
					}
		});
	}
	
	void onSureToAdd(String contactids){
		if ( TextUtils.isEmpty(contactids) ){
			Bog.toast(R.string.selected_empty);
		} else {
			mLoadingDialog.show();
			ValidateInfo info = new ValidateInfo();
			info.setContact_ids(contactids);
			info.setIs_group_to_user("0");
			info.setStart_user_id(AccountInfo.getInstance().getId());
			info.setEnd_user_id(mFriend.getId());
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
								Bog.toast(R.string.success);
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
					simpleGetResultFormActivity(
							SelectContactsActivity.class, RequestCode.SELECT_CONTACTS);
				}
				break;
	
			default:
				break;
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK){
			String contactids = data.getStringExtra(SelectContactsActivity.SELECTED_CONTACT);
			onSureToAdd(contactids);
		}
	}
}
